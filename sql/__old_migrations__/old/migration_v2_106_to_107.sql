BEGIN TRANSACTION;
-- Insere TOKEN_INTEGRACAO para a empresa IMEDIATO
INSERT INTO INTEGRACAO.TOKEN_INTEGRACAO(COD_EMPRESA, TOKEN_INTEGRACAO)
VALUES (6, 'ep9c306ch0esa7v6ki58u99nr92fko8gvd5h7c1lrgtb9r2am1v');

--######################################################################################################################
--######################################################################################################################
--##################################            FUNCTIONS NECESSÁRIAS            #######################################
--######################################################################################################################
--######################################################################################################################
-- PL-2271
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_LISTA_TIPOS_MARCACOES(F_TOKEN_INTEGRACAO TEXT,
                                                                          F_APENAS_TIPO_MARCACOES_ATIVAS BOOLEAN)
    RETURNS TABLE
            (
                COD_EMPRESA                  BIGINT,
                COD_UNIDADE                  BIGINT,
                CODIGO                       BIGINT,
                NOME                         TEXT,
                ICONE                        TEXT,
                TEMPO_RECOMENDADO_EM_MINUTOS BIGINT,
                TEMPO_ESTOURO_EM_MINUTOS     BIGINT,
                HORARIO_SUGERIDO_MARCAR      TIME,
                IS_TIPO_JORNADA              BOOLEAN,
                DESCONTA_JORNADA_BRUTA       BOOLEAN,
                DESCONTA_JORNADA_LIQUIDA     BOOLEAN,
                STATUS_ATIVO                 BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_TOKEN BIGINT := (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
BEGIN
    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_TOKEN,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    RETURN QUERY
        SELECT U.COD_EMPRESA                                       AS COD_EMPRESA,
               U.CODIGO                                            AS COD_UNIDADE,
               IT.CODIGO                                           AS CODIGO,
               IT.NOME::TEXT                                       AS NOME,
               IT.ICONE::TEXT                                      AS ICONE,
               IT.TEMPO_RECOMENDADO_MINUTOS                        AS TEMPO_RECOMENDADO_EM_MINNUTOS,
               IT.TEMPO_ESTOURO_MINUTOS                            AS TEMPO_ESTOURO_EM_MINUTOS,
               IT.HORARIO_SUGERIDO                                 AS HORARIO_SUGERIDO_MARCAR,
               F_IF(IT.CODIGO = MTJ.COD_TIPO_JORNADA, TRUE, FALSE) AS IS_TIPO_JORNADA,
               F_IF(MTDCJBL.DESCONTA_JORNADA_BRUTA IS NULL,
                    FALSE,
                    MTDCJBL.DESCONTA_JORNADA_BRUTA)                AS DESCONTA_JORNADA_BRUTA,
               F_IF(MTDCJBL.DESCONTA_JORNADA_LIQUIDA IS NULL,
                    FALSE,
                    MTDCJBL.DESCONTA_JORNADA_LIQUIDA)              AS DESCONTA_JORNADA_LIQUIDA,
               IT.ATIVO                                            AS STATUS_ATIVO
        FROM INTERVALO_TIPO IT
                 JOIN UNIDADE U
                      ON IT.COD_UNIDADE = U.CODIGO
                 LEFT JOIN MARCACAO_TIPO_JORNADA MTJ
                           ON IT.COD_UNIDADE = MTJ.COD_UNIDADE AND IT.CODIGO = MTJ.COD_TIPO_JORNADA
                 LEFT JOIN MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA MTDCJBL
                           ON IT.COD_UNIDADE = MTDCJBL.COD_UNIDADE AND IT.CODIGO = MTDCJBL.COD_TIPO_DESCONTADO
        WHERE IT.COD_UNIDADE IN (SELECT U.CODIGO
                                 FROM UNIDADE U
                                 WHERE U.COD_EMPRESA = COD_EMPRESA_TOKEN)
          AND F_IF(F_APENAS_TIPO_MARCACOES_ATIVAS, IT.ATIVO = TRUE, TRUE);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2273
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_LISTA_MARCACOES_REALIZADAS(F_TOKEN_INTEGRACAO TEXT,
                                                                               F_COD_ULTIMA_MARCACAO_SINCRONIZADA BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE                        BIGINT,
                CODIGO                             BIGINT,
                COD_MARCACAO_VINCULO               BIGINT,
                COD_TIPO_MARCACAO                  BIGINT,
                CPF_COLABORADOR                    TEXT,
                TIPO_MARCACAO                      TEXT,
                DATA_HORA_MARCACAO_UTC             TIMESTAMP WITHOUT TIME ZONE,
                FONTE_DATA_HORA                    TEXT,
                JUSTIFICATIVA_TEMPO_RECOMENDADO    TEXT,
                JUSTIFICATIVA_ESTOURO              TEXT,
                LATITUDE_MARCACAO                  TEXT,
                LONGITUDE_MARCACAO                 TEXT,
                DATA_HORA_SINCRONIZACAO_UTC        TIMESTAMP WITHOUT TIME ZONE,
                DEVICE_IMEI                        TEXT,
                DEVICE_ID                          TEXT,
                MARCA_DEVICE                       TEXT,
                MODELO_DEVICE                      TEXT,
                VERSAO_APP_MOMENTO_MARCACAO        INTEGER,
                VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
                DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
                DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                ANDROID_API_VERSION                INTEGER,
                STATUS_ATIVO                       BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_TOKEN BIGINT := (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
BEGIN
    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_TOKEN,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    RETURN QUERY
        SELECT I.COD_UNIDADE                                AS COD_UNIDADE,
               I.CODIGO                                     AS CODIGO,
               CASE
                   WHEN I.TIPO_MARCACAO = 'MARCACAO_INICIO'
                       THEN
                       (SELECT MVIF.COD_MARCACAO_FIM
                        FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                        WHERE MVIF.COD_MARCACAO_INICIO = I.CODIGO)
                   ELSE
                       (SELECT MVIF.COD_MARCACAO_INICIO
                        FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                        WHERE MVIF.COD_MARCACAO_FIM = I.CODIGO)
                   END                                      AS COD_MARCACAO_VINCULO,
               I.COD_TIPO_INTERVALO                         AS COD_TIPO_MARCACAO,
               LPAD(I.CPF_COLABORADOR::TEXT, 11, '0')::TEXT AS CPF_COLABORADOR,
               I.TIPO_MARCACAO::TEXT                        AS TIPO_MARCACAO,
               I.DATA_HORA AT TIME ZONE 'UTC'               AS DATA_HORA_MARCACAO_UTC,
               I.FONTE_DATA_HORA::TEXT                      AS FONTE_DATA_HORA,
               I.JUSTIFICATIVA_TEMPO_RECOMENDADO            AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
               I.JUSTIFICATIVA_ESTOURO                      AS JUSTIFICATIVA_ESTOURO,
               I.LATITUDE_MARCACAO                          AS LATITUDE_MARCACAO,
               I.LONGITUDE_MARCACAO                         AS LONGITUDE_MARCACAO,
               I.DATA_HORA_SINCRONIZACAO AT TIME ZONE 'UTC' AS DATA_HORA_SINCRONIZACAO_UTC,
               I.DEVICE_IMEI                                AS DEVICE_IMEI,
               I.DEVICE_ID                                  AS DEVICE_ID,
               I.MARCA_DEVICE                               AS MARCA_DEVICE,
               I.MODELO_DEVICE                              AS MODELO_DEVICE,
               I.VERSAO_APP_MOMENTO_MARCACAO                AS VERSAO_APP_MOMENTO_MARCACAO,
               I.VERSAO_APP_MOMENTO_SINCRONIZACAO           AS VERSAO_APP_MOMENTO_SINCRONIZACAO,
               I.DEVICE_UPTIME_REALIZACAO_MILLIS            AS DEVICE_UPTIME_REALIZACAO_MILLIS,
               I.DEVICE_UPTIME_SINCRONIZACAO_MILLIS         AS DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
               I.ANDROID_API_VERSION                        AS ANDROID_API_VERSION,
               I.STATUS_ATIVO                               AS STATUS_ATIVO
        FROM INTERVALO I
        WHERE I.COD_UNIDADE IN (SELECT U.CODIGO FROM UNIDADE U WHERE U.COD_EMPRESA = COD_EMPRESA_TOKEN)
          AND I.CODIGO > F_COD_ULTIMA_MARCACAO_SINCRONIZADA
        ORDER BY I.CODIGO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2274
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_LISTA_AJUSTES_MARCACOES_REALIZADOS(F_TOKEN_INTEGRACAO TEXT,
                                                                                       F_COD_ULTIMO_AJUSTE_MARCACAO_SINCRONIZADO BIGINT)
    RETURNS TABLE
            (
                CODIGO_EDICAO                        BIGINT,
                COD_JUSTIFICATIVA_SELECIONADA_AJUSTE BIGINT,
                JUSTIFICATIVA_SELECIONADA_AJUSTE     TEXT,
                OBSERVACAO_AJUSTE_MARCACAO           TEXT,
                ACAO_AJUSTE_MARCACAO                 TEXT,
                CPF_COLABORADOR_AJUSTE               TEXT,
                DATA_HORA_AJUSTE_UTC                 TIMESTAMP WITHOUT TIME ZONE,
                COD_UNIDADE                          BIGINT,
                CODIGO                               BIGINT,
                COD_MARCACAO_VINCULO                 BIGINT,
                COD_TIPO_MARCACAO                    BIGINT,
                CPF_COLABORADOR                      TEXT,
                TIPO_MARCACAO                        TEXT,
                DATA_HORA_MARCACAO_UTC               TIMESTAMP WITHOUT TIME ZONE,
                FONTE_DATA_HORA                      TEXT,
                JUSTIFICATIVA_TEMPO_RECOMENDADO      TEXT,
                JUSTIFICATIVA_ESTOURO                TEXT,
                LATITUDE_MARCACAO                    TEXT,
                LONGITUDE_MARCACAO                   TEXT,
                DATA_HORA_SINCRONIZACAO_UTC          TIMESTAMP WITHOUT TIME ZONE,
                DEVICE_IMEI                          TEXT,
                DEVICE_ID                            TEXT,
                MARCA_DEVICE                         TEXT,
                MODELO_DEVICE                        TEXT,
                VERSAO_APP_MOMENTO_MARCACAO          INTEGER,
                VERSAO_APP_MOMENTO_SINCRONIZACAO     INTEGER,
                DEVICE_UPTIME_REALIZACAO_MILLIS      BIGINT,
                DEVICE_UPTIME_SINCRONIZACAO_MILLIS   BIGINT,
                ANDROID_API_VERSION                  INTEGER,
                STATUS_ATIVO                         BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_TOKEN BIGINT := (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
BEGIN
    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_TOKEN,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    RETURN QUERY
        SELECT MH.CODIGO                                    AS CODIGO_EDICAO,
               MA.COD_JUSTIFICATIVA_AJUSTE                  AS COD_JUSTIFICATIVA_SELECIONADA_AJUSTE,
               MJA.NOME                                     AS JUSTIFICATIVA_SELECIONADA_AJUSTE,
               MA.OBSERVACAO_AJUSTE                         AS OBSERVACAO_AJUSTE_MARCACAO,
               MA.ACAO_AJUSTE::TEXT                         AS ACAO_AJUSTE_MARCACAO,
               LPAD(C.CPF::TEXT, 11, '0')::TEXT             AS CPF_COLABORADOR_AJUSTE,
               MA.DATA_HORA_AJUSTE AT TIME ZONE 'UTC'       AS DATA_HORA_AJUSTE_UTC,
               I.COD_UNIDADE                                AS COD_UNIDADE,
               I.CODIGO                                     AS CODIGO,
               CASE
                   WHEN I.TIPO_MARCACAO = 'MARCACAO_INICIO'
                       THEN
                       (SELECT MVIF.COD_MARCACAO_FIM
                        FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                        WHERE MVIF.COD_MARCACAO_INICIO = I.CODIGO)
                   ELSE
                       (SELECT MVIF.COD_MARCACAO_INICIO
                        FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                        WHERE MVIF.COD_MARCACAO_FIM = I.CODIGO)
                   END                                      AS COD_MARCACAO_VINCULO,
               I.COD_TIPO_INTERVALO                         AS COD_TIPO_MARCACAO,
               LPAD(I.CPF_COLABORADOR::TEXT, 11, '0')::TEXT AS CPF_COLABORADOR,
               I.TIPO_MARCACAO::TEXT                        AS TIPO_MARCACAO,
               I.DATA_HORA AT TIME ZONE 'UTC'               AS DATA_HORA_MARCACAO_UTC,
               I.FONTE_DATA_HORA::TEXT                      AS FONTE_DATA_HORA,
               I.JUSTIFICATIVA_TEMPO_RECOMENDADO            AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
               I.JUSTIFICATIVA_ESTOURO                      AS JUSTIFICATIVA_ESTOURO,
               I.LATITUDE_MARCACAO                          AS LATITUDE_MARCACAO,
               I.LONGITUDE_MARCACAO                         AS LONGITUDE_MARCACAO,
               I.DATA_HORA_SINCRONIZACAO AT TIME ZONE 'UTC' AS DATA_HORA_SINCRONIZACAO_UTC,
               I.DEVICE_IMEI                                AS DEVICE_IMEI,
               I.DEVICE_ID                                  AS DEVICE_ID,
               I.MARCA_DEVICE                               AS MARCA_DEVICE,
               I.MODELO_DEVICE                              AS MODELO_DEVICE,
               I.VERSAO_APP_MOMENTO_MARCACAO                AS VERSAO_APP_MOMENTO_MARCACAO,
               I.VERSAO_APP_MOMENTO_SINCRONIZACAO           AS VERSAO_APP_MOMENTO_SINCRONIZACAO,
               I.DEVICE_UPTIME_REALIZACAO_MILLIS            AS DEVICE_UPTIME_REALIZACAO_MILLIS,
               I.DEVICE_UPTIME_SINCRONIZACAO_MILLIS         AS DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
               I.ANDROID_API_VERSION                        AS ANDROID_API_VERSION,
               I.STATUS_ATIVO                               AS STATUS_ATIVO
        FROM MARCACAO_HISTORICO MH
                 JOIN MARCACAO_AJUSTE MA ON MH.COD_AJUSTE = MA.CODIGO
                 JOIN MARCACAO_JUSTIFICATIVA_AJUSTE MJA ON MA.COD_JUSTIFICATIVA_AJUSTE = MJA.CODIGO
                 JOIN INTERVALO I ON MH.COD_MARCACAO = I.CODIGO
                 JOIN COLABORADOR C ON MA.COD_COLABORADOR_AJUSTE = C.CODIGO
        WHERE MA.COD_UNIDADE_AJUSTE IN (SELECT U.CODIGO FROM UNIDADE U WHERE U.COD_EMPRESA = COD_EMPRESA_TOKEN)
          AND MH.CODIGO > F_COD_ULTIMO_AJUSTE_MARCACAO_SINCRONIZADO
        ORDER BY MH.CODIGO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PLI-45
DROP FUNCTION INTEGRACAO.FUNC_RELATORIO_INTERVALO_PORTARIA_1510_TIPO_3(F_TOKEN_INTEGRACAO TEXT,
    F_COD_ULTIMA_MARCACAO_SINCRONIZADA BIGINT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE,
    F_COD_UNIDADE BIGINT,
    F_COD_TIPO_INTERVALO BIGINT,
    F_CPF_COLABORADOR BIGINT);

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_RELATORIO_INTERVALO_PORTARIA_1510_TIPO_3(F_TOKEN_INTEGRACAO TEXT,
                                                                                             F_DATA_INICIAL DATE,
                                                                                             F_DATA_FINAL DATE,
                                                                                             F_COD_UNIDADE BIGINT,
                                                                                             F_COD_TIPO_INTERVALO BIGINT,
                                                                                             F_CPF_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                NSR              TEXT,
                TIPO_REGISTRO    TEXT,
                DATA_MARCACAO    TEXT,
                HORARIO_MARCACAO TEXT,
                PIS_COLABORADOR  TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT LPAD(VI.CODIGO_MARCACAO_POR_UNIDADE::TEXT, 9, '0')                        AS NSR,
               '3'::TEXT                                                                 AS TIPO_REGISTRO,
               TO_CHAR(VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE), 'DDMMYYYY') AS DATA_MARCACAO,
               TO_CHAR(VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE), 'HH24MI')   AS HORARIO_MARCACAO,
               LPAD(C.PIS::TEXT, 12, '0')                                                AS PIS_COLABORADOR
        FROM VIEW_INTERVALO VI
                 JOIN COLABORADOR C ON VI.CPF_COLABORADOR = C.CPF AND C.PIS IS NOT NULL
                 JOIN UNIDADE U ON U.CODIGO = VI.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
        WHERE (VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE))::DATE >= F_DATA_INICIAL
          AND (VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE))::DATE <= F_DATA_FINAL
          AND E.CODIGO =
              (SELECT TI.COD_EMPRESA FROM INTEGRACAO.TOKEN_INTEGRACAO TI WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
          AND F_IF(F_COD_UNIDADE IS NULL, TRUE, VI.COD_UNIDADE = F_COD_UNIDADE)
          AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, VI.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO)
          AND F_IF(F_CPF_COLABORADOR IS NULL, TRUE, VI.CPF_COLABORADOR = F_CPF_COLABORADOR);
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;