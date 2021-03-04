-- Sobre:
-- Cria trigger function para obrigar que a solicitação de socorro tenha uma descrição de acordo com o tipo de problema.
--
-- Histórico:
-- 2019-12-09 -> Function criada (wvinim - PL-2420).
CREATE OR REPLACE FUNCTION TG_FUNC_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
DECLARE
  OBRIGA_DESCRICAO BOOLEAN := (SELECT OBRIGA_DESCRICAO
                                FROM SOCORRO_ROTA_OPCAO_PROBLEMA
                                WHERE CODIGO = NEW.COD_PROBLEMA_SOCORRO_ROTA);
BEGIN
  IF OBRIGA_DESCRICAO AND (NEW.DESCRICAO_PROBLEMA IS NULL OR LENGTH(NEW.DESCRICAO_PROBLEMA) = 0)
  THEN RAISE EXCEPTION 'Essa opção de problema exige uma descrição.';
  END IF;
  RETURN NEW;
END;
$$;

-- Cria o evento da trigger e chama a function específica
CREATE TRIGGER TG_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA
  BEFORE INSERT
  ON SOCORRO_ROTA_ABERTURA
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA();