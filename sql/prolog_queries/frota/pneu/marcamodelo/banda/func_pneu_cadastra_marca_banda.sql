-- Sobre:
--
-- Esta function cadastra uma marca de banda.
--
--
-- Histórico:
-- 2019-10-15 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_CADASTRA_MARCA_BANDA(F_COD_EMPRESA BIGINT,
                                                          F_MARCA_BANDA VARCHAR)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_MARCA_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT MB.NOME
              FROM MARCA_BANDA MB
              WHERE UNACCENT(TRIM(MB.NOME)) ILIKE UNACCENT(TRIM(F_MARCA_BANDA))
                AND MB.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma marca de nome \'%s\' cadastrada na empresa', F_MARCA_BANDA));
    END IF;

    INSERT INTO MARCA_BANDA (COD_EMPRESA, NOME)
    SELECT F_COD_EMPRESA,
           REMOVE_EXTRA_SPACES(F_MARCA_BANDA)
           RETURNING CODIGO INTO COD_MARCA_INSERIDO;

    RETURN COD_MARCA_INSERIDO;
END;
$$;