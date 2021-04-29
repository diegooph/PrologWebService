-- Sobre:
--
-- Function criada para utilizar o algoritmo de comparação de strings Jaro, substituindo o soundex.
-- https://rosettacode.org/wiki/Jaro_distance
-- Esse é o mesmo algortimo usado para detectar mudanças nas respostas de alternativas tipo_outros no java, na classe
-- TipoOutrosSimilarityFinder.
--
-- Recebe dois textos e retorna um índice de equivalência.
CREATE FUNCTION FUNC_JARO(TEXT, TEXT)
    RETURNS DOUBLE PRECISION
    IMMUTABLE
    STRICT
    LANGUAGE SQL
AS
$$
SELECT JARO(UNACCENT(LOWER(TRIM($1))), UNACCENT(LOWER(TRIM($2))));
$$;