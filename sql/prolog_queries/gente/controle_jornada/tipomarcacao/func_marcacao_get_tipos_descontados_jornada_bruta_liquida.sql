CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPOS_DESCONTADOS_JORNADA_BRUTA_LIQUIDA(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_TIPO_DESCONTADO      BIGINT,
    NOME_TIPO_DESCONTADO     TEXT,
    DESCONTA_JORNADA_BRUTA   BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  M.COD_TIPO_DESCONTADO      AS COD_TIPO_DESCONTADO,
  VIT.NOME                   AS NOME_TIPO_DESCONTADO,
  M.DESCONTA_JORNADA_BRUTA   AS DESCONTA_JORNADA_BRUTA,
  M.DESCONTA_JORNADA_LIQUIDA AS DESCONTA_JORNADA_LIQUIDA
FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
  JOIN VIEW_INTERVALO_TIPO VIT
    ON M.COD_TIPO_DESCONTADO = VIT.CODIGO
WHERE M.COD_UNIDADE = F_COD_UNIDADE;
$$;