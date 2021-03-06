BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
--########################## Atualiza function de fechamento de item integrado da Translecchi ##########################
--######################################################################################################################
--######################################################################################################################
-- PLI-50
-- Dropa function
DROP FUNCTION INTEGRACAO.FUNC_INTEGRACAO_RESOLVE_ITENS_PENDENTES_EMPRESA(F_COD_UNIDADE_ORDEM_SERVICO BIGINT,
    F_COD_ORDEM_SERVICO BIGINT,
    F_COD_ITEM_RESOLVIDO BIGINT,
    F_CPF_COLABORADOR_RESOLUCAO BIGINT,
    F_KM_MOMENTO_RESOLUCAO BIGINT,
    F_DURACAO_RESOLUCAO_MS BIGINT,
    F_FEEDBACK_RESOLUCAO TEXT,
    F_DATA_HORA_RESOLVIDO_PROLOG TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
    F_TOKEN_INTEGRACAO TEXT,
    F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE);

-- Cria nova function
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_INTEGRACAO_RESOLVE_ITENS_PENDENTES_EMPRESA(F_COD_UNIDADE_ORDEM_SERVICO BIGINT,
                                                               F_COD_ORDEM_SERVICO BIGINT,
                                                               F_COD_ITEM_RESOLVIDO BIGINT,
                                                               F_CPF_COLABORADOR_RESOLUCAO BIGINT,
                                                               F_KM_MOMENTO_RESOLUCAO BIGINT,
                                                               F_DURACAO_RESOLUCAO_MS BIGINT,
                                                               F_FEEDBACK_RESOLUCAO TEXT,
                                                               F_DATA_HORA_RESOLVIDO_PROLOG TIMESTAMP WITH TIME ZONE,
                                                               F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                               F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                               F_TOKEN_INTEGRACAO TEXT,
                                                               F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                               F_DEVE_SOBRESCREVER_DADOS BOOLEAN DEFAULT TRUE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE  TEXT := 'P';
    F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO TEXT := 'R';
    F_STATUS_ORDEM_SERVICO_ABERTA         TEXT := 'A';
    F_STATUS_ORDEM_SERVICO_FECHADA        TEXT := 'F';
    F_QTD_ROWS_ITENS_OS                   BIGINT;
    F_QTD_ROWS_ITENS_RESOLVIDOS_OS        BIGINT;
BEGIN
    -- Antes de realizar o processo de fechamento de Item de Ordem de Servi??o, validamos os dados e v??nculos
    -- 1?? - Validamos se existe a O.S na unidade.
    IF (SELECT NOT EXISTS(
            SELECT COS.CODIGO
            FROM CHECKLIST_ORDEM_SERVICO COS
            WHERE COS.CODIGO = F_COD_ORDEM_SERVICO
              AND COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('A Ordem de Servi??o "%s" n??o existe na unidade "%s" do ProLog',
                               F_COD_ORDEM_SERVICO,
                               F_COD_UNIDADE_ORDEM_SERVICO));
    END IF;

    -- 2?? - Validamos se a O.S est?? ABERTA. Apenas validamos se n??o devemos sobrescrever os dados, caso devemos
    -- sobrescrever ent??o n??o tem necessidade de validar essa informa????o pois se o a O.S j?? fechada, apenas continuar??.
    IF (NOT F_DEVE_SOBRESCREVER_DADOS AND (SELECT COS.STATUS
                                           FROM CHECKLIST_ORDEM_SERVICO COS
                                           WHERE COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                                             AND COS.CODIGO = F_COD_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_FECHADA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('A Ordem de Servi??o "%s" j?? est?? fechada no ProLog', F_COD_ORDEM_SERVICO));
    END IF;

    -- 3?? - Validamos se o Item a ser resolvido pertence a O.S.
    IF ((SELECT COSI.COD_OS
         FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
         WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) != F_COD_ORDEM_SERVICO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O Item "%s" n??o pertence ?? O.S "%s" do ProLog',
                                                  F_COD_ITEM_RESOLVIDO,
                                                  F_COD_ORDEM_SERVICO));
    END IF;

    -- 4?? - Validamos se o Item da O.S est?? PENDENTE. Apenas validamos se n??o devemos sobrescrever as informa????es.
    IF (NOT F_DEVE_SOBRESCREVER_DADOS AND (SELECT COSI.STATUS_RESOLUCAO
                                           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                           WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) !=
                                          F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('O Item "%s" da O.S "%s" j?? est?? resolvido no ProLog',
                               F_COD_ITEM_RESOLVIDO,
                               F_COD_ORDEM_SERVICO));
    END IF;

    -- 5?? - Validamos se o CPF est?? presente na empresa
    IF (SELECT NOT EXISTS(SELECT C.CODIGO
                          FROM COLABORADOR C
                          WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                            AND C.COD_EMPRESA =
                                (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORDEM_SERVICO)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF %s n??o encontra-se cadastrado no ProLog',
                                                  PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
    END IF;

    -- POR SEGURAN??A, VERIFICAMOS SE A INTEGRA????O EST?? FECHANDO OS ITENS DE O.S. QUE PERTENCEM A EMPRESA CORRETA.
    IF (F_COD_UNIDADE_ORDEM_SERVICO NOT IN (SELECT CODIGO
                                            FROM UNIDADE
                                            WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        'Voc?? est?? tentando fechar um item de uma OS que n??o pertence ?? sua empresa');
    END IF;

    -- N??o verificamos a DATA_HORA_CONSERTO no WHERE pois n??o nos importa se est?? fechada ou n??o. Se chegou nesse ponto
    -- iremos atualizar as informa????es afim de fechar o item ou afim de sobrescrever as informa????es.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS
    SET CPF_MECANICO               = F_CPF_COLABORADOR_RESOLUCAO,
        TEMPO_REALIZACAO           = F_DURACAO_RESOLUCAO_MS,
        KM                         = F_KM_MOMENTO_RESOLUCAO,
        STATUS_RESOLUCAO           = F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO,
        DATA_HORA_CONSERTO         = F_DATA_HORA_RESOLVIDO_PROLOG,
        DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
        DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO,
        FEEDBACK_CONSERTO          = F_FEEDBACK_RESOLUCAO
    WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
      AND CODIGO = F_COD_ITEM_RESOLVIDO;

    -- O ATRIBUTO 'ROW_COUNT' CONTER?? A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
    -- IR?? RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
    GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

    -- O PRIMEIRO IF VERIFICA SE O ITEM EST?? PENDENTE, SE AP??S O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA
    -- QUE O UPDATE N??O EXECUTOU CORRETAMENTE. LAN??AMOS AQUI UMA EXCE????O PARA RASTREAR ESSE ERRO
    IF F_QTD_ROWS_ITENS_OS <= 0
    THEN
        RAISE EXCEPTION 'N??o foi poss??vel resolver o item %', F_COD_ITEM_RESOLVIDO;
    END IF;

    -- AO RESOLVER UM ITEM DE ORDEM DE SERVI??O ?? NECESS??RIO VERIFICAR SE A ORDEM DE SERVI??O FOI FINALIZADA.
    -- UMA 'O.S.' FECHADA CONSISTE EM UMA 'O.S.' QUE POSSUI TODOS OS SEUS ITENS RESOLVIDOS.
    UPDATE CHECKLIST_ORDEM_SERVICO
    SET STATUS               = F_STATUS_ORDEM_SERVICO_FECHADA,
        DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
    WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
      AND CODIGO = F_COD_ORDEM_SERVICO
      -- UTILIZAMOS ESSA VERIFICA????O PARA FOR??AR QUE O UPDATE ACONTE??A APENAS SE A 'O.S.' TIVER
      -- TODOS SEUS ITENS RESOLVIDOS.
      AND (SELECT COUNT(*)
           FROM CHECKLIST_ORDEM_SERVICO_ITENS
           WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
             AND COD_OS = F_COD_ORDEM_SERVICO
             AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0;

    -- PARA GARANTIR A CONSIST??NCIA DO PROCESSO DE RESOLU????O DE ITENS E FECHAMENTO DE ORDENS DE SERVI??O
    -- VERIFICAMOS SE A 'O.S.' QUE POSSUIU SEU ITEM FECHADO EST?? COM O 'STATUS' CORRETO.
    IF (((SELECT COUNT(*)
          FROM CHECKLIST_ORDEM_SERVICO_ITENS
          WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
            AND COD_OS = F_COD_ORDEM_SERVICO
            AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0)
        AND (SELECT STATUS
             FROM CHECKLIST_ORDEM_SERVICO
             WHERE CODIGO = F_COD_ORDEM_SERVICO
               AND COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_ABERTA)
        -- CASO A 'O.S.' N??O TEM NENHUM ITEM PENDENTE MAS O SEU STATUS ?? 'ABERTA', ENT??O TEMOS DADOS INCONSISTENTES.
    THEN
        RAISE EXCEPTION 'N??o foi poss??vel fechar a Ordem de Servi??o %', F_COD_ORDEM_SERVICO;
    END IF;

    -- AP??S REALIZAR O PROCESSO DE FECHAMENTO, INSERIMOS O ITEM RESOLVIDO NA TABELA DE MAPEAMENTO DE ITENS RESOLVIDOS
    -- ATRAV??S DA INTEGRA????O
    IF (F_DEVE_SOBRESCREVER_DADOS)
    THEN
        -- Se devemos sobrescrever as informa????es, eventualmente, j?? teremos fechado o item e este j?? est?? mapeado na
        -- estrutura de controle de itens fechados, nesse caso, atualizamos a data_hora de sincronia e segue o baile.
        INSERT INTO INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
        VALUES ((SELECT TI.COD_EMPRESA
                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO),
                F_COD_UNIDADE_ORDEM_SERVICO,
                F_COD_ORDEM_SERVICO,
                F_COD_ITEM_RESOLVIDO,
                F_DATA_HORA_SINCRONIA_RESOLUCAO)
        ON CONFLICT ON CONSTRAINT UNIQUE_ITEM_ORDEM_SERVICO_RESOLVIDO
            DO UPDATE SET DATA_HORA_SINCRONIA_RESOLUCAO = F_DATA_HORA_SINCRONIA_RESOLUCAO;
    ELSE
        -- Caso n??o ?? para sobrescrever, temos que estar atentos ??s constraints, evitando que tente-se inserir um
        -- item que j?? foi fechado previamente.
        INSERT INTO INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
        VALUES ((SELECT TI.COD_EMPRESA
                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO),
                F_COD_UNIDADE_ORDEM_SERVICO,
                F_COD_ORDEM_SERVICO,
                F_COD_ITEM_RESOLVIDO,
                F_DATA_HORA_SINCRONIA_RESOLUCAO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ITENS_RESOLVIDOS_OS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTE DE ITENS RESOLVIDOS NA INTEGRA????O OCORREU COM ??XITO
    IF F_QTD_ROWS_ITENS_RESOLVIDOS_OS <= 0
    THEN
        RAISE EXCEPTION
            'N??o foi poss??vel inserir o item resolvido na tabela de mapeamento, item %', F_COD_ITEM_RESOLVIDO;
    END IF;

    RETURN F_QTD_ROWS_ITENS_OS;
END;
$$;
END TRANSACTION;