create function f_size_array(anyarray) returns integer
    language plpgsql
as
$$
begin
    return coalesce(array_length($1, 1), 0);
end;
$$;


CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_TRANFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM BIGINT,
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

    IF (F_SIZE_ARRAY(V_COD_PNEUS_TRANSFERIR) <= 0)
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


CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_VALIDA_POSICOES_SISTEMA_PARCEIRO(F_COD_DIAGRAMA BIGINT,
                                                                                 F_POSICOES_PARCEIRO TEXT[],
                                                                                 F_POSICOES_PROLOG BIGINT[])
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_POSICOES_DIAGRAMA_PROLOG    BIGINT[];
    V_POSICOES_INVALIDAS          BIGINT[];
    V_POSICOES_REPETIDAS_PARCEIRO TEXT[];
    V_POSICOES_REPETIDAS_PROLOG   TEXT[];
    V_MENSAGEM_RETORNO            TEXT := '';
    V_TEM_ERRO                    BOOLEAN;
BEGIN
    -- Valida se diagrama recebido n??o ?? null.
    IF (F_COD_DIAGRAMA IS NULL OR F_COD_DIAGRAMA <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('O c??digo diagrama n??o pode ser nulo');
    END IF;

    -- Valida diagrama existe no Sistema Prolog.
    IF NOT EXISTS(SELECT VD.CODIGO FROM VEICULO_DIAGRAMA VD WHERE VD.CODIGO = F_COD_DIAGRAMA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT('O c??digo diagrama (%s) informado n??o existe no Sistema Prolog', F_COD_DIAGRAMA));
    END IF;

    -- Valida se a lista de posi????es n??o est?? vazia.
    IF NOT (F_SIZE_ARRAY(F_POSICOES_PROLOG) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('A lista com as posi????es do diagrama est?? vazia');
    END IF;

    -- Busca todas as posi????es do diagrama.
    SELECT ARRAY_AGG(VDPP.POSICAO_PROLOG)
    FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
    WHERE VDPP.COD_DIAGRAMA = F_COD_DIAGRAMA
    INTO V_POSICOES_DIAGRAMA_PROLOG;

    -- Valida se as posi????es do diagrama foram encontradas.
    IF NOT (F_SIZE_ARRAY(V_POSICOES_DIAGRAMA_PROLOG) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT('N??o foi poss??vel buscar as posi????es do diagrama de c??digo (%s)', F_COD_DIAGRAMA));
    END IF;

    -- Valida se as posi????es recebidas n??o cont??m nas posi????es do diagrama.
    IF NOT (F_POSICOES_PROLOG <@ V_POSICOES_DIAGRAMA_PROLOG)
    THEN
        -- Busca as posi????es que foram enviadas e n??o fazem parte das posi????es do diagrama.
        SELECT ARRAY_AGG(POSICAO.POSICAO_CLIENTE)
        FROM (SELECT UNNEST(F_POSICOES_PROLOG) AS POSICAO_CLIENTE) AS POSICAO
        WHERE POSICAO.POSICAO_CLIENTE NOT IN (SELECT UNNEST(V_POSICOES_DIAGRAMA_PROLOG))
        INTO V_POSICOES_INVALIDAS;
    END IF;

    -- Filtra as posi????es repetidas do cliente.
    WITH POSICOES_DUPLICADAS AS (
        SELECT POSICAO_DUPLICADA AS POSICAO_DUPLICADA
        FROM UNNEST(F_POSICOES_PROLOG) AS POSICAO_DUPLICADA
        GROUP BY POSICAO_DUPLICADA
        HAVING COUNT(*) > 1
    )

    SELECT ARRAY_AGG(PD.POSICAO_DUPLICADA)
    FROM POSICOES_DUPLICADAS PD
    INTO V_POSICOES_REPETIDAS_PROLOG;

    -- Filtra as posi????es repetidas do cliente.
    WITH POSICOES_DUPLICADAS AS (
        SELECT POSICAO_DUPLICADA AS POSICAO_DUPLICADA
        FROM UNNEST(F_POSICOES_PARCEIRO) AS POSICAO_DUPLICADA
        GROUP BY POSICAO_DUPLICADA
        HAVING COUNT(*) > 1
    )

    SELECT ARRAY_AGG(PD.POSICAO_DUPLICADA)
    FROM POSICOES_DUPLICADAS PD
    INTO V_POSICOES_REPETIDAS_PARCEIRO;

    -- Faz direcionamento para o retorno da exception.
    IF (F_SIZE_ARRAY(V_POSICOES_INVALIDAS) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posi????es do Sistema Prolog ('
                   || ARRAY_TO_STRING(V_POSICOES_INVALIDAS, ', ') || ') n??o pertencem ao diagrama de c??digo ('
                   || F_COD_DIAGRAMA || E')\n'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;
    IF (F_SIZE_ARRAY(V_POSICOES_REPETIDAS_PROLOG) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posi????es do Sistema Prolog ('
                   || ARRAY_TO_STRING(V_POSICOES_REPETIDAS_PROLOG, ', ') || ') est??o repetidas no diagrama de c??digo ('
                   || F_COD_DIAGRAMA || E')\n'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;
    IF (F_SIZE_ARRAY(V_POSICOES_REPETIDAS_PARCEIRO) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posi????es do Globus ('
                   || ARRAY_TO_STRING(V_POSICOES_REPETIDAS_PARCEIRO, ', ') ||
               ') est??o repetidas no diagrama de c??digo (' || F_COD_DIAGRAMA || ')'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;

    IF (V_TEM_ERRO) THEN
        PERFORM THROW_GENERIC_ERROR(V_MENSAGEM_RETORNO);
    END IF;

    -- Caso nenhuma exception for lan??ada, retornamos sucesso.
    RETURN F_COD_DIAGRAMA;
END;
$$;