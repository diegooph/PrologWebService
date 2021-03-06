alter table integracao.token_integracao
    drop constraint unique_token_token_integracao;

CREATE OR REPLACE FUNCTION
    PICCOLOTUR.FUNC_CHECK_OS_INSERE_ITEM_OS_ABERTA(F_COD_OS_GLOBUS BIGINT,
                                                   F_COD_UNIDADE_OS BIGINT,
                                                   F_COD_CHECKLIST BIGINT,
                                                   F_COD_ITEM_OS_GLOBUS BIGINT,
                                                   F_COD_CONTEXTO_PERGUNTA_CHECKLIST BIGINT,
                                                   F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST BIGINT,
                                                   F_DATA_HORA_SINCRONIZACAO_PENDENCIA TIMESTAMP WITH TIME ZONE,
                                                   F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_OS_ABERTA        CONSTANT TEXT   := 'A';
    V_STATUS_ITEM_OS_PENDENTE CONSTANT TEXT   := 'P';
    V_CODIGO_PERGUNTA         CONSTANT BIGINT := (SELECT CP.CODIGO
                                                  FROM CHECKLIST_PERGUNTAS CP
                                                  WHERE CP.CODIGO_CONTEXTO = F_COD_CONTEXTO_PERGUNTA_CHECKLIST
                                                    AND CP.COD_VERSAO_CHECKLIST_MODELO =
                                                        (SELECT C.COD_VERSAO_CHECKLIST_MODELO
                                                         FROM CHECKLIST C
                                                         WHERE C.CODIGO = F_COD_CHECKLIST));
    V_CODIGO_ALTERNATIVA      CONSTANT BIGINT := (SELECT CAP.CODIGO
                                                  FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                                                  WHERE CAP.CODIGO_CONTEXTO = F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST
                                                    AND CAP.COD_PERGUNTA = V_CODIGO_PERGUNTA
                                                    AND CAP.COD_VERSAO_CHECKLIST_MODELO =
                                                        (SELECT C.COD_VERSAO_CHECKLIST_MODELO
                                                         FROM CHECKLIST C
                                                         WHERE C.CODIGO = F_COD_CHECKLIST));
    V_COD_EMPRESA_OS          CONSTANT BIGINT := (SELECT TI.COD_EMPRESA
                                                  FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                           JOIN UNIDADE U ON U.COD_EMPRESA = TI.COD_EMPRESA
                                                  WHERE U.CODIGO = F_COD_UNIDADE_OS);
    V_COD_ITEM_OS_PROLOG               BIGINT;
BEGIN
    -- Antes de processarmos a abertura da O.S e inser????o de Itens, validamos todos os c??digos de v??nculo.
    -- Validamos se o c??digo da unidade da O.S bate com a empresa do Token
    IF (V_COD_EMPRESA_OS IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] O token "%s" n??o est?? autorizado a inserir dados da unidade "%s"',
                               F_TOKEN_INTEGRACAO,
                               F_COD_UNIDADE_OS));
    END IF;

    -- Validamos se o Item da Ordem de Servi??o j?? existe no ProLog.
    -- Nesse caso, n??o podemos incrementar a quantidade de apontamentos pois, o incremento ?? feito em JAVA, quando
    -- enviamos os itens NOK para o Globus. Em teoria, deveria chegar para o ProLog apenas novos itens de OS.
--     IF (SELECT EXISTS(SELECT *
--                       FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO COSIV
--                       WHERE COSIV.COD_UNIDADE = F_COD_UNIDADE_OS
--                         AND COSIV.COD_OS_GLOBUS = F_COD_OS_GLOBUS
--                         AND COSIV.COD_ITEM_OS_GLOBUS = F_COD_ITEM_OS_GLOBUS
--                         AND COSIV.COD_ITEM_OS_PROLOG))
--     THEN
--         PERFORM PUBLIC.THROW_GENERIC_ERROR(
--                         FORMAT('[ERRO DE STATUS] O servi??o "%s" j?? est?? existe na O.S "%s" do ProLog',
--                                F_COD_ITEM_OS_GLOBUS,
--                                F_COD_OS_GLOBUS));
--     END IF;

    -- Validamos se o c??digo do checklist existe.
    IF (SELECT NOT EXISTS(
            SELECT C.CODIGO
            FROM PUBLIC.CHECKLIST C
            WHERE C.CODIGO = F_COD_CHECKLIST
              AND C.COD_UNIDADE = F_COD_UNIDADE_OS))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] O checklist "%s" n??o encontra-se na base de dados do ProLog',
                               F_COD_CHECKLIST));
    END IF;

    -- Validamos se a pergunta existe e est?? mesmo vinculada ao checklist realizado.
    IF (SELECT NOT EXISTS(
            SELECT CRN.COD_PERGUNTA
            FROM PUBLIC.CHECKLIST_RESPOSTAS_NOK CRN
            WHERE CRN.COD_CHECKLIST = F_COD_CHECKLIST
              AND CRN.COD_PERGUNTA = V_CODIGO_PERGUNTA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] A pergunta "%s" n??o possui v??nculo com o checklist "%s"',
                               F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                               F_COD_CHECKLIST));
    END IF;

    -- Validamos se a alternativa existe e pertence a pergunta do checklist realizado.
    IF (SELECT NOT EXISTS(
            SELECT CRN.COD_ALTERNATIVA
            FROM PUBLIC.CHECKLIST_RESPOSTAS_NOK CRN
            WHERE CRN.COD_CHECKLIST = F_COD_CHECKLIST
              AND CRN.COD_PERGUNTA = V_CODIGO_PERGUNTA
              AND CRN.COD_ALTERNATIVA = V_CODIGO_ALTERNATIVA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] A alternativa "%s" n??o possui v??nculo com a pergunta "%s"',
                               F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
                               F_COD_CONTEXTO_PERGUNTA_CHECKLIST));
    END IF;

    -- Validamos se o Item da O.S pertencem a um checklist que de fato foi enviado para o Globus.
    IF (NOT (SELECT EXISTS(
                            SELECT *
                            FROM PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS CINEG
                            WHERE CINEG.COD_CHECKLIST = F_COD_CHECKLIST
                              AND CINEG.COD_CONTEXTO_PERGUNTA = F_COD_CONTEXTO_PERGUNTA_CHECKLIST
                              AND CINEG.COD_CONTEXTO_ALTERNATIVA = F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT(
                                    '[ERRO DE V??NCULO] N??o existe v??nculo entre o cod_checklist "%s",' ||
                                    ' cod_pergunta "%s" e cod_alternativa "%s"',
                                    F_COD_CHECKLIST,
                                    F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                                    F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST));
    END IF;

    -- Se chegou nesse est??gio, j?? validamos todos os cen??rios do item, devemos ent??o inserir.
    -- Se a Ordem de Servi??o n??o existe, ent??o criamos ela.
    IF (SELECT NOT EXISTS(
            SELECT COS.CODIGO
            FROM PUBLIC.CHECKLIST_ORDEM_SERVICO COS
            WHERE COS.CODIGO = F_COD_OS_GLOBUS
              AND COS.COD_UNIDADE = F_COD_UNIDADE_OS))
    THEN
        INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO(CODIGO,
                                                   COD_UNIDADE,
                                                   COD_CHECKLIST,
                                                   STATUS)
        VALUES (F_COD_OS_GLOBUS, F_COD_UNIDADE_OS, F_COD_CHECKLIST, V_STATUS_OS_ABERTA);
    ELSE
        -- Caso a OS estiver fechada, iremos reabrir para inserir o novo item.
        -- Se estiver aberta, iremos apenas adicionar o item nela.
        UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO
        SET STATUS               = V_STATUS_OS_ABERTA,
            DATA_HORA_FECHAMENTO = NULL
        WHERE CODIGO = F_COD_OS_GLOBUS
          AND COD_UNIDADE = F_COD_UNIDADE_OS;
    END IF;

    -- N??o precisamos validar novamente se o item j?? existe no banco de dados, apenas inserimos.
    INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS(COD_UNIDADE,
                                                     COD_OS,
                                                     STATUS_RESOLUCAO,
                                                     COD_CONTEXTO_PERGUNTA,
                                                     COD_CONTEXTO_ALTERNATIVA,
                                                     COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                                                     COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO)
    VALUES (F_COD_UNIDADE_OS,
            F_COD_OS_GLOBUS,
            V_STATUS_ITEM_OS_PENDENTE,
            F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
            F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
            V_CODIGO_PERGUNTA,
            V_CODIGO_ALTERNATIVA)
    RETURNING CODIGO INTO V_COD_ITEM_OS_PROLOG;

    -- N??o chegar?? nesse ponto um 'item', 'checklist' ou 'alternativa' que n??o existam, ent??o podemos inserir os
    -- dados com seguran??a. Tamb??m, n??o chegar?? aqui um item que n??o deveremos inserir ou que devemos aumentar a
    -- quantidade de apontamentos, nesse est??gio o item SEMPRE tera 'NOVA_QTD_APONTAMENTOS' = 1 (primeiro apontamento).
    INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS(COD_ITEM_ORDEM_SERVICO,
                                                                  COD_CHECKLIST_REALIZADO,
                                                                  COD_ALTERNATIVA,
                                                                  NOVA_QTD_APONTAMENTOS)
    VALUES (V_COD_ITEM_OS_PROLOG, F_COD_CHECKLIST, V_CODIGO_ALTERNATIVA, 1);

    -- Ap??s salvar o item, criamos o v??nculo dele na tabela DE-PARA.
    INSERT INTO PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO(COD_UNIDADE,
                                                                COD_OS_GLOBUS,
                                                                COD_ITEM_OS_GLOBUS,
                                                                COD_ITEM_OS_PROLOG,
                                                                PLACA_VEICULO_OS,
                                                                COD_CHECKLIST_OS_PROLOG,
                                                                COD_CONTEXTO_PERGUNTA_OS_PROLOG,
                                                                COD_CONTEXTO_ALTERNATIVA_OS_PROLOG,
                                                                DATA_HORA_SINCRONIA_PENDENCIA)
    VALUES (F_COD_UNIDADE_OS,
            F_COD_OS_GLOBUS,
            F_COD_ITEM_OS_GLOBUS,
            V_COD_ITEM_OS_PROLOG,
            (SELECT C.PLACA_VEICULO FROM PUBLIC.CHECKLIST C WHERE C.CODIGO = F_COD_CHECKLIST),
            F_COD_CHECKLIST,
            F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
            F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
            F_DATA_HORA_SINCRONIZACAO_PENDENCIA);

    RETURN V_COD_ITEM_OS_PROLOG;
END;
$$;


CREATE OR REPLACE FUNCTION
    PICCOLOTUR.FUNC_CHECK_OS_RESOLVE_ITEM_PENDENTE(F_COD_UNIDADE_ITEM_OS BIGINT,
                                                   F_COD_OS_GLOBUS BIGINT,
                                                   F_COD_ITEM_RESOLVIDO_GLOBUS BIGINT,
                                                   F_CPF_COLABORADOR_RESOLUCAO BIGINT,
                                                   F_PLACA_VEICULO_ITEM_OS TEXT,
                                                   F_KM_COLETADO_RESOLUCAO BIGINT,
                                                   F_DURACAO_RESOLUCAO_MS BIGINT,
                                                   F_FEEDBACK_RESOLUCAO TEXT,
                                                   F_DATA_HORA_RESOLVIDO_PROLOG TIMESTAMP WITH TIME ZONE,
                                                   F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                   F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                   F_TOKEN_INTEGRACAO TEXT,
                                                   F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_ITEM_OS_PENDENTE CONSTANT TEXT   := 'P';
    V_COD_RETORNO_SUCESSO     CONSTANT BIGINT := 1;
    V_COD_EMPRESA_OS          CONSTANT BIGINT := (SELECT TI.COD_EMPRESA
                                                  FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                           JOIN UNIDADE U ON U.COD_EMPRESA = TI.COD_EMPRESA
                                                  WHERE U.CODIGO = F_COD_UNIDADE_ITEM_OS);
    V_COD_ITEM_RESOLVIDO_PROLOG        BIGINT;
    V_QTD_ROWS_ITENS_OS                BIGINT;
    V_QTD_ROWS_VINCULOS_ALTERADOS      BIGINT;
BEGIN
    -- Antes de processarmos a resolu????o de Itens de O.S., validamos todos os c??digos de v??nculo poss??veis.
    -- Por seguran??a, verificamos se a integra????o est?? fechando os itens de o.s. que pertencem a empresa correta.
    IF (V_COD_EMPRESA_OS IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] O token "%s" n??o est?? autorizado a inserir dados da unidade "%s"',
                               F_TOKEN_INTEGRACAO,
                               F_COD_UNIDADE_ITEM_OS));
    END IF;

    -- Validamos se o c??digo do item fechado no Globus, est?? mapeado no ProLog.
    IF (SELECT EXISTS(SELECT COD_ITEM_OS_PROLOG
                      FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
                      WHERE COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS
                        AND COD_OS_GLOBUS = F_COD_OS_GLOBUS
                        AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS))
    THEN
        SELECT COD_ITEM_OS_PROLOG
        FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
        WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
          AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS
          AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
        INTO V_COD_ITEM_RESOLVIDO_PROLOG;
    ELSE
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE V??NCULO] O item "%s" da O.S. "%s" n??o possu?? v??nculo no ProLog',
                               F_COD_ITEM_RESOLVIDO_GLOBUS,
                               F_COD_OS_GLOBUS));
    END IF;

    -- Validamos se o item mapeado est?? pendente no ProLog. Caso j?? est?? resolvido, apenas retorna sucesso.
    IF (SELECT COSID.STATUS_RESOLUCAO
        FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO COSIV
                 JOIN CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                      ON COSIV.COD_ITEM_OS_PROLOG = COSID.CODIGO
        WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
          AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != V_STATUS_ITEM_OS_PENDENTE
    THEN
        RETURN V_COD_RETORNO_SUCESSO;
    END IF;

    -- Validamos se o usu??rio est?? na base de dados do ProLog, podendo estar em qualquer unidade da empresa integrada.
    IF (SELECT NOT EXISTS(SELECT C.CODIGO
                          FROM PUBLIC.COLABORADOR C
                          WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                            AND C.COD_EMPRESA = (SELECT U.COD_EMPRESA
                                                 FROM PUBLIC.UNIDADE U
                                                 WHERE U.CODIGO = F_COD_UNIDADE_ITEM_OS)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF "%s" n??o encontra-se cadastrado no ProLog',
                                                  PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
    END IF;

    -- Validamos se a placa ?? a mesma do item pendente mapeado no ProLog.
    IF ((SELECT PLACA_VEICULO_OS
         FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
         WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
           AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
           AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != F_PLACA_VEICULO_ITEM_OS)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('A placa "%s" n??o bate com a placa do item pendente "%s" do ProLog',
                               F_PLACA_VEICULO_ITEM_OS,
                               V_COD_ITEM_RESOLVIDO_PROLOG));
    END IF;

    -- Depois de validar podemos resolver o item.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET CPF_MECANICO               = F_CPF_COLABORADOR_RESOLUCAO,
        KM                         = F_KM_COLETADO_RESOLUCAO,
        STATUS_RESOLUCAO           = 'R',
        TEMPO_REALIZACAO           = F_DURACAO_RESOLUCAO_MS,
        DATA_HORA_CONSERTO         = F_DATA_HORA_RESOLVIDO_PROLOG,
        FEEDBACK_CONSERTO          = F_FEEDBACK_RESOLUCAO,
        DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
        DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO
    WHERE CODIGO = V_COD_ITEM_RESOLVIDO_PROLOG;

    -- O ATRIBUTO 'ROW_COUNT' CONTER?? A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
    -- IR?? RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
    GET DIAGNOSTICS V_QTD_ROWS_ITENS_OS = ROW_COUNT;

    -- SE AP??S O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA QUE O UPDATE N??O EXECUTOU CORRETAMENTE.
    -- LAN??AMOS AQUI UMA EXCE????O PARA RASTREAR ESSE ERRO.
    IF V_QTD_ROWS_ITENS_OS <= 0
    THEN
        RAISE EXCEPTION 'N??o foi poss??vel resolver o item do ProLog "%s"', V_COD_ITEM_RESOLVIDO_PROLOG;
    END IF;

    -- Vamos fechar a O.S. caso todos os itens dela j?? estejam resolvidos.
    UPDATE CHECKLIST_ORDEM_SERVICO_DATA
    SET STATUS               = 'F',
        DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
    WHERE CODIGO = F_COD_OS_GLOBUS
      AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
      AND (SELECT COUNT(*)
           FROM PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS COSI
           WHERE COSI.COD_OS = F_COD_OS_GLOBUS
             AND COSI.COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
             AND COSI.STATUS_RESOLUCAO = 'P') = 0;

    -- Para finalizar, atualizamos a tabela de v??nculo marcando o item como resolvido.
    UPDATE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
    SET DATA_HORA_SINCRONIA_RESOLUCAO = F_DATA_HORA_SINCRONIA_RESOLUCAO
    WHERE COD_ITEM_OS_PROLOG = V_COD_ITEM_RESOLVIDO_PROLOG
      AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS;

    GET DIAGNOSTICS V_QTD_ROWS_VINCULOS_ALTERADOS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE ITENS RESOLVIDOS NA INTEGRA????O OCORREU COM ??XITO.
    IF V_QTD_ROWS_VINCULOS_ALTERADOS <= 0
    THEN
        RAISE EXCEPTION
            'N??o foi poss??vel inserir o item do ProLog resolvido na tabela de mapeamento, item "%s"',
            V_COD_ITEM_RESOLVIDO_PROLOG;
    END IF;

    RETURN V_QTD_ROWS_ITENS_OS;
END;
$$;