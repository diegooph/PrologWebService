-- Sobre:
--
-- Function disponível na API do ProLog para deletar de forma lógica, os serviços presentes para o pneu. A function é
-- utilizada no processo de sobrecarga, quando um pneu sofre uma atualização que altera a vida do pneu.
-- Para esse cenário, deletamos todos os serviços e criamos um serviço cadastro.
--
-- Histórico:
-- 2020-01-22 -> Function criada (diogenesvanzella - PLI-43).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_DELETA_SERVICOS_MOVIMENTACAO_PNEU(F_COD_PNEU_PROLOG BIGINT,
                                                           F_DATA_HORA_DELECAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL AS
$$
DECLARE
    -- Códigos dos serviços que estão pendentes e deverão ser deletados.
    COD_SERVICOS_PARA_DELETAR CONSTANT BIGINT[] := ARRAY(SELECT PSRD.CODIGO
                                                         FROM PUBLIC.PNEU_SERVICO_REALIZADO_DATA PSRD
                                                         WHERE PSRD.DELETADO = FALSE
                                                           AND PSRD.COD_PNEU = F_COD_PNEU_PROLOG);
BEGIN
    -- Deleta lógicamente incrementos de vida para o pneu.
    UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE DELETADO = FALSE
      AND COD_SERVICO_REALIZADO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente vínculo entre serviço e recapadora que realizou o servico, para o pneu.
    UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_SERVICO_REALIZADO_MOVIMENTACAO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente vinculo entre serviço e movimentação de origem do serviço, para o pneu.
    UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_SERVICO_REALIZADO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente os serviços realizados no pneu.
    UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente os serviços de cadastro realizados no pneu.
    UPDATE PUBLIC.PNEU_SERVICO_CADASTRO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_PNEU = F_COD_PNEU_PROLOG;
END;
$$;