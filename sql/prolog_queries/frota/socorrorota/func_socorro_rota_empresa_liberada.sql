CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESAS_LIBERADAS BIGINT[] := (SELECT ARRAY_AGG(COD_EMPRESA) FROM SOCORRO_ROTA_EMPRESA_LIBERADA);
BEGIN
    -- Por enquanto apenas a fadel está utilizando esta funcionalidade.
    -- TODO: Criar estrutura própria para realizar essa verificação
    IF NOT F_COD_EMPRESA = ANY (F_COD_EMPRESAS_LIBERADAS) THEN
        PERFORM THROW_GENERIC_ERROR(
                        'A funcionalidade de Socorro em Rota não está liberada para a sua empresa, entre em contato com contato@prologapp.com para contratar!');
    END IF;
END;
$$;