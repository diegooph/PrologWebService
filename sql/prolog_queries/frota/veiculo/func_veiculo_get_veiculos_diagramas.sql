CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_VEICULOS_DIAGRAMAS(
  F_COD_VEICULOS                 BIGINT [],
  F_FILTRO_VEICULO_POSSUI_DIAGRAMA BOOLEAN DEFAULT NULL)
  RETURNS TABLE(
    COD_VEICULO       BIGINT,
    PLACA_VEICULO     TEXT,
    COD_TIPO_VEICULO  BIGINT,
    NOME_TIPO_VEICULO TEXT,
    POSSUI_DIAGAMA    BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH DADOS_VEICULOS AS (
      SELECT
        V.CODIGO                                   AS COD_VEICULO,
        V.PLACA :: TEXT                            AS PLACA_VEICULO,
        VT.CODIGO                                  AS COD_TIPO_VEICULO,
        VT.NOME :: TEXT                            AS NOME_TIPO_VEICULO,
        F_IF(VT.COD_DIAGRAMA IS NULL, FALSE, TRUE) AS POSSUI_DIAGAMA
      FROM VEICULO V
        JOIN VEICULO_TIPO VT
          ON V.COD_TIPO = VT.CODIGO
      WHERE V.CODIGO = ANY (F_COD_VEICULOS)
  )

  SELECT
    DV.COD_VEICULO       AS COD_VEICULO,
    DV.PLACA_VEICULO     AS PLACA_VEICULO,
    DV.COD_TIPO_VEICULO  AS COD_TIPO_VEICULO,
    DV.NOME_TIPO_VEICULO AS NOME_TIPO_VEICULO,
    DV.POSSUI_DIAGAMA    AS POSSUI_DIAGRAMA
  FROM DADOS_VEICULOS DV
  WHERE F_IF(F_FILTRO_VEICULO_POSSUI_DIAGRAMA IS NULL, TRUE, F_FILTRO_VEICULO_POSSUI_DIAGRAMA = DV.POSSUI_DIAGAMA);
END;
$$;