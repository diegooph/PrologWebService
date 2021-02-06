-- Sobre:
-- A function retorna as aferições por placa com paginação.
--
-- Histórico:
-- 2020-05-05 -> Arquivo especifico da function criado (gustavocnp95 - PL-2684).
-- 2020-05-05 -> Adiciona nova coluna de forma de coleta de dados (gustavocnp95 - PL-2684).
-- 2020-06-18 -> Adiciona identificador de frota (thaisksf - PL-2760).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT,
                                                                       F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                IDENTIFICADOR_FROTA   TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;