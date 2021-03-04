-- Sobre:
-- Essa function realiza o cadastro de uma unidade no sistema. Além disso, já é vinculado os pilares que a unidade
-- terá acesso.
-- Toda unidade criada terá acesso ao pilar Gente (3). Mesmo que não seja informado explicitamente para liberar esse
-- pilar.
-- Dessa forma, garantimos que toda a unidade cadastrada terá acesso ao pilar de gestão de cargos e pessoas.
--
-- Todos os parâmetros fornecidos para a function são validados.
--
-- Histórico:
-- 2019-08-15 -> Function criada (luizfp - PL-2200).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_UNIDADE_CADASTRA_UNIDADE(F_COD_EMPRESA BIGINT,
                                                                 F_COD_REGIONAL BIGINT,
                                                                 F_NOME_UNIDADE TEXT,
                                                                 F_TIMEZONE TEXT,
                                                                 F_PILARES_LIBERADOS INTEGER[],
                                                                 OUT AVISO_UNIDADE_CADASTRADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_CADASTRADAS BIGINT;
    COD_UNIDADE_CADASTRADA BIGINT;
    MAX_LENGTH_COLUMN      INTEGER := 40;
    COD_PILAR_GENTE        INTEGER := 3;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);
    PERFORM FUNC_GARANTE_REGIONAL_EXISTE(F_COD_REGIONAL);
    PERFORM FUNC_GARANTE_NOT_NULL(F_NOME_UNIDADE, 'Nome Unidade');
    PERFORM FUNC_GARANTE_NOT_NULL(F_TIMEZONE, 'Timezone');
    PERFORM FUNC_GARANTE_NOT_NULL(F_PILARES_LIBERADOS, 'Pilares Liberados');
    PERFORM FUNC_GARANTE_PILARES_VALIDOS(F_PILARES_LIBERADOS);

    -- Todas as unidades devem ter o pilar GENTE.
    SELECT ARRAY_APPEND(F_PILARES_LIBERADOS, COD_PILAR_GENTE) INTO F_PILARES_LIBERADOS;
    -- Após adicionar o pilar GENTE, removemos do array qualquer valor duplicado.
    SELECT ARRAY_DISTINCT(F_PILARES_LIBERADOS) INTO F_PILARES_LIBERADOS;

    -- Garante que nome unidade não tenha mais do que 40 caracteres.
    IF (LENGTH(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE)) > MAX_LENGTH_COLUMN)
    THEN
        RAISE EXCEPTION 'O nome da unidade não pode ter mais do que % caracteres', MAX_LENGTH_COLUMN;
    END IF;

    -- Garante que unidade com mesmo nome não exista para a mesma empresa.
    IF (LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE))) IN
        (SELECT LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(U.NOME)))
         FROM UNIDADE U
         WHERE U.COD_EMPRESA = F_COD_EMPRESA))
    THEN
        RAISE EXCEPTION 'Já existe uma unidade com nome % cadastrada para a empresa %',
            TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE),
            F_COD_EMPRESA;
    END IF;

    -- Garante que o timezone informado exista.
    IF (NOT IS_TIMEZONE(F_TIMEZONE))
    THEN
        RAISE EXCEPTION '% não é um timezone válido', F_TIMEZONE;
    END IF;

    -- Insere a unidade.
    INSERT INTO UNIDADE (NOME, TIMEZONE, COD_REGIONAL, COD_EMPRESA)
    VALUES (F_NOME_UNIDADE, F_TIMEZONE, F_COD_REGIONAL, F_COD_EMPRESA) RETURNING CODIGO INTO COD_UNIDADE_CADASTRADA;

    -- Verifica se insert de unidade funcionou.
    IF (COD_UNIDADE_CADASTRADA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao cadastrar unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    -- Insere os pilares.
    INSERT INTO UNIDADE_PILAR_PROLOG (COD_UNIDADE, COD_PILAR)
    SELECT COD_UNIDADE_CADASTRADA,
           UNNEST(F_PILARES_LIBERADOS);

    GET DIAGNOSTICS QTD_LINHAS_CADASTRADAS = ROW_COUNT;

    -- Verifica se insert de pilares funcionou.
    IF (QTD_LINHAS_CADASTRADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao liberar pilares para a unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    SELECT 'UNIDADE CADASTRADA: '
               || F_NOME_UNIDADE
               || ', CÓDIGO UNIDADE: '
               || COD_UNIDADE_CADASTRADA
               || ', E PILARES: '
               || ARRAY_TO_STRING(F_PILARES_LIBERADOS, ', ')
    INTO AVISO_UNIDADE_CADASTRADA;
END ;
$$;