-- Sobre:
-- Function responsável por fazer a atualização do status de um pneu. Essa function fecha todos os serviços que o
-- pneu possui em aberto, logo após realizar o fechamento, o pneu tem seu status atualizado.
--
-- Histórico:
-- 2020-03-12 -> Function criada (natanrotta - PLI-79).
-- 2020-03-24 -> Adiciona flag boolean para fechamento de serviços (natanrotta - PLI-102)
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(F_COD_PNEU BIGINT,
                                                       F_STATUS_PNEU CHARACTER VARYING,
                                                       F_DEVE_FECHAR_SERVICOS BOOLEAN,
                                                       F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_ATUAL_PNEU   TEXT    := (SELECT STATUS
                                      FROM PNEU
                                      WHERE CODIGO = F_COD_PNEU);
    V_TEM_SERVICO_ABERTO  BOOLEAN := (SELECT EXISTS(SELECT COD_PNEU
                                                    FROM AFERICAO_MANUTENCAO_DATA
                                                    WHERE COD_PNEU = F_COD_PNEU
                                                      AND DELETADO IS FALSE
                                                      AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                                      AND FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE));
    V_RETIRADO_DO_VEICULO BOOLEAN := (SELECT (V_STATUS_ATUAL_PNEU = 'EM_USO' AND F_STATUS_PNEU <> 'EM_USO'));
BEGIN
    --VERIFICA SE PNEU POSSUI SERVIÇOS EM ABERTO
    IF (V_TEM_SERVICO_ABERTO AND V_RETIRADO_DO_VEICULO AND F_DEVE_FECHAR_SERVICOS)
    THEN
        PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU, F_DATA_HORA_ALTERACAO_STATUS);
    END IF;

    IF (V_RETIRADO_DO_VEICULO)
    THEN
        -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
        DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = F_COD_PNEU;
    END IF;

    --ATUALIZA STATUS
    UPDATE PUBLIC.PNEU
    SET STATUS = F_STATUS_PNEU
    WHERE CODIGO = F_COD_PNEU;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                       F_COD_PNEU,
                       F_STATUS_PNEU));
    END IF;
END;
$$;