alter table dimensao_pneu add column cod_empresa bigint;
alter table dimensao_pneu add column cod_auxiliar bigint;
alter table dimensao_pneu add constraint unique_dimensao_cod_empresa unique (codigo, cod_empresa);
alter table dimensao_pneu add column status_ativo boolean;
drop view view_pneu_analise_vida_atual;
drop view view_analise_pneus;
alter table dimensao_pneu alter column aro type numeric(7,2);
alter table dimensao_pneu alter column altura type numeric(7,2);
alter table dimensao_pneu alter column largura type numeric(7,2);

create or replace view view_pneu_analise_vida_atual as
with dados as (
    select vpav.cod_pneu,
           vpav.vida_analisada_pneu,
           vpav.status,
           vpav.valor_pneu,
           vpav.valor_banda,
           vpav.quantidade_afericoes_pneu_vida,
           vpav.data_hora_primeira_afericao,
           vpav.data_hora_ultima_afericao,
           vpav.total_dias_ativo,
           vpav.total_km_rodado_vida,
           vpav.maior_sulco_aferido_vida,
           vpav.menor_sulco_aferido_vida,
           vpav.sulco_gasto,
           vpav.sulco_restante,
           vpav.km_por_mm_vida,
           vpav.valor_por_km_vida
    from view_pneu_analise_vidas vpav
)
select p.cod_unidade                                                 as cod_unidade,
       u.nome                                                        as nome_unidade_alocado,
       p.codigo                                                      as cod_pneu,
       p.codigo_cliente                                              as cod_cliente_pneu,
       p.valor + sum(pvv.valor)                                      as valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                            as km_acumulado,
       p.vida_atual                                                  as vida_atual,
       p.status                                                      as status_pneu,
       p.valor                                                       as valor_pneu,
       case
           when dados.vida_analisada_pneu = 1
               then dados.valor_pneu
           else dados.valor_banda
           end                                                       as valor_vida_atual,
       map.nome                                                      as nome_marca,
       mp.nome                                                       as nome_modelo,
       dp.largura || '/' || dp.altura || ' R' || dp.aro              as medidas,
       dados.quantidade_afericoes_pneu_vida                          as qtd_afericoes,
       dados.data_hora_primeira_afericao                             as data_hora_primeira_afericao,
       dados.data_hora_ultima_afericao                               as data_hora_ultima_afericao,
       dados.total_dias_ativo                                        as dias_ativo,
       round(
               case
                   when dados.total_dias_ativo > 0
                       then dados.total_km_rodado_vida / dados.total_dias_ativo::numeric
                   else null::numeric
                   end)                                              as media_km_por_dia,
       round(dados.maior_sulco_aferido_vida::numeric, 2)             as maior_sulco_vida,
       round(dados.menor_sulco_aferido_vida::numeric, 2)             as menor_sulco_vida,
       round(dados.sulco_gasto::numeric, 2)                          as milimetros_gastos,
       round(dados.km_por_mm_vida::numeric, 2)                       as kms_por_milimetro,
       round(dados.valor_por_km_vida::numeric, 2)                    as valor_por_km,
       round((
                 case
                     when (sum(v.total_km_rodado_todas_vidas) > 0::numeric)
                         then (p.valor + sum(pvv.valor)) /
                              sum(v.total_km_rodado_todas_vidas)::double precision
                     else 0
                     end)::numeric, 2)                               as valor_por_km_acumulado,
       round((dados.km_por_mm_vida * dados.sulco_restante)::numeric) as kms_a_percorrer,
       trunc(
               case
                   when (((dados.total_km_rodado_vida > 0::numeric) and (dados.total_dias_ativo > 0)) and
                         ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > 0::numeric)) then (
                           (dados.km_por_mm_vida * dados.sulco_restante) /
                           ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision)
                   else 0::double precision
                   end)                                              as dias_restantes_pneu,
       case
           when (((dados.total_km_rodado_vida > 0::numeric) and (dados.total_dias_ativo > 0)) and
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > 0::numeric)) then (
                   (((dados.km_por_mm_vida * dados.sulco_restante) /
                     ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                   current_date)
           else null::date
           end                                                       as data_prevista_troca,
       case
           when (p.vida_atual = p.vida_total) then 'DESCARTE'::text
           else 'ANÁLISE'::text
           end                                                       as destino_pneu
from pneu p
         join dados on dados.cod_pneu = p.codigo and dados.vida_analisada_pneu = p.vida_atual
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo
         join marca_pneu map on map.codigo = mp.cod_marca
         join view_pneu_km_rodado_total v on p.codigo = v.cod_pneu and p.vida_atual = v.vida_pneu
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo
group by u.nome, p.codigo, p.valor, p.vida_atual, p.status, p.vida_total, p.codigo_cliente, p.cod_unidade,
         dados.valor_banda, dados.valor_pneu, map.nome,
         mp.nome, dp.largura, dp.altura, dp.aro, dados.quantidade_afericoes_pneu_vida,
         dados.data_hora_primeira_afericao, dados.data_hora_ultima_afericao, dados.total_dias_ativo,
         dados.total_km_rodado_vida, dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto,
         dados.km_por_mm_vida, dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu;

create or replace view view_analise_pneus as
select p.cod_unidade                                                                  as cod_unidade_alocado,
       u.nome                                                                         as nome_unidade_alocado,
       p.codigo                                                                       as cod_pneu,
       p.codigo_cliente                                                               as cod_cliente_pneu,
       p.status                                                                       as status_pneu,
       map.nome                                                                       as nome_marca,
       mp.nome                                                                        as nome_modelo,
       dp.largura || '/' || dp.altura || ' R' || dp.aro                               as medidas,
       dados.qt_afericoes                                                             as qtd_afericoes,
       to_char(dados.primeira_afericao::timestamp with time zone, 'DD/MM/YYYY'::text) as data_primeira_afericao,
       to_char(dados.ultima_afericao::timestamp with time zone, 'DD/MM/YYYY'::text)   as data_ultima_afericao,
       dados.total_dias                                                               as dias_ativo,
       round(case
                 when dados.total_dias > 0 then dados.total_km / dados.total_dias::numeric
                 else null::numeric
           end)                                                                       as media_km_por_dia,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_externo,
       round(dados.maior_sulco::numeric, 2)                                           as maior_sulco_vida,
       round(dados.menor_sulco::numeric, 2)                                           as menor_sulco_vida,
       round(dados.sulco_gasto::numeric, 2)                                           as milimetros_gastos,
       round(dados.km_por_mm::numeric, 2)                                             as kms_por_milimetro,
       round((dados.km_por_mm * dados.sulco_restante)::numeric)                       as kms_a_percorrer,
       trunc(
               case
                   when dados.total_km > 0::numeric and dados.total_dias > 0 and
                        (dados.total_km / dados.total_dias::numeric) > 0::numeric then
                               dados.km_por_mm * dados.sulco_restante /
                               (dados.total_km / dados.total_dias::numeric)::double precision
                   else 0::double precision
                   end)                                                               as dias_restantes,
       case
           when dados.total_km > 0::numeric and dados.total_dias > 0 and
                (dados.total_km / dados.total_dias::numeric) > 0::numeric
               then (dados.km_por_mm * dados.sulco_restante /
                     (dados.total_km / dados.total_dias::numeric)::double precision)::integer +
                    current_date
           else null::date
           end                                                                        as previsao_troca
from pneu p
         join (select av.cod_pneu,
                      av.cod_unidade,
                      count(av.altura_sulco_central_interno)                                     as qt_afericoes,
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as primeira_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as ultima_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as total_dias,
                      max(total_km.total_km)                                                     as total_km,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))    as maior_sulco,
                      min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))       as menor_sulco,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))
                          - min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                      av.altura_sulco_central_externo, av.altura_sulco_externo)) as sulco_gasto,
                      case
                          when
                                  case
                                      when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_descarte
                                      when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_recapagem
                                      else null::real
                                      end < 0::double precision then 0::real
                          else
                              case
                                  when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_descarte
                                  when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_recapagem
                                  else null::real
                                  end
                          end                                                                    as sulco_restante,
                      case
                          when (max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                                min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date) > 0 then
                                      max(total_km.total_km)::double precision / max(
                                          greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                                              least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                    av.altura_sulco_central_externo, av.altura_sulco_externo))
                          else 0::double precision
                          end                                                                    as km_por_mm
               from afericao_valores av
                        join afericao a on a.codigo = av.cod_afericao
                        join pneu p_1 on p_1.codigo::text = av.cod_pneu::text and p_1.status::text = 'EM_USO'::text
                        join pneu_restricao_unidade pru on pru.cod_unidade = av.cod_unidade
                        join (select total_km_rodado.cod_pneu,
                                     total_km_rodado.cod_unidade,
                                     sum(total_km_rodado.km_rodado) as total_km
                              from (select av_1.cod_pneu,
                                           av_1.cod_unidade,
                                           max(a_1.km_veiculo) - min(a_1.km_veiculo) as km_rodado
                                    from afericao_valores av_1
                                             join afericao a_1 on a_1.codigo = av_1.cod_afericao
                                    group by av_1.cod_pneu, av_1.cod_unidade, a_1.cod_veiculo) total_km_rodado
                              group by total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                             on total_km.cod_pneu = av.cod_pneu and total_km.cod_unidade = av.cod_unidade
               group by av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                        pru.sulco_minimo_recapagem) dados on dados.cod_pneu = p.codigo
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca;

alter table dimensao_pneu add column data_hora_cadastro date default now();
alter table dimensao_pneu add column cod_colaborador_cadastro bigint;
alter table dimensao_pneu add column data_hora_ultima_atualizacao date;
alter table dimensao_pneu add column cod_colaborador_ultima_atualizacao bigint;

create or replace function suporte.func_pneu_cadastra_dimensao_pneu(f_altura bigint,
                                                                    f_largura bigint,
                                                                    f_aro real,
                                                                    out aviso_dimensao_criada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    cod_dimensao_existente bigint := (select codigo
                                      from dimensao_pneu
                                      where largura = f_largura
                                        and altura = f_altura
                                        and aro = f_aro);
    cod_dimensao_criada    bigint;
begin
    perform throw_generic_error('o cadastro de dimensão deve ser realizado pelo site ou pelo app');
    perform suporte.func_historico_salva_execucao();
    --verifica se os dados informados são maiores que 0.
    if(f_altura < 0)
    then
        raise exception 'o valor atribuído para altura deve ser maior que 0(zero). valor informado: %', f_altura;
    end if;

    if(f_largura < 0)
    then
        raise exception 'o valor atribuído para largura deve ser maior que 0(zero). valor informado: %', f_largura;
    end if;

    if(f_aro < 0)
    then
        raise exception 'o valor atribuído para aro deve ser maior que 0(zero). valor informado: %', f_aro;
    end if;

    --verifica se essa dimensão existe na base de dados.
    if (cod_dimensao_existente is not null)
    then
        raise exception 'erro! essa dimensão já está cadastrada, possui o código = %.', cod_dimensao_existente;
    end if;

    --adiciona nova dimensão e retorna seu id.
    insert into dimensao_pneu(altura, largura, aro)
    values (f_altura, f_largura, f_aro) returning codigo into cod_dimensao_criada;

    --mensagem de sucesso.
    select 'dimensão cadastrada com sucesso! dimensão: ' || f_largura || '/' || f_altura || 'r' || f_aro ||
           ' com código: '
               || cod_dimensao_criada || '.'
    into aviso_dimensao_criada;
end
$$;

alter table dimensao_pneu drop constraint unique_dimensao_pneu;
alter table dimensao_pneu add constraint  unique_dimensao_pneu unique (altura, largura, aro, cod_empresa);

insert into dimensao_pneu (altura, largura, aro, cod_empresa, status_ativo,
                           cod_colaborador_cadastro, data_hora_ultima_atualizacao, cod_colaborador_ultima_atualizacao)
select distinct d.altura, d.largura, d.aro, e.codigo, true, 2316, now(), 2316 from dimensao_pneu d join pneu_data pd on d.codigo = pd.cod_dimensao
                                            join empresa e on pd.cod_empresa = e.codigo;

