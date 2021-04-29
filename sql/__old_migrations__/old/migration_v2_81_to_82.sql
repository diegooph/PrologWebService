BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--#######################################   MIGRATION DAS INFORMAÇÕES ANTIGAS   ########################################
--######################################        SOBRE ABERTURA DE SERVIÇOS       #######################################
--######################################################################################################################
--######################################################################################################################
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD COLUMN DEVE_ABRIR_ORDEM_SERVICO BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG ADD COLUMN DEVE_ABRIR_ORDEM_SERVICO BOOLEAN NOT NULL DEFAULT TRUE;

--######################################################################################################################
--######################################################################################################################
-- FUNCTION PARA BUSCAR ALTERNATIVAS DE UM MODELO DE CHECKLIST QUE PODE ABRIR ORDEM DE SERVIÇO E TAMBÉM
-- A QUANTIDADE DE APONTAMENTOS E A ORDEM DE SERVIÇO CASO A ALTERNATIVA JÁ POSSUA ALGUM ITEM PENDENTE.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(
  F_COD_MODELO_CHECKLIST BIGINT,
  F_PLACA_VEICULO        TEXT)
  RETURNS TABLE(
    COD_ALTERNATIVA          BIGINT,
    COD_ITEM_ORDEM_SERVICO   BIGINT,
    DEVE_ABRIR_ORDEM_SERVICO BOOLEAN,
    QTD_APONTAMENTOS_ITEM    INTEGER)
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITEM_PENDENTE TEXT = 'P';
BEGIN
  RETURN QUERY
  WITH ITENS_PENDENTES AS (
      SELECT
        COSI.CODIGO            AS COD_ITEM_ORDEM_SERVICO,
        COSI.COD_ALTERNATIVA   AS COD_ALTERNATIVA,
        COSI.QT_APONTAMENTOS   AS QTD_APONTAMENTOS_ITEM,
        C.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO
      FROM CHECKLIST C
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON C.CODIGO = COS.COD_CHECKLIST
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
             AND COS.COD_UNIDADE = COSI.COD_UNIDADE
      WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
            AND C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
            AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_PENDENTE
  )


  SELECT
    CAP.CODIGO                   AS COD_ALTERNATIVA,
    IP.COD_ITEM_ORDEM_SERVICO    AS COD_ITEM_ORDEM_SERVICO,
    CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO,
    IP.QTD_APONTAMENTOS_ITEM     AS QTD_APONTAMENTOS_ITEM
  FROM CHECKLIST_MODELO CM
    JOIN CHECKLIST_PERGUNTAS CP
      ON CP.COD_CHECKLIST_MODELO = CM.CODIGO
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON CAP.COD_PERGUNTA = CP.CODIGO
    LEFT JOIN ITENS_PENDENTES IP
      ON IP.COD_ALTERNATIVA = CAP.CODIGO
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--####################################### CRIA TABELAS PARA NOVA FUNCIONALIDADE ########################################
--######################################          TRANSFERÊNCIA DE PNEUS         #######################################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE PNEU_TRANSFERENCIA_PROCESSO (
  CODIGO                           BIGSERIAL                NOT NULL,
  COD_UNIDADE_ORIGEM               BIGINT                   NOT NULL,
  COD_UNIDADE_DESTINO              BIGINT                   NOT NULL,
  COD_UNIDADE_COLABORADOR          BIGINT                   NOT NULL,
  COD_COLABORADOR                  BIGINT                   NOT NULL,
  DATA_HORA_TRANSFERENCIA_PROCESSO TIMESTAMP WITH TIME ZONE NOT NULL,
  OBSERVACAO                       VARCHAR,
  CONSTRAINT PK_PNEU_TRANSFERENCIA_PROCESSO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_UNIDADE_ORIGEM FOREIGN KEY (COD_UNIDADE_ORIGEM) REFERENCES UNIDADE (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_UNIDADE_DESTINO FOREIGN KEY (COD_UNIDADE_DESTINO) REFERENCES UNIDADE (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_COLABORADOR FOREIGN KEY (COD_COLABORADOR) REFERENCES COLABORADOR (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_UNIDADE_COLABORADOR FOREIGN KEY (COD_UNIDADE_COLABORADOR) REFERENCES COLABORADOR (CODIGO),
  CONSTRAINT UNIDADE_ORIGEM_DESTINO_DIFERENTES CHECK(COD_UNIDADE_ORIGEM != COD_UNIDADE_DESTINO)
);

CREATE TABLE PNEU_TRANSFERENCIA_INFORMACOES (
  CODIGO                       BIGSERIAL NOT NULL,
  COD_TRANSFERENCIA            BIGINT    NOT NULL,
  COD_PNEU                     BIGINT    NOT NULL,
  ALTURA_SULCO_INTERNO         REAL      NOT NULL,
  ALTURA_SULCO_CENTRAL_INTERNO REAL      NOT NULL,
  ALTURA_SULCO_CENTRAL_EXTERNO REAL      NOT NULL,
  ALTURA_SULCO_EXTERNO         REAL      NOT NULL,
  PSI                          REAL      NOT NULL,
  VIDA_MOMENTO_TRANSFERENCIA   INTEGER   NOT NULL,
  CONSTRAINT PK_PNEU_TRANSFERENCIA_INFORMACOES PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_INFORMACOES_PNEU_TRANSFERENCIA_PROCESSO FOREIGN KEY (COD_TRANSFERENCIA) REFERENCES PNEU_TRANSFERENCIA_PROCESSO (CODIGO),
  CONSTRAINT FK_PNEU_TRANSFERENCIA_INFORMACOES_PNEU FOREIGN KEY (COD_PNEU) REFERENCES PNEU (CODIGO)
);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################## CRIA FUNCTION PARA INSERIR INFORMAÇÕES DOS PNEUS TRANSFERIDOS ###############################
--######################################    EM PNEU_TRANSFERENCIA_INFORMACOES    #######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(
  F_COD_TRANSFERENCIA BIGINT,
  F_COD_PNEUS         BIGINT []
)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_ROWS BIGINT;
BEGIN
  WITH INFORMACOES_PNEUS AS (
      SELECT
        CODIGO,
        ALTURA_SULCO_INTERNO,
        ALTURA_SULCO_CENTRAL_INTERNO,
        ALTURA_SULCO_CENTRAL_EXTERNO,
        ALTURA_SULCO_EXTERNO,
        PRESSAO_ATUAL,
        VIDA_ATUAL
      FROM PNEU
      WHERE CODIGO = ANY (F_COD_PNEUS)
  )

  INSERT INTO PNEU_TRANSFERENCIA_INFORMACOES (
    COD_TRANSFERENCIA,
    COD_PNEU,
    ALTURA_SULCO_INTERNO,
    ALTURA_SULCO_CENTRAL_INTERNO,
    ALTURA_SULCO_CENTRAL_EXTERNO,
    ALTURA_SULCO_EXTERNO,
    PSI,
    VIDA_MOMENTO_TRANSFERENCIA)
    SELECT
      F_COD_TRANSFERENCIA,
      IP.CODIGO,
      IP.ALTURA_SULCO_INTERNO,
      IP.ALTURA_SULCO_CENTRAL_INTERNO,
      IP.ALTURA_SULCO_CENTRAL_EXTERNO,
      IP.ALTURA_SULCO_EXTERNO,
      IP.PRESSAO_ATUAL,
      IP.VIDA_ATUAL
    FROM INFORMACOES_PNEUS IP;
  GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;
  RETURN QTD_ROWS;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################# CRIA FUNCTION PARA LISTAR TRANSFERENCIAS DE PNEUS ##################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_LISTAGEM(
  F_COD_UNIDADES_ORIGEM  BIGINT [],
  F_COD_UNIDADES_DESTINO BIGINT [],
  F_DATA_INICIAL         DATE,
  F_DATA_FINAL           DATE)
  RETURNS TABLE(
    COD_TRANSFERENCIA       BIGINT,
    REGIONAL_ORIGEM         TEXT,
    UNIDADE_ORIGEM          TEXT,
    REGIONAL_DESTINO        TEXT,
    UNIDADE_DESTINO         TEXT,
    NOME_COLABORADOR        TEXT,
    DATA_HORA_TRANSFERENCIA TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO              TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH REGIONAL_ORIGEM AS (
      SELECT
        R.REGIAO AS REGIAO_ORIGEM,
        U.NOME   AS UNIDADE_ORIGEM,
        U.CODIGO AS COD_UNIDADE_ORIGEM
      FROM REGIONAL R
        JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_ORIGEM)
  ),

      REGIONAL_DESTINO AS (
        SELECT
          R.REGIAO AS REGIAO_DESTINO,
          U.NOME   AS UNIDADE_DESTINO,
          U.CODIGO AS COD_UNIDADE_DESTINO
        FROM REGIONAL R
          JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
        WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO)
    )

  SELECT
    PTP.CODIGO                 AS COD_TRANSFERENCIA,
    RO.REGIAO_ORIGEM :: TEXT   AS REGIAO_ORIGEM,
    RO.UNIDADE_ORIGEM :: TEXT  AS UNIDADE_ORIGEM,
    RD.REGIAO_DESTINO :: TEXT  AS REGIAO_DESTINO,
    RD.UNIDADE_DESTINO :: TEXT AS UNIDADE_DESTINO,
    CO.NOME :: TEXT            AS NOME_COLABORADOR,
    PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR),
    PTP.OBSERVACAO :: TEXT
  FROM PNEU_TRANSFERENCIA_PROCESSO PTP
    JOIN REGIONAL_ORIGEM RO ON RO.COD_UNIDADE_ORIGEM = PTP.COD_UNIDADE_ORIGEM
    JOIN REGIONAL_DESTINO RD ON RD.COD_UNIDADE_DESTINO = PTP.COD_UNIDADE_DESTINO
    JOIN COLABORADOR CO ON CO.CODIGO = PTP.COD_COLABORADOR
  WHERE
    PTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES_ORIGEM) AND
    PTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES_DESTINO) AND
    (DATA_HORA_TRANSFERENCIA_PROCESSO AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR)) :: DATE
    BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
  ORDER BY DATA_HORA_TRANSFERENCIA_PROCESSO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################ CRIA FUNCTION PARA VISUALIZAÇÃO DE TRANSFERENCIA DE PNEUS ###############################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(
  F_COD_TRANSFERENCIA BIGINT)
  RETURNS TABLE(
    COD_TRANSFERENCIA       BIGINT,
    REGIONAL_ORIGEM         TEXT,
    UNIDADE_ORIGEM          TEXT,
    REGIONAL_DESTINO        TEXT,
    UNIDADE_DESTINO         TEXT,
    NOME_COLABORADOR        TEXT,
    DATA_HORA_TRANSFERENCIA TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO              TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH TRANSFERENCIA_PROCESSO AS (
      SELECT
        PTP.CODIGO                           AS COD_TRANSFERENCIA,
        RO.REGIAO                            AS REGIONAL_ORIGEM,
        PTP.COD_UNIDADE_ORIGEM               AS COD_UNIDADE_ORIGEM,
        RD.REGIAO                            AS REGIONAL_DESTINO,
        UD.NOME                              AS UNIDADE_DESTINO,
        UO.NOME                              AS UNIDADE_ORIGEM,
        PTP.COD_UNIDADE_DESTINO              AS COD_UNIDADE_DESTINO,
        PTP.COD_UNIDADE_COLABORADOR          AS COD_UNIDADE_COLABORADOR,
        CO.NOME                              AS NOME_COLABORADOR,
        PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AS DATA_HORA_TRANSFERENCIA_PROCESSO,
        PTP.OBSERVACAO                       AS OBSERVACAO
      FROM PNEU_TRANSFERENCIA_PROCESSO PTP
        JOIN COLABORADOR CO ON PTP.COD_COLABORADOR = CO.CODIGO
        JOIN UNIDADE UO ON UO.CODIGO = PTP.COD_UNIDADE_ORIGEM
        JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
        JOIN UNIDADE UD ON UD.CODIGO = PTP.COD_UNIDADE_DESTINO
        JOIN REGIONAL RD ON UD.COD_REGIONAL = RO.CODIGO
      WHERE PTP.CODIGO = F_COD_TRANSFERENCIA
  )

  SELECT
    TP.COD_TRANSFERENCIA                           AS COD_TRANSFERENCIA,
    TP.REGIONAL_ORIGEM :: TEXT                     AS REGIAO_ORIGEM,
    TP.UNIDADE_ORIGEM :: TEXT                      AS UNIDADE_ORIGEM,
    TP.REGIONAL_DESTINO :: TEXT                    AS REGIONAL_DESTINO,
    TP.UNIDADE_DESTINO :: TEXT                     AS UNIDADE_DESTINO,
    TP.NOME_COLABORADOR :: TEXT                    AS NOME_COLABORADOR,
    TP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(TP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_TRANSFERENCIA_PROCESSO,
    TP.OBSERVACAO :: TEXT                          AS OBSERVACAO
  FROM TRANSFERENCIA_PROCESSO TP
  WHERE
    TP.COD_TRANSFERENCIA = F_COD_TRANSFERENCIA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### NOVA COLUNA TABELA VEICULO ###############################################
--######################################################################################################################
--######################################################################################################################
ALTER TABLE VEICULO
  ADD COLUMN CODIGO BIGSERIAL UNIQUE NOT NULL;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################# CRIA PERMISSÃO PARA TRANSFERÊNCIAS DE PNEUS E VEÍCULOS #################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR)
VALUES (141, 'Geral - Transferir pneus e veículos entre unidades', 1);

-- LIBERA ESSA PERMISSÃO PARA QUEM PODE EDITAR PNEUS E TENHA NÍVEL DE ACESSO > 1.
INSERT INTO PUBLIC.CARGO_FUNCAO_PROLOG_V11 (
  COD_UNIDADE,
  COD_FUNCAO_COLABORADOR,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
  SELECT
    CFP.COD_UNIDADE,
    CFP.COD_FUNCAO_COLABORADOR,
    141,
    1
  FROM CARGO_FUNCAO_PROLOG_V11 CFP
    JOIN COLABORADOR C
      ON C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
         AND C.COD_UNIDADE = CFP.COD_UNIDADE
  WHERE C.COD_PERMISSAO > 1
  GROUP BY CFP.COD_UNIDADE, CFP.COD_FUNCAO_COLABORADOR
  HAVING '{17}' <@ ARRAY_AGG(COD_FUNCAO_PROLOG)
  ORDER BY CFP.COD_UNIDADE, CFP.COD_FUNCAO_COLABORADOR;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ CRIA FUNCTION PARA REALIZAR UPDATE NA TABELA PNEU ###################################
--################################# QUE ALTERA A UNIDADE DE ALOCAÇÃO - TRANSFERÊNCIA ###################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(
  F_COD_UNIDADE_ORIGEM  BIGINT,
  F_COD_UNIDADE_DESTINO BIGINT,
  F_COD_PNEUS           BIGINT []
)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_ROWS BIGINT;
BEGIN
  UPDATE PNEU
  SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM AND CODIGO = ANY (F_COD_PNEUS);
  GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;
  RETURN QTD_ROWS;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################## INSERE NOVO TIPO DE DIAGRAMA ##########################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO VEICULO_DIAGRAMA (CODIGO, NOME, URL_IMAGEM) VALUES (11, 'TRUCK SINGLE', 'WWW.GOOGLE.COM/TRUCK-SINGLE');
--######################################################################################################################
--######################################################################################################################

END TRANSACTION;