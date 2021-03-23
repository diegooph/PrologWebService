-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica os dados que são inseridos na tabela de 'pré-vínculo', procurando os códigos correspondentes para efetuar
-- os vinculos entre pneus e veículos.
--
-- Pré-requisitos:
-- functions criadas:
-- REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS.
-- REMOVE_ALL_SPACES.
--
-- Histórico:
-- 2020-08-28 -> Function criada (thaisksf - PL-2771).
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_VINCULO_VEICULO_PNEU_CONFERE_PLANILHA_VINCULO()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_ERROS            SMALLINT := 0;
    V_MSGS_ERROS           TEXT;
    V_QUEBRA_LINHA         TEXT     := CHR(10);
    V_COD_PNEU             BIGINT;
    V_STATUS_PNEU          VARCHAR(255);
    V_COD_UNIDADE_PNEU     BIGINT;
    V_PLACA                VARCHAR(7);
    V_COD_TIPO_VEICULO     BIGINT;
    V_COD_DIAGRAMA_VEICULO BIGINT;
    V_COD_UNIDADE_PLACA    BIGINT;
    V_COD_EMPRESA_PLACA    BIGINT;
    V_POSICAO_PROLOG       INTEGER;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_VINCULO_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE = OLD.COD_UNIDADE;
            NEW.COD_EMPRESA = OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.PLACA_FORMATADA_VINCULO := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PLACA_EDITAVEL);
        NEW.NUMERO_FOGO_PNEU_FORMATADO_VINCULO := REMOVE_ALL_SPACES(NEW.NUMERO_FOGO_PNEU_EDITAVEL);
        NEW.NOMENCLATURA_POSICAO_FORMATADA_VINCULO := REMOVE_ALL_SPACES(NEW.NOMENCLATURA_POSICAO_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    concat(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    concat(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    concat(V_MSGS_ERROS, V_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES PLACA.
        -- Placa nula: Erro.
        -- Placa cadastrada em outra empresa: Erro.
        -- Placa cadastrada em outra unidade da mesma empresa: Erro.
        -- Posicao já ocupada por outro pneu: Erro.
        IF ((NEW.PLACA_FORMATADA_VINCULO IS NOT NULL) AND
            (LENGTH(NEW.PLACA_FORMATADA_VINCULO) <> 0))
        THEN
            SELECT V.PLACA,
                   V.COD_TIPO,
                   V.COD_DIAGRAMA,
                   V.COD_UNIDADE,
                   V.COD_EMPRESA
            INTO V_PLACA, V_COD_TIPO_VEICULO, V_COD_DIAGRAMA_VEICULO, V_COD_UNIDADE_PLACA, V_COD_EMPRESA_PLACA
            FROM VEICULO V
            WHERE REMOVE_ALL_SPACES(V.PLACA) ILIKE
                  NEW.PLACA_FORMATADA_VINCULO;
            IF (V_PLACA IS NULL)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                      '- A PLACA NÃO FOI ENCONTRADA',
                                      V_QUEBRA_LINHA);
                NEW.STATUS_VINCULO_REALIZADO = FALSE;
            ELSE
                IF (V_COD_EMPRESA_PLACA != NEW.COD_EMPRESA)
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA EMPRESA',
                                          V_QUEBRA_LINHA);
                    NEW.STATUS_VINCULO_REALIZADO = FALSE;
                ELSE
                    IF (V_COD_UNIDADE_PLACA != NEW.COD_UNIDADE)
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                              '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA UNIDADE',
                                              V_QUEBRA_LINHA);
                        NEW.STATUS_VINCULO_REALIZADO = FALSE;
                    ELSE
                        -- VERIFICAR SE A POSIÇÃO EXISTE NESSE VEÍCULO E SE ESTÁ DISPONÍVEL
                        IF ((NEW.NOMENCLATURA_POSICAO_FORMATADA_VINCULO IS NOT NULL) AND
                            (LENGTH(NEW.NOMENCLATURA_POSICAO_FORMATADA_VINCULO) <> 0))
                        THEN
                            SELECT PPNE.POSICAO_PROLOG
                            INTO V_POSICAO_PROLOG
                            FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                            WHERE PPNE.COD_DIAGRAMA = V_COD_DIAGRAMA_VEICULO
                              AND REMOVE_ALL_SPACES(PPNE.NOMENCLATURA) ILIKE NEW.NOMENCLATURA_POSICAO_FORMATADA_VINCULO
                              AND PPNE.COD_EMPRESA = NEW.COD_EMPRESA;
                            IF (V_POSICAO_PROLOG IS NOT NULL)
                            THEN
                                IF EXISTS(SELECT VP.PLACA
                                          FROM VEICULO_PNEU VP
                                          WHERE REMOVE_ALL_SPACES(VP.PLACA) = NEW.PLACA_FORMATADA_VINCULO
                                            AND VP.POSICAO = V_POSICAO_PROLOG
                                            AND VP.COD_UNIDADE = NEW.COD_UNIDADE)
                                THEN
                                    V_QTD_ERROS = V_QTD_ERROS + 1;
                                    V_MSGS_ERROS =
                                            CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                   '- JÁ EXISTE PNEU VINCULADO À POSIÇÃO (NOMENCLATURA) INFORMADA',
                                                   V_QUEBRA_LINHA);
                                END IF;
                            ELSE
                                V_QTD_ERROS = V_QTD_ERROS + 1;
                                V_MSGS_ERROS =
                                        CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                               '- NOMENCLATURA NÃO ENCONTRADA',
                                               V_QUEBRA_LINHA);
                                NEW.STATUS_VINCULO_REALIZADO = FALSE;
                            END IF;
                        ELSE
                            V_QTD_ERROS = V_QTD_ERROS + 1;
                            V_MSGS_ERROS =
                                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                           '- NOMENCLATURA NÃO PODE SER NULA',
                                           V_QUEBRA_LINHA);
                        END IF;
                    END IF;
                END IF;
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A PLACA DE FOGO NÃO PODE SER NULA',
                           V_QUEBRA_LINHA);
        END IF;


        -- VERIFICAÇÕES NÚMERO DE FOGO.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Código do pneu não encontrado: Erro.
        -- Status do pneu diferente de 'ESTOQUE': Erro.
        IF ((NEW.NUMERO_FOGO_PNEU_FORMATADO_VINCULO IS NOT NULL) AND
            (LENGTH(NEW.NUMERO_FOGO_PNEU_FORMATADO_VINCULO) <> 0))
        THEN
            SELECT P.CODIGO,
                   P.STATUS,
                   P.COD_UNIDADE
            INTO V_COD_PNEU, V_STATUS_PNEU, V_COD_UNIDADE_PNEU
            FROM PNEU P
            WHERE REMOVE_ALL_SPACES(P.CODIGO_CLIENTE) ILIKE
                  NEW.NUMERO_FOGO_PNEU_FORMATADO_VINCULO
              AND P.COD_EMPRESA = NEW.COD_EMPRESA;
            IF (V_COD_PNEU IS NULL)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                      '- O PNEU NÃO FOI ENCONTRADO',
                                      V_QUEBRA_LINHA);
                NEW.STATUS_VINCULO_REALIZADO = FALSE;
            ELSE
                IF (V_COD_UNIDADE_PNEU != NEW.COD_UNIDADE)
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS O PNEU PERTENCE A OUTRA UNIDADE',
                                          V_QUEBRA_LINHA);
                    NEW.STATUS_VINCULO_REALIZADO = FALSE;
                ELSE
                    IF (V_STATUS_PNEU != 'ESTOQUE')
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS =
                                CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                       '- PARA REALIZAR O VÍNCULO O PNEU DEVE ESTAR EM ESTOQUE, O STATUS ATUAL DO PNEU É: ',
                                       V_STATUS_PNEU, V_QUEBRA_LINHA);
                        NEW.STATUS_VINCULO_REALIZADO = FALSE;
                    END IF;
                END IF;
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O NÚMERO DE FOGO NÃO PODE SER NULO',
                           V_QUEBRA_LINHA);
        END IF;

        IF (V_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = V_MSGS_ERROS;
        ELSE
            UPDATE PNEU_DATA SET STATUS = 'EM_USO' WHERE CODIGO = V_COD_PNEU;
            INSERT INTO VEICULO_PNEU (PLACA,
                                      COD_PNEU,
                                      COD_UNIDADE,
                                      POSICAO,
                                      COD_DIAGRAMA)
            VALUES (V_PLACA,
                    V_COD_PNEU,
                    NEW.COD_UNIDADE,
                    V_POSICAO_PROLOG,
                    V_COD_DIAGRAMA_VEICULO);

            NEW.STATUS_VINCULO_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Cria uma tabela de "pré-import" e aplica uma trigger para verificar os dados inseridos e realizar o vinculo entre
-- veículos e pneus.
--
-- Pré-requisitos:
-- Func remove_all_apaces criada.
--
-- Histórico:
-- 2020-08-27 -> Function criada (thaisksf - PL-2771).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VINCULO_VEICULO_PNEU_CRIA_TABELA_VINCULO(F_COD_EMPRESA BIGINT,
                                                                                     F_COD_UNIDADE BIGINT,
                                                                                     F_USUARIO TEXT,
                                                                                     F_DATA DATE,
                                                                                     OUT NOME_TABELA_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_DIA                TEXT := (SELECT EXTRACT(DAY FROM F_DATA));
    V_MES                TEXT := (SELECT EXTRACT(MONTH FROM F_DATA));
    V_ANO                TEXT := (SELECT EXTRACT(YEAR FROM F_DATA)) ;
    V_NOME_TABELA_IMPORT TEXT := LOWER(REMOVE_ALL_SPACES(
                                        'VP_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' ||
                                        V_ANO || '_' || V_MES ||
                                        '_' || V_DIA || '_' || F_USUARIO));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
                CODIGO                                    BIGSERIAL,
                COD_DADOS_AUTOR_IMPORT                    BIGINT,
                COD_EMPRESA                               BIGINT,
                COD_UNIDADE                               BIGINT,
                PLACA_EDITAVEL                            VARCHAR(255),
                NUMERO_FOGO_PNEU_EDITAVEL                 VARCHAR(255),
                NOMENCLATURA_POSICAO_EDITAVEL             VARCHAR(255),
                PLACA_FORMATADA_VINCULO                   VARCHAR(7),
                NUMERO_FOGO_PNEU_FORMATADO_VINCULO        VARCHAR(255),
                NOMENCLATURA_POSICAO_FORMATADA_VINCULO    VARCHAR(255),
                STATUS_VINCULO_REALIZADO                  BOOLEAN,
                ERROS_ENCONTRADOS                         VARCHAR(255),
                USUARIO_UPDATE                            VARCHAR(255),
                PRIMARY KEY (CODIGO),
                FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
            );', V_NOME_TABELA_IMPORT);

    -- Trigger para verificar planilha e realizar o import de veículos.
    EXECUTE FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_VINCULO_VEICULO_PNEU ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_VINCULO_VEICULO_PNEU
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VINCULO_VEICULO_PNEU_CONFERE_PLANILHA_VINCULO();',
                   V_NOME_TABELA_IMPORT,
                   V_NOME_TABELA_IMPORT);

    -- Cria audit para a tabela.
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_VINCULO_VEICULO_PNEU ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_VINCULO_VEICULO_PNEU
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                   V_NOME_TABELA_IMPORT,
                   V_NOME_TABELA_IMPORT);

    -- Garante select, update para o usuário que está realizando o import.
    EXECUTE FORMAT(
            'GRANT SELECT, UPDATE ON IMPLANTACAO.%I TO %I;',
            V_NOME_TABELA_IMPORT,
            (SELECT INTERNO.FUNC_BUSCA_NOME_USUARIO_BANCO(F_USUARIO)));

    -- Retorna nome da tabela.
    SELECT V_NOME_TABELA_IMPORT INTO NOME_TABELA_CRIADA;
END ;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Insere os dados na tabela dados_autor_import, após isso chama a function correspondente ao tipo de import, e
-- retorna o código que foi inserido na tabela de dados_autor_import e o nome da tabela de pré-import criada.
--
-- Pré-requisitos:
-- function func_veiculo_import_cria_tabela_import criada.
-- function func_pneu_import_cria_tabela_import criada.
-- function func_colaborador_import_cria_tabela_import criada.
-- function func_vinculo_veiculo_pneu_cria_tabela_vinculo criada.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
-- 2019-12-13 -> Mescla para ser utilizada em qualquer tipo de import (thaisksf - PL-2320).
-- 2020-08-31 -> Adiciona tipo 'vinculo' para criação de tabela (thaisksf - PL-2771).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_IMPORT_INSERE_DADOS_AUTOR(F_COD_EMPRESA BIGINT,
                                                                      F_COD_UNIDADE BIGINT,
                                                                      F_USUARIO TEXT,
                                                                      F_TIPO_IMPORT TEXT)
    RETURNS TABLE
            (
                COD_DADOS_AUTOR_IMPORT BIGINT,
                NOME_TABELA_IMPORT     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATA_HORA_IMPORT       TIMESTAMP WITH TIME ZONE := NOW();
    DATA_IMPORT            DATE                     := CURRENT_DATE;
    COD_DADOS_AUTOR_IMPORT BIGINT;
    NOME_TABELA_CRIADA     TEXT;
BEGIN
    -- VERIFICA SE UNIDADE DESTINO EXISTE E SE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    INSERT INTO IMPLANTACAO.DADOS_AUTOR_IMPORT (COD_EMPRESA, COD_UNIDADE, TIPO_IMPORT, USUARIO, DATA_HORA)
    VALUES (F_COD_EMPRESA, F_COD_UNIDADE, F_TIPO_IMPORT, F_USUARIO, DATA_HORA_IMPORT) RETURNING CODIGO
        INTO COD_DADOS_AUTOR_IMPORT;

    -- Verificamos se o insert funcionou.
    IF COD_DADOS_AUTOR_IMPORT > 0
    THEN
        CASE
            WHEN F_TIPO_IMPORT = 'PNEU'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_PNEU_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'VEICULO'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'COLABORADOR'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_COLABORADOR_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'VINCULO'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_VINCULO_VEICULO_PNEU_CRIA_TABELA_VINCULO(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            ELSE
                PERFORM THROW_GENERIC_ERROR(
                                'Não foi possível identificar o tipo de import, verifique para tentar novamente.');
            END CASE;
    ELSE
        PERFORM THROW_GENERIC_ERROR('Não foi possível inserir os dados do autor de import, tente novamente');
    END IF;

    RETURN QUERY SELECT COD_DADOS_AUTOR_IMPORT, NOME_TABELA_CRIADA;
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
-- 2020-08-27 -> Function criada (thaisksf - PL-2771).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VINCULO_VEICULO_PNEU_INSERE_PLANILHA_VINCULO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                                         F_NOME_TABELA_IMPORT TEXT,
                                                                                         F_COD_EMPRESA BIGINT,
                                                                                         F_COD_UNIDADE BIGINT,
                                                                                         F_JSON_VINCULO JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                PLACA_EDITAVEL,
                                                NUMERO_FOGO_PNEU_EDITAVEL,
                                                NOMENCLATURA_POSICAO_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT,
                          (SRC ->> ''numeroFogo'') :: TEXT,
                          (SRC ->> ''nomenclatura'') :: TEXT
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_EMPRESA,
                   F_COD_UNIDADE,
                   F_JSON_VINCULO);
END
$$;