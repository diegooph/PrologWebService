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
    -- Valida se diagrama recebido não é null.
    IF (F_COD_DIAGRAMA IS NULL OR F_COD_DIAGRAMA <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('O código diagrama não pode ser nulo');
    END IF;

    -- Valida diagrama existe no Sistema Prolog.
    IF NOT EXISTS(SELECT VD.CODIGO FROM VEICULO_DIAGRAMA VD WHERE VD.CODIGO = F_COD_DIAGRAMA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('O código diagrama (%s) informado não existe no Sistema Prolog', F_COD_DIAGRAMA));
    END IF;

    -- Valida se a lista de posições não está vazia.
    IF NOT (ARRAY_LENGTH(F_POSICOES_PROLOG, 1) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('A lista com as posições do diagrama está vazia');
    END IF;

    -- Busca todas as posições do diagrama.
    SELECT ARRAY_AGG(VDPP.POSICAO_PROLOG)
    FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
    WHERE VDPP.COD_DIAGRAMA = F_COD_DIAGRAMA
    INTO V_POSICOES_DIAGRAMA_PROLOG;

    -- Valida se as posições do diagrama foram encontradas.
    IF NOT (ARRAY_LENGTH(V_POSICOES_DIAGRAMA_PROLOG, 1) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('Não foi possível buscar as posições do diagrama de código (%s)', F_COD_DIAGRAMA));
    END IF;

    -- Valida se as posições recebidas não contém nas posições do diagrama.
    IF NOT (F_POSICOES_PROLOG <@ V_POSICOES_DIAGRAMA_PROLOG)
    THEN
        -- Busca as posições que foram enviadas e não fazem parte das posições do diagrama.
        SELECT ARRAY_AGG(POSICAO.POSICAO_CLIENTE)
        FROM (SELECT UNNEST(F_POSICOES_PROLOG) AS POSICAO_CLIENTE) AS POSICAO
        WHERE POSICAO.POSICAO_CLIENTE NOT IN (SELECT UNNEST(V_POSICOES_DIAGRAMA_PROLOG))
        INTO V_POSICOES_INVALIDAS;
    END IF;

    -- Filtra as posições repetidas do cliente.
    WITH POSICOES_DUPLICADAS AS (
        SELECT POSICAO_DUPLICADA AS POSICAO_DUPLICADA
        FROM UNNEST(F_POSICOES_PROLOG) AS POSICAO_DUPLICADA
        GROUP BY POSICAO_DUPLICADA
        HAVING COUNT(*) > 1
    )

    SELECT ARRAY_AGG(PD.POSICAO_DUPLICADA)
    FROM POSICOES_DUPLICADAS PD
    INTO V_POSICOES_REPETIDAS_PROLOG;

    -- Filtra as posições repetidas do cliente.
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
    IF (ARRAY_LENGTH(V_POSICOES_INVALIDAS, 1) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posições do Sistema Prolog ('
                   || ARRAY_TO_STRING(V_POSICOES_INVALIDAS, ', ') || ') não pertencem ao diagrama de código ('
                   || F_COD_DIAGRAMA || E')\n'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;
    IF (ARRAY_LENGTH(V_POSICOES_REPETIDAS_PROLOG, 1) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posições do Sistema Prolog ('
                   || ARRAY_TO_STRING(V_POSICOES_REPETIDAS_PROLOG, ', ') || ') estão repetidas no diagrama de código ('
                   || F_COD_DIAGRAMA || E')\n'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;
    IF (ARRAY_LENGTH(V_POSICOES_REPETIDAS_PARCEIRO, 1) > 0)
    THEN
        SELECT V_MENSAGEM_RETORNO || 'As posições do Globus ('
                   || ARRAY_TO_STRING(V_POSICOES_REPETIDAS_PARCEIRO, ', ') ||
               ') estão repetidas no diagrama de código (' || F_COD_DIAGRAMA || ')'
        INTO V_MENSAGEM_RETORNO;
        V_TEM_ERRO = TRUE;
    END IF;

    IF (V_TEM_ERRO) THEN
        PERFORM THROW_GENERIC_ERROR(V_MENSAGEM_RETORNO);
    END IF;

    -- Caso nenhuma exception for lançada, retornamos sucesso.
    RETURN F_COD_DIAGRAMA;
END;
$$;