--######################################################################################################################
--######################################################################################################################
-- FUNCTION PARA RELATÓRIO QUE BUSCA O MENOR SULCO E PRESSÃO DE CADA PNEU DAS UNIDADES FILTRADAS. ELE É UTILIZADO
-- PARA POPULAR O COMPONENT DE SCATTER DA DASHBOARD.
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_MENOR_SULCO_E_PRESSAO_PNEUS(F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    COD_PNEU         BIGINT,
    COD_PNEU_CLIENTE TEXT,
    PRESSAO_ATUAL    NUMERIC,
    MENOR_SULCO      NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO                                                                                   AS COD_PNEU,
  P.CODIGO_CLIENTE                                                                           AS COD_PNEU_CLIENTE,
  TRUNC(P.PRESSAO_ATUAL :: NUMERIC, 2)                                                       AS PRESSAO_ATUAL,
  TRUNC(LEAST(P.ALTURA_SULCO_INTERNO, P.ALTURA_SULCO_EXTERNO,
              P.ALTURA_SULCO_CENTRAL_EXTERNO, P.ALTURA_SULCO_CENTRAL_INTERNO) :: NUMERIC, 2) AS MENOR_SULCO
FROM PNEU P
WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY MENOR_SULCO ASC
$$;