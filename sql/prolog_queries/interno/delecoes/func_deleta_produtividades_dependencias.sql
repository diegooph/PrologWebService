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
