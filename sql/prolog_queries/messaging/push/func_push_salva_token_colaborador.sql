CREATE OR REPLACE FUNCTION
    MESSAGING.FUNC_PUSH_SALVA_TOKEN_COLABORADOR(F_COD_COLABORADOR BIGINT,
                                                F_TOKEN_COLABORADOR_LOGADO TEXT,
                                                F_APLICACAO_REFERENCIA_TOKEN MESSAGING.APLICACAO_REFERENCIA_TOKEN_TYPE,
                                                F_TOKEN_PUSH_FIREBASE TEXT,
                                                F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE) RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MESSAGING.PUSH_COLABORADOR_TOKEN (COD_COLABORADOR,
                                                  TOKEN_COLABORADOR_LOGADO,
                                                  APLICACAO_REFERENCIA_TOKEN,
                                                  TOKEN_PUSH_FIREBASE,
                                                  DATA_HORA_CADASTRO)
    VALUES (F_COD_COLABORADOR,
            F_TOKEN_COLABORADOR_LOGADO,
            F_APLICACAO_REFERENCIA_TOKEN,
            F_TOKEN_PUSH_FIREBASE,
            F_DATA_HORA_ATUAL)
    ON CONFLICT ON CONSTRAINT UNIQUE_TOKEN
        DO UPDATE SET COD_COLABORADOR          = F_COD_COLABORADOR,
                      TOKEN_COLABORADOR_LOGADO = F_TOKEN_COLABORADOR_LOGADO,
                      DATA_HORA_CADASTRO       = F_DATA_HORA_ATUAL;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao salvar token de push para o colaborador.
            cod_colaborador: %s - aplicacao_referencia_token: %s - token_push: %s',
            F_COD_COLABORADOR,
            F_APLICACAO_REFERENCIA_TOKEN,
            F_TOKEN_PUSH_FIREBASE;
    END IF;
END;
$$;