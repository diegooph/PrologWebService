-- Sobre:
-- Essa function retorna os veículos disponíveis por codigo de unidade.
--
-- Histórico:
-- 2020-03-11 -> Function criada (gustavocnp95 - PL-2577).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_GET_VEICULOS_DISPONIVEIS_BY_UNIDADE(
    F_COD_UNIDADE BIGINT
)
    RETURNS TABLE
            (
                CODIGO_VEICULO BIGINT,
                PLACA_VEICULO  TEXT,
                KM_ATUAL_VEICULO     BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT VE.CODIGO AS CODIGO_VEICULO,
       VE.PLACA AS PLACA_VEICULO,
       VE.KM AS KM_ATUAL_VEICULO
FROM VEICULO VE
WHERE VE.COD_UNIDADE = F_COD_UNIDADE
  AND VE.STATUS_ATIVO = TRUE
ORDER BY VE.PLACA;
$$;