-- Sobre:
-- Verifica se os pilares informados existem.
--
-- Histórico:
-- 2019-08-15 -> Function criada (luizfp - PL-2200).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_PILARES_VALIDOS(F_PILARES_VERIFICACAO INTEGER[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PILARES_VALIDOS INTEGER[] := ARRAY [1, 2, 3, 4, 5];
BEGIN
    IF NOT (F_PILARES_VERIFICACAO <@ PILARES_VALIDOS)
    THEN
        RAISE EXCEPTION 'Apenas os pilares % são válidos', ARRAY_TO_STRING(PILARES_VALIDOS, ', ');
    END IF;
END;
$$;