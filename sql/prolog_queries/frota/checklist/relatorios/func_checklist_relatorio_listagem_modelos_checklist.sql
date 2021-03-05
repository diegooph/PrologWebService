CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
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
AS
$$
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
              ON CP.CODIGO = CAP.COD_PERGUNTA
         JOIN CHECKLIST_MODELO CM
              ON CAP.COD_VERSAO_CHECKLIST_MODELO = CM.COD_VERSAO_ATUAL
         JOIN UNIDADE U
              ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY U.NOME, CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
$$;