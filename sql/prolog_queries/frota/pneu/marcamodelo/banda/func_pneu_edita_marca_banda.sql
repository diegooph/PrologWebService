CREATE OR REPLACE FUNCTION FUNC_PNEU_EDITA_MARCA_BANDA(F_COD_MARCA_BANDA BIGINT,
                                                       F_NOME_MARCA_BANDA VARCHAR)
    RETURNS BIGINT
    LANGUAGE SQL
AS
$$
UPDATE MARCA_BANDA
SET NOME = REMOVE_EXTRA_SPACES(F_NOME_MARCA_BANDA)
WHERE CODIGO = F_COD_MARCA_BANDA RETURNING CODIGO;
$$;