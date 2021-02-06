-- Sobre:
-- Busca todos os colaboradores ATIVOS de uma empresa que possuam a permissão de poder liberar permissões para outros.
-- cargos. Essa function é um atalho para a function: FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA.
--
-- Histórico:
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_BUSCA_VINCULAR_PERMISSAO(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                FUNCIONALIDADE  TEXT,
                PERMISSAO       TEXT,
                COD_EMPRESA     BIGINT,
                EMPRESA         TEXT,
                COD_UNIDADE     BIGINT,
                UNIDADE         TEXT,
                COD_COLABORADOR BIGINT,
                COLABORADOR     TEXT,
                CPF             BIGINT,
                DATA_NASCIMENTO DATE,
                CARGO           TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_FUNCAO BIGINT := 329;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    RETURN QUERY
        SELECT *
        FROM SUPORTE.FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA(F_COD_EMPRESA, F_COD_FUNCAO);
END;
$$;