-- Sobre:
--
-- Essa view retorna uma lista de perguntas de modelos de checklist.
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição de coluna de mídia para pergunta OK (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
CREATE OR REPLACE VIEW CHECKLIST_PERGUNTAS AS
SELECT CP.COD_CHECKLIST_MODELO,
       CP.COD_VERSAO_CHECKLIST_MODELO,
       CP.COD_UNIDADE,
       CP.ORDEM,
       CP.PERGUNTA,
       CP.SINGLE_CHOICE,
       CP.COD_IMAGEM,
       CP.CODIGO,
       CP.CODIGO_CONTEXTO,
       CP.ANEXO_MIDIA_RESPOSTA_OK
FROM CHECKLIST_PERGUNTAS_DATA CP
WHERE CP.DELETADO = FALSE;