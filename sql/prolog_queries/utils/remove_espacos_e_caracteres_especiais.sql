CREATE OR REPLACE FUNCTION REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN REGEXP_REPLACE(UNACCENT(F_TEXTO), '[^a-zA-Z0-9]+', '', 'g');
END;
$$;