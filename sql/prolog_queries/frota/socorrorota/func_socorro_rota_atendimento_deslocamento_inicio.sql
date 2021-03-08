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