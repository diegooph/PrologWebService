-- Sobre:
--
-- Esta função retorna uma lista de marcações agrupadas, filtradas por tipo, colaborador e dia.
--
-- Histórico:
-- 2019-08-01 -> Function alterada para inserir registros de IMEI (wvinim - PL-2152).
-- 2019-08-07 -> Function alterada para inserir registros de marca e modelo do dispositivo (wvinim - PL-2152)
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_ACOMPANHAMENTO(
  F_COD_INICIO BIGINT,
  F_COD_FIM    BIGINT,
  F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO                    TEXT,
    FONTE_DATA_HORA_FIM                       TEXT,
    JUSTIFICATIVA_ESTOURO                     TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO           TEXT,
    LATITUDE_MARCACAO_INICIO                  TEXT,
    LONGITUDE_MARCACAO_INICIO                 TEXT,
    LATITUDE_MARCACAO_FIM                     TEXT,
    LONGITUDE_MARCACAO_FIM                    TEXT,
    COD_UNIDADE                               BIGINT,
    CPF_COLABORADOR                           TEXT,
    NOME_COLABORADOR                          TEXT,
    NOME_TIPO_MARCACAO                        TEXT,
    DATA_HORA_INICIO                          TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                             TIMESTAMP WITHOUT TIME ZONE,
    TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS BIGINT,
    TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS  BIGINT,
    COD_MARCACAO_INICIO                       BIGINT,
    COD_MARCACAO_FIM                          BIGINT,
    STATUS_ATIVO_INICIO                       BOOLEAN,
    STATUS_ATIVO_FIM                          BOOLEAN,
    FOI_AJUSTADO_INICIO                       BOOLEAN,
    FOI_AJUSTADO_FIM                          BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO            TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM               TIMESTAMP WITHOUT TIME ZONE,
    VERSAO_APP_MOMENTO_MARCACAO_INICIO        INTEGER,
    VERSAO_APP_MOMENTO_MARCACAO_FIM           INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO   INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM      INTEGER,
    TIPO_JORNADA                              BOOLEAN,
    DEVICE_IMEI_INICIO                        TEXT,
    DEVICE_IMEI_INICIO_RECONHECIDO            BOOLEAN,
    DEVICE_MARCA_INICIO                       TEXT,
    DEVICE_MODELO_INICIO                      TEXT,
    DEVICE_IMEI_FIM                           TEXT,
    DEVICE_IMEI_FIM_RECONHECIDO               BOOLEAN,
    DEVICE_MARCA_FIM                          TEXT,
    DEVICE_MODELO_FIM                         TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Se os dois códigos foram fornecidos, garantimos que eles são do mesmo colaborador e também que são marcações
  -- vinculadas.
  IF F_COD_INICIO IS NOT NULL AND F_COD_FIM IS NOT NULL
  THEN
    IF (SELECT I.CPF_COLABORADOR
        FROM INTERVALO I
        WHERE I.CODIGO = F_COD_INICIO) <> (SELECT I.CPF_COLABORADOR
                                           FROM INTERVALO I
                                           WHERE I.CODIGO = F_COD_FIM)
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não pertencem ao mesmo colaborador';
    END IF;
    IF (SELECT NOT EXISTS(SELECT MV.CODIGO
                          FROM MARCACAO_VINCULO_INICIO_FIM MV
                          WHERE MV.COD_MARCACAO_INICIO = F_COD_INICIO AND MV.COD_MARCACAO_FIM = F_COD_FIM))
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não estão vinculadas';
    END IF;
  END IF;

  RETURN QUERY
  WITH INICIOS AS (
      SELECT MI.COD_MARCACAO_INICIO             AS COD_MARCACAO_INICIO,
             MV.COD_MARCACAO_FIM                AS COD_MARCACAO_VINCULO,
             I.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_INICIO,
             I.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_INICIO,
             I.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_INICIO,
             I.COD_UNIDADE                      AS COD_UNIDADE,
             I.CPF_COLABORADOR                  AS CPF_COLABORADOR,
             I.DATA_HORA                        AS DATA_HORA_INICIO,
             I.CODIGO                           AS CODIGO_INICIO,
             I.STATUS_ATIVO                     AS STATUS_ATIVO_INICIO,
             I.FOI_AJUSTADO                     AS FOI_AJUSTADO_INICIO,
             I.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_INICIO,
             I.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
             I.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
             I.DEVICE_IMEI                      AS DEVICE_IMEI,
             DMI.IMEI IS NOT NULL               AS DEVICE_RECONHECIDO,
             I.MARCA_DEVICE                     AS DEVICE_MARCA,
             I.MODELO_DEVICE                    AS DEVICE_MODELO,
             VIT.NOME                           AS NOME_TIPO_MARCACAO,
             VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
             VIT.TIPO_JORNADA                   AS TIPO_JORNADA
      FROM MARCACAO_INICIO MI
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
        JOIN INTERVALO I
          ON MI.COD_MARCACAO_INICIO = I.CODIGO
        JOIN UNIDADE UNI
          ON UNI.CODIGO = I.COD_UNIDADE
        LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
            ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = I.DEVICE_IMEI
        JOIN VIEW_INTERVALO_TIPO VIT
          ON I.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F_IF(F_COD_INICIO IS NULL, I.CODIGO IS NULL, I.CODIGO = F_COD_INICIO)
  ),

      FINS AS (
        SELECT
          MF.COD_MARCACAO_FIM                AS COD_MARCACAO_FIM,
          MV.COD_MARCACAO_INICIO             AS COD_MARCACAO_VINCULO,
          F.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_FIM,
          F.JUSTIFICATIVA_ESTOURO            AS JUSTIFICATIVA_ESTOURO,
          F.JUSTIFICATIVA_TEMPO_RECOMENDADO  AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
          F.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_FIM,
          F.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_FIM,
          F.COD_UNIDADE                      AS COD_UNIDADE,
          F.CPF_COLABORADOR                  AS CPF_COLABORADOR,
          F.DATA_HORA                        AS DATA_HORA_FIM,
          F.CODIGO                           AS CODIGO_FIM,
          F.STATUS_ATIVO                     AS STATUS_ATIVO_FIM,
          F.FOI_AJUSTADO                     AS FOI_AJUSTADO_FIM,
          F.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_FIM,
          F.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
          F.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
          F.DEVICE_IMEI                      AS DEVICE_IMEI,
          DMI.IMEI IS NOT NULL               AS DEVICE_RECONHECIDO,
          F.MARCA_DEVICE                     AS DEVICE_MARCA,
          F.MODELO_DEVICE                    AS DEVICE_MODELO,
          VIT.NOME                           AS NOME_TIPO_MARCACAO,
          VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TIPO_JORNADA                   AS TIPO_JORNADA
        FROM MARCACAO_FIM MF
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
            ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
          JOIN INTERVALO F
            ON MF.COD_MARCACAO_FIM = F.CODIGO
          JOIN UNIDADE UNI
            ON UNI.CODIGO = F.COD_UNIDADE
          LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
            ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = F.DEVICE_IMEI
          JOIN VIEW_INTERVALO_TIPO VIT
            ON F.COD_TIPO_INTERVALO = VIT.CODIGO
        WHERE F_IF(F_COD_FIM IS NULL, F.CODIGO IS NULL, F.CODIGO = F_COD_FIM)
    )

  -- Por algum outro erro que não seja códigos de início e fim de colaboradores diferentes e marcações não vinculadas,
  -- a function poderia também acabar retornando mais de uma linha, preferimos não utilizar limit aqui e deixar esse
  -- erro subir para o servidor tratar.
  SELECT I.FONTE_DATA_HORA_INICIO :: TEXT                                      AS FONTE_DATA_HORA_INICIO,
         F.FONTE_DATA_HORA_FIM :: TEXT                                         AS FONTE_DATA_HORA_FIM,
         F.JUSTIFICATIVA_ESTOURO :: TEXT                                       AS JUSTIFICATIVA_ESTOURO,
         F.JUSTIFICATIVA_TEMPO_RECOMENDADO :: TEXT                             AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
         I.LATITUDE_MARCACAO_INICIO :: TEXT                                    AS LATITUDE_MARCACAO_INICIO,
         I.LONGITUDE_MARCACAO_INICIO :: TEXT                                   AS LONGITUDE_MARCACAO_INICIO,
         F.LATITUDE_MARCACAO_FIM :: TEXT                                       AS LATITUDE_MARCACAO_FIM,
         F.LONGITUDE_MARCACAO_FIM :: TEXT                                      AS LONGITUDE_MARCACAO_FIM,
         COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                                AS COD_UNIDADE,
         LPAD(COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR) :: TEXT, 11, '0') AS CPF_COLABORADOR,
         INITCAP(C.NOME) :: TEXT                                               AS NOME_COLABORADOR,
         COALESCE(I.NOME_TIPO_MARCACAO, F.NOME_TIPO_MARCACAO) :: TEXT          AS NOME_TIPO_MARCACAO,
         I.DATA_HORA_INICIO AT TIME ZONE F_TZ_UNIDADE                          AS DATA_HORA_INICIO,
         F.DATA_HORA_FIM AT TIME ZONE F_TZ_UNIDADE                             AS DATA_HORA_FIM,
         TO_SECONDS(F.DATA_HORA_FIM - I.DATA_HORA_INICIO)                      AS TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS,
         COALESCE(I.TEMPO_RECOMENDADO_MINUTOS,
                  F.TEMPO_RECOMENDADO_MINUTOS) *
         60                                                                    AS TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS,
         I.CODIGO_INICIO                                                       AS CODIGO_INICIO,
         F.CODIGO_FIM                                                          AS CODIGO_FIM,
         I.STATUS_ATIVO_INICIO                                                 AS STATUS_ATIVO_INICIO,
         F.STATUS_ATIVO_FIM                                                    AS STATUS_ATIVO_FIM,
         I.FOI_AJUSTADO_INICIO                                                 AS FOI_AJUSTADO_INICIO,
         F.FOI_AJUSTADO_FIM                                                    AS FOI_AJUSTADO_FIM,
         I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE F_TZ_UNIDADE            AS DATA_HORA_SINCRONIZACAO_INICIO,
         F.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE F_TZ_UNIDADE               AS DATA_HORA_SINCRONIZACAO_FIM,
         I.VERSAO_APP_MOMENTO_MARCACAO_INICIO                                  AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
         F.VERSAO_APP_MOMENTO_MARCACAO_FIM                                     AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
         I.VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO                             AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
         F.VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM                                AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
         (F.TIPO_JORNADA OR I.TIPO_JORNADA)                                    AS TIPO_JORNADA,
         I.DEVICE_IMEI :: TEXT                                                 AS DEVICE_IMEI_INICIO,
         I.DEVICE_RECONHECIDO :: BOOLEAN                                       AS DEVICE_IMEI_INICIO_RECONHECIDO,
         I.DEVICE_MARCA :: TEXT                                                AS DEVICE_MARCA_INICIO,
         I.DEVICE_MODELO :: TEXT                                               AS DEVICE_MODELO_INICIO,
         F.DEVICE_IMEI :: TEXT                                                 AS DEVICE_IMEI_FIM,
         F.DEVICE_RECONHECIDO :: BOOLEAN                                       AS DEVICE_IMEI_FIM_RECONHECIDO,
         F.DEVICE_MARCA :: TEXT                                                AS DEVICE_MARCA_FIM,
         F.DEVICE_MODELO :: TEXT                                               AS DEVICE_MODELO_FIM
  FROM INICIOS I
    FULL OUTER JOIN FINS F
      ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
    JOIN COLABORADOR C
      ON C.CPF = COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR);
END;
$$;