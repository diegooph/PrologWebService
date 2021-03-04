-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a função - cargo - existe na empresa informada.
--
-- Precondições:
-- 1) Necessário o código da empresa e o código da funcao para a verificação da integridade entre empresa-funcao.
--
-- Histórico:
-- 2019-07-26 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_CARGO_EXISTE(
  F_COD_EMPRESA BIGINT,
  F_COD_FUNCAO BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE A FUNCAO EXISTE
  IF NOT EXISTS (SELECT F.CODIGO FROM FUNCAO F WHERE F.CODIGO = F_COD_FUNCAO AND F.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'A funcao de codigo: % não existe na empresa: %.', F_COD_FUNCAO,
  (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA);
  END IF;
END;
$$;