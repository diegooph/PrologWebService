-- Sobre:
--
-- Function que retorna um timestamp com formato customizado e valor padrão em caso nulo.
--
-- Histórico:
-- 2020-03-03 -> Criação do arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE FUNCTION FORMAT_TIMESTAMP(TS_TZ TIMESTAMP,
                                            TS_FORTMAT TEXT,
                                            VALUE_IF_NULL TEXT DEFAULT NULL)
    RETURNS TEXT
    IMMUTABLE
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN COALESCE(TO_CHAR(TS_TZ, TS_FORTMAT), VALUE_IF_NULL);
END;
$$;