-- Sobre:
--
-- Esta function cadastra um modelo de uma banda.
--
-- Précondicões
-- Func REMOVE_EXTRA_SPACES criada.

-- Histórico:
-- 2019-09-25 -> Function criada. (thaisksf PL-2263)
CREATE OR REPLACE FUNCTION FUNC_PNEU_CADASTRA_MODELO_BANDA(F_COD_EMPRESA BIGINT,
                                                           F_COD_MARCA_BANDA BIGINT,
                                                           F_NOME_MODELO_BANDA VARCHAR(255),
                                                           F_QTD_SULCOS INTEGER,
                                                           F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_MODELO_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT MB.CODIGO
              FROM MODELO_BANDA MB
              WHERE UNACCENT(TRIM(MB.NOME)) ILIKE UNACCENT(TRIM(F_NOME_MODELO_BANDA))
                AND MB.COD_MARCA = F_COD_MARCA_BANDA
                AND MB.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe um modelo de nome \'%s\' cadastrado na mesma marca', F_NOME_MODELO_BANDA));
    END IF;

    INSERT INTO MODELO_BANDA (COD_EMPRESA,
                              COD_MARCA,
                              NOME,
                              QT_SULCOS,
                              ALTURA_SULCOS)
    SELECT F_COD_EMPRESA,
           F_COD_MARCA_BANDA,
           REMOVE_EXTRA_SPACES(F_NOME_MODELO_BANDA),
           F_QTD_SULCOS,
           F_ALTURA_SULCOS
           RETURNING CODIGO INTO COD_MODELO_INSERIDO;

    RETURN COD_MODELO_INSERIDO;
END;
$$;