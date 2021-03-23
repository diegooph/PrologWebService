-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta a nomenclatura de estepes de um diagrama.
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_DELETA_ESTEPES(F_COD_EMPRESA BIGINT,
                                                                 F_COD_DIAGRAMA BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    ESTEPES BIGINT := 900;
BEGIN
    DELETE FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA WHERE COD_EMPRESA = F_COD_EMPRESA AND
                                                        COD_DIAGRAMA = F_COD_DIAGRAMA AND
                                                        POSICAO_PROLOG >= ESTEPES;
END;
$$;
