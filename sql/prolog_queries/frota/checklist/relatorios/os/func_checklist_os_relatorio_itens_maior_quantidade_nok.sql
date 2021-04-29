CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(F_COD_UNIDADES BIGINT[],
                                                                                  F_DATA_INICIAL DATE,
                                                                                  F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                UNIDADE               TEXT,
                "MODELO CHECKLIST"    TEXT,
                PERGUNTA              TEXT,
                ALTERNATIVA           TEXT,
                PRIORIDADE            TEXT,
                "TOTAL MARCAÇÕES NOK" BIGINT,
                "TOTAL REALIZAÇÕES"   BIGINT,
                "PROPORÇÃO"           TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT Q.NOME_UNIDADE          AS NOME_UNIDADE,
       Q.NOME_MODELO_CHECKLIST AS NOME_MODELO_CHECKLIST,
       Q.PERGUNTA              AS PERGUNTA,
       Q.ALTERNATIVA           AS ALTERNATIVA,
       Q.PRIORIDADE            AS PRIORIDADE,
       Q.TOTAL_MARCACOES_NOK   AS TOTAL_MARCACOES_NOK,
       Q.TOTAL_REALIZACOES     AS TOTAL_REALIZACOES,
       Q.PROPORCAO_NUM || '%'  AS PROPORCAO
FROM (SELECT U.NOME                                                         AS NOME_UNIDADE,
             CM.NOME                                                        AS NOME_MODELO_CHECKLIST,
             CP.PERGUNTA                                                    AS PERGUNTA,
             CAP.ALTERNATIVA                                                AS ALTERNATIVA,
             CAP.PRIORIDADE                                                 AS PRIORIDADE,
             COUNT(CRN.CODIGO)                                              AS TOTAL_MARCACOES_NOK,
             COUNT(DISTINCT C.CODIGO)                                       AS TOTAL_REALIZACOES,
             TRUNC(((COUNT(CRN.CODIGO)
                 / COUNT(DISTINCT C.CODIGO) :: FLOAT) * 100) :: NUMERIC, 2) AS PROPORCAO_NUM
      FROM CHECKLIST C
               -- O JOIN é feito pelo código do modelo e não da versão pois queremos saber as proporções de
               -- respostas NOK em todas as versões de cada modelo.
               JOIN CHECKLIST_PERGUNTAS CP
                    ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
               JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                    ON CP.CODIGO = CAP.COD_PERGUNTA
               LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                         ON C.CODIGO = CRN.COD_CHECKLIST AND CAP.CODIGO = CRN.COD_ALTERNATIVA
               JOIN CHECKLIST_MODELO CM
                    ON CM.CODIGO = C.COD_CHECKLIST_MODELO
               JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
        AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
      GROUP BY U.NOME, CAP.PRIORIDADE, CP.PERGUNTA, CAP.ALTERNATIVA, CM.NOME, C.COD_CHECKLIST_MODELO) Q
ORDER BY Q.NOME_UNIDADE, Q.PROPORCAO_NUM DESC, Q.TOTAL_MARCACOES_NOK DESC, Q.PERGUNTA;
$$;