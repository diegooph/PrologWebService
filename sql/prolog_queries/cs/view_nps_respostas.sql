CREATE OR REPLACE VIEW CS.VIEW_NPS_RESPOSTAS
AS
SELECT E.CODIGO                                                                 AS COD_EMPRESA,
       E.NOME                                                                   AS NOME_EMPRESA,
       U.CODIGO                                                                 AS COD_UNIDADE,
       U.NOME                                                                   AS NOME_UNIDADE,
       NR.COD_NPS_PESQUISA                                                      AS COD_NPS_PESQUISA,
       NR.RESPOSTA_PERGUNTA_ESCALA                                              AS RESPOSTA_PERGUNTA_ESCALA,
       NR.RESPOSTA_PERGUNTA_DESCRITIVA                                          AS RESPOSTA_PERGUNTA_DESCRITIVA,
       NR.DATA_HORA_REALIZACAO_PESQUISA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_RESPOSTA,
       CD.CODIGO                                                                AS COD_COLABORADOR,
       CD.CPF                                                                   AS CPF_COLABORADOR,
       CD.DATA_NASCIMENTO                                                       AS DATA_NASCIMENTO_COLABORADOR,
       CD.NOME                                                                  AS NOME_COLABORADOR
FROM CS.NPS_RESPOSTAS NR
         JOIN COLABORADOR_DATA CD ON NR.COD_COLABORADOR_RESPOSTAS = CD.CODIGO
         JOIN UNIDADE U ON CD.COD_UNIDADE = U.CODIGO
         JOIN EMPRESA E ON CD.COD_EMPRESA = E.CODIGO
ORDER BY E.NOME ASC, NR.RESPOSTA_PERGUNTA_ESCALA DESC;