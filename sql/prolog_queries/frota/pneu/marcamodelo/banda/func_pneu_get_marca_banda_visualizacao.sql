-- Sobre:
--
-- Esta function busca uma marca específica de banda pelo seu código.
--
--
-- Histórico:
-- 2019-10-25 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCA_BANDA_VISUALIZACAO(F_COD_MARCA BIGINT)
    RETURNS TABLE
            (
                CODIGO BIGINT,
                NOME   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MB.CODIGO       AS COD_MARCA_PNEU,
       MB.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_BANDA MB
WHERE MB.CODIGO = F_COD_MARCA;
$$;