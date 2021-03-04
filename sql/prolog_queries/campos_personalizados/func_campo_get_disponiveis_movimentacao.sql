-- Sobre:
-- Busca os campos personalizados que estão disponíveis para preenchimento durante um processo de movimentação na
-- unidade. Note que para um campo estar disponível para uso no processo de movimentação na unidade, além de estar
-- cadastrado na empresa para a funcionalidade de movimentação, também precisa estar vinculado à unidade na tabela:
-- "movimentacao_campo_personalizado_unidade".
--
-- Histórico:
-- 2020-03-23 -> Function criada (luizfp - PL-2615).
CREATE OR REPLACE FUNCTION FUNC_CAMPO_GET_DISPONIVEIS_MOVIMENTACAO(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_CAMPO                                BIGINT,
                COD_EMPRESA                              BIGINT,
                COD_FUNCAO_PROLOG_AGRUPAMENTO            SMALLINT,
                COD_TIPO_CAMPO                           SMALLINT,
                NOME_CAMPO                               TEXT,
                DESCRICAO_CAMPO                          TEXT,
                TEXTO_AUXILIO_PREENCHIMENTO_CAMPO        TEXT,
                PREENCHIMENTO_OBRIGATORIO_CAMPO          BOOLEAN,
                MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO_CAMPO TEXT,
                PERMITE_SELECAO_MULTIPLA_CAMPO           BOOLEAN,
                OPCOES_SELECAO_CAMPO                     TEXT[],
                ORDEM_EXIBICAO                           SMALLINT
            )
    LANGUAGE SQL
AS
$$
SELECT CPE.CODIGO                              AS COD_CAMPO,
       CPE.COD_EMPRESA                         AS COD_EMPRESA,
       CPE.COD_FUNCAO_PROLOG_AGRUPAMENTO       AS COD_FUNCAO_PROLOG_AGRUPAMENTO,
       CPE.COD_TIPO_CAMPO                      AS COD_TIPO_CAMPO,
       CPE.NOME::TEXT                          AS NOME_CAMPO,
       CPE.DESCRICAO::TEXT                     AS DESCRICAO_CAMPO,
       CPE.TEXTO_AUXILIO_PREENCHIMENTO::TEXT   AS TEXTO_AUXILIO_PREENCHIMENTO_CAMPO,
       MCPU.PREENCHIMENTO_OBRIGATORIO          AS PREENCHIMENTO_OBRIGATORIO_CAMPO,
       MCPU.MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO AS MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO_CAMPO,
       CPE.PERMITE_SELECAO_MULTIPLA            AS PERMITE_SELECAO_MULTIPLA_CAMPO,
       CPE.OPCOES_SELECAO                      AS OPCOES_SELECAO_CAMPO,
       MCPU.ORDEM_EXIBICAO                     AS ORDEM_EXIBICAO
FROM CAMPO_PERSONALIZADO_EMPRESA CPE
         JOIN MOVIMENTACAO_CAMPO_PERSONALIZADO_UNIDADE MCPU
              ON CPE.CODIGO = MCPU.COD_CAMPO
WHERE CPE.STATUS_ATIVO = TRUE
  AND MCPU.HABILITADO_PARA_USO = TRUE
  AND MCPU.COD_UNIDADE = F_COD_UNIDADE
ORDER BY MCPU.ORDEM_EXIBICAO;
$$;