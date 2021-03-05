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