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
                "CÓDICO ITEM OS"               BIGINT,
                "ABERTURA OS"                  TEXT,
                "DATA LIMITE CONSERTO"         TEXT,
                "STATUS OS"                    TEXT,
                "PLACA"                        TEXT,
                "TIPO DE VEÍCULO"              TEXT,
                "TIPO DO CHECKLIST"            TEXT,
                "MODELO DO CHECKLIST"          TEXT,
                "PERGUNTA"                     TEXT,
                "ALTERNATIVA"                  TEXT,
                "QTD APONTAMENTOS"             INTEGER,
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
                "MOTORISTA"                    TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                             AS NOME_UNIDADE,
       EO.COD_OS                                                                          AS CODIGO_OS,
       EO.CODIGO                                                                          AS COD_ITEM_OS,
       FORMAT_TIMESTAMP(EO.DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
       FORMAT_TIMESTAMP(EO.DATA_HORA + (EO.PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
       (CASE
            WHEN STATUS_OS = 'A'
                THEN 'ABERTA'
            ELSE 'FECHADA' END)                                                           AS STATUS_OS,
       EO.PLACA_VEICULO                                                                      AS PLACA,
       VT.NOME                                                                            AS TIPO_VEICULO,
       CASE
           WHEN TIPO_CHECKLIST = 'S'
               THEN 'SAÍDA'
           ELSE 'RETORNO' END                                                             AS TIPO_CHECKLIST,
       CM.NOME                                                                            AS CHECKLIST_MODELO,
       PERGUNTA                                                                           AS PERGUNTA,
       ALTERNATIVA                                                                        AS ALTERNATIVA,
       QT_APONTAMENTOS                                                                    AS QTD_APONTAMENTOS,
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
       EO.TEMPO_REALIZACAO / 1000 / 60                                                    AS TEMPO_CONSERTO_MINUTOS,
       KM                                                                                 AS KM_ABERTURA,
       KM_FECHAMENTO                                                                      AS KM_FECHAMENTO,
       COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                                        AS KM_PERCORRIDO,
       NOME_REALIZADOR_CHECKLIST                                                          AS MOTORISTA
FROM ESTRATIFICACAO_OS EO
         JOIN UNIDADE U
              ON EO.COD_UNIDADE = U.CODIGO
         JOIN VEICULO_TIPO VT
             ON VT.CODIGO = EO.COD_TIPO
         JOIN CHECKLIST C
            ON C.CODIGO = EO.COD_CHECKLIST
         JOIN CHECKLIST_MODELO CM
            ON CM.CODIGO = C.COD_CHECKLIST_MODELO

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