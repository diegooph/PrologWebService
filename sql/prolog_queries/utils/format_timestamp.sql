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