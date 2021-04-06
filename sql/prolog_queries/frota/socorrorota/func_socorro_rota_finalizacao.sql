CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_FINALIZACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_FINALIZACAO BIGINT,
                                                         F_MOTIVO_FINALIZACAO TEXT,
                                                         F_DATA_HORA_FINALIZACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_FINALIZACAO TEXT,
                                                         F_URL_FOTO_2_FINALIZACAO TEXT,
                                                         F_URL_FOTO_3_FINALIZACAO TEXT,
                                                         F_LATITUDE_FINALIZACAO TEXT,
                                                         F_LONGITUDE_FINALIZACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_DEVICE_ID_FINALIZACAO TEXT,
                                                         F_DEVICE_IMEI_FINALIZACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_FINALIZACAO BIGINT,
                                                         F_ANDROID_API_VERSION_FINALIZACAO INTEGER,
                                                         F_MARCA_DEVICE_FINALIZACAO TEXT,
                                                         F_MODELO_DEVICE_FINALIZACAO TEXT,
                                                         F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                         F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO      BIGINT                   := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                                    FROM SOCORRO_ROTA_ATENDIMENTO
                                                                    WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_SOCORRO_FINALIZACAO_INSERIDO BIGINT;
    F_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe
    IF F_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível localizar esse socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está finalizando.
    IF F_COD_COLABORADOR_ATENDIMENTO IS NOT NULL AND F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_FINALIZACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível finalizar um socorro que foi atendido por outro colaborador.');
    END IF;

    IF F_STATUS_SOCORRO = 'ABERTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele ainda não foi atendido.');
    END IF;
    IF F_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi invalidado.');
    END IF;
    IF F_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi finalizado.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    INSERT INTO SOCORRO_ROTA_FINALIZACAO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_FINALIZACAO,
                                          OBSERVACAO_FINALIZACAO,
                                          DATA_HORA_FINALIZACAO,
                                          URL_FOTO_1_FINALIZACAO,
                                          URL_FOTO_2_FINALIZACAO,
                                          URL_FOTO_3_FINALIZACAO,
                                          LATITUDE_FINALIZACAO,
                                          LONGITUDE_FINALIZACAO,
                                          PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_FINALIZACAO,
                                          DEVICE_IMEI_FINALIZACAO,
                                          DEVICE_UPTIME_MILLIS_FINALIZACAO,
                                          ANDROID_API_VERSION_FINALIZACAO,
                                          MARCA_DEVICE_FINALIZACAO,
                                          MODELO_DEVICE_FINALIZACAO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_FINALIZACAO,
            F_MOTIVO_FINALIZACAO,
            F_DATA_HORA_FINALIZACAO,
            F_URL_FOTO_1_FINALIZACAO,
            F_URL_FOTO_2_FINALIZACAO,
            F_URL_FOTO_3_FINALIZACAO,
            F_LATITUDE_FINALIZACAO,
            F_LONGITUDE_FINALIZACAO,
            F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_FINALIZACAO,
            F_DEVICE_IMEI_FINALIZACAO,
            F_DEVICE_UPTIME_MILLIS_FINALIZACAO,
            F_ANDROID_API_VERSION_FINALIZACAO,
            F_MARCA_DEVICE_FINALIZACAO,
            F_MODELO_DEVICE_FINALIZACAO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO F_COD_SOCORRO_FINALIZACAO_INSERIDO;

    IF F_COD_SOCORRO_FINALIZACAO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_FINALIZACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL                = 'FINALIZADO',
            COD_FINALIZACAO = F_COD_SOCORRO_FINALIZACAO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar a finalização desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a finalização desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_FINALIZACAO_INSERIDO;
END;
$$;