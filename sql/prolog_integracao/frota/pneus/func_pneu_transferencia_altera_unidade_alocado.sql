-- Sobre:
-- Essa function é responsável por atualizar a unidade dos pneus.
-- Atualizando na tabela PNEU e na tabela de integração PNEU_CADASTRADO.
-- IMPORTANTE: Essa function ignora a unidade onde o pneu está atualmente, entendemos ela como responsável por colocar
-- o pneu onde a integração quer que esteja, independente de qual unidade ele se encontra no momento.
--
-- Histórico:
-- 2020-03-25 -> Function criada (natanrotta - PLI-80).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(F_COD_UNIDADE_DESTINO BIGINT,
                                                                                      F_COD_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_ROWS BIGINT;
BEGIN
    UPDATE PNEU
    SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
    WHERE CODIGO = ANY (F_COD_PNEUS);
    GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;

    IF (QTD_ROWS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar cod_unidade em PNEU_DATA';
    END IF;

    -- Atualiza unidade na tabela de integração pneu_cadastrado.
    UPDATE INTEGRACAO.PNEU_CADASTRADO
    SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO
    WHERE COD_PNEU_CADASTRO_PROLOG = ANY (F_COD_PNEUS);

    GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;

    IF (QTD_ROWS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar cod_unidade em PNEU_CADASTRADO';
    END IF;
END;
$$;