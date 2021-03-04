-- Cria a coluna para armezenar o código do colaborador que realizou o cadastro.
alter table colaborador_data
	add cod_colaborador_cadastro BIGINT;
comment on column colaborador_data.cod_colaborador_cadastro is 'Código do coolaborador que realizou o cadastro, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table colaborador_data
	add constraint fk_colaborador_cadastro
		foreign key (cod_colaborador_cadastro) references colaborador_data (codigo);

-- Cria a coluna para armazenar a data e hora da última alteração.
alter table colaborador_data
	add data_hora_ultima_alteracao TIMESTAMP WITH TIME ZONE;
comment on column colaborador_data.data_hora_ultima_alteracao is 'Data e hora da última alteração, pode ser nulo.';

-- Cria a coluna para armazenar o código do colaborador que realizou a última alteração.
alter table colaborador_data
	add cod_colaborador_ultima_alteracao BIGINT;
comment on column colaborador_data.cod_colaborador_ultima_alteracao is 'Código do colaborador que realizou a última alteração, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table colaborador_data
	add constraint fk_colaborador_ultima_alteracao
		foreign key (cod_colaborador_ultima_alteracao) references colaborador_data (codigo);

-- Refaz a view de colaborador para adicionar os campos novos.
CREATE OR REPLACE VIEW COLABORADOR AS
SELECT C.CPF,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       C.NOME,
       C.COD_EQUIPE,
       C.COD_FUNCAO,
       C.COD_UNIDADE,
       C.COD_PERMISSAO,
       C.COD_EMPRESA,
       C.COD_SETOR,
       C.PIS,
       C.DATA_HORA_CADASTRO,
       C.CODIGO,
       C.COD_UNIDADE_CADASTRO,
       C.COD_COLABORADOR_CADASTRO,
       C.COD_COLABORADOR_ULTIMA_ALTERACAO,
       C.DATA_HORA_ULTIMA_ALTERACAO
FROM COLABORADOR_DATA C
WHERE C.DELETADO = FALSE;

-- Altera a function de inserção de colaborador.
-- Agora código do colaborador que realizou a requisição é recebido via parâmetro, não utilizando mais o token.
-- Parâmetros adicionados: COD_COLABORADOR_CADASTRO, DATA_HORA_ULTIMA_ALTERACAO.
-- Os novos parâmetros serão inseridos na tabela COLABORADOR_DATA.
DROP FUNCTION FUNC_COLABORADOR_INSERT_COLABORADOR(F_CPF BIGINT,
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
                                                  F_TOKEN TEXT);
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
                                                               F_COD_COLABORADOR_CADASTRO BIGINT,
                                                               F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
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
                             COD_COLABORADOR_CADASTRO,
                             DATA_HORA_ULTIMA_ALTERACAO,
                             COD_COLABORADOR_ULTIMA_ALTERACAO)
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
            F_COD_UNIDADE,
            F_COD_COLABORADOR_CADASTRO,
            F_DATA_HORA_ULTIMA_ALTERACAO,
            F_COD_COLABORADOR_CADASTRO)
    RETURNING CODIGO
        INTO F_COD_COLABORADOR_INSERIDO;

    -- Verificamos se o insert de colaborador funcionou.
    IF NOT FOUND
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
                F_COD_COLABORADOR_CADASTRO)
        RETURNING CODIGO
            INTO F_COD_TELEFONE_INSERIDO;

        -- Verificamos se o insert do telefone do colaborador funcionou.
        IF NOT FOUND
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
                                               F_COD_COLABORADOR_CADASTRO)
                                               RETURNING CODIGO
            INTO F_COD_EMAIL_INSERIDO;

         -- Verificamos se o insert do email funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível inserir o colaborador devido a problemas no e-mail, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR_INSERIDO;
END;
$$;

-- Altera a function de update de colaborador.
-- Agora código do colaborador que realizou a requisição é recebido via parâmetro, não utilizando mais o token.
-- Parâmetros adicionados: COD_COLABORADOR_ULTIMA_ALTERACAO, DATA_HORA_ULTIMA_ALTERACAO.
-- Os novos parâmetros serão inseridos na tabela COLABORADOR_DATA.
-- Foi adicionada uma lógica que verificação de alterações para poupar processamento em updates desnecessários.
DROP FUNCTION FUNC_COLABORADOR_UPDATE_COLABORADOR(F_COD_COLABORADOR BIGINT,
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
                                                  F_TOKEN TEXT);
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
                                                               F_COD_COLABORADOR_ULTIMA_ALTERACAO BIGINT,
                                                               F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_MUDOU_COLABORADOR BOOLEAN = (SELECT (CPF IS DISTINCT FROM F_CPF) OR
                                          (MATRICULA_AMBEV IS DISTINCT FROM F_MATRICULA_AMBEV) OR
                                          (MATRICULA_TRANS IS DISTINCT FROM F_MATRICULA_TRANS) OR
                                          (DATA_NASCIMENTO IS DISTINCT FROM F_DATA_NASCIMENTO) OR
                                          (DATA_ADMISSAO IS DISTINCT FROM F_DATA_ADMISSAO) OR
                                          (TRIM(NOME) IS DISTINCT FROM TRIM(F_NOME)) OR
                                          (COD_SETOR IS DISTINCT FROM F_COD_SETOR) OR
                                          (COD_FUNCAO IS DISTINCT FROM F_COD_FUNCAO) OR
                                          (COD_UNIDADE IS DISTINCT FROM F_COD_UNIDADE) OR
                                          (COD_PERMISSAO IS DISTINCT FROM F_COD_PERMISSAO) OR
                                          (COD_EMPRESA IS DISTINCT FROM F_COD_EMPRESA) OR
                                          (COD_EQUIPE IS DISTINCT FROM F_COD_EQUIPE) OR
                                          (TRIM(PIS) IS DISTINCT FROM TRIM(F_PIS))
                                   FROM COLABORADOR
                                   WHERE CODIGO = F_COD_COLABORADOR);
    V_MUDOU_TELEFONE    BOOLEAN = (SELECT (TRIM(SIGLA_ISO2) IS DISTINCT FROM TRIM(F_SIGLA_ISO2)) OR
                                          (PREFIXO_PAIS IS DISTINCT FROM F_PREFIXO_PAIS) OR
                                          (TRIM(NUMERO_TELEFONE) IS DISTINCT FROM TRIM(F_TELEFONE))
                                   FROM COLABORADOR_TELEFONE
                                   WHERE COD_COLABORADOR = F_COD_COLABORADOR);
    V_MUDOU_EMAIL       BOOLEAN = (SELECT TRIM(EMAIL) IS DISTINCT FROM TRIM(F_EMAIL)
                                   FROM COLABORADOR_EMAIL
                                   WHERE COD_COLABORADOR = F_COD_COLABORADOR);
BEGIN

    IF V_MUDOU_COLABORADOR THEN
        UPDATE COLABORADOR
        SET CPF                              = F_CPF,
            MATRICULA_AMBEV                  = F_MATRICULA_AMBEV,
            MATRICULA_TRANS                  = F_MATRICULA_TRANS,
            DATA_NASCIMENTO                  = F_DATA_NASCIMENTO,
            DATA_ADMISSAO                    = F_DATA_ADMISSAO,
            NOME                             = F_NOME,
            COD_SETOR                        = F_COD_SETOR,
            COD_FUNCAO                       = F_COD_FUNCAO,
            COD_UNIDADE                      = F_COD_UNIDADE,
            COD_PERMISSAO                    = F_COD_PERMISSAO,
            COD_EMPRESA                      = F_COD_EMPRESA,
            COD_EQUIPE                       = F_COD_EQUIPE,
            PIS                              = F_PIS,
            COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ULTIMA_ALTERACAO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_HORA_ULTIMA_ALTERACAO
        WHERE CODIGO = F_COD_COLABORADOR;

        -- Validamos se houve alguma atualização dos valores.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados do colaborador, tente novamente');
        END IF;
    END IF;

    -- Será permitido somente 1 email e telefone por colaborador no lançamento inicial.
    -- Deletamos email e telefone vinculados ao colaborador
    IF V_MUDOU_TELEFONE THEN
        DELETE FROM COLABORADOR_TELEFONE WHERE COD_COLABORADOR = F_COD_COLABORADOR;
        IF F_PREFIXO_PAIS IS NOT NULL AND F_TELEFONE IS NOT NULL THEN
            INSERT INTO COLABORADOR_TELEFONE (SIGLA_ISO2,
                                              PREFIXO_PAIS,
                                              COD_COLABORADOR,
                                              NUMERO_TELEFONE,
                                              COD_COLABORADOR_ULTIMA_ATUALIZACAO)
            VALUES (F_SIGLA_ISO2,
                    F_PREFIXO_PAIS,
                    F_COD_COLABORADOR,
                    F_TELEFONE,
                    F_COD_COLABORADOR_ULTIMA_ALTERACAO);

            -- Verificamos se o insert do telefone do colaborador funcionou.
            IF NOT FOUND
            THEN
                PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atualizar o colaborador devido a problemas no telefone, tente novamente');
            END IF;
        END IF;
    END IF;

    IF V_MUDOU_EMAIL THEN
        DELETE FROM COLABORADOR_EMAIL WHERE COD_COLABORADOR = F_COD_COLABORADOR;
        IF F_EMAIL IS NOT NULL
        THEN
            INSERT INTO COLABORADOR_EMAIL (COD_COLABORADOR,
                                           EMAIL,
                                           COD_COLABORADOR_ULTIMA_ATUALIZACAO)
            VALUES (F_COD_COLABORADOR,
                    F_EMAIL,
                    F_COD_COLABORADOR_ULTIMA_ALTERACAO);

            -- Verificamos se o insert do email funcionou.
            IF NOT FOUND
            THEN
                PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atualizar o colaborador devido a problemas no e-mail, tente novamente');
            END IF;
        END IF;
    END IF;

    -- Caso o telefone ou e-mail tenham mudado e o colaborador não, atualiza quem fez a última alteração na tabela de
    -- colaborador.
    IF NOT V_MUDOU_COLABORADOR AND (V_MUDOU_EMAIL OR V_MUDOU_TELEFONE) THEN
        UPDATE COLABORADOR
        SET COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ULTIMA_ALTERACAO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_HORA_ULTIMA_ALTERACAO
        WHERE CODIGO = F_COD_COLABORADOR;

        -- Verificamos se o update de última alteração funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                    'Não foi possível atualizar o colaborador, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR;
END;
$$;