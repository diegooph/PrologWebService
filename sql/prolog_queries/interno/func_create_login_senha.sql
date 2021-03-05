CREATE OR REPLACE FUNCTION INTERNO.FUNC_CREATE_LOGIN_SENHA(F_USERNAME TEXT, F_PASSWORD TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_CODIGO_USUARIO BIGINT;
BEGIN
    -- VERIFICA SE USUÁRIO JÁ EXISTE.
    IF EXISTS(SELECT PU.CODIGO, PU.PASSWORD FROM INTERNO.PROLOG_USER PU WHERE PU.USERNAME = F_USERNAME)
    THEN
        RAISE EXCEPTION
            'O usuário % já existe.', F_USERNAME;
    END IF;

    -- SE NÃO EXISTIR, CADASTRA.
    INSERT INTO INTERNO.PROLOG_USER (USERNAME, PASSWORD)
    VALUES (F_USERNAME, F_PASSWORD) RETURNING CODIGO INTO F_CODIGO_USUARIO;

    IF (F_CODIGO_USUARIO IS NULL OR F_CODIGO_USUARIO <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao criar login e senha');
    END IF;
END;
$$;