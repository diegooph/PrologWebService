begin transaction ;
--######################################################################################################################
--######################################################################################################################
--#################### MIGRATION DE IMPLEMENTAÇÃO DA NOVA ESTRUTURA DE CHECKLISTS NOS RELATÓRIOS #######################
--######################################################################################################################
--######################################################################################################################
-- PL-2230

-- FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS - Aplica as alterações no relatório de resumo de checklist
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "CÓDIGO CHECKLIST"            BIGINT,
    "DATA REALIZAÇÃO"             TEXT,
    "DATA IMPORTADO"              TEXT,
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
      C.CODIGO                                                                          AS COD_CHECKLIST,
      U.CODIGO                                                                          AS COD_UNIDADE,
      U.NOME                                                                            AS NOME_UNIDADE,
      C.DATA_HORA                                                                       AS DATA_HORA_REALIZACAO,
      C.DATA_HORA_SINCRONIZACAO                                                         AS DATA_HORA_SINCRONIZACAO,
      TO_CHAR(C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE), 'DD/MM/YYYY HH24:MI') AS DATA_REALIZACAO_CHECK,
      TO_CHAR(C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE),
              'DD/MM/YYYY HH24:MI')                                                     AS DATA_IMPORTADO,
      CO.NOME                                                                           AS NOME_COLABORADOR,
      C.PLACA_VEICULO                                                                   AS PLACA_VEICULO,
      C.KM_VEICULO                                                                      AS KM_VEICULO,
      C.TEMPO_REALIZACAO / 1000                                                         AS TEMPO_REALIZACAO_SEGUNDOS,
      F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                            AS TIPO_CHECKLIST,
      COUNT(C.CODIGO)                                                                   AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
      JOIN CHECKLIST_PERGUNTAS CP
        ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
      JOIN COLABORADOR CO
        ON C.CPF_COLABORADOR = CO.CPF
      JOIN UNIDADE U
        ON C.COD_UNIDADE = U.CODIGO
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CP.STATUS_ATIVO
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY
      C.CODIGO,
      U.CODIGO,
      CO.CPF,
      CO.NOME,
      C.DATA_HORA,
      C.DATA_HORA_IMPORTADO_PROLOG,
      C.DATA_HORA_SINCRONIZACAO,
      C.COD_UNIDADE,
      C.PLACA_VEICULO,
      C.KM_VEICULO,
      C.TEMPO_REALIZACAO,
      C.TIPO),

    RESPOSTAS_NOK AS (
      SELECT
        CR.COD_CHECKLIST AS COD_CHECKLIST,
        COUNT(CR.CODIGO)    AS TOTAL_NOK,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'BAIXA'
          THEN 1 END)    AS TOTAL_BAIXAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'ALTA'
          THEN 1 END)    AS TOTAL_ALTAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'CRITICA'
          THEN 1 END)    AS TOTAL_CRITICAS
      FROM CHECKLIST_RESPOSTAS_NOK CR
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CR.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON CR.COD_CHECKLIST = C.CODIGO
      WHERE
        C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND CAP.STATUS_ATIVO
        AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY CR.COD_CHECKLIST
  )

SELECT
  C.NOME_UNIDADE,
  C.COD_CHECKLIST,
  C.DATA_REALIZACAO_CHECK,
  COALESCE(C.DATA_IMPORTADO, '-'),
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

-- FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK - Aplica as alterações no relatório de resumo de checklist
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE)
  RETURNS TABLE(
    UNIDADE               TEXT,
    "MODELO CHECKLIST"    TEXT,
    PERGUNTA              TEXT,
    ALTERNATIVA           TEXT,
    PRIORIDADE            TEXT,
    "TOTAL MARCAÇÕES NOK" BIGINT,
    "TOTAL REALIZAÇÕES"   BIGINT,
    "PROPORÇÃO"           TEXT)
LANGUAGE SQL
AS $$
SELECT U.NOME AS NOME_UNIDADE,
       CM.NOME AS NOME_MODELO_CHECKLIST,
       CP.PERGUNTA                                               AS PERGUNTA,
       CAP.ALTERNATIVA AS ALTERNATIVA,
       CAP.PRIORIDADE                                                      AS PRIORIDADE,
       COUNT(CRN.CODIGO)                                         AS TOTAL_MARCACOES_NOK,
       COUNT(DISTINCT C.CODIGO)                                  AS TOTAL_REALIZACOES,
       TRUNC(((COUNT(CRN.CODIGO)
          / COUNT(DISTINCT C.CODIGO) :: FLOAT) * 100) :: NUMERIC, 1) || '%' AS PROPORCAO
FROM CHECKLIST_DATA C
         JOIN CHECKLIST_PERGUNTAS_DATA CP
              ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
         FULL OUTER JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
              ON CP.CODIGO = CAP.COD_PERGUNTA
         FULL OUTER JOIN CHECKLIST_RESPOSTAS_NOK CRN
              ON C.CODIGO = CRN.COD_CHECKLIST AND CAP.CODIGO = CRN.COD_ALTERNATIVA
         JOIN CHECKLIST_MODELO_DATA CM
              ON CM.CODIGO = C.COD_CHECKLIST_MODELO
        JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.NOME, CAP.PRIORIDADE, CP.PERGUNTA, CAP.ALTERNATIVA, CM.NOME, C.COD_CHECKLIST_MODELO
ORDER BY U.NOME, PROPORCAO DESC;
$$;

-- FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST - Aplica alterações no relatórios de listagem de modelos de check
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    UNIDADE                 TEXT,
    "CÓDIGO DO CHECKLIST"   TEXT,
    "NOME DO CHECKLIST"     TEXT,
    ATIVO                   TEXT,
    "CÓDIGO DA PERGUNTA"    TEXT,
    PERGUNTA                TEXT,
    "CÓDIGO DA ALTERNATIVA" TEXT,
    ALTERNATIVA             TEXT,
    "TIPO DE RESPOSTA"      TEXT,
    PRIORIDADE              TEXT
  )
LANGUAGE SQL
AS $$
SELECT U.NOME                                              AS NOME_UNIDADE,
       CM.CODIGO::TEXT                                     AS COD_MODELO_CHECKLIST,
       CM.NOME                                             AS NOME_MODELO,
       F_IF(CM.STATUS_ATIVO, 'SIM' :: TEXT, 'NÃO')         AS ATIVO,
       CP.CODIGO::TEXT                                     AS COD_PERGUNTA,
       CP.PERGUNTA                                         AS PERGUNTA,
       CAP.CODIGO::TEXT                                    AS COD_ALTERNATIVA,
       CAP.ALTERNATIVA                                     AS ALTERNATIVA,
       F_IF(CP.SINGLE_CHOICE, 'ÚNICA' :: TEXT, 'MÚLTIPLA') AS TIPO_DE_RESPOSTA,
       CAP.PRIORIDADE                                      AS PRIORIDADE
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.COD_UNIDADE = CAP.COD_UNIDADE
                  AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
                  AND CP.CODIGO = CAP.COD_PERGUNTA
         JOIN CHECKLIST_MODELO CM
              ON CAP.COD_VERSAO_CHECKLIST_MODELO = CM.COD_VERSAO_ATUAL
         JOIN UNIDADE U
              ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY U.NOME, CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
$$;

-- FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK
-- Aplica alterações no relatório de estratificação de respostas não ok
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
  CRN.RESPOSTA_OUTROS                                                                 AS RESPOSTA,
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
  JOIN CHECKLIST_RESPOSTAS_NOK CRN
    ON C.CODIGO = CRN.COD_CHECKLIST
       AND CRN.COD_ALTERNATIVA = CAP.CODIGO
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

-- Dropa e recria a view ESTRATIFICACAO_OS
DROP VIEW ESTRATIFICACAO_OS;

CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
SELECT
    COS.CODIGO                                                       AS COD_OS,
    REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
    C.PLACA_VEICULO,
    C.KM_VEICULO                                                     AS KM,
    TIMEZONE(TZ_UNIDADE(COS.COD_UNIDADE), C.DATA_HORA)               AS DATA_HORA,
    C.TIPO                                                           AS TIPO_CHECKLIST,
    CP.CODIGO                                                        AS COD_PERGUNTA,
    CP.CODIGO_FIXO_PERGUNTA                                          AS COD_FIXO_PERGUNTA,
    CP.ORDEM                                                         AS ORDEM_PERGUNTA,
    CP.PERGUNTA,
    CP.SINGLE_CHOICE,
    NULL :: UNKNOWN                                                  AS URL_IMAGEM,
    CAP.PRIORIDADE,
    CASE CAP.PRIORIDADE
    WHEN 'CRITICA' :: TEXT
      THEN 1
    WHEN 'ALTA' :: TEXT
      THEN 2
    WHEN 'BAIXA' :: TEXT
      THEN 3
    ELSE NULL :: INTEGER
    END                                                              AS PRIORIDADE_ORDEM,
    CAP.CODIGO                                                       AS COD_ALTERNATIVA,
    CAP.CODIGO_FIXO_ALTERNATIVA                                      AS COD_FIXO_ALTERNATIVA,
    CAP.ALTERNATIVA,
    PRIO.PRAZO,
    CRN.RESPOSTA_OUTROS,
    V.COD_TIPO,
    COS.COD_UNIDADE,
    COS.STATUS                                                       AS STATUS_OS,
    COS.COD_CHECKLIST,
    TZ_UNIDADE(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
    COSI.STATUS_RESOLUCAO                                            AS STATUS_ITEM,
    MECANICO.NOME                                                    AS NOME_MECANICO,
    COSI.CPF_MECANICO,
    COSI.TEMPO_REALIZACAO,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
    COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
    COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
    COSI.KM                                                          AS KM_FECHAMENTO,
    COSI.QT_APONTAMENTOS,
    COSI.FEEDBACK_CONSERTO,
    COSI.CODIGO
FROM CHECKLIST_DATA C
JOIN COLABORADOR REALIZADOR ON REALIZADOR.CPF = C.CPF_COLABORADOR
JOIN VEICULO V ON V.PLACA :: TEXT = C.PLACA_VEICULO :: TEXT
JOIN CHECKLIST_PERGUNTAS_DATA CP ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO AND C.COD_UNIDADE = CP.COD_UNIDADE
JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP ON CAP.COD_PERGUNTA = CP.CODIGO AND CAP.COD_UNIDADE = C.COD_UNIDADE
JOIN CHECKLIST_ORDEM_SERVICO_DATA COS ON C.CODIGO = COS.COD_CHECKLIST AND C.COD_UNIDADE = COS.COD_UNIDADE
JOIN CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI ON COS.CODIGO = COSI.COD_OS AND COS.COD_UNIDADE = COSI.COD_UNIDADE
     AND COSI.COD_FIXO_PERGUNTA = CP.CODIGO_FIXO_PERGUNTA AND COSI.COD_FIXO_ALTERNATIVA = CAP.CODIGO_FIXO_ALTERNATIVA
JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
JOIN CHECKLIST_RESPOSTAS_NOK CRN ON CRN.COD_CHECKLIST = C.CODIGO AND CRN.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
AND CRN.COD_UNIDADE = C.COD_UNIDADE AND CRN.COD_PERGUNTA = CP.CODIGO AND CRN.COD_ALTERNATIVA = CAP.CODIGO
LEFT JOIN COLABORADOR MECANICO ON MECANICO.CPF = COSI.CPF_MECANICO;

-- Dropa e recria a FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS
DROP FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(BIGINT[], TEXT, TEXT, TEXT, DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(F_COD_UNIDADES BIGINT[],
                                                                         F_PLACA_VEICULO TEXT,
                                                                         F_STATUS_OS TEXT,
                                                                         F_STATUS_ITEM TEXT,
                                                                         F_DATA_INICIAL_ABERTURA DATE,
                                                                         F_DATA_FINAL_ABERTURA DATE,
                                                                         F_DATA_INICIAL_RESOLUCAO DATE,
                                                                         F_DATA_FINAL_RESOLUCAO DATE)
    RETURNS TABLE
            (
                UNIDADE                        TEXT,
                "CÓDICO OS"                    BIGINT,
                "ABERTURA OS"                  TEXT,
                "DATA LIMITE CONSERTO"         TEXT,
                "STATUS OS"                    TEXT,
                "PLACA"                        TEXT,
                "PERGUNTA"                     TEXT,
                "ALTERNATIVA"                  TEXT,
                "PRIORIDADE"                   TEXT,
                "PRAZO EM HORAS"               INTEGER,
                "DESCRIÇÃO"                    TEXT,
                "STATUS ITEM"                  TEXT,
                "DATA INÍCIO RESOLUÇÃO"        TEXT,
                "DATA FIM RESOLUÇÃO"           TEXT,
                "DATA RESOLIVDO PROLOG"        TEXT,
                "MECÂNICO"                     TEXT,
                "DESCRIÇÃO CONSERTO"           TEXT,
                "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
                "KM ABERTURA"                  BIGINT,
                "KM FECHAMENTO"                BIGINT,
                "KM PERCORRIDO"                TEXT,
                "MOTORISTA"                    TEXT,
                "TIPO DO CHECKLIST"            TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                    AS NOME_UNIDADE,
       EO.COD_OS                                                                 AS CODIGO_OS,
       TO_CHAR(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
       TO_CHAR(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
       (CASE
            WHEN STATUS_OS = 'A'
                THEN 'ABERTA'
            ELSE 'FECHADA' END)                                                  AS STATUS_OS,
       PLACA_VEICULO                                                             AS PLACA,
       PERGUNTA                                                                  AS PERGUNTA,
       ALTERNATIVA                                                               AS ALTERNATIVA,
       PRIORIDADE                                                                AS PRIORIDADE,
       PRAZO                                                                     AS PRAZO_EM_HORAS,
       RESPOSTA_OUTROS                                                           AS DESCRICAO,
       CASE
           WHEN STATUS_ITEM = 'P'
               THEN 'PENDENTE'
           ELSE 'RESOLVIDO' END                                                  AS STATUS_ITEM,
       COALESCE(TO_CHAR(
                        DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                             AS DATA_INICIO_RESOLUCAO,
       COALESCE(TO_CHAR(
                        DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                             AS DATA_FIM_RESOLUCAO,
       TO_CHAR(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_RESOLVIDO_PROLOG,
       NOME_MECANICO                                                             AS MECANICO,
       FEEDBACK_CONSERTO                                                         AS DESCRICAO_CONSERTO,
       TEMPO_REALIZACAO / 1000 / 60                                              AS TEMPO_CONSERTO_MINUTOS,
       KM                                                                        AS KM_ABERTURA,
       KM_FECHAMENTO                                                             AS KM_FECHAMENTO,
       COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                               AS KM_PERCORRIDO,
       NOME_REALIZADOR_CHECKLIST                                                 AS MOTORISTA,
       CASE
           WHEN TIPO_CHECKLIST = 'S'
               THEN 'SAÍDA'
           ELSE 'RETORNO' END                                                    AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
         JOIN UNIDADE U
              ON EO.COD_UNIDADE = U.CODIGO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
  AND EO.STATUS_OS LIKE F_STATUS_OS
  AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
  AND CASE
          -- O usuário pode filtrar tanto por início e fim de abertura ou por início e fim de resolução ou, ainda,
          -- por ambos.
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA,
                F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN (
                  EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
                  AND
                  EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO)
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA) IS NOT NULL
              THEN
              EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
          WHEN (F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN
              EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO

          -- Se não entrar em nenhuma condição conhecida, retornamos FALSE para o relatório não retornar dado nenhum.
          ELSE FALSE END
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
end transaction ;