CREATE OR REPLACE FUNCTION FUNC_CALCULA_DISTANCIA_UNIDADE_ABERTURA(F_LONGITUDE_UNIDADE REAL,
                                                                   F_LATITUDE_UNIDADE REAL,
                                                                   F_LONGITUDE_ABERTURA REAL,
                                                                   F_LATITUDE_ABERTURA REAL)
    RETURNS NUMERIC
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    RETURN TRUNC(
                   (ST_DISTANCE_SPHERE(
                            ST_POINT(
                                    F_LONGITUDE_UNIDADE::REAL,
                                    F_LATITUDE_UNIDADE::REAL),
                            ST_POINT(
                                    F_LONGITUDE_ABERTURA::REAL,
                                    --Neste ponto é realizada a divisão por 1000 pois o retorno do ST_DISTANCE_SPHERE
                                    --é em metros. Dessa forma, dividindo por 1000, passamos para KM.
                                    F_LATITUDE_ABERTURA::REAL)) / 1000)::NUMERIC,
                    -- Este parametro, fazendo parte da function TRUNC, limita o retorno em 2 caracteres apos a virgula.
                   2);
END;
$$;