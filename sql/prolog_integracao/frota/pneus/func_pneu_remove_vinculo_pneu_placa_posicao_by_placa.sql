-- Sobre:
--
-- Function disponível na API do ProLog para remover o vínculo entre pneu e veículo. A function remove todos os pneus
-- da 'PLACA' repassada.
-- Após remover o vínculo entre o pneu e a placa, a function altera o status dos pneus para 'ESTOQUE'.
--
-- Histórico:
-- 2020-01-22 -> Function criada (diogenesvanzella - PLI-64).
-- 2020-07-27 -> Volta arquivo base para versão de Prod (diogenesvanzella - PLI-189).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PNEUS_PARA_ATUALIZAR BIGINT[] := (SELECT ARRAY_AGG(COD_PNEU)
                                          FROM PUBLIC.VEICULO_PNEU
                                          WHERE PLACA = F_PLACA_VEICULO);
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE PLACA = F_PLACA_VEICULO;

    UPDATE PUBLIC.PNEU
    SET STATUS = 'ESTOQUE'
    WHERE CODIGO = ANY (COD_PNEUS_PARA_ATUALIZAR);
END;
$$;