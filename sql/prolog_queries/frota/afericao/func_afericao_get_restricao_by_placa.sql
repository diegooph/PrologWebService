CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_RESTRICAO_BY_PLACA(F_PLACA_VEICULO TEXT)
  RETURNS TABLE(
    SULCO_MINIMO_DESCARTE REAL,
    SULCO_MINIMO_RECAPAGEM REAL,
    TOLERANCIA_CALIBRAGEM REAL,
    TOLERANCIA_INSPECAO REAL,
    PERIODO_AFERICAO_SULCO INTEGER,
    PERIODO_AFERICAO_PRESSAO INTEGER)
LANGUAGE SQL
AS $$
SELECT
  ER.SULCO_MINIMO_DESCARTE,
  ER.SULCO_MINIMO_RECAPAGEM,
  ER.TOLERANCIA_CALIBRAGEM,
  ER.TOLERANCIA_INSPECAO,
  ER.PERIODO_AFERICAO_SULCO,
  ER.PERIODO_AFERICAO_PRESSAO
FROM VEICULO V
  JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
  JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA
  JOIN PNEU_RESTRICAO_UNIDADE ER ON ER.COD_EMPRESA = E.CODIGO AND ER.COD_UNIDADE = U.CODIGO
WHERE V.PLACA = F_PLACA_VEICULO;
$$;