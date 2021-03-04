-- Sobre:
-- Busca as alternatias de seleção para abertura de um relato, filtrando por unidade, setor e status_ativo.
-- Caso a alternativa tenha COD_SETOR nulo na tabela, ela será retornada independente do F_COD_SETOR fornecido, mas
-- ainda respeitando F_COD_UNIDADE e F_STATUS_ATIVO.
--
-- Histórico:
-- 2020-02-27 -> Arquivo do function criado (luizfp).
-- 2020-02-27 -> Altera para retornar as alternativas se a coluna COD_SETOR for NULL (luizfp).
CREATE OR REPLACE FUNCTION FUNC_RELATO_GET_ALTERNATIVAS(F_COD_UNIDADE BIGINT, F_COD_SETOR BIGINT, F_STATUS_ATIVO BOOLEAN)
    RETURNS TABLE
            (
                CODIGO       BIGINT,
                ALTERNATIVA  TEXT,
                STATUS_ATIVO BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT RA.CODIGO       AS CODIGO,
       RA.ALTERNATIVA  AS ALTERNATIVA,
       RA.STATUS_ATIVO AS STATUS_ATIVO
FROM RELATO_ALTERNATIVA RA
WHERE RA.COD_UNIDADE = F_COD_UNIDADE
  AND (RA.COD_SETOR IS NULL OR F_IF(F_COD_SETOR IS NOT NULL, F_COD_SETOR = RA.COD_SETOR, TRUE))
  AND F_IF(F_STATUS_ATIVO IS NOT NULL, F_STATUS_ATIVO = RA.STATUS_ATIVO, TRUE)
ORDER BY RA.ALTERNATIVA ASC;
$$;