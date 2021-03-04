-- Sobre:
-- Esta view busca os tipos ativos de forma de coleta dos dados da aferição. Ela já considera a config LC_MESSAGES
-- do postgres para retornar o traduzio (ou não) no status_legivel.
--
-- Histórico:
-- 2020-05-08 -> View criada (luiz_fp - PL-2689).
CREATE OR REPLACE VIEW TYPES.AFERICAO_FORMA_COLETA_DADOS AS
SELECT FORMA_COLETA_DADOS                     AS FORMA_COLETA_DADOS,
       F_IF((SELECT current_setting('lc_messages') = 'es_es.UTF-8'),
            FORMA_COLETA_DADOS_LEGIVEL_ES,
            FORMA_COLETA_DADOS_LEGIVEL_PT_BR) AS STATUS_LEGIVEL,
       ORDEM_EXIBICAO_RELATORIOS              AS ORDEM_EXIBICAO_RELATORIOS,
       ORDEM_EXIBICAO_LISTAGEM                AS ORDEM_EXIBICAO_LISTAGEM
FROM TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
WHERE ATIVO = TRUE;