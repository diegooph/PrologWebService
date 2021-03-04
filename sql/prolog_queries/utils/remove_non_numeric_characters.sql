-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove letras, characteres especiais e espaços do text recebido na function.
--
-- Histórico:
-- 2019-12-13 -> Function criada (thaisksf - PL-2320).
CREATE OR REPLACE FUNCTION REMOVE_NON_NUMERIC_CHARACTERS(F_TEXTO TEXT)
    RETURNS TEXT
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Remove letras, characteres especiais e espaços.
    -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
    RETURN REGEXP_REPLACE(F_TEXTO, '[^0-9]+', '', 'g');
END;
$$;