-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a String possui apenas characteres numéricos.
-- Retorno: true - apenas números.
--          false - possui letras.
-- Histórico:
-- 2020-08-03 -> Function criada (thaisksf - PL-2460).
CREATE OR REPLACE FUNCTION CHECK_NON_NUMERIC_CHARACTERS(F_TEXTO TEXT)
    RETURNS BOOLEAN
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Verifica se a String possui apenas characteres numéricos.
    RETURN F_TEXTO ~ '^[0-9\.]+$';
END;
$$;