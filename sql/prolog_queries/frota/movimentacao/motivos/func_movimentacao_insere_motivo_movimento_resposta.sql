-- Sobre:
-- Esta função cria uma relação movimentação x motivo.
--
-- Histórico:
-- 2020-03-18 -> Function criada (gustavocnp95 - PL-2607).
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_INSERE_MOTIVO_MOVIMENTO_RESPOSTA(F_COD_MOVIMENTO BIGINT,
                                                                              F_COD_MOTIVO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_RESPOSTA (COD_MOVIMENTACAO,
                                                        COD_MOTIVO_MOVIMENTO)
    VALUES (F_COD_MOVIMENTO,
            F_COD_MOTIVO);

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao salvar motivo de movimento, tente novamente.');
    END IF;
END;
$$;
