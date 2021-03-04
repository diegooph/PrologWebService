-- Sobre:
--
-- Essa view retorna uma lista de alternativas de perguntas dos modelos de checklist.
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição de coluna de mídia alternativa NOK (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
CREATE OR REPLACE VIEW CHECKLIST_ALTERNATIVA_PERGUNTA AS
SELECT CAP.COD_CHECKLIST_MODELO,
       CAP.COD_VERSAO_CHECKLIST_MODELO,
       CAP.COD_UNIDADE,
       CAP.ALTERNATIVA,
       CAP.ORDEM,
       CAP.COD_PERGUNTA,
       CAP.CODIGO,
       CAP.CODIGO_CONTEXTO,
       CAP.ALTERNATIVA_TIPO_OUTROS,
       CAP.PRIORIDADE,
       CAP.DEVE_ABRIR_ORDEM_SERVICO,
       CAP.ANEXO_MIDIA
FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
WHERE CAP.DELETADO = FALSE;