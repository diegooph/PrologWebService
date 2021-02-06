-- Sobre:
-- Esta function edita uma opção de problema
--
-- Observações:
-- É mantida uma tabela de histórico de alterações, ela recebe todas as informações anteriores ao update
-- da tabela socorro_rota_opcao_problema.
--
-- Histórico:
-- 2020-01-15 -> Function criada (wvinim - PL-2467).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_UPDATE_OPCOES_PROBLEMAS(F_COD_OPCAO_PROBLEMA BIGINT,
                                                                     F_COD_EMPRESA BIGINT,
                                                                     F_DESCRICAO TEXT,
                                                                     F_OBRIGA_DESCRICAO BOOLEAN,
                                                                     F_COD_COLABORADOR BIGINT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE) RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Todos os campos a seguir devem ser buscados no registro anterior, antes da alteração
    -- Estes campos serão adicionados na tabela de histórico
    F_OLD_DESCRICAO                          TEXT;
    F_OLD_OBRIGA_DESCRICAO                   BOOLEAN;
    F_OLD_STATUS_ATIVO                       BOOLEAN;
    F_OLD_DATA_HORA_ALTERACAO                TIMESTAMP WITH TIME ZONE;
    F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    F_COD_HISTORICO_ALTERACAO                BIGINT;
BEGIN
    IF EXISTS(SELECT SROP.CODIGO
              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
              WHERE UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_DESCRICAO))
                AND SROP.CODIGO != F_COD_OPCAO_PROBLEMA
                AND SROP.STATUS_ATIVO
                AND SROP.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma descrição \'%s\' cadastrada na empresa', F_DESCRICAO));
    END IF;

    SELECT DESCRICAO, OBRIGA_DESCRICAO, DATA_HORA_ULTIMA_ATUALIZACAO, COD_COLABORADOR_ULTIMA_ATUALIZACAO, STATUS_ATIVO
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA
    WHERE CODIGO = F_COD_OPCAO_PROBLEMA
    INTO F_OLD_DESCRICAO, F_OLD_OBRIGA_DESCRICAO, F_OLD_DATA_HORA_ALTERACAO, F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
        F_OLD_STATUS_ATIVO;

    IF F_OLD_DESCRICAO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar a opção de problema.');
    END IF;

    UPDATE SOCORRO_ROTA_OPCAO_PROBLEMA
    SET DESCRICAO                          = F_DESCRICAO,
        OBRIGA_DESCRICAO                   = F_OBRIGA_DESCRICAO,
        COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
        DATA_HORA_ULTIMA_ATUALIZACAO       = F_DATA_HORA
    WHERE CODIGO = F_COD_OPCAO_PROBLEMA;

    -- FOUND será true se alguma linha foi modificada pela query executada.
    IF NOT FOUND THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível editar a opção de problema.');
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
            F_OLD_STATUS_ATIVO) RETURNING CODIGO INTO F_COD_HISTORICO_ALTERACAO;

    IF NOT FOUND OR F_COD_HISTORICO_ALTERACAO IS NULL OR F_COD_HISTORICO_ALTERACAO <= 0 THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível criar um histórico para a edição de problema, ' ||
                                    'contate nosso suporte.');
    END IF;

    RETURN TRUE;
END;
$$;