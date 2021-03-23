-- Deleta a function anterior do relatório pois a quantidade de colunas do retorno foi alterada.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(BIGINT[], CHARACTER VARYING, DATE, DATE);

-- Recria a functon do relatório contendo a nova coluna referente à ação gerada pela resposta.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(F_COD_UNIDADES BIGINT[],
                                                                                 F_PLACA_VEICULO CHARACTER VARYING,
                                                                                 F_DATA_INICIAL DATE,
                                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                         TEXT,
                "CODIGO CHECKLIST"                BIGINT,
                "DATA"                            CHARACTER VARYING,
                "PLACA"                           CHARACTER VARYING,
                "TIPO DE VEÍCULO"                 TEXT,
                "TIPO"                            TEXT,
                "KM"                              BIGINT,
                "NOME"                            CHARACTER VARYING,
                "PERGUNTA"                        CHARACTER VARYING,
                "ALTERNATIVA"                     CHARACTER VARYING,
                "RESPOSTA"                        CHARACTER VARYING,
                "IMAGENS ADICIONADAS ALTERNATIVA" BIGINT,
                "PRIORIDADE"                      CHARACTER VARYING,
                "PRAZO EM HORAS"                  INTEGER,
                "AÇÃO GERADA"                     TEXT
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
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_MIDIAS_ALTERNATIVAS_NOK CRMAN
        WHERE C.CODIGO = CRMAN.COD_CHECKLIST
          AND CRN.COD_ALTERNATIVA = CRMAN.COD_ALTERNATIVA)                        AS TOTAL_MIDIAS_ALTERNATIVA,
       CAP.PRIORIDADE                                                             AS PRIORIDADE,
       PRIO.PRAZO                                                                 AS PRAZO,
       CASE
           WHEN COSIA.NOVA_QTD_APONTAMENTOS IS NULL
               THEN 'Não abriu O.S.'
           WHEN COSIA.NOVA_QTD_APONTAMENTOS = 1
               THEN 'Abriu O.S.'
           WHEN COSIA.NOVA_QTD_APONTAMENTOS > 1
               THEN 'Incrementou Apontamentos'
           END                                                                    AS ACAO_ORDEM_SERVICO
FROM CHECKLIST C
         JOIN VEICULO V
              ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CAP.COD_PERGUNTA = CP.CODIGO
         JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
              ON PRIO.PRIORIDADE::TEXT = CAP.PRIORIDADE::TEXT
         JOIN CHECKLIST_RESPOSTAS_NOK CRN
              ON C.CODIGO = CRN.COD_CHECKLIST
                  AND CRN.COD_ALTERNATIVA = CAP.CODIGO
         JOIN COLABORADOR CO
              ON CO.CPF = C.CPF_COLABORADOR
         JOIN UNIDADE U
              ON C.COD_UNIDADE = U.CODIGO
         LEFT JOIN CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS COSIA
                   ON CAP.DEVE_ABRIR_ORDEM_SERVICO IS TRUE
                       AND COSIA.COD_CHECKLIST_REALIZADO = C.CODIGO
                       AND COSIA.COD_ALTERNATIVA = CRN.COD_ALTERNATIVA
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO::DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO::DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA_SINCRONIZACAO DESC, C.CODIGO
$$;