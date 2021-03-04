-- Sobre:
-- Verifica se o atributo enviado é um timezone válido. Retorna TRUE caso seja válido; FALSE caso contrário.
--
-- O modo como a verificação funciona é tentando aplicar o timezone fornecido no now(). Se um erro ocorrer, significa
-- que o timezone fornecido não é válido e então a func retornará FALSE.
--
-- Histórico:
-- 2019-08-15 -> Function criada (luizfp - PL-2200).
CREATE OR REPLACE FUNCTION IS_TIMEZONE(TZ TEXT) RETURNS BOOLEAN AS
$$
DECLARE
    DATE TIMESTAMPTZ;
BEGIN
    DATE := NOW() AT TIME ZONE TZ;
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE PLPGSQL STABLE;