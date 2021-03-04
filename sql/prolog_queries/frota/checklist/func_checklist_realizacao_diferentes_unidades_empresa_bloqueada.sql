CREATE FUNCTION FUNC_CHECKLIST_REALIZACAO_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT EXISTS(SELECT CDUEB.COD_EMPRESA
                FROM CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA CDUEB
                WHERE CDUEB.COD_EMPRESA = F_COD_EMPRESA);
END;
$$;