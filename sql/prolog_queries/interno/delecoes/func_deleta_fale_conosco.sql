-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta fale conosco de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_FALE_CONOSCO(F_COD_FALE_CONOSCO BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM FALE_CONOSCO FC
    WHERE FC.COD_UNIDADE = ANY (F_COD_FALE_CONOSCO);
END;
$$;