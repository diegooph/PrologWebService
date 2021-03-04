-- Sobre:
--
-- Function disponível na API do ProLog para buscar a configuração de carga inicial para empresa em questão. A function
-- retorna se a empresa está configurada para sobrescrever (true) ou não (false) as informações de pneus.
--
-- Histórico:
-- 2019-01-22 -> Function criada (diogenesvanzella - PL-64).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_PNEUS(F_COD_EMPRESA BIGINT)
    RETURNS BOOLEAN
    LANGUAGE SQL
AS
$$
SELECT F_IF(SOBRESCREVE_PNEUS IS NULL, FALSE, SOBRESCREVE_PNEUS) AS SOBRESCREVE_PNEUS
FROM INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL
WHERE COD_EMPRESA = F_COD_EMPRESA;
$$;