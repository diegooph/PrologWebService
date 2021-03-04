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