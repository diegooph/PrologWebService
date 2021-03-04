BEGIN TRANSACTION ;

-- ########################################################################################################
-- ########################################################################################################
-- ######################### CRIA TABELA PARA SALVAR OS CONTATOS DE CLIENTES  #############################
-- ########################################################################################################
-- ########################################################################################################
CREATE SCHEMA COMERCIAL;
CREATE TABLE IF NOT EXISTS COMERCIAL.MENSAGEM_CONTATO (
  CODIGO BIGSERIAL NOT NULL,
  NOME TEXT NOT NULL,
  EMAIL TEXT NOT NULL,
  TELEFONE TEXT,
  EMPRESA TEXT,
  MENSAGEM TEXT NOT NULL,
  DATA_HORA_CRIACAO TIMESTAMP DEFAULT NOW(),
  CONSTRAINT PK_MENSAGEM_CONTATO PRIMARY KEY (CODIGO)
);
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ##############  ATUALIZA COMPONENTE DA DASSHBOARD PARA MOSTRAR APENAS O VÉICULOS ATIVOS  ###############
-- ########################################################################################################
-- ########################################################################################################
UPDATE DASHBOARD_COMPONENTE SET
  SUBTITULO = NULL,
  DESCRICAO = 'Mostra a quantidade de veículos ativos',
  URL_ENDPOINT_DADOS = '/dashboards/veiculos/quantidade-veiculos-ativos'
WHERE CODIGO = 2 AND COD_PILAR_PROLOG_COMPONENTE = 1 AND COD_TIPO_COMPONENTE = 2;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############################# ALTERA ATRIBUTOS NA TABELA COLABORADOR ###################################
-- ########################################################################################################
-- ########################################################################################################

-- ################################### REMOVE A COLUNA SETOR ##############################################
ALTER TABLE colaborador DROP COLUMN setor;

-- ############################ REMOVE O VALOR DEFAULT DA COLUNA COD_EMPRESA ##############################
ALTER TABLE colaborador ALTER COLUMN cod_empresa DROP DEFAULT;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ##################### ADICIONA COLUNA COD_UNIDADE_CADASTRO NAS TABELAS #################################
-- ###################################### PNEU, VEICULO E COLABORADOR #####################################
-- ########################################################################################################
-- ################### ESSA COLUNA GUARDA A INFORMAÇÃO DA UNIDADE QUE FOI REALIZADO O #####################
-- ############################### CADASTRO INICIAL DAS INFORMAÇÕES #######################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################## ADICIONA COD_UNIDADE_CADSTRO NA TABELA PNEU #################################
-- ########################################################################################################
ALTER TABLE pneu ADD COLUMN cod_unidade_cadastro INTEGER;
UPDATE pneu SET cod_unidade_cadastro = cod_unidade;
ALTER TABLE pneu ALTER COLUMN cod_unidade_cadastro SET NOT NULL;
ALTER TABLE pneu ADD CONSTRAINT FK_PNEU_UNIDADE_CADASTRO FOREIGN KEY (cod_unidade_cadastro) REFERENCES unidade(CODIGO);

-- ########################################################################################################
-- ########################## ADICIONA COD_UNIDADE_CADSTRO NA TABELA COLABORADOR ##########################
-- ########################################################################################################
ALTER TABLE colaborador ADD COLUMN cod_unidade_cadastro INTEGER;
UPDATE colaborador SET cod_unidade_cadastro = cod_unidade;
ALTER TABLE colaborador ALTER COLUMN cod_unidade_cadastro SET NOT NULL;
ALTER TABLE colaborador ADD CONSTRAINT FK_COLABORADOR_UNIDADE_CADASTRO FOREIGN KEY (cod_unidade_cadastro) REFERENCES unidade(CODIGO);

-- ########################################################################################################
-- ############################ ADICIONA COD_UNIDADE_CADSTRO NA TABELA VEICULO ############################
-- ########################################################################################################
ALTER TABLE veiculo ADD COLUMN cod_unidade_cadastro INTEGER;
UPDATE veiculo SET cod_unidade_cadastro = cod_unidade;
ALTER TABLE veiculo ALTER COLUMN cod_unidade_cadastro SET NOT NULL;
ALTER TABLE veiculo ADD CONSTRAINT FK_VEICULO_UNIDADE_CADASTRO FOREIGN KEY (cod_unidade_cadastro) REFERENCES unidade(CODIGO);

-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;