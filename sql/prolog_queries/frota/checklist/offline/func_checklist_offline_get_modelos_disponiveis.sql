-- Sobre:
--
-- Function que retorna os modelos de checklist disponíveis para a realização offline.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição das informações de parametrização de fotos (luiz_fp - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_MODELOS_DISPONIVEIS(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_MODELO_CHECKLIST      BIGINT,
                COD_MODELO_CHECKLIST              BIGINT,
                COD_VERSAO_ATUAL_MODELO_CHECKLIST BIGINT,
                NOME_MODELO_CHECKLIST             TEXT,
                COD_PERGUNTA                      BIGINT,
                COD_CONTEXTO_PERGUNTA             BIGINT,
                DESCRICAO_PERGUNTA                TEXT,
                COD_IMAGEM                        BIGINT,
                URL_IMAGEM                        TEXT,
                PERGUNTA_ORDEM_EXIBICAO           INTEGER,
                SINGLE_CHOICE                     BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK           TEXT,
                COD_ALTERNATIVA                   BIGINT,
                COD_CONTEXTO_ALTERNATIVA          BIGINT,
                DESCRICAO_ALTERNATIVA             TEXT,
                TIPO_OUTROS                       BOOLEAN,
                ALTERNATIVA_ORDEM_EXIBICAO        INTEGER,
                PRIORIDADE_ALTERNATIVA            TEXT,
                ANEXO_MIDIA                       TEXT,
                COD_CARGO                         BIGINT,
                COD_TIPO_VEICULO                  BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH CHECKLIST_MODELO_ATIVO AS (
            SELECT CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
                   CM.CODIGO                   AS COD_MODELO_CHECKLIST,
                   CM.COD_VERSAO_ATUAL         AS COD_VERSAO_MODELO_CHECKLIST,
                   CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
                   CP.CODIGO                   AS COD_PERGUNTA,
                   CP.CODIGO_CONTEXTO          AS COD_CONTEXTO_PERGUNTA,
                   CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
                   CP.COD_IMAGEM               AS COD_IMAGEM,
                   CGI.URL_IMAGEM              AS URL_IMAGEM,
                   CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
                   CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
                   CP.ANEXO_MIDIA_RESPOSTA_OK  AS ANEXO_MIDIA_RESPOSTA_OK,
                   CAP.CODIGO                  AS COD_ALTERNATIVA,
                   CAP.CODIGO_CONTEXTO         AS COD_CONTEXTO_ALTERNATIVA,
                   CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
                   CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
                   CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
                   CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
                   CAP.ANEXO_MIDIA             AS ANEXO_MIDIA,
                   NULL :: BIGINT              AS COD_CARGO,
                   NULL :: BIGINT              AS COD_TIPO_VEICULO
            FROM CHECKLIST_MODELO CM
                     JOIN CHECKLIST_PERGUNTAS CP
                          ON CM.CODIGO = CP.COD_CHECKLIST_MODELO
                              AND CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CP.CODIGO = CAP.COD_PERGUNTA
                -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
                     LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                               ON CP.COD_IMAGEM = CGI.COD_IMAGEM
            WHERE CM.COD_UNIDADE = F_COD_UNIDADE
              AND CM.STATUS_ATIVO
        ),

             CHECKLIST_MODELO_CARGO AS (
                 SELECT NULL :: BIGINT           AS COD_UNIDADE_MODELO_CHECKLIST,
                        CMF.COD_CHECKLIST_MODELO AS COD_MODELO_CHECKLIST,
                        NULL :: BIGINT           AS COD_VERSAO_MODELO_CHECKLIST,
                        NULL :: TEXT             AS NOME_MODELO_CHECKLIST,
                        NULL :: BIGINT           AS COD_PERGUNTA,
                        NULL :: BIGINT           AS COD_CONTEXTO_PERGUNTA,
                        NULL :: TEXT             AS DESCRICAO_PERGUNTA,
                        NULL :: BIGINT           AS COD_IMAGEM,
                        NULL :: TEXT             AS URL_IMAGEM,
                        NULL :: INTEGER          AS PERGUNTA_ORDEM_EXIBICAO,
                        NULL :: BOOLEAN          AS SINGLE_CHOICE,
                        NULL :: TEXT             AS ANEXO_MIDIA_RESPOSTA_OK,
                        NULL :: BIGINT           AS COD_ALTERNATIVA,
                        NULL :: BIGINT           AS COD_CONTEXTO_ALTERNATIVA,
                        NULL :: TEXT             AS DESCRICAO_ALTERNATIVA,
                        NULL :: BOOLEAN          AS TIPO_OUTROS,
                        NULL :: INTEGER          AS ALTERNATIVA_ORDEM_EXIBICAO,
                        NULL :: TEXT             AS PRIORIDADE_ALTERNATIVA,
                        NULL :: TEXT             AS ANEXO_MIDIA,
                        CMF.COD_FUNCAO           AS COD_CARGO,
                        NULL :: BIGINT           AS COD_TIPO_VEICULO
                 FROM CHECKLIST_MODELO_FUNCAO CMF
                 WHERE CMF.COD_CHECKLIST_MODELO IN (SELECT CODIGO
                                                    FROM CHECKLIST_MODELO CM
                                                    WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                                      AND CM.STATUS_ATIVO = TRUE)
             ),

             CHECKLIST_MODELO_TIPO_VEICULO AS (
                 SELECT NULL :: BIGINT        AS COD_UNIDADE_MODELO_CHECKLIST,
                        CMVT.COD_MODELO       AS COD_MODELO_CHECKLIST,
                        NULL :: BIGINT        AS COD_VERSAO_MODELO_CHECKLIST,
                        NULL :: TEXT          AS NOME_MODELO_CHECKLIST,
                        NULL :: BIGINT        AS COD_PERGUNTA,
                        NULL :: BIGINT        AS COD_CONTEXTO_PERGUNTA,
                        NULL :: TEXT          AS DESCRICAO_PERGUNTA,
                        NULL :: BIGINT        AS COD_IMAGEM,
                        NULL :: TEXT          AS URL_IMAGEM,
                        NULL :: INTEGER       AS PERGUNTA_ORDEM_EXIBICAO,
                        NULL :: BOOLEAN       AS SINGLE_CHOICE,
                        NULL :: TEXT          AS ANEXO_MIDIA_RESPOSTA_OK,
                        NULL :: BIGINT        AS COD_ALTERNATIVA,
                        NULL :: BIGINT        AS COD_CONTEXTO_ALTERNATIVA,
                        NULL :: TEXT          AS DESCRICAO_ALTERNATIVA,
                        NULL :: BOOLEAN       AS TIPO_OUTROS,
                        NULL :: INTEGER       AS ALTERNATIVA_ORDEM_EXIBICAO,
                        NULL :: TEXT          AS PRIORIDADE_ALTERNATIVA,
                        NULL :: TEXT          AS ANEXO_MIDIA,
                        NULL :: BIGINT        AS COD_CARGO,
                        CMVT.COD_TIPO_VEICULO AS COD_TIPO_VEICULO
                 FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
                 WHERE CMVT.COD_MODELO IN (SELECT CODIGO
                                           FROM CHECKLIST_MODELO CM
                                           WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                             AND CM.STATUS_ATIVO = TRUE)
             ),

             CHECKLISTS_FILTRADOS AS (
                 SELECT *
                 FROM CHECKLIST_MODELO_ATIVO
                 UNION ALL
                 SELECT *
                 FROM CHECKLIST_MODELO_CARGO
                 UNION ALL
                 SELECT *
                 FROM CHECKLIST_MODELO_TIPO_VEICULO
             )

        SELECT CF.COD_UNIDADE_MODELO_CHECKLIST AS COD_UNIDADE_MODELO_CHECKLIST,
               CF.COD_MODELO_CHECKLIST         AS COD_MODELO_CHECKLIST,
               CF.COD_VERSAO_MODELO_CHECKLIST  AS COD_VERSAO_MODELO_CHECKLIST,
               CF.NOME_MODELO_CHECKLIST        AS NOME_MODELO_CHECKLIST,
               CF.COD_PERGUNTA                 AS COD_PERGUNTA,
               CF.COD_CONTEXTO_PERGUNTA        AS COD_CONTEXTO_PERGUNTA,
               CF.DESCRICAO_PERGUNTA           AS DESCRICAO_PERGUNTA,
               CF.COD_IMAGEM                   AS COD_IMAGEM,
               CF.URL_IMAGEM                   AS URL_IMAGEM,
               CF.PERGUNTA_ORDEM_EXIBICAO      AS PERGUNTA_ORDEM_EXIBICAO,
               CF.SINGLE_CHOICE                AS SINGLE_CHOICE,
               CF.ANEXO_MIDIA_RESPOSTA_OK      AS ANEXO_MIDIA_RESPOSTA_OK,
               CF.COD_ALTERNATIVA              AS COD_ALTERNATIVA,
               CF.COD_CONTEXTO_ALTERNATIVA     AS COD_CONTEXTO_ALTERNATIVA,
               CF.DESCRICAO_ALTERNATIVA        AS DESCRICAO_ALTERNATIVA,
               CF.TIPO_OUTROS                  AS TIPO_OUTROS,
               CF.ALTERNATIVA_ORDEM_EXIBICAO   AS ALTERNATIVA_ORDEM_EXIBICAO,
               CF.PRIORIDADE_ALTERNATIVA       AS PRIORIDADE_ALTERNATIVA,
               CF.ANEXO_MIDIA                  AS ANEXO_MIDIA,
               CF.COD_CARGO                    AS COD_CARGO,
               CF.COD_TIPO_VEICULO             AS COD_TIPO_VEICULO
        FROM CHECKLISTS_FILTRADOS CF
        ORDER BY CF.COD_MODELO_CHECKLIST,
                 CF.PERGUNTA_ORDEM_EXIBICAO,
                 CF.COD_PERGUNTA,
                 CF.ALTERNATIVA_ORDEM_EXIBICAO,
                 CF.COD_ALTERNATIVA,
                 CF.COD_CARGO,
                 CF.COD_TIPO_VEICULO;
END;
$$;