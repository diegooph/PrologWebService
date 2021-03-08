CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                               F_COD_MARCA BIGINT,
                                                               F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_PNEU   BIGINT,
                NOME_MARCA_PNEU  TEXT,
                COD_MODELO_PNEU  BIGINT,
                NOME_MODELO_PNEU TEXT,
                QTD_SULCOS       SMALLINT,
                ALTURA_SULCOS    NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAP.CODIGO                             AS COD_MARCA_PNEU,
       MAP.NOME :: TEXT                       AS NOME_MARCA_PNEU,
       MOP.CODIGO                             AS COD_MODELO_PNEU,
       MOP.NOME :: TEXT                       AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOP.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_PNEU MAP
         LEFT JOIN MODELO_PNEU MOP
                   ON MAP.CODIGO = MOP.COD_MARCA
                       AND F_IF(F_COD_EMPRESA IS NULL, TRUE, MOP.COD_EMPRESA = F_COD_EMPRESA)
WHERE F_IF(F_COD_MARCA IS NULL, TRUE, MAP.CODIGO = F_COD_MARCA)
  AND F_IF(F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE, TRUE, MOP.CODIGO IS NOT NULL)
ORDER BY NOME_MARCA_PNEU, NOME_MODELO_PNEU
$$;