CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA(F_COD_EMPRESA BIGINT,
                                                            F_NOME_EMPRESA TEXT,
                                                            F_ERROR_MESSAGE TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_ERROR_MESSAGE TEXT :=
        F_IF(F_ERROR_MESSAGE IS NULL,
             FORMAT('Empresa de código %s com nome %s não existe!', F_COD_EMPRESA, F_NOME_EMPRESA), F_ERROR_MESSAGE);
BEGIN
    IF NOT EXISTS(SELECT E.CODIGO
                  FROM EMPRESA E
                  WHERE E.CODIGO = F_COD_EMPRESA
                    AND E.NOME = F_NOME_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(V_ERROR_MESSAGE);
    END IF;
END;
$$;