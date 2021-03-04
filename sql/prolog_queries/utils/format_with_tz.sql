CREATE OR REPLACE FUNCTION FORMAT_WITH_TZ(
  TS_TZ         TIMESTAMP WITH TIME ZONE,
  TZ_UNIDADE    TEXT,
  TS_FORTMAT    TEXT,
  VALUE_IF_NULL TEXT DEFAULT NULL)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN COALESCE(TO_CHAR(TS_TZ AT TIME ZONE TZ_UNIDADE, TS_FORTMAT), VALUE_IF_NULL);
END;
$$;