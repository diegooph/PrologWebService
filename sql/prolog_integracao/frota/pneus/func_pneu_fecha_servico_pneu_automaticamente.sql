-- Sobre:
--
-- Function disponível na API do ProLog para fechar automaticamente serviços pendentes de pneus.
--
-- Utiliza-se essa function sempre que uma alteração de status de pneu for realizada. Utilizamos a flag
-- 'fechado_automaticamente_integracao' para rastrear os serviços de pneus que foram fechados automaticamente pela
-- integração.
-- Também setamos a flag 'fechado_automaticamente_movimentacao' para sinalizar que foi uma atualização de status que
-- originou o fechamento automaticamente.
--
-- Histórico:
-- 2019-09-18 -> Function criada (diogenesvanzella - PL-2302).
-- 2020-01-22 -> Corrige timezone da data recebida (diogenesvanzella - PLI-43).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU_PROLOG BIGINT,
                                                            F_DATA_HORA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    UPDATE PUBLIC.AFERICAO_MANUTENCAO
    SET KM_MOMENTO_CONSERTO                  = 0, -- Zero pois no fechamento automatico não há o input de KM
        DATA_HORA_RESOLUCAO                  = F_DATA_HORA_RESOLUCAO,
        FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE,
        FECHADO_AUTOMATICAMENTE_INTEGRACAO   = TRUE
    WHERE COD_PNEU = F_COD_PNEU_PROLOG
      AND DATA_HORA_RESOLUCAO IS NULL;
END;
$$;