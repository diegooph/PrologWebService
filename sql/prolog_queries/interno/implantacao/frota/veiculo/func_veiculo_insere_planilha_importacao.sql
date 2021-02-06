-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Insere os dados na tabela dinâmica criada através da function: func_import_cria_tabela_import.
--
-- Pré-requisitos:
-- function func_import_cria_tabela_import criada.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
-- 2020-05-19 -> Adiciona a coluna identificador_frota no insert de import (thaisksf - PL-2712).
-- 2020-05-26 -> Adiciona cod_empresa no insert da planilha (thaisksf - PL-2511).
-- 2020-11-23 -> Adiciona possui_hubodometro no insert da planilha (thaisksf - PL-3288).
create or replace function implantacao.func_veiculo_insere_planilha_importacao(f_cod_dados_autor_import bigint,
                                                                               f_nome_tabela_import text,
                                                                               f_cod_empresa bigint,
                                                                               f_cod_unidade bigint,
                                                                               f_json_veiculos jsonb)
    returns void
    language plpgsql
as
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