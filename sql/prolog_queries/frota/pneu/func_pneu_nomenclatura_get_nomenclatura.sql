-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- retorna a nomenclatura das posições de um determinado diagrama.
--
-- Precondições:
-- 1) Informações necessárias para realizar a busca.
-- 2) Informações cadastradas no banco.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
-- 2020-05-11 -> Adiciona cod_auxiliar (diogenesvanzella - PLI-142).
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                                   F_COD_DIAGRAMA BIGINT)
    RETURNS TABLE
            (
                NOMENCLATURA   TEXT,
                COD_AUXILIAR   TEXT,
                POSICAO_PROLOG INTEGER
            )
    LANGUAGE SQL
AS
$$
SELECT PPNE.NOMENCLATURA::TEXT AS NOMENCLATURA,
       PPNE.COD_AUXILIAR       AS COD_AUXILIAR,
       PPNE.POSICAO_PROLOG     AS POSICAO_PROLOG
FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA
  AND PPNE.COD_DIAGRAMA = F_COD_DIAGRAMA
$$;