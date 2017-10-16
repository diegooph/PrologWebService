-- A Avilan terá seu próprio esquema no banco de dados do ProLog.
CREATE SCHEMA AVILAN;

CREATE TABLE IF NOT EXISTS AVILAN.TIPO_VEICULO (
  CODIGO VARCHAR(255) NOT NULL,
  DESCRICAO VARCHAR(255) NOT NULL,
  COD_PROLOG SMALLSERIAL NOT NULL,
  CONSTRAINT PK_TIPO_VEICULO PRIMARY KEY (CODIGO),
  CONSTRAINT UNIQUE_TIPO_VEICULO UNIQUE (CODIGO, COD_PROLOG)
);
COMMENT ON TABLE AVILAN.TIPO_VEICULO IS 'Essa tabela mapeia os tipos de veículo da Avilan para um código numérico
 que será utilizado pelo ProLog. Ela é necessária pois o código de um tipo de veículo no ProLog é um número,
 porém, no ERP da Avilan é uma String.Para cada tipo de veículo que a Avilan possua, será criado um código
 númerico equivalente.'


CREATE TABLE IF NOT EXISTS AVILAN.FILIAL (
  CODIGO SMALLINT NOT NULL,
  COD_UNIDADE_PROLOG BIGINT NOT NULL,
  CONSTRAINT PK_FILIAL PRIMARY KEY (CODIGO),
  CONSTRAINT FK_FILIAL_AVILAN_UNIDADE_PROLOG FOREIGN KEY (COD_UNIDADE_PROLOG) REFERENCES PUBLIC.UNIDADE(CODIGO),
  CONSTRAINT UNIQUE_FILIAL UNIQUE (CODIGO, COD_UNIDADE_PROLOG)
);
COMMENT ON TABLE AVILAN.FILIAL IS 'Representa uma filial no banco de dados da Avilan. Uma filial pode ter várias
 unidades (AVILAN.UNIDADE) associadas a ela. Filial é o equivalente a uma unidade no ProLog. Por exemplo: Santa
 Maria é uma filial no ERP da Avilan mas uma  unidade no ProLog.';


CREATE TABLE IF NOT EXISTS AVILAN.UNIDADE (
  CODIGO SMALLINT NOT NULL,
  COD_FILIAL SMALLINT NOT NULL,
  CONSTRAINT PK_UNIDADE PRIMARY KEY (CODIGO, COD_FILIAL),
  CONSTRAINT FK_UNIDADE_FILIAL FOREIGN KEY (COD_FILIAL) REFERENCES AVILAN.FILIAL(CODIGO),
  CONSTRAINT UNIQUE_UNIDADE UNIQUE (CODIGO, COD_FILIAL)
);