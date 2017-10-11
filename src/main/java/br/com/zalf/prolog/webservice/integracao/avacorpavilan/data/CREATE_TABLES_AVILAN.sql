-- A Avilan terá seu próprio esquema no banco de dados ProLog.
CREATE SCHEMA AVILAN;

-- Essa tabela mapeia os tipos de veículo da Avilan para um código numérico que será utilizado pelo ProLog.
-- Ela é necessária pois o código de um tipo de veículo no ProLog é um número, porém, no ERP da Avilan é uma String.
-- Para cada tipo de veículo que a Avilan possua, será criado um código númerico equivalente.
CREATE TABLE IF NOT EXISTS AVILAN.TIPO_VEICULO (
  CODIGO VARCHAR(255) NOT NULL,
  DESCRICAO VARCHAR(255) NOT NULL,
  COD_PROLOG SMALLSERIAL NOT NULL,
  CONSTRAINT PK_TIPO_VEICULO PRIMARY KEY (CODIGO),
  CONSTRAINT UNIQUE_TIPO_VEICULO UNIQUE (CODIGO, COD_PROLOG)
);