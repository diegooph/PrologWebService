-- Sobre:
--
-- Esta function retorna todas as marcas de pneu do ProLog.
--
--
-- Histórico:
-- 2019-11-02 -> Function criada (luizfp PL-2263).
-- 2019-11-17 -> Permite filtrar para incluir marcas não utilizadas pela empresa (luizfp PL-2390).
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                              F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_PNEU  BIGINT,
                NOME_MARCA_PNEU TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MP.CODIGO       AS COD_MARCA_PNEU,
       MP.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_PNEU MP
WHERE CASE
          WHEN F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE
              THEN TRUE
          ELSE (SELECT EXISTS(SELECT MOP.COD_MARCA
                              FROM MODELO_PNEU MOP
                              WHERE MOP.COD_EMPRESA = F_COD_EMPRESA
                                AND MOP.COD_MARCA = MP.CODIGO))
          END
ORDER BY MP.NOME;
$$;