--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR AS PLACAS VINCULADAS A        ########################################
--#######################           MODELOS DE CHECKLIST ATIVOS                 ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_PLACAS_DISPONIVEIS(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_VEICULO      BIGINT,
    PLACA_VEICULO    TEXT,
    COD_TIPO_VEICULO BIGINT,
    KM_ATUAL_VEICULO BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    V.CODIGO        AS COD_VEICULO,
    V.PLACA :: TEXT AS PLACA_VEICULO,
    V.COD_TIPO      AS COD_TIPO_VEICULO,
    v.KM            AS KM_ATUAL_VEICULO
  FROM VEICULO V
  WHERE V.COD_UNIDADE = F_COD_UNIDADE
        AND V.STATUS_ATIVO
        AND V.COD_TIPO IN (SELECT CMVT.COD_TIPO_VEICULO
                           FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
                             JOIN CHECKLIST_MODELO CM
                               ON CMVT.COD_MODELO = CM.CODIGO
                           WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                 AND CM.STATUS_ATIVO = TRUE);
END;
$$;