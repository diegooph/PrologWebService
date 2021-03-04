-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se o as empresas são iguais.
--
-- Precondições:
-- 1) Necessário o código das empresas para verificar se são iguais.
--
-- Histórico:
-- 2019-07-23 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESAS_DISTINTAS(
  F_COD_EMPRESA_1 BIGINT,
  F_COD_EMPRESA_2 BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Verifica se as empresas são iguais.
  IF (F_COD_EMPRESA_1 = F_COD_EMPRESA_2)
  THEN RAISE EXCEPTION 'As empresas são iguais';
  END IF;
END;
$$;