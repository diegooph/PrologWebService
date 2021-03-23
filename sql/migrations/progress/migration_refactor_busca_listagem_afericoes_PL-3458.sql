drop function if exists func_afericao_get_afericoes_avulsas_paginada(f_cod_unidade bigint,
                                                                     f_data_inicial date,
                                                                     f_data_final date,
                                                                     f_limit bigint,
                                                                     f_offset bigint,
                                                                     f_tz_unidade text);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADES BIGINT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_LIMIT BIGINT,
                                                                        F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
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
SELECT A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE (SELECT TZ_UNIDADE(A.COD_UNIDADE)) AS DATA_HORA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
   AND A.TIPO_PROCESSO_COLETA = 'PNEU_AVULSO'
  AND (A.DATA_HORA AT TIME ZONE (SELECT TZ_UNIDADE(A.COD_UNIDADE)))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

drop function if exists func_afericao_get_afericoes_placas_paginada(f_cod_unidade bigint,
                                                                    f_cod_tipo_veiculo bigint,
                                                                    f_placa_veiculo text,
                                                                    f_data_inicial date,
                                                                    f_data_final date,
                                                                    f_limit bigint,
                                                                    f_offset bigint,
                                                                    f_tz_unidade text);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADES BIGINT[], F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT)
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
       A.DATA_HORA AT TIME ZONE (SELECT TZ_UNIDADE(A.COD_UNIDADE)) AS DATA_HORA,
       V.PLACA                               AS PLACA_VEICULO,
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
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_COD_TIPO_VEICULO != -1 AND F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO != '' AND F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE (SELECT TZ_UNIDADE(A.COD_UNIDADE)))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;