-- Sobre:
-- Esta function retorna uma das opções de problemas com base em seu código.
--
-- Histórico:
-- 2020-01-16 -> Function criada (thais - PL-2468).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_OPCAO_PROBLEMA_ITEM(F_COD_OPCAO_PROBLEMA BIGINT)
    RETURNS TABLE
            (
                COD_OPCAO_PROBLEMA                  BIGINT,
                DESCRICAO                           TEXT,
                OBRIGA_DESCRICAO                    BOOLEAN,
                STATUS_ATIVO                        BOOLEAN,
                NOME_COLABORADOR_ULTIMA_ATUALIZACAO TEXT,
                DATA_HORA_ULTIMA_ATUALIZACAO        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT SROP.CODIGO,
               SROP.DESCRICAO :: TEXT,
               SROP.OBRIGA_DESCRICAO,
               SROP.STATUS_ATIVO,
               (SELECT CD.NOME
                FROM COLABORADOR_DATA CD
                WHERE CD.CODIGO = SROP.COD_COLABORADOR_ULTIMA_ATUALIZACAO)::TEXT,
               TO_CHAR((SROP.DATA_HORA_ULTIMA_ATUALIZACAO), 'DD/MM/YYYY HH24:MI')
        FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
        WHERE SROP.CODIGO = F_COD_OPCAO_PROBLEMA;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível encontrar essa opção de problema');
    END IF;
END;
$$;