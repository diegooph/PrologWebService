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