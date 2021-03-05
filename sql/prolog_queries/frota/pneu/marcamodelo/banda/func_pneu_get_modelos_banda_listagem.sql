CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                                F_COD_MARCA BIGINT,
                                                                F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_BANDA   BIGINT,
                NOME_MARCA_BANDA  TEXT,
                COD_MODELO_BANDA  BIGINT,
                NOME_MODELO_BANDA TEXT,
                QTD_SULCOS        SMALLINT,
                ALTURA_SULCOS     NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAB.CODIGO                             AS COD_MARCA_BANDA,
       MAB.NOME :: TEXT                       AS NOME_MARCA_BANDA,
       MOB.CODIGO                             AS COD_MODELO_BANDA,
       MOB.NOME :: TEXT                       AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOB.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_BANDA MAB
         LEFT JOIN MODELO_BANDA MOB
                   ON MAB.CODIGO = MOB.COD_MARCA
WHERE MAB.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_COD_MARCA IS NULL, TRUE, MAB.CODIGO = F_COD_MARCA)
  AND F_IF(F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE, TRUE, MOB.CODIGO IS NOT NULL)
ORDER BY NOME_MARCA_BANDA, NOME_MODELO_BANDA
$$;