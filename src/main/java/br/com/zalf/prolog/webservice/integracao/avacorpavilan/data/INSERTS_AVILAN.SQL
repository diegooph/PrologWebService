--=====================================================================================================================
--=====================================================================================================================

-- FILIAIS
-- Sapucaia do Sul.
INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES(11, 2);
-- Santa Cruz do Sul.
INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES(8, 4);

--=====================================================================================================================
--=====================================================================================================================

-- UNIDADES
-- Sapucaia do Sul.
INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES(1, 11);
-- Santa Cruz do Sul.
INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES(1, 8);

--=====================================================================================================================
--=====================================================================================================================

-- Mapper de tipo de veículo para o seu diagrama no ProLog.
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

--=====================================================================================================================
--=====================================================================================================================
