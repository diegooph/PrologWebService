CREATE OR REPLACE FUNCTION FUNC_CARGOS_DELETA_CARGO(F_COD_EMPRESA BIGINT,
                                                    F_COD_CARGO BIGINT,
                                                    F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_COLABORADOR_UPDATE BIGINT := (SELECT COD_COLABORADOR
                                        FROM TOKEN_AUTENTICACAO
                                        WHERE TOKEN = F_TOKEN);
    QTD_LINHAS_ATUALIZADAS   BIGINT;
BEGIN
    IF F_COD_COLABORADOR_UPDATE IS NULL OR F_COD_COLABORADOR_UPDATE <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível validar sua sessão, por favor, faça login novamente');
    END IF;

    IF ((SELECT COUNT(CODIGO)
         FROM FUNCAO
         WHERE COD_EMPRESA = F_COD_EMPRESA
           AND CODIGO = F_COD_CARGO) <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Erro ao deletar, possivelmente este cargo já foi deletado');
    END IF;

    IF ((SELECT COUNT(CODIGO)
         FROM COLABORADOR
         WHERE COD_EMPRESA = F_COD_EMPRESA
           AND COD_FUNCAO = F_COD_CARGO) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível deletar pois existem colaboradores vinculados a este cargo');
    END IF;

    -- Deleta cargo.
    UPDATE FUNCAO_DATA
    SET DELETADO               = TRUE,
        DATA_HORA_DELETADO     = NOW(),
        DATA_HORA_UPDATE       = NOW(),
        PG_USERNAME_DELECAO    = SESSION_USER,
        COD_COLABORADOR_UPDATE = F_COD_COLABORADOR_UPDATE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND CODIGO = F_COD_CARGO;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao deletar o cargo, tente novamente');
    END IF;

    RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;