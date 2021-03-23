create function deprecated_func_relatorio_pneu_problemas_placas_extrato(f_placa_veiculo text)
  returns TABLE("PLACA" text, "CODIGO" character varying, "POSIÇÃO" text, "SULCO EXTERNO" numeric, "SULCO CENTRAL EXTERNO" numeric, "SULCO CENTRAL INTERNO" numeric, "SULCO INTERNO" numeric, "PROBLEMA PRESSÃO" text, "PROBLEMA CAMBAGEM" text)
language sql
as $$
SELECT
  d.placa,
  d.codigo,
  d.nomenclatura,
  d.s_externo,
  d.s_central_externo,
  d.s_central_interno,
  d.s_interno,
  CASE WHEN D.diferenca_sulcos < 0.8
    THEN 'BAIXA PRESSAO'
  WHEN d.diferenca_sulcos > 1.2
    THEN 'ALTA PRESSAO'
  ELSE '-'
  END AS HISTORICO_PRESSAO,
  d.cambagem
FROM
  (SELECT
     P.CODIGO,
     round(P.altura_sulco_interno :: NUMERIC, 2)                             AS s_interno,
     round(P.altura_sulco_central_interno :: NUMERIC, 2)                     AS s_central_interno,
     round(P.altura_sulco_central_externo :: NUMERIC, 2)                     AS s_central_externo,
     round(P.altura_sulco_externo :: NUMERIC, 2)                             AS s_externo,
     round(LEAST(P.altura_sulco_interno, P.altura_sulco_central_interno, P.altura_sulco_central_externo,
                 P.altura_sulco_externo) :: NUMERIC, 2)                      AS MENOR_SULCO,
     CASE WHEN (P.altura_sulco_central_externo + P.altura_sulco_central_interno) = 0
       THEN 0
     ELSE
       (P.altura_sulco_externo + P.altura_sulco_interno) /
       (P.altura_sulco_central_externo + P.altura_sulco_central_interno) END AS diferenca_sulcos,

     CASE WHEN P.altura_sulco_externo =
               LEAST(P.altura_sulco_interno, P.altura_sulco_central_interno, P.altura_sulco_central_externo,
                     P.altura_sulco_externo)
               AND P.altura_sulco_central_externo =
                   LEAST(P.altura_sulco_interno, P.altura_sulco_central_interno, P.altura_sulco_central_externo)
               AND P.altura_sulco_central_interno = LEAST(P.altura_sulco_interno, P.altura_sulco_central_interno)
               AND NOT (p.altura_sulco_central_externo = p.altura_sulco_central_interno AND
                        p.altura_sulco_central_externo = p.altura_sulco_externo
                        AND p.altura_sulco_central_externo = p.altura_sulco_interno)
       THEN 'POSITIVA'
     WHEN P.altura_sulco_interno =
          LEAST(P.altura_sulco_interno, P.altura_sulco_central_interno, P.altura_sulco_central_externo,
                P.altura_sulco_externo)
          AND P.altura_sulco_central_interno =
              LEAST(P.altura_sulco_externo, P.altura_sulco_central_interno, P.altura_sulco_central_externo)
          AND P.altura_sulco_central_externo = LEAST(P.altura_sulco_externo, P.altura_sulco_central_externo)
          AND NOT (p.altura_sulco_central_externo = p.altura_sulco_central_interno AND
                   p.altura_sulco_central_externo = p.altura_sulco_externo
                   AND p.altura_sulco_central_externo = p.altura_sulco_interno)
       THEN 'NEGATIVA'
     ELSE '-' END                                                            AS cambagem,
     VP.PLACA,
     ordem.nomenclatura,
     P.STATUS
   FROM PNEU P
     LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU AND VP.COD_UNIDADE = P.COD_UNIDADE
     LEFT JOIN VEICULO V ON V.PLACA = VP.placa and v.cod_unidade = vp.cod_unidade
     LEFT JOIN pneu_ordem_nomenclatura_unidade ORDEM
       ON ORDEM.cod_unidade = P.COD_UNIDADE AND ORDEM.cod_tipo_veiculo = V.cod_tipo and vp.posicao = ordem.posicao_prolog
  where vp.placa like upper(f_placa_veiculo)) AS D
order by 1
$$;