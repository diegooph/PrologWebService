CREATE OR REPLACE FUNCTION FUNC_GARANTE_UPDATE_OK(F_UPDATE_OK BOOLEAN, F_ERROR_MESSAGE TEXT DEFAULT NULL::TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_ERROR_MESSAGE TEXT := F_IF(F_ERROR_MESSAGE IS NULL, 'Erro ao realizar o update!', F_ERROR_MESSAGE);
BEGIN
    IF NOT F_UPDATE_OK
    THEN
        PERFORM THROW_GENERIC_ERROR(V_ERROR_MESSAGE);
    END IF;
END;
$$;