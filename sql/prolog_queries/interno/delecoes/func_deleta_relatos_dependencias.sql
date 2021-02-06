-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta relatos de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_RELATOS_DEPENDENCIAS(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE FROM RELATO R WHERE R.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE FROM RELATO_ALTERNATIVA RA WHERE RA.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;