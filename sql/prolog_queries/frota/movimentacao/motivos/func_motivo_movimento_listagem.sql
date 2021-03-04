-- Sobre:
-- Esta função realiza a busca de uma lista de motivos com base no código da empresa.
--
--
-- Histórico:
-- 2020-03-18 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                          F_APENAS_ATIVOS BOOLEAN,
                                                          F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO                     BIGINT,
                NOME_EMPRESA                      TEXT,
                DESCRICAO_MOTIVO                  TEXT,
                CODIGO_AUXILIAR                   TEXT,
                DATA_HORA_ULTIMA_ALTERACAO_MOTIVO TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT,
                ATIVO_MOTIVO                      BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
       E.NOME                                                  AS NOME_EMPRESA,
       MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
       MMM.COD_AUXILIAR                                      AS CODIGO_AUXILIAR,
       MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO_MOTIVO,
       CUA.NOME                                                AS NOME_COLABORADOR_ULTIMA_ALTERACAO,
       MMM.ATIVO                                               AS ATIVO_MOTIVO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN EMPRESA E ON E.CODIGO = MMM.COD_EMPRESA
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMM.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_APENAS_ATIVOS IS FALSE, TRUE, MMM.ATIVO = TRUE)
ORDER BY MMM.CODIGO;
$$;