-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Garante que a nomenclatura enviada pelo servidor possui todas as posições nomeadas.
--
-- Precondições:
-- 1) Tabela veiculo_diagrama_posicao_prolog criada
-- 2) Function array_sort criada
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_PNEU_NOMENCLATURA_COMPLETA(F_COD_DIAGRAMA BIGINT,
                                                                   F_POSICOES_PROLOG INTEGER[],
                                                                   F_ERROR_MESSAGE TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    POSICAO_ESTEPE BIGINT := 900;
    ERROR_MESSAGE  TEXT   :=
        F_IF(F_ERROR_MESSAGE IS NULL, 'Erro! Nomenclatura incompleta.', F_ERROR_MESSAGE);
BEGIN
    IF (ARRAY_SORT(F_POSICOES_PROLOG) <> (SELECT ARRAY_AGG(VDP.POSICAO_PROLOG ORDER BY VDP.POSICAO_PROLOG) :: INTEGER[]
                                          FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDP
                                          WHERE VDP.COD_DIAGRAMA = F_COD_DIAGRAMA
                                            AND VDP.POSICAO_PROLOG < POSICAO_ESTEPE))
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;
END;
$$;