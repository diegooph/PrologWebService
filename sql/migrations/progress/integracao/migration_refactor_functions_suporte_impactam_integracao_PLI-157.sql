DROP FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
    F_CODIGO_PNEU BIGINT,
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
    V_STATUS_PNEU_ANALISE CONSTANT   TEXT := 'ANALISE';
    V_QTD_LINHAS_ATUALIZADAS         BIGINT;
    V_COD_AFERICAO                   BIGINT[];
    V_COD_AFERICAO_FOREACH           BIGINT;
    V_QTD_AFERICAO_VALORES           BIGINT;
    V_QTD_AFERICAO_VALORES_DELETADOS BIGINT;
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
           AND P.STATUS = V_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se pneu é integrado
    IF EXISTS(SELECT IPC.COD_PNEU_CADASTRO_PROLOG
              FROM INTEGRACAO.PNEU_CADASTRADO IPC
              WHERE IPC.COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
                AND IPC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE)
    THEN
        -- Deleta Pneu (Não temos deleção lógica)
        DELETE
        FROM INTEGRACAO.PNEU_CADASTRADO
        WHERE COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE;
    END IF;

    -- Deleta pneu Prolog.
    UPDATE PNEU_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_CODIGO_PNEU
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
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

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
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

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
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
    INTO V_COD_AFERICAO;

    -- Verifica se algum valor foi deletado em afericao_valores_data.
    IF (V_COD_AFERICAO IS NOT NULL AND ARRAY_LENGTH(V_COD_AFERICAO, 1) > 0)
    THEN
        -- Iteração com cada cod_afericao deletado em afericao_valores_data.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_COD_AFERICAO
            LOOP
                -- Coleta a quantidade de aferições em afericao_valores_data.
                V_QTD_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                          FROM AFERICAO_VALORES_DATA AVD
                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                -- Coleta a quantidade de aferições deletadas em afericao_valores_data.
                V_QTD_AFERICAO_VALORES_DELETADOS = (SELECT COUNT(AVD.COD_AFERICAO)
                                                    FROM AFERICAO_VALORES_DATA AVD
                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                      AND AVD.DELETADO IS TRUE);

                -- Verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                IF (V_QTD_AFERICAO_VALORES = V_QTD_AFERICAO_VALORES_DELETADOS)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE CODIGO = V_COD_AFERICAO_FOREACH;

                    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

                    -- Garante que a deleção foi realizada.
                    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
                    THEN
                        RAISE EXCEPTION 'Erro ao deletar aferição com Código: %, Unidade: %',
                            V_COD_AFERICAO_FOREACH, F_COD_UNIDADE;
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

DROP FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
    F_PLACA VARCHAR(255),
    OUT DEPENDENCIAS_DELETADAS TEXT);

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
                                                               F_PLACA VARCHAR(255),
                                                               OUT DEPENDENCIAS_DELETADAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_CODIGO_LOOP                   BIGINT;
    V_LISTA_COD_AFERICAO_PLACA      BIGINT[];
    V_LISTA_COD_CHECK_PLACA         BIGINT[];
    V_LISTA_COD_PROLOG_DELETADO_COS BIGINT[];
    V_NOME_EMPRESA                  VARCHAR(255) := (SELECT E.NOME
                                                     FROM EMPRESA E
                                                     WHERE E.CODIGO =
                                                           (SELECT U.COD_EMPRESA
                                                            FROM UNIDADE U
                                                            WHERE U.CODIGO = F_COD_UNIDADE));
    V_NOME_UNIDADE                  VARCHAR(255) := (SELECT U.NOME
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
        INTO V_LISTA_COD_AFERICAO_PLACA;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO.
        UPDATE AFERICAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_AFERICAO_PLACA);
    END IF;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    IF EXISTS(SELECT C.PLACA_VEICULO FROM CHECKLIST_DATA C WHERE C.PLACA_VEICULO = F_PLACA)
    THEN
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        SELECT ARRAY_AGG(C.CODIGO)
        FROM CHECKLIST_DATA C
        WHERE C.PLACA_VEICULO = F_PLACA
        INTO V_LISTA_COD_CHECK_PLACA;

        -- DELETA COD_CHECK EM COS.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE DELETADO = FALSE
          AND COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA);

        -- BUSCO OS CODIGO PROLOG DELETADOS EM COS.
        SELECT ARRAY_AGG(CODIGO_PROLOG)
        FROM CHECKLIST_ORDEM_SERVICO_DATA
        WHERE COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA)
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO IS TRUE
        INTO V_LISTA_COD_PROLOG_DELETADO_COS;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        FOREACH V_CODIGO_LOOP IN ARRAY V_LISTA_COD_PROLOG_DELETADO_COS
            LOOP
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET DELETADO            = TRUE,
                    DATA_HORA_DELETADO  = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER
                WHERE DELETADO = FALSE
                  AND (COD_OS, COD_UNIDADE) = (SELECT COS.CODIGO, COS.COD_UNIDADE
                                               FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                               WHERE COS.CODIGO_PROLOG = V_CODIGO_LOOP);
            END LOOP;

        -- DELETA TODOS CHECKLIST DA PLACA.
        UPDATE CHECKLIST_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE PLACA_VEICULO = F_PLACA
          AND DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_CHECK_PLACA);
    END IF;

    -- Verifica se a placa é integrada.
    IF EXISTS(SELECT IVC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO IVC
              WHERE IVC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE
                AND IVC.PLACA_VEICULO_CADASTRO = F_PLACA)
    THEN
        -- Realiza a deleção da placa. (Não possuímos deleção lógica)
        DELETE
        FROM INTEGRACAO.VEICULO_CADASTRADO
        WHERE COD_UNIDADE_CADASTRO = F_COD_UNIDADE
          AND PLACA_VEICULO_CADASTRO = F_PLACA;
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
               || V_NOME_EMPRESA
               || ', Unidade: '
               || V_NOME_UNIDADE
    INTO DEPENDENCIAS_DELETADAS;
END;
$$;

-- #####################################################################################################################
-- #####################################################################################################################

DROP FUNCTION SUPORTE.FUNC_VEICULO_TRANSFERE_VEICULO_ENTRE_EMPRESAS(F_PLACA_VEICULO VARCHAR(7),
    F_COD_EMPRESA_ORIGEM BIGINT,
    F_COD_UNIDADE_ORIGEM BIGINT,
    F_COD_EMPRESA_DESTINO BIGINT,
    F_COD_UNIDADE_DESTINO BIGINT,
    F_COD_MODELO_VEICULO_DESTINO BIGINT,
    F_COD_TIPO_VEICULO_DESTINO BIGINT,
    OUT VEICULO_TRANSFERIDO TEXT);

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
    V_NOME_EMPRESA_DESTINO                           VARCHAR(255) := (SELECT E.NOME
                                                                      FROM EMPRESA E
                                                                      WHERE E.CODIGO = F_COD_EMPRESA_DESTINO);
    V_NOME_UNIDADE_DESTINO                           VARCHAR(255) := (SELECT U.NOME
                                                                      FROM UNIDADE U
                                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO);
    V_LISTA_COD_AFERICAO_PLACA                       BIGINT[];
    V_COD_AFERICAO_FOREACH                           BIGINT;
    V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO          BIGINT[];
    V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES           BIGINT;
    V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES BIGINT;


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
            V_NOME_EMPRESA_DESTINO;
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
            V_NOME_EMPRESA_DESTINO;
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
        INTO V_LISTA_COD_AFERICAO_PLACA;

        --LAÇO FOR PARA PERCORRER TODOS OS VALORES EM F_LISTA_COD_AFERICAO_PLACA.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_LISTA_COD_AFERICAO_PLACA
            LOOP
                --PARA CADA VALOR EM: F_LISTA_COD_AFERICAO_PLACA
                IF EXISTS(SELECT AM.COD_AFERICAO
                          FROM AFERICAO_MANUTENCAO AM
                          WHERE AM.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                            AND AM.DATA_HORA_RESOLUCAO IS NULL
                            AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                            AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE)
                THEN
                    --COLETA O(S) COD_PNEU CORRESPONDENTES AO COD_AFERICAO.
                    SELECT ARRAY_AGG(AM.COD_PNEU)
                    FROM AFERICAO_MANUTENCAO AM
                    WHERE AM.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND AM.DATA_HORA_RESOLUCAO IS NULL
                      AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                      AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                    INTO V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO;

                    --DELETA AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_MANUTENCAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);

                    --DELETA AFERICAO EM AFERICAO_VALORES_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_VALORES_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);
                END IF;
            END LOOP;

        --SE, E SOMENTE SE, A AFERIÇÃO POSSUIR TODOS OS VALORES EXCLUÍDOS, DEVE-SE EXCLUIR TODA A AFERIÇÃO.
        --SENÃO, A AFERIÇÃO CONTINUA EXISTINDO.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_LISTA_COD_AFERICAO_PLACA
            LOOP
                V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                          FROM AFERICAO_VALORES_DATA AVD
                                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                                    FROM AFERICAO_VALORES_DATA AVD
                                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                                      AND AVD.DELETADO IS TRUE);

                --SE A QUANTIDADE DE UM COD_AFERICAO EM AFERICAO_VALORES_DATA FOR IGUAL A QUANTIDADE DE UM COD_AFERICAO
                --DELETADO EM AFERICAO_VALORES_DATA, DEVEMOS EXCLUIR A AFERIÇÃO, POIS, TODOS SEUS VALORES FORAM
                --DELETADOS.
                IF (V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES =
                    V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND CODIGO = V_COD_AFERICAO_FOREACH;
                END IF;
            END LOOP;
    END IF;

    -- SE O VEÍCULO FOR INTEGRADO, ATUALIZA OS DADOS DE EMPRESA E UNIDADE NA TABELA DE INTEGRAÇÃO.
    IF EXISTS(SELECT IVC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO IVC
              WHERE IVC.COD_EMPRESA_CADASTRO = F_COD_EMPRESA_ORIGEM
                AND IVC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE_ORIGEM
                AND IVC.PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO)
    THEN
        UPDATE INTEGRACAO.VEICULO_CADASTRADO
        SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO,
            COD_EMPRESA_CADASTRO = F_COD_EMPRESA_DESTINO
        WHERE COD_EMPRESA_CADASTRO = F_COD_EMPRESA_ORIGEM
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE_ORIGEM
          AND PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO;
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
           ' FOI TRANSFERIDO PARA A EMPRESA ' || V_NOME_EMPRESA_DESTINO || ' JUNTO A UNIDADE ' ||
           V_NOME_UNIDADE_DESTINO || '.'
    INTO VEICULO_TRANSFERIDO;
END
$$;

-- #####################################################################################################################
-- #####################################################################################################################

DROP FUNCTION SUPORTE.FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST(F_COD_UNIDADE BIGINT,
    F_COD_OS BIGINT,
    F_COD_CHECKLIST BIGINT,
    OUT AVISO_CHECKLIST_OS_DELETADO TEXT);


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                         F_COD_OS BIGINT,
                                                                         F_COD_CHECKLIST BIGINT,
                                                                         OUT AVISO_CHECKLIST_OS_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    V_COD_COSI             BIGINT[];
BEGIN
    --VERIFICA SE EXISTE UNIDADE
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    --VERIFICA A EXISTÊNCIA DA ORDEM DE SERVIÇO DE ACORDO COM A CHAVE COMPOSTA RECEBIDA POR PARÂMETRO:
    IF NOT EXISTS(
            SELECT *
            FROM CHECKLIST_ORDEM_SERVICO_DATA
            WHERE CODIGO = F_COD_OS
              AND COD_UNIDADE = F_COD_UNIDADE
              AND COD_CHECKLIST = F_COD_CHECKLIST
        )
    THEN
        RAISE EXCEPTION 'ORDEM DE SERVIÇO COM CÓDIGO: %, UNIDADE: %, CÓDIGO DE CHECKLIST: % NÃO ENCONTRADO',
            F_COD_OS, F_COD_UNIDADE, F_COD_CHECKLIST;
    END IF;

    --DELETA ITEM ORDEM SERVIÇO:
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_OS = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ITEM DA ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: % E CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    --DELETA OS:
    UPDATE CHECKLIST_ORDEM_SERVICO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST = F_COD_CHECKLIST;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: %, CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    -- Busca código do item
    SELECT ARRAY_AGG(CODIGO)
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_OS = F_COD_OS
    INTO V_COD_COSI;

    -- Verifica se checklist está integrado, caso esteja é deletado
    IF EXISTS(SELECT ICOSI.COD_EMPRESA
              FROM INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO ICOSI
              WHERE ICOSI.COD_OS = F_COD_OS
                AND ICOSI.COD_ITEM_OS = ANY (V_COD_COSI))
    THEN
        -- Realiza a deleção (Não possuímos deleção lógica).
        DELETE
        FROM INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = F_COD_OS
          AND COD_ITEM_OS = ANY (V_COD_COSI);
    END IF;

    SELECT 'DELEÇÃO DA OS: '
               || F_COD_OS
               || ', CÓDIGO CHECKLIST'
               || F_COD_CHECKLIST
               || ', CÓDIGO UNIDADE: '
               || F_COD_UNIDADE
               || ' REALIZADO COM SUCESSO.'
    INTO AVISO_CHECKLIST_OS_DELETADO;
END
$$;