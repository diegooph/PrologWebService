CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_UPDATE_STATUS_MODELO(F_COD_UNIDADE BIGINT,
                                                               F_COD_MODELO BIGINT,
                                                               F_STATUS_ATIVO BOOLEAN)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_NOME_MODELO_CHECKLIST TEXT := (SELECT NOME
                                     FROM CHECKLIST_MODELO_DATA
                                     WHERE COD_UNIDADE = F_COD_UNIDADE
                                       AND CODIGO = F_COD_MODELO);
BEGIN
    -- VERIFICA SE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Se estamos ativando o modelo e existe outro na mesma unidade, de mesmo nome e já ativo, lançamos um erro.
    IF F_STATUS_ATIVO AND (SELECT EXISTS(SELECT CM.CODIGO
                                         FROM CHECKLIST_MODELO CM
                                         WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                           AND LOWER(CM.NOME) = LOWER(F_NOME_MODELO_CHECKLIST)
                                           AND CM.STATUS_ATIVO = TRUE))
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro! Já existe um modelo de checklist ativo com esse nome.');
    END IF;

    UPDATE CHECKLIST_MODELO_DATA
    SET STATUS_ATIVO = F_STATUS_ATIVO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_COD_MODELO;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o status do modelo de checklist % para %', F_COD_MODELO, F_STATUS_ATIVO;
    END IF;
END
$$;