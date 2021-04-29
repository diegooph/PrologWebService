CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_MEDIA_TEMPO_CONSERTO_ITEM(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE                                      TEXT,
    PERGUNTA                                     TEXT,
    ALTERNATIVA                                  TEXT,
    PRIORIDADE                                   TEXT,
    "PRAZO CONSERTO EM HORAS"                    INTEGER,
    "QTD APONTADOS"                              BIGINT,
    "TOTAL ITENS RESOLVIDOS"                     BIGINT,
    "QTD RESOLVIDOS DENTRO PRAZO"                BIGINT,
    "MÉDIA TEMPO CONSERTO EM HORAS / EM MINUTOS" TEXT,
    "PORCENTAGEM"                                TEXT)
LANGUAGE SQL
AS $$
SELECT
  NOME_UNIDADE                                                               AS NOME_UNIDADE,
  PERGUNTA                                                                   AS PERGUNTA,
  ALTERNATIVA                                                                AS ALTERNATIVA,
  PRIORIDADE                                                                 AS PRIORIDADE,
  PRAZO_CONSERTO_EM_HORAS                                                    AS PRAZO_CONSERTO_HORAS,
  QTD_APONTADOS                                                              AS QTD_APONTADOS,
  TOTAL_ITENS_RESOLVIDOS                                                     AS TOTAL_ITENS_RESOLVIDOS,
  QTD_RESOLVIDOS_DENTRO_PRAZO                                                AS QTD_RESOLVIDOS_DENTRO_PRAZO,
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 3600) || ' / ' ||
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 60)                                  AS MD_TEMPO_CONSERTO_HORAS_MINUTOS,
  ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS :: FLOAT) * 100) || '%' AS PORCENTAGEM
FROM
  (SELECT
     U.NOME                                       AS NOME_UNIDADE,
     EO.PERGUNTA,
     EO.ALTERNATIVA,
     EO.PRIORIDADE,
     EO.PRAZO                                     AS PRAZO_CONSERTO_EM_HORAS,
     COUNT(EO.PERGUNTA)                           AS QTD_APONTADOS,
     SUM(CASE WHEN EO.CPF_MECANICO IS NOT NULL
       THEN 1
         ELSE 0 END)                              AS TOTAL_ITENS_RESOLVIDOS,
     SUM(CASE WHEN (TO_SECONDS(EO.DATA_HORA_CONSERTO - EO.DATA_HORA)) <= EO.PRAZO
       THEN 1
         ELSE 0 END)                              AS QTD_RESOLVIDOS_DENTRO_PRAZO,
     TRUNC(EXTRACT(EPOCH FROM AVG(EO.DATA_HORA_CONSERTO -
                                  EO.DATA_HORA))) AS MEDIA_TEMPO_CONSERTO_SEGUNDOS
   FROM ESTRATIFICACAO_OS EO
     JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
   WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND EO.DATA_HORA :: DATE >= F_DATA_INICIAL
         AND EO.DATA_HORA :: DATE <= F_DATA_FINAL
   GROUP BY U.CODIGO, EO.PERGUNTA, EO.ALTERNATIVA, EO.PRIORIDADE, EO.PRAZO) AS DADOS
ORDER BY DADOS.NOME_UNIDADE, ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS :: FLOAT) * 100) DESC
$$;