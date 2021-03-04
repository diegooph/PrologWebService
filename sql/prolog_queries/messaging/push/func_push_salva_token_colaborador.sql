-- Sobre:
-- Esta function salva um token de push notification para um colaborador e aplicação específicos.
--
-- Na estrutura do FCM, um token é vinculado a um aparelho e aplicação (aplicativo), assim, o mesmo colaborador pode
-- ter um token válido para cada aplicação mapeada no MESSAGING.APLICACAO_REFERENCIA_TOKEN_TYPE.
--
-- Caso o insert falhe violando a unique UNIQUE_COLABORADOR_POR_APLICACAO, será feito um UPDATE atualizando o token e
-- a DATA_HORA_CADASTRO.
-- Como a tabela de token possui audit ativado, podemos saber as mudanças de token que um colaborador teve para uma
-- mesma aplicação, caso necessário.
--
-- Histórico:
-- 2020-01-31 -> Function criada (luizfp - PL-2496).
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