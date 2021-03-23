-- #####################################################################################################################
-- #####################################################################################################################
-- ######################################## CRIA FUNC_PNEU_REMOVE_VINCULO_PNEU #########################################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2502
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_REMOVE_VINCULO_PNEU(F_CPF_SOLICITANTE BIGINT,
                                                                 F_COD_UNIDADE BIGINT,
                                                                 F_PLACA_VEICULO TEXT,
                                                                 F_LISTA_PNEUS VARCHAR[],
                                                                 OUT AVISO_PNEUS_DESVINCULADOS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    STATUS_PNEU_ESTOQUE              TEXT                     := 'ESTOQUE';
    STATUS_PNEU_EM_USO               TEXT                     := 'EM_USO';
    DATA_HORA_ATUAL                  TIMESTAMP WITH TIME ZONE := NOW();
    COD_CLIENTE_PNEU_DA_VEZ          TEXT;
    COD_PNEU_INFORMADO               BIGINT;
    COD_MOVIMENTACAO_CRIADA          BIGINT;
    COD_PROCESSO_MOVIMENTACAO_CRIADO BIGINT;
    VIDA_ATUAL_PNEU                  BIGINT;
    POSICAO_PNEU                     INTEGER;
    KM_ATUAL_VEICULO                 BIGINT                   := (SELECT V.KM
                                                                  FROM VEICULO V
                                                                  WHERE V.COD_UNIDADE = F_COD_UNIDADE
                                                                    AND V.PLACA = F_PLACA_VEICULO);
    NOME_COLABORADOR                 TEXT                     := (SELECT C.NOME
                                                                  FROM COLABORADOR C
                                                                  WHERE C.CPF = F_CPF_SOLICITANTE);
BEGIN
    -- VERIFICA SE COLABORADOR POSSUI INTEGRIDADE COM UNIDADE;
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE, F_CPF_SOLICITANTE);

    -- VERIFICA SE UNIDADE EXISTE;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO EXISTE;
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_VEICULO);

    -- VERIFICA QUANTIADE DE PNEUS RECEBIDA;
    IF (ARRAY_LENGTH(F_LISTA_PNEUS, 1) > 0)
    THEN
        -- CRIA PROCESSO PARA MOVIMENTAÇÃO
        INSERT INTO MOVIMENTACAO_PROCESSO(COD_UNIDADE, DATA_HORA, CPF_RESPONSAVEL, OBSERVACAO)
        VALUES (F_COD_UNIDADE,
                DATA_HORA_ATUAL,
                F_CPF_SOLICITANTE,
                'Processo para desvincular o pneu de uma placa')
        RETURNING CODIGO INTO COD_PROCESSO_MOVIMENTACAO_CRIADO;

        FOREACH COD_CLIENTE_PNEU_DA_VEZ IN ARRAY F_LISTA_PNEUS
            LOOP
                -- COLETA O COD_PNEU REFERENTE AO CODIGO_CLIENTE INFORMADO;
                COD_PNEU_INFORMADO = (SELECT P.CODIGO
                                      FROM PNEU P
                                      WHERE P.COD_UNIDADE = F_COD_UNIDADE
                                        AND P.CODIGO_CLIENTE = COD_CLIENTE_PNEU_DA_VEZ);

                IF COD_PNEU_INFORMADO IS NULL
                THEN
                    RAISE EXCEPTION 'Erro! O pneu % não foi encontrado na unidade %',
                        COD_CLIENTE_PNEU_DA_VEZ, F_COD_UNIDADE;
                END IF;

                -- VERIFICA SE PNEU NÃO ESTÁ VINCULADO A PLACA INFORMADA;
                IF NOT EXISTS(SELECT VP.PLACA
                              FROM VEICULO_PNEU VP
                              WHERE VP.PLACA = F_PLACA_VEICULO
                                AND VP.COD_PNEU = COD_PNEU_INFORMADO)
                THEN
                    RAISE EXCEPTION 'Erro! O pneu com código cliente: % não está vinculado ao veículo %',
                        COD_CLIENTE_PNEU_DA_VEZ, F_PLACA_VEICULO;
                END IF;

                -- BUSCA VIDA ATUAL E POSICAO DO PNEU;
                SELECT P.VIDA_ATUAL, VP.POSICAO
                FROM PNEU P
                         JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
                INTO VIDA_ATUAL_PNEU, POSICAO_PNEU;

                IF (COD_PROCESSO_MOVIMENTACAO_CRIADO > 0)
                THEN
                    -- INSERE MOVIMENTAÇÃO RETORNANDO O CÓDIGO DA MESMA;
                    INSERT INTO MOVIMENTACAO(COD_MOVIMENTACAO_PROCESSO,
                                             COD_UNIDADE,
                                             COD_PNEU,
                                             SULCO_INTERNO,
                                             SULCO_CENTRAL_INTERNO,
                                             SULCO_EXTERNO,
                                             VIDA,
                                             OBSERVACAO,
                                             SULCO_CENTRAL_EXTERNO)
                    SELECT COD_PROCESSO_MOVIMENTACAO_CRIADO,
                           F_COD_UNIDADE,
                           COD_PNEU_INFORMADO,
                           P.ALTURA_SULCO_INTERNO,
                           P.ALTURA_SULCO_CENTRAL_INTERNO,
                           P.ALTURA_SULCO_EXTERNO,
                           VIDA_ATUAL_PNEU,
                           NULL,
                           P.ALTURA_SULCO_CENTRAL_EXTERNO
                    FROM PNEU P
                    WHERE P.CODIGO = COD_PNEU_INFORMADO
                    RETURNING CODIGO INTO COD_MOVIMENTACAO_CRIADA;

                    -- INSERE DESTINO DA MOVIMENTAÇÃO;
                    INSERT INTO MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO)
                    VALUES (COD_MOVIMENTACAO_CRIADA, STATUS_PNEU_ESTOQUE);

                    -- INSERE ORIGEM DA MOVIMENTAÇÃO;
                    PERFORM FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_ORIGEM(COD_PNEU_INFORMADO,
                                                                                 F_COD_UNIDADE,
                                                                                 STATUS_PNEU_EM_USO,
                                                                                 COD_MOVIMENTACAO_CRIADA,
                                                                                 F_PLACA_VEICULO,
                                                                                 KM_ATUAL_VEICULO,
                                                                                 POSICAO_PNEU);

                    -- REMOVE PNEU DO VINCULO;
                    DELETE FROM VEICULO_PNEU WHERE COD_PNEU = COD_PNEU_INFORMADO AND PLACA = F_PLACA_VEICULO;

                    -- ATUALIZA STATUS DO PNEU
                    UPDATE PNEU
                    SET STATUS = STATUS_PNEU_ESTOQUE
                    WHERE CODIGO = COD_PNEU_INFORMADO
                      AND COD_UNIDADE = F_COD_UNIDADE;

                    -- VERIFICA SE O PNEU POSSUI SERVIÇOS EM ABERTO;
                    IF EXISTS(SELECT AM.COD_PNEU
                              FROM AFERICAO_MANUTENCAO AM
                              WHERE AM.COD_UNIDADE = F_COD_UNIDADE
                                AND AM.COD_PNEU = COD_PNEU_INFORMADO
                                AND AM.DATA_HORA_RESOLUCAO IS NULL
                                AND AM.CPF_MECANICO IS NULL
                                AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE)
                    THEN
                        -- REMOVE SERVIÇOS EM ABERTO;
                        UPDATE AFERICAO_MANUTENCAO
                        SET FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE,
                            COD_PROCESSO_MOVIMENTACAO            = COD_PROCESSO_MOVIMENTACAO_CRIADO,
                            DATA_HORA_RESOLUCAO                  = DATA_HORA_ATUAL
                        WHERE COD_UNIDADE = F_COD_UNIDADE
                          AND COD_PNEU = COD_PNEU_INFORMADO
                          AND DATA_HORA_RESOLUCAO IS NULL
                          AND CPF_MECANICO IS NULL
                          AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                          AND FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE;
                    END IF;
                ELSE
                    RAISE EXCEPTION 'Erro! Não foi possível realizar o processo de movimentação para o pneu: %',
                        COD_PNEU_INFORMADO;
                END IF;
            END LOOP;
    ELSE
        RAISE EXCEPTION 'Erro! Precisa-se de pelo menos um (1) pneu para realizar a operação!';
    END IF;

    -- MENSAGEM DE SUCESSO;
    SELECT 'Movimentação realizada com sucesso!! Autorizada por ' || NOME_COLABORADOR ||
           ' com CPF: ' || F_CPF_SOLICITANTE || '. Os pneus que estavam na placa ' || F_PLACA_VEICULO ||
           ' foram movidos para estoque.'
    INTO AVISO_PNEUS_DESVINCULADOS;
END
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ################################ ATUALIZA FUNCTION PARA DELETAR VEÍCULO E SUAS DEPENDÊNCIAS #########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2330

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
                                                               F_PLACA VARCHAR(255),
                                                               OUT DEPENDENCIAS_DELETADAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    CODIGO_LOOP                   BIGINT;
    LISTA_COD_AFERICAO_PLACA      BIGINT[];
    LISTA_COD_CHECK_PLACA         BIGINT[];
    LISTA_COD_PROLOG_DELETADO_COS BIGINT[];
    NOME_EMPRESA                  VARCHAR(255) := (SELECT E.NOME
                                                   FROM EMPRESA E
                                                   WHERE E.CODIGO =
                                                         (SELECT U.COD_EMPRESA
                                                          FROM UNIDADE U
                                                          WHERE U.CODIGO = F_COD_UNIDADE));
    NOME_UNIDADE                  VARCHAR(255) := (SELECT U.NOME
                                                   FROM UNIDADE U
                                                   WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    -- VERIFICA SE UNIDADE EXISTE;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO EXISTE.
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);

    -- VERIFICA SE VEÍCULO POSSUI PNEU APLICADOS.
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA AND VP.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', F_PLACA;
    END IF;

    -- VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO FROM AFERICAO_DATA A WHERE A.PLACA_VEICULO = F_PLACA)
    THEN
        -- COLETAMOS TODOS OS COD_AFERICAO QUE A PLACA POSSUI.
        SELECT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO_DATA A
        WHERE A.PLACA_VEICULO = F_PLACA
        INTO LISTA_COD_AFERICAO_PLACA;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO.
        UPDATE AFERICAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND CODIGO = ANY (LISTA_COD_AFERICAO_PLACA);
    END IF;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    IF EXISTS(SELECT C.PLACA_VEICULO FROM CHECKLIST_DATA C WHERE C.PLACA_VEICULO = F_PLACA)
    THEN
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        SELECT ARRAY_AGG(C.CODIGO)
        FROM CHECKLIST_DATA C
        WHERE C.PLACA_VEICULO = F_PLACA
        INTO LISTA_COD_CHECK_PLACA;

        -- DELETA COD_CHECK EM COS.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_CHECKLIST = ANY (LISTA_COD_CHECK_PLACA);

        -- BUSCO OS CODIGO PROLOG DELETADOS EM COS.
        SELECT ARRAY_AGG(CODIGO_PROLOG)
        FROM CHECKLIST_ORDEM_SERVICO_DATA
        WHERE COD_CHECKLIST = ANY (LISTA_COD_CHECK_PLACA)
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO IS TRUE
        INTO LISTA_COD_PROLOG_DELETADO_COS;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        FOREACH CODIGO_LOOP IN ARRAY LISTA_COD_PROLOG_DELETADO_COS
            LOOP
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET DELETADO            = TRUE,
                    DATA_HORA_DELETADO  = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER
                WHERE DELETADO = FALSE
                  AND (COD_OS, COD_UNIDADE) = (SELECT COS.CODIGO, COS.COD_UNIDADE
                                               FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                               WHERE COS.CODIGO_PROLOG = CODIGO_LOOP);
            END LOOP;

        -- DELETA TODOS CHECKLIST DA PLACA.
        UPDATE CHECKLIST_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE PLACA_VEICULO = F_PLACA
          AND DELETADO = FALSE
          AND CODIGO = ANY (LISTA_COD_CHECK_PLACA);
    END IF;

    -- REALIZA DELEÇÃO DA PLACA.
    UPDATE VEICULO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA = F_PLACA
      AND DELETADO = FALSE;

    -- MENSAGEM DE SUCESSO.
    SELECT 'Veículo deletado junto com suas dependências. Veículo: '
               || F_PLACA
               || ', Empresa: '
               || NOME_EMPRESA
               || ', Unidade: '
               || NOME_UNIDADE
    INTO DEPENDENCIAS_DELETADAS;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2270

-- Query para detectar quantos pneus deletados ainda possuem serviços não deletados em aberto.
-- Usada apenas nos testes.
-- select *
-- from afericao_manutencao_data amd
-- where amd.cod_pneu in (select pd.codigo from pneu_data pd where pd.deletado = true)
--   and amd.deletado = false
--   and amd.data_hora_resolucao is null;

-- Query para deletar serviços de pneus que estão em aberto mas os pneus estão deletados.
UPDATE AFERICAO_MANUTENCAO_DATA
SET DELETADO            = TRUE,
    DATA_HORA_DELETADO  = now(),
    PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO = FALSE
  AND DATA_HORA_RESOLUCAO IS NULL
  AND COD_PNEU IN (SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.DELETADO = TRUE);

DROP FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
    F_CODIGO BIGINT,
    F_CODIGO_CLIENTE TEXT,
    OUT AVISO_PNEU_DELETADO TEXT);

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
                                                         F_CODIGO_PNEU BIGINT,
                                                         F_CODIGO_CLIENTE TEXT,
                                                         OUT AVISO_PNEU_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_STATUS_PNEU_ANALISE CONSTANT   TEXT := 'ANALISE';
    QTD_LINHAS_ATUALIZADAS           BIGINT;
    F_COD_AFERICAO                   BIGINT[];
    F_COD_AFERICAO_FOREACH           BIGINT;
    F_QTD_AFERICAO_VALORES           BIGINT;
    F_QTD_AFERICAO_VALORES_DELETADOS BIGINT;
BEGIN

    -- Verifica se o pneu existe.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está aplicado.
    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_CODIGO_PNEU
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em análise.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE
           AND P.STATUS = F_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Deleta pneu.
    UPDATE PNEU_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_CODIGO_PNEU
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em afericao_manutencao_data.
    IF (SELECT EXISTS(SELECT AM.COD_AFERICAO
                      FROM AFERICAO_MANUTENCAO_DATA AM
                      WHERE AM.COD_PNEU = F_CODIGO_PNEU
                        AND AM.COD_UNIDADE = F_COD_UNIDADE
                        AND AM.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código Cliente % e Unidade % '
                'em afericao_manutencao_data', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Verifica se o pneu está em afericao_valores_data.
    IF (SELECT EXISTS(SELECT AV.COD_AFERICAO
                      FROM AFERICAO_VALORES_DATA AV
                      WHERE AV.COD_PNEU = F_CODIGO_PNEU
                        AND AV.COD_UNIDADE = F_COD_UNIDADE
                        AND AV.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade % em afericao_valores_data',
                F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    --Busca todos os cod_afericao deletados a partir do pneu.
    SELECT ARRAY_AGG(AV.COD_AFERICAO)
    FROM AFERICAO_VALORES_DATA AV
    WHERE AV.COD_PNEU = F_CODIGO_PNEU
      AND AV.COD_UNIDADE = F_COD_UNIDADE
      AND AV.DELETADO IS TRUE
    INTO F_COD_AFERICAO;

    -- Verifica se algum valor foi deletado em afericao_valores_data.
    IF (F_COD_AFERICAO IS NOT NULL AND ARRAY_LENGTH(F_COD_AFERICAO, 1) > 0)
    THEN
        -- Iteração com cada cod_afericao deletado em afericao_valores_data.
        FOREACH F_COD_AFERICAO_FOREACH IN ARRAY F_COD_AFERICAO
            LOOP
                -- Coleta a quantidade de aferições em afericao_valores_data.
                F_QTD_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                          FROM AFERICAO_VALORES_DATA AVD
                                          WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH);

                -- Coleta a quantidade de aferições deletadas em afericao_valores_data.
                F_QTD_AFERICAO_VALORES_DELETADOS = (SELECT COUNT(AVD.COD_AFERICAO)
                                                    FROM AFERICAO_VALORES_DATA AVD
                                                    WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                                                      AND AVD.DELETADO IS TRUE);

                -- Verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                IF (F_QTD_AFERICAO_VALORES = F_QTD_AFERICAO_VALORES_DELETADOS)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE CODIGO = F_COD_AFERICAO_FOREACH;

                    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

                    -- Garante que a deleção foi realizada.
                    IF (QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <= 0)
                    THEN
                        RAISE EXCEPTION 'Erro ao deletar aferição com Código: %, Unidade: %',
                            F_COD_AFERICAO_FOREACH, F_COD_UNIDADE;
                    END IF;
                END IF;
            END LOOP;
    END IF;

    SELECT 'PNEU DELETADO: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DO CLIENTE: '
               || F_CODIGO_CLIENTE
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_PNEU_DELETADO;
END
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- Alterar function que busca as unidades que um colaborador tem acesso.
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR BIGINT, F_EQUIPE_OBRIGATORIA BOOLEAN DEFAULT TRUE)
    RETURNS TABLE
            (
                CODIGO_EMPRESA  BIGINT,
                NOME_EMPRESA    TEXT,
                CODIGO_REGIONAL BIGINT,
                NOME_REGIONAL   TEXT,
                CODIGO_UNIDADE  BIGINT,
                NOME_UNIDADE    TEXT,
                CODIGO_EQUIPE   BIGINT,
                NOME_EQUIPE     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_PERMISSAO SMALLINT;
    F_COD_EMPRESA   BIGINT;
    F_COD_REGIONAL  BIGINT;
    F_COD_UNIDADE   BIGINT;
    F_COD_EQUIPE    BIGINT;
BEGIN
    SELECT INTO F_COD_PERMISSAO, F_COD_EMPRESA, F_COD_REGIONAL, F_COD_UNIDADE, F_COD_EQUIPE C.COD_PERMISSAO,
                                                                                            C.COD_EMPRESA,
                                                                                            R.CODIGO,
                                                                                            C.COD_UNIDADE,
                                                                                            C.COD_EQUIPE
    FROM COLABORADOR C
             JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
             JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
    WHERE C.CODIGO = F_COD_COLABORADOR;
    IF F_EQUIPE_OBRIGATORIA
    THEN
        RETURN QUERY
        SELECT EMP.CODIGO       AS COD_EMPRESA,
               EMP.NOME :: TEXT AS NOME_EMPRESA,
               R.CODIGO         AS COD_REGIONAL,
               R.REGIAO :: TEXT AS NOME_REGIONAL,
               U.CODIGO         AS COD_UNIDADE,
               U.NOME :: TEXT   AS NOME_UNIDADE,
               EQ.CODIGO        AS COD_EQUIPE,
               EQ.NOME :: TEXT  AS NOME_EQUIPE
        FROM UNIDADE U
                 JOIN REGIONAL R
                      ON R.CODIGO = U.COD_REGIONAL
                 JOIN EMPRESA EMP
                      ON EMP.CODIGO = U.COD_EMPRESA
                 JOIN EQUIPE EQ
                      ON U.CODIGO = EQ.COD_UNIDADE
        WHERE EMP.CODIGO = F_COD_EMPRESA
          AND F_IF(F_COD_PERMISSAO <= 2, R.CODIGO = F_COD_REGIONAL, TRUE)
          AND F_IF(F_COD_PERMISSAO <= 1, U.CODIGO = F_COD_UNIDADE, TRUE)
          AND F_IF(F_COD_PERMISSAO = 0, EQ.CODIGO = F_COD_EQUIPE, TRUE)
        ORDER BY EMP.CODIGO ASC, R.CODIGO ASC, U.CODIGO ASC, EQ.CODIGO ASC;
    ELSE
        RETURN QUERY
        SELECT EMP.CODIGO       AS COD_EMPRESA,
               EMP.NOME :: TEXT AS NOME_EMPRESA,
               R.CODIGO         AS COD_REGIONAL,
               R.REGIAO :: TEXT AS NOME_REGIONAL,
               U.CODIGO         AS COD_UNIDADE,
               U.NOME :: TEXT   AS NOME_UNIDADE,
               EQ.CODIGO        AS COD_EQUIPE,
               EQ.NOME :: TEXT  AS NOME_EQUIPE
        FROM UNIDADE U
                 JOIN REGIONAL R
                      ON R.CODIGO = U.COD_REGIONAL
                 JOIN EMPRESA EMP
                      ON EMP.CODIGO = U.COD_EMPRESA
                 LEFT JOIN EQUIPE EQ
                           ON U.CODIGO = EQ.COD_UNIDADE
        WHERE EMP.CODIGO = F_COD_EMPRESA
          AND F_IF(F_COD_PERMISSAO <= 2, R.CODIGO = F_COD_REGIONAL, TRUE)
          AND F_IF(F_COD_PERMISSAO <= 1, U.CODIGO = F_COD_UNIDADE, TRUE)
          AND F_IF(F_COD_PERMISSAO = 0, EQ.CODIGO = F_COD_EQUIPE, TRUE)
        ORDER BY EMP.CODIGO ASC, R.CODIGO ASC, U.CODIGO ASC, EQ.CODIGO ASC;
    END IF;
END;
$$;

-- Alterar function que lista as configurações de restrição de pneus por unidade
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_BY_COLABORADOR(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                CODIGO                             BIGINT,
                CODIGO_EMPRESA                     BIGINT,
                CODIGO_REGIONAL                    BIGINT,
                NOME_REGIONAL                      TEXT,
                CODIGO_UNIDADE                     BIGINT,
                NOME_UNIDADE                       TEXT,
                COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT,
                DATA_HORA_ULTIMA_ATUALIZACAO       TIMESTAMP WITHOUT TIME ZONE,
                TOLERANCIA_CALIBRAGEM              REAL,
                TOLERANCIA_INSPECAO                REAL,
                SULCO_MINIMO_RECAPAGEM             REAL,
                SULCO_MINIMO_DESCARTE              REAL,
                PERIODO_AFERICAO_PRESSAO           INTEGER,
                PERIODO_AFERICAO_SULCO             INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        WITH UNIDADES_ACESSO AS (
            SELECT DISTINCT ON (F.CODIGO_UNIDADE) F.CODIGO_UNIDADE,
                                                  F.NOME_UNIDADE,
                                                  F.CODIGO_EMPRESA,
                                                  F.CODIGO_REGIONAL,
                                                  F.NOME_REGIONAL
            FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR, FALSE) F
        )
        SELECT PRU.CODIGO                                                                AS CODIGO,
               UA.CODIGO_EMPRESA                                                         AS CODIGO_EMPRESA,
               UA.CODIGO_REGIONAL                                                        AS CODIGO_REGIONAL,
               UA.NOME_REGIONAL                                                          AS NOME_REGIONAL,
               UA.CODIGO_UNIDADE                                                         AS CODIGO_UNIDADE,
               UA.NOME_UNIDADE                                                           AS NOME_UNIDADE,
               PRU.COD_COLABORADOR_ULTIMA_ATUALIZACAO                                    AS COD_COLABORADOR_ULTIMA_ATUALIZACAO,
               PRU.DATA_HORA_ULTIMA_ATUALIZACAO AT TIME ZONE TZ_UNIDADE(PRU.COD_UNIDADE) AS DATA_HORA_ULTIMA_ATUALIZACAO,
               PRU.TOLERANCIA_CALIBRAGEM                                                 AS TOLERANCIA_CALIBRAGEM,
               PRU.TOLERANCIA_INSPECAO                                                   AS TOLERANCIA_INSPECAO,
               PRU.SULCO_MINIMO_RECAPAGEM                                                AS SULCO_MINIMO_RECAPAGEM,
               PRU.SULCO_MINIMO_DESCARTE                                                 AS SULCO_MINIMO_DESCARTE,
               PRU.PERIODO_AFERICAO_PRESSAO                                              AS PERIODO_AFERICAO_PRESSAO,
               PRU.PERIODO_AFERICAO_SULCO                                                AS PERIODO_AFERICAO_SULCO
        FROM UNIDADES_ACESSO UA
                 LEFT JOIN PNEU_RESTRICAO_UNIDADE PRU ON UA.CODIGO_UNIDADE = PRU.COD_UNIDADE
        ORDER BY UA.NOME_REGIONAL ASC, UA.NOME_UNIDADE ASC;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2527 - Diferenciar de qual plataforma foi feita cada mudança de fase

-- Representa a plataforma
CREATE TYPE PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE AS ENUM (
  'WEBSITE',
  'ANDROID',
  'INTEGRACOES');

-- #####################################################################################################################
-- Tabela de abertura
-- Adiciona coluna na tabela para armazenar a plataforma de origem da ação
ALTER TABLE SOCORRO_ROTA_ABERTURA ADD PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE;

-- Seta o valor da coluna de plataforma_origem antes de torná-la not null
UPDATE SOCORRO_ROTA_ABERTURA SET PLATAFORMA_ORIGEM = 'ANDROID' WHERE TRUE;

-- Altera a coluna para not null
ALTER TABLE SOCORRO_ROTA_ABERTURA ALTER COLUMN PLATAFORMA_ORIGEM SET NOT NULL;

-- Altera o nome da coluna na tabela
ALTER TABLE SOCORRO_ROTA_ABERTURA RENAME COLUMN VERSAO_APP_MOMENTO_ABERTURA TO VERSAO_PLATAFORMA_ORIGEM;

-- Altera o tipo de dado para text
ALTER TABLE SOCORRO_ROTA_ABERTURA ALTER COLUMN VERSAO_PLATAFORMA_ORIGEM TYPE TEXT USING VERSAO_PLATAFORMA_ORIGEM::TEXT;
-- #####################################################################################################################

-- #####################################################################################################################
-- Tabela de atendimento
-- Adiciona coluna na tabela para armazenar a plataforma de origem da ação
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO ADD PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE;

-- Seta o valor da coluna de plataforma_origem antes de torná-la not null
UPDATE SOCORRO_ROTA_ATENDIMENTO SET PLATAFORMA_ORIGEM = 'ANDROID' WHERE TRUE;

-- Altera a coluna para not null
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO ALTER COLUMN PLATAFORMA_ORIGEM SET NOT NULL;

-- Altera o nome da coluna na tabela
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO RENAME COLUMN VERSAO_APP_MOMENTO_ATENDIMENTO TO VERSAO_PLATAFORMA_ORIGEM;

-- Altera o tipo de dado para text
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO ALTER COLUMN VERSAO_PLATAFORMA_ORIGEM TYPE TEXT USING VERSAO_PLATAFORMA_ORIGEM::TEXT;
-- #####################################################################################################################

-- #####################################################################################################################
-- Tabela de invalidação
-- Adiciona coluna na tabela para armazenar a plataforma de origem da ação
ALTER TABLE SOCORRO_ROTA_INVALIDACAO ADD PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE;

-- Seta o valor da coluna de plataforma_origem antes de torná-la not null
UPDATE SOCORRO_ROTA_INVALIDACAO SET PLATAFORMA_ORIGEM = 'ANDROID' WHERE TRUE;

-- Altera a coluna para not null
ALTER TABLE SOCORRO_ROTA_INVALIDACAO ALTER COLUMN PLATAFORMA_ORIGEM SET NOT NULL;

-- Altera o nome da coluna na tabela
ALTER TABLE SOCORRO_ROTA_INVALIDACAO RENAME COLUMN VERSAO_APP_MOMENTO_INVALIDACAO TO VERSAO_PLATAFORMA_ORIGEM;

-- Altera o tipo de dado para text
ALTER TABLE SOCORRO_ROTA_INVALIDACAO ALTER COLUMN VERSAO_PLATAFORMA_ORIGEM TYPE TEXT USING VERSAO_PLATAFORMA_ORIGEM::TEXT;
-- #####################################################################################################################

-- #####################################################################################################################
-- Tabela de finalização
-- Adiciona coluna na tabela para armazenar a plataforma de origem da ação
ALTER TABLE SOCORRO_ROTA_FINALIZACAO ADD PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE;

-- Seta o valor da coluna de plataforma_origem antes de torná-la not null
UPDATE SOCORRO_ROTA_FINALIZACAO SET PLATAFORMA_ORIGEM = 'ANDROID' WHERE TRUE;

-- Altera a coluna para not null
ALTER TABLE SOCORRO_ROTA_FINALIZACAO ALTER COLUMN PLATAFORMA_ORIGEM SET NOT NULL;

-- Altera o nome da coluna na tabela
ALTER TABLE SOCORRO_ROTA_FINALIZACAO RENAME COLUMN VERSAO_APP_MOMENTO_FINALIZACAO TO VERSAO_PLATAFORMA_ORIGEM;

-- Altera o tipo de dado para text
ALTER TABLE SOCORRO_ROTA_FINALIZACAO ALTER COLUMN VERSAO_PLATAFORMA_ORIGEM TYPE TEXT USING VERSAO_PLATAFORMA_ORIGEM::TEXT;
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################


-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2521 - Melhorar amarrações na estrutura de tabelas de socorro
-- #####################################################################################################################
-- #####################################################################################################################

-- Cria function genérica para verificação de liberação de funcionalidade por empresa
-- Function já lançada em prod, por isso comentada.
-- CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA BIGINT)
--     RETURNS VOID
--     LANGUAGE PLPGSQL
-- AS
-- $$
-- DECLARE
--     -- Liberado para Fadel (1), Avilan (2) e Testes (3).
--     F_COD_EMPRESAS_LIBERADAS BIGINT[] := ARRAY [1,2,3];
-- BEGIN
--     -- Por enquanto apenas a fadel está utilizando esta funcionalidade.
--     -- TODO: Criar estrutura própria para realizar essa verificação
--     IF NOT F_COD_EMPRESA = ANY (F_COD_EMPRESAS_LIBERADAS) THEN
--         PERFORM THROW_GENERIC_ERROR(
--                         'A funcionalidade de Socorro em Rota não está liberada para a sua empresa, entre em contato com conexao@zalf.com.br para contratar!');
--     END IF;
-- END;
-- $$;

-- #####################################################################################################################
-- #####################################################################################################################
-- onde foi criada.

-- Cria um índice que possibilite a criação da FK
ALTER TABLE SOCORRO_ROTA_OPCAO_PROBLEMA
    ADD CONSTRAINT UNIQUE_CODIGO_EMPRESA UNIQUE (CODIGO, COD_EMPRESA);

-- Adiciona o código de empresa na tabela de abertura para compor a FK
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ADD COD_EMPRESA BIGINT;

-- Atualiza os registros pré-existentes
UPDATE SOCORRO_ROTA_ABERTURA SRA
SET COD_EMPRESA = (
    SELECT U.COD_EMPRESA
    FROM SOCORRO_ROTA SR
             JOIN UNIDADE U ON SR.COD_UNIDADE = U.CODIGO
    WHERE SR.CODIGO = SRA.COD_SOCORRO_ROTA);

-- Seta o código de empresa como not null
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ALTER COLUMN COD_EMPRESA SET NOT NULL;

-- Dropa a FK antiga que continha somente o código
ALTER TABLE SOCORRO_ROTA_ABERTURA
    DROP CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA_OPCAO_PROBLEMA;

-- Recria a FK com código de empresa e código de problema
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ADD CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA_OPCAO_PROBLEMA
        FOREIGN KEY (COD_PROBLEMA_SOCORRO_ROTA, COD_EMPRESA) REFERENCES SOCORRO_ROTA_OPCAO_PROBLEMA (CODIGO, COD_EMPRESA);
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- Adiciona fk entre a tabela pai e a abertura
-- Cria a coluna
ALTER TABLE SOCORRO_ROTA
	ADD COD_ABERTURA BIGINT;

-- Adiciona valores à nova coluna
UPDATE SOCORRO_ROTA SR
SET COD_ABERTURA = (SELECT SRA.CODIGO
                    FROM SOCORRO_ROTA_ABERTURA SRA
                    WHERE SRA.COD_SOCORRO_ROTA = SR.CODIGO);

-- Seta a coluna como not null (obrigatório ter abertura para haver processo de socorro)
ALTER TABLE SOCORRO_ROTA
    ALTER COLUMN COD_ABERTURA SET NOT NULL;

-- Cria índice único que possibilite a FK na pai para filha
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_ABERTURA_COD_SOCORRO UNIQUE (CODIGO, COD_SOCORRO_ROTA);

-- Adiciona a FK na pai para filha
ALTER TABLE SOCORRO_ROTA
	ADD CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_ABERTURA_FK
		FOREIGN KEY (CODIGO, COD_ABERTURA) REFERENCES SOCORRO_ROTA_ABERTURA (COD_SOCORRO_ROTA, CODIGO) DEFERRABLE;

-- Cria índice único que possibilite a FK na filha para pai
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_COD_ABERTURA UNIQUE (CODIGO, COD_ABERTURA);

-- Dropa a FK da filha para a pai
ALTER TABLE SOCORRO_ROTA_ABERTURA DROP CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA;

-- Adiciona a FK na filha para a pai
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ADD CONSTRAINT  SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA_FK
        FOREIGN KEY (COD_SOCORRO_ROTA, CODIGO) REFERENCES SOCORRO_ROTA (CODIGO, COD_ABERTURA) DEFERRABLE;

-- #####################################################################################################################

-- #####################################################################################################################
-- Adiciona fk entre a tabela pai e o atendimento
-- Cria a coluna
ALTER TABLE SOCORRO_ROTA
	ADD COD_ATENDIMENTO BIGINT;

-- Adiciona valores à nova coluna
UPDATE SOCORRO_ROTA SR
SET COD_ATENDIMENTO = (SELECT SRA.CODIGO
                                FROM SOCORRO_ROTA_ATENDIMENTO SRA
                                WHERE SRA.COD_SOCORRO_ROTA = SR.CODIGO);

-- Cria índice único que possibilite a FK na pai para filha
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_ATENDIMENTO_COD_SOCORRO UNIQUE (CODIGO, COD_SOCORRO_ROTA);

-- Adiciona a FK na pai para filha
ALTER TABLE SOCORRO_ROTA
	ADD CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_ATENDIMENTO_FK
		FOREIGN KEY (CODIGO, COD_ATENDIMENTO) REFERENCES SOCORRO_ROTA_ATENDIMENTO (COD_SOCORRO_ROTA, CODIGO) DEFERRABLE;

-- Cria índice único que possibilite a FK na filha para pai
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_COD_ATENDIMENTO UNIQUE (CODIGO, COD_ATENDIMENTO);

-- Dropa a FK da filha para a pai
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO DROP CONSTRAINT FK_SOCORRO_ROTA_ATENDIMENTO_SOCORRO_ROTA;

-- Adiciona a FK na filha para a pai
ALTER TABLE SOCORRO_ROTA_ATENDIMENTO
    ADD CONSTRAINT  SOCORRO_ROTA_ATENDIMENTO_SOCORRO_ROTA_FK
        FOREIGN KEY (COD_SOCORRO_ROTA, CODIGO) REFERENCES SOCORRO_ROTA (CODIGO, COD_ATENDIMENTO) DEFERRABLE;
-- #####################################################################################################################

-- #####################################################################################################################
-- Adiciona fk entre a tabela pai e a invalidação
-- Cria a coluna
ALTER TABLE SOCORRO_ROTA
	ADD COD_INVALIDACAO BIGINT;

-- Adiciona valores à nova coluna
UPDATE SOCORRO_ROTA SR
SET COD_INVALIDACAO = (SELECT SRI.CODIGO
                                FROM SOCORRO_ROTA_INVALIDACAO SRI
                                WHERE SRI.COD_SOCORRO_ROTA = SR.CODIGO);

-- Cria índice único que possibilite a FK na pai para filha
ALTER TABLE SOCORRO_ROTA_INVALIDACAO
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_INVALIDACAO_COD_SOCORRO UNIQUE (CODIGO, COD_SOCORRO_ROTA);

-- Adiciona a FK na pai para filha
ALTER TABLE SOCORRO_ROTA
	ADD CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_INVALIDACAO_FK
		FOREIGN KEY (CODIGO, COD_INVALIDACAO) REFERENCES SOCORRO_ROTA_INVALIDACAO (COD_SOCORRO_ROTA, CODIGO) DEFERRABLE;

-- Cria índice único que possibilite a FK na filha para pai
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_COD_INVALIDACAO UNIQUE (CODIGO, COD_INVALIDACAO);

-- Dropa a FK da filha para a pai
ALTER TABLE SOCORRO_ROTA_INVALIDACAO DROP CONSTRAINT FK_SOCORRO_ROTA_INVALIDACAO_SOCORRO_ROTA;

-- Adiciona a FK na filha para a pai
ALTER TABLE SOCORRO_ROTA_INVALIDACAO
    ADD CONSTRAINT  SOCORRO_ROTA_INVALIDACAO_SOCORRO_ROTA_FK
        FOREIGN KEY (COD_SOCORRO_ROTA, CODIGO) REFERENCES SOCORRO_ROTA (CODIGO, COD_INVALIDACAO) DEFERRABLE;
-- #####################################################################################################################

-- #####################################################################################################################
-- Adiciona fk entre a tabela pai e a finalização
-- Cria a coluna
ALTER TABLE SOCORRO_ROTA
	ADD COD_FINALIZACAO BIGINT;

-- Adiciona valores à nova coluna
UPDATE SOCORRO_ROTA SR
SET COD_FINALIZACAO = (SELECT SRF.CODIGO
                                FROM SOCORRO_ROTA_FINALIZACAO SRF
                                WHERE SRF.COD_SOCORRO_ROTA = SR.CODIGO);

-- Cria índice único que possibilite a FK na pai para filha
ALTER TABLE SOCORRO_ROTA_FINALIZACAO
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_FINALIZACAO_COD_SOCORRO UNIQUE (CODIGO, COD_SOCORRO_ROTA);

-- Adiciona a FK na pai para filha
ALTER TABLE SOCORRO_ROTA
	ADD CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_FINALIZACAO_FK
		FOREIGN KEY (CODIGO, COD_FINALIZACAO) REFERENCES SOCORRO_ROTA_FINALIZACAO (COD_SOCORRO_ROTA, CODIGO) DEFERRABLE;

-- Cria índice único que possibilite a FK na filha para pai
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT UNIQUE_SOCORRO_ROTA_COD_FINALIZACAO UNIQUE (CODIGO, COD_FINALIZACAO);

-- Dropa a FK da filha para a pai
ALTER TABLE SOCORRO_ROTA_FINALIZACAO DROP CONSTRAINT FK_SOCORRO_ROTA_FINALIZACAO_SOCORRO_ROTA;

-- Adiciona a FK na filha para a pai
ALTER TABLE SOCORRO_ROTA_FINALIZACAO
    ADD CONSTRAINT  SOCORRO_ROTA_FINALIZACAO_SOCORRO_ROTA_FK
        FOREIGN KEY (COD_SOCORRO_ROTA, CODIGO) REFERENCES SOCORRO_ROTA (CODIGO, COD_FINALIZACAO) DEFERRABLE;
-- #####################################################################################################################

-- #####################################################################################################################
-- Garante que no status aberto só existam dados nas filhas para este status
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ABERTURA
        CHECK (STATUS_ATUAL <> 'ABERTO' OR
               (STATUS_ATUAL = 'ABERTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NULL AND
                COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

-- Garante que no status em atendimento só existam dados nas filhas para este status
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ATENDIMENTO
        CHECK (STATUS_ATUAL <> 'EM_ATENDIMENTO' OR
               (STATUS_ATUAL = 'EM_ATENDIMENTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

-- Garante que no status de invalidação só existam dados nas filhas para este status
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_INVALIDACAO
        CHECK (STATUS_ATUAL <> 'INVALIDO' OR
               (STATUS_ATUAL = 'INVALIDO' AND COD_ABERTURA IS NOT NULL AND COD_INVALIDACAO IS NOT NULL AND
                COD_FINALIZACAO IS NULL));

-- Garante que no status finalizado só existam dados nas filhas para este status
ALTER TABLE SOCORRO_ROTA
    ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_FINALIZACAO
        CHECK (STATUS_ATUAL <> 'FINALIZADO' OR
               (STATUS_ATUAL = 'FINALIZADO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NOT NULL));
-- #####################################################################################################################
-- Dropa a function de listagem.
DROP FUNCTION FUNC_SOCORRO_ROTA_LISTAGEM(BIGINT[], DATE, DATE, TEXT);
-- Recria a function de listagem para adicionar a verificação de empresa liberada.
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_LISTAGEM(F_COD_UNIDADES BIGINT[],
                                                      F_DATA_INICIAL DATE,
                                                      F_DATA_FINAL DATE,
                                                      F_TOKEN TEXT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                          BIGINT,
                UNIDADE                                   TEXT,
                PLACA_VEICULO                             TEXT,
                VEICULO_DELETADO                          BOOLEAN,
                NOME_RESPONSAVEL_ABERTURA_SOCORRO         TEXT,
                COLABORADOR_DELETADO                      BOOLEAN,
                DESCRICAO_FORNECIDA_ABERTURA_SOCORRO      TEXT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO TEXT,
                DATA_HORA_ABERTURA_SOCORRO                TIMESTAMP WITHOUT TIME ZONE,
                ENDERECO_AUTOMATICO_ABERTURA_SOCORRO      TEXT,
                URL_FOTO_1_ABERTURA                       TEXT,
                URL_FOTO_2_ABERTURA                       TEXT,
                URL_FOTO_3_ABERTURA                       TEXT,
                STATUS_ATUAL_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Permissões para ver todos os socorros em rota
    -- 146 - TRATAR_SOCORRO
    -- 147 - VISUALIZAR_SOCORROS_E_RELATORIOS
    F_PERMISSOES_VISUALIZAR_TODOS INTEGER[] := ARRAY [146,147];
    F_VER_TODOS                   BOOLEAN   := (SELECT POSSUI_PERMISSSAO
                                                FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN,
                                                                                                F_PERMISSOES_VISUALIZAR_TODOS,
                                                                                                FALSE,
                                                                                                TRUE));
    F_COD_COLABORADOR             BIGINT    := (SELECT COD_COLABORADOR
                                                FROM TOKEN_AUTENTICACAO
                                                WHERE TOKEN = F_TOKEN);
    -- Busca o código de empresa com base na primeira unidade do array recebido
    F_COD_EMPRESA                 BIGINT    := (SELECT COD_EMPRESA
                                                FROM UNIDADE
                                                WHERE CODIGO = (SELECT (F_COD_UNIDADES)[1]));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    RETURN QUERY
        SELECT SR.CODIGO                                                      AS COD_SOCORRO_ROTA,
               U.NOME :: TEXT                                                 AS UNIDADE,
               VD.PLACA :: TEXT                                               AS PLACA_VEICULO,
               VD.DELETADO                                                    AS VEICULO_DELETADO,
               CD.NOME :: TEXT                                                AS NOME_RESPONSAVEL,
               CD.DELETADO                                                    AS COLABORADOR_DELETADO,
               SRA.DESCRICAO_PROBLEMA                                         AS DESCRICAO_FORNECIDA,
               SROP.DESCRICAO :: TEXT                                         AS DESCRICAO_OPCAO_PROBLEMA,
               SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_ABERTURA,
               SRA.ENDERECO_AUTOMATICO                                        AS ENDERECO_AUTOMATICO_ABERTURA,
               SRA.URL_FOTO_1_ABERTURA :: TEXT                                AS URL_FOTO_1_ABERTURA,
               SRA.URL_FOTO_2_ABERTURA :: TEXT                                AS URL_FOTO_2_ABERTURA,
               SRA.URL_FOTO_3_ABERTURA :: TEXT                                AS URL_FOTO_3_ABERTURA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                    AS STATUS_ATUAL_SOCORRO
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON U.CODIGO = SR.COD_UNIDADE
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
                 JOIN VEICULO_DATA VD ON SRA.COD_VEICULO_PROBLEMA = VD.CODIGO
                 JOIN COLABORADOR_DATA CD ON SRA.COD_COLABORADOR_ABERTURA = CD.CODIGO
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRA.COD_PROBLEMA_SOCORRO_ROTA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          -- Aplica o filtro por colaborador apenas se não tiver permissão para ver todos
          AND F_IF(F_VER_TODOS, TRUE, SRA.COD_COLABORADOR_ABERTURA = F_COD_COLABORADOR)
          AND (SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) :: DATE
            BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY SRA.DATA_HORA_ABERTURA DESC;
END;
$$;
-- Dropa a function de abertura.
DROP FUNCTION FUNC_SOCORRO_ROTA_ABERTURA(BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, TEXT, TIMESTAMP WITH TIME ZONE,
    TEXT, TEXT, TEXT, TEXT, TEXT, NUMERIC, TEXT, TEXT, BIGINT, TEXT, TEXT, BIGINT, INTEGER, TEXT, TEXT);

-- Recria a function de abertura para adicionar o código de abertura na tabela pai e diferenciar a plataforma.
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA(F_COD_UNIDADE BIGINT,
                                                      F_COD_COLABORADOR_ABERTURA BIGINT,
                                                      F_COD_VEICULO_PROBLEMA BIGINT,
                                                      F_KM_VEICULO_ABERTURA BIGINT,
                                                      F_COD_PROBLEMA_SOCORRO_ROTA BIGINT,
                                                      F_DESCRICAO_PROBLEMA TEXT,
                                                      F_DATA_HORA_ABERTURA TIMESTAMP WITH TIME ZONE,
                                                      F_URL_FOTO_1_ABERTURA TEXT,
                                                      F_URL_FOTO_2_ABERTURA TEXT,
                                                      F_URL_FOTO_3_ABERTURA TEXT,
                                                      F_LATITUDE_ABERTURA TEXT,
                                                      F_LONGITUDE_ABERTURA TEXT,
                                                      F_PRECISAO_LOCALIZACAO_ABERTURA_METROS NUMERIC,
                                                      F_ENDERECO_AUTOMATICO TEXT,
                                                      F_PONTO_REFERENCIA TEXT,
                                                      F_DEVICE_ID_ABERTURA TEXT,
                                                      F_DEVICE_IMEI_ABERTURA TEXT,
                                                      F_DEVICE_UPTIME_MILLIS_ABERTURA BIGINT,
                                                      F_ANDROID_API_VERSION_ABERTURA INTEGER,
                                                      F_MARCA_DEVICE_ABERTURA TEXT,
                                                      F_MODELO_DEVICE_ABERTURA TEXT,
                                                      F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                      F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_SOCORRO_INSERIDO          BIGINT;
    F_COD_SOCORRO_ABERTURA_INSERIDO BIGINT;
    F_COD_EMPRESA                   BIGINT := (SELECT COD_EMPRESA
                                               FROM UNIDADE
                                               WHERE CODIGO = F_COD_UNIDADE);
    F_COD_ABERTURA                  BIGINT;
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    -- Assim conseguimos inserir mantendo a referência circular entre a pai e as filhas.
    SET CONSTRAINTS ALL DEFERRED;

    -- Pega o código de abertura da sequence para poder atualizar a tabela pai
    F_COD_ABERTURA := (SELECT NEXTVAL(PG_GET_SERIAL_SEQUENCE('socorro_rota_abertura', 'codigo')));

    -- Insere na tabela pai
    INSERT INTO SOCORRO_ROTA (COD_UNIDADE, STATUS_ATUAL, COD_ABERTURA)
    VALUES (F_COD_UNIDADE, 'ABERTO', F_COD_ABERTURA)
    RETURNING CODIGO INTO F_COD_SOCORRO_INSERIDO;

    -- Exibe erro se não puder inserir
    IF F_COD_SOCORRO_INSERIDO IS NULL OR F_COD_SOCORRO_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    -- Insere na tabela de abertura
    INSERT INTO SOCORRO_ROTA_ABERTURA (CODIGO,
                                       COD_SOCORRO_ROTA,
                                       COD_COLABORADOR_ABERTURA,
                                       COD_VEICULO_PROBLEMA,
                                       KM_VEICULO_ABERTURA,
                                       COD_PROBLEMA_SOCORRO_ROTA,
                                       DESCRICAO_PROBLEMA,
                                       DATA_HORA_ABERTURA,
                                       URL_FOTO_1_ABERTURA,
                                       URL_FOTO_2_ABERTURA,
                                       URL_FOTO_3_ABERTURA,
                                       LATITUDE_ABERTURA,
                                       LONGITUDE_ABERTURA,
                                       PRECISAO_LOCALIZACAO_ABERTURA_METROS,
                                       ENDERECO_AUTOMATICO,
                                       PONTO_REFERENCIA,
                                       DEVICE_ID_ABERTURA,
                                       DEVICE_IMEI_ABERTURA,
                                       DEVICE_UPTIME_MILLIS_ABERTURA,
                                       ANDROID_API_VERSION_ABERTURA,
                                       MARCA_DEVICE_ABERTURA,
                                       MODELO_DEVICE_ABERTURA,
                                       COD_EMPRESA,
                                       PLATAFORMA_ORIGEM,
                                       VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_ABERTURA,
            F_COD_SOCORRO_INSERIDO,
            F_COD_COLABORADOR_ABERTURA,
            F_COD_VEICULO_PROBLEMA,
            F_KM_VEICULO_ABERTURA,
            F_COD_PROBLEMA_SOCORRO_ROTA,
            F_DESCRICAO_PROBLEMA,
            F_DATA_HORA_ABERTURA,
            F_URL_FOTO_1_ABERTURA,
            F_URL_FOTO_2_ABERTURA,
            F_URL_FOTO_3_ABERTURA,
            F_LATITUDE_ABERTURA,
            F_LONGITUDE_ABERTURA,
            F_PRECISAO_LOCALIZACAO_ABERTURA_METROS,
            F_ENDERECO_AUTOMATICO,
            F_PONTO_REFERENCIA,
            F_DEVICE_ID_ABERTURA,
            F_DEVICE_IMEI_ABERTURA,
            F_DEVICE_UPTIME_MILLIS_ABERTURA,
            F_ANDROID_API_VERSION_ABERTURA,
            F_MARCA_DEVICE_ABERTURA,
            F_MODELO_DEVICE_ABERTURA,
            F_COD_EMPRESA,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM)
    RETURNING CODIGO INTO F_COD_SOCORRO_ABERTURA_INSERIDO;

    -- Exibe erro se não puder inserir
    IF F_COD_SOCORRO_ABERTURA_INSERIDO IS NULL OR F_COD_SOCORRO_ABERTURA_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    -- Atualiza o KM no veículo, caso:
    -- Não esteja deletado
    -- O KM coletado na abertura seja maior que o atual do veículo
    UPDATE VEICULO SET KM = F_KM_VEICULO_ABERTURA WHERE CODIGO = F_COD_VEICULO_PROBLEMA AND KM < F_KM_VEICULO_ABERTURA;

    -- Retorna o código do socorro
    RETURN F_COD_SOCORRO_INSERIDO;
END;
$$;

-- Dropa a function de atendimento.
DROP FUNCTION FUNC_SOCORRO_ROTA_ATENDIMENTO(BIGINT, BIGINT, TEXT, TIMESTAMP WITH TIME ZONE, TEXT, TEXT, NUMERIC, TEXT,
    BIGINT, TEXT, TEXT, BIGINT, INTEGER, TEXT, TEXT);

-- Recria a function de atendimento para adicionar o código de atendimento na tabela pai e diferenciar a plataforma.
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ATENDIMENTO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_ATENDIMENTO BIGINT,
                                                         F_OBSERVACAO_ATENDIMENTO TEXT,
                                                         F_DATA_HORA_ATENDIMENTO TIMESTAMP WITH TIME ZONE,
                                                         F_LATITUDE_ATENDIMENTO TEXT,
                                                         F_LONGITUDE_ATENDIMENTO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_DEVICE_ID_ATENDIMENTO TEXT,
                                                         F_DEVICE_IMEI_ATENDIMENTO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_ATENDIMENTO BIGINT,
                                                         F_ANDROID_API_VERSION_ATENDIMENTO INTEGER,
                                                         F_MARCA_DEVICE_ATENDIMENTO TEXT,
                                                         F_MODELO_DEVICE_ATENDIMENTO TEXT,
                                                         F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                         F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_SOCORRO_ATENDIMENTO_INSERIDO BIGINT;
    F_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe
    IF F_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível localizar o socorro em rota.');
    END IF;

    IF F_STATUS_SOCORRO = 'EM_ATENDIMENTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi atendido.');
    END IF;
    IF F_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi invalidado.');
    END IF;
    IF F_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi finalizado.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    INSERT INTO SOCORRO_ROTA_ATENDIMENTO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_ATENDIMENTO,
                                          OBSERVACAO_ATENDIMENTO,
                                          DATA_HORA_ATENDIMENTO,
                                          LATITUDE_ATENDIMENTO,
                                          LONGITUDE_ATENDIMENTO,
                                          PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_ATENDIMENTO,
                                          DEVICE_IMEI_ATENDIMENTO,
                                          DEVICE_UPTIME_MILLIS_ATENDIMENTO,
                                          ANDROID_API_VERSION_ATENDIMENTO,
                                          MARCA_DEVICE_ATENDIMENTO,
                                          MODELO_DEVICE_ATENDIMENTO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_ATENDIMENTO,
            F_OBSERVACAO_ATENDIMENTO,
            F_DATA_HORA_ATENDIMENTO,
            F_LATITUDE_ATENDIMENTO,
            F_LONGITUDE_ATENDIMENTO,
            F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_ATENDIMENTO,
            F_DEVICE_IMEI_ATENDIMENTO,
            F_DEVICE_UPTIME_MILLIS_ATENDIMENTO,
            F_ANDROID_API_VERSION_ATENDIMENTO,
            F_MARCA_DEVICE_ATENDIMENTO,
            F_MODELO_DEVICE_ATENDIMENTO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO F_COD_SOCORRO_ATENDIMENTO_INSERIDO;

    IF F_COD_SOCORRO_ATENDIMENTO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_ATENDIMENTO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL    = 'EM_ATENDIMENTO',
            COD_ATENDIMENTO = F_COD_SOCORRO_ATENDIMENTO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
            THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar o atendimento desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a atendimento desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_ATENDIMENTO_INSERIDO;
END;
$$;

-- Dropa a function de invalidação.
DROP FUNCTION FUNC_SOCORRO_ROTA_INVALIDACAO(BIGINT, BIGINT, TEXT, TIMESTAMP WITH TIME ZONE, TEXT, TEXT, TEXT, TEXT,
    TEXT, NUMERIC, TEXT, BIGINT, TEXT, TEXT, BIGINT, INTEGER, TEXT, TEXT);

-- Recria a function de invalidação para adicionar o código de invalidação na tabela pai e diferenciar a plataforma.
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_INVALIDACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_INVALIDACAO BIGINT,
                                                         F_MOTIVO_INVALIDACAO TEXT,
                                                         F_DATA_HORA_INVALIDACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_INVALIDACAO TEXT,
                                                         F_URL_FOTO_2_INVALIDACAO TEXT,
                                                         F_URL_FOTO_3_INVALIDACAO TEXT,
                                                         F_LATITUDE_INVALIDACAO TEXT,
                                                         F_LONGITUDE_INVALIDACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_DEVICE_ID_INVALIDACAO TEXT,
                                                         F_DEVICE_IMEI_INVALIDACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_INVALIDACAO BIGINT,
                                                         F_ANDROID_API_VERSION_INVALIDACAO INTEGER,
                                                         F_MARCA_DEVICE_INVALIDACAO TEXT,
                                                         F_MODELO_DEVICE_INVALIDACAO TEXT,
                                                         F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                         F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_PERMISSAO_TRATAR_SOCORRO                BOOLEAN;
    F_PERMISSAO_ABERTURA_SOCORRO              BOOLEAN;
    F_STATUS_SOCORRO                          SOCORRO_ROTA_STATUS_TYPE;
    F_COD_SOCORRO_INVALIDACAO_INSERIDO        BIGINT;
    F_QTD_LINHAS_ATUALIZADAS                  BIGINT;
    F_COD_COLABORADOR_ABERTURA       CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ABERTURA
                                                          FROM SOCORRO_ROTA_ABERTURA
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO    CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                          FROM SOCORRO_ROTA_ATENDIMENTO
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_PERMISSAO_TRATAR_SOCORRO   CONSTANT INTEGER := 146;
    F_COD_PERMISSAO_ABERTURA_SOCORRO CONSTANT INTEGER := 145;
BEGIN
    -- Verifica se o socorro em rota existe.
    IF F_COD_COLABORADOR_ABERTURA IS NULL
        THEN
        PERFORM THROW_GENERIC_ERROR(
                    'Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está invalidando.
    IF F_COD_COLABORADOR_ATENDIMENTO IS NOT NULL AND F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_INVALIDACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível invalidar um socorro que foi atendido por outro colaborador.');
    END IF;

    F_PERMISSAO_TRATAR_SOCORRO := (SELECT *
                                   FROM
                                       FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                               F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_TRATAR_SOCORRO));
    F_PERMISSAO_ABERTURA_SOCORRO := (SELECT *
                                     FROM
                                         FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                                 F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_ABERTURA_SOCORRO));
    F_STATUS_SOCORRO := (SELECT STATUS_ATUAL
                         FROM SOCORRO_ROTA
                         WHERE CODIGO = F_COD_SOCORRO_ROTA);

    -- Caso tenha a permissão de tratar socorros, impede a invalidação caso os status sejam INVALIDO e FINALZADO.
    IF F_PERMISSAO_TRATAR_SOCORRO
    THEN
        IF F_STATUS_SOCORRO = 'INVALIDO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi invalidado.');
        END IF;
        IF F_STATUS_SOCORRO = 'FINALIZADO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi finalizado.');
        END IF;
    -- Caso tenha a permissão de abertura, impede a invalidação de socorros de outros colaboradores e também
    -- de socorros que não estão mais em aberto.
    ELSEIF F_PERMISSAO_ABERTURA_SOCORRO
    THEN
        IF F_COD_COLABORADOR_ABERTURA <> F_COD_COLABORADOR_INVALIDACAO
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Você não tem permissão para invalidar pedidos de socorro de outros colaboradores.');
        END IF;
        IF F_STATUS_SOCORRO <> 'ABERTO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele não está mais em aberto.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                    'Você não ter permissão para invalidar o socorro.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    INSERT INTO SOCORRO_ROTA_INVALIDACAO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_INVALIDACAO,
                                          MOTIVO_INVALIDACAO,
                                          DATA_HORA_INVALIDACAO,
                                          URL_FOTO_1_INVALIDACAO,
                                          URL_FOTO_2_INVALIDACAO,
                                          URL_FOTO_3_INVALIDACAO,
                                          LATITUDE_INVALIDACAO,
                                          LONGITUDE_INVALIDACAO,
                                          PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_INVALIDACAO,
                                          DEVICE_IMEI_INVALIDACAO,
                                          DEVICE_UPTIME_MILLIS_INVALIDACAO,
                                          ANDROID_API_VERSION_INVALIDACAO,
                                          MARCA_DEVICE_INVALIDACAO,
                                          MODELO_DEVICE_INVALIDACAO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_INVALIDACAO,
            F_MOTIVO_INVALIDACAO,
            F_DATA_HORA_INVALIDACAO,
            F_URL_FOTO_1_INVALIDACAO,
            F_URL_FOTO_2_INVALIDACAO,
            F_URL_FOTO_3_INVALIDACAO,
            F_LATITUDE_INVALIDACAO,
            F_LONGITUDE_INVALIDACAO,
            F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_INVALIDACAO,
            F_DEVICE_IMEI_INVALIDACAO,
            F_DEVICE_UPTIME_MILLIS_INVALIDACAO,
            F_ANDROID_API_VERSION_INVALIDACAO,
            F_MARCA_DEVICE_INVALIDACAO,
            F_MODELO_DEVICE_INVALIDACAO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO F_COD_SOCORRO_INVALIDACAO_INSERIDO;

    IF F_COD_SOCORRO_INVALIDACAO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_INVALIDACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL    = 'INVALIDO',
            COD_INVALIDACAO = F_COD_SOCORRO_INVALIDACAO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_INVALIDACAO_INSERIDO;
END;
$$;

-- Dropa a functiona de finalização.
DROP FUNCTION FUNC_SOCORRO_ROTA_FINALIZACAO(BIGINT, BIGINT, TEXT, TIMESTAMP WITH TIME ZONE, TEXT, TEXT, TEXT, TEXT,
    TEXT, NUMERIC, TEXT, BIGINT, TEXT, TEXT, BIGINT, INTEGER, TEXT, TEXT);

-- Recria a function de finalização para adicionar o código de finalização na tabela pai e diferenciar a plataforma.
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_FINALIZACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_FINALIZACAO BIGINT,
                                                         F_MOTIVO_FINALIZACAO TEXT,
                                                         F_DATA_HORA_FINALIZACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_FINALIZACAO TEXT,
                                                         F_URL_FOTO_2_FINALIZACAO TEXT,
                                                         F_URL_FOTO_3_FINALIZACAO TEXT,
                                                         F_LATITUDE_FINALIZACAO TEXT,
                                                         F_LONGITUDE_FINALIZACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_DEVICE_ID_FINALIZACAO TEXT,
                                                         F_DEVICE_IMEI_FINALIZACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_FINALIZACAO BIGINT,
                                                         F_ANDROID_API_VERSION_FINALIZACAO INTEGER,
                                                         F_MARCA_DEVICE_FINALIZACAO TEXT,
                                                         F_MODELO_DEVICE_FINALIZACAO TEXT,
                                                         F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                         F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO      BIGINT                   := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                                    FROM SOCORRO_ROTA_ATENDIMENTO
                                                                    WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_SOCORRO_FINALIZACAO_INSERIDO BIGINT;
    F_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe
    IF F_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível localizar esse socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está finalizando.
    IF F_COD_COLABORADOR_ATENDIMENTO IS NOT NULL AND F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_FINALIZACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível finalizar um socorro que foi atendido por outro colaborador.');
    END IF;

    IF F_STATUS_SOCORRO = 'ABERTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele ainda não foi atendido.');
    END IF;
    IF F_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi invalidado.');
    END IF;
    IF F_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi finalizado.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    INSERT INTO SOCORRO_ROTA_FINALIZACAO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_FINALIZACAO,
                                          OBSERVACAO_FINALIZACAO,
                                          DATA_HORA_FINALIZACAO,
                                          URL_FOTO_1_FINALIZACAO,
                                          URL_FOTO_2_FINALIZACAO,
                                          URL_FOTO_3_FINALIZACAO,
                                          LATITUDE_FINALIZACAO,
                                          LONGITUDE_FINALIZACAO,
                                          PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_FINALIZACAO,
                                          DEVICE_IMEI_FINALIZACAO,
                                          DEVICE_UPTIME_MILLIS_FINALIZACAO,
                                          ANDROID_API_VERSION_FINALIZACAO,
                                          MARCA_DEVICE_FINALIZACAO,
                                          MODELO_DEVICE_FINALIZACAO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_FINALIZACAO,
            F_MOTIVO_FINALIZACAO,
            F_DATA_HORA_FINALIZACAO,
            F_URL_FOTO_1_FINALIZACAO,
            F_URL_FOTO_2_FINALIZACAO,
            F_URL_FOTO_3_FINALIZACAO,
            F_LATITUDE_FINALIZACAO,
            F_LONGITUDE_FINALIZACAO,
            F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_FINALIZACAO,
            F_DEVICE_IMEI_FINALIZACAO,
            F_DEVICE_UPTIME_MILLIS_FINALIZACAO,
            F_ANDROID_API_VERSION_FINALIZACAO,
            F_MARCA_DEVICE_FINALIZACAO,
            F_MODELO_DEVICE_FINALIZACAO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO F_COD_SOCORRO_FINALIZACAO_INSERIDO;

    IF F_COD_SOCORRO_FINALIZACAO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_FINALIZACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL                = 'FINALIZADO',
            COD_FINALIZACAO = F_COD_SOCORRO_FINALIZACAO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_FINALIZACAO_INSERIDO;
END;
$$;

-- Atualiza a visualização para incorporar a verificação de liberação de funcionalidade por empresa
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_SOCORRO_ROTA BIGINT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                    BIGINT,
                STATUS_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE,
                PLACA_VEICULO_ABERTURA              TEXT,
                COD_COLABORADOR_ABERTURA            BIGINT,
                NOME_RESPONSAVEL_ABERTURA           TEXT,
                KM_VEICULO_COLETADO_ABERTURA        BIGINT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA   TEXT,
                DESCRICAO_FORNECIDA_ABERTURA        TEXT,
                PONTO_REFERENCIA_FORNECIDO_ABERTURA TEXT,
                DATA_HORA_ABERTURA                  TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ABERTURA                   TEXT,
                LONGITUDE_ABERTURA                  TEXT,
                ENDERECO_AUTOMATICO_ABERTURA        TEXT,
                MARCA_APARELHO_ABERTURA             TEXT,
                MODELO_APARELHO_ABERTURA            TEXT,
                IMEI_APARELHO_ABERTURA              TEXT,
                URL_FOTO_1_ABERTURA                 TEXT,
                URL_FOTO_2_ABERTURA                 TEXT,
                URL_FOTO_3_ABERTURA                 TEXT,
                COD_COLABORADOR_ATENDIMENTO         BIGINT,
                NOME_RESPONSAVEL_ATENDIMENTO        TEXT,
                OBSERVACAO_ATENDIMENTO              TEXT,
                DATA_HORA_ATENDIMENTO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ATENDIMENTO                TEXT,
                LONGITUDE_ATENDIMENTO               TEXT,
                ENDERECO_AUTOMATICO_ATENDIMENTO     TEXT,
                MARCA_APARELHO_ATENDIMENTO          TEXT,
                MODELO_APARELHO_ATENDIMENTO         TEXT,
                IMEI_APARELHO_ATENDIMENTO           TEXT,
                COD_COLABORADOR_INVALIDACAO         BIGINT,
                NOME_RESPONSAVEL_INVALIDACAO        TEXT,
                MOTIVO_INVALIDACAO                  TEXT,
                DATA_HORA_INVALIDACAO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_INVALIDACAO                TEXT,
                LONGITUDE_INVALIDACAO               TEXT,
                ENDERECO_AUTOMATICO_INVALIDACAO     TEXT,
                MARCA_APARELHO_INVALIDACAO          TEXT,
                MODELO_APARELHO_INVALIDACAO         TEXT,
                IMEI_APARELHO_INVALIDACAO           TEXT,
                URL_FOTO_1_INVALIDACAO              TEXT,
                URL_FOTO_2_INVALIDACAO              TEXT,
                URL_FOTO_3_INVALIDACAO              TEXT,
                COD_COLABORADOR_FINALIZACAO         BIGINT,
                NOME_RESPONSAVEL_FINALIZACAO        TEXT,
                OBSERVACAO_FINALIZACAO              TEXT,
                DATA_HORA_FINALIZACAO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_FINALIZACAO                TEXT,
                LONGITUDE_FINALIZACAO               TEXT,
                ENDERECO_AUTOMATICO_FINALIZACAO     TEXT,
                MARCA_APARELHO_FINALIZACAO          TEXT,
                MODELO_APARELHO_FINALIZACAO         TEXT,
                IMEI_APARELHO_FINALIZACAO           TEXT,
                URL_FOTO_1_FINALIZACAO              TEXT,
                URL_FOTO_2_FINALIZACAO              TEXT,
                URL_FOTO_3_FINALIZACAO              TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Busca o código da empresa com base no código da unidade do socorro em rota.
    F_COD_EMPRESA BIGINT := (SELECT COD_EMPRESA
                             FROM UNIDADE
                             WHERE CODIGO =
                                   (SELECT COD_UNIDADE FROM SOCORRO_ROTA WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa.
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    RETURN QUERY
        SELECT SR.CODIGO                                                          AS COD_SOCORRO_ROTA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                        AS STATUS_SOCORRO_ROTA,
               V.PLACA :: TEXT                                                    AS PLACA_VEICULO_ABERTURA,
               SRAB.COD_COLABORADOR_ABERTURA                                      AS COD_COLABORADOR_ABERTURA,
               CDAB.NOME :: TEXT                                                  AS NOME_RESPONSAVEL_ABERTURA,
               SRAB.KM_VEICULO_ABERTURA                                           AS KM_VEICULO_COLETADO_ABERTURA,
               SROP.DESCRICAO :: TEXT                                             AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               SRAB.DESCRICAO_PROBLEMA :: TEXT                                    AS DESCRICAO_FORNECIDA_ABERTURA,
               SRAB.PONTO_REFERENCIA :: TEXT                                      AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)    AS DATA_HORA_ABERTURA,
               SRAB.LATITUDE_ABERTURA :: TEXT                                     AS LATITUDE_ABERTURA,
               SRAB.LONGITUDE_ABERTURA :: TEXT                                    AS LONGITUDE_ABERTURA,
               SRAB.ENDERECO_AUTOMATICO :: TEXT                                   AS ENDERECO_AUTOMATICO_ABERTURA,
               SRAB.MARCA_DEVICE_ABERTURA :: TEXT                                 AS MARCA_APARELHO_ABERTURA,
               SRAB.MODELO_DEVICE_ABERTURA :: TEXT                                AS MODELO_APARELHO_ABERTURA,
               SRAB.DEVICE_IMEI_ABERTURA :: TEXT                                  AS IMEI_APARELHO_ABERTURA,
               SRAB.URL_FOTO_1_ABERTURA :: TEXT                                   AS URL_FOTO_1_ABERTURA,
               SRAB.URL_FOTO_2_ABERTURA :: TEXT                                   AS URL_FOTO_2_ABERTURA,
               SRAB.URL_FOTO_3_ABERTURA :: TEXT                                   AS URL_FOTO_3_ABERTURA,
               SRAT.COD_COLABORADOR_ATENDIMENTO                                   AS COD_COLABORADOR_ATENDIMENTO,
               CDAT.NOME :: TEXT                                                  AS NOME_RESPONSAVEL_ATENDIMENTO,
               SRAT.OBSERVACAO_ATENDIMENTO :: TEXT                                AS OBSERVACAO_ATENDIMENTO,
               SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_ATENDIMENTO,
               SRAT.LATITUDE_ATENDIMENTO :: TEXT                                  AS LATITUDE_ATENDIMENTO,
               SRAT.LONGITUDE_ATENDIMENTO :: TEXT                                 AS LONGITUDE_ATENDIMENTO,
               SRAT.ENDERECO_AUTOMATICO :: TEXT                                   AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT                              AS MARCA_APARELHO_ATENDIMENTO,
               SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT                             AS MODELO_APARELHO_ATENDIMENTO,
               SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT                               AS IMEI_APARELHO_ATENDIMENTO,
               SRI.COD_COLABORADOR_INVALIDACAO                                    AS COD_COLABORADOR_INVALIDACAO,
               CDI.NOME :: TEXT                                                   AS NOME_RESPONSAVEL_INVALIDACAO,
               SRI.MOTIVO_INVALIDACAO :: TEXT                                     AS MOTIVO_INVALIDACAO,
               SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)  AS DATA_HORA_INVALIDACAO,
               SRI.LATITUDE_INVALIDACAO :: TEXT                                   AS LATITUDE_INVALIDACAO,
               SRI.LONGITUDE_INVALIDACAO :: TEXT                                  AS LONGITUDE_INVALIDACAO,
               SRI.ENDERECO_AUTOMATICO :: TEXT                                    AS ENDERECO_AUTOMATICO_INVALIDACAO,
               SRI.MARCA_DEVICE_INVALIDACAO :: TEXT                               AS MARCA_APARELHO_INVALIDACAO,
               SRI.MODELO_DEVICE_INVALIDACAO :: TEXT                              AS MODELO_APARELHO_INVALIDACAO,
               SRI.DEVICE_IMEI_INVALIDACAO :: TEXT                                AS IMEI_APARELHO_INVALIDACAO,
               SRI.URL_FOTO_1_INVALIDACAO :: TEXT                                 AS URL_FOTO_1_INVALIDACAO,
               SRI.URL_FOTO_2_INVALIDACAO :: TEXT                                 AS URL_FOTO_2_INVALIDACAO,
               SRI.URL_FOTO_3_INVALIDACAO :: TEXT                                 AS URL_FOTO_3_INVALIDACAO,
               SRF.COD_COLABORADOR_FINALIZACAO                                    AS COD_COLABORADOR_FINALIZACAO,
               CDF.NOME :: TEXT                                                   AS NOME_RESPONSAVEL_FINALIZACAO,
               SRF.OBSERVACAO_FINALIZACAO :: TEXT                                 AS OBSERVACAO_FINALIZACAO,
               SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)  AS DATA_HORA_FINALIZACAO,
               SRF.LATITUDE_FINALIZACAO :: TEXT                                   AS LATITUDE_FINALIZACAO,
               SRF.LONGITUDE_FINALIZACAO :: TEXT                                  AS LONGITUDE_FINALIZACAO,
               SRF.ENDERECO_AUTOMATICO :: TEXT                                    AS ENDERECO_AUTOMATICO_FINALIZACAO,
               SRF.MARCA_DEVICE_FINALIZACAO :: TEXT                               AS MARCA_APARELHO_FINALIZACAO,
               SRF.MODELO_DEVICE_FINALIZACAO :: TEXT                              AS MODELO_APARELHO_FINALIZACAO,
               SRF.DEVICE_IMEI_FINALIZACAO :: TEXT                                AS IMEI_APARELHO_FINALIZACAO,
               SRF.URL_FOTO_1_FINALIZACAO :: TEXT                                 AS URL_FOTO_1_FINALIZACAO,
               SRF.URL_FOTO_2_FINALIZACAO :: TEXT                                 AS URL_FOTO_2_FINALIZACAO,
               SRF.URL_FOTO_3_FINALIZACAO :: TEXT                                 AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.CODIGO = SRAB.COD_SOCORRO_ROTA
                 JOIN VEICULO_DATA V ON V.CODIGO = SRAB.COD_VEICULO_PROBLEMA
                 JOIN COLABORADOR_DATA CDAB ON CDAB.CODIGO = SRAB.COD_COLABORADOR_ABERTURA
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SR.CODIGO = SRAT.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              CDAT.CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
                 LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
                 LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
        WHERE SR.CODIGO = F_COD_SOCORRO_ROTA;
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível encontrar esse socorro');
    END IF;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2482
-- Implementa melhorias na function de deletar um colaborador.
-- Deleta estrutura antiga da function.
DROP FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_COD_UNIDADE BIGINT, F_COD_COLABORADOR BIGINT, F_CPF BIGINT,
    OUT AVISO_COLABORADOR_DELETADO TEXT);

-- Cria nova estrutura da function.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_CPF_COLABORADOR BIGINT,
                                                                       F_COD_COLABORADOR BIGINT,
                                                                       F_COD_UNIDADE BIGINT,
                                                                       OUT AVISO_COLABORADOR_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    F_COD_EMPRESA          BIGINT := (SELECT U.COD_EMPRESA
                                      FROM UNIDADE U
                                      WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    -- Verifica integridade de colaborador com unidade.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(F_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Deleta colaborador.
    UPDATE COLABORADOR_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_COD_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CPF = F_CPF_COLABORADOR;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %',
            F_COD_COLABORADOR, F_CPF_COLABORADOR, F_COD_UNIDADE;
    END IF;

    -- Desloga colaborador.
    DELETE FROM TOKEN_AUTENTICACAO WHERE COD_COLABORADOR = F_COD_COLABORADOR AND CPF_COLABORADOR = F_CPF_COLABORADOR;

    SELECT 'COLABORADOR COM CÓDIGO: '
               || F_COD_COLABORADOR
               || ', CPF: '
               || F_CPF_COLABORADOR
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ' DELETADO COM SUCESSO!'
    INTO AVISO_COLABORADOR_DELETADO;
END
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ########################## Adiciona quantidade de permissões ao buscar cargos de uma unidade ########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2532
DROP FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS_UNIDADE(BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS_UNIDADE(
    F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_CARGO      BIGINT,
                NOME_CARGO     TEXT,
                QTD_PERMISSOES BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH QTD_PERMISSOES AS (
            SELECT DISTINCT COUNT(CFPV11.COD_FUNCAO_COLABORADOR) AS QTD_PERMISSOES_CARGO,
                            COD_FUNCAO_COLABORADOR               AS COD_FUNCAO_COLABORADOR,
                            COD_UNIDADE                          AS COD_UNIDADE
            FROM CARGO_FUNCAO_PROLOG_V11 CFPV11
            WHERE CFPV11.COD_UNIDADE = F_COD_UNIDADE
            GROUP BY CFPV11.COD_FUNCAO_COLABORADOR, CFPV11.COD_UNIDADE
        )
        SELECT F.CODIGO                             AS COD_CARGO,
               F.NOME :: TEXT                       AS NOME_CARGO,
               COALESCE(QP.QTD_PERMISSOES_CARGO, 0) AS QTD_PERMISSOES
        FROM FUNCAO F
                 JOIN UNIDADE U ON U.COD_EMPRESA = F.COD_EMPRESA
                 LEFT JOIN QTD_PERMISSOES QP ON QP.COD_UNIDADE = U.CODIGO AND QP.COD_FUNCAO_COLABORADOR = F.CODIGO
        WHERE U.CODIGO = F_COD_UNIDADE
        GROUP BY F.CODIGO, F.NOME, QP.QTD_PERMISSOES_CARGO
        ORDER BY F.NOME ASC, F.CODIGO, QP.QTD_PERMISSOES_CARGO;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- Aqui apenas para histórico, já foi rodado em prod em 20/02/20.

-- Sobre:
-- Esta function gera um erro genérico caso a funcionalidade de socorro em rota não esteja liberada para a empresa.
--
-- Histórico:
-- 2020-02-11 -> Function criada (wvinim).
-- CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA BIGINT)
--     RETURNS VOID
--     LANGUAGE PLPGSQL
-- AS
-- $$
-- DECLARE
--     F_COD_EMPRESAS_LIBERADAS BIGINT[] := (SELECT ARRAY_AGG(COD_EMPRESA) FROM SOCORRO_ROTA_EMPRESA_LIBERADA);
-- BEGIN
--     -- Por enquanto apenas a fadel está utilizando esta funcionalidade.
--     -- TODO: Criar estrutura própria para realizar essa verificação
--     IF NOT F_COD_EMPRESA = ANY (F_COD_EMPRESAS_LIBERADAS) THEN
--         PERFORM THROW_GENERIC_ERROR(
--                         'A funcionalidade de Socorro em Rota não está liberada para a sua empresa, entre em contato com conexao@zalf.com.br para contratar!');
--     END IF;
-- END;
-- $$;
--
-- CREATE TABLE SOCORRO_ROTA_EMPRESA_LIBERADA (
--     COD_EMPRESA BIGINT PRIMARY KEY REFERENCES EMPRESA (CODIGO)
-- );
--
-- SELECT FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(232);
--
-- GRANT SELECT, INSERT, DELETE ON SOCORRO_ROTA_EMPRESA_LIBERADA TO PROLOG_USER_LUCAS;
--
-- CREATE TRIGGER TG_FUNC_AUDIT_SOCORRO_ROTA_EMPRESA_LIBERADA
--   AFTER INSERT OR UPDATE OR DELETE
--   ON SOCORRO_ROTA_EMPRESA_LIBERADA
--   FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ########################## ALTERA FK PARA CONTER COD_UNIDADE E COD_MODELO DE CHECKLIST ##############################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2458

-- No banco de teste haviam 4 inconsistências, no banco de prod rodei a query e não retornou nenhuma, portanto não há o
-- que deletar. (21/02/2020)

-- Query para consultar inconsistências:
/*SELECT CMVT.COD_UNIDADE, CMVT.COD_MODELO FROM
 CHECKLIST_MODELO_VEICULO_TIPO CMVT  EXCEPT DISTINCT SELECT CMD.COD_UNIDADE, CMD.CODIGO FROM CHECKLIST_MODELO_DATA CMD;*/

-- Deleta as FK's antigas
ALTER TABLE CHECKLIST_MODELO_VEICULO_TIPO DROP CONSTRAINT FK_CHECKLIST_MODELO_VEICULO_TIPO_UNIDADE;
ALTER TABLE CHECKLIST_MODELO_VEICULO_TIPO DROP CONSTRAINT FK_CHECKLIST_MODELO_VEICULO_TIPO_CHECKLIST_MODELO;

-- Cria unique em checklist_modelo_data
ALTER TABLE CHECKLIST_MODELO_DATA ADD CONSTRAINT UNICO_MODELO_POR_UNIDADE UNIQUE (CODIGO, COD_UNIDADE);

-- Cria a nova FK.
ALTER TABLE CHECKLIST_MODELO_VEICULO_TIPO ADD CONSTRAINT FK_CHECKLIST_MODELO_UNIDADE FOREIGN KEY
    (COD_UNIDADE, COD_MODELO) REFERENCES CHECKLIST_MODELO_DATA (COD_UNIDADE, CODIGO);
-- #####################################################################################################################
-- #####################################################################################################################
-- Adiciona novas colunas no relatório de cronograma de aferições - PL-2511

-- Dropa a function antiga
DROP FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(BIGINT[],TIMESTAMP WITH TIME ZONE,TIMESTAMP WITH TIME ZONE);

-- Adiciona a nova function para geração do relatório
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(
  F_COD_UNIDADES                BIGINT [],
  F_DATA_HORA_ATUAL_UTC         TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    UNIDADE                              TEXT,
    PLACA                                TEXT,
    "QTD PNEUS APLICADOS"                TEXT,
    "MODELO VEÍCULO"                     TEXT,
    "TIPO VEÍCULO"                       TEXT,
    "STATUS SULCO"                       TEXT,
    "STATUS PRESSÃO"                     TEXT,
    "DATA VENCIMENTO SULCO"              TEXT,
    "DATA VENCIMENTO PRESSÃO"            TEXT,
    "DIAS VENCIMENTO SULCO"              TEXT,
    "DIAS VENCIMENTO PRESSÃO"            TEXT,
    "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
    "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
    "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
    "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
    "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
BEGIN
  RETURN QUERY
  WITH DADOS AS (SELECT
                   U.NOME :: TEXT        AS NOME_UNIDADE,
                   V.PLACA :: TEXT       AS PLACA_VEICULO,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.PLACA = V.PLACA
                    GROUP BY
                      VP.PLACA) :: TEXT  AS QTD_PNEUS_APLICADOS,
                   MV.NOME :: TEXT       AS NOME_MODELO_VEICULO,
                   VT.NOME :: TEXT       AS NOME_TIPO_VEICULO,
                   TO_CHAR(SULCO.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                   TO_CHAR(PRESSAO.DATA_HORA_ULTIMA_AFERICAO_PRESSAO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                   TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO + (PRU.PERIODO_AFERICAO_SULCO ||
                                                               ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO + (PRU.PERIODO_AFERICAO_PRESSAO ||
                                                                   ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_PRESSAO,
                   (PRU.PERIODO_AFERICAO_SULCO -
                    SULCO.DIAS) :: TEXT  AS DIAS_VENCIMENTO_SULCO,
                   (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS) :: TEXT
                                         AS DIAS_VENCIMENTO_PRESSAO,
                   SULCO.DIAS :: TEXT    AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   PRESSAO.DIAS :: TEXT  AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   F_IF(CONFIG.PODE_AFERIR_SULCO OR CONFIG.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_SULCO,
                   F_IF(CONFIG.PODE_AFERIR_PRESSAO OR CONFIG.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_PRESSAO,
                   F_IF(SULCO.DIAS IS NULL, TRUE,
                        FALSE)           AS SULCO_NUNCA_AFERIDO,
                   F_IF(PRESSAO.DIAS IS NULL, TRUE,
                        FALSE)           AS PRESSAO_NUNCA_AFERIDA,
                   F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                        FALSE)           AS AFERICAO_SULCO_VENCIDA,
                   F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                        FALSE)           AS AFERICAO_PRESSAO_VENCIDA
                 FROM VEICULO V
                   JOIN MODELO_VEICULO MV
                     ON MV.CODIGO = V.COD_MODELO
                   JOIN VEICULO_TIPO VT
                     ON VT.CODIGO = V.COD_TIPO
                   JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                     ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                               AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                        AS DATA_ULTIMA_AFERICAO_PRESSAO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                      -- TODO: TENHO DÚVIDAS SOBRE ESSA SUBTRAÇÃO AQUI :thinking_face:
                      EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS PRESSAO ON PRESSAO.PLACA_INTERVALO = V.PLACA
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                             AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                      -- TODO: TENHO DÚVIDAS SOBRE ESSA SUBTRAÇÃO AQUI :thinking_face:
                      EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS SULCO ON SULCO.PLACA_INTERVALO = V.PLACA
                   JOIN PNEU_RESTRICAO_UNIDADE PRU
                     ON PRU.COD_UNIDADE = V.COD_UNIDADE
                   JOIN UNIDADE U
                     ON U.CODIGO = V.COD_UNIDADE
                 WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                 ORDER BY U.CODIGO ASC, V.PLACA ASC)

  -- TODOS OS COALESCE FICAM AQUI.
  SELECT
    D.NOME_UNIDADE                                               AS NOME_UNIDADE,
    D.PLACA_VEICULO                                              AS PLACA_VEICULO,
    COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
    D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
    D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
    CASE
    WHEN D.SULCO_NUNCA_AFERIDO
      THEN 'SULCO NUNCA AFERIDO'
    WHEN NOT D.PODE_AFERIR_SULCO
      THEN 'BLOQUEADO AFERIÇÃO'
    WHEN D.AFERICAO_SULCO_VENCIDA
      THEN 'VENCIDO'
    ELSE 'NO PRAZO'
    END                                                          AS STATUS_SULCO,
    CASE
    WHEN D.PRESSAO_NUNCA_AFERIDA
      THEN 'PRESSÃO NUNCA AFERIDA'
    WHEN NOT D.PODE_AFERIR_PRESSAO
      THEN 'BLOQUEADO AFERIÇÃO'
    WHEN D.AFERICAO_PRESSAO_VENCIDA
      THEN 'VENCIDO'
    ELSE 'NO PRAZO'
    END                                                          AS STATUS_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
    D.DATA_HORA_ULTIMA_AFERICAO_SULCO                            AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
    D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO                          AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
    TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
  FROM DADOS D;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ##################  Alterar estrutura para garantir que pneus serão aplicados a posições válidas  ###################
-- #####################################################################################################################
-- #####################################################################################################################
-- ṔL-1965

-- Dropa as triggers das tabelas relacionadas para evitar criar dados desnecessários.

-- Dropa a trigger de audit na tabela veiculo_data.
DROP TRIGGER TG_FUNC_AUDIT_VEICULO ON VEICULO_DATA;

-- Dropa a trigger de audit na tabela veiculo_pneu.
DROP TRIGGER TG_FUNC_AUDIT_VEICULO_PNEU ON VEICULO_PNEU;

-- Atualiza o status dos pneus que estão em posição inválida.
WITH PNEUS_ERRADOS AS (
SELECT VP.COD_PNEU
FROM VEICULO_PNEU VP
         JOIN VEICULO_DATA VD ON VD.PLACA = VP.PLACA AND VD.COD_UNIDADE = VP.COD_UNIDADE
         JOIN VEICULO_TIPO VT ON VT.CODIGO = VD.COD_TIPO AND VT.COD_EMPRESA = VD.COD_EMPRESA
WHERE NOT EXISTS(SELECT *
                 FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                 WHERE VDPP.COD_DIAGRAMA = VT.COD_DIAGRAMA
                   AND VDPP.POSICAO_PROLOG = VP.POSICAO)
    )
UPDATE PNEU_DATA SET STATUS = 'ESTOQUE' FROM PNEUS_ERRADOS WHERE CODIGO = PNEUS_ERRADOS.COD_PNEU;


-- Deleta logicamente os serviços dos pneus desvinculados.
WITH PNEUS_ERRADOS AS (
SELECT VP.COD_PNEU
FROM VEICULO_PNEU VP
         JOIN VEICULO_DATA VD ON VD.PLACA = VP.PLACA AND VD.COD_UNIDADE = VP.COD_UNIDADE
         JOIN VEICULO_TIPO VT ON VT.CODIGO = VD.COD_TIPO AND VT.COD_EMPRESA = VD.COD_EMPRESA
WHERE NOT EXISTS(SELECT *
                 FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                 WHERE VDPP.COD_DIAGRAMA = VT.COD_DIAGRAMA
                   AND VDPP.POSICAO_PROLOG = VP.POSICAO)
    )
UPDATE AFERICAO_MANUTENCAO_DATA
SET
    DELETADO = TRUE,
    DATA_HORA_DELETADO = NOW(),
    PG_USERNAME_DELECAO = SESSION_USER
WHERE DELETADO = FALSE AND COD_PNEU IN (SELECT PE.COD_PNEU FROM PNEUS_ERRADOS PE);

-- Deleta o vínculo entre pneus em posição errada e veículos.
DELETE FROM VEICULO_PNEU VP
USING ( SELECT VP.COD_PNEU
FROM VEICULO_PNEU VP
         JOIN VEICULO_DATA VD ON VD.PLACA = VP.PLACA AND VD.COD_UNIDADE = VP.COD_UNIDADE
         JOIN VEICULO_TIPO VT ON VT.CODIGO = VD.COD_TIPO AND VT.COD_EMPRESA = VD.COD_EMPRESA
WHERE NOT EXISTS(SELECT *
                 FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                 WHERE VDPP.COD_DIAGRAMA = VT.COD_DIAGRAMA
                   AND VDPP.POSICAO_PROLOG = VP.POSICAO) ) PNEUS_ERRADOS
WHERE VP.COD_PNEU = PNEUS_ERRADOS.COD_PNEU;

-- Adiciona coluna para o código de diagrama na tabela veiculo_data.
ALTER TABLE VEICULO_DATA
	ADD COD_DIAGRAMA BIGINT;

-- Adiciona coluna para o código de diagrama na tabela veiculo_pneu.
ALTER TABLE VEICULO_PNEU
	ADD COD_DIAGRAMA BIGINT;

-- Adiciona um diagrama aos tipos de veículos que não tinham
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 5 WHERE CODIGO = 556 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 12 WHERE CODIGO = 583 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 382 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 363 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 12 WHERE CODIGO = 366 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 368 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 1 WHERE CODIGO = 373 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 13 WHERE CODIGO = 389 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 396 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 1 WHERE CODIGO = 411 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 417 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 418 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 2 WHERE CODIGO = 483 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 3 WHERE CODIGO = 484 AND COD_DIAGRAMA IS NULL;
UPDATE VEICULO_TIPO SET COD_DIAGRAMA = 8 WHERE CODIGO = 533 AND COD_DIAGRAMA IS NULL;

-- Atualiza os dados da coluna cod_diagrama de acordo com o tipo do veículo na tabela veiculo_data.
UPDATE VEICULO_DATA VD SET COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA FROM VEICULO_TIPO VT WHERE VT.CODIGO = VD.COD_TIPO);

-- Atualiza os dados da coluna cod_diagrama de acordo com o tipo do veículo na tabela veiculo_pneu.
UPDATE VEICULO_PNEU VP
SET COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                    FROM VEICULO_TIPO VT
                             JOIN VEICULO_DATA VD ON VT.CODIGO = VD.COD_TIPO AND VT.COD_EMPRESA = VD.COD_EMPRESA
                    WHERE VD.PLACA = VP.PLACA AND VD.COD_UNIDADE = VP.COD_UNIDADE);

-- Torna o código de diagrama not null na tabela veiculo_pneu
ALTER TABLE VEICULO_PNEU ALTER COLUMN COD_DIAGRAMA SET NOT NULL;

-- Seta a coluna de diagrama como not null na tabela veiculo_tipo
ALTER TABLE VEICULO_TIPO ALTER COLUMN COD_DIAGRAMA SET NOT NULL;

-- Seta a coluna de diagrama como not null na tabela veiculo_data
ALTER TABLE VEICULO_DATA ALTER COLUMN COD_DIAGRAMA SET NOT NULL;

-- Dropa a constraint de veículo e unidade na tabela veiculo_pneu.
-- constraint fk_veiculo_pneu_veiculo
--     foreign key (placa, cod_unidade) references veiculo_data (placa, cod_unidade)
--         deferrable
ALTER TABLE VEICULO_PNEU DROP CONSTRAINT FK_VEICULO_PNEU_VEICULO;

-- Dropa a constraint de veículo e unidade na tabela veiculo_data.
ALTER TABLE VEICULO_DATA DROP CONSTRAINT UNIQUE_VEICULO_UNIDADE;

-- Dropa a constraint de tipo e empresa na tabela veiculo_data.
-- constraint fk_veiculo_tipo
--         foreign key (cod_tipo, cod_empresa) references veiculo_tipo (codigo, cod_empresa)
ALTER TABLE VEICULO_DATA DROP CONSTRAINT FK_VEICULO_TIPO;

-- Dropa o constraint de código e código de empresa na tabela veiculo_tipo.
ALTER TABLE VEICULO_TIPO DROP CONSTRAINT UNIQUE_VEICULO_TIPO_EMPRESA;

-- Cria a constraint de código, empresa e diagrama na tabela veiculo_tipo.
ALTER TABLE VEICULO_TIPO
	ADD CONSTRAINT UNIQUE_VEICULO_TIPO_EMPRESA
		UNIQUE (CODIGO, COD_EMPRESA, COD_DIAGRAMA);

-- Cria a constraint de veículo, unidade e diagrama na tabela veiculo_data.
ALTER TABLE VEICULO_DATA
	ADD CONSTRAINT UNIQUE_VEICULO_UNIDADE_DIAGRAMA
		UNIQUE (PLACA, COD_UNIDADE, COD_DIAGRAMA);

-- Cria a FK de veículo, unidade e diagrama entre a veiculo_pneu e veiculo_data.
ALTER TABLE VEICULO_PNEU
	ADD CONSTRAINT FK_VEICULO_PNEU_VEICULO
		FOREIGN KEY (PLACA, COD_UNIDADE, COD_DIAGRAMA) REFERENCES VEICULO_DATA (PLACA, COD_UNIDADE, COD_DIAGRAMA)
			DEFERRABLE;

-- Cria a FK de tipo, empresa e diagrama na entre a veiculo_data e veiculo_tipo.
ALTER TABLE VEICULO_DATA
    ADD CONSTRAINT FK_VEICULO_TIPO
        FOREIGN KEY (COD_TIPO, COD_EMPRESA, COD_DIAGRAMA) REFERENCES VEICULO_TIPO (CODIGO, COD_EMPRESA, COD_DIAGRAMA);

-- Cria a FK de diagrama e posicao entre a veiculo_pneu e veiculo_diagrama_posicao_prolog.
ALTER TABLE VEICULO_PNEU
    ADD CONSTRAINT FK_VEICULO_DIAGRAMA_POSICAO_PROLOG
        FOREIGN KEY (COD_DIAGRAMA, POSICAO) REFERENCES VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG);

-- Recria trigger de audit na tabela veiculo_data.
CREATE TRIGGER TG_FUNC_AUDIT_VEICULO
  AFTER INSERT OR UPDATE OR DELETE
  ON VEICULO_DATA
  FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

-- Recria trigger de audit na tabela veiculo_pneu.
CREATE TRIGGER TG_FUNC_AUDIT_VEICULO_PNEU
  AFTER INSERT OR UPDATE OR DELETE
  ON VEICULO_PNEU
  FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

-- Recria a view de veículo para adicionar o código de diagrama
CREATE OR REPLACE VIEW VEICULO
            (PLACA, COD_UNIDADE, COD_EMPRESA, KM, STATUS_ATIVO, COD_TIPO, COD_MODELO, COD_EIXOS,
             DATA_HORA_CADASTRO, COD_UNIDADE_CADASTRO, CODIGO, COD_DIAGRAMA)
AS
SELECT V.PLACA,
       V.COD_UNIDADE,
       V.COD_EMPRESA,
       V.KM,
       V.STATUS_ATIVO,
       V.COD_TIPO,
       V.COD_MODELO,
       V.COD_EIXOS,
       V.DATA_HORA_CADASTRO,
       V.COD_UNIDADE_CADASTRO,
       V.CODIGO,
       V.COD_DIAGRAMA
FROM VEICULO_DATA V
WHERE V.DELETADO = FALSE;

-- Cria a function que insere um veículo
CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA           BIGINT;
    F_STATUS_ATIVO CONSTANT BOOLEAN := TRUE;
    F_COD_DIAGRAMA          BIGINT;
    F_COD_VEICULO_PROLOG    BIGINT;
BEGIN
    -- Busca o código da empresa de acordo com a unidade
    F_COD_EMPRESA := (SELECT U.COD_EMPRESA
                      FROM UNIDADE U
                      WHERE U.CODIGO = F_COD_UNIDADE);

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL < 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'A quilometragem do veículo não pode ser um número negativo.');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = F_COD_EMPRESA
                            AND MV.CODIGO = F_COD_MODELO))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Por favor, verifique o modelo do veículo e tente novamente.');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO
                            AND VT.COD_EMPRESA = F_COD_EMPRESA))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Por favor, verifique o tipo do veículo e tente novamente.');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo.
    F_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_COD_TIPO
                         AND VT.COD_EMPRESA = F_COD_EMPRESA);

    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO VEICULO(COD_EMPRESA,
                             COD_UNIDADE,
                             PLACA,
                             KM,
                             STATUS_ATIVO,
                             COD_TIPO,
                             COD_MODELO,
                             COD_DIAGRAMA,
                             COD_UNIDADE_CADASTRO)
    VALUES (F_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            F_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            F_COD_DIAGRAMA,
            F_COD_UNIDADE)
    RETURNING CODIGO INTO F_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF F_COD_VEICULO_PROLOG IS NULL OR F_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN F_COD_VEICULO_PROLOG;
END;
$$;

-- Cria function que atualiza um veículo.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_ATUALIZA_VEICULO(F_PLACA TEXT,
                                                         F_NOVO_KM BIGINT,
                                                         F_NOVO_COD_MODELO BIGINT,
                                                         F_NOVO_COD_TIPO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA        BIGINT;
    F_COD_TIPO_ANTIGO    BIGINT;
    F_COD_DIAGRAMA       BIGINT;
    F_COD_VEICULO        BIGINT;
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
    -- Validamos se o KM foi inputado corretamente.
    IF (F_NOVO_KM < 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'A quilometragem do veículo não pode ser um número negativo.');
    END IF;

    F_COD_EMPRESA := (SELECT VD.COD_EMPRESA
                      FROM VEICULO_DATA VD
                      WHERE VD.PLACA = F_PLACA);

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = F_COD_EMPRESA
                            AND MV.CODIGO = F_NOVO_COD_MODELO))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Por favor, verifique o modelo do veículo e tente novamente.');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_NOVO_COD_TIPO
                            AND VT.COD_EMPRESA = F_COD_EMPRESA))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Por favor, verifique o tipo do veículo e tente novamente.');
    END IF;

    F_COD_TIPO_ANTIGO := (SELECT VD.COD_TIPO
                          FROM VEICULO_DATA VD
                          WHERE VD.PLACA = F_PLACA);

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    IF ((F_COD_TIPO_ANTIGO <> F_NOVO_COD_TIPO)
        AND (SELECT COUNT(VP.*) FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'O tipo do veículo não pode ser alterado se a placa contém pneus aplicados.');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo.
    F_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_NOVO_COD_TIPO
                         AND VT.COD_EMPRESA = F_COD_EMPRESA);

    UPDATE VEICULO
    SET KM           = F_NOVO_KM,
        COD_MODELO   = F_NOVO_COD_MODELO,
        COD_TIPO     = F_NOVO_COD_TIPO,
        COD_DIAGRAMA = F_COD_DIAGRAMA
    WHERE PLACA = F_PLACA
    RETURNING CODIGO INTO F_COD_VEICULO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O UPDATE NA TABELA DE VEÍCULOS OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS IS NULL OR F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar a placa "%"', F_PLACA;
    END IF;

    RETURN F_COD_VEICULO;
END;
$$;

-- Cria function que faz o vínculo entre um pneu e um veículo.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO_PNEU(F_COD_UNIDADE BIGINT,
                                                            F_PLACA TEXT,
                                                            F_COD_PNEU BIGINT,
                                                            F_POSICAO BIGINT)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA  BIGINT;
    F_COD_TIPO     BIGINT;
    F_COD_DIAGRAMA BIGINT;
BEGIN
    -- Busca o código da empresa de acordo com a unidade
    F_COD_EMPRESA := (SELECT U.COD_EMPRESA
                      FROM UNIDADE U
                      WHERE U.CODIGO = F_COD_UNIDADE);

    -- Busca o código do tipo de veículo pela placa
    F_COD_TIPO := (SELECT VD.COD_TIPO
                   FROM VEICULO_DATA VD
                   WHERE VD.PLACA = F_PLACA);

    -- Busca o código do diagrama de acordo com o tipo de veículo
    F_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM PUBLIC.VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_COD_TIPO
                         AND VT.COD_EMPRESA = F_COD_EMPRESA);

    IF F_COD_DIAGRAMA IS NULL OR F_COD_DIAGRAMA <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível realizar o vínculo entre veículo e pneu.');
    END IF;

    -- Aqui devemos apenas inserir o veículo no prolog.
    INSERT INTO VEICULO_PNEU(COD_UNIDADE,
                             PLACA,
                             COD_PNEU,
                             POSICAO,
                             COD_DIAGRAMA)
    VALUES (F_COD_UNIDADE,
            F_PLACA,
            F_COD_PNEU,
            F_POSICAO,
            F_COD_DIAGRAMA);

    -- Validamos se houve alguma inserção ou atualização dos valores.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível realizar o vínculo entre veículo e pneu.');
    END IF;

    RETURN FOUND;
END;
$$;

-- Atualiza function de suporte que realiza tranferência de veículos entre empresas
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_TRANSFERE_VEICULO_ENTRE_EMPRESAS(F_PLACA_VEICULO VARCHAR(7),
                                                                                 F_COD_EMPRESA_ORIGEM BIGINT,
                                                                                 F_COD_UNIDADE_ORIGEM BIGINT,
                                                                                 F_COD_EMPRESA_DESTINO BIGINT,
                                                                                 F_COD_UNIDADE_DESTINO BIGINT,
                                                                                 F_COD_MODELO_VEICULO_DESTINO BIGINT,
                                                                                 F_COD_TIPO_VEICULO_DESTINO BIGINT,
                                                                                 OUT VEICULO_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_NOME_EMPRESA_DESTINO                           VARCHAR(255) := (SELECT E.NOME
                                                                      FROM EMPRESA E
                                                                      WHERE E.CODIGO = F_COD_EMPRESA_DESTINO);
    F_NOME_UNIDADE_DESTINO                           VARCHAR(255) := (SELECT U.NOME
                                                                      FROM UNIDADE U
                                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO);
    F_LISTA_COD_AFERICAO_PLACA                       BIGINT[];
    F_COD_AFERICAO_FOREACH                           BIGINT;
    F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO          BIGINT[];
    F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES           BIGINT;
    F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES BIGINT;


BEGIN
    --VERIFICA SE EMPRESA ORIGEM POSSUI UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_ORIGEM);

    --VERIFICA SE EMPRESA DESTINO POSSUI UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

    --VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS.
    PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(F_COD_EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

    --VERIFICA SE VEICULO EXISTE
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE_ORIGEM, F_PLACA_VEICULO);

    --VERIFICA SE A PLACA POSSUI PNEUS.
    IF EXISTS(SELECT VP.COD_PNEU
              FROM VEICULO_PNEU VP
              WHERE VP.PLACA = F_PLACA_VEICULO
                AND VP.COD_UNIDADE = F_COD_UNIDADE_ORIGEM)
    THEN
        RAISE EXCEPTION 'ERRO! A PLACA: % POSSUI PNEUS VINCULADOS, FAVOR REMOVER OS PNEUS DO MESMO', F_PLACA_VEICULO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI TIPO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(
            SELECT VT.CODIGO
            FROM VEICULO_TIPO VT
            WHERE VT.COD_EMPRESA = F_COD_EMPRESA_DESTINO
              AND VT.CODIGO = F_COD_TIPO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO TIPO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_TIPO_VEICULO_DESTINO,
            F_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE O TIPO DE VEÍCULO INFORMADO TEM O MESMO DIAGRAMA DO VEÍCULO
    IF NOT EXISTS(
            SELECT VD.CODIGO
            FROM VEICULO_DATA VD
                     JOIN VEICULO_TIPO VT ON VD.COD_DIAGRAMA = VT.COD_DIAGRAMA
            WHERE VD.PLACA = F_PLACA_VEICULO
              AND VT.CODIGO = F_COD_TIPO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O DIAGRAMA DO TIPO: % É DIFERENTE DO VEÍCULO: %', F_COD_TIPO_VEICULO_DESTINO,
            F_PLACA_VEICULO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI MODELO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(SELECT MV.CODIGO
                  FROM MODELO_VEICULO MV
                  WHERE MV.COD_EMPRESA = F_COD_EMPRESA_DESTINO
                    AND MV.CODIGO = F_COD_MODELO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO MODELO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_MODELO_VEICULO_DESTINO,
            F_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO
              FROM AFERICAO A
              WHERE A.PLACA_VEICULO = F_PLACA_VEICULO)
    THEN
        --ENTÃO COLETAMOS TODOS OS CÓDIGOS DAS AFERIÇÕES QUE A PLACA POSSUI E ADICIONAMOS NO ARRAY.
        SELECT DISTINCT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO A
        WHERE A.PLACA_VEICULO = F_PLACA_VEICULO
        INTO F_LISTA_COD_AFERICAO_PLACA;

        --LAÇO FOR PARA PERCORRER TODOS OS VALORES EM F_LISTA_COD_AFERICAO_PLACA.
        FOREACH F_COD_AFERICAO_FOREACH IN ARRAY F_LISTA_COD_AFERICAO_PLACA
            LOOP
                --PARA CADA VALOR EM: F_LISTA_COD_AFERICAO_PLACA
                IF EXISTS(SELECT AM.COD_AFERICAO
                          FROM AFERICAO_MANUTENCAO AM
                          WHERE AM.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                            AND AM.DATA_HORA_RESOLUCAO IS NULL
                            AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                            AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE)
                THEN
                    --COLETA O(S) COD_PNEU CORRESPONDENTES AO COD_AFERICAO.
                    SELECT ARRAY_AGG(AM.COD_PNEU)
                    FROM AFERICAO_MANUTENCAO AM
                    WHERE AM.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND AM.DATA_HORA_RESOLUCAO IS NULL
                      AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                      AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                    INTO F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO;

                    --DELETA AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_MANUTENCAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);

                    --DELETA AFERICAO EM AFERICAO_VALORES_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_VALORES_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);
                END IF;
            END LOOP;

        --SE, E SOMENTE SE, A AFERIÇÃO POSSUIR TODOS OS VALORES EXCLUÍDOS, DEVE-SE EXCLUIR TODA A AFERIÇÃO.
        --SENÃO, A AFERIÇÃO CONTINUA EXISTINDO.
        FOREACH F_COD_AFERICAO_FOREACH IN ARRAY F_LISTA_COD_AFERICAO_PLACA
            LOOP
                F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                          FROM AFERICAO_VALORES_DATA AVD
                                                          WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH);

                F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                                    FROM AFERICAO_VALORES_DATA AVD
                                                                    WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                                                                      AND AVD.DELETADO IS TRUE);

                --SE A QUANTIDADE DE UM COD_AFERICAO EM AFERICAO_VALORES_DATA FOR IGUAL A QUANTIDADE DE UM COD_AFERICAO
                --DELETADO EM AFERICAO_VALORES_DATA, DEVEMOS EXCLUIR A AFERIÇÃO, POIS, TODOS SEUS VALORES FORAM
                --DELETADOS.
                IF (F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES =
                    F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND CODIGO = F_COD_AFERICAO_FOREACH;
                END IF;
            END LOOP;
    END IF;

    --REALIZA TRANSFERÊNCIA.
    UPDATE VEICULO_DATA
    SET COD_EMPRESA = F_COD_EMPRESA_DESTINO,
        COD_UNIDADE = F_COD_UNIDADE_DESTINO,
        COD_TIPO    = F_COD_TIPO_VEICULO_DESTINO,
        COD_MODELO  = F_COD_MODELO_VEICULO_DESTINO
    WHERE COD_EMPRESA = F_COD_EMPRESA_ORIGEM
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM
      AND PLACA = F_PLACA_VEICULO;

    --MENSAGEM DE SUCESSO.
    SELECT 'VEÍCULO TRANSFERIDO COM SUCESSO! O VEÍCULO COM PLACA: ' || F_PLACA_VEICULO ||
           ' FOI TRANSFERIDO PARA A EMPRESA ' || F_NOME_EMPRESA_DESTINO || ' JUNTO A UNIDADE ' ||
           F_NOME_UNIDADE_DESTINO || '.'
    INTO VEICULO_TRANSFERIDO;
END
$$;

-- Deleta function que sobrescreve o veículo
DROP FUNCTION INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(TEXT, BIGINT, BIGINT, BIGINT, BIGINT);

-- Recria a function para sobrescrever um veículo já cadastrado (Integração)
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(F_PLACA_VEICULO TEXT,
                                                           F_COD_UNIDADE_VEICULO BIGINT,
                                                           F_KM_ATUAL_VEICULO BIGINT,
                                                           F_COD_TIPO_VEICULO BIGINT,
                                                           F_COD_DIAGRAMA_VEICULO BIGINT,
                                                           F_COD_MODELO_VEICULO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO CONSTANT BIGINT := (SELECT COD_EMPRESA
                                            FROM UNIDADE
                                            WHERE CODIGO = F_COD_UNIDADE_VEICULO);
    COD_UNIDADE_ATUAL_VEICULO CONSTANT BIGINT := (SELECT COD_UNIDADE
                                                  FROM PUBLIC.VEICULO
                                                  WHERE PLACA = F_PLACA_VEICULO
                                                    AND COD_EMPRESA = COD_EMPRESA_VEICULO);
BEGIN
    -- Devemos tratar os serviços abertos para o veículo (setar fechado_integracao), apenas se a unidade mudar.
    IF (COD_UNIDADE_ATUAL_VEICULO <> F_COD_UNIDADE_VEICULO)
    THEN
        PERFORM INTEGRACAO.FUNC_VEICULO_DELETA_SERVICOS_ABERTOS_PLACA(F_PLACA_VEICULO,
                                                                      F_COD_UNIDADE_VEICULO);
    END IF;

    UPDATE PUBLIC.VEICULO
    SET COD_UNIDADE  = F_COD_UNIDADE_VEICULO,
        KM           = F_KM_ATUAL_VEICULO,
        COD_TIPO     = F_COD_TIPO_VEICULO,
        COD_DIAGRAMA = F_COD_DIAGRAMA_VEICULO,
        COD_MODELO   = F_COD_MODELO_VEICULO
    WHERE PLACA = F_PLACA_VEICULO
      AND COD_EMPRESA = COD_EMPRESA_VEICULO;
END;
$$;

-- Function para inserir um veículo (Integração)
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                                         F_PLACA_VEICULO_CADASTRADO TEXT,
                                                                         F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                                         F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                         F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT TI.COD_EMPRESA
                                                   FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                   WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_DIAGRAMA_VEICULO               BIGINT;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO)
        <>
        (SELECT TI.COD_EMPRESA
         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT(
                                '[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                                 confira se está usando o token correto',
                                F_TOKEN_INTEGRACAO,
                                F_COD_UNIDADE_VEICULO_ALOCADO));
    END IF;

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (VEICULO_ESTA_NO_PROLOG AND NOT DEVE_SOBRESCREVER_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                               F_PLACA_VEICULO_CADASTRADO));
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo
    COD_DIAGRAMA_VEICULO := (SELECT VT.COD_DIAGRAMA
                             FROM PUBLIC.VEICULO_TIPO VT
                             WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                               AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO);

    IF (VEICULO_ESTA_NO_PROLOG AND DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Buscamos o código do veículo que será sobrescrito.
        SELECT V.CODIGO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO_CADASTRADO
          AND V.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO COD_VEICULO_PROLOG;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        PERFORM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO_CADASTRADO);

        -- Sebrescrevemos os dados do veículo.
        PERFORM INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(
                        F_PLACA_VEICULO_CADASTRADO,
                        F_COD_UNIDADE_VEICULO_ALOCADO,
                        F_KM_ATUAL_VEICULO_CADASTRADO,
                        F_COD_TIPO_VEICULO_CADASTRADO,
                        COD_DIAGRAMA_VEICULO,
                        F_COD_MODELO_VEICULO_CADASTRADO);

    ELSE
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                COD_DIAGRAMA_VEICULO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO)
        RETURNING CODIGO INTO COD_VEICULO_PROLOG;
    END IF;

    IF (DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_VEICULO_CADASTRO_PROLOG = COD_VEICULO_PROLOG,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_VEICULO_ALOCADO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_VEICULO_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;

-- Function para atualizar um veículo (Integração)
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_ATUALIZA_VEICULO_PROLOG(F_COD_UNIDADE_ORIGINAL_ALOCADO BIGINT,
                                                                           F_PLACA_ORIGINAL_VEICULO TEXT,
                                                                           F_NOVO_COD_UNIDADE_ALOCADO BIGINT,
                                                                           F_NOVA_PLACA_VEICULO TEXT,
                                                                           F_NOVO_KM_VEICULO BIGINT,
                                                                           F_NOVO_COD_MODELO_VEICULO BIGINT,
                                                                           F_NOVO_COD_TIPO_VEICULO BIGINT,
                                                                           F_DATA_HORA_EDICAO_VEICULO TIMESTAMP WITH TIME ZONE,
                                                                           F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO     BIGINT := (SELECT TI.COD_EMPRESA
                                       FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                       WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    COD_TIPO_VEICULO_ANTIGO BIGINT := (SELECT VD.COD_TIPO
                                       FROM PUBLIC.VEICULO_DATA VD
                                       WHERE VD.PLACA = F_PLACA_ORIGINAL_VEICULO);
    COD_VEICULO_PROLOG      BIGINT;
    COD_DIAGRAMA_VEICULO    BIGINT;
    F_QTD_ROWS_ALTERADAS    BIGINT;
BEGIN
    -- Validamos se o usuário trocou a unidade alocada do veículo.
    IF (F_COD_UNIDADE_ORIGINAL_ALOCADO <> F_NOVO_COD_UNIDADE_ALOCADO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    END IF;

    -- Validamos se o usuário trocou a placa do veículo.
    IF (F_PLACA_ORIGINAL_VEICULO <> F_NOVA_PLACA_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE OPERAÇÃO] O ProLog não permite a edição da placa do veículo');
    END IF;

    -- Validamos se a Unidade do veículo trocou
    IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA = F_PLACA_ORIGINAL_VEICULO)
        <> F_COD_UNIDADE_ORIGINAL_ALOCADO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    END IF;

    -- Validamos se a Unidade pertence a mesma empresa do token.
    IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_NOVO_COD_UNIDADE_ALOCADO)
        <>
        (SELECT TI.COD_EMPRESA
         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT(
                                '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
                                F_TOKEN_INTEGRACAO,
                                F_NOVO_COD_UNIDADE_ALOCADO));
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (SELECT NOT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_NOVA_PLACA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', F_NOVA_PLACA_VEICULO));
    END IF;

    -- Validamos se o KM foi inputado corretamente.
    IF (F_NOVO_KM_VEICULO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_NOVO_COD_MODELO_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_NOVO_COD_TIPO_VEICULO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo
    COD_DIAGRAMA_VEICULO := (SELECT VT.COD_DIAGRAMA
                             FROM PUBLIC.VEICULO_TIPO VT
                             WHERE VT.CODIGO = F_NOVO_COD_TIPO_VEICULO
                               AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO);

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    IF ((COD_TIPO_VEICULO_ANTIGO <> F_NOVO_COD_TIPO_VEICULO)
        AND (SELECT COUNT(VP.*) FROM PUBLIC.VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_ORIGINAL_VEICULO) > 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE OPERAÇÃO] O tipo do veículo não pode ser alterado se a placa contém pneus aplicados');
    END IF;

    UPDATE PUBLIC.VEICULO_DATA
    SET KM           = F_NOVO_KM_VEICULO,
        COD_MODELO   = F_NOVO_COD_MODELO_VEICULO,
        COD_TIPO     = F_NOVO_COD_TIPO_VEICULO,
        COD_DIAGRAMA = COD_DIAGRAMA_VEICULO
    WHERE PLACA = F_PLACA_ORIGINAL_VEICULO
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGINAL_ALOCADO
    RETURNING CODIGO INTO COD_VEICULO_PROLOG;

    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_VEICULO
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_VEICULO
      AND PLACA_VEICULO_CADASTRO = F_PLACA_ORIGINAL_VEICULO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O UPDATE NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar a placa "%" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- Já lançado em prod em 04/03/20, aqui apenas para registro.

-- alter table unidade add column latitude_unidade text;
-- alter table unidade add column longitude_unidade text;
--
-- UPDATE public.unidade SET latitude_unidade = '-27.641642', longitude_unidade = ' -48.679314' WHERE codigo = 5;
-- UPDATE public.unidade SET latitude_unidade = '-27.641642', longitude_unidade = ' -48.679314' WHERE codigo = 215;
--
-- DROP FUNCTION FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
--     F_DATA_INICIAL DATE,
--     F_DATA_FINAL DATE,
--     F_STATUS_SOCORRO_ROTA VARCHAR[]);
--
-- CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
--                                                                     F_DATA_INICIAL DATE,
--                                                                     F_DATA_FINAL DATE,
--                                                                     F_STATUS_SOCORRO_ROTA VARCHAR[])
--     RETURNS TABLE
--             (
--                 "UNIDADE"                                      TEXT,
--                 "DISTÂNCIA ENTRE UNIDADE E ABERTURA"           TEXT,
--                 "CÓDIGO SOCORRO ROTA"                          TEXT,
--                 "STATUS SOCORRO ROTA"                          TEXT,
--                 "PLACA VEÍCULO ABERTURA"                       TEXT,
--                 "CÓDIGO COLABORADOR ABERTURA"                  TEXT,
--                 "NOME RESPONSÁVEL ABERTURA"                    TEXT,
--                 "KM VEÍCULO COLETADO ABERTURA"                 TEXT,
--                 "DESCRIÇÃO OPÇÃO PROBLEMA ABERTURA"            TEXT,
--                 "DESCRIÇÃO FORNECIDA ABERTURA"                 TEXT,
--                 "PONTO REFERÊNCIA FORNECIDO ABERTURA"          TEXT,
--                 "DATA/HORA ABERTURA"                           TEXT,
--                 "LATITUDE ABERTURA"                            TEXT,
--                 "LONGITUDE ABERTURA"                           TEXT,
--                 "ENDEREÇO AUTOMÁTICO ABERTURA"                 TEXT,
--                 "MARCA APARELHO ABERTURA"                      TEXT,
--                 "MODELO APARELHO ABERTURA"                     TEXT,
--                 "IMEI APARELHO ABERTURA"                       TEXT,
--                 "URL FOTO 1 ABERTURA"                          TEXT,
--                 "URL FOTO 2 ABERTURA"                          TEXT,
--                 "URL FOTO 3 ABERTURA"                          TEXT,
--                 "CÓDIGO COLABORADOR ATENDIMENTO"               TEXT,
--                 "NOME RESPONSÁVEL ATENDIMENTO"                 TEXT,
--                 "OBSERVAÇÃO ATENDIMENTO"                       TEXT,
--                 "TEMPO ENTRE ABERTURA/ATENDIMENTO HH:MM:SS"    TEXT,
--                 "DATA/HORA ATENDIMENTO"                        TEXT,
--                 "LATITUDE ATENDIMENTO"                         TEXT,
--                 "LONGITUDE ATENDIMENTO"                        TEXT,
--                 "ENDEREÇO AUTOMÁTICO ATENDIMENTO"              TEXT,
--                 "MARCA APARELHO ATENDIMENTO"                   TEXT,
--                 "MODELO APARELHO ATENDIMENTO"                  TEXT,
--                 "IMEI APARELHO ATENDIMENTO"                    TEXT,
--                 "CÓDIGO COLABORADOR INVALIDAÇÃO"               TEXT,
--                 "NOME RESPONSÁVEL INVALIDAÇÃO"                 TEXT,
--                 "MOTIVO INVALIDAÇÃO"                           TEXT,
--                 "TEMPO ENTRE ABERTURA/INVALIDAÇÃO HH:MM:SS"    TEXT,
--                 "TEMPO ENTRE ATENDIMENTO/INVALIDAÇÃO HH:MM:SS" TEXT,
--                 "DATA/HORA INVALIDAÇÃO"                        TEXT,
--                 "LATITUDE INVALIDAÇÃO"                         TEXT,
--                 "LONGITUDE INVALIDAÇÃO"                        TEXT,
--                 "ENDEREÇO AUTOMÁTICO INVALIDAÇÃO"              TEXT,
--                 "MARCA APARELHO INVALIDAÇÃO"                   TEXT,
--                 "MODELO APARELHO INVALIDAÇÃO"                  TEXT,
--                 "IMEI APARELHO INVALIDAÇÃO"                    TEXT,
--                 "URL FOTO 1 INVALIDAÇÃO"                       TEXT,
--                 "URL FOTO 2 INVALIDAÇÃO"                       TEXT,
--                 "URL FOTO 3 INVALIDAÇÃO"                       TEXT,
--                 "CÓDIGO COLABORADOR FINALIZAÇÃO"               TEXT,
--                 "NOME RESPONSÁVEL FINALIZAÇÃO"                 TEXT,
--                 "OBSERVAÇÃO FINALIZAÇÃO"                       TEXT,
--                 "TEMPO ENTRE ATENDIMENTO/FINALIZAÇÃO HH:MM:SS" TEXT,
--                 "DATA/HORA FINALIZAÇÃO"                        TEXT,
--                 "LATITUDE FINALIZAÇÃO"                         TEXT,
--                 "LONGITUDE FINALIZAÇÃO"                        TEXT,
--                 "ENDEREÇO AUTOMÁTICO FINALIZAÇÃO"              TEXT,
--                 "MARCA APARELHO FINALIZAÇÃO"                   TEXT,
--                 "MODELO APARELHO FINALIZAÇÃO"                  TEXT,
--                 "IMEI APARELHO FINALIZAÇÃO"                    TEXT,
--                 "URL FOTO 1 FINALIZAÇÃO"                       TEXT,
--                 "URL FOTO 2 FINALIZAÇÃO"                       TEXT,
--                 "URL FOTO 3 FINALIZAÇÃO"                       TEXT
--             )
--     LANGUAGE PLPGSQL
-- AS
-- $$
-- DECLARE
--     F_ARRAY_CONTEM_STATUS_ABERTO         BOOLEAN := CASE
--                                                         WHEN ('ABERTO' = ANY (F_STATUS_SOCORRO_ROTA))
--                                                             THEN TRUE
--                                                         ELSE FALSE END;
--     F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO BOOLEAN := CASE
--                                                         WHEN ('EM_ATENDIMENTO' = ANY (F_STATUS_SOCORRO_ROTA))
--                                                             THEN TRUE
--                                                         ELSE FALSE END;
--     F_ARRAY_CONTEM_STATUS_FINALIZADO     BOOLEAN := CASE
--                                                         WHEN ('FINALIZADO' = ANY (F_STATUS_SOCORRO_ROTA))
--                                                             THEN TRUE
--                                                         ELSE FALSE END;
--     F_ARRAY_CONTEM_STATUS_INVALIDO       BOOLEAN := CASE
--                                                         WHEN ('INVALIDO' = ANY (F_STATUS_SOCORRO_ROTA))
--                                                             THEN TRUE
--                                                         ELSE FALSE END;
-- BEGIN
--     RETURN QUERY
--         SELECT U.NOME :: TEXT                                                                 AS NOME_UNIDADE,
--                COALESCE(
--                        TRUNC(
--                                (ST_DISTANCE_SPHERE(
--                                         ST_POINT(
--                                                 LONGITUDE_UNIDADE::REAL,
--                                                 LATITUDE_UNIDADE::REAL),
--                                         ST_POINT(
--                                                 LONGITUDE_ABERTURA::REAL,
--                                                 LATITUDE_ABERTURA::REAL)) / 1000)::NUMERIC, 2)::TEXT || ' KM',
--                        'Unidade sem localização definida no Prolog')                          AS DISTANCIA_ENTRE_UNIDADE_ABERTURA,
--                COALESCE(SR.CODIGO ::TEXT, '-')                                                AS COD_SOCORRO_ROTA,
--                COALESCE(SR.STATUS_ATUAL :: TEXT, '-')                                         AS STATUS_SOCORRO_ROTA,
--                COALESCE(V.PLACA :: TEXT, '-')                                                 AS PLACA_VEICULO_ABERTURA,
--                COALESCE(SRAB.COD_COLABORADOR_ABERTURA :: TEXT, '-')                           AS COD_COLABORADOR_ABERTURA,
--                COALESCE(CDAB.NOME :: TEXT, '-')                                               AS NOME_RESPONSAVEL_ABERTURA,
--                COALESCE(SRAB.KM_VEICULO_ABERTURA ::TEXT, '-')                                 AS KM_VEICULO_COLETADO_ABERTURA,
--                COALESCE(SROP.DESCRICAO :: TEXT, '-')                                          AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
--                COALESCE(SRAB.DESCRICAO_PROBLEMA :: TEXT, '-')                                 AS DESCRICAO_FORNECIDA_ABERTURA,
--                COALESCE(SRAB.PONTO_REFERENCIA :: TEXT, '-')                                   AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
--                COALESCE((SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
--                         '-')                                                                  AS DATA_HORA_ABERTURA,
--                COALESCE(SRAB.LATITUDE_ABERTURA :: TEXT, '-')                                  AS LATITUDE_ABERTURA,
--                COALESCE(SRAB.LONGITUDE_ABERTURA :: TEXT, '-')                                 AS LONGITUDE_ABERTURA,
--                COALESCE(SRAB.ENDERECO_AUTOMATICO :: TEXT, '-')                                AS ENDERECO_AUTOMATICO_ABERTURA,
--                COALESCE(SRAB.MARCA_DEVICE_ABERTURA :: TEXT, '-')                              AS MARCA_APARELHO_ABERTURA,
--                COALESCE(SRAB.MODELO_DEVICE_ABERTURA :: TEXT, '-')                             AS MODELO_APARELHO_ABERTURA,
--                COALESCE(SRAB.DEVICE_IMEI_ABERTURA :: TEXT, '-')                               AS IMEI_APARELHO_ABERTURA,
--                COALESCE(SRAB.URL_FOTO_1_ABERTURA :: TEXT, '-')                                AS URL_FOTO_1_ABERTURA,
--                COALESCE(SRAB.URL_FOTO_2_ABERTURA :: TEXT, '-')                                AS URL_FOTO_2_ABERTURA,
--                COALESCE(SRAB.URL_FOTO_3_ABERTURA :: TEXT, '-')                                AS URL_FOTO_3_ABERTURA,
--                COALESCE(SRAT.COD_COLABORADOR_ATENDIMENTO :: TEXT, '-')                        AS COD_COLABORADOR_ATENDIMENTO,
--                COALESCE(CDAT.NOME :: TEXT, '-')                                               AS NOME_RESPONSAVEL_ATENDIMENTO,
--                COALESCE(SRAT.OBSERVACAO_ATENDIMENTO :: TEXT, '-')                             AS OBSERVACAO_ATENDIMENTO,
--                COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
--                                 SRAT.DATA_HORA_ATENDIMENTO - SRAB.DATA_HORA_ABERTURA),
--                         '-')                                                                  AS TEMPO_ABERTURA_ATENDIMENTO,
--                COALESCE((SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
--                         '-')                                                                  AS DATA_HORA_ATENDIMENTO,
--                COALESCE(SRAT.LATITUDE_ATENDIMENTO :: TEXT, '-')                               AS LATITUDE_ATENDIMENTO,
--                COALESCE(SRAT.LONGITUDE_ATENDIMENTO :: TEXT, '-')                              AS LONGITUDE_ATENDIMENTO,
--                COALESCE(SRAT.ENDERECO_AUTOMATICO :: TEXT, '-')                                AS ENDERECO_AUTOMATICO_ATENDIMENTO,
--                COALESCE(SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT, '-')                           AS MARCA_APARELHO_ATENDIMENTO,
--                COALESCE(SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT, '-')                          AS MODELO_APARELHO_ATENDIMENTO,
--                COALESCE(SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT, '-')                            AS IMEI_APARELHO_ATENDIMENTO,
--                COALESCE(SRI.COD_COLABORADOR_INVALIDACAO :: TEXT, '-')                         AS COD_COLABORADOR_INVALIDACAO,
--                COALESCE(CDI.NOME :: TEXT, '-')                                                AS NOME_RESPONSAVEL_INVALIDACAO,
--                COALESCE(SRI.MOTIVO_INVALIDACAO :: TEXT, '-')                                  AS MOTIVO_INVALIDACAO,
--                COALESCE(CASE
--                             WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NULL)
--                                 THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
--                                     SRI.DATA_HORA_INVALIDACAO - SRAB.DATA_HORA_ABERTURA)
--                             ELSE '-'
--                             END,
--                         '-')                                                                  AS TEMPO_ABERTURA_INVALIDACAO,
--                COALESCE(CASE
--                             WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NOT NULL)
--                                 THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
--                                     SRI.DATA_HORA_INVALIDACAO - SRAT.DATA_HORA_ATENDIMENTO)
--                             ELSE '-'
--                             END,
--                         '-')                                                                  AS TEMPO_ATENDIMENTO_INVALIDACAO,
--                COALESCE((SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
--                         '-')                                                                  AS DATA_HORA_INVALIDACAO,
--                COALESCE(SRI.LATITUDE_INVALIDACAO :: TEXT, '-')                                AS LATITUDE_INVALIDACAO,
--                COALESCE(SRI.LONGITUDE_INVALIDACAO :: TEXT, '-')                               AS LONGITUDE_INVALIDACAO,
--                COALESCE(SRI.ENDERECO_AUTOMATICO :: TEXT, '-')                                 AS ENDERECO_AUTOMATICO_INVALIDACAO,
--                COALESCE(SRI.MARCA_DEVICE_INVALIDACAO :: TEXT, '-')                            AS MARCA_APARELHO_INVALIDACAO,
--                COALESCE(SRI.MODELO_DEVICE_INVALIDACAO :: TEXT, '-')                           AS MODELO_APARELHO_INVALIDACAO,
--                COALESCE(SRI.DEVICE_IMEI_INVALIDACAO :: TEXT, '-')                             AS IMEI_APARELHO_INVALIDACAO,
--                COALESCE(SRI.URL_FOTO_1_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_1_INVALIDACAO,
--                COALESCE(SRI.URL_FOTO_2_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_2_INVALIDACAO,
--                COALESCE(SRI.URL_FOTO_3_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_3_INVALIDACAO,
--                COALESCE(SRF.COD_COLABORADOR_FINALIZACAO :: TEXT, '-')                         AS COD_COLABORADOR_FINALIZACAO,
--                COALESCE(CDF.NOME :: TEXT, '-')                                                AS NOME_RESPONSAVEL_FINALIZACAO,
--                COALESCE(SRF.OBSERVACAO_FINALIZACAO :: TEXT, '-')                              AS OBSERVACAO_FINALIZACAO,
--                COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
--                                 SRF.DATA_HORA_FINALIZACAO - SRAT.DATA_HORA_ATENDIMENTO),
--                         '-')                                                                  AS TEMPO_ATENDIMENTO_FINALIZACAO,
--                COALESCE((SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
--                         '-')                                                                  AS DATA_HORA_FINALIZACAO,
--                COALESCE(SRF.LATITUDE_FINALIZACAO :: TEXT, '-')                                AS LATITUDE_FINALIZACAO,
--                COALESCE(SRF.LONGITUDE_FINALIZACAO :: TEXT, '-')                               AS LONGITUDE_FINALIZACAO,
--                COALESCE(SRF.ENDERECO_AUTOMATICO :: TEXT, '-')                                 AS ENDERECO_AUTOMATICO_FINALIZACAO,
--                COALESCE(SRF.MARCA_DEVICE_FINALIZACAO :: TEXT, '-')                            AS MARCA_APARELHO_FINALIZACAO,
--                COALESCE(SRF.MODELO_DEVICE_FINALIZACAO :: TEXT, '-')                           AS MODELO_APARELHO_FINALIZACAO,
--                COALESCE(SRF.DEVICE_IMEI_FINALIZACAO :: TEXT, '-')                             AS IMEI_APARELHO_FINALIZACAO,
--                COALESCE(SRF.URL_FOTO_1_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_1_FINALIZACAO,
--                COALESCE(SRF.URL_FOTO_2_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_2_FINALIZACAO,
--                COALESCE(SRF.URL_FOTO_3_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_3_FINALIZACAO
--         FROM SOCORRO_ROTA SR
--                  JOIN UNIDADE U ON SR.COD_UNIDADE = U.CODIGO
--                  JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.
--                                                         CODIGO = SRAB.COD_SOCORRO_ROTA
--                  JOIN VEICULO_DATA V ON V.
--                                             CODIGO = SRAB.COD_VEICULO_PROBLEMA
--                  JOIN COLABORADOR_DATA CDAB ON CDAB.
--                                                    CODIGO = SRAB.COD_COLABORADOR_ABERTURA
--                  JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.
--                                                               CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
--                  LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
--                            ON SR.STATUS_ATUAL::
--                                   TEXT = ANY
--                               (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO'])
--                                AND
--                               SR.
--                                   CODIGO = SRAT.COD_SOCORRO_ROTA
--                  LEFT JOIN COLABORADOR_DATA CDAT
--                            ON SR.STATUS_ATUAL::
--                                   TEXT = ANY
--                               (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO'])
--                                AND
--                               CDAT.
--                                   CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
--                  LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
--                            ON SR.
--                                   STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
--                  LEFT JOIN COLABORADOR_DATA CDI
--                            ON SR.
--                                   STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
--                  LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
--                            ON SR.
--                                   STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
--                  LEFT JOIN COLABORADOR_DATA CDF
--                            ON SR.
--                                   STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
--         WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
--             AND F_IF(F_ARRAY_CONTEM_STATUS_ABERTO,
--                      (SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
--                      FALSE)
--            OR F_IF(F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO,
--                    (SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
--                    FALSE)
--            OR F_IF(F_ARRAY_CONTEM_STATUS_FINALIZADO,
--                    (SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
--                    FALSE)
--            OR F_IF(F_ARRAY_CONTEM_STATUS_INVALIDO,
--                    (SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)):: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
--                    FALSE);
-- END ;
-- $$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ##################################### CRIA FUNC_CHECKLIST_OS_RESOLVER_ITENS #########################################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2500
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RESOLVER_ITENS(F_COD_UNIDADE BIGINT,
                                                            F_COD_ITENS BIGINT[],
                                                            F_CPF BIGINT,
                                                            F_TEMPO_REALIZACAO BIGINT,
                                                            F_KM BIGINT,
                                                            F_STATUS_RESOLUCAO TEXT,
                                                            F_DATA_HORA_CONSERTO TIMESTAMP WITH TIME ZONE,
                                                            F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                            F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                            F_FEEDBACK_CONSERTO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_ITEM                  BIGINT;
    DATA_REALIZACAO_CHECKLIST TIMESTAMP WITH TIME ZONE;
    COD_ALTERNATIVA_ITEM      BIGINT;
    ERROR_MESSAGE             TEXT :=   E'Erro!\nA data de resolução é menor do que o primeiro apontamento do item "%s".';
    QTD_LINHAS_ATUALIZADAS    BIGINT;
    TOTAL_LINHAS_ATUALIZADAS  BIGINT := 0;
BEGIN
    FOREACH COD_ITEM IN ARRAY F_COD_ITENS
        LOOP
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
            SELECT INTO DATA_REALIZACAO_CHECKLIST, COD_ALTERNATIVA_ITEM C.DATA_HORA,
                                                                        COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                     JOIN CHECKLIST_ORDEM_SERVICO COS
                          ON COSI.COD_OS = COS.CODIGO AND COSI.COD_UNIDADE = COS.COD_UNIDADE
                     JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
            WHERE COSI.CODIGO = COD_ITEM;

            -- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
            IF DATA_REALIZACAO_CHECKLIST IS NOT NULL AND DATA_REALIZACAO_CHECKLIST >= F_DATA_HORA_INICIO_RESOLUCAO
            THEN
                PERFORM THROW_GENERIC_ERROR(FORMAT(ERROR_MESSAGE, (SELECT CAPD.ALTERNATIVA
                                                                   FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
                                                                   WHERE CAPD.CODIGO = COD_ALTERNATIVA_ITEM)));
            END IF;

            -- Atualiza os itens
            UPDATE CHECKLIST_ORDEM_SERVICO_ITENS
            SET CPF_MECANICO               = F_CPF,
                TEMPO_REALIZACAO           = F_TEMPO_REALIZACAO,
                KM                         = F_KM,
                STATUS_RESOLUCAO           = F_STATUS_RESOLUCAO,
                DATA_HORA_CONSERTO         = F_DATA_HORA_CONSERTO,
                DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
                DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO,
                FEEDBACK_CONSERTO          = F_FEEDBACK_CONSERTO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND CODIGO = COD_ITEM
              AND DATA_HORA_CONSERTO IS NULL;

            GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            -- Verificamos se o update funcionou.
            IF QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <= 0
            THEN
                PERFORM THROW_GENERIC_ERROR('Erro ao marcar os itens como resolvidos.');
            END IF;
            TOTAL_LINHAS_ATUALIZADAS := TOTAL_LINHAS_ATUALIZADAS + QTD_LINHAS_ATUALIZADAS;
        END LOOP;
    RETURN TOTAL_LINHAS_ATUALIZADAS;
END;
$$;