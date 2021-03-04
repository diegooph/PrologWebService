-- Sobre:
--
-- Esta função agrupa e retorna uma lista de marcações, filtradas por tipo, colaborador e unidade.
--
-- Histórico:
-- 2019-08-01 -> Function alterada para inserir registros de IMEI (wvinim - PL-2152).
-- 2020-04-28 -> Alterado para usar views ao invés de CTEs e removido order by (para não pagarmos esse tempo de sort)
--               (luiz_fp - PL-2720).
-- 2020-05-06 -> Corrige para aplicar filtragem em inícios ou fins apenas se houverem marcações (luiz_fp).
CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE BIGINT,
                                                     F_CPF_COLABORADOR BIGINT,
                                                     F_COD_TIPO_INTERVALO BIGINT)
    RETURNS TABLE
            (
                FONTE_DATA_HORA_FIM             TEXT,
                FONTE_DATA_HORA_INICIO          TEXT,
                JUSTIFICATIVA_ESTOURO           TEXT,
                JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
                LATITUDE_MARCACAO_INICIO        TEXT,
                LONGITUDE_MARCACAO_INICIO       TEXT,
                LATITUDE_MARCACAO_FIM           TEXT,
                LONGITUDE_MARCACAO_FIM          TEXT,
                COD_UNIDADE                     BIGINT,
                CPF_COLABORADOR                 BIGINT,
                COD_TIPO_INTERVALO              BIGINT,
                COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
                DATA_HORA_INICIO                TIMESTAMP WITH TIME ZONE,
                DATA_HORA_FIM                   TIMESTAMP WITH TIME ZONE,
                COD_MARCACAO_INICIO             BIGINT,
                COD_MARCACAO_FIM                BIGINT,
                STATUS_ATIVO_INICIO             BOOLEAN,
                STATUS_ATIVO_FIM                BOOLEAN,
                FOI_AJUSTADO_INICIO             BOOLEAN,
                FOI_AJUSTADO_FIM                BOOLEAN,
                DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITH TIME ZONE,
                DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITH TIME ZONE,
                TIPO_JORNADA                    BOOLEAN,
                DEVICE_IMEI_INICIO              TEXT,
                DEVICE_IMEI_INICIO_RECONHECIDO  BOOLEAN,
                DEVICE_MARCA_INICIO             TEXT,
                DEVICE_MODELO_INICIO            TEXT,
                DEVICE_IMEI_FIM                 TEXT,
                DEVICE_IMEI_FIM_RECONHECIDO     BOOLEAN,
                DEVICE_MARCA_FIM                TEXT,
                DEVICE_MODELO_FIM               TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT I.FONTE_DATA_HORA_INICIO                               AS FONTE_DATA_HORA_INICIO,
       F.FONTE_DATA_HORA_FIM                                  AS FONTE_DATA_HORA_FIM,
       F.JUSTIFICATIVA_ESTOURO                                AS JUSTIFICATIVA_ESTOURO,
       F.JUSTIFICATIVA_TEMPO_RECOMENDADO                      AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
       I.LATITUDE_MARCACAO_INICIO                             AS LATITUDE_MARCACAO_INICIO,
       I.LONGITUDE_MARCACAO_INICIO                            AS LONGITUDE_MARCACAO_INICIO,
       F.LATITUDE_MARCACAO_FIM                                AS LATITUDE_MARCACAO_FIM,
       F.LONGITUDE_MARCACAO_FIM                               AS LONGITUDE_MARCACAO_FIM,
       COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                 AS COD_UNIDADE,
       COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR)         AS CPF_COLABORADOR,
       COALESCE(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)   AS COD_TIPO_INTERVALO,
       COALESCE(
               VITI.CODIGO_TIPO_INTERVALO_POR_UNIDADE,
               VITF.CODIGO_TIPO_INTERVALO_POR_UNIDADE)        AS COD_TIPO_INTERVALO_POR_UNIDADE,
       I.DATA_HORA_INICIO                                     AS DATA_HORA_INICIO,
       F.DATA_HORA_FIM                                        AS DATA_HORA_FIM,
       I.CODIGO_INICIO                                        AS CODIGO_INICIO,
       F.CODIGO_FIM                                           AS CODIGO_FIM,
       I.STATUS_ATIVO_INICIO                                  AS STATUS_ATIVO_INICIO,
       F.STATUS_ATIVO_FIM                                     AS STATUS_ATIVO_FIM,
       I.FOI_AJUSTADO_INICIO                                  AS FOI_AJUSTADO_INICIO,
       F.FOI_AJUSTADO_FIM                                     AS FOI_AJUSTADO_FIM,
       I.DATA_HORA_SINCRONIZACAO_INICIO                       AS DATA_HORA_SINCRONIZACAO_INICIO,
       F.DATA_HORA_SINCRONIZACAO_FIM                          AS DATA_HORA_SINCRONIZACAO_FIM,
       (VITI.TIPO_JORNADA = TRUE OR VITF.TIPO_JORNADA = TRUE) AS TIPO_JORNADA,
       I.DEVICE_IMEI :: TEXT                                  AS DEVICE_IMEI_INICIO,
       I.DEVICE_RECONHECIDO :: BOOLEAN                        AS DEVICE_IMEI_INICIO_RECONHECIDO,
       I.DEVICE_MARCA :: TEXT                                 AS DEVICE_MARCA_INICIO,
       I.DEVICE_MODELO :: TEXT                                AS DEVICE_MODELO_INICIO,
       F.DEVICE_IMEI :: TEXT                                  AS DEVICE_IMEI_FIM,
       F.DEVICE_RECONHECIDO :: BOOLEAN                        AS DEVICE_IMEI_FIM_RECONHECIDO,
       F.DEVICE_MARCA :: TEXT                                 AS DEVICE_MARCA_FIM,
       F.DEVICE_MODELO :: TEXT                                AS DEVICE_MODELO_FIM
FROM VIEW_MARCACAO_INICIOS I
         FULL OUTER JOIN VIEW_MARCACAO_FINS F
                         ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
         LEFT JOIN VIEW_INTERVALO_TIPO VITI
                   ON I.COD_TIPO_INTERVALO = VITI.CODIGO
         LEFT JOIN VIEW_INTERVALO_TIPO VITF
                   ON F.COD_TIPO_INTERVALO = VITF.CODIGO
-- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
-- diretamente para as inner views e elas são executadas já com os filtros aplicados.
-- Inícios - apenas se houver um início.
WHERE CASE
          WHEN I.COD_UNIDADE IS NOT NULL
              THEN
                  CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE I.COD_UNIDADE = F_COD_UNIDADE END
                  AND
                  CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE I.CPF_COLABORADOR = F_CPF_COLABORADOR END
                  AND
                  CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
          ELSE TRUE
    END
-- Fins - apenas se houver um fim.
  AND CASE
          WHEN F.COD_UNIDADE IS NOT NULL
              THEN
                  CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE F.COD_UNIDADE = F_COD_UNIDADE END
                  AND
                  CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE F.CPF_COLABORADOR = F_CPF_COLABORADOR END
                  AND
                  CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE F.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
          ELSE TRUE
    END
$$;