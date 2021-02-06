-- Sobre:
-- Esta função realiza a busca de uma lista de origens e destinos com base no código da unidade,
-- sem repetir as relações (select distinct).
--
--
-- Histórico:
-- 2020-03-18 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_GET_TRANSICAO_BY_UNIDADE(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                CODIGO_UNIDADE BIGINT,
                ORIGEM         MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO        MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO    BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT DISTINCT MMMT.COD_UNIDADE AS CODIGO_UNIDADE,
                MMMT.ORIGEM      AS ORIGEM,
                MMMT.DESTINO     AS DESTINO,
                MMMT.OBRIGATORIO AS OBRIGATORIO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
                    ON MMM.CODIGO = MMMT.COD_MOTIVO
WHERE MMMT.COD_UNIDADE = F_COD_UNIDADE
  AND MMM.ATIVO = TRUE
ORDER BY MMMT.ORIGEM, MMMT.DESTINO;
$$;