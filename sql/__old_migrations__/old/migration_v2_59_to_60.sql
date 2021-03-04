BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ######################## CRIA COLUNA BIGSERIAL EM COLABORADOR ##########################################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE COLABORADOR ADD COLUMN CODIGO BIGSERIAL;
ALTER TABLE COLABORADOR ALTER COLUMN CODIGO SET NOT NULL;
ALTER TABLE COLABORADOR ADD CONSTRAINT UNIQUE_CODIGO_COLABORADOR UNIQUE (CODIGO);
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ######################## DELETA OS TOKENS DE QUEM PODE CADASTRAR UM PNEU ###############################
-- ########################################################################################################
-- ########################################################################################################
-- Com isso garantimos que eles irão logar novamente no ProLog e terão a AmazonCredentials disponível para
-- uso ao sincronizar fotos tiradas no cadastro do pneu.
DELETE FROM TOKEN_AUTENTICACAO TA WHERE TA.CPF_COLABORADOR IN
                                        (SELECT C.CPF FROM COLABORADOR
                                        C JOIN CARGO_FUNCAO_PROLOG_V11 CF ON C.COD_FUNCAO = CF.COD_FUNCAO_COLABORADOR
                                        WHERE CF.COD_FUNCAO_PROLOG = 15);
END TRANSACTION ;