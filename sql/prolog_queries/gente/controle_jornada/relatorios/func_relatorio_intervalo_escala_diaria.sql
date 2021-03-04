-- Sobre:
--
-- Esta função retorna o relatório de marcações comparando com a escala diária.
--
-- Histórico:
-- 2019-08-06 -> Criado tracking da function (wvinim - PL-2152).
-- 2020-04-28 -> Aplica 'order by' (luiz_fp - PL-2720).
-- 2020-07-30 -> Retira marcações inativas da exibição (wvinim - PL-2832).
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