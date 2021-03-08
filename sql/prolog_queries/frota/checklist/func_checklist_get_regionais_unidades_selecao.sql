-- Sobre:
-- Busca as regionais e unidades que o colaborador tem acesso para seleção na realização de um checklist.
--
-- Histórico:
-- 2019-10-25 -> Corrige order by da function (luizfp).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REGIONAIS_UNIDADES_SELECAO(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    CODIGO_REGIONAL                                            BIGINT,
    NOME_REGIONAL                                              TEXT,
    CODIGO_UNIDADE                                             BIGINT,
    NOME_UNIDADE                                               TEXT,
    REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  EMPRESA_BLOQUEADA_DIFERENTES_UNIDADES BOOLEAN := FUNC_CHECKLIST_REALIZACAO_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(
      (SELECT C.COD_EMPRESA
       FROM COLABORADOR C
       WHERE C.CODIGO = F_COD_COLABORADOR));
BEGIN
  RETURN QUERY
  SELECT DISTINCT ON (F.CODIGO_REGIONAL, F.CODIGO_UNIDADE)
    F.CODIGO_REGIONAL                     AS CODIGO_REGIONAL,
    F.NOME_REGIONAL                       AS NOME_REGIONAL,
    F.CODIGO_UNIDADE                      AS CODIGO_UNIDADE,
    F.NOME_UNIDADE                        AS NOME_UNIDADE,
    EMPRESA_BLOQUEADA_DIFERENTES_UNIDADES AS REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA
  FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
  ORDER BY F.CODIGO_REGIONAL ASC, F.CODIGO_UNIDADE ASC;
END;
$$;