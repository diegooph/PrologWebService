-- Sobre:
--
-- Function disponível na API do ProLog para remover o vínculo entre pneu e veículo.
--
-- Essa function deve ser utilizada especificamente em cenários ocorrem diferentes movimentações de pneus dentro da
-- mesma placa, movimentação essa chamada de rodízio de pneus.
-- A function recebe uma lista de pneus como parâmetro e para eles, deleta o vínculo com a placa, possibilitando que
-- outro pneu possa ocupar a sua atual posição.
-- Após remover o vínculo entre o pneu e a placa, a function altera o status dos pneus para 'ESTOQUE'.
--
-- Precondições:
-- Para o correto funcionamento da function, é necessário repassar a ela uma lista de 'cod_pneu_sistema_integrado'.
-- Como se trata de uma function de integração, ela é limitada a operar apenas com pneus que foram vinculados através
-- da integração.
--
-- Histórico:
-- 2019-09-16 -> Function criada (diogenesvanzella - PL-2222).
-- 2019-11-26 -> FIX - adiciona validação do código da empresa (diogenesvanzella - PLI-41).
-- 2020-01-22 -> Move pneus para o estoque quando removidos do veículo (diogenesvanzella - PLI-64).
-- 2020-04-01 -> Altera estrutura para atualizar status do pneu. (natanrotta - PLI-102).
-- 2020-07-27 -> Volta arquivo base para versão de Prod (diogenesvanzella - PLI-189).
-- 2020-08-06 -> Adapta function para lidar com tokens repetidos (diogenesvanzella - PLI-175).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
                                                           F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PNEUS_PROLOG CONSTANT BIGINT[] := (SELECT ARRAY_AGG(PC.COD_PNEU_CADASTRO_PROLOG)
                                           FROM INTEGRACAO.PNEU_CADASTRADO PC
                                           WHERE PC.COD_EMPRESA_CADASTRO IN (SELECT TI.COD_EMPRESA
                                                                             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
                                             AND PC.COD_PNEU_SISTEMA_INTEGRADO = ANY (F_COD_SISTEMA_INTEGRADO_PNEUS));
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE COD_PNEU = ANY (COD_PNEUS_PROLOG);

    UPDATE PNEU
    SET STATUS = 'ESTOQUE'
    WHERE CODIGO = ANY (COD_PNEUS_PROLOG);
END;
$$;