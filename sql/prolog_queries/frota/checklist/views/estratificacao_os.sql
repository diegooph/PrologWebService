CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
SELECT COS.CODIGO                                                       AS COD_OS,
       REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
       C.PLACA_VEICULO,
       C.KM_VEICULO                                                     AS KM,
       C.DATA_HORA_REALIZACAO_TZ_APLICADO                               AS DATA_HORA,
       C.TIPO                                                           AS TIPO_CHECKLIST,
       CP.CODIGO                                                        AS COD_PERGUNTA,
       CP.CODIGO_CONTEXTO                                               AS COD_CONTEXTO_PERGUNTA,
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
           END                                                          AS PRIORIDADE_ORDEM,
       CAP.CODIGO                                                       AS COD_ALTERNATIVA,
       CAP.CODIGO_CONTEXTO                                              AS COD_CONTEXTO_ALTERNATIVA,
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
         JOIN COLABORADOR REALIZADOR
              ON REALIZADOR.CPF = C.CPF_COLABORADOR
         JOIN VEICULO V
              ON V.PLACA :: TEXT = C.PLACA_VEICULO :: TEXT
         JOIN CHECKLIST_ORDEM_SERVICO COS
              ON C.CODIGO = COS.COD_CHECKLIST
         JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
              ON COS.CODIGO = COSI.COD_OS
                  AND COS.COD_UNIDADE = COSI.COD_UNIDADE
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
                  AND COSI.COD_CONTEXTO_PERGUNTA = CP.CODIGO_CONTEXTO
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CAP.COD_PERGUNTA = CP.CODIGO
                  AND COSI.COD_CONTEXTO_ALTERNATIVA = CAP.CODIGO_CONTEXTO
         JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
              ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
         JOIN CHECKLIST_RESPOSTAS_NOK CRN
              ON CRN.COD_CHECKLIST = C.CODIGO
                  AND CRN.COD_ALTERNATIVA = CAP.CODIGO
         LEFT JOIN COLABORADOR MECANICO ON MECANICO.CPF = COSI.CPF_MECANICO;