BEGIN TRANSACTION ;
--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA ATUALIZAR OU INSERIR UM TOKEN E    ########################################
--#######################       VERSÃO DOS DADOS PARA UMA UNIDADE ESPECIFICA    ########################################
--######################################################################################################################
--######################################################################################################################
-- PL-1789
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE(F_COD_UNIDADE BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  VERSAO_DADOS_BD BIGINT;
BEGIN
  IF (SELECT EXISTS(SELECT COD_UNIDADE FROM CHECKLIST_OFFLINE_DADOS_UNIDADE WHERE COD_UNIDADE = F_COD_UNIDADE))
  THEN
    UPDATE CHECKLIST_OFFLINE_DADOS_UNIDADE SET VERSAO_DADOS = VERSAO_DADOS + 1
    WHERE COD_UNIDADE = F_COD_UNIDADE RETURNING VERSAO_DADOS INTO VERSAO_DADOS_BD;
  ELSE
    INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE(COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
    VALUES (F_COD_UNIDADE, F_RANDOM_STRING(64)) RETURNING VERSAO_DADOS INTO VERSAO_DADOS_BD;
  END IF;
  RETURN VERSAO_DADOS_BD;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA ATUALIZAR OU INSERIR UM TOKEN E    ########################################
--#######################     VERSÃO DOS DADOS PARA A UNIDADE DO COLABORADOR    ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_COLABORADOR(F_COD_COLABORADOR BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT C.COD_UNIDADE
                                                             FROM COLABORADOR C
                                                             WHERE C.CODIGO = F_COD_COLABORADOR));
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA ATUALIZAR OU INSERIR UM TOKEN E            ################################
--#######################     VERSÃO DOS DADOS PARA A UNIDADE DO MODELO DE CHECKLIST    ################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_MODELO_CHECK(
  F_COD_MODELO_CHECKLIST BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT CM.COD_UNIDADE
                                                             FROM CHECKLIST_MODELO CM
                                                             WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST));
END;
$$;


--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA ATUALIZAR OU INSERIR UM TOKEN E            ################################
--#######################     VERSÃO DOS DADOS PARA A UNIDADE DO VEÍCULO                ################################
--######################################################################################################################
--######################################################################################################################
CREATE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_VEICULO(F_COD_VEICULO BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT V.COD_UNIDADE
                                                             FROM VEICULO V
                                                             WHERE V.CODIGO = F_COD_VEICULO));
END;
$$;


--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA VERIFICAR SE UM COLABORADOR POSSUI    #####################################
--#######################      ACESSO A UMA PERMISSÃO ESPECÍFICA DO PROLOG         #####################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
  F_COD_COLABORADOR BIGINT,
  F_COD_FUNCAO_PROLOG INTEGER)
  RETURNS BOOLEAN
LANGUAGE SQL
AS $$
SELECT EXISTS(
           SELECT
             C.CODIGO
           FROM COLABORADOR C
             JOIN CARGO_FUNCAO_PROLOG_V11 CARGO
               ON C.COD_UNIDADE = CARGO.COD_UNIDADE
                  AND C.COD_FUNCAO = CARGO.COD_FUNCAO_COLABORADOR
           WHERE C.CODIGO = F_COD_COLABORADOR
                 AND CARGO.COD_FUNCAO_PROLOG = F_COD_FUNCAO_PROLOG) AS TEM_PERMISSAO
$$;

--######################################################################################################################
--######################################################################################################################
--#######################      FUNCTION PARA VERIFICAR SE VEÍCULO POSSUI UM     ########################################
--#######################      VÍNCULO COM ALGUM MODELO DE CHECKLIST            ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_VEICULO_VERIFICA_POSSUI_VINCULO_MODELO_CHECKLIST(F_COD_VEICULO BIGINT)
  RETURNS BOOLEAN
LANGUAGE SQL
AS $$
SELECT EXISTS(
           SELECT
             V.CODIGO
           FROM VEICULO V
             JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT
               ON V.COD_TIPO = CMVT.COD_TIPO_VEICULO
                  AND V.COD_UNIDADE = CMVT.COD_UNIDADE
             JOIN CHECKLIST_MODELO CM
               ON CMVT.COD_MODELO = CM.CODIGO
                  AND CM.STATUS_ATIVO = TRUE
           WHERE V.CODIGO = F_COD_VEICULO) AS ESTA_VINCULADO_MODELO_CHECKLIST
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
--############################## ALTERA FUNCTION DE LISTAGEM DE CHECKLISTS REALIZADOS ##################################
--###################################### ORDENA POR DATA_HORA_SINCRONIZACAO ############################################
--######################################################################################################################
--######################################################################################################################
-- PL-1796

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(
  F_COD_UNIDADE      BIGINT,
  F_COD_EQUIPE       BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    CHARACTER VARYING,
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_TIMEZONE         TEXT,
  F_LIMIT            INTEGER,
  F_OFFSET           BIGINT)
  RETURNS TABLE(
    CODIGO               BIGINT,
    DATA_HORA            TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO BIGINT,
    KM_VEICULO           BIGINT,
    TEMPO_REALIZACAO     BIGINT,
    CPF_COLABORADOR      BIGINT,
    PLACA_VEICULO        CHARACTER VARYING,
    TIPO                 CHARACTER,
    NOME                 CHARACTER VARYING,
    TOTAL_ITENS_OK       BIGINT,
    TOTAL_ITENS_NOK      BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  RESPOSTA_OK VARCHAR(2) := 'OK';
  F_HAS_EQUIPE INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 1 ELSE 0 END;
  F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 1 ELSE 0 END;
  F_HAS_PLACA_VEICULO INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO                                              AS CODIGO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE                   AS DATA_HORA,
    C.COD_CHECKLIST_MODELO                                AS COD_CHECKLIST_MODELO,
    C.KM_VEICULO                                          AS KM_VEICULO,
    C.TEMPO_REALIZACAO                                    AS TEMPO_REALIZACAO,
    C.CPF_COLABORADOR                                     AS CPF_COLABORADOR,
    C.PLACA_VEICULO                                       AS PLACA_VEICULO,
    C.TIPO                                                AS TIPO,
    CO.NOME                                               AS NOME_COLABORADOR,
    (SELECT COUNT(*) - COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_OK,
    (SELECT COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_NOK
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
    JOIN EQUIPE E
      ON E.CODIGO = CO.COD_EQUIPE
    JOIN VEICULO V
      ON V.PLACA = C.PLACA_VEICULO
  WHERE (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL
        AND C.COD_UNIDADE = F_COD_UNIDADE
        AND (F_HAS_EQUIPE = 1 OR E.CODIGO = F_COD_EQUIPE)
        AND (F_HAS_COD_TIPO_VEICULO = 1 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
        AND (F_HAS_PLACA_VEICULO = 1 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
  ORDER BY DATA_HORA_SINCRONIZACAO DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################## ALTERA FUNCTION DO RELATÓRIO DE RESUMO DE CHECKLISTS ##################################
--###################################### ORDENA POR DATA_HORA_SINCRONIZACAO ############################################
--######################################################################################################################
--######################################################################################################################
-- PL-1796

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "DATA"                        TEXT,
    "HORA"                        TEXT,
    "COLABORADOR"                 TEXT,
    "PLACA"                       TEXT,
    "KM"                          BIGINT,
    "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
    "TIPO"                        TEXT,
    "TOTAL DE PERGUNTAS"          BIGINT,
    "TOTAL NOK"                   BIGINT,
    "PRIORIDADE BAIXA"            BIGINT,
    "PRIORIDADE ALTA"             BIGINT,
    "PRIORIDADE CRÍTICA"          BIGINT)
LANGUAGE SQL
AS $$
WITH CHECKLITS AS (
    SELECT
      C.CODIGO                                                                            AS COD_CHECKLIST,
      U.CODIGO                                                                            AS COD_UNIDADE,
      U.NOME                                                                              AS NOME_UNIDADE,
      C.DATA_HORA                                                                         AS DATA_HORA_REALIZACAO,
      C.DATA_HORA_SINCRONIZACAO                                                           AS DATA_HORA_SINCRONIZACAO,
      TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE, 'DD/MM/YYYY') AS DATA_REALIZACAO_CHECK,
      TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: TIME, 'HH24:MI')    AS HORARIO_REALIZACAO_CHECK,
      CO.NOME                                                                             AS NOME_COLABORADOR,
      C.PLACA_VEICULO                                                                     AS PLACA_VEICULO,
      C.KM_VEICULO                                                                        AS KM_VEICULO,
      C.TEMPO_REALIZACAO / 1000                                                           AS TEMPO_REALIZACAO_SEGUNDOS,
      F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                              AS TIPO_CHECKLIST,
      COUNT(C.CODIGO)                                                                     AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
      JOIN CHECKLIST_PERGUNTAS CP
        ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
      JOIN COLABORADOR CO
        ON C.CPF_COLABORADOR = CO.CPF
      JOIN UNIDADE U
        ON C.COD_UNIDADE = U.CODIGO
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY C.CODIGO, U.CODIGO, CO.CPF, C.DATA_HORA, C.DATA_HORA_SINCRONIZACAO, C.COD_UNIDADE, C.PLACA_VEICULO,
      C.KM_VEICULO, C.TEMPO_REALIZACAO, C.TIPO),

    RESPOSTAS_NOK AS (
      SELECT
        CR.COD_CHECKLIST AS COD_CHECKLIST,
        COUNT(CASE WHEN CR.RESPOSTA <> 'OK'
          THEN 1 END)    AS TOTAL_NOK,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'BAIXA'
          THEN 1 END)    AS TOTAL_BAIXAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'ALTA'
          THEN 1 END)    AS TOTAL_ALTAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'CRITICA'
          THEN 1 END)    AS TOTAL_CRITICAS
      FROM CHECKLIST_RESPOSTAS CR
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CR.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON CR.COD_CHECKLIST = C.CODIGO
      WHERE
        CR.RESPOSTA <> 'OK'
        AND C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY CR.COD_CHECKLIST
  )

SELECT
  C.NOME_UNIDADE,
  C.DATA_REALIZACAO_CHECK,
  C.HORARIO_REALIZACAO_CHECK,
  C.NOME_COLABORADOR,
  C.PLACA_VEICULO,
  C.KM_VEICULO,
  C.TEMPO_REALIZACAO_SEGUNDOS,
  C.TIPO_CHECKLIST,
  C.TOTAL_PERGUNTAS,
  COALESCE(RN.TOTAL_NOK, 0),
  COALESCE(RN.TOTAL_BAIXAS, 0),
  COALESCE(RN.TOTAL_ALTAS, 0),
  COALESCE(RN.TOTAL_CRITICAS, 0)
FROM CHECKLITS C
  LEFT JOIN RESPOSTAS_NOK RN
    ON C.COD_CHECKLIST = RN.COD_CHECKLIST
ORDER BY
  C.NOME_UNIDADE,
  C.DATA_HORA_SINCRONIZACAO DESC;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################## ALTERA FUNCTION DO RELATÓRIO DE ESTRATIFICAÇÃO DE RESPOSTAS NOK #############################
--###################################### ORDENA POR DATA_HORA_SINCRONIZACAO ############################################
--######################################################################################################################
--######################################################################################################################
-- PL-1796

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO CHARACTER VARYING,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"          TEXT,
    "CODIGO CHECKLIST" BIGINT,
    "DATA"             CHARACTER VARYING,
    "PLACA"            CHARACTER VARYING,
    "TIPO"             TEXT,
    "KM"               BIGINT,
    "NOME"             CHARACTER VARYING,
    "PERGUNTA"         CHARACTER VARYING,
    "ALTERNATIVA"      CHARACTER VARYING,
    "RESPOSTA"         CHARACTER VARYING,
    "PRIORIDADE"       CHARACTER VARYING,
    "PRAZO EM HORAS"   INTEGER)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                              AS NOME_UNIDADE,
  C.CODIGO                                                                            AS COD_CHECKLIST,
  TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') AS DATA_HORA_CHECK,
  C.PLACA_VEICULO                                                                     AS PLACA_VEICULO,
  CASE WHEN C.TIPO = 'S'
    THEN 'Saída'
  ELSE 'Retorno' END                                                                  AS TIPO_CHECKLIST,
  C.KM_VEICULO                                                                        AS KM_VEICULO,
  CO.NOME                                                                             AS NOME_REALIZADOR_CHECK,
  CP.PERGUNTA                                                                         AS DESCRICAO_PERGUNTA,
  CAP.ALTERNATIVA                                                                     AS DESCRICAO_ALTERNATIVA,
  CR.RESPOSTA                                                                         AS RESPOSTA,
  CAP.PRIORIDADE                                                                      AS PRIORIDADE,
  PRIO.PRAZO                                                                          AS PRAZO
FROM CHECKLIST C
  JOIN VEICULO V
    ON V.PLACA = C.PLACA_VEICULO
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_PERGUNTA = CP.CODIGO
  JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
    ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
  JOIN CHECKLIST_RESPOSTAS CR
    ON C.CODIGO = CR.COD_CHECKLIST
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
       AND CR.RESPOSTA <> 'OK'
  JOIN COLABORADOR CO
    ON CO.CPF = C.CPF_COLABORADOR
  JOIN UNIDADE U
    ON C.COD_UNIDADE = U.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA_SINCRONIZACAO DESC, C.CODIGO ASC
$$;


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################





--######################################################################################################################
--######################################################################################################################
--###################################### ADICIONA VIDA ATUAL E TOTAL NA BUSCA DE UMA AFERIÇÃO ##########################
--######################################################################################################################
--######################################################################################################################
-- PL-1677

DROP FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(
F_COD_UNIDADE BIGINT,
F_COD_AFERICAO BIGINT,
F_TZ_UNIDADE TEXT );

CREATE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(
  F_COD_UNIDADE  BIGINT,
  F_COD_AFERICAO BIGINT,
  F_TZ_UNIDADE   TEXT)
  RETURNS TABLE(
    COD_AFERICAO                 BIGINT,
    COD_UNIDADE                  BIGINT,
    DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO                TEXT,
    KM_VEICULO                   BIGINT,
    TEMPO_REALIZACAO             BIGINT,
    TIPO_PROCESSO_COLETA         TEXT,
    TIPO_MEDICAO_COLETADA        TEXT,
    CPF                          TEXT,
    NOME                         TEXT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    ALTURA_SULCO_INTERNO         REAL,
    PRESSAO_PNEU                 INTEGER,
    POSICAO_PNEU                 INTEGER,
    VIDA_PNEU_MOMENTO_AFERICAO   INTEGER,
    VIDAS_TOTAL_PNEU             INTEGER,
    CODIGO_PNEU                  BIGINT,
    CODIGO_PNEU_CLIENTE          TEXT,
    PRESSAO_RECOMENDADA          REAL)
LANGUAGE SQL
AS $$
SELECT
  A.CODIGO                              AS COD_AFERICAO,
  A.COD_UNIDADE                         AS COD_UNIDADE,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
  A.PLACA_VEICULO :: TEXT               AS PLACA_VEICULO,
  A.KM_VEICULO                          AS KM_VEICULO,
  A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO,
  A.TIPO_PROCESSO_COLETA :: TEXT        AS TIPO_PROCESSO_COLETA,
  A.TIPO_MEDICAO_COLETADA :: TEXT       AS TIPO_MEDICAO_COLETADA,
  C.CPF :: TEXT                         AS CPF,
  C.NOME :: TEXT                        AS NOME,
  AV.ALTURA_SULCO_CENTRAL_INTERNO       AS ALTURA_SULCO_CENTRAL_INTERNO,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO       AS ALTURA_SULCO_CENTRAL_EXTERNO,
  AV.ALTURA_SULCO_EXTERNO               AS ALTURA_SULCO_EXTERNO,
  AV.ALTURA_SULCO_INTERNO               AS ALTURA_SULCO_INTERNO,
  AV.PSI :: INT                         AS PRESSAO_PNEU,
  AV.POSICAO                            AS POSICAO_PNEU,
  AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU_MOMENTO_AFERICAO,
  P.VIDA_TOTAL                          AS VIDAS_TOTAL_PNEU,
  P.CODIGO                              AS CODIGO_PNEU,
  P.CODIGO_CLIENTE :: TEXT              AS CODIGO_PNEU_CLIENTE,
  P.PRESSAO_RECOMENDADA                 AS PRESSAO_RECOMENDADA
FROM AFERICAO A
  JOIN AFERICAO_VALORES AV
    ON A.CODIGO = AV.COD_AFERICAO
  JOIN PNEU_ORDEM PO
    ON AV.POSICAO = PO.POSICAO_PROLOG
  JOIN PNEU P
    ON P.CODIGO = AV.COD_PNEU
  JOIN MODELO_PNEU MO
    ON MO.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP
    ON MP.CODIGO = MO.COD_MARCA
  JOIN COLABORADOR C
    ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO
      AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################ CRIA ESTRUTURA PARA DELEÇÃO LÓGICA DE AFERIÇÕES #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-1947

-- ALTERA O NOME DA TABELA DE AFERIÇÃO
ALTER TABLE AFERICAO
  RENAME TO AFERICAO_DATA;

-- ALTERA O NOME DA TABELA DE VALORES DE AFERIÇÃO
ALTER TABLE AFERICAO_VALORES
  RENAME TO AFERICAO_VALORES_DATA;

-- ALTERA O NOME DA TABELA DE MANUTENÇÃO DE AFERIÇÃO
ALTER TABLE AFERICAO_MANUTENCAO
  RENAME TO AFERICAO_MANUTENCAO_DATA;

-- ADICIONA A COLUNA DELETADO NA TABELA DE AFERIÇÃO
ALTER TABLE AFERICAO_DATA
  ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- ADICIONA A COLUNA DELETADO NA TABELA DE VALORES DE AFERIÇÃO
ALTER TABLE AFERICAO_VALORES_DATA
  ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- ADICIONA A COLUNA DELETADO NA TABELA DE MANUTENÇÃO DE AFERIÇÃO
ALTER TABLE AFERICAO_MANUTENCAO_DATA
  ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- CRIA A VIEW DE AFERIÇÕES PARA MANTER O RESTANTE DAS DEPENDÊNCIAS FUNCIONANDO, SEM LISTAR AS AFERIÇÕES DELETADAS
-- LOGICAMENTE.
CREATE OR REPLACE VIEW AFERICAO AS
  SELECT
    AD.CODIGO,
    AD.DATA_HORA,
    AD.PLACA_VEICULO,
    AD.CPF_AFERIDOR,
    AD.KM_VEICULO,
    AD.TEMPO_REALIZACAO,
    AD.TIPO_MEDICAO_COLETADA,
    AD.COD_UNIDADE,
    AD.TIPO_PROCESSO_COLETA
  FROM AFERICAO_DATA AD
  WHERE AD.DELETADO = FALSE;

-- CRIA A VIEW DE VALORES DE AFERIÇÕES PARA MANTER O RESTANTE DAS DEPENDÊNCIAS FUNCIONANDO, SEM LISTAR OS VALORES
-- DELETADOS LOGICAMENTE.
CREATE OR REPLACE VIEW AFERICAO_VALORES AS
  SELECT
    AV.COD_AFERICAO,
    AV.COD_PNEU,
    AV.COD_UNIDADE,
    AV.ALTURA_SULCO_CENTRAL_INTERNO,
    AV.ALTURA_SULCO_EXTERNO,
    AV.ALTURA_SULCO_INTERNO,
    AV.PSI,
    AV.POSICAO,
    AV.VIDA_MOMENTO_AFERICAO,
    AV.ALTURA_SULCO_CENTRAL_EXTERNO
  FROM AFERICAO_VALORES_DATA AV
  WHERE AV.DELETADO = FALSE;

-- CRIA A VIEW DE MANUTENÇÃO DE AFERIÇÕES PARA MANTER O RESTANTE DAS DEPENDÊNCIAS FUNCIONANDO, SEM LISTAR OS ITENS
-- DELETADOS LOGICAMENTE.
CREATE OR REPLACE VIEW AFERICAO_MANUTENCAO AS
  SELECT
    AM.COD_AFERICAO,
    AM.COD_PNEU,
    AM.COD_UNIDADE,
    AM.TIPO_SERVICO,
    AM.DATA_HORA_RESOLUCAO,
    AM.CPF_MECANICO,
    AM.QT_APONTAMENTOS,
    AM.PSI_APOS_CONSERTO,
    AM.KM_MOMENTO_CONSERTO,
    AM.COD_ALTERNATIVA,
    AM.COD_PNEU_INSERIDO,
    AM.CODIGO,
    AM.COD_PROCESSO_MOVIMENTACAO,
    AM.TEMPO_REALIZACAO_MILLIS,
    AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO
  FROM AFERICAO_MANUTENCAO_DATA AM
  WHERE AM.DELETADO = FALSE;

-- CRIA FUNÇÃO PARA DELETAR AFERIÇÕES E DEPENDÊNCIAS LOGICAMENTE.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_DELETA_AFERICAO(
      F_COD_UNIDADE       BIGINT,
      F_PLACA             TEXT,
      F_CODIGO_AFERICAO   BIGINT,
  OUT AVISO_AFERICAO_DELETADA TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  IF ((SELECT COUNT(codigo) FROM AFERICAO_DATA WHERE CODIGO = F_CODIGO_AFERICAO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade %, Placa % e Código %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;

  -- DELETA AFERIÇÃO.
  UPDATE AFERICAO_DATA
  SET DELETADO = TRUE
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA
        AND CODIGO = F_CODIGO_AFERICAO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO VALORES.
  UPDATE AFERICAO_VALORES_DATA
  SET DELETADO = TRUE
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
  IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                          FROM AFERICAO_VALORES_DATA AVD
                                            WHERE AVD.COD_UNIDADE = F_COD_UNIDADE
                                                  AND AVD.COD_AFERICAO = F_CODIGO_AFERICAO) > 0))
  THEN
    RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO MANUTENÇÃO.
  -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
  UPDATE AFERICAO_MANUTENCAO_DATA
  SET DELETADO = TRUE
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO;

  SELECT 'AFERIÇÃO DELETADA: '
         || F_CODIGO_AFERICAO
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_AFERICAO_DELETADA;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################# CRIA ESTRUTURA PARA DELEÇÃO LÓGICA DE VEÍCULOS #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-1946

-- Altera o nome da tabela.
ALTER TABLE VEICULO RENAME TO VEICULO_DATA;

-- Adiciona a coluna.
ALTER TABLE VEICULO_DATA ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- Cria view de veículo.
CREATE OR REPLACE VIEW VEICULO AS
  SELECT
    V.PLACA,
    V.COD_UNIDADE,
    V.KM,
    V.STATUS_ATIVO,
    V.COD_TIPO,
    V.COD_MODELO,
    V.COD_EIXOS,
    V.DATA_HORA_CADASTRO,
    V.COD_UNIDADE_CADASTRO,
    V.CODIGO
  FROM VEICULO_DATA V
  WHERE V.DELETADO = FALSE;

-- Cria function para "deleção" de veículo.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_DELETA_VEICULO(
      F_COD_UNIDADE            BIGINT,
      F_COD_VEICULO            BIGINT,
      F_PLACA                  TEXT,
  OUT AVISO_VEICULO_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  IF ((SELECT COUNT(CODIGO) FROM VEICULO_DATA WHERE CODIGO = F_COD_VEICULO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA = F_PLACA) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum veículo encontrado com estes parâmetros: Código %, Placa % e Unidade %', F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
  END IF;

  IF ((SELECT COUNT(VP.PLACA)
       FROM VEICULO_PNEU VP
       WHERE VP.PLACA = F_PLACA AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
  THEN
    RAISE EXCEPTION 'O veículo de placa % não pode ser deletado pois possui pneus aplicados', F_PLACA;
  END IF;

  -- Deleta veículo.
  UPDATE VEICULO_DATA
  SET DELETADO = TRUE
  WHERE CODIGO = F_COD_VEICULO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA = F_PLACA;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o veículo de código: %, placa: % e Unidade: %', F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'VEÍCULO DELETADO: '
         || F_COD_VEICULO
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_VEICULO_DELETADO;
END;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################### CRIA ESTRUTURA PARA DELEÇÃO LÓGICA DE COLABORADORES ##################################
--######################################################################################################################
--######################################################################################################################
-- PL-1945

-- Altera o nome da tabela.
ALTER TABLE COLABORADOR RENAME TO COLABORADOR_DATA;

-- Adiciona a coluna.
ALTER TABLE COLABORADOR_DATA ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- Cria view de colaborador.
CREATE OR REPLACE VIEW COLABORADOR AS
  SELECT
    C.CPF,
    C.MATRICULA_AMBEV,
    C.MATRICULA_TRANS,
    C.DATA_NASCIMENTO,
    C.DATA_ADMISSAO,
    C.DATA_DEMISSAO,
    C.STATUS_ATIVO,
    C.NOME,
    C.COD_EQUIPE,
    C.COD_FUNCAO,
    C.COD_UNIDADE,
    C.COD_PERMISSAO,
    C.COD_EMPRESA,
    C.COD_SETOR,
    C.PIS,
    C.DATA_HORA_CADASTRO,
    C.CODIGO,
    C.COD_UNIDADE_CADASTRO
  FROM COLABORADOR_DATA C
  WHERE C.DELETADO = FALSE;

-- Cria function para "deleção" de colaborador.
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_DELETA_COLABORADOR(
      F_COD_UNIDADE            BIGINT,
      F_COD_COLABORADOR        BIGINT,
      F_CPF                    BIGINT,
  OUT AVISO_COLABORADOR_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  IF ((SELECT COUNT(codigo) FROM COLABORADOR_DATA WHERE CODIGO = F_COD_COLABORADOR
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CPF = F_CPF) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum colaborador encontrado com estes parâmetros: Código %, CPF % e Unidade %', F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
  END IF;

  -- Deleta colaborador.
  UPDATE COLABORADOR_DATA
  SET DELETADO = TRUE
  WHERE CODIGO = F_COD_COLABORADOR
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CPF = F_CPF;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %', F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'COLABORADOR DELETADO: '
         || F_COD_COLABORADOR
         || ', CPF: '
         || F_CPF
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_COLABORADOR_DELETADO;
END;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### CRIA ESTRUTURA PARA DELEÇÃO LÓGICA DE PNEUS ######################################
--######################################################################################################################
--######################################################################################################################
-- PL-1973

--ALTERA O NOME DA TABELA DE PNEU
ALTER TABLE PNEU
  RENAME TO PNEU_DATA;

--ADICIONA A COLUNA DELETADO
ALTER TABLE PNEU_DATA
  ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

--REMOVE A CONSTRAINT PARA QUE O CLIENTE CONSIGA CADASTRAR OUTRO PNEU UTILIZANDO O MESMO CÓDIGO DE PNEU (DO CLIENTE)
ALTER TABLE PNEU_DATA
  DROP CONSTRAINT UNIQUE_PNEU_EMPRESA;

--ADICIONA UM ÍNDICE ÚNICO PARA OS CÓDIGOS DE PNEUS (DO CLIENTE) APENAS PARA PNEUS QUE NÃO FORAM DELETADOS LOGICAMENTE
CREATE UNIQUE INDEX UNIQUE_PNEU_EMPRESA
  ON PNEU_DATA (CODIGO_CLIENTE, COD_EMPRESA)
  WHERE (DELETADO IS FALSE);

--CRIA A VIEW DE PNEUS PARA MANTER O RESTANTE DAS DEPENDÊNCIAS FUNCIONANDO, SEM LISTAR OS PNEUS DELETADOS LOGICAMENTE
CREATE OR REPLACE VIEW PNEU AS
  SELECT
    P.CODIGO_CLIENTE,
    P.COD_MODELO,
    P.COD_DIMENSAO,
    P.PRESSAO_RECOMENDADA,
    P.PRESSAO_ATUAL,
    P.ALTURA_SULCO_INTERNO,
    P.ALTURA_SULCO_CENTRAL_INTERNO,
    P.ALTURA_SULCO_EXTERNO,
    P.COD_UNIDADE,
    P.STATUS,
    P.VIDA_ATUAL,
    P.VIDA_TOTAL,
    P.COD_MODELO_BANDA,
    P.ALTURA_SULCO_CENTRAL_EXTERNO,
    P.DOT,
    P.VALOR,
    P.DATA_HORA_CADASTRO,
    P.PNEU_NOVO_NUNCA_RODADO,
    P.CODIGO,
    P.COD_EMPRESA,
    P.COD_UNIDADE_CADASTRO
  FROM PNEU_DATA P
  WHERE P.DELETADO = FALSE;

--CRIA FUNÇÃO PARA DELETAR PNEUS LOGICAMENTE
CREATE OR REPLACE FUNCTION FUNC_PNEU_DELETA_PNEU(
      F_COD_UNIDADE       BIGINT,
      F_CODIGO            BIGINT,
      F_CODIGO_CLIENTE    TEXT,
  OUT AVISO_PNEU_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
  F_STATUS_PNEU_ANALISE TEXT := 'ANALISE';
BEGIN

  -- Verifica se o pneu existe.
  IF ((SELECT COUNT(CODIGO)
       FROM PNEU_DATA
       WHERE CODIGO = F_CODIGO
             AND COD_UNIDADE = F_COD_UNIDADE
             AND CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Verifica se o pneu está aplicado.
  IF ((SELECT COUNT(VP.PLACA)
       FROM VEICULO_PNEU VP
       WHERE VP.COD_PNEU = F_CODIGO AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
  THEN
    RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Verifica se o pneu está em análise.
  IF ((SELECT COUNT(codigo)
       FROM PNEU_DATA
       WHERE CODIGO = F_CODIGO
             AND COD_UNIDADE = F_COD_UNIDADE
             AND CODIGO_CLIENTE = F_CODIGO_CLIENTE
             AND STATUS = F_STATUS_PNEU_ANALISE) > 0)
  THEN
    RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Deleta pneu.
  UPDATE PNEU_DATA
  SET DELETADO = TRUE
  WHERE CODIGO = F_CODIGO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'PNEU DELETADO: '
         || F_CODIGO
         || ', CÓDIGO DO CLIENTE: '
         || F_CODIGO_CLIENTE
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_PNEU_DELETADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################### ALTERA A FUNCTION DO RELATÓRIO DE SERVIÇOS FECHADOS ##################################
--##################### ADICIONA OS SERVIÇOS FECHADOS AUTOMATICAMENTE E COLUNA PARA IDENTIFICAR ########################
--######################################################################################################################
--######################################################################################################################
-- PL-1864

DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(TEXT[], DATE, DATE);

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(
  F_COD_UNIDADE  TEXT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE DO SERVIÇO"               TEXT,
    "DATA AFERIÇÃO"                    TEXT,
    "DATA RESOLUÇÃO"                   TEXT,
    "HORAS PARA RESOLVER"              DOUBLE PRECISION,
    "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
    "PLACA"                            TEXT,
    "KM AFERIÇÃO"                      BIGINT,
    "KM CONSERTO"                      BIGINT,
    "KM PERCORRIDO"                    BIGINT,
    "COD PNEU"                         CHARACTER VARYING,
    "PRESSÃO RECOMENDADA"              REAL,
    "PRESSÃO AFERIÇÃO"                 TEXT,
    "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
    "PRESSÃO INSERIDA"                 TEXT,
    "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
    "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
    "SERVIÇO"                          TEXT,
    "MECÂNICO"                         TEXT,
    "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
    "FECHADO AUTOMATICAMENTE"          TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                                      AS UNIDADE_SERVICO,
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
          'DD/MM/YYYY HH24:MI:SS')                                                                            AS DATA_HORA_AFERICAO,
  TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
          'DD/MM/YYYY HH24:MI:SS')                                                                            AS DATA_HORA_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
        3600)                                                                                                 AS HORAS_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
        60)                                                                                                   AS MINUTOS_RESOLUCAO,
  A.PLACA_VEICULO,
  A.KM_VEICULO                                                                                                AS KM_AFERICAO,
  AM.KM_MOMENTO_CONSERTO,
  AM.KM_MOMENTO_CONSERTO -
  A.KM_VEICULO                                                                                                AS KM_PERCORRIDO,
  P.CODIGO_CLIENTE                                                                                            AS CODIGO_CLIENTE_PNEU,
  P.PRESSAO_RECOMENDADA                                                                                       AS PRESSAO_RECOMENDADA_PNEU,
  COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.',','),'-')                                         AS PSI_AFERICAO,
  COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',','),'-')  AS DISPERSAO_PRESSAO_ANTES,
  COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.',','),'-')                           AS PSI_POS_CONSERTO,
  COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
            ','),'-')                                                                                         AS DISPERSAO_PRESSAO_DEPOIS,
  COALESCE(PON.NOMENCLATURA,'-')                                                                              AS POSICAO,
  AM.TIPO_SERVICO,
  COALESCE(INITCAP(C.NOME),'-')                                                                               AS NOME_MECANICO,
  COALESCE(AA.ALTERNATIVA,'-')                                                                                AS PROBLEMA_APONTADO,
  F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, 'Sim' :: TEXT, 'Não')                                                 AS TIPO_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
  JOIN UNIDADE U
    ON AM.COD_UNIDADE = U.CODIGO
  JOIN AFERICAO_VALORES AV
    ON AM.COD_UNIDADE = AV.COD_UNIDADE
       AND AM.COD_AFERICAO = AV.COD_AFERICAO
       AND AM.COD_PNEU = AV.COD_PNEU
  JOIN AFERICAO A
    ON A.CODIGO = AV.COD_AFERICAO
  LEFT JOIN COLABORADOR C
    ON AM.CPF_MECANICO = C.CPF
  JOIN PNEU P
    ON P.CODIGO = AV.COD_PNEU
  LEFT JOIN VEICULO_PNEU VP
    ON VP.COD_PNEU = P.CODIGO
       AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
    ON AA.CODIGO = AM.COD_ALTERNATIVA
  LEFT JOIN VEICULO V
    ON V.PLACA = VP.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
    ON PON.COD_UNIDADE = P.COD_UNIDADE
       AND PON.COD_TIPO_VEICULO = V.COD_TIPO
       AND PON.POSICAO_PROLOG = AV.POSICAO
WHERE AV.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
      AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;

--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
-- PL-1952
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_RELATORIO_LISTAGEM_COLABORADORES_BY_UNIDADE(
  F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    UNIDADE                     TEXT,
    CPF                         TEXT,
    COLABORADOR                 TEXT,
    "DATA NASCIMENTO"           TEXT,
    PIS                         TEXT,
    CARGO                       TEXT,
    SETOR                       TEXT,
    EQUIPE                      TEXT,
    "STATUS"                    TEXT,
    "DATA ADMISSÃO"             TEXT,
    "DATA DEMISSÃO"             TEXT,
    "QTD PERMISSÕES ASSOCIADAS" BIGINT,
    "MATRÍCULA AMBEV"           TEXT,
    "MATRÍCULA TRANSPORTADORA"  TEXT,
    "NÍVEL ACESSO INFORMAÇÃO"   TEXT,
    "DATA/HORA CADASTRO"        TEXT
  )
LANGUAGE PLPGSQL
AS $$

BEGIN
  RETURN QUERY
  SELECT
    U.NOME :: TEXT                                                                        AS NOME_UNIDADE,
    LPAD(CO.CPF :: TEXT, 11, '0')                                                         AS CPF_COLABORADOR,
    CO.NOME :: TEXT                                                                       AS NOME_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_NASCIMENTO, 'DD/MM/YYYY'), '-')                              AS DATA_NASCIMENTO_COLABORADOR,
    COALESCE(LPAD(CO.PIS :: TEXT, 12, '0'), '-')                                          AS PIS_COLABORADOR,
    F.NOME :: TEXT                                                                        AS NOME_CARGO,
    SE.NOME :: TEXT                                                                       AS NOME_SETOR,
    E.NOME :: TEXT                                                                        AS NOME_EQUIPE,
    F_IF(CO.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)                             AS STATUS_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_ADMISSAO, 'DD/MM/YYYY'), '-')                                AS DATA_ADMISSAO_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_DEMISSAO, 'DD/MM/YYYY'), '-')                                AS DATA_DEMISSAO_COLABORADOR,
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    -- CONSIDERAMOS APENAS AS PERMISSÕES DE PILARES LIBERADOS PARA A UNIDADE DO COLABORADOR.
                    AND CFP.COD_PILAR_PROLOG IN (SELECT UPP.COD_PILAR
                                                 FROM UNIDADE_PILAR_PROLOG UPP
                                                 WHERE UPP.COD_UNIDADE = CO.COD_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS,
    COALESCE(CO.MATRICULA_AMBEV :: TEXT,
             '-')                                                                         AS MATRICULA_AMBEV_COLABORADOR,
    COALESCE(CO.MATRICULA_TRANS :: TEXT,
             '-')                                                                         AS MATRICULA_TRANSPORTADORA_COLABORADOR,
    PE.DESCRICAO :: TEXT                                                                  AS DESCRICAO_PERMISSAO,
    COALESCE(TO_CHAR(CO.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(CO.COD_UNIDADE),
                     'DD/MM/YYYY HH24:MI'),
             '-')                                                                         AS DATA_HORA_CADASTRO_COLABORADOR
  FROM COLABORADOR CO
    JOIN UNIDADE U
      ON CO.COD_UNIDADE = U.CODIGO
    JOIN FUNCAO F
      ON CO.COD_FUNCAO = F.CODIGO
    JOIN SETOR SE
      ON CO.COD_UNIDADE = SE.COD_UNIDADE AND CO.COD_SETOR = SE.CODIGO
    JOIN EQUIPE E
      ON CO.COD_EQUIPE = E.CODIGO
    JOIN PERMISSAO PE
      ON CO.COD_PERMISSAO = PE.CODIGO
    JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON CO.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
         AND CO.COD_UNIDADE = CFP.COD_UNIDADE
  WHERE CO.COD_UNIDADE = ANY (F_COD_UNIDADES)
  GROUP BY
    U.NOME,
    CO.CPF,
    CO.NOME,
    F.NOME,
    SE.NOME,
    E.NOME,
    CO.STATUS_ATIVO,
    CO.COD_UNIDADE,
    CO.DATA_NASCIMENTO,
    CO.DATA_ADMISSAO,
    CO.DATA_DEMISSAO,
    CO.MATRICULA_AMBEV,
    CO.MATRICULA_TRANS,
    CO.PIS,
    PE.DESCRICAO,
    CO.DATA_HORA_CADASTRO
  ORDER BY
    U.NOME,
    CO.NOME,
    F.NOME,
    CO.STATUS_ATIVO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-1952
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"                              TEXT,
    "NOME"                                 TEXT,
    "FUNÇÃO"                               TEXT,
    "CHECKS SAÍDA"                         BIGINT,
    "CHECKS RETORNO"                       BIGINT,
    "TOTAL"                                BIGINT,
    "MÉDIA TEMPO DE REALIZAÇÃO (SEGUNDOS)" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                AS NOME_UNIDADE,
  CO.NOME                               AS NOME,
  F.NOME                                AS FUNCAO,
  SUM(CASE WHEN C.TIPO = 'S'
    THEN 1
      ELSE 0 END)                       AS CHECKS_SAIDA,
  SUM(CASE WHEN C.TIPO = 'R'
    THEN 1
      ELSE 0 END)                       AS CHECKS_RETORNO,
  COUNT(C.TIPO)                         AS TOTAL_CHECKS,
  ROUND(AVG(C.TEMPO_REALIZACAO) / 1000) AS MEDIA_SEGUNDOS_REALIZACAO
FROM CHECKLIST C
  JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR
  JOIN FUNCAO F ON F.CODIGO = CO.COD_FUNCAO AND F.COD_EMPRESA = CO.COD_EMPRESA
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CO.CPF, CO.NOME, F.CODIGO
ORDER BY U.NOME, CO.NOME
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-1703
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE TEXT [] );

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT [])
  RETURNS TABLE(
    "UNIDADE ALOCADO"               TEXT,
    "PNEU"                          TEXT,
    "STATUS ATUAL"                  TEXT,
    "MARCA PNEU"                    TEXT,
    "MODELO PNEU"                   TEXT,
    "MEDIDAS"                       TEXT,
    "PLACA APLICADO"                TEXT,
    "MARCA VEÍCULO"                 TEXT,
    "MODELO VEÍCULO"                TEXT,
    "TIPO VEÍCULO"                  TEXT,
    "POSIÇÃO APLICADO"              TEXT,
    "SULCO INTERNO"                 TEXT,
    "SULCO CENTRAL INTERNO"         TEXT,
    "SULCO CENTRAL EXTERNO"         TEXT,
    "SULCO EXTERNO"                 TEXT,
    "PRESSÃO (PSI)"                 TEXT,
    "VIDA ATUAL"                    TEXT,
    "DOT"                           TEXT,
    "ÚLTIMA AFERIÇÃO"               TEXT,
    "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Essa CTE busca o código da última aferição de cada pneu.
  -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
  RETURN QUERY
  WITH CODS_AFERICOES AS (
      SELECT
        AV.COD_PNEU   AS COD_PNEU_AFERIDO,
        MAX(A.CODIGO) AS COD_AFERICAO
      FROM AFERICAO A
        JOIN AFERICAO_VALORES AV
          ON AV.COD_AFERICAO = A.CODIGO
        JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
      WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
      GROUP BY AV.COD_PNEU
  ),

      ULTIMAS_AFERICOES AS (
        SELECT
          CA.COD_PNEU_AFERIDO    AS COD_PNEU_AFERIDO,
          A.DATA_HORA            AS DATA_HORA_AFERICAO,
          A.COD_UNIDADE          AS COD_UNIDADE_AFERICAO,
          A.TIPO_PROCESSO_COLETA AS TIPO_PROCESSO_COLETA
        FROM CODS_AFERICOES CA
          JOIN AFERICAO A ON A.CODIGO = CA.COD_AFERICAO)

  SELECT
    U.NOME :: TEXT                                                                        AS UNIDADE_ALOCADO,
    P.CODIGO_CLIENTE :: TEXT                                                              AS COD_PNEU,
    P.STATUS :: TEXT                                                                      AS STATUS_ATUAL,
    MAP.NOME :: TEXT                                                                      AS NOME_MARCA,
    MP.NOME :: TEXT                                                                       AS NOME_MODELO,
    ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
     DP.ARO)                                                                              AS MEDIDAS,
    COALESCE(VP.PLACA, '-') :: TEXT                                                       AS PLACA,
    COALESCE(MARV.NOME, '-') :: TEXT                                                      AS MARCA_VEICULO,
    COALESCE(MODV.NOME, '-') :: TEXT                                                      AS MODELO_VEICULO,
    COALESCE(VT.NOME, '-') :: TEXT                                                        AS TIPO_VEICULO,
    COALESCE(PONU.NOMENCLATURA, '-') :: TEXT                                              AS POSICAO_PNEU,
    REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_INTERNO,
    REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
            ',')                                                                          AS SULCO_CENTRAL_INTERNO,
    REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
            ',')                                                                          AS SULCO_CENTRAL_EXTERNO,
    REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_EXTERNO,
    REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                      AS PRESSAO_ATUAL,
    P.VIDA_ATUAL :: TEXT                                                                  AS VIDA_ATUAL,
    COALESCE(P.DOT, '-') :: TEXT                                                          AS DOT,
    COALESCE(TO_CHAR(UA.DATA_HORA_AFERICAO AT TIME ZONE
                     tz_unidade(UA.COD_UNIDADE_AFERICAO),
                     'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                              AS ULTIMA_AFERICAO,
    CASE
    WHEN UA.TIPO_PROCESSO_COLETA IS NULL
      THEN 'Nunca Aferido'
    WHEN UA.TIPO_PROCESSO_COLETA = 'PLACA'
      THEN 'Aferido em uma placa'
    ELSE 'Aferido Avulso (em estoque)' END                                                AS TIPO_PROCESSO_ULTIMA_AFERICAO
  FROM PNEU P
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    LEFT JOIN VEICULO_PNEU VP
      ON P.CODIGO = VP.COD_PNEU
         AND P.COD_UNIDADE = VP.COD_UNIDADE
    LEFT JOIN VEICULO V
      ON VP.PLACA = V.PLACA
         AND VP.COD_UNIDADE = V.COD_UNIDADE
    LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
      ON PONU.COD_UNIDADE = V.COD_UNIDADE
         AND PONU.COD_TIPO_VEICULO = V.COD_TIPO
         AND PONU.POSICAO_PROLOG = VP.POSICAO
    LEFT JOIN VEICULO_TIPO VT
      ON VT.CODIGO = V.COD_TIPO
    LEFT JOIN MODELO_VEICULO MODV
      ON MODV.CODIGO = V.COD_MODELO
    LEFT JOIN MARCA_VEICULO MARV
      ON MARV.CODIGO = MODV.COD_MARCA
    LEFT JOIN ULTIMAS_AFERICOES UA
      ON UA.COD_PNEU_AFERIDO = P.CODIGO
  WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
  ORDER BY U.NOME, P.CODIGO_CLIENTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--###################################### INCREMENTA AS TABELAS DE PERMISSÕES ###########################################
--##################### ADICIONA AGRUPAMENTO POR FUNCIONALIDADE E CATEGORIZAÇÃO POR impacto ########################
--######################################################################################################################
--######################################################################################################################
-- PL-2004

-- Representa o impacto da permissão no ProLog.
CREATE TYPE PROLOG_IMPACTO_PERMISSAO_TYPE AS ENUM (
  'BAIXO',
  'MEDIO',
  'ALTO',
  'CRITICO');

-- ADICIONA COLUNA PARA ARMAZENAR O IMPACTO DA PERMISSÃO
ALTER TABLE FUNCAO_PROLOG_V11 ADD IMPACTO PROLOG_IMPACTO_PERMISSAO_TYPE;

-- ADICIONA COLUNA PARA ARMAZENAR O CÓDIGO DE AGRUPAMENTO
ALTER TABLE FUNCAO_PROLOG_V11 ADD COD_AGRUPAMENTO SMALLINT;

-- ADICIONA COLUNA PARA ARMAZENAR A DESCRIÇÃO DA PERMISSÃO
ALTER TABLE FUNCAO_PROLOG_V11 ADD DESCRICAO TEXT;

-- SCRIPT DE ATUALIZAÇÃO DOS REGISTROS DE PERMISSÕES PARA INSERIR A DESCRIÇÃO E IMPACTO
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário configurar informações de parametrização relacionados ao processo de aferição da unidade.', IMPACTO = 'CRITICO' WHERE codigo = 100;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que realize processos de aferição de placas cadastradas na unidade.', IMPACTO = 'ALTO' WHERE codigo = 18;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que realize processos de aferição de pneus presentes em estoque de forma avulsa.', IMPACTO = 'ALTO' WHERE codigo = 140;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar todas as aferições realizadas nas unidades à qual possui acesso.', IMPACTO = 'ALTO' WHERE codigo = 117;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar/editar informações de um evento criado para a sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 319;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário criar um novo evento para adicionar ao Calendário de sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 324;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os eventos adicionados ao Calendário de sua unidade.', IMPACTO = 'BAIXO' WHERE codigo = 32;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário vincular permissões de funcionalidades no ProLog à cargos.', IMPACTO = 'CRITICO' WHERE codigo = 329;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar, sem alterar, as permissões que estão vinculadas à cada cargo.', IMPACTO = 'MEDIO' WHERE codigo = 328;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário a edição dos modelos, possibilitando a  configuração dos mesmos, mudanças, alterações e inclusões de perguntas e respostas, ativação e inativação dos modelo de Checklists presentes nas unidades às quais possui acesso.', IMPACTO = 'CRITICO' WHERE codigo = 114;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso ao processo de criação de um novo modelo de Checklist para as unidades às quais possui acesso.', IMPACTO = 'ALTO' WHERE codigo = 113;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar o fechamento das Ordens de Serviços geradas em sua unidade a partir das realizações de Checklists.', IMPACTO = 'ALTO' WHERE codigo = 13;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar o processo de Checklists para os modelos aos quais o mesmo possui acesso em sua unidade.', IMPACTO = 'BAIXO' WHERE codigo = 11;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem do Farol de Checklist, que indica, de acordo com os Checklists realizados no dia, quais placas estão aptas ou não a partirem para sua rota.', IMPACTO = 'ALTO' WHERE codigo = 10;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem de modelos de Checklists presentes em sua unidade e suas respectivas informações de configuração.', IMPACTO = 'MEDIO' WHERE codigo = 112;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar as Ordens de Serviços geradas, nas unidades em que possui acesso à visualização, a partir das realizações de Checklist.', IMPACTO = 'MEDIO' WHERE codigo = 12;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados à funcionalidade de Checklist e à informações gráficas presentes na DashBoard.', IMPACTO = 'MEDIO' WHERE codigo = 121;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar todas as Realizações de Checklist ocorridas.', IMPACTO = 'ALTO' WHERE codigo = 118;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar/alterar informações de cadastros de usuários.', IMPACTO = 'ALTO' WHERE codigo = 325;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar novos usuários à sua empresa no ProLog.', IMPACTO = 'ALTO' WHERE codigo = 310;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem de usuários cadastrados à sua unidade', IMPACTO = 'MEDIO' WHERE codigo = 316;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar, incluir, editar e excluir marcações realizadas pelos usuários de sua unidade.', IMPACTO = 'CRITICO' WHERE codigo = 338;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar configurações de Marcações cadastradas em sua unidade.', IMPACTO = 'CRITICO' WHERE codigo = 344;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário criar um novo Tipo de Marcação para a sua unidade.', IMPACTO = 'CRITICO' WHERE codigo = 340;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que torne um Tipo de Marcação cadastrado em sua unidade INATIVO.', IMPACTO = 'CRITICO' WHERE codigo = 341;
UPDATE funcao_prolog_v11 SET descricao = 'Permite aos usuários que realize o Controle de Marcações.', IMPACTO = 'BAIXO' WHERE codigo = 336;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os relatórios relacionados à "Controle de Marcações".', IMPACTO = 'MEDIO' WHERE codigo = 342;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar todas as marcações realizadas pelos colaboradores de sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 337;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar/alterar o nome de Equipes cadastradas em sua unidade', IMPACTO = 'ALTO' WHERE codigo = 313;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar novas Equipes à sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 311;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem de Equipes  cadastradas em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 317;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário deletar itens presentes na Escala Diária de sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 410;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar e editar itens presentes na Escala Diária de sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 413;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário a realização de download de planilha padrão e importação da mesma após preenchimento.', IMPACTO = 'ALTO' WHERE codigo = 411;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem das Escalas Diárias importadas no ProLog.', IMPACTO = 'MEDIO' WHERE codigo = 412;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar envio de mensagem na funcionalidade Fale Conosco', IMPACTO = 'BAIXO' WHERE codigo = 314;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar e responder as mensagens realizadas na funcionalidade Fale Conosco.', IMPACTO = 'ALTO' WHERE codigo = 315;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados ao Fale Conosco.', IMPACTO = 'MEDIO' WHERE codigo = 331;
UPDATE funcao_prolog_v11 SET descricao = 'Permite aos usuários visualizar todas as mensagens realizadas no Fale Conosco.', IMPACTO = 'ALTO' WHERE codigo = 322;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso à funcionalidade de Transferência de Pneus entre unidades da empresa. Importante: o usuário poderá realizar a transferência apenas entre unidades às quais possui visualização.', IMPACTO = 'CRITICO' WHERE codigo = 141;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que visualize os indicadores de resultado na operação de entrega para os motoristas e ajudantes de sua unidade.', IMPACTO = 'BAIXO' WHERE codigo = 40;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados aos indicadores de operação de entrega da unidade.', IMPACTO = 'MEDIO' WHERE codigo = 41;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário a importação dos arquivos 2art e Tracking para o sistema.', IMPACTO = 'ALTO' WHERE codigo = 42;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar dados dados presentes nos arquivos importados.', IMPACTO = 'MEDIO' WHERE codigo = 43;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar as metas  da unidade para cada um dos indicadores presente.', IMPACTO = 'ALTO' WHERE codigo = 44;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar as metas da unidade para cada indicador.', IMPACTO = 'MEDIO' WHERE codigo = 47;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso à funcionalidade de Movimentação de Pneus para que realize as seguintes ações de movimentação: Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)', IMPACTO = 'MEDIO' WHERE codigo = 142;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso à funcionalidade de Movimentação de Pneus para que realize as seguintes ações de movimentação: Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)', IMPACTO = 'ALTO' WHERE codigo = 143;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso à funcionalidade de Movimentação de Pneus para que realize as seguintes ações de movimentação: Descarte (Estoque ou Veículo ou Análise -> Descarte)', IMPACTO = 'CRITICO' WHERE codigo = 144;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar novos motivos de descarte.', IMPACTO = 'ALTO' WHERE codigo = 123;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar os motivos de descarte cadastros.', IMPACTO = 'ALTO' WHERE codigo = 124;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar as informações dos pneus cadastrados.', IMPACTO = 'ALTO' WHERE codigo = 17;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar novos pneus.', IMPACTO = 'ALTO' WHERE codigo = 15;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar o fechamento das Ordens de Serviços relacionadas à pneus geradas a partir de aferições realizadas.', IMPACTO = 'MEDIO' WHERE codigo = 19;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar vínculos de pneus ao veículo através de cadastro no aplicativo.', IMPACTO = 'MEDIO' WHERE codigo = 111;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem de pneus cadastrados em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 116;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar as Ordens de Serviços relacionadas à pneus geradas a partir de aferições realizadas.', IMPACTO = 'MEDIO' WHERE codigo = 119;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados às funcionalidades de pneus.', IMPACTO = 'MEDIO' WHERE codigo = 110;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar Tipos de Serviço de conserto aos quais os pneus podem ser submetidos.', IMPACTO = 'ALTO' WHERE codigo = 133;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar os Tipos de Serviço de conserto de pneus cadastrados.', IMPACTO = 'ALTO' WHERE codigo = 135;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os Tipos de Serviço de conserto de pneus cadastrados.', IMPACTO = 'MEDIO' WHERE codigo = 134;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar upload de arquivos com informações de Pré-Contracheques de colaboradores de sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 34;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que tenha acesso à listagem de pré-contracheques dos colaboradores de sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 35;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar seus ganhos de produtividade referentes ao cumprimento de suas metas.', IMPACTO = 'BAIXO' WHERE codigo = 45;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar o consolidado mensal da Produtividade para cada colaborador.', IMPACTO = 'ALTO' WHERE codigo = 46;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os relatórios referentes à Produtividade de Entrega de sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 48;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar o upload do arquivo de Prontuário do Condutor (Portal Gente e Gestão) no ProLog.', IMPACTO = 'ALTO' WHERE codigo = 333;
UPDATE funcao_prolog_v11 SET descricao = 'Permite aos condutores que visualizem a sua pontuação referente ao seu prontuário do condutor.', IMPACTO = 'BAIXO' WHERE codigo = 334;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuários visualizar as pontuações dos condutores cadastrados em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 335;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar/editar os modelos de Quiz cadastrados em sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 321;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário criar um novo modelo de Quiz à sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 37;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário que responda aos modelos de Quizzes disponíveis à ele.', IMPACTO = 'BAIXO' WHERE codigo = 36;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar a listagem de todos os modelos de Quizzes cadastradas em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 320;
UPDATE funcao_prolog_v11 SET descricao = 'Permite aos usuários visualizar os relatórios realizados pelos demais usuários em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 326;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados à parte de Quiz.', IMPACTO = 'MEDIO' WHERE codigo = 330;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar recapadoras utilizadas para o encaminhamento de pneus para conserto.', IMPACTO = 'MEDIO' WHERE codigo = 130;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar o cadastro de recapadoras.', IMPACTO = 'MEDIO' WHERE codigo = 132;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar as recapadoras cadastradas.', IMPACTO = 'MEDIO' WHERE codigo = 131;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário classificar um relato de segurança realizado como válido (ou não) para posterior fechamento.', IMPACTO = 'ALTO' WHERE codigo = 23;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar o fechamento de um relato de segurança aberto.', IMPACTO = 'ALTO' WHERE codigo = 24;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar um novo relato de segurança.', IMPACTO = 'BAIXO' WHERE codigo = 21;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar todos os relatos de segurança realizados em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 25;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os relatórios relacionados à "Relatos de Segurança".', IMPACTO = 'MEDIO' WHERE codigo = 26;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário realizar Solicitações de Folga', IMPACTO = 'BAIXO' WHERE codigo = 38;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário à responder as Solicitações de Folga realizadas em sua unidade.', IMPACTO = 'ALTO' WHERE codigo = 39;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar as Solicitações de Folga realizadas em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 327;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados à parte de Solicitações de Folga.', IMPACTO = 'MEDIO' WHERE codigo = 332;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário alterar/editar configurações dos Treinamentos  cadastrados em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 318;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar um novo modelo de Treinamento à sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 323;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário visualizar os Treinamentos cadastrados em sua unidade.', IMPACTO = 'MEDIO' WHERE codigo = 30;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados à parte de Treinamento.', IMPACTO = 'MEDIO' WHERE codigo = 343;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário editar/alterar dados de um veículo já cadastrado.', IMPACTO = 'MEDIO' WHERE codigo = 16;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário cadastrar um novo veículo.', IMPACTO = 'ALTO' WHERE codigo = 14;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário a visualização da listagem de veículos cadastrado à unidade.', IMPACTO = 'MEDIO' WHERE codigo = 115;
UPDATE funcao_prolog_v11 SET descricao = 'Permite ao usuário acesso aos relatórios relacionados à veículos.', IMPACTO = 'MEDIO' WHERE codigo = 122;


-- ADICIONA AS DESCRIÇÕES QUE FALTAM NAS PERMISSÕES.
-- Essas permissões não estavam no arquivo de mapeamento do Bruno.
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário visualizar seus ganhos de produtividade.', impacto = 'BAIXO' WHERE "codigo" = 415 AND "cod_pilar" = 4;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário editar registros de produtividade.', impacto = 'MEDIO' WHERE "codigo" = 416 AND "cod_pilar" = 4;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário deletar registros de produtividade.', impacto = 'ALTO' WHERE "codigo" = 418 AND "cod_pilar" = 4;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário inserir registros de produtividade.', impacto = 'MEDIO' WHERE "codigo" = 417 AND "cod_pilar" = 4;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário a visualização do ranking.', impacto = 'BAIXO' WHERE "codigo" = 33 AND "cod_pilar" = 3;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário visualizar os relatórios de produtividade.', impacto = 'MEDIO' WHERE "codigo" = 419 AND "cod_pilar" = 4;
UPDATE funcao_prolog_v11 SET "descricao" = 'Permite ao usuário a visualização de todos os registros de produtividade.', impacto = 'MEDIO' WHERE "codigo" = 414 AND "cod_pilar" = 4;

-- ALTERA
ALTER TABLE FUNCAO_PROLOG_V11 ALTER COLUMN IMPACTO SET NOT NULL;

-- ALTERA A COLUNA DE DESCRIÇÃO PARA NOTNULL.
ALTER TABLE FUNCAO_PROLOG_V11 ALTER COLUMN DESCRICAO SET NOT NULL;

-- CRIA A TABELA PARA ARMAZENAR OS NOMES DOS AGRUPAMENTOS DE PERMISSÕES
CREATE TABLE IF NOT EXISTS FUNCAO_PROLOG_AGRUPAMENTO (
  CODIGO    SMALLSERIAL  NOT NULL,
  NOME      VARCHAR(255) NOT NULL,
  COD_PILAR BIGINT       NOT NULL,
  CONSTRAINT PK_FUNCAO_PROLOG_AGRUPAMENTO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_FUNCAO_PROLOG_AGRUPAMENTO_PILAR_PROLOG FOREIGN KEY (COD_PILAR) REFERENCES PILAR_PROLOG (CODIGO),
  -- Constraint criada para podermos usar como FK na tabela de permissões do ProLog.
  CONSTRAINT UNIQUE_AGRUPAMENTO_PILAR UNIQUE(CODIGO, COD_PILAR)
);
COMMENT ON TABLE FUNCAO_PROLOG_AGRUPAMENTO IS 'Armazena os nomes dos grupos em que as permissões podem ser reunidas.';

-- ADICIONA OS NOMES DE AGRUPAMENTOS CONHECIDOS
INSERT INTO FUNCAO_PROLOG_AGRUPAMENTO (NOME, COD_PILAR)
VALUES ('Aferição', 1),
       ('Calendário', 3),
       ('Cargo', 3),
       ('Checklist', 1),
       ('Colaboradores', 3),
       ('Controle de Jornada', 3),
       ('Equipe', 3),
       ('Escala Diária', 4),
       ('Fale Conosco', 3),
       ('Geral', 1),
       ('Indicadores', 4),
       ('Mapa (2art) e Tracking', 4),
       ('Metas', 4),
       ('Movimentação', 1),
       ('Pneu', 1),
       ('Produtividade', 4),
       ('Prontuário do Condutor', 3),
       ('Pré-Contracheque', 3),
       ('Quiz', 3),
       ('Raízen Produtividade', 4),
       ('Ranking', 3),
       ('Recapadora', 1),
       ('Relato', 2),
       ('Solicitação de Folga', 3),
       ('Treinamento', 3),
       ('Veículo', 1);

-- CORRIGE OS NOMES DAS PERMISSÕES PARA FACILITAR O VÍNCULO COM OS AGRUPAMENTOS
-- PRECISA ESTAR ANTES DOS UPDATES DE CÓDIGO DE AGRUPAMENTO ABAIXO, PARA ELES FUNCIONAREM CORRETAMENTE.
UPDATE funcao_prolog_v11 SET funcao = 'Pneu - Tipo de Serviço - Cadastrar' WHERE codigo = 133;
UPDATE funcao_prolog_v11 SET funcao = 'Pneu - Tipo de Serviço - Visualizar' WHERE codigo = 134;
UPDATE funcao_prolog_v11 SET funcao = 'Pneu - Tipo de Serviço - Editar' WHERE codigo = 135;

-- ATUALIZA O VÍNCULO ENTRE A PERMISSÃO E O AGRUPAMENTO
-- Como a coluna na CODIGO na tabela FUNCAO_PROLOG_AGRUPAMENTO é um SMALLSERIAL, os códigos são criados na ordem abaixo pois
-- o insert dos agrupamentos é feito nessa ordem.
UPDATE funcao_prolog_v11 SET cod_agrupamento = 1 WHERE funcao LIKE 'Aferição -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 2 WHERE funcao LIKE 'Calendário -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 3 WHERE funcao LIKE 'Cargo -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 4 WHERE funcao LIKE 'Checklist -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 5 WHERE funcao LIKE 'Colaboradores -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 6 WHERE funcao LIKE 'Controle de Jornada -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 7 WHERE funcao LIKE 'Equipe -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 8 WHERE funcao LIKE 'Escala Diária -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 9 WHERE funcao LIKE 'Fale Conosco -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 10 WHERE funcao LIKE 'Geral -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 11 WHERE funcao LIKE 'Indicadores -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 12 WHERE funcao LIKE 'Mapa (2art) e Tracking -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 13 WHERE funcao LIKE 'Metas -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 14 WHERE funcao LIKE 'Movimentação -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 15 WHERE funcao LIKE 'Pneu -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 16 WHERE funcao LIKE 'Produtividade -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 17 WHERE funcao LIKE 'Prontuário do Condutor -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 18 WHERE funcao LIKE 'Pré-Contracheque -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 19 WHERE funcao LIKE 'Quiz -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 20 WHERE funcao LIKE 'Raizen Produtividade -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 21 WHERE funcao LIKE 'Ranking -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 22 WHERE funcao LIKE 'Recapadora -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 23 WHERE funcao LIKE 'Relato -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 24 WHERE funcao LIKE 'Solicitação de Folga -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 25 WHERE funcao LIKE 'Treinamento -%';
UPDATE funcao_prolog_v11 SET cod_agrupamento = 26 WHERE funcao LIKE 'Veículo -%';

-- RENOMEIA A COLUNA DE NOME DE PERMISSÃO PARA TER UM BACKUP
ALTER TABLE FUNCAO_PROLOG_V11 RENAME COLUMN FUNCAO TO FUNCAO_OLD;

-- CRIA A NOVA COLUNA DE NOME DE PERMISSÃO
ALTER TABLE FUNCAO_PROLOG_V11 ADD FUNCAO VARCHAR(255);

-- CORRIGE OS NOMES DAS PERMISSÕES NA NOVA COLUNA
UPDATE funcao_prolog_v11 SET funcao = 'Realizar Aferição de Placas (cronograma)' WHERE codigo = 18 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar Aferição de Pneus (avulsa)' WHERE codigo = 140 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Configurar' WHERE codigo = 100 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualização' WHERE codigo = 33 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Upload dos arquivos 2art e Tracking' WHERE codigo = 42 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Inserir Registros (upload e cadastro)' WHERE codigo = 417 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 30 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Verificar dados importados' WHERE codigo = 43 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar ' WHERE codigo = 11 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = '2) Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)' WHERE codigo = 143 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 131 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 115 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Inativar tipo de marcação' WHERE codigo = 341 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 317 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar' WHERE codigo = 325 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar' WHERE codigo = 311 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar modelo' WHERE codigo = 114 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 26 AND cod_pilar = 2;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar marcações' WHERE codigo = 336 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar tipo de marcação' WHERE codigo = 344 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar modelo' WHERE codigo = 321 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Deletar itens' WHERE codigo = 410 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Consertar item em Ordem de Serviço (O.S.)' WHERE codigo = 13 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Vincular ao veículo' WHERE codigo = 111 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Upload' WHERE codigo = 333 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 48 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Consertar item em Ordem de Serviço (O.S.)' WHERE codigo = 19 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar próprios' WHERE codigo = 415 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Editar' WHERE codigo = 44 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar ' WHERE codigo = 327 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Responder a uma solicitação' WHERE codigo = 39 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Criar tipo de marcação' WHERE codigo = 340 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar' WHERE codigo = 17 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Transferir pneus e veículos entre unidades' WHERE codigo = 141 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 334 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar modelo de ' WHERE codigo = 320 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 122 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Editar' WHERE codigo = 416 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar Ordem de Serviço (O.S.)' WHERE codigo = 12 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar permissões' WHERE codigo = 328 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Edição de itens' WHERE codigo = 413 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar indicadores individuais' WHERE codigo = 40 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar' WHERE codigo = 36 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Vincular permissão' WHERE codigo = 329 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar evento' WHERE codigo = 319 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Criar' WHERE codigo = 323 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar realizados' WHERE codigo = 326 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar novos motivos para o descarte de pneus' WHERE codigo = 123 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar' WHERE codigo = 21 AND cod_pilar = 2;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 330 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Upload' WHERE codigo = 34 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar modelo' WHERE codigo = 112 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 116 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Tipo de Serviço - Cadastrar' WHERE codigo = 133 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 110 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Criar modelo' WHERE codigo = 37 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar todos os realizados' WHERE codigo = 118 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 35 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = '3) Descarte (Estoque ou Veículo ou Análise -> Descarte)' WHERE codigo = 144 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 342 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar realizadas' WHERE codigo = 117 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar' WHERE codigo = 313 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Criar evento' WHERE codigo = 324 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar' WHERE codigo = 16 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar todos' WHERE codigo = 414 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar todas as marcações' WHERE codigo = 337 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Editar' WHERE codigo = 132 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar' WHERE codigo = 14 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Responder' WHERE codigo = 315 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Tipo de Serviço - Visualizar' WHERE codigo = 134 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar solicitação' WHERE codigo = 38 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar Relatórios' WHERE codigo = 419 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar farol de realização do checklist' WHERE codigo = 10 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Upload do arquivo' WHERE codigo = 411 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 331 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Deletar' WHERE codigo = 418 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualização' WHERE codigo = 412 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = '1) Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)' WHERE codigo = 142 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar eventos' WHERE codigo = 32 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 316 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 47 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 121 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Ajuste de Marcações (criar, editar, inativar)' WHERE codigo = 338 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 343 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar modelo' WHERE codigo = 113 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar Ordens de Serviços' WHERE codigo = 119 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar todos' WHERE codigo = 335 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Tipo de Serviço - Editar' WHERE codigo = 135 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios de indicadores' WHERE codigo = 41 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar' WHERE codigo = 130 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar' WHERE codigo = 15 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar consolidado' WHERE codigo = 46 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar todos' WHERE codigo = 322 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 45 AND cod_pilar = 4;
UPDATE funcao_prolog_v11 SET funcao = 'Cadastrar' WHERE codigo = 310 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Realizar' WHERE codigo = 314 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Fechar' WHERE codigo = 24 AND cod_pilar = 2;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar' WHERE codigo = 25 AND cod_pilar = 2;
UPDATE funcao_prolog_v11 SET funcao = 'Edição dos motivos de descarte de pneus' WHERE codigo = 124 AND cod_pilar = 1;
UPDATE funcao_prolog_v11 SET funcao = 'Alterar' WHERE codigo = 318 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Visualizar relatórios' WHERE codigo = 332 AND cod_pilar = 3;
UPDATE funcao_prolog_v11 SET funcao = 'Classificar' WHERE codigo = 23 AND cod_pilar = 2;

-- CRIA O VÍNCULO ENTRE AS PERMISSÕES E OS AGRUPAMENTOS PARA GARANTIR A INTEGRIDADE.
ALTER TABLE FUNCAO_PROLOG_V11
ADD CONSTRAINT FK_FUNCAO_PROLOG_V11_FUNCAO_PROLOG_AGRUPAMENTO
FOREIGN KEY (COD_AGRUPAMENTO, COD_PILAR) REFERENCES FUNCAO_PROLOG_AGRUPAMENTO (CODIGO, COD_PILAR);

-- ALTERA A FK DE AGRUPAMENTO PARA NOT NULL.
ALTER TABLE funcao_prolog_v11 ALTER COLUMN COD_AGRUPAMENTO SET NOT NULL;
-- ALTERA A COLUNA FUNCAO PARA NOT NULL.
ALTER TABLE funcao_prolog_v11 ALTER COLUMN FUNCAO SET NOT NULL;


-- ADICIONA A FUNCTION DE LISTAGEM DE PERMISSÕES DETALHADAS
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_PERMISSOES_DETALHADAS(
  F_COD_UNIDADE BIGINT,
  F_COD_CARGO   BIGINT)
  RETURNS TABLE(
    COD_CARGO           BIGINT,
    COD_UNIDADE_CARGO   BIGINT,
    NOME_CARGO          VARCHAR(255),
    COD_PILAR           BIGINT,
    NOME_PILAR          VARCHAR(255),
    COD_FUNCIONALIDADE  SMALLINT,
    NOME_FUNCIONALIDADE VARCHAR(255),
    COD_PERMISSAO       BIGINT,
    NOME_PERMISSAO      VARCHAR(255),
    IMPACTO_PERMISSAO   PROLOG_IMPACTO_PERMISSAO_TYPE,
    DESCRICAO_PERMISSAO TEXT,
    PERMISSAO_LIBERADA  BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH PERMISSOES_CARGO_UNIDADE AS (
      SELECT
        CFP.COD_FUNCAO_COLABORADOR AS COD_CARGO,
        CFP.COD_UNIDADE            AS COD_UNIDADE_CARGO,
        CFP.COD_FUNCAO_PROLOG      AS COD_FUNCAO_PROLOG,
        CFP.COD_PILAR_PROLOG       AS COD_PILAR_PROLOG
      FROM CARGO_FUNCAO_PROLOG_V11 CFP
      WHERE CFP.COD_UNIDADE = F_COD_UNIDADE AND CFP.COD_FUNCAO_COLABORADOR = F_COD_CARGO
  )

  SELECT
    F_COD_CARGO                       AS COD_CARGO,
    F_COD_UNIDADE                     AS COD_UNIDADE_CARGO,
    F.NOME                            AS NOME_CARGO,
    FP.COD_PILAR                      AS COD_PILAR,
    PP.PILAR                          AS NOME_PILAR,
    FP.COD_AGRUPAMENTO                AS COD_FUNCIONALIDADE,
    FPA.NOME                          AS NOME_FUNCIONALIDADE,
    FP.CODIGO                         AS COD_PERMISSAO,
    FP.FUNCAO                         AS NOME_PERMISSAO,
    FP.IMPACTO                        AS IMPACTO_PERMISSAO,
    FP.DESCRICAO                      AS DESCRICAO_PERMISSAO,
    PCU.COD_UNIDADE_CARGO IS NOT NULL AS PERMISSAO_LIBERADA
  FROM PILAR_PROLOG PP
    JOIN FUNCAO_PROLOG_V11 FP ON FP.COD_PILAR = PP.CODIGO
    JOIN UNIDADE_PILAR_PROLOG UPP ON UPP.COD_PILAR = PP.CODIGO
    JOIN FUNCAO_PROLOG_AGRUPAMENTO FPA ON FPA.CODIGO = FP.COD_AGRUPAMENTO
    JOIN FUNCAO F ON F.CODIGO = F_COD_CARGO
    LEFT JOIN PERMISSOES_CARGO_UNIDADE PCU ON PCU.COD_FUNCAO_PROLOG = FP.CODIGO
  WHERE UPP.COD_UNIDADE = F_COD_UNIDADE AND FP.COD_PILAR = ANY (PILARES_LIBERADOS_UNIDADE)
  ORDER BY PP.PILAR, FP.COD_AGRUPAMENTO, FP.IMPACTO DESC;
END;
$$;

--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
--######################################## ALTERA FUNCÇÕES DE DELEÇÃO LÓGICA ###########################################
--##################################### ADICIONA DATA E HORA DE DELEÇÃO LÓGICA #########################################
--######################### ADICIONA CONTROLE DE PRIVILÉGIOS PARA UPDATE NA COLUNA DELETADO ############################
--######################################################################################################################
--######################################################################################################################
--PL-2030
--PL-2031

-- INÍCIO: AFERIÇÃO
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NAS TABELAS DE AFERIÇÃO
REVOKE UPDATE (deletado)
ON AFERICAO_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON AFERICAO_VALORES_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON AFERICAO_MANUTENCAO_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NAS TABELAS DE AFERIÇÃO
ALTER TABLE AFERICAO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE AFERICAO_VALORES_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE AFERICAO_MANUTENCAO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE AFERICAO_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE AFERICAO_VALORES_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE AFERICAO_MANUTENCAO_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE AFERICAO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE AFERICAO_VALORES_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE AFERICAO_MANUTENCAO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_AFERICAO_DELETA_AFERICAO
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_DELETA_AFERICAO(
      F_COD_UNIDADE       BIGINT,
      F_PLACA             TEXT,
      F_CODIGO_AFERICAO   BIGINT,
  OUT AVISO_AFERICAO_DELETADA TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  IF ((SELECT COUNT(codigo) FROM AFERICAO_DATA WHERE CODIGO = F_CODIGO_AFERICAO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade %, Placa % e Código %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;

  -- DELETA AFERIÇÃO.
  UPDATE AFERICAO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA
        AND CODIGO = F_CODIGO_AFERICAO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO VALORES.
  UPDATE AFERICAO_VALORES_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
  IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                          FROM AFERICAO_VALORES_DATA AVD
                                            WHERE AVD.COD_UNIDADE = F_COD_UNIDADE
                                                  AND AVD.COD_AFERICAO = F_CODIGO_AFERICAO) > 0))
  THEN
    RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO MANUTENÇÃO.
  -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
  UPDATE AFERICAO_MANUTENCAO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO;

  SELECT 'AFERIÇÃO DELETADA: '
         || F_CODIGO_AFERICAO
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_AFERICAO_DELETADA;
END;
$$;
-- FIM: AFERIÇÃO

-- INÍCIO: CHECKLIST E OS
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NAS TABELAS DE CHECKLIST E OS
REVOKE UPDATE (deletado)
ON CHECKLIST_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON CHECKLIST_ORDEM_SERVICO_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON CHECKLIST_ORDEM_SERVICO_ITENS_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NAS TABELAS DE CHECKLIST E OS
ALTER TABLE CHECKLIST_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE CHECKLIST_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE CHECKLIST_ORDEM_SERVICO_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE CHECKLIST_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(
      F_COD_UNIDADE            BIGINT,
      F_COD_CHECKLIST          BIGINT,
      F_PLACA                  TEXT,
      F_CPF_COLABORADOR        BIGINT,
  OUT AVISO_CHECKLIST_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  -- Deleta checklist.
  UPDATE CHECKLIST_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE CODIGO = F_COD_CHECKLIST
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA
        AND CPF_COLABORADOR = F_CPF_COLABORADOR;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o checklist de código: % e Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  -- Deleta O.S.
  UPDATE CHECKLIST_ORDEM_SERVICO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_CHECKLIST = F_COD_CHECKLIST
        AND COD_UNIDADE = F_COD_UNIDADE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                          FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                          WHERE COSD.COD_CHECKLIST = F_COD_CHECKLIST
                                                AND COSD.COD_UNIDADE = F_COD_UNIDADE) > 0))
  THEN
    RAISE EXCEPTION 'Erro ao deletar O.S. do checklist de código: % e Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  -- Deleta itens da O.S.
  UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_OS =
            (SELECT COSD.CODIGO
             FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
             WHERE COSD.COD_CHECKLIST = F_COD_CHECKLIST
                   AND COSD.COD_UNIDADE = F_COD_UNIDADE);

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  -- Se tem O.S. então também deverá ter itens, por isso a verificação serve.
  IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                          FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                          WHERE COS.COD_CHECKLIST = F_COD_CHECKLIST
                                                AND COS.COD_UNIDADE = F_COD_UNIDADE) > 0))
  THEN
    RAISE EXCEPTION 'Erro ao deletar itens da O.S. do checklist de código: % e Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'CHECKLIST DELETADO: '
         || F_COD_CHECKLIST
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_CHECKLIST_DELETADO;
END;
$$;
-- FIM: CHECKLIST E OS

-- INÍCIO: MODELO DE CHECKLIST
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NAS TABELAS DE MODELO DE CHECKLIST
REVOKE UPDATE (deletado)
ON CHECKLIST_MODELO_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON CHECKLIST_PERGUNTAS_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

REVOKE UPDATE (deletado)
ON CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NAS TABELAS DE MODELO DE CHECKLIST
ALTER TABLE CHECKLIST_MODELO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE CHECKLIST_PERGUNTAS_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE CHECKLIST_MODELO_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE CHECKLIST_PERGUNTAS_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;


-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE CHECKLIST_MODELO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE CHECKLIST_PERGUNTAS_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(
      F_COD_UNIDADE                                BIGINT,
      F_COD_MODELO_CHECKLIST                       BIGINT,
      F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO BOOLEAN DEFAULT FALSE,
  OUT AVISO_MODELO_CHECKLIST_DELETADO              TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
  CHECKLIST              RECORD;
BEGIN

  IF F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
  THEN
    FOR CHECKLIST IN SELECT
                       C.COD_UNIDADE,
                       C.CODIGO
                     FROM CHECKLIST C
                     WHERE C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
                           AND C.COD_UNIDADE = F_COD_UNIDADE
    LOOP
      PERFORM FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE := CHECKLIST.COD_UNIDADE,
                                                   F_COD_CHECKLIST := CHECKLIST.CODIGO);
    END LOOP;
  END IF;

  -- Deleta modelo de checklist.
  UPDATE CHECKLIST_MODELO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE CODIGO = F_COD_MODELO_CHECKLIST
        AND COD_UNIDADE = F_COD_UNIDADE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o modelo de checklist de código: % e Unidade: %', F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  -- Deleta pergundas do modelo de checklist.
  UPDATE CHECKLIST_PERGUNTAS_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
        AND COD_UNIDADE = F_COD_UNIDADE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar as perguntas do modelo de checklist de código: % e Unidade: %', F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  -- Deleta as alternativas das pergundas do modelo de checklist.
  UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
        AND COD_UNIDADE = F_COD_UNIDADE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar as alternativas do modelo de checklist de código: % e Unidade: %', F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
  END IF;
  --
  --

  -- As únicas coisas que deletamos de fato são os vínculos de cargos e tipos de veículos, assim um modelo marcado
  -- como "deletado" não fica com vínculos que podem bloquear outras operações do BD.
  DELETE FROM CHECKLIST_MODELO_FUNCAO
  WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST;
  DELETE FROM CHECKLIST_MODELO_VEICULO_TIPO
  WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_MODELO = F_COD_MODELO_CHECKLIST;

  SELECT 'MODELO DE CHECKLIST DELETADO: '
         || F_COD_MODELO_CHECKLIST
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
         || F_IF(F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO,
                 '. OS CHECKLISTS REALIZADOS DESSE MODELO TAMBÉM FORAM DELETADOS.' :: TEXT,
                 '. OS CHECKLISTS REALIZADOS DESSE MODELO NÃO FORAM DELETADOS.' :: TEXT)
  INTO AVISO_MODELO_CHECKLIST_DELETADO;
END;
$$;
-- FIM: MODELO DE CHECKLIST

-- INÍCIO: COLABORADOR
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NA TABELA DE COLABORADOR
REVOKE UPDATE (deletado)
ON COLABORADOR_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NA TABELA DE COLABORADOR
ALTER TABLE COLABORADOR_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE COLABORADOR_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE COLABORADOR_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_COLABORADOR_DELETA_COLABORADOR
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_DELETA_COLABORADOR(
      F_COD_UNIDADE            BIGINT,
      F_COD_COLABORADOR        BIGINT,
      F_CPF                    BIGINT,
  OUT AVISO_COLABORADOR_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  IF ((SELECT COUNT(codigo) FROM COLABORADOR_DATA WHERE CODIGO = F_COD_COLABORADOR
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CPF = F_CPF) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum colaborador encontrado com estes parâmetros: Código %, CPF % e Unidade %', F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
  END IF;

  -- Deleta colaborador.
  UPDATE COLABORADOR_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE CODIGO = F_COD_COLABORADOR
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CPF = F_CPF;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %', F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'COLABORADOR DELETADO: '
         || F_COD_COLABORADOR
         || ', CPF: '
         || F_CPF
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_COLABORADOR_DELETADO;
END;
$$;
-- FIM: COLABORADOR

-- INÍCIO: PNEU
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NA TABELA DE PNEU
REVOKE UPDATE (deletado)
ON PNEU_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NA TABELA DE PNEU
ALTER TABLE PNEU_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE PNEU_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE PNEU_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_PNEU_DELETA_PNEU
CREATE OR REPLACE FUNCTION FUNC_PNEU_DELETA_PNEU(
      F_COD_UNIDADE       BIGINT,
      F_CODIGO            BIGINT,
      F_CODIGO_CLIENTE    TEXT,
  OUT AVISO_PNEU_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
  F_STATUS_PNEU_ANALISE TEXT := 'ANALISE';
BEGIN

  -- Verifica se o pneu existe.
  IF ((SELECT COUNT(CODIGO)
       FROM PNEU_DATA
       WHERE CODIGO = F_CODIGO
             AND COD_UNIDADE = F_COD_UNIDADE
             AND CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Verifica se o pneu está aplicado.
  IF ((SELECT COUNT(VP.PLACA)
       FROM VEICULO_PNEU VP
       WHERE VP.COD_PNEU = F_CODIGO AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
  THEN
    RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Verifica se o pneu está em análise.
  IF ((SELECT COUNT(codigo)
       FROM PNEU_DATA
       WHERE CODIGO = F_CODIGO
             AND COD_UNIDADE = F_COD_UNIDADE
             AND CODIGO_CLIENTE = F_CODIGO_CLIENTE
             AND STATUS = F_STATUS_PNEU_ANALISE) > 0)
  THEN
    RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;

  -- Deleta pneu.
  UPDATE PNEU_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE CODIGO = F_CODIGO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'PNEU DELETADO: '
         || F_CODIGO
         || ', CÓDIGO DO CLIENTE: '
         || F_CODIGO_CLIENTE
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_PNEU_DELETADO;
END;
$$;
-- FIM: PNEU

-- INÍCIO: VEÍCULO
-- REMOVE A PERMISSÃO PARA REALIZAR UPDATE DA COLUNA "DELETADO" NA TABELA DE VEÍCULO
REVOKE UPDATE (deletado)
ON VEICULO_DATA
FROM
prolog_group_importadores,
prolog_group_suporte,
prolog_user_bruno,
prolog_user_diogenes,
prolog_user_euclides,
prolog_user_gabriel,
prolog_user_thais;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO NA TABELA DE VEÍCULO
ALTER TABLE VEICULO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;

-- ATUALIZA O CAMPO DE DATA_HORA_DELETADO PARA POSSIBILITAR A CRIAÇÃO DA CONSTRAINT
UPDATE VEICULO_DATA SET DATA_HORA_DELETADO = NOW() WHERE DELETADO AND DATA_HORA_DELETADO IS NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE VEICULO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ADICIONA DATA E HORA DE DELEÇÃO E CONTROLE DE PRIVILÉGIOS NA FUNC_VEICULO_DELETA_VEICULO
CREATE OR REPLACE FUNCTION FUNC_VEICULO_DELETA_VEICULO(
      F_COD_UNIDADE            BIGINT,
      F_COD_VEICULO            BIGINT,
      F_PLACA                  TEXT,
  OUT AVISO_VEICULO_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  IF ((SELECT COUNT(CODIGO) FROM VEICULO_DATA WHERE CODIGO = F_COD_VEICULO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA = F_PLACA) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhum veículo encontrado com estes parâmetros: Código %, Placa % e Unidade %', F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
  END IF;

  IF ((SELECT COUNT(VP.PLACA)
       FROM VEICULO_PNEU VP
       WHERE VP.PLACA = F_PLACA AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
  THEN
    RAISE EXCEPTION 'O veículo de placa % não pode ser deletado pois possui pneus aplicados', F_PLACA;
  END IF;

  -- Deleta veículo.
  UPDATE VEICULO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE CODIGO = F_COD_VEICULO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA = F_PLACA;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o veículo de código: %, placa: % e Unidade: %', F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
  END IF;
  --
  --

  SELECT 'VEÍCULO DELETADO: '
         || F_COD_VEICULO
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_VEICULO_DELETADO;
END;
$$;
-- FIM: VEÍCULO

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2045
ALTER TABLE PUBLIC.QUIZ_ALTERNATIVA_PERGUNTA ALTER COLUMN ALTERNATIVA TYPE TEXT USING ALTERNATIVA::TEXT;
ALTER TABLE PUBLIC.QUIZ_PERGUNTAS ALTER COLUMN PERGUNTA TYPE TEXT USING PERGUNTA::TEXT;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2023
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_INTEGRACAO_RESOLVE_ITENS_PENDENTES_EMPRESA(
  F_COD_UNIDADE_ORDEM_SERVICO     BIGINT,
  F_COD_ORDEM_SERVICO             BIGINT,
  F_COD_ITEM_RESOLVIDO            BIGINT,
  F_CPF_COLABORADOR_RESOLUCAO     BIGINT,
  F_KM_MOMENTO_RESOLUCAO          BIGINT,
  F_DURACAO_RESOLUCAO_MS          BIGINT,
  F_FEEDBACK_RESOLUCAO            TEXT,
  F_DATA_HORA_RESOLVIDO_PROLOG    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_INICIO_RESOLUCAO    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_FIM_RESOLUCAO       TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO              TEXT,
  F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE  TEXT := 'P';
  F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO TEXT := 'R';
  F_STATUS_ORDEM_SERVICO_ABERTA         TEXT := 'A';
  F_STATUS_ORDEM_SERVICO_FECHADA        TEXT := 'F';
  F_QTD_ROWS_ITENS_OS                   BIGINT;
  F_QTD_ROWS_ITENS_RESOLVIDOS_OS        BIGINT;
BEGIN
  -- Antes de realizar o processo de fechamento de Item de Ordem de Serviço, validamos os dados e vínculos
  -- 1° - Validamos se existe a O.S na unidade.
  IF (SELECT NOT EXISTS(
      SELECT COS.CODIGO FROM CHECKLIST_ORDEM_SERVICO COS
      WHERE COS.CODIGO = F_COD_ORDEM_SERVICO AND COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO))
  THEN RAISE EXCEPTION
  'A Ordem de Serviço "%" não existe na unidade "%" do ProLog', F_COD_ORDEM_SERVICO, F_COD_UNIDADE_ORDEM_SERVICO
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- 2° - Validamos se a O.S está ABERTA.
  IF ((SELECT COS.STATUS FROM CHECKLIST_ORDEM_SERVICO COS
  WHERE COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND COS.CODIGO = F_COD_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_FECHADA)
  THEN RAISE EXCEPTION 'A Ordem de Serviço "%" já está fechada no ProLog', F_COD_ORDEM_SERVICO
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- 3° - Validamos se o Item a ser resolvido pertence a O.S.
  IF ((SELECT COSI.COD_OS FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
  WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) != F_COD_ORDEM_SERVICO)
  THEN RAISE EXCEPTION 'O Item "%" não pertence à O.S "%" do ProLog', F_COD_ITEM_RESOLVIDO, F_COD_ORDEM_SERVICO
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- 4° - Validamos se o Item da O.S está PENDENTE.
  IF ((SELECT COSI.STATUS_RESOLUCAO FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
  WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) != F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE)
  THEN RAISE EXCEPTION 'O Item "%" da O.S "%" já está resolvido no ProLog', F_COD_ITEM_RESOLVIDO, F_COD_ORDEM_SERVICO
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- 5° - Validamos se o CPF está presente na empresa
  IF (SELECT NOT EXISTS(SELECT C.CODIGO FROM COLABORADOR C
  WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
        AND C.COD_EMPRESA = (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORDEM_SERVICO)))
  THEN RAISE EXCEPTION 'O CPF % não encontra-se cadastrado no ProLog', FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- POR SEGURANÇA, VERIFICAMOS SE A INTEGRAÇÃO ESTÁ FECHANDO OS ITENS DE O.S. QUE PERTENCEM A EMPRESA CORRETA.
  IF (F_COD_UNIDADE_ORDEM_SERVICO NOT IN (SELECT CODIGO
                                          FROM UNIDADE
                                          WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
  THEN RAISE EXCEPTION 'Você está tentando fechar um item de uma OS que não pertence à sua empresa.'
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET
    CPF_MECANICO               = F_CPF_COLABORADOR_RESOLUCAO,
    TEMPO_REALIZACAO           = F_DURACAO_RESOLUCAO_MS,
    KM                         = F_KM_MOMENTO_RESOLUCAO,
    STATUS_RESOLUCAO           = F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO,
    DATA_HORA_CONSERTO         = F_DATA_HORA_RESOLVIDO_PROLOG,
    DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
    DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO,
    FEEDBACK_CONSERTO          = F_FEEDBACK_RESOLUCAO
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND CODIGO  = F_COD_ITEM_RESOLVIDO
        AND DATA_HORA_CONSERTO IS NULL;

  -- O ATRIBUTO 'ROW_COUNT' CONTERÁ A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
  -- IRÁ RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
  GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

  -- O PRIMEIRO IF VERIFICA SE O ITEM ESTÁ PENDENTE, SE APÓS O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA
  -- QUE O UPDATE NÃO EXECUTOU CORRETAMENTE. LANÇAMOS AQUI UMA EXCEÇÃO PARA RASTREAR ESSE ERRO
  IF F_QTD_ROWS_ITENS_OS <= 0
  THEN RAISE EXCEPTION 'Não foi possível resolver o item %', F_COD_ITEM_RESOLVIDO;
  END IF;

  -- AO RESOLVER UM ITEM DE ORDEM DE SERVIÇO É NECESSÁRIO VERIFICAR SE A ORDEM DE SERVIÇO FOI FINALIZADA.
  -- UMA 'O.S.' FECHADA CONSISTE EM UMA 'O.S.' QUE POSSUI TODOS OS SEUS ITENS RESOLVIDOS.
  UPDATE CHECKLIST_ORDEM_SERVICO SET
    STATUS = F_STATUS_ORDEM_SERVICO_FECHADA,
    DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND CODIGO = F_COD_ORDEM_SERVICO
        -- UTILIZAMOS ESSA VERIFICAÇÃO PARA FORÇAR QUE O UPDATE ACONTEÇA APENAS SE A 'O.S.' TIVER
        -- TODOS SEUS ITENS RESOLVIDOS.
        AND (SELECT COUNT(*) FROM CHECKLIST_ORDEM_SERVICO_ITENS
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND COD_OS = F_COD_ORDEM_SERVICO
        AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0;

  -- PARA GARANTIR A CONSISTÊNCIA DO PROCESSO DE RESOLUÇÃO DE ITENS E FECHAMENTO DE ORDENS DE SERVIÇO
  -- VERIFICAMOS SE A 'O.S.' QUE POSSUIU SEU ITEM FECHADO ESTÁ COM O 'STATUS' CORRETO.
  IF (((SELECT COUNT(*)
        FROM CHECKLIST_ORDEM_SERVICO_ITENS
        WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
              AND COD_OS = F_COD_ORDEM_SERVICO
              AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0)
      AND (SELECT STATUS
           FROM CHECKLIST_ORDEM_SERVICO
           WHERE CODIGO = F_COD_ORDEM_SERVICO
                 AND COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_ABERTA)
  -- CASO A 'O.S.' NÃO TEM NENHUM ITEM PENDENTE MAS O SEU STATUS É 'ABERTA', ENTÃO TEMOS DADOS INCONSISTENTES.
  THEN RAISE EXCEPTION 'Não foi possível fechar a Ordem de Serviço %', F_COD_ORDEM_SERVICO;
  END IF;

  -- APÓS REALIZAR O PROCESSO DE FECHAMENTO, INSERIMOS O ITEM RESOLVIDO NA TABELA DE MAPEAMENTO DE INTES RESOLVIDOS
  -- ATRAVÉS DA INTEGRAÇÃO
  INSERT INTO INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
  VALUES ((SELECT TI.COD_EMPRESA
           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO),
          F_COD_UNIDADE_ORDEM_SERVICO,
          F_COD_ORDEM_SERVICO,
          F_COD_ITEM_RESOLVIDO,
          F_DATA_HORA_SINCRONIA_RESOLUCAO);

  GET DIAGNOSTICS F_QTD_ROWS_ITENS_RESOLVIDOS_OS = ROW_COUNT;

  -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTE DE ITENS RESOLVIDOS NA INTEGRAÇÃO OCORREU COM ÊXITO
  IF F_QTD_ROWS_ITENS_RESOLVIDOS_OS <= 0
  THEN
    RAISE EXCEPTION 'Não foi possível inserir o item resolvido na tabela de mapeamento, item %', F_COD_ITEM_RESOLVIDO;
  END IF;

  RETURN F_QTD_ROWS_ITENS_OS;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################  CRIA TRIGGER AO ADICIONAR UMA NOVA EMPRESA  #####################################
--####################### ADICIONA A EMPRESA ADICIONA À LISTA DE BLOQUEIO DO CHECKLIST OFFLINE #########################
--######################################################################################################################
--######################################################################################################################
-- PL-2012

-- ADICIONA A EMPRESA ADICIONADA À LISTA DE BLOQUEIO PARA CHECKLIST OFFLINE
CREATE OR REPLACE FUNCTION TG_FUNC_EMPRESA_BLOQUEIA_CHECKLIST_OFFLINE()
  RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA (COD_EMPRESA) VALUES (NEW.CODIGO);
  RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- CRIA A TRIGGER
CREATE CONSTRAINT TRIGGER TG_BLOQUEIA_CHECKLIST_OFFLINE_EMPRESA_NOVA
  AFTER INSERT ON EMPRESA
  DEFERRABLE
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_EMPRESA_BLOQUEIA_CHECKLIST_OFFLINE();

--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;