-- Se o código da OS for null, nós geramos um código baseado no maior código já existente para a unidade + 1.
-- Utilizamos a tabela CHECKLIST_ORDEM_SERVICO_DATA para isso e não a view CHECKLIST_ORDEM_SERVICO para considerar
-- OSs deletadas na busca do maior código.
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_OS_GERA_CODIGO_OS()
  RETURNS TRIGGER AS $$
BEGIN
  -- Em integrações forçamos o código. Por isso pode ser diferente de NULL.
  IF NEW.CODIGO IS NULL
  THEN
    -- Precisamos usar um COALESCE pois a unidade pode não ter nenhuma O.S. em aberto, assim, MAX(COS.CODIGO) + 1 iria
    -- retornar null.
    NEW.CODIGO := (SELECT COALESCE(MAX(COS.CODIGO) + 1, 1)
                   FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                   WHERE COS.COD_UNIDADE = NEW.COD_UNIDADE);
  END IF;
  RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

-- CRIA A TRIGGER.
CREATE TRIGGER TG_GERA_CODIGO_OS
  BEFORE INSERT
  ON CHECKLIST_ORDEM_SERVICO_DATA
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_CHECKLIST_OS_GERA_CODIGO_OS();