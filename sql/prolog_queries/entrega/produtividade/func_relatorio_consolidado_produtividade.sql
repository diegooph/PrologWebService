create function func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint)
    returns TABLE("MATRICULA AMBEV" integer, "COLABORADOR" text, "FUNÇÃO" text, "CXS ENTREGUES" integer, "JORNADAS BATIDAS" bigint, "RESULTADO JORNADA" text, "DEV PDV" text, "META DEV PDV" text, "RECEBE BÔNUS" text, "VALOR BÔNUS" text, "Nº FATOR 1" bigint, "Nº FATOR 2" bigint, "Nº ROTAS" bigint, "VALOR ROTA" text, "Nº RECARGAS" bigint, "VALOR RECARGA" text, "Nº ELD" bigint, "DIFERENÇA ELD" text, "Nº AS" bigint, "VALOR AS" text, "Nº MAPAS TOTAL" bigint, "VALOR TOTAL" text)
    language sql
as
$$
SELECT
  matricula_ambev,
  initcap(nome_colaborador) AS "COLABORADOR",
  funcao AS "FUNÇÃO",
  trunc(sum(cxentreg))::INT        AS "CXS ENTREGUES",
  sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
    then 1 else 0 end ) as qtde_jornada_batida,
  trunc((sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
    then 1 else 0 end )::float / count(meta_jornada_liquida_horas))*100) || '%' as porcentagem_jornada,
  REPLACE(round( ((sum(entregasnaorealizadas + entregasparciais))::numeric /  nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric)*100, 2)::TEXT, '.', ',') || '%' as "DEV PDV",
  REPLACE(round((meta_dev_pdv * 100)::numeric, 2)::TEXT, '.', ',') || '%' AS "META DEV PDV",
  CASE WHEN round(1 - sum(entregascompletas)/ nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric, 4) <= meta_dev_pdv THEN
    'SIM' ELSE 'NÃO' END as "RECEBE BÔNUS",
  REPLACE(  (CASE WHEN round(1 - sum(entregascompletas)/ nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = ufp.cod_funcao_motorista THEN
    PCI.bonus_motorista
             WHEN round(1 - sum(entregascompletas)/ nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = ufp.cod_funcao_ajudante THEN
               PCI.bonus_ajudante
             ELSE 0 END)::TEXT, '.', ',') as "VALOR BÔNUS",
  sum(CASE WHEN fator = 1 then 1 else 0 end) as "Nº FATOR 1",
  sum(CASE WHEN fator = 2 then 1 else 0 end) as "Nº FATOR 2",
  sum(CASE WHEN valor_rota > 0 THEN 1 else 0 END) as "Nº ROTAS",
  REPLACE('R$ ' || trunc(sum(valor_rota)::NUMERIC, 2),'.', ',') AS "VALOR ROTA",
  sum(CASE WHEN valor_recarga > 0 THEN 1 else 0 END) as "Nº RECARGAS",
  REPLACE('R$ ' || trunc(sum(valor_recarga) :: NUMERIC, 2),'.', ',') AS "VALOR RECARGA",
  sum(CASE WHEN valor_diferenca_eld > 0 THEN 1 else 0 END) as "Nº ELD",
  REPLACE('R$ ' || trunc(sum(valor_DIFERENCA_ELD) :: NUMERIC, 2), '.', ',') AS "DIFERENÇA ELD" ,
  sum(CASE WHEN valor_as > 0 THEN 1 else 0 END) as "Nº AS",
  REPLACE('R$ ' || trunc(sum(valor_AS) :: NUMERIC, 2), '.', ',') AS "VALOR AS",
  sum(CASE WHEN valor > 0 THEN 1 else 0 END) as "Nº MAPAS TOTAL",
  REPLACE('R$ ' ||trunc(((CASE WHEN round(1 - sum(entregascompletas)/ nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = ufp.cod_funcao_motorista THEN
    PCI.bonus_motorista
                          WHEN round(1 - sum(entregascompletas)/ nullif(sum(entregascompletas+entregasparciais+entregasnaorealizadas), 0)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = ufp.cod_funcao_ajudante THEN
                            PCI.bonus_ajudante
                          ELSE 0 END) +
                         sum(valor)) :: NUMERIC, 2), '.', ',') AS "VALOR TOTAL"
FROM view_produtividade_extrato vpe
  LEFT JOIN pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
  LEFT JOIN unidade_funcao_produtividade ufp on ufp.cod_unidade = vpe.cod_unidade
WHERE vpe.cod_unidade = f_cod_unidade
     AND vpe.data BETWEEN f_dt_inicial AND f_dt_final
GROUP BY matricula_ambev, nome_colaborador, vpe.cod_funcao,funcao, meta_dev_pdv, ufp.cod_funcao_ajudante, ufp.cod_funcao_motorista, PCI.bonus_ajudante, PCI.bonus_motorista
ORDER BY nome_colaborador;
$$;