CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_KM_RODADO_POR_VIDA_BASE(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                UNIDADE_ALOCADO       TEXT,
                COD_PNEU              BIGINT,
                COD_CLIENTE_PNEU      TEXT,
                MARCA                 TEXT,
                MODELO                TEXT,
                DIMENSAO              TEXT,
                VIDA_PNEU             INTEGER,
                VALOR_VIDA            NUMERIC,
                KM_RODADO_VIDA        NUMERIC,
                VALOR_POR_KM_VIDA     TEXT,
                KM_RODADO_TODAS_VIDAS NUMERIC
            )
    LANGUAGE SQL
AS
$$
WITH PNEU_VALOR_TODAS_VIDAS AS (
    SELECT P.COD_UNIDADE                                                                    AS COD_UNIDADE_PNEU,
           P.CODIGO                                                                         AS COD_PNEU,
           P.CODIGO_CLIENTE                                                                 AS COD_CLIENTE_PNEU,
           P.COD_DIMENSAO                                                                   AS COD_DIMENSAO,
           P.COD_MODELO                                                                     AS COD_MODELO_PNEU,
           PVV.COD_MODELO_BANDA                                                             AS COD_MODELO_BANDA,
           -- A pvv só tem acima da primeira vida.
           -- Caso o pneu esteja na primeira vida o valor será pego da própria tabela pneu.
           COALESCE(PVV.VIDA, P.VIDA_ATUAL)                                                 AS VIDA_PNEU,
           F_IF(P.VIDA_ATUAL = 1, ROUND(P.VALOR::NUMERIC, 2), ROUND(PVV.VALOR::NUMERIC, 2)) AS VALOR_VIDA
    FROM PNEU P
             LEFT JOIN PNEU_VALOR_VIDA PVV
                       ON PVV.COD_PNEU = P.CODIGO
    WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
)

SELECT U.NOME                                                        AS UNIDADE_ALOCADO,
       PVTV.COD_PNEU                                                 AS COD_PNEU,
       PVTV.COD_CLIENTE_PNEU                                         AS COD_CLIENTE_PNEU,
       F_IF(PVTV.VIDA_PNEU = 1, MARCA_PNEU.NOME, MARCA_BANDA.NOME)   AS MARCA,
       F_IF(PVTV.VIDA_PNEU = 1, MODELO_PNEU.NOME, MODELO_BANDA.NOME) AS MODELO,
       FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.LARGURA, DP.ARO)     AS DIMENSAO,
       PVTV.VIDA_PNEU                                                AS VIDA_PNEU,
       PVTV.VALOR_VIDA                                               AS VALOR_VIDA,
       COALESCE(VP.KM_RODADO_VIDA, 0)                                AS KM_RODADO_VIDA,
       -- O nullif() nesse case serve para impedir erro de divisão por zero.
       COALESCE(
               ROUND((CASE
                          WHEN VP.VIDA_PNEU = 1
                              THEN PVTV.VALOR_VIDA / NULLIF(VP.KM_RODADO_VIDA, 0)
                          ELSE
                              COALESCE(PVTV.VALOR_VIDA, 0) / NULLIF(VP.KM_RODADO_VIDA, 0)
                   END)::NUMERIC, 3)::TEXT, '-')                     AS VALOR_POR_KM_VIDA,
       COALESCE(VP.TOTAL_KM_RODADO_TODAS_VIDAS, 0)                   AS KM_RODADO_TODAS_VIDAS
FROM PNEU_VALOR_TODAS_VIDAS PVTV
         JOIN MODELO_PNEU
              ON MODELO_PNEU.CODIGO = PVTV.COD_MODELO_PNEU
         JOIN MARCA_PNEU
              ON MARCA_PNEU.CODIGO = MODELO_PNEU.COD_MARCA
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = PVTV.COD_DIMENSAO
         JOIN UNIDADE U
              ON U.CODIGO = PVTV.COD_UNIDADE_PNEU
         LEFT JOIN MODELO_BANDA
                   ON MODELO_BANDA.CODIGO = PVTV.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA
                   ON MARCA_BANDA.CODIGO = MODELO_BANDA.COD_MARCA
         LEFT JOIN VIEW_PNEU_KM_RODADO_TOTAL VP
                   ON VP.COD_PNEU = PVTV.COD_PNEU AND VP.VIDA_PNEU = PVTV.VIDA_PNEU
ORDER BY U.CODIGO, PVTV.COD_CLIENTE_PNEU, PVTV.VIDA_PNEU;
$$;