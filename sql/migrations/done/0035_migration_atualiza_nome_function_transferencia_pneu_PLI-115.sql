DROP FUNCTION
    INTEGRACAO.FUNC_PNEU_TRANFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM BIGINT,
    F_COD_UNIDADE_DESTINO BIGINT,
    F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
    F_LISTA_PNEUS TEXT[],
    F_OBSERVACAO TEXT,
    F_TOKEN_INTEGRACAO TEXT,
    F_DATA_HORA TIMESTAMP WITH TIME ZONE,
    F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA BOOLEAN);


CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM BIGINT,
                                                       F_COD_UNIDADE_DESTINO BIGINT,
                                                       F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                       F_LISTA_PNEUS TEXT[],
                                                       F_OBSERVACAO TEXT,
                                                       F_TOKEN_INTEGRACAO TEXT,
                                                       F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                       F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                BIGINT := (SELECT TI.COD_EMPRESA
                                            FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                            WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    V_COD_COLABORADOR            BIGINT := (SELECT C.CODIGO
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_COD_UNIDADE_COLABORADOR    BIGINT := (SELECT C.COD_UNIDADE
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_TIPO_TRANSFERENCIA         TIPO_PROCESSO_TRANSFERENCIA_PNEU
                                        := 'TRANSFERENCIA_APENAS_PNEUS';
    V_PNEUS_NAO_MAPEADOS         TEXT[] := (SELECT ARRAY_AGG(CCNE.COD_CLIENTE)
                                            FROM (SELECT UNNEST(F_LISTA_PNEUS) AS COD_CLIENTE) AS CCNE
                                            WHERE CCNE.COD_CLIENTE
                                                      NOT IN (SELECT PC.COD_CLIENTE_PNEU_CADASTRO
                                                              FROM INTEGRACAO.PNEU_CADASTRADO PC
                                                              WHERE PC.COD_EMPRESA_CADASTRO = V_COD_EMPRESA));
    V_COD_PROCESSO_TRANSFERENCIA BIGINT;
    V_COD_PNEUS_TRANSFERIR       BIGINT[];
    V_QTD_ROWS                   BIGINT;
BEGIN
    -- Verificamos se a empresa existe
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado n??o est?? autorizado: %s', F_TOKEN_INTEGRACAO));

    -- Verifica se unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do pneu n??o est?? mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verifica se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do pneu n??o est?? mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_ORIGEM,
            FORMAT('Unidade (%s) n??o autorizada para o token: %s', F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_DESTINO,
            FORMAT('Unidade (%s) n??o autorizada para o token: %s', F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF NOT EXISTS(SELECT C.CODIGO FROM COLABORADOR_DATA C WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s n??o est?? cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Verificamos se temos algum pneu n??o mapeado.
    IF (ARRAY_LENGTH(V_PNEUS_NAO_MAPEADOS, 1) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('Os pneus (%s) n??o est??o cadastrados no Sistema Prolog',
                                           ARRAY_TO_STRING(V_PNEUS_NAO_MAPEADOS, ', ')));
    END IF;

    -- Ap??s validar todos os cen??rios, pegamos os c??digos do pneus que iremos transferir. Transferimos apenas os pneus
    -- que n??o est??o na unidade de destino, ou seja, transferimos somente os que precisam.
    SELECT ARRAY_AGG(P.CODIGO)
    FROM PNEU P
    WHERE P.COD_EMPRESA = V_COD_EMPRESA
      AND P.CODIGO_CLIENTE = ANY (F_LISTA_PNEUS)
      AND P.COD_UNIDADE != F_COD_UNIDADE_DESTINO
    INTO V_COD_PNEUS_TRANSFERIR;

    IF (ARRAY_LENGTH(V_COD_PNEUS_TRANSFERIR, 1) <= 0)
    THEN
        -- Retornamos 1, simulando um processo de transfer??ncia que foi sucesso, por??m nada foi transferido.
        RETURN 1;
    END IF;

    -- Verifica se os pneus est??o aplicados a uma placa na transfer??ncia de ve??culo.
    IF (F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA IS FALSE)
    THEN
        -- Devemos alterar o status de todos os pneus, coloc??-los em estoque caso ainda n??o estejam.
        DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = ANY (V_COD_PNEUS_TRANSFERIR);

        UPDATE PUBLIC.PNEU
        SET STATUS = 'ESTOQUE'
        WHERE CODIGO = ANY (V_COD_PNEUS_TRANSFERIR);
    ELSE
        V_TIPO_TRANSFERENCIA = 'TRANSFERENCIA_JUNTO_A_VEICULO';
    END IF;

    -- Cria processo de transfer??ncia.
    INSERT INTO PNEU_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                            COD_UNIDADE_DESTINO,
                                            COD_UNIDADE_COLABORADOR,
                                            COD_COLABORADOR,
                                            DATA_HORA_TRANSFERENCIA_PROCESSO,
                                            OBSERVACAO,
                                            TIPO_PROCESSO_TRANSFERENCIA)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO,
            V_TIPO_TRANSFERENCIA)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA IS NULL OR V_COD_PROCESSO_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transfer??ncia';
    END IF;

    -- Insere valores da transfer??ncia
    V_QTD_ROWS = (FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(V_COD_PROCESSO_TRANSFERENCIA,
                                                             V_COD_PNEUS_TRANSFERIR));
    -- Verifica se atualizou.
    IF (V_QTD_ROWS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao realizar a transfer??ncia de informa????es';
    END IF;

    -- Atualiza unidade alocada.
    PERFORM INTEGRACAO.FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(F_COD_UNIDADE_DESTINO,
                                                                       V_COD_PNEUS_TRANSFERIR);

    RETURN V_COD_PROCESSO_TRANSFERENCIA;
END;
$$;


CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_TRANSFERE_VEICULO(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                     F_COD_UNIDADE_DESTINO BIGINT,
                                                                     F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                                     F_PLACA TEXT,
                                                                     F_OBSERVACAO TEXT,
                                                                     F_TOKEN_INTEGRACAO TEXT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                        BIGINT := (SELECT ITI.COD_EMPRESA
                                                    FROM INTEGRACAO.TOKEN_INTEGRACAO ITI
                                                    WHERE ITI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    V_OBSERVACAO_TRANSFERENCIA_PNEU      TEXT   := 'Transfer??ncia de pneus aplicados';
    V_COD_VEICULO                        BIGINT;
    V_COD_DIAGRAMA_VEICULO               BIGINT;
    V_COD_TIPO_VEICULO                   BIGINT;
    V_KM_VEICULO                         BIGINT;
    V_COD_COLABORADOR                    BIGINT;
    V_COD_UNIDADE_COLABORADOR            BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT;
    V_COD_INFORMACOES_TRANSFERENCIA      BIGINT;
    V_COD_PNEUS_TRANSFERIR               TEXT[];
BEGIN
    -- Verificamos se a empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado n??o est?? autorizado: %s', F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do ve??culo n??o est?? mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verificamos se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do ve??culo n??o est?? mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_ORIGEM,
                                                FORMAT('Unidade (%s) n??o autorizada para o token: %s',
                                                       F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_DESTINO,
                                                FORMAT('Unidade (%s) n??o autorizada para o token: %s',
                                                       F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Pegamos informa????es necess??rias para executar o procedimento de transfer??ncia
    SELECT V.CODIGO,
           VT.COD_DIAGRAMA,
           VT.CODIGO,
           V.KM
    FROM VEICULO_DATA V
             JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
        -- Fazemos esse Join para remover a placa do retorno caso ela n??o estiver mapeada na tabela de integra????o
             JOIN INTEGRACAO.VEICULO_CADASTRADO VC ON V.CODIGO = VC.COD_VEICULO_CADASTRO_PROLOG
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_DIAGRAMA_VEICULO, V_COD_TIPO_VEICULO, V_KM_VEICULO;

    -- Verificamos se o ve??culo pertence a unidade origem.
    IF (V_COD_VEICULO IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('A placa (%s) n??o est?? cadastrada no Sistema Prolog', F_PLACA));
    END IF;

    -- Pegamos informa????es necess??rias para executar o procedimento de transfer??ncia
    SELECT C.CODIGO,
           C.COD_UNIDADE
    FROM COLABORADOR_DATA C
    WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA
    INTO V_COD_COLABORADOR, V_COD_UNIDADE_COLABORADOR;

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF (V_COD_COLABORADOR IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s n??o est?? cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Precisamos desativar as constraints, para verificar apenas no commit da function, assim poderemos transferir o
    -- ve??culo e seus pneus com seguran??a.
    SET CONSTRAINTS ALL DEFERRED;

    -- Cria processo de transfer??ncia para ve??culo.
    INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                               COD_UNIDADE_DESTINO,
                                               COD_UNIDADE_COLABORADOR,
                                               COD_COLABORADOR_REALIZACAO,
                                               DATA_HORA_TRANSFERENCIA_PROCESSO,
                                               OBSERVACAO)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA_VEICULO;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA_VEICULO IS NULL OR V_COD_PROCESSO_TRANSFERENCIA_VEICULO <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transfer??ncia';
    END IF;

    -- Insere valores da transfer??ncia do ve??culo
    INSERT INTO VEICULO_TRANSFERENCIA_INFORMACOES(COD_PROCESSO_TRANSFERENCIA,
                                                  COD_VEICULO,
                                                  COD_DIAGRAMA_VEICULO,
                                                  COD_TIPO_VEICULO,
                                                  KM_VEICULO_MOMENTO_TRANSFERENCIA)
    VALUES (V_COD_PROCESSO_TRANSFERENCIA_VEICULO,
            V_COD_VEICULO,
            V_COD_DIAGRAMA_VEICULO,
            V_COD_TIPO_VEICULO,
            V_KM_VEICULO)
    RETURNING CODIGO INTO V_COD_INFORMACOES_TRANSFERENCIA;

    -- Verifica se os valores da transfer??ncia foram adicionados com sucesso.
    IF (V_COD_INFORMACOES_TRANSFERENCIA IS NULL OR V_COD_INFORMACOES_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao adicionar valores da transfer??ncia';
    END IF;

    -- TODO - verificar atrav??s da func, se devemos fechar ou n??o Itens de O.S (FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO)
    -- Deleta O.S. do ve??culo transferido.
    PERFORM FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(V_COD_VEICULO,
                                                               V_COD_INFORMACOES_TRANSFERENCIA,
                                                               F_DATA_HORA);

    -- Transfere ve??culo.
    UPDATE VEICULO
    SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
    WHERE CODIGO = V_COD_VEICULO;

    -- Transfere ve??culo na integra????o.
    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO
    WHERE COD_VEICULO_CADASTRO_PROLOG = V_COD_VEICULO;

    -- Verifica se placa possui pneus aplicados, caso tenha, transferimos esses pneus para a unidade origem.
    IF (EXISTS(SELECT COD_PNEU FROM VEICULO_PNEU WHERE PLACA = F_PLACA))
    THEN
        -- Criamos array com os cod_pneu.
        SELECT ARRAY_AGG(P.CODIGO_CLIENTE)
        FROM PNEU_DATA P
        WHERE P.CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA)
        INTO V_COD_PNEUS_TRANSFERIR;

        V_COD_PROCESSO_TRANSFERENCIA_PNEU =
                (INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM,
                                                                    F_COD_UNIDADE_DESTINO,
                                                                    F_CPF_COLABORADOR_TRANSFERENCIA,
                                                                    V_COD_PNEUS_TRANSFERIR,
                                                                    V_OBSERVACAO_TRANSFERENCIA_PNEU,
                                                                    F_TOKEN_INTEGRACAO,
                                                                    F_DATA_HORA,
                                                                    TRUE));
        -- Verifica se a transfer??ncia deu certo.
        IF (V_COD_PROCESSO_TRANSFERENCIA_PNEU <= 0)
        THEN
            RAISE EXCEPTION
                'Erro ao transferir os pneus (%s) aplicados ao ve??culo', ARRAY_TO_STRING(V_COD_PNEUS_TRANSFERIR, ', ');
        END IF;

        --  Modifica unidade dos v??nculos.
        UPDATE VEICULO_PNEU
        SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
        WHERE PLACA = F_PLACA;

        -- Adiciona VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU.
        INSERT INTO VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(COD_VEICULO_TRANSFERENCIA_INFORMACOES,
                                                                COD_PROCESSO_TRANSFERENCIA_PNEU)
        VALUES (V_COD_INFORMACOES_TRANSFERENCIA,
                V_COD_PROCESSO_TRANSFERENCIA_PNEU);
    END IF;
END;
$$;