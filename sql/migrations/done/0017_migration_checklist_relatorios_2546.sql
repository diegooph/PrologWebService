-- #####################################################################################################################
-- #####################################################################################################################
-- #################### Adiciona o tipo do veículo nos relatórios de checklist que contém placa ########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2546

-- Dropa function antiga.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(BIGINT[],TEXT,DATE,DATE);

-- Recria a function com a coluna de tipo de veículo do relatório de "Resumo de Checlist".
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
                                                                      F_PLACA_VEICULO TEXT,
                                                                      F_DATA_INICIAL DATE,
                                                                      F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                     TEXT,
                "MODELO CHECKLIST"            TEXT,
                "CÓDIGO CHECKLIST"            BIGINT,
                "DATA REALIZAÇÃO"             TEXT,
                "DATA IMPORTADO"              TEXT,
                "COLABORADOR"                 TEXT,
                "CPF"                         TEXT,
                "PLACA"                       TEXT,
                "TIPO DE VEÍCULO"             TEXT,
                "KM"                          BIGINT,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
                "TIPO"                        TEXT,
                "TOTAL DE PERGUNTAS"          SMALLINT,
                "TOTAL NOK"                   BIGINT,
                "PRIORIDADE BAIXA"            BIGINT,
                "PRIORIDADE ALTA"             BIGINT,
                "PRIORIDADE CRÍTICA"          BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                 AS NOME_UNIDADE,
       CM.NOME                                                AS NOME_MODELO,
       C.CODIGO                                               AS COD_CHECKLIST,
       FORMAT_TIMESTAMP(
               C.DATA_HORA_REALIZACAO_TZ_APLICADO,
               'DD/MM/YYYY HH24:MI')                          AS DATA_HORA_REALIZACAO,
       FORMAT_WITH_TZ(
               C.DATA_HORA_IMPORTADO_PROLOG,
               TZ_UNIDADE(C.COD_UNIDADE),
               'DD/MM/YYYY HH24:MI',
               '-')                                           AS DATA_HORA_IMPORTADO,
       CO.NOME                                                AS NOME_COLABORADOR,
       LPAD(CO.CPF :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
       C.PLACA_VEICULO                                        AS PLACA_VEICULO,
       VT.NOME                                                AS TIPO_VEICULO,
       C.KM_VEICULO                                           AS KM_VEICULO,
       C.TEMPO_REALIZACAO / 1000                              AS TEMPO_REALIZACAO_SEGUNDOS,
       F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT) AS TIPO_CHECKLIST,
       C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK           AS TOTAL_PERGUNTAS,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
        WHERE CRN.COD_CHECKLIST = C.CODIGO)                   AS TOTAL_NOK,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'BAIXA')                       AS TOTAL_BAIXA,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'ALTA')                        AS TOTAL_ALTA,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'CRITICA')                     AS TOTAL_CRITICA
FROM CHECKLIST C
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
         JOIN COLABORADOR CO
              ON C.CPF_COLABORADOR = CO.CPF
         JOIN UNIDADE U
              ON C.COD_UNIDADE = U.CODIGO
         JOIN CHECKLIST_MODELO CM ON CM.CODIGO = C.COD_CHECKLIST_MODELO
         JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
  AND (F_PLACA_VEICULO = '%' OR C.PLACA_VEICULO LIKE F_PLACA_VEICULO)
GROUP BY C.CODIGO,
         CM.NOME,
         C.TOTAL_PERGUNTAS_OK,
         C.TOTAL_PERGUNTAS_NOK,
         U.CODIGO,
         U.NOME,
         CO.CPF,
         CO.NOME,
         CO.CPF,
         C.DATA_HORA,
         C.DATA_HORA_REALIZACAO_TZ_APLICADO,
         C.DATA_HORA_IMPORTADO_PROLOG,
         C.DATA_HORA_SINCRONIZACAO,
         C.COD_UNIDADE,
         C.PLACA_VEICULO,
         VT.NOME,
         C.KM_VEICULO,
         C.TEMPO_REALIZACAO,
         C.TIPO
ORDER BY U.NOME,
         C.DATA_HORA_SINCRONIZACAO DESC;
$$;

-- Dropa function antiga.
DROP FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(BIGINT[],TEXT,TEXT,TEXT,DATE,DATE,DATE,DATE);

-- Recria a function com a coluna de tipo de veículo no relatório de "Estratificação de O.S.".
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
                "TIPO DE VEÍCULO"              TEXT,
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
SELECT U.NOME                                                                             AS NOME_UNIDADE,
       EO.COD_OS                                                                          AS CODIGO_OS,
       FORMAT_TIMESTAMP(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
       FORMAT_TIMESTAMP(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
       (CASE
            WHEN STATUS_OS = 'A'
                THEN 'ABERTA'
            ELSE 'FECHADA' END)                                                           AS STATUS_OS,
       PLACA_VEICULO                                                                      AS PLACA,
       VT.NOME                                                                            AS TIPO_VEICULO,
       PERGUNTA                                                                           AS PERGUNTA,
       ALTERNATIVA                                                                        AS ALTERNATIVA,
       PRIORIDADE                                                                         AS PRIORIDADE,
       PRAZO                                                                              AS PRAZO_EM_HORAS,
       RESPOSTA_OUTROS                                                                    AS DESCRICAO,
       CASE
           WHEN STATUS_ITEM = 'P'
               THEN 'PENDENTE'
           ELSE 'RESOLVIDO' END                                                           AS STATUS_ITEM,
       FORMAT_TIMESTAMP(
               DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI',
               '-')                                                                       AS DATA_INICIO_RESOLUCAO,
       FORMAT_TIMESTAMP(
               DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI', '-')                                                 AS DATA_FIM_RESOLUCAO,
       FORMAT_TIMESTAMP(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_RESOLVIDO_PROLOG,
       NOME_MECANICO                                                                      AS MECANICO,
       FEEDBACK_CONSERTO                                                                  AS DESCRICAO_CONSERTO,
       TEMPO_REALIZACAO / 1000 / 60                                                       AS TEMPO_CONSERTO_MINUTOS,
       KM                                                                                 AS KM_ABERTURA,
       KM_FECHAMENTO                                                                      AS KM_FECHAMENTO,
       COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                                        AS KM_PERCORRIDO,
       NOME_REALIZADOR_CHECKLIST                                                          AS MOTORISTA,
       CASE
           WHEN TIPO_CHECKLIST = 'S'
               THEN 'SAÍDA'
           ELSE 'RETORNO' END                                                             AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
         JOIN UNIDADE U
              ON EO.COD_UNIDADE = U.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = EO.COD_TIPO
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

-- Dropa function antiga.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(BIGINT[],CHARACTER VARYING,DATE,DATE);

-- Recria a function com a coluna de tipo de veículo no relatório de "Estratificação de Respostas NOK".
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(F_COD_UNIDADES BIGINT[],
                                                                                 F_PLACA_VEICULO CHARACTER VARYING,
                                                                                 F_DATA_INICIAL DATE,
                                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"          TEXT,
                "CODIGO CHECKLIST" BIGINT,
                "DATA"             CHARACTER VARYING,
                "PLACA"            CHARACTER VARYING,
                "TIPO DE VEÍCULO"  TEXT,
                "TIPO"             TEXT,
                "KM"               BIGINT,
                "NOME"             CHARACTER VARYING,
                "PERGUNTA"         CHARACTER VARYING,
                "ALTERNATIVA"      CHARACTER VARYING,
                "RESPOSTA"         CHARACTER VARYING,
                "PRIORIDADE"       CHARACTER VARYING,
                "PRAZO EM HORAS"   INTEGER
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                     AS NOME_UNIDADE,
       C.CODIGO                                                                   AS COD_CHECKLIST,
       FORMAT_TIMESTAMP(C.DATA_HORA_REALIZACAO_TZ_APLICADO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_CHECK,
       C.PLACA_VEICULO                                                            AS PLACA_VEICULO,
       VT.NOME                                                                    AS TIPO_VEICULO,
       CASE
           WHEN C.TIPO = 'S'
               THEN 'Saída'
           ELSE 'Retorno' END                                                     AS TIPO_CHECKLIST,
       C.KM_VEICULO                                                               AS KM_VEICULO,
       CO.NOME                                                                    AS NOME_REALIZADOR_CHECK,
       CP.PERGUNTA                                                                AS DESCRICAO_PERGUNTA,
       CAP.ALTERNATIVA                                                            AS DESCRICAO_ALTERNATIVA,
       CRN.RESPOSTA_OUTROS                                                        AS RESPOSTA,
       CAP.PRIORIDADE                                                             AS PRIORIDADE,
       PRIO.PRAZO                                                                 AS PRAZO
FROM CHECKLIST C
         JOIN VEICULO V
              ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
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
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA_SINCRONIZACAO DESC, C.CODIGO ASC
$$;

-- Dropa function antiga.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(BIGINT[],DATE,DATE);

-- Recria a function com a coluna de tipo de veículo no relatório de "Extrato Checklists Realizados no Dia".
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(F_COD_UNIDADES BIGINT[],
                                                                                 F_DATA_INICIAL DATE,
                                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"         TEXT,
                "DATA"            TEXT,
                "PLACA"           TEXT,
                "TIPO DE VEÍCULO" TEXT,
                "CHECKS SAÍDA"    BIGINT,
                "CHECKS RETORNO"  BIGINT
            )
    LANGUAGE SQL
AS
$$
WITH MAPAS AS (
    SELECT M.DATA AS DATA_MAPA,
           M.MAPA,
           M.PLACA
    FROM MAPA M
             JOIN VEICULO V ON V.PLACA = M.PLACA
    WHERE M.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND M.DATA >= F_DATA_INICIAL
      AND M.DATA <= F_DATA_FINAL
    ORDER BY M.DATA ASC),
     CHECKS AS (SELECT C.COD_UNIDADE                                              AS COD_UNIDADE,
                       (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE AS DATA,
                       C.PLACA_VEICULO                                            AS PLACA_VEICULO,
                       VT.NOME                                                    AS TIPO_VEICULO,
                       SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)              AS CHECKS_SAIDA,
                       SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)              AS CHECKS_RETORNO
                FROM CHECKLIST C
                         JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                         JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO
                         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
                         LEFT JOIN MAPAS AS M
                                   ON M.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE
                                       AND M.PLACA = C.PLACA_VEICULO
                WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
                GROUP BY C.COD_UNIDADE, DATA, C.PLACA_VEICULO, VT.NOME
                ORDER BY C.COD_UNIDADE, DATA, C.PLACA_VEICULO, VT.NOME)

SELECT (SELECT NOME
        FROM UNIDADE U
        WHERE U.CODIGO = C.COD_UNIDADE) AS NOME_UNIDADE,
       TO_CHAR(C.DATA, 'DD/MM/YYYY')    AS DATA,
       C.PLACA_VEICULO,
       C.TIPO_VEICULO,
       C.CHECKS_SAIDA,
       C.CHECKS_RETORNO
FROM CHECKS C
ORDER BY NOME_UNIDADE, DATA
$$;