-- MODIFICAÇÕES EM IMPORT DE PNEUS
-- 2020-05-25 -> Modifica FK cod_unidade para que referencie a tabela dados_autor_import (thaisksf - PL-2711).
-- 2020-05-25 -> Adiciona cod_empresa na criação da tabela (thaisksf - PL-2711).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_PNEU_IMPORT_CRIA_TABELA_IMPORT(F_COD_EMPRESA BIGINT,
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
                                        'P_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' ||
                                        F_ANO || '_' || F_MES || '_' || F_DIA || '_' ||
                                        F_USUARIO)));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                                   BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT                   BIGINT,
            COD_EMPRESA                              BIGINT,
            COD_UNIDADE                              BIGINT,
            NUMERO_FOGO_EDITAVEL                     VARCHAR(255),
            MARCA_EDITAVEL                           VARCHAR(255),
            MODELO_EDITAVEL                          VARCHAR(255),
            DOT_EDITAVEL                             VARCHAR(20),
            DIMENSAO_EDITAVEL                        VARCHAR(255),
            PRESSAO_RECOMENDADA_EDITAVEL             VARCHAR(255),
            QTD_SULCOS_EDITAVEL                      VARCHAR(255),
            ALTURA_SULCOS_EDITAVEL                   VARCHAR(255),
            VALOR_PNEU_EDITAVEL                      VARCHAR(255),
            VALOR_BANDA_EDITAVEL                     VARCHAR(255),
            VIDA_ATUAL_EDITAVEL                      VARCHAR(255),
            VIDA_TOTAL_EDITAVEL                      VARCHAR(255),
            MARCA_BANDA_EDITAVEL                     VARCHAR(255),
            MODELO_BANDA_EDITAVEL                    VARCHAR(255),
            QTD_SULCOS_BANDA_EDITAVEL                VARCHAR(255),
            ALTURA_SULCOS_BANDA_EDITAVEL             VARCHAR(255),
            PNEU_NOVO_NUNCA_RODADO_EDITAVEL          VARCHAR(255),
            NUMERO_FOGO_FORMATADO_IMPORT             VARCHAR(255),
            MARCA_FORMATADA_IMPORT                   VARCHAR(255),
            MODELO_FORMATADO_IMPORT                  VARCHAR(255),
            DOT_FORMATADO_IMPORT                     VARCHAR(255),
            DIMENSAO_FORMATADA_IMPORT                VARCHAR(255),
            PRESSAO_RECOMENDADA_FORMATADA_IMPORT     REAL,
            QTD_SULCOS_FORMATADA_IMPORT              SMALLINT,
            ALTURA_SULCOS_FORMATADA_IMPORT           REAL,
            VALOR_PNEU_FORMATADO_IMPORT              REAL,
            VALOR_BANDA_FORMATADO_IMPORT             REAL,
            VIDA_ATUAL_FORMATADA_IMPORT              SMALLINT,
            VIDA_TOTAL_FORMATADA_IMPORT              SMALLINT,
            MARCA_BANDA_FORMATADA_IMPORT             VARCHAR(255),
            MODELO_BANDA_FORMATADO_IMPORT            VARCHAR(255),
            QTD_SULCOS_BANDA_FORMATADA_IMPORT        SMALLINT,
            ALTURA_SULCOS_BANDA_FORMATADA_IMPORT     REAL,
            PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT  BOOLEAN,
            STATUS_IMPORT_REALIZADO                  BOOLEAN,
            ERROS_ENCONTRADOS                        TEXT,
            USUARIO_UPDATE                           VARCHAR(255),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
        );', F_NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE PNEUS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_PNEU ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_PNEU
                    BEFORE INSERT OR UPDATE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU();',
                   F_NOME_TABELA_IMPORT,
                   F_NOME_TABELA_IMPORT);

    --CRIA AUDIT PARA A TABELA
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_PNEU ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_PNEU
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                   F_NOME_TABELA_IMPORT,
                   F_NOME_TABELA_IMPORT);

    -- GARANTE UPDATE PARA O NATAN
    -- TODO REMOVER HARDCODED
    EXECUTE FORMAT(
            'grant select, update on implantacao.%I to prolog_user_natan;', F_NOME_TABELA_IMPORT
        );

    --RETORNA NOME DA TABELA
    SELECT F_NOME_TABELA_IMPORT INTO F_NOME_TABELA_CRIADA;
END ;
$$;

CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_PNEU_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                            F_NOME_TABELA_IMPORT TEXT,
                                                                            F_COD_EMPRESA BIGINT,
                                                                            F_COD_UNIDADE BIGINT,
                                                                            F_JSON_PNEUS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                NUMERO_FOGO_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MODELO_EDITAVEL,
                                                DOT_EDITAVEL,
                                                DIMENSAO_EDITAVEL,
                                                PRESSAO_RECOMENDADA_EDITAVEL,
                                                QTD_SULCOS_EDITAVEL,
                                                ALTURA_SULCOS_EDITAVEL,
                                                VALOR_PNEU_EDITAVEL,
                                                VALOR_BANDA_EDITAVEL,
                                                VIDA_ATUAL_EDITAVEL,
                                                VIDA_TOTAL_EDITAVEL,
                                                MARCA_BANDA_EDITAVEL,
                                                MODELO_BANDA_EDITAVEL,
                                                QTD_SULCOS_BANDA_EDITAVEL,
                                                ALTURA_SULCOS_BANDA_EDITAVEL,
                                                PNEU_NOVO_NUNCA_RODADO_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''numeroFogo'') :: TEXT,
                          (SRC ->> ''marca'') :: TEXT,
                          (SRC ->> ''modelo'') :: TEXT,
                          (SRC ->> ''dot'') :: TEXT,
                          (SRC ->> ''dimensao'') :: TEXT,
                          (SRC ->> ''pressaoIdeal'') :: TEXT,
                          (SRC ->> ''qtdSulcos'') :: TEXT,
                          (SRC ->> ''alturaSulcos'') :: TEXT,
                          (SRC ->> ''valorPneu'') :: TEXT,
                          (SRC ->> ''valorBanda'') :: TEXT,
                          (SRC ->> ''vidaAtual'') :: TEXT,
                          (SRC ->> ''vidaTotal'') :: TEXT,
                          (SRC ->> ''marcaBanda'') :: TEXT,
                          (SRC ->> ''modeloBanda'') :: TEXT,
                          (SRC ->> ''qtdSulcosBanda'') :: TEXT,
                          (SRC ->> ''alturaSulcos'') :: TEXT,
                          (SRC ->> ''pneuNovoNuncaRodado'') :: TEXT
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_EMPRESA,
                   F_COD_UNIDADE,
                   F_JSON_PNEUS);
END
$$;

-- 2020-05-25 -> Adiciona verificações de cod_empresa e cod_unidade (thaisksf - PL-2711).
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_VALOR_SIMILARIDADE CONSTANT              REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIMENSAO CONSTANT     REAL     := 0.55;
    F_SEM_SIMILARIDADE CONSTANT                REAL     := 0.0;
    F_QTD_ERROS                                SMALLINT := 0;
    F_MSGS_ERROS                               TEXT;
    F_QUEBRA_LINHA                             TEXT     := CHR(10);
    F_COD_MARCA_BANCO                          BIGINT;
    F_SIMILARIDADE_MARCA                       REAL;
    F_MARCA_MODELO                             TEXT;
    F_COD_MODELO_BANCO                         BIGINT;
    F_SIMILARIDADE_MODELO                      REAL;
    F_COD_MARCA_BANDA_BANCO                    BIGINT;
    F_SIMILARIDADE_MARCA_BANDA                 REAL;
    F_MARCA_MODELO_BANDA                       TEXT;
    F_COD_MODELO_BANDA_BANCO                   BIGINT;
    F_SIMILARIDADE_MODELO_BANDA                REAL;
    DATE_CONVERTER                             TEXT     := 'YYYYWW';
    PREFIXO_ANO                                TEXT     := SUBSTRING(CURRENT_TIMESTAMP::TEXT, 1, 2);
    DOT_EM_DATA                                DATE;
    F_COD_DIMENSAO                             BIGINT;
    F_SIMILARIDADE_DIMENSAO                    REAL;
    F_ALTURA_MIN_SULCOS                        REAL     := 1;
    F_ALTURA_MAX_SULCOS                        REAL     := 50;
    F_QTD_MIN_SULCOS                           SMALLINT := 1;
    F_QTD_MAX_SULCOS                           SMALLINT := 6;
    F_QTD_SULCOS_DEFAULT                       SMALLINT := 4;
    F_ERRO_SULCOS                              BOOLEAN  := FALSE;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO      REAL     := 0;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP REAL;
    F_PNEU_NOVO_NUNCA_RODADO_ARRAY             TEXT[]   := ('{"SIM", "OK", "TRUE"}');
    F_PNEU_NOVO_NUNCA_RODADO                   TEXT;
    F_COD_TIPO_SERVICO                         BIGINT;
    F_COD_SERVICO_REALIZADO                    BIGINT;
    F_COD_PNEU                                 BIGINT;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE = OLD.COD_UNIDADE;
            NEW.COD_EMPRESA = OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NUMERO_FOGO_FORMATADO_IMPORT = UPPER(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.NUMERO_FOGO_EDITAVEL));
        NEW.MARCA_FORMATADA_IMPORT = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.DOT_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.DOT_EDITAVEL);
        NEW.DIMENSAO_FORMATADA_IMPORT := REMOVE_ALL_SPACES(NEW.DIMENSAO_EDITAVEL);
        NEW.MARCA_BANDA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_BANDA_EDITAVEL);
        NEW.MODELO_BANDA_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_BANDA_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES NÚMERO DE FOGO.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Número de fogo cadastrado na mesma unidade: Erro.
        IF (NEW.NUMERO_FOGO_FORMATADO_IMPORT IS NOT NULL)
        THEN
            IF EXISTS(SELECT P.CODIGO_CLIENTE
                      FROM PNEU P
                      WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                            NEW.NUMERO_FOGO_FORMATADO_IMPORT
                        AND P.COD_EMPRESA = NEW.COD_EMPRESA
                        AND P.COD_UNIDADE != NEW.COD_UNIDADE)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O PNEU JÁ ESTÁ CADASTRADO E PERTENCE A OUTRA UNIDADE',
                                      F_QUEBRA_LINHA);
                NEW.STATUS_IMPORT_REALIZADO = TRUE;
            ELSE
                IF EXISTS(SELECT P.CODIGO_CLIENTE
                          FROM PNEU P
                          WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                                NEW.NUMERO_FOGO_FORMATADO_IMPORT
                            AND P.COD_EMPRESA = NEW.COD_EMPRESA
                            AND P.COD_UNIDADE = NEW.COD_UNIDADE)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O PNEU JÁ ESTÁ CADASTRADO NA UNIDADE INFORMADA',
                                          F_QUEBRA_LINHA);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O NÚMERO DE FOGO NÃO PODE SER NULO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES MARCA.
        -- Marca nula: Erro.
        IF (NEW.MARCA_FORMATADA_IMPORT IS NOT NULL)
        THEN
            -- Procura marca similar no banco.
            SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAP.CODIGO                                                        AS COD_MARCA_BANCO,
                                                            MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAP.NOME)) AS SIMILARIEDADE_MARCA
            INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
            FROM MARCA_PNEU MAP
            GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAP.NOME, MAP.CODIGO
            ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

            F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
            -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
            -- Se não for: Mostra erro de marca não encontrada (Não cadastra pois é nível Prolog).
            IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
            THEN
                -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
                IF (NEW.MODELO_FORMATADO_IMPORT IS NOT NULL)
                THEN
                    SELECT DISTINCT ON (F_MARCA_MODELO) MOP.CODIGO AS COD_MODELO_PNEU,
                                                        CASE
                                                            WHEN F_COD_MARCA_BANCO = MOP.COD_MARCA
                                                                THEN
                                                                MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                           CONCAT(MOP.COD_MARCA, MOP.NOME)))
                                                            ELSE F_SEM_SIMILARIDADE
                                                            END    AS SIMILARIEDADE_MODELO
                    INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
                    FROM MODELO_PNEU MOP
                    WHERE MOP.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO, MOP.NOME, MOP.CODIGO
                    ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;

                    -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.
                    IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO IS NULL)
                    THEN
                        BEGIN
                            -- VERIFICAÇÃO DE SULCOS.
                            -- Parse para smallint.
                            NEW.QTD_SULCOS_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O ' ||
                                                      'VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS NÃO PODE SER MAIOR QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS NÃO PODE SER MAIOR QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A ALTURA DOS SULCOS NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);

                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_FORMATADA_IMPORT) RETURNING CODIGO INTO F_COD_MODELO_BANCO;
                        END IF;
                    END IF;
                ELSE
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE PNEU NÃO PODE SER NULO', F_QUEBRA_LINHA);
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DOT
        IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) > 4)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT DEVE POSSUIR NO MÁXIMO 4 DÍGITOS',
                                  F_QUEBRA_LINHA);

        ELSE
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) < 4)
            THEN
                NEW.DOT_FORMATADO_IMPORT = LPAD(NEW.DOT_FORMATADO_IMPORT, 4, '0');
            END IF;
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) = 4)
            THEN
                BEGIN
                    -- Transforma o DOT_FORMATADO em data
                    DOT_EM_DATA := TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 3, 4)),
                                                  (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 1, 2))),
                                           DATE_CONVERTER);
                    -- Verifica se a data do DOT que foi transformado é maior que a data atual, se for está errado.
                    IF (DOT_EM_DATA > CURRENT_DATE)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT NÃO PODE SER MAIOR QUE A DATA ATUAL',
                                       F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_datetime_format THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS,
                                       '- DOT COM CARACTERES INCORRETOS - DEVE POSSUIR APENAS NÚMEROS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        -- VERIFICAÇÕES DIMENSÃO
        IF (NEW.DIMENSAO_FORMATADA_IMPORT IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            SELECT DISTINCT ON (NEW.DIMENSAO_FORMATADA_IMPORT) DP.CODIGO                                                                    AS COD_DIMENSAO_BANCO,
                                                               MAX(func_gera_similaridade(NEW.DIMENSAO_FORMATADA_IMPORT,
                                                                                          CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO))) AS SIMILARIDADE_DIMENSAO
            INTO F_COD_DIMENSAO, F_SIMILARIDADE_DIMENSAO
            FROM DIMENSAO_PNEU DP
            GROUP BY NEW.DIMENSAO_FORMATADA_IMPORT, NEW.DIMENSAO_EDITAVEL,
                     CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO),
                     DP.CODIGO
            ORDER BY NEW.DIMENSAO_FORMATADA_IMPORT, SIMILARIDADE_DIMENSAO DESC;

            IF (F_SIMILARIDADE_DIMENSAO < F_VALOR_SIMILARIDADE_DIMENSAO)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        END IF;

        -- VERIFICAÇÕES PRESSÃO IDEAL
        IF (NEW.PRESSAO_RECOMENDADA_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PRESSÃO RECOMENDADA NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.PRESSAO_RECOMENDADA_EDITAVEL), ',', '.')::REAL;
                IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A PRESSÃO RECOMENDADA NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT > 150)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PRESSÃO RECOMENDADA NÃO PODE SER MAIOR QUE 150',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- PRESSÃO IDEAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VALOR PNEU
        IF (NEW.VALOR_PNEU_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NULO',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.VALOR_PNEU_FORMATADO_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.VALOR_PNEU_EDITAVEL), ',', '.')::REAL;
                IF (NEW.VALOR_PNEU_FORMATADO_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NEGATIVO',
                                          F_QUEBRA_LINHA);
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA TOTAL.
        IF (NEW.VIDA_TOTAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA TOTAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_TOTAL_FORMATADA_IMPORT.
                -- Acrescentado +1 à vida_total devido ao prolog considerar que a vida_atual do pneu novo é = 1, não 0.
                NEW.VIDA_TOTAL_FORMATADA_IMPORT := (NEW.VIDA_TOTAL_EDITAVEL :: INTEGER + 1);
                IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT < 1)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA TOTAL DO PNEU NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT > 10)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA TOTAL DO PNEU NÃO PODE SER MAIOR QUE 10',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA TOTAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA ATUAL
        IF (NEW.VIDA_ATUAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA ATUAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_ATUAL_FORMATADA_IMPORT.
                -- É incrementado +1 à vida_atual devido ao prolog considerar que a vida do pneu novo é = 1 e não 0.
                NEW.VIDA_ATUAL_FORMATADA_IMPORT := (NEW.VIDA_ATUAL_EDITAVEL :: INTEGER + 1);

                --VIDA_ATUAL FOR MAIOR QUE A VIDA TOTAL: Erro.
                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > NEW.VIDA_TOTAL_FORMATADA_IMPORT)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA ATUAL NÃO PODE SER MAIOR QUE A VIDA TOTAL',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT < 1)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA ATUAL DO PNEU NÃO PODE SER NEGATIVA',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;

                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT = 1)
                THEN
                    IF (NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL IS NOT NULL)
                    THEN
                        FOREACH F_PNEU_NOVO_NUNCA_RODADO IN ARRAY F_PNEU_NOVO_NUNCA_RODADO_ARRAY
                            LOOP
                                F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP :=
                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL,
                                                                   F_PNEU_NOVO_NUNCA_RODADO));
                                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP >
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO)
                                THEN
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO := F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP;
                                END IF;
                            END LOOP;
                    END IF;
                END IF;
                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO >= F_VALOR_SIMILARIDADE)
                THEN
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := TRUE;
                ELSE
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := FALSE;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA ATUAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        --VERIFICAÇÕES BANDA
        IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT IS NOT NULL AND NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
        THEN
            IF (NEW.MARCA_BANDA_FORMATADA_IMPORT IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- A MARCA DE BANDA NÃO PODE SER NULA PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                -- VERIFICAÇÕES MARCA DE BANDA: Procura marca de banda similar no banco.
                SELECT DISTINCT ON (NEW.MARCA_BANDA_FORMATADA_IMPORT) MAB.CODIGO                                                                  AS COD_MARCA_BANDA_BANCO,
                                                                      MAX(
                                                                              FUNC_GERA_SIMILARIDADE(NEW.MARCA_BANDA_FORMATADA_IMPORT, MAB.NOME)) AS SIMILARIEDADE_MARCA_BANDA
                INTO F_COD_MARCA_BANDA_BANCO, F_SIMILARIDADE_MARCA_BANDA
                FROM MARCA_BANDA MAB
                WHERE MAB.COD_EMPRESA = NEW.COD_EMPRESA
                GROUP BY NEW.MARCA_BANDA_FORMATADA_IMPORT, NEW.MARCA_BANDA_EDITAVEL, MAB.NOME, MAB.CODIGO
                ORDER BY NEW.MARCA_BANDA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA_BANDA DESC;

                F_MARCA_MODELO_BANDA := CONCAT(F_COD_MARCA_BANDA_BANCO, NEW.MODELO_BANDA_FORMATADO_IMPORT);
                -- Se a similaridade da marca de banda for menor que o exigido: Cadastra.
                IF (F_SIMILARIDADE_MARCA_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MARCA_BANDA IS NULL)
                THEN
                    INSERT INTO MARCA_BANDA (NOME, COD_EMPRESA)
                    VALUES (NEW.MARCA_BANDA_EDITAVEL, NEW.COD_EMPRESA) RETURNING CODIGO INTO F_COD_MARCA_BANDA_BANCO;
                END IF;

                IF (NEW.MODELO_BANDA_FORMATADO_IMPORT IS NULL)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                          F_QUEBRA_LINHA);
                ELSE
                    -- VERIFICAÇÕES MODELO DE BANDA: Procura modelo SIMILAR NO banco.
                    SELECT DISTINCT ON (F_MARCA_MODELO_BANDA) MOB.CODIGO AS COD_MODELO_BANDA,
                                                              CASE
                                                                  WHEN F_COD_MARCA_BANDA_BANCO = MOB.COD_MARCA
                                                                      THEN
                                                                      MAX(FUNC_GERA_SIMILARIDADE(
                                                                              F_MARCA_MODELO_BANDA,
                                                                              CONCAT(MOB.COD_MARCA, MOB.NOME)))
                                                                  ELSE F_SEM_SIMILARIDADE
                                                                  END    AS SIMILARIEDADE_MODELO_BANDA
                    INTO F_COD_MODELO_BANDA_BANCO, F_SIMILARIDADE_MODELO_BANDA
                    FROM MODELO_BANDA MOB
                    WHERE MOB.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO_BANDA, MOB.NOME, MOB.CODIGO
                    ORDER BY F_MARCA_MODELO_BANDA, SIMILARIEDADE_MODELO_BANDA DESC;

                    -- Se a similaridade do modelo de banda for menor do que o exigido: cadastra novo modelo de banda.
                    IF (F_SIMILARIDADE_MODELO_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO_BANDA IS NULL)
                    THEN
                        BEGIN
                            NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_BANDA_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O' ||
                                                      ' VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MAIOR' ||
                                                              ' QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS DE BANDA COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_BANDA_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_BANDA_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MAIOR ' ||
                                                              'QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS DE BANDA COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS,
                                           '- A ALTURA DOS SULCOS DE BANDA NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);
                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_BANDA (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_BANDA_EDITAVEL, F_COD_MARCA_BANDA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT) RETURNING CODIGO INTO F_COD_MODELO_BANDA_BANCO;
                        END IF;
                    END IF;
                END IF;
                --ELSE MARCA DE BANDA
            END IF;

            --VERIFICAÇÕES VALOR DE BANDA.
            IF (NEW.VALOR_BANDA_EDITAVEL IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O VALOR DA BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                BEGIN
                    NEW.VALOR_BANDA_FORMATADO_IMPORT :=
                            REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                            NEW.VALOR_BANDA_EDITAVEL), ',', '.')::REAL;
                    IF (NEW.VALOR_BANDA_FORMATADO_IMPORT < 0)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DA BANDA NÃO PODE SER NEGATIVO',
                                              F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_text_representation THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        IF (F_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            INSERT INTO PNEU (CODIGO_CLIENTE,
                              COD_MODELO,
                              COD_DIMENSAO,
                              PRESSAO_RECOMENDADA,
                              COD_UNIDADE,
                              STATUS,
                              VIDA_ATUAL,
                              VIDA_TOTAL,
                              PNEU_NOVO_NUNCA_RODADO,
                              COD_MODELO_BANDA,
                              DOT,
                              VALOR,
                              COD_EMPRESA,
                              COD_UNIDADE_CADASTRO)
            VALUES (NEW.NUMERO_FOGO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANCO,
                    F_COD_DIMENSAO,
                    NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT,
                    NEW.COD_UNIDADE,
                    'ESTOQUE',
                    NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                    NEW.VIDA_TOTAL_FORMATADA_IMPORT,
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANDA_BANCO,
                    NEW.DOT_FORMATADO_IMPORT,
                    NEW.VALOR_PNEU_FORMATADO_IMPORT,
                    NEW.COD_EMPRESA,
                    NEW.COD_UNIDADE) RETURNING CODIGO INTO F_COD_PNEU;


            IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
            THEN
                SELECT PTS.CODIGO AS CODIGO
                FROM PNEU_TIPO_SERVICO AS PTS
                WHERE PTS.COD_EMPRESA IS NULL
                  AND PTS.STATUS_ATIVO = TRUE
                  AND PTS.INCREMENTA_VIDA = TRUE
                  AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE
                INTO F_COD_TIPO_SERVICO;

                INSERT INTO PNEU_SERVICO_REALIZADO (COD_TIPO_SERVICO,
                                                    COD_UNIDADE,
                                                    COD_PNEU,
                                                    CUSTO,
                                                    VIDA,
                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_TIPO_SERVICO,
                        NEW.COD_UNIDADE,
                        F_COD_PNEU,
                        NEW.VALOR_BANDA_FORMATADO_IMPORT,
                        (NEW.VIDA_ATUAL_FORMATADA_IMPORT - 1),
                        'FONTE_CADASTRO') RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

                INSERT INTO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA (COD_SERVICO_REALIZADO,
                                                                    COD_MODELO_BANDA,
                                                                    VIDA_NOVA_PNEU,
                                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_SERVICO_REALIZADO,
                        F_COD_MODELO_BANDA_BANCO,
                        NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                        'FONTE_CADASTRO');

                INSERT INTO PNEU_SERVICO_CADASTRO (COD_PNEU,
                                                   COD_SERVICO_REALIZADO)
                VALUES (F_COD_PNEU, F_COD_SERVICO_REALIZADO);
            END IF;
            NEW.STATUS_IMPORT_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

-- MODIFICAÇÕES EM IMPORT DE VEICULOS
-- 2020-05-25 -> Remove FK cod_unidade (thaisksf - PL-2711).
-- 2020-05-26 -> Adiciona cod_empresa na criação da tabela (thaisksf - PL-2711).

CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_USUARIO TEXT,
                                                                              F_DATA DATE,
                                                                              OUT NOME_TABELA_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DIA                TEXT := (SELECT EXTRACT(DAY FROM F_DATA));
    MES                TEXT := (SELECT EXTRACT(MONTH FROM F_DATA));
    ANO                TEXT := (SELECT EXTRACT(YEAR FROM F_DATA)) ;
    NOME_TABELA_IMPORT TEXT := lower(remove_all_spaces(
                        'V_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' || ANO || '_' || MES ||
                        '_' || DIA || '_' || F_USUARIO));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                       BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT       BIGINT,
            COD_EMPRESA                  BIGINT,
            COD_UNIDADE                  BIGINT,
            PLACA_EDITAVEL               VARCHAR(255),
            KM_EDITAVEL                  BIGINT,
            MARCA_EDITAVEL               VARCHAR(255),
            MODELO_EDITAVEL              VARCHAR(255),
            TIPO_EDITAVEL                VARCHAR(255),
            QTD_EIXOS_EDITAVEL           VARCHAR(255),
            IDENTIFICADOR_FROTA_EDITAVEL VARCHAR(15),
            PLACA_FORMATADA_IMPORT       VARCHAR(255),
            MARCA_FORMATADA_IMPORT       VARCHAR(255),
            MODELO_FORMATADO_IMPORT      VARCHAR(255),
            TIPO_FORMATADO_IMPORT        VARCHAR(255),
            IDENTIFICADOR_FROTA_IMPORT   VARCHAR(15),
            STATUS_IMPORT_REALIZADO      BOOLEAN,
            ERROS_ENCONTRADOS            VARCHAR(255),
            USUARIO_UPDATE               VARCHAR(255),
            PRIMARY KEY (CODIGO),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
        );', NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE VEÍCULOS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO();',
                   NOME_TABELA_IMPORT,
                   NOME_TABELA_IMPORT);

    --CRIA AUDIT PARA A TABELA
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_VEICULO ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_VEICULO
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                    NOME_TABELA_IMPORT,
                    NOME_TABELA_IMPORT);

    -- GARANTE UPDATE PARA O NATAN
    -- TODO REMOVER HARDCODED
      EXECUTE FORMAT(
      'grant select, update on implantacao.%I to prolog_user_natan;', NOME_TABELA_IMPORT
      );

    --RETORNA NOME DA TABELA
    SELECT NOME_TABELA_IMPORT INTO NOME_TABELA_CRIADA;
END ;
$$;

-- 2020-05-25 -> Adiciona cod_empresa no insert da planilha (thaisksf - PL-2511).
DROP FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                  F_NOME_TABELA_IMPORT TEXT,
                                                                  F_COD_UNIDADE BIGINT,
                                                                  F_JSON_VEICULOS JSONB);
-- 2020-05-26 -> Adiciona cod_empresa no insert da planilha (thaisksf - PL-2511).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                               F_NOME_TABELA_IMPORT TEXT,
                                                                               F_COD_EMPRESA BIGINT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_JSON_VEICULOS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                PLACA_EDITAVEL,
                                                PLACA_FORMATADA_IMPORT,
                                                KM_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MARCA_FORMATADA_IMPORT,
                                                MODELO_EDITAVEL,
                                                MODELO_FORMATADO_IMPORT,
                                                TIPO_EDITAVEL,
                                                TIPO_FORMATADO_IMPORT,
                                                QTD_EIXOS_EDITAVEL,
                                                IDENTIFICADOR_FROTA_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT                                         AS PLACA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> ''placa'')) :: TEXT  AS PLACA_FORMATADA_IMPORT,
                          (SRC ->> ''km'') :: BIGINT                                          AS KM,
                          (SRC ->> ''marca'') :: TEXT                                         AS MARCA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''marca'')) :: TEXT  AS MARCA_FORMATADA_IMPORT,
                          (SRC ->> ''modelo'') :: TEXT                                        AS MODELO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''modelo'')) :: TEXT AS MODELO_FORMATADO_IMPORT,
                          (SRC ->> ''tipo'') :: TEXT                                          AS TIPO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''tipo'')) :: TEXT   AS TIPO_FORMATADO_IMPORT,
                          (SRC ->> ''qtdEixos'') :: TEXT                                      AS QTD_EIXOS,
                          (SRC ->> ''identificadorFrota'') :: TEXT                            AS IDENTIFICADOR_FROTA
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_EMPRESA,
                   F_COD_UNIDADE,
                   F_JSON_VEICULOS);
END
$$;

-- 2020-05-26 -> Adiciona verificações de cod_empresa e cod_unidade (thaisksf - PL-2711).
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO()
    RETURNS TRIGGER
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_VALOR_SIMILARIDADE CONSTANT          REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIAGRAMA CONSTANT REAL     := 0.5;
    F_SEM_SIMILARIDADE CONSTANT            REAL     := 0.0;
    F_QTD_ERROS                            SMALLINT := 0;
    F_MSGS_ERROS                           TEXT;
    F_QUEBRA_LINHA                         TEXT     := CHR(10);
    F_COD_MARCA_BANCO                      BIGINT;
    F_SIMILARIDADE_MARCA                   REAL;
    F_MARCA_MODELO                         TEXT;
    F_COD_MODELO_BANCO                     BIGINT;
    F_SIMILARIDADE_MODELO                  REAL;
    F_COD_DIAGRAMA_BANCO                   BIGINT;
    F_NOME_DIAGRAMA_BANCO                  TEXT;
    F_SIMILARIDADE_DIAGRAMA                REAL;
    F_DIAGRAMA_TIPO                        TEXT;
    F_EIXOS_DIAGRAMA                       TEXT;
    F_COD_TIPO_BANCO                       BIGINT;
    F_SIMILARIDADE_TIPO                    REAL;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
       IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE = OLD.COD_UNIDADE;
            NEW.COD_EMPRESA = OLD.COD_EMPRESA;
        END IF;
        NEW.PLACA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PLACA_EDITAVEL);
        NEW.MARCA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.TIPO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TIPO_EDITAVEL);
        NEW.IDENTIFICADOR_FROTA_IMPORT := NEW.IDENTIFICADOR_FROTA_EDITAVEL;
        NEW.USUARIO_UPDATE := SESSION_USER;

       -- VERIFICA SE EMPRESA EXISTE
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        IF (NEW.PLACA_FORMATADA_IMPORT IS NOT NULL) THEN
            IF LENGTH(NEW.PLACA_FORMATADA_IMPORT) <> 7
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO POSSUI 7 CARACTERES', F_QUEBRA_LINHA);
            ELSE
                IF EXISTS(SELECT V.PLACA
                          FROM VEICULO V
                          WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                            AND V.COD_EMPRESA != NEW.COD_EMPRESA)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   F_QUEBRA_LINHA);
                ELSE
                    IF EXISTS(SELECT V.PLACA
                              FROM VEICULO V
                              WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                AND V.COD_EMPRESA = NEW.COD_EMPRESA
                                AND COD_UNIDADE != NEW.COD_UNIDADE)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;
        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAV.CODIGO                                                        AS COD_MARCA_BANCO,
                                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAV.NOME)) AS SIMILARIEDADE_MARCA
        INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
        FROM MARCA_VEICULO MAV
        GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAV.NOME, MAV.CODIGO
        ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

        F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
        THEN
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            SELECT DISTINCT ON (F_MARCA_MODELO) MOV.CODIGO AS COD_MODELO_VEICULO,
                                                CASE
                                                    WHEN F_COD_MARCA_BANCO = MOV.COD_MARCA
                                                        THEN
                                                        MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                   CONCAT(MOV.COD_MARCA, MOV.NOME)))
                                                    ELSE F_SEM_SIMILARIDADE
                                                    END    AS SIMILARIEDADE_MODELO
            INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
            FROM MODELO_VEICULO MOV
            WHERE MOV.COD_EMPRESA = NEW.COD_EMPRESA
            GROUP BY F_MARCA_MODELO, MOV.NOME, MOV.CODIGO
            ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO IS NULL)
            THEN
                INSERT INTO MODELO_VEICULO (NOME, COD_MARCA, COD_EMPRESA)
                VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, NEW.COD_EMPRESA) RETURNING CODIGO INTO F_COD_MODELO_BANCO;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        F_EIXOS_DIAGRAMA := CONCAT(NEW.QTD_EIXOS_EDITAVEL, NEW.TIPO_FORMATADO_IMPORT);
        -- Procura diagrama no banco:
        WITH INFO_DIAGRAMAS AS (
            SELECT COUNT(VDE.POSICAO) AS QTD_EIXOS, VDE.COD_DIAGRAMA AS CODIGO, VD.NOME AS NOME
            FROM VEICULO_DIAGRAMA_EIXOS VDE
                     JOIN
                 VEICULO_DIAGRAMA VD ON VDE.COD_DIAGRAMA = VD.CODIGO
            GROUP BY VDE.COD_DIAGRAMA, VD.NOME),

             DIAGRAMAS AS (
                 SELECT VDUP.COD_VEICULO_DIAGRAMA AS COD_DIAGRAMA,
                        VDUP.NOME                 AS NOME_DIAGRAMA,
                        VDUP.QTD_EIXOS            AS QTD_EIXOS
                 FROM IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG VDUP
                 UNION ALL
                 SELECT ID.CODIGO AS COD_DIAGRAMA, ID.NOME AS NOME_DIAGRAMA, ID.QTD_EIXOS
                 FROM INFO_DIAGRAMAS ID)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        SELECT DISTINCT ON (F_EIXOS_DIAGRAMA) D.NOME_DIAGRAMA AS NOME_DIAGRAMA,
                                              D.COD_DIAGRAMA  AS DIAGRAMA_BANCO,
                                              CASE
                                                  WHEN D.QTD_EIXOS ::TEXT = NEW.QTD_EIXOS_EDITAVEL
                                                      THEN
                                                      MAX(FUNC_GERA_SIMILARIDADE(F_EIXOS_DIAGRAMA,
                                                                                 CONCAT(D.QTD_EIXOS, D.NOME_DIAGRAMA)))
                                                  ELSE F_SEM_SIMILARIDADE
                                                  END         AS SIMILARIEDADE_DIAGRAMA
        INTO F_NOME_DIAGRAMA_BANCO, F_COD_DIAGRAMA_BANCO,
            F_SIMILARIDADE_DIAGRAMA
        FROM DIAGRAMAS D
        GROUP BY F_EIXOS_DIAGRAMA, D.NOME_DIAGRAMA, D.COD_DIAGRAMA, D.QTD_EIXOS
        ORDER BY F_EIXOS_DIAGRAMA, SIMILARIEDADE_DIAGRAMA DESC;

        F_DIAGRAMA_TIPO := CONCAT(F_NOME_DIAGRAMA_BANCO, NEW.TIPO_FORMATADO_IMPORT);
        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        CASE WHEN (F_SIMILARIDADE_DIAGRAMA >= F_VALOR_SIMILARIDADE_DIAGRAMA)
            THEN
                SELECT DISTINCT ON (F_DIAGRAMA_TIPO) VT.CODIGO AS COD_TIPO_VEICULO,
                                                     CASE
                                                         WHEN F_COD_DIAGRAMA_BANCO = VT.COD_DIAGRAMA
                                                             THEN MAX(FUNC_GERA_SIMILARIDADE(NEW.TIPO_FORMATADO_IMPORT, VT.NOME))
                                                         ELSE F_SEM_SIMILARIDADE
                                                         END   AS SIMILARIEDADE_TIPO_DIAGRAMA
                INTO F_COD_TIPO_BANCO, F_SIMILARIDADE_TIPO
                FROM VEICULO_TIPO VT
                WHERE VT.COD_EMPRESA = NEW.COD_EMPRESA
                GROUP BY F_DIAGRAMA_TIPO,
                         VT.CODIGO
                ORDER BY F_DIAGRAMA_TIPO, SIMILARIEDADE_TIPO_DIAGRAMA DESC;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                IF (F_SIMILARIDADE_TIPO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_TIPO IS NULL)
                THEN
                    INSERT INTO VEICULO_TIPO (NOME, STATUS_ATIVO, COD_DIAGRAMA, COD_EMPRESA)
                    VALUES (NEW.TIPO_EDITAVEL, TRUE, F_COD_DIAGRAMA_BANCO, NEW.COD_EMPRESA) RETURNING CODIGO INTO F_COD_TIPO_BANCO;
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS =
                        concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', F_QUEBRA_LINHA);
            END CASE;
        -- VERIFICA QTD DE ERROS
        IF (F_QTD_ERROS > 0)
        THEN
            NEW.STATUS_IMPORT_REALIZADO = FALSE;
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            IF (F_QTD_ERROS = 0 AND EXISTS(SELECT V.PLACA
                                           FROM VEICULO V
                                           WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                             AND V.COD_EMPRESA = NEW.COD_EMPRESA
                                             AND COD_UNIDADE = NEW.COD_UNIDADE))
            THEN
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                UPDATE VEICULO_DATA
                SET COD_MODELO = F_COD_MODELO_BANCO,
                    COD_TIPO   = F_COD_TIPO_BANCO,
                    KM         = NEW.KM_EDITAVEL,
                    COD_DIAGRAMA = F_COD_DIAGRAMA_BANCO,
                    IDENTIFICADOR_FROTA = NEW.IDENTIFICADOR_FROTA_IMPORT
                WHERE PLACA = NEW.PLACA_FORMATADA_IMPORT
                  AND COD_EMPRESA = NEW.COD_EMPRESA
                  AND COD_UNIDADE = NEW.COD_UNIDADE;
                NEW.STATUS_IMPORT_REALIZADO = NULL;
                NEW.ERROS_ENCONTRADOS = 'A PLACA JÁ ESTAVA CADASTRADA - INFORMAÇÕES FORAM ATUALIZADAS.';
            ELSE
                IF (F_QTD_ERROS = 0 AND NOT EXISTS(SELECT V.PLACA
                                                   FROM VEICULO V
                                                   WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT))
                THEN
                    -- CADASTRA VEÍCULO.
                    INSERT INTO VEICULO (PLACA,
                                         COD_UNIDADE,
                                         KM,
                                         STATUS_ATIVO,
                                         COD_TIPO,
                                         COD_MODELO,
                                         COD_EIXOS,
                                         DATA_HORA_CADASTRO,
                                         COD_UNIDADE_CADASTRO,
                                         COD_EMPRESA,
                                         COD_DIAGRAMA,
                                         IDENTIFICADOR_FROTA)
                    VALUES (NEW.PLACA_FORMATADA_IMPORT,
                            NEW.COD_UNIDADE,
                            NEW.KM_EDITAVEL,
                            TRUE,
                            F_COD_TIPO_BANCO,
                            F_COD_MODELO_BANCO,
                            1,
                            NOW(),
                            NEW.COD_UNIDADE,
                            NEW.COD_EMPRESA,
                            F_COD_DIAGRAMA_BANCO,
                            NEW.IDENTIFICADOR_FROTA_IMPORT);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                    NEW.ERROS_ENCONTRADOS = '-';
                END IF;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;