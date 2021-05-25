create or replace function func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint)
    returns table
            (
                "MATRICULA AMBEV"   integer,
                "COLABORADOR"       text,
                "FUNÇÃO"            text,
                "CXS ENTREGUES"     integer,
                "JORNADAS BATIDAS"  bigint,
                "RESULTADO JORNADA" text,
                "DEV PDV"           text,
                "META DEV PDV"      text,
                "RECEBE BÔNUS"      text,
                "VALOR BÔNUS"       text,
                "Nº FATOR 1"        bigint,
                "Nº FATOR 2"        bigint,
                "Nº ROTAS"          bigint,
                "VALOR ROTA"        text,
                "Nº RECARGAS"       bigint,
                "VALOR RECARGA"     text,
                "Nº ELD"            bigint,
                "DIFERENÇA ELD"     text,
                "Nº AS"             bigint,
                "VALOR AS"          text,
                "Nº MAPAS TOTAL"    bigint,
                "VALOR TOTAL"       text
            )
    language sql
as
$$
select matricula_ambev,
       initcap(nome_colaborador)                                                            as "COLABORADOR",
       funcao                                                                               as "FUNÇÃO",
       trunc(sum(cxentreg))::int                                                            as "CXS ENTREGUES",
       sum(case
               when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
                   then 1
               else 0 end)                                                                  as qtde_jornada_batida,
       trunc((sum(case
                      when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
                          then 1
                      else 0 end)::float / count(meta_jornada_liquida_horas)) * 100) || '%' as porcentagem_jornada,
       REPLACE(round(((sum(entregasnaorealizadas + entregasparciais))::numeric /
                      nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas), 0)::numeric) * 100,
                     2)::text, '.', ',') || '%'                                             as "DEV PDV",
       REPLACE(round((meta_dev_pdv * 100)::numeric, 2)::text, '.', ',') || '%'              as "META DEV PDV",
       case
           when round(1 - sum(entregascompletas) /
                          nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas), 0)::numeric, 4) <=
                meta_dev_pdv and sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
               then
               'SIM'
           else 'NÃO' end                                                                   as "RECEBE BÔNUS",
       REPLACE((case
                    when round(1 - sum(entregascompletas) /
                                   nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                          0)::numeric, 4) <= meta_dev_pdv and vpe.cod_funcao = ufp.cod_funcao_motorista
                        and sum(case when valor > 0 then 1 else 0 end) >= vpe.rm_numero_viagens
                        then
                        pci.bonus_motorista
                    when round(1 - sum(entregascompletas) /
                                   nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                          0)::numeric, 4) <= meta_dev_pdv and vpe.cod_funcao = ufp.cod_funcao_ajudante
                        and sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
                        then
                        pci.bonus_ajudante
                    else 0 end)::text, '.', ',')                                            as "VALOR BÔNUS",
       sum(case when fator = 1 then 1 else 0 end)                                           as "Nº FATOR 1",
       sum(case when fator = 2 then 1 else 0 end)                                           as "Nº FATOR 2",
       sum(case when valor_rota > 0 then 1 else 0 end)                                      as "Nº ROTAS",
       REPLACE('R$ ' || trunc(sum(valor_rota)::numeric, 2), '.', ',')                       as "VALOR ROTA",
       sum(case when valor_recarga > 0 then 1 else 0 end)                                   as "Nº RECARGAS",
       REPLACE('R$ ' || trunc(sum(valor_recarga) :: numeric, 2), '.', ',')                  as "VALOR RECARGA",
       sum(case when valor_diferenca_eld > 0 then 1 else 0 end)                             as "Nº ELD",
       REPLACE('R$ ' || trunc(sum(valor_diferenca_eld) :: numeric, 2), '.', ',')            as "DIFERENÇA ELD",
       sum(case when valor_as > 0 then 1 else 0 end)                                        as "Nº AS",
       REPLACE('R$ ' || trunc(sum(valor_as) :: numeric, 2), '.', ',')                       as "VALOR AS",
       sum(case when valor > 0 then 1 else 0 end)                                           as "Nº MAPAS TOTAL",
       REPLACE('R$ ' || trunc(((case
                                    when sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
                                        then (case
                                                  when round(1 - sum(entregascompletas) / nullif(
                                                          sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                                          0)::numeric, 4) <= meta_dev_pdv and
                                                       vpe.cod_funcao = ufp.cod_funcao_motorista then
                                                      pci.bonus_motorista
                                                  when round(1 - sum(entregascompletas) / nullif(
                                                          sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                                          0)::numeric, 4) <= meta_dev_pdv and
                                                       vpe.cod_funcao = ufp.cod_funcao_ajudante
                                                      then
                                                      pci.bonus_ajudante
                                                  else 0 end)
                                    else 0 end) +
                               sum(valor)) :: numeric, 2), '.', ',')                        as "VALOR TOTAL"
from view_produtividade_extrato_com_total vpe
         left join pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
         left join unidade_funcao_produtividade ufp on ufp.cod_unidade = vpe.cod_unidade
where vpe.cod_unidade = f_cod_unidade
  and vpe.data between f_dt_inicial and f_dt_final
group by matricula_ambev, nome_colaborador, vpe.cod_funcao, funcao, meta_dev_pdv, ufp.cod_funcao_ajudante,
         ufp.cod_funcao_motorista, pci.bonus_ajudante, pci.bonus_motorista, vpe.rm_numero_viagens
order by nome_colaborador;
$$;