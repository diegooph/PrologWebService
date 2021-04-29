BEGIN TRANSACTION ;

-- ########################################################################################################
-- ########################################################################################################
-- ##################################  REMOVE TABELA UNIDADE_FUNCAO  ######################################
-- ########################################################################################################
-- ########################################################################################################
-- Serão removidas também tabelas não mais utilizadas que aindam tem FK em UNIDADE_FUNCAO e algumas constraints
-- de umas tabelas atuais.
DROP TABLE cargo_funcao_prolog;
ALTER TABLE cargo_funcao_prolog_v11 DROP CONSTRAINT cargo_funcao_prolog_v11_cod_unidade_fkey;
ALTER TABLE intervalo_tipo_cargo DROP CONSTRAINT fk_intervalo_tipo_cargo;
ALTER TABLE quiz_modelo_funcao DROP CONSTRAINT fk_quiz_modelo_funcao_unidade_funcao;
ALTER TABLE checklist_modelo_funcao DROP CONSTRAINT fk_checklist_modelo_unidade_funcao;
DROP TABLE UNIDADE_FUNCAO;
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;