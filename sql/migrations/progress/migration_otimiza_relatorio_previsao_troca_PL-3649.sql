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
                   else (0)::double precision
                   end)                                              as dias_restantes_pneu,
       case
           when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
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