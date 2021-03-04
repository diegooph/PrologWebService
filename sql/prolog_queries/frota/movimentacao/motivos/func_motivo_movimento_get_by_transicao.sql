-- Sobre:
-- Esta função realiza a busca de uma lista de motivos, com base na origem e no destino.
--
-- Histórico:
-- 2020-03-23 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_GET_BY_TRANSICAO(F_ORIGEM MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_DESTINO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO    BIGINT,
                DESCRICAO_MOTIVO TEXT,
                OBRIGATORIO      BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO       AS CODIGO_MOTIVO,
       MMM.MOTIVO       AS DESCRICAO_MOTIVO,
       MMMT.OBRIGATORIO AS OBRIGATORIO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT ON MMMT.COD_MOTIVO = MMM.CODIGO
WHERE MMMT.ORIGEM = F_ORIGEM
  AND MMMT.DESTINO = F_DESTINO
  AND MMMT.COD_UNIDADE = F_COD_UNIDADE
  AND MMM.ATIVO = TRUE
ORDER BY MMM.MOTIVO;
$$;