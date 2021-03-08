CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_INSERT_OPCOES_PROBLEMAS(F_COD_EMPRESA BIGINT,
                                                                     F_DESCRICAO TEXT,
                                                                     F_OBRIGA_DESCRICAO BOOLEAN,
                                                                     F_COD_COLABORADOR BIGINT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_OPCAO_PROBLEMA_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT SROP.CODIGO
              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
              WHERE UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_DESCRICAO))
                AND SROP.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma descrição \'%s\' cadastrada nesta empresa', F_DESCRICAO));
    END IF;

    INSERT INTO SOCORRO_ROTA_OPCAO_PROBLEMA (COD_EMPRESA, DESCRICAO, OBRIGA_DESCRICAO,
                                             COD_COLABORADOR_ULTIMA_ATUALIZACAO, DATA_HORA_ULTIMA_ATUALIZACAO)
    VALUES (F_COD_EMPRESA, F_DESCRICAO, F_OBRIGA_DESCRICAO, F_COD_COLABORADOR, F_DATA_HORA)
    RETURNING CODIGO INTO F_COD_OPCAO_PROBLEMA_INSERIDO;

    IF F_COD_OPCAO_PROBLEMA_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o opção de problema, tente novamente');
    END IF;

    RETURN F_COD_OPCAO_PROBLEMA_INSERIDO;
END;
$$;