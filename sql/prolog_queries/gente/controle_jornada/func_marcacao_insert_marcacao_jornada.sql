-- Sobre:
--
-- Essa function insere os dados de intervalo originados no APP.
--
-- Histórico:
-- 2019-07-31 -> Adicionados dados do dispositivo (wvinim - PL-2152).
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(
                                                      F_COD_UNIDADE                        BIGINT,
                                                      F_COD_TIPO_INTERVALO                 BIGINT,
                                                      F_CPF_COLABORADOR                    BIGINT,
                                                      F_DATA_HORA                          TIMESTAMP WITH TIME ZONE,
                                                      F_TIPO_MARCACAO                      TEXT,
                                                      F_FONTE_DATA_HORA                    TEXT,
                                                      F_JUSTIFICATIVA_TEMPO_RECOMENDADO    TEXT,
                                                      F_JUSTIFICATIVA_ESTOURO              TEXT,
                                                      F_LATITUDE_MARCACAO                  TEXT,
                                                      F_LONGITUDE_MARCACAO                 TEXT,
                                                      F_DATA_HORA_SINCRONIZACAO            TIMESTAMP WITH TIME ZONE,
                                                      F_VERSAO_APP_MOMENTO_MARCACAO        INTEGER,
                                                      F_VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
                                                      F_DEVICE_ID                          TEXT,
                                                      F_DEVICE_IMEI                        TEXT,
                                                      F_DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
                                                      F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                                                      F_ANDROID_API_VERSION                BIGINT,
                                                      F_MARCA_DEVICE                       TEXT,
                                                      F_MODELO_DEVICE                      TEXT)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
INSERT INTO INTERVALO (COD_UNIDADE,
                       COD_TIPO_INTERVALO,
                       CPF_COLABORADOR,
                       DATA_HORA,
                       TIPO_MARCACAO,
                       FONTE_DATA_HORA,
                       JUSTIFICATIVA_TEMPO_RECOMENDADO,
                       JUSTIFICATIVA_ESTOURO,
                       LATITUDE_MARCACAO,
                       LONGITUDE_MARCACAO,
                       DATA_HORA_SINCRONIZACAO,
                       VERSAO_APP_MOMENTO_MARCACAO,
                       VERSAO_APP_MOMENTO_SINCRONIZACAO,
                       DEVICE_ID,
                       DEVICE_IMEI,
                       DEVICE_UPTIME_REALIZACAO_MILLIS,
                       DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                       ANDROID_API_VERSION,
                       MARCA_DEVICE,
                       MODELO_DEVICE)
VALUES (F_COD_UNIDADE,
        F_COD_TIPO_INTERVALO,
        F_CPF_COLABORADOR,
        F_DATA_HORA,
        F_TIPO_MARCACAO,
        F_FONTE_DATA_HORA,
        F_JUSTIFICATIVA_TEMPO_RECOMENDADO,
        F_JUSTIFICATIVA_ESTOURO,
        F_LATITUDE_MARCACAO,
        F_LONGITUDE_MARCACAO,
        F_DATA_HORA_SINCRONIZACAO,
        F_VERSAO_APP_MOMENTO_MARCACAO,
        F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
        F_DEVICE_ID,
        F_DEVICE_IMEI,
        F_DEVICE_UPTIME_REALIZACAO_MILLIS,
        F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
        F_ANDROID_API_VERSION,
        F_MARCA_DEVICE,
        F_MODELO_DEVICE)
    RETURNING CODIGO;
$$;