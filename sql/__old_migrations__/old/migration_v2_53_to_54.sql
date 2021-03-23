-- Essa migração deve ser executada quando o WS versão 54 for publicado.
BEGIN TRANSACTION;
-- ########################################################################################################
-- ########################################################################################################
-- ####################### CRIAÇÃO DA TABELA PARA SALVAR AS IMAGENS DO CADASTRO DO PNEU ###################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS PNEU_FOTO_CADASTRO (
  CODIGO BIGSERIAL NOT NULL,
  COD_PNEU TEXT NOT NULL,
  COD_UNIDADE_PNEU BIGINT NOT NULL,
  URL_FOTO TEXT NOT NULL UNIQUE,
  FOTO_SINCRONIZADA BOOLEAN DEFAULT FALSE NOT NULL,
  DATA_HORA_SINCRONIZACAO_FOTO TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  CONSTRAINT PK_PNEU_FOTO_CADASTRO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PNEU_FOTO_CADASTRO_PNEU FOREIGN KEY (COD_PNEU, COD_UNIDADE_PNEU) REFERENCES PNEU(CODIGO, COD_UNIDADE)
);
COMMENT ON TABLE PNEU_FOTO_CADASTRO
IS 'Tabela que contém as fotos dos pneus capturadas no momento do cadastro';
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ###################   ALTERAÇÃO DO INTERVALO PARA CONTER DATA DE INSERÇÃO    ###########################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE intervalo ADD COLUMN data_hora_sincronizacao TIMESTAMP WITH TIME ZONE DEFAULT NULL;
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION;