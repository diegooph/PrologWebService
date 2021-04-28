CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_INSERT_COLABORADOR(F_CPF BIGINT,
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
                                                               F_COD_UNIDADE_CADASTRO INTEGER,
                                                               F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_COLABORADOR_UPDATE CONSTANT BIGINT := (SELECT COD_COLABORADOR
                                                 FROM TOKEN_AUTENTICACAO
                                                 WHERE TOKEN = F_TOKEN);
    F_COD_COLABORADOR_INSERIDO        BIGINT;
    F_COD_TELEFONE_INSERIDO           BIGINT;
    F_COD_EMAIL_INSERIDO              BIGINT;
BEGIN
    INSERT INTO COLABORADOR (CPF,
                             MATRICULA_AMBEV,
                             MATRICULA_TRANS,
                             DATA_NASCIMENTO,
                             DATA_ADMISSAO,
                             NOME,
                             COD_SETOR,
                             COD_FUNCAO,
                             COD_UNIDADE,
                             COD_PERMISSAO,
                             COD_EMPRESA,
                             COD_EQUIPE,
                             PIS,
                             COD_UNIDADE_CADASTRO,
                             COD_COLABORADOR_CADASTRO)
    VALUES (F_CPF,
            F_MATRICULA_AMBEV,
            F_MATRICULA_TRANS,
            F_DATA_NASCIMENTO,
            F_DATA_ADMISSAO,
            F_NOME,
            F_COD_SETOR,
            F_COD_FUNCAO,
            F_COD_UNIDADE,
            F_COD_PERMISSAO,
            F_COD_EMPRESA,
            F_COD_EQUIPE,
            F_PIS,
            F_COD_UNIDADE_CADASTRO,
            F_COD_COLABORADOR_UPDATE)
    RETURNING CODIGO
        INTO F_COD_COLABORADOR_INSERIDO;

    -- Verificamos se o insert de colaborador funcionou.
    IF F_COD_COLABORADOR_INSERIDO IS NULL OR F_COD_COLABORADOR_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o colaborador, tente novamente');
    END IF;

    IF F_PREFIXO_PAIS IS NOT NULL AND F_TELEFONE IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_TELEFONE (SIGLA_ISO2,
                                          PREFIXO_PAIS,
                                          COD_COLABORADOR,
                                          NUMERO_TELEFONE,
                                          COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_SIGLA_ISO2,
                F_PREFIXO_PAIS,
                F_COD_COLABORADOR_INSERIDO,
                F_TELEFONE,
                F_COD_COLABORADOR_UPDATE)
        RETURNING CODIGO
            INTO F_COD_TELEFONE_INSERIDO;

        -- Verificamos se o insert do telefone do colaborador funcionou.
        IF F_COD_TELEFONE_INSERIDO IS NULL OR F_COD_TELEFONE_INSERIDO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível inserir o colaborador devido a problemas no telefone, tente novamente');
        END IF;
    END IF;

    IF F_EMAIL IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_EMAIL (COD_COLABORADOR,
                                       EMAIL,
                                       COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                                       VALUES (F_COD_COLABORADOR_INSERIDO,
                                               F_EMAIL,
                                               F_COD_COLABORADOR_UPDATE)
                                               RETURNING CODIGO
            INTO F_COD_EMAIL_INSERIDO;

         -- Verificamos se o insert do email funcionou.
        IF F_COD_EMAIL_INSERIDO IS NULL OR F_COD_EMAIL_INSERIDO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível inserir o colaborador devido a problemas no e-mail, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR_INSERIDO;
END;
$$;
