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

    -- Trigger para verificar planilha e realizar o import de pneus.
    EXECUTE FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_PNEU ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_PNEU
                    BEFORE INSERT OR UPDATE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU();',
                   F_NOME_TABELA_IMPORT,
                   F_NOME_TABELA_IMPORT);

    -- Cria audit para a tabela.
    EXECUTE FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_PNEU ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_PNEU
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