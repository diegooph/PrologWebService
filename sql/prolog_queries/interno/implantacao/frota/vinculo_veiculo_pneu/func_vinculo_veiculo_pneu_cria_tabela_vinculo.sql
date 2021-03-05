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