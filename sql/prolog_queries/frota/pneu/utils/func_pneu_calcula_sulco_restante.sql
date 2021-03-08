DROP FUNCTION FUNC_PNEU_CALCULA_SULCO_RESTANTE(
  F_VIDA_ATUAL_PNEU        INTEGER,
  F_VIDAS_TOTAL_PNEU       INTEGER,
  F_SULCO_1                REAL,
  F_SULCO_2                REAL,
  F_SULCO_3                REAL,
  F_SULCO_4                REAL,
  F_SULCO_MINIMO_RECAPAGEM REAL,
  F_SULCO_MINIMO_DESCARTE  REAL);


CREATE FUNCTION FUNC_PNEU_CALCULA_SULCO_RESTANTE(
  F_VIDA_ATUAL_PNEU        INTEGER,
  F_VIDAS_TOTAL_PNEU       INTEGER,
  F_SULCO_1                REAL,
  F_SULCO_2                REAL,
  F_SULCO_3                REAL,
  F_SULCO_4                REAL,
  F_SULCO_MINIMO_RECAPAGEM REAL,
  F_SULCO_MINIMO_DESCARTE  REAL)
  RETURNS REAL
LANGUAGE PLPGSQL
AS $$
DECLARE
  SULCO_MININO REAL;
BEGIN
  IF F_VIDA_ATUAL_PNEU > F_VIDAS_TOTAL_PNEU
  THEN RAISE EXCEPTION 'A vida atual do pneu não pode ser maior do que o total de vidas';
  END IF;

  SULCO_MININO := CASE
                  WHEN F_VIDA_ATUAL_PNEU < F_VIDAS_TOTAL_PNEU
                    THEN F_SULCO_MINIMO_RECAPAGEM
                  WHEN F_VIDA_ATUAL_PNEU = F_VIDAS_TOTAL_PNEU
                    THEN F_SULCO_MINIMO_DESCARTE
                  END;
  RETURN LEAST(F_SULCO_1, F_SULCO_2, F_SULCO_3, F_SULCO_4) - SULCO_MININO;
END;
$$;