-- Sobre:
-- Esta view busca os tipos ativos de parametrizamção de coleta de mídias na realização de checklist. Ela já considera a
-- config LC_MESSAGES do postgres para retornar o traduzio (ou não) no status_legivel.
--
-- Histórico:
-- 2020-07-07 -> Criação da function (luiz_fp - PL-2705).
-- 2020-07-13 -> Criação de arquivo específico e documentação (wvinim - PL-2824).
CREATE OR REPLACE VIEW TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA AS
SELECT TIPO_PARAMETRIZACAO_MIDIA                     AS TIPO_PARAMETRIZACAO_MIDIA,
       F_IF((SELECT CURRENT_SETTING('lc_messages') = 'es_es.UTF-8'),
            TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_ES,
            TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_PT_BR) AS TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL
FROM TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
WHERE ATIVO = TRUE;