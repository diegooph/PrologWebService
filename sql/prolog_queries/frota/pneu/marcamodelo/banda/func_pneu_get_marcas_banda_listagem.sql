-- Sobre:
--
-- Esta function retorna todas as marcas de banda de uma empresa.
--
--
-- Histórico:
-- 2019-10-15 -> Function criada. (thaisksf PL-2263)
-- 2019-11-17 -> Altera nome das colunas de retorno (luizfp PL-2390).
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_BANDA  BIGINT,
                NOME_MARCA_BANDA TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MB.CODIGO       AS COD_MARCA_PNEU,
       MB.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_BANDA MB
WHERE MB.COD_EMPRESA = F_COD_EMPRESA
ORDER BY MB.NOME;
$$;