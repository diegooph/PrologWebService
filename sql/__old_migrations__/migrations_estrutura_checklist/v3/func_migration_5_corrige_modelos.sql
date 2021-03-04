create or replace function migration_checklist.func_migration_5_corrige_modelo()
    returns void
    language plpgsql
as
$$
begin
    -- #####################################################################################################################
-- #####################################################################################################################
-- ####### CORRIGE OS ÚLTIMOS MODELOS DE CHECK PARA DELETAR LOGICAMENTE AS PERGUNTAS E ALTERNATIVAS INATIVAS ###########
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2230
-- Sobre: Encontramos divergências na contagem de perguntas e alternativas nos modelos de checklist devido a um problema
-- na antiga estrutura, para corrigir, precisamos deletar logicamente todas as perguntas e alternativas inativas das
-- últimas versões de modelos de checklist na estrutura nova e alterar as dependências.

-- Deleta logicamente as alternativas inativas do últimos modelos de checklist
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_VERSAO_CHECKLIST_MODELO IN
          (SELECT COD_VERSAO_ATUAL FROM CHECKLIST_MODELO_DATA ORDER BY COD_VERSAO_ATUAL DESC)
      AND STATUS_ATIVO = FALSE;

-- Deleta logicamente as perguntas inativas do últimos modelos de checklist
    UPDATE CHECKLIST_PERGUNTAS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_VERSAO_CHECKLIST_MODELO IN
          (SELECT COD_VERSAO_ATUAL FROM CHECKLIST_MODELO_DATA ORDER BY COD_VERSAO_ATUAL DESC)
      AND STATUS_ATIVO = FALSE;

-- DROPA A VIEW CHECKLIST_ALTERNATIVA_PERGUNTA
    DROP VIEW CHECKLIST_ALTERNATIVA_PERGUNTA;

-- DROPA COLUNA DE STATUS DAS ALTERNATIVAS
    ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
        DROP COLUMN STATUS_ATIVO;

    -- RECRIA VIEW SEM A COLUNA STATUS_ATIVO
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
           CAP.DEVE_ABRIR_ORDEM_SERVICO
    FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
    WHERE CAP.DELETADO = FALSE;


-- DROPA A VIEW CHECKLIST_PERGUNTAS
    DROP VIEW CHECKLIST_PERGUNTAS;

-- DROPA COLUNA DE STATUS DAS ALTERNATIVAS
    ALTER TABLE CHECKLIST_PERGUNTAS_DATA
        DROP COLUMN STATUS_ATIVO;

    -- RECRIA VIEW SEM A COLUNA STATUS_ATIVO
    CREATE OR REPLACE VIEW CHECKLIST_PERGUNTAS AS
    SELECT CP.COD_CHECKLIST_MODELO,
           CP.COD_VERSAO_CHECKLIST_MODELO,
           CP.COD_UNIDADE,
           CP.ORDEM,
           CP.PERGUNTA,
           CP.SINGLE_CHOICE,
           CP.COD_IMAGEM,
           CP.CODIGO,
           CP.CODIGO_CONTEXTO
    FROM CHECKLIST_PERGUNTAS_DATA CP
    WHERE CP.DELETADO = FALSE;

    -- #####################################################################################################################
-- #####################################################################################################################
-- CORRIGE O VÍNCULO DAS PERGUNTAS MIGRADAS NAS ALTERNATIVAS
-- TODO REFATORAR PARA OTIMIZAR (JANELA)
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
    SET COD_PERGUNTA = (
        SELECT CPA.COD_PERGUNTA_NOVO
        FROM MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX CPA
        WHERE CPA.COD_PERGUNTA_ANTIGO = CAP.COD_PERGUNTA
          AND CPA.COD_MODELO_VERSAO = CAP.COD_VERSAO_CHECKLIST_MODELO)
    WHERE COD_PERGUNTA IN
          (SELECT CPA.COD_PERGUNTA_ANTIGO FROM MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX CPA)
      AND COD_VERSAO_CHECKLIST_MODELO IN
          (SELECT CPA.COD_MODELO_VERSAO FROM MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX CPA);
    -- #####################################################################################################################
-- #####################################################################################################################

end;
$$;