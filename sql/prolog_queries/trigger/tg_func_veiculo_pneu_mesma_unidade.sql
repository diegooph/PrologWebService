--Impede que seja alterado a unidade na tabela veiculo_pneu, caso o veículo tenha pneus associados que estejam na unidade anterior


CREATE OR REPLACE FUNCTION tg_func_veiculo_pneu_mesma_unidade()
  RETURNS trigger AS $veiculo_pneu_mesma_unidade_trigger$
BEGIN
  IF (SELECT V.COD_UNIDADE
      FROM VEICULO V
      WHERE V.PLACA = NEW.PLACA) != (SELECT P.COD_UNIDADE
                                     FROM PNEU P
                                     WHERE P.CODIGO = NEW.COD_PNEU)
  THEN
    RAISE EXCEPTION 'Não é possível associar pneus a veículos que sejam de unidades diferentes';
  END IF;
  RETURN NEW;
END;
$veiculo_pneu_mesma_unidade_trigger$
LANGUAGE plpgsql;

CREATE TRIGGER tg_veiculo_pneu_mesma_unidade
  BEFORE INSERT OR UPDATE ON VEICULO_PNEU
  FOR EACH ROW
  EXECUTE PROCEDURE tg_func_veiculo_pneu_mesma_unidade();

