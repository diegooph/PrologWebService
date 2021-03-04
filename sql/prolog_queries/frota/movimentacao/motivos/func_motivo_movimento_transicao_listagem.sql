-- Sobre:
-- Esta função retorna a lista de todas as relações motivo, origem e destino existentes
-- para as unidades permitidas pro usuário que realizou a requisição.
--
-- Histórico:
-- 2020-03-23 -> Function criada (gustavocnp95 - PL-2607).
-- 2020-04-14 -> Function refatorada para fazer left join com unidade (gustavocnp95 - PL-2681).
-- 2020-08-20 -> Altera para buscar mesmo as unidades que não possuem equipe. (luizfp).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_LISTAGEM(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                CODIGO_UNIDADE    BIGINT,
                NOME_UNIDADE      VARCHAR(40),
                CODIGO_MOTIVO     BIGINT,
                DESCRICAO_MOTIVO  CITEXT,
                ORIGEM_MOVIMENTO  MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO_MOVIMENTO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO       BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN

    RETURN QUERY
        SELECT U.CODIGO AS CODIGO_UNIDADE,
               U.NOME           AS NOME_UNIDADE,
               MMMT.COD_MOTIVO  AS CODIGO_MOTIVO,
               MMM.MOTIVO       AS DESCRICAO_MOTIVO,
               MMMT.ORIGEM      AS ORIGEM_MOVIMENTO,
               MMMT.DESTINO     AS DESTINO_MOVIMENTO,
               MMMT.OBRIGATORIO AS OBRIGATORIO
        FROM UNIDADE U
                 left JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT ON MMMT.cod_unidade = U.codigo
                 left JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMT.COD_MOTIVO
        WHERE U.CODIGO IN (SELECT DISTINCT FCGUA.CODIGO_UNIDADE
                                FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR, FALSE) FCGUA)
        ORDER BY MMMT.COD_UNIDADE, MMMT.ORIGEM, MMMT.DESTINO, MMM.codigo;
END;
$$;