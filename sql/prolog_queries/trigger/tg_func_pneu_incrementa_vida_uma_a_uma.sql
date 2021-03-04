-- Impede que a vida do pneu seja incrementada em mais de 1 valor por vez. Ou seja, não podemos alterar a vida do pneu
-- de 1 para 3, por exemplo.
CREATE OR REPLACE FUNCTION tg_func_pneu_incrementa_vida_uma_a_uma()
  RETURNS trigger AS $pneu_incrementa_vida_uma_a_uma$
BEGIN
  IF ((NEW.VIDA_ATUAL > OLD.VIDA_ATUAL) AND (NEW.VIDA_ATUAL != (OLD.VIDA_ATUAL + 1)))
  THEN
    RAISE EXCEPTION 'Não é possível incrementar mais de uma vida por vez do pneu!'
    USING HINT = 'A vida atual é ' || OLD.VIDA_ATUAL || ' e você está tentando inserir a vida ' || NEW.VIDA_ATUAL || '. Tente aumentar uma vida por vez.';
  END IF;
  RETURN NEW;
END;
$pneu_incrementa_vida_uma_a_uma$
LANGUAGE plpgsql;

CREATE TRIGGER tg_pneu_incrementa_vida_uma_a_uma
  BEFORE UPDATE ON PNEU
  FOR EACH ROW
  EXECUTE PROCEDURE tg_func_pneu_incrementa_vida_uma_a_uma();

