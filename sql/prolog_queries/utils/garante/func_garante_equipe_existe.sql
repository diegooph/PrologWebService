-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a equipe existe na unidade informada.
--
-- Precondições:
-- 1) Necessário o código da unidade e o código da equipe para a verificação da integridade entre unidade-equipe.
--
-- Histórico:
-- 2019-07-26 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EQUIPE_EXISTE(
  F_COD_UNIDADE BIGINT,
  F_COD_EQUIPE BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE A EQUIPE EXISTE
  IF NOT EXISTS (SELECT E.CODIGO FROM EQUIPE E WHERE E.CODIGO = F_COD_EQUIPE AND E.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'A equipe de codigo: % não existe na unidade: %.', F_COD_EQUIPE,
  (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;