CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                               F_NOME_TABELA_IMPORT TEXT,
                                                                               F_COD_EMPRESA BIGINT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_JSON_VEICULOS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
begin
    execute FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
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
                                                IDENTIFICADOR_FROTA_EDITAVEL,
                                                POSSUI_HUBODOMETRO_EDITAVEL  )
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
                          (SRC ->> ''identificadorFrota'') :: TEXT                            AS IDENTIFICADOR_FROTA,
                          (SRC ->> ''possuiHubodometro'') :: TEXT                             AS POSSUI_HUBODOMETRO
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   f_nome_tabela_import,
                   f_cod_dados_autor_import,
                   f_cod_empresa,
                   f_cod_unidade,
                   f_json_veiculos);
end
$$;