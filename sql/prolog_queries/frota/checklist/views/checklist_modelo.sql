-- Sobre:
--
-- Essa view retorna uma lista de modelos de checklist.
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE VIEW CHECKLIST_MODELO AS
    SELECT CM.COD_UNIDADE,
           CM.CODIGO,
           CM.COD_VERSAO_ATUAL,
           CM.NOME,
           CM.STATUS_ATIVO
    FROM CHECKLIST_MODELO_DATA CM
    WHERE CM.DELETADO = FALSE;