-- Sobre:
--
-- Esta function retorna um modelo de banda.
--
--
-- Histórico:
-- 2019-10-15 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELO_BANDA_VISUALIZACAO(F_COD_MODELO_BANDA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_BANDA            BIGINT,
                NOME_MARCA_BANDA           TEXT,
                COD_MODELO_BANDA           BIGINT,
                NOME_MODELO_BANDA          TEXT,
                QT_SULCOS_MODELO_BANDA     SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAB.CODIGO                            AS COD_MARCA_BANDA,
       MAB.NOME :: TEXT                      AS NOME_MARCA_BANDA,
       MOB.CODIGO                            AS COD_MODELO_BANDA,
       MOB.NOME :: TEXT                      AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                         AS QT_SULCOS_MODELO_BANDA,
       TRUNC(MOB.ALTURA_SULCOS ::NUMERIC, 2) AS ALTURA_SULCOS_MODELO_BANDA
FROM MODELO_BANDA MOB
         JOIN MARCA_BANDA MAB
              ON MOB.COD_MARCA = MAB.CODIGO
                  AND MOB.COD_EMPRESA = MAB.COD_EMPRESA
WHERE MOB.CODIGO = F_COD_MODELO_BANDA;
$$;