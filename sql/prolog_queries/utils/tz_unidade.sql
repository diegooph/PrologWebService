-- Sobre:
--
-- Function que retorna o timezone da unidade a partir do código.
--
-- Histórico:
-- 2020-03-03 -> Criação do arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE FUNCTION TZ_UNIDADE(F_COD_UNIDADE BIGINT) RETURNS TEXT
    LANGUAGE PLPGSQL
    STABLE STRICT
AS
$$
DECLARE
    TZ TEXT;
BEGIN
    SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE INTO TZ;
    RETURN TZ;
END;
$$;