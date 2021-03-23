-- Sobre:
--
-- Function utilizada para buscar os itens de O.S que estão abertos. Utilizamos o parâmetro
-- 'f_cod_ultimo_item_pendente_sincronizado' para identificar qual foi último item de O.S já sincronizado e buscar os
-- novos a partir dele.
-- A function lista, exclusivamente, itens de O.S que estão PENDENTES no ProLog, estes itens irão compor uma nova
-- ordem de serviço no Sistema Parceiro e lá será feita a resolução.
--
-- Histórico:
-- 2019-02-18 -> Function criada (diogenesvanzella - PL-1603).
-- 2019-11-07 -> Atualiza function para buscar itens de O.S na nova estrutura (diogenesvanzella - PL-2416).
-- 2020-01-08 -> Corrige tipo de retorno das colunas (diogenesvanzella - PL-2416).
-- 2020-01-14 -> Passa a utilizar código do contexto na integração (diogenesvanzella - PL-2416).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO BIGINT,
                                                      F_TOKEN_INTEGRACAO TEXT)
    RETURNS TABLE
            (
                PLACA_VEICULO                      TEXT,
                KM_ABERTURA_SERVICO                BIGINT,
                COD_ORDEM_SERVICO                  BIGINT,
                COD_UNIDADE_ORDEM_SERVICO          BIGINT,
                STATUS_ORDEM_SERVICO               TEXT,
                DATA_HORA_ABERTURA_SERVICO         TIMESTAMP WITHOUT TIME ZONE,
                COD_ITEM_ORDEM_SERVICO             BIGINT,
                COD_UNIDADE_ITEM_ORDEM_SERVICO     BIGINT,
                DATA_HORA_PRIMEIRO_APONTAMENTO     TIMESTAMP WITHOUT TIME ZONE,
                STATUS_ITEM_ORDEM_SERVICO          TEXT,
                PRAZO_RESOLUCAO_ITEM_HORAS         INTEGER,
                QTD_APONTAMENTOS                   INTEGER,
                COD_CHECKLIST_PRIMEIRO_APONTAMENTO BIGINT,
                COD_CONTEXTO_PERGUNTA              BIGINT,
                DESCRICAO_PERGUNTA                 TEXT,
                COD_CONTEXTO_ALTERNATIVA           BIGINT,
                DESCRICAO_ALTERNATIVA              TEXT,
                IS_TIPO_OUTROS                     BOOLEAN,
                DESCRICAO_TIPO_OUTROS              TEXT,
                PRIORIDADE_ALTERNATIVA             TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE TEXT := 'P';
BEGIN
    RETURN QUERY
        SELECT CD.PLACA_VEICULO::TEXT                               AS PLACA_VEICULO,
               CD.KM_VEICULO                                        AS KM_ABERTURA_SERVICO,
               COSD.CODIGO                                          AS COD_ORDEM_SERVICO,
               COSD.COD_UNIDADE                                     AS COD_UNIDADE_ORDEM_SERVICO,
               COSD.STATUS::TEXT                                    AS STATUS_ORDEM_SERVICO,
               CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_ABERTURA_SERVICO,
               COSID.CODIGO                                         AS COD_ITEM_ORDEM_SERVICO,
               COSID.COD_UNIDADE                                    AS COD_UNIDADE_ITEM_ORDEM_SERVICO,
               CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_PRIMEIRO_APONTAMENTO,
               COSID.STATUS_RESOLUCAO::TEXT                         AS STATUS_ITEM_ORDEM_SERVICO,
               CAP.PRAZO                                            AS PRAZO_RESOLUCAO_ITEM_HORAS,
               COSID.QT_APONTAMENTOS                                AS QTD_APONTAMENTOS,
               CD.CODIGO                                            AS COD_CHECKLIST_PRIMEIRO_APONTAMENTO,
               COSID.COD_CONTEXTO_PERGUNTA                          AS COD_CONTEXTO_PERGUNTA,
               CPD.PERGUNTA                                         AS DESCRICAO_PERGUNTA,
               COSID.COD_CONTEXTO_ALTERNATIVA                       AS COD_CONTEXTO_ALTERNATIVA,
               CAPD.ALTERNATIVA                                     AS DESCRICAO_ALTERNATIVA,
               CAPD.ALTERNATIVA_TIPO_OUTROS                         AS IS_TIPO_OUTROS,
               CASE
                   WHEN CAPD.ALTERNATIVA_TIPO_OUTROS
                       THEN
                       (SELECT CRN.RESPOSTA_OUTROS
                        FROM CHECKLIST_RESPOSTAS_NOK CRN
                        WHERE CRN.COD_CHECKLIST = CD.CODIGO
                          AND CRN.COD_ALTERNATIVA = CAPD.CODIGO)
                   ELSE
                       NULL
                   END                                              AS DESCRICAO_TIPO_OUTROS,
               CAPD.PRIORIDADE::TEXT                                AS PRIORIDADE_ALTERNATIVA
        FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                 JOIN CHECKLIST_ORDEM_SERVICO_DATA COSD
                      ON COSID.COD_OS = COSD.CODIGO AND COSID.COD_UNIDADE = COSD.COD_UNIDADE
                 JOIN CHECKLIST_DATA CD ON COSD.COD_CHECKLIST = CD.CODIGO
                 JOIN CHECKLIST_PERGUNTAS_DATA CPD ON COSID.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CPD.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
                      ON COSID.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAPD.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE CAP ON CAPD.PRIORIDADE = CAP.PRIORIDADE
        WHERE COSID.COD_UNIDADE IN (SELECT U.CODIGO
                                    FROM UNIDADE U
                                    WHERE U.COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
          AND COSID.STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE
          AND COSID.CODIGO > F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO
        ORDER BY COSID.CODIGO;
END;
$$;