-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta testes de aferidor de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TESTES_AFERIDOR(F_COD_TESTES_AFERIDOR BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM AFERIDOR.PROCEDIMENTO_TESTE PT
    WHERE PT.CODIGO = ANY (F_COD_TESTES_AFERIDOR);
END;
$$;