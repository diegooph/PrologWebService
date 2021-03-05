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