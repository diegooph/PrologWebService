BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--###################################### CRIA FUNCTION PARA DELETAR CHECKLISTS E OS ####################################
--######################################################################################################################
--######################################################################################################################
-- PL-1943
ALTER TABLE CHECKLIST ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE CHECKLIST_ORDEM_SERVICO ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE CHECKLIST RENAME TO CHECKLIST_DATA;
ALTER TABLE CHECKLIST_ORDEM_SERVICO RENAME TO CHECKLIST_ORDEM_SERVICO_DATA;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS RENAME TO CHECKLIST_ORDEM_SERVICO_ITENS_DATA;

CREATE VIEW CHECKLIST AS
  SELECT
    C.COD_UNIDADE,
    C.COD_CHECKLIST_MODELO,
    C.CODIGO,
    C.DATA_HORA,
    C.CPF_COLABORADOR,
    C.PLACA_VEICULO,
    C.TIPO,
    C.TEMPO_REALIZACAO,
    C.KM_VEICULO,
    C.DATA_HORA_SINCRONIZACAO,
    C.FONTE_DATA_HORA_REALIZACAO,
    C.VERSAO_APP_MOMENTO_REALIZACAO,
    C.VERSAO_APP_MOMENTO_SINCRONIZACAO,
    C.DEVICE_ID,
    C.DEVICE_IMEI,
    C.DEVICE_UPTIME_REALIZACAO_MILLIS,
    C.DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
    C.FOI_OFFLINE
  FROM CHECKLIST_DATA C
  WHERE C.DELETADO = FALSE;

CREATE VIEW CHECKLIST_ORDEM_SERVICO AS
  SELECT
    COS.CODIGO,
    COS.COD_UNIDADE,
    COS.COD_CHECKLIST,
    COS.STATUS,
    COS.DATA_HORA_FECHAMENTO
  FROM CHECKLIST_ORDEM_SERVICO_DATA COS
  WHERE COS.DELETADO = FALSE;

CREATE VIEW CHECKLIST_ORDEM_SERVICO_ITENS AS
  SELECT
    COSI.COD_UNIDADE,
    COSI.CODIGO,
    COSI.COD_OS,
    COSI.CPF_MECANICO,
    COSI.COD_PERGUNTA,
    COSI.COD_ALTERNATIVA,
    COSI.STATUS_RESOLUCAO,
    COSI.QT_APONTAMENTOS,
    COSI.KM,
    COSI.DATA_HORA_CONSERTO,
    COSI.DATA_HORA_INICIO_RESOLUCAO,
    COSI.DATA_HORA_FIM_RESOLUCAO,
    COSI.TEMPO_REALIZACAO,
    COSI.FEEDBACK_CONSERTO
  FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
  WHERE COSI.DELETADO = FALSE;

-- Altera constraints de not null para não validarem linhas deletadas.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA DROP CONSTRAINT CHECK_DATA_HORA_INICIO_RESOLUCAO_NOT_NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA DROP CONSTRAINT CHECK_DATA_HORA_FIM_RESOLUCAO_NOT_NULL;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
  ADD CONSTRAINT CHECK_DATA_HORA_INICIO_RESOLUCAO_NOT_NULL CHECK (
  DELETADO OR ((DATA_HORA_CONSERTO IS NOT NULL AND DATA_HORA_INICIO_RESOLUCAO IS NOT NULL)
               OR (DATA_HORA_CONSERTO IS NULL AND DATA_HORA_INICIO_RESOLUCAO IS NULL))) NOT VALID;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
  ADD CONSTRAINT CHECK_DATA_HORA_FIM_RESOLUCAO_NOT_NULL CHECK (
  DELETADO OR ((DATA_HORA_CONSERTO IS NOT NULL AND DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
               OR (DATA_HORA_CONSERTO IS NULL AND DATA_HORA_FIM_RESOLUCAO IS NULL))) NOT VALID;

COMMENT ON CONSTRAINT CHECK_DATA_HORA_INICIO_RESOLUCAO_NOT_NULL
ON CHECKLIST_ORDEM_SERVICO_ITENS_DATA
IS 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_INICIO_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';

COMMENT ON CONSTRAINT CHECK_DATA_HORA_FIM_RESOLUCAO_NOT_NULL
ON CHECKLIST_ORDEM_SERVICO_ITENS_DATA
IS 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_FIM_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(
      F_COD_UNIDADE            BIGINT,
      F_COD_CHECKLIST          BIGINT,
      F_PLACA                  TEXT,
      F_CPF_COLABORADOR        BIGINT,
  OUT AVISO_CHECKLIST_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

  -- Deleta checklist.
  UPDATE CHECKLIST_DATA
  SET DELETADO = TRUE
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
  SET DELETADO = TRUE
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
  SET DELETADO = TRUE
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

-- Deleta todas as OSs sem itens no BD. Garante que a deleção de checklist vai funcionar corretamente.
WITH OS_SEM_ITEM AS (
    SELECT
      COS.COD_UNIDADE,
      COS.CODIGO
    FROM CHECKLIST_ORDEM_SERVICO COS
      LEFT JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
        ON COSI.COD_UNIDADE = COS.COD_UNIDADE
           AND COSI.COD_OS = COS.CODIGO
    WHERE COSI.COD_OS IS NULL
)

DELETE FROM CHECKLIST_ORDEM_SERVICO COS
USING OS_SEM_ITEM OST
WHERE OST.COD_UNIDADE = COS.COD_UNIDADE
      AND OST.CODIGO = COS.CODIGO;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### CRIA FUNCTION PARA DELETAR UM MODELO DE CHECKLIST #############################
--######################################################################################################################
--######################################################################################################################
ALTER TABLE CHECKLIST_MODELO
  ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE CHECKLIST_PERGUNTAS
  ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA
  ADD COLUMN DELETADO BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE CHECKLIST_MODELO
  RENAME TO CHECKLIST_MODELO_DATA;
ALTER TABLE CHECKLIST_PERGUNTAS
  RENAME TO CHECKLIST_PERGUNTAS_DATA;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA
  RENAME TO CHECKLIST_ALTERNATIVA_PERGUNTA_DATA;

CREATE OR REPLACE VIEW CHECKLIST_MODELO AS
  SELECT
    CM.COD_UNIDADE,
    CM.CODIGO,
    CM.NOME,
    CM.STATUS_ATIVO
  FROM CHECKLIST_MODELO_DATA CM
  WHERE CM.DELETADO = FALSE;

CREATE OR REPLACE VIEW CHECKLIST_PERGUNTAS AS
  SELECT
    CP.COD_CHECKLIST_MODELO,
    CP.COD_UNIDADE,
    CP.ORDEM,
    CP.PERGUNTA,
    CP.STATUS_ATIVO,
    CP.SINGLE_CHOICE,
    CP.COD_IMAGEM,
    CP.CODIGO
  FROM CHECKLIST_PERGUNTAS_DATA CP
  WHERE CP.DELETADO = FALSE;

CREATE OR REPLACE VIEW CHECKLIST_ALTERNATIVA_PERGUNTA AS
  SELECT
    CAP.COD_CHECKLIST_MODELO,
    CAP.COD_UNIDADE,
    CAP.ALTERNATIVA,
    CAP.ORDEM,
    CAP.STATUS_ATIVO,
    CAP.COD_PERGUNTA,
    CAP.CODIGO,
    CAP.ALTERNATIVA_TIPO_OUTROS,
    CAP.PRIORIDADE,
    CAP.DEVE_ABRIR_ORDEM_SERVICO
  FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
  WHERE CAP.DELETADO = FALSE;


-- Function para deletar um modelo de checklist, com opção para deletar também todos os checklists realizados do modelo
-- em questão.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(
      F_COD_UNIDADE                                BIGINT,
      F_COD_MODELO_CHECKLIST                       BIGINT,
      F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO BOOLEAN DEFAULT FALSE,
  OUT AVISO_MODELO_CHECKLIST_DELETADO              TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
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
  SET DELETADO = TRUE
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
  SET DELETADO = TRUE
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
  SET DELETADO = TRUE
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
--######################################################################################################################
--######################################################################################################################

-- Corrige functions/views que possam ter quebrado por conta das alterações de tabelas para views.
-- Adiciona status_ativo no group by.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT, F_CARGOS TEXT)
  RETURNS TABLE(
    MODELO          TEXT,
    COD_MODELO      BIGINT,
    COD_UNIDADE     BIGINT,
    NOME_CARGO      TEXT,
    TIPO_VEICULO    TEXT,
    TOTAL_PERGUNTAS BIGINT,
    STATUS_ATIVO    BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  CM.NOME          AS MODELO,
  CM.CODIGO        AS COD_MODELO,
  CM.COD_UNIDADE   AS COD_UNIDADE,
  F.NOME           AS NOME_CARGO,
  VT.NOME          AS TIPO_VEICULO,
  COUNT(CP.CODIGO) AS TOTAL_PERGUNTAS,
  CM.STATUS_ATIVO  AS STATUS_ATIVO
FROM CHECKLIST_MODELO CM
  JOIN CHECKLIST_PERGUNTAS CP ON CM.COD_UNIDADE = CP.COD_UNIDADE
                                 AND CM.CODIGO = CP.COD_CHECKLIST_MODELO
                                 AND CP.STATUS_ATIVO = TRUE
  LEFT JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.COD_UNIDADE = CMF.COD_UNIDADE
                                           AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO
  LEFT JOIN FUNCAO F ON CMF.COD_FUNCAO = F.CODIGO
  LEFT JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CM.COD_UNIDADE = CMVT.COD_UNIDADE
                                                  AND CM.CODIGO = CMVT.COD_MODELO
  LEFT JOIN VEICULO_TIPO VT ON CMVT.COD_TIPO_VEICULO = VT.CODIGO
WHERE CM.COD_UNIDADE = F_COD_UNIDADE
      AND CMF.COD_FUNCAO :: TEXT LIKE F_CARGOS
GROUP BY CM.NOME, CM.CODIGO, CM.COD_UNIDADE, F.NOME, VT.NOME, CM.STATUS_ATIVO
ORDER BY CM.STATUS_ATIVO DESC, CM.CODIGO ASC;
$$;

--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE)
  RETURNS TABLE(
    UNIDADE               TEXT,
    PERGUNTA              TEXT,
    ALTERNATIVA           TEXT,
    PRIORIDADE            TEXT,
    "TOTAL MARCAÇÕES NOK" BIGINT,
    "TOTAL REALIZAÇÕES"   BIGINT,
    "PROPORÇÃO"           TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                              AS NOME_UNIDADE,
  CP.PERGUNTA                                                         AS PERGUNTA,
  CAP.ALTERNATIVA                                                     AS ALTERNATIVA,
  CAP.PRIORIDADE                                                      AS PRIORIDADE,
  SUM(CASE WHEN CR.RESPOSTA <> 'OK' THEN 1 ELSE 0 END)                AS TOTAL_MARCACOES_NOK,
  COUNT(CP.PERGUNTA)                                                  AS TOTAL_REALIZACOES,
  TRUNC(((SUM(CASE WHEN CR.RESPOSTA <> 'OK' THEN 1 ELSE 0 END)
          / COUNT(CP.PERGUNTA) :: FLOAT) * 100) :: NUMERIC, 1) || '%' AS PROPORCAO
FROM CHECKLIST C
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_PERGUNTA = CP.CODIGO
  JOIN CHECKLIST_RESPOSTAS CR
    ON CR.COD_CHECKLIST = C.CODIGO
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
  JOIN UNIDADE U
    ON U.CODIGO = C.COD_UNIDADE
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CP.CODIGO, CAP.CODIGO, CAP.PRIORIDADE, CP.PERGUNTA, CAP.ALTERNATIVA
ORDER BY U.NOME, PROPORCAO DESC
$$;

--######################################################################################################################
--######################################################################################################################
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
    GROUP BY C.CODIGO, U.CODIGO, CO.CPF, C.DATA_HORA, C.COD_UNIDADE, C.PLACA_VEICULO, C.KM_VEICULO, C.TEMPO_REALIZACAO, C.TIPO),

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
  C.DATA_HORA_REALIZACAO DESC;
$$;

-- PL-1794
--######################################################################################################################
--######################################################################################################################
-- Bloqueia a deleção de perguntas e alternativas para evitar problemas ao sincronizar o checklist offline.
CREATE RULE ALTERNATIVA_CHECK_DELETE_PROTECT AS ON DELETE TO PUBLIC.CHECKLIST_ALTERNATIVA_PERGUNTA_DATA DO INSTEAD NOTHING;
CREATE RULE PERGUNTA_CHECK_DELETE_PROTECT AS ON DELETE TO PUBLIC.CHECKLIST_PERGUNTAS_DATA DO INSTEAD NOTHING;

-- Deixa comentado nas tabelas o porquê de a deleção ter sido bloqueada.
COMMENT ON TABLE PUBLIC.CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
IS 'Alternativas de uma pergunta do checklist. ATENÇÃO: A deleção direta de alternativas está bloqueada por problemas de
    vínculo que podem acontecer no checklist offline. Só é possível deletar alternativas se todo o modelo de checklist
    for deletado junto. Caso queira remover uma alternativa específica mas ainda deixar o modelo rodando, opte por
    inatvar a alternativa ao invés de deletá-la. Além disso, se quiser remover o modelo como um todo, o ProLog possui
    functions que irão remover os bloqueios de deleção da tabela, deletar o modelo, e reativar os bloqueios.';

COMMENT ON TABLE PUBLIC.CHECKLIST_PERGUNTAS_DATA
IS 'Perguntas de um checklist. ATENÇÃO: A deleção direta de perguntas está bloqueada por problemas de
    vínculo que podem acontecer no checklist offline. Só é possível deletar perguntas se todo o modelo de checklist
    for deletado junto. Caso queira remover uma pergunta específica mas ainda deixar o modelo rodando, opte por
    inatvar a perguntas ao invés de deletá-la. Além disso, se quiser remover o modelo como um todo, o ProLog possui
    functions que irão remover os bloqueios de deleção da tabela, deletar o modelo, e reativar os bloqueios.';

-- CRIAMOS CONSTRAINTS DE FK PARA AS ALTERNATIVAS E PERGUNTAS DOS ITENS DE O.S.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA ADD CONSTRAINT FK_CHECKLIST_ORDEM_SERVICO_ITENS_PERGUNTAS
FOREIGN KEY (COD_PERGUNTA) REFERENCES CHECKLIST_PERGUNTAS_DATA(CODIGO);

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA ADD CONSTRAINT FK_CHECKLIST_ORDEM_SERVICO_ITENS_ALTERNATIVA_PERGUNTA
FOREIGN KEY (COD_ALTERNATIVA) REFERENCES CHECKLIST_ALTERNATIVA_PERGUNTA_DATA(CODIGO);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################################# LISTAGEM DE VEÍCULOS ###############################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    UNIDADE                TEXT,
    PLACA                  TEXT,
    MARCA                  TEXT,
    MODELO                 TEXT,
    TIPO                   TEXT,
    "DIAGRAMA VINCULADO?"  TEXT,
    "KM ATUAL"             TEXT,
    STATUS                 TEXT,
    "DATA/HORA CADASTRO"   TEXT,
    "QTD PNEUS VINCULADOS" TEXT,
    "QTD SLOTS DIAGRAMA"   TEXT,
    "QTD SLOTS SEM PNEUS"  TEXT,
    "QTD ESTEPES"          TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  ESTEPES INTEGER := 900;
BEGIN
  RETURN QUERY
  WITH QTD_PNEUS_VINCULADOS_PLACA AS (
      SELECT
        V.PLACA,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO < ESTEPES)  AS QTD_PNEUS_VINCULADOS,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO >= ESTEPES) AS QTD_ESTEPES_VINCULADOS,
        VT.COD_DIAGRAMA
      FROM VEICULO V
        JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
        LEFT JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
      WHERE V.COD_UNIDADE = ANY(F_COD_UNIDADES)
      GROUP BY
        V.PLACA,
        VT.COD_DIAGRAMA
  ),

      QTD_SLOTS_DIAGRAMA AS (
        SELECT
          VDE.COD_DIAGRAMA,
          SUM(VDE.QT_PNEUS) AS QTD_SLOTS_DIAGRAMA
        FROM VEICULO_DIAGRAMA_EIXOS VDE
        GROUP BY COD_DIAGRAMA
    )

  SELECT
    U.NOME :: TEXT                                                     AS UNIDADE,
    V.PLACA :: TEXT                                                    AS PLACA,
    MA.NOME :: TEXT                                                    AS MARCA,
    MO.NOME :: TEXT                                                    AS MODELO,
    VT.NOME :: TEXT                                                    AS TIPO,
    CASE WHEN QPVP.COD_DIAGRAMA IS NULL THEN 'NÃO' ELSE 'SIM' END      AS POSSUI_DIAGRAMA,
    V.KM :: TEXT                                                       AS KM_ATUAL,
    F_IF(V.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)           AS STATUS,
    COALESCE(TO_CHAR(V.DATA_HORA_CADASTRO, 'DD/MM/YYYY HH24:MI'), '-') AS DATA_HORA_CADASTRO,
    QPVP.QTD_PNEUS_VINCULADOS :: TEXT                                  AS QTD_PNEUS_VINCULADOS,
    QSD.QTD_SLOTS_DIAGRAMA :: TEXT                                     AS QTD_SLOTS_DIAGRAMA,
    (QSD.QTD_SLOTS_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) :: TEXT       AS QTD_SLOTS_SEM_PNEUS,
    QPVP.QTD_ESTEPES_VINCULADOS :: TEXT                                AS QTD_ESTEPES_VINCULADOS
  FROM VEICULO V
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
    JOIN MODELO_VEICULO MO ON V.COD_MODELO = MO.CODIGO
    JOIN MARCA_VEICULO MA ON MO.COD_MARCA = MA.CODIGO
    JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
    RIGHT JOIN QTD_PNEUS_VINCULADOS_PLACA QPVP ON QPVP.PLACA = V.PLACA
    LEFT JOIN QTD_SLOTS_DIAGRAMA QSD ON QSD.COD_DIAGRAMA = QPVP.COD_DIAGRAMA
  ORDER BY
    U.NOME ASC,
    STATUS ASC,
    V.PLACA ASC,
    MA.NOME ASC,
    MO.NOME ASC,
    VT.NOME ASC,
    QTD_SLOTS_SEM_PNEUS DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### MODIFICA RELATORIO DE VALIDADE DE DOT - PL1999 ###################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(BIGINT[], TIMESTAMP WITHOUT TIME ZONE);

CREATE FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(
  F_COD_UNIDADES BIGINT[],
  F_DATA_ATUAL TIMESTAMP WITHOUT TIME ZONE)
  RETURNS TABLE(
    "UNIDADE" TEXT,
    "COD PNEU" TEXT,
    "PLACA" TEXT,
    "POSIÇÃO" TEXT,
    "DOT CADASTRADO" TEXT,
    "DOT VÁLIDO" TEXT,
    "TEMPO DE USO" TEXT,
    "TEMPO RESTANTE" TEXT,
    "DATA VENCIMENTO" TEXT,
    "VENCIDO" TEXT,
    "DATA GERAÇÃO" TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT        TEXT := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
  DIA_MES_ANO_FORMAT TEXT := 'DD/MM/YYYY';
  DATA_HORA_FORMAT   TEXT := 'DD/MM/YYYY HH24:MI';
  DATE_CONVERTER     TEXT := 'YYYYWW';
  PREFIXO_ANO        TEXT := SUBSTRING(F_DATA_ATUAL::TEXT, 1, 2);
BEGIN
  RETURN QUERY

  WITH INFORMACOES_PNEU AS (
      SELECT
        P.CODIGO_CLIENTE                               AS COD_PNEU,
        P.DOT                                          AS DOT_CADASTRADO,
        -- Remove letras, characteres especiais e espaços do dot.
        -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
        TRIM(REGEXP_REPLACE(P.DOT, '[^0-9]', '', 'g')) AS DOT_LIMPO,
        P.COD_UNIDADE                                  AS COD_UNIDADE,
        U.NOME                                         AS UNIDADE,
        VP.PLACA                                       AS PLACA_APLICADO,
        PONU.NOMENCLATURA                              AS POSICAO_PNEU
      FROM PNEU P
        JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
        LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
        LEFT JOIN VEICULO V ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
        LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
          ON U.CODIGO = PONU.COD_UNIDADE AND V.COD_TIPO = PONU.COD_TIPO_VEICULO AND VP.POSICAO = PONU.POSICAO_PROLOG
      WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
  ),

      DATA_DOT AS (
        SELECT
          IP.COD_PNEU,
          -- Transforma o DOT_FORMATADO em data
          CASE WHEN (CHAR_LENGTH(IP.DOT_LIMPO) = 4)
            THEN
              TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(IP.DOT_LIMPO, 3, 4)), (SUBSTRING(IP.DOT_LIMPO, 1, 2))),
                      DATE_CONVERTER)
          ELSE NULL END AS DOT_EM_DATA
        FROM INFORMACOES_PNEU IP
    ),

      VENCIMENTO_DOT AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
          -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
          -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
          -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
          CASE WHEN DD.DOT_EM_DATA <= (F_DATA_ATUAL::DATE)
            THEN DD.DOT_EM_DATA + INTERVAL '5 DAYS 5 YEARS' ELSE NULL END AS DATA_VENCIMENTO
        FROM DATA_DOT DD
    ),

      CALCULOS AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se o dot é válido
          -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
          -- podemos considerar inválidos os que possuem vencimento = null.
          CASE WHEN VD.DATA_VENCIMENTO IS NULL THEN 'INVÁLIDO' ELSE 'VÁLIDO' END     AS DOT_VALIDO,
          -- Cálculo tempo de uso
          CASE WHEN VD.DATA_VENCIMENTO IS NULL
            THEN NULL
          ELSE
            TO_CHAR(AGE((F_DATA_ATUAL :: DATE), DD.DOT_EM_DATA), DATE_FORMAT) END    AS TEMPO_DE_USO,
          -- Cálculo dias restantes
          TO_CHAR(AGE(VD.DATA_VENCIMENTO, F_DATA_ATUAL), DATE_FORMAT)                AS TEMPO_RESTANTE,
          -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
          F_IF(((VD.DATA_VENCIMENTO::DATE) - (F_DATA_ATUAL::DATE)) < 0, TRUE, FALSE) AS VENCIDO
        FROM DATA_DOT DD
          JOIN VENCIMENTO_DOT VD ON DD.COD_PNEU = VD.COD_PNEU
    )
  SELECT
    IP.UNIDADE::TEXT,
    IP.COD_PNEU::TEXT,
    COALESCE(IP.PLACA_APLICADO::TEXT, '-'),
    COALESCE(IP.POSICAO_PNEU::TEXT, '-'),
    COALESCE(IP.DOT_CADASTRADO::TEXT, '-'),
    CA.DOT_VALIDO,
    COALESCE(CA.TEMPO_DE_USO, '-'),
    COALESCE(CA.TEMPO_RESTANTE, '-'),
    COALESCE(TO_CHAR(VD.DATA_VENCIMENTO, DIA_MES_ANO_FORMAT)::TEXT, '-'),
    F_IF(CA.VENCIDO, 'SIM' :: TEXT, 'NÃO' :: TEXT),
    TO_CHAR(F_DATA_ATUAL, DATA_HORA_FORMAT)::TEXT
  FROM
    INFORMACOES_PNEU IP
    JOIN VENCIMENTO_DOT VD ON IP.COD_PNEU = VD.COD_PNEU
    JOIN CALCULOS CA ON CA.COD_PNEU = VD.COD_PNEU AND CA.COD_PNEU = IP.COD_PNEU
  ORDER BY VD.DATA_VENCIMENTO ASC, IP.PLACA_APLICADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################## INSERE NOVO TIPO DE DIAGRAMA ##########################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO VEICULO_DIAGRAMA (
  CODIGO,
  NOME,
  URL_IMAGEM)
VALUES (
  12,
  'MOTO',
  'WWW.GOOGLE.COM/MOTO'
);

INSERT INTO VEICULO_DIAGRAMA_EIXOS (
  COD_DIAGRAMA,
  TIPO_EIXO,
  POSICAO,
  QT_PNEUS,
  EIXO_DIRECIONAL)
VALUES (
  12,
  'D',
  1,
  1,
  TRUE
),
  (
    12,
    'T',
    2,
    1,
    FALSE
  );
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################## ADICIONA NA LISTAGEM DE PNEUS #############################################
--#################################### PLACA E CÓDIGO DO VEÍCULO QUANDO APLICADO  ######################################
--#################################### POSIÇÃO DO PNEU NA NOMENCLATURA DO CLIENTE ######################################
--######################################################################################################################
--######################################################################################################################
-- PL-1942
DROP FUNCTION FUNC_PNEUS_GET_LISTAGEM_PNEUS_BY_STATUS(BIGINT, TEXT);

CREATE FUNCTION FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(
  F_COD_UNIDADE BIGINT,
  F_STATUS_PNEU TEXT)
  RETURNS TABLE(
    CODIGO                       BIGINT,
    CODIGO_CLIENTE               TEXT,
    DOT                          TEXT,
    VALOR                        REAL,
    COD_UNIDADE_ALOCADO          BIGINT,
    COD_REGIONAL_ALOCADO         BIGINT,
    PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
    COD_MARCA_PNEU               BIGINT,
    NOME_MARCA_PNEU              TEXT,
    COD_MODELO_PNEU              BIGINT,
    NOME_MODELO_PNEU             TEXT,
    QT_SULCOS_MODELO_PNEU        SMALLINT,
    COD_MARCA_BANDA              BIGINT,
    NOME_MARCA_BANDA             TEXT,
    ALTURA_SULCOS_MODELO_PNEU    REAL,
    COD_MODELO_BANDA             BIGINT,
    NOME_MODELO_BANDA            TEXT,
    QT_SULCOS_MODELO_BANDA       SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA   REAL,
    VALOR_BANDA                  REAL,
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    PRESSAO_RECOMENDADA          REAL,
    PRESSAO_ATUAL                REAL,
    STATUS                       TEXT,
    VIDA_ATUAL                   INTEGER,
    VIDA_TOTAL                   INTEGER,
    POSICAO_PNEU                 INTEGER,
    POSICAO_APLICADO_CLIENTE     TEXT,
    COD_VEICULO_APLICADO         BIGINT,
    PLACA_APLICADO               TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO,
  P.CODIGO_CLIENTE,
  P.DOT,
  P.VALOR,
  U.CODIGO                                 AS COD_UNIDADE_ALOCADO,
  R.CODIGO                                 AS COD_REGIONAL_ALOCADO,
  P.PNEU_NOVO_NUNCA_RODADO,
  MP.CODIGO                                AS COD_MARCA_PNEU,
  MP.NOME                                  AS NOME_MARCA_PNEU,
  MOP.CODIGO                               AS COD_MODELO_PNEU,
  MOP.NOME                                 AS NOME_MODELO_PNEU,
  MOP.QT_SULCOS                            AS QT_SULCOS_MODELO_PNEU,
  MAB.CODIGO                               AS COD_MARCA_BANDA,
  MAB.NOME                                 AS NOME_MARCA_BANDA,
  MOP.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_PNEU,
  MOB.CODIGO                               AS COD_MODELO_BANDA,
  MOB.NOME                                 AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
  PVV.VALOR                                AS VALOR_BANDA,
  PD.ALTURA,
  PD.LARGURA,
  PD.ARO,
  PD.CODIGO                                AS COD_DIMENSAO,
  P.ALTURA_SULCO_CENTRAL_INTERNO,
  P.ALTURA_SULCO_CENTRAL_EXTERNO,
  P.ALTURA_SULCO_INTERNO,
  P.ALTURA_SULCO_EXTERNO,
  P.PRESSAO_RECOMENDADA,
  P.PRESSAO_ATUAL,
  P.STATUS,
  P.VIDA_ATUAL,
  P.VIDA_TOTAL,
  VP.POSICAO                               AS POSICAO_PNEU,
  COALESCE(PONU.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO,
  VEI.CODIGO                               AS COD_VEICULO,
  VEI.PLACA                                AS PLACA
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
  LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
  LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU ON PONU.COD_UNIDADE = VEI.COD_UNIDADE
                                                    AND PONU.COD_TIPO_VEICULO = VEI.COD_TIPO
                                                    AND PONU.POSICAO_PROLOG = VP.POSICAO
WHERE P.COD_UNIDADE = F_COD_UNIDADE
      AND P.STATUS LIKE F_STATUS_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;


DROP FUNCTION FUNC_PNEUS_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT);

CREATE FUNCTION FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT)
  RETURNS TABLE(
    CODIGO                       BIGINT,
    CODIGO_CLIENTE               TEXT,
    DOT                          TEXT,
    VALOR                        REAL,
    COD_UNIDADE_ALOCADO          BIGINT,
    COD_REGIONAL_ALOCADO         BIGINT,
    PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
    COD_MARCA_PNEU               BIGINT,
    NOME_MARCA_PNEU              TEXT,
    COD_MODELO_PNEU              BIGINT,
    NOME_MODELO_PNEU             TEXT,
    QT_SULCOS_MODELO_PNEU        SMALLINT,
    COD_MARCA_BANDA              BIGINT,
    NOME_MARCA_BANDA             TEXT,
    ALTURA_SULCOS_MODELO_PNEU    REAL,
    COD_MODELO_BANDA             BIGINT,
    NOME_MODELO_BANDA            TEXT,
    QT_SULCOS_MODELO_BANDA       SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA   REAL,
    VALOR_BANDA                  REAL,
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    PRESSAO_RECOMENDADA          REAL,
    PRESSAO_ATUAL                REAL,
    STATUS                       TEXT,
    VIDA_ATUAL                   INTEGER,
    VIDA_TOTAL                   INTEGER,
    POSICAO_PNEU                 INTEGER,
    POSICAO_APLICADO_CLIENTE     TEXT,
    COD_VEICULO_APLICADO         BIGINT,
    PLACA_APLICADO               TEXT)
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO,
  P.CODIGO_CLIENTE,
  P.DOT,
  P.VALOR,
  U.CODIGO                                 AS COD_UNIDADE_ALOCADO,
  R.CODIGO                                 AS COD_REGIONAL_ALOCADO,
  P.PNEU_NOVO_NUNCA_RODADO,
  MP.CODIGO                                AS COD_MARCA_PNEU,
  MP.NOME                                  AS NOME_MARCA_PNEU,
  MOP.CODIGO                               AS COD_MODELO_PNEU,
  MOP.NOME                                 AS NOME_MODELO_PNEU,
  MOP.QT_SULCOS                            AS QT_SULCOS_MODELO_PNEU,
  MAB.CODIGO                               AS COD_MARCA_BANDA,
  MAB.NOME                                 AS NOME_MARCA_BANDA,
  MOP.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_PNEU,
  MOB.CODIGO                               AS COD_MODELO_BANDA,
  MOB.NOME                                 AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
  PVV.VALOR                                AS VALOR_BANDA,
  PD.ALTURA,
  PD.LARGURA,
  PD.ARO,
  PD.CODIGO                                AS COD_DIMENSAO,
  P.ALTURA_SULCO_CENTRAL_INTERNO,
  P.ALTURA_SULCO_CENTRAL_EXTERNO,
  P.ALTURA_SULCO_INTERNO,
  P.ALTURA_SULCO_EXTERNO,
  P.PRESSAO_RECOMENDADA,
  P.PRESSAO_ATUAL,
  P.STATUS,
  P.VIDA_ATUAL,
  P.VIDA_TOTAL,
  VP.POSICAO                               AS POSICAO_PNEU,
  COALESCE(PONU.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO_CLIENTE,
  VEI.CODIGO                               AS COD_VEICULO_APLICADO,
  VEI.PLACA                                AS PLACA_APLICADO
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
  LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
  LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
  LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU ON PONU.COD_UNIDADE = VEI.COD_UNIDADE
                                                    AND PONU.COD_TIPO_VEICULO = VEI.COD_TIPO
                                                    AND PONU.POSICAO_PROLOG = VP.POSICAO
WHERE P.CODIGO = F_COD_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;


-- Recria function alterando nome da func de busca de pneu pelo código.
DROP FUNCTION FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(F_COD_PNEU BIGINT, F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(F_COD_PNEU BIGINT, F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    CODIGO                                BIGINT,
    CODIGO_CLIENTE                        TEXT,
    DOT                                   TEXT,
    VALOR                                 REAL,
    COD_UNIDADE_ALOCADO                   BIGINT,
    COD_REGIONAL_ALOCADO                  BIGINT,
    PNEU_NOVO_NUNCA_RODADO                BOOLEAN,
    COD_MARCA_PNEU                        BIGINT,
    NOME_MARCA_PNEU                       TEXT,
    COD_MODELO_PNEU                       BIGINT,
    NOME_MODELO_PNEU                      TEXT,
    QT_SULCOS_MODELO_PNEU                 SMALLINT,
    COD_MARCA_BANDA                       BIGINT,
    NOME_MARCA_BANDA                      TEXT,
    ALTURA_SULCOS_MODELO_PNEU             REAL,
    COD_MODELO_BANDA                      BIGINT,
    NOME_MODELO_BANDA                     TEXT,
    QT_SULCOS_MODELO_BANDA                SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA            REAL,
    VALOR_BANDA                           REAL,
    ALTURA                                INTEGER,
    LARGURA                               INTEGER,
    ARO                                   REAL,
    COD_DIMENSAO                          BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO          REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO          REAL,
    ALTURA_SULCO_INTERNO                  REAL,
    ALTURA_SULCO_EXTERNO                  REAL,
    PRESSAO_RECOMENDADA                   REAL,
    PRESSAO_ATUAL                         REAL,
    STATUS                                TEXT,
    VIDA_ATUAL                            INTEGER,
    VIDA_TOTAL                            INTEGER,
    POSICAO_PNEU                          INTEGER,
    POSICAO_APLICADO_CLIENTE              TEXT,
    COD_VEICULO_APLICADO                  BIGINT,
    PLACA_APLICADO                        TEXT,
    JA_FOI_AFERIDO                        BOOLEAN,
    COD_ULTIMA_AFERICAO                   BIGINT,
    DATA_HORA_ULTIMA_AFERICAO             TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO_ULTIMA_AFERICAO         TEXT,
    TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO TEXT,
    TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO  TEXT,
    NOME_COLABORADOR_ULTIMA_AFERICAO      TEXT)
LANGUAGE SQL
AS $$
WITH AFERICOES AS (
    SELECT
      INNER_TABLE.CODIGO           AS COD_AFERICAO,
      INNER_TABLE.COD_PNEU         AS COD_PNEU,
      INNER_TABLE.DATA_HORA,
      INNER_TABLE.PLACA_VEICULO,
      INNER_TABLE.TIPO_MEDICAO_COLETADA,
      INNER_TABLE.TIPO_PROCESSO_COLETA,
      INNER_TABLE.NOME_COLABORADOR AS NOME_COLABORADOR,
      CASE WHEN INNER_TABLE.NOME_COLABORADOR IS NOT NULL
        THEN TRUE
      ELSE FALSE END               AS JA_FOI_AFERIDO
    FROM (SELECT
            A.CODIGO,
            AV.COD_PNEU,
            A.DATA_HORA,
            A.PLACA_VEICULO,
            A.TIPO_MEDICAO_COLETADA,
            A.TIPO_PROCESSO_COLETA,
            C.NOME                    AS NOME_COLABORADOR,
            MAX(A.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
            LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
            LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
            LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.STATUS = 'ESTOQUE' AND P.CODIGO = F_COD_PNEU) AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT
  F.*,
  A.JA_FOI_AFERIDO                      AS JA_FOI_AFERIDO,
  A.COD_AFERICAO                        AS COD_ULTIMA_AFERICAO,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMA_AFERICAO,
  A.PLACA_VEICULO :: TEXT               AS PLACA_VEICULO_ULTIMA_AFERICAO,
  A.TIPO_MEDICAO_COLETADA :: TEXT       AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
  A.TIPO_PROCESSO_COLETA :: TEXT        AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
  A.NOME_COLABORADOR :: TEXT            AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU) AS F
  LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU
WHERE F.CODIGO = F_COD_PNEU;
$$;



DROP FUNCTION FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(F_COD_UNIDADE BIGINT);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    CODIGO                                BIGINT,
    CODIGO_CLIENTE                        TEXT,
    DOT                                   TEXT,
    VALOR                                 REAL,
    COD_UNIDADE_ALOCADO                   BIGINT,
    COD_REGIONAL_ALOCADO                  BIGINT,
    PNEU_NOVO_NUNCA_RODADO                BOOLEAN,
    COD_MARCA_PNEU                        BIGINT,
    NOME_MARCA_PNEU                       TEXT,
    COD_MODELO_PNEU                       BIGINT,
    NOME_MODELO_PNEU                      TEXT,
    QT_SULCOS_MODELO_PNEU                 SMALLINT,
    COD_MARCA_BANDA                       BIGINT,
    NOME_MARCA_BANDA                      TEXT,
    ALTURA_SULCOS_MODELO_PNEU             REAL,
    COD_MODELO_BANDA                      BIGINT,
    NOME_MODELO_BANDA                     TEXT,
    QT_SULCOS_MODELO_BANDA                SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA            REAL,
    VALOR_BANDA                           REAL,
    ALTURA                                INTEGER,
    LARGURA                               INTEGER,
    ARO                                   REAL,
    COD_DIMENSAO                          BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO          REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO          REAL,
    ALTURA_SULCO_INTERNO                  REAL,
    ALTURA_SULCO_EXTERNO                  REAL,
    PRESSAO_RECOMENDADA                   REAL,
    PRESSAO_ATUAL                         REAL,
    STATUS                                TEXT,
    VIDA_ATUAL                            INTEGER,
    VIDA_TOTAL                            INTEGER,
    POSICAO_PNEU                          INTEGER,
    POSICAO_APLICADO_CLIENTE              TEXT,
    COD_VEICULO_APLICADO                  BIGINT,
    PLACA_APLICADO                        TEXT,
    JA_FOI_AFERIDO                        BOOLEAN,
    COD_ULTIMA_AFERICAO                   BIGINT,
    DATA_HORA_ULTIMA_AFERICAO             TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO_ULTIMA_AFERICAO         TEXT,
    TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO TEXT,
    TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO  TEXT,
    NOME_COLABORADOR_ULTIMA_AFERICAO      TEXT)
LANGUAGE SQL
AS $$
WITH AFERICOES AS (
    SELECT
      INNER_TABLE.CODIGO           AS COD_AFERICAO,
      INNER_TABLE.COD_PNEU         AS COD_PNEU,
      INNER_TABLE.DATA_HORA,
      INNER_TABLE.PLACA_VEICULO,
      INNER_TABLE.TIPO_MEDICAO_COLETADA,
      INNER_TABLE.TIPO_PROCESSO_COLETA,
      INNER_TABLE.NOME_COLABORADOR AS NOME_COLABORADOR,
      CASE WHEN INNER_TABLE.NOME_COLABORADOR IS NOT NULL
        THEN TRUE
      ELSE FALSE END               AS JA_FOI_AFERIDO
    FROM (SELECT
            A.CODIGO,
            AV.COD_PNEU,
            A.DATA_HORA,
            A.PLACA_VEICULO,
            A.TIPO_MEDICAO_COLETADA,
            A.TIPO_PROCESSO_COLETA,
            C.NOME                    AS NOME_COLABORADOR,
            MAX(A.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
            LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
            LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
            LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.COD_UNIDADE = F_COD_UNIDADE AND P.STATUS = 'ESTOQUE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT
  F.*,
  A.JA_FOI_AFERIDO                                   AS JA_FOI_AFERIDO,
  A.COD_AFERICAO                                     AS COD_ULTIMA_AFERICAO,
  A.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE) AS DATA_HORA_ULTIMA_AFERICAO,
  A.PLACA_VEICULO :: TEXT                            AS PLACA_VEICULO_ULTIMA_AFERICAO,
  A.TIPO_MEDICAO_COLETADA :: TEXT                    AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
  A.TIPO_PROCESSO_COLETA :: TEXT                     AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
  A.NOME_COLABORADOR :: TEXT                         AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(F_COD_UNIDADE, 'ESTOQUE') AS F
  LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU;
$$;


DROP FUNCTION FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE(F_COD_UNIDADE BIGINT);

CREATE OR REPLACE FUNCTION FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    CODIGO                       BIGINT,
    CODIGO_CLIENTE               TEXT,
    DOT                          TEXT,
    VALOR                        REAL,
    COD_UNIDADE_ALOCADO          BIGINT,
    COD_REGIONAL_ALOCADO         BIGINT,
    PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
    COD_MARCA_PNEU               BIGINT,
    NOME_MARCA_PNEU              TEXT,
    COD_MODELO_PNEU              BIGINT,
    NOME_MODELO_PNEU             TEXT,
    QT_SULCOS_MODELO_PNEU        SMALLINT,
    COD_MARCA_BANDA              BIGINT,
    NOME_MARCA_BANDA             TEXT,
    ALTURA_SULCOS_MODELO_PNEU    REAL,
    COD_MODELO_BANDA             BIGINT,
    NOME_MODELO_BANDA            TEXT,
    QT_SULCOS_MODELO_BANDA       SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA   REAL,
    VALOR_BANDA                  REAL,
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    PRESSAO_RECOMENDADA          REAL,
    PRESSAO_ATUAL                REAL,
    STATUS                       TEXT,
    VIDA_ATUAL                   INTEGER,
    VIDA_TOTAL                   INTEGER,
    POSICAO_PNEU                 INTEGER,
    POSICAO_APLICADO_CLIENTE     TEXT,
    COD_VEICULO_APLICADO         BIGINT,
    PLACA_APLICADO               TEXT,
    COD_MOVIMENTACAO             BIGINT,
    COD_RECAPADORA               BIGINT,
    NOME_RECAPADORA              TEXT,
    COD_EMPRESA_RECAPADORA       BIGINT,
    RECAPADORA_ATIVA             BOOLEAN,
    COD_COLETA                   TEXT)
LANGUAGE SQL
AS $$
WITH MOVIMENTACOES_ANALISE AS (
    SELECT
      INNER_TABLE.CODIGO                 AS COD_MOVIMENTACAO,
      INNER_TABLE.COD_PNEU               AS COD_PNEU,
      INNER_TABLE.COD_RECAPADORA_DESTINO AS COD_RECAPADORA,
      INNER_TABLE.NOME                   AS NOME_RECAPADORA,
      INNER_TABLE.COD_EMPRESA            AS COD_EMPRESA_RECAPADORA,
      INNER_TABLE.ATIVA                  AS RECAPADORA_ATIVA,
      INNER_TABLE.COD_COLETA             AS COD_COLETA
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
          WHERE COD_UNIDADE = F_COD_UNIDADE AND MD.TIPO_DESTINO = 'ANALISE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_MOVIMENTACAO
)

SELECT
  FUNC.*,
  MA.COD_MOVIMENTACAO,
  MA.COD_RECAPADORA,
  MA.NOME_RECAPADORA,
  MA.COD_EMPRESA_RECAPADORA,
  MA.RECAPADORA_ATIVA,
  MA.COD_COLETA
FROM FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(F_COD_UNIDADE, 'ANALISE') AS FUNC
  JOIN MOVIMENTACOES_ANALISE MA ON MA.COD_PNEU = FUNC.CODIGO;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Permite filtrar por colaboradores ativos nos relatórios de folha ponto. (PL-1905)
DROP FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE,
  F_TIME_ZONE_UNIDADE  TEXT);

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(
  F_COD_UNIDADE                 BIGINT,
  F_COD_TIPO_INTERVALO          BIGINT,
  F_CPF_COLABORADOR             BIGINT,
  F_DATA_INICIAL                DATE,
  F_DATA_FINAL                  DATE,
  F_APENAS_COLABORADORES_ATIVOS BOOLEAN,
  F_TIME_ZONE_UNIDADE           TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                BIGINT,
    NOME_COLABORADOR               TEXT,
    COD_TIPO_INTERVALO             BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_INICIO_UTC           TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_UTC              TIMESTAMP WITHOUT TIME ZONE,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN,
    DIFERENCA_MARCACOES_SEGUNDOS   DOUBLE PRECISION,
    TROCOU_DIA                     BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  F.CPF_COLABORADOR                                                                      AS CPF_COLABORADOR,
  C.NOME                                                                                 AS NOME_COLABORADOR,
  F.COD_TIPO_INTERVALO                                                                   AS COD_TIPO_INTERVALO,
  F.COD_TIPO_INTERVALO_POR_UNIDADE                                                       AS COD_TIPO_INTERVALO_POR_UNIDADE,
  F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL) AT TIME ZONE F_TIME_ZONE_UNIDADE AS DATA_HORA_INICIO,
  F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL) AT TIME ZONE F_TIME_ZONE_UNIDADE       AS DATA_HORA_FIM,
  F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL) AT TIME ZONE 'UTC'               AS DATA_HORA_INICIO_UTC,
  F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL) AT TIME ZONE 'UTC'                     AS DATA_HORA_FIM_UTC,
  F_IF(F.STATUS_ATIVO_INICIO, F.FOI_AJUSTADO_INICIO, NULL)                               AS FOI_AJUSTADO_INICIO,
  F_IF(F.STATUS_ATIVO_FIM, F.FOI_AJUSTADO_FIM, NULL)                                     AS FOI_AJUSTADO_FIM,
  F_IF(F.STATUS_ATIVO_INICIO AND F.STATUS_ATIVO_FIM,
       EXTRACT(EPOCH FROM (F.DATA_HORA_FIM - F.DATA_HORA_INICIO)),
       NULL)                                                                             AS DIFERENCA_MARCACOES_SEGUNDOS,
  -- Só importa essa verificação se início e fim estiverem ativos.
  F.STATUS_ATIVO_INICIO AND F.STATUS_ATIVO_FIM AND
  (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE !=
  (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE                             AS TROCOU_DIA
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  F_IF(F_APENAS_COLABORADORES_ATIVOS IS NULL, TRUE, C.STATUS_ATIVO)
  AND
  (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
  AND
  -- FILTRA POR MARCAÇÕES QUE TENHAM SEU INÍCIO DENTRO DO PERÍODO FILTRADO, NÃO IMPORTANDO SE TENHAM FIM.
  (((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
    AND (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
   OR
   -- FILTRA POR MARCAÇÕES QUE TENHAM SEU FIM DENTRO DO PERÍODO FILTRADO, NÃO IMPORTANDO SE TENHAM INÍCIO.
   ((F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
    AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
   OR
   -- FILTRA POR MARCAÇÕES QUE TIVERAM SEU INÍCIO ANTES DO FILTRO E FIM APÓS O FILTRO.
   ((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE < F_DATA_INICIAL
    AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE > F_DATA_FINAL))
ORDER BY F.CPF_COLABORADOR, COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) ASC;
$$;


DROP FUNCTION FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE,
  F_TIME_ZONE_UNIDADE  TEXT);

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(
  F_COD_UNIDADE                 BIGINT,
  F_COD_TIPO_INTERVALO          BIGINT,
  F_CPF_COLABORADOR             BIGINT,
  F_DATA_INICIAL                DATE,
  F_DATA_FINAL                  DATE,
  F_APENAS_COLABORADORES_ATIVOS BOOLEAN,
  F_TIME_ZONE_UNIDADE           TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                TEXT,
    NOME_COLABORADOR               TEXT,
    COD_MARCACAO_JORNADA           BIGINT,
    DIA_BASE                       DATE,
    COD_TIPO_INTERVALO             BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    DIFERENCA_MARCACOES_SEGUNDOS   BIGINT,
    TEMPO_NOTURNO_EM_SEGUNDOS      BIGINT,
    MARCACAO_INICIO_AJUSTADA       BOOLEAN,
    MARCACAO_FIM_AJUSTADA          BOOLEAN,
    TROCOU_DIA                     BOOLEAN,
    TIPO_JORNADA                   BOOLEAN,
    DESCONTA_JORNADA_BRUTA         BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA       BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPOS_DESCONTADOS_BRUTA   BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_BRUTA);
  COD_TIPOS_DESCONTADOS_LIQUIDA BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_LIQUIDA);
BEGIN
  RETURN QUERY
  WITH TODAS_MARCACOES_UNIDADE AS (
      SELECT
        F.CPF_COLABORADOR                                        AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                                     AS COD_TIPO_INTERVALO,
        F.COD_TIPO_INTERVALO_POR_UNIDADE                         AS COD_TIPO_INTERVALO_POR_UNIDADE,
        F_IF(F.STATUS_ATIVO_INICIO, F.COD_MARCACAO_INICIO, NULL) AS COD_MARCACAO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.COD_MARCACAO_FIM, NULL)       AS COD_MARCACAO_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL)    AS DATA_HORA_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL)          AS DATA_HORA_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.FOI_AJUSTADO_INICIO, NULL) AS FOI_AJUSTADO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.FOI_AJUSTADO_FIM, NULL)       AS FOI_AJUSTADO_FIM,
        F.TIPO_JORNADA                                           AS TIPO_JORNADA
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) AS F
      WHERE (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
  ),

    -- MARCAÇÕES DE JORNADA QUE POSSUEM INÍCIO E FIM MARCADOS.
      APENAS_JORNADAS_COMPLETAS AS (
        SELECT
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL)
                   OR
                   (TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) < F_DATA_INICIAL
                    AND TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) > F_DATA_FINAL))
    ),

    -- MARCAÇÕES DE JORNADA QUE NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM.
      APENAS_JORNADAS_INCOMPLETAS AS (
        SELECT
          COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)                         AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND (TDU.DATA_HORA_INICIO IS NULL
                   OR TDU.DATA_HORA_FIM IS NULL)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO E FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_DENTRO_JORNADA AS (
        SELECT
          AJC.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM)
               @> TSTZRANGE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
               AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
               AND TDU.DATA_HORA_INICIO IS NOT NULL
               AND TDU.DATA_HORA_FIM IS NOT NULL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA AS (
        SELECT
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM) @> TDU.DATA_HORA_INICIO
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA AS (
        SELECT
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM) @> TDU.DATA_HORA_FIM
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
              -- Se uma marcação teve início dentro de uma jornada e fim dentro de outra, então ela foi adiciona à uma
              -- jornada na CTE MARCACOES_INICIO_DENTRO_JORNADA e agora seria adicionada novamente a outra jornada,
              -- precisamos impedir isso de acontecer.
              AND (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                              WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
    ),

    -- MARCAÇÕES QUE OU NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM MAS ESTÃO DENTRO DE UMA JORNADA.
      MARCACOES_INCOMPLETAS_DENTRO_JORNADA AS (
        SELECT
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
               AND TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM)
                   @> COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS E NÃO TÊM INÍCIO E NEM FIM DENTRO DE NENHUMA JORNADA.
      MARCACOES_COMPLETAS_FORA_JORNADA AS (
        SELECT
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL))
              AND ((NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
    ),

    -- MARCAÇÕES QUE POSSUEM APENAS INÍCIO OU FIM, NÃO AMBOS, E ESTÃO FORA DE JORNADA.
      MARCACOES_INCOMPLETAS_FORA_JORNADA AS (
        SELECT
          NULL :: BIGINT                                                                  AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
              -- Nossa maneira de garantir que a marcação está fora de jornada, é garantindo que ela não está dentro
              -- através das CTEs criadas anteriormente que contém as marcações incompletas dentro de jornada.
              AND (NOT EXISTS(SELECT COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM)
                              FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA MIDJ
                              -- Se nossas marcações de início e fim tivessem códigos diferentes, isso não funcionaria,
                              -- mas como o código é sequencial, não importa se compararmos um início com um fim ou
                              -- vice versa, porque os códigos não vão bater.
                              WHERE COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM) =
                                    COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)))
    ),

      TODAS_MARCACOES AS (
      SELECT *
      FROM APENAS_JORNADAS_COMPLETAS
      UNION ALL
      SELECT *
      FROM APENAS_JORNADAS_INCOMPLETAS
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FORA_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_FORA_JORNADA
    )

  SELECT
    LPAD(TM.CPF_COLABORADOR :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
    C.NOME :: TEXT                                                     AS NOME_COLABORADOR,
    TM.COD_MARCACAO_JORNADA                                            AS COD_MARCACAO_JORNADA,
    TM.DIA_BASE                                                        AS DIA_BASE,
    TM.COD_TIPO_INTERVALO                                              AS COD_TIPO_INTERVALO,
    TM.COD_TIPO_INTERVALO_POR_UNIDADE                                  AS COD_TIPO_INTERVALO_POR_UNIDADE,
    TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE               AS DATA_HORA_INICIO,
    TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE                  AS DATA_HORA_FIM,
    TO_SECONDS(TM.DATA_HORA_FIM - TM.DATA_HORA_INICIO)                 AS DIFERENCA_MARCACOES_SEGUNDOS,
    COALESCE(
        FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(TM.DATA_HORA_INICIO,
                                                               TM.DATA_HORA_FIM,
                                                               F_TIME_ZONE_UNIDADE),
        0)                                                             AS TEMPO_NOTURNO_EM_SEGUNDOS,
    TM.FOI_AJUSTADO_INICIO                                             AS FOI_AJUSTADO_INICIO,
    TM.FOI_AJUSTADO_FIM                                                AS FOI_AJUSTADO_FIM,
    TM.DATA_HORA_INICIO IS NOT NULL AND TM.DATA_HORA_FIM IS NOT NULL
    AND (TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE
        != (TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE AS TROCOU_DIA,
    TM.TIPO_JORNADA                                                    AS TIPO_JORNADA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_BRUTA)          AS DESCONTA_JORNADA_BRUTA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_LIQUIDA)        AS DESCONTA_JORNADA_LIQUIDA
  FROM TODAS_MARCACOES AS TM
    JOIN COLABORADOR C
      ON TM.CPF_COLABORADOR = C.CPF
  WHERE
    F_IF(F_APENAS_COLABORADORES_ATIVOS IS NULL, TRUE, C.STATUS_ATIVO)
  ORDER BY TM.CPF_COLABORADOR,
    TM.DIA_BASE,
    TM.COD_MARCACAO_JORNADA ASC,
    COALESCE(TM.DATA_HORA_INICIO, TM.DATA_HORA_FIM) ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Retorna erro específico caso marcação de fim antes da de ínicio ao gerar o relatório.
-- PL-1744
CREATE OR REPLACE FUNCTION PROLOG_TSTZRANGE(
  LOWER_BOUND TIMESTAMP WITH TIME ZONE,
  UPPER_BOUND TIMESTAMP WITH TIME ZONE,
  ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS TSTZRANGE
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF ERROR_MESSAGE IS NOT NULL AND LOWER_BOUND > UPPER_BOUND
    THEN
    RAISE EXCEPTION '%', ERROR_MESSAGE
    USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  RETURN TSTZRANGE(LOWER_BOUND, UPPER_BOUND);
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(
  F_COD_UNIDADE                 BIGINT,
  F_COD_TIPO_INTERVALO          BIGINT,
  F_CPF_COLABORADOR             BIGINT,
  F_DATA_INICIAL                DATE,
  F_DATA_FINAL                  DATE,
  F_APENAS_COLABORADORES_ATIVOS BOOLEAN,
  F_TIME_ZONE_UNIDADE           TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                TEXT,
    NOME_COLABORADOR               TEXT,
    COD_MARCACAO_JORNADA           BIGINT,
    DIA_BASE                       DATE,
    COD_TIPO_INTERVALO             BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    DIFERENCA_MARCACOES_SEGUNDOS   BIGINT,
    TEMPO_NOTURNO_EM_SEGUNDOS      BIGINT,
    MARCACAO_INICIO_AJUSTADA       BOOLEAN,
    MARCACAO_FIM_AJUSTADA          BOOLEAN,
    TROCOU_DIA                     BOOLEAN,
    TIPO_JORNADA                   BOOLEAN,
    DESCONTA_JORNADA_BRUTA         BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA       BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPOS_DESCONTADOS_BRUTA   BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_BRUTA);
  COD_TIPOS_DESCONTADOS_LIQUIDA BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_LIQUIDA);
  -- Usamos o 'E' no começo das duas linhas para escapar os \n.
  ERROR_MESSAGE_TSTZRANGE TEXT := E'Erro!\nA marcação do colaborador <b>%s</b> possui fim antes do início, ' ||
                                  E'impossibilitando a geração do relatório.\n<b>Início: %s  </b>\n<b>Fim: %s  </b>\n\n' ||
                                  '<a href="https://prologapp.zendesk.com/hc/pt-br/articles/360002008792-Relat%%C3%%B3rios-Controle-de-Jornada#%%E2%%80%%9Cfolha" ' ||
                                  'target="_blank">Clique aqui para mais informações </ a >';
  ERROR_DATE_FORMAT TEXT := 'DD/MM/YYYY HH24:MI:SS';
BEGIN
  RETURN QUERY
  WITH TODAS_MARCACOES_UNIDADE AS (
      SELECT
        C.NOME                                                   AS NOME_COLABORADOR,
        F.CPF_COLABORADOR                                        AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                                     AS COD_TIPO_INTERVALO,
        F.COD_TIPO_INTERVALO_POR_UNIDADE                         AS COD_TIPO_INTERVALO_POR_UNIDADE,
        F_IF(F.STATUS_ATIVO_INICIO, F.COD_MARCACAO_INICIO, NULL) AS COD_MARCACAO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.COD_MARCACAO_FIM, NULL)       AS COD_MARCACAO_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL)    AS DATA_HORA_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL)          AS DATA_HORA_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.FOI_AJUSTADO_INICIO, NULL) AS FOI_AJUSTADO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.FOI_AJUSTADO_FIM, NULL)       AS FOI_AJUSTADO_FIM,
        F.TIPO_JORNADA                                           AS TIPO_JORNADA
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) AS F
        -- O JOIN com colaborador é feito na primeira CTE para além de evitarmos processar colaboradores inativos
        -- (caso esse seja o filtro), também evitarmos de dar um erro de TSRANGE para um colaborador inativo.
        JOIN COLABORADOR C
          ON F.CPF_COLABORADOR = C.CPF
      WHERE
        (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
        AND F_IF(F_APENAS_COLABORADORES_ATIVOS IS NULL, TRUE, C.STATUS_ATIVO)
  ),

    -- MARCAÇÕES DE JORNADA QUE POSSUEM INÍCIO E FIM MARCADOS.
      APENAS_JORNADAS_COMPLETAS AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL)
                   OR
                   (TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) < F_DATA_INICIAL
                    AND TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) > F_DATA_FINAL))
    ),

    -- MARCAÇÕES DE JORNADA QUE NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM.
      APENAS_JORNADAS_INCOMPLETAS AS (
        SELECT
          TDU.NOME_COLABORADOR                                                            AS NOME_COLABORADOR,
          COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)                         AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND (TDU.DATA_HORA_INICIO IS NULL
                   OR TDU.DATA_HORA_FIM IS NULL)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO E FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          AJC.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> PROLOG_TSTZRANGE(
                   TDU.DATA_HORA_INICIO,
                   TDU.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(TDU.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(TDU.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
               AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
               AND TDU.DATA_HORA_INICIO IS NOT NULL
               AND TDU.DATA_HORA_FIM IS NOT NULL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> TDU.DATA_HORA_INICIO
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> TDU.DATA_HORA_FIM
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
              -- Se uma marcação teve início dentro de uma jornada e fim dentro de outra, então ela foi adiciona à uma
              -- jornada na CTE MARCACOES_INICIO_DENTRO_JORNADA e agora seria adicionada novamente a outra jornada,
              -- precisamos impedir isso de acontecer.
              AND (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                              WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
    ),

    -- MARCAÇÕES QUE OU NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM MAS ESTÃO DENTRO DE UMA JORNADA.
      MARCACOES_INCOMPLETAS_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
               AND PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
                   @> COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS E NÃO TÊM INÍCIO E NEM FIM DENTRO DE NENHUMA JORNADA.
      MARCACOES_COMPLETAS_FORA_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL))
              AND ((NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
    ),

    -- MARCAÇÕES QUE POSSUEM APENAS INÍCIO OU FIM, NÃO AMBOS, E ESTÃO FORA DE JORNADA.
      MARCACOES_INCOMPLETAS_FORA_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                                                            AS NOME_COLABORADOR,
          NULL :: BIGINT                                                                  AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
              -- Nossa maneira de garantir que a marcação está fora de jornada, é garantindo que ela não está dentro
              -- através das CTEs criadas anteriormente que contém as marcações incompletas dentro de jornada.
              AND (NOT EXISTS(SELECT COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM)
                              FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA MIDJ
                              -- Se nossas marcações de início e fim tivessem códigos diferentes, isso não funcionaria,
                              -- mas como o código é sequencial, não importa se compararmos um início com um fim ou
                              -- vice versa, porque os códigos não vão bater.
                              WHERE COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM) =
                                    COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)))
    ),

      TODAS_MARCACOES AS (
      SELECT *
      FROM APENAS_JORNADAS_COMPLETAS
      UNION ALL
      SELECT *
      FROM APENAS_JORNADAS_INCOMPLETAS
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FORA_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_FORA_JORNADA
    )

  SELECT
    LPAD(TM.CPF_COLABORADOR :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
    TM.NOME_COLABORADOR :: TEXT                                        AS NOME_COLABORADOR,
    TM.COD_MARCACAO_JORNADA                                            AS COD_MARCACAO_JORNADA,
    TM.DIA_BASE                                                        AS DIA_BASE,
    TM.COD_TIPO_INTERVALO                                              AS COD_TIPO_INTERVALO,
    TM.COD_TIPO_INTERVALO_POR_UNIDADE                                  AS COD_TIPO_INTERVALO_POR_UNIDADE,
    TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE               AS DATA_HORA_INICIO,
    TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE                  AS DATA_HORA_FIM,
    TO_SECONDS(TM.DATA_HORA_FIM - TM.DATA_HORA_INICIO)                 AS DIFERENCA_MARCACOES_SEGUNDOS,
    COALESCE(
        FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(TM.DATA_HORA_INICIO,
                                                               TM.DATA_HORA_FIM,
                                                               F_TIME_ZONE_UNIDADE),
        0)                                                             AS TEMPO_NOTURNO_EM_SEGUNDOS,
    TM.FOI_AJUSTADO_INICIO                                             AS FOI_AJUSTADO_INICIO,
    TM.FOI_AJUSTADO_FIM                                                AS FOI_AJUSTADO_FIM,
    TM.DATA_HORA_INICIO IS NOT NULL AND TM.DATA_HORA_FIM IS NOT NULL
    AND (TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE
        != (TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE AS TROCOU_DIA,
    TM.TIPO_JORNADA                                                    AS TIPO_JORNADA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_BRUTA)          AS DESCONTA_JORNADA_BRUTA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_LIQUIDA)        AS DESCONTA_JORNADA_LIQUIDA
  FROM TODAS_MARCACOES AS TM
  ORDER BY TM.CPF_COLABORADOR,
    TM.DIA_BASE,
    TM.COD_MARCACAO_JORNADA ASC,
    COALESCE(TM.DATA_HORA_INICIO, TM.DATA_HORA_FIM) ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Aplica filtro selecionado pelo colaborador na query e remove filtro apenas pelos ativos. PL-1667
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TEMPO_TOTAL_POR_TIPO_MARCACAO(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE)
  RETURNS TABLE(
    CPF_COLABORADOR                   TEXT,
    NOME                              TEXT,
    CARGO                             TEXT,
    COD_TIPO_INTERVALO                TEXT,
    NOME_TIPO_INTERVALO               TEXT,
    TEMPO_TOTAL_MILLIS                TEXT,
    TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH TIPOS_UNIDADE AS (
      SELECT
        C.CPF     AS CPF_COLABORADOR,
        C.NOME    AS NOME_COLABORADOR,
        F.NOME    AS NOME_CARGO,
        IT.CODIGO AS COD_TIPO_MARCACAO,
        IT.NOME   AS NOME_TIPO_MARCACAO
      FROM COLABORADOR C
        JOIN FUNCAO F
          ON C.COD_FUNCAO = F.CODIGO
        JOIN INTERVALO_TIPO IT
          ON C.COD_UNIDADE = IT.COD_UNIDADE
      WHERE
        IT.COD_UNIDADE = F_COD_UNIDADE
        AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.CODIGO = F_COD_TIPO_INTERVALO)
      ORDER BY C.CPF, IT.CODIGO
  ),
      TOTAIS_POR_TIPO AS (
        SELECT *
        FROM
          (SELECT
             C.CPF     AS CPF_COLABORADOR,
             IT.CODIGO AS COD_TIPO_INTERVALO,
             SUM(TO_SECONDS(I.DATA_HORA_FIM - I.DATA_HORA_INICIO))
             OVER W    AS TEMPO_TOTAL_SEGUNDOS,
             SUM(FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(I.DATA_HORA_INICIO,
                                                                        I.DATA_HORA_FIM,
                                                                        TZ_UNIDADE))
             OVER W    AS TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS
           FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, F_COD_TIPO_INTERVALO) AS I
             JOIN COLABORADOR AS C
               ON I.CPF_COLABORADOR = C.CPF
             LEFT JOIN INTERVALO_TIPO AS IT
               ON I.COD_TIPO_INTERVALO = IT.CODIGO
           WHERE ((((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE)::DATE >= F_DATA_INICIAL)
                   AND (((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE)::DATE <= F_DATA_FINAL)))
                  OR
                  (((I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE)::DATE >= F_DATA_INICIAL)
                   AND ((I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE)::DATE <= F_DATA_FINAL)))
                  -- Expurga marcações que não tem início ou fim.
                  AND I.DATA_HORA_INICIO IS NOT NULL AND I.DATA_HORA_FIM IS NOT NULL
                  -- Retiramos do cálculo as marcações que foram desativadas.
                  AND I.STATUS_ATIVO_INICIO = TRUE
                  AND I.STATUS_ATIVO_FIM = TRUE
           GROUP BY C.CPF, I.COD_TIPO_INTERVALO, I.DATA_HORA_FIM, I.DATA_HORA_INICIO, IT.CODIGO
           WINDOW W AS (
             PARTITION BY C.CPF, IT.CODIGO)) AS T
        GROUP BY
          T.CPF_COLABORADOR,
          T.COD_TIPO_INTERVALO,
          T.TEMPO_TOTAL_SEGUNDOS,
          T.TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS
        ORDER BY T.CPF_COLABORADOR, T.COD_TIPO_INTERVALO)

  SELECT
    LPAD(TU.CPF_COLABORADOR::TEXT, 11, '0')                AS CPF_COLABORADOR,
    TU.NOME_COLABORADOR::TEXT                              AS NOME_COLABORADOR,
    TU.NOME_CARGO::TEXT                                    AS NOME_CARGO,
    TU.COD_TIPO_MARCACAO::TEXT                             AS COD_TIPO_MARCACAO,
    TU.NOME_TIPO_MARCACAO::TEXT                            AS NOME_TIPO_MARCACAO,
    (TPT.TEMPO_TOTAL_SEGUNDOS * 1000)::TEXT                AS TEMPO_TOTAL_MILLIS,
    (TPT.TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS * 1000)::TEXT AS TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS
  FROM TIPOS_UNIDADE TU
    LEFT JOIN TOTAIS_POR_TIPO TPT
      ON TU.CPF_COLABORADOR = TPT.CPF_COLABORADOR AND TU.COD_TIPO_MARCACAO = TPT.COD_TIPO_INTERVALO
  ORDER BY TU.CPF_COLABORADOR, TU.COD_TIPO_MARCACAO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- (PL-1975) Adiciona medidas do pneu no relatório de serviços fechados.
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(
F_COD_UNIDADE TEXT [],
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE,
F_DATA_ATUAL DATE );

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(
  F_COD_UNIDADE  TEXT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE,
  F_DATA_ATUAL   DATE)
  RETURNS TABLE(
    "UNIDADE DO SERVIÇO"            TEXT,
    "CÓDIGO DO SERVIÇO"             BIGINT,
    "TIPO DO SERVIÇO"               TEXT,
    "QTD APONTAMENTOS"              INTEGER,
    "DATA HORA ABERTURA"            TEXT,
    "QTD DIAS EM ABERTO"            TEXT,
    "NOME DO COLABORADOR"           TEXT,
    "PLACA"                         TEXT,
    "PNEU"                          TEXT,
    "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
    "MEDIDAS"                       TEXT,
    "COD AFERIÇÃO"                  BIGINT,
    "SULCO INTERNO"                 REAL,
    "SULCO CENTRAL INTERNO"         REAL,
    "SULCO CENTRAL EXTERNO"         REAL,
    "SULCO EXTERNO"                 REAL,
    "PRESSÃO (PSI)"                 REAL,
    "PRESSÃO RECOMENDADA (PSI)"     REAL,
    "ESTADO ATUAL"                  TEXT,
    "MÁXIMO DE RECAPAGENS"          TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                       AS UNIDADE_SERVICO,
  AM.CODIGO                                                                                    AS CODIGO_SERVICO,
  AM.TIPO_SERVICO                                                                              AS TIPO_SERVICO,
  AM.QT_APONTAMENTOS                                                                           AS QT_APONTAMENTOS,
  TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA_ABERTURA,
  (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT     AS DIAS_EM_ABERTO,
  C.NOME                                                                                       AS NOME_COLABORADOR,
  A.PLACA_VEICULO                                                                              AS PLACA_VEICULO,
  P.CODIGO_CLIENTE                                                                             AS COD_PNEU_PROBLEMA,
  COALESCE(PONU.NOMENCLATURA, '-')                                                             AS POSICAO_PNEU_PROBLEMA,
  DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                             AS MEDIDAS,
  A.CODIGO                                                                                     AS COD_AFERICAO,
  AV.ALTURA_SULCO_EXTERNO                                                                      AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO                                                              AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO                                                              AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO                                                                      AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI                                                                                       AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA                                                                        AS PRESSAO_RECOMENDADA,
  PVN.NOME                                                                                     AS VIDA_PNEU_PROBLEMA,
  PRN.NOME                                                                                     AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P
    ON AM.COD_PNEU = P.CODIGO
  JOIN DIMENSAO_PNEU DP
    ON DP.CODIGO = P.COD_DIMENSAO
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
WHERE AM.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE >= f_data_inicial
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE <= f_data_final
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- (PL-1737) Adiciona valor da banda aplicada ao relatório de resumo de pneus.
DROP FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(
F_COD_UNIDADE TEXT [],
F_STATUS_PNEU TEXT );

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(
  F_COD_UNIDADE TEXT [],
  F_STATUS_PNEU TEXT)
  RETURNS TABLE(
    "UNIDADE ALOCADO"       TEXT,
    "PNEU"                  TEXT,
    "STATUS"                TEXT,
    "VALOR DE AQUISIÇÃO"    TEXT,
    "DATA/HORA CADASTRO"    TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "VALOR DA BANDA"        TEXT,
    "MEDIDAS"               TEXT,
    "PLACA"                 TEXT,
    "TIPO"                  TEXT,
    "POSIÇÃO"               TEXT,
    "QUANTIDADE DE SULCOS"  TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "PRESSÃO IDEAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "DOT"                   TEXT,
    "ÚLTIMA AFERIÇÃO"       TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                AS UNIDADE_ALOCADO,
  P.CODIGO_CLIENTE                                                                      AS COD_PNEU,
  P.STATUS                                                                              AS STATUS,
  COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                                   AS VALOR_AQUISICAO,
  COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                   'DD/MM/YYYY HH24:MI'), '-')                                          AS DATA_HORA_CADASTRO,
  MAP.NOME                                                                              AS NOME_MARCA_PNEU,
  MP.NOME                                                                               AS NOME_MODELO_PNEU,
  CASE WHEN MARB.CODIGO IS NULL
    THEN 'Nunca Recapado'
  ELSE MARB.NOME || ' - ' || MODB.NOME
  END                                                                                   AS BANDA_APLICADA,
  COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                                 AS VALOR_BANDA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)              AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                      AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                      AS POSICAO_PNEU,
  COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                                        AS QTD_SULCOS,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                         AS PRESSAO_ATUAL,
  P.PRESSAO_RECOMENDADA :: TEXT                                                         AS PRESSAO_RECOMENDADA,
  PVN.NOME :: TEXT                                                                      AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                                                                  AS DOT,
  COALESCE(
      TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_ULTIMA_AFERICAO),
              'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                                   AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
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
                 ON V.COD_TIPO = VT.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura.
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F
    ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
      AND CASE
          WHEN F_STATUS_PNEU IS NULL
            THEN TRUE
          ELSE P.STATUS = F_STATUS_PNEU
          END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- (PL-1710) Adiciona informação de duração da última viagem na function de busca de colaboradores em descanso para
-- funcionalidade de acompanhamento de viagens.

DROP FUNCTION FUNC_MARCACAO_GET_COLABORADORES_EM_DESCANSO(
F_COD_UNIDADE BIGINT,
F_COD_CARGOS BIGINT [],
F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE );

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_COLABORADORES_EM_DESCANSO(
  F_COD_UNIDADE         BIGINT,
  F_COD_CARGOS          BIGINT [],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    NOME_COLABORADOR               TEXT,
    DATA_HORA_INICIO_ULTIMA_VIAGEM TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_ULTIMA_VIAGEM    TIMESTAMP WITHOUT TIME ZONE,
    DURACAO_ULTIMA_VIAGEM          BIGINT,
    TEMPO_DESCANSO_SEGUNDOS        BIGINT,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_JORNADA BIGINT;
  TZ_UNIDADE       TEXT;
BEGIN
  SELECT U.TIMEZONE
  FROM UNIDADE U
  WHERE U.CODIGO = F_COD_UNIDADE
  INTO TZ_UNIDADE;

  SELECT VIT.CODIGO
  FROM VIEW_INTERVALO_TIPO VIT
  WHERE
    VIT.COD_UNIDADE = F_COD_UNIDADE
    AND VIT.TIPO_JORNADA
  INTO COD_TIPO_JORNADA;

  IF COD_TIPO_JORNADA IS NULL OR COD_TIPO_JORNADA <= 0
  THEN RAISE EXCEPTION 'Unidade não possui nenhum tipo de marcação definido como jornada';
  END IF;

  RETURN QUERY
  -- SELECT DISTINCT ON ( EXPRESSION [, ...] ) KEEPS ONLY THE FIRST ROW OF EACH SET OF ROWS WHERE THE GIVEN EXPRESSIONS EVALUATE TO EQUAL.
  -- DOCS: HTTPS://WWW.POSTGRESQL.ORG/DOCS/9.5/SQL-SELECT.HTML#SQL-DISTINCT
  WITH ULTIMA_MARCACAO_JORNADA_COLABORADORES AS (
      SELECT
        DISTINCT ON (F.CPF_COLABORADOR)
        C.NOME                AS NOME,
        F.DATA_HORA_INICIO    AS DATA_HORA_INICIO,
        F.DATA_HORA_FIM       AS DATA_HORA_FIM,
        F.COD_MARCACAO_INICIO AS COD_MARCACAO_INICIO,
        F.COD_MARCACAO_FIM    AS COD_MARCACAO_FIM,
        F.STATUS_ATIVO_INICIO AS STATUS_ATIVO_INICIO,
        F.STATUS_ATIVO_FIM    AS STATUS_ATIVO_FIM,
        F.FOI_AJUSTADO_INICIO AS FOI_AJUSTADO_INICIO,
        F.FOI_AJUSTADO_FIM    AS FOI_AJUSTADO_FIM
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, COD_TIPO_JORNADA) F
        JOIN COLABORADOR C
          ON C.CPF = F.CPF_COLABORADOR
      WHERE
        C.COD_FUNCAO = ANY (F_COD_CARGOS)
      ORDER BY
        F.CPF_COLABORADOR,
        COALESCE(F.COD_MARCACAO_INICIO, F.COD_MARCACAO_FIM) DESC
  )

  SELECT
    UMJC.NOME :: TEXT                                      AS NOME_COLABORADOR,
    UMJC.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE          AS DATA_HORA_INICIO_ULTIMA_VIAGEM,
    UMJC.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE             AS DATA_HORA_FIM_ULTIMA_VIAGEM,
    TO_SECONDS(UMJC.DATA_HORA_FIM - UMJC.DATA_HORA_INICIO) AS DURACAO_ULTIMA_VIAGEM,
    TO_SECONDS(F_DATA_HORA_ATUAL_UTC - UMJC.DATA_HORA_FIM) AS TEMPO_DESCANSO_SEGUNDOS,
    UMJC.FOI_AJUSTADO_INICIO                               AS FOI_AJUSTADO_INICIO,
    UMJC.FOI_AJUSTADO_FIM                                  AS FOI_AJUSTADO_FIM
  FROM ULTIMA_MARCACAO_JORNADA_COLABORADORES UMJC
  WHERE
    -- Precisamos filtrar isso apenas depois de pegarmos a última marcação de jornada de um colaborador,
    -- pois nesse caso pegaremos até mesmo marcações inativas e em andamento. Se fôssemos filtrar na CTE anterior,
    -- poderíamos acabar removendo antes as em andamento e inativas e trazer como última jornada do colaborador uma que
    -- pode ter sido finalizada a até um mês atrás. Ou mesmo uma que está inativa.

    -- Filtramos apenas por fim not null, pois iremos considerar como em descanso, marcações que tem apenas fim.
    UMJC.COD_MARCACAO_FIM IS NOT NULL
    -- Se apenas início ou fim estiverem ativos, queremos trazer essa linha.
    AND (UMJC.STATUS_ATIVO_INICIO OR UMJC.STATUS_ATIVO_FIM);
END;
$$;
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;