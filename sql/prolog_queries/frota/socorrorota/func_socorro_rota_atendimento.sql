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
            F_VERSAO_PLATAFORMA_ORIGEM)
    RETURNING CODIGO INTO V_COD_SOCORRO_ATENDIMENTO_INSERIDO;

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