-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Function responsável por deletar logicamente todos os serviços em aberto de um determinado pneu.
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER (natanrotta - PL-2229).
-- 2020-07-08 -> Padroniza variáveis (natanrotta - PL-2661).
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU(F_COD_VEICULO BIGINT,
                                                                           F_COD_PNEU BIGINT,
                                                                           F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
                                                                           F_DATA_HORA_REALIZACAO_TRANSFERENCIA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_QTD_INSERTS   BIGINT;
    V_QTD_UPDATES   BIGINT;
    V_PLACA_VEICULO TEXT := (SELECT V.PLACA
                             FROM VEICULO V
                             WHERE V.CODIGO = F_COD_VEICULO);
BEGIN
    INSERT INTO AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA (COD_SERVICO,
                                                                    COD_VEICULO_TRANSFERENCIA_INFORMACOES)
    SELECT AM.CODIGO,
           F_COD_TRANSFERENCIA_VEICULO_INFORMACOES
           -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    FROM AFERICAO_MANUTENCAO AM
             JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
    WHERE A.PLACA_VEICULO = V_PLACA_VEICULO
      AND AM.COD_PNEU = F_COD_PNEU
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

    GET DIAGNOSTICS V_QTD_INSERTS = ROW_COUNT;

    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        PG_USERNAME_DELECAO = SESSION_USER,
        DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
    WHERE COD_PNEU = F_COD_PNEU
      AND DELETADO = FALSE
      AND DATA_HORA_RESOLUCAO IS NULL
      AND (FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;

    -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
    -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
    -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo
    -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
    -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
    -- Porém, podemos ter uma inconsistência no BD.
    -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
    -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
    IF V_QTD_INSERTS <> V_QTD_UPDATES
    THEN
        RAISE EXCEPTION 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
    END IF;
END;
$$;