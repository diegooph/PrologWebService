-- Sobre:
-- Verifica se o código da unidade fornecido é um código válido, ou seja, existe no banco de dados.
-- A function pode receber como parâmetro a mensagem de erro que deverá lançar caso o código da unidade não seja
-- válido. Caso não receber nenhuma mensagem no parâmetro, irá lançar uma mensagem genérica.
--
-- Histórico:
-- 2019-07-03 -> Function criada (luizfp).
-- 2019-08-19 -> Function alterada (diogenesvanzella - PL-2222).
--  • Recebe mensagem que deve lançar em caso de erro.
CREATE OR REPLACE FUNCTION FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE BIGINT, F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL, FORMAT('Unidade de código %s não existe!', F_COD_UNIDADE), F_ERROR_MESSAGE);
BEGIN
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;