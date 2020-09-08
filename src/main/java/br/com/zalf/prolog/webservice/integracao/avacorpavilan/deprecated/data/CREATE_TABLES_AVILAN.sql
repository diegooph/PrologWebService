--=====================================================================================================================
--=====================================================================================================================

-- A Avilan terá seu próprio esquema no banco de dados do ProLog.
CREATE SCHEMA AVILAN;

--=====================================================================================================================
--=====================================================================================================================

CREATE TABLE IF NOT EXISTS AVILAN.VEICULO_TIPO (
  CODIGO VARCHAR(5) NOT NULL,
  DESCRICAO VARCHAR(50) NOT NULL,
  COD_PROLOG SMALLSERIAL NOT NULL,
  CONSTRAINT PK_VEICULO_TIPO PRIMARY KEY (CODIGO),
  CONSTRAINT UNIQUE_VEICULO_TIPO UNIQUE (CODIGO, COD_PROLOG)
);
COMMENT ON TABLE AVILAN.VEICULO_TIPO IS 'Essa tabela mapeia os tipos de veículo da Avilan para um código numérico
 que será utilizado pelo ProLog. Ela é necessária pois o código de um tipo de veículo no ProLog é um número,
 porém, no ERP da Avilan é uma String.Para cada tipo de veículo que a Avilan possua, será criado um código
 númerico equivalente.'

--=====================================================================================================================
--=====================================================================================================================

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

--=====================================================================================================================
--=====================================================================================================================

CREATE TABLE IF NOT EXISTS AVILAN.UNIDADE (
  CODIGO SMALLINT NOT NULL,
  COD_FILIAL SMALLINT NOT NULL,
  CONSTRAINT PK_UNIDADE PRIMARY KEY (CODIGO, COD_FILIAL),
  CONSTRAINT FK_UNIDADE_FILIAL FOREIGN KEY (COD_FILIAL) REFERENCES AVILAN.FILIAL(CODIGO),
  CONSTRAINT UNIQUE_UNIDADE UNIQUE (CODIGO, COD_FILIAL)
);
COMMENT ON TABLE AVILAN.UNIDADE IS 'Representa uma unidade no banco de dados da Avilan. Uma unidade pertence a uma filial.';

--=====================================================================================================================
--=====================================================================================================================

CREATE TABLE IF NOT EXISTS AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (
  COD_VEICULO_TIPO VARCHAR(5) NOT NULL,
  COD_VEICULO_DIAGRAMA_PROLOG SMALLINT NOT NULL,
  CONSTRAINT PK_VEICULO_TIPO_VEICULO_DIAGRAMA PRIMARY KEY (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG),
  CONSTRAINT FK_VEICULO_TIPO_VEICULO_DIAGRAMA_VEICULO_TIPO FOREIGN KEY (COD_VEICULO_TIPO)
  REFERENCES AVILAN.VEICULO_TIPO(CODIGO),
  CONSTRAINT FK_VEICULO_TIPO_VEICULO_DIAGRAMA_VEICULO_DIAGRAMA FOREIGN KEY (COD_VEICULO_DIAGRAMA_PROLOG)
  REFERENCES PUBLIC.VEICULO_DIAGRAMA(CODIGO)
);
COMMENT ON TABLE AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA
IS 'Associa um tipo de veículo da Avilan a um diagrama de veículo existente no ProLog.';

--=====================================================================================================================
--=====================================================================================================================

CREATE TABLE IF NOT EXISTS AVILAN.PNEU_POSICAO (
  POSICAO_PNEU VARCHAR(10) NOT NULL,
  DESCRICAO_POSICAO VARCHAR(255),
  CONSTRAINT PK_PNEU_POSICAO PRIMARY KEY (POSICAO_PNEU)
);
COMMENT ON TABLE AVILAN.PNEU_POSICAO IS 'Salva as posições que a Avilan usa para os pneus e a descrição de cada posição.';

--=====================================================================================================================
--=====================================================================================================================

-- Criação da tabela para armazenar o mapeamento das posições
CREATE TABLE IF NOT EXISTS AVILAN.PNEU_POSICAO_AVILAN_PROLOG (
  POSICAO_PNEU_AVILAN VARCHAR(10) NOT NULL,
  POSICAO_PNEU_PROLOG SMALLINT NOT NULL,
  COD_VEICULO_TIPO VARCHAR(5) NOT NULL,
  CONSTRAINT PK_PNEU_POSICAO_AVILAN_PROLOG PRIMARY KEY (POSICAO_PNEU_AVILAN, POSICAO_PNEU_PROLOG, COD_VEICULO_TIPO),
  CONSTRAINT FK_PNEU_POSICAO_AVILAN_PROLOG_PNEU_POSICAO_PROLOG FOREIGN KEY (POSICAO_PNEU_PROLOG)
  REFERENCES PUBLIC.PNEU_POSICAO(POSICAO_PNEU),
  CONSTRAINT FK_PNEU_POSICAO_AVILAN_PROLOG_PNEU_POSICAO_AVILAN FOREIGN KEY (POSICAO_PNEU_AVILAN)
  REFERENCES AVILAN.PNEU_POSICAO(POSICAO_PNEU),
  CONSTRAINT FK_PNEU_POSICAO_AVILAN_PROLOG_VEICULO_TIPO_AVILAN FOREIGN KEY (COD_VEICULO_TIPO)
  REFERENCES AVILAN.VEICULO_TIPO(CODIGO)
);
COMMENT ON TABLE AVILAN.PNEU_POSICAO_AVILAN_PROLOG IS
'Mapeia uma posição de pneu na Avilan para uma do ProLog levando em conta o tipo do veículo.';

--=====================================================================================================================
--=====================================================================================================================