CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_UNIDADES(F_COD_EMPRESA_BASE BIGINT,
                                                       F_COD_EMPRESA_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO UNIDADE (NOME,
                         TOTAL_COLABORADORES,
                         COD_REGIONAL,
                         COD_EMPRESA,
                         TIMEZONE,
                         DATA_HORA_CADASTRO,
                         STATUS_ATIVO)
    SELECT U.NOME,
           U.TOTAL_COLABORADORES,
           U.COD_REGIONAL,
           F_COD_EMPRESA_USUARIO,
           U.TIMEZONE,
           now(),
           TRUE
    FROM UNIDADE U
    WHERE U.COD_EMPRESA = F_COD_EMPRESA_BASE;
END;
$$;