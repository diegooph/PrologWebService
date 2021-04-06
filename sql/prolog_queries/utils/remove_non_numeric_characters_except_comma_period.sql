CREATE OR REPLACE FUNCTION remove_non_numeric_characters_except_comma_period(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Remove letras, characteres especiais e espaços, exceto vírgula e ponto final.
    -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
    RETURN REGEXP_REPLACE(F_TEXTO, '[^-,.0-9]+', '', 'g');
END;
$$;