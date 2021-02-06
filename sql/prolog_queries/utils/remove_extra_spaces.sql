-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove espaços excedentes e "characteres" que simulam espaço.
--
-- Precondições:
-- 1) Recebe um texto para verificar e remover os espaços.
-- 2) Booleando: se for true, remove também os characteres que simulam espaço.
--                      false, apenas os espaços.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION REMOVE_EXTRA_SPACES(F_TEXT TEXT, F_REMOVE_CHAR_LIKE_SPACE BOOLEAN DEFAULT FALSE) RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF F_REMOVE_CHAR_LIKE_SPACE
    THEN
        RETURN REGEXP_REPLACE(TRIM(REGEXP_REPLACE((F_TEXT), '[\u0080-\u00ff]', '', 'g')), '\s+', ' ', 'g');
        ELSE
        RETURN TRIM(REGEXP_REPLACE(F_TEXT, '\s+', ' ', 'g'));
    END IF;
END;
$$;