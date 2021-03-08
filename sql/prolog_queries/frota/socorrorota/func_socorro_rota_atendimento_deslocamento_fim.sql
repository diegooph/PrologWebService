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
        PERFORM THROW_GENERIC_ERROR('Não foi possível finalizar o deslocamento, pois o socorro ainda não foi atendido.');
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