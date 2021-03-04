-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove TODOS os espaços.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
CREATE OR REPLACE FUNCTION REMOVE_ALL_SPACES(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN REGEXP_REPLACE(F_TEXTO, '\s', '', 'g');
END;
$$;