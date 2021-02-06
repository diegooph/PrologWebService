-- Sobre:
--
-- Realiza o sort dos elementos de um array de qualquer tipo.
--
-- Histórico:
-- 2019-09-12 -> Function criada (luizfp PL-2259).
CREATE OR REPLACE FUNCTION ARRAY_SORT(ANYARRAY)
    RETURNS ANYARRAY
    LANGUAGE SQL
AS
$$
SELECT ARRAY(SELECT UNNEST($1) ORDER BY 1)
$$;