CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_NOMENCLATURAS(F_COD_EMPRESA_BASE BIGINT,
                                                            F_COD_EMPRESA_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- COPIA NOMENCLATURAS, CASO EXISTAM.
    IF EXISTS(SELECT PPNE.NOMENCLATURA
              FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
              WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_EMPRESA,
                                                       COD_DIAGRAMA,
                                                       POSICAO_PROLOG,
                                                       NOMENCLATURA,
                                                       DATA_HORA_CADASTRO)
        SELECT F_COD_EMPRESA_USUARIO,
               PPNE.COD_DIAGRAMA,
               PPNE.POSICAO_PROLOG,
               PPNE.NOMENCLATURA,
               now()
        FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
        WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA_BASE;
    END IF;
END;
$$;