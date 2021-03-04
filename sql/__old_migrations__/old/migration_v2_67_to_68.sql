BEGIN TRANSACTION ;


-- Cria função com nome menor para substituiur a func_get_time_zone_unidade
CREATE OR REPLACE FUNCTION tz_unidade(f_cod_unidade bigint)
  RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  tz text;
BEGIN
  SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE INTO tz;
  RETURN tz;
END;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ########## Atualzia nome funcionalidade de controle de intervalos para controle de jornada  ############
-- ########################################################################################################
-- ########################################################################################################
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Criar tipo de marcação' WHERE codigo = 340 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Editar Marcações' WHERE codigo = 338 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Inativar tipo de marcação' WHERE codigo = 341 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Invalidar marcações' WHERE codigo = 339 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Realizar marcações' WHERE codigo = 336 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Visualizar relatórios' WHERE codigo = 342 AND cod_pilar = 3;
UPDATE public.funcao_prolog_v11 SET funcao = 'Controle de Jornada - Visualizar todas as marcações' WHERE codigo = 337 AND cod_pilar = 3;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #################### FUNCTION PARA BUSCAR LISTAGEM DE INTERVALOS DO COLABORADOR  #######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_intervalos_get_marcacoes_colaborador(
  f_cod_unidade bigint, f_cpf_colaborador bigint, f_cod_tipo_intervalo bigint, f_limit bigint, f_offset bigint)
  RETURNS TABLE(COD_UNIDADE BIGINT, COD_TIPO_INTERVALO BIGINT, NOME_TIPO_INTERVALO TEXT, CPF_COLABORADOR BIGINT,
                DATA_HORA_INICIO TIMESTAMP WITHOUT TIME ZONE, DATA_HORA_FIM TIMESTAMP WITHOUT TIME ZONE,
                FONTE_DATA_HORA_INICIO TEXT, FONTE_DATA_HORA_FIM TEXT, JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
                JUSTIFICATIVA_ESTOURO TEXT, LATITUDE_MARCACAO_INICIO TEXT, LATITUDE_MARCACAO_FIM TEXT,
                LONGITUDE_MARCACAO_INICIO TEXT, LONGITUDE_MARCACAO_FIM TEXT)
LANGUAGE SQL
AS $$
SELECT
  I.COD_UNIDADE AS COD_UNIDADE,
  I.COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO,
  IT.NOME AS NOME_TIPO_INTERVALO,
  I.CPF_COLABORADOR AS CPF_COLABORADOR,
  I.DATA_HORA_INICIO AT TIME ZONE tz_unidade(I.COD_UNIDADE) AS DATA_HORA_INICIO,
  I.DATA_HORA_FIM AT TIME ZONE tz_unidade(I.COD_UNIDADE)  AS DATA_HORA_FIM,
  I.FONTE_DATA_HORA_INICIO AS FONTE_DATA_HORA_INICIO,
  I.FONTE_DATA_HORA_FIM AS FONTE_DATA_HORA_FIM,
  I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
  I.JUSTIFICATIVA_ESTOURO AS JUSTIFICATIVA_ESTOURO,
  I.LATITUDE_MARCACAO_INICIO AS LATITUDE_MARCACAO_INICIO,
  I.LATITUDE_MARCACAO_FIM AS LATITUDE_MARCACAO_FIM,
  I.LONGITUDE_MARCACAO_INICIO AS LONGITUDE_MARCACAO_INICIO,
  I.LONGITUDE_MARCACAO_FIM AS LONGITUDE_MARCACAO_FIM
FROM FUNC_INTERVALOS_AGRUPADOS(f_cod_unidade, f_cpf_colaborador, f_cod_tipo_intervalo) AS I
  JOIN INTERVALO_TIPO AS IT
    ON I.COD_TIPO_INTERVALO = IT.CODIGO
ORDER BY COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM) DESC
LIMIT f_limit OFFSET f_offset;
$$;
-- ########################################################################################################
-- ########################################################################################################












-- Migration dos tipos de serviços











-- ########################################################################################################
-- ########################################################################################################
-- ######################### ADICAO DE ATRIBUTOS NA MOVIMENTACAO PARA ANALISE  ############################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS PNEU_TIPO_SERVICO (
  CODIGO BIGSERIAL NOT NULL,
  COD_EMPRESA BIGINT,
  NOME VARCHAR(255) NOT NULL,
  INCREMENTA_VIDA BOOLEAN NOT NULL,
  STATUS_ATIVO BOOLEAN DEFAULT TRUE NOT NULL,
  EDITAVEL BOOLEAN DEFAULT TRUE NOT NULL,
  UTILIZADO_CADASTRO_PNEU BOOLEAN DEFAULT FALSE NOT NULL,
  COD_COLABORADOR_CRIACAO BIGINT,
  DATA_HORA_CRIACAO TIMESTAMP,
  COD_COLABORADOR_EDICAO BIGINT,
  DATA_HORA_EDICAO TIMESTAMP,
  CONSTRAINT PK_PNEU_TIPO_SERVICO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PNEU_TIPO_SERVICO_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO),
  CONSTRAINT FK_PNEU_TIPO_SERVICO_COLABORADOR_CRIACAO FOREIGN KEY (COD_COLABORADOR_CRIACAO)
  REFERENCES COLABORADOR(CODIGO),
  CONSTRAINT FK_PNEU_TIPO_SERVICO_COLABORADOR_EDICAO FOREIGN KEY (COD_COLABORADOR_EDICAO)
  REFERENCES COLABORADOR(CODIGO),
  CONSTRAINT UNIQUE_PNEU_TIPO_SERVICO UNIQUE(COD_EMPRESA, NOME)
);
COMMENT ON TABLE PNEU_TIPO_SERVICO
IS 'Tipos de serviços que podem ser executados em um pneu';
COMMENT ON COLUMN PNEU_TIPO_SERVICO.COD_EMPRESA
IS 'Para os Tipos de Serviços que o COD_EMPRESA é null, significa que eles são de nível ProLog, disponíveis para todas as empresas';

-- Insere tipo de serviço que será utilizado sempre que um pneu for cadastrado acima de primeira vida
INSERT INTO PNEU_TIPO_SERVICO(NOME, COD_EMPRESA, INCREMENTA_VIDA, UTILIZADO_CADASTRO_PNEU, STATUS_ATIVO, EDITAVEL, DATA_HORA_CRIACAO)
VALUES ('CADASTRO PNEU (acima primeira vida)', NULL, TRUE, TRUE, TRUE, FALSE, NOW());

-- Insere Tipo de Serviço RECAPAGEM e VULCANIZAÇÃO para todas as empresas do ProLog
INSERT INTO PNEU_TIPO_SERVICO(NOME, COD_EMPRESA, INCREMENTA_VIDA, STATUS_ATIVO, EDITAVEL, DATA_HORA_CRIACAO)
SELECT 'RECAPAGEM', CODIGO, TRUE, TRUE, TRUE, NOW() FROM EMPRESA E;

INSERT INTO PNEU_TIPO_SERVICO(NOME, COD_EMPRESA, INCREMENTA_VIDA, STATUS_ATIVO, EDITAVEL, DATA_HORA_CRIACAO)
SELECT 'VULCANIZAÇÃO', CODIGO, FALSE, TRUE, TRUE, NOW() FROM EMPRESA E;

-- ############# ADICIONA ATRIBUTOS DE RECAPADORA E CODIGO COLETA NA MOVIMENTACAO DESTINO ################
ALTER TABLE MOVIMENTACAO_DESTINO ADD COLUMN COD_RECAPADORA_DESTINO BIGINT;
ALTER TABLE MOVIMENTACAO_DESTINO ADD CONSTRAINT FK_MOVIMENTACAO_DESTINO_RECAPADORA
FOREIGN KEY (COD_RECAPADORA_DESTINO) REFERENCES RECAPADORA(CODIGO);

ALTER TABLE MOVIMENTACAO_DESTINO ADD COLUMN COD_COLETA VARCHAR(255);

-- #################### CRIA TABELAS PARA ARMAZENAR OS SERVIÇOS PNEUS EM ANÁLISE #########################
CREATE TABLE PNEU_SERVICO_REALIZADO (
  CODIGO BIGSERIAL NOT NULL,
  COD_PNEU_TIPO_SERVICO BIGINT NOT NULL,
  COD_UNIDADE BIGINT NOT NULL,
  COD_PNEU BIGINT NOT NULL,
  CUSTO REAL NOT NULL,
  VIDA SMALLINT NOT NULL,
  FONTE_SERVICO_REALIZADO VARCHAR(20) NOT NULL,
  CONSTRAINT PK_PNEU_SERVICO_REALIZADO PRIMARY KEY (CODIGO, FONTE_SERVICO_REALIZADO),
  CONSTRAINT FK_PNEU_SERVICO_REALIZADO_PNEU_TIPO_SERVICO FOREIGN KEY (COD_PNEU_TIPO_SERVICO)
  REFERENCES PNEU_TIPO_SERVICO(CODIGO),
  CONSTRAINT FK_PNEU_SERVICO_REALIZADO_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT FK_PNEU_SERVICO_REALIZADO_PNEU FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO),
  CONSTRAINT CHECK_CUSTO_NUMERO_POSITIVO CHECK (CUSTO >= 0),
  CONSTRAINT CHECK_FONTE_SERVICO_REALIZADO CHECK (FONTE_SERVICO_REALIZADO = 'FONTE_MOVIMENTACAO'
                                                  OR FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO')
);
COMMENT ON TABLE PNEU_SERVICO_REALIZADO
IS 'Serviços que foram realizados no pneu';

CREATE TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA (
  COD_PNEU_SERVICO_REALIZADO BIGINT NOT NULL,
  COD_MODELO_BANDA BIGINT NOT NULL,
  VIDA_NOVA_PNEU SMALLINT NOT NULL,
  FONTE_SERVICO_REALIZADO VARCHAR(20) NOT NULL,
  CONSTRAINT FK_PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_PNEU_SERVICO_REALIZADO
  FOREIGN KEY (COD_PNEU_SERVICO_REALIZADO, FONTE_SERVICO_REALIZADO)
  REFERENCES PNEU_SERVICO_REALIZADO (CODIGO, FONTE_SERVICO_REALIZADO),
  CONSTRAINT FK_PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_MODELO_BANDA FOREIGN KEY (COD_MODELO_BANDA)
  REFERENCES MODELO_BANDA(CODIGO),
  CONSTRAINT CHECK_FONTE_PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA CHECK (FONTE_SERVICO_REALIZADO = 'FONTE_MOVIMENTACAO'
                                                                       OR FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO')
);
COMMENT ON TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
IS 'Tabela para quando o serviço realizado for especificamente um serviço que incrementa a vida do pneu';

CREATE TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO (
  COD_MOVIMENTACAO BIGINT NOT NULL,
  COD_PNEU_SERVICO_REALIZADO BIGINT NOT NULL,
  FONTE_SERVICO_REALIZADO VARCHAR(20) DEFAULT 'FONTE_MOVIMENTACAO' NOT NULL,
  CONSTRAINT PK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO PRIMARY KEY (COD_MOVIMENTACAO, COD_PNEU_SERVICO_REALIZADO),
  CONSTRAINT FK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_MOVIMENTACAO FOREIGN KEY (COD_MOVIMENTACAO)
  REFERENCES MOVIMENTACAO(CODIGO),
  CONSTRAINT FK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_PNEU_SERVICO_REALIZADO
  FOREIGN KEY (COD_PNEU_SERVICO_REALIZADO, FONTE_SERVICO_REALIZADO)
  REFERENCES PNEU_SERVICO_REALIZADO(CODIGO, FONTE_SERVICO_REALIZADO),
  CONSTRAINT CHECK_FONTE_SERVICO_REALIZADO_FONTE_MOVIMENTACAO CHECK (FONTE_SERVICO_REALIZADO = 'FONTE_MOVIMENTACAO')
);
COMMENT ON TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO
IS 'Tabela de relacionamento entre movimentação e o serviço realizado no pneu';

CREATE TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA (
  COD_MOVIMENTACAO BIGINT NOT NULL,
  COD_PNEU_SERVICO_REALIZADO BIGINT NOT NULL,
  COD_RECAPADORA BIGINT NOT NULL,
  CONSTRAINT PK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
  PRIMARY KEY (COD_MOVIMENTACAO, COD_PNEU_SERVICO_REALIZADO, COD_RECAPADORA),
  CONSTRAINT FK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_MOVIMENTACAO_PNEU_SERVICO_REALIZADO
  FOREIGN KEY (COD_MOVIMENTACAO, COD_PNEU_SERVICO_REALIZADO)
  REFERENCES MOVIMENTACAO_PNEU_SERVICO_REALIZADO(COD_MOVIMENTACAO, COD_PNEU_SERVICO_REALIZADO),
  CONSTRAINT FK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_RECAPADORA FOREIGN KEY (COD_RECAPADORA)
  REFERENCES RECAPADORA (CODIGO)
);
COMMENT ON TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
IS 'Tabela de relacionamento entre movimentação e a recapadora onde os serviços aconteceram';

CREATE TABLE PNEU_SERVICO_CADASTRO (
  COD_PNEU BIGINT NOT NULL,
  COD_PNEU_SERVICO_REALIZADO BIGINT NOT NULL,
  FONTE_SERVICO_REALIZADO VARCHAR(20) DEFAULT 'FONTE_CADASTRO' NOT NULL,
  CONSTRAINT PK_PNEU_SERVICO_CADASTRO PRIMARY KEY (COD_PNEU, COD_PNEU_SERVICO_REALIZADO),
  CONSTRAINT FK_PNEU_SERVICO_CADASTRO_PNEU FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO),
  CONSTRAINT FK_PNEU_SERVICO_CADASTRO_PNEU_SERVICO_REALIZADO FOREIGN KEY (COD_PNEU_SERVICO_REALIZADO, FONTE_SERVICO_REALIZADO)
  REFERENCES PNEU_SERVICO_REALIZADO(CODIGO, FONTE_SERVICO_REALIZADO),
  CONSTRAINT CHECK_FONTE_SERVICO_REALIZADO_FONTE_CADASTRO CHECK (FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO')
);
COMMENT ON TABLE PNEU_SERVICO_CADASTRO
IS 'Pneus cadastrados que não são novos terão um serviço associado para mapear a troca de banda que o pneu sofreu';

-- ######### ADCIONA PERMISSÕES PARA INSERIR/EDITAR/VISUALIZAR OS TIPOS DE SERVIÇOS ######################
INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (133, 'Pneu • Tipo de Serviço - Cadastrar', 1);
INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (134, 'Pneu • Tipo de Serviço - Visualizar', 1);
INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (135, 'Pneu • Tipo de Serviço - Editar', 1);

-- ################ ASSOCIA PERMISSÃO A CARGOS JÁ EXISTENTES PARA POPULAR ################################
INSERT INTO CARGO_FUNCAO_PROLOG_V11(COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
  SELECT
    CFPV11.COD_UNIDADE,
    CFPV11.COD_FUNCAO_COLABORADOR,
    CASE
    WHEN (CFPV11.COD_FUNCAO_PROLOG = 130) THEN (133) -- Se tiver cadastro de recapadoras
    WHEN (CFPV11.COD_FUNCAO_PROLOG = 131) THEN (134) -- Se tiver visualização de recapadoras
    WHEN (CFPV11.COD_FUNCAO_PROLOG = 132) THEN (135) -- Se tiver Edição de recapadoras
    END,
    CFPV11.COD_PILAR_PROLOG
  FROM CARGO_FUNCAO_PROLOG_V11 AS CFPV11
  WHERE CFPV11.COD_FUNCAO_PROLOG = 130
        OR CFPV11.COD_FUNCAO_PROLOG = 131
        OR CFPV11.COD_FUNCAO_PROLOG = 132;

-- ########################################################################################################
-- ########################################################################################################
-- ########## CRIA FUNCTION PARA BUSCAR OS PNEUS DE UMA UNIDADE COM BASE NO STATUS  #######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_pneus_get_listagem_pneus_by_status(f_cod_unidade bigint, f_status_pneu text)
  RETURNS TABLE(
    CODIGO BIGINT,
    CODIGO_CLIENTE TEXT,
    POSICAO_PNEU INTEGER,
    DOT TEXT,
    VALOR REAL,
    COD_UNIDADE_ALOCADO BIGINT,
    COD_REGIONAL_ALOCADO BIGINT,
    PNEU_NOVO_NUNCA_RODADO BOOLEAN,
    COD_MARCA_PNEU BIGINT,
    NOME_MARCA_PNEU TEXT,
    COD_MODELO_PNEU BIGINT,
    NOME_MODELO_PNEU TEXT,
    QT_SULCOS_MODELO_PNEU SMALLINT,
    COD_MARCA_BANDA BIGINT,
    NOME_MARCA_BANDA TEXT,
    ALTURA_SULCOS_MODELO_PNEU REAL,
    COD_MODELO_BANDA BIGINT,
    NOME_MODELO_BANDA TEXT,
    QT_SULCOS_MODELO_BANDA SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA REAL,
    VALOR_BANDA REAL,
    ALTURA INTEGER,
    LARGURA INTEGER,
    ARO REAL,
    COD_DIMENSAO BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO REAL,
    ALTURA_SULCO_EXTERNO REAL,
    PRESSAO_RECOMENDADA REAL,
    PRESSAO_ATUAL REAL,
    STATUS TEXT,
    VIDA_ATUAL INTEGER,
    VIDA_TOTAL INTEGER)
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO,
  P.CODIGO_CLIENTE,
  VP.POSICAO                                 AS POSICAO_PNEU,
  P.DOT,
  P.VALOR,
  U.CODIGO                                   AS COD_UNIDADE_ALOCADO,
  R.CODIGO                                   AS COD_REGIONAL_ALOCADO,
  P.PNEU_NOVO_NUNCA_RODADO,
  MP.CODIGO                                  AS COD_MARCA_PNEU,
  MP.NOME                                    AS NOME_MARCA_PNEU,
  MOP.CODIGO                                 AS COD_MODELO_PNEU,
  MOP.NOME                                   AS NOME_MODELO_PNEU,
  MOP.QT_SULCOS                              AS QT_SULCOS_MODELO_PNEU,
  MAB.CODIGO                                 AS COD_MARCA_BANDA,
  MAB.NOME                                   AS NOME_MARCA_BANDA,
  MOP.ALTURA_SULCOS                          AS ALTURA_SULCOS_MODELO_PNEU,
  MOB.CODIGO                                 AS COD_MODELO_BANDA,
  MOB.NOME                                   AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS                              AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS                          AS ALTURA_SULCOS_MODELO_BANDA,
  PVV.VALOR                                  AS VALOR_BANDA,
  PD.ALTURA,
  PD.LARGURA,
  PD.ARO,
  PD.CODIGO                                  AS COD_DIMENSAO,
  P.ALTURA_SULCO_CENTRAL_INTERNO,
  P.ALTURA_SULCO_CENTRAL_EXTERNO,
  P.ALTURA_SULCO_INTERNO,
  P.ALTURA_SULCO_EXTERNO,
  P.PRESSAO_RECOMENDADA,
  P.PRESSAO_ATUAL,
  P.STATUS,
  P.VIDA_ATUAL,
  P.VIDA_TOTAL
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
  LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
WHERE P.COD_UNIDADE = f_cod_unidade AND P.STATUS LIKE f_status_pneu ORDER BY P.CODIGO_CLIENTE ASC;
$$;

-- ################ CRIA FUNCTION PARA BUSCAR OS PNEUS QUE ESTÃO EM ANALISE   #############################
CREATE OR REPLACE FUNCTION func_pneus_get_listagem_pneus_movimentacoes_analise(f_cod_unidade bigint)
  RETURNS TABLE(
    CODIGO BIGINT,
    CODIGO_CLIENTE TEXT,
    POSICAO_PNEU INTEGER,
    DOT TEXT,
    VALOR REAL,
    COD_UNIDADE_ALOCADO BIGINT,
    COD_REGIONAL_ALOCADO BIGINT,
    PNEU_NOVO_NUNCA_RODADO BOOLEAN,
    COD_MARCA_PNEU BIGINT,
    NOME_MARCA_PNEU TEXT,
    COD_MODELO_PNEU BIGINT,
    NOME_MODELO_PNEU TEXT,
    QT_SULCOS_MODELO_PNEU SMALLINT,
    COD_MARCA_BANDA BIGINT,
    NOME_MARCA_BANDA TEXT,
    ALTURA_SULCOS_MODELO_PNEU REAL,
    COD_MODELO_BANDA BIGINT,
    NOME_MODELO_BANDA TEXT,
    QT_SULCOS_MODELO_BANDA SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA REAL,
    VALOR_BANDA REAL,
    ALTURA INTEGER,
    LARGURA INTEGER,
    ARO REAL,
    COD_DIMENSAO BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO REAL,
    ALTURA_SULCO_EXTERNO REAL,
    PRESSAO_RECOMENDADA REAL,
    PRESSAO_ATUAL REAL,
    STATUS TEXT,
    VIDA_ATUAL INTEGER,
    VIDA_TOTAL INTEGER,
    COD_MOVIMENTACAO BIGINT,
    COD_RECAPADORA BIGINT,
    NOME_RECAPADORA TEXT,
    COD_EMPRESA_RECAPADORA BIGINT,
    RECAPADORA_ATIVA BOOLEAN,
    COD_COLETA TEXT)
LANGUAGE SQL
AS $$
WITH MOVIMENTACOES_ANALISE AS (
    SELECT
      INNER_TABLE.CODIGO AS COD_MOVIMENTACAO,
      INNER_TABLE.COD_PNEU AS COD_PNEU,
      INNER_TABLE.COD_RECAPADORA_DESTINO AS COD_RECAPADORA,
      INNER_TABLE.NOME AS NOME_RECAPADORA,
      INNER_TABLE.COD_EMPRESA AS COD_EMPRESA_RECAPADORA,
      INNER_TABLE.ATIVA AS RECAPADORA_ATIVA,
      INNER_TABLE.COD_COLETA AS COD_COLETA
    FROM (SELECT
            MOV.CODIGO,
            MOV.COD_PNEU,
            MAX(MOV.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_MOVIMENTACAO,
            MD.COD_RECAPADORA_DESTINO,
            REC.NOME,
            REC.COD_EMPRESA,
            REC.ATIVA,
            MD.COD_COLETA
          FROM MOVIMENTACAO AS MOV
            JOIN MOVIMENTACAO_DESTINO AS MD ON MOV.CODIGO = MD.COD_MOVIMENTACAO
            LEFT JOIN RECAPADORA AS REC ON MD.COD_RECAPADORA_DESTINO = REC.CODIGO
          WHERE COD_UNIDADE = f_cod_unidade AND MD.TIPO_DESTINO = 'ANALISE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_MOVIMENTACAO
)

SELECT FUNC.*,
  MA.COD_MOVIMENTACAO,
  MA.COD_RECAPADORA,
  MA.NOME_RECAPADORA,
  MA.COD_EMPRESA_RECAPADORA,
  MA.RECAPADORA_ATIVA,
  MA.COD_COLETA
FROM func_pneus_get_listagem_pneus_by_status(f_cod_unidade, 'ANALISE') AS FUNC
  JOIN MOVIMENTACOES_ANALISE MA ON MA.COD_PNEU = FUNC.CODIGO;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- #############################            WARNING           #############################################
-- ########## ESSA MIGRATION NÃO É TODA AUTOMATIZADA, O BANCO CONTÉM ALGUMAS INCONSISTÊNCIAS  #############
-- ########## QUE FAZEM ALGUMAS ENTRADAS DA PNEU_VALOR_VIDA NÃO SEREM MAPEADAS, É NECESSÁRIO  #############
-- ########## FAZER UMA CONTAGEM E IDENTIFICAR QUAIS SÃO ESSES PNEUS, NO AMBIENTE DE PRODUÇÃO #############
-- ########## PARA MIGRAR MANUALMENTE ESSES CASOS. NÃO SÃO MUITOS, ATÉ O MOMENTO APENAS 2     #############
-- ########## FORAM IDENTIFICADOS:  PNEU_CODIGO = 3633 e PNEU_CODIGO = 7113                   #############
-- ########################################################################################################
-- ########################################################################################################

-- ####################### MIGRA DADOS DA PNEU_VALOR_VIDA PARA OS SERVIÇOS ################################
-- Colunas provisórias par facilitar na migração dos dados. Serão removidas no fim do migration.
ALTER TABLE PNEU_SERVICO_REALIZADO ADD COLUMN COD_MODELO_BANDA BIGINT;
ALTER TABLE PNEU_SERVICO_REALIZADO ADD COLUMN VIDA_NOVA_PNEU INTEGER;
ALTER TABLE PNEU_SERVICO_REALIZADO ADD COLUMN COD_MOVIMENTACAO BIGINT;

-- QUERY PARA MIGRAR VALORES DA TABELA PNEU_VALOR_VIDA QUE TEM MOVIMENTAÇÕES ASSOCIADAS
INSERT INTO PNEU_SERVICO_REALIZADO
(COD_PNEU_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, COD_MODELO_BANDA, VIDA_NOVA_PNEU, COD_MOVIMENTACAO, FONTE_SERVICO_REALIZADO)
  SELECT
    (SELECT PTS.CODIGO
     FROM PNEU_TIPO_SERVICO AS PTS
     WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE AND INCREMENTA_VIDA = TRUE),
    PVV.COD_UNIDADE,
    PVV.COD_PNEU,
    PVV.VALOR,
    PVV.VIDA - 1 AS VIDA_MOMENTO_SERVICO,
    PVV.COD_MODELO_BANDA,
    PVV.VIDA AS VIDA_NOVA,
    M.CODIGO AS COD_MOVIMENTACAO,
    'FONTE_MOVIMENTACAO' AS FONTE_SERVICO_REALIZADO
  FROM PNEU_VALOR_VIDA AS PVV
    LEFT JOIN MOVIMENTACAO AS M
      ON M.COD_PNEU = PVV.COD_PNEU AND PVV.VIDA - 1 = M.VIDA
    JOIN MOVIMENTACAO_ORIGEM AS MO
      ON M.CODIGO = MO.COD_MOVIMENTACAO AND MO.TIPO_ORIGEM = 'ANALISE'
    JOIN MOVIMENTACAO_DESTINO AS MD
      ON M.CODIGO = MD.COD_MOVIMENTACAO AND MD.TIPO_DESTINO = 'ESTOQUE';

-- QUERY PARA MIGRAR VALORES DA TABELA PNEU_VALOR_VIDA QUE SÃO ASSOCIADOS A CADASTRO
INSERT INTO PNEU_SERVICO_REALIZADO
(COD_PNEU_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, COD_MODELO_BANDA, VIDA_NOVA_PNEU, COD_MOVIMENTACAO, FONTE_SERVICO_REALIZADO)
  SELECT
    (SELECT PTS.CODIGO
     FROM PNEU_TIPO_SERVICO AS PTS
     WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE AND INCREMENTA_VIDA = TRUE AND UTILIZADO_CADASTRO_PNEU = TRUE),
    PVV.COD_UNIDADE,
    PVV.COD_PNEU,
    PVV.VALOR,
    PVV.VIDA - 1 AS VIDA_MOMENTO_SERVICO,
    PVV.COD_MODELO_BANDA,
    PVV.VIDA,
    M.CODIGO AS COD_MOVIMENTACAO,
    'FONTE_CADASTRO' AS FONTE_SERVICO_REALIZADO
  FROM PNEU_VALOR_VIDA AS PVV
    LEFT JOIN MOVIMENTACAO AS M
      ON M.COD_PNEU = PVV.COD_PNEU AND PVV.VIDA - 1 = M.VIDA
  WHERE M.VIDA IS NULL;

-- QUERY PARA MIGRAR VALORES DA TABELA PNEU_VALOR_VIDA QUE TEM INCOSISTÊNCIA
INSERT INTO PNEU_SERVICO_REALIZADO
(COD_PNEU_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, COD_MODELO_BANDA, VIDA_NOVA_PNEU, FONTE_SERVICO_REALIZADO)
  SELECT
    (SELECT PTS.CODIGO
     FROM PNEU_TIPO_SERVICO AS PTS
     WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE AND INCREMENTA_VIDA = TRUE AND UTILIZADO_CADASTRO_PNEU = TRUE),
    PVV.COD_UNIDADE,
    PVV.COD_PNEU,
    PVV.VALOR,
    PVV.VIDA - 1 AS VIDA_MOMENTO_SERVICO,
    PVV.COD_MODELO_BANDA,
    PVV.VIDA,
    'FONTE_CADASTRO' AS FONTE_SERVICO_REALIZADO
  FROM PNEU_VALOR_VIDA AS PVV WHERE PVV.COD_PNEU = 3633;

INSERT INTO PNEU_SERVICO_REALIZADO
(COD_PNEU_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, COD_MODELO_BANDA, VIDA_NOVA_PNEU, FONTE_SERVICO_REALIZADO)
  SELECT
    (SELECT PTS.CODIGO
     FROM PNEU_TIPO_SERVICO AS PTS
     WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE AND INCREMENTA_VIDA = TRUE AND UTILIZADO_CADASTRO_PNEU = TRUE),
    PVV.COD_UNIDADE,
    PVV.COD_PNEU,
    PVV.VALOR,
    PVV.VIDA - 1 AS VIDA_MOMENTO_SERVICO,
    PVV.COD_MODELO_BANDA,
    PVV.VIDA,
    'FONTE_CADASTRO' AS FONTE_SERVICO_REALIZADO
  FROM PNEU_VALOR_VIDA AS PVV WHERE PVV.COD_PNEU = 7113;

-- TODOS OS SERVIÇOS MIGRADOS SÃO RECAPAGENS, DEVEMOS INSERIR NA TABELA SERVICO_REALIZADO_RECAPAGEM
INSERT INTO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
(COD_PNEU_SERVICO_REALIZADO, COD_MODELO_BANDA, VIDA_NOVA_PNEU, FONTE_SERVICO_REALIZADO)
  SELECT
    PSR.CODIGO,
    PSR.COD_MODELO_BANDA,
    PSR.VIDA_NOVA_PNEU,
    PSR.FONTE_SERVICO_REALIZADO
  FROM PNEU_SERVICO_REALIZADO AS PSR;

-- PARA OS SERVIÇOS QUE TEM MOVIMENTAÇÃO ASSOCIADA, FAZEMOS A INSERÇÃO NA TABELA DE MAPEAMENTO DE MOVIMENTACAO
INSERT INTO MOVIMENTACAO_PNEU_SERVICO_REALIZADO (COD_MOVIMENTACAO, COD_PNEU_SERVICO_REALIZADO)
  SELECT
    PSR.COD_MOVIMENTACAO,
    PSR.CODIGO
  FROM PNEU_SERVICO_REALIZADO AS PSR
  WHERE PSR.COD_MOVIMENTACAO IS NOT NULL AND PSR.FONTE_SERVICO_REALIZADO = 'FONTE_MOVIMENTACAO';

-- PARA OS SERVIÇOS QUE NÃO TEM MOVIMENTAÇÃO ASSOCIADA, FAZEMOS A INSERÇÃO NA TABELA DE MAPEAMENTO DE CADASTRO
INSERT INTO PNEU_SERVICO_CADASTRO (COD_PNEU, COD_PNEU_SERVICO_REALIZADO)
  SELECT
    PSR.COD_PNEU,
    PSR.CODIGO
  FROM PNEU_SERVICO_REALIZADO AS PSR
  WHERE PSR.COD_MOVIMENTACAO IS NULL AND PSR.FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO';

ALTER TABLE PNEU_SERVICO_REALIZADO DROP COLUMN COD_MODELO_BANDA;
ALTER TABLE PNEU_SERVICO_REALIZADO DROP COLUMN VIDA_NOVA_PNEU;
ALTER TABLE PNEU_SERVICO_REALIZADO DROP COLUMN COD_MOVIMENTACAO;

-- ########################### DROPA DEPENDENCIAS DA PNEU_VALOR_VIDA ####################################
DROP VIEW VIEW_PNEU_ANALISE_VIDA_ATUAL;
DROP VIEW VIEW_PNEU_ANALISE_VIDAS;
DROP TABLE PNEU_VALOR_VIDA;

-- ################### CRIA VIEW PARA SUBSTITUIR A ANTIGA TABELA PNEU VALOR VIDA #########################
CREATE OR REPLACE VIEW PNEU_VALOR_VIDA AS
  SELECT
    SRR.COD_UNIDADE        AS COD_UNIDADE,
    SRR.COD_PNEU           AS COD_PNEU,
    SRREC.COD_MODELO_BANDA AS COD_MODELO_BANDA,
    SRREC.VIDA_NOVA_PNEU   AS VIDA,
    SRR.CUSTO              AS VALOR
  FROM PNEU_SERVICO_REALIZADO AS SRR
    JOIN PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA AS SRREC
      ON SRR.CODIGO = SRREC.COD_PNEU_SERVICO_REALIZADO;
COMMENT ON VIEW PNEU_VALOR_VIDA
IS 'View que contém o valor e a vida associados a um pneu, somente para pneus que já foram recapados.';

-- ################### RECRIA VIEW QUE FOI DELETADA PARA RECRIAÇÃO DA PNEU VALOR VIDA #########################
create or replace view view_pneu_analise_vidas as
  SELECT av.cod_pneu,
    av.vida_momento_afericao AS vida,
    p_1.status,
    p_1.valor AS valor_pneu,
    COALESCE(pvv.valor, (0)::real) AS valor_banda,
    av.cod_unidade,
    count(av.altura_sulco_central_interno) AS qt_afericoes,
    (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                    FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS primeira_afericao,
    (max(timezone(( SELECT func_get_time_zone_unidade.timezone
                    FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS ultima_afericao,
    ((max(timezone(( SELECT func_get_time_zone_unidade.timezone
                     FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                                                                                                                                                   FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) AS total_dias,
    max(total_km.total_km) AS total_km_percorrido_vida,
    max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS maior_sulco,
    min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS menor_sulco,
    (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) AS sulco_gasto,
    CASE
    WHEN (
      CASE
      WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
      WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
      ELSE NULL::real
      END < (0)::double precision) THEN (0)::real
    ELSE
      CASE
      WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
      WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
      ELSE NULL::real
      END
    END AS sulco_restante,
    CASE
    WHEN ((((max(timezone(( SELECT func_get_time_zone_unidade.timezone
                            FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                                                                                                                                                          FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) > 0) AND ((max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) > (0)::double precision)) THEN ((max(total_km.total_km))::double precision / (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))))
    ELSE (0)::double precision
    END AS km_por_mm,
    CASE
    WHEN (max(total_km.total_km) <= (0)::numeric) THEN (0)::double precision
    ELSE
      CASE
      WHEN (av.vida_momento_afericao = 1) THEN (p_1.valor / (max(total_km.total_km))::double precision)
      ELSE (COALESCE(pvv.valor, (0)::real) / (max(total_km.total_km))::double precision)
      END
    END AS valor_por_km_vida_atual
  FROM (((((afericao_valores av
    JOIN afericao a ON ((a.codigo = av.cod_afericao)))
    JOIN pneu p_1 ON ((((p_1.codigo)::text = (av.cod_pneu)::text) AND (p_1.cod_unidade = av.cod_unidade))))
    JOIN pneu_restricao_unidade erp ON ((erp.cod_unidade = av.cod_unidade)))
    LEFT JOIN pneu_valor_vida pvv ON (((pvv.cod_unidade = p_1.cod_unidade) AND ((pvv.cod_pneu)::text = (p_1.codigo)::text) AND (pvv.vida = av.vida_momento_afericao))))
    JOIN ( SELECT view_pneu_km_percorrido.cod_pneu,
             view_pneu_km_percorrido.vida,
             view_pneu_km_percorrido.cod_unidade,
             view_pneu_km_percorrido.total_km
           FROM view_pneu_km_percorrido) total_km ON ((((total_km.cod_pneu)::text = (av.cod_pneu)::text) AND (total_km.cod_unidade = av.cod_unidade) AND (total_km.vida = av.vida_momento_afericao))))
  GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, erp.sulco_minimo_descarte, erp.sulco_minimo_recapagem, av.vida_momento_afericao, pvv.valor, p_1.valor, p_1.status
  ORDER BY av.cod_pneu, av.vida_momento_afericao;

-- ################### RECRIA VIEW QUE FOI DELETADA PARA RECRIAÇÃO DA PNEU VALOR VIDA #########################
create or replace view view_pneu_analise_vida_atual as
  SELECT p.codigo AS "COD PNEU",
         p.codigo_cliente AS "COD PNEU CLIENTE",
         (p.valor + sum(acumulado.valor_banda)) AS valor_acumulado,
         sum(acumulado.total_km_percorrido_vida) AS km_acumulado,
         p.vida_atual AS "VIDA ATUAL",
         p.status AS "STATUS PNEU",
    p.cod_unidade,
         p.valor AS valor_pneu,
         CASE
         WHEN (dados.vida = 1) THEN dados.valor_pneu
         ELSE dados.valor_banda
         END AS valor_vida_atual,
         map.nome AS "MARCA",
         mp.nome AS "MODELO",
         ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
         dados.qt_afericoes AS "QTD DE AFERIÇÕES",
         to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
         to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
         dados.total_dias AS "DIAS ATIVO",
         round(
             CASE
             WHEN (dados.total_dias > 0) THEN (dados.total_km_percorrido_vida / (dados.total_dias)::numeric)
             ELSE NULL::numeric
             END) AS "MÉDIA KM POR DIA",
         round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
         round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
         round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
         round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
         round((dados.valor_por_km_vida_atual)::numeric, 2) AS "VALOR POR KM",
         round((
                 CASE
                 WHEN (sum(acumulado.total_km_percorrido_vida) > (0)::numeric) THEN ((p.valor + sum(acumulado.valor_banda)) / (sum(acumulado.total_km_percorrido_vida))::double precision)
                 ELSE (0)::double precision
                 END)::numeric, 2) AS "VALOR POR KM ACUMULADO",
         round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
         trunc(
             CASE
             WHEN ((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision)
             ELSE (0)::double precision
             END) AS "DIAS RESTANTES",
         CASE
         WHEN ((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
         ELSE NULL::date
         END AS "PREVISÃO DE TROCA",
         CASE
         WHEN (p.vida_atual = p.vida_total) THEN 'DESCARTE'::text
         ELSE 'ANÁLISE'::text
         END AS "DESTINO"
  FROM ((((((pneu p
    JOIN ( SELECT view_pneu_analise_vidas.cod_pneu,
             view_pneu_analise_vidas.vida,
             view_pneu_analise_vidas.status,
             view_pneu_analise_vidas.valor_pneu,
             view_pneu_analise_vidas.valor_banda,
             view_pneu_analise_vidas.cod_unidade,
             view_pneu_analise_vidas.qt_afericoes,
             view_pneu_analise_vidas.primeira_afericao,
             view_pneu_analise_vidas.ultima_afericao,
             view_pneu_analise_vidas.total_dias,
             view_pneu_analise_vidas.total_km_percorrido_vida,
             view_pneu_analise_vidas.maior_sulco,
             view_pneu_analise_vidas.menor_sulco,
             view_pneu_analise_vidas.sulco_gasto,
             view_pneu_analise_vidas.sulco_restante,
             view_pneu_analise_vidas.km_por_mm,
             view_pneu_analise_vidas.valor_por_km_vida_atual
           FROM view_pneu_analise_vidas) dados ON ((((dados.cod_pneu)::text = (p.codigo)::text) AND (dados.cod_unidade = p.cod_unidade) AND (dados.vida = p.vida_atual))))
    JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
    JOIN unidade u ON ((u.codigo = p.cod_unidade)))
    JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
    JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)))
    JOIN view_pneu_analise_vidas acumulado ON ((((acumulado.cod_pneu)::text = (p.codigo)::text) AND (acumulado.cod_unidade = p.cod_unidade))))
  GROUP BY p.codigo, p.cod_unidade, dados.valor_banda, dados.valor_pneu, map.nome, mp.nome, dp.largura, dp.altura, dp.aro, dados.qt_afericoes, dados.primeira_afericao, dados.ultima_afericao, dados.total_dias, dados.total_km_percorrido_vida, dados.maior_sulco, dados.menor_sulco, dados.sulco_gasto, dados.km_por_mm, dados.valor_por_km_vida_atual, dados.sulco_restante, dados.vida
  ORDER BY
    CASE
    WHEN ((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
    ELSE NULL::date
    END;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #########################     CRIA FUNCTION PARA ATUALIZAR A VIDA DO PNEU     ##########################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_pneus_incrementa_vida_pneu(f_cod_pneu bigint, f_cod_modelo_banda bigint)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
  altura_sulco_banda REAL;
  vida_nova_pneu INTEGER;
BEGIN
  altura_sulco_banda = (SELECT MB.ALTURA_SULCOS FROM MODELO_BANDA AS MB WHERE CODIGO = f_cod_modelo_banda);
  vida_nova_pneu = (SELECT P.VIDA_ATUAL FROM PNEU AS P WHERE CODIGO = f_cod_pneu) + 1;
  UPDATE PNEU AS P SET
    VIDA_ATUAL = vida_nova_pneu,
    VIDA_TOTAL = (CASE WHEN (vida_nova_pneu > P.VIDA_TOTAL) THEN vida_nova_pneu ELSE P.VIDA_TOTAL END),
    COD_MODELO_BANDA = f_cod_modelo_banda,
    ALTURA_SULCO_INTERNO = altura_sulco_banda,
    ALTURA_SULCO_CENTRAL_INTERNO = altura_sulco_banda,
    ALTURA_SULCO_CENTRAL_EXTERNO = altura_sulco_banda,
    ALTURA_SULCO_EXTERNO = altura_sulco_banda,
    PNEU_NOVO_NUNCA_RODADO = FALSE
  WHERE CODIGO = f_cod_pneu;
  RETURN TRUE;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################














-- Migration dos relatórios dos pneus














-- ########################################################################################################
-- ########################################################################################################
-- ##################  CRIA FUNCTION PARA RETONAR A LISTAGEM DE FAIXAS DOS PNEUS      #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
CREATE OR REPLACE FUNCTION func_relatorio_pneus_by_faixa_sulco(f_cod_unidade text[], f_status_pneu text[])
  RETURNS TABLE(ALTURA_SULCO_CENTRAL REAL)
LANGUAGE SQL
AS $$
SELECT COALESCE(ALTURA_SULCO_CENTRAL_INTERNO, ALTURA_SULCO_CENTRAL_EXTERNO, -1) AS ALTURA_SULCO_CENTRAL
FROM PNEU
WHERE COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND STATUS LIKE ANY (f_status_pneu)
ORDER BY 1 DESC;
$$;
-- ########################################################################################################
-- ########################################################################################################

DROP VIEW VIEW_PNEU_ANALISE_VIDA_ATUAL;
DROP VIEW VIEW_PNEU_ANALISE_VIDAS;
DROP VIEW VIEW_ANALISE_PNEUS;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA VIEW QUE LISTA INFORMAÇÕES DO PNEU COM BASE NA VIDA     #####################
-- ##################  INSERE INFORMAÇÃO DO NOME DA UNIDADE PARA QUE SEJA MOSTRADO    #####################
-- ##################  NOS RELATÓRIOS DE ESTRATIFICAÇÃO DE MAIS DE UMA UNIADE         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
CREATE OR REPLACE VIEW VIEW_PNEU_ANALISE_VIDAS AS
  SELECT
    AV.COD_PNEU,
    AV.VIDA_MOMENTO_AFERICAO                                                                                                            AS VIDA,
    P.STATUS,
    P.VALOR                                                                                                                             AS VALOR_PNEU,
    COALESCE(PVV.VALOR, (0)::REAL)                                                                                                      AS VALOR_BANDA,
    AV.COD_UNIDADE,
    COUNT(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                                                                              AS QT_AFERICOES,
    (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE                                                                     AS PRIMEIRA_AFERICAO,
    (MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE                                                                     AS ULTIMA_AFERICAO,
    ((MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE - (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE) AS TOTAL_DIAS,
    MAX(TOTAL_KM.TOTAL_KM)                                                                                                              AS TOTAL_KM_PERCORRIDO_VIDA,
    MAX(GREATEST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))   AS MAIOR_SULCO,
    MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))      AS MENOR_SULCO,
    (MAX(GREATEST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
     - MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO)))  AS SULCO_GASTO,
    CASE
    WHEN (CASE
          WHEN (P.VIDA_ATUAL = P.VIDA_TOTAL)
            THEN (MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
                  - PRU.SULCO_MINIMO_DESCARTE)
          WHEN (P.VIDA_ATUAL < P.VIDA_TOTAL)
            THEN (MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
                  - PRU.SULCO_MINIMO_RECAPAGEM)
          ELSE NULL::REAL
          END < (0)::DOUBLE PRECISION)
      THEN (0)::REAL
    ELSE
      CASE
      WHEN (P.VIDA_ATUAL = P.VIDA_TOTAL)
        THEN (MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
              - PRU.SULCO_MINIMO_DESCARTE)
      WHEN (P.VIDA_ATUAL < P.VIDA_TOTAL)
        THEN (MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
              - PRU.SULCO_MINIMO_RECAPAGEM)
      ELSE NULL::REAL
      END
    END                                                                                                                                 AS SULCO_RESTANTE,
    CASE
    WHEN ((((MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE
            - (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE) > 0)
          AND ((MAX(GREATEST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
                - MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO)))
               > (0)::DOUBLE PRECISION))
      THEN ((MAX(TOTAL_KM.TOTAL_KM))::DOUBLE PRECISION
            / (MAX(GREATEST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))
               - MIN(LEAST(AV.ALTURA_SULCO_CENTRAL_INTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO, AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_INTERNO))))
    ELSE (0)::DOUBLE PRECISION
    END                                                                                                                                 AS KM_POR_MM,
    CASE
    WHEN (MAX(TOTAL_KM.TOTAL_KM) <= (0)::NUMERIC)
      THEN (0)::DOUBLE PRECISION
    ELSE
      CASE
      WHEN (AV.VIDA_MOMENTO_AFERICAO = 1)
        THEN (P.VALOR / (MAX(TOTAL_KM.TOTAL_KM))::DOUBLE PRECISION)
      ELSE (COALESCE(PVV.VALOR, (0)::REAL) / (MAX(TOTAL_KM.TOTAL_KM))::DOUBLE PRECISION)
      END
    END                                                                                                                                 AS VALOR_POR_KM_VIDA_ATUAL
  FROM (((((AFERICAO_VALORES AV
    JOIN AFERICAO A
      ON ((A.CODIGO = AV.COD_AFERICAO)))
    JOIN PNEU P
      ON ((((P.CODIGO)::TEXT = (AV.COD_PNEU)::TEXT))))
    JOIN PNEU_RESTRICAO_UNIDADE PRU
      ON ((PRU.COD_UNIDADE = AV.COD_UNIDADE)))
    LEFT JOIN PNEU_VALOR_VIDA PVV
      ON ((((PVV.COD_PNEU)::TEXT = (P.CODIGO)::TEXT)
           AND (PVV.VIDA = AV.VIDA_MOMENTO_AFERICAO))))
    JOIN (SELECT VIEW_PNEU_KM_PERCORRIDO.COD_PNEU,
            VIEW_PNEU_KM_PERCORRIDO.VIDA,
            VIEW_PNEU_KM_PERCORRIDO.COD_UNIDADE,
            VIEW_PNEU_KM_PERCORRIDO.TOTAL_KM
          FROM VIEW_PNEU_KM_PERCORRIDO) TOTAL_KM
      ON ((((TOTAL_KM.COD_PNEU)::TEXT = (AV.COD_PNEU)::TEXT)
           AND (TOTAL_KM.COD_UNIDADE = AV.COD_UNIDADE)
           AND (TOTAL_KM.VIDA = AV.VIDA_MOMENTO_AFERICAO))))
  GROUP BY AV.COD_PNEU, AV.COD_UNIDADE, P.VIDA_ATUAL, P.VIDA_TOTAL, PRU.SULCO_MINIMO_DESCARTE,
    PRU.SULCO_MINIMO_RECAPAGEM, AV.VIDA_MOMENTO_AFERICAO, PVV.VALOR, P.VALOR, P.STATUS
  ORDER BY AV.COD_PNEU, AV.VIDA_MOMENTO_AFERICAO;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA VIEW VIEW_PNEU_ANALISE_VIDA_ATUAL ###########################################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
-- Essa View utiliza o NOW e soma o total de dias que o pneu ainda tem para rodar. Precisamos verificar se essa
-- abordagem lida corretamente com time zone.
CREATE OR REPLACE VIEW VIEW_PNEU_ANALISE_VIDA_ATUAL AS
  SELECT
    U.NOME                                                                            AS "UNIDADE ALOCADO",
    P.CODIGO                                                                          AS "COD PNEU",
    P.CODIGO_CLIENTE                                                                  AS "COD PNEU CLIENTE",
    (P.VALOR + SUM(ACUMULADO.VALOR_BANDA))                                            AS VALOR_ACUMULADO,
    SUM(ACUMULADO.TOTAL_KM_PERCORRIDO_VIDA)                                           AS KM_ACUMULADO,
    P.VIDA_ATUAL                                                                      AS "VIDA ATUAL",
    P.STATUS                                                                          AS "STATUS PNEU",
    P.COD_UNIDADE,
    P.VALOR                                                                           AS VALOR_PNEU,
    CASE
    WHEN (DADOS.VIDA = 1)
      THEN DADOS.VALOR_PNEU
    ELSE DADOS.VALOR_BANDA
    END                                                                               AS VALOR_VIDA_ATUAL,
    MAP.NOME                                                                          AS "MARCA",
    MP.NOME                                                                           AS "MODELO",
    ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)              AS "MEDIDAS",
    DADOS.QT_AFERICOES                                                                AS "QTD DE AFERIÇÕES",
    TO_CHAR(DADOS.PRIMEIRA_AFERICAO, 'DD/MM/YYYY')                                    AS "DTA 1a AFERIÇÃO",
    TO_CHAR(DADOS.ULTIMA_AFERICAO, 'DD/MM/YYYY')                                      AS "DTA ÚLTIMA AFERIÇÃO",
    DADOS.TOTAL_DIAS                                                                  AS "DIAS ATIVO",
    ROUND(CASE
          WHEN (DADOS.TOTAL_DIAS > 0)
            THEN (DADOS.TOTAL_KM_PERCORRIDO_VIDA / (DADOS.TOTAL_DIAS)::NUMERIC)
          ELSE NULL::NUMERIC
          END)                                                                        AS "MÉDIA KM POR DIA",
    ROUND((DADOS.MAIOR_SULCO)::NUMERIC, 2)                                            AS "MAIOR MEDIÇÃO VIDA",
    ROUND((DADOS.MENOR_SULCO)::NUMERIC, 2)                                            AS "MENOR SULCO ATUAL",
    ROUND((DADOS.SULCO_GASTO)::NUMERIC, 2)                                            AS "MILIMETROS GASTOS",
    ROUND((DADOS.KM_POR_MM)::NUMERIC, 2)                                              AS "KMS POR MILIMETRO",
    ROUND((DADOS.VALOR_POR_KM_VIDA_ATUAL)::NUMERIC, 2)                                AS "VALOR POR KM",
    ROUND((CASE
           WHEN (SUM(ACUMULADO.TOTAL_KM_PERCORRIDO_VIDA) > (0)::NUMERIC)
             THEN ((P.VALOR + SUM(ACUMULADO.VALOR_BANDA)) / (SUM(ACUMULADO.TOTAL_KM_PERCORRIDO_VIDA))::DOUBLE PRECISION)
           ELSE (0)::DOUBLE PRECISION
           END)::NUMERIC, 2)                                                          AS "VALOR POR KM ACUMULADO",
    ROUND(((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE))::NUMERIC)                        AS "KMS A PERCORRER",
    TRUNC(CASE
          WHEN ((DADOS.TOTAL_KM_PERCORRIDO_VIDA > (0)::NUMERIC)
                AND (DADOS.TOTAL_DIAS > 0)
                AND ((DADOS.TOTAL_KM_PERCORRIDO_VIDA / (DADOS.TOTAL_DIAS)::NUMERIC) > (0)::NUMERIC))
            THEN ((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE) /
                  ((DADOS.TOTAL_KM_PERCORRIDO_VIDA / (DADOS.TOTAL_DIAS)::NUMERIC))::DOUBLE PRECISION)
          ELSE (0)::DOUBLE PRECISION
          END)                                                                        AS "DIAS RESTANTES",
    CASE
    WHEN ((DADOS.TOTAL_KM_PERCORRIDO_VIDA > (0)::NUMERIC)
          AND (DADOS.TOTAL_DIAS > 0)
          AND ((DADOS.TOTAL_KM_PERCORRIDO_VIDA / (DADOS.TOTAL_DIAS)::NUMERIC) > (0)::NUMERIC))
      THEN ((((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE)
              / ((DADOS.TOTAL_KM_PERCORRIDO_VIDA
                  / (DADOS.TOTAL_DIAS)::NUMERIC))::DOUBLE PRECISION))::INTEGER + ('NOW'::TEXT)::DATE)
    ELSE NULL::DATE
    END                                                                               AS "PREVISÃO DE TROCA",
    CASE
    WHEN (P.VIDA_ATUAL = P.VIDA_TOTAL)
      THEN 'DESCARTE'::TEXT
    ELSE 'ANÁLISE'::TEXT
    END                                                                               AS "DESTINO"
  FROM PNEU P
    JOIN (SELECT VIEW_PNEU_ANALISE_VIDAS.COD_PNEU,
             VIEW_PNEU_ANALISE_VIDAS.VIDA,
             VIEW_PNEU_ANALISE_VIDAS.STATUS,
             VIEW_PNEU_ANALISE_VIDAS.VALOR_PNEU,
             VIEW_PNEU_ANALISE_VIDAS.VALOR_BANDA,
             VIEW_PNEU_ANALISE_VIDAS.COD_UNIDADE,
             VIEW_PNEU_ANALISE_VIDAS.QT_AFERICOES,
             VIEW_PNEU_ANALISE_VIDAS.PRIMEIRA_AFERICAO,
             VIEW_PNEU_ANALISE_VIDAS.ULTIMA_AFERICAO,
             VIEW_PNEU_ANALISE_VIDAS.TOTAL_DIAS,
             VIEW_PNEU_ANALISE_VIDAS.TOTAL_KM_PERCORRIDO_VIDA,
             VIEW_PNEU_ANALISE_VIDAS.MAIOR_SULCO,
             VIEW_PNEU_ANALISE_VIDAS.MENOR_SULCO,
             VIEW_PNEU_ANALISE_VIDAS.SULCO_GASTO,
             VIEW_PNEU_ANALISE_VIDAS.SULCO_RESTANTE,
             VIEW_PNEU_ANALISE_VIDAS.KM_POR_MM,
             VIEW_PNEU_ANALISE_VIDAS.VALOR_POR_KM_VIDA_ATUAL
           FROM VIEW_PNEU_ANALISE_VIDAS) DADOS
      ON DADOS.COD_PNEU = P.CODIGO AND DADOS.VIDA = P.VIDA_ATUAL
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    JOIN VIEW_PNEU_ANALISE_VIDAS ACUMULADO ON ACUMULADO.COD_PNEU = P.CODIGO
  GROUP BY U.NOME, P.CODIGO, P.COD_UNIDADE, DADOS.VALOR_BANDA, DADOS.VALOR_PNEU, MAP.NOME, MP.NOME, DP.LARGURA, DP.ALTURA,
    DP.ARO, DADOS.QT_AFERICOES, DADOS.PRIMEIRA_AFERICAO, DADOS.ULTIMA_AFERICAO, DADOS.TOTAL_DIAS, DADOS.TOTAL_KM_PERCORRIDO_VIDA,
    DADOS.MAIOR_SULCO, DADOS.MENOR_SULCO, DADOS.SULCO_GASTO, DADOS.KM_POR_MM, DADOS.VALOR_POR_KM_VIDA_ATUAL, DADOS.SULCO_RESTANTE, DADOS.VIDA
  ORDER BY
    CASE
    WHEN ((DADOS.TOTAL_KM_PERCORRIDO_VIDA > (0)::NUMERIC)
          AND (DADOS.TOTAL_DIAS > 0)
          AND ((DADOS.TOTAL_KM_PERCORRIDO_VIDA / (DADOS.TOTAL_DIAS)::NUMERIC) > (0)::NUMERIC))
      THEN ((((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE)
              / ((DADOS.TOTAL_KM_PERCORRIDO_VIDA
                  / (DADOS.TOTAL_DIAS)::NUMERIC))::DOUBLE PRECISION))::INTEGER + ('NOW'::TEXT)::DATE)
    ELSE NULL::DATE
    END;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA VIEW QUE LISTA INFORMAÇÕES DO PNEU                      #####################
-- ##################  INSERE INFORMAÇÃO DO NOME DA UNIDADE PARA QUE SEJA MOSTRADO    #####################
-- ##################  NOS RELATÓRIOS CONSOLIDADEOS DE MAIS DE UMA UNIADE             #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
-- Essa View utiliza o NOW e soma o total de dias que o pneu ainda tem para rodar. Precisamos verificar se essa
-- abordagem lida corretamente com time zone.
CREATE OR REPLACE VIEW VIEW_ANALISE_PNEUS AS
  SELECT
    U.NOME                                                                              AS "UNIDADE ALOCADO",
    P.CODIGO                                                                            AS "COD PNEU",
    P.CODIGO_CLIENTE                                                                    AS "COD PNEU CLIENTE",
    P.STATUS                                                                            AS "STATUS PNEU",
    P.COD_UNIDADE,
    MAP.NOME                                                                            AS "MARCA",
    MP.NOME                                                                             AS "MODELO",
    ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)                AS "MEDIDAS",
    DADOS.QT_AFERICOES                                                                  AS "QTD DE AFERIÇÕES",
    TO_CHAR(DADOS.PRIMEIRA_AFERICAO, 'DD/MM/YYYY')                                      AS "DTA 1a AFERIÇÃO",
    TO_CHAR(DADOS.ULTIMA_AFERICAO, 'DD/MM/YYYY')                                        AS "DTA ÚLTIMA AFERIÇÃO",
    DADOS.TOTAL_DIAS                                                                    AS "DIAS ATIVO",
    ROUND(CASE
          WHEN (DADOS.TOTAL_DIAS > 0)
            THEN (DADOS.TOTAL_KM / (DADOS.TOTAL_DIAS)::NUMERIC)
          ELSE NULL::NUMERIC
          END)                                                                          AS "MÉDIA KM POR DIA",
    P.ALTURA_SULCO_INTERNO,
    P.ALTURA_SULCO_CENTRAL_INTERNO,
    P.ALTURA_SULCO_CENTRAL_EXTERNO,
    P.ALTURA_SULCO_EXTERNO,
    ROUND((DADOS.MAIOR_SULCO)::NUMERIC, 2)                                              AS "MAIOR MEDIÇÃO VIDA",
    ROUND((DADOS.MENOR_SULCO)::NUMERIC, 2)                                              AS "MENOR SULCO ATUAL",
    ROUND((DADOS.SULCO_GASTO)::NUMERIC, 2)                                              AS "MILIMETROS GASTOS",
    ROUND((DADOS.KM_POR_MM)::NUMERIC, 2)                                                AS "KMS POR MILIMETRO",
    ROUND(((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE))::NUMERIC)                          AS "KMS A PERCORRER",
    TRUNC(CASE
          WHEN ((DADOS.TOTAL_KM > (0)::NUMERIC)
                AND (DADOS.TOTAL_DIAS > 0)
                AND ((DADOS.TOTAL_KM / (DADOS.TOTAL_DIAS)::NUMERIC) > (0)::NUMERIC))
            THEN ((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE)
                  / ((DADOS.TOTAL_KM / (DADOS.TOTAL_DIAS)::NUMERIC))::DOUBLE PRECISION)
          ELSE (0)::DOUBLE PRECISION
          END)                                                                          AS "DIAS RESTANTES",
    CASE
    WHEN ((DADOS.TOTAL_KM > (0)::NUMERIC)
          AND (DADOS.TOTAL_DIAS > 0)
          AND ((DADOS.TOTAL_KM / (DADOS.TOTAL_DIAS)::NUMERIC) > (0)::NUMERIC))
      THEN ((((DADOS.KM_POR_MM * DADOS.SULCO_RESTANTE)
              / ((DADOS.TOTAL_KM / (DADOS.TOTAL_DIAS)::NUMERIC))::DOUBLE PRECISION))::INTEGER + ('NOW'::TEXT)::DATE)
    ELSE NULL::DATE
    END                                                                                 AS "PREVISÃO DE TROCA"
  FROM (((((PNEU P
    JOIN (SELECT AV.COD_PNEU,
            AV.COD_UNIDADE,
            COUNT(AV.ALTURA_SULCO_CENTRAL_INTERNO) AS QT_AFERICOES,
            (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE AS PRIMEIRA_AFERICAO,
            (MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE AS ULTIMA_AFERICAO,
            ((MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE
             - (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE) AS TOTAL_DIAS,
            MAX(TOTAL_KM.TOTAL_KM) AS TOTAL_KM,
            MAX(GREATEST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) AS MAIOR_SULCO,
            MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) AS MENOR_SULCO,
            (MAX(GREATEST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO))
             - MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO))) AS SULCO_GASTO,
            CASE
            WHEN (
              CASE
              WHEN (P.VIDA_ATUAL = P.VIDA_TOTAL)
                THEN (MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) - PRU.SULCO_MINIMO_DESCARTE)
              WHEN (P.VIDA_ATUAL < P.VIDA_TOTAL)
                THEN (MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) - PRU.SULCO_MINIMO_RECAPAGEM)
              ELSE NULL::REAL
              END < (0)::DOUBLE PRECISION) THEN (0)::REAL
            ELSE
              CASE
              WHEN (P.VIDA_ATUAL = P.VIDA_TOTAL)
                THEN (MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) - PRU.SULCO_MINIMO_DESCARTE)
              WHEN (P.VIDA_ATUAL < P.VIDA_TOTAL)
                THEN (MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)) - PRU.SULCO_MINIMO_RECAPAGEM)
              ELSE NULL::REAL
              END
            END AS SULCO_RESTANTE,
            CASE
            WHEN (((MAX(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE
                   - (MIN(A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)))::DATE) > 0)
              THEN (((MAX(TOTAL_KM.TOTAL_KM))::DOUBLE PRECISION
                     / MAX(GREATEST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)))
                    - MIN(LEAST(
                    AV.ALTURA_SULCO_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                    AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                    AV.ALTURA_SULCO_EXTERNO)))
            ELSE (0)::DOUBLE PRECISION
            END AS KM_POR_MM
          FROM ((((AFERICAO_VALORES AV
            JOIN AFERICAO A ON ((A.CODIGO = AV.COD_AFERICAO)))
            JOIN PNEU P ON ((((P.CODIGO)::TEXT = (AV.COD_PNEU)::TEXT)
                             AND ((P.STATUS)::TEXT = 'EM_USO'::TEXT))))
            JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.COD_UNIDADE = AV.COD_UNIDADE)
            JOIN (SELECT TOTAL_KM_RODADO.COD_PNEU,
                    TOTAL_KM_RODADO.COD_UNIDADE,
                    SUM(TOTAL_KM_RODADO.KM_RODADO) AS TOTAL_KM
                  FROM (SELECT AV_1.COD_PNEU,
                          AV_1.COD_UNIDADE,
                          A_1.PLACA_VEICULO,
                          (MAX(A_1.KM_VEICULO) - MIN(A_1.KM_VEICULO)) AS KM_RODADO
                        FROM (AFERICAO_VALORES AV_1
                          JOIN AFERICAO A_1 ON ((A_1.CODIGO = AV_1.COD_AFERICAO)))
                        GROUP BY AV_1.COD_PNEU, AV_1.COD_UNIDADE, A_1.PLACA_VEICULO) TOTAL_KM_RODADO
                  GROUP BY TOTAL_KM_RODADO.COD_PNEU, TOTAL_KM_RODADO.COD_UNIDADE) TOTAL_KM
              ON TOTAL_KM.COD_PNEU = AV.COD_PNEU AND TOTAL_KM.COD_UNIDADE = AV.COD_UNIDADE) -- TODO: Compara com cod_unidade ou não?
          GROUP BY AV.COD_PNEU, AV.COD_UNIDADE, P.VIDA_ATUAL, P.VIDA_TOTAL, PRU.SULCO_MINIMO_DESCARTE, PRU.SULCO_MINIMO_RECAPAGEM) DADOS
      ON DADOS.COD_PNEU = P.CODIGO)
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO)
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE)
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA)
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA);
COMMENT ON VIEW VIEW_ANALISE_PNEUS
IS 'View utilizada para gerar dados de uso sobre os pneus, esses dados são usados para gerar relatórios';

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION QUE RETORNA A PREVISÃO DE TROCA DOS PNEUS      #####################
-- ##################  RETORNA AGORA A UNIDADE DE CADA LINHA PARA A ESTRATIFICAÇÃO    #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
-- Essa function utiliza a VIEW_PNEU_ANALISE_VIDA_ATUAL como base, precisamos verificar o uso do NOW nessa view
-- para saber se o WHERE utilizado nessa function já está correto.
DROP FUNCTION func_relatorio_previsao_troca(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_status_pneu character varying);
CREATE OR REPLACE FUNCTION func_relatorio_previsao_troca(f_data_inicial date, f_data_final date, f_cod_unidade text[], f_status_pneu character varying)
  RETURNS TABLE(
    "UNIDADE ALOCADO" TEXT,
    "COD PNEU" TEXT,
    "STATUS" TEXT,
    "VIDA ATUAL" INTEGER,
    "MARCA" TEXT,
    "MODELO" TEXT,
    "MEDIDAS" TEXT,
    "QTD DE AFERIÇÕES" BIGINT,
    "DATA 1ª AFERIÇÃO" TEXT,
    "DATA ÚLTIMA AFERIÇÃO" TEXT,
    "DIAS ATIVO" INTEGER,
    "MÉDIA KM POR DIA" NUMERIC,
    "MAIOR MEDIÇÃO VIDA" NUMERIC,
    "MENOR SULCO ATUAL" NUMERIC,
    "MILÍMETROS GASTOS" NUMERIC,
    "KMS POR MILÍMETRO" NUMERIC,
    "VALOR VIDA" REAL,
    "VALOR ACUMULADO" REAL,
    "VALOR POR KM VIDA ATUAL" NUMERIC,
    "VALOR POR KM ACUMULADO" NUMERIC,
    "KMS A PERCORRER" NUMERIC,
    "DIAS RESTANTES" DOUBLE PRECISION,
    "PREVISÃO DE TROCA" TEXT,
    "DESTINO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  VAP."UNIDADE ALOCADO",
  VAP."COD PNEU CLIENTE",
  VAP."STATUS PNEU",
  VAP."VIDA ATUAL",
  VAP."MARCA",
  VAP."MODELO",
  VAP."MEDIDAS",
  VAP."QTD DE AFERIÇÕES",
  VAP."DTA 1a AFERIÇÃO",
  VAP."DTA ÚLTIMA AFERIÇÃO",
  VAP."DIAS ATIVO",
  VAP."MÉDIA KM POR DIA",
  VAP."MAIOR MEDIÇÃO VIDA",
  VAP."MENOR SULCO ATUAL",
  VAP."MILIMETROS GASTOS",
  VAP."KMS POR MILIMETRO",
  VAP.VALOR_VIDA_ATUAL,
  VAP.VALOR_ACUMULADO,
  VAP."VALOR POR KM",
  VAP."VALOR POR KM ACUMULADO" ,
  VAP."KMS A PERCORRER",
  VAP."DIAS RESTANTES",
  TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'),
  VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL AS VAP
WHERE VAP.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND VAP."PREVISÃO DE TROCA" BETWEEN f_data_inicial AND f_data_final
      AND VAP."STATUS PNEU" LIKE f_status_pneu
ORDER BY VAP."UNIDADE ALOCADO";
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  CRIA FUNCTION PARA BUSCAR O RELATORIO DE PREVISAO DE           #####################
-- ##################  TROCA DE PNEUS DE FORMA CONSOLIDADA POR UNIDADE                #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
CREATE OR REPLACE FUNCTION func_relatorio_pneu_previsao_troca_consolidado
  (f_cod_unidade text[], f_status_pneu text, f_data_inicial date, f_data_final date)
  RETURNS TABLE("UNIDADE" TEXT, DATA TEXT, MARCA TEXT, MODELO TEXT, MEDIDAS TEXT, "QUANTIDADE" BIGINT)
LANGUAGE SQL
AS $$
SELECT
  VAP."UNIDADE ALOCADO",
  TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY') AS DATA,
  VAP."MARCA",
  VAP."MODELO",
  VAP."MEDIDAS",
  COUNT(VAP."MODELO") AS QUANTIDADE
FROM VIEW_ANALISE_PNEUS VAP
WHERE VAP.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND VAP."PREVISÃO DE TROCA" BETWEEN f_data_inicial AND f_data_final
      AND VAP."STATUS PNEU" = f_status_pneu
GROUP BY VAP."UNIDADE ALOCADO", VAP."PREVISÃO DE TROCA", VAP."MARCA",  VAP."MODELO",  VAP."MEDIDAS"
ORDER BY VAP."UNIDADE ALOCADO", VAP."PREVISÃO DE TROCA" ASC, QUANTIDADE DESC;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_pneus_descartados(f_cod_unidade bigint, f_data_inicial date, f_data_final date);
CREATE OR REPLACE FUNCTION func_relatorio_pneus_descartados(f_cod_unidade text[], f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    "UNIDADE DO DESCARTE" TEXT,
    "RESPONSÁVEL PELO DESCARTE" TEXT,
    "DATA/HORA DO DESCARTE" TEXT,
    "CÓDIGO DO PNEU" TEXT,
    "MARCA DO PNEU" TEXT,
    "MODELO DO PNEU" TEXT,
    "MARCA DA BANDA" TEXT,
    "MODELO DA BANDA" TEXT,
    "DIMENSÃO DO PNEU" TEXT,
    "ÚLTIMA PRESSÃO" NUMERIC,
    "TOTAL DE VIDAS" INTEGER,
    "ALTURA SULCO INTERNO" NUMERIC,
    "ALTURA SULCO CENTRAL INTERNO" NUMERIC,
    "ALTURA SULCO CENTRAL EXTERNO" NUMERIC,
    "ALTURA SULCO EXTERNO" NUMERIC,
    "DOT" TEXT,
    "MOTIVO DO DESCARTE" TEXT,
    "FOTO 1" TEXT,
    "FOTO 2" TEXT,
    "FOTO 3" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                        AS "UNIDADE DO DESCARTE",
  C.NOME                        AS "RESPONSÁVEL PELO DESCARTE",
  TO_CHAR(MP.DATA_HORA AT TIME ZONE tz_unidade(P.COD_UNIDADE),
          'DD/MM/YYYY HH24:MI') AS "DATA/HORA DESCARTE",
  P.CODIGO_CLIENTE              AS "CÓDIGO DO PNEU",
  MAP.NOME                      AS "MARCA DO PNEU",
  MOP.NOME                      AS "MODELO DO PNEU",
  MAB.NOME                      AS "MARCA DA BANDA",
  MOB.NOME                      AS "MODELO DA BANDA",
  'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' || DP.ARO      AS "DIMENSÃO DO PNEU",
  ROUND(P.PRESSAO_ATUAL :: NUMERIC,2)                      AS "ÚLTIMA PRESSÃO",
  P.VIDA_ATUAL                  AS "TOTAL DE VIDAS",
  ROUND(P.ALTURA_SULCO_INTERNO :: NUMERIC,2)                      AS "ALTURA SULCO INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC,2)              AS "ALTURA SULCO CENTRAL INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC,2)              AS "ALTURA SULCO CENTRAL EXTERNO",
  ROUND(P.ALTURA_SULCO_EXTERNO :: NUMERIC,2)                      AS "ALTURA SULCO EXTERNO",
  P.DOT                         AS "DOT",
  MMDE.MOTIVO                   AS "MOTIVO DO DESCARTE",
  MD.URL_IMAGEM_DESCARTE_1      AS "FOTO 1",
  MD.URL_IMAGEM_DESCARTE_2      AS "FOTO 2",
  MD.URL_IMAGEM_DESCARTE_3      AS "FOTO 3"
FROM PNEU P
  JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
  JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
  LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
  LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
  LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
  LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
  LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
  LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
  LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
    ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
WHERE P.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND P.STATUS = 'DESCARTE'
      AND M.COD_PNEU = P.CODIGO
      AND MD.TIPO_DESTINO = 'DESCARTE'
      AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE))::DATE >= f_data_inicial
      AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE))::DATE <= f_data_final
ORDER BY U.NOME;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_pneu_resumo_geral_pneus(F_COD_UNIDADE BIGINT, F_STATUS_PNEU TEXT, F_TIME_ZONE TEXT);
CREATE OR REPLACE FUNCTION func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade text[], f_status_pneu text)
  RETURNS TABLE(
    "UNIDADE ALOCADO" TEXT,
    "PNEU" TEXT,
    "STATUS" TEXT,
    "VALOR DE AQUISIÇÃO" TEXT,
    "MARCA" TEXT,
    "MODELO" TEXT,
    "BANDA APLICADA" TEXT,
    "MEDIDAS" TEXT,
    "PLACA" TEXT,
    "TIPO" TEXT,
    "POSIÇÃO" TEXT,
    "QUANTIDADE DE SULCOS" TEXT,
    "SULCO INTERNO" TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO" TEXT,
    "PRESSÃO ATUAL (PSI)" TEXT,
    "PRESSÃO IDEAL (PSI)" TEXT,
    "VIDA ATUAL" TEXT,
    "DOT" TEXT,
    "ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME AS "UNIDADE ALOCADO",
  P.CODIGO_CLIENTE                                         AS COD_PNEU,
  P.STATUS AS STATUS,
  CASE WHEN P.VALOR IS NULL THEN '-' ELSE P.VALOR::TEXT END AS VALOR,
  MAP.NOME                                         AS NOME_MARCA_PNEU,
  MP.NOME                                          AS NOME_MODELO_PNEU,
  CASE WHEN MARB.CODIGO IS NULL
    THEN 'Nunca Recapado'
  ELSE MARB.NOME || ' - ' || MODB.NOME
  END                                              AS BANDA_APLICADA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                 AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                   AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                         AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                         AS POSICAO_PNEU,
  COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS)::TEXT   AS QTD_SULCOS,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')        AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO::NUMERIC, 2)::TEXT, '-') , '.', ',')AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO::NUMERIC, 2)::TEXT, '-') , '.', ',')       AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                    AS PRESSAO_ATUAL,
  P.PRESSAO_RECOMENDADA :: TEXT                    AS PRESSAO_RECOMENDADA,
  PVN.NOME :: TEXT                             AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                             AS DOT,
  COALESCE(
      TO_CHAR(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA),
              'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                          AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_UNIDADE = VT.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN (SELECT
               AV.COD_PNEU,
               A.COD_UNIDADE                  AS COD_UNIDADE_DATA,
               MAX(A.DATA_HORA)               AS ULTIMA_AFERICAO
             FROM AFERICAO A
               JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
             GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO
    ON DATA_ULTIMA_AFERICAO.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND CASE
          WHEN f_status_pneu IS NULL
            THEN TRUE
          ELSE P.STATUS = f_status_pneu
          END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_dados_ultima_afericao_pneu(f_cod_unidade bigint, f_time_zone_unidade text);
CREATE OR REPLACE function func_relatorio_dados_ultima_afericao_pneu(f_cod_unidade text[])
  RETURNS TABLE(
    "UNIDADE ALOCADO" TEXT,
    "PNEU" TEXT,
    "MARCA" TEXT,
    "MODELO" TEXT,
    "MEDIDAS" TEXT,
    "PLACA" TEXT,
    "TIPO" TEXT,
    "POSIÇÃO" TEXT,
    "SULCO INTERNO" TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO" TEXT,
    "PRESSÃO (PSI)" TEXT,
    "VIDA" TEXT,
    "DOT" TEXT,
    "ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                           AS "UNIDADE ALOCADO",
  P.CODIGO_CLIENTE                                 AS COD_PNEU,
  MAP.NOME                                         AS NOME_MARCA,
  MP.NOME                                          AS NOME_MODELO,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
   DP.ARO)                                         AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU,
           '-')                                    AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-') AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-') AS POSICAO_PNEU,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT,'-'), '.', ',') AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT,'-'), '.', ',') AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT,'-'), '.', ',') AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT,    '-') AS PRESSAO_ATUAL,
  P.VIDA_ATUAL :: TEXT                             AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                             AS DOT,
  COALESCE(TO_CHAR(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE tz_unidade(DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA),
                   'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                          AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_UNIDADE = VT.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN(SELECT
              AV.COD_PNEU,
              A.COD_UNIDADE                  AS COD_UNIDADE_DATA,
              MAX(A.DATA_HORA) AS ULTIMA_AFERICAO
            FROM AFERICAO A
              JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
            GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO
    ON DATA_ULTIMA_AFERICAO.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_pneu_aderencia_afericao(f_cod_unidade bigint, f_data_inicial date, f_data_final date);
CREATE OR REPLACE FUNCTION func_relatorio_pneu_aderencia_afericao(f_cod_unidade text[], f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    "UNIDADE ALOCADA" TEXT,
    "PLACA" CHARACTER VARYING,
    "QT AFERIÇÕES DE PRESSÃO" BIGINT,
    "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META" BIGINT,
    "ADERÊNCIA AFERIÇÕES DE PRESSÃO" TEXT,
    "QT AFERIÇÕES DE SULCO" BIGINT,
    "MAX DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "MIN DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "QTD AFERIÇÕES DE SULCO DENTRO DA META" BIGINT,
    "ADERÊNCIA AFERIÇÕES DE SULCO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME  AS "UNIDADE ALOCADA",
  V.PLACA AS PLACA,
  COALESCE(CALCULO_PRESSAO.QTD_AFERICOES, 0),
  COALESCE(CALCULO_PRESSAO.MAX_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.MIN_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.MD_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.QTD_AFERICOES_DENTRO_META, 0),
  COALESCE(CALCULO_PRESSAO.ADERENCIA, '0'),
  COALESCE(CALCULO_SULCO.QTD_AFERICOES, 0),
  COALESCE(CALCULO_SULCO.MAX_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.MIN_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.MD_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.QTD_AFERICOES_DENTRO_META, 0),
  COALESCE(CALCULO_SULCO.ADERENCIA, '0')
FROM VEICULO V
  JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
  LEFT JOIN (SELECT
               CALCULO_AFERICAO_PRESSAO.PLACA,
               COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)                              AS QTD_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                       AS MAX_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                       AS MIN_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN TRUNC(
                     CASE WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                       THEN
                         SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
                         SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
                           THEN 1 ELSE 0 END)
                     END)::TEXT
               ELSE '-' END                                                       AS MD_DIAS_ENTRE_AFERICOES,
               SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END)                                               AS QTD_AFERICOES_DENTRO_META,
               TRUNC(SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END) / COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)::NUMERIC * 100) || '%' AS ADERENCIA
             FROM
               (SELECT
                  A.PLACA_VEICULO            AS PLACA,
                  A.DATA_HORA,
                  A.TIPO_AFERICAO,
                  R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                  CASE WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                    THEN EXTRACT(DAYS FROM A.DATA_HORA - LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                  END                        AS DIAS_ENTRE_AFERICOES
                FROM AFERICAO A
                  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
                  JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >= (f_data_inicial)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (f_data_final)
                      AND (A.TIPO_AFERICAO = 'PRESSAO' OR A.TIPO_AFERICAO = 'SULCO_PRESSAO')
                ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
             GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO
    ON CALCULO_PRESSAO.PLACA = V.PLACA
  LEFT JOIN (SELECT
               CALCULO_AFERICAO_SULCO.PLACA,
               COUNT(CALCULO_AFERICAO_SULCO.PLACA)                              AS QTD_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                     AS MAX_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                     AS MIN_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN TRUNC(
                     CASE WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                       THEN
                         SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
                         SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
                           THEN 1 ELSE 0 END)
                     END) :: TEXT
               ELSE '-' END                                                     AS MD_DIAS_ENTRE_AFERICOES,
               SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END)                                                  AS QTD_AFERICOES_DENTRO_META,
               TRUNC(SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END) / COUNT(CALCULO_AFERICAO_SULCO.PLACA)::NUMERIC * 100) || '%' AS ADERENCIA
             FROM
               (SELECT
                  A.PLACA_VEICULO            AS PLACA,
                  A.DATA_HORA,
                  A.TIPO_AFERICAO,
                  R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                  CASE WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                    THEN EXTRACT(DAYS FROM A.DATA_HORA - LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                  ELSE 0
                  END                        AS DIAS_ENTRE_AFERICOES
                FROM AFERICAO A
                  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
                  JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >= (f_data_inicial)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (f_data_final)
                      AND (A.TIPO_AFERICAO = 'SULCO' OR A.TIPO_AFERICAO = 'SULCO_PRESSAO')
                ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
             GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
    ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade bigint, f_data_inicial date, f_data_final date);
CREATE OR REPLACE FUNCTION func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade text[], f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    "UNIDADE DO SERVIÇO" TEXT,
    "DATA AFERIÇÃO" TEXT,
    "DATA RESOLUÇÃO" TEXT,
    "HORAS PARA RESOLVER" DOUBLE PRECISION,
    "MINUTOS PARA RESOLVER" DOUBLE PRECISION,
    "PLACA" TEXT,
    "KM AFERIÇÃO" BIGINT,
    "KM CONSERTO" BIGINT,
    "KM PERCORRIDO" BIGINT,
    "COD PNEU" CHARACTER VARYING,
    "PRESSÃO RECOMENDADA" REAL,
    "PRESSÃO AFERIÇÃO" TEXT,
    "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
    "PRESSÃO INSERIDA" TEXT,
    "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
    "POSIÇÃO" TEXT,
    "SERVIÇO" TEXT,
    "MECÂNICO" TEXT,
    "PROBLEMA APONTADO (INSPEÇÃO)" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                                      AS "UNIDADE DO SERVIÇO",
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MM:SS')                     AS DATA_HORA_AFERICAO,
  TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MM:SS')          AS DATA_HORA_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA ))) / 3600)                               AS HORAS_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA ))) / 60)                                 AS MINUTOS_RESOLUCAO,
  A.PLACA_VEICULO,
  A.KM_VEICULO                                                                                                AS KM_AFERICAO,
  AM.KM_MOMENTO_CONSERTO,
  AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                                                       AS KM_PERCORRIDO,
  P.CODIGO_CLIENTE,
  P.PRESSAO_RECOMENDADA,
  REPLACE(ROUND(AV.PSI::NUMERIC, 2)::TEXT, '.', ',')                                                          AS PSI_AFERICAO,
  REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ',')               AS DISPERSAO_PRESSAO_ANTES,
  REPLACE(ROUND(AM.PSI_APOS_CONSERTO::NUMERIC, 2)::TEXT, '.', ',')                                            AS PSI_POS_CONSERTO,
  REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ',') AS DISPERSAO_PRESSAO_DEPOIS,
  COALESCE(PON.NOMENCLATURA, '-')                                                                             AS POSICAO,
  AM.TIPO_SERVICO,
  INITCAP(C.NOME)                                                                                             AS NOME_MECANICO,
  COALESCE(AA.ALTERNATIVA, '-')                                                                               AS PROBLEMA_APONTADO
FROM AFERICAO_MANUTENCAO AM
  JOIN UNIDADE U
    ON AM.COD_UNIDADE = U.CODIGO
  JOIN AFERICAO_VALORES AV
    ON AM.COD_UNIDADE = AV.COD_UNIDADE
       AND AM.COD_AFERICAO = AV.COD_AFERICAO
       AND AM.COD_PNEU = AV.COD_PNEU
  JOIN AFERICAO A
    ON A.CODIGO = AV.COD_AFERICAO
  JOIN COLABORADOR C
    ON AM.CPF_MECANICO = C.CPF
  JOIN PNEU P
    ON P.CODIGO = AV.COD_PNEU
  JOIN VEICULO_PNEU VP
    ON VP.COD_PNEU = P.CODIGO
       AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
    ON AA.CODIGO = AM.COD_ALTERNATIVA
  JOIN VEICULO V
    ON V.PLACA = VP.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
    ON PON.COD_UNIDADE = P.COD_UNIDADE
       AND PON.COD_TIPO_VEICULO = V.COD_TIPO
       AND PON.POSICAO_PROLOG = AV.POSICAO
WHERE AV.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE >= f_data_inicial
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE <= f_data_final
ORDER BY U.NOME, A.DATA_HORA DESC
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ##################  ALTERA FUNCTION PARA MOSTRAR A UNIDADE DO DADO LISTADO         #####################
-- ########################################################################################################
-- ########################################################################################################
-- REVIEW OK - LUIZ
DROP FUNCTION func_relatorio_pneu_extrato_servicos_abertos(f_cod_unidade bigint, f_data_inicial date,
f_data_final date, f_data_atual date, f_time_zone text);
CREATE OR REPLACE FUNCTION func_relatorio_pneu_extrato_servicos_abertos(f_cod_unidade text[], f_data_inicial date,
                                                                        f_data_final date, f_data_atual date)
  RETURNS TABLE(
    "UNIDADE DO SERVIÇO" TEXT,
    "CÓDIGO DO SERVIÇO" BIGINT,
    "TIPO DO SERVIÇO" TEXT,
    "QTD APONTAMENTOS" INTEGER,
    "DATA HORA ABERTURA" TEXT,
    "QTD DIAS EM ABERTO" TEXT,
    "NOME DO COLABORADOR" TEXT,
    "PLACA" TEXT,
    "AFERIÇÃO" BIGINT,
    "PNEU" TEXT,
    "SULCO INTERNO" REAL,
    "SULCO CENTRAL INTERNO" REAL,
    "SULCO CENTRAL EXTERNO" REAL,
    "SULCO EXTERNO" REAL,
    "PRESSÃO (PSI)" REAL,
    "PRESSÃO RECOMENDADA (PSI)" REAL,
    "POSIÇÃO DO PNEU" TEXT,
    "ESTADO ATUAL" TEXT,
    "MÁXIMO DE RECAPAGENS" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME AS "UNIDADE DO SERVIÇO",
  AM.CODIGO AS CODIGO_SERVICO,
  AM.TIPO_SERVICO,
  AM.QT_APONTAMENTOS,
  TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI')::TEXT AS DATA_HORA_ABERTURA,
  (SELECT (EXTRACT(EPOCH FROM AGE(f_data_atual, A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))) / 86400)::INTEGER)::TEXT AS DIAS_EM_ABERTO,
  C.NOME AS NOME_COLABORADOR,
  A.PLACA_VEICULO AS PLACA_VEICULO,
  A.CODIGO AS COD_AFERICAO,
  P.CODIGO_CLIENTE AS COD_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA,
  COALESCE(PONU.NOMENCLATURA, '-') AS POSICAO_PNEU_PROBLEMA,
  PVN.NOME AS VIDA_PNEU_PROBLEMA,
  PRN.NOME AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P
    ON AM.COD_PNEU = P.CODIGO
  JOIN AFERICAO A
    ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C
    ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV
    ON AV.COD_AFERICAO = AM.COD_AFERICAO
       AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U
    ON U.CODIGO = AM.COD_UNIDADE
  JOIN PNEU_VIDA_NOMENCLATURA PVN
    ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
    ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
  JOIN VEICULO V
    ON A.PLACA_VEICULO = V.PLACA
       AND V.COD_UNIDADE = A.COD_UNIDADE
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON AM.COD_UNIDADE = PONU.COD_UNIDADE
       AND AV.POSICAO = PONU.POSICAO_PROLOG
       AND V.COD_TIPO = PONU.COD_TIPO_VEICULO
WHERE AM.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE >= f_data_inicial
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE <= f_data_final
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;

END TRANSACTION;