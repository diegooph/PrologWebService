-- Sobre:
--
-- Function responsável por alterar as informações de banda de um pneu.
--
-- Histórico:
-- 2019-11-28 -> Altera nome de colunas de algumas tabelas (luizfp - PL-2295).
CREATE OR REPLACE FUNCTION FUNC_PNEUS_UPDATE_BANDA_PNEU(F_COD_PNEU BIGINT,
                                                        F_COD_MODELO_BANDA BIGINT,
                                                        F_CUSTO_BANDA REAL)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_SERVICO_REALIZADO BIGINT;
BEGIN
    F_COD_SERVICO_REALIZADO = (
        SELECT CODIGO
        FROM PNEU_SERVICO_REALIZADO PSR
                 JOIN PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA PSRIV
                      ON PSR.CODIGO = PSRIV.COD_SERVICO_REALIZADO
                          AND PSR.FONTE_SERVICO_REALIZADO = PSRIV.FONTE_SERVICO_REALIZADO
        WHERE PSR.COD_PNEU = F_COD_PNEU
        ORDER BY CODIGO DESC
        LIMIT 1);
    UPDATE PNEU_SERVICO_REALIZADO
    SET CUSTO = F_CUSTO_BANDA
    WHERE CODIGO = F_COD_SERVICO_REALIZADO;
    UPDATE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    SET COD_MODELO_BANDA = F_COD_MODELO_BANDA
    WHERE COD_SERVICO_REALIZADO = F_COD_SERVICO_REALIZADO;

    -- FOUND será true se alguma linha foi modificada pela query executada.
    IF FOUND THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$;