-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove espaços excedentes.
--
-- Precondições:
-- 1) Recebe um texto para verificar e remover os espaços.
--
-- Histórico:
-- 2019-09-03 -> deprecated - utilizar a function remove_extra_spaces - (thaisksf).
CREATE OR REPLACE FUNCTION TRIM_AND_REMOVE_EXTRA_SPACES(F_TEXT TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Além do trim, se existir mais de um espaço entre duas strings, ele será reduzido a apenas um espaço.
  RETURN TRIM(REGEXP_REPLACE(F_TEXT, '\s+', ' ', 'g'));
END;
$$;