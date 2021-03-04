--######################################################################################################################
--######################################################################################################################
--#######################              FUNCTION PARA VERIFICAR SE UM            ########################################
--#######################                   CHECKLIST JÁ EXISTE                 ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_COD_CHECKLIST_DUPLICADO(
  F_COD_UNIDADE_CHECKLIST           BIGINT,
  F_COD_MODELO_CHECKLIST            BIGINT,
  F_DATA_HORA_REALIZACAO            TIMESTAMP WITH TIME ZONE,
  F_COD_COLABORADOR                 BIGINT,
  F_PLACA_VEICULO                   TEXT,
  F_TIPO_CHECKLIST                  CHAR,
  F_KM_COLETADO                     BIGINT,
  F_TEMPO_REALIZACAO                BIGINT,
  F_FONTE_DATA_HORA_REALIZACAO      TEXT,
  F_VERSAO_APP_MOMENTO_REALIZACAO   INTEGER,
  F_DEVICE_ID                       TEXT,
  F_DEVICE_IMEI                     TEXT,
  F_DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT)
  RETURNS TABLE(
    CHECKLIST_JA_EXISTE BOOLEAN,
    COD_CHECKLIST       BIGINT)
LANGUAGE SQL
AS $$
WITH CTE AS (
    -- O duplo select serve para retornar null caso o código não seja encontrado.
    SELECT (SELECT C.CODIGO AS COD_CHECKLIST
            FROM CHECKLIST C
            WHERE C.COD_UNIDADE = F_COD_UNIDADE_CHECKLIST
                  AND C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
                  AND C.DATA_HORA = F_DATA_HORA_REALIZACAO
                  AND C.CPF_COLABORADOR = (SELECT CO.CPF
                                           FROM COLABORADOR CO
                                           WHERE CO.CODIGO = F_COD_COLABORADOR)
                  AND C.PLACA_VEICULO = F_PLACA_VEICULO
                  AND C.TIPO = F_TIPO_CHECKLIST
                  AND C.KM_VEICULO = F_KM_COLETADO
                  AND C.TEMPO_REALIZACAO = F_TEMPO_REALIZACAO
                  AND C.FONTE_DATA_HORA_REALIZACAO = F_FONTE_DATA_HORA_REALIZACAO
                  AND C.VERSAO_APP_MOMENTO_REALIZACAO = F_VERSAO_APP_MOMENTO_REALIZACAO
                  AND C.DEVICE_ID = F_DEVICE_ID
                  AND C.DEVICE_IMEI = F_DEVICE_IMEI
                  AND C.DEVICE_UPTIME_REALIZACAO_MILLIS = F_DEVICE_UPTIME_REALIZACAO_MILLIS) AS COD_CHECKLIST
)

SELECT
  CTE.COD_CHECKLIST IS NOT NULL,
  CTE.COD_CHECKLIST
FROM CTE;
$$;