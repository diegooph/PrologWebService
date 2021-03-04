-- Sobre:
--
-- Function utilizada para buscar o código de um pneu dado o código do cliente (número de fogo) do pneu.
-- Essa function pode receber uma lista de número de fogo e retornará uma lista contendo o código do pneu referente a
-- cada número de fogo.
--
-- Histórico:
-- 2020-01-03 -> Function criada (diogenesvanzella - PLI-30)
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_COD_PNEU_BY_CODIGO_CLIENTE(F_COD_EMPRESA BIGINT, F_COD_CLIENTE TEXT[])
    RETURNS TABLE
            (
                COD_PNEU BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT P.CODIGO AS COD_PNEU
FROM PNEU P
WHERE P.COD_EMPRESA = F_COD_EMPRESA
  AND P.CODIGO_CLIENTE LIKE ANY (F_COD_CLIENTE);
$$;