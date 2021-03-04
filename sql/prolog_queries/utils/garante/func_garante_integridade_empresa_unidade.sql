-- Sobre:
-- Function que executa uma série de verificações afim de saber se um código de unidade é íntegro com o código de uma
-- empresa.
-- Ser íntegro, neste cenário, significa que ambos os códigos existem no banco de dados e que a unidade pertence à
-- empresa em questão.
-- A function recebe uma mensagem como parâmetro e está é repassada para as demais funtions. Se nenhuma mensagem for
-- fornecida então é repassado NULL para as functions e uma mensagem genérica é exibida em caso de erros.
--
-- Histórico:
-- 2019-07-17 -> Function criada (luizfp).
-- 2019-08-19 -> Function alterada (diogenesvanzella - PL-2222).
--  • Recebe mensagem que deve lançar em caso de erro.
CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA, F_ERROR_MESSAGE);
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE, F_ERROR_MESSAGE);
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE, F_ERROR_MESSAGE);
END;
$$;