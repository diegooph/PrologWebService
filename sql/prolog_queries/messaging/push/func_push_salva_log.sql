-- Sobre:
-- Esta function salva o log de cada requisição feita ao FCM. Esse log contém tanto as informações da requisição quanto
-- da resposta da API.
--
-- Histórico:
-- 2020-01-31 -> Function criada (luizfp - PL-2496).
CREATE OR REPLACE FUNCTION MESSAGING.FUNC_PUSH_SALVA_LOG(F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                         F_PUSH_MESSAGE_SCOPE TEXT,
                                                         F_PUSH_MESSAGE_SENT JSONB,
                                                         F_MESSAGE_TYPE MESSAGING.PUSH_MESSAGE_TYPE,
                                                         F_PLATAFORM_DESTINATION MESSAGING.PUSH_PLATAFORM_DESTINATION,
                                                         F_REQUEST_RESPONSE_FIREBASE JSONB,
                                                         F_FATAL_SEND_EXCEPTION TEXT) RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MESSAGING.PUSH_LOG (DATA_HORA_LOG,
                                    PUSH_MESSAGE_SCOPE,
                                    PUSH_MESSAGE_SENT,
                                    MESSAGE_TYPE,
                                    PLATAFORM_DESTINATION,
                                    REQUEST_RESPONSE_FIREBASE,
                                    FATAL_SEND_EXCEPTION)
    VALUES (F_DATA_HORA_ATUAL,
            F_PUSH_MESSAGE_SCOPE,
            F_PUSH_MESSAGE_SENT,
            F_MESSAGE_TYPE,
            F_PLATAFORM_DESTINATION,
            F_REQUEST_RESPONSE_FIREBASE,
            F_FATAL_SEND_EXCEPTION);

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao salvar log da mensagem: %s', F_PUSH_MESSAGE_SENT;
    END IF;
END;
$$;