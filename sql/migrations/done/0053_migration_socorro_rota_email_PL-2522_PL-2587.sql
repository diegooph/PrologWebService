-- PL-2522.

create table messaging.email_log
(
    codigo               bigserial                not null,
    data_hora_log        timestamp with time zone not null,
    email_message_scope  text                     not null,
    request_to_api       jsonb,
    response_from_api    jsonb,
    fatal_send_exception text,
    constraint pk_email_log primary key (codigo)
);

comment on table messaging.email_log
    is 'Salva os logs de requisições e respostas de envio de e-mails feitas via API.';

comment on column messaging.email_log.request_to_api
    is 'Contém o JSON de cada request feito à API de e-mail.';

comment on column messaging.email_log.response_from_api
    is 'Contém o JSON de cada response recebido da API de e-mail.';

comment on column messaging.email_log.fatal_send_exception
    is 'Pode conter o stacktrace de uma exception fatal capturada na tentativa de realizar o envio de e-mails.
    Se existir algum conteúdo nessa coluna, nenhum e-mail pôde ser entregue, pois a falha foi geral. Nesse caso, não
    teremos JSON na coluna "response_from_api"';

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

DROP FUNCTION FUNC_SOCORRO_ROTA_ABERTURA_GET_COLABORADORES_NOTIFICACAO(BIGINT);

CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA_GET_COLABORADORES_NOTIFICACAO(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_COLABORADOR      BIGINT,
                EMAIL_COLABORADOR    TEXT,
                TOKENS_PUSH_FIREBASE TEXT[]
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PERMISSAO_TRATAR_SOCORRO CONSTANT BIGINT := 146;
    EMPTY_ARRAY              CONSTANT TEXT[] := '{}';
BEGIN
    RETURN QUERY
        WITH DADOS AS (
            SELECT C.CODIGO                                           AS COD_COLABORADOR,
                   CE.EMAIL::TEXT                                     AS EMAIL_COLABORADOR,
                   ARRAY_AGG(PCT.TOKEN_PUSH_FIREBASE)
                   FILTER (WHERE PCT.TOKEN_PUSH_FIREBASE IS NOT NULL) AS TOKENS_PUSH_FIREBASE
            FROM COLABORADOR C
                     JOIN CARGO_FUNCAO_PROLOG_V11 CFP
                          ON C.COD_UNIDADE = CFP.COD_UNIDADE
                              AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                     JOIN FUNCAO F
                          ON F.CODIGO = C.COD_FUNCAO
                              AND F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
                              AND C.COD_EMPRESA = F.COD_EMPRESA
                     LEFT JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT
                               ON C.CODIGO = PCT.COD_COLABORADOR
                                   -- Filtra apenas por aplicativos do Prolog.
                                   AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD')
                     LEFT JOIN COLABORADOR_EMAIL CE
                               ON C.CODIGO = CE.COD_COLABORADOR
            WHERE C.COD_UNIDADE = F_COD_UNIDADE
              AND C.STATUS_ATIVO = TRUE
              AND CFP.COD_FUNCAO_PROLOG = PERMISSAO_TRATAR_SOCORRO
            GROUP BY C.CODIGO, CE.EMAIL
        )

        SELECT D.COD_COLABORADOR                             AS COD_COLABORADOR,
               D.EMAIL_COLABORADOR                           AS EMAIL_COLABORADOR,
               COALESCE(D.TOKENS_PUSH_FIREBASE, EMPTY_ARRAY) AS TOKENS_PUSH_FIREBASE
        FROM DADOS D
        WHERE (D.TOKENS_PUSH_FIREBASE IS NOT NULL OR D.EMAIL_COLABORADOR IS NOT NULL);
END;
$$;

-- PL-2587.
DROP FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_SOCORRO_ROTA BIGINT);

CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_COLABORADOR_REQUEST BIGINT,
                                                                 F_COD_SOCORRO_ROTA BIGINT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                       BIGINT,
                STATUS_SOCORRO_ROTA                    SOCORRO_ROTA_STATUS_TYPE,
                PLACA_VEICULO_ABERTURA                 TEXT,
                COD_COLABORADOR_ABERTURA               BIGINT,
                NOME_RESPONSAVEL_ABERTURA              TEXT,
                KM_VEICULO_COLETADO_ABERTURA           BIGINT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA      TEXT,
                DESCRICAO_FORNECIDA_ABERTURA           TEXT,
                PONTO_REFERENCIA_FORNECIDO_ABERTURA    TEXT,
                DATA_HORA_ABERTURA                     TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ABERTURA                      TEXT,
                LONGITUDE_ABERTURA                     TEXT,
                ENDERECO_AUTOMATICO_ABERTURA           TEXT,
                MARCA_APARELHO_ABERTURA                TEXT,
                MODELO_APARELHO_ABERTURA               TEXT,
                IMEI_APARELHO_ABERTURA                 TEXT,
                URL_FOTO_1_ABERTURA                    TEXT,
                URL_FOTO_2_ABERTURA                    TEXT,
                URL_FOTO_3_ABERTURA                    TEXT,
                COD_COLABORADOR_ATENDIMENTO            BIGINT,
                NOME_RESPONSAVEL_ATENDIMENTO           TEXT,
                OBSERVACAO_ATENDIMENTO                 TEXT,
                TEMPO_ABERTURA_ATENDIMENTO_SEGUNDOS    BIGINT,
                DATA_HORA_ATENDIMENTO                  TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ATENDIMENTO                   TEXT,
                LONGITUDE_ATENDIMENTO                  TEXT,
                ENDERECO_AUTOMATICO_ATENDIMENTO        TEXT,
                MARCA_APARELHO_ATENDIMENTO             TEXT,
                MODELO_APARELHO_ATENDIMENTO            TEXT,
                IMEI_APARELHO_ATENDIMENTO              TEXT,
                LATITUDE_INICIO                        TEXT,
                LONGITUDE_INICIO                       TEXT,
                DATA_HORA_DESLOCAMENTO_INICIO          TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_FIM                           TEXT,
                LONGITUDE_FIM                          TEXT,
                DATA_HORA_DESLOCAMENTO_FIM             TIMESTAMP WITHOUT TIME ZONE,
                COD_COLABORADOR_INVALIDACAO            BIGINT,
                NOME_RESPONSAVEL_INVALIDACAO           TEXT,
                MOTIVO_INVALIDACAO                     TEXT,
                TEMPO_ABERTURA_INVALIDACAO_SEGUNDOS    BIGINT,
                TEMPO_ATENDIMENTO_INVALIDACAO_SEGUNDOS BIGINT,
                DATA_HORA_INVALIDACAO                  TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_INVALIDACAO                   TEXT,
                LONGITUDE_INVALIDACAO                  TEXT,
                ENDERECO_AUTOMATICO_INVALIDACAO        TEXT,
                MARCA_APARELHO_INVALIDACAO             TEXT,
                MODELO_APARELHO_INVALIDACAO            TEXT,
                IMEI_APARELHO_INVALIDACAO              TEXT,
                URL_FOTO_1_INVALIDACAO                 TEXT,
                URL_FOTO_2_INVALIDACAO                 TEXT,
                URL_FOTO_3_INVALIDACAO                 TEXT,
                COD_COLABORADOR_FINALIZACAO            BIGINT,
                NOME_RESPONSAVEL_FINALIZACAO           TEXT,
                OBSERVACAO_FINALIZACAO                 TEXT,
                TEMPO_ATENDIMENTO_FINALIZACAO_SEGUNDOS BIGINT,
                DATA_HORA_FINALIZACAO                  TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_FINALIZACAO                   TEXT,
                LONGITUDE_FINALIZACAO                  TEXT,
                ENDERECO_AUTOMATICO_FINALIZACAO        TEXT,
                MARCA_APARELHO_FINALIZACAO             TEXT,
                MODELO_APARELHO_FINALIZACAO            TEXT,
                IMEI_APARELHO_FINALIZACAO              TEXT,
                URL_FOTO_1_FINALIZACAO                 TEXT,
                URL_FOTO_2_FINALIZACAO                 TEXT,
                URL_FOTO_3_FINALIZACAO                 TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Busca o código da empresa com base no código da unidade do socorro em rota.
    V_COD_EMPRESA CONSTANT BIGINT := (SELECT COD_EMPRESA
                                      FROM UNIDADE
                                      WHERE CODIGO =
                                            (SELECT COD_UNIDADE
                                             FROM SOCORRO_ROTA
                                             WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA));

BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa.
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(V_COD_EMPRESA);

    -- Verifica se o colaborador que está realizando a requisição pode acessar o socorro que ele está tentando
    -- consultar.
    -- Isso é importante pois o socorro em rota é navegável no App via clicks no link e se o usuário forjar um link
    -- com código de um socorro que ele não tenha acesso, precisamos barrar.
    IF ((SELECT SR.COD_UNIDADE FROM SOCORRO_ROTA SR WHERE SR.CODIGO = F_COD_SOCORRO_ROTA)
        NOT IN
        (SELECT DISTINCT FCGUA.CODIGO_UNIDADE
         FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR_REQUEST) FCGUA))
    THEN
        PERFORM THROW_GENERIC_ERROR('Você não tem permissão para visualizar este pedido de socorro.');
    END IF;

    RETURN QUERY
        SELECT SR.CODIGO                                                               AS COD_SOCORRO_ROTA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                             AS STATUS_SOCORRO_ROTA,
               V.PLACA :: TEXT                                                         AS PLACA_VEICULO_ABERTURA,
               SRAB.COD_COLABORADOR_ABERTURA                                           AS COD_COLABORADOR_ABERTURA,
               CDAB.NOME :: TEXT                                                       AS NOME_RESPONSAVEL_ABERTURA,
               SRAB.KM_VEICULO_ABERTURA                                                AS KM_VEICULO_COLETADO_ABERTURA,
               SROP.DESCRICAO :: TEXT                                                  AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               SRAB.DESCRICAO_PROBLEMA :: TEXT                                         AS DESCRICAO_FORNECIDA_ABERTURA,
               SRAB.PONTO_REFERENCIA :: TEXT                                           AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)         AS DATA_HORA_ABERTURA,
               SRAB.LATITUDE_ABERTURA :: TEXT                                          AS LATITUDE_ABERTURA,
               SRAB.LONGITUDE_ABERTURA :: TEXT                                         AS LONGITUDE_ABERTURA,
               SRAB.ENDERECO_AUTOMATICO :: TEXT                                        AS ENDERECO_AUTOMATICO_ABERTURA,
               SRAB.MARCA_DEVICE_ABERTURA :: TEXT                                      AS MARCA_APARELHO_ABERTURA,
               SRAB.MODELO_DEVICE_ABERTURA :: TEXT                                     AS MODELO_APARELHO_ABERTURA,
               SRAB.DEVICE_IMEI_ABERTURA :: TEXT                                       AS IMEI_APARELHO_ABERTURA,
               SRAB.URL_FOTO_1_ABERTURA :: TEXT                                        AS URL_FOTO_1_ABERTURA,
               SRAB.URL_FOTO_2_ABERTURA :: TEXT                                        AS URL_FOTO_2_ABERTURA,
               SRAB.URL_FOTO_3_ABERTURA :: TEXT                                        AS URL_FOTO_3_ABERTURA,
               SRAT.COD_COLABORADOR_ATENDIMENTO                                        AS COD_COLABORADOR_ATENDIMENTO,
               CDAT.NOME :: TEXT                                                       AS NOME_RESPONSAVEL_ATENDIMENTO,
               SRAT.OBSERVACAO_ATENDIMENTO :: TEXT                                     AS OBSERVACAO_ATENDIMENTO,
               TO_SECONDS_TRUNC(
                       SRAT.DATA_HORA_ATENDIMENTO - SRAB.DATA_HORA_ABERTURA)           AS TEMPO_ABERTURA_ATENDIMENTO_SEGUNDOS,
               SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)      AS DATA_HORA_ATENDIMENTO,
               SRAT.LATITUDE_ATENDIMENTO :: TEXT                                       AS LATITUDE_ATENDIMENTO,
               SRAT.LONGITUDE_ATENDIMENTO :: TEXT                                      AS LONGITUDE_ATENDIMENTO,
               SRAT.ENDERECO_AUTOMATICO :: TEXT                                        AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT                                   AS MARCA_APARELHO_ATENDIMENTO,
               SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT                                  AS MODELO_APARELHO_ATENDIMENTO,
               SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT                                    AS IMEI_APARELHO_ATENDIMENTO,
               SRAD.LATITUDE_INICIO :: TEXT                                            AS LATITUDE_INICIO,
               SRAD.LONGITUDE_INICIO :: TEXT                                           AS LONGITUDE_INICIO,
               SRAD.DATA_HORA_DESLOCAMENTO_INICIO AT TIME ZONE
               TZ_UNIDADE(SR.COD_UNIDADE)                                              AS DATA_HORA_DESLOCAMENTO_INICIO,
               SRAD.LATITUDE_FIM :: TEXT                                               AS LATITUDE_FIM,
               SRAD.LONGITUDE_FIM :: TEXT                                              AS LONGITUDE_FIM,
               SRAD.DATA_HORA_DESLOCAMENTO_FIM AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_DESLOCAMENTO_FIM,
               SRI.COD_COLABORADOR_INVALIDACAO                                         AS COD_COLABORADOR_INVALIDACAO,
               CDI.NOME :: TEXT                                                        AS NOME_RESPONSAVEL_INVALIDACAO,
               SRI.MOTIVO_INVALIDACAO :: TEXT                                          AS MOTIVO_INVALIDACAO,
               TO_SECONDS_TRUNC(SRI.DATA_HORA_INVALIDACAO - SRAB.DATA_HORA_ABERTURA)   AS TEMPO_ABERTURA_INVALIDACAO_SEGUNDOS,
               TO_SECONDS_TRUNC(
                       SRI.DATA_HORA_INVALIDACAO - SRAT.DATA_HORA_ATENDIMENTO)         AS TEMPO_ATENDIMENTO_INVALIDACAO_SEGUNDOS,
               SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)       AS DATA_HORA_INVALIDACAO,
               SRI.LATITUDE_INVALIDACAO :: TEXT                                        AS LATITUDE_INVALIDACAO,
               SRI.LONGITUDE_INVALIDACAO :: TEXT                                       AS LONGITUDE_INVALIDACAO,
               SRI.ENDERECO_AUTOMATICO :: TEXT                                         AS ENDERECO_AUTOMATICO_INVALIDACAO,
               SRI.MARCA_DEVICE_INVALIDACAO :: TEXT                                    AS MARCA_APARELHO_INVALIDACAO,
               SRI.MODELO_DEVICE_INVALIDACAO :: TEXT                                   AS MODELO_APARELHO_INVALIDACAO,
               SRI.DEVICE_IMEI_INVALIDACAO :: TEXT                                     AS IMEI_APARELHO_INVALIDACAO,
               SRI.URL_FOTO_1_INVALIDACAO :: TEXT                                      AS URL_FOTO_1_INVALIDACAO,
               SRI.URL_FOTO_2_INVALIDACAO :: TEXT                                      AS URL_FOTO_2_INVALIDACAO,
               SRI.URL_FOTO_3_INVALIDACAO :: TEXT                                      AS URL_FOTO_3_INVALIDACAO,
               SRF.COD_COLABORADOR_FINALIZACAO                                         AS COD_COLABORADOR_FINALIZACAO,
               CDF.NOME :: TEXT                                                        AS NOME_RESPONSAVEL_FINALIZACAO,
               SRF.OBSERVACAO_FINALIZACAO :: TEXT                                      AS OBSERVACAO_FINALIZACAO,
               TO_SECONDS_TRUNC(
                       SRF.DATA_HORA_FINALIZACAO - SRAT.DATA_HORA_ATENDIMENTO)         AS TEMPO_ATENDIMENTO_FINALIZACAO_SEGUNDOS,
               SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)       AS DATA_HORA_FINALIZACAO,
               SRF.LATITUDE_FINALIZACAO :: TEXT                                        AS LATITUDE_FINALIZACAO,
               SRF.LONGITUDE_FINALIZACAO :: TEXT                                       AS LONGITUDE_FINALIZACAO,
               SRF.ENDERECO_AUTOMATICO :: TEXT                                         AS ENDERECO_AUTOMATICO_FINALIZACAO,
               SRF.MARCA_DEVICE_FINALIZACAO :: TEXT                                    AS MARCA_APARELHO_FINALIZACAO,
               SRF.MODELO_DEVICE_FINALIZACAO :: TEXT                                   AS MODELO_APARELHO_FINALIZACAO,
               SRF.DEVICE_IMEI_FINALIZACAO :: TEXT                                     AS IMEI_APARELHO_FINALIZACAO,
               SRF.URL_FOTO_1_FINALIZACAO :: TEXT                                      AS URL_FOTO_1_FINALIZACAO,
               SRF.URL_FOTO_2_FINALIZACAO :: TEXT                                      AS URL_FOTO_2_FINALIZACAO,
               SRF.URL_FOTO_3_FINALIZACAO :: TEXT                                      AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.CODIGO = SRAB.COD_SOCORRO_ROTA
                 JOIN VEICULO_DATA V ON V.CODIGO = SRAB.COD_VEICULO_PROBLEMA
                 JOIN COLABORADOR_DATA CDAB ON CDAB.CODIGO = SRAB.COD_COLABORADOR_ABERTURA
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SR.CODIGO = SRAT.COD_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO SRAD
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAD.COD_SOCORRO_ROTA_ATENDIMENTO = SRAT.CODIGO
                 LEFT JOIN COLABORADOR_DATA CDAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              CDAT.CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
                 LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
                 LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
        WHERE SR.CODIGO = F_COD_SOCORRO_ROTA;
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Não foi possível encontrar esse socorro');
    END IF;
END ;
$$;

end transaction;