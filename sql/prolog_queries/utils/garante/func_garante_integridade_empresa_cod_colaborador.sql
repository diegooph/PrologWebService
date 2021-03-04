-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador está na empresa informada.
--
-- Precondições:
-- 1) Necessário o código do colaborador e o código da empresa para a verificar a integridade.
--
-- Histórico:
-- 2020-02-03 -> Function criada (wvinim).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(
  F_COD_EMPRESA BIGINT,
  F_COD_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Verifica se colaborador existe.
  PERFORM FUNC_GARANTE_COD_COLABORADOR_EXISTE(F_COD_COLABORADOR);

  -- Verifica se empresa existe.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  -- Verifica se o colaborador pertence à empresa.
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CODIGO = F_COD_COLABORADOR AND C.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'O colaborador com o código: %, nome: %, não pertence a empresa: % - %!',
  F_COD_COLABORADOR,
  (SELECT C.NOME FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
  F_COD_EMPRESA,
  (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA);
  END IF;
END;
$$;