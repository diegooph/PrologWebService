CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_VEICULOS_SELECAO(F_COD_UNIDADE_ORIGEM BIGINT)
  RETURNS TABLE(
    COD_VEICULO                 BIGINT,
    PLACA_VEICULO               TEXT,
    KM_ATUAL_VEICULO            BIGINT,
    QTD_PNEUS_APLICADOS_VEICULO BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    V.CODIGO                                 AS COD_VEICULO,
    V.PLACA :: TEXT                          AS PLACA_VEICULO,
    V.KM                                     AS KM_ATUAL_VEICULO,
    COUNT(*)
      -- Com esse filter veículos sem pneu retornam 0 na quantidade e não 1.
      FILTER (WHERE VP.COD_PNEU IS NOT NULL) AS QTD_PNEUS_APLICADOS_VEICULO
  FROM VEICULO V
    LEFT JOIN VEICULO_PNEU VP
      ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
  WHERE V.COD_UNIDADE = F_COD_UNIDADE_ORIGEM
  GROUP BY V.CODIGO, V.PLACA, V.KM;
END;
$$;