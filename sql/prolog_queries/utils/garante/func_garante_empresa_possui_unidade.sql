-- Sobre:
-- Verifica se o código da unidade fornecido pertence ao código da empresa fornecida. Caso a unidade não pertença a
-- empresa então a function irá lançar uma mensagem de erro. A mensagem de erro pode ser passada como parâmetro ou,
-- caso nenhuma seja fornecida, uma mensagem genérica será lançada.
--
-- Histórico:
-- 2019-07-03 -> Function criada (luizfp).
-- 2019-08-19 -> Function alterada (diogenesvanzella - PL-2222).
--  • Recebe mensagem que deve lançar em caso de erro.
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL,
       FORMAT('A unidade %s não pertence a empresa %s!', F_COD_UNIDADE, F_COD_EMPRESA),
       F_ERROR_MESSAGE);
BEGIN
  -- Verifica se a unidade é da empresa informada.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE AND U.COD_EMPRESA = F_COD_EMPRESA)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;