BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--##################################### TABELA PARA SALVAR PARAMETRIZAÇÕES DE ALERTA DE SULCO ##########################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE IF NOT EXISTS AFERICAO_CONFIGURACAO_ALERTA_SULCO (
  CODIGO BIGSERIAL NOT NULL,
  COD_UNIDADE BIGINT NOT NULL,
  VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION NOT NULL,
  VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION NOT NULL,
  CONSTRAINT PK_AFERICAO_CONFIGURACAO_ALERTA_SULCO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_AFERICAO_CONFIGURACAO_ALERTA_SULCO_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT UNICA_CONFIG_UNIDADE UNIQUE (COD_UNIDADE)
);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################ TABELA PARA SALVAR PARAMETRIZAÇÕES DEFAULT A NÍVEL PROLOG DA PARTE DE AFERIÇÃO ######################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE IF NOT EXISTS AFERICAO_CONFIGURACAO_PROLOG (
  VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION NOT NULL,
  VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION NOT NULL
);
COMMENT ON TABLE AFERICAO_CONFIGURACAO_PROLOG
IS 'Configurações de aferição que são definidas a nível ProLog. Para algumas parametrizações, esses valores são utilizados caso o cliente não defina algo específico para sua operação.';

INSERT INTO AFERICAO_CONFIGURACAO_PROLOG VALUES (3, 3);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############### FUNCTION PARA BUSCAR UNIDADES QUE O COLABORADOR TEM ACESSO COM BASE EM SUA PERMISSÃO #################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    CODIGO_EMPRESA  BIGINT,
    NOME_EMPRESA    TEXT,
    CODIGO_REGIONAL BIGINT,
    NOME_REGIONAL   TEXT,
    CODIGO_UNIDADE  BIGINT,
    NOME_UNIDADE    TEXT,
    CODIGO_EQUIPE   BIGINT,
    NOME_EQUIPE     TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_PERMISSAO SMALLINT;
  F_COD_EMPRESA   BIGINT;
  F_COD_REGIONAL  BIGINT;
  F_COD_UNIDADE   BIGINT;
  F_COD_EQUIPE    BIGINT;
BEGIN
  SELECT INTO F_COD_PERMISSAO, F_COD_EMPRESA, F_COD_REGIONAL, F_COD_UNIDADE, F_COD_EQUIPE
    C.COD_PERMISSAO,
    C.COD_EMPRESA,
    R.CODIGO,
    C.COD_UNIDADE,
    C.COD_EQUIPE
  FROM COLABORADOR C
    JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
    JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  WHERE C.CODIGO = F_COD_COLABORADOR;

  RETURN QUERY
  SELECT
    EMP.CODIGO       AS COD_EMPRESA,
    EMP.NOME :: TEXT AS NOME_EMPRESA,
    R.CODIGO         AS COD_REGIONAL,
    R.REGIAO :: TEXT AS NOME_REGIONAL,
    U.CODIGO         AS COD_UNIDADE,
    U.NOME :: TEXT   AS NOME_UNIDADE,
    EQ.CODIGO        AS COD_EQUIPE,
    EQ.NOME :: TEXT  AS NOME_EQUIPE
  FROM UNIDADE U
    JOIN REGIONAL R
      ON R.CODIGO = U.COD_REGIONAL
    JOIN EMPRESA EMP
      ON EMP.CODIGO = U.COD_EMPRESA
    JOIN EQUIPE EQ
      ON U.CODIGO = EQ.COD_UNIDADE
  WHERE EMP.CODIGO = F_COD_EMPRESA
        AND F_IF(F_COD_PERMISSAO <= 2, R.CODIGO = F_COD_REGIONAL, TRUE)
        AND F_IF(F_COD_PERMISSAO <= 1, U.CODIGO = F_COD_UNIDADE, TRUE)
        AND F_IF(F_COD_PERMISSAO = 0, EQ.CODIGO = F_COD_EQUIPE, TRUE)
  ORDER BY EMP.CODIGO ASC, R.CODIGO ASC, U.CODIGO ASC, EQ.CODIGO ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######## FUNCTION PARA BUSCAR AS CONFIGURAÇÕES DE COLETA DE SULCO DAS UNIDADES QUE O COLABORADOR TEM ACESSO ##########
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIG_ALERTA_COLETA_SULCO(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    COD_UNIDADE                            BIGINT,
    NOME_UNIDADE                           TEXT,
    CODIGO                                 BIGINT,
    VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
    VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION)
LANGUAGE PLPGSQL
AS $$
DECLARE
  VARIACAO_MENOR_DEFAULT DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS
                                              FROM AFERICAO_CONFIGURACAO_PROLOG AP);
  VARIACAO_MAIOR_DEFAULT DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS
                                              FROM AFERICAO_CONFIGURACAO_PROLOG AP);
BEGIN
  RETURN QUERY
  WITH UNIDADES_ACESSO AS (
      SELECT
        DISTINCT ON (F.CODIGO_UNIDADE)
        F.CODIGO_UNIDADE,
        F.NOME_UNIDADE
      FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
  )

  SELECT
    -- Precisamos utilizar o código de unidade de UA pois a unidade pode não possuir config criada e ainda
    -- assim precisamos retornar o código dela.
    UA.CODIGO_UNIDADE,
    UA.NOME_UNIDADE,
    CONFIG.CODIGO,
    COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS, VARIACAO_MENOR_DEFAULT),
    COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS, VARIACAO_MAIOR_DEFAULT)
  FROM UNIDADES_ACESSO UA
    LEFT JOIN AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG
      ON UA.CODIGO_UNIDADE = CONFIG.COD_UNIDADE
  ORDER BY UA.NOME_UNIDADE ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################### FUNCTION PARA INSERIR OU ATUALIZAR AS PARAMETRIZAÇÕES DE COLETA DE SULCO #####################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(
  F_CODIGO               BIGINT,
  F_COD_UNIDADE          BIGINT,
  F_VARIACAO_SULCO_MENOR DOUBLE PRECISION,
  F_VARIACAO_SULCO_MAIOR DOUBLE PRECISION)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF F_CODIGO IS NULL
  THEN
    INSERT INTO AFERICAO_CONFIGURACAO_ALERTA_SULCO (
      COD_UNIDADE,
      VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
      VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS)
    VALUES (
      F_COD_UNIDADE,
      F_VARIACAO_SULCO_MENOR,
      F_VARIACAO_SULCO_MAIOR);
  ELSE
    UPDATE AFERICAO_CONFIGURACAO_ALERTA_SULCO
    SET
      VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS = F_VARIACAO_SULCO_MENOR,
      VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS = F_VARIACAO_SULCO_MAIOR
    WHERE CODIGO = F_CODIGO;
  END IF;

  RETURN FOUND;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--####################### VIEW PARA FACILITAR A BUSCA DAS CONFIGS DE ALERTA DE COLETA DE SULCO #########################
--######################################################################################################################
--######################################################################################################################
CREATE VIEW VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS
  SELECT
    U.CODIGO                                         AS COD_UNIDADE,
    CONFIG.CODIGO                                    AS COD_CONFIG,
    COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
             (SELECT AP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS
              FROM AFERICAO_CONFIGURACAO_PROLOG AP)) AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
    COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
             (SELECT AP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS
              FROM AFERICAO_CONFIGURACAO_PROLOG AP)) AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
    F_IF(CONFIG.CODIGO IS NULL, TRUE,
         FALSE)                                      AS USA_DEFAULT_PROLOG
  FROM UNIDADE U
    LEFT JOIN AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG ON U.CODIGO = CONFIG.COD_UNIDADE;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################### ATUALIZA FUNCTION DE BUSCA DAS CONFIGURAÇÕES DE AFERIÇÃO POR PLACA ###########################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_AFERICAO_BY_PLACA(F_PLACA_VEICULO TEXT);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(F_PLACA_VEICULO TEXT)
  RETURNS TABLE(
    SULCO_MINIMO_DESCARTE                  REAL,
    SULCO_MINIMO_RECAPAGEM                 REAL,
    TOLERANCIA_CALIBRAGEM                  REAL,
    TOLERANCIA_INSPECAO                    REAL,
    PERIODO_AFERICAO_SULCO                 INTEGER,
    PERIODO_AFERICAO_PRESSAO               INTEGER,
    PODE_AFERIR_SULCO                      BOOLEAN,
    PODE_AFERIR_PRESSAO                    BOOLEAN,
    PODE_AFERIR_SULCO_PRESSAO              BOOLEAN,
    PODE_AFERIR_ESTEPE                     BOOLEAN,
    VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
    VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
    VARIACOES_SULCO_DEFAULT_PROLOG         BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_UNIDADE      BIGINT;
  F_COD_TIPO_VEICULO BIGINT;
BEGIN
  SELECT INTO F_COD_UNIDADE, F_COD_TIPO_VEICULO
    V.COD_UNIDADE,
    V.COD_TIPO
  FROM VEICULO V
  WHERE V.PLACA = F_PLACA_VEICULO;

  RETURN QUERY
  SELECT
    PRU.SULCO_MINIMO_DESCARTE,
    PRU.SULCO_MINIMO_RECAPAGEM,
    PRU.TOLERANCIA_INSPECAO,
    PRU.TOLERANCIA_CALIBRAGEM,
    PRU.PERIODO_AFERICAO_SULCO,
    PRU.PERIODO_AFERICAO_PRESSAO,
    CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO,
    CONFIG_PODE_AFERIR.PODE_AFERIR_PRESSAO,
    CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO_PRESSAO,
    CONFIG_PODE_AFERIR.PODE_AFERIR_ESTEPE,
    CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
    CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
    CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG AS VARIACOES_SULCO_DEFAULT_PROLOG
  FROM VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO AS CONFIG_PODE_AFERIR
    JOIN VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS CONFIG_ALERTA_SULCO
      ON CONFIG_PODE_AFERIR.COD_UNIDADE = CONFIG_ALERTA_SULCO.COD_UNIDADE
    JOIN PNEU_RESTRICAO_UNIDADE PRU
      ON PRU.COD_UNIDADE = CONFIG_PODE_AFERIR.COD_UNIDADE
  WHERE CONFIG_PODE_AFERIR.COD_UNIDADE = F_COD_UNIDADE
        AND CONFIG_PODE_AFERIR.COD_TIPO_VEICULO = F_COD_TIPO_VEICULO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################### CRIA FUNCTION DE BUSCA DAS CONFIGURAÇÕES DE AFERIÇÃO POR PNEU ################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_AVULSA(F_COD_PNEU BIGINT)
  RETURNS TABLE(
    SULCO_MINIMO_DESCARTE                  REAL,
    SULCO_MINIMO_RECAPAGEM                 REAL,
    TOLERANCIA_CALIBRAGEM                  REAL,
    TOLERANCIA_INSPECAO                    REAL,
    PERIODO_AFERICAO_SULCO                 INTEGER,
    PERIODO_AFERICAO_PRESSAO               INTEGER,
    VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
    VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
    VARIACOES_SULCO_DEFAULT_PROLOG         BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_UNIDADE BIGINT;
BEGIN
  SELECT INTO F_COD_UNIDADE P.COD_UNIDADE
  FROM PNEU P
  WHERE P.CODIGO = F_COD_PNEU;

  RETURN QUERY
  SELECT
    PRU.SULCO_MINIMO_DESCARTE                                  AS SULCO_MINIMO_DESCARTE,
    PRU.SULCO_MINIMO_RECAPAGEM                                 AS SULCO_MINIMO_RECAPAGEM,
    PRU.TOLERANCIA_INSPECAO                                    AS TOLERANCIA_INSPECAO,
    PRU.TOLERANCIA_CALIBRAGEM                                  AS TOLERANCIA_CALIBRAGEM,
    PRU.PERIODO_AFERICAO_SULCO                                 AS PERIODO_AFERICAO_SULCO,
    PRU.PERIODO_AFERICAO_PRESSAO                               AS PERIODO_AFERICAO_PRESSAO,
    CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
    CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
    CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG                     AS VARIACOES_SULCO_DEFAULT_PROLOG
  FROM VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG_ALERTA_SULCO
    JOIN PNEU_RESTRICAO_UNIDADE PRU
      ON PRU.COD_UNIDADE = CONFIG_ALERTA_SULCO.COD_UNIDADE
  WHERE CONFIG_ALERTA_SULCO.COD_UNIDADE = F_COD_UNIDADE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################### FUNC DE CÓPIA DE NOMENCLATURA DE PNEUS #######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEUS_COPIA_NOMENCLATURAS_ENTRE_UNIDADES(
    F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS BIGINT,
    F_COD_UNIDADES_DESTINO_NOMENCLATURAS     BIGINT[],
  OUT AVISO_NOMENCLATURAS_COPIADAS           TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- REMOVE UNIDADES DUPLICADAS DO ARRAY DE DESTINO.
  F_COD_UNIDADES_DESTINO_NOMENCLATURAS := ARRAY_DISTINCT(F_COD_UNIDADES_DESTINO_NOMENCLATURAS);

  -- VERIFICA SE A UNIDADE DE ORIGEM NÃO ESTÁ ENTRE AS DE DESTINO.
  IF F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)
  THEN RAISE EXCEPTION 'O CÓDIGO DA UNIDADE DE ORIGEM NÃO PODE CONSTAR NAS UNIDADES DE DESTINO!';
  END IF;

  -- VERIFICA SE TODAS AS UNIDADES DE DESTINO PERTENCEM A MESMA EMPRESA.
  IF (SELECT COUNT(DISTINCT U.COD_EMPRESA)
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)) > 1
  THEN RAISE EXCEPTION 'SÓ É POSSÍVEL COPIAR AS NOMENCLATURAS PARA UNIDADES DA MESMA EMPRESA!';
  END IF;

  -- VERIFICA SE A EMPRESA DA UNIDADE DE ORIGEM É A MESMA DAS UNIDADES DE DESTINO.
  IF (SELECT DISTINCT U.COD_EMPRESA
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)) != (SELECT U.COD_EMPRESA
                                                                       FROM UNIDADE U
                                                                       WHERE U.CODIGO =
                                                                             F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS)
  THEN RAISE EXCEPTION 'A EMPRESA DA UNIDADE DE ORIGEM PRECISA SER A MESMA DAS UNIDADES DE DESTINO!';
  END IF;

  -- DELETA AS NOMENCLATURAS DA(S) UNIDADE(S) DESTINO EXISTENTES NA TABELA PNEU_ORDEM_NOMENCLATURA_UNIDADE
  -- QUE POSSUEM O MESMO CODIGO DE DIAGRAMA DA UNIDADE ORIGEM.
  DELETE FROM PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    USING VEICULO_TIPO VT
  WHERE VT.CODIGO = PONU.COD_TIPO_VEICULO
        AND PONU.COD_UNIDADE = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)
        AND VT.COD_DIAGRAMA IN (SELECT VT.COD_DIAGRAMA FROM VEICULO_TIPO VT
    JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU ON PONU.COD_TIPO_VEICULO = VT.CODIGO
  WHERE PONU.COD_TIPO_VEICULO IN (SELECT VT.CODIGO FROM VEICULO_TIPO VT WHERE VT.COD_UNIDADE = F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS));

  -- COPIA AS NOMECLATURAS DA UNIDADE DE ORIGEM PARA TODAS AS DE DESTINO.
    WITH NOMENCLATURAS AS (SELECT VT.CODIGO           AS COD_TIPO_VEICULO,
                                  VT.COD_UNIDADE      AS COD_UNIDADE,
                                  VT.COD_DIAGRAMA     AS COD_DIAGRAMA,
                                  PONU.POSICAO_PROLOG AS POSICAO_PROLOG,
                                  PONU.NOMENCLATURA   AS NOMENCLATURA
                            FROM VEICULO_TIPO VT
                              JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
                                ON PONU.COD_TIPO_VEICULO = VT.CODIGO
                                AND PONU.COD_UNIDADE = VT.COD_UNIDADE
                            WHERE VT.COD_UNIDADE = F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS
    )

   INSERT INTO PNEU_ORDEM_NOMENCLATURA_UNIDADE (COD_TIPO_VEICULO, COD_UNIDADE, POSICAO_PROLOG, NOMENCLATURA)
    SELECT  VT.CODIGO        AS COD_TIPO_VEICULO,
            VT.COD_UNIDADE   AS COD_UNIDADE,
            N.POSICAO_PROLOG AS POSICAO_PROLOG,
            N.NOMENCLATURA   AS NOMENCLATURA
    FROM NOMENCLATURAS N
      CROSS JOIN UNNEST(F_COD_UNIDADES_DESTINO_NOMENCLATURAS) T(COD_UNIDADE)
      JOIN VEICULO_TIPO VT
        ON VT.COD_UNIDADE = T.COD_UNIDADE
        AND N.COD_DIAGRAMA = VT.COD_DIAGRAMA
    ORDER BY
      T.COD_UNIDADE ASC,
      VT.CODIGO ASC;
   SELECT 'NOMENCLATURAS COPIADAS COM SUCESSO DA UNIDADE '
         || F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS
         || ' PARA A(S) UNIDADE(S) '
         || ARRAY_TO_STRING(F_COD_UNIDADES_DESTINO_NOMENCLATURAS, ', ')
  INTO AVISO_NOMENCLATURAS_COPIADAS;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################## CRIA FUNCTION PARA EXIBIR TODOS OS MODELO DE CHECKLIST DAS UNIDADES SELECIONADAS ##################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    UNIDADE             TEXT,
    "NOME DO CHECKLIST" TEXT,
    PERGUNTA            TEXT,
    ALTERNATIVA         TEXT,
    "TIPO DE RESPOSTA"  TEXT,
    PRIORIDADE          TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  U.NOME,
  CM.NOME,
  CP.PERGUNTA,
  CAP.ALTERNATIVA,
  CASE WHEN CP.SINGLE_CHOICE = TRUE THEN 'ÚNICA' ELSE 'MÚLTIPLA' END AS TIPO_DE_RESPOSTA,
  CP.PRIORIDADE
FROM CHECKLIST_PERGUNTAS CP
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.COD_UNIDADE = CAP.COD_UNIDADE
    AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
    AND CP.CODIGO = CAP.COD_PERGUNTA
  JOIN CHECKLIST_MODELO CM ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
  JOIN UNIDADE U ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  ORDER BY U.NOME,CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### CRIA FUNCTION PARA EXIBIR DADOS GERAIS DE CHECKLIST REALIZADOS #############################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES     BIGINT[],
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_PLACA            TEXT,
  F_COD_COLABORADOR  BIGINT)
  RETURNS TABLE(
    UNIDADE                   TEXT,
    "DATA/HORA REALIZAÇÃO"    TEXT,
    "CPF COLABORADOR"         TEXT,
    "NOME COLABORADOR"        TEXT,
    "TIPO VEÍCULO"            TEXT,
    PLACA                     TEXT,
    "TIPO CHECKLIST"          TEXT,
    "CÓDIGO MODELO"           TEXT,
    "NOME MODELO"             TEXT,
    PERGUNTA                  TEXT,
    ALTERNATIVA               TEXT,
    RESPOSTA                  TEXT,
    PRIORIDADE                TEXT,
    "TIPO RESPOSTA"           TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                             AS NOME_UNIDADE,
  TO_CHAR(CL.DATA_HORA, 'DD/MM/YYYY HH24:MI:SS')                     AS DATA_HORA_REALIZACAO,
  LPAD(CL.CPF_COLABORADOR :: TEXT, 11, '0'),
  CO.NOME                                                            AS NOME_COLABORADOR,
  VT.NOME                                                            AS NOME_TIPO_VEICULO,
  CL.PLACA_VEICULO,
  CASE WHEN CL.TIPO = 'S' THEN 'SAÍDA' ELSE 'RETORNO' END            AS TIPO_CHECKLIST,
  CM.CODIGO :: TEXT                                                  AS CODIGO_MODELO_CHECKLIST,
  CM.NOME                                                            AS NOME_MODELO_CHECKLIST,
  CP.PERGUNTA,
  CAP.ALTERNATIVA,
  CR.RESPOSTA,
  CP.PRIORIDADE,
  CASE WHEN CP.SINGLE_CHOICE = TRUE THEN 'ÚNICA ESCOLHA' ELSE 'MÚLTIPLA ESCOLHA' END AS TIPO_RESPOSTA
FROM CHECKLIST CL
  JOIN CHECKLIST_MODELO CM ON CL.COD_CHECKLIST_MODELO = CM.CODIGO
  JOIN CHECKLIST_PERGUNTAS CP ON CM.CODIGO = CP.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.CODIGO = CAP.COD_PERGUNTA
  JOIN UNIDADE U ON CL.COD_UNIDADE = U.CODIGO
  JOIN CHECKLIST_RESPOSTAS CR ON CAP.CODIGO = CR.COD_ALTERNATIVA
                                 AND CR.COD_CHECKLIST = CL.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = CL.CPF_COLABORADOR
  JOIN VEICULO_TIPO VT ON VT.CODIGO =
                          (SELECT V.COD_TIPO
                           FROM VEICULO V
                           WHERE V.PLACA = CL.PLACA_VEICULO)
WHERE U.CODIGO = ANY  (F_COD_UNIDADES)
  AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE))::DATE >= F_DATA_INICIAL
  AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE))::DATE <= F_DATA_FINAL
  AND F_IF(F_PLACA IS NOT NULL, CL.PLACA_VEICULO = F_PLACA, TRUE)
  AND F_IF(F_COD_COLABORADOR IS NOT NULL, CO.CODIGO = F_COD_COLABORADOR, TRUE)
ORDER BY U.CODIGO, CL.DATA_HORA DESC, CL.PLACA_VEICULO, CM.CODIGO,CP.PERGUNTA,CAP.CODIGO;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- CRIA FUNCTION PARA RELATÓRIO QUE BUSCA O MENOR SULCO E PRESSÃO DE CADA PNEU DAS UNIDADES FILTRADAS. ELE É UTILIZADO
-- PARA POPULAR O COMPONENT DE SCATTER DA DASHBOARD.
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_MENOR_SULCO_E_PRESSAO_PNEUS(F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    COD_PNEU         BIGINT,
    COD_PNEU_CLIENTE TEXT,
    PRESSAO_ATUAL    NUMERIC,
    MENOR_SULCO      NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO                                                                                   AS COD_PNEU,
  P.CODIGO_CLIENTE                                                                           AS COD_PNEU_CLIENTE,
  TRUNC(P.PRESSAO_ATUAL :: NUMERIC, 2)                                                       AS PRESSAO_ATUAL,
  TRUNC(LEAST(P.ALTURA_SULCO_INTERNO, P.ALTURA_SULCO_EXTERNO,
              P.ALTURA_SULCO_CENTRAL_EXTERNO, P.ALTURA_SULCO_CENTRAL_INTERNO) :: NUMERIC, 2) AS MENOR_SULCO
FROM PNEU P
WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY MENOR_SULCO ASC
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;