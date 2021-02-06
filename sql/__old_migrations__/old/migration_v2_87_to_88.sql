BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--################################  TABELA PARA SALVAR TOKEN E VERSÃO DOS DADOS  #######################################
--######################################################################################################################
--######################################################################################################################

-- CRIA TABELA PARA ARMAZENAR O TOKEN E A VERSÃO DE DADOS DE CADA UNIDADE QUE UTILIZA O CHECKLIST OFFLINE.
CREATE TABLE IF NOT EXISTS CHECKLIST_OFFLINE_DADOS_UNIDADE (
  COD_UNIDADE BIGINT NOT NULL,
  VERSAO_DADOS BIGINT NOT NULL DEFAULT 1,
  TOKEN_SINCRONIZACAO_CHECKLIST TEXT NOT NULL,
  CONSTRAINT PK_CHECKLIST_OFFLINE_DADOS_UNIDADE PRIMARY KEY (COD_UNIDADE),
  CONSTRAINT FK_CHECKLIST_OFFLINE_DADOS_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT UNIQUE_TOKEN_SINCRONIZACAO_CHECKLIST UNIQUE (TOKEN_SINCRONIZACAO_CHECKLIST)
);
COMMENT ON TABLE CHECKLIST_OFFLINE_DADOS_UNIDADE
IS 'Esta tabela contém os dados de controle para possibilitar o checklist offline';

--######################################################################################################################
--######################################################################################################################
--#####################  TABELA PARA BLOQUEAR EMPRESAS DE REALIZAR CHECKLIST  OFFLINE  #################################
--######################################################################################################################
--######################################################################################################################

-- CRIA TABELA PARA ARMAZENAR AS EMPRESAS QUE POSSUEM O CHECKLIST OFFLINE BLOQUEADO.
CREATE TABLE IF NOT EXISTS CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA (
  COD_EMPRESA BIGINT NOT NULL,
  CONSTRAINT PK_CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA PRIMARY KEY (COD_EMPRESA),
  CONSTRAINT FK_CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO)
);
COMMENT ON TABLE CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA
IS 'Essa tabela contém as empresas em que o checklist offline não é permitido';

-- INICIALMENTE IREMOS BLOQUEAR O CHECKLIST OFFLINE PARA TODAS AS EMPRESAS, LIBERANDO APENAS A
-- Andrade Transportes (CÓDIGO 28), QUE SERÁ A PRIMEIRA EMPRESA A TESTAR O CHECKLIST OFFLINE.
INSERT INTO CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA (COD_EMPRESA)
  SELECT CODIGO FROM EMPRESA;
DELETE FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA WHERE COD_EMPRESA = 28;

-- Cria um token de sincronia de checklist para a unidade de Bom Retiro (140) da empresa Andrade (28).
CREATE OR REPLACE FUNCTION F_RANDOM_STRING(LENGTH INTEGER)
  RETURNS TEXT AS
$$
DECLARE
  CHARS  TEXT [] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  RESULT TEXT := '';
  I      INTEGER := 0;
BEGIN
  IF LENGTH < 0
  THEN
    RAISE EXCEPTION 'Given length cannot be less than 0!';
  END IF;
  FOR I IN 1..LENGTH LOOP
    RESULT := RESULT || CHARS [CEIL(61 * RANDOM())];
  END LOOP;
  RETURN RESULT;
END;
$$
LANGUAGE PLPGSQL;

INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE(COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
SELECT
  140,
  (SELECT F_RANDOM_STRING(64));

--######################################################################################################################
--######################################################################################################################
--#######################    INSERE INFORMAÇÕES EXTRAS NA REALIZAÇÃO DO CHECKLIST    ###################################
--######################################################################################################################
--######################################################################################################################

-- ADICIONA DATA HORA DE SINCRONIZAÇÃO. PARA AS ANTIGAS, COPIA A DATA_HORA_REALIZACAO.
ALTER TABLE CHECKLIST ADD DATA_HORA_SINCRONIZACAO TIMESTAMP WITH TIME ZONE;
-- INSERE A DATA DE SINCORNIZAÇÃO COMO A DATA_HORA EM QUE FOI REALIZADO.
UPDATE CHECKLIST SET DATA_HORA_SINCRONIZACAO = DATA_HORA;
ALTER TABLE CHECKLIST ALTER COLUMN DATA_HORA_SINCRONIZACAO SET NOT NULL;

-- ADICIONA A FONTE DA DATA_HORA EM QUE O CHECKLIST FOI REALIZADO.
ALTER TABLE CHECKLIST ADD FONTE_DATA_HORA_REALIZACAO TEXT;
UPDATE CHECKLIST SET FONTE_DATA_HORA_REALIZACAO = 'SERVIDOR';
ALTER TABLE CHECKLIST ALTER COLUMN FONTE_DATA_HORA_REALIZACAO SET NOT NULL;
ALTER TABLE CHECKLIST
  ADD CONSTRAINT CHECK_FONTE_DATA_HORA_REALIZACAO CHECK (FONTE_DATA_HORA_REALIZACAO = 'SERVIDOR'
                                                         OR FONTE_DATA_HORA_REALIZACAO = 'REDE_CELULAR'
                                                         OR FONTE_DATA_HORA_REALIZACAO = 'LOCAL_CELULAR');

-- ADICIONA NO CHECKLIST A VERSÃO QUE O APP TINHA QUANDO O CHECKLIST FOI REALIZADO E QUANDO FOI SINCRONIZADO.
ALTER TABLE CHECKLIST ADD COLUMN VERSAO_APP_MOMENTO_REALIZACAO BIGINT;
ALTER TABLE CHECKLIST ADD COLUMN VERSAO_APP_MOMENTO_SINCRONIZACAO BIGINT;

-- COLUNA PARA IDENTIFCAR O DEVICE_ID DO APARELHO QUE REALIZOU CADA CHECKLIST.
ALTER TABLE CHECKLIST ADD COLUMN DEVICE_ID TEXT;

-- COLUNA PARA IDENTIFCAR O DEVICE_ID DO APARELHO QUE REALIZOU CADA CHECKLIST.
ALTER TABLE CHECKLIST ADD COLUMN DEVICE_IMEI TEXT;

-- ADICIONA COLUNAS PARA SALVAR O TEMPO QUE O APARELHO ESTAVA LIGADO NO MOMENTO DA REALIZACAO E DA SINCRONIZAÇÃO.
ALTER TABLE CHECKLIST ADD COLUMN DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT;
ALTER TABLE CHECKLIST ADD COLUMN DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT;

-- ADICIONA COLUNA PARA FACIALMENTE IDENTIFICARMOS SE UM CHECKLIST FOI FEITO OFFLINE.
ALTER TABLE CHECKLIST ADD COLUMN FOI_OFFLINE BOOLEAN;
UPDATE CHECKLIST SET FOI_OFFLINE = FALSE;
ALTER TABLE CHECKLIST ALTER COLUMN FOI_OFFLINE SET NOT NULL;

-- CRIA CONSTRAINT UNIQUE PARA EVITAR CHECKLISTS DUPLICADOS
ALTER TABLE CHECKLIST ADD CONSTRAINT UNIQUE_CHECKLIST
UNIQUE (COD_UNIDADE,
        COD_CHECKLIST_MODELO,
        DATA_HORA,
        CPF_COLABORADOR,
        PLACA_VEICULO,
        TIPO,
        TEMPO_REALIZACAO,
        KM_VEICULO,
        FONTE_DATA_HORA_REALIZACAO,
        VERSAO_APP_MOMENTO_REALIZACAO,
        DEVICE_ID,
        DEVICE_IMEI,
        DEVICE_UPTIME_REALIZACAO_MILLIS);

--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR INFORMAÇÃO SE A EMPRESA ESTÁ  ########################################
--#######################         LIBERADA PARA REALIZAR CHECKLIST OFFLINE      ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_EMPRESA_LIBERADA(F_COD_EMPRESA BIGINT)
  RETURNS BOOLEAN
LANGUAGE SQL
AS $$
SELECT NOT EXISTS(
    SELECT COEB.COD_EMPRESA
    FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA COEB
    WHERE COEB.COD_EMPRESA = F_COD_EMPRESA)
$$;

--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR OS MODELOS DE CHECKLIST COM   ########################################
--#######################         AS PERGUNTAS E ALTERNATIVAS ASSOCIADAS        ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_MODELOS_DISPONIVEIS(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_UNIDADE_MODELO_CHECKLIST BIGINT,
    COD_MODELO_CHECKLIST         BIGINT,
    NOME_MODELO_CHECKLIST        TEXT,
    COD_PERGUNTA                 BIGINT,
    DESCRICAO_PERGUNTA           TEXT,
    COD_IMAGEM                   BIGINT,
    URL_IMAGEM                   TEXT,
    PERGUNTA_ORDEM_EXIBICAO      INTEGER,
    SINGLE_CHOICE                BOOLEAN,
    COD_ALTERNATIVA              BIGINT,
    DESCRICAO_ALTERNATIVA        TEXT,
    TIPO_OUTROS                  BOOLEAN,
    ALTERNATIVA_ORDEM_EXIBICAO   INTEGER,
    PRIORIDADE_ALTERNATIVA       TEXT,
    COD_CARGO                    BIGINT,
    COD_TIPO_VEICULO             BIGINT
  )
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH CHECKLIST_MODELO_ATIVO AS (
      SELECT
        CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
        CM.CODIGO                   AS COD_MODELO_CHECKLIST,
        CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
        CP.CODIGO                   AS COD_PERGUNTA,
        CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
        CP.COD_IMAGEM               AS COD_IMAGEM,
        CGI.URL_IMAGEM              AS URL_IMAGEM,
        CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
        CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
        CAP.CODIGO                  AS COD_ALTERNATIVA,
        CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
        CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
        CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
        CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
        NULL :: BIGINT              AS COD_CARGO,
        NULL :: BIGINT              AS COD_TIPO_VEICULO
      FROM CHECKLIST_MODELO CM
        JOIN CHECKLIST_PERGUNTAS CP
          ON CM.CODIGO = CP.COD_CHECKLIST_MODELO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CP.CODIGO = CAP.COD_PERGUNTA
        -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
        LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
          ON CP.COD_IMAGEM = CGI.COD_IMAGEM
      WHERE CM.COD_UNIDADE = F_COD_UNIDADE
            AND CM.STATUS_ATIVO
            AND CP.STATUS_ATIVO
            AND CAP.STATUS_ATIVO
  ),

      CHECKLIST_MODELO_CARGO AS (
        SELECT
          NULL :: BIGINT           AS COD_UNIDADE_MODELO_CHECKLIST,
          CMF.COD_CHECKLIST_MODELO AS COD_MODELO_CHECKLIST,
          NULL :: TEXT             AS NOME_MODELO_CHECKLIST,
          NULL :: BIGINT           AS COD_PERGUNTA,
          NULL :: TEXT             AS DESCRICAO_PERGUNTA,
          NULL :: BIGINT           AS COD_IMAGEM,
          NULL :: TEXT             AS URL_IMAGEM,
          NULL :: INTEGER          AS PERGUNTA_ORDEM_EXIBICAO,
          NULL :: BOOLEAN          AS SINGLE_CHOICE,
          NULL :: BIGINT           AS COD_ALTERNATIVA,
          NULL :: TEXT             AS DESCRICAO_ALTERNATIVA,
          NULL :: BOOLEAN          AS TIPO_OUTROS,
          NULL :: INTEGER          AS ALTERNATIVA_ORDEM_EXIBICAO,
          NULL :: TEXT             AS PRIORIDADE_ALTERNATIVA,
          CMF.COD_FUNCAO           AS COD_CARGO,
          NULL :: BIGINT           AS COD_TIPO_VEICULO
        FROM CHECKLIST_MODELO_FUNCAO CMF
        WHERE CMF.COD_CHECKLIST_MODELO IN (SELECT CODIGO
                                           FROM CHECKLIST_MODELO CM
                                           WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                                 AND CM.STATUS_ATIVO = TRUE)
    ),

      CHECKLIST_MODELO_TIPO_VEICULO AS (
        SELECT
          NULL :: BIGINT        AS COD_UNIDADE_MODELO_CHECKLIST,
          CMVT.COD_MODELO       AS COD_MODELO_CHECKLIST,
          NULL :: TEXT          AS NOME_MODELO_CHECKLIST,
          NULL :: BIGINT        AS COD_PERGUNTA,
          NULL :: TEXT          AS DESCRICAO_PERGUNTA,
          NULL :: BIGINT        AS COD_IMAGEM,
          NULL :: TEXT          AS URL_IMAGEM,
          NULL :: INTEGER       AS PERGUNTA_ORDEM_EXIBICAO,
          NULL :: BOOLEAN       AS SINGLE_CHOICE,
          NULL :: BIGINT        AS COD_ALTERNATIVA,
          NULL :: TEXT          AS DESCRICAO_ALTERNATIVA,
          NULL :: BOOLEAN       AS TIPO_OUTROS,
          NULL :: INTEGER       AS ALTERNATIVA_ORDEM_EXIBICAO,
          NULL :: TEXT          AS PRIORIDADE_ALTERNATIVA,
          NULL :: BIGINT        AS COD_CARGO,
          CMVT.COD_TIPO_VEICULO AS COD_TIPO_VEICULO
        FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
        WHERE CMVT.COD_MODELO IN (SELECT CODIGO
                                  FROM CHECKLIST_MODELO CM
                                  WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                        AND CM.STATUS_ATIVO = TRUE)
    ),

      CHECKLISTS_FILTRADOS AS (
      SELECT *
      FROM CHECKLIST_MODELO_ATIVO
      UNION ALL
      SELECT *
      FROM CHECKLIST_MODELO_CARGO
      UNION ALL
      SELECT *
      FROM CHECKLIST_MODELO_TIPO_VEICULO
    )

  SELECT
    CF.COD_UNIDADE_MODELO_CHECKLIST AS COD_UNIDADE_MODELO_CHECKLIST,
    CF.COD_MODELO_CHECKLIST         AS COD_MODELO_CHECKLIST,
    CF.NOME_MODELO_CHECKLIST        AS NOME_MODELO_CHECKLIST,
    CF.COD_PERGUNTA                 AS COD_PERGUNTA,
    CF.DESCRICAO_PERGUNTA           AS DESCRICAO_PERGUNTA,
    CF.COD_IMAGEM                   AS COD_IMAGEM,
    CF.URL_IMAGEM                   AS URL_IMAGEM,
    CF.PERGUNTA_ORDEM_EXIBICAO      AS PERGUNTA_ORDEM_EXIBICAO,
    CF.SINGLE_CHOICE                AS SINGLE_CHOICE,
    CF.COD_ALTERNATIVA              AS COD_ALTERNATIVA,
    CF.DESCRICAO_ALTERNATIVA        AS DESCRICAO_ALTERNATIVA,
    CF.TIPO_OUTROS                  AS TIPO_OUTROS,
    CF.ALTERNATIVA_ORDEM_EXIBICAO   AS ALTERNATIVA_ORDEM_EXIBICAO,
    CF.PRIORIDADE_ALTERNATIVA       AS PRIORIDADE_ALTERNATIVA,
    CF.COD_CARGO                    AS COD_CARGO,
    CF.COD_TIPO_VEICULO             AS COD_TIPO_VEICULO
  FROM CHECKLISTS_FILTRADOS CF
  ORDER BY
    CF.COD_MODELO_CHECKLIST,
    CF.PERGUNTA_ORDEM_EXIBICAO,
    CF.COD_PERGUNTA,
    CF.ALTERNATIVA_ORDEM_EXIBICAO,
    CF.COD_ALTERNATIVA,
    CF.COD_CARGO,
    CF.COD_TIPO_VEICULO;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR OS COLABORADORES COM          ########################################
--#######################           ACESSO A REALIZAÇÃO DE CHECKLIST            ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_COLABORADORES_DISPONIVEIS(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_UNIDADE_COLABORADOR   BIGINT,
    COD_COLABORADOR           BIGINT,
    NOME_COLABORADOR          TEXT,
    CPF_COLABORADOR           TEXT,
    DATA_NASCIMENTO           DATE,
    COD_CARGO_COLABORADOR     INTEGER,
    COD_PERMISSAO_COLABORADOR INTEGER)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    C.COD_UNIDADE :: BIGINT      AS COD_UNIDADE_COLABORADOR,
    C.CODIGO                     AS COD_COLABORADOR,
    C.NOME :: TEXT               AS NOME_COLABORADOR,
    LPAD(C.CPF :: TEXT, 11, '0') AS CPF_COLABORADOR,
    C.DATA_NASCIMENTO :: DATE    AS DATA_NASCIMENTO,
    C.COD_FUNCAO                 AS COD_CARGO_COLABORADOR,
    C.COD_PERMISSAO :: INTEGER   AS COD_PERMISSAO_COLABORADOR
  FROM COLABORADOR C
  WHERE C.COD_UNIDADE = F_COD_UNIDADE
        AND C.STATUS_ATIVO
        -- Apenas colaboradores que possuem funções associadas a modelos de checklist ativos.
        AND C.COD_FUNCAO IN (SELECT CMF.COD_FUNCAO
                             FROM CHECKLIST_MODELO CM
                               JOIN CHECKLIST_MODELO_FUNCAO CMF
                                 ON CM.CODIGO = CMF.COD_CHECKLIST_MODELO
                             WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                   AND CM.STATUS_ATIVO = TRUE);
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR AS PLACAS VINCULADAS A        ########################################
--#######################           MODELOS DE CHECKLIST ATIVOS                 ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_PLACAS_DISPONIVEIS(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_VEICULO      BIGINT,
    PLACA_VEICULO    TEXT,
    COD_TIPO_VEICULO BIGINT,
    KM_ATUAL_VEICULO BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    V.CODIGO        AS COD_VEICULO,
    V.PLACA :: TEXT AS PLACA_VEICULO,
    V.COD_TIPO      AS COD_TIPO_VEICULO,
    v.KM            AS KM_ATUAL_VEICULO
  FROM VEICULO V
  WHERE V.COD_UNIDADE = F_COD_UNIDADE
        AND V.STATUS_ATIVO
        AND V.COD_TIPO IN (SELECT CMVT.COD_TIPO_VEICULO
                           FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
                             JOIN CHECKLIST_MODELO CM
                               ON CMVT.COD_MODELO = CM.CODIGO
                           WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                 AND CM.STATUS_ATIVO = TRUE);
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR INFORMAÇÕES DA EMPRESA        ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_INFORMACOES_UNIDADE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_EMPRESA   BIGINT,
    NOME_EMPRESA  TEXT,
    COD_REGIONAL  BIGINT,
    NOME_REGIONAL TEXT,
    COD_UNIDADE   BIGINT,
    NOME_UNIDADE  TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    E.CODIGO       AS COD_EMPRESA,
    E.NOME::TEXT   AS NOME_EMPRESA,
    R.CODIGO       AS COD_REGIONAL,
    R.REGIAO::TEXT AS NOME_REGIONAL,
    U.CODIGO       AS COD_UNIDADE,
    U.NOME::TEXT   AS NOME_UNIDADE
  FROM UNIDADE U
    JOIN EMPRESA E
      ON U.COD_EMPRESA = E.CODIGO
    JOIN REGIONAL R
      ON U.COD_REGIONAL = R.CODIGO
  WHERE U.CODIGO = F_COD_UNIDADE;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA INSERIR AS INFORMAÇÕES DE UM       ########################################
--#######################             CHECKLIST, MAS NÃO AS PERGUNTAS           ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(
  F_COD_UNIDADE_CHECKLIST              BIGINT,
  F_COD_MODELO_CHECKLIST               BIGINT,
  F_DATA_HORA_REALIZACAO               TIMESTAMP WITH TIME ZONE,
  F_COD_COLABORADOR                    BIGINT,
  F_COD_VEICULO                        BIGINT,
  F_PLACA_VEICULO                      TEXT,
  F_TIPO_CHECKLIST                     CHAR,
  F_KM_COLETADO                        BIGINT,
  F_TEMPO_REALIZACAO                   BIGINT,
  F_DATA_HORA_SINCRONIZACAO            TIMESTAMP WITH TIME ZONE,
  F_FONTE_DATA_HORA_REALIZACAO         TEXT,
  F_VERSAO_APP_MOMENTO_REALIZACAO      INTEGER,
  F_VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
  F_DEVICE_ID                          TEXT,
  F_DEVICE_IMEI                        TEXT,
  F_DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
  F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
  DEVE_ATUALIZAR_KM_VEICULO BOOLEAN := (CASE
                                        WHEN (F_KM_COLETADO > (SELECT V.KM FROM VEICULO V WHERE V.CODIGO = F_COD_VEICULO))
                                          THEN
                                            TRUE
                                        ELSE FALSE END);
  COD_CHECKLIST_INSERIDO    BIGINT;
  QTD_LINHAS_ATUALIZADAS    BIGINT;
BEGIN

  INSERT INTO CHECKLIST(COD_UNIDADE,
                        COD_CHECKLIST_MODELO,
                        DATA_HORA,
                        CPF_COLABORADOR,
                        PLACA_VEICULO,
                        TIPO,
                        TEMPO_REALIZACAO,
                        KM_VEICULO,
                        DATA_HORA_SINCRONIZACAO,
                        FONTE_DATA_HORA_REALIZACAO,
                        VERSAO_APP_MOMENTO_REALIZACAO,
                        VERSAO_APP_MOMENTO_SINCRONIZACAO,
                        DEVICE_ID,
                        DEVICE_IMEI,
                        DEVICE_UPTIME_REALIZACAO_MILLIS,
                        DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                        FOI_OFFLINE)
  VALUES(
    F_COD_UNIDADE_CHECKLIST,
    F_COD_MODELO_CHECKLIST,
    F_DATA_HORA_REALIZACAO,
    (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
    F_PLACA_VEICULO,
    F_TIPO_CHECKLIST,
    F_TEMPO_REALIZACAO,
    F_KM_COLETADO,
    F_DATA_HORA_SINCRONIZACAO,
    F_FONTE_DATA_HORA_REALIZACAO,
    F_VERSAO_APP_MOMENTO_REALIZACAO,
    F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
    F_DEVICE_ID,
    F_DEVICE_IMEI,
    F_DEVICE_UPTIME_REALIZACAO_MILLIS,
    F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
    TRUE
  ) RETURNING CODIGO INTO COD_CHECKLIST_INSERIDO;

  -- Verificamos se o insert funcionou.
  IF COD_CHECKLIST_INSERIDO <= 0
  THEN
    RAISE EXCEPTION 'Não foi possível inserir o checklist';
  END IF;

  IF DEVE_ATUALIZAR_KM_VEICULO
  THEN
    UPDATE VEICULO SET KM = F_KM_COLETADO WHERE CODIGO = F_COD_VEICULO;
  END IF;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
  IF (DEVE_ATUALIZAR_KM_VEICULO AND QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Não foi possível atualizar o km do veículo';
  END IF;

  RETURN COD_CHECKLIST_INSERIDO;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################          FUNCTION PARA INSERIR AS RESPOSTAS           ########################################
--#######################                    DE UM CHECKLIST                    ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_RESPOSTAS_CHECKLIST(
  F_COD_UNIDADE_CHECKLIST BIGINT,
  F_COD_MODELO_CHECKLIST  BIGINT,
  F_COD_CHECKLIST         BIGINT,
  F_RESPOSTA              TEXT,
  F_COD_PERGUNTA          BIGINT,
  F_COD_ALTERNATIVA       BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_INSERIDAS BIGINT;
BEGIN
  INSERT INTO CHECKLIST_RESPOSTAS(COD_CHECKLIST_MODELO,
                                  COD_UNIDADE,
                                  COD_CHECKLIST,
                                  RESPOSTA,
                                  COD_PERGUNTA,
                                  COD_ALTERNATIVA)
  VALUES(F_COD_MODELO_CHECKLIST,
         F_COD_UNIDADE_CHECKLIST,
         F_COD_CHECKLIST,
         F_RESPOSTA,
         F_COD_PERGUNTA,
         F_COD_ALTERNATIVA);

  GET DIAGNOSTICS QTD_LINHAS_INSERIDAS = ROW_COUNT;

  IF QTD_LINHAS_INSERIDAS <> 1
  THEN
    RAISE EXCEPTION 'Não foi possível inserir a resposta';
  END IF;

  RETURN QTD_LINHAS_INSERIDAS;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################              FUNCTION PARA VERIFICAR SE UM            ########################################
--#######################                   CHECKLIST JÁ EXISTE                 ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_COD_CHECKLIST_DUPLICADO(
  F_COD_UNIDADE_CHECKLIST           BIGINT,
  F_COD_MODELO_CHECKLIST            BIGINT,
  F_DATA_HORA_REALIZACAO            TIMESTAMP WITH TIME ZONE,
  F_COD_COLABORADOR                 BIGINT,
  F_PLACA_VEICULO                   TEXT,
  F_TIPO_CHECKLIST                  CHAR,
  F_KM_COLETADO                     BIGINT,
  F_TEMPO_REALIZACAO                BIGINT,
  F_FONTE_DATA_HORA_REALIZACAO      TEXT,
  F_VERSAO_APP_MOMENTO_REALIZACAO   INTEGER,
  F_DEVICE_ID                       TEXT,
  F_DEVICE_IMEI                     TEXT,
  F_DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT)
  RETURNS TABLE(
    CHECKLIST_JA_EXISTE BOOLEAN,
    COD_CHECKLIST       BIGINT)
LANGUAGE SQL
AS $$
WITH CTE AS (
    -- O duplo select serve para retornar null caso o código não seja encontrado.
    SELECT (SELECT C.CODIGO AS COD_CHECKLIST
            FROM CHECKLIST C
            WHERE C.COD_UNIDADE = F_COD_UNIDADE_CHECKLIST
                  AND C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
                  AND C.DATA_HORA = F_DATA_HORA_REALIZACAO
                  AND C.CPF_COLABORADOR = (SELECT CO.CPF
                                           FROM COLABORADOR CO
                                           WHERE CO.CODIGO = F_COD_COLABORADOR)
                  AND C.PLACA_VEICULO = F_PLACA_VEICULO
                  AND C.TIPO = F_TIPO_CHECKLIST
                  AND C.KM_VEICULO = F_KM_COLETADO
                  AND C.TEMPO_REALIZACAO = F_TEMPO_REALIZACAO
                  AND C.FONTE_DATA_HORA_REALIZACAO = F_FONTE_DATA_HORA_REALIZACAO
                  AND C.VERSAO_APP_MOMENTO_REALIZACAO = F_VERSAO_APP_MOMENTO_REALIZACAO
                  AND C.DEVICE_ID = F_DEVICE_ID
                  AND C.DEVICE_IMEI = F_DEVICE_IMEI
                  AND C.DEVICE_UPTIME_REALIZACAO_MILLIS = F_DEVICE_UPTIME_REALIZACAO_MILLIS) AS COD_CHECKLIST
)

SELECT
  CTE.COD_CHECKLIST IS NOT NULL,
  CTE.COD_CHECKLIST
FROM CTE;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################  DELETA TODOS OS TOKEN DA EMPRESA ANDRADE PARA FORÇAR O LOGIN  ###############################
--######################################################################################################################
--######################################################################################################################
DELETE FROM TOKEN_AUTENTICACAO WHERE CPF_COLABORADOR IN (SELECT C.CPF FROM COLABORADOR C WHERE C.COD_EMPRESA = 28);
END TRANSACTION;