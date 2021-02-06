create or replace function migration_checklist.func_migration_13_atualiza_os_integracao()
    returns void
    language plpgsql
as
$func$
begin
    DROP FUNCTION INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(BIGINT, TEXT);
    CREATE OR REPLACE FUNCTION
        INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO BIGINT,
                                                          F_TOKEN_INTEGRACAO TEXT)
        RETURNS TABLE
                (
                    PLACA_VEICULO                      TEXT,
                    KM_ABERTURA_SERVICO                BIGINT,
                    COD_ORDEM_SERVICO                  BIGINT,
                    COD_UNIDADE_ORDEM_SERVICO          BIGINT,
                    STATUS_ORDEM_SERVICO               TEXT,
                    DATA_HORA_ABERTURA_SERVICO         TIMESTAMP WITHOUT TIME ZONE,
                    COD_ITEM_ORDEM_SERVICO             BIGINT,
                    COD_UNIDADE_ITEM_ORDEM_SERVICO     BIGINT,
                    DATA_HORA_PRIMEIRO_APONTAMENTO     TIMESTAMP WITHOUT TIME ZONE,
                    STATUS_ITEM_ORDEM_SERVICO          TEXT,
                    PRAZO_RESOLUCAO_ITEM_HORAS         INTEGER,
                    QTD_APONTAMENTOS                   INTEGER,
                    COD_CHECKLIST_PRIMEIRO_APONTAMENTO BIGINT,
                    COD_CONTEXTO_PERGUNTA              BIGINT,
                    DESCRICAO_PERGUNTA                 TEXT,
                    COD_CONTEXTO_ALTERNATIVA           BIGINT,
                    DESCRICAO_ALTERNATIVA              TEXT,
                    IS_TIPO_OUTROS                     BOOLEAN,
                    DESCRICAO_TIPO_OUTROS              TEXT,
                    PRIORIDADE_ALTERNATIVA             TEXT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE TEXT := 'P';
    BEGIN
        RETURN QUERY
            SELECT CD.PLACA_VEICULO::TEXT                               AS PLACA_VEICULO,
                   CD.KM_VEICULO                                        AS KM_ABERTURA_SERVICO,
                   COSD.CODIGO                                          AS COD_ORDEM_SERVICO,
                   COSD.COD_UNIDADE                                     AS COD_UNIDADE_ORDEM_SERVICO,
                   COSD.STATUS::TEXT                                    AS STATUS_ORDEM_SERVICO,
                   CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_ABERTURA_SERVICO,
                   COSID.CODIGO                                         AS COD_ITEM_ORDEM_SERVICO,
                   COSID.COD_UNIDADE                                    AS COD_UNIDADE_ITEM_ORDEM_SERVICO,
                   CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_PRIMEIRO_APONTAMENTO,
                   COSID.STATUS_RESOLUCAO::TEXT                         AS STATUS_ITEM_ORDEM_SERVICO,
                   CAP.PRAZO                                            AS PRAZO_RESOLUCAO_ITEM_HORAS,
                   COSID.QT_APONTAMENTOS                                AS QTD_APONTAMENTOS,
                   CD.CODIGO                                            AS COD_CHECKLIST_PRIMEIRO_APONTAMENTO,
                   COSID.COD_CONTEXTO_PERGUNTA                          AS COD_CONTEXTO_PERGUNTA,
                   CPD.PERGUNTA                                         AS DESCRICAO_PERGUNTA,
                   COSID.COD_CONTEXTO_ALTERNATIVA                       AS COD_CONTEXTO_ALTERNATIVA,
                   CAPD.ALTERNATIVA                                     AS DESCRICAO_ALTERNATIVA,
                   CAPD.ALTERNATIVA_TIPO_OUTROS                         AS IS_TIPO_OUTROS,
                   CASE
                       WHEN CAPD.ALTERNATIVA_TIPO_OUTROS
                           THEN
                           (SELECT CRN.RESPOSTA_OUTROS
                            FROM CHECKLIST_RESPOSTAS_NOK CRN
                            WHERE CRN.COD_CHECKLIST = CD.CODIGO
                              AND CRN.COD_ALTERNATIVA = CAPD.CODIGO)
                       ELSE
                           NULL
                       END                                              AS DESCRICAO_TIPO_OUTROS,
                   CAPD.PRIORIDADE::TEXT                                AS PRIORIDADE_ALTERNATIVA
            FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                     JOIN CHECKLIST_ORDEM_SERVICO_DATA COSD
                          ON COSID.COD_OS = COSD.CODIGO AND COSID.COD_UNIDADE = COSD.COD_UNIDADE
                     JOIN CHECKLIST_DATA CD ON COSD.COD_CHECKLIST = CD.CODIGO
                     JOIN CHECKLIST_PERGUNTAS_DATA CPD ON COSID.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CPD.CODIGO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
                          ON COSID.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAPD.CODIGO
                     JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE CAP ON CAPD.PRIORIDADE = CAP.PRIORIDADE
            WHERE COSID.COD_UNIDADE IN (SELECT U.CODIGO
                                        FROM UNIDADE U
                                        WHERE U.COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
              AND COSID.STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE
              AND COSID.CODIGO > F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO
            ORDER BY COSID.CODIGO;
    END;
    $$;

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
        -- Antes de realizar o processo de fechamento de Item de Ordem de Serviço, validamos os dados e vínculos
        -- 1° - Validamos se existe a O.S na unidade.
        IF (SELECT NOT EXISTS(
                SELECT COS.CODIGO
                FROM CHECKLIST_ORDEM_SERVICO COS
                WHERE COS.CODIGO = F_COD_ORDEM_SERVICO
                  AND COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('A Ordem de Serviço "%s" não existe na unidade "%s" do ProLog',
                                   F_COD_ORDEM_SERVICO,
                                   F_COD_UNIDADE_ORDEM_SERVICO));
        END IF;

        -- 2° - Validamos se a O.S. está ABERTA.
        -- Apenas validamos se não devemos sobrescrever os dados, caso devemos sobrescrever então não tem necessidade
        -- de validar essa informação pois se a O.S. já está fechada, apenas continuará.
        IF (NOT F_DEVE_SOBRESCREVER_DADOS AND (SELECT COS.STATUS
                                               FROM CHECKLIST_ORDEM_SERVICO COS
                                               WHERE COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                                                 AND COS.CODIGO = F_COD_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_FECHADA)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('A Ordem de Serviço "%s" já está fechada no ProLog', F_COD_ORDEM_SERVICO));
        END IF;

        -- 3° - Validamos se o Item a ser resolvido pertence a O.S..
        IF ((SELECT COSI.COD_OS
             FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
             WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) != F_COD_ORDEM_SERVICO)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O Item "%s" não pertence à O.S "%s" do ProLog',
                                                      F_COD_ITEM_RESOLVIDO,
                                                      F_COD_ORDEM_SERVICO));
        END IF;

        -- 4° - Validamos se o Item da O.S. está PENDENTE. Apenas validamos se não devemos sobrescrever as informações.
        IF (NOT F_DEVE_SOBRESCREVER_DADOS AND (SELECT COSI.STATUS_RESOLUCAO
                                               FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                               WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) !=
                                              F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('O Item "%s" da O.S "%s" já está resolvido no ProLog',
                                   F_COD_ITEM_RESOLVIDO,
                                   F_COD_ORDEM_SERVICO));
        END IF;

        -- 5° - Validamos se o CPF está presente na empresa.
        IF (SELECT NOT EXISTS(SELECT C.CODIGO
                              FROM COLABORADOR C
                              WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                                AND C.COD_EMPRESA =
                                    (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORDEM_SERVICO)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF %s não encontra-se cadastrado no ProLog',
                                                      PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
        END IF;

        -- Por segurança, verificamos se a integração está fechando os itens de O.S. que pertencem a empresa correta.
        IF (F_COD_UNIDADE_ORDEM_SERVICO NOT IN (SELECT CODIGO
                                                FROM UNIDADE
                                                WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                                     FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                     WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            'Você está tentando fechar um item de uma OS que não pertence à sua empresa');
        END IF;

        -- Não verificamos a DATA_HORA_CONSERTO no WHERE pois não nos importa se está fechada ou não. Se chegou nesse ponto
        -- iremos atualizar as informações afim de fechar o item ou afim de sobrescrever as informações. Temos segurança de
        -- fazer isso pois o 4º IF verifica se o item já está fechado porém não devemos sobrescrever.
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

        -- O atributo 'ROW_COUNT' conterá a quantidade de linhas que foram atualizadas pelo update acima. a funciton
        -- irá retornar esse atributo para que possamos validar se todos os updates aconteceram como deveriam.
        GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

        -- O primeiro if verifica se o item está pendente, se após o update nenhuma linha for alterada, significa
        -- que o update não executou corretamente. lançamos aqui uma exceção para rastrear esse erro
        IF F_QTD_ROWS_ITENS_OS <= 0
        THEN
            RAISE EXCEPTION 'Não foi possível resolver o item %', F_COD_ITEM_RESOLVIDO;
        END IF;

        -- Ao resolver um item de ordem de serviço é necessário verificar se a Ordem de Serviço foi finalizada.
        -- uma O.S. fechada consiste em uma O.S. que possui todos os seus itens resolvidos.
        UPDATE CHECKLIST_ORDEM_SERVICO
        SET STATUS               = F_STATUS_ORDEM_SERVICO_FECHADA,
            DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
        WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
          AND CODIGO = F_COD_ORDEM_SERVICO
          -- Utilizamos essa verificação para forçar que o update aconteça apenas se a O.S. tiver
          -- todos seus itens resolvidos.
          AND (SELECT COUNT(*)
               FROM CHECKLIST_ORDEM_SERVICO_ITENS
               WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                 AND COD_OS = F_COD_ORDEM_SERVICO
                 AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0;

        -- Para garantir a consistência do processo de resolução de itens e fechamento de ordens de serviço
        -- verificamos se a O.S. que possuiu seu item fechado está com o 'status' correto.
        IF (((SELECT COUNT(*)
              FROM CHECKLIST_ORDEM_SERVICO_ITENS
              WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                AND COD_OS = F_COD_ORDEM_SERVICO
                AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0)
            AND (SELECT STATUS
                 FROM CHECKLIST_ORDEM_SERVICO
                 WHERE CODIGO = F_COD_ORDEM_SERVICO
                   AND COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_ABERTA)
            -- Caso a O.S. não tem nenhum item pendente mas o seu status é 'aberta', então temos dados inconsistentes.
        THEN
            RAISE EXCEPTION 'Não foi possível fechar a Ordem de Serviço %', F_COD_ORDEM_SERVICO;
        END IF;

        -- Após realizar o processo de fechamento, inserimos o item resolvido na tabela de mapeamento de itens resolvidos
        -- através da integração.
        IF (F_DEVE_SOBRESCREVER_DADOS)
        THEN
            -- Se devemos sobrescrever as informações, eventualmente, já teremos fechado o item e este já está mapeado na
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
            -- Caso não é para sobrescrever, temos que estar atentos às constraints, evitando que tente-se inserir um
            -- item que já foi fechado previamente.
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

        -- Verificamos se o insert na tabela de mapeamento de itens resolvidos na integração ocorreu com êxito.
        IF F_QTD_ROWS_ITENS_RESOLVIDOS_OS <= 0
        THEN
            RAISE EXCEPTION
                'Não foi possível inserir o item resolvido na tabela de mapeamento, item %', F_COD_ITEM_RESOLVIDO;
        END IF;

        RETURN F_QTD_ROWS_ITENS_OS;
    END;
    $$;

    ALTER TABLE PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS
        RENAME COD_PERGUNTA TO COD_CONTEXTO_PERGUNTA;
    ALTER TABLE PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS
        RENAME COD_ALTERNATIVA TO COD_CONTEXTO_ALTERNATIVA;
    ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
        RENAME COD_PERGUNTA_OS_PROLOG TO COD_CONTEXTO_PERGUNTA_OS_PROLOG;
    ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
        RENAME COD_ALTERNATIVA_OS_PROLOG TO COD_CONTEXTO_ALTERNATIVA_OS_PROLOG;

    DROP FUNCTION PICCOLOTUR.FUNC_CHECK_OS_INSERE_ITEM_OS_ABERTA(BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, TIMESTAMP WITH TIME ZONE, TEXT);
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
        STATUS_OS_ABERTA        TEXT   := 'A';
        STATUS_OS_FECHADA       TEXT   := 'F';
        STATUS_ITEM_OS_PENDENTE TEXT   := 'P';
        CODIGO_PERGUNTA         BIGINT := (SELECT CP.CODIGO
                                           FROM CHECKLIST_PERGUNTAS CP
                                           WHERE CP.CODIGO_CONTEXTO = F_COD_CONTEXTO_PERGUNTA_CHECKLIST
                                             AND CP.COD_VERSAO_CHECKLIST_MODELO =
                                                 (SELECT C.COD_VERSAO_CHECKLIST_MODELO
                                                  FROM CHECKLIST C
                                                  WHERE C.CODIGO = F_COD_CHECKLIST));
        CODIGO_ALTERNATIVA      BIGINT := (SELECT CAP.CODIGO
                                           FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                                           WHERE CAP.CODIGO_CONTEXTO = F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST
                                             AND CAP.COD_PERGUNTA = CODIGO_PERGUNTA
                                             AND CAP.COD_VERSAO_CHECKLIST_MODELO = (SELECT C.COD_VERSAO_CHECKLIST_MODELO
                                                                                    FROM CHECKLIST C
                                                                                    WHERE C.CODIGO = F_COD_CHECKLIST));
        COD_ITEM_OS_PROLOG      BIGINT;
    BEGIN
        -- Antes de processarmos a abertura da O.S e inserção de Itens, validamos todos os códigos de vínculo.
        -- Validamos se o código da unidade da O.S bate com a empresa do Token
        IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_OS)
            <>
            (SELECT TI.COD_EMPRESA
             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s"',
                                   F_TOKEN_INTEGRACAO,
                                   F_COD_UNIDADE_OS));
        END IF;

        -- Validamos se o Item da Ordem de Serviço já existe no ProLog.
        IF (SELECT EXISTS(
                           SELECT *
                           FROM PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS
                           WHERE COD_OS = F_COD_OS_GLOBUS
                             AND COD_UNIDADE = F_COD_UNIDADE_OS
                             AND CODIGO = F_COD_ITEM_OS_GLOBUS))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        -- TODO - Será que devemos lançar o erro, incrementar a quantidade, ou não fazer nada?
                            FORMAT('[ERRO DE STATUS] O serviço "%s" já está existe na O.S "%s" do ProLog',
                                   F_COD_ITEM_OS_GLOBUS,
                                   F_COD_OS_GLOBUS));
        END IF;

        -- Validamos se a Ordem de Serviço já está fechada. Se estiver aberta, iremos adicionar o item nela.
        IF ((SELECT COS.STATUS
             FROM PUBLIC.CHECKLIST_ORDEM_SERVICO COS
             WHERE COS.CODIGO = F_COD_OS_GLOBUS
               AND COS.COD_UNIDADE = F_COD_UNIDADE_OS) = STATUS_OS_FECHADA)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        -- TODO - Será que devemos lançar um erro, ou ignorar pois já está fechada, ou reabrir e inserir o item?
                            FORMAT('[ERRO DE STATUS] A O.S "%s" já está FECHADA no ProLog', F_COD_OS_GLOBUS));
        END IF;

        -- Validamos se o código do checklist existe.
        IF (SELECT NOT EXISTS(
                SELECT C.CODIGO
                FROM PUBLIC.CHECKLIST C
                WHERE C.CODIGO = F_COD_CHECKLIST
                  AND C.COD_UNIDADE = F_COD_UNIDADE_OS))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] O checklist "%s" não encontra-se na base de dados do ProLog',
                                   F_COD_CHECKLIST));
        END IF;

        -- Validamos se a pergunta existe e está mesmo vinculada ao checklist realizado.
        IF (SELECT NOT EXISTS(
                SELECT CRN.COD_PERGUNTA
                FROM PUBLIC.CHECKLIST_RESPOSTAS_NOK CRN
                WHERE CRN.COD_CHECKLIST = F_COD_CHECKLIST
                  AND CRN.COD_PERGUNTA = CODIGO_PERGUNTA))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] A pergunta "%s" não possui vínculo com o checklist "%s"',
                                   F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                                   F_COD_CHECKLIST));
        END IF;

        -- Validamos se a alternativa existe e pertence a pergunta do checklist realizado.
        IF (SELECT NOT EXISTS(
                SELECT CRN.COD_ALTERNATIVA
                FROM PUBLIC.CHECKLIST_RESPOSTAS_NOK CRN
                WHERE CRN.COD_CHECKLIST = F_COD_CHECKLIST
                  AND CRN.COD_PERGUNTA = CODIGO_PERGUNTA
                  AND CRN.COD_ALTERNATIVA = CODIGO_ALTERNATIVA))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] A alternativa "%s" não possui vínculo com a pergunta "%s"',
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
                                    '[ERRO DE VÍNCULO] Não existe vínculo entre o cod_checklist "%s", cod_pergunta "%s" e cod_alternativa "%s"',
                                    F_COD_CHECKLIST,
                                    F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                                    F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST));
        END IF;

        -- Se a Ordem de Serviço não existe, então criamos ela.
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
            VALUES (F_COD_OS_GLOBUS, F_COD_UNIDADE_OS, F_COD_CHECKLIST, STATUS_OS_ABERTA);
        END IF;

        -- Não precisamos validar novamente se o item já existe no banco de dados, apenas inserimos.
        INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS(COD_UNIDADE,
                                                         COD_OS,
                                                         STATUS_RESOLUCAO,
                                                         COD_CONTEXTO_PERGUNTA,
                                                         COD_CONTEXTO_ALTERNATIVA,
                                                         COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                                                         COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO)
        VALUES (F_COD_UNIDADE_OS,
                F_COD_OS_GLOBUS,
                STATUS_ITEM_OS_PENDENTE,
                F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
                CODIGO_PERGUNTA,
                CODIGO_ALTERNATIVA)
        RETURNING CODIGO INTO COD_ITEM_OS_PROLOG;

        -- Não chegará nesse ponto um 'item', 'checklist' ou 'alternativa' que não existam, então podemos inserir os
        -- dados com segurança. Também, não chegará aqui um item que não deveremos inserir ou que devemos aumentar a
        -- quantidade de apontamentos, nesse estágio o item SEMPRE tera 'NOVA_QTD_APONTAMENTOS' = 1 (primeiro apontamento).
        INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS(COD_ITEM_ORDEM_SERVICO,
                                                                      COD_CHECKLIST_REALIZADO,
                                                                      COD_ALTERNATIVA,
                                                                      NOVA_QTD_APONTAMENTOS)
        VALUES (COD_ITEM_OS_PROLOG, F_COD_CHECKLIST, CODIGO_ALTERNATIVA, 1);

        -- Após salvar o item, criamos o vínculo dele na tabela DE-PARA.
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
                COD_ITEM_OS_PROLOG,
                (SELECT C.PLACA_VEICULO FROM PUBLIC.CHECKLIST C WHERE C.CODIGO = F_COD_CHECKLIST),
                F_COD_CHECKLIST,
                F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
                F_DATA_HORA_SINCRONIZACAO_PENDENCIA);

        RETURN COD_ITEM_OS_PROLOG;
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
        STATUS_ITEM_OS_PENDENTE       TEXT   := 'P';
        COD_ITEM_RESOLVIDO_PROLOG     BIGINT;
        COD_RETORNO_SUCESSO           BIGINT := 1;
        F_QTD_ROWS_ITENS_OS           BIGINT;
        F_QTD_ROWS_VINCULOS_ALTERADOS BIGINT;
    BEGIN
        -- Antes de processarmos a resolução de Itens de O.S., validamos todos os códigos de vínculo possíveis.
        -- Por segurança, verificamos se a integração está fechando os itens de o.s. que pertencem a empresa correta.
        IF (F_COD_UNIDADE_ITEM_OS NOT IN (SELECT CODIGO
                                          FROM PUBLIC.UNIDADE
                                          WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s"',
                                   F_TOKEN_INTEGRACAO,
                                   F_COD_UNIDADE_ITEM_OS));
        END IF;

        -- Validamos se o código do item fechado no Globus, está mapeado no ProLog.
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
            INTO COD_ITEM_RESOLVIDO_PROLOG;
        ELSE
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE VÍNCULO] O item "%s" da O.S. "%s" não possuí vínculo no ProLog',
                                   F_COD_ITEM_RESOLVIDO_GLOBUS,
                                   F_COD_OS_GLOBUS));
        END IF;

        -- Validamos se o item mapeado está pendente no ProLog. Caso já está resolvido, apenas retorna sucesso.
        IF (SELECT COSID.STATUS_RESOLUCAO
            FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO COSIV
                     JOIN CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                          ON COSIV.COD_ITEM_OS_PROLOG = COSID.CODIGO
            WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
              AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != STATUS_ITEM_OS_PENDENTE
        THEN
            RETURN COD_RETORNO_SUCESSO;
        END IF;

        -- Validamos se o usuário está na base de dados do ProLog, podendo estar em qualquer unidade da empresa integrada.
        IF (SELECT NOT EXISTS(SELECT C.CODIGO
                              FROM PUBLIC.COLABORADOR C
                              WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                                AND C.COD_EMPRESA = (SELECT U.COD_EMPRESA
                                                     FROM PUBLIC.UNIDADE U
                                                     WHERE U.CODIGO = F_COD_UNIDADE_ITEM_OS)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF "%s" não encontra-se cadastrado no ProLog',
                                                      PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
        END IF;

        -- Validamos se a placa é a mesma do item pendente mapeado no ProLog.
        IF ((SELECT PLACA_VEICULO_OS
             FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
             WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
               AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
               AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != F_PLACA_VEICULO_ITEM_OS)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('A placa "%s" não bate com a placa do item pendente "%s" do ProLog',
                                   F_PLACA_VEICULO_ITEM_OS,
                                   COD_ITEM_RESOLVIDO_PROLOG));
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
        WHERE CODIGO = COD_ITEM_RESOLVIDO_PROLOG;

        -- O ATRIBUTO 'ROW_COUNT' CONTERÁ A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
        -- IRÁ RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
        GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

        -- SE APÓS O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA QUE O UPDATE NÃO EXECUTOU CORRETAMENTE.
        -- LANÇAMOS AQUI UMA EXCEÇÃO PARA RASTREAR ESSE ERRO.
        IF F_QTD_ROWS_ITENS_OS <= 0
        THEN
            RAISE EXCEPTION 'Não foi possível resolver o item do ProLog "%s"', COD_ITEM_RESOLVIDO_PROLOG;
        END IF;

        -- Vamos fechar a O.S. caso todos os itens dela já estejam resolvidos.
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

        -- Para finalizar, atualizamos a tabela de vínculo marcando o item como resolvido.
        UPDATE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
        SET DATA_HORA_SINCRONIA_RESOLUCAO = F_DATA_HORA_SINCRONIA_RESOLUCAO
        WHERE COD_ITEM_OS_PROLOG = COD_ITEM_RESOLVIDO_PROLOG
          AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS;

        GET DIAGNOSTICS F_QTD_ROWS_VINCULOS_ALTERADOS = ROW_COUNT;

        -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE ITENS RESOLVIDOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
        IF F_QTD_ROWS_VINCULOS_ALTERADOS <= 0
        THEN
            RAISE EXCEPTION
                'Não foi possível inserir o item do ProLog resolvido na tabela de mapeamento, item "%s"',
                COD_ITEM_RESOLVIDO_PROLOG;
        END IF;

        RETURN F_QTD_ROWS_ITENS_OS;
    END;
    $$;

    CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_GET_NEXT_COD_CHECKLIST_PARA_SINCRONIZAR()
        RETURNS TABLE
                (
                    COD_CHECKLIST BIGINT,
                    IS_LAST_COD   BOOLEAN
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        COD_CHECKLIST BIGINT;
        IS_LAST_COD   BOOLEAN;
    BEGIN
        --   1° - verifica se existe um checklist para sincronizar, se não, seta o de menor código como apto a
        --   sincronização.
        IF ((SELECT COD_CHECKLIST_PARA_SINCRONIZAR
             FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
             WHERE NEXT_TO_SYNC IS TRUE
               AND SINCRONIZADO IS FALSE
               AND PRECISA_SER_SINCRONIZADO IS TRUE) IS NULL)
        THEN
            UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
            SET NEXT_TO_SYNC = TRUE
            WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                    FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
                                                    WHERE CPPS.SINCRONIZADO IS FALSE
                                                      AND CPPS.PRECISA_SER_SINCRONIZADO IS TRUE
                                                    ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                    LIMIT 1);
        END IF;

        --   2° - Verifica se o código marcado para sincronizar é o último código a ser sincronizado
        SELECT CPPS.NEXT_TO_SYNC
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
        WHERE CPPS.PRECISA_SER_SINCRONIZADO
          AND CPPS.SINCRONIZADO IS FALSE
        ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR DESC
        LIMIT 1
        INTO IS_LAST_COD;

        --   3° - Pega o código que está marcado para tentar sincronizar
        SELECT COD_CHECKLIST_PARA_SINCRONIZAR
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE NEXT_TO_SYNC = TRUE
        INTO COD_CHECKLIST;

        --   4° - Remove a marcação do checklist que estava marcado par sincronizar
        UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        SET NEXT_TO_SYNC = FALSE
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = COD_CHECKLIST;

        --   5° - Marca o próximo código que precisa ser sincronizado, se for o último código, então seta o
        -- primeiro como o próximo a ser sincronizado
        IF IS_LAST_COD
        THEN
            UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
            SET NEXT_TO_SYNC = TRUE
            WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                    FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
                                                    WHERE CPPS.SINCRONIZADO IS FALSE
                                                      AND CPPS.PRECISA_SER_SINCRONIZADO IS TRUE
                                                    ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                    LIMIT 1);
        ELSE
            UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
            SET NEXT_TO_SYNC = TRUE
            WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                                                    FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                                                    WHERE SINCRONIZADO IS FALSE
                                                      AND PRECISA_SER_SINCRONIZADO IS TRUE
                                                      AND NEXT_TO_SYNC IS FALSE
                                                      AND COD_CHECKLIST_PARA_SINCRONIZAR > COD_CHECKLIST
                                                    ORDER BY COD_CHECKLIST_PARA_SINCRONIZAR
                                                    LIMIT 1);
        END IF;

        --   6° - Retorna o código que será sincronizado
        RETURN QUERY
            SELECT COD_CHECKLIST, IS_LAST_COD;
    END;
    $$;

    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_INCREMENTA_QTD_APONTAMENTOS_ITEM(F_COD_ITEM_ORDEM_SERVICO BIGINT,
                                                                                  F_COD_CHECKLIST_REALIZADO BIGINT,
                                                                                  F_COD_ALTERNATIVA BIGINT,
                                                                                  F_STATUS_RESOLUCAO TEXT)
        RETURNS VOID
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        NOVA_QTD_APONTAMENTOS_ITEM INTEGER;
    BEGIN
        -- Atualiza quantidade de apontamentos do item.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS
        SET QT_APONTAMENTOS = QT_APONTAMENTOS + 1
        WHERE CODIGO = F_COD_ITEM_ORDEM_SERVICO
          AND STATUS_RESOLUCAO = F_STATUS_RESOLUCAO
        RETURNING QT_APONTAMENTOS INTO NOVA_QTD_APONTAMENTOS_ITEM;

        -- Insere a alternativa que incrementou a quantidade de apontamentos na tabela.
        INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS (COD_ITEM_ORDEM_SERVICO,
                                                                COD_CHECKLIST_REALIZADO,
                                                                COD_ALTERNATIVA,
                                                                NOVA_QTD_APONTAMENTOS)
        VALUES (F_COD_ITEM_ORDEM_SERVICO, F_COD_CHECKLIST_REALIZADO, F_COD_ALTERNATIVA, NOVA_QTD_APONTAMENTOS_ITEM);
    END;
    $$;

    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_INSERE_ITEM_OS(F_COD_UNIDADE BIGINT,
                                                                F_COD_OS BIGINT,
                                                                F_COD_PERGUNTA_PRIMEIRO_APONTAMENTO BIGINT,
                                                                F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO BIGINT,
                                                                F_STATUS_RESOLUCAO TEXT,
                                                                F_COD_CONTEXTO_PERGUNTA BIGINT,
                                                                F_COD_CONTEXTO_ALTERNATIVA BIGINT,
                                                                F_COD_CHECKLIST_REALIZADO BIGINT)
        RETURNS BIGINT
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        NOVA_QTD_APONTAMENTOS_ITEM INTEGER := 1;
        CODIGO_ITEM_OS             BIGINT;
    BEGIN
        -- Insere o item da O.S
        INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS(COD_UNIDADE,
                                                  COD_OS,
                                                  COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                                                  COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                                                  STATUS_RESOLUCAO,
                                                  COD_CONTEXTO_PERGUNTA,
                                                  COD_CONTEXTO_ALTERNATIVA)
        VALUES (F_COD_UNIDADE,
                F_COD_OS,
                F_COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                F_STATUS_RESOLUCAO,
                F_COD_CONTEXTO_PERGUNTA,
                F_COD_CONTEXTO_ALTERNATIVA)
        RETURNING CODIGO INTO CODIGO_ITEM_OS;

        -- Insere a alternativa que incrementou a quantidade de apontamentos na tabela.
        INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS (COD_ITEM_ORDEM_SERVICO,
                                                                COD_CHECKLIST_REALIZADO,
                                                                COD_ALTERNATIVA,
                                                                NOVA_QTD_APONTAMENTOS)
        VALUES (CODIGO_ITEM_OS,
                F_COD_CHECKLIST_REALIZADO,
                F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                NOVA_QTD_APONTAMENTOS_ITEM);

        RETURN CODIGO_ITEM_OS;
    END;
    $$;
end;
$func$;