CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_UPDATE_COLABORADOR(F_COD_COLABORADOR BIGINT,
                                                               F_CPF BIGINT,
                                                               F_MATRICULA_AMBEV INTEGER,
                                                               F_MATRICULA_TRANS INTEGER,
                                                               F_DATA_NASCIMENTO DATE,
                                                               F_DATA_ADMISSAO DATE,
                                                               F_NOME VARCHAR,
                                                               F_COD_SETOR BIGINT,
                                                               F_COD_FUNCAO INTEGER,
                                                               F_COD_UNIDADE INTEGER,
                                                               F_COD_PERMISSAO BIGINT,
                                                               F_COD_EMPRESA BIGINT,
                                                               F_COD_EQUIPE BIGINT,
                                                               F_PIS VARCHAR,
                                                               F_SIGLA_ISO2 CHARACTER VARYING,
                                                               F_PREFIXO_PAIS INTEGER,
                                                               F_TELEFONE TEXT,
                                                               F_EMAIL EMAIL,
                                                               F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_COLABORADOR_UPDATE CONSTANT BIGINT := (SELECT COD_COLABORADOR
                                                 FROM TOKEN_AUTENTICACAO
                                                 WHERE TOKEN = F_TOKEN);
BEGIN
    UPDATE COLABORADOR
    SET CPF                                = F_CPF,
        MATRICULA_AMBEV                    = F_MATRICULA_AMBEV,
        MATRICULA_TRANS                    = F_MATRICULA_TRANS,
        DATA_NASCIMENTO                    = F_DATA_NASCIMENTO,
        DATA_ADMISSAO                      = F_DATA_ADMISSAO,
        NOME                               = F_NOME,
        COD_SETOR                          = F_COD_SETOR,
        COD_FUNCAO                         = F_COD_FUNCAO,
        COD_UNIDADE                        = F_COD_UNIDADE,
        COD_PERMISSAO                      = F_COD_PERMISSAO,
        COD_EMPRESA                        = F_COD_EMPRESA,
        COD_EQUIPE                         = F_COD_EQUIPE,
        PIS                                = F_PIS,
        COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR_UPDATE
    WHERE CODIGO = F_COD_COLABORADOR;

    -- Validamos se houve alguma atualização dos valores.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados do colaborador, tente novamente');
    END IF;

    -- Será permitido somente 1 email e telefone por colaborador no lançamento inicial.
    -- Deletamos email e telefone vinculados ao colaborador
    DELETE FROM COLABORADOR_EMAIL WHERE COD_COLABORADOR = F_COD_COLABORADOR;
    DELETE FROM COLABORADOR_TELEFONE WHERE COD_COLABORADOR = F_COD_COLABORADOR;

    IF F_PREFIXO_PAIS IS NOT NULL AND F_TELEFONE IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_TELEFONE (SIGLA_ISO2,
                                          PREFIXO_PAIS,
                                          COD_COLABORADOR,
                                          NUMERO_TELEFONE,
                                          COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_SIGLA_ISO2,
                F_PREFIXO_PAIS,
                F_COD_COLABORADOR,
                F_TELEFONE,
                F_COD_COLABORADOR_UPDATE);

        -- Verificamos se o insert do telefone do colaborador funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível atualizar o colaborador devido a problemas no telefone, tente novamente');
        END IF;
    END IF;

    IF F_EMAIL IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_EMAIL (COD_COLABORADOR,
                                       EMAIL,
                                       COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                                       VALUES (F_COD_COLABORADOR,
                                               F_EMAIL,
                                               F_COD_COLABORADOR_UPDATE);

         -- Verificamos se o insert do email funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível atualizar o colaborador devido a problemas no e-mail, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR;
END;
$$;
