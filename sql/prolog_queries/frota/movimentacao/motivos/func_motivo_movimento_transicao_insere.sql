-- Sobre:
-- Esta função cria uma nova relação motivo, origem e destino por unidade.
--
-- Atenção: A function realiza a validação da já existência da relação por unidade.
-- Se existir, verifica se as informações mudaram, que neste momento é apenas obrigatoriedade.
-- Se sim, exclui a linha e a recria. Se não, apenas ignora, retornando o código já existente.
--
-- Histórico:
-- 2020-03-18 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_INSERE(F_COD_MOTIVO BIGINT,
                                                                  F_COD_EMPRESA BIGINT,
                                                                  F_COD_UNIDADE BIGINT,
                                                                  F_ORIGEM MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_DESTINO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_OBRIGATORIO BOOLEAN,
                                                                  F_DATA_HORA_INSERCAO TIMESTAMP WITH TIME ZONE,
                                                                  F_COD_COLABORADOR_INSERCAO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MOTIVO_ORIGEM_DESTINO BIGINT;
BEGIN
    INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO (COD_MOTIVO,
                                                         COD_EMPRESA,
                                                         COD_UNIDADE,
                                                         ORIGEM,
                                                         DESTINO,
                                                         OBRIGATORIO,
                                                         DATA_HORA_ULTIMA_ALTERACAO,
                                                         COD_COLABORADOR_ULTIMA_ALTERACAO)
    VALUES (F_COD_MOTIVO,
            F_COD_EMPRESA,
            F_COD_UNIDADE,
            F_ORIGEM,
            F_DESTINO,
            F_OBRIGATORIO,
            F_DATA_HORA_INSERCAO,
            F_COD_COLABORADOR_INSERCAO)
    RETURNING CODIGO INTO V_COD_MOTIVO_ORIGEM_DESTINO;

    RETURN V_COD_MOTIVO_ORIGEM_DESTINO;
END
$$;
