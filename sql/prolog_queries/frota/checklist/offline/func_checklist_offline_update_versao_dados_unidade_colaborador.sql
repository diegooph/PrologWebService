CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_COLABORADOR(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO BOOLEAN,
    VERSAO_DADOS_CHECKLIST_UNIDADE      BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT *
  FROM FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT C.COD_UNIDADE
                                                           FROM COLABORADOR C
                                                           WHERE C.CODIGO = F_COD_COLABORADOR));
END;
$$;