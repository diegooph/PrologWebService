-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador existe a partir do código informado.
--
-- Precondições:
-- 1) Necessário o código do colaborador para a verificação.
--
-- Histórico:
-- 2020-02-03 -> Function criada (wvinim).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_COD_COLABORADOR_EXISTE(
  F_COD_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE O COLABORADOR EXISTE
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CODIGO = F_COD_COLABORADOR)
  THEN RAISE EXCEPTION 'O colaborador com CÓDIGO: % não está cadastrado.', F_COD_COLABORADOR;
  END IF;
END;
$$;