BEGIN TRANSACTION;

    -- 1) Criar tabela PUBLIC.PNEU_POSICAO contendo as posições utilizadas
    -- no ProLog.
    CREATE TABLE IF NOT EXISTS PUBLIC.PNEU_POSICAO (
      POSICAO_PNEU SMALLINT NOT NULL,
      DESCRICAO_POSICAO VARCHAR(255),
      CONSTRAINT PK_PNEU_POSICAO PRIMARY KEY (POSICAO_PNEU)
    );

    -- 2) Inserir dados na tabela PUBLIC.PNEU_POSICAO.
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (111);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (211);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (212);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (222);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (221);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (311);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (312);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (322);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (321);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (121);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (411);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (412);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (421);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (422);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (900);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (901);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (902);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (903);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (904);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (905);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (906);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (907);
    INSERT INTO PUBLIC.PNEU_POSICAO (POSICAO_PNEU) VALUES (908);

    -- 3) Adicionar constraint na tabela PUBLIC.PNEU_ORDEM para a coluna POSICAO_PROLOG
    -- ser FK da coluna POSICAO_PNEU na tabela PUBLIC.PNEU_POSICAO.
    ALTER TABLE PUBLIC.PNEU_ORDEM ADD CONSTRAINT FK_PNEU_ORDEM_PNEU_POSICAO
    FOREIGN KEY(POSICAO_PROLOG) REFERENCES PUBLIC.PNEU_POSICAO(POSICAO_PNEU);

    -- 4) Remover constraint FK_PNEU_ORDEM_NOMENCLATURA_UNIDADE_PNEU_ORDEM da
    -- tabela PUBLIC.PNEU_ORDEM_NOMENCLATURA_UNIDADE
    ALTER TABLE PUBLIC.PNEU_ORDEM_NOMENCLATURA_UNIDADE DROP
    CONSTRAINT FK_PNEU_ORDEM_NOMENCLATURA_UNIDADE_PNEU_ORDEM;

    -- 5) Adicionar constraint na tabela PUBLIC.PNEU_ORDEM_NOMENCLATURA_UNIDADE para
    -- a coluna POSICAO_PROLOG ser FK da coluna POSICAO_PNEU na tabela
    -- PUBLIC.PNEU_POSICAO.
    ALTER TABLE PUBLIC.PNEU_ORDEM_NOMENCLATURA_UNIDADE ADD CONSTRAINT
    FK_PNEU_ORDEM_NOMENCLATURA_UNIDADE_PNEU_POSICAO FOREIGN KEY (POSICAO_PROLOG)
    REFERENCES PUBLIC.PNEU_POSICAO(POSICAO_PNEU);
END TRANSACTION;

BEGIN TRANSACTION;
    -- 6) Dar um drop na tabela AVILAN.TIPO_VEICULO.
    DROP TABLE AVILAN.TIPO_VEICULO;

    -- 7) Criar tabela AVILAN.VEICULO_TIPO.
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
    númerico equivalente.';

    -- 8) Adicionar dados na tabela AVILAN.VEICULO_TIPO.
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('A2', 'A');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('A', 'AUTOMÓVEL');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CT', 'CAMINHÃO TOCO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CTU', 'CAMINHÃO TOCO (ROMEU)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CTR', 'CAMINHÃO TRUCK');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CTR2E', 'CAMINHÃO TRUCK 2 EIXOS DIANTEIROS, TRAÇADO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CTR2D', 'CAMINHÃO TRUCK DE 2 EIXOS DIANTEIROS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CTRU', 'CAMINHÃO TRUCK (ROMEU)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CAMI', 'CAMINHONETA');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CAMIS', 'CAMINHONETE EIXO TRASEIRO COM RODADO SIMPLES');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C1', 'CARRETA 1 EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C1J', 'CARRETA 1 EIXO (JULIETA )');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2', 'CARRETA 2 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2J', 'CARRETA 2 EIXOS (JULIETA)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2RCB', 'CARRETA 2 EIXOS REBOCADA (BTREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2RCR', 'CARRETA 2 EIXOS REBOCADA (RODOTREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2RRB', 'CARRETA 2 EIXOS REBOCADORA (BTREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C2RRR', 'CARRETA 2 EIXOS REBOCADORA (RODOTREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C3', 'CARRETA 3 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C3RCB', 'CARRETA 3 EIXOS REBOCADA (BITREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C3RRB', 'CARRETA 3 EIXOS REBOCADORA (BITREM)');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C3S', 'CARRETA 3 EIXOS SINGLE');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C4', 'CARRETA 4 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C6', 'CARRETA 6 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('C8', 'CARRETA 8 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CVT', 'CAVALO TOCO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CVTR', 'CAVALO TRUCK');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('CVTR2', 'CAVALO TRUCK DE 2 EIXOS DIANTEIROS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('D1', 'DOLEM 1 EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('D2', 'DOLEM 2 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('EMP', 'EMPILHADEIRA');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('EMPPP', 'EMPILHADEIRA');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('EMPR', 'EMPILHADEIRA EIXO DIANTEIRO COM RODADO DUPLO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('ME', 'MÁQUINAS/EQUIPAMENTOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('MIO', 'MICRO-ÔNIBUS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('MIOTS', 'MICRO-ÔNIBUS EIXO TRASEIRO COM RODADO SIMPLES');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('M', 'MOTO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('O2ED', 'ÔNIBUS DE 2 EIXOS DIANTEIROS E 2 RODAS NO 4º EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('O2R2E', 'ÔNIBUS DE 2 RODAS NO 2º EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('O2R3E', 'ÔNIBUS DE 2 RODAS NO 3º EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('OT', 'ÔNIBUS TOCO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('OTR', 'ÔNIBUS TRUCK');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('PAL', 'PALETEIRAS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('R1', 'REBOQUE 1 EIXO');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('R2', 'REBOQUE 2 EIXOS');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('TES', 'TES');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('112', 'TESTE');
    INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES ('TRA', 'TRATOR');
END TRANSACTION;

-- 9) Criar tabela AVILAN.FILIAL.
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

-- 10) Criar tabela AVILAN.UNIDADE.
CREATE TABLE IF NOT EXISTS AVILAN.UNIDADE (
  CODIGO SMALLINT NOT NULL,
  COD_FILIAL SMALLINT NOT NULL,
  CONSTRAINT PK_UNIDADE PRIMARY KEY (CODIGO, COD_FILIAL),
  CONSTRAINT FK_UNIDADE_FILIAL FOREIGN KEY (COD_FILIAL) REFERENCES AVILAN.FILIAL(CODIGO),
  CONSTRAINT UNIQUE_UNIDADE UNIQUE (CODIGO, COD_FILIAL)
);
COMMENT ON TABLE AVILAN.UNIDADE IS 'Representa uma unidade no banco de dados da Avilan. Uma unidade pertence a uma filial.';

BEGIN TRANSACTION;
    -- 11) Inserir dados de todas as filiais na tabela AVILAN.FILIAL.
    -- Sapucaia do Sul.
    INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES(11, 2);
    -- Santa Cruz do Sul.
    INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES(8, 4);
    -- Santa Maria.
    INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES(9, 3);

    -- 12) Inserir dados na tabela AVILAN.UNIDADE.
    -- Sapucaia do Sul.
    INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES(1, 11);
    -- Santa Cruz do Sul.
    INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES(1, 8);
    -- Santa Maria.
    INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES(1, 9);
END TRANSACTION;

-- 13) Criar tabela AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA.
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

BEGIN TRANSACTION;
    -- 14) Inserir dados na tabela AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA.
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CVT',   1);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CT',    1);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CVTR',  2);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CTR',   2);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C3RRB', 3);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C3S',   3);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C3',    3);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CTR2E', 4);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('CTR2D', 4);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C2RRR', 5);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C2RRB', 5);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C2RCB', 5);
    INSERT INTO AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA (COD_VEICULO_TIPO, COD_VEICULO_DIAGRAMA_PROLOG) VALUES('C2RCR', 5);
END TRANSACTION;

-- 15) Criar tabela AVILAN.PNEU_POSICAO.
CREATE TABLE IF NOT EXISTS AVILAN.PNEU_POSICAO (
  POSICAO_PNEU VARCHAR(10) NOT NULL,
  DESCRICAO_POSICAO VARCHAR(255),
  CONSTRAINT PK_PNEU_POSICAO PRIMARY KEY (POSICAO_PNEU)
);
COMMENT ON TABLE AVILAN.PNEU_POSICAO IS 'Salva as posições que a Avilan usa para os pneus e a descrição de cada posição.';

BEGIN TRANSACTION;
    -- 16) Inserir dados na tabela AVILAN.PNEU_POSICAO.
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2_DD', 'SEGUNDO DIANTEIRO DIREITO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2_DE', 'SEGUNDO DIANTEIRO ESQUERDO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2°DE', 'SEGUNDO DIREITO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2°DI', 'SEGUNDO DIREITO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2°EE', 'SEGUNDO ESQUERDO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('2°EI', 'SEGUNDO ESQUERDO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('3_DE', 'TERCEIRO DIREITO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('3_DI', 'TERCEIRO DIREITO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('3_EE', 'TERCEIRO ESQUERDO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('3_EI', 'TERCEIRO ESQUERDO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('4_DE', 'QUARTO DIREITO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('4_DI', 'QUARTO DIREITO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('4_EE', 'QUARTO ESQUERDO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('4_EI', 'QUARTO ESQUERDO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('A_DE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('A_DI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('A_EE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('A_EI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('B_DE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('B_DI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('B_EE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('B_EI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('C_DE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('C_DI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('C_EE', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('C_EI', NULL);
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('DD', 'DIANTEIRO DIREITO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('DE', 'DIANTEIRO ESQUERDO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('EST_1', 'PRIMEIRO ESTEPE');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('EST_2', 'SEGUNDO ESTEPE');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('EST_3', 'TERCEIRO ESTEPE');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('EST_4', 'QUARTO ESTEPE');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('EST_5', 'QUINTO ESTEPE');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('TDE', 'TRAÇÃO DIREITO ENTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('TDI', 'TRAÇÃO DIREITO INTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('TEE', 'TRAÇÃO ESQUERDO EXTERNO');
    INSERT INTO AVILAN.PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO) VALUES ('TEI', 'TRAÇÃO ESQUERDO INTERNO');
END TRANSACTION;

-- 17) Criar tabela AVILAN.PNEU_POSICAO_AVILAN_PROLOG.
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

BEGIN TRANSACTION;
    -- 18) Inserir dados na tabela AVILAN.PNEU_POSICAO_AVILAN_PROLOG.
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DE', 111, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DD', 121, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('2_DE', 211, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('2_DD', 221, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEE', 311, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEI', 312, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDE', 321, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDI', 322, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('4_EE', 411, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('4_EI', 412, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('4_DE', 421, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('4_DI', 422, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_1', 900, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_2', 901, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_3', 902, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_4', 903, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_5', 904, 'CTR2E');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_DI', 322, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_EE', 311, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_DE', 321, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_EI', 312, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDI', 222, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDE', 221, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEI', 212, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEE', 211, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DD', 121, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DE', 111, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('B_EI', 312, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_1', 900, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('B_EE', 311, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('A_EE', 211, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('A_EI', 212, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('A_DE', 221, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('A_DI', 222, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('C_EI', 412, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('B_DI', 322, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('C_EE', 411, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('B_DE', 321, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEE', 211, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDE', 221, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_EE', 311, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_EI', 312, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_DE', 321, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('3_DI', 322, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_1', 900, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEI', 212, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DE', 111, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DD', 121, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDI', 222, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DE', 111, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('DD', 121, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEE', 211, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TEI', 212, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDI', 222, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('TDE', 221, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_1', 900, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_2', 901, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_3', 902, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_4', 903, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_5', 904, 'CT');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_2', 901, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_3', 902, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_4', 903, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_5', 904, 'CTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_2', 901, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_3', 902, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_4', 903, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_5', 904, 'C3');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_2', 901, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_3', 902, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_4', 903, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('EST_5', 904, 'CVTR');
    INSERT INTO avilan.pneu_posicao_avilan_prolog (posicao_pneu_avilan, posicao_pneu_prolog, cod_veiculo_tipo) VALUES ('C_DI', 422, 'C3');
END TRANSACTION;
