CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_OS_BUSCA_CHECKLIST_ITENS_NOK(F_COD_CHECKLIST_PROLOG BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_CHECKLIST        BIGINT,
                COD_MODELO_CHECKLIST         BIGINT,
                COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                CPF_COLABORADOR_REALIZACAO   TEXT,
                PLACA_VEICULO_CHECKLIST      TEXT,
                KM_COLETADO_CHECKLIST        BIGINT,
                TIPO_CHECKLIST               TEXT,
                DATA_HORA_REALIZACAO         TIMESTAMP WITHOUT TIME ZONE,
                TOTAL_ALTERNATIVAS_NOK       INTEGER,
                COD_CONTEXTO_PERGUNTA_NOK    BIGINT,
                DESCRICAO_PERGUNTA_NOK       TEXT,
                COD_ALTERNATIVA_NOK          BIGINT,
                COD_CONTEXTO_ALTERNATIVA_NOK BIGINT,
                DESCRICAO_ALTERNATIVA_NOK    TEXT,
                PRIORIDADE_ALTERNATIVA_NOK   TEXT
            )
    LANGUAGE SQL
AS
$$
WITH ALTERNATIVAS AS (
    SELECT CRN.COD_CHECKLIST                                                       AS COD_CHECKLIST,
           COUNT(CAP.CODIGO) OVER (PARTITION BY CAP.DEVE_ABRIR_ORDEM_SERVICO)      AS QTD_ALTERNATIVAS_NOK,
           CP.CODIGO_CONTEXTO                                                      AS COD_CONTEXTO_PERGUNTA,
           CP.PERGUNTA                                                             AS DESCRICAO_PERGUNTA,
           CAP.CODIGO                                                              AS COD_ALTERNATIVA,
           CAP.CODIGO_CONTEXTO                                                     AS COD_CONTEXTO_ALTERNATIVA,
           F_IF(CAP.ALTERNATIVA_TIPO_OUTROS, CRN.RESPOSTA_OUTROS, CAP.ALTERNATIVA) AS DESCRICAO_ALTERNATIVA,
           CAP.PRIORIDADE                                                          AS PRIORIDADE_ALTERNATIVA
    FROM CHECKLIST_RESPOSTAS_NOK CRN
             JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
        -- Fazemos o JOIN apenas para as alternativas que abrem OS, pois para as demais, não nos interessa nada.
                  ON CAP.CODIGO = CRN.COD_ALTERNATIVA AND CAP.DEVE_ABRIR_ORDEM_SERVICO
             JOIN CHECKLIST_PERGUNTAS CP ON CRN.COD_PERGUNTA = CP.CODIGO
    WHERE CRN.COD_CHECKLIST = F_COD_CHECKLIST_PROLOG
)

SELECT C.COD_UNIDADE                                            AS COD_UNIDADE_CHECKLIST,
       C.COD_CHECKLIST_MODELO                                   AS COD_MODELO_CHECKLIST,
       C.COD_VERSAO_CHECKLIST_MODELO                            AS COD_VERSAO_MODELO_CHECKLIST,
       LPAD(C.CPF_COLABORADOR::TEXT, 11, '0')                   AS CPF_COLABORADOR_REALIZACAO,
       C.PLACA_VEICULO::TEXT                                    AS PLACA_VEICULO_CHECKLIST,
       C.KM_VEICULO                                             AS KM_COLETADO_CHECKLIST,
       F_IF(C.TIPO::TEXT = 'S', 'SAIDA'::TEXT, 'RETORNO'::TEXT) AS TIPO_CHECKLIST,
       C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)       AS DATA_HORA_REALIZACAO,
       COALESCE(A.QTD_ALTERNATIVAS_NOK, 0)::INTEGER             AS TOTAL_ALTERNATIVAS_NOK,
       A.COD_CONTEXTO_PERGUNTA                                  AS COD_CONTEXTO_PERGUNTA_NOK,
       A.DESCRICAO_PERGUNTA                                     AS DESCRICAO_PERGUNTA_NOK,
       A.COD_ALTERNATIVA                                        AS COD_ALTERNATIVA_NOK,
       A.COD_CONTEXTO_ALTERNATIVA                               AS COD_CONTEXTO_ALTERNATIVA_NOK,
       A.DESCRICAO_ALTERNATIVA                                  AS DESCRICAO_ALTERNATIVA_NOK,
       A.PRIORIDADE_ALTERNATIVA                                 AS PRIORIDADE_ALTERNATIVA_NOK
FROM CHECKLIST C
         -- Usamos LEFT JOIN para os cenários onde o check não possuir nenhum item NOK, mesmo para esses cenários
         -- devemos retornar as infos do checklist mesmo assim.
         LEFT JOIN ALTERNATIVAS A ON A.COD_CHECKLIST = C.CODIGO
WHERE C.CODIGO = F_COD_CHECKLIST_PROLOG
ORDER BY A.COD_ALTERNATIVA;
$$;