CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(F_COD_PROCESSO_TRANSFERENCIA BIGINT,
                                                                      F_COD_PNEUS BIGINT[])
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_ROWS BIGINT;
BEGIN
    -- noinspection SqlInsertValues
    INSERT INTO PNEU_TRANSFERENCIA_INFORMACOES(COD_PROCESSO_TRANSFERENCIA,
                                               COD_PNEU,
                                               ALTURA_SULCO_INTERNO,
                                               ALTURA_SULCO_CENTRAL_INTERNO,
                                               ALTURA_SULCO_CENTRAL_EXTERNO,
                                               ALTURA_SULCO_EXTERNO,
                                               PSI,
                                               VIDA_MOMENTO_TRANSFERENCIA,
                                               POSICAO_PNEU_TRANSFERENCIA)
    SELECT F_COD_PROCESSO_TRANSFERENCIA,
           P.CODIGO,
           P.ALTURA_SULCO_INTERNO,
           P.ALTURA_SULCO_CENTRAL_INTERNO,
           P.ALTURA_SULCO_CENTRAL_EXTERNO,
           P.ALTURA_SULCO_EXTERNO,
           P.PRESSAO_ATUAL,
           P.VIDA_ATUAL,
           (SELECT VP.POSICAO FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = P.CODIGO)
    FROM PNEU P
    WHERE P.CODIGO = ANY (F_COD_PNEUS);

    GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;
    RETURN QTD_ROWS;
END;
$$;