-- Sobre:
--
-- Function que retorna a conversão da data com o timezone aplicado a partir de um timestamp.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE FUNCTION TZ_DATE(TIMESTAMP WITH TIME ZONE, TEXT) RETURNS DATE
    LANGUAGE PLPGSQL
    STABLE STRICT
AS
$$
BEGIN
    RETURN ($1 AT TIME ZONE $2) :: DATE;
END;
$$;