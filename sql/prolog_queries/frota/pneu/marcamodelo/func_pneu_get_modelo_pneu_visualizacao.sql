-- Sobre:
--
-- Esta function retorna um modelo de pneu.
--
--
-- Histórico:
-- 2019-10-15 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELO_PNEU_VISUALIZACAO(F_COD_MODELO BIGINT)
    RETURNS TABLE
            (
                COD_EMPRESA   BIGINT,
                COD_MARCA     BIGINT,
                COD_MODELO    BIGINT,
                NOME_MODELO   TEXT,
                QTD_SULCOS    SMALLINT,
                ALTURA_SULCOS NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MP.COD_EMPRESA,
       MP.COD_MARCA,
       MP.CODIGO,
       MP.NOME :: TEXT,
       MP.QT_SULCOS,
       TRUNC(MP.ALTURA_SULCOS :: NUMERIC, 2)
FROM MODELO_PNEU MP
WHERE MP.CODIGO = F_COD_MODELO;
$$;