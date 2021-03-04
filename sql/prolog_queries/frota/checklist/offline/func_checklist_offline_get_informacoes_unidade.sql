--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR INFORMAÇÕES DA EMPRESA        ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_INFORMACOES_UNIDADE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_EMPRESA   BIGINT,
    NOME_EMPRESA  TEXT,
    COD_REGIONAL  BIGINT,
    NOME_REGIONAL TEXT,
    COD_UNIDADE   BIGINT,
    NOME_UNIDADE  TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    E.CODIGO       AS COD_EMPRESA,
    E.NOME::TEXT   AS NOME_EMPRESA,
    R.CODIGO       AS COD_REGIONAL,
    R.REGIAO::TEXT AS NOME_REGIONAL,
    U.CODIGO       AS COD_UNIDADE,
    U.NOME::TEXT   AS NOME_UNIDADE
  FROM UNIDADE U
    JOIN EMPRESA E
      ON U.COD_EMPRESA = E.CODIGO
    JOIN REGIONAL R
      ON U.COD_REGIONAL = R.CODIGO
  WHERE U.CODIGO = F_COD_UNIDADE;
END;
$$;