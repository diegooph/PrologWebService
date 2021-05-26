drop function func_relatorio_previsao_troca(f_data_inicial date,
    f_data_final date,
    f_cod_unidade text[],
    f_status_pneu character varying);

create or replace function func_pneu_relatorio_previsao_troca(f_data_final date,
                                                              f_cod_unidades bigint[],
                                                              f_status_pneu text)
    returns table
            (
                "UNIDADE ALOCADO"         text,
                "COD PNEU"                text,
                "STATUS"                  text,
                "VIDA ATUAL"              integer,
                "MARCA"                   text,
                "MODELO"                  text,
                "MEDIDAS"                 text,
                "PLACA APLICADO"          text,
                "POSIÇÃO APLICADO"        text,
                "QTD DE AFERIÇÕES"        bigint,
                "DATA 1ª AFERIÇÃO"        text,
                "DATA ÚLTIMA AFERIÇÃO"    text,
                "DIAS ATIVO"              integer,
                "MÉDIA KM POR DIA"        numeric,
                "MAIOR MEDIÇÃO VIDA"      numeric,
                "MENOR SULCO ATUAL"       numeric,
                "MILÍMETROS GASTOS"       numeric,
                "KMS POR MILÍMETRO"       numeric,
                "VALOR VIDA"              real,
                "VALOR ACUMULADO"         real,
                "VALOR POR KM VIDA ATUAL" numeric,
                "VALOR POR KM ACUMULADO"  numeric,
                "KMS A PERCORRER"         numeric,
                "DIAS RESTANTES"          double precision,
                "PREVISÃO DE TROCA"       text,
                "DESTINO"                 text
            )
    language sql
as
$$
select vap.nome_unidade_alocado                       as nome_unidade_alocado,
       vap.cod_cliente_pneu                           as cod_cliente_pneu,
       vap.status_pneu                                as status_pneu,
       vap.vida_atual                                 as vida_atual,
       vap.nome_marca                                 as nome_marca,
       vap.nome_modelo                                as nome_modelo,
       vap.medidas                                    as medidas,
       v.placa                                        as placa_aplicado,
       coalesce(ppne.nomenclatura, '-') :: text       as posicao_aplicado,
       vap.qtd_afericoes                              as qtd_afericoes,
       vap.data_primeira_afericao                     as data_primeira_afericao,
       vap.data_ultima_afericao                       as data_ultima_afericao,
       vap.dias_ativo                                 as dias_ativo,
       vap.media_km_por_dia                           as media_km_por_dia,
       vap.maior_sulco_vida                           as maior_sulco_vida,
       vap.menor_sulco_vida                           as menor_sulco_vida,
       vap.milimetros_gastos                          as milimetros_gastos,
       vap.kms_por_milimetro                          as kms_por_milimetro,
       vap.valor_vida_atual                           as valor_vida_atual,
       vap.valor_acumulado                            as valor_acumulado,
       vap.valor_por_km                               as valor_por_km,
       vap.valor_por_km_acumulado                     as valor_por_km_acumulado,
       vap.kms_a_percorrer                            as kms_a_percorrer,
       vap.dias_restantes_pneu                        as dias_restantes_pneu,
       to_char(vap.data_prevista_troca, 'DD/MM/YYYY') as data_prevista_troca,
       vap.destino_pneu                               as destino_pneu
from view_pneu_analise_vida_atual as vap
         join veiculo_pneu vp
              on vap.cod_pneu = vp.cod_pneu
         join veiculo v
              on vp.cod_veiculo = v.codigo
         left join veiculo_tipo vt
                   on v.cod_tipo = vt.codigo
         join empresa e on vt.cod_empresa = e.codigo
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and vp.posicao = ppne.posicao_prolog
where vap.cod_unidade = any (f_cod_unidades)
  and vap.data_prevista_troca <= f_data_final
  and vap.status_pneu like f_status_pneu
order by vap.nome_unidade_alocado;
$$;

drop function func_relatorio_pneu_previsao_troca_consolidado(f_cod_unidade text[],
    f_status_pneu text,
    f_data_inicial date,
    f_data_final date);
create function func_pneu_relatorio_previsao_troca_consolidado(f_cod_unidades bigint[],
                                                               f_status_pneu text,
                                                               f_data_inicial date,
                                                               f_data_final date)
    returns table
            (
                "UNIDADE"    text,
                data         text,
                marca        text,
                modelo       text,
                medidas      text,
                "QUANTIDADE" bigint
            )
    language sql
as
$$
select vap."UNIDADE ALOCADO",
       to_char(vap."PREVISÃO DE TROCA", 'DD/MM/YYYY') as data,
       vap."MARCA",
       vap."MODELO",
       vap."MEDIDAS",
       count(vap."MODELO")                            as quantidade
from view_analise_pneus vap
where vap.cod_unidade = any (f_cod_unidades)
  and vap."PREVISÃO DE TROCA" between f_data_inicial and f_data_final
  and vap."STATUS PNEU" = f_status_pneu
group by vap."UNIDADE ALOCADO", vap."PREVISÃO DE TROCA", vap."MARCA", vap."MODELO", vap."MEDIDAS"
order by vap."UNIDADE ALOCADO", vap."PREVISÃO DE TROCA", quantidade desc;
$$;

drop view view_pneu_analise_vida_atual;
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
       (dp.largura || '/' || dp.altura || ' R' || dp.aro)            as medidas,
       dados.quantidade_afericoes_pneu_vida                          as qtd_afericoes,
       to_char(dados.data_hora_primeira_afericao, 'DD/MM/YYYY')      as data_primeira_afericao,
       to_char(dados.data_hora_ultima_afericao, 'DD/MM/YYYY')        as data_ultima_afericao,
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

create or replace view view_pneu_analise_vidas as
with dados_afericao as (
    select a.codigo                                                                            as cod_afericao,
           a.cod_unidade                                                                       as cod_unidade_afericao,
           a.data_hora                                                                         as data_hora_afericao,
           a.tipo_processo_coleta                                                              as tipo_processo_coleta_afericao,
           av.cod_pneu                                                                         as cod_pneu,
           av.vida_momento_afericao                                                            as vida_momento_afericao,
           av.altura_sulco_central_interno                                                     as altura_sulco_central_interno,
           av.altura_sulco_central_externo                                                     as altura_sulco_central_externo,
           av.altura_sulco_externo                                                             as altura_sulco_externo,
           av.altura_sulco_interno                                                             as altura_sulco_interno,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora)      as row_number_asc,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora desc) as row_number_desc
    from afericao a
             join afericao_valores av on a.codigo = av.cod_afericao
),
     primeira_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where da.row_number_asc = 1
     ),
     ultima_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where da.row_number_desc = 1
     ),
     analises_afericoes as (
         select da.cod_pneu                            as cod_pneu,
                da.vida_momento_afericao               as vida_analisada_pneu,
                count(da.cod_pneu)                     as quantidade_afericoes_pneu_vida,
                max(greatest(da.altura_sulco_externo,
                             da.altura_sulco_central_externo,
                             da.altura_sulco_central_interno,
                             da.altura_sulco_interno)) as maior_sulco_aferido_vida,
                min(least(da.altura_sulco_externo,
                          da.altura_sulco_central_externo,
                          da.altura_sulco_central_interno,
                          da.altura_sulco_interno))    as menor_sulco_aferido_vida
         from dados_afericao da
         group by da.cod_pneu, da.vida_momento_afericao
     )
select p.codigo                                                                            as cod_pneu,
       p.status                                                                            as status,
       p.valor                                                                             as valor_pneu,
       coalesce(pvv.valor, 0::real)                                                        as valor_banda,
       pa.data_hora_afericao                                                               as data_hora_primeira_afericao,
       pa.cod_afericao                                                                     as cod_primeira_afericao,
       pa.cod_unidade_afericao                                                             as cod_unidade_primeira_afericao,
       ua.data_hora_afericao                                                               as data_hora_ultima_afericao,
       ua.cod_afericao                                                                     as cod_ultima_afericao,
       ua.cod_unidade_afericao                                                             as cod_unidade_ultima_afericao,
       aa.vida_analisada_pneu                                                              as vida_analisada_pneu,
       aa.quantidade_afericoes_pneu_vida                                                   as quantidade_afericoes_pneu_vida,
       aa.maior_sulco_aferido_vida                                                         as maior_sulco_aferido_vida,
       aa.menor_sulco_aferido_vida                                                         as menor_sulco_aferido_vida,
       (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida)                         as sulco_gasto,
       (date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)))::integer as total_dias_ativo,
       km_rodado_pneu.km_rodado_vida                                                       as total_km_rodado_vida,
       func_pneu_calcula_sulco_restante(p.vida_atual,
                                        p.vida_total,
                                        p.altura_sulco_externo,
                                        p.altura_sulco_central_externo,
                                        p.altura_sulco_central_interno,
                                        p.altura_sulco_interno,
                                        pru.sulco_minimo_recapagem,
                                        pru.sulco_minimo_descarte)                         as sulco_restante,
       case
           when ((date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)) > 0::double precision) and
                 ((aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > 0::double precision)) then (
                   (km_rodado_pneu.km_rodado_vida)::double precision /
                   (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida))
           else 0::double precision
           end                                                                             as km_por_mm_vida,
       case
           when (km_rodado_pneu.km_rodado_vida = 0::numeric) then 0::double precision
           else
               case
                   when (km_rodado_pneu.vida_pneu = 1)
                       then (p.valor / (km_rodado_pneu.km_rodado_vida)::double precision)
                   else (coalesce(pvv.valor, 0::real) / (km_rodado_pneu.km_rodado_vida)::double precision)
                   end
           end                                                                             as valor_por_km_vida
from analises_afericoes aa
         join primeira_afericao pa
              on pa.cod_pneu = aa.cod_pneu
                  and pa.vida_momento_afericao = aa.vida_analisada_pneu
         join ultima_afericao ua
              on ua.cod_pneu = aa.cod_pneu
                  and ua.vida_momento_afericao = aa.vida_analisada_pneu
         join pneu p on aa.cod_pneu = p.codigo
         join pneu_restricao_unidade pru on p.cod_unidade = pru.cod_unidade
         join view_pneu_km_rodado_vida km_rodado_pneu
              on km_rodado_pneu.cod_pneu = aa.cod_pneu
                  and km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu
order by aa.cod_pneu, aa.vida_analisada_pneu;