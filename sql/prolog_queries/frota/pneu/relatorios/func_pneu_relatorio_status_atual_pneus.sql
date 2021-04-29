CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_STATUS_ATUAL_PNEUS(
    F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"    TEXT,
                "PNEU"               TEXT,
                "STATUS ATUAL"       TEXT,
                "PLACA APLICADO"     TEXT,
                "POSIÇÃO APLICADO"   TEXT,
                "RECAPADORA ALOCADO" TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_ANALISE TEXT := 'ANALISE';
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                           AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                 AS COD_PNEU,
               P.STATUS :: TEXT                         AS STATUS_ATUAL,
               COALESCE(VP.PLACA :: TEXT, '-')          AS PLACA_APLICADO,
               COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO,
               COALESCE(
                       CASE
                           WHEN P.STATUS = F_STATUS_ANALISE
                               THEN (SELECT R.NOME AS NOME_RECAPADORA
                                     FROM MOVIMENTACAO M
                                              JOIN MOVIMENTACAO_DESTINO MD
                                                   ON M.CODIGO = MD.COD_MOVIMENTACAO
                                              JOIN RECAPADORA R ON MD.COD_RECAPADORA_DESTINO = R.CODIGO
                                     WHERE M.COD_PNEU = P.CODIGO
                                     ORDER BY M.CODIGO DESC
                                     LIMIT 1)
                           END,
                       '-')                             AS RECAPADORA_ALOCADO
        FROM PNEU P
                 JOIN UNIDADE U
                      ON P.COD_UNIDADE = U.CODIGO
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY U.CODIGO ASC, P.CODIGO_CLIENTE ASC;
END;
$$;