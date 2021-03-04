-- Sobre:
--
-- Esta function retorna a lista de perguntas de acordo com a unidade, modelo e versão de um checklist.
--
-- Histórico:
-- 2019-10-02 -> Function criada (wvinim - PL-2231).
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição de colunas de mídia para pergunta e alternativa (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
-- 2020-07-27 -> Insere nova coluna de cod auxiliar (gustavocnp95 - PLI-178)
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO BIGINT,
                                                                          F_COD_VERSAO_MODELO BIGINT)
    RETURNS TABLE
            (
                COD_PERGUNTA             BIGINT,
                COD_CONTEXTO_PERGUNTA    BIGINT,
                COD_IMAGEM               BIGINT,
                URL_IMAGEM               TEXT,
                PERGUNTA                 TEXT,
                ORDEM_PERGUNTA           INTEGER,
                SINGLE_CHOICE            BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK  TEXT,
                COD_ALTERNATIVA          BIGINT,
                COD_CONTEXTO_ALTERNATIVA BIGINT,
                ALTERNATIVA              TEXT,
                PRIORIDADE               TEXT,
                ORDEM_ALTERNATIVA        INTEGER,
                DEVE_ABRIR_ORDEM_SERVICO BOOLEAN,
                ALTERNATIVA_TIPO_OUTROS  BOOLEAN,
                ANEXO_MIDIA              TEXT,
                COD_AUXILIAR_ALTERNATIVA TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT CP.CODIGO                    AS COD_PERGUNTA,
       CP.CODIGO_CONTEXTO           AS COD_CONTEXTO_PERGUNTA,
       CGI.COD_IMAGEM               AS COD_IMAGEM,
       CGI.URL_IMAGEM               AS URL_IMAGEM,
       CP.PERGUNTA                  AS PERGUNTA,
       CP.ORDEM                     AS ORDEM_PERGUNTA,
       CP.SINGLE_CHOICE             AS SINGLE_CHOICE,
       CP.ANEXO_MIDIA_RESPOSTA_OK   AS ANEXO_MIDIA_RESPOSTA_OK,
       CAP.CODIGO                   AS COD_ALTERNATIVA,
       CAP.CODIGO_CONTEXTO          AS COD_CONTEXTO_ALTERNATIVA,
       CAP.ALTERNATIVA              AS ALTERNATIVA,
       CAP.PRIORIDADE :: TEXT       AS PRIORIDADE,
       CAP.ORDEM                    AS ORDEM_ALTERNATIVA,
       CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO,
       CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS,
       CAP.ANEXO_MIDIA              AS ANEXO_MIDIA,
       CAP.COD_AUXILIAR :: TEXT     AS COD_AUXILIAR_ALTERNATIVA
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.CODIGO = CAP.COD_PERGUNTA
                  AND CAP.COD_UNIDADE = CP.COD_UNIDADE
                  AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
                  AND CAP.COD_VERSAO_CHECKLIST_MODELO = CP.COD_VERSAO_CHECKLIST_MODELO
         LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                   ON CGI.COD_IMAGEM = CP.COD_IMAGEM
WHERE CP.COD_UNIDADE = F_COD_UNIDADE
  AND CP.COD_CHECKLIST_MODELO = F_COD_MODELO
  AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
ORDER BY CP.ORDEM, CP.PERGUNTA, CAP.ORDEM;
$$;