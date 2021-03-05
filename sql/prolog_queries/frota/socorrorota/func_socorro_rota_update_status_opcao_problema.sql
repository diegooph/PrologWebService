CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_UPDATE_STATUS_OPCAO_PROBLEMA(F_COD_EMPRESA BIGINT,
                                                                          F_COD_COLABORADOR BIGINT,
                                                                          F_COD_OPCAO_PROBLEMA BIGINT,
                                                                          F_STATUS_ATIVO BOOLEAN)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_CPF_COLABORADOR                        BIGINT := (SELECT CD.CPF
                                                        FROM COLABORADOR_DATA CD
                                                        WHERE CD.CODIGO = F_COD_COLABORADOR);
    -- Todos os campos a seguir devem ser buscados no registro anterior, antes da alteração
    -- Estes campos serão adicionados na tabela de histórico.
    F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    F_OLD_DATA_HORA_ALTERACAO                TIMESTAMP WITH TIME ZONE;
    F_OLD_DESCRICAO                          TEXT;
    F_OLD_OBRIGA_DESCRICAO                   BOOLEAN;
    F_OLD_STATUS_ATIVO                       BOOLEAN;
    F_COD_HISTORICO_ALTERACAO                BIGINT;

BEGIN
    -- VERIFICA SE EMPRESA EXISTE.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(F_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Busca registro para histórico.
    SELECT SROP.COD_COLABORADOR_ULTIMA_ATUALIZACAO,
           SROP.DATA_HORA_ULTIMA_ATUALIZACAO,
           SROP.DESCRICAO,
           SROP.OBRIGA_DESCRICAO,
           SROP.STATUS_ATIVO
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
    WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
      AND SROP.CODIGO = F_COD_OPCAO_PROBLEMA
    INTO F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
        F_OLD_DATA_HORA_ALTERACAO,
        F_OLD_DESCRICAO,
        F_OLD_OBRIGA_DESCRICAO,
        F_OLD_STATUS_ATIVO;

    IF F_OLD_DESCRICAO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar a opção de problema.');
    END IF;

    -- Se estamos ativando uma opcao de problema e existe outra na mesma empresa, de mesma descrição e já ativa, é
    -- lançado erro.
    IF F_STATUS_ATIVO AND (SELECT EXISTS(SELECT SROP.CODIGO
                                         FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
                                         WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
                                           AND UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_OLD_DESCRICAO))
                                           AND SROP.STATUS_ATIVO = TRUE))
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro! Já existe uma opção de problema ativa com esse nome.');
    END IF;

    UPDATE SOCORRO_ROTA_OPCAO_PROBLEMA
    SET STATUS_ATIVO                       = F_STATUS_ATIVO,
        COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
        DATA_HORA_ULTIMA_ATUALIZACAO       = NOW()
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND CODIGO = F_COD_OPCAO_PROBLEMA;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o status da opção de problema % para %', F_OLD_DESCRICAO, F_STATUS_ATIVO;
    END IF;

    INSERT INTO SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO (COD_PROBLEMA_SOCORRO_ROTA,
                                                       COD_COLABORADOR_ALTERACAO,
                                                       DATA_HORA_ALTERACAO,
                                                       COD_EMPRESA,
                                                       DESCRICAO,
                                                       OBRIGA_DESCRICAO,
                                                       STATUS_ATIVO)
    VALUES (F_COD_OPCAO_PROBLEMA,
            F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
            F_OLD_DATA_HORA_ALTERACAO,
            F_COD_EMPRESA,
            F_OLD_DESCRICAO,
            F_OLD_OBRIGA_DESCRICAO,
            F_STATUS_ATIVO) RETURNING CODIGO INTO F_COD_HISTORICO_ALTERACAO;

    IF NOT FOUND OR F_COD_HISTORICO_ALTERACAO IS NULL OR F_COD_HISTORICO_ALTERACAO <= 0 THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível criar um histórico para essa mudança de status de opção de' ||
                                    ' problema, contate nosso suporte.');
    END IF;
END;
$$;