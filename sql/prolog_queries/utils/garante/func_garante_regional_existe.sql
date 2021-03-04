-- Sobre:
-- Verifica se a regional informada existe.
--
-- Histórico:
-- 2019-08-15 -> Function criada (luizfp - PL-2200).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_REGIONAL_EXISTE(F_COD_REGIONAL BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NOT EXISTS(SELECT R.CODIGO
                  FROM REGIONAL R
                  WHERE R.CODIGO = F_COD_REGIONAL)
    THEN
        RAISE EXCEPTION 'Regional de código % não existe!', F_COD_REGIONAL;
    END IF;
END;
$$;