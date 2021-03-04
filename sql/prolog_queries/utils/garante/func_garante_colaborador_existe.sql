-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador existe a partir do cpf informado.
--
-- Precondições:
-- 1) Necessário o CPF do colaborador para a verificação.
--
-- Histórico:
-- 2019-07-30 -> Function criada (thaisksf).
-- 2020-03-25 -> altara function para lançar mensagem genérica (diogenesvanzella - PLI-80).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_COLABORADOR_EXISTE(F_CPF_COLABORADOR BIGINT, F_ERROR_MESSAGE TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    ERROR_MESSAGE TEXT :=
        F_IF(F_ERROR_MESSAGE IS NULL,
             FORMAT('O colaborador com CPF: %s não está cadastrado.', F_CPF_COLABORADOR),
             F_ERROR_MESSAGE);
BEGIN
    -- VERIFICA SE O COLABORADOR EXISTE
    IF NOT EXISTS(SELECT C.CPF
                  FROM COLABORADOR C
                  WHERE C.CPF = F_CPF_COLABORADOR)
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;
END;
$$;