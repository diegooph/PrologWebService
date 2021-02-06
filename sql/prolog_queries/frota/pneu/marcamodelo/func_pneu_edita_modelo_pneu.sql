-- Sobre:
--
-- Esta function edita o modelo de pneu.
--
--
-- Histórico:
-- 2019-09-23 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_EDITA_MODELO_PNEU(F_COD_EMPRESA BIGINT,
                                                       F_COD_MARCA_PNEU BIGINT,
                                                       F_COD_MODELO_PNEU BIGINT,
                                                       F_NOME_MODELO_PNEU VARCHAR(255),
                                                       F_QTD_SULCOS INTEGER,
                                                       F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE SQL
AS
$$
UPDATE MODELO_PNEU
SET COD_MARCA     = F_COD_MARCA_PNEU,
    NOME          = REMOVE_EXTRA_SPACES(F_NOME_MODELO_PNEU),
    QT_SULCOS     = F_QTD_SULCOS,
    ALTURA_SULCOS = F_ALTURA_SULCOS
WHERE COD_EMPRESA = F_COD_EMPRESA
  AND CODIGO = F_COD_MODELO_PNEU RETURNING CODIGO;
$$;