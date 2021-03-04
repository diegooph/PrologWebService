-- Sobre:
-- Esta function foi criada para permitir que os cálculos de diferença entre dias sejam retornados com horas acumuladas
-- além de 24, que é o formato padrão do cast.
--
-- Histórico:
-- 2020-02-14 -> Function criada (wvinim - PL-2523).
-- 2020-04-13 -> A function foi tornada IMMUTABLE STRICT. Além disso, dois problemas foram corrigidos:
--               1 - Quando o F_INTERVAL recebido era nulo, ela retornava ':' ao invés de 'null'. Essa alteração
--               permitiu tornar a function STRICT.
--               2 - Quando um INTERVAL tinha menos de 10 horas, o retorno era assim: H:MM:SS, ao invés do padrão
--               HH:MM:SS. Perceba que o HH pode aumentar indefinidamente se o INTERVAL tiver muitas horas, mas ele
--               tem que ter, pelo menos, duas casas decimais, preenchendo a esquerda com 0 se o total de horas for
--               menor do que 10 (luiz_fp - PL-2670).
CREATE OR REPLACE FUNCTION FUNC_CONVERTE_INTERVAL_HHMMSS(F_INTERVAL INTERVAL)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    IMMUTABLE STRICT
AS
$$
DECLARE
	V_DATA_FORMATADA TEXT;
    V_HOURS CONSTANT TEXT := TRUNC(EXTRACT(EPOCH FROM (F_INTERVAL)) / 3600)::TEXT;
BEGIN
    IF F_INTERVAL IS NULL
    THEN
        RETURN NULL;
    END IF;

    SELECT CONCAT(
                   GREATEST(V_HOURS, LPAD(V_HOURS, 2, '0')),
                   ':',
                   TO_CHAR(TO_TIMESTAMP(
                                   EXTRACT(EPOCH FROM (F_INTERVAL))
                               ), 'MI:SS')
               ) INTO V_DATA_FORMATADA;
    RETURN  V_DATA_FORMATADA;
END;
$$;