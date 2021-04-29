CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS_DEPRECATED(F_COD_UNIDADE BIGINT,
                                                                                   F_COD_EQUIPE BIGINT,
                                                                                   F_COD_TIPO_VEICULO BIGINT,
                                                                                   F_PLACA_VEICULO CHARACTER VARYING,
                                                                                   F_DATA_INICIAL DATE,
                                                                                   F_DATA_FINAL DATE,
                                                                                   F_TIMEZONE TEXT,
                                                                                   F_LIMIT INTEGER,
                                                                                   F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                CPF_COLABORADOR               BIGINT,
                PLACA_VEICULO                 TEXT,
                TIPO_CHECKLIST                CHAR,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT,
                OBSERVACAO                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_EQUIPE           INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 0 ELSE 1 END;
    F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 0 ELSE 1 END;
    F_HAS_PLACA_VEICULO    INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 0 ELSE 1 END;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA AT TIME ZONE F_TIMEZONE                  AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               V.PLACA :: TEXT                                      AS PLACA_VEICULO,
               C.TIPO                                               AS TIPO_CHECKLIST,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_ITENS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK,
               C.OBSERVACAO                                         AS OBSERVACAO
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN EQUIPE E
                      ON E.CODIGO = CO.COD_EQUIPE
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
          AND C.COD_UNIDADE = F_COD_UNIDADE
          AND (F_HAS_EQUIPE = 0 OR E.CODIGO = F_COD_EQUIPE)
          AND (F_HAS_COD_TIPO_VEICULO = 0 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
          AND (F_HAS_PLACA_VEICULO = 0 OR V.PLACA = F_PLACA_VEICULO)
        ORDER BY DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;