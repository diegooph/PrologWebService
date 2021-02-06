CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_SALVA_LOG_INTEGRACAO(F_TOKEN_INTEGRACAO CHARACTER VARYING,
                                                                      F_RESPONSE_STATUS INTEGER,
                                                                      F_REQUEST_JSON JSONB,
                                                                      F_RESPONSE_JSON JSONB,
                                                                      F_DATA_HORA_REQUEST TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO INTEGRACAO.LOG_REQUEST_RESPONSE(COD_EMRESA,
                                                TOKEN_INTEGRACAO,
                                                RESPONSE_STATUS,
                                                REQUEST_JSON,
                                                RESPONSE_JSON,
                                                DATA_HORA_REQUEST)
    VALUES ((SELECT TI.COD_EMPRESA
             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO
             LIMIT 1),
            F_TOKEN_INTEGRACAO,
            F_RESPONSE_STATUS,
            F_REQUEST_JSON,
            F_RESPONSE_JSON,
            F_DATA_HORA_REQUEST);

    IF NOT FOUND
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('Não foi possível inserir o Log de request e response');
    END IF;
END;
$$;