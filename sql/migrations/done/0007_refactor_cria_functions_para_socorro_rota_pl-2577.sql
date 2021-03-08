CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_OPCOES_PROBLEMAS_ABERTURA_BY_EMPRESA(
    F_COD_EMPRESA BIGINT
)
    RETURNS TABLE
            (
                CODIGO_OPCAO_PROBLEMA           BIGINT,
                DESCRICAO_OPCAO_PROBLEMA        CITEXT,
                OBRIGA_DESCRICAO_OPCAO_PROBLEMA BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT SROP.CODIGO    AS     CODIGO_OPCAO_PROBLEMA,
       SROP.DESCRICAO AS     DESCRICAO_OPCAO_PROBLEMA,
       SROP.OBRIGA_DESCRICAO OBRIGA_DESCRICAO_OPCAO_PROBLEMA
FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
  AND SROP.STATUS_ATIVO = TRUE
ORDER BY SROP.DESCRICAO;
$$;

CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_GET_VEICULOS_DISPONIVEIS_BY_UNIDADE(
    F_COD_UNIDADE BIGINT
)
    RETURNS TABLE
            (
                CODIGO_VEICULO   BIGINT,
                PLACA_VEICULO    TEXT,
                KM_ATUAL_VEICULO BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT VE.CODIGO AS CODIGO_VEICULO,
       VE.PLACA  AS PLACA_VEICULO,
       VE.KM     AS KM_ATUAL_VEICULO
FROM VEICULO VE
WHERE VE.COD_UNIDADE = F_COD_UNIDADE
  AND VE.STATUS_ATIVO = TRUE
ORDER BY VE.PLACA;
$$;