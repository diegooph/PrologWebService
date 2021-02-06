-- Sobre:
-- Esta função realiza a busca de uma transição e o motivo vinculado com base no código da transição.
--
--
-- Histórico:
-- 2020-03-18 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_VISUALIZACAO(F_COD_MOTIVO_TRANSICAO BIGINT,
                                                                        F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO_TRANSICAO           BIGINT,
                NOME_EMPRESA                      TEXT,
                DESCRICAO_MOTIVO                  TEXT,
                ORIGEM                            MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO                           MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO                       BOOLEAN,
                DATA_HORA_ULTIMA_ALTERACAO        TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MMMT.CODIGO                                              AS CODIGO_MOTIVO_TRANSICAO,
       E.NOME                                                   AS NOME_EMPRESA,
       MMM.MOTIVO                                               AS DESCRICAO_MOTIVO,
       MMMT.ORIGEM                                              AS ORIGEM,
       MMMT.DESTINO                                             AS DESTINO,
       MMMT.OBRIGATORIO                                         AS OBRIGATORIO,
       MMMT.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO,
       CUA.NOME                                                 AS NOME_COLABORADOR_ULTIMA_ALTERACAO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT
         INNER JOIN EMPRESA E ON E.CODIGO = MMMT.COD_EMPRESA
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMT.COD_MOTIVO
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMMT.CODIGO = F_COD_MOTIVO_TRANSICAO;
$$;