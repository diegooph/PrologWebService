-- Sobre:
-- Relatório mostrando todas as marcações que aconteceram na unidade e período filtrados.
-- Apenas o filtro de CPF é opcional. Para trazer de todos os CPFs, basta passar NULL.
--
-- Histórico:
-- 2019-10-23 -> Adicionado coluna EQUIPE ao relatório (luizfp - PL-2364).
-- 2020-07-30 -> Retira marcações inativas da exibição (wvinim - PL-2832).
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