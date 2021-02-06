BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
-- Apenas para histórico, já foi executado em 02/10/19.
-- create index idx_checklist_placa_veiculo
--     on checklist_data (placa_veiculo);
--
-- create index idx_afericao_placa_veiculo
--     on afericao_data (placa_veiculo);
--
-- create index idx_movimentacao_origem_placa_veiculo
--     on movimentacao_origem (placa);
--
-- create index idx_movimentacao_destino_placa_veiculo
--     on movimentacao_destino (placa);
--
-- create index idx_afericao_valores_cod_pneu
--     on afericao_valores_data (cod_pneu);
--
-- create index idx_afericao_manutencao_cod_pneu
--     on afericao_manutencao_data (cod_pneu);
--
-- create index idx_movimentacao_cod_pneu
--     on movimentacao (cod_pneu);
--
-- create index idx_pneu_servico_realizado_cod_pneu
--     on pneu_servico_realizado (cod_pneu);
--
-- create index idx_pneu_servico_cadastro_cod_pneu
--     on pneu_servico_cadastro (cod_pneu);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Apenas para histórico, já foi executado em 02/10/19.
-- alter table movimentacao drop constraint fk_movimentacao_pneu;
-- alter table movimentacao add constraint fk_movimentacao_pneu foreign key(cod_pneu) references pneu_data(codigo);
--
-- alter table afericao_valores_data drop constraint fk_afericao_valores_pneu;
-- alter table afericao_valores_data add constraint fk_afericao_valores_pneu foreign key(cod_pneu) references pneu_data(codigo);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Apenas para histórico, já foi executado em 02/10/19.
-- alter table pneu_posicao_nomenclatura_empresa drop constraint check_colaborador_not_null;
-- comment on column pneu_posicao_nomenclatura_empresa.cod_colaborador_cadastro is 'Se a coluna possuir valor null, a inserção
-- foi realizada através da migração da tabela antiga ou import';
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2342
-- Criamos o diagrama
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM) VALUES (16, 'SUPERARTICULADO', 'WWW.GOOGLE.COM/SUPERARTICULADO');

-- Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (16, 'D', 1, 2, TRUE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (16, 'T', 2, 4, FALSE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (16, 'T', 3, 4, FALSE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (16, 'T', 4, 2, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 111);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 121);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 211);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 212);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 221);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 222);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 311);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 312);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 321);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 322);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 411);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 421);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 900);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 901);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 902);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 903);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 904);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 905);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 906);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 907);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (16, 908);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2328
-- Permite estepes para as motos
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 900);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 901);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 902);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 903);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 904);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 905);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 906);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 907);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (12, 908);

-- Permite estepes para os triciclos
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 900);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 901);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 902);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 903);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 904);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 905);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 906);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 907);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (13, 908);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2344
DROP FUNCTION FUNC_PNEU_GET_PNEU_BY_PLACA(VARCHAR);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_PLACA(F_PLACA VARCHAR(7))
  RETURNS TABLE (
    NOME_MARCA_PNEU              VARCHAR(255),
    COD_MARCA_PNEU               BIGINT,
    CODIGO                       BIGINT,
    CODIGO_CLIENTE               VARCHAR(255),
    COD_UNIDADE_ALOCADO          BIGINT,
    COD_REGIONAL_ALOCADO         BIGINT,
    PRESSAO_ATUAL                REAL,
    VIDA_ATUAL                   INTEGER,
    VIDA_TOTAL                   INTEGER,
    PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
    NOME_MODELO_PNEU             VARCHAR(255),
    COD_MODELO_PNEU              BIGINT,
    QT_SULCOS_MODELO_PNEU        SMALLINT,
    ALTURA_SULCOS_MODELO_PNEU    REAL,
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    PRESSAO_RECOMENDADA          REAL,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    STATUS                       VARCHAR(255),
    DOT                          VARCHAR(20),
    VALOR                        REAL,
    COD_MODELO_BANDA             BIGINT,
    NOME_MODELO_BANDA            VARCHAR(255),
    QT_SULCOS_MODELO_BANDA       SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA   REAL,
    COD_MARCA_BANDA              BIGINT,
    NOME_MARCA_BANDA             VARCHAR(255),
    VALOR_BANDA                  REAL,
    POSICAO_PNEU                 INTEGER,
    POSICAO_APLICADO_CLIENTE     VARCHAR(255),
    COD_VEICULO_APLICADO         BIGINT,
    PLACA_APLICADO               VARCHAR(7)
  )
LANGUAGE SQL
AS $$
SELECT
  MP.NOME                                  AS NOME_MARCA_PNEU,
  MP.CODIGO                                AS COD_MARCA_PNEU,
  P.CODIGO,
  P.CODIGO_CLIENTE,
  U.CODIGO                                 AS COD_UNIDADE_ALOCADO,
  R.CODIGO                                 AS COD_REGIONAL_ALOCADO,
  P.PRESSAO_ATUAL,
  P.VIDA_ATUAL,
  P.VIDA_TOTAL,
  P.PNEU_NOVO_NUNCA_RODADO,
  MOP.NOME                                 AS NOME_MODELO_PNEU,
  MOP.CODIGO                               AS COD_MODELO_PNEU,
  MOP.QT_SULCOS                            AS QT_SULCOS_MODELO_PNEU,
  MOP.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_PNEU,
  PD.ALTURA,
  PD.LARGURA,
  PD.ARO,
  PD.CODIGO                                AS COD_DIMENSAO,
  P.PRESSAO_RECOMENDADA,
  P.ALTURA_SULCO_CENTRAL_INTERNO,
  P.ALTURA_SULCO_CENTRAL_EXTERNO,
  P.ALTURA_SULCO_INTERNO,
  P.ALTURA_SULCO_EXTERNO,
  P.STATUS,
  P.DOT,
  P.VALOR,
  MOB.CODIGO                               AS COD_MODELO_BANDA,
  MOB.NOME                                 AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
  MAB.CODIGO                               AS COD_MARCA_BANDA,
  MAB.NOME                                 AS NOME_MARCA_BANDA,
  PVV.VALOR                                AS VALOR_BANDA,
  PO.POSICAO_PROLOG                        AS POSICAO_PNEU,
  COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO_CLIENTE,
  VEI.CODIGO                               AS COD_VEICULO_APLICADO,
  VEI.PLACA                                AS PLACA_APLICADO
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
  LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
  LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = P.COD_EMPRESA
  LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
  LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG
  LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
  LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
  LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON
                                                     PPNE.COD_EMPRESA = P.COD_EMPRESA AND
                                                     PPNE.COD_DIAGRAMA = VD.CODIGO AND
                                                     PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE VP.PLACA = F_PLACA
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;