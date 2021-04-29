BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--####################################    FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS    #################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                             F_CPF_COLABORADOR BIGINT,
                                                                             F_COD_UNIDADE_DESTINO INTEGER,
                                                                             F_COD_EMPRESA_DESTINO BIGINT,
                                                                             F_COD_SETOR_DESTINO BIGINT,
                                                                             F_COD_EQUIPE_DESTINO BIGINT,
                                                                             F_COD_FUNCAO_DESTINO INTEGER,
                                                                             F_MATRICULA_TRANS INTEGER DEFAULT NULL,
                                                                             F_MATRICULA_AMBEV INTEGER DEFAULT NULL,
                                                                             F_NIVEL_PERMISSAO INTEGER DEFAULT 0,
                                                                             OUT AVISO_COLABORADOR_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    EMPRESA_ORIGEM BIGINT := (SELECT U.COD_EMPRESA AS EMPRESA_ORIGEM
                              FROM UNIDADE U
                              WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
BEGIN
    -- VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS
    PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

    -- VERIFICA SE UNIDADE DESTINO EXISTE E SE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

    --VERIFICA SE O COLABORADOR ESTÁ CADASTRADO E SE PERTENCE A UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE_ORIGEM, F_CPF_COLABORADOR);

    -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

    -- VERIFICA SE A EQUIPE EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EQUIPE_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_EQUIPE_DESTINO);

    -- VERIFICA SE A FUNÇÃO EXISTE NA EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_CARGO_EXISTE(F_COD_EMPRESA_DESTINO, F_COD_FUNCAO_DESTINO);

    -- VERIFICA SE PERMISSÃO EXISTE
    IF NOT EXISTS(SELECT P.CODIGO FROM PERMISSAO P WHERE P.CODIGO = F_NIVEL_PERMISSAO)
    THEN
        RAISE EXCEPTION 'Não existe permissão com o código: %', F_NIVEL_PERMISSAO;
    END IF;

    -- TRANSFERE COLABORADOR
    UPDATE COLABORADOR
    SET COD_UNIDADE     = F_COD_UNIDADE_DESTINO,
        COD_EMPRESA     = F_COD_EMPRESA_DESTINO,
        COD_SETOR       = F_COD_SETOR_DESTINO,
        COD_EQUIPE      = F_COD_EQUIPE_DESTINO,
        COD_FUNCAO      = F_COD_FUNCAO_DESTINO,
        MATRICULA_TRANS = F_MATRICULA_TRANS,
        MATRICULA_AMBEV = F_MATRICULA_AMBEV,
        COD_PERMISSAO   = F_NIVEL_PERMISSAO
    WHERE CPF = F_CPF_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM;

    SELECT ('COLABORADOR: '
                || (SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR)
                || ' , TRANSFERIDO PARA A UNIDADE: '
        || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO))
    INTO AVISO_COLABORADOR_TRANSFERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--##############################                 CRIA MIGRATION            #############################################
--######################################################################################################################
--ṔL-2367

-- Dropa e recria a function de busca de colaboradores por unidade para adicionar o TZ do colaborador
DROP FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(BIGINT,BOOLEAN);

CREATE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_STATUS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO             BIGINT,
                CPF                BIGINT,
                PIS                CHARACTER VARYING,
                MATRICULA_AMBEV    INTEGER,
                MATRICULA_TRANS    INTEGER,
                DATA_NASCIMENTO    DATE,
                DATA_ADMISSAO      DATE,
                DATA_DEMISSAO      DATE,
                STATUS_ATIVO       BOOLEAN,
                NOME_COLABORADOR   TEXT,
                NOME_EMPRESA       TEXT,
                COD_EMPRESA        BIGINT,
                LOGO_THUMBNAIL_URL TEXT,
                NOME_REGIONAL      TEXT,
                COD_REGIONAL       BIGINT,
                NOME_UNIDADE       TEXT,
                COD_UNIDADE        BIGINT,
                NOME_EQUIPE        TEXT,
                COD_EQUIPE         BIGINT,
                NOME_SETOR         TEXT,
                COD_SETOR          BIGINT,
                COD_FUNCAO         BIGINT,
                NOME_FUNCAO        TEXT,
                PERMISSAO          BIGINT,
                TZ_UNIDADE         TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       C.CPF,
       C.PIS,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       EM.NOME         AS NOME_EMPRESA,
       EM.CODIGO       AS COD_EMPRESA,
       EM.LOGO_THUMBNAIL_URL,
       R.REGIAO        AS NOME_REGIONAL,
       R.CODIGO        AS COD_REGIONAL,
       U.NOME          AS NOME_UNIDADE,
       U.CODIGO        AS COD_UNIDADE,
       EQ.NOME         AS NOME_EQUIPE,
       EQ.CODIGO       AS COD_EQUIPE,
       S.NOME          AS NOME_SETOR,
       S.CODIGO        AS COD_SETOR,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       C.COD_PERMISSAO AS PERMISSAO,
       U.TIMEZONE      AS TZ_UNIDADE
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE C.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_STATUS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_STATUS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;

-- Dropa e recria a function de busca de colaboradores por empresa para adicionar o TZ do colaborador
DROP FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(BIGINT,BOOLEAN);

CREATE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO             BIGINT,
                CPF                BIGINT,
                PIS                CHARACTER VARYING,
                MATRICULA_AMBEV    INTEGER,
                MATRICULA_TRANS    INTEGER,
                DATA_NASCIMENTO    DATE,
                DATA_ADMISSAO      DATE,
                DATA_DEMISSAO      DATE,
                STATUS_ATIVO       BOOLEAN,
                NOME_COLABORADOR   TEXT,
                NOME_EMPRESA       TEXT,
                COD_EMPRESA        BIGINT,
                LOGO_THUMBNAIL_URL TEXT,
                NOME_REGIONAL      TEXT,
                COD_REGIONAL       BIGINT,
                NOME_UNIDADE       TEXT,
                COD_UNIDADE        BIGINT,
                NOME_EQUIPE        TEXT,
                COD_EQUIPE         BIGINT,
                NOME_SETOR         TEXT,
                COD_SETOR          BIGINT,
                COD_FUNCAO         BIGINT,
                NOME_FUNCAO        TEXT,
                PERMISSAO          BIGINT,
                TZ_UNIDADE         TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       C.CPF,
       C.PIS,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       EM.NOME         AS NOME_EMPRESA,
       EM.CODIGO       AS COD_EMPRESA,
       EM.LOGO_THUMBNAIL_URL,
       R.REGIAO        AS NOME_REGIONAL,
       R.CODIGO        AS COD_REGIONAL,
       U.NOME          AS NOME_UNIDADE,
       U.CODIGO        AS COD_UNIDADE,
       EQ.NOME         AS NOME_EQUIPE,
       EQ.CODIGO       AS COD_EQUIPE,
       S.NOME          AS NOME_SETOR,
       S.CODIGO        AS COD_SETOR,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       C.COD_PERMISSAO AS PERMISSAO,
       U.TIMEZONE      AS TZ_UNIDADE
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE C.COD_EMPRESA = F_COD_EMPRESA
  AND CASE
          WHEN F_STATUS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_STATUS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################################### ADICIONA COLUNA NA TABELA DADOS_AUTOR_IMPORT ####################################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE IF NOT EXISTS IMPLANTACAO.DADOS_AUTOR_IMPORT
(
    CODIGO      BIGSERIAL,
    COD_EMPRESA BIGINT,
    COD_UNIDADE BIGINT,
    USUARIO     VARCHAR(255),
    DATA_HORA   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (CODIGO),
    FOREIGN KEY (COD_EMPRESA, COD_UNIDADE) REFERENCES UNIDADE (COD_EMPRESA, CODIGO)
);

-- Altera o tipo da coluna do usuário para TEXT
ALTER TABLE IMPLANTACAO.DADOS_AUTOR_IMPORT ALTER COLUMN USUARIO TYPE TEXT USING USUARIO::TEXT;

-- Adiciona a coluna e constraint para o tipo de import
ALTER TABLE IMPLANTACAO.DADOS_AUTOR_IMPORT ADD COLUMN TIPO_IMPORT VARCHAR(255);
ALTER TABLE IMPLANTACAO.DADOS_AUTOR_IMPORT ADD CONSTRAINT CHECK_TIPO_IMPORT_NOT_NULL
    CHECK (TIPO_IMPORT IS NOT NULL) NOT VALID;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################# MODIFICA FUNCTION PARA USAR EM TODOS OS IMPORTS ####################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION IF EXISTS IMPLANTACAO.FUNC_VEICULO_IMPORT_INSERE_DADOS_AUTOR(
f_cod_empresa BIGINT,
f_cod_unidade BIGINT,
f_usuario TEXT);
DROP FUNCTION IF EXISTS IMPLANTACAO.FUNC_PNEU_IMPORT_INSERE_DADOS_AUTOR(
f_cod_empresa BIGINT,
f_cod_unidade BIGINT,
f_usuario TEXT);

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Insere os dados na tabela dados_autor_import, após isso chama a function correspondente ao tipo de import, e
-- retorna o código que foi inserido na tabela de dados_autor_import e o nome da tabela de pré-import criada.
--
-- Pré-requisitos:
-- function func_veiculo_import_cria_tabela_import criada.
-- function func_pneu_import_cria_tabela_import criada.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
-- 2019-12-13 -> Mescla para ser utilizada em qualquer tipo de import (thaisksf - PL-2320)
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
            WHEN F_TIPO_IMPORT = 'COLABORADOR' --Preparado para quando for criado o import de colaborador
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_COLABORADOR_IMPORT_CRIA_TABELA_IMPORT(
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
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################## FUNC QUE CRIA TABELA PRÉ-IMPORT #############################################
--######################################################################################################################
--######################################################################################################################
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
                        'P_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' || F_ANO || '_' ||
                        F_MES || '_' || F_DIA || '_' || F_USUARIO)));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                                   BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT                   BIGINT,
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
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO),
            FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE (CODIGO)
        );', F_NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE PNEUS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_PNEU ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_PNEU
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU()',
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
      'grant update on implantacao.% to prolog_user_natan;', F_NOME_TABELA_IMPORT
      );

    --RETORNA NOME DA TABELA
    SELECT F_NOME_TABELA_IMPORT INTO F_NOME_TABELA_CRIADA;
END ;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################ FUNCTION PARA INSERIR NA TABELA CRIADA PARA O IMPORT DE PNEUS ###########################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_PNEU_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                            F_NOME_TABELA_IMPORT TEXT,
                                                                            F_COD_UNIDADE BIGINT,
                                                                            F_JSON_PNEUS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
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
                   F_COD_UNIDADE,
                   F_JSON_PNEUS);
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################  CRIA REGEX PARA REMOVER CHARACTERES NÃO NUMÉRICOS ##############################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION REMOVE_NON_NUMERIC_CHARACTERS(F_TEXTO TEXT)
    RETURNS TEXT
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Remove letras, characteres especiais e espaços.
    -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
    RETURN REGEXP_REPLACE(F_TEXTO, '[^0-9]+', '', 'g');
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################## FUNC PARA CONFERIR PLANILHA E IMPORTAR OS DADOS CORRETOS PARA O BANCO #######################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA                              BIGINT;
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
    F_PNEU_NOVO_NUNCA_RODADO_ARRAY             TEXT[] := ('{"SIM", "OK", "TRUE"}');
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
        END IF;
        F_COD_EMPRESA := (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE);
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NUMERO_FOGO_FORMATADO_IMPORT = UPPER(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.NUMERO_FOGO_EDITAVEL));
        NEW.MARCA_FORMATADA_IMPORT = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.DOT_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.DOT_EDITAVEL);
        NEW.DIMENSAO_FORMATADA_IMPORT := REMOVE_ALL_SPACES(NEW.DIMENSAO_EDITAVEL);
        NEW.MARCA_BANDA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_BANDA_EDITAVEL);
        NEW.MODELO_BANDA_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_BANDA_EDITAVEL);

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
                        AND P.COD_EMPRESA = F_COD_EMPRESA
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
                            AND P.COD_EMPRESA = F_COD_EMPRESA
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
            SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT)
                MAP.CODIGO                                                        AS COD_MARCA_BANCO,
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
                    WHERE MOP.COD_EMPRESA = F_COD_EMPRESA
                    GROUP BY F_MARCA_MODELO, MOP.NOME, MOP.CODIGO
                    ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;

                    -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.
                    IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE)
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
                            VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, F_COD_EMPRESA,
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
                SELECT DISTINCT ON (NEW.DIMENSAO_FORMATADA_IMPORT)
                    DP.CODIGO                                                   AS COD_DIMENSAO_BANCO,
                    MAX(func_gera_similaridade(NEW.DIMENSAO_FORMATADA_IMPORT,
                        CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO)))       AS SIMILARIDADE_DIMENSAO
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
        END IF ;

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
                SELECT DISTINCT ON (NEW.MARCA_BANDA_FORMATADA_IMPORT)
                    MAB.CODIGO                                                              AS COD_MARCA_BANDA_BANCO,
                    MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_BANDA_FORMATADA_IMPORT, MAB.NOME)) AS SIMILARIEDADE_MARCA_BANDA
                INTO F_COD_MARCA_BANDA_BANCO, F_SIMILARIDADE_MARCA_BANDA
                FROM MARCA_BANDA MAB WHERE MAB.COD_EMPRESA = F_COD_EMPRESA
                GROUP BY NEW.MARCA_BANDA_FORMATADA_IMPORT, NEW.MARCA_BANDA_EDITAVEL, MAB.NOME, MAB.CODIGO
                ORDER BY NEW.MARCA_BANDA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA_BANDA DESC;

                 F_MARCA_MODELO_BANDA := CONCAT(F_COD_MARCA_BANDA_BANCO, NEW.MODELO_BANDA_FORMATADO_IMPORT);
                -- Se a similaridade da marca de banda for menor que o exigido: Cadastra.
                IF (F_SIMILARIDADE_MARCA_BANDA < F_VALOR_SIMILARIDADE)
                THEN
                    INSERT INTO MARCA_BANDA (NOME, COD_EMPRESA)
                    VALUES (NEW.MARCA_BANDA_EDITAVEL, F_COD_EMPRESA) RETURNING CODIGO INTO F_COD_MARCA_BANDA_BANCO;
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
                    WHERE MOB.COD_EMPRESA = F_COD_EMPRESA
                    GROUP BY F_MARCA_MODELO_BANDA, MOB.NOME, MOB.CODIGO
                    ORDER BY F_MARCA_MODELO_BANDA, SIMILARIEDADE_MODELO_BANDA DESC;

                    -- Se a similaridade do modelo de banda for menor do que o exigido: cadastra novo modelo de banda.
                    IF (F_SIMILARIDADE_MODELO_BANDA < F_VALOR_SIMILARIDADE)
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
                            VALUES (NEW.MODELO_BANDA_EDITAVEL, F_COD_MARCA_BANDA_BANCO, F_COD_EMPRESA, NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT, NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT)
                                    RETURNING CODIGO INTO F_COD_MODELO_BANDA_BANCO;
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
            INSERT INTO PNEU_DATA (CODIGO_CLIENTE,
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
                    F_COD_EMPRESA,
                    NEW.COD_UNIDADE) RETURNING CODIGO INTO F_COD_PNEU;


            IF(NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
                THEN
                    SELECT PTS.CODIGO AS CODIGO FROM PNEU_TIPO_SERVICO AS PTS
                    WHERE PTS.COD_EMPRESA IS NULL
                    AND PTS.STATUS_ATIVO = TRUE
                    AND PTS.INCREMENTA_VIDA = TRUE
                    AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE INTO F_COD_TIPO_SERVICO;

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
                            (NEW.VIDA_ATUAL_FORMATADA_IMPORT -1),
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
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################  CRIA REGEX PARA REMOVER CHARACTERES NÃO NUMÉRICOS ##############################
--############################################### EXCETO VÍRGULA E PONTO FINAL #########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(F_TEXTO TEXT)
    RETURNS TEXT
    IMMUTABLE STRICT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Remove letras, characteres especiais e espaços, exceto vírgula e ponto final..
    -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
    RETURN REGEXP_REPLACE(F_TEXTO, '[^-,.0-9]+', '', 'g');
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################## CORRIGE FUNCTION QUE CRIA TABELA DE IMPORT DE VEÍCULO #################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION IF EXISTS IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(
F_COD_EMPRESA BIGINT,
F_COD_UNIDADE BIGINT,
F_USUARIO TEXT,
F_DATA DATE,
NOME_TABELA_CRIADA TEXT);

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
            CODIGO                  BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT  BIGINT,
            COD_UNIDADE_EDITAVEL    BIGINT,
            PLACA_EDITAVEL          VARCHAR(255),
            KM_EDITAVEL             BIGINT,
            MARCA_EDITAVEL          VARCHAR(255),
            MODELO_EDITAVEL         VARCHAR(255),
            TIPO_EDITAVEL           VARCHAR(255),
            QTD_EIXOS_EDITAVEL      VARCHAR(255),
            PLACA_FORMATADA_IMPORT  VARCHAR(255),
            MARCA_FORMATADA_IMPORT  VARCHAR(255),
            MODELO_FORMATADO_IMPORT VARCHAR(255),
            TIPO_FORMATADO_IMPORT   VARCHAR(255),
            STATUS_IMPORT_REALIZADO BOOLEAN,
            ERROS_ENCONTRADOS       VARCHAR(255),
            USUARIO_UPDATE          VARCHAR(255),
            PRIMARY KEY (CODIGO),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO),
            FOREIGN KEY (COD_UNIDADE_EDITAVEL) REFERENCES UNIDADE (CODIGO)
        );', NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE VEÍCULOS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO()',
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
      'grant update on implantacao.% to prolog_user_natan;', NOME_TABELA_IMPORT
      );

    --RETORNA NOME DA TABELA
    SELECT NOME_TABELA_IMPORT INTO NOME_TABELA_CRIADA;
END ;
$$;
--######################################################################################################################
--######################################################################################################################

END TRANSACTION;