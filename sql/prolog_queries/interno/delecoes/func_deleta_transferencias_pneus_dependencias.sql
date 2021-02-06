-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta transferencias de pneus de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TRANSFERENCIAS_PNEUS_DEPENDENCIAS(V_COD_PNEU_TRANSFERENCIAS_PROCESSOS BIGINT[],
                                                                                 F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM PNEU_TRANSFERENCIA_INFORMACOES PTI
    WHERE PTI.COD_PROCESSO_TRANSFERENCIA = ANY (V_COD_PNEU_TRANSFERENCIAS_PROCESSOS);

    DELETE
    FROM PNEU_TRANSFERENCIA_PROCESSO PTP
    WHERE (PTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES))
       OR (PTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES));
END;
$$;
