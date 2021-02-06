-- Sobre:
--
-- Function que lista todas as alternativas presentes em modelos de checklist.
--
-- Essa function recebe parâmetros de filtragem que seão utilizados para buscar apenas alternativas ativas, de perguntas
-- ativas e de modelos ativos, dependendo da necessidade da aplicação que está utilizando a listagem.
--
-- Histórico:
-- 2019-08-07 -> Function criada (diogenesvanzella - PL-2213).
-- 2020-08-05 -> Adapta function para token duplicado (diogenesvanzella - PLI-175).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_CHECKLIST_ALTERNATIVAS_MODELO_CHECKLIST(F_TOKEN_INTEGRACAO TEXT,
                                                            F_APENAS_MODELOS_CHECKLIST_ATIVOS BOOLEAN,
                                                            F_APENAS_PERGUNTAS_ATIVAS BOOLEAN,
                                                            F_APENAS_ALTERNATIVAS_ATIVAS BOOLEAN)
    RETURNS TABLE
            (
                COD_UNIDADE              BIGINT,
                NOME_UNIDADE             TEXT,
                COD_MODELO_CHECKLIST     BIGINT,
                NOME_MODELO              TEXT,
                STATUS_MODELO_CHECKLIST  BOOLEAN,
                CODIGO_PERGUNTA          BIGINT,
                DESCRICAO_PERGUNTA       TEXT,
                TIPO_DE_RESPOSTA         BOOLEAN,
                CODIGO_ALTERNATIVA       BIGINT,
                DESCRICAO_ALTERNATIVA    TEXT,
                ALTERNATIVA_TIPO_OUTROS  BOOLEAN,
                PRIORIDADE               TEXT,
                DEVE_ABRIR_ORDEM_SERVICO BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT U.CODIGO                     AS COD_UNIDADE,
       U.NOME                       AS NOME_UNIDADE,
       CM.CODIGO                    AS COD_MODELO_CHECKLIST,
       CM.NOME                      AS NOME_MODELO,
       CM.STATUS_ATIVO              AS STATUS_MODELO_CHECKLIST,
       CP.CODIGO                    AS CODIGO_PERGUNTA,
       CP.PERGUNTA                  AS DESCRICAO_PERGUNTA,
       CP.SINGLE_CHOICE             AS TIPO_DE_RESPOSTA,
       CAP.CODIGO                   AS CODIGO_ALTERNATIVA,
       CAP.ALTERNATIVA              AS DESCRICAO_ALTERNATIVA,
       CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS,
       CAP.PRIORIDADE               AS PRIORIDADE,
       CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.COD_UNIDADE = CAP.COD_UNIDADE
                  AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
                  AND CP.CODIGO = CAP.COD_PERGUNTA
         JOIN CHECKLIST_MODELO CM
              ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
         JOIN UNIDADE U
              ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE IN (SELECT U.CODIGO
                         FROM UNIDADE U
                         WHERE U.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
  AND F_IF(F_APENAS_MODELOS_CHECKLIST_ATIVOS, CM.STATUS_ATIVO = TRUE, TRUE)
ORDER BY CM.STATUS_ATIVO DESC, U.CODIGO, CM.CODIGO, CP.CODIGO, CAP.CODIGO;
$$;