BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
-- Altera nome de coluna COD_PERGUNTA e COD_ALTERNATIVA para COD_CONTEXTO_PERGUNTA e COD_CONTEXTO_ALTERNATIVA,
-- respectivamente.
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
--######################################################################################################################
--######################################################################################################################

-- Corrige constraints das tabelas do schema piccolotur
ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
    DROP CONSTRAINT UNIQUE_ITEM_ORDEM_SERVICO_VINCULO_ITEM_PROLOG;
ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
    ADD CONSTRAINT UNIQUE_ITEM_ORDEM_SERVICO_VINCULO_ITEM_PROLOG UNIQUE (COD_UNIDADE, COD_OS_GLOBUS, COD_ITEM_OS_PROLOG);

ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
    DROP CONSTRAINT FK_INTEGRACAO_ITEM_VINCULO_ITEM_NOK_ENVIADO_GLOBUS;
ALTER TABLE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
    ADD CONSTRAINT FK_INTEGRACAO_ITEM_VINCULO_ITEM_NOK_ENVIADO_GLOBUS
        FOREIGN KEY (COD_CHECKLIST_OS_PROLOG, COD_CONTEXTO_PERGUNTA_OS_PROLOG, COD_CONTEXTO_ALTERNATIVA_OS_PROLOG)
            REFERENCES PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS (COD_CHECKLIST, COD_CONTEXTO_PERGUNTA, COD_CONTEXTO_ALTERNATIVA);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Corrige query de abertura de Item de OS.
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
    -- Nesse caso, não podemos incrementar a quantidade de apontamentos pois, o incremento é feito em JAVA, quando
    -- enviamos os itens NOK para o Globus. Em teoria, deveria chegar para o ProLog apenas novos itens de OS.
    IF (SELECT EXISTS(SELECT *
                      FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO COSIV
                      WHERE COSIV.COD_UNIDADE = F_COD_UNIDADE_OS
                        AND COSIV.COD_OS_GLOBUS = F_COD_OS_GLOBUS
                        AND COSIV.COD_ITEM_OS_GLOBUS = F_COD_ITEM_OS_GLOBUS))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE STATUS] O serviço "%s" já está existe na O.S "%s" do ProLog',
                               F_COD_ITEM_OS_GLOBUS,
                               F_COD_OS_GLOBUS));
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

    -- Se chegou nesse estágio, já validamos todos os cenários do item, devemos então inserir.
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
    ELSE
        -- Caso a OS estiver fechada, iremos reabrir para inserir o novo item.
        -- Se estiver aberta, iremos apenas adicionar o item nela.
        UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO
        SET STATUS               = STATUS_OS_ABERTA,
            DATA_HORA_FECHAMENTO = NULL
        WHERE CODIGO = F_COD_OS_GLOBUS
          AND COD_UNIDADE = F_COD_UNIDADE_OS;
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
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PLI-66
CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_BUSCA_CHECKLIST_ITENS_NOK(F_COD_CHECKLIST_PROLOG BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_CHECKLIST        BIGINT,
                COD_MODELO_CHECKLIST         BIGINT,
                COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                CPF_COLABORADOR_REALIZACAO   TEXT,
                PLACA_VEICULO_CHECKLIST      TEXT,
                KM_COLETADO_CHECKLIST        BIGINT,
                TIPO_CHECKLIST               TEXT,
                DATA_HORA_REALIZACAO         TIMESTAMP WITHOUT TIME ZONE,
                TOTAL_ALTERNATIVAS_NOK       INTEGER,
                COD_CONTEXTO_PERGUNTA_NOK    BIGINT,
                DESCRICAO_PERGUNTA_NOK       TEXT,
                COD_ALTERNATIVA_NOK          BIGINT,
                COD_CONTEXTO_ALTERNATIVA_NOK BIGINT,
                DESCRICAO_ALTERNATIVA_NOK    TEXT,
                PRIORIDADE_ALTERNATIVA_NOK   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.COD_UNIDADE                                                           AS COD_UNIDADE_CHECKLIST,
       C.COD_CHECKLIST_MODELO                                                  AS COD_MODELO_CHECKLIST,
       C.COD_VERSAO_CHECKLIST_MODELO                                           AS COD_VERSAO_MODELO_CHECKLIST,
       LPAD(C.CPF_COLABORADOR::TEXT, 11, '0')                                  AS CPF_COLABORADOR_REALIZACAO,
       C.PLACA_VEICULO::TEXT                                                   AS PLACA_VEICULO_CHECKLIST,
       C.KM_VEICULO                                                            AS KM_COLETADO_CHECKLIST,
       F_IF(C.TIPO::TEXT = 'S'::TEXT, 'SAIDA'::TEXT, 'RETORNO'::TEXT)          AS TIPO_CHECKLIST,
       C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)                      AS DATA_HORA_REALIZACAO,
       C.TOTAL_ALTERNATIVAS_NOK::INTEGER                                       AS TOTAL_ALTERNATIVAS_NOK,
       CP.CODIGO_CONTEXTO                                                      AS COD_CONTEXTO_PERGUNTA_NOK,
       CP.PERGUNTA                                                             AS DESCRICAO_PERGUNTA_NOK,
       CAP.CODIGO                                                              AS COD_ALTERNATIVA_NOK,
       CAP.CODIGO_CONTEXTO                                                     AS COD_CONTEXTO_ALTERNATIVA_NOK,
       F_IF(CAP.ALTERNATIVA_TIPO_OUTROS, CRN.RESPOSTA_OUTROS, CAP.ALTERNATIVA) AS DESCRICAO_ALTERNATIVA_NOK,
       CAP.PRIORIDADE                                                          AS PRIORIDADE_ALTERNATIVA_NOK
FROM CHECKLIST C
         -- Usamos LEFT JOIN para os cenários onde o check não possuir nenhum item NOK, devemos retornar as infos do
         -- checklist mesmo assim.
         LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN ON C.CODIGO = CRN.COD_CHECKLIST
         LEFT JOIN CHECKLIST_PERGUNTAS CP ON CRN.COD_PERGUNTA = CP.CODIGO
    AND C.COD_VERSAO_CHECKLIST_MODELO = CP.COD_VERSAO_CHECKLIST_MODELO
         LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CRN.COD_ALTERNATIVA = CAP.CODIGO
    AND C.COD_VERSAO_CHECKLIST_MODELO = CAP.COD_VERSAO_CHECKLIST_MODELO
WHERE C.CODIGO = F_COD_CHECKLIST_PROLOG;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;