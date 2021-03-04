-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se o setor existe na unidade informada.
--
-- Precondições:
-- 1) Necessário o código da unidade e o código do setor para a verificação da integridade entre unidade-setor.
--
-- Histórico:
-- 2019-07-23 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_SETOR_EXISTE(
  F_COD_UNIDADE BIGINT,
  F_COD_SETOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE O SETOR EXISTE
  IF NOT EXISTS (SELECT S.CODIGO FROM SETOR S WHERE S.CODIGO = F_COD_SETOR AND S.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'O setor % não existe na unidade %.', F_COD_SETOR, (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;