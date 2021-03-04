-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Retorna de 0 a 1 a similariedade entre dois TEXTs. Aonde 0 significa que os textos são diferentes e 1 que são
-- identicos.
--
-- Précondições:
-- 1) A function remove os caracteres especiais dos atributos antes de realizar a similaridade. Após, realiza o
-- procedimento e retorna o valor da similaridade encontrada.
-- 2) Function: REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(TEXT) criada.
--
-- Histórico:
-- 2019-08-13 -> Function criada (thaisksf - PL-2186).
CREATE OR REPLACE FUNCTION FUNC_GERA_SIMILARIDADE(F_TEXTO1 TEXT,
                                                  F_TEXTO2 TEXT)
    RETURNS REAL
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN SIMILARITY(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO1),
                      REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO2));
END;
$$;