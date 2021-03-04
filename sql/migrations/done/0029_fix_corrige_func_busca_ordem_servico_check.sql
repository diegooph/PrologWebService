-- Sobre:
--
-- Function utilizada para buscar uma Ordem de Serviço para ser fechada.
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-04-02 -> Altera join com perguntas e alternativas para utilizar tabela _DATA (luizfp).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(F_COD_UNIDADE BIGINT,
                                                                         F_COD_OS BIGINT,
                                                                         F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA_VEICULO                         TEXT,
                KM_ATUAL_VEICULO                      BIGINT,
                COD_OS                                BIGINT,
                COD_UNIDADE_OS                        BIGINT,
                STATUS_OS                             TEXT,
                DATA_HORA_ABERTURA_OS                 TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_FECHAMENTO_OS               TIMESTAMP WITHOUT TIME ZONE,
                COD_ITEM_OS                           BIGINT,
                COD_UNIDADE_ITEM_OS                   BIGINT,
                DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP WITHOUT TIME ZONE,
                STATUS_ITEM_OS                        TEXT,
                PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
                PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
                QTD_APONTAMENTOS                      INTEGER,
                COD_COLABORADOR_RESOLUCAO             BIGINT,
                NOME_COLABORADOR_RESOLUCAO            TEXT,
                DATA_HORA_RESOLUCAO                   TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_INICIO_RESOLUCAO            TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_FIM_RESOLUCAO               TIMESTAMP WITHOUT TIME ZONE,
                FEEDBACK_RESOLUCAO                    TEXT,
                DURACAO_RESOLUCAO_MINUTOS             BIGINT,
                KM_VEICULO_COLETADO_RESOLUCAO         BIGINT,
                COD_PERGUNTA                          BIGINT,
                DESCRICAO_PERGUNTA                    TEXT,
                COD_ALTERNATIVA                       BIGINT,
                DESCRICAO_ALTERNATIVA                 TEXT,
                ALTERNATIVA_TIPO_OUTROS               BOOLEAN,
                DESCRICAO_TIPO_OUTROS                 TEXT,
                PRIORIDADE_ALTERNATIVA                TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT C.PLACA_VEICULO :: TEXT                                                AS PLACA_VEICULO,
               V.KM                                                                   AS KM_ATUAL_VEICULO,
               COS.CODIGO                                                             AS COD_OS,
               COS.COD_UNIDADE                                                        AS COD_UNIDADE_OS,
               COS.STATUS :: TEXT                                                     AS STATUS_OS,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                                     AS DATA_HORA_ABERTURA_OS,
               COS.DATA_HORA_FECHAMENTO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)        AS DATA_HORA_FECHAMENTO_OS,
               COSI.CODIGO                                                            AS COD_ITEM_OS,
               COS.COD_UNIDADE                                                        AS COD_UNIDADE_ITEM_OS,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
               COSI.STATUS_RESOLUCAO                                                  AS STATUS_ITEM_OS,
               PRIO.PRAZO                                                             AS PRAZO_RESOLUCAO_ITEM_HORAS,
               TO_MINUTES_TRUNC((C.DATA_HORA
                   + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                   -
                                F_DATA_HORA_ATUAL_UTC)                                AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
               COSI.QT_APONTAMENTOS                                                   AS QTD_APONTAMENTOS,
               CO.CODIGO                                                              AS COD_COLABORADOR_RESOLUCAO,
               CO.NOME :: TEXT                                                        AS NOME_COLABORADOR_RESOLUCAO,
               COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)         AS DATA_HORA_RESOLUCAO,
               COSI.DATA_HORA_INICIO_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_INICIO_RESOLUCAO,
               COSI.DATA_HORA_FIM_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)    AS DATA_HORA_FIM_RESOLUCAO,
               COSI.FEEDBACK_CONSERTO                                                 AS FEEDBACK_RESOLUCAO,
               MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                               AS DURACAO_RESOLUCAO_MINUTOS,
               COSI.KM                                                                AS KM_VEICULO_COLETADO_RESOLUCAO,
               CP.CODIGO                                                              AS COD_PERGUNTA,
               CP.PERGUNTA                                                            AS DESCRICAO_PERGUNTA,
               CAP.CODIGO                                                             AS COD_ALTERNATIVA,
               CAP.ALTERNATIVA                                                        AS DESCRICAO_ALTERNATIVA,
               CAP.ALTERNATIVA_TIPO_OUTROS                                            AS ALTERNATIVA_TIPO_OUTROS,
               CASE
                   WHEN CAP.ALTERNATIVA_TIPO_OUTROS
                       THEN
                       (SELECT CRN.RESPOSTA_OUTROS
                        FROM CHECKLIST_RESPOSTAS_NOK CRN
                        WHERE CRN.COD_CHECKLIST = C.CODIGO
                          AND CRN.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT
                   ELSE NULL
                   END                                                                AS DESCRICAO_TIPO_OUTROS,
               CAP.PRIORIDADE :: TEXT                                                 AS PRIORIDADE_ALTERNATIVA
        FROM CHECKLIST C
                 JOIN CHECKLIST_ORDEM_SERVICO COS
                      ON C.CODIGO = COS.COD_CHECKLIST
                 JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                      ON COS.CODIGO = COSI.COD_OS
                          AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                 -- O join com perguntas e alternativas é feito com a tabela _DATA pois OSs de perguntas e alternativas
                 -- deletadas ainda devem ser exibidas.
                 JOIN CHECKLIST_PERGUNTAS_DATA CP
                      ON COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CP.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
                      ON COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAP.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
                      ON CAP.PRIORIDADE = PRIO.PRIORIDADE
                 JOIN VEICULO V
                      ON C.PLACA_VEICULO = V.PLACA
                 LEFT JOIN COLABORADOR CO
                           ON CO.CPF = COSI.CPF_MECANICO
        WHERE COS.CODIGO = F_COD_OS
          AND COS.COD_UNIDADE = F_COD_UNIDADE;
END;
$$;