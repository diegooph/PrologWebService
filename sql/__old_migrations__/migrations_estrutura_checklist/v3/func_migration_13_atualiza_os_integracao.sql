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

        -- 2?? - Validamos se a O.S. est?? ABERTA.
        -- Apenas validamos se n??o devemos sobrescrever os dados, caso devemos sobrescrever ent??o n??o tem necessidade
        -- de validar essa informa????o pois se a O.S. j?? est?? fechada, apenas continuar??.
        IF (NOT F_DEVE_SOBRESCREVER_DADOS AND (SELECT COS.STATUS
                                               FROM CHECKLIST_ORDEM_SERVICO COS
                                               WHERE COS.COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                                                 AND COS.CODIGO = F_COD_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_FECHADA)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('A Ordem de Servi??o "%s" j?? est?? fechada no ProLog', F_COD_ORDEM_SERVICO));
        END IF;

        -- 3?? - Validamos se o Item a ser resolvido pertence a O.S..
        IF ((SELECT COSI.COD_OS
             FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
             WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) != F_COD_ORDEM_SERVICO)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O Item "%s" n??o pertence ?? O.S "%s" do ProLog',
                                                      F_COD_ITEM_RESOLVIDO,
                                                      F_COD_ORDEM_SERVICO));
        END IF;

        -- 4?? - Validamos se o Item da O.S. est?? PENDENTE. Apenas validamos se n??o devemos sobrescrever as informa????es.
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

        -- 5?? - Validamos se o CPF est?? presente na empresa.
        IF (SELECT NOT EXISTS(SELECT C.CODIGO
                              FROM COLABORADOR C
                              WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                                AND C.COD_EMPRESA =
                                    (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORDEM_SERVICO)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF %s n??o encontra-se cadastrado no ProLog',
                                                      PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
        END IF;

        -- Por seguran??a, verificamos se a integra????o est?? fechando os itens de O.S. que pertencem a empresa correta.
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
        -- iremos atualizar as informa????es afim de fechar o item ou afim de sobrescrever as informa????es. Temos seguran??a de
        -- fazer isso pois o 4?? IF verifica se o item j?? est?? fechado por??m n??o devemos sobrescrever.
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

        -- O atributo 'ROW_COUNT' conter?? a quantidade de linhas que foram atualizadas pelo update acima. a funciton
        -- ir?? retornar esse atributo para que possamos validar se todos os updates aconteceram como deveriam.
        GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

        -- O primeiro if verifica se o item est?? pendente, se ap??s o update nenhuma linha for alterada, significa
        -- que o update n??o executou corretamente. lan??amos aqui uma exce????o para rastrear esse erro
        IF F_QTD_ROWS_ITENS_OS <= 0
        THEN
            RAISE EXCEPTION 'N??o foi poss??vel resolver o item %', F_COD_ITEM_RESOLVIDO;
        END IF;

        -- Ao resolver um item de ordem de servi??o ?? necess??rio verificar se a Ordem de Servi??o foi finalizada.
        -- uma O.S. fechada consiste em uma O.S. que possui todos os seus itens resolvidos.
        UPDATE CHECKLIST_ORDEM_SERVICO
        SET STATUS               = F_STATUS_ORDEM_SERVICO_FECHADA,
            DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
        WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
          AND CODIGO = F_COD_ORDEM_SERVICO
          -- Utilizamos essa verifica????o para for??ar que o update aconte??a apenas se a O.S. tiver
          -- todos seus itens resolvidos.
          AND (SELECT COUNT(*)
               FROM CHECKLIST_ORDEM_SERVICO_ITENS
               WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                 AND COD_OS = F_COD_ORDEM_SERVICO
                 AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0;

        -- Para garantir a consist??ncia do processo de resolu????o de itens e fechamento de ordens de servi??o
        -- verificamos se a O.S. que possuiu seu item fechado est?? com o 'status' correto.
        IF (((SELECT COUNT(*)
              FROM CHECKLIST_ORDEM_SERVICO_ITENS
              WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
                AND COD_OS = F_COD_ORDEM_SERVICO
                AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0)
            AND (SELECT STATUS
                 FROM CHECKLIST_ORDEM_SERVICO
                 WHERE CODIGO = F_COD_ORDEM_SERVICO
                   AND COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_ABERTA)
            -- Caso a O.S. n??o tem nenhum item pendente mas o seu status ?? 'aberta', ent??o temos dados inconsistentes.
        THEN
            RAISE EXCEPTION 'N??o foi poss??vel fechar a Ordem de Servi??o %', F_COD_ORDEM_SERVICO;
        END IF;

        -- Ap??s realizar o processo de fechamento, inserimos o item resolvido na tabela de mapeamento de itens resolvidos
        -- atrav??s da integra????o.
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

        -- Verificamos se o insert na tabela de mapeamento de itens resolvidos na integra????o ocorreu com ??xito.
        IF F_QTD_ROWS_ITENS_RESOLVIDOS_OS <= 0
        THEN
            RAISE EXCEPTION
                'N??o foi poss??vel inserir o item resolvido na tabela de mapeamento, item %', F_COD_ITEM_RESOLVIDO;
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
        -- Antes de processarmos a abertura da O.S e inser????o de Itens, validamos todos os c??digos de v??nculo.
        -- Validamos se o c??digo da unidade da O.S bate com a empresa do Token
        IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_OS)
            <>
            (SELECT TI.COD_EMPRESA
             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                            FORMAT('[ERRO DE V??NCULO] O token "%s" n??o est?? autorizado a inserir dados da unidade "%s"',
                                   F_TOKEN_INTEGRACAO,
                                   F_COD_UNIDADE_OS));
        END IF;

        -- Validamos se o Item da Ordem de Servi??o j?? existe no ProLog.
        IF (SELECT EXISTS(
                           SELECT *
                           FROM PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS
                           WHERE COD_OS = F_COD_OS_GLOBUS
                             AND COD_UNIDADE = F_COD_UNIDADE_OS
                             AND CODIGO = F_COD_ITEM_OS_GLOBUS))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        -- TODO - Ser?? que devemos lan??ar o erro, incrementar a quantidade, ou n??o fazer nada?
                            FORMAT('[ERRO DE STATUS] O servi??o "%s" j?? est?? existe na O.S "%s" do ProLog',
                                   F_COD_ITEM_OS_GLOBUS,
                                   F_COD_OS_GLOBUS));
        END IF;

        -- Validamos se a Ordem de Servi??o j?? est?? fechada. Se estiver aberta, iremos adicionar o item nela.
        IF ((SELECT COS.STATUS
             FROM PUBLIC.CHECKLIST_ORDEM_SERVICO COS
             WHERE COS.CODIGO = F_COD_OS_GLOBUS
               AND COS.COD_UNIDADE = F_COD_UNIDADE_OS) = STATUS_OS_FECHADA)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        -- TODO - Ser?? que devemos lan??ar um erro, ou ignorar pois j?? est?? fechada, ou reabrir e inserir o item?
                            FORMAT('[ERRO DE STATUS] A O.S "%s" j?? est?? FECHADA no ProLog', F_COD_OS_GLOBUS));
        END IF;

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
                  AND CRN.COD_PERGUNTA = CODIGO_PERGUNTA))
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
                  AND CRN.COD_PERGUNTA = CODIGO_PERGUNTA
                  AND CRN.COD_ALTERNATIVA = CODIGO_ALTERNATIVA))
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
                                    '[ERRO DE V??NCULO] N??o existe v??nculo entre o cod_checklist "%s", cod_pergunta "%s" e cod_alternativa "%s"',
                                    F_COD_CHECKLIST,
                                    F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                                    F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST));
        END IF;

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
            VALUES (F_COD_OS_GLOBUS, F_COD_UNIDADE_OS, F_COD_CHECKLIST, STATUS_OS_ABERTA);
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
                STATUS_ITEM_OS_PENDENTE,
                F_COD_CONTEXTO_PERGUNTA_CHECKLIST,
                F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST,
                CODIGO_PERGUNTA,
                CODIGO_ALTERNATIVA)
        RETURNING CODIGO INTO COD_ITEM_OS_PROLOG;

        -- N??o chegar?? nesse ponto um 'item', 'checklist' ou 'alternativa' que n??o existam, ent??o podemos inserir os
        -- dados com seguran??a. Tamb??m, n??o chegar?? aqui um item que n??o deveremos inserir ou que devemos aumentar a
        -- quantidade de apontamentos, nesse est??gio o item SEMPRE tera 'NOVA_QTD_APONTAMENTOS' = 1 (primeiro apontamento).
        INSERT INTO PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS(COD_ITEM_ORDEM_SERVICO,
                                                                      COD_CHECKLIST_REALIZADO,
                                                                      COD_ALTERNATIVA,
                                                                      NOVA_QTD_APONTAMENTOS)
        VALUES (COD_ITEM_OS_PROLOG, F_COD_CHECKLIST, CODIGO_ALTERNATIVA, 1);

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
        -- Antes de processarmos a resolu????o de Itens de O.S., validamos todos os c??digos de v??nculo poss??veis.
        -- Por seguran??a, verificamos se a integra????o est?? fechando os itens de o.s. que pertencem a empresa correta.
        IF (F_COD_UNIDADE_ITEM_OS NOT IN (SELECT CODIGO
                                          FROM PUBLIC.UNIDADE
                                          WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
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
            INTO COD_ITEM_RESOLVIDO_PROLOG;
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
              AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != STATUS_ITEM_OS_PENDENTE
        THEN
            RETURN COD_RETORNO_SUCESSO;
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

        -- O ATRIBUTO 'ROW_COUNT' CONTER?? A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
        -- IR?? RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
        GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

        -- SE AP??S O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA QUE O UPDATE N??O EXECUTOU CORRETAMENTE.
        -- LAN??AMOS AQUI UMA EXCE????O PARA RASTREAR ESSE ERRO.
        IF F_QTD_ROWS_ITENS_OS <= 0
        THEN
            RAISE EXCEPTION 'N??o foi poss??vel resolver o item do ProLog "%s"', COD_ITEM_RESOLVIDO_PROLOG;
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
        WHERE COD_ITEM_OS_PROLOG = COD_ITEM_RESOLVIDO_PROLOG
          AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS;

        GET DIAGNOSTICS F_QTD_ROWS_VINCULOS_ALTERADOS = ROW_COUNT;

        -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE ITENS RESOLVIDOS NA INTEGRA????O OCORREU COM ??XITO.
        IF F_QTD_ROWS_VINCULOS_ALTERADOS <= 0
        THEN
            RAISE EXCEPTION
                'N??o foi poss??vel inserir o item do ProLog resolvido na tabela de mapeamento, item "%s"',
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
        --   1?? - verifica se existe um checklist para sincronizar, se n??o, seta o de menor c??digo como apto a
        --   sincroniza????o.
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

        --   2?? - Verifica se o c??digo marcado para sincronizar ?? o ??ltimo c??digo a ser sincronizado
        SELECT CPPS.NEXT_TO_SYNC
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
        WHERE CPPS.PRECISA_SER_SINCRONIZADO
          AND CPPS.SINCRONIZADO IS FALSE
        ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR DESC
        LIMIT 1
        INTO IS_LAST_COD;

        --   3?? - Pega o c??digo que est?? marcado para tentar sincronizar
        SELECT COD_CHECKLIST_PARA_SINCRONIZAR
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE NEXT_TO_SYNC = TRUE
        INTO COD_CHECKLIST;

        --   4?? - Remove a marca????o do checklist que estava marcado par sincronizar
        UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        SET NEXT_TO_SYNC = FALSE
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = COD_CHECKLIST;

        --   5?? - Marca o pr??ximo c??digo que precisa ser sincronizado, se for o ??ltimo c??digo, ent??o seta o
        -- primeiro como o pr??ximo a ser sincronizado
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

        --   6?? - Retorna o c??digo que ser?? sincronizado
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