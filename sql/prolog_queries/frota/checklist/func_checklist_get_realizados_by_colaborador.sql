CREATE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(F_COD_COLABORADOR BIGINT,
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
                COD_COLABORADOR               BIGINT,
                CPF_COLABORADOR               BIGINT,
                NOME_COLABORADOR              TEXT,
                COD_VEICULO                   BIGINT,
                PLACA_VEICULO                 TEXT,
                IDENTIFICADOR_FROTA           TEXT,
                TIPO_CHECKLIST                CHARACTER,
                TOTAL_PERGUNTAS_OK            SMALLINT,
                TOTAL_PERGUNTAS_NOK           SMALLINT,
                TOTAL_ALTERNATIVAS_OK         SMALLINT,
                TOTAL_ALTERNATIVAS_NOK        SMALLINT,
                TOTAL_MIDIAS_PERGUNTAS_OK     SMALLINT,
                TOTAL_MIDIAS_ALTERNATIVAS_NOK SMALLINT,
                TOTAL_NOK_BAIXA               SMALLINT,
                TOTAL_NOK_ALTA                SMALLINT,
                TOTAL_NOK_CRITICA             SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                   AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               CO.CODIGO                                            AS COD_COLABORADOR,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               V.CODIGO                                             AS COD_VEICULO,
               C.PLACA_VEICULO :: TEXT                              AS PLACA_VEICULO,
               V.IDENTIFICADOR_FROTA :: TEXT                        AS IDENTIFICADOR_FROTA,
               C.TIPO                                               AS TIPO_CHECKLIST,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_PERGUNTAS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_PERGUNTAS_NOK,
               C.TOTAL_ALTERNATIVAS_OK                              AS TOTAL_ALTERNATIVAS_OK,
               C.TOTAL_ALTERNATIVAS_NOK                             AS TOTAL_ALTERNATIVAS_NOK,
               C.TOTAL_MIDIAS_PERGUNTAS_OK                          AS TOTAL_MIDIAS_PERGUNTAS_OK,
               C.TOTAL_MIDIAS_ALTERNATIVAS_NOK                      AS TOTAL_MIDIAS_ALTERNATIVAS_NOK,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'BAIXA') :: SMALLINT         AS TOTAL_BAIXA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'ALTA') :: SMALLINT          AS TOTAL_ALTA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'CRITICA') :: SMALLINT       AS TOTAL_CRITICA
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE CO.CODIGO = F_COD_COLABORADOR
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
        ORDER BY C.DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;