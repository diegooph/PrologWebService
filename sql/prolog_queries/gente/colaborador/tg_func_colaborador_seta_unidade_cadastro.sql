CREATE OR REPLACE FUNCTION TG_FUNC_COLABORADOR_SETA_UNIDADE_CADASTRO()
  RETURNS TRIGGER AS $$
BEGIN
  IF NEW.COD_UNIDADE_CADASTRO IS NULL
  THEN
    NEW.COD_UNIDADE_CADASTRO := NEW.COD_UNIDADE;
  ELSEIF NEW.COD_UNIDADE <> NEW.COD_UNIDADE_CADASTRO
    THEN
      RAISE EXCEPTION
      'COD_UNIDADE (%) e COD_UNIDADE_CADASTRO (%) não podem ser diferentes!',
      NEW.COD_UNIDADE,
      NEW.COD_UNIDADE_CADASTRO;
  END IF;
  RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

-- CRIA A TRIGGER.
CREATE TRIGGER TG_SETA_UNIDADE_CADASTRO_COLABORADOR
  BEFORE INSERT
  ON COLABORADOR_DATA
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_COLABORADOR_SETA_UNIDADE_CADASTRO();