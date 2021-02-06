-- Sobre:
-- Esta function salva o log de cada requisição feita à API de e-mail. Esse log contém tanto as informações da
-- requisição quanto da resposta da API.
--
-- Histórico:
-- 2020-02-28 -> Function criada (luizfp - PL-2522).
CREATE OR REPLACE FUNCTION MESSAGING.FUNC_EMAIL_SALVA_LOG(F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                          F_EMAIL_MESSAGE_SCOPE TEXT,
                                                          F_REQUEST_TO_API JSONB,
                                                          F_RESPONSE_FROM_API JSONB,
                                                          F_FATAL_SEND_EXCEPTION TEXT) RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MESSAGING.EMAIL_LOG(DATA_HORA_LOG,
                                    EMAIL_MESSAGE_SCOPE,
                                    REQUEST_TO_API,
                                    RESPONSE_FROM_API,
                                    FATAL_SEND_EXCEPTION)
    VALUES (F_DATA_HORA_ATUAL,
            F_EMAIL_MESSAGE_SCOPE,
            F_REQUEST_TO_API,
            F_RESPONSE_FROM_API,
            F_FATAL_SEND_EXCEPTION);

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao salvar log do e-mail';
    END IF;
END;
$$;