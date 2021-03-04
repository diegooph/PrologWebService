-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove caracteres especiais, letras com acentos e espaços do TEXT passado para a function.
--
-- Histórico:
-- 2019-08-13 -> Function criada (thaisksf - PL-2186).
CREATE OR REPLACE FUNCTION REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN REGEXP_REPLACE(UNACCENT(F_TEXTO), '[^a-zA-Z0-9]+', '', 'g');
END;
$$;