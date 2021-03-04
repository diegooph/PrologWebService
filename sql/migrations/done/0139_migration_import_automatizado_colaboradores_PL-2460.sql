-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a String possui apenas characteres numéricos.
-- Retorno: true - apenas números.
--          false - possui letras.
-- Histórico:
-- 2020-08-03 -> Function criada (thaisksf - PL-2460).
CREATE OR REPLACE FUNCTION CHECK_NON_NUMERIC_CHARACTERS(F_TEXTO TEXT)
    RETURNS BOOLEAN
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Verifica se a String possui apenas characteres numéricos.
    RETURN F_TEXTO ~ '^[0-9\.]+$';
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Cria uma tabela de "pré-import" e aplica uma trigger para verificar os dados inseridos e importar o que estiver de
-- acordo com as verificações.
--
-- Pré-requisitos:
-- Func remove_all_apaces criada.
--
-- Histórico:
-- 2020-07-30 -> Function criada (thaisksf - PL-2460).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_COLABORADOR_IMPORT_CRIA_TABELA_IMPORT(F_COD_EMPRESA BIGINT,
                                                                                  F_COD_UNIDADE BIGINT,
                                                                                  F_USUARIO TEXT,
                                                                                  F_DATA DATE,
                                                                                  OUT F_NOME_TABELA_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_DIA                TEXT := (SELECT EXTRACT(DAY FROM F_DATA));
    F_MES                TEXT := (SELECT EXTRACT(MONTH FROM F_DATA));
    F_ANO                TEXT := (SELECT EXTRACT(YEAR FROM F_DATA)) ;
    F_NOME_TABELA_IMPORT TEXT := UNACCENT(LOWER(REMOVE_ALL_SPACES(
                                        'C_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' ||
                                        F_ANO || '_' || F_MES || '_' || F_DIA || '_' ||
                                        F_USUARIO)));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                                   BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT                   BIGINT,
            COD_EMPRESA                              BIGINT,
            COD_UNIDADE                              INTEGER,
            CPF_EDITAVEL                             VARCHAR(255),
            PIS_EDITAVEL                             VARCHAR(255),
            NOME_EDITAVEL                            VARCHAR(255),
            DATA_NASCIMENTO_EDITAVEL                 VARCHAR(255),
            DATA_ADMISSAO_EDITAVEL                   VARCHAR(255),
            MATRICULA_PROMAX_EDITAVEL                VARCHAR(255),
            MATRICULA_PONTO_EDITAVEL                 VARCHAR(255),
            EQUIPE_EDITAVEL                          VARCHAR(255),
            SETOR_EDITAVEL                           VARCHAR(255),
            FUNCAO_EDITAVEL                          VARCHAR(255),
            EMAIL_EDITAVEL                           VARCHAR(255),
            TELEFONE_EDITAVEL                        VARCHAR(255),
            PAIS_EDITAVEL                            VARCHAR(255),
            CPF_FORMATADO_IMPORT                     BIGINT,
            PIS_FORMATADO_IMPORT                     VARCHAR(11),
            NOME_FORMATADO_IMPORT                    VARCHAR(255),
            DATA_NASCIMENTO_FORMATADA_IMPORT         DATE,
            DATA_ADMISSAO_FORMATADA_IMPORT           DATE,
            MATRICULA_PROMAX_FORMATADA_IMPORT        INTEGER,
            MATRICULA_PONTO_FORMATADA_IMPORT         INTEGER,
            EQUIPE_FORMATADA_IMPORT                  VARCHAR(255),
            SETOR_FORMATADO_IMPORT                   VARCHAR(255),
            FUNCAO_FORMATADA_IMPORT                  VARCHAR(255),
            EMAIL_FORMATADO_IMPORT                   EMAIL,
            TELEFONE_FORMATADO_IMPORT                BIGINT,
            PAIS_FORMATADO_IMPORT                    VARCHAR(255),
            STATUS_IMPORT_REALIZADO                  BOOLEAN,
            ERROS_ENCONTRADOS                        TEXT,
            USUARIO_UPDATE                           VARCHAR(255),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
        );', F_NOME_TABELA_IMPORT);

    -- Trigger para verificar planilha e realizar o import de colaboradores.
    EXECUTE FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_COLABORADOR ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_IMPORT_COLABORADOR
                    BEFORE INSERT OR UPDATE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_COLABORADOR_CONFERE_PLANILHA_IMPORTA_COLABORADOR();',
                   F_NOME_TABELA_IMPORT,
                   F_NOME_TABELA_IMPORT);

    -- Cria audit para a tabela.
    EXECUTE FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_COLABORADOR ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_COLABORADOR
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                   F_NOME_TABELA_IMPORT,
                   F_NOME_TABELA_IMPORT);

    -- Garante select, update para o usuário que está realizando o import.
    EXECUTE FORMAT(
            'grant select, update on implantacao.%I to %I;',
            F_NOME_TABELA_IMPORT,
            (SELECT INTERNO.FUNC_BUSCA_NOME_USUARIO_BANCO(F_USUARIO)));

    -- Retorna nome da tabela.
    SELECT F_NOME_TABELA_IMPORT INTO F_NOME_TABELA_CRIADA;
END ;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Insere os dados na tabela dinâmica criada através da function: func_import_cria_tabela_import.
--
-- Pré-requisitos:
-- function func_import_cria_tabela_import criada.
--
-- Histórico:
-- 2019-12-13 -> Function criada (thaisksf - PL-2460).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_COLABORADOR_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                                   F_NOME_TABELA_IMPORT TEXT,
                                                                                   F_COD_EMPRESA BIGINT,
                                                                                   F_COD_UNIDADE BIGINT,
                                                                                   F_JSON_COLABORADORES JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                CPF_EDITAVEL,
                                                PIS_EDITAVEL,
                                                NOME_EDITAVEL,
                                                DATA_NASCIMENTO_EDITAVEL,
                                                DATA_ADMISSAO_EDITAVEL,
                                                MATRICULA_PROMAX_EDITAVEL,
                                                MATRICULA_PONTO_EDITAVEL,
                                                EQUIPE_EDITAVEL,
                                                SETOR_EDITAVEL,
                                                FUNCAO_EDITAVEL,
                                                EMAIL_EDITAVEL,
                                                TELEFONE_EDITAVEL,
                                                PAIS_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''cpf'') :: TEXT,
                          (SRC ->> ''pis'') :: TEXT,
                          (SRC ->> ''nome'') :: TEXT,
                          (SRC ->> ''dataNascimento'') :: TEXT,
                          (SRC ->> ''dataAdmissao'') :: TEXT,
                          (SRC ->> ''matriculaPromax'') :: TEXT,
                          (SRC ->> ''matriculaPonto'') :: TEXT,
                          (SRC ->> ''equipe'') :: TEXT,
                          (SRC ->> ''setor'') :: TEXT,
                          (SRC ->> ''funcao'') :: TEXT,
                          (SRC ->> ''email'') :: TEXT,
                          (SRC ->> ''telefone'') :: TEXT,
                          (SRC ->> ''pais'') :: TEXT
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_EMPRESA,
                   F_COD_UNIDADE,
                   F_JSON_COLABORADORES);
END
$$;


-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica os dados que são inseridos na tabela de 'pré-import', procurando os códigos correspondentes para efetuar a
-- importação dos colaboradores.
--
-- Pré-requisitos:
-- functions criadas:
-- REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS.
-- FUNC_GERA_SIMILARIDADE.
-- REMOVE_ALL_SPACES.
-- CHECK_NON_NUMERIC_CHARACTERS.
--
-- Histórico:
-- 2020-07-31 -> Function criada (thaisksf - PL-2460).
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_COLABORADOR_CONFERE_PLANILHA_IMPORTA_COLABORADOR()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_VALOR_SIMILARIDADE CONSTANT REAL     := 0.45;
    V_QTD_ERROS                   SMALLINT := 0;
    V_MSGS_ERROS                  TEXT;
    V_QUEBRA_LINHA                TEXT     := CHR(10);
    V_COD_EQUIPE_BANCO            BIGINT;
    V_SIMILARIDADE_EQUIPE         REAL;
    V_COD_SETOR_BANCO             BIGINT;
    V_SIMILARIDADE_SETOR          REAL;
    V_COD_FUNCAO_BANCO            BIGINT;
    V_SIMILARIDADE_FUNCAO         REAL;
    V_FUNCAO_DELETADA             BOOLEAN;
    V_VALOR_SIMILARIDADE_PAISES   REAL     := 0.4;
    V_NOME_PAIS                   VARCHAR(255);
    V_SIGLA_ISO2                  CHAR(2);
    V_EXISTE_TELEFONE             BOOLEAN  := FALSE;
    V_SIMILARIDADE_PAISES         REAL;
    V_PREFIXO_PAIS                INTEGER;
    V_EXISTE_EMAIL                BOOLEAN  := FALSE;
    V_COD_COLABORADOR_CADASTRADO  BIGINT;

BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE := OLD.COD_UNIDADE;
            NEW.COD_EMPRESA := OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NOME_FORMATADO_IMPORT := UPPER(NEW.NOME_EDITAVEL);
        NEW.EQUIPE_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.EQUIPE_EDITAVEL);
        NEW.SETOR_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.SETOR_EDITAVEL);
        NEW.FUNCAO_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.FUNCAO_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE.
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE.
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA.
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES CPF.
        -- Nulo: erro.
        -- Com mais de 11 dígitos: erro.
        -- Com menos de 7 dígitos: erro.
        -- Com letras: erro.
        -- Cadastrado em outra unidade da mesma empresa: erro.
        -- Cadastrado na mesma unidade: erro.
        -- Cadastrado em outra empresa: erro.
        IF (NEW.CPF_EDITAVEL IS NOT NULL)
        THEN
            -- CPF com mais de 11 dígitos.
            IF (LENGTH(NEW.CPF_EDITAVEL) > 11)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                      '- O CPF DEVE CONTER ATÉ 11 DÍGITOS',
                                      V_QUEBRA_LINHA);
            ELSE
                -- CPF com menos de 7 dígitos.
                IF (LENGTH(NEW.CPF_EDITAVEL) < 7)
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                          '- O CPF DEVE CONTER MAIS QUE 7 DÍGITOS',
                                          V_QUEBRA_LINHA);
                ELSE
                    --CPF com letras.
                    IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.CPF_EDITAVEL)) IS FALSE)
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                              '- O CPF DEVE CONTER APENAS NÚMEROS',
                                              V_QUEBRA_LINHA);
                    ELSE
                        NEW.CPF_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.CPF_EDITAVEL);
                        -- CPF cadastrado na mesma empresa mas em outra unidade.
                        IF EXISTS(SELECT CD.CPF
                                  FROM COLABORADOR_DATA CD
                                  WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                    AND CD.COD_EMPRESA = NEW.COD_EMPRESA
                                    AND CD.COD_UNIDADE != NEW.COD_UNIDADE)
                        THEN
                            V_QTD_ERROS = V_QTD_ERROS + 1;
                            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                  '- O CPF JÁ ESTÁ CADASTRADO E PERTENCE A OUTRA UNIDADE',
                                                  V_QUEBRA_LINHA);
                            NEW.STATUS_IMPORT_REALIZADO = TRUE;
                        ELSE
                            -- CPF cadastrado na mesma unidade.
                            IF EXISTS(SELECT CD.CPF
                                      FROM COLABORADOR_DATA CD
                                      WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                        AND CD.COD_EMPRESA = NEW.COD_EMPRESA
                                        AND CD.COD_UNIDADE = NEW.COD_UNIDADE)
                            THEN
                                V_QTD_ERROS = V_QTD_ERROS + 1;
                                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                      '- O CPF JÁ ESTÁ CADASTRADO NA UNIDADE INFORMADA',
                                                      V_QUEBRA_LINHA);
                                NEW.STATUS_IMPORT_REALIZADO = TRUE;
                            ELSE
                                -- CPF em outra empresa.
                                IF EXISTS(SELECT CD.CPF
                                          FROM COLABORADOR_DATA CD
                                          WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                            AND CD.COD_EMPRESA != NEW.COD_EMPRESA)
                                THEN
                                    V_QTD_ERROS = V_QTD_ERROS + 1;
                                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                          '- O CPF JÁ ESTÁ CADASTRADO EM OUTRA EMPRESA',
                                                          V_QUEBRA_LINHA);
                                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                                END IF;
                            END IF;
                        END IF;
                    END IF;
                END IF;
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O CPF NÃO PODE SER NULO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES PIS.
        -- PIS com menos de 11 dígitos: erro.
        IF ((NEW.PIS_EDITAVEL IS NOT NULL) AND (LENGTH(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL)) <> 11))
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PIS DEVE CONTER 11 DÍGITOS', V_QUEBRA_LINHA);
        ELSE
            IF ((NEW.PIS_EDITAVEL IS NOT NULL) AND
                (LENGTH(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL)) = 11))
            THEN
                NEW.PIS_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL);
            END IF;
        END IF;

        -- Data de nascimento.
        -- Nula: erro.
        -- Maior que data atual: erro.
        IF (NEW.DATA_NASCIMENTO_EDITAVEL IS NOT NULL)
        THEN
            -- Utilizamos o LTRIM para remover zeros à esquerda. Exemplo: se n retirarmos o '0' uma data errada como:
            -- 0111990, seria cadastrado como 01-11-1990 pois pegaria o primeiro dia do mês 11.
            IF (LENGTH(LTRIM(NEW.DATA_NASCIMENTO_EDITAVEL, '0')) > 6)
            THEN
                NEW.DATA_NASCIMENTO_FORMATADA_IMPORT := TO_DATE(LPAD(NEW.DATA_NASCIMENTO_EDITAVEL, 8, '0'), 'DDMMYYYY');
                IF (NEW.DATA_NASCIMENTO_FORMATADA_IMPORT > NOW())
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE NASCIMENTO NÃO PODE SER MAIOR QUE A
                                                                     DATA ATUAL', V_QUEBRA_LINHA);
                END IF;
            ELSE
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- DATA DE NASCIMENTO INVÁLIDA', V_QUEBRA_LINHA);
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE NASCIMENTO NÃO PODE SER NULA', V_QUEBRA_LINHA);
        END IF;

        -- Data de admissão.
        -- Nula: erro.
        -- Maior que data atual: erro.
        IF (NEW.DATA_ADMISSAO_EDITAVEL IS NOT NULL)
        THEN
            -- Utilizamos o LTRIM para remover zeros à esquerda. Exemplo: se n retirarmos o '0' uma data errada como:
            -- 0111990, seria cadastrado como 01-11-1990 pois pegaria o primeiro dia do mês 11.
            IF (LENGTH(LTRIM(NEW.DATA_ADMISSAO_EDITAVEL, '0')) > 6)
            THEN
                NEW.DATA_ADMISSAO_FORMATADA_IMPORT := TO_DATE(LPAD(NEW.DATA_ADMISSAO_EDITAVEL, 8, '0'), 'DDMMYYYY');
                IF (NEW.DATA_ADMISSAO_FORMATADA_IMPORT > NOW())
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE ADMISSÃO NÃO PODE SER MAIOR QUE A DATA
                                                                     ATUAL', V_QUEBRA_LINHA);
                END IF;
            ELSE
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- DATA DE ADMISSÃO INVÁLIDA', V_QUEBRA_LINHA);
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE ADMISSÃO NÃO PODE SER NULA', V_QUEBRA_LINHA);
        END IF;

        -- Matrícula Promax.
        -- Com letras: erro.
        IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PROMAX_EDITAVEL)) IS FALSE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A MATRÍCULA PROMAX DEVE CONTER APENAS NÚMEROS',
                                  V_QUEBRA_LINHA);
        ELSE
            NEW.MATRICULA_PROMAX_FORMATADA_IMPORT :=
                    REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PROMAX_EDITAVEL);
        END IF;

        -- Matricula ponto
        -- Com letras: erro.
        IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PONTO_EDITAVEL)) IS FALSE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A MATRÍCULA PONTO DEVE CONTER APENAS NÚMEROS',
                                  V_QUEBRA_LINHA);
        ELSE
            NEW.MATRICULA_PONTO_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PONTO_EDITAVEL);
        END IF;

        -- Equipe.
        -- Nula: erro.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        IF (NEW.EQUIPE_FORMATADA_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A EQUIPE NÃO PODE SER NULA.', V_QUEBRA_LINHA);
        ELSE
            -- Procura equipe similar no banco.
            SELECT DISTINCT ON
                (NEW.EQUIPE_FORMATADA_IMPORT) E.CODIGO                                                         AS COD_EQUIPE,
                                              MAX(FUNC_GERA_SIMILARIDADE(NEW.EQUIPE_FORMATADA_IMPORT, E.NOME)) AS SIMILARIEDADE_EQUIPE
            INTO  V_COD_EQUIPE_BANCO, V_SIMILARIDADE_EQUIPE
            FROM EQUIPE E
            WHERE E.COD_UNIDADE = NEW.COD_UNIDADE
            GROUP BY NEW.EQUIPE_FORMATADA_IMPORT, E.NOME, E.CODIGO
            ORDER BY NEW.EQUIPE_FORMATADA_IMPORT, SIMILARIEDADE_EQUIPE DESC;

            -- Se a similaridade da equipe for menor que o exigido ou nula: Cadastra.
            IF (V_SIMILARIDADE_EQUIPE < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_EQUIPE IS NULL)
            THEN
                INSERT INTO EQUIPE (NOME, COD_UNIDADE)
                VALUES (NEW.EQUIPE_EDITAVEL, NEW.COD_UNIDADE) RETURNING CODIGO INTO V_COD_EQUIPE_BANCO;
            END IF;
        END IF;

        -- Setor.
        -- Nulo: erro.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        IF (NEW.SETOR_FORMATADO_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O SETOR NÃO PODE SER NULO.', V_QUEBRA_LINHA);
        ELSE
            -- Procura setor similar no banco.
            SELECT DISTINCT ON
                (NEW.SETOR_FORMATADO_IMPORT) S.CODIGO                                                        AS COD_SETOR,
                                             MAX(FUNC_GERA_SIMILARIDADE(NEW.SETOR_FORMATADO_IMPORT, S.NOME)) AS SIMILARIEDADE_SETOR
            INTO  V_COD_SETOR_BANCO, V_SIMILARIDADE_SETOR
            FROM SETOR S
            WHERE S.COD_UNIDADE = NEW.COD_UNIDADE
            GROUP BY NEW.SETOR_FORMATADO_IMPORT, S.NOME, S.CODIGO
            ORDER BY NEW.SETOR_FORMATADO_IMPORT, SIMILARIEDADE_SETOR DESC;

            -- Se a similaridade do setor for menor que o exigido ou nula: Cadastra.
            IF (V_SIMILARIDADE_SETOR < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_SETOR IS NULL)
            THEN
                INSERT INTO SETOR (NOME, COD_UNIDADE)
                VALUES (NEW.SETOR_EDITAVEL, NEW.COD_UNIDADE) RETURNING CODIGO INTO V_COD_SETOR_BANCO;
            END IF;

        END IF;

        -- Função.
        -- Nula: erro.
        -- Deletada: update para remover a deleção.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        IF (NEW.FUNCAO_FORMATADA_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A FUNÇÃO NÃO PODE SER NULA.', V_QUEBRA_LINHA);
        ELSE
            -- Procura função similar no banco.
            SELECT DISTINCT ON
                (NEW.FUNCAO_FORMATADA_IMPORT) FD.CODIGO                                                         AS COD_FUNCAO,
                                              MAX(FUNC_GERA_SIMILARIDADE(NEW.FUNCAO_FORMATADA_IMPORT, FD.NOME)) AS SIMILARIEDADE_FUNCAO
            INTO  V_COD_FUNCAO_BANCO, V_SIMILARIDADE_FUNCAO, V_FUNCAO_DELETADA
            FROM FUNCAO_DATA FD
            WHERE FD.COD_EMPRESA = NEW.COD_EMPRESA
            GROUP BY NEW.FUNCAO_FORMATADA_IMPORT, FD.NOME, FD.CODIGO
            ORDER BY NEW.FUNCAO_FORMATADA_IMPORT, SIMILARIEDADE_FUNCAO DESC;

            -- Se a similaridade da funcao for menor que o exigido ou nula: Cadastra.
            IF (V_SIMILARIDADE_FUNCAO < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_FUNCAO IS NULL)
            THEN
                INSERT INTO FUNCAO_DATA (NOME, COD_EMPRESA)
                VALUES (NEW.FUNCAO_EDITAVEL, NEW.COD_EMPRESA) RETURNING CODIGO INTO V_COD_FUNCAO_BANCO;
            ELSE
                -- Se a similaridade da funcao for maior que o exigido e ela estiver deletada: Realiza update.
                IF (V_SIMILARIDADE_FUNCAO > V_VALOR_SIMILARIDADE AND V_FUNCAO_DELETADA)
                THEN
                    UPDATE FUNCAO_DATA
                    SET DELETADO            = FALSE,
                        DATA_HORA_DELETADO  = NULL,
                        PG_USERNAME_DELECAO = NULL
                    WHERE CODIGO = V_COD_FUNCAO_BANCO
                      AND COD_EMPRESA = NEW.COD_EMPRESA;
                END IF;
            END IF;
        END IF;

        -- Telefone.
        -- Com letras ou caracteres especiais: erro.
        -- Sem pais: erro.
        IF ((NEW.TELEFONE_EDITAVEL IS NOT NULL) AND (LENGTH(NEW.TELEFONE_EDITAVEL) > 0))
        THEN
            IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TELEFONE_EDITAVEL)) IS FALSE)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS =
                        CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O TELEFONE DEVE CONTER APENAS NÚMEROS', V_QUEBRA_LINHA);
            ELSE
                -- Verifica pais
                IF (NEW.PAIS_EDITAVEL IS NOT NULL)
                THEN
                    NEW.PAIS_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PAIS_EDITAVEL);
                    -- Verifica similaridade do pais para pegar sigla.
                    SELECT DISTINCT ON
                        (NEW.PAIS_FORMATADO_IMPORT) PP.NOME                                                         AS NOME_PAIS,
                                                    PP.SIGLA_ISO2                                                   AS SIGLA_ISO2,
                                                    PP.PREFIXO_TELEFONE                                             AS PREFIXO_PAIS,
                                                    MAX(FUNC_GERA_SIMILARIDADE(NEW.PAIS_FORMATADO_IMPORT, PP.NOME)) AS SIMILARIEDADE_PAISES
                    INTO  V_NOME_PAIS, V_SIGLA_ISO2, V_PREFIXO_PAIS, V_SIMILARIDADE_PAISES
                    FROM PROLOG_PAISES PP
                    GROUP BY NEW.PAIS_FORMATADO_IMPORT, PP.NOME, PP.SIGLA_ISO2, PREFIXO_TELEFONE
                    ORDER BY NEW.PAIS_FORMATADO_IMPORT, SIMILARIEDADE_PAISES DESC;

                    IF (V_SIMILARIDADE_PAISES < V_VALOR_SIMILARIDADE_PAISES OR V_SIMILARIDADE_PAISES IS NULL)
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PAIS NÃO FOI ENCONTRADO', V_QUEBRA_LINHA);
                    ELSE
                        NEW.TELEFONE_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TELEFONE_EDITAVEL);
                        V_EXISTE_TELEFONE := TRUE;
                    END IF;
                ELSE
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PAIS NÃO PODE SER NULO', V_QUEBRA_LINHA);
                END IF;
            END IF;
        END IF;

        -- E-mail.
        -- Se não for do tipo EMAIL: erro.
        IF ((NEW.EMAIL_EDITAVEL IS NOT NULL) AND (LENGTH(NEW.TELEFONE_EDITAVEL) > 0))
        THEN
            BEGIN
                NEW.EMAIL_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.EMAIL_EDITAVEL);
            EXCEPTION
                WHEN INVALID_TEXT_REPRESENTATION THEN -- error that can be handled
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS =
                            CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- E-MAIL INCORRETO', V_QUEBRA_LINHA);
            END;
            V_EXISTE_EMAIL = TRUE;
        END IF;


        IF (V_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = V_MSGS_ERROS;
        ELSE
            INSERT INTO COLABORADOR_DATA (CPF,
                                          MATRICULA_AMBEV,
                                          MATRICULA_TRANS,
                                          DATA_NASCIMENTO,
                                          DATA_ADMISSAO,
                                          NOME,
                                          COD_EQUIPE,
                                          COD_FUNCAO,
                                          COD_UNIDADE,
                                          COD_PERMISSAO,
                                          COD_EMPRESA,
                                          COD_SETOR,
                                          PIS,
                                          COD_UNIDADE_CADASTRO,
                                          DELETADO)

            VALUES (NEW.CPF_FORMATADO_IMPORT,
                    NEW.MATRICULA_PROMAX_FORMATADA_IMPORT,
                    NEW.MATRICULA_PONTO_FORMATADA_IMPORT,
                    NEW.DATA_NASCIMENTO_FORMATADA_IMPORT,
                    NEW.DATA_ADMISSAO_FORMATADA_IMPORT,
                    NEW.NOME_FORMATADO_IMPORT,
                    V_COD_EQUIPE_BANCO,
                    V_COD_FUNCAO_BANCO,
                    NEW.COD_UNIDADE,
                    0,
                    NEW.COD_EMPRESA,
                    V_COD_SETOR_BANCO,
                    NEW.PIS_FORMATADO_IMPORT,
                    NEW.COD_UNIDADE,
                    FALSE) RETURNING CODIGO INTO V_COD_COLABORADOR_CADASTRADO;

            IF (V_EXISTE_TELEFONE IS TRUE)
            THEN
                INSERT INTO COLABORADOR_TELEFONE(COD_COLABORADOR,
                                                 SIGLA_ISO2,
                                                 PREFIXO_PAIS,
                                                 NUMERO_TELEFONE,
                                                 COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                VALUES (V_COD_COLABORADOR_CADASTRADO,
                        V_SIGLA_ISO2,
                        V_PREFIXO_PAIS,
                        NEW.TELEFONE_FORMATADO_IMPORT,
                        V_COD_COLABORADOR_CADASTRADO);
            END IF;

            IF (V_EXISTE_EMAIL IS TRUE)
            THEN
                INSERT INTO COLABORADOR_EMAIL(COD_COLABORADOR,
                                              EMAIL,
                                              COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                VALUES (V_COD_COLABORADOR_CADASTRADO,
                        NEW.EMAIL_FORMATADO_IMPORT,
                        V_COD_COLABORADOR_CADASTRADO);
            END IF;
            NEW.STATUS_IMPORT_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;