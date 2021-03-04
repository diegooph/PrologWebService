-- Critério de aceitação: alterar as views das tabelas que tiveram alteração na PL-3170 e adicionar o código do veículo.

-- Aferição.
-- Drops de views necessários.
drop view view_pneu_analise_vida_atual;
drop view view_pneu_km_rodado_total;
drop view view_pneu_analise_vidas;
drop view view_pneu_km_rodado_vida;
drop view afericao;

create view afericao
            (codigo,
             data_hora,
             placa_veiculo,
             cod_veiculo,
             cpf_aferidor,
             km_veiculo,
             tempo_realizacao,
             tipo_medicao_coletada,
             cod_unidade,
             tipo_processo_coleta,
             forma_coleta_dados)
as
select ad.codigo,
       ad.data_hora,
       ad.placa_veiculo,
       ad.cod_veiculo,
       ad.cpf_aferidor,
       ad.km_veiculo,
       ad.tempo_realizacao,
       ad.tipo_medicao_coletada,
       ad.cod_unidade,
       ad.tipo_processo_coleta,
       ad.forma_coleta_dados
from afericao_data ad
where ad.deletado = false;

create view view_pneu_km_rodado_vida
            (cod_pneu,
             vida_pneu,
             km_rodado_vida)
as
select q.cod_pneu,
       q.vida_pneu,
       COALESCE(sum(q.km_rodado), 0::numeric) +
       ((select func_pneu_calcula_km_aplicacao_remocao_pneu(q.cod_pneu, q.vida_pneu)
                    as func_pneu_calcula_km_aplicacao_remocao_pneu)) as km_rodado_vida
from (select av.cod_pneu,
             av.vida_momento_afericao              as vida_pneu,
             max(a.km_veiculo) - min(a.km_veiculo) as km_rodado
      from afericao_valores av
               join afericao a on a.codigo = av.cod_afericao
      where a.tipo_processo_coleta::text = 'PLACA'::text
      group by av.cod_pneu, a.placa_veiculo, av.vida_momento_afericao
      order by av.cod_pneu) q
group by q.cod_pneu, q.vida_pneu
order by q.cod_pneu, q.vida_pneu;

-- Recria views que foram deletadas.
create view view_pneu_analise_vidas
            (cod_pneu,
             status,
             valor_pneu,
             valor_banda,
             data_hora_primeira_afericao,
             cod_primeira_afericao,
             cod_unidade_primeira_afericao,
             data_hora_ultima_afericao,
             cod_ultima_afericao,
             cod_unidade_ultima_afericao,
             vida_analisada_pneu,
             quantidade_afericoes_pneu_vida,
             maior_sulco_aferido_vida,
             menor_sulco_aferido_vida,
             sulco_gasto,
             total_dias_ativo,
             total_km_rodado_vida,
             sulco_restante,
             km_por_mm_vida,
             valor_por_km_vida)
as
with dados_afericao as (
    select a.codigo               as cod_afericao,
           a.cod_unidade          as cod_unidade_afericao,
           a.data_hora            as data_hora_afericao,
           a.tipo_processo_coleta as tipo_processo_coleta_afericao,
           av.cod_pneu,
           av.vida_momento_afericao,
           av.altura_sulco_central_interno,
           av.altura_sulco_central_externo,
           av.altura_sulco_externo,
           av.altura_sulco_interno,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora)
                                  as row_number_asc,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora desc)
                                  as row_number_desc
    from afericao_data a
             join afericao_valores_data av on a.codigo = av.cod_afericao
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
         select da.cod_pneu,
                da.vida_momento_afericao               as vida_analisada_pneu,
                count(da.cod_pneu)                     as quantidade_afericoes_pneu_vida,
                max(GREATEST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                             da.altura_sulco_interno)) as maior_sulco_aferido_vida,
                min(LEAST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                          da.altura_sulco_interno))    as menor_sulco_aferido_vida
         from dados_afericao da
         group by da.cod_pneu, da.vida_momento_afericao
     )
select p.codigo                                                                        as cod_pneu,
       p.status,
       p.valor                                                                         as valor_pneu,
       COALESCE(pvv.valor, 0::real)                                                    as valor_banda,
       pa.data_hora_afericao                                                           as data_hora_primeira_afericao,
       pa.cod_afericao                                                                 as cod_primeira_afericao,
       pa.cod_unidade_afericao                                                         as cod_unidade_primeira_afericao,
       ua.data_hora_afericao                                                           as data_hora_ultima_afericao,
       ua.cod_afericao                                                                 as cod_ultima_afericao,
       ua.cod_unidade_afericao                                                         as cod_unidade_ultima_afericao,
       aa.vida_analisada_pneu,
       aa.quantidade_afericoes_pneu_vida,
       aa.maior_sulco_aferido_vida,
       aa.menor_sulco_aferido_vida,
       aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida                       as sulco_gasto,
       date_part('days'::text, ua.data_hora_afericao - pa.data_hora_afericao)::integer as total_dias_ativo,
       km_rodado_pneu.km_rodado_vida                                                   as total_km_rodado_vida,
       func_pneu_calcula_sulco_restante(p.vida_atual,
                                        p.vida_total,
                                        p.altura_sulco_externo,
                                        p.altura_sulco_central_externo,
                                        p.altura_sulco_central_interno,
                                        p.altura_sulco_interno,
                                        pru.sulco_minimo_recapagem,
                                        pru.sulco_minimo_descarte)  as sulco_restante,
       case
           when date_part('days'::text, ua.data_hora_afericao - pa.data_hora_afericao) > 0::double precision and
                (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > 0::double precision then
                   km_rodado_pneu.km_rodado_vida::double precision /
                   (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida)
           else 0::double precision
           end                                                                         as km_por_mm_vida,
       case
           when km_rodado_pneu.km_rodado_vida = 0::numeric then 0::double precision
           else
               case
                   when km_rodado_pneu.vida_pneu = 1 then p.valor / km_rodado_pneu.km_rodado_vida::double precision
                   else COALESCE(pvv.valor, 0::real) / km_rodado_pneu.km_rodado_vida::double precision
                   end
           end                                                                         as valor_por_km_vida
from analises_afericoes aa
         join primeira_afericao pa on pa.cod_pneu = aa.cod_pneu and pa.vida_momento_afericao = aa.vida_analisada_pneu
         join ultima_afericao ua on ua.cod_pneu = aa.cod_pneu and ua.vida_momento_afericao = aa.vida_analisada_pneu
         join pneu_data p on aa.cod_pneu = p.codigo
         join pneu_restricao_unidade pru on p.cod_unidade = pru.cod_unidade
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu
         join view_pneu_km_rodado_vida km_rodado_pneu
              on km_rodado_pneu.cod_pneu = aa.cod_pneu and km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu
order by aa.cod_pneu, aa.vida_analisada_pneu;

create view view_pneu_km_rodado_total
            (cod_pneu,
             vida_pneu,
             km_rodado_vida,
             total_km_rodado_todas_vidas)
as
with km_rodado_total as (
    select view_pneu_km_rodado_vida.cod_pneu,
           sum(view_pneu_km_rodado_vida.km_rodado_vida) as total_km_rodado_todas_vidas
    from view_pneu_km_rodado_vida
    group by view_pneu_km_rodado_vida.cod_pneu
    order by view_pneu_km_rodado_vida.cod_pneu
)
select km_vida.cod_pneu,
       km_vida.vida_pneu,
       km_vida.km_rodado_vida,
       km_total.total_km_rodado_todas_vidas
from view_pneu_km_rodado_vida km_vida
         join km_rodado_total km_total on km_vida.cod_pneu = km_total.cod_pneu
order by km_vida.cod_pneu, km_vida.vida_pneu;

create view view_pneu_analise_vida_atual
            ("unidade alocado",
             "cod pneu",
             "cod pneu cliente",
             valor_acumulado,
             km_acumulado,
             "vida atual",
             "status pneu",
             cod_unidade,
             valor_pneu,
             valor_vida_atual,
             "marca",
             "modelo",
             "medidas",
             "qtd de aferições",
             "dta 1a aferição",
             "dta última aferição",
             "dias ativo",
             "média km por dia",
             "maior medição vida",
             "menor sulco atual",
             "milimetros gastos",
             "kms por milimetro",
             "valor por km",
             "valor por km acumulado",
             "kms a percorrer",
             "dias restantes",
             "previsão de troca",
             "destino")
as
select u.nome                                                             as "unidade alocado",
       p.codigo                                                           as "cod pneu",
       p.codigo_cliente                                                   as "cod pneu cliente",
       p.valor + sum(pvv.valor)                                           as valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                                 as km_acumulado,
       p.vida_atual                                                       as "vida atual",
       p.status                                                           as "status pneu",
       p.cod_unidade,
       p.valor                                                            as valor_pneu,
       case
           when dados.vida_analisada_pneu = 1 then dados.valor_pneu
           else dados.valor_banda
           end                                                            as valor_vida_atual,
       map.nome                                                           as "marca",
       mp.nome                                                            as "modelo",
       (((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro as "medidas",
       dados.quantidade_afericoes_pneu_vida                               as "qtd de aferições",
       to_char(dados.data_hora_primeira_afericao, 'DD/MM/YYYY'::text)     as "dta 1a aferição",
       to_char(dados.data_hora_ultima_afericao, 'DD/MM/YYYY'::text)       as "dta última aferição",
       dados.total_dias_ativo                                             as "dias ativo",
       round(
               case
                   when dados.total_dias_ativo > 0 then dados.total_km_rodado_vida / dados.total_dias_ativo::numeric
                   else null::numeric
                   end)                                                   as "média km por dia",
       round(dados.maior_sulco_aferido_vida::numeric, 2)                  as "maior medição vida",
       round(dados.menor_sulco_aferido_vida::numeric, 2)                  as "menor sulco atual",
       round(dados.sulco_gasto::numeric, 2)                               as "milimetros gastos",
       round(dados.km_por_mm_vida::numeric, 2)                            as "kms por milimetro",
       round(dados.valor_por_km_vida::numeric, 2)                         as "valor por km",
       round(
               case
                   when sum(v.total_km_rodado_todas_vidas) > 0::numeric
                       then
                           (p.valor + sum(pvv.valor)) /
                           sum(v.total_km_rodado_todas_vidas)::double precision
                   else 0::double precision
                   end::numeric, 2)                                       as "valor por km acumulado",
       round((dados.km_por_mm_vida * dados.sulco_restante)::numeric)      as "kms a percorrer",
       trunc(
               case
                   when dados.total_km_rodado_vida > 0::numeric and dados.total_dias_ativo > 0 and
                        (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric) > 0::numeric
                       then
                           dados.km_por_mm_vida * dados.sulco_restante /
                           (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric)::double precision
                   else 0::double precision
                   end)                                                   as "dias restantes",
       case
           when dados.total_km_rodado_vida > 0::numeric and dados.total_dias_ativo > 0 and
                (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric) > 0::numeric
               then
                   (dados.km_por_mm_vida * dados.sulco_restante /
                    (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric)::double precision)::integer +
                   'NOW'::text::date
           else null::date
           end                                                            as "previsão de troca",
       case
           when p.vida_atual = p.vida_total then 'DESCARTE'::text
           else 'ANÁLISE'::text
           end                                                            as "destino"
from pneu_data p
         join (select view_pneu_analise_vidas.cod_pneu,
                      view_pneu_analise_vidas.vida_analisada_pneu,
                      view_pneu_analise_vidas.status,
                      view_pneu_analise_vidas.valor_pneu,
                      view_pneu_analise_vidas.valor_banda,
                      view_pneu_analise_vidas.quantidade_afericoes_pneu_vida,
                      view_pneu_analise_vidas.data_hora_primeira_afericao,
                      view_pneu_analise_vidas.data_hora_ultima_afericao,
                      view_pneu_analise_vidas.total_dias_ativo,
                      view_pneu_analise_vidas.total_km_rodado_vida,
                      view_pneu_analise_vidas.maior_sulco_aferido_vida,
                      view_pneu_analise_vidas.menor_sulco_aferido_vida,
                      view_pneu_analise_vidas.sulco_gasto,
                      view_pneu_analise_vidas.sulco_restante,
                      view_pneu_analise_vidas.km_por_mm_vida,
                      view_pneu_analise_vidas.valor_por_km_vida
               from view_pneu_analise_vidas) dados
              on dados.cod_pneu = p.codigo and dados.vida_analisada_pneu = p.vida_atual
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca
         join view_pneu_km_rodado_total v on p.codigo = v.cod_pneu and p.vida_atual = v.vida_pneu
         left join pneu_valor_vida pvv on pvv.cod_pneu = p.codigo
group by u.nome, p.codigo, p.cod_unidade, dados.valor_banda, dados.valor_pneu, map.nome, mp.nome, dp.largura, dp.altura,
         dp.aro, dados.quantidade_afericoes_pneu_vida, dados.data_hora_primeira_afericao,
         dados.data_hora_ultima_afericao, dados.total_dias_ativo, dados.total_km_rodado_vida,
         dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto, dados.km_por_mm_vida,
         dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu
order by (
             case
                 when dados.total_km_rodado_vida > 0::numeric and dados.total_dias_ativo > 0 and
                      (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric) > 0::numeric
                     then
                         (dados.km_por_mm_vida * dados.sulco_restante /
                          (dados.total_km_rodado_vida / dados.total_dias_ativo::numeric)::double precision)::integer +
                         'NOW'::text::date
                 else null::date
                 end);


-- Checklist.
-- Drop de view necessário.
drop view checklist;
create view checklist
            (cod_unidade,
             cod_checklist_modelo,
             cod_versao_checklist_modelo,
             codigo,
             data_hora,
             data_hora_realizacao_tz_aplicado,
             data_hora_importado_prolog,
             cpf_colaborador,
             placa_veiculo,
             cod_veiculo,
             tipo,
             tempo_realizacao,
             km_veiculo,
             data_hora_sincronizacao,
             fonte_data_hora_realizacao,
             versao_app_momento_realizacao,
             versao_app_momento_sincronizacao,
             device_id,
             device_imei,
             device_uptime_realizacao_millis,
             device_uptime_sincronizacao_millis,
             foi_offline,
             total_perguntas_ok,
             total_perguntas_nok,
             total_alternativas_ok,
             total_alternativas_nok,
             total_midias_perguntas_ok,
             total_midias_alternativas_nok)
as
select cd.cod_unidade,
       cd.cod_checklist_modelo,
       cd.cod_versao_checklist_modelo,
       cd.codigo,
       cd.data_hora,
       cd.data_hora_realizacao_tz_aplicado,
       cd.data_hora_importado_prolog,
       cd.cpf_colaborador,
       cd.placa_veiculo,
       cd.cod_veiculo,
       cd.tipo,
       cd.tempo_realizacao,
       cd.km_veiculo,
       cd.data_hora_sincronizacao,
       cd.fonte_data_hora_realizacao,
       cd.versao_app_momento_realizacao,
       cd.versao_app_momento_sincronizacao,
       cd.device_id,
       cd.device_imei,
       cd.device_uptime_realizacao_millis,
       cd.device_uptime_sincronizacao_millis,
       cd.foi_offline,
       cd.total_perguntas_ok,
       cd.total_perguntas_nok,
       cd.total_alternativas_ok,
       cd.total_alternativas_nok,
       cd.total_midias_perguntas_ok,
       cd.total_midias_alternativas_nok
from checklist_data cd
where cd.deletado = false;