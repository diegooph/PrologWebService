-- Sobre:
-- Remove todos os valores repetidos do array informado. Inclusive valores NULLs repetidos (apenas um será mantido).
-- Em caso de elementos repetidos, não há garantia de qual local do array o elemento repetido será removido.
--
-- Também é possível remover os valores NULL por completo, passando TRUE como segundo parâmetro (o default é FALSE).
CREATE OR REPLACE FUNCTION ARRAY_DISTINCT(ANYARRAY, BOOLEAN DEFAULT FALSE) RETURNS ANYARRAY
    IMMUTABLE
    LANGUAGE SQL
AS
$$
SELECT ARRAY_AGG(DISTINCT X)
FROM UNNEST($1) T(X)
WHERE CASE WHEN $2 THEN X IS NOT NULL ELSE TRUE END;
$$;