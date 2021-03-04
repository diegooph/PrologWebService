-- Sobre:
--
-- Function disponível na API do ProLog para deletar os serviços abertos para a placa. A function faz a deleção somente
-- dos serviços pendentes. Para casos onde a OS fica sem nenhum serviço, ou seja, todos foram deletados logicamente, a
-- ordem de serviço também é deletada logicamente.
--
-- Histórico:
-- 2020-01-22 -> Function criada (diogenesvanzella - PLI-64).
-- 2020-01-28 -> Fix tipo do parametro onde recebemos a placa e usos indevidos do IN (diogenesvanzella - PLI-64).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_DELETA_SERVICOS_ABERTOS_PLACA(F_PLACA_VEICULO TEXT,
                                                          F_COD_UNIDADE BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_SERVICOS_PARA_DELETAR   CONSTANT BIGINT[] := (SELECT ARRAY_AGG(COSI.CODIGO)
                                                      FROM CHECKLIST_ORDEM_SERVICO COS
                                                               JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
                                                               JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                                                    ON COS.CODIGO = COSI.COD_OS
                                                                        AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                                                      WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
                                                        AND COSI.STATUS_RESOLUCAO = 'P'
                                                        AND COSI.COD_UNIDADE = F_COD_UNIDADE);
    -- Usamos o 'DISTINCT' para não repetir o 'cod_os' no array gerado.
    COD_ORDENS_SERVICO_ANALISAR CONSTANT BIGINT[] := (SELECT ARRAY_AGG(DISTINCT COD_OS)
                                                      FROM CHECKLIST_ORDEM_SERVICO_ITENS
                                                      WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR));
BEGIN
    -- Aqui deletamos os ITENS que estão pendentes de resolução na placa informada.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Após deletarmos os itens, varremos as OSs para saber se alguma das OSs que tiveram seus itens deletados estão
    -- vazias, se estiverem vazias (count() = 0) então deletamos também.
    -- The secret key:
    -- O segredo para esse update funcionar está em utilizar a view 'checklist_ordem_servico_itens' e não filtrar
    -- por status dos itens, pois assim saberemos se após deletar lógicamente os itens na query anterior, a OS se
    -- mantem com algum item dentro dela, seja pendente ou resolvido. Caso tiver, não devemos deletar.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_DATA COSD
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COSD.COD_UNIDADE = F_COD_UNIDADE
      AND NOT DELETADO -- Se já está deletada, não nos interessa.
      AND COSD.CODIGO = ANY (COD_ORDENS_SERVICO_ANALISAR)
      AND ((SELECT COUNT(COSI.CODIGO)
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = COSD.CODIGO
              AND COSI.COD_UNIDADE = F_COD_UNIDADE) = 0);

    -- Pode acontecer um cenário onde a OS tenha 2 itens, um resolvido e um pendente. Neste cenário a OS está aberta,
    -- ao deletar o item pendente, devemos fechar a OS e inserir a data de fechamento como a data do último item
    -- resolvido.
    -- The secret key:
    -- O segredo aqui esta em usar a view 'checklist_ordem_servico' para realizar o update, pois ela já não trará as
    -- OSs que foram deletados na query acima. Bastando verificar se a OS não tem nenhum item pendente, para esses
    -- casos buscamos a maior 'data_hora_conserto' e usamos ela para fechar a OS.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO AS COS
    SET STATUS               = 'F',
        DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
                                FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                WHERE COSI.COD_OS = COS.CODIGO
                                  AND COSI.COD_UNIDADE = COS.COD_UNIDADE)
    WHERE COS.COD_UNIDADE = F_COD_UNIDADE
      AND COS.CODIGO = ANY (COD_ORDENS_SERVICO_ANALISAR)
      AND COS.STATUS = 'A'
      AND ((SELECT COUNT(COSI.CODIGO)
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = COS.CODIGO
              AND COSI.COD_UNIDADE = F_COD_UNIDADE
              AND COSI.STATUS_RESOLUCAO = 'P') = 0);
END;
$$;