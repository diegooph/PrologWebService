-- Altera a function de listagem de marcações para remover as inativas
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_LISTAGEM_MARCACOES(
  F_COD_UNIDADE        BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO          TEXT,
    FONTE_DATA_HORA_FIM             TEXT,
    JUSTIFICATIVA_ESTOURO           TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    LATITUDE_MARCACAO_INICIO        TEXT,
    LONGITUDE_MARCACAO_INICIO       TEXT,
    LATITUDE_MARCACAO_FIM           TEXT,
    LONGITUDE_MARCACAO_FIM          TEXT,
    COD_UNIDADE                     BIGINT,
    CPF_COLABORADOR                 TEXT,
    NOME_COLABORADOR                TEXT,
    COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
    COD_TIPO_INTERVALO              BIGINT,
    NOME_TIPO_INTERVALO             TEXT,
    ICONE_TIPO_INTERVALO            TEXT,
    TEMPO_RECOMENDADO_MINUTOS       BIGINT,
    TEMPO_ESTOURO_MINUTOS           BIGINT,
    DATA_HORA_INICIO                TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                   TIMESTAMP WITHOUT TIME ZONE,
    DURACAO_EM_SEGUNDOS             BIGINT,
    COD_MARCACAO_VINCULO            BIGINT,
    COD_MARCACAO_INICIO             BIGINT,
    COD_MARCACAO_FIM                BIGINT,
    STATUS_ATIVO_INICIO             BOOLEAN,
    STATUS_ATIVO_FIM                BOOLEAN,
    FOI_AJUSTADO_INICIO             BOOLEAN,
    FOI_AJUSTADO_FIM                BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITHOUT TIME ZONE,
    TIPO_JORNADA                    BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_TIME_ZONE_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH INTERVALOS AS (
      SELECT IT.*
      FROM INTERVALO IT
      WHERE IT.COD_UNIDADE = F_COD_UNIDADE
            AND F_IF(F_CPF_COLABORADOR IS NULL, TRUE, IT.CPF_COLABORADOR = F_CPF_COLABORADOR)
            AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO)
            AND ((IT.DATA_HORA AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
                 AND (IT.DATA_HORA AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)),
      INICIOS AS (
        SELECT
          MI.COD_MARCACAO_INICIO                AS COD_MARCACAO_INICIO,
          MV.COD_MARCACAO_FIM                   AS COD_MARCACAO_VINCULO,
          I.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_INICIO,
          I.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_INICIO,
          I.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_INICIO,
          I.COD_UNIDADE                         AS COD_UNIDADE,
          I.CPF_COLABORADOR                     AS CPF_COLABORADOR,
          CO.NOME                               AS NOME_COLABORADOR,
          I.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
          VIT.NOME                              AS NOME_TIPO_INTERVALO,
          VIT.ICONE                             AS ICONE_TIPO_INTERVALO,
          VIT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
          I.DATA_HORA                           AS DATA_HORA,
          I.CODIGO                              AS CODIGO_INICIO,
          I.STATUS_ATIVO                        AS STATUS_ATIVO_INICIO,
          I.FOI_AJUSTADO                        AS FOI_AJUSTADO_INICIO,
          I.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_INICIO,
          VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
          VIT.TIPO_JORNADA                      AS TIPO_JORNADA
        FROM MARCACAO_INICIO MI
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
          JOIN INTERVALOS I ON MI.COD_MARCACAO_INICIO = I.CODIGO
          JOIN COLABORADOR CO ON CO.CPF = I.CPF_COLABORADOR
          JOIN VIEW_INTERVALO_TIPO VIT ON I.COD_TIPO_INTERVALO = VIT.CODIGO),
      FINS AS (
        SELECT
          MF.COD_MARCACAO_FIM                   AS COD_MARCACAO_FIM,
          MV.COD_MARCACAO_INICIO                AS COD_MARCACAO_VINCULO,
          F.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_FIM,
          F.JUSTIFICATIVA_ESTOURO               AS JUSTIFICATIVA_ESTOURO,
          F.JUSTIFICATIVA_TEMPO_RECOMENDADO     AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
          F.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_FIM,
          F.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_FIM,
          F.COD_UNIDADE                         AS COD_UNIDADE,
          F.CPF_COLABORADOR                     AS CPF_COLABORADOR,
          CO.NOME                               AS NOME_COLABORADOR,
          F.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
          VIT.NOME                              AS NOME_TIPO_INTERVALO,
          VIT.ICONE                             AS ICONE_TIPO_INTERVALO,
          VIT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
          F.DATA_HORA                           AS DATA_HORA,
          F.CODIGO                              AS CODIGO_FIM,
          F.STATUS_ATIVO                        AS STATUS_ATIVO_FIM,
          F.FOI_AJUSTADO                        AS FOI_AJUSTADO_FIM,
          F.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_FIM,
          VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
          VIT.TIPO_JORNADA                      AS TIPO_JORNADA
        FROM MARCACAO_FIM MF
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
          JOIN INTERVALOS F ON MF.COD_MARCACAO_FIM = F.CODIGO
          JOIN COLABORADOR CO ON CO.CPF = F.CPF_COLABORADOR
          JOIN VIEW_INTERVALO_TIPO VIT ON F.COD_TIPO_INTERVALO = VIT.CODIGO)

  SELECT
    COALESCE(IC.FONTE_DATA_HORA_INICIO, IVI.FONTE_DATA_HORA) :: TEXT              AS FONTE_DATA_HORA_INICIO,
    COALESCE(F.FONTE_DATA_HORA_FIM, IVF.FONTE_DATA_HORA) :: TEXT                  AS FONTE_DATA_HORA_FIM,
    F.JUSTIFICATIVA_ESTOURO                                                       AS JUSTIFICATIVA_ESTOURO,
    F.JUSTIFICATIVA_TEMPO_RECOMENDADO                                             AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
    COALESCE(IC.LATITUDE_MARCACAO_INICIO, IVI.LATITUDE_MARCACAO)                  AS LATITUDE_MARCACAO_INICIO,
    COALESCE(IC.LONGITUDE_MARCACAO_INICIO, IVI.LONGITUDE_MARCACAO)                AS LONGITUDE_MARCACAO_INICIO,
    COALESCE(F.LATITUDE_MARCACAO_FIM, IVF.LATITUDE_MARCACAO)                      AS LATITUDE_MARCACAO_FIM,
    COALESCE(F.LONGITUDE_MARCACAO_FIM, IVF.LONGITUDE_MARCACAO)                    AS LONGITUDE_MARCACAO_FIM,
    COALESCE(IC.COD_UNIDADE, F.COD_UNIDADE)                                       AS COD_UNIDADE,
    LPAD(COALESCE(IC.CPF_COLABORADOR, F.CPF_COLABORADOR) :: TEXT, 11, '0')        AS CPF_COLABORADOR,
    COALESCE(IC.NOME_COLABORADOR, F.NOME_COLABORADOR) :: TEXT                     AS NOME_COLABORADOR,
    COALESCE(IC.COD_TIPO_INTERVALO_POR_UNIDADE,
             F.COD_TIPO_INTERVALO_POR_UNIDADE)                                    AS COD_TIPO_INTERVALO_POR_UNIDADE,
    COALESCE(IC.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                         AS COD_TIPO_INTERVALO,
    COALESCE(IC.NOME_TIPO_INTERVALO, F.NOME_TIPO_INTERVALO) :: TEXT               AS NOME_TIPO_INTERVALO,
    COALESCE(IC.ICONE_TIPO_INTERVALO, F.ICONE_TIPO_INTERVALO) :: TEXT             AS ICONE_TIPO_INTERVALO,
    COALESCE(IC.TEMPO_RECOMENDADO_MINUTOS, F.TEMPO_RECOMENDADO_MINUTOS) :: BIGINT AS TEMPO_RECOMENDADO_MINUTOS,
    COALESCE(IC.TEMPO_ESTOURO_MINUTOS, F.TEMPO_ESTOURO_MINUTOS) :: BIGINT         AS TEMPO_ESTOURO_MINUTOS,
    COALESCE(IC.DATA_HORA, IVI.DATA_HORA) AT TIME ZONE F_TIME_ZONE_UNIDADE        AS DATA_HORA_INICIO,
    COALESCE(F.DATA_HORA, IVF.DATA_HORA) AT TIME ZONE F_TIME_ZONE_UNIDADE         AS DATA_HORA_FIM,
    TO_SECONDS(COALESCE(F.DATA_HORA, IVF.DATA_HORA) -
               COALESCE(IC.DATA_HORA, IVI.DATA_HORA))                             AS DURACAO_EM_SEGUNDOS,
    COALESCE(IC.COD_MARCACAO_VINCULO, F.COD_MARCACAO_VINCULO)                     AS COD_MARCACAO_VINCULO,
    COALESCE(IC.CODIGO_INICIO, IVI.CODIGO)                                        AS COD_MARCACAO_INICIO,
    COALESCE(F.CODIGO_FIM, IVF.CODIGO)                                            AS COD_MARCACAO_FIM,
    COALESCE(IC.STATUS_ATIVO_INICIO, IVI.STATUS_ATIVO)                            AS STATUS_ATIVO_INICIO,
    COALESCE(F.STATUS_ATIVO_FIM, IVF.STATUS_ATIVO)                                AS STATUS_ATIVO_FIM,
    COALESCE(IC.FOI_AJUSTADO_INICIO, IVI.FOI_AJUSTADO)                            AS FOI_AJUSTADO_INICIO,
    COALESCE(F.FOI_AJUSTADO_FIM, IVF.FOI_AJUSTADO)                                AS FOI_AJUSTADO_FIM,
    COALESCE(IC.DATA_HORA_SINCRONIZACAO_INICIO,
             IVI.DATA_HORA_SINCRONIZACAO) AT TIME ZONE
    F_TIME_ZONE_UNIDADE                                                           AS DATA_HORA_SINCRONIZACAO_INICIO,
    COALESCE(F.DATA_HORA_SINCRONIZACAO_FIM, IVF.DATA_HORA_SINCRONIZACAO) AT TIME ZONE
    F_TIME_ZONE_UNIDADE                                                           AS DATA_HORA_SINCRONIZACAO_FIM,
    (F.TIPO_JORNADA = TRUE OR IC.TIPO_JORNADA = TRUE)                             AS TIPO_JORNADA
  FROM INICIOS IC
    FULL OUTER JOIN FINS F ON IC.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
    -- Com esses últimos left joins com INTERVALO garantimos que será buscado inícios ou fins vinculados a marcações
    -- que estão fora do filtro de data aplicado na primeira CTE INTERVALOS.
    -- Exemplo: se filtramos de 01/01/19 a 31/01/19 e temos uma marcação iniciada em 31/01/19 e finalizada em 01/02/19,
    -- sem esses left joins, esse fim não seria buscado.
    LEFT JOIN INTERVALO IVI ON IVI.CODIGO = F.COD_MARCACAO_VINCULO
    LEFT JOIN INTERVALO IVF ON IVF.CODIGO = IC.COD_MARCACAO_VINCULO
  ORDER BY
    CPF_COLABORADOR,
    COD_TIPO_INTERVALO,
    COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM);
END;
$$;

-- Altera o relatório de marcações diárias para remover as inativas
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(F_COD_UNIDADE BIGINT,
                                                                     F_DATA_INICIAL DATE,
                                                                     F_DATA_FINAL DATE,
                                                                     F_CPF TEXT)
    RETURNS TABLE
            (
                "NOME"                                         TEXT,
                "MATRÍCULA TRANSPORTADORA"                     TEXT,
                "MATRÍCULA AMBEV"                              TEXT,
                "CARGO"                                        TEXT,
                "SETOR"                                        TEXT,
                "EQUIPE"                                       TEXT,
                "INTERVALO"                                    TEXT,
                "INICIO INTERVALO"                             TEXT,
                "LATITUDE INÍCIO"                              TEXT,
                "LONGITUDE INÍCIO"                             TEXT,
                "FONTE DATA/HORA INÍCIO"                       TEXT,
                "DATA/HORA SINCRONIZAÇÃO INÍCIO"               TEXT,
                "FIM INTERVALO"                                TEXT,
                "LATITUDE FIM"                                 TEXT,
                "LONGITUDE FIM"                                TEXT,
                "FONTE DATA/HORA FIM"                          TEXT,
                "DATA/HORA SINCRONIZAÇÃO FIM"                  TEXT,
                "TEMPO DECORRIDO (MINUTOS)"                    TEXT,
                "TEMPO RECOMENDADO (MINUTOS)"                  BIGINT,
                "CUMPRIU TEMPO MÍNIMO"                         TEXT,
                "JUSTIFICATIVA NÃO CUMPRIMENTO TEMPO MÍNIMO"   TEXT,
                "JUSTIFICATIVA ESTOURO TEMPO MÁXIMO PERMITIDO" TEXT,
                "DISTANCIA ENTRE INÍCIO E FIM (METROS)"        TEXT,
                "DEVICE IMEI INÍCIO"                           TEXT,
                "DEVICE IMEI INÍCIO RECONHECIDO"               TEXT,
                "DEVICE IMEI FIM"                              TEXT,
                "DEVICE IMEI FIM RECONHECIDO"                  TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.NOME                                                                       AS NOME_COLABORADOR,
       COALESCE(C.MATRICULA_TRANS :: TEXT, '-')                                     AS MATRICULA_TRANS,
       COALESCE(C.MATRICULA_AMBEV :: TEXT, '-')                                     AS MATRICULA_AMBEV,
       F.NOME                                                                       AS CARGO,
       S.NOME                                                                       AS SETOR,
       E.NOME                                                                       AS EQUIPE,
       IT.NOME                                                                      AS INTERVALO,
       F_IF(I.STATUS_ATIVO_INICIO,
            COALESCE(
                    TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                            'DD/MM/YYYY HH24:MI:SS'),
                    ''),
            '')                                                                     AS DATA_HORA_INICIO,
       F_IF(I.STATUS_ATIVO_INICIO,
            I.LATITUDE_MARCACAO_INICIO :: TEXT,
            '')                                                                     AS LATITUDE_INICIO,
       F_IF(I.STATUS_ATIVO_INICIO,
            I.LONGITUDE_MARCACAO_INICIO :: TEXT,
            '')                                                                     AS LONGITUDE_INICIO,
       F_IF(I.STATUS_ATIVO_INICIO,
            I.FONTE_DATA_HORA_INICIO :: TEXT,
            '')                                                                     AS FONTE_DATA_HORA_INICIO,
       F_IF(I.STATUS_ATIVO_INICIO,
            COALESCE(
                    TO_CHAR(I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                            'DD/MM/YYYY HH24:MI:SS'),
                    ''),
            '')                                                                     AS DATA_HORA_SINCRONIZACAO_INICIO,

       F_IF(I.STATUS_ATIVO_FIM,
            COALESCE(
                    TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                            'DD/MM/YYYY HH24:MI:SS'),
                    ''),
            '')                                                                     AS DATA_HORA_FIM,
       F_IF(I.STATUS_ATIVO_FIM,
            I.LATITUDE_MARCACAO_FIM :: TEXT,
            '')                                                                     AS LATITUDE_FIM,
       F_IF(I.STATUS_ATIVO_FIM,
            I.LONGITUDE_MARCACAO_FIM :: TEXT,
            '')                                                                     AS LONGITUDE_FIM,
       F_IF(I.STATUS_ATIVO_FIM,
            I.FONTE_DATA_HORA_FIM :: TEXT,
            '')                                                                     AS FONTE_DATA_HORA_FIM,
       F_IF(I.STATUS_ATIVO_FIM,
            COALESCE(
                    TO_CHAR(I.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                            'DD/MM/YYYY HH24:MI:SS'),
                    ''),
            '')                                                                     AS DATA_HORA_SINCRONIZACAO_FIM,
       F_IF(I.STATUS_ATIVO_INICIO AND I.STATUS_ATIVO_FIM,
            COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60) :: TEXT,
                     ''), '')
                                                                                    AS TEMPO_DECORRIDO_MINUTOS,
       IT.TEMPO_RECOMENDADO_MINUTOS,
       F_IF(I.STATUS_ATIVO_INICIO AND I.STATUS_ATIVO_FIM,
            CASE
                WHEN I.DATA_HORA_FIM IS NULL OR I.DATA_HORA_INICIO IS NULL
                    THEN ''
                WHEN IT.TEMPO_RECOMENDADO_MINUTOS >
                     (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
                    THEN 'NÃO'
                ELSE 'SIM' END, '')                                                 AS CUMPRIU_TEMPO_MINIMO,
       I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
       I.JUSTIFICATIVA_ESTOURO,
       F_IF(I.STATUS_ATIVO_INICIO AND I.STATUS_ATIVO_FIM,
            COALESCE(TRUNC((ST_DISTANCE(
                    ST_POINT(I.LONGITUDE_MARCACAO_INICIO :: FLOAT,
                             I.LATITUDE_MARCACAO_INICIO :: FLOAT) :: GEOGRAPHY,
                    ST_POINT(I.LONGITUDE_MARCACAO_FIM :: FLOAT,
                             I.LATITUDE_MARCACAO_FIM :: FLOAT) :: GEOGRAPHY))) :: TEXT,
                     '-'),
            '')                                                                     AS DISTANCIA,
       F_IF(I.STATUS_ATIVO_INICIO, COALESCE(I.DEVICE_IMEI_INICIO :: TEXT, '-'), '') AS DEVICE_IMEI_INICIO,
       F_IF(
               I.DEVICE_IMEI_INICIO IS NOT NULL AND I.STATUS_ATIVO_INICIO,
               F_IF(I.DEVICE_IMEI_INICIO_RECONHECIDO, 'SIM', 'NÃO' :: TEXT),
               '-' :: TEXT)                                                         AS DEVICE_IMEI_INICIO_RECONHECIDO,
       F_IF(I.STATUS_ATIVO_FIM, COALESCE(I.DEVICE_IMEI_FIM :: TEXT, '-'), '')       AS DEVICE_IMEI_FIM,
       F_IF(
               I.DEVICE_IMEI_FIM IS NOT NULL AND I.STATUS_ATIVO_FIM,
               F_IF(I.DEVICE_IMEI_FIM_RECONHECIDO, 'SIM', 'NÃO' :: TEXT), '-')      AS DEVICE_IMEI_FIM_RECONHECIDO
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, CASE
                                                  WHEN F_CPF = '%'
                                                      THEN NULL
                                                  ELSE F_CPF :: BIGINT END, NULL) I
         JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
         JOIN INTERVALO_TIPO IT ON IT.CODIGO = I.COD_TIPO_INTERVALO
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR
         JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE
WHERE ((I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
           BETWEEN F_DATA_INICIAL
           AND F_DATA_FINAL
    OR (I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
           BETWEEN F_DATA_INICIAL
           AND F_DATA_FINAL)
ORDER BY I.DATA_HORA_INICIO, C.NOME
$$;

-- Altera a function base para relatórios de intervalo versus mapas para remover as marcações inativas
CREATE OR REPLACE FUNCTION func_marcacao_intervalos_versus_mapas(F_COD_UNIDADE BIGINT,
                                                                 F_DATA_INICIAL DATE,
                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                DATA                        DATE,
                MAPA                        INTEGER,
                COD_UNIDADE                 BIGINT,
                INTERVALOS_PREVISTOS        INTEGER,
                INTERVALOS_REALIZADOS       INTEGER,
                CPF_MOTORISTA               BIGINT,
                NOME_MOTORISTA              TEXT,
                INICIO_INTERVALO_MOT        TEXT,
                FIM_INTERVALO_MOT           TEXT,
                MARCACOES_RECONHECIDAS_MOT  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_MOT TEXT,
                MOT_CUMPRIU_TEMPO_MINIMO    TEXT,
                CPF_AJ1                     BIGINT,
                NOME_AJ1                    TEXT,
                INICIO_INTERVALO_AJ1        TEXT,
                FIM_INTERVALO_AJ1           TEXT,
                MARCACOES_RECONHECIDAS_AJ1  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_AJ1 TEXT,
                AJ1_CUMPRIU_TEMPO_MINIMO    TEXT,
                CPF_AJ2                     BIGINT,
                NOME_AJ2                    TEXT,
                INICIO_INTERVALO_AJ2        TEXT,
                FIM_INTERVALO_AJ2           TEXT,
                MARCACOES_RECONHECIDAS_AJ2  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_AJ2 TEXT,
                AJ2_CUMPRIU_TEMPO_MINIMO    TEXT
            )
    LANGUAGE SQL
AS
$$
WITH INTERVALOS_AGRUPADOS AS (
    SELECT coalesce(I.CPF_COLABORADOR, F.CPF_COLABORADOR)                      AS CPF_COLABORADOR,
           coalesce(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                AS COD_TIPO_INTERVALO,
           F_IF(I.STATUS_ATIVO_INICIO, I.DATA_HORA_INICIO, NULL)                 AS DATA_HORA_INICIO,
           F_IF(I.STATUS_ATIVO_INICIO, (I.DATA_HORA_INICIO AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE,
                NULL)                                                            AS DATA_INICIO_TZ,
           F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL)                       AS DATA_HORA_FIM,
           F_IF(F.STATUS_ATIVO_FIM, (F.DATA_HORA_FIM AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE,
                NULL)                                                            AS DATA_FIM_TZ,
           F_IF(I.STATUS_ATIVO_INICIO, I.DEVICE_RECONHECIDO :: BOOLEAN, NULL) AS DEVICE_IMEI_INICIO_RECONHECIDO,
           F_IF(F.STATUS_ATIVO_FIM, F.DEVICE_RECONHECIDO :: BOOLEAN, NULL)    AS DEVICE_IMEI_FIM_RECONHECIDO,
           tz_unidade(coalesce(I.COD_UNIDADE, F.COD_UNIDADE))                  AS TZ_UNIDADE
    FROM VIEW_MARCACAO_INICIOS I
             FULL OUTER JOIN VIEW_MARCACAO_FINS F
                             ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
         -- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
         -- diretamente para as inner views e elas são executadas já com os filtros aplicados.
         -- Inícios.
    WHERE I.COD_UNIDADE = F_COD_UNIDADE
      -- Fins.
      AND F.COD_UNIDADE = F_COD_UNIDADE
      AND (((I.DATA_HORA_INICIO AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL)
        OR
           ((F.DATA_HORA_FIM AT TIME ZONE tz_unidade(F.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL))
)

SELECT M.DATA                        AS DATA,
       M.MAPA                        AS MAPA,
       M.COD_UNIDADE                 AS COD_UNIDADE,
       -- (PL-2220) Antes era utilizado "m.fator + 1", mas isso não funciona porque acaba considerando colaboradores
       -- que não estão cadastrados no Prolog e possuem apenas os mapas importados. Lembre-se que fator é quantidade de
       -- ajudantes que sairam junto do motorista. Podendo ser, atualmente, 1 ou 2.
       (F_IF(MOT.CPF IS NULL, 0, 1) + F_IF(AJ1.CPF IS NULL, 0, 1) +
        F_IF(AJ2.CPF IS NULL, 0, 1)) AS INTERVALOS_PREVISTOS,
       (F_IF(INTERVALO_MOT.DATA_HORA_FIM IS NULL OR INTERVALO_MOT.DATA_HORA_INICIO IS NULL, 0, 1) +
        F_IF(INTERVALO_AJ1.DATA_HORA_FIM IS NULL OR INTERVALO_AJ1.DATA_HORA_INICIO IS NULL, 0, 1) +
        F_IF(INTERVALO_AJ2.DATA_HORA_FIM IS NULL OR INTERVALO_AJ2.DATA_HORA_INICIO IS NULL, 0,
             1))                     AS INTERVALOS_REALIZADOS,
       MOT.CPF                       AS CPF_MOTORISTA,
       MOT.NOME                      AS NOME_MOTORISTA,
       COALESCE(to_char(INTERVALO_MOT.DATA_HORA_INICIO AT TIME ZONE INTERVALO_MOT.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_MOT,
       COALESCE(to_char(INTERVALO_MOT.DATA_HORA_FIM AT TIME ZONE INTERVALO_MOT.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_MOT,
       F_IF(INTERVALO_MOT.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_MOT.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_MOT,
       coalesce(to_minutes_trunc(INTERVALO_MOT.DATA_HORA_FIM - INTERVALO_MOT.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_MOT,
       CASE
           WHEN (INTERVALO_MOT.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_MOT.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_MOT.DATA_HORA_FIM - INTERVALO_MOT.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS MOT_CUMPRIU_TEMPO_MINIMO,
       AJ1.CPF                       AS CPF_AJ1,
       COALESCE(AJ1.NOME, '-')       AS NOME_AJ1,
       COALESCE(to_char(INTERVALO_AJ1.DATA_HORA_INICIO AT TIME ZONE INTERVALO_AJ1.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_AJ1,
       COALESCE(to_char(INTERVALO_AJ1.DATA_HORA_FIM AT TIME ZONE INTERVALO_AJ1.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_AJ1,
       F_IF(INTERVALO_AJ1.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_AJ1.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_AJ1,
       coalesce(to_minutes_trunc(INTERVALO_AJ1.DATA_HORA_FIM - INTERVALO_AJ1.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_AJ1,
       CASE
           WHEN (INTERVALO_AJ1.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_AJ1.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_AJ1.DATA_HORA_FIM - INTERVALO_AJ1.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS AJ1_CUMPRIU_TEMPO_MINIMO,
       AJ2.CPF                       AS CPF_AJ2,
       COALESCE(AJ2.NOME, '-')       AS NOME_AJ2,
       COALESCE(to_char(INTERVALO_AJ2.DATA_HORA_INICIO AT TIME ZONE INTERVALO_AJ2.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_AJ2,
       COALESCE(to_char(INTERVALO_AJ2.DATA_HORA_FIM AT TIME ZONE INTERVALO_AJ2.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_AJ2,
       F_IF(INTERVALO_AJ2.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_AJ2.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_AJ2,
       coalesce(to_minutes_trunc(INTERVALO_AJ2.DATA_HORA_FIM - INTERVALO_AJ2.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_AJ2,
       CASE
           WHEN (INTERVALO_AJ2.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_AJ2.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_AJ2.DATA_HORA_FIM - INTERVALO_AJ2.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS AJ2_CUMPRIU_TEMPO_MINIMO
FROM MAPA M
         JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP
              ON UFP.COD_UNIDADE = M.COD_UNIDADE
         JOIN COLABORADOR MOT
              ON MOT.COD_UNIDADE = M.COD_UNIDADE
                  AND MOT.COD_FUNCAO = UFP.COD_FUNCAO_MOTORISTA
                  AND MOT.MATRICULA_AMBEV = M.MATRICMOTORISTA
         LEFT JOIN COLABORADOR AJ1
                   ON AJ1.COD_UNIDADE = M.COD_UNIDADE
                       AND AJ1.COD_FUNCAO = UFP.COD_FUNCAO_AJUDANTE
                       AND AJ1.MATRICULA_AMBEV = M.MATRICAJUD1
         LEFT JOIN COLABORADOR AJ2
                   ON AJ2.COD_UNIDADE = M.COD_UNIDADE
                       AND AJ2.COD_FUNCAO = UFP.COD_FUNCAO_AJUDANTE
                       AND AJ2.MATRICULA_AMBEV = M.MATRICAJUD2
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_MOT
                   ON INTERVALO_MOT.CPF_COLABORADOR = MOT.CPF
                       AND INTERVALO_MOT.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_MOT.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_MOT
                   ON TIPO_MOT.CODIGO = INTERVALO_MOT.COD_TIPO_INTERVALO
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_AJ1
                   ON INTERVALO_AJ1.CPF_COLABORADOR = AJ1.CPF
                       AND INTERVALO_AJ1.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_AJ1.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_AJ1
                   ON TIPO_AJ1.CODIGO = INTERVALO_AJ1.COD_TIPO_INTERVALO
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_AJ2
                   ON INTERVALO_AJ2.CPF_COLABORADOR = AJ2.CPF
                       AND INTERVALO_AJ2.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_AJ2.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_AJ2
                   ON TIPO_AJ2.CODIGO = INTERVALO_AJ2.COD_TIPO_INTERVALO
WHERE M.COD_UNIDADE = F_COD_UNIDADE
  AND M.DATA BETWEEN F_DATA_INICIAL AND F_DATA_FINAL;
$$;

-- Altera o relatório de intervalo no padrão da portaria 1510 para remover as marcações inativas
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
          AND F_IF(F_CPF_COLABORADOR IS NULL, TRUE, VI.CPF_COLABORADOR = F_CPF_COLABORADOR)
          AND VI.STATUS_ATIVO;
END;
$$;

-- Altera o relatório de exportação genérica para remover as marcações inativas
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_EXPORTACAO_GENERICA(F_COD_UNIDADE             BIGINT,
                                                                       F_COD_TIPO_INTERVALO      BIGINT,
                                                                       F_COD_COLABORADOR         BIGINT,
                                                                       F_APENAS_MARCACOES_ATIVAS BOOLEAN,
                                                                       F_DATA_INICIAL            DATE,
                                                                       F_DATA_FINAL              DATE)
    RETURNS TABLE
            (
                PIS           TEXT,
                EVENTO        TEXT,
                DATA          TEXT,
                HORA          TEXT,
                NUMERORELOGIO TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
    RETURN QUERY
        SELECT LPAD(C.PIS :: TEXT, 11, '0')                             AS PIS,
               COALESCE(IT.COD_AUXILIAR, '00')                          AS EVENTO,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'DDMMYYYY') AS DATA,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'HH24mi')   AS HORA,
               COALESCE(U.COD_AUXILIAR, '00')                           AS NUMERORELOGIO
        FROM INTERVALO I
                 JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF
                 JOIN INTERVALO_TIPO IT ON I.COD_UNIDADE = IT.COD_UNIDADE AND I.COD_TIPO_INTERVALO = IT.CODIGO
                 JOIN UNIDADE U ON I.COD_UNIDADE = U.CODIGO
        WHERE I.COD_UNIDADE = F_COD_UNIDADE
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE >= F_DATA_INICIAL
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE <= F_DATA_FINAL
          AND C.PIS IS NOT NULL
          AND C.PIS <> ''
          AND F_IF(F_COD_COLABORADOR IS NULL, TRUE, C.CODIGO = F_COD_COLABORADOR)
          AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.CODIGO = F_COD_TIPO_INTERVALO)
          AND F_IF(F_APENAS_MARCACOES_ATIVAS IS NULL, TRUE, I.STATUS_ATIVO)
          AND I.STATUS_ATIVO;
END;
$$;

-- Altera o relatório de comparação de marcações com escalas diárias para remover as marcações inativas
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_ESCALA_DIARIA(F_COD_UNIDADE BIGINT,
                                                                  F_COD_TIPO_INTERVALO BIGINT,
                                                                  F_DATA_INICIAL DATE,
                                                                  F_DATA_FINAL DATE,
                                                                  F_TIME_ZONE_UNIDADE TEXT)
    RETURNS TABLE
            (
                "UNIDADE"                     TEXT,
                "PLACA VEÍCULO"               TEXT,
                "CÓDIGO ROTA (MAPA)"          BIGINT,
                "DATA"                        TEXT,
                "TIPO DE INTERVALO"           TEXT,
                "MOTORISTA"                   TEXT,
                "INÍCIO INTERVALO MOTORISTA"  TEXT,
                "FIM INTERVALO MOTORISTA"     TEXT,
                "MARCAÇÕES RECONHECIDAS MOT"  TEXT,
                "AJUDANTE 1"                  TEXT,
                "INÍCIO INTERVALO AJUDANTE 1" TEXT,
                "FIM INTERVALO AJUDANTE 1"    TEXT,
                "MARCAÇÕES RECONHECIDAS AJ 1" TEXT,
                "AJUDANTE 2"                  TEXT,
                "INÍCIO INTERVALO AJUDANTE 2" TEXT,
                "FIM INTERVALO AJUDANTE 2"    TEXT,
                "MARCAÇÕES RECONHECIDAS AJ 2" TEXT
            )
    LANGUAGE SQL
AS
$$
WITH TABLE_INTERVALOS AS (SELECT *
                          FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, F_COD_TIPO_INTERVALO) F
                          WHERE (COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) AT TIME ZONE
                                 F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
                            AND (COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) AT TIME ZONE
                                 F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)

SELECT (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE),
       ED.PLACA,
       ED.MAPA,
       TO_CHAR(ED.DATA, 'DD/MM/YYYY'),
       (SELECT IT.NOME FROM INTERVALO_TIPO IT WHERE IT.CODIGO = F_COD_TIPO_INTERVALO),
       -- MOTORISTA
       F_IF(CM.CPF IS NULL, 'MOTORISTA NÃO CADASTRADO', CM.NOME)    AS NOME_MOTORISTA,
       F_IF(INT_MOT.DATA_HORA_INICIO IS NOT NULL AND INT_MOT.STATUS_ATIVO_INICIO,
            TO_CHAR(INT_MOT.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_MOT.DATA_HORA_FIM IS NOT NULL AND INT_MOT.STATUS_ATIVO_FIM,
            TO_CHAR(INT_MOT.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_MOT.DEVICE_IMEI_INICIO_RECONHECIDO
                        AND INT_MOT.DEVICE_IMEI_FIM_RECONHECIDO
                        AND INT_MOT.STATUS_ATIVO_INICIO
                        AND INT_MOT.STATUS_ATIVO_FIM,
                    TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_MOT,
       -- AJUDANTE 1
       F_IF(CA1.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA1.NOME) AS NOME_AJUDANTE_1,
       F_IF(INT_AJ1.DATA_HORA_INICIO IS NOT NULL AND INT_AJ1.STATUS_ATIVO_INICIO,
            TO_CHAR(INT_AJ1.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ1.DATA_HORA_FIM IS NOT NULL AND INT_AJ1.STATUS_ATIVO_FIM,
            TO_CHAR(INT_AJ1.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ1.DEVICE_IMEI_INICIO_RECONHECIDO
                        AND INT_AJ1.DEVICE_IMEI_FIM_RECONHECIDO
                        AND INT_AJ1.STATUS_ATIVO_INICIO
                        AND INT_AJ1.STATUS_ATIVO_FIM
                   , TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ1,
       -- AJUDANTE 2
       F_IF(CA2.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA2.NOME) AS NOME_AJUDANTE_2,
       F_IF(INT_AJ2.DATA_HORA_INICIO IS NOT NULL AND INT_AJ2.STATUS_ATIVO_INICIO,
            TO_CHAR(INT_AJ2.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ2.DATA_HORA_FIM IS NOT NULL AND INT_AJ2.STATUS_ATIVO_FIM,
            TO_CHAR(INT_AJ2.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ2.DEVICE_IMEI_INICIO_RECONHECIDO
                        AND INT_AJ2.DEVICE_IMEI_FIM_RECONHECIDO
                        AND INT_AJ2.STATUS_ATIVO_INICIO
                        AND INT_AJ2.STATUS_ATIVO_FIM, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ2
FROM ESCALA_DIARIA AS ED
         LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA
         LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1
         LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2
         LEFT JOIN TABLE_INTERVALOS INT_MOT
                   ON (COALESCE(INT_MOT.DATA_HORA_INICIO, INT_MOT.DATA_HORA_FIM) AT TIME ZONE
                       F_TIME_ZONE_UNIDADE) :: DATE =
                      ED.DATA
                       AND INT_MOT.CPF_COLABORADOR = ED.CPF_MOTORISTA
         LEFT JOIN TABLE_INTERVALOS INT_AJ1
                   ON (COALESCE(INT_AJ1.DATA_HORA_INICIO, INT_AJ1.DATA_HORA_FIM) AT TIME ZONE
                       F_TIME_ZONE_UNIDADE) :: DATE =
                      ED.DATA
                       AND INT_AJ1.CPF_COLABORADOR = ED.CPF_AJUDANTE_1
         LEFT JOIN TABLE_INTERVALOS INT_AJ2
                   ON (COALESCE(INT_AJ2.DATA_HORA_INICIO, INT_AJ2.DATA_HORA_FIM) AT TIME ZONE
                       F_TIME_ZONE_UNIDADE) :: DATE =
                      ED.DATA
                       AND INT_AJ2.CPF_COLABORADOR = ED.CPF_AJUDANTE_2

WHERE (ED.DATA >= F_DATA_INICIAL AND ED.DATA <= F_DATA_FINAL)
  AND ED.COD_UNIDADE = F_COD_UNIDADE
ORDER BY ED.COD_UNIDADE, ED.DATA DESC;
$$;
