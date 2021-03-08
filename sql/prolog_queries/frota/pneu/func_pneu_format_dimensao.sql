CREATE OR REPLACE FUNCTION FUNC_PNEU_FORMAT_DIMENSAO(F_LARGURA INTEGER,
                                                     F_ALTURA INTEGER,
                                                     F_ARO REAL)
    RETURNS TEXT
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN (((F_LARGURA || '/' :: TEXT) || F_ALTURA) || ' R' :: TEXT) || F_ARO;
END;
$$;
