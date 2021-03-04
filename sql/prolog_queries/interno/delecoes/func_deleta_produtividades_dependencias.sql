-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta produtividades de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_PRODUTIVIDADES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM ACESSOS_PRODUTIVIDADE AP
    WHERE AP.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
