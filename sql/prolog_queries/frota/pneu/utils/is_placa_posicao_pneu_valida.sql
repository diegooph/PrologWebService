-- Sobre:
--
-- Function genérica utilizada para validar se a posição é valida para o pneu em questão. A function valida a posição,
-- inclusive, se tratando de posições de ESTEPES, mas para isso é preciso indicar para a function que o pneu é um
-- estepe.
--
-- Histórico:
-- 2019-08-15 -> Function criada (diogenesvanzella - PL-2222).
-- 2019-09-10 -> Altera vínculo da tabela pneu_ordem_nomenclatura_unidade para
--               veiculo_diagrama_posicao_prolog. (thaisksf - PL-2258)
CREATE OR REPLACE FUNCTION IS_PLACA_POSICAO_PNEU_VALIDA(
  F_COD_VEICULO BIGINT,
  F_POSICAO_PNEU INTEGER,
  F_IS_PNEU_ESTEPE BOOLEAN)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS
$$
DECLARE
  IS_POSICAO_VALIDA BOOLEAN;
BEGIN
  IF (F_IS_PNEU_ESTEPE)
  THEN
    SELECT (F_POSICAO_PNEU >= 900 AND F_POSICAO_PNEU <= 908) INTO IS_POSICAO_VALIDA;
  ELSE
    SELECT EXISTS(SELECT
                    VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.POSICAO_PROLOG = F_POSICAO_PNEU
                        AND VDPP.COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                                                 FROM VEICULO_TIPO VT
                                                 WHERE VT.CODIGO = (SELECT V.COD_TIPO
                                                                    FROM VEICULO V
                                                                    WHERE V.CODIGO = F_COD_VEICULO)))
    INTO IS_POSICAO_VALIDA;
  END IF;
  RETURN IS_POSICAO_VALIDA;
END;
$$;