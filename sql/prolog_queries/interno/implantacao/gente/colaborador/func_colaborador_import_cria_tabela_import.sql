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