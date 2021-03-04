-- #####################################################################################################################
-- #####################################################################################################################
-- ################## Adiciona a verificação e registro de deslocamento de socorros em atendimento #####################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2631

-- Cria tabela para armazenar os registros de deslocamento.
CREATE TABLE PUBLIC.SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO
(
    COD_SOCORRO_ROTA_ATENDIMENTO       BIGINT                              NOT NULL
        CONSTRAINT PK_ATENDIMENTO_DESLOCAMENTO
            PRIMARY KEY,
    DATA_HORA_DESLOCAMENTO_INICIO      TIMESTAMP WITH TIME ZONE            NOT NULL,
    LATITUDE_INICIO                    TEXT                                NOT NULL,
    LONGITUDE_INICIO                   TEXT                                NOT NULL,
    PRECISAO_LOCALIZACAO_INICIO_METROS NUMERIC                             NOT NULL,
    ENDERECO_AUTOMATICO_INICIO         TEXT,
    DEVICE_ID_INICIO                   TEXT,
    DEVICE_IMEI_INICIO                 TEXT,
    DEVICE_UPTIME_MILLIS_INICIO        BIGINT,
    ANDROID_API_VERSION_INICIO         INTEGER,
    MARCA_DEVICE_INICIO                TEXT,
    MODELO_DEVICE_INICIO               TEXT,
    PLATAFORMA_ORIGEM_INICIO           PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE NOT NULL,
    VERSAO_PLATAFORMA_ORIGEM_INICIO    TEXT                                NOT NULL,
    DATA_HORA_DESLOCAMENTO_FIM         TIMESTAMP WITH TIME ZONE,
    LATITUDE_FIM                       TEXT,
    LONGITUDE_FIM                      TEXT,
    PRECISAO_LOCALIZACAO_FIM_METROS    NUMERIC,
    ENDERECO_AUTOMATICO_FIM            TEXT,
    DEVICE_ID_FIM                      TEXT,
    DEVICE_IMEI_FIM                    TEXT,
    DEVICE_UPTIME_MILLIS_FIM           BIGINT,
    ANDROID_API_VERSION_FIM            INTEGER,
    MARCA_DEVICE_FIM                   TEXT,
    MODELO_DEVICE_FIM                  TEXT,
    PLATAFORMA_ORIGEM_FIM              PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
    VERSAO_PLATAFORMA_ORIGEM_FIM       TEXT,
    CONSTRAINT SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO_SOCORRO_ROTA_ATENDIMENTO_FK
        FOREIGN KEY (COD_SOCORRO_ROTA_ATENDIMENTO) REFERENCES PUBLIC.SOCORRO_ROTA_ATENDIMENTO (CODIGO)
);

-- Dropa a function antiga de atendimento.
DROP FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_ATENDIMENTO(F_COD_SOCORRO_ROTA BIGINT,
    F_COD_COLABORADOR_ATENDIMENTO BIGINT,
    F_OBSERVACAO_ATENDIMENTO TEXT,
    F_DATA_HORA_ATENDIMENTO TIMESTAMP WITH TIME ZONE,
    F_LATITUDE_ATENDIMENTO TEXT,
    F_LONGITUDE_ATENDIMENTO TEXT,
    F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS NUMERIC,
    F_ENDERECO_AUTOMATICO TEXT,
    F_DEVICE_ID_ATENDIMENTO TEXT,
    F_DEVICE_IMEI_ATENDIMENTO TEXT,
    F_DEVICE_UPTIME_MILLIS_ATENDIMENTO BIGINT,
    F_ANDROID_API_VERSION_ATENDIMENTO INTEGER,
    F_MARCA_DEVICE_ATENDIMENTO TEXT,
    F_MODELO_DEVICE_ATENDIMENTO TEXT,
    F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
    F_VERSAO_PLATAFORMA_ORIGEM TEXT);

-- Recria a function de atendimento com a verificação e registro de deslocamento iniciado.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_ATENDIMENTO(F_COD_SOCORRO_ROTA BIGINT,
                                                                F_COD_COLABORADOR_ATENDIMENTO BIGINT,
                                                                F_OBSERVACAO_ATENDIMENTO TEXT,
                                                                F_DATA_HORA_ATENDIMENTO TIMESTAMP WITH TIME ZONE,
                                                                F_LATITUDE_ATENDIMENTO TEXT,
                                                                F_LONGITUDE_ATENDIMENTO TEXT,
                                                                F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS NUMERIC,
                                                                F_ENDERECO_AUTOMATICO TEXT,
                                                                F_DEVICE_ID_ATENDIMENTO TEXT,
                                                                F_DEVICE_IMEI_ATENDIMENTO TEXT,
                                                                F_DEVICE_UPTIME_MILLIS_ATENDIMENTO BIGINT,
                                                                F_ANDROID_API_VERSION_ATENDIMENTO INTEGER,
                                                                F_MARCA_DEVICE_ATENDIMENTO TEXT,
                                                                F_MODELO_DEVICE_ATENDIMENTO TEXT,
                                                                F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                                F_VERSAO_PLATAFORMA_ORIGEM TEXT,
                                                                F_DESLOCAMENTO_INICIADO BOOLEAN) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    V_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    V_COD_SOCORRO_ATENDIMENTO_INSERIDO BIGINT;
    V_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe e valida o status.
    IF V_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível localizar o socorro em rota.');
    END IF;

    IF V_STATUS_SOCORRO = 'EM_ATENDIMENTO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível atender o socorro, pois ele já foi atendido.');
    END IF;
    IF V_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível atender o socorro, pois ele já foi invalidado.');
    END IF;
    IF V_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível atender o socorro, pois ele já foi finalizado.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- Insere as informações de atendimento.
    INSERT INTO SOCORRO_ROTA_ATENDIMENTO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_ATENDIMENTO,
                                          OBSERVACAO_ATENDIMENTO,
                                          DATA_HORA_ATENDIMENTO,
                                          LATITUDE_ATENDIMENTO,
                                          LONGITUDE_ATENDIMENTO,
                                          PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_ATENDIMENTO,
                                          DEVICE_IMEI_ATENDIMENTO,
                                          DEVICE_UPTIME_MILLIS_ATENDIMENTO,
                                          ANDROID_API_VERSION_ATENDIMENTO,
                                          MARCA_DEVICE_ATENDIMENTO,
                                          MODELO_DEVICE_ATENDIMENTO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_ATENDIMENTO,
            F_OBSERVACAO_ATENDIMENTO,
            F_DATA_HORA_ATENDIMENTO,
            F_LATITUDE_ATENDIMENTO,
            F_LONGITUDE_ATENDIMENTO,
            F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_ATENDIMENTO,
            F_DEVICE_IMEI_ATENDIMENTO,
            F_DEVICE_UPTIME_MILLIS_ATENDIMENTO,
            F_ANDROID_API_VERSION_ATENDIMENTO,
            F_MARCA_DEVICE_ATENDIMENTO,
            F_MODELO_DEVICE_ATENDIMENTO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO V_COD_SOCORRO_ATENDIMENTO_INSERIDO;

    -- Verifica se os dados de atendimento foram inseridos.
    IF V_COD_SOCORRO_ATENDIMENTO_INSERIDO IS NOT NULL AND V_COD_SOCORRO_ATENDIMENTO_INSERIDO > 0
    THEN
        -- Verifica se o deslocamento foi iniciado e seta as informações na tabela específica.
        IF F_DESLOCAMENTO_INICIADO
        THEN
            INSERT INTO SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO(COD_SOCORRO_ROTA_ATENDIMENTO,
                                                              DATA_HORA_DESLOCAMENTO_INICIO,
                                                              LATITUDE_INICIO,
                                                              LONGITUDE_INICIO,
                                                              PRECISAO_LOCALIZACAO_INICIO_METROS,
                                                              ENDERECO_AUTOMATICO_INICIO,
                                                              VERSAO_PLATAFORMA_ORIGEM_INICIO,
                                                              DEVICE_ID_INICIO,
                                                              DEVICE_IMEI_INICIO,
                                                              DEVICE_UPTIME_MILLIS_INICIO,
                                                              ANDROID_API_VERSION_INICIO,
                                                              MARCA_DEVICE_INICIO,
                                                              MODELO_DEVICE_INICIO,
                                                              PLATAFORMA_ORIGEM_INICIO)
            VALUES (V_COD_SOCORRO_ATENDIMENTO_INSERIDO,
                    F_DATA_HORA_ATENDIMENTO,
                    F_LATITUDE_ATENDIMENTO,
                    F_LONGITUDE_ATENDIMENTO,
                    F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
                    F_ENDERECO_AUTOMATICO,
                    F_VERSAO_PLATAFORMA_ORIGEM,
                    F_DEVICE_ID_ATENDIMENTO,
                    F_DEVICE_IMEI_ATENDIMENTO,
                    F_DEVICE_UPTIME_MILLIS_ATENDIMENTO,
                    F_ANDROID_API_VERSION_ATENDIMENTO,
                    F_MARCA_DEVICE_ATENDIMENTO,
                    F_MODELO_DEVICE_ATENDIMENTO,
                    F_PLATAFORMA_ORIGEM);

            -- Retorna erro caso não consiga inserir dados na tabela de informações de deslocamento.
            IF NOT FOUND
            THEN
                PERFORM THROW_GENERIC_ERROR(
                                'Não foi possível realizar o atendimento desse socorro em rota, tente novamente.');
            END IF;
        END IF;

        -- Atualiza o status do socorro.
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL    = 'EM_ATENDIMENTO',
            COD_ATENDIMENTO = V_COD_SOCORRO_ATENDIMENTO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar o atendimento desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR('Não foi possível realizar a atendimento desse socorro em rota, tente novamente.');
    END IF;

    RETURN V_COD_SOCORRO_ATENDIMENTO_INSERIDO;
END;
$$;

-- Cria function de início de deslocamento.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO_INICIO(F_COD_SOCORRO_ROTA BIGINT,
                                                                                    F_COD_COLABORADOR BIGINT,
                                                                                    F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                                                    F_LATITUDE TEXT,
                                                                                    F_LONGITUDE TEXT,
                                                                                    F_PRECISAO_LOCALIZACAO NUMERIC,
                                                                                    F_ENDERECO_AUTOMATICO TEXT,
                                                                                    F_DEVICE_ID TEXT,
                                                                                    F_DEVICE_IMEI TEXT,
                                                                                    F_DEVICE_UPTIME_MILLIS BIGINT,
                                                                                    F_ANDROID_API_VERSION INTEGER,
                                                                                    F_MARCA_DEVICE TEXT,
                                                                                    F_MODELO_DEVICE TEXT,
                                                                                    F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                                                    F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_SOCORRO              SOCORRO_ROTA_STATUS_TYPE;
    V_COD_COLABORADOR_ATENDIMENTO BIGINT;
    V_COD_ATENDIMENTO             BIGINT;
BEGIN
    -- STATUS, COD_ATENDIMENTO, COLAB_ATENDIMENTO.
    SELECT INTO V_STATUS_SOCORRO, V_COD_ATENDIMENTO, V_COD_COLABORADOR_ATENDIMENTO SR.STATUS_ATUAL,
                                                                                   SRA.CODIGO,
                                                                                   SRA.COD_COLABORADOR_ATENDIMENTO
    FROM SOCORRO_ROTA SR
             JOIN SOCORRO_ROTA_ATENDIMENTO SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA;

    -- Verifica se o socorro em rota existe.
    IF V_STATUS_SOCORRO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o colaborador que está iniciando o deslocamento é o mesmo do atendimento.
    IF V_COD_COLABORADOR_ATENDIMENTO <> F_COD_COLABORADOR
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível concluir a operação, o deslocamento deve ser iniciado pelo colaborador que atendeu ao socorro.');
    END IF;

    -- Valida o status.
    IF V_STATUS_SOCORRO = 'ABERTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível iniciar o deslocamento, pois o socorro ainda não foi atendido.');
    END IF;

    IF V_STATUS_SOCORRO = 'EM_ATENDIMENTO'
    THEN
        IF EXISTS(SELECT DATA_HORA_DESLOCAMENTO_INICIO
                  FROM SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO
                  WHERE COD_SOCORRO_ROTA_ATENDIMENTO = V_COD_ATENDIMENTO)
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível concluir a operação, pois este atendimento já possui um registro de início de deslocamento.');
        END IF;
    END IF;

    IF V_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível iniciar o deslocamento, pois o socorro já foi invalidado.');
    END IF;
    IF V_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível iniciar o deslocamento, pois o socorro já foi finalizado.');
    END IF;

    -- Insere as informações de início de deslocamento.
    INSERT INTO SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO(COD_SOCORRO_ROTA_ATENDIMENTO,
                                                      DATA_HORA_DESLOCAMENTO_INICIO,
                                                      LATITUDE_INICIO,
                                                      LONGITUDE_INICIO,
                                                      PRECISAO_LOCALIZACAO_INICIO_METROS,
                                                      ENDERECO_AUTOMATICO_INICIO,
                                                      VERSAO_PLATAFORMA_ORIGEM_INICIO,
                                                      DEVICE_ID_INICIO,
                                                      DEVICE_IMEI_INICIO,
                                                      DEVICE_UPTIME_MILLIS_INICIO,
                                                      ANDROID_API_VERSION_INICIO,
                                                      MARCA_DEVICE_INICIO,
                                                      MODELO_DEVICE_INICIO,
                                                      PLATAFORMA_ORIGEM_INICIO)
    VALUES (V_COD_ATENDIMENTO,
            F_DATA_HORA,
            F_LATITUDE,
            F_LONGITUDE,
            F_PRECISAO_LOCALIZACAO,
            F_ENDERECO_AUTOMATICO,
            F_VERSAO_PLATAFORMA_ORIGEM,
            F_DEVICE_ID,
            F_DEVICE_IMEI,
            F_DEVICE_UPTIME_MILLIS,
            F_ANDROID_API_VERSION,
            F_MARCA_DEVICE,
            F_MODELO_DEVICE,
            F_PLATAFORMA_ORIGEM);

    -- Verifica se os dados de início de deslocamento foram inseridos.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível iniciar o deslocamento, tente novamente.');
    END IF;

    RETURN FOUND;
END;
$$;

-- Cria function de finalização de deslocamento.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO_FIM(F_COD_SOCORRO_ROTA BIGINT,
                                                                                 F_COD_COLABORADOR BIGINT,
                                                                                 F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                                                 F_LATITUDE TEXT,
                                                                                 F_LONGITUDE TEXT,
                                                                                 F_PRECISAO_LOCALIZACAO NUMERIC,
                                                                                 F_ENDERECO_AUTOMATICO TEXT,
                                                                                 F_DEVICE_ID TEXT,
                                                                                 F_DEVICE_IMEI TEXT,
                                                                                 F_DEVICE_UPTIME_MILLIS BIGINT,
                                                                                 F_ANDROID_API_VERSION INTEGER,
                                                                                 F_MARCA_DEVICE TEXT,
                                                                                 F_MODELO_DEVICE TEXT,
                                                                                 F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                                                 F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_SOCORRO              SOCORRO_ROTA_STATUS_TYPE;
    V_COD_COLABORADOR_ATENDIMENTO BIGINT;
    V_COD_ATENDIMENTO             BIGINT;
BEGIN
    -- STATUS, COD_ATENDIMENTO, COLAB_ATENDIMENTO.
    SELECT INTO V_STATUS_SOCORRO, V_COD_ATENDIMENTO, V_COD_COLABORADOR_ATENDIMENTO SR.STATUS_ATUAL,
                                                                                   SRA.CODIGO,
                                                                                   SRA.COD_COLABORADOR_ATENDIMENTO
    FROM SOCORRO_ROTA SR
             JOIN SOCORRO_ROTA_ATENDIMENTO SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA;

    -- Verifica se o socorro em rota existe e valida o status.
    IF V_STATUS_SOCORRO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o colaborador que está finalizando o deslocamento é o mesmo do atendimento.
    IF V_COD_COLABORADOR_ATENDIMENTO <> F_COD_COLABORADOR
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível concluir a operação, o deslocamento deve ser finalizado pelo colaborador que atendeu ao socorro.');
    END IF;

    IF V_STATUS_SOCORRO = 'ABERTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o deslocamento, pois o socorro ainda não foi atendido.');
    END IF;

    IF V_STATUS_SOCORRO = 'EM_ATENDIMENTO'
    THEN
        IF EXISTS(SELECT DATA_HORA_DESLOCAMENTO_FIM
                  FROM SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO
                  WHERE COD_SOCORRO_ROTA_ATENDIMENTO = V_COD_ATENDIMENTO
                    AND DATA_HORA_DESLOCAMENTO_FIM IS NOT NULL)
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível concluir a operação, pois este atendimento já possui um registro de finalização de deslocamento.');
        END IF;
    END IF;

    IF V_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível finalizar o deslocamento, pois o socorro já foi invalidado.');
    END IF;
    IF V_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível finalizar o deslocamento, pois o socorro já foi finalizado.');
    END IF;

    -- Insere as informações de finalização de deslocamento.
    UPDATE SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO
    SET DATA_HORA_DESLOCAMENTO_FIM      = F_DATA_HORA,
        LATITUDE_FIM                    = F_LATITUDE,
        LONGITUDE_FIM                   = F_LONGITUDE,
        PRECISAO_LOCALIZACAO_FIM_METROS = F_PRECISAO_LOCALIZACAO,
        ENDERECO_AUTOMATICO_FIM         = F_ENDERECO_AUTOMATICO,
        VERSAO_PLATAFORMA_ORIGEM_FIM    = F_VERSAO_PLATAFORMA_ORIGEM,
        DEVICE_ID_FIM                   = F_DEVICE_ID,
        DEVICE_IMEI_FIM                 = F_DEVICE_IMEI,
        DEVICE_UPTIME_MILLIS_FIM        = F_DEVICE_UPTIME_MILLIS,
        ANDROID_API_VERSION_FIM         = F_ANDROID_API_VERSION,
        MARCA_DEVICE_FIM                = F_MARCA_DEVICE,
        MODELO_DEVICE_FIM               = F_MODELO_DEVICE,
        PLATAFORMA_ORIGEM_FIM           = F_PLATAFORMA_ORIGEM
    WHERE COD_SOCORRO_ROTA_ATENDIMENTO = V_COD_ATENDIMENTO;

    -- Verifica se os dados de finalização de deslocamento foram inseridos.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível finalizar o deslocamento, tente novamente.');
    END IF;

    RETURN FOUND;
END;
$$;

-- Dropa a function de listagem de socorros em rota.
DROP FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_LISTAGEM(BIGINT[], DATE, DATE, TEXT);

-- Recria a function de listagem de socorros em rota para adicionar as datas de deslocamento.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_LISTAGEM(F_COD_UNIDADES BIGINT[],
                                                             F_DATA_INICIAL DATE,
                                                             F_DATA_FINAL DATE,
                                                             F_TOKEN TEXT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                          BIGINT,
                UNIDADE                                   TEXT,
                PLACA_VEICULO                             TEXT,
                VEICULO_DELETADO                          BOOLEAN,
                NOME_RESPONSAVEL_ABERTURA_SOCORRO         TEXT,
                COLABORADOR_DELETADO                      BOOLEAN,
                DESCRICAO_FORNECIDA_ABERTURA_SOCORRO      TEXT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO TEXT,
                DATA_HORA_ABERTURA_SOCORRO                TIMESTAMP WITHOUT TIME ZONE,
                ENDERECO_AUTOMATICO_ABERTURA_SOCORRO      TEXT,
                URL_FOTO_1_ABERTURA                       TEXT,
                URL_FOTO_2_ABERTURA                       TEXT,
                URL_FOTO_3_ABERTURA                       TEXT,
                STATUS_ATUAL_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE,
                DATA_HORA_DESLOCAMENTO_INICIO             TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_DESLOCAMENTO_FIM                TIMESTAMP WITHOUT TIME ZONE
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Permissões para ver todos os socorros em rota
    -- 146 - TRATAR_SOCORRO
    -- 147 - VISUALIZAR_SOCORROS_E_RELATORIOS
    F_PERMISSOES_VISUALIZAR_TODOS INTEGER[] := ARRAY [146,147];
    F_VER_TODOS                   BOOLEAN   := (SELECT POSSUI_PERMISSSAO
                                                FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN,
                                                                                                F_PERMISSOES_VISUALIZAR_TODOS,
                                                                                                FALSE,
                                                                                                TRUE));
    F_COD_COLABORADOR             BIGINT    := (SELECT COD_COLABORADOR
                                                FROM TOKEN_AUTENTICACAO
                                                WHERE TOKEN = F_TOKEN);
    -- Busca o código de empresa com base na primeira unidade do array recebido
    F_COD_EMPRESA                 BIGINT    := (SELECT COD_EMPRESA
                                                FROM UNIDADE
                                                WHERE CODIGO = (SELECT (F_COD_UNIDADES)[1]));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    RETURN QUERY
        SELECT SR.CODIGO                                                               AS COD_SOCORRO_ROTA,
               U.NOME :: TEXT                                                          AS UNIDADE,
               VD.PLACA :: TEXT                                                        AS PLACA_VEICULO,
               VD.DELETADO                                                             AS VEICULO_DELETADO,
               CD.NOME :: TEXT                                                         AS NOME_RESPONSAVEL,
               CD.DELETADO                                                             AS COLABORADOR_DELETADO,
               SRA.DESCRICAO_PROBLEMA                                                  AS DESCRICAO_FORNECIDA,
               SROP.DESCRICAO :: TEXT                                                  AS DESCRICAO_OPCAO_PROBLEMA,
               SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)          AS DATA_HORA_ABERTURA,
               SRA.ENDERECO_AUTOMATICO                                                 AS ENDERECO_AUTOMATICO_ABERTURA,
               SRA.URL_FOTO_1_ABERTURA :: TEXT                                         AS URL_FOTO_1_ABERTURA,
               SRA.URL_FOTO_2_ABERTURA :: TEXT                                         AS URL_FOTO_2_ABERTURA,
               SRA.URL_FOTO_3_ABERTURA :: TEXT                                         AS URL_FOTO_3_ABERTURA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                             AS STATUS_ATUAL_SOCORRO,
               SRAD.DATA_HORA_DESLOCAMENTO_INICIO AT TIME ZONE
               TZ_UNIDADE(SR.COD_UNIDADE)                                              AS DATA_HORA_DESLOCAMENTO_INICIO,
               SRAD.DATA_HORA_DESLOCAMENTO_FIM AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_DESLOCAMENTO_FIM
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON U.CODIGO = SR.COD_UNIDADE
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAT.COD_SOCORRO_ROTA = SR.CODIGO
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO SRAD
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAD.COD_SOCORRO_ROTA_ATENDIMENTO = SRAT.CODIGO
                 JOIN VEICULO_DATA VD ON SRA.COD_VEICULO_PROBLEMA = VD.CODIGO
                 JOIN COLABORADOR_DATA CD ON SRA.COD_COLABORADOR_ABERTURA = CD.CODIGO
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRA.COD_PROBLEMA_SOCORRO_ROTA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          -- Aplica o filtro por colaborador apenas se não tiver permissão para ver todos
          AND F_IF(F_VER_TODOS, TRUE, SRA.COD_COLABORADOR_ABERTURA = F_COD_COLABORADOR)
          AND (SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) :: DATE
            BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY SRA.DATA_HORA_ABERTURA DESC;
END;
$$;

-- Dropa a function antiga do relatório de dados gerais de socorro.
DROP FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(BIGINT[], DATE, DATE, CHARACTER VARYING[]);

-- Recria a function de relatório de dados gerais de socorro com as informações de deslocamento.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                           F_DATA_INICIAL DATE,
                                                                           F_DATA_FINAL DATE,
                                                                           F_STATUS_SOCORRO_ROTA VARCHAR[])
    RETURNS TABLE
            (
                "UNIDADE"                                      TEXT,
                "DISTÂNCIA ENTRE UNIDADE E ABERTURA"           TEXT,
                "CÓDIGO SOCORRO ROTA"                          TEXT,
                "STATUS SOCORRO ROTA"                          TEXT,
                "PLACA VEÍCULO ABERTURA"                       TEXT,
                "CÓDIGO COLABORADOR ABERTURA"                  TEXT,
                "NOME RESPONSÁVEL ABERTURA"                    TEXT,
                "KM VEÍCULO COLETADO ABERTURA"                 TEXT,
                "DESCRIÇÃO OPÇÃO PROBLEMA ABERTURA"            TEXT,
                "DESCRIÇÃO FORNECIDA ABERTURA"                 TEXT,
                "PONTO REFERÊNCIA FORNECIDO ABERTURA"          TEXT,
                "DATA/HORA ABERTURA"                           TEXT,
                "LATITUDE ABERTURA"                            TEXT,
                "LONGITUDE ABERTURA"                           TEXT,
                "ENDEREÇO AUTOMÁTICO ABERTURA"                 TEXT,
                "MARCA APARELHO ABERTURA"                      TEXT,
                "MODELO APARELHO ABERTURA"                     TEXT,
                "IMEI APARELHO ABERTURA"                       TEXT,
                "URL FOTO 1 ABERTURA"                          TEXT,
                "URL FOTO 2 ABERTURA"                          TEXT,
                "URL FOTO 3 ABERTURA"                          TEXT,
                "CÓDIGO COLABORADOR ATENDIMENTO"               TEXT,
                "NOME RESPONSÁVEL ATENDIMENTO"                 TEXT,
                "OBSERVAÇÃO ATENDIMENTO"                       TEXT,
                "TEMPO ENTRE ABERTURA/ATENDIMENTO HH:MM:SS"    TEXT,
                "DATA/HORA ATENDIMENTO"                        TEXT,
                "LATITUDE ATENDIMENTO"                         TEXT,
                "LONGITUDE ATENDIMENTO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO ATENDIMENTO"              TEXT,
                "MARCA APARELHO ATENDIMENTO"                   TEXT,
                "MODELO APARELHO ATENDIMENTO"                  TEXT,
                "IMEI APARELHO ATENDIMENTO"                    TEXT,
                "LATITUDE INICIAL DE DESLOCAMENTO"             TEXT,
                "LONGITUDE INICIAL DE DESLOCAMENTO"            TEXT,
                "DATA/HORA INICIAL DE DESLOCAMENTO"            TEXT,
                "LATITUDE FINAL DE DESLOCAMENTO"               TEXT,
                "LONGITUDE FINAL DE DESLOCAMENTO"              TEXT,
                "DATA/HORA FINAL DE DESLOCAMENTO"              TEXT,
                "CÓDIGO COLABORADOR INVALIDAÇÃO"               TEXT,
                "NOME RESPONSÁVEL INVALIDAÇÃO"                 TEXT,
                "MOTIVO INVALIDAÇÃO"                           TEXT,
                "TEMPO ENTRE ABERTURA/INVALIDAÇÃO HH:MM:SS"    TEXT,
                "TEMPO ENTRE ATENDIMENTO/INVALIDAÇÃO HH:MM:SS" TEXT,
                "DATA/HORA INVALIDAÇÃO"                        TEXT,
                "LATITUDE INVALIDAÇÃO"                         TEXT,
                "LONGITUDE INVALIDAÇÃO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO INVALIDAÇÃO"              TEXT,
                "MARCA APARELHO INVALIDAÇÃO"                   TEXT,
                "MODELO APARELHO INVALIDAÇÃO"                  TEXT,
                "IMEI APARELHO INVALIDAÇÃO"                    TEXT,
                "URL FOTO 1 INVALIDAÇÃO"                       TEXT,
                "URL FOTO 2 INVALIDAÇÃO"                       TEXT,
                "URL FOTO 3 INVALIDAÇÃO"                       TEXT,
                "CÓDIGO COLABORADOR FINALIZAÇÃO"               TEXT,
                "NOME RESPONSÁVEL FINALIZAÇÃO"                 TEXT,
                "OBSERVAÇÃO FINALIZAÇÃO"                       TEXT,
                "TEMPO ENTRE ATENDIMENTO/FINALIZAÇÃO HH:MM:SS" TEXT,
                "DATA/HORA FINALIZAÇÃO"                        TEXT,
                "LATITUDE FINALIZAÇÃO"                         TEXT,
                "LONGITUDE FINALIZAÇÃO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO FINALIZAÇÃO"              TEXT,
                "MARCA APARELHO FINALIZAÇÃO"                   TEXT,
                "MODELO APARELHO FINALIZAÇÃO"                  TEXT,
                "IMEI APARELHO FINALIZAÇÃO"                    TEXT,
                "URL FOTO 1 FINALIZAÇÃO"                       TEXT,
                "URL FOTO 2 FINALIZAÇÃO"                       TEXT,
                "URL FOTO 3 FINALIZAÇÃO"                       TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_ARRAY_CONTEM_STATUS_ABERTO         BOOLEAN := CASE
                                                        WHEN ('ABERTO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO BOOLEAN := CASE
                                                        WHEN ('EM_ATENDIMENTO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_FINALIZADO     BOOLEAN := CASE
                                                        WHEN ('FINALIZADO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_INVALIDO       BOOLEAN := CASE
                                                        WHEN ('INVALIDO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                                            AS NOME_UNIDADE,
               COALESCE(
                           TRUNC(
                                   (ST_DISTANCE_SPHERE(
                                            ST_POINT(
                                                    LONGITUDE_UNIDADE::REAL,
                                                    LATITUDE_UNIDADE::REAL),
                                            ST_POINT(
                                                    LONGITUDE_ABERTURA::REAL,
                                                    LATITUDE_ABERTURA::REAL)) / 1000)::NUMERIC, 2)::TEXT || ' KM',
                           'Unidade sem localização definida no Prolog') AS DISTANCIA_ENTRE_UNIDADE_ABERTURA,
               COALESCE(SR.CODIGO ::TEXT, '-')                           AS COD_SOCORRO_ROTA,
               COALESCE(SR.STATUS_ATUAL :: TEXT, '-')                    AS STATUS_SOCORRO_ROTA,
               COALESCE(V.PLACA :: TEXT, '-')                            AS PLACA_VEICULO_ABERTURA,
               COALESCE(SRAB.COD_COLABORADOR_ABERTURA :: TEXT, '-')      AS COD_COLABORADOR_ABERTURA,
               COALESCE(CDAB.NOME :: TEXT, '-')                          AS NOME_RESPONSAVEL_ABERTURA,
               COALESCE(SRAB.KM_VEICULO_ABERTURA ::TEXT, '-')            AS KM_VEICULO_COLETADO_ABERTURA,
               COALESCE(SROP.DESCRICAO :: TEXT, '-')                     AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               COALESCE(SRAB.DESCRICAO_PROBLEMA :: TEXT, '-')            AS DESCRICAO_FORNECIDA_ABERTURA,
               COALESCE(SRAB.PONTO_REFERENCIA :: TEXT, '-')              AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               COALESCE((SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                             AS DATA_HORA_ABERTURA,
               COALESCE(SRAB.LATITUDE_ABERTURA :: TEXT, '-')             AS LATITUDE_ABERTURA,
               COALESCE(SRAB.LONGITUDE_ABERTURA :: TEXT, '-')            AS LONGITUDE_ABERTURA,
               COALESCE(SRAB.ENDERECO_AUTOMATICO :: TEXT, '-')           AS ENDERECO_AUTOMATICO_ABERTURA,
               COALESCE(SRAB.MARCA_DEVICE_ABERTURA :: TEXT, '-')         AS MARCA_APARELHO_ABERTURA,
               COALESCE(SRAB.MODELO_DEVICE_ABERTURA :: TEXT, '-')        AS MODELO_APARELHO_ABERTURA,
               COALESCE(SRAB.DEVICE_IMEI_ABERTURA :: TEXT, '-')          AS IMEI_APARELHO_ABERTURA,
               COALESCE(SRAB.URL_FOTO_1_ABERTURA :: TEXT, '-')           AS URL_FOTO_1_ABERTURA,
               COALESCE(SRAB.URL_FOTO_2_ABERTURA :: TEXT, '-')           AS URL_FOTO_2_ABERTURA,
               COALESCE(SRAB.URL_FOTO_3_ABERTURA :: TEXT, '-')           AS URL_FOTO_3_ABERTURA,
               COALESCE(SRAT.COD_COLABORADOR_ATENDIMENTO :: TEXT, '-')   AS COD_COLABORADOR_ATENDIMENTO,
               COALESCE(CDAT.NOME :: TEXT, '-')                          AS NOME_RESPONSAVEL_ATENDIMENTO,
               COALESCE(SRAT.OBSERVACAO_ATENDIMENTO :: TEXT, '-')        AS OBSERVACAO_ATENDIMENTO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRAT.DATA_HORA_ATENDIMENTO - SRAB.DATA_HORA_ABERTURA),
                        '-')                                             AS TEMPO_ABERTURA_ATENDIMENTO,
               COALESCE((SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                             AS DATA_HORA_ATENDIMENTO,
               COALESCE(SRAT.LATITUDE_ATENDIMENTO :: TEXT, '-')          AS LATITUDE_ATENDIMENTO,
               COALESCE(SRAT.LONGITUDE_ATENDIMENTO :: TEXT, '-')         AS LONGITUDE_ATENDIMENTO,
               COALESCE(SRAT.ENDERECO_AUTOMATICO :: TEXT, '-')           AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               COALESCE(SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT, '-')      AS MARCA_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT, '-')     AS MODELO_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT, '-')       AS IMEI_APARELHO_ATENDIMENTO,
               COALESCE(SRAD.LATITUDE_INICIO :: TEXT, '-')               AS LATITUDE_INICIO,
               COALESCE(SRAD.LONGITUDE_INICIO :: TEXT, '-')              AS LONGITUDE_INICIO,
               COALESCE((SRAD.DATA_HORA_DESLOCAMENTO_INICIO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                             AS DATA_HORA_DESLOCAMENTO_INICIO,
               COALESCE(SRAD.LATITUDE_FIM :: TEXT, '-')                  AS LATITUDE_FIM,
               COALESCE(SRAD.LONGITUDE_FIM :: TEXT, '-')                 AS LONGITUDE_FIM,
               COALESCE((SRAD.DATA_HORA_DESLOCAMENTO_FIM AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT, '-')
                                                                         AS DATA_HORA_DESLOCAMENTO_FIM,
               COALESCE(SRI.COD_COLABORADOR_INVALIDACAO :: TEXT, '-')    AS COD_COLABORADOR_INVALIDACAO,
               COALESCE(CDI.NOME :: TEXT, '-')                           AS NOME_RESPONSAVEL_INVALIDACAO,
               COALESCE(SRI.MOTIVO_INVALIDACAO :: TEXT, '-')             AS MOTIVO_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAB.DATA_HORA_ABERTURA)
                            ELSE '-'
                            END,
                        '-')                                             AS TEMPO_ABERTURA_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NOT NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAT.DATA_HORA_ATENDIMENTO)
                            ELSE '-'
                            END,
                        '-')                                             AS TEMPO_ATENDIMENTO_INVALIDACAO,
               COALESCE((SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                             AS DATA_HORA_INVALIDACAO,
               COALESCE(SRI.LATITUDE_INVALIDACAO :: TEXT, '-')           AS LATITUDE_INVALIDACAO,
               COALESCE(SRI.LONGITUDE_INVALIDACAO :: TEXT, '-')          AS LONGITUDE_INVALIDACAO,
               COALESCE(SRI.ENDERECO_AUTOMATICO :: TEXT, '-')            AS ENDERECO_AUTOMATICO_INVALIDACAO,
               COALESCE(SRI.MARCA_DEVICE_INVALIDACAO :: TEXT, '-')       AS MARCA_APARELHO_INVALIDACAO,
               COALESCE(SRI.MODELO_DEVICE_INVALIDACAO :: TEXT, '-')      AS MODELO_APARELHO_INVALIDACAO,
               COALESCE(SRI.DEVICE_IMEI_INVALIDACAO :: TEXT, '-')        AS IMEI_APARELHO_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_1_INVALIDACAO :: TEXT, '-')         AS URL_FOTO_1_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_2_INVALIDACAO :: TEXT, '-')         AS URL_FOTO_2_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_3_INVALIDACAO :: TEXT, '-')         AS URL_FOTO_3_INVALIDACAO,
               COALESCE(SRF.COD_COLABORADOR_FINALIZACAO :: TEXT, '-')    AS COD_COLABORADOR_FINALIZACAO,
               COALESCE(CDF.NOME :: TEXT, '-')                           AS NOME_RESPONSAVEL_FINALIZACAO,
               COALESCE(SRF.OBSERVACAO_FINALIZACAO :: TEXT, '-')         AS OBSERVACAO_FINALIZACAO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRF.DATA_HORA_FINALIZACAO - SRAT.DATA_HORA_ATENDIMENTO),
                        '-')                                             AS TEMPO_ATENDIMENTO_FINALIZACAO,
               COALESCE((SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                             AS DATA_HORA_FINALIZACAO,
               COALESCE(SRF.LATITUDE_FINALIZACAO :: TEXT, '-')           AS LATITUDE_FINALIZACAO,
               COALESCE(SRF.LONGITUDE_FINALIZACAO :: TEXT, '-')          AS LONGITUDE_FINALIZACAO,
               COALESCE(SRF.ENDERECO_AUTOMATICO :: TEXT, '-')            AS ENDERECO_AUTOMATICO_FINALIZACAO,
               COALESCE(SRF.MARCA_DEVICE_FINALIZACAO :: TEXT, '-')       AS MARCA_APARELHO_FINALIZACAO,
               COALESCE(SRF.MODELO_DEVICE_FINALIZACAO :: TEXT, '-')      AS MODELO_APARELHO_FINALIZACAO,
               COALESCE(SRF.DEVICE_IMEI_FINALIZACAO :: TEXT, '-')        AS IMEI_APARELHO_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_1_FINALIZACAO :: TEXT, '-')         AS URL_FOTO_1_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_2_FINALIZACAO :: TEXT, '-')         AS URL_FOTO_2_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_3_FINALIZACAO :: TEXT, '-')         AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON SR.COD_UNIDADE = U.CODIGO
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
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND (F_IF(F_ARRAY_CONTEM_STATUS_ABERTO,
                    (SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                    FALSE)
            OR F_IF(F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO,
                    (SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                    FALSE)
            OR F_IF(F_ARRAY_CONTEM_STATUS_FINALIZADO,
                    (SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                    FALSE)
            OR F_IF(F_ARRAY_CONTEM_STATUS_INVALIDO,
                    (SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)):: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                    FALSE));
END ;
$$;

-- #####################################################################################################################
-- #####################################################################################################################
-- ################## Colocar na visualização de socorro o tempo transcorrido entre cada status ########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2585

-- Deleta a function antiga.
DROP FUNCTION func_socorro_rota_visualizacao(BIGINT);

-- Recria a function com as informações de tempo transcorrido entre cada status.
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_SOCORRO_ROTA BIGINT)
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
    F_COD_EMPRESA BIGINT := (SELECT COD_EMPRESA
                             FROM UNIDADE
                             WHERE CODIGO =
                                   (SELECT COD_UNIDADE FROM SOCORRO_ROTA WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa.
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

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