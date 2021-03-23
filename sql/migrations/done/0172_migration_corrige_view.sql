drop view view_pneu_analise_vida_atual;
create view view_pneu_analise_vida_atual as
SELECT u.nome                                                               AS "UNIDADE ALOCADO",
       p.codigo                                                             AS "COD PNEU",
       p.codigo_cliente                                                     AS "COD PNEU CLIENTE",
       (p.valor + sum(pvv.valor))                                           AS valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                                   AS km_acumulado,
       p.vida_atual                                                         AS "VIDA ATUAL",
       p.status                                                             AS "STATUS PNEU",
       p.cod_unidade,
       p.valor                                                              AS valor_pneu,
       CASE
           WHEN (dados.vida_analisada_pneu = 1) THEN dados.valor_pneu
           ELSE dados.valor_banda
           END                                                              AS valor_vida_atual,
       map.nome                                                             AS "MARCA",
       mp.nome                                                              AS "MODELO",
       ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
       dados.quantidade_afericoes_pneu_vida                                 AS "QTD DE AFERIÇÕES",
       to_char(dados.data_hora_primeira_afericao, 'DD/MM/YYYY'::text)       AS "DTA 1a AFERIÇÃO",
       to_char(dados.data_hora_ultima_afericao, 'DD/MM/YYYY'::text)         AS "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias_ativo                                               AS "DIAS ATIVO",
       round(
               CASE
                   WHEN (dados.total_dias_ativo > 0)
                       THEN (dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric)
                   ELSE NULL::numeric
                   END)                                                     AS "MÉDIA KM POR DIA",
       round((dados.maior_sulco_aferido_vida)::numeric, 2)                  AS "MAIOR MEDIÇÃO VIDA",
       round((dados.menor_sulco_aferido_vida)::numeric, 2)                  AS "MENOR SULCO ATUAL",
       round((dados.sulco_gasto)::numeric, 2)                               AS "MILIMETROS GASTOS",
       round((dados.km_por_mm_vida)::numeric, 2)                            AS "KMS POR MILIMETRO",
       round((dados.valor_por_km_vida)::numeric, 2)                         AS "VALOR POR KM",
       round((
                 CASE
                     WHEN (sum(v.total_km_rodado_todas_vidas) > (0)::numeric) THEN ((p.valor + sum(pvv.valor)) /
                                                                                    (sum(v.total_km_rodado_todas_vidas))::double precision)
                     ELSE (0)::double precision
                     END)::numeric, 2)                                      AS "VALOR POR KM ACUMULADO",
       round(((dados.km_por_mm_vida * dados.sulco_restante))::numeric)      AS "KMS A PERCORRER",
       trunc(
               CASE
                   WHEN (((dados.total_km_rodado_vida > (0)::numeric) AND (dados.total_dias_ativo > 0)) AND
                         ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) THEN (
                           (dados.km_por_mm_vida * dados.sulco_restante) /
                           ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision)
                   ELSE (0)::double precision
                   END)                                                     AS "DIAS RESTANTES",
       CASE
           WHEN (((dados.total_km_rodado_vida > (0)::numeric) AND (dados.total_dias_ativo > 0)) AND
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) THEN (
                   (((dados.km_por_mm_vida * dados.sulco_restante) /
                     ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                   ('NOW'::text)::date)
           ELSE NULL::date
           END                                                              AS "PREVISÃO DE TROCA",
       CASE
           WHEN (p.vida_atual = p.vida_total) THEN 'DESCARTE'::text
           ELSE 'ANÁLISE'::text
           END                                                              AS "DESTINO"
FROM (((((((pneu p
    JOIN (SELECT view_pneu_analise_vidas.cod_pneu,
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
          FROM view_pneu_analise_vidas) dados ON (((dados.cod_pneu = p.codigo) AND
                                                   (dados.vida_analisada_pneu = p.vida_atual))))
    JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
    JOIN unidade u ON ((u.codigo = p.cod_unidade)))
    JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
    JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)))
    JOIN view_pneu_km_rodado_total v ON (((p.codigo = v.cod_pneu) AND (p.vida_atual = v.vida_pneu))))
         LEFT JOIN pneu_valor_vida pvv ON ((pvv.cod_pneu = p.codigo)))
GROUP BY u.nome, p.codigo, p.valor, p.vida_atual, p.status, p.vida_total, p.codigo_cliente, p.cod_unidade,
         dados.valor_banda, dados.valor_pneu, map.nome,
         mp.nome, dp.largura, dp.altura, dp.aro, dados.quantidade_afericoes_pneu_vida,
         dados.data_hora_primeira_afericao, dados.data_hora_ultima_afericao, dados.total_dias_ativo,
         dados.total_km_rodado_vida, dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto,
         dados.km_por_mm_vida, dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu
ORDER BY CASE
             WHEN (((dados.total_km_rodado_vida > (0)::numeric) AND (dados.total_dias_ativo > 0)) AND
                   ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) THEN (
                     (((dados.km_por_mm_vida * dados.sulco_restante) /
                       ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                     ('NOW'::text)::date)
             ELSE NULL::date
             END;
