-- Sobre:
-- Verifica se o código da empresa fornecido é um código válido, ou seja, existe no banco de dados.
-- A function pode receber como parâmetro a mensagem de erro que deverá lançar caso o código da empresa não seja
-- válido. Caso não receber nenhuma mensagem no parâmetro, irá lançar uma mensagem genérica.
--
-- Histórico:
-- 2019-07-03 -> Function criada (luizfp).
-- 2019-08-19 -> Function alterada (diogenesvanzella - PL-2222).
--  • Recebe mensagem que deve lançar em caso de erro.
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA BIGINT, F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL, FORMAT('Empresa de código %s não existe!', F_COD_EMPRESA), F_ERROR_MESSAGE);
BEGIN
  IF NOT EXISTS(SELECT E.CODIGO
                FROM EMPRESA E
                WHERE E.CODIGO = F_COD_EMPRESA)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;