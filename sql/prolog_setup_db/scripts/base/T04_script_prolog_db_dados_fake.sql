--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria a empresa e suas unidades.
INSERT INTO public.empresa (codigo, nome, logo_thumbnail_url, data_hora_cadastro)
VALUES (3, 'Zalf Sistemas', 'https://s3-sa-east-1.amazonaws.com/empresas-logos/zalf_logo.png',
        '2019-08-18 10:47:00.210000');
SELECT setval('empresa_codigo_seq', 3, true);

INSERT INTO public.unidade (codigo, nome, total_colaboradores, cod_regional, cod_empresa, timezone, data_hora_cadastro,
                            status_ativo, cod_auxiliar)
VALUES (5, 'Unidade Teste Zalf ', 0, 1, 3, 'America/Sao_Paulo', '2019-01-01 10:00:00.000000', false, null);
INSERT INTO public.unidade (codigo, nome, total_colaboradores, cod_regional, cod_empresa, timezone, data_hora_cadastro,
                            status_ativo, cod_auxiliar)
VALUES (215, 'Unidade de testes', 0, 1, 3, 'America/Sao_Paulo', '2019-08-18 10:47:00.210000', true, '1:1');
SELECT setval('unidade_codigo_seq', 215, true);

INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (5, 1);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (5, 2);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (5, 3);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (5, 5);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (5, 4);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (215, 1);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (215, 2);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (215, 3);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (215, 4);
INSERT INTO public.unidade_pilar_prolog (cod_unidade, cod_pilar)
VALUES (215, 5);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do colaborador e insere colaboradores.
INSERT INTO public.funcao (codigo, nome, cod_empresa)
VALUES (159, 'Motorista', 3);
INSERT INTO public.funcao (codigo, nome, cod_empresa)
VALUES (951, 'Gerente', 3);
SELECT setval('funcao_data_codigo_seq', 951, true);

INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 10, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 11, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 12, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 13, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 14, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 15, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 16, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 17, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 18, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 19, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 100, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 110, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 111, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 112, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 113, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 114, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 115, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 116, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 117, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 118, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 119, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 121, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 122, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 123, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 124, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 140, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 142, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 143, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 144, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 319, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 324, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 32, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 328, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 310, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 316, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 344, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 340, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 338, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 341, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 336, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 342, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 337, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 311, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 317, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 314, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 315, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 331, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 322, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 34, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 35, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 333, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 334, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 335, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 321, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 37, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 36, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 320, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 326, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 330, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 38, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 39, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 327, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 332, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 318, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 323, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 30, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 501, 5);

INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 100, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 140, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 117, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 18, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 114, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 118, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 13, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 113, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 10, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 121, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 12, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 112, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 11, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 141, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 144, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 143, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 124, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 123, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 142, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 17, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 135, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 133, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 15, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 110, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 134, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 116, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 19, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 111, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 119, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 131, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 132, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 130, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 14, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 16, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 115, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 122, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 319, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 324, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 32, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 329, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 328, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 310, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 325, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 316, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 338, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 341, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 340, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 344, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 337, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 342, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 336, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 313, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 311, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 317, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 322, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 315, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 331, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 314, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 333, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 335, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 334, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 34, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 35, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 321, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 37, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 326, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 320, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 330, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 36, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 39, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 332, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 327, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 38, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 343, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 323, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 30, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 318, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 942, 336, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 336, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 501, 5);

INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (13, 5, 'Administrativo');
INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (356, 5, 'Frota');
SELECT setval('setor_codigo_seq', 356, true);

INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (7, 'Sala 1', 5);
INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (38, 'Sala 2', 5);
SELECT setval('equipe_codigo_seq', 38, true);


INSERT INTO public.colaborador_data (cpf, matricula_ambev, matricula_trans, data_nascimento, data_admissao,
                                     data_demissao, status_ativo, nome, cod_equipe, cod_funcao, cod_unidade,
                                     cod_permissao, cod_empresa, cod_setor, pis, data_hora_cadastro, codigo,
                                     cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES (3383283194, 4, null, '2018-02-26', '2018-02-20', null, true, 'John Doe', 7, 159, 5, 3, 3, 356, '', null,
        2272, 215, false, null, null);
INSERT INTO public.colaborador_data (cpf, matricula_ambev, matricula_trans, data_nascimento, data_admissao,
                                     data_demissao, status_ativo, nome, cod_equipe, cod_funcao, cod_unidade,
                                     cod_permissao, cod_empresa, cod_setor, pis, data_hora_cadastro, codigo,
                                     cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES (53439478084, null, 500, '1991-10-26', '2019-08-19', '2019-10-30', true, 'Maria Silva', 38, 951, 215, 1, 3, 13,
        '88558403410', '2019-08-19 17:04:46.675294', 30745, 215, false, null, null);
SELECT setval('colaborador_data_codigo_seq', 30745, true);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do veículo e insere veículos.
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (13, '12 BAIAS', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (63, 'TOCO', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (64, 'TRUCK', true, 2, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (65, 'CARRETA', true, 3, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (411, 'TOCO', true, null, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (579, 'Carreta 4 Eixos', true, 9, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (610, 'EMPILHADEIRA', true, 6, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (617, 'Truck Elétrico', true, 10, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (633, 'Fiscalização', true, 8, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (634, 'Leitura', true, 12, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (635, 'Caminhões', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (641, 'Toco (ROTA)', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (642, 'Carreta 3 eixos', true, 3, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (643, 'Toco', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (665, 'teste', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (702, 'MOTO', true, 12, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (774, 'OF 1721PL', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (775, 'VW-16-210PL', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (776, 'VW-8140-PL', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (973, 'Prancha 6 eixos', true, 20, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (974, 'Prancha 8 eixos', true, 21, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (975, 'Prancha 4 eixos', true, 22, 3);
SELECT setval('veiculo_tipo_codigo_seq', 975, true);

INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (118, 'Carreta 4 eixos', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (119, 'VW 10160', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (120, 'VW 2220', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (121, 'VW 17190', 4, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1015, 'ATEGO 2426', 4, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1069, 'Saveiro', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1070, 'BROS', 44, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1076, 'Modelo 1', 3, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1077, 'Modelo1', 4, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1187, 'vw 17190', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1207, 'FH16', 2, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1491, 'SUZ', 53, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1709, 'OF-1721PL', 4, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1710, 'VW-16-210PL', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (1711, 'VW-8140PL', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (2171, '1241254', 17, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (2172, 'dasdas1', 12, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (2338, 'tr 123', 6, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (2339, 'tTEACX', 20, 3);
SELECT setval('modelo_veiculo_codigo_seq', 2339, true);

INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('AAA1111', 5, 897641, true, 63, 119, 1, '2019-01-03 17:30:13.496945', 5, 354, true,
        '2019-10-23 17:31:29.990601', 'prolog_user_thais', 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0006', 5, 30984, true, 64, 118, 2, null, 5, 705, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0014', 5, 54822, false, 63, 120, 1, null, 5, 1593, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('GGG2222', 5, 0, true, 65, 118, 5, '2018-06-03 20:36:30.741481', 5, 2257, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('FFF2222', 5, 322232, true, 64, 120, 2, '2018-05-17 20:04:23.105235', 5, 2385, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('MMM0001', 5, 246996, true, 65, 121, 4, null, 5, 2901, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0020', 5, 36370, false, 63, 120, 1, null, 5, 3031, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0021', 5, 25837, false, 63, 120, 1, null, 5, 3032, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0022', 5, 25261, false, 64, 121, 2, null, 5, 3033, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0023', 5, 50746, false, 64, 118, 2, null, 5, 3034, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0004', 5, 24284, true, 64, 118, 2, null, 5, 3103, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0019', 5, 32529, false, 64, 121, 2, null, 5, 3118, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0042', 5, 27057, false, 64, 121, 2, null, 5, 3119, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0003', 5, 4848, true, 64, 118, 2, null, 5, 3121, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0001', 5, 869900, true, 63, 120, 1, null, 5, 3195, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0015', 5, 28541, false, 63, 120, 1, null, 5, 3513, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0024', 5, 200, false, 64, 118, 2, null, 5, 3514, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0041', 5, 26411, false, 64, 121, 2, null, 5, 3516, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0043', 5, 37856, false, 64, 121, 2, null, 5, 3517, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0044', 5, 27312, false, 64, 121, 2, null, 5, 3518, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0046', 5, 26339, false, 64, 121, 2, null, 5, 3519, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0012', 5, 23780, false, 63, 120, 1, null, 5, 3520, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0025', 5, 1202, false, 64, 118, 2, null, 5, 3521, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0026', 5, 24156, false, 64, 118, 2, null, 5, 3522, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0027', 5, 23473, false, 63, 120, 1, null, 5, 3523, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0028', 5, 21132, false, 63, 120, 1, null, 5, 3524, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0029', 5, 27991, false, 64, 121, 2, null, 5, 3525, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0030', 5, 24613, false, 64, 121, 2, null, 5, 3526, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0031', 5, 25003, false, 64, 118, 2, null, 5, 3527, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0032', 5, 22292, false, 64, 118, 2, null, 5, 3528, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0033', 5, 29423, false, 64, 121, 2, null, 5, 3529, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0011', 103, 41969, true, 63, 120, 1, null, 103, 3530, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0047', 5, 23401, false, 64, 121, 2, null, 5, 3531, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0048', 5, 35794, false, 64, 121, 2, null, 5, 3532, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0049', 5, 20924, false, 64, 119, 2, null, 5, 3533, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0034', 5, 18056, false, 64, 118, 2, null, 5, 3534, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0016', 5, 21449, false, 64, 118, 2, null, 5, 3535, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0017', 5, 31254, false, 64, 118, 2, null, 5, 3536, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0018', 5, 25952, false, 64, 118, 2, null, 5, 3537, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0035', 5, 45109, false, 64, 121, 2, null, 5, 3538, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0050', 5, 94511, false, 63, 120, 1, null, 5, 3539, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0051', 5, 342, false, 63, 120, 1, null, 5, 3540, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0013', 5, 25888, false, 63, 120, 1, null, 5, 3541, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0036', 5, 19263, false, 64, 119, 2, null, 5, 3542, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0037', 5, 23513, false, 64, 118, 2, null, 5, 3543, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0038', 5, 27299, false, 64, 121, 2, null, 5, 3544, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0039', 103, 30778, false, 64, 121, 2, null, 103, 3545, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0045', 103, 24016, false, 64, 121, 2, null, 103, 3546, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0040', 5, 48110, false, 64, 121, 2, null, 5, 3547, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0007', 5, 39072, true, 63, 121, 2, null, 5, 3548, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0008', 5, 22977, true, 64, 118, 2, null, 5, 3549, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0009', 103, 45692, true, 63, 120, 1, null, 103, 3552, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0010', 103, 28310, true, 63, 120, 1, null, 103, 3567, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0005', 5, 57742, true, 63, 120, 1, null, 5, 3568, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0002', 5, 90300, true, 63, 120, 1, null, 5, 4035, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('99EMP55', 5, 45491, true, 65, 119, 1, '2019-03-21 17:27:16.358036', 5, 5549, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO1234', 103, 911, true, 579, 119, 1, '2019-04-13 21:03:02.252587', 103, 6482, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('AAA1232', 103, 10061, true, 579, 121, 1, '2019-04-22 19:36:24.037065', 103, 6698, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('MEL4999', 103, 123, true, 63, 119, 1, '2019-05-15 19:40:42.463701', 103, 7496, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('TST0001', 5, 100110, true, 64, 120, 1, '2019-05-16 13:49:25.228329', 5, 7600, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0179', 179, 150498, true, 63, 119, 1, '2019-06-04 16:54:54.671068', 179, 7944, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0180', 179, 214561, false, 63, 119, 1, '2019-06-04 16:54:54.671068', 179, 7945, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0181', 179, 111321, true, 63, 121, 1, '2019-06-04 16:54:54.671068', 179, 7946, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0182', 179, 152292, true, 63, 119, 1, '2019-06-04 16:54:54.671068', 179, 7947, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0183', 179, 11506, true, 63, 120, 1, '2019-06-04 16:54:54.671068', 179, 7948, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0184', 179, 106965, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7949, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0185', 179, 3194, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7950, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0186', 179, 164638, true, 64, 121, 2, '2019-06-04 16:54:54.671068', 179, 7951, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0187', 179, 174005, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7952, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0188', 179, 134254, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7953, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0189', 179, 139480, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7954, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0190', 179, 137997, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 179, 7955, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0191', 179, 170845, true, 610, 120, 1, '2019-06-04 16:54:54.671068', 179, 7956, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0192', 179, 130636, true, 610, 120, 1, '2019-06-04 16:54:54.671068', 179, 7957, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0193', 179, 147290, true, 610, 120, 1, '2019-06-04 16:54:54.671068', 179, 7958, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0194', 179, 12943, true, 610, 120, 1, '2019-06-04 16:54:54.671068', 179, 7959, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRI1010', 5, 1000, true, 63, 119, 1, '2019-07-09 14:30:15.555985', 5, 8310, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('QJI5667', 5, 12000, true, 633, 1069, 1, '2019-08-13 16:32:39.338470', 5, 8983, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('LLL1234', 5, 25000, true, 64, 1076, 1, '2019-08-18 13:40:49.008644', 5, 9369, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('ABC1234', 5, 45410, true, 63, 1077, 1, '2019-08-18 14:00:53.196949', 5, 9370, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('MEE0499', 5, 310100, true, 641, 1207, 1, '2019-09-16 18:59:02.113893', 5, 10626, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('MOT1111', 215, 1001, true, 702, 1491, 1, '2019-09-26 16:44:54.162804', 215, 14334, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('MEE1313', 5, 10090, true, 64, 1207, 1, '2019-10-16 12:25:19.653003', 5, 17462, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('31', 215, 11331, true, 579, 2172, 1, '2019-11-13 12:29:03.160291', 215, 23364, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('ABC2222', 215, 1000, true, 610, 119, 1, '2019-11-17 15:18:49.226438', 215, 23410, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('1', 215, 1501, true, 610, 2171, 1, '2019-11-18 12:07:49.163321', 215, 23411, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('''ASD134', 215, 1, true, 579, 2338, 1, '2019-12-03 19:46:07.612884', 215, 24327, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('''AVA312', 215, 1, true, 617, 2339, 1, '2019-12-03 19:47:06.484230', 215, 24328, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('JHT1000', 5, 1000, true, 973, 119, 1, '2020-01-20 21:01:14.198201', 5, 41169, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('DIA0021', 5, 1000, true, 974, 1076, 1, '2020-01-21 17:13:51.711592', 5, 41170, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('DIA0020', 215, 1000, true, 975, 1207, 1, '2020-01-21 19:15:31.971416', 5, 41171, false, null, null, 3);
SELECT setval('veiculo_data_codigo_seq', 41171, true);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do pneu e insere pneus.
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (28, 'H2R', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (129, 'G685', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (130, 'Regional RHS', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (131, 'R268', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (132, 'R155', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (133, 'M814', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (134, 'R297', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (135, 'G658', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (136, 'KS461', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (137, 'HSR', 3, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (138, 'HSR2', 3, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (139, 'FS511', 2, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (140, 'FS557', 2, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (141, 'Drive II', 5, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (142, 'G665', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (143, 'G658', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (144, 'R268', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (145, 'RHS', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (146, 'KRS03', 8, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (147, 'XZE2', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (148, 'XZU', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (149, 'XZE2 PLUS', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (150, 'FR85', 6, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (151, 'MC45', 6, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (152, 'FR01', 6, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (153, 'XZE2', 6, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (154, 'R155', 6, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (155, 'DSR115', 9, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (156, 'XDE2', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (157, 'R163', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (158, 'FS400', 2, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (159, 'R250', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (160, 'R227', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (161, 'X MULT', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (162, 'G667', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (163, 'KMax S', 1, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (164, 'kunh', 219, 3, 4, 13);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (165, 'ModeloB', 4, 3, 5, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (291, 'gol plus', 3, 3, 3, 16);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (292, 'mc01', 6, 3, 3, 16);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1051, 'Front Rubber', 198, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1052, 'D601', 187, 3, 4, 14.5);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1053, 'bomlog3', 11, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1057, 'Nome', 183, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1152, 'teste', 217, 3, 4, 10);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1273, 'XZE5', 7, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1331, '4005B', 227, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1492, 'Modelo2', 6, 3, 3, 18);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1666, 'Teste', 12, 3, 4, 16);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2378, 'FR302', 4, 3, 4, 18);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2480, '17 MC-45PL', 7, 3, 4, 18);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2481, 'G-359 - PL', 1, 3, 4, 18);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2482, 'PT-11 - PL', 6, 3, 4, 18);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2827, 'abcd', 12, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (2912, '412', 165, 3, 2, 1);
SELECT setval('modelo_pneu_codigo_seq', 2912, true);

INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (5, 'Banda teste', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (6, 'Bandag', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (7, 'Marca 1', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (8, 'Marca 2', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (9, 'Marca Banda', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (17, 'adsadsa', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (19, 'Bandag2', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (23, 'hhgl', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (25, 'Vipal', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (45, 'banda 1', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (108, 'RECAPADORA TVL', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (137, 'VUC TRUCK', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (138, 'Marc 1', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (139, 'Marc 2', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (160, 'BandaTeste2', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (270, 'Vipal 1', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (290, 'MICHELIN-PL', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (291, 'GOODYEAR-PL', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (292, 'PIRELLI-PL', 3);
SELECT setval('marca_banda_codigo_seq', 292, true);

INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (9, 'modelo banda teste ', 5, 3, 3, 10);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (10, 'modelo banda teste 2', 5, 3, 4, 12);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (11, 'BTLSA2', 6, 3, 5, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (12, 'Modelo 1', 7, 3, 6, 15.5);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (13, 'Modelo 2', 7, 3, 3, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (14, 'Teste 1', 6, 3, 4, 124.11);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (31, 'ModeloTeste', 19, 3, 4, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (56, 'vl530l', 25, 3, 3, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (57, 'vlw110', 25, 3, 3, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (58, 'dvum3b258', 25, 3, 3, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (91, 'teste2', 19, 3, 6, 33);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (104, 'teste', 17, 3, 4, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (395, 'Banda Klabin', 45, 3, 4, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (461, 'Bandag333', 6, 3, 4, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (478, 'ABC123', 108, 3, 4, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (507, 'BTLSA3', 6, 3, 4, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (580, 'Brid 1', 138, 3, 3, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (666, 'ModeloTeste2', 160, 3, 4, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (667, 'ModeloTesteVipal', 25, 3, 5, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (722, 'Teste', 25, 3, 4, 20);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (780, 'tESTE', 8, 3, 3, 2.1);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1249, 'TR 12', 270, 3, 3, 16);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1330, 'P510', 292, 3, 4, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1331, 'G-500PL', 291, 3, 4, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1332, 'M-700PL', 290, 3, 4, 18);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (1333, 'G359-PL', 292, 3, 4, 18);
SELECT setval('modelo_banda_codigo_seq', 1333, true);


create table public.pneu_script
(
    codigo_cliente               varchar(255)                           not null,
    cod_modelo                   bigint                                 not null,
    cod_dimensao                 bigint                                 not null,
    pressao_recomendada          real                                   not null,
    pressao_atual                real,
    altura_sulco_interno         real,
    altura_sulco_central_interno real,
    altura_sulco_externo         real,
    cod_unidade                  bigint                                 not null,
    status                       varchar(255)                           not null,
    vida_atual                   integer,
    vida_total                   integer,
    cod_modelo_banda             bigint,
    altura_sulco_central_externo real,
    dot                          varchar(20),
    valor                        real                                   not null,
    data_hora_cadastro           timestamp with time zone default now(),
    pneu_novo_nunca_rodado       boolean                  default false not null,
    codigo                       bigserial                              not null,
    cod_empresa                  bigint                                 not null,
    cod_unidade_cadastro         integer                                not null,
    deletado                     boolean                  default false not null,
    data_hora_deletado           timestamp with time zone,
    pg_username_delecao          text
);

CREATE
OR REPLACE FUNCTION TG_FUNC_PNEU_SCRIPT_INSERT()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
PNEU_PRIMEIRA_VIDA  BIGINT  := 1;
    PNEU_POSSUI_BANDA
BOOLEAN := F_IF(NEW.vida_atual > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_PNEU_PROLOG
BIGINT;
    F_QTD_ROWS_AFETADAS
BIGINT;
    F_COD_SERVICO_REALIZADO
BIGINT;
BEGIN
INSERT INTO PUBLIC.PNEU(CODIGO,
                        COD_EMPRESA,
                        COD_UNIDADE_CADASTRO,
                        COD_UNIDADE,
                        CODIGO_CLIENTE,
                        COD_MODELO,
                        COD_DIMENSAO,
                        PRESSAO_RECOMENDADA,
                        PRESSAO_ATUAL,
                        ALTURA_SULCO_INTERNO,
                        ALTURA_SULCO_CENTRAL_INTERNO,
                        ALTURA_SULCO_CENTRAL_EXTERNO,
                        ALTURA_SULCO_EXTERNO,
                        STATUS,
                        VIDA_ATUAL,
                        VIDA_TOTAL,
                        DOT,
                        VALOR,
                        COD_MODELO_BANDA,
                        PNEU_NOVO_NUNCA_RODADO,
                        DATA_HORA_CADASTRO)
VALUES (NEW.codigo,
        NEW.cod_empresa,
        NEW.cod_unidade,
        NEW.cod_unidade,
        NEW.codigo_cliente,
        NEW.cod_modelo,
        NEW.cod_dimensao,
        NEW.pressao_recomendada,
        NEW.pressao_atual, -- PRESSAO_ATUAL
        NEW.altura_sulco_interno, -- ALTURA_SULCO_INTERNO
        NEW.altura_sulco_central_interno, -- ALTURA_SULCO_CENTRAL_INTERNO
        NEW.altura_sulco_central_externo, -- ALTURA_SULCO_CENTRAL_EXTERNO
        NEW.altura_sulco_externo, -- ALTURA_SULCO_EXTERNO
        NEW.status,
        NEW.vida_atual,
        NEW.vida_total,
        NEW.dot,
        NEW.valor,
        NEW.cod_modelo_banda,
        NEW.pneu_novo_nunca_rodado,
        NEW.data_hora_cadastro) RETURNING CODIGO
INTO COD_PNEU_PROLOG;

-- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
IF
(PNEU_POSSUI_BANDA)
    THEN
        --  Inserimos o serviço realizado, retornando o código.
        INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
          COD_TIPO_SERVICO,
          COD_UNIDADE,
          COD_PNEU,
          CUSTO,
          VIDA,
          FONTE_SERVICO_REALIZADO)
        VALUES(
         (SELECT PTS.CODIGO
                FROM PNEU_TIPO_SERVICO AS PTS
                WHERE PTS.COD_EMPRESA IS NULL
                  AND PTS.STATUS_ATIVO = TRUE
                  AND PTS.INCREMENTA_VIDA = TRUE
                  AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE),
          NEW.cod_unidade,
          COD_PNEU_PROLOG,
          200, -- Fixamos 200.
          NEW.vida_atual - 1,
          'FONTE_CADASTRO') RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

        -- Mapeamos o incremento de vida do serviço realizado acima.
INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(COD_SERVICO_REALIZADO,
                                                          COD_MODELO_BANDA,
                                                          VIDA_NOVA_PNEU,
                                                          FONTE_SERVICO_REALIZADO)
VALUES (F_COD_SERVICO_REALIZADO,
        NEW.cod_modelo_banda,
        NEW.vida_atual,
        'FONTE_CADASTRO');

INSERT INTO PUBLIC.PNEU_SERVICO_CADASTRO(COD_PNEU,
                                         COD_SERVICO_REALIZADO)
VALUES (COD_PNEU_PROLOG,
        F_COD_SERVICO_REALIZADO);

GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

-- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
IF
(F_QTD_ROWS_AFETADAS <= 0)
        THEN
          PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', COD_PNEU_PROLOG));
END IF;
END IF;

GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

-- Verificamos se a inserção ocorreu com sucesso.
IF
(F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%"', COD_PNEU_PROLOG;
END IF;

RETURN NEW;
END
$$;

CREATE TRIGGER TG_PNEU_SCRIPT_INSERT
    BEFORE INSERT
    ON PNEU_SCRIPT
    FOR EACH ROW
    EXECUTE PROCEDURE TG_FUNC_PNEU_SCRIPT_INSERT();

INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('Didi', 139, 1, 100, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('26354', 133, 2, 122, 0, 1, 1, 1, 5, 'DESCARTE', 2, 4, 56, 1, '1012', 1120, '2018-03-26 18:24:46.745298', false,
        2, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29167', 133, 2, 90, 0, 1, 1, 1, 5, 'EM_USO', 2, 4, 56, 1, '1210', 1120, '2018-03-26 18:31:27.652918', false, 6,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('58624', 291, 1, 120, 0, 16, 16, 16, 5, 'ESTOQUE', 1, 4, null, 16, '2217', 0.01, '2018-03-26 18:35:06.572154',
        false, 24, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55284', 292, 1, 120, 0, 16, 16, 16, 5, 'EM_USO', 1, 4, null, 16, '2216', 0.01, '2018-03-26 18:37:53.212183',
        false, 60, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('22351', 132, 1, 120, 0, 16, 16, 16, 5, 'DESCARTE', 3, 4, 13, 16, '2215', 0.01, '2018-03-26 18:42:00.864434',
        false, 76, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29066', 132, 1, 120, 0, 1, 1, 1, 5, 'EM_USO', 2, 4, 58, 1, '4812', 0.01, '2018-03-26 18:49:39.090519', false,
        87, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('12345', 160, 1, 9, 0, 2, 3, 2, 103, 'EM_USO', 2, 4, 58, 3, '2414', 0.01, '2018-03-26 18:55:39.776027', false,
        129, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('509', 143, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 187, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('510', 156, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 189, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('511', 156, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 190, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('512', 143, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 191, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('513', 143, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 192, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('514', 143, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 217, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('515', 156, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 218, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('516', 133, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 219, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('267', 158, 3, 115, 108.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 478, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('521', 137, 2, 105, 96.15, 6.91, 5.45, 7.56, 5, 'DESCARTE', 1, 4, null, 6.32, '', 1200, null, false, 547, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('522', 137, 2, 105, 0, 17.8, 18.94, 18.04, 5, 'DESCARTE', 1, 4, null, 17.54, '', 1200, null, false, 548, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('523', 137, 2, 105, 94.22, 8.37, 8.01, 7.91, 5, 'EM_USO', 1, 4, null, 7.82, '', 1200, null, false, 549, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('75', 143, 1, 115, 100.5, 15.5, 15.5, 15.5, 5, 'DESCARTE', 1, 4, null, 15.5, '', 1200, null, false, 681, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('352', 143, 1, 115, 114.42, 12.13, 11.76, 13.23, 5, 'DESCARTE', 1, 4, null, 11.65, '', 1200, null, false, 821,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('397', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'DESCARTE', 1, 4, null, 15, '3817',
        1200, null, false, 849, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('2', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3717',
        1200, null, false, 850, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('3', 137, 2, 105, 102.44, 6.26, 6.75, 7.59, 5, 'DESCARTE', 1, 4, null, 7.39, '', 1200, null, false, 851, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('4', 137, 2, 105, 99.65, 4.12, 3.21, 6, 5, 'EM_USO', 1, 4, null, 4.12, '', 1200, null, false, 852, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('7', 157, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '', 1200,
        null, false, 862, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('14B', 141, 1, 115, 120.57, 9.64, 8.85, 8.44, 5, 'EM_USO', 1, 4, null, 7.38, '1209', 1200, null, false, 863, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('8', 158, 3, 115, 114.87, 2.79, 2.17, 3.37, 5, 'ESTOQUE', 1, 4, null, 2.17, '', 1200, null, false, 867, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('9', 158, 3, 115, 126.77, 16, 16, 16, 5, 'ESTOQUE', 2, 4, 56, 16, '', 1200, null, false, 868, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('384', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 215, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 899, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('12', 137, 2, 105, 101.57, 15, 15, 15, 5, 'DESCARTE', 3, 4, 11, 15, '2546', 1200, null, false, 913, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('3000', 146, 5, 150, 0, 10, 10, 10, 5, 'DESCARTE', 1, 4, null, 10, 'dot teste', 300, null, false, 914, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('289', 163, 1, 115, 4.52, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217', 1200, null, false, 915, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('10', 139, 18, 115, 139.89, 7.62, 8.82, 10.64, 5, 'DESCARTE', 1, 4, null, 8.82, '', 1200, null, false, 916, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('700', 164, 3, 115, 0, 10, 10, 10, 5, 'DESCARTE', 3, 4, 9, 10, 'dd mm yy', 1500, null, false, 917, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('25', 143, 1, 115, 113.19, 12.34, 12.44, 12, 103, 'EM_USO', 1, 4, null, 11.93, '', 1200, null, false, 918, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13', 137, 2, 105, 96.5, 5.06, 3.69, 6.84, 103, 'EM_USO', 1, 4, null, 4.57, '', 1200, null, false, 921, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('598', 143, 1, 115, 75.9, 7.52, 4.71, 6.49, 5, 'DESCARTE', 1, 4, null, 3.35, '', 1200, null, false, 938, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('445', 143, 1, 115, 100.5, 17.5, 17.5, 17.5, 5, 'DESCARTE', 1, 4, null, 17.5, '', 1200, null, false, 951, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('15', 137, 2, 105, 96.67, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 952, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('16', 137, 2, 105, 109.44, 5.7, 24.6, 9.8, 103, 'ESTOQUE', 1, 3, null, 14.8, '', 1200, null, false, 964, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('17', 158, 3, 115, 107.17, 5.22, 5.25, 7.3, 5, 'DESCARTE', 1, 4, null, 5.25, '', 1200, null, false, 966, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('18', 158, 3, 115, 6.57, 9.6, 9.02, 10.35, 5, 'EM_USO', 1, 4, null, 9.02, '', 1200, null, false, 967, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('20', 158, 3, 115, 109.44, 5.12, 4.02, 5.64, 5, 'EM_USO', 1, 4, null, 4.02, '', 1200, null, false, 969, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('24', 158, 3, 115, 112.07, 6.29, 7.62, 10.15, 5, 'ANALISE', 1, 4, null, 7.62, '', 1200, null, false, 972, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('23', 158, 3, 115, 110.84, 16, 16, 16, 5, 'EM_USO', 3, 4, 13, 16, '', 1200, null, false, 973, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('485', 159, 4, 105, 0, 16.06, 16.55, 16.13, 5, 'EM_USO', 1, 4, null, 16.68, '', 1200, null, false, 978, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('486', 143, 4, 105, 0, 16.29, 16.16, 16.22, 5, 'EM_USO', 1, 4, null, 16.48, '', 1200, null, false, 979, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('107', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 2, 4, 11, 17.5, '', 1200, null, false, 980, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('484', 152, 4, 105, 100.5, 16.13, 16.13, 16.26, 5, 'EM_USO', 1, 4, null, 16.16, '', 1200, null, false, 981, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('26', 157, 1, 115, 109.67, 17.6, 18.18, 17.29, 5, 'EM_USO', 1, 4, null, 17.05, '', 1200, null, false, 984, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29', 137, 2, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 987, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('30', 137, 2, 105, 120.29, 11.97, 12.1, 12.46, 5, 'EM_USO', 1, 4, null, 12.07, '', 1200, null, false, 988, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('32', 143, 1, 115, 117.41, 9.23, 9.26, 8.41, 5, 'EM_USO', 1, 4, null, 6.9, '', 1200, null, false, 989, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('33', 143, 1, 115, 116, 7.21, 6.59, 7.07, 5, 'EM_USO', 1, 4, null, 6.39, '', 1200, null, false, 990, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('487', 159, 4, 105, 0, 17.59, 21.31, 21.99, 5, 'EM_USO', 1, 4, null, 22.45, '', 1200, null, false, 1041, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('488', 159, 4, 105, 0, 16, 9.77, 18.8, 5, 'EM_USO', 1, 4, null, 16.06, '', 1200, null, false, 1044, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('489', 159, 4, 105, 0, 16.26, 16.26, 18.37, 5, 'EM_USO', 1, 4, null, 16.22, '', 1200, null, false, 1045, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('490', 150, 4, 105, 0, 16.26, 16.52, 16.58, 5, 'EM_USO', 1, 4, null, 16.32, '', 1200, null, false, 1048, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('31', 163, 1, 115, 4.34, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '3217', 1200, null, false, 1049, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('493', 156, 4, 105, 0, 16.06, 16.13, 16.16, 5, 'EM_USO', 1, 4, null, 16.09, '', 1200, null, false, 1051, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('494', 156, 4, 105, 0, 2.28, 2.7, 8.92, 5, 'EM_USO', 1, 4, null, 4.49, '', 1200, null, false, 1056, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('34', 137, 2, 105, 105.94, 11.71, 12.39, 12.91, 5, 'ESTOQUE', 1, 4, null, 13.14, '', 1200, null, false, 1060, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35', 133, 2, 105, 112.42, 12.39, 11.45, 12.81, 5, 'EM_USO', 1, 4, null, 12.17, '', 1200, null, false, 1063, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('36', 137, 2, 105, 104.37, 8.01, 7.82, 8.63, 5, 'ESTOQUE', 1, 4, null, 6.39, '', 1200, null, false, 1075, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('37', 137, 2, 105, 96.85, 11.35, 11.58, 11.84, 5, 'ESTOQUE', 1, 4, null, 11.32, '', 1200, null, false, 1077, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('38', 137, 2, 105, 114.52, 10.32, 11.16, 11.52, 215, 'EM_USO', 1, 4, null, 11.48, '', 1200, null, false, 1087,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('39', 137, 2, 105, 104.19, 11.13, 11.39, 11.39, 215, 'EM_USO', 1, 4, null, 11.55, '', 1200, null, false, 1095,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('40', 137, 2, 105, 118.54, 10.61, 10.96, 11.13, 215, 'EM_USO', 1, 4, null, 11.55, '', 1200, null, false, 1096,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('41', 137, 2, 105, 105.77, 7.26, 7.98, 6.32, 5, 'EM_USO', 1, 4, null, 8.17, '', 1200, null, false, 1102, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('42', 145, 2, 105, 101.04, 5.71, 6.65, 8.17, 5, 'EM_USO', 1, 4, null, 7.07, '', 1200, null, false, 1103, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('43', 143, 1, 115, 113.82, 11.29, 9.47, 10.9, 5, 'ESTOQUE', 1, 4, null, 9.47, '', 1200, null, false, 1115, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('44', 158, 3, 115, 118.02, 6.71, 6.03, 7.07, 5, 'ESTOQUE', 1, 4, null, 6.03, '', 1200, null, false, 1116, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('49', 145, 2, 105, 59.75, 10.09, 8.47, 9.76, 5, 'EM_USO', 1, 4, null, 7.95, '', 1200, null, false, 1121, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('50', 145, 2, 105, 38.93, 9.31, 8.53, 9.41, 5, 'ESTOQUE', 1, 4, null, 8.53, '', 1200, null, false, 1122, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('51', 137, 2, 105, 112.59, 2.66, 2.23, 2.53, 5, 'ESTOQUE', 1, 4, null, 1.97, '', 1200, null, false, 1123, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('52', 137, 2, 105, 95.8, 3.79, 3.69, 3.11, 5, 'EM_USO', 1, 4, null, 3.37, '', 1200, null, false, 1124, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53', 137, 2, 105, 95.45, 7.75, 8.76, 7.82, 5, 'EM_USO', 1, 4, null, 8.34, '', 1200, null, false, 1125, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54', 137, 2, 105, 63.43, 3.21, 3.89, 4.86, 5, 'EM_USO', 1, 4, null, 4.86, '', 1200, null, false, 1126, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55', 137, 2, 105, 95.8, 4.44, 4.05, 3.92, 5, 'EM_USO', 1, 4, null, 3.6, '', 1200, null, false, 1127, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56', 137, 2, 105, 103.14, 2.04, 1.81, 3.66, 5, 'EM_USO', 1, 4, null, 2.79, '', 1200, null, false, 1128, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('57', 157, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '', 1200,
        null, false, 1134, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62', 133, 2, 105, 104.37, 10.64, 9.41, 10.77, 5, 'ESTOQUE', 1, 4, null, 10.06, '', 1200, null, false, 1199, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63', 137, 2, 105, 95.45, 5.61, 6.36, 5.84, 5, 'ESTOQUE', 1, 4, null, 5.61, '', 1200, null, false, 1233, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('64', 133, 2, 105, 109.27, 14.18, 14.27, 14.92, 215, 'EM_USO', 1, 4, null, 14.47, '', 1200, null, false, 1248,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('70', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 103, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 1270, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('65', 137, 2, 105, 115.39, 11.81, 12.2, 12.13, 5, 'ESTOQUE', 1, 4, null, 12.43, '', 1200, null, false, 1273, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('66', 137, 2, 105, 112.42, 8.92, 8.14, 9.28, 5, 'ESTOQUE', 1, 4, null, 8.17, '', 1200, null, false, 1274, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('67', 137, 2, 105, 110.67, 12.3, 13.53, 13.33, 5, 'ESTOQUE', 1, 4, null, 13.43, '', 1200, null, false, 1294, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('68', 137, 2, 105, 111.19, 12.26, 11.91, 13.66, 5, 'ESTOQUE', 1, 4, null, 12.55, '', 1200, null, false, 1295, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('69', 137, 2, 105, 106.99, 11.81, 11.81, 12.07, 5, 'ESTOQUE', 1, 4, null, 12.3, '', 1200, null, false, 1296, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('72', 143, 1, 115, 122.74, 8.21, 6.81, 7.3, 5, 'ESTOQUE', 1, 4, null, 4.8, '', 1200, null, false, 1301, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('71', 143, 1, 115, 108.44, 10.87, 9.94, 7.79, 5, 'EM_USO', 1, 4, null, 7.48, '', 1200, null, false, 1312, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('73', 137, 2, 105, 103.32, 6.26, 6.71, 7.43, 5, 'ESTOQUE', 1, 4, null, 7.1, '', 1200, null, false, 1321, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('74', 133, 2, 105, 99.82, 11.61, 8.95, 11.55, 5, 'ESTOQUE', 1, 4, null, 11.61, '', 1200, null, false, 1322, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('76', 133, 2, 105, 104.72, 11.03, 10.93, 12.78, 5, 'ESTOQUE', 1, 4, null, 11.78, '', 1200, null, false, 1323, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('77', 137, 2, 105, 98.95, 6.55, 6.32, 5.74, 5, 'ESTOQUE', 1, 4, null, 5.74, '', 1200, null, false, 1324, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('78', 137, 2, 105, 105.24, 4.34, 5.32, 6.55, 5, 'ESTOQUE', 1, 4, null, 6.06, '', 1200, null, false, 1325, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('79', 137, 2, 105, 99.12, 6.49, 6.32, 6.91, 5, 'ESTOQUE', 1, 4, null, 5.19, '', 1200, null, false, 1326, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('80', 137, 2, 105, 98.77, 7.91, 7.88, 8.98, 5, 'ESTOQUE', 1, 4, null, 7.43, '', 1200, null, false, 1327, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('81', 137, 2, 105, 113.64, 11.19, 11.48, 11.91, 5, 'ESTOQUE', 1, 4, null, 11.78, '', 1200, null, false, 1328, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('82', 137, 2, 105, 94.22, 4.44, 3.53, 6.23, 5, 'ESTOQUE', 1, 4, null, 4.93, '', 1200, null, false, 1329, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('83', 137, 2, 105, 97.2, 6.94, 7.43, 6.62, 5, 'ESTOQUE', 1, 4, null, 7.36, '', 1200, null, false, 1330, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('84', 133, 2, 105, 108.92, 10.87, 11.06, 12.81, 5, 'ESTOQUE', 1, 4, null, 11.58, '', 1200, null, false, 1331, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('624', 143, 4, 105, 105.1, 1.46, 1.46, 15.5, 5, 'ESTOQUE', 1, 4, null, 15.5, '', 1200, null, false, 1382, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('85', 143, 1, 115, 113.71, 10.94, 10.9, 9.77, 5, 'ESTOQUE', 1, 4, null, 8.75, '', 1200, null, false, 1450, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('86', 161, 4, 105, 0.0000000000000000000000000000000000001, 17, 17, 17, 5, 'ESTOQUE', 1, 4, null, 17, '', 1200,
        null, false, 1451, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('158', 143, 1, 115, 106.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 1521, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('105', 291, 2, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 11, null, false, 1733, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('103', 137, 2, 105, 0, 5.58, 6.27, 6.79, 5, 'DESCARTE', 3, 4, 461, 8.26, '', 1200, null, false, 1734, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('91', 143, 1, 115, 119.52, 8.61, 8.41, 7.38, 5, 'ESTOQUE', 1, 4, null, 6.15, '', 1200, null, false, 1735, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('92', 150, 4, 105, 0.0000000000000000000000000000000000001, 17, 17, 17, 5, 'ESTOQUE', 1, 4, null, 17, '', 1200,
        null, false, 1736, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('93', 137, 2, 105, 112.42, 4.47, 3.89, 5.41, 5, 'ESTOQUE', 1, 4, null, 4.86, '', 1200, null, false, 1737, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('94', 137, 2, 105, 89.15, 3.99, 2.85, 4.77, 5, 'ESTOQUE', 1, 4, null, 3.24, '', 1200, null, false, 1739, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('95', 137, 2, 105, 102.27, 6.23, 5.25, 7.88, 5, 'ESTOQUE', 1, 4, null, 7.49, '', 1200, null, false, 1740, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('96', 137, 2, 105, 109.62, 9.18, 9.57, 10.32, 5, 'ESTOQUE', 1, 4, null, 9.96, '', 1200, null, false, 1741, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('97', 137, 2, 105, 86, 9.54, 9.96, 9.47, 5, 'ESTOQUE', 1, 4, null, 10.41, '', 1200, null, false, 1742, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('98', 137, 2, 105, 85.3, 7.1, 7.43, 8.73, 5, 'ESTOQUE', 1, 4, null, 8.04, '', 1200, null, false, 1743, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('99', 137, 2, 105, 109.09, 7.82, 6.97, 6.81, 5, 'ESTOQUE', 1, 4, null, 6.55, '', 1200, null, false, 1744, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('88', 143, 1, 115, 0, 1.37, 0, 0.55, 5, 'EM_USO', 1, 4, null, 13.22, '', 1200, null, false, 1761, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('120', 158, 3, 115, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1762, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('11', 158, 3, 115, 108.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 2, 4, 11, 15.5, '', 1200, null, false, 1763, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('101', 137, 2, 150, 117.14, 15, 15, 15, 5, 'DESCARTE', 4, 4, 11, 15, '', 1200, null, false, 1765, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('121', 158, 3, 115, 100.5, 17.5, 17.5, 17.5, 5, 'DESCARTE', 1, 4, null, 17.5, '', 1200, null, false, 1766, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('108', 140, 3, 115, 0, 14.97, 14.9, 14.89, 103, 'ESTOQUE', 2, 3, 666, 14.89, '', 1200, null, false, 1767, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('106', 143, 1, 115, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1768, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('113', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1769, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('100', 137, 2, 105, 110.32, 5.48, 4.18, 5.64, 5, 'DESCARTE', 1, 4, null, 4.83, '', 1200, null, false, 1770, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('102', 137, 2, 105, 105.24, 9.96, 9.99, 8.08, 5, 'DESCARTE', 1, 4, null, 9.63, '', 1200, null, false, 1771, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('135', 137, 2, 105, 109.5, 15.62, 15.65, 15.49, 103, 'ESTOQUE', 1, 3, null, 15.75, '', 1200, null, false, 1772,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('133', 143, 1, 115, 108.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 1775, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('4422', 165, 1, 115, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, 'DOT TESTE', 1500, null, false, 1776, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('109', 158, 3, 115, 115.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 2, 4, 11, 17.5, '', 1200, null, false, 1777, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('134', 137, 2, 105, 113.99, 11.16, 12.78, 11.94, 5, 'DESCARTE', 1, 4, null, 12.78, '', 1200, null, false, 1783,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('130', 143, 1, 115, 107.21, 3, 1.94, 7.52, 103, 'EM_USO', 1, 4, null, 3.79, '', 1200, null, false, 1784, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('110', 137, 2, 105, 105.94, 15, 15, 15, 103, 'EM_USO', 3, 4, 31, 15, '', 1200, null, false, 1785, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('136', 158, 3, 115, 94.5, 8.5, 9.5, 10.5, 5, 'ESTOQUE', 3, 4, 13, 9.5, '', 1200, null, false, 1786, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('139', 137, 2, 105, 100, 15, 15, 15, 5, 'DESCARTE', 3, 4, 11, 15, '', 1200, null, false, 1787, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('140', 158, 3, 115, 115.92, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 1788, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('142', 137, 2, 105, 86.7, 4.05, 3.5, 6.36, 5, 'DESCARTE', 1, 4, null, 5.06, '', 1200, null, false, 1789, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('143', 158, 3, 115, -1, 16, 16, 16, 5, 'ESTOQUE', 2, 4, 58, 16, '', 1200, null, false, 1790, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('145', 137, 2, 105, 94.92, 15, 15, 15, 5, 'DESCARTE', 4, 4, 11, 15, '', 1200, null, false, 1792, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('146', 145, 2, 105, 106.29, 1.52, 2.88, 3.11, 5, 'DESCARTE', 1, 4, null, 3.92, '', 1200, null, false, 1793, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('147', 137, 2, 105, -1, -1, -1, -1, 5, 'DESCARTE', 1, 4, null, -1, '', 1200, null, false, 1794, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('148', 137, 2, 105, 130.5, 6, 6, 9.5, 5, 'DESCARTE', 1, 4, null, 6, '', 1200, null, false, 1795, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('149', 137, 2, 105, 130.5, 15, 15, 15, 103, 'ESTOQUE', 2, 4, 11, 15, '', 1200, null, false, 1796, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('150', 137, 2, 105, 93.17, 7.36, 8.21, 7.75, 5, 'DESCARTE', 1, 4, null, 7.3, '', 1200, null, false, 1797, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('152', 137, 2, 105, 105.59, 2.17, 3.37, 3.99, 5, 'DESCARTE', 1, 4, null, 3.73, '', 1200, null, false, 1798, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('154', 137, 2, 105, 112.94, 5.12, 5.03, 5.12, 5, 'DESCARTE', 1, 4, null, 6.75, '', 1200, null, false, 1799, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('112', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1800, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('114', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1801, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('117', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1802, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('160', 137, 2, 105, 101.92, 7.59, 5.77, 6.23, 103, 'ESTOQUE', 1, 3, null, 5.61, '', 1200, null, false, 1803, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('162', 137, 2, 105, 107.87, 9.02, 9.24, 10.64, 103, 'ESTOQUE', 1, 3, null, 10.32, '', 1200, null, false, 1805,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('163', 137, 2, 105, 4.34, 16, 16, 16, 5, 'EM_USO', 1, 4, null, 16, '', 1200, null, false, 1806, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('111', 137, 2, 105, 4.34, 7.3, 6.39, 7.26, 5, 'EM_USO', 1, 4, null, 6.55, '', 1200, null, false, 1807, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('137', 137, 2, 105, 0, 15, 15, 15, 103, 'ESTOQUE', 2, 3, 11, 15, '', 1200, null, false, 1808, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('132', 137, 2, 105, 109.62, 9.5, 8.27, 10.35, 103, 'ESTOQUE', 1, 3, null, 9.89, '', 1200, null, false, 1809, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('128', 137, 2, 105, 91.25, 9.31, 9.7, 10.41, 5, 'DESCARTE', 1, 4, null, 10.06, '', 1200, null, false, 1810, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('153', 137, 2, 105, 105.94, 7.23, 6.23, 5.32, 5, 'DESCARTE', 1, 4, null, 4.54, '', 1200, null, false, 1817, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('164', 137, 2, 105, 100.5, 10.49, 10.49, 10.49, 5, 'ESTOQUE', 3, 4, 13, 10.49, '', 1200, null, false, 1821, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('165', 137, 2, 105, 109.09, 6.68, 9.47, 8.92, 103, 'ESTOQUE', 1, 3, null, 9.47, '', 1200, null, false, 1822, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('166', 137, 2, 105, 106.99, 5.29, 5.58, 6.19, 103, 'ESTOQUE', 1, 3, null, 4.47, '', 1200, null, false, 1823, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('167', 137, 2, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 1824, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('168', 158, 3, 115, 100.5, 10.49, 10.49, 10.49, 5, 'DESCARTE', 3, 4, 10, 10.49, '', 1200, null, false, 1825, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('169', 137, 2, 105, 105.94, 3.31, 3.56, 3.56, 5, 'EM_USO', 1, 4, null, 3.31, '', 1200, null, false, 1826, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('170', 137, 2, 105, 114.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 1827, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('171', 137, 2, 105, 93.52, 4.76, 2.44, 3.82, 5, 'EM_USO', 1, 4, null, 2.92, '', 1200, null, false, 1828, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('172', 137, 2, 105, 100.5, 10.49, 10.49, 10.49, 5, 'ESTOQUE', 2, 4, 9, 10.49, '', 1200, null, false, 1829, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('173', 137, 2, 105, 100.5, 10.49, 10.49, 10.49, 215, 'ESTOQUE', 2, 4, 13, 10.49, '', 1200, null, false, 1830, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('174', 158, 3, 115, 118.02, 15, 15, 15, 103, 'ESTOQUE', 3, 4, 11, 15, '', 1200, null, false, 1831, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('175', 158, 3, 115, 118.37, 6.68, 5.19, 5.41, 5, 'DESCARTE', 1, 4, null, 5.19, '', 1200, null, false, 1832, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('126', 137, 2, 105, 111.25, 10.87, 8.88, 8.88, 5, 'EM_USO', 1, 4, null, 9.23, '', 1200, null, false, 1833, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('118', 137, 2, 105, 100.5, 17.5, 17.5, 17.5, 103, 'ESTOQUE', 1, 3, null, 17.5, '', 1200, null, false, 1834, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('116', 137, 2, 105, 66.23, 15, 15, 15, 5, 'DESCARTE', 5, 5, 11, 15, '', 1200, null, false, 1836, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('115', 137, 2, 105, 105, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 1837, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('199', 143, 1, 115, 0, 3.12, 3.46, 3.15, 103, 'EM_USO', 1, 4, null, 3.34, '', 1200, null, false, 1846, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('176', 143, 1, 115, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1849, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('177', 158, 3, 115, 94.5, 1.5, 1.5, 12.5, 103, 'EM_USO', 1, 4, null, 1.5, '', 1200, null, false, 1850, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('178', 145, 2, 105, 4.52, 8.66, 9.28, 10.41, 5, 'EM_USO', 1, 4, null, 9.28, '', 1200, null, false, 1851, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('187', 158, 3, 115, 0.0000000000000000000000000000000000001, 0.0000000000000000000000000000000000001,
        0.0000000000000000000000000000000000001, 0.0000000000000000000000000000000000001, 5, 'DESCARTE', 1, 4, null,
        0.0000000000000000000000000000000000001, '', 1200, null, false, 1863, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('188', 158, 3, 115, 6.92, 0.0000000000000000000000000000000000001, 0.0000000000000000000000000000000000001,
        0.0000000000000000000000000000000000001, 5, 'EM_USO', 1, 4, null, 0.0000000000000000000000000000000000001, '',
        1200, null, false, 1864, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('189', 158, 3, 115, 100.5, 10.49, 10.49, 10.49, 215, 'ESTOQUE', 2, 4, 13, 10.49, '', 1200, null, false, 1865, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('190', 158, 3, 115, 0.0000000000000000000000000000000000001, 16, 16, 16, 5, 'DESCARTE', 2, 4, 13, 16, '', 1200,
        null, false, 1866, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('191', 133, 2, 105, 0.0000000000000000000000000000000000001, 16, 16, 16, 103, 'ESTOQUE', 2, 4, 13, 16, '', 1200,
        null, false, 1867, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('192', 133, 2, 105, 0.0000000000000000000000000000000000001, 16, 16, 16, 103, 'ESTOQUE', 2, 4, 13, 16, '', 1200,
        null, false, 1868, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('193', 137, 2, 105, 94.5, 1.5, 1.5, 1.5, 103, 'EM_USO', 2, 4, 13, 1.5, '', 1200, null, false, 1869, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('194', 137, 2, 105, 118.89, 16, 16, 16, 103, 'ESTOQUE', 2, 4, 13, 16, '', 1200, null, false, 1870, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('195', 137, 2, 105, 127.99, 11.61, 12, 10.22, 5, 'DESCARTE', 1, 4, null, 12.3, '', 1200, null, false, 1871, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('196', 137, 2, 105, 110.14, 16, 16, 16, 5, 'DESCARTE', 2, 4, 13, 16, '', 1200, null, false, 1872, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('197', 137, 2, 105, 112.42, 15.5, 15.5, 15.5, 5, 'EM_USO', 2, 4, 13, 15.5, '', 1200, null, false, 1873, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('198', 137, 2, 105, 102.44, 16, 16, 16, 5, 'EM_USO', 2, 4, 13, 16, '', 1200, null, false, 1874, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('202', 163, 1, 115, 109.14, 16, 16, 16, 103, 'ESTOQUE', 2, 4, 13, 16, '3817', 1200, null, false, 1875, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('203', 137, 2, 105, 90.55, 10.48, 10.35, 9.57, 5, 'EM_USO', 1, 4, null, 9.57, '', 1200, null, false, 1876, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('204', 137, 2, 105, 95.8, 7.82, 5.64, 8.56, 5, 'DESCARTE', 1, 4, null, 7.95, '', 1200, null, false, 1877, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('119', 158, 3, 115, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 1878, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('138', 150, 4, 105, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'DESCARTE', 4, 4, 11, 15, '', 1200,
        null, false, 1879, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('206', 143, 1, 115, 115.47, 16, 16, 16, 5, 'EM_USO', 2, 4, 13, 16, '', 1200, null, false, 1884, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('207', 158, 3, 115, 115, 0.0000000000000000000000000000000000001, 0.0000000000000000000000000000000000001,
        0.0000000000000000000000000000000000001, 5, 'EM_USO', 1, 4, null, 0.0000000000000000000000000000000000001, '',
        1200, null, false, 1885, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('213', 137, 2, 105, 101.39, 2.49, 4.44, 6.84, 103, 'ESTOQUE', 1, 4, null, 4.44, '', 1200, null, false, 1891, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('214', 158, 3, 115, 114.69, 7.65, 6.49, 6.13, 5, 'EM_USO', 1, 4, null, 6.49, '', 1200, null, false, 1892, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('215', 143, 1, 115, 107.52, 11.13, 9.76, 10.45, 5, 'DESCARTE', 1, 4, null, 9.76, '', 1200, null, false, 1893, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('200', 143, 1, 115, 123.91, 14.97, 14.56, 15.86, 5, 'EM_USO', 1, 4, null, 15.21, '', 1200, null, false, 1894, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('201', 143, 1, 115, 113.89, 15, 15, 15, 103, 'ESTOQUE', 2, 4, 11, 15, '', 1200, null, false, 1902, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('122', 137, 2, 105, 88.97, 4.28, 4.67, 4.08, 5, 'DESCARTE', 1, 4, null, 4.54, '', 1200, null, false, 1903, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('269', 158, 3, 115, 126.59, 16, 16, 16, 5, 'EM_USO', 2, 4, 395, 16, '', 1200, null, false, 1939, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('271', 143, 1, 115, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 1961, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('251', 143, 1, 115, 101.93, 3.86, 2.18, 5.33, 5, 'DESCARTE', 1, 4, null, 3.07, '', 1200, null, false, 1984, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28', 137, 2, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 1988, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('259', 137, 2, 105, 102.62, 4.9, 5.97, 6.75, 5, 'DESCARTE', 1, 4, null, 6.13, '', 1200, null, false, 2021, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('258', 133, 2, 105, 110.67, 11.61, 11.71, 13.4, 5, 'DESCARTE', 1, 4, null, 11.87, '', 1200, null, false, 2048,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('216', 143, 1, 115, 118.89, 16, 16, 16, 5, 'DESCARTE', 2, 4, 13, 16, '', 1200, null, false, 2051, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('252', 145, 2, 105, 109.79, 4.08, 4.86, 5.16, 5, 'DESCARTE', 1, 4, null, 4.9, '', 1200, null, false, 2052, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('257', 137, 2, 105, 109.79, 7.95, 7.75, 8.92, 5, 'DESCARTE', 1, 4, null, 8.53, '', 1200, null, false, 2053, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('230', 143, 1, 115, 50.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2109, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('270', 145, 2, 105, 100.87, 1.42, 2.62, 3.86, 5, 'DESCARTE', 1, 4, null, 4.12, '', 1200, null, false, 2117, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('250', 137, 2, 105, 109.09, 2.75, 4.05, 4.96, 5, 'DESCARTE', 1, 4, null, 4.05, '', 1200, null, false, 2118, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('247', 143, 1, 115, 112.42, 12.3, 11.19, 13.07, 5, 'DESCARTE', 1, 4, null, 11.19, '', 1200, null, false, 2119,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('273', 158, 3, 115, 126.07, 10.96, 10.28, 12.36, 5, 'EM_USO', 1, 4, null, 10.28, '', 1200, null, false, 2120, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('236', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'ANALISE', 1, 4, null, 15, '3817',
        1200, null, false, 2121, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('237', 158, 3, 115, 115.39, 10.74, 10.74, 10.32, 5, 'EM_USO', 1, 4, null, 10.74, '', 1200, null, false, 2122, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('238', 158, 3, 115, 114.87, 2.79, 1.88, 1.81, 5, 'ANALISE', 1, 4, null, 1.88, '', 1200, null, false, 2124, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('239', 158, 3, 115, 111.89, 7.91, 6.65, 8.79, 5, 'ANALISE', 1, 4, null, 6.65, '', 1200, null, false, 2125, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('240', 143, 1, 115, 124.32, 16, 16, 16, 5, 'DESCARTE', 2, 4, 56, 16, '', 1200, null, false, 2126, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('231', 143, 1, 115, 115.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2127, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('268', 158, 3, 115, 132.71, 12.2, 11.81, 12.07, 5, 'DESCARTE', 1, 4, null, 11.81, '', 1200, null, false, 2128,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('260', 137, 2, 105, 89.32, 8.73, 9.41, 9.02, 5, 'DESCARTE', 1, 4, null, 7.98, '', 1200, null, false, 2142, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('225', 145, 2, 105, 108.92, 8.47, 5.64, 4.28, 5, 'DESCARTE', 1, 4, null, 7.01, '', 1200, null, false, 2143, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('226', 137, 2, 105, 108.57, 5.9, 4.83, 3.66, 5, 'DESCARTE', 1, 4, null, 4.83, '', 1200, null, false, 2144, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('223', 137, 2, 105, 111.02, 10.67, 11.03, 11.16, 5, 'DESCARTE', 1, 4, null, 12.07, '', 1200, null, false, 2145,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('229', 158, 3, 115, 104.37, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 2146, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('253', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 2153, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('262', 137, 2, 105, 114.34, 15.5, 15.5, 15.5, 5, 'ANALISE', 2, 4, 12, 15.5, '', 1200, null, false, 2156, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('263', 145, 2, 105, 97.9, 5.48, 5.25, 6.71, 5, 'ANALISE', 1, 4, null, 4.93, '', 1200, null, false, 2157, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('264', 137, 2, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2158, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('265', 143, 1, 115, 121.87, 4.05, 2.85, 8.21, 5, 'DESCARTE', 1, 4, null, 5.16, '', 1200, null, false, 2159, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('254', 143, 1, 115, 104.74, 7.07, 4.68, 5.5, 5, 'EM_USO', 1, 4, null, 2.01, '', 1200, null, false, 2160, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('249', 137, 2, 105, 103.32, 5.29, 5.51, 7.72, 5, 'DESCARTE', 1, 4, null, 5.51, '', 1200, null, false, 2172, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('261', 137, 2, 105, 111.72, 6.94, 6.88, 6.52, 5, 'DESCARTE', 1, 4, null, 6.91, '', 1200, null, false, 2173, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('246', 143, 1, 115, 111.37, 10.54, 8.5, 10.09, 5, 'DESCARTE', 1, 4, null, 8.5, '', 1200, null, false, 2174, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('255', 143, 1, 115, 108.55, 15.5, 15.5, 15.5, 5, 'DESCARTE', 1, 4, null, 15.5, '', 1200, null, false, 2175, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('266', 158, 3, 115, 124.32, 11.78, 9.76, 10.83, 5, 'EM_USO', 1, 4, null, 9.76, '', 1200, null, false, 2220, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('579', 159, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2326, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('580', 160, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2384, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('287', 143, 1, 115, 108.96, 9.43, 5.09, 7.82, 5, 'EM_USO', 1, 4, null, 4.47, '', 1200, null, false, 2389, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('281', 137, 2, 105, 112.24, 5.93, 6.65, 7.1, 5, 'ANALISE', 1, 4, null, 7.59, '', 1200, null, false, 2391, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('282', 137, 2, 105, 104.37, 7.85, 8.21, 8.47, 5, 'ANALISE', 1, 4, null, 7.98, '', 1200, null, false, 2392, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('283', 137, 2, 105, 114.52, 3.31, 4.28, 3.76, 5, 'EM_USO', 1, 4, null, 5.06, '', 1200, null, false, 2393, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('284', 143, 1, 115, 118.54, 6.16, 4.57, 6.88, 5, 'EM_USO', 1, 4, null, 4.41, '', 1200, null, false, 2394, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('285', 137, 2, 105, 54.33, 12.48, 12.1, 11.18, 5, 'ANALISE', 1, 4, null, 12.9, '', 1200, null, false, 2395, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('286', 137, 2, 105, 103.32, 4.77, 6.29, 6.13, 103, 'EM_USO', 1, 4, null, 5.87, '', 1200, null, false, 2396, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('275', 158, 3, 115, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2397, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('291', 143, 1, 115, 122.33, 19.35, 19.41, 18.87, 103, 'ESTOQUE', 1, 4, null, 18.87, '', 1200, null, false, 2399,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('292', 143, 1, 115, 112.13, 5.6, 4.58, 8.17, 103, 'ESTOQUE', 1, 4, null, 5.19, '', 1200, null, false, 2400, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('279', 133, 2, 105, 111.54, 13.92, 13.01, 13.66, 5, 'DESCARTE', 1, 4, null, 13.4, '', 1200, null, false, 2401,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('272', 158, 3, 115, 113.99, 16, 16, 16, 5, 'ESTOQUE', 2, 4, 57, 16, '', 1200, null, false, 2402, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('278', 133, 2, 105, 109.44, 12.1, 11.19, 11.87, 5, 'ANALISE', 1, 4, null, 11.61, '', 1200, null, false, 2403, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('241', 143, 1, 115, 115.57, 7.36, 6, 8.98, 5, 'DESCARTE', 1, 4, null, 6, '', 1200, null, false, 2404, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('248', 137, 2, 105, 103.84, 10.54, 10.9, 10.61, 5, 'DESCARTE', 1, 4, null, 10.9, '', 1200, null, false, 2405, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('256', 133, 2, 105, 95.97, 12.04, 10.87, 13.33, 5, 'DESCARTE', 1, 4, null, 10.64, '', 1200, null, false, 2406,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('244', 137, 2, 105, 95.62, 6.03, 4.15, 5.25, 5, 'DESCARTE', 1, 4, null, 4.15, '', 1200, null, false, 2409, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('302', 143, 1, 115, 108.26, 6.8, 6.15, 7.07, 103, 'EM_USO', 1, 4, null, 6.05, '', 1200, null, false, 2410, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('303', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 2411, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('304', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 2412, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('277', 133, 2, 105, 113.12, 11.35, 11.03, 11.52, 5, 'ANALISE', 1, 4, null, 11.13, '', 1200, null, false, 2413,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('280', 137, 2, 105, 110.14, 6.32, 6.39, 6.49, 5, 'ANALISE', 1, 4, null, 6.75, '', 1200, null, false, 2414, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('276', 137, 2, 105, 100.34, 6.81, 6.78, 6.26, 5, 'DESCARTE', 1, 4, null, 7.23, '', 1200, null, false, 2415, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('308', 137, 2, 105, 107.87, 1.59, 4.96, 4.31, 5, 'EM_USO', 1, 4, null, 3.11, '', 1200, null, false, 2416, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('309', 137, 2, 105, 84.07, 3.31, 3.6, 5.74, 5, 'EM_USO', 1, 4, null, 5.16, '', 1200, null, false, 2417, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('310', 143, 1, 115, 95.27, 8.27, 5.25, 7.78, 5, 'ANALISE', 1, 4, null, 4.8, '', 1200, null, false, 2418, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('311', 143, 1, 115, 111.25, 6.9, 5.88, 8.85, 5, 'EM_USO', 1, 4, null, 6.18, '', 1200, null, false, 2419, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('242', 143, 1, 115, 119.77, 10.22, 5.41, 8.69, 5, 'DESCARTE', 1, 4, null, 5.41, '', 1200, null, false, 2420, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('27', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 3, 4, 11, 15, '3617', 1200,
        null, false, 2421, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('274', 158, 3, 115, 124.67, 12.13, 12.39, 12.36, 5, 'ANALISE', 1, 4, null, 12.39, '', 1200, null, false, 2422,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('325', 143, 1, 115, 110.55, 3.24, 1.88, 4.82, 5, 'EM_USO', 1, 4, null, 2.29, '', 1200, null, false, 2423, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('326', 143, 1, 115, 108.79, 4.88, 4.44, 6.7, 5, 'ESTOQUE', 1, 4, null, 4.3, '', 1200, null, false, 2424, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('288', 143, 1, 115, 116.7, 11.55, 12.13, 13.12, 5, 'EM_USO', 1, 4, null, 11.17, '', 1200, null, false, 2427, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('581', 160, 4, 105, -1, -1, -1, -1, 5, 'ESTOQUE', 1, 4, null, -1, '', 1200, null, false, 2430, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('582', 159, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2431, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('583', 150, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2432, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('333', 143, 1, 115, 114.77, 6.63, 7.14, 8.44, 5, 'EM_USO', 1, 4, null, 6.42, '', 1200, null, false, 2437, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('221', 137, 2, 105, 115.39, 10, 10, 10, 5, 'EM_USO', 4, 4, 9, 10, '', 1200, null, false, 2450, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('228', 158, 3, 115, 115.22, 16, 16, 16, 5, 'EM_USO', 3, 4, 13, 16, '', 1200, null, false, 2451, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('224', 137, 2, 105, 96.32, 7.43, 5.06, 6.03, 5, 'DESCARTE', 1, 4, null, 5.8, '', 1200, null, false, 2452, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('227', 158, 3, 115, 118.19, 7.49, 7.43, 8.4, 5, 'DESCARTE', 1, 4, null, 7.43, '', 1200, null, false, 2453, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('222', 137, 2, 105, 111.02, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 2455, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('585', 156, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2459, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('385', 143, 1, 115, 108.79, 5.26, 4.03, 8.2, 215, 'EM_USO', 1, 4, null, 4.61, '', 1200, null, false, 2461, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('334', 143, 1, 115, 109.79, 9.28, 7.17, 8.86, 5, 'EM_USO', 1, 4, null, 6.75, '', 1200, null, false, 2462, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('335', 143, 1, 115, 111.89, 14.05, 13.72, 14.4, 5, 'EM_USO', 1, 4, null, 14.37, '', 1200, null, false, 2463, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('584', 156, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2466, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('426', 150, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2468, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('427', 133, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 2469, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('pro10', 132, 6, 115, 0, 14, 14, 14, 103, 'ESTOQUE', 1, 5, null, 47, '', 1400, null, false, 2471, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('386', 143, 1, 115, 0, 15, 15, 15, 215, 'EM_USO', 2, 4, 11, 15, '', 1200, null, false, 2485, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('617', 150, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2487, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('618', 156, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2488, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('619', 156, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2505, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('620', 159, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2506, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('621', 156, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2507, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('622', 160, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2508, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('623', 159, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2509, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('465', 136, 4, 105, 100.5, 7.31, 7.41, 8.85, 5, 'EM_USO', 1, 4, null, 7.58, '', 1200, null, false, 2510, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('466', 133, 4, 105, 100.5, 10.9, 10.42, 10.52, 5, 'EM_USO', 1, 4, null, 10.25, '', 1200, null, false, 2511, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('443', 143, 1, 115, 124.97, 18.49, 18.25, 19.17, 5, 'ESTOQUE', 1, 4, null, 16.92, '', 1200, null, false, 2513,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('161', 137, 2, 105, 108.04, 10.58, 9.89, 10.64, 5, 'DESCARTE', 1, 4, null, 8.76, '', 1200, null, false, 2515, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('388', 143, 1, 115, 104.22, 0.55, 2.5, 0.55, 215, 'EM_USO', 1, 4, null, 0.39, '', 1200, null, false, 2518, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('299', 143, 1, 115, 112.13, 7.85, 7.98, 7.98, 5, 'DESCARTE', 1, 4, null, 8.14, '', 1200, null, false, 2519, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('448', 155, 2, 105, 86.87, 4.34, 5.03, 7.1, 5, 'ESTOQUE', 1, 4, null, 6.91, '', 1200, null, false, 2526, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('449', 137, 2, 105, 95.97, 7.36, 7.2, 6.91, 5, 'ESTOQUE', 1, 4, null, 6.68, '', 1200, null, false, 2527, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('450', 137, 2, 105, 97.72, 7.49, 6.97, 7.49, 5, 'ESTOQUE', 1, 4, null, 7.43, '', 1200, null, false, 2528, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('451', 137, 2, 105, 100, 4.18, 4.67, 4.25, 5, 'EM_USO', 1, 4, null, 3.99, '', 1200, null, false, 2529, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('452', 137, 2, 105, 95.62, 9.02, 8.53, 8.98, 5, 'EM_USO', 1, 4, null, 8.56, '', 1200, null, false, 2530, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('453', 137, 2, 105, 95.45, 7.95, 8.3, 8.21, 5, 'EM_USO', 1, 4, null, 8.3, '', 1200, null, false, 2531, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('454', 137, 2, 105, 105.24, 6.32, 6.26, 7.33, 5, 'EM_USO', 1, 4, null, 6.68, '', 1200, null, false, 2532, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('455', 133, 2, 105, 106.99, 11.32, 9.93, 11.74, 5, 'EM_USO', 1, 4, null, 10.74, '', 1200, null, false, 2533, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('456', 137, 2, 105, 105.94, 5.22, 5.45, 5.93, 5, 'EM_USO', 1, 4, null, 6.26, '', 1200, null, false, 2534, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('124', 137, 2, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 2539, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('442', 143, 1, 115, 100.5, 17.5, 17.5, 17.5, 5, 'EM_USO', 1, 4, null, 17.5, '', 1200, null, false, 2551, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('505', 133, 2, 105, 103.67, 12.23, 11.78, 13.37, 215, 'EM_USO', 1, 4, null, 11.81, '', 1200, null, false, 2564,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('217', 143, 1, 115, 121.69, 15.5, 15.5, 15.5, 5, 'EM_USO', 2, 4, 11, 15.5, '', 1200, null, false, 2566, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('218', 137, 2, 105, 108.04, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 2567, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('22', 137, 2, 105, null, 15, 15, 15, 5, 'EM_USO', 2, 4, 11, 15, '', 1200, null, false, 2568, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('564', 150, 4, 105, 100.5, 6.35, 5.84, 8.17, 5, 'EM_USO', 1, 4, null, 6.08, '', 1200, null, false, 2573, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('565', 156, 4, 105, 100.5, 11.58, 12.17, 10.39, 5, 'EM_USO', 1, 4, null, 11.04, '', 1200, null, false, 2574, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('566', 156, 4, 105, 100.5, 9.43, 8.64, 10.8, 5, 'EM_USO', 1, 4, null, 9.5, '', 1200, null, false, 2575, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('567', 156, 4, 105, 100.5, 0.64, 0.71, 9.84, 5, 'EM_USO', 1, 4, null, 9.09, '', 1200, null, false, 2576, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('568', 156, 4, 105, 100.5, 9.09, 8.85, 10.18, 5, 'EM_USO', 1, 4, null, 9.23, '', 1200, null, false, 2577, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('569', 159, 4, 105, 100.5, 9.4, 6.9, 8.23, 5, 'EM_USO', 1, 4, null, 5.94, '', 1200, null, false, 2578, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('570', 150, 4, 105, 100.5, 7.07, 6.63, 7.41, 5, 'EM_USO', 1, 4, null, 6.46, '', 1200, null, false, 2579, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('571', 133, 4, 105, 100.5, 11.41, 11.41, 12.68, 5, 'EM_USO', 1, 4, null, 12.23, '', 1200, null, false, 2580, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('517', 143, 1, 115, 108.26, 7.21, 4.92, 8.82, 5, 'EM_USO', 1, 4, null, 5.67, '', 1200, null, false, 2581, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('518', 133, 2, 105, 108.39, 12.39, 11.61, 12.65, 215, 'EM_USO', 1, 4, null, 11.42, '', 1200, null, false, 2582,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('519', 133, 2, 105, 96.5, 11.78, 11.09, 11.39, 5, 'EM_USO', 1, 4, null, 10.28, '', 1200, null, false, 2583, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('520', 133, 2, 105, 103.49, 12.3, 11.65, 11.78, 5, 'EM_USO', 1, 4, null, 7.59, '', 1200, null, false, 2584, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('524', 137, 2, 105, 104.72, 4.05, 4.93, 6.49, 5, 'ESTOQUE', 1, 4, null, 6.29, '', 1200, null, false, 2585, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('525', 137, 2, 105, 109.79, 7.88, 8.17, 9.63, 5, 'DESCARTE', 1, 4, null, 8.73, '', 1200, null, false, 2586, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('526', 137, 2, 105, 100.69, 5.74, 5.77, 7.33, 5, 'EM_USO', 1, 4, null, 6.29, '', 1200, null, false, 2587, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('527', 137, 2, 105, 95.8, 9.63, 9.15, 8.82, 5, 'EM_USO', 1, 4, null, 9.28, '', 1200, null, false, 2588, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('528', 137, 2, 105, 93.52, 6.94, 7.65, 9.54, 5, 'DESCARTE', 1, 4, null, 8.27, '', 1200, null, false, 2589, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('529', 137, 2, 105, 92.65, 6.42, 6.81, 7.56, 5, 'EM_USO', 1, 4, null, 7.26, '', 1200, null, false, 2590, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('530', 137, 2, 105, 101.39, 4.38, 4.77, 6.03, 5, 'EM_USO', 1, 4, null, 5.38, '', 1200, null, false, 2591, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('219', 137, 2, 105, 103.14, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 2622, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('220', 137, 2, 105, 111.89, 8.86, 9.41, 9.73, 5, 'DESCARTE', 1, 4, null, 9.96, '', 1200, null, false, 2623, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('586', 143, 1, 115, 113.01, 7.93, 6.83, 8.44, 215, 'EM_USO', 1, 4, null, 7.35, '', 1200, null, false, 2661, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('587', 143, 1, 115, 113.19, 4.54, 4.3, 4.92, 215, 'EM_USO', 1, 4, null, 4.82, '', 1200, null, false, 2669, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('588', 143, 1, 115, 103.51, 10.18, 6.87, 8.75, 215, 'EM_USO', 1, 4, null, 5.88, '', 1200, null, false, 2670, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('589', 143, 1, 115, 126.38, 7.55, 7.65, 9.4, 215, 'EM_USO', 1, 4, null, 7.11, '', 1200, null, false, 2671, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('590', 143, 1, 115, 106.85, 7.48, 7.04, 8.92, 215, 'EM_USO', 1, 4, null, 8.06, '', 1200, null, false, 2682, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('599', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 215, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 2683, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('601', 143, 1, 115, 117.93, 8.03, 8.61, 10.01, 215, 'EM_USO', 1, 4, null, 7.38, '', 1200, null, false, 2684, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('602', 143, 1, 115, 107.73, 5.94, 4.44, 5.77, 215, 'EM_USO', 1, 4, null, 3.93, '', 1200, null, false, 2709, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('603', 160, 4, 105, 0.0000000000000000000000000000000000001, 17, 17, 17, 215, 'EM_USO', 1, 4, null, 17, '',
        1200, null, false, 2710, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('604', 133, 2, 105, 107.87, 12.04, 11.32, 11.91, 5, 'ESTOQUE', 1, 4, null, 10.77, '', 1200, null, false, 2711,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('605', 137, 2, 105, 105.77, 2.69, 3.44, 3.69, 5, 'ESTOQUE', 1, 4, null, 3.86, '', 1200, null, false, 2712, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('606', 145, 2, 105, 109.27, 5.38, 5.48, 4.08, 5, 'ESTOQUE', 1, 4, null, 5.06, '', 1200, null, false, 2713, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('607', 137, 2, 105, 88.97, 6.03, 6.32, 7.65, 5, 'ESTOQUE', 1, 4, null, 7.17, '', 1200, null, false, 2714, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('608', 137, 2, 105, 97.37, 5.84, 7.07, 6.42, 5, 'ESTOQUE', 1, 4, null, 7.49, '', 1200, null, false, 2719, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('609', 137, 2, 105, 104.89, 6.78, 7.04, 7.72, 5, 'ESTOQUE', 1, 4, null, 7.52, '', 1200, null, false, 2720, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('610', 137, 2, 105, 105.94, 12.17, 11.94, 11, 5, 'ANALISE', 1, 4, null, 12.23, '', 1200, null, false, 2742, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('611', 137, 2, 105, 105.59, 12.17, 12.49, 12.43, 5, 'ESTOQUE', 1, 4, null, 12.55, '', 1200, null, false, 2743,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('612', 133, 2, 105, 100.69, 9.7, 10.35, 11.97, 5, 'ESTOQUE', 1, 4, null, 11.78, '', 1200, null, false, 2744, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('613', 137, 2, 105, 94.22, 8.34, 9.02, 9.5, 5, 'ESTOQUE', 1, 4, null, 9.08, '', 1200, null, false, 2745, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('2134', 135, 1, 120, 0, 16, 16, 16, 103, 'ESTOQUE', 2, 4, 13, 16, '4516', 0.01, null, false, 2764, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('1234', 132, 1, 120, 0, 10, 10, 10, 103, 'EM_USO', 1, 4, null, 10, '4517', 0.01, null, false, 2769, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('123', 137, 2, 105, 98.25, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 2799, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('12356', 137, 1, 120, 0, 10, 10, 10, 103, 'EM_USO', 1, 4, null, 10, '1236', 1, null, false, 2817, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('667', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '3217',
        1200, null, false, 2984, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('670', 143, 1, 115, 113.12, 4.15, 3.34, 7.78, 5, 'ESTOQUE', 1, 4, null, 4.99, '', 1200, null, false, 2985, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('671', 137, 2, 105, 102.97, 4.83, 4.15, 6.29, 5, 'ESTOQUE', 1, 4, null, 5.29, '', 1200, null, false, 2986, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('672', 137, 2, 105, 101.92, 8.24, 6.78, 4.47, 5, 'ESTOQUE', 1, 4, null, 4.57, '', 1200, null, false, 2987, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('673', 137, 2, 105, 114.17, 10.48, 12.26, 12.52, 5, 'ESTOQUE', 1, 4, null, 12.46, '', 1200, null, false, 2988,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('674', 137, 2, 105, 109.27, 7.46, 6.42, 8.34, 5, 'ESTOQUE', 1, 4, null, 6.65, '', 1200, null, false, 2989, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('678', 143, 1, 115, 107.91, 9.77, 6.18, 7.69, 5, 'ESTOQUE', 1, 4, null, 5.23, '', 1200, null, false, 2990, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('679', 143, 1, 115, 107.21, 5.02, 4.95, 9.91, 5, 'ESTOQUE', 1, 4, null, 6.22, '', 1200, null, false, 2991, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('680', 143, 1, 115, 102.98, 5.29, 4.37, 9.6, 5, 'ESTOQUE', 1, 4, null, 5.6, '', 1200, null, false, 2992, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('684', 143, 1, 115, 125.32, 19.11, 19.28, 19.62, 5, 'ESTOQUE', 1, 4, null, 19.11, '', 1200, null, false, 2994,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('685', 143, 1, 115, 112.07, 4.05, 2.49, 6.94, 5, 'ESTOQUE', 1, 4, null, 2.49, '', 1200, null, false, 2995, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('686', 143, 1, 115, 114.87, 11.68, 8.98, 11.48, 5, 'ESTOQUE', 1, 4, null, 8.98, '', 1200, null, false, 2996, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('689', 143, 1, 115, 116.35, 9.02, 8.88, 9.47, 5, 'ESTOQUE', 1, 4, null, 6.35, '', 1200, null, false, 2998, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('690', 137, 2, 105, 87.57, 5.8, 6.13, 6.42, 5, 'ESTOQUE', 1, 4, null, 6.29, '', 1200, null, false, 2999, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('691', 137, 2, 105, 120.99, 9.37, 10.22, 9.8, 5, 'ESTOQUE', 1, 4, null, 10.64, '', 1200, null, false, 3000, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('645', 143, 1, 115, 0, 9.86, 9.24, 13.73, 5, 'EM_USO', 1, 4, null, 9.15, '', 1200, null, false, 3003, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('646', 143, 1, 115, 102.98, 6.94, 2.73, 4.23, 5, 'ESTOQUE', 1, 4, null, 1.16, '', 1200, null, false, 3015, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('648', 143, 1, 115, 0, 27.28, 28.38, 0, 5, 'EM_USO', 1, 4, null, 28.4, '', 1200, null, false, 3016, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('649', 143, 1, 115, 108.44, 13.26, 12.92, 13.88, 5, 'EM_USO', 1, 4, null, 10.32, '', 1200, null, false, 3017, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('650', 143, 1, 115, 103.34, 10.05, 10.46, 10.87, 5, 'EM_USO', 1, 4, null, 8.85, '', 1200, null, false, 3018, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('651', 143, 1, 115, 104.57, 9.09, 9.19, 9.64, 5, 'EM_USO', 1, 4, null, 8.61, '', 1200, null, false, 3019, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('652', 143, 1, 115, 100.7, 8.06, 5.6, 5.29, 5, 'EM_USO', 1, 4, null, 4.3, '', 1200, null, false, 3020, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('653', 143, 1, 115, 103.16, 4.54, 2.32, 7.21, 5, 'EM_USO', 1, 4, null, 4.27, '', 1200, null, false, 3021, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('654', 143, 1, 115, 126.9, 6.7, 2.22, 4.41, 5, 'EM_USO', 1, 4, null, 1.74, '', 1200, null, false, 3022, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('647', 143, 1, 115, 103.34, 3.72, 1.12, 6.29, 215, 'EM_USO', 1, 4, null, 1.7, '', 1200, null, false, 3035, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('294', 164, 1, 115, 134.29, 14.42, 14.7, 14.87, 5, 'EM_USO', 1, 4, null, 14.8, '', 1200, null, false, 3036, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('58', 143, 1, 115, 111.78, 14.76, 15.76, 15.17, 5, 'EM_USO', 1, 4, null, 14.46, '', 1200, null, false, 3037, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59', 156, 4, 105, 107.73, 6.52, 6.18, 5.77, 5, 'EM_USO', 1, 4, null, 5.74, '', 1200, null, false, 3038, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('87', 143, 1, 115, 0, 18.2, 13.72, 4.41, 5, 'EM_USO', 1, 4, null, 0.87, '', 1200, null, false, 3039, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('89', 157, 1, 115, 112.66, 17.29, 16.92, 17.53, 5, 'EM_USO', 1, 4, null, 17.09, '', 1200, null, false, 3040, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('90', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 103, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3041, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('127', 143, 1, 115, 118.29, 10.76, 10.63, 10.39, 5, 'EM_USO', 1, 4, null, 9.19, '', 1200, null, false, 3042, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('129', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3043, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('155', 143, 1, 115, 106.85, 12, 11.41, 11.58, 5, 'EM_USO', 1, 4, null, 10.73, '', 1200, null, false, 3044, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('156', 143, 1, 115, 119.07, 4.25, 2.53, 7.17, 5, 'EM_USO', 1, 4, null, 3.21, '', 1200, null, false, 3045, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('157', 143, 1, 115, 112.66, 11, 11.28, 12.82, 5, 'EM_USO', 1, 4, null, 11.72, '', 1200, null, false, 3046, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('159', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3047, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('205', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3048, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('232', 143, 1, 115, 110.49, 4.47, 3.31, 7.01, 5, 'EM_USO', 1, 4, null, 3.37, '', 1200, null, false, 3049, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('233', 163, 1, 115, -1, -1, -1, -1, 5, 'ANALISE', 1, 4, null, -1, '3717', 1200, null, false, 3050, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('234', 143, 1, 115, 109.44, 6.81, 5.87, 10.8, 5, 'EM_USO', 1, 4, null, 7.95, '', 1200, null, false, 3051, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('235', 157, 1, 115, 116.88, 15.24, 15, 17.36, 5, 'EM_USO', 1, 4, null, 15.72, '', 1200, null, false, 3056, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('295', 143, 1, 115, 115.47, 10.9, 13.77, 13.36, 5, 'EM_USO', 1, 4, null, 13.53, '', 1200, null, false, 3057, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('297', 143, 1, 115, 106.33, 12.2, 10.15, 13.02, 5, 'EM_USO', 1, 4, null, 11.21, '', 1200, null, false, 3058, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('298', 143, 1, 115, 127.08, 14.73, 14.42, 14.76, 5, 'EM_USO', 1, 4, null, 15.21, '', 1200, null, false, 3059, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('300', 143, 1, 115, 21.25, 2.88, 3.37, 3.39, 103, 'EM_USO', 1, 4, null, 3.35, '', 1200, null, false, 3060, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('301', 143, 1, 115, 115.65, 14.29, 13.26, 13.26, 5, 'EM_USO', 1, 4, null, 13.05, '', 1200, null, false, 3061, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('305', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3071, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('306', 163, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '3717', 1200, null, false, 3072, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('307', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3073, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('312', 157, 1, 115, 117.06, 14.59, 13.6, 16.44, 5, 'EM_USO', 1, 4, null, 12.2, '', 1200, null, false, 3074, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('313', 163, 3, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3075, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('314', 143, 1, 115, 118.11, 6.66, 5.05, 7.07, 5, 'EM_USO', 1, 4, null, 5.26, '', 1200, null, false, 3076, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('315', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3077, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('316', 157, 1, 115, 117.93, 11.62, 11.04, 12.44, 5, 'EM_USO', 1, 4, null, 10.73, '', 1200, null, false, 3078, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('317', 143, 1, 115, 125.5, 7.65, 6.63, 8.41, 5, 'EM_USO', 1, 4, null, 7.14, '', 1200, null, false, 3079, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('318', 143, 1, 115, 114.59, 8.27, 8.85, 8.99, 5, 'EM_USO', 1, 4, null, 7.86, '', 1200, null, false, 3080, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('319', 143, 1, 115, 115.12, 10.25, 9.16, 9.47, 5, 'EM_USO', 1, 4, null, 7.58, '', 1200, null, false, 3081, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('320', 143, 1, 115, 103.34, 7.79, 8.03, 9.43, 5, 'EM_USO', 1, 4, null, 7.58, '', 1200, null, false, 3082, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('321', 157, 1, 115, 102.81, 14.94, 13.6, 14.7, 5, 'EM_USO', 1, 4, null, 14.35, '', 1200, null, false, 3083, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('322', 143, 1, 115, 100.87, 12, 12.82, 12.71, 5, 'EM_USO', 1, 4, null, 13.74, '', 1200, null, false, 3084, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('323', 143, 1, 115, 109.67, 13.53, 12.64, 13.33, 5, 'EM_USO', 1, 4, null, 14.59, '', 1200, null, false, 3085, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('324', 143, 1, 115, 106.85, 8.54, 6.94, 7.89, 5, 'EM_USO', 1, 4, null, 6.8, '', 1200, null, false, 3086, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('327', 143, 1, 115, 114.59, 11.93, 12.13, 12.06, 5, 'EM_USO', 1, 4, null, 11.14, '', 1200, null, false, 3087, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('328', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3717',
        1200, null, false, 3088, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('329', 143, 1, 115, 100.52, 14.52, 14.63, 15.41, 5, 'EM_USO', 1, 4, null, 15.11, '', 1200, null, false, 3089, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('330', 143, 1, 115, 126.02, 12.44, 12.51, 13.12, 5, 'EM_USO', 1, 4, null, 12.2, '', 1200, null, false, 3090, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('332', 143, 1, 115, 110.2, 12.78, 13.26, 13.64, 5, 'EM_USO', 1, 4, null, 12.85, '', 1200, null, false, 3091, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('336', 143, 1, 115, 111.43, 14.56, 14.7, 14.56, 5, 'EM_USO', 1, 4, null, 14.29, '', 1200, null, false, 3092, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('337', 157, 1, 115, 118.64, 13.81, 13.6, 14.46, 5, 'EM_USO', 1, 4, null, 13.09, '', 1200, null, false, 3093, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('338', 143, 1, 115, 111.43, 9.74, 6.87, 7.69, 5, 'EM_USO', 1, 4, null, 5.98, '', 1200, null, false, 3094, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('339', 143, 1, 115, 112.31, 7.11, 7.35, 8.51, 5, 'EM_USO', 1, 4, null, 7.04, '', 1200, null, false, 3095, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('340', 143, 1, 115, 117.76, 9.36, 10.59, 9.23, 5, 'EM_USO', 1, 4, null, 7.96, '', 1200, null, false, 3096, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('341', 143, 1, 115, 122.16, 11.76, 12.37, 12.27, 5, 'EM_USO', 1, 4, null, 12.2, '', 1200, null, false, 3097, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('342', 143, 1, 115, 111.43, 12.78, 12.71, 13.12, 103, 'EM_USO', 1, 4, null, 12.54, '', 1200, null, false, 3098,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('343', 143, 1, 115, 118.64, 12.75, 12.75, 13.19, 5, 'EM_USO', 1, 4, null, 12.85, '', 1200, null, false, 3099, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('345', 143, 1, 115, 107.91, 11.86, 11.89, 12.03, 5, 'EM_USO', 1, 4, null, 10.97, '', 1200, null, false, 3100, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('346', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '3817',
        1200, null, false, 3101, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('347', 133, 4, 105, 90.9, 15.36, 15, 16.22, 103, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3102, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('348', 143, 1, 115, 115.65, 14.11, 13.84, 15.11, 5, 'EM_USO', 1, 4, null, 13.53, '', 1200, null, false, 3103, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('293', 159, 5, 115, 111.95, 14.25, 14.11, 14.76, 5, 'EM_USO', 1, 4, null, 14.18, '', 12003, null, false, 3104,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('344', 143, 1, 115, 113.01, 10.63, 12.92, 13.53, 5, 'ESTOQUE', 1, 4, null, 12.34, '', 1200, null, false, 3105,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('296', 163, 1, 115, 0.0000000000000000000000000000000000001, 15.5, 15.5, 15.5, 5, 'EM_USO', 2, 4, 12, 15.5,
        '3217', 1200, null, false, 3106, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('131', 143, 1, 115, 100.5, 15, 15, 15, 103, 'ESTOQUE', 2, 3, 31, 15, '', 1200, null, false, 3107, 3, 103, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('331', 143, 1, 115, 0, 19.52, 18.3, 17.69, 5, 'EM_USO', 1, 4, null, 18.6, '', 1200, null, false, 3108, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5', 163, 1, 115, 100.5, 15.5, 15.5, 15.5, 5, 'DESCARTE', 1, 4, null, 15.5, '3217', 1200, null, false, 3109, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('349', 143, 1, 115, 104.39, 12.82, 13.47, 13.57, 5, 'EM_USO', 1, 4, null, 12.92, '', 1200, null, false, 3110, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('350', 143, 1, 115, 106.99, 6.58, 3.14, 4.7, 5, 'EM_USO', 1, 4, null, 3.08, '', 1200, null, false, 3111, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('351', 143, 1, 115, 94.5, 12.5, 12.5, 12.5, 103, 'EM_USO', 1, 4, null, 12.5, '', 1200, null, false, 3112, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('353', 143, 1, 115, 116.53, 11.07, 10.9, 12.1, 5, 'EM_USO', 1, 4, null, 12, '', 1200, null, false, 3113, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('354', 143, 1, 115, 112.42, 8.66, 4.73, 5.77, 5, 'EM_USO', 1, 4, null, 4.25, '', 1200, null, false, 3114, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('355', 143, 1, 115, 123.91, 14.42, 14.49, 14.63, 5, 'EM_USO', 1, 4, null, 14.7, '', 1200, null, false, 3115, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('356', 143, 1, 115, 108.04, 9.83, 6.75, 8.82, 5, 'EM_USO', 1, 4, null, 6.29, '', 1200, null, false, 3116, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('357', 143, 1, 115, 102.63, 15, 15, 15, 5, 'ESTOQUE', 2, 4, 104, 15, '', 1200, null, false, 3117, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('358', 143, 1, 115, 102.63, 4.1, 3.04, 6.59, 5, 'ESTOQUE', 1, 4, null, 3.89, '', 1200, null, false, 3118, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('359', 143, 1, 115, 111.08, 9.12, 9.33, 9.43, 5, 'ESTOQUE', 1, 4, null, 8.85, '', 1200, null, false, 3119, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('360', 143, 1, 115, 99.47, 11.35, 11.24, 12.23, 5, 'EM_USO', 1, 4, null, 10.9, '', 1200, null, false, 3120, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('361', 143, 1, 115, 124.62, 6.83, 6.52, 9.19, 5, 'EM_USO', 1, 4, null, 7.76, '', 1200, null, false, 3121, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('362', 143, 1, 115, 127.26, 12.44, 13.02, 13.12, 5, 'EM_USO', 1, 4, null, 12.3, '', 1200, null, false, 3122, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('363', 143, 1, 115, 113.36, 9.88, 12.68, 12.13, 5, 'EM_USO', 1, 4, null, 11.72, '', 1200, null, false, 3123, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('364', 143, 1, 115, 100.5, 13.5, 13.5, 15.5, 103, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3124, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('365', 143, 1, 115, 97.5, 13.5, 13.5, 13.5, 103, 'EM_USO', 1, 4, null, 12.5, '', 1200, null, false, 3125, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('366', 143, 1, 115, 131.3, 12.23, 13.26, 12.64, 5, 'EM_USO', 1, 4, null, 10.18, '', 1200, null, false, 3128, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('367', 143, 1, 115, 102.46, 9.43, 9.33, 11.28, 5, 'ESTOQUE', 1, 4, null, 8.95, '', 1200, null, false, 3129, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('368', 143, 1, 115, 101.93, 10.63, 10.73, 9.33, 5, 'ESTOQUE', 1, 4, null, 8.61, '', 1200, null, false, 3130, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('369', 143, 1, 115, 0, 1.45, 1.87, 0.16, 5, 'EM_USO', 1, 4, null, 1.31, '', 1200, null, false, 3131, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('370', 143, 1, 115, 22.27, 2.26, 3.44, 2.41, 103, 'EM_USO', 1, 4, null, 3.54, '', 1200, null, false, 3132, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('371', 143, 1, 115, 75.37, 9.36, 9.26, 10.35, 5, 'EM_USO', 1, 4, null, 9.12, '', 1200, null, false, 3133, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('372', 143, 1, 115, 109.32, 6.56, 7, 9.12, 5, 'EM_USO', 1, 4, null, 7.55, '', 1200, null, false, 3135, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('373', 143, 1, 115, 114.24, 6.9, 4.95, 9.02, 5, 'EM_USO', 1, 4, null, 4.58, '', 1200, null, false, 3136, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('375', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3137, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('376', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3138, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('377', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3139, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('378', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3140, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('379', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3141, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('380', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3142, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('381', 143, 1, 115, 107.91, 9.43, 8.03, 9.02, 5, 'EM_USO', 1, 4, null, 7.76, '', 1200, null, false, 3143, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('382', 143, 1, 115, 98.59, 3.72, 2.87, 7.07, 5, 'EM_USO', 1, 4, null, 2.9, '', 1200, null, false, 3144, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('383', 143, 1, 115, 106.5, 7.38, 6.87, 9.29, 5, 'EM_USO', 1, 4, null, 7.65, '', 1200, null, false, 3145, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('387', 143, 1, 115, 112.13, 15.58, 14.63, 14.46, 5, 'EM_USO', 1, 4, null, 13.81, '', 1200, null, false, 3147, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('389', 143, 1, 115, 121.98, 12.06, 11.76, 12.75, 5, 'EM_USO', 1, 4, null, 11.14, '', 1200, null, false, 3148, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('390', 143, 1, 115, 107.17, 6.06, 5.22, 9.96, 5, 'EM_USO', 1, 4, null, 6.03, '', 1200, null, false, 3149, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('391', 143, 1, 115, 106.64, 5.51, 3.66, 6.78, 5, 'EM_USO', 1, 4, null, 4.02, '', 1200, null, false, 3150, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('392', 143, 1, 115, 98.41, 10.01, 10.32, 10.9, 5, 'EM_USO', 1, 4, null, 11.14, '', 1200, null, false, 3151, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('393', 143, 1, 115, 110.55, 6.15, 5.84, 8.06, 5, 'EM_USO', 1, 4, null, 5.67, '', 1200, null, false, 3152, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('394', 143, 1, 115, 102.46, 9.98, 9.57, 9.23, 5, 'EM_USO', 1, 4, null, 8.23, '', 1200, null, false, 3153, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('395', 143, 1, 115, 114.34, 4.77, 4.47, 8.76, 5, 'EM_USO', 1, 4, null, 5.16, '', 1200, null, false, 3154, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('396', 143, 1, 115, 104.89, 5.54, 2.92, 7.07, 5, 'EM_USO', 1, 4, null, 3.14, '', 1200, null, false, 3155, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('398', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 3156, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('399', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 3157, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('400', 143, 1, 115, 98.76, 7.11, 4.17, 6.42, 5, 'EM_USO', 1, 4, null, 3.76, '', 1200, null, false, 3158, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('401', 143, 1, 115, 104.74, 2.25, 1.09, 4.95, 5, 'EM_USO', 1, 4, null, 1.91, '', 1200, null, false, 3159, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('402', 143, 1, 115, 110.55, 11.76, 11.86, 10.94, 5, 'EM_USO', 1, 4, null, 10.83, '', 1200, null, false, 3160, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('403', 157, 1, 115, 116.18, 15.31, 15.69, 15.55, 5, 'EM_USO', 1, 4, null, 14.97, '', 1200, null, false, 3161, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('404', 143, 1, 115, 114.94, 8.41, 8.3, 7.96, 5, 'EM_USO', 1, 4, null, 6.32, '', 1200, null, false, 3162, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('405', 143, 1, 115, 113.71, 12.06, 12.27, 12.78, 103, 'EM_USO', 1, 4, null, 12.68, '', 1200, null, false, 3163,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('406', 143, 1, 115, 111.95, 12.1, 12.82, 13.23, 5, 'EM_USO', 1, 4, null, 13.12, '', 1200, null, false, 3164, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('408', 143, 1, 115, 96.65, 12.78, 12.58, 12.58, 5, 'EM_USO', 1, 4, null, 12.54, '', 1200, null, false, 3165, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('409', 143, 1, 115, 111.95, 11.17, 11.58, 12.68, 5, 'EM_USO', 1, 4, null, 12.54, '', 1200, null, false, 3166, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('410', 143, 1, 115, 110.37, 12.68, 12.51, 12.71, 5, 'EM_USO', 1, 4, null, 11.79, '', 1200, null, false, 3167, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('412', 143, 1, 115, 114.59, 11.89, 11.93, 13.36, 5, 'EM_USO', 1, 4, null, 10.05, '', 1200, null, false, 3168, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('413', 143, 1, 115, 113.71, 12.51, 12.99, 12.23, 5, 'EM_USO', 1, 4, null, 11.55, '', 1200, null, false, 3169, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('414', 157, 1, 115, 118.11, 15, 15, 15, 5, 'EM_USO', 3, 4, 10, 15, '', 1200, null, false, 3170, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('415', 143, 1, 115, 100, 11.41, 12.44, 12.54, 5, 'EM_USO', 1, 4, null, 12.44, '', 1200, null, false, 3171, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('411', 143, 1, 115, 114.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3172, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('407', 143, 1, 115, 0, 18.15, 17.83, 17.64, 5, 'EM_USO', 1, 4, null, 17.88, '', 1200, null, false, 3173, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('374', 143, 1, 115, 107.03, 7.26, 2.86, 0.84, 5, 'ESTOQUE', 1, 4, null, 7.65, '', 1200, null, false, 3174, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('416', 143, 1, 115, 99.64, 12.85, 13.6, 12, 5, 'EM_USO', 1, 4, null, 12.06, '', 1200, null, false, 3175, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('417', 143, 1, 115, 113.36, 12.2, 11.45, 10.66, 5, 'EM_USO', 1, 4, null, 9.23, '', 1200, null, false, 3176, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('418', 143, 1, 115, 112.48, 12.41, 12.06, 11.93, 5, 'EM_USO', 1, 4, null, 11.28, '', 1200, null, false, 3177, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('419', 143, 1, 115, 115.3, 11.62, 12.23, 12.03, 5, 'EM_USO', 1, 4, null, 12.13, '', 1200, null, false, 3178, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('420', 143, 1, 115, 19.85, 3.21, 3.73, 2.76, 103, 'EM_USO', 1, 4, null, 3.72, '', 1200, null, false, 3179, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('421', 143, 1, 115, 0, 3.13, 3.18, 3.19, 103, 'EM_USO', 1, 4, null, 3.13, '', 1200, null, false, 3180, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('422', 143, 1, 115, 28.7, 2.21, 3.27, 3.28, 103, 'EM_USO', 1, 4, null, 3.37, '', 1200, null, false, 3181, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('423', 143, 1, 115, 98.41, 11.65, 12.44, 12.85, 5, 'EM_USO', 1, 4, null, 10.9, '', 1200, null, false, 3182, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('424', 143, 1, 115, 98.41, 10.49, 10.39, 11.62, 5, 'EM_USO', 1, 4, null, 10.42, '', 1200, null, false, 3183, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('425', 157, 1, 115, 118.46, 15.76, 15.69, 15.93, 215, 'EM_USO', 1, 4, null, 15.28, '', 1200, null, false, 3184,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('444', 143, 1, 115, 106.68, 6.32, 5.43, 6.83, 5, 'EM_USO', 1, 4, null, 5.57, '', 1200, null, false, 3192, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('446', 137, 2, 105, 100.69, 2.59, 3.79, 3.73, 5, 'EM_USO', 1, 4, null, 3.79, '', 1200, null, false, 3193, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('447', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3194, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('508', 133, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 3195, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('469', 133, 4, 105, 111.78, 9.29, 9.88, 10.11, 5, 'EM_USO', 1, 4, null, 10.18, '', 1200, null, false, 3196, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('470', 156, 4, 105, 108.61, 7.41, 7.38, 8, 5, 'EM_USO', 1, 4, null, 6.39, '', 1200, null, false, 3198, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('471', 156, 4, 105, 113.54, 7.62, 5.94, 7.96, 5, 'EM_USO', 1, 4, null, 6.7, '', 1200, null, false, 3199, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('472', 143, 4, 105, 108.44, 6.49, 6.18, 8.51, 5, 'EM_USO', 1, 4, null, 6.8, '', 1200, null, false, 3200, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('473', 133, 4, 105, 113.54, 10.76, 10.76, 9.57, 5, 'EM_USO', 1, 4, null, 9.36, '', 1200, null, false, 3201, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('491', 143, 1, 115, 117.41, 6.66, 9.09, 10.7, 5, 'DESCARTE', 1, 4, null, 8.2, '', 1200, null, false, 3209, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('429', 156, 4, 105, 100.5, 8, 6.59, 9.19, 5, 'EM_USO', 1, 4, null, 7.17, '', 1200, null, false, 3210, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('457', 133, 4, 105, 100.5, 9.09, 9.4, 10.63, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 3211, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('428', 159, 4, 105, 100.5, 10.42, 9.4, 10.32, 5, 'EM_USO', 1, 4, null, 9.91, '', 1200, null, false, 3212, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('483', 150, 4, 105, 100.5, 16.26, 16.39, 16.09, 5, 'EM_USO', 1, 4, null, 16.26, '', 1200, null, false, 3213, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('430', 150, 4, 105, 100.5, 6.42, 6.46, 7.38, 5, 'EM_USO', 1, 4, null, 7.79, '', 1200, null, false, 3214, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('431', 143, 4, 105, 100.5, 9.53, 8.2, 8.88, 5, 'EM_USO', 1, 4, null, 7.17, '', 1200, null, false, 3215, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('468', 150, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 2, 4, 13, 15.5, '', 1200, null, false, 3216, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('458', 159, 4, 105, 100.5, 7.72, 2.08, 6.49, 5, 'EM_USO', 1, 4, null, 6.9, '', 1200, null, false, 3217, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('467', 133, 4, 105, 100.5, 11.5, 11.5, 11.5, 5, 'EM_USO', 1, 4, null, 11.5, '', 1200, null, false, 3218, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('459', 156, 4, 105, 100.5, 2.35, 3.76, 6.08, 5, 'EM_USO', 1, 4, null, 4.23, '', 1200, null, false, 3219, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('460', 156, 4, 105, 100.5, 10.08, 9.4, 8.23, 5, 'EM_USO', 1, 4, null, 8.23, '', 1200, null, false, 3220, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('461', 150, 4, 105, 100.5, 7.07, 8.13, 9.05, 5, 'EM_USO', 1, 4, null, 7.86, '', 1200, null, false, 3221, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('462', 156, 4, 105, 100.5, 8.34, 7.72, 9.64, 5, 'EM_USO', 1, 4, null, 7.31, '', 1200, null, false, 3222, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('463', 156, 4, 105, 100.5, 6.83, 6.7, 9.23, 5, 'EM_USO', 1, 4, null, 7.69, '', 1200, null, false, 3223, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('464', 156, 4, 105, 100.5, 8.3, 6.59, 7.86, 5, 'EM_USO', 1, 4, null, 6.73, '', 1200, null, false, 3224, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('492', 163, 1, 115, 0.0000000000000000000000000000000000001, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3217',
        1200, null, false, 3225, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('502', 163, 1, 115, 100.5, 15.5, 15.5, 15.5, 103, 'EM_USO', 1, 4, null, 15.5, '3217', 1200, null, false, 3226,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('503', 143, 1, 115, 103.51, 5.98, 5.29, 5.67, 5, 'EM_USO', 1, 4, null, 5.36, '', 1200, null, false, 3227, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('532', 163, 1, 115, 94.5, 1.5, 1.5, 1.5, 103, 'EM_USO', 1, 4, null, 1.5, '3217', 1200, null, false, 3228, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('540', 133, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 3239, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('542', 160, 4, 105, 109.67, 11.62, 9.88, 11, 5, 'EM_USO', 1, 4, null, 10.11, '', 1200, null, false, 3240, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('543', 150, 4, 105, 107.21, 9.64, 8.44, 9.81, 5, 'EM_USO', 1, 4, null, 9.05, '', 1200, null, false, 3241, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('544', 156, 4, 105, 107.03, 10.22, 11.21, 10.97, 5, 'EM_USO', 1, 4, null, 11, '', 1200, null, false, 3242, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('531', 133, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 3243, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('533', 150, 4, 105, 100.5, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 3244, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('541', 156, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 3245, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('545', 159, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 3249, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('546', 150, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 3250, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('474', 156, 4, 105, 0, 15.34, 15.45, 15.36, 5, 'EM_USO', 1, 4, null, 15.28, '', 1200, null, false, 3251, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('475', 159, 4, 105, 0, 12.88, 15.42, 12.57, 5, 'EM_USO', 1, 4, null, 15.67, '', 1200, null, false, 3252, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('476', 159, 4, 105, 0, 15.2, 15.34, 15.45, 5, 'EM_USO', 1, 4, null, 15.56, '', 1200, null, false, 3253, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('477', 156, 4, 105, 0, 15.28, 15.28, 15.34, 5, 'EM_USO', 1, 4, null, 15.28, '', 1200, null, false, 3254, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('553', 156, 4, 105, 111.08, 4.37, 4.17, 5.4, 5, 'EM_USO', 1, 4, null, 4.2, '', 1200, null, false, 3256, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('554', 159, 4, 105, 98.76, 9.81, 9.53, 8.68, 5, 'EM_USO', 1, 4, null, 9.16, '', 1200, null, false, 3257, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('555', 156, 4, 105, 108.79, 7.07, 6.49, 6.83, 5, 'EM_USO', 1, 4, null, 6.73, '', 1200, null, false, 3258, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('556', 161, 4, 105, 98.06, 10.83, 9.6, 9.88, 5, 'EM_USO', 1, 4, null, 9.16, '', 1200, null, false, 3259, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('557', 159, 4, 105, 111.08, 9.43, 8.64, 10.39, 5, 'EM_USO', 1, 4, null, 9.4, '', 1200, null, false, 3260, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('558', 156, 4, 105, 97.71, 10.46, 9.16, 8.71, 5, 'EM_USO', 1, 4, null, 8.85, '', 1200, null, false, 3261, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('559', 161, 4, 105, 97.18, 9.91, 8.34, 9.88, 5, 'EM_USO', 1, 4, null, 8.82, '', 1200, null, false, 3262, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('560', 156, 4, 105, 98.24, 9.19, 8.06, 9.33, 5, 'EM_USO', 1, 4, null, 7.69, '', 1200, null, false, 3263, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('561', 161, 4, 105, 96.3, 6.9, 5.98, 7.69, 5, 'EM_USO', 1, 4, null, 5.98, '', 1200, null, false, 3264, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('504', 143, 1, 115, 4.69, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3266, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('563', 159, 4, 105, 100.5, 8.78, 7.48, 8.1, 5, 'EM_USO', 1, 4, null, 7.65, '', 1200, null, false, 3267, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('562', 133, 4, 105, 100.5, 11.48, 11.58, 11.89, 5, 'EM_USO', 1, 4, null, 10.9, '', 1200, null, false, 3268, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('495', 143, 4, 105, 100.5, 8.5, 8.5, 8.5, 5, 'EM_USO', 1, 4, null, 8.5, '', 1200, null, false, 3271, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('496', 159, 4, 105, 100.5, 6.5, 6.5, 8.5, 5, 'EM_USO', 1, 4, null, 6.5, '', 1200, null, false, 3272, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('497', 160, 4, 105, 100.5, 9.5, 9.5, 9.5, 5, 'EM_USO', 1, 4, null, 9.5, '', 1200, null, false, 3273, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('498', 160, 4, 105, 100.5, 9.5, 9.5, 9.5, 5, 'EM_USO', 1, 4, null, 9.5, '', 1200, null, false, 3274, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('499', 156, 4, 105, 100.5, 6.5, 6.5, 6.5, 5, 'EM_USO', 1, 4, null, 6.5, '', 1200, null, false, 3275, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('500', 156, 4, 105, 100.5, 6.5, 6.5, 6.5, 5, 'EM_USO', 1, 4, null, 6.5, '', 1200, null, false, 3276, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('501', 143, 4, 105, 100.5, 6.5, 6.5, 6.5, 5, 'EM_USO', 1, 4, null, 6.5, '', 1200, null, false, 3277, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('506', 143, 4, 105, 100.5, 6.5, 6.5, 6.5, 5, 'EM_USO', 1, 4, null, 6.5, '', 1200, null, false, 3278, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('507', 133, 4, 105, 100.5, 12.5, 12.5, 12.5, 5, 'EM_USO', 1, 4, null, 12.5, '', 1200, null, false, 3279, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('591', 143, 1, 115, 118.99, 9.67, 9.67, 11.58, 5, 'EM_USO', 1, 4, null, 9.26, '', 1200, null, false, 3281, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('592', 157, 1, 115, 117.06, 12.54, 13.26, 14.7, 5, 'EM_USO', 1, 4, null, 12.27, '', 1200, null, false, 3282, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('593', 143, 1, 115, 116.7, 12.51, 12.99, 12.99, 5, 'EM_USO', 1, 4, null, 12.23, '', 1200, null, false, 3283, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('594', 143, 1, 115, 134.47, 12.61, 14.87, 14.76, 5, 'EM_USO', 1, 4, null, 14.76, '', 1200, null, false, 3284, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('595', 143, 1, 115, 120.57, 9.81, 9.94, 11.14, 5, 'EM_USO', 1, 4, null, 8.78, '', 1200, null, false, 3285, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('596', 143, 1, 115, 118.29, 15.38, 12.06, 15.07, 5, 'EM_USO', 1, 4, null, 15.07, '', 1200, null, false, 3286, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('597', 160, 4, 105, 109.84, 7.45, 9.36, 11.24, 5, 'EM_USO', 1, 4, null, 10.63, '', 1200, null, false, 3287, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('600', 143, 1, 115, 103.16, 6.8, 6.9, 8.1, 5, 'EM_USO', 1, 4, null, 7.24, '', 1200, null, false, 3288, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('626', 133, 4, 105, 92.43, 15.88, 15.25, 15.22, 103, 'EM_USO', 1, 4, null, 15.39, '', 1200, null, false, 3290,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('627', 143, 4, 105, 91.51, 15.25, 15, 14.92, 103, 'EM_USO', 1, 4, null, 15.11, '', 1200, null, false, 3291, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('628', 156, 4, 105, 93.04, 15.17, 15.19, 15.14, 103, 'EM_USO', 1, 4, null, 15.3, '', 1200, null, false, 3292, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('629', 156, 4, 105, 90.59, 16.66, 15.83, 16.3, 103, 'EM_USO', 1, 4, null, 15.83, '', 1200, null, false, 3293, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('630', 150, 4, 105, 88.45, 15.72, 15.47, 15.47, 103, 'EM_USO', 1, 4, null, 15.44, '', 1200, null, false, 3294,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('631', 152, 4, 105, 88.6, 15.25, 15.5, 15.36, 103, 'EM_USO', 1, 4, null, 15.72, '', 1200, null, false, 3297, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('632', 143, 1, 115, 110.2, 9.47, 9.23, 7.89, 5, 'EM_USO', 1, 4, null, 6.9, '', 1200, null, false, 3298, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('633', 143, 1, 115, 112.13, 14.01, 7.93, 15.45, 5, 'EM_USO', 1, 4, null, 13.57, '', 1200, null, false, 3299, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('634', 143, 1, 115, 115.3, 15.07, 14.63, 15.07, 5, 'EM_USO', 1, 4, null, 15.11, '', 1200, null, false, 3300, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('635', 143, 1, 115, 112.77, 8.63, 4.47, 7.01, 5, 'EM_USO', 1, 4, null, 2.82, '', 1200, null, false, 3301, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('636', 143, 1, 115, 110.37, 13.88, 13.91, 14.39, 5, 'EM_USO', 1, 4, null, 12.92, '', 1200, null, false, 3302, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('637', 157, 1, 115, 0.0000000000000000000000000000000000001, 17, 17, 17, 5, 'EM_USO', 1, 4, null, 17, '', 1200,
        null, false, 3303, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('638', 157, 1, 115, 0.0000000000000000000000000000000000001, 17, 17, 17, 5, 'EM_USO', 1, 4, null, 17, '', 1200,
        null, false, 3304, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('614', 150, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 3307, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('625', 161, 4, 105, 0, 15.31, 15.28, 12.49, 5, 'EM_USO', 1, 4, null, 15.23, '', 1200, null, false, 3313, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('639', 162, 4, 105, 88.75, 15.77, 15.91, 16.13, 103, 'EM_USO', 1, 4, null, 15.72, '', 1200, null, false, 3318,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('640', 156, 4, 105, 89.06, 15.47, 15.72, 15.77, 103, 'EM_USO', 1, 4, null, 15.25, '', 1200, null, false, 3319,
        3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('641', 143, 4, 105, 89.52, 15.8, 16.16, 16.3, 103, 'EM_USO', 1, 4, null, 15.14, '', 1200, null, false, 3320, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('642', 143, 1, 115, 8.79, 12.97, 11.6, 15.7, 5, 'EM_USO', 1, 4, null, 5.52, '', 1200, null, false, 3321, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('643', 143, 1, 115, 8.68, 0, 4.17, 12.87, 5, 'EM_USO', 1, 4, null, 17.41, '', 1200, null, false, 3322, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('644', 143, 1, 115, 0, 0, 0, 0, 5, 'EM_USO', 1, 4, null, 0, '', 1200, null, false, 3323, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('655', 143, 1, 115, 114.24, 3.72, 4.03, 8.78, 5, 'EM_USO', 1, 4, null, 4.75, '', 1200, null, false, 3324, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('656', 143, 1, 115, 100.52, 11, 10.08, 11.35, 5, 'EM_USO', 1, 4, null, 8.47, '', 1200, null, false, 3325, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('657', 143, 1, 115, 102.98, 7.82, 8.06, 9.02, 5, 'EM_USO', 1, 4, null, 8.03, '', 1200, null, false, 3326, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('659', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3327, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('660', 143, 1, 115, 113.71, 7.11, 6.01, 6.94, 5, 'EM_USO', 1, 4, null, 6.18, '', 1200, null, false, 3328, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('661', 143, 1, 115, 114.24, 13.26, 12.68, 12.51, 5, 'EM_USO', 1, 4, null, 12.2, '', 1200, null, false, 3329, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('662', 143, 1, 115, 114.94, 5.4, 3.79, 8, 5, 'EM_USO', 1, 4, null, 2.52, '', 1200, null, false, 3330, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('663', 143, 1, 115, 100.52, 11.45, 12.3, 13.6, 103, 'EM_USO', 1, 4, null, 12.92, '', 1200, null, false, 3331, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('664', 143, 1, 115, 94.5, 12.5, 12.5, 12.5, 103, 'EM_USO', 1, 4, null, 12.5, '', 1200, null, false, 3332, 3,
        103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('665', 143, 1, 115, 115.65, 12.3, 11.55, 12.37, 5, 'EM_USO', 1, 4, null, 12.2, '', 1200, null, false, 3333, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('573', 156, 4, 105, 100.5, 7.41, 5.94, 6.8, 5, 'EM_USO', 1, 4, null, 5.74, '', 1200, null, false, 3334, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('574', 156, 4, 105, 100.5, 7.14, 5.88, 8.82, 5, 'EM_USO', 1, 4, null, 6.39, '', 1200, null, false, 3335, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('572', 133, 4, 105, 100.5, 10.59, 10.39, 9.23, 5, 'EM_USO', 1, 4, null, 9.02, '', 1200, null, false, 3336, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('615', 156, 4, 105, 100.5, 10.11, 9.47, 8.37, 5, 'EM_USO', 1, 4, null, 8.75, '', 1200, null, false, 3337, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('575', 156, 4, 105, 100.5, 6.49, 5.12, 6.63, 5, 'EM_USO', 1, 4, null, 5.26, '', 1200, null, false, 3338, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('576', 133, 4, 105, 100.5, 0.34, 10.42, 9.81, 5, 'EM_USO', 1, 4, null, 9.67, '', 1200, null, false, 3340, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('616', 159, 4, 105, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3341, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('666', 143, 1, 115, 110.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3343, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('668', 143, 1, 115, 114.94, 8.99, 8.03, 8.92, 5, 'EM_USO', 1, 4, null, 6.46, '', 1200, null, false, 3344, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('669', 143, 1, 115, 114.24, 8.71, 6.83, 10.46, 5, 'EM_USO', 1, 4, null, 7.82, '', 1200, null, false, 3345, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('675', 143, 1, 115, 101.23, 7.76, 6.56, 8.61, 5, 'EM_USO', 1, 4, null, 7.21, '', 1200, null, false, 3346, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('676', 143, 1, 115, 100.7, 4.99, 4.44, 3.96, 5, 'EM_USO', 1, 4, null, 2.7, '', 1200, null, false, 3347, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('681', 143, 1, 115, 109.84, 9.36, 9.02, 11.04, 5, 'EM_USO', 1, 4, null, 9.26, '', 1200, null, false, 3348, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('682', 143, 1, 115, 104.74, 6.87, 6.32, 6.15, 5, 'EM_USO', 1, 4, null, 6.52, '', 1200, null, false, 3349, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('683', 143, 1, 115, 104.22, 5.81, 5.53, 7.82, 5, 'EM_USO', 1, 4, null, 6.15, '', 1200, null, false, 3350, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('692', 143, 1, 115, 124.62, 14.7, 14.7, 14.87, 5, 'EM_USO', 1, 4, null, 15.21, '', 1200, null, false, 3351, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('693', 143, 1, 115, 118.89, 8.53, 8.86, 10.45, 5, 'EM_USO', 1, 4, null, 9.7, '', 1200, null, false, 3352, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('694', 143, 1, 115, 102.11, 14.63, 13.84, 15.17, 5, 'EM_USO', 1, 4, null, 14.66, '', 1200, null, false, 3353, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('695', 143, 1, 115, 105.07, 7.62, 4.41, 4.38, 5, 'EM_USO', 1, 4, null, 3.11, '', 1200, null, false, 3354, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('696', 143, 1, 115, 126.9, 14.8, 14.8, 15.24, 5, 'EM_USO', 1, 4, null, 14.97, '', 1200, null, false, 3355, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('697', 143, 1, 115, 110.84, 7.72, 4.77, 6.13, 5, 'EM_USO', 1, 4, null, 4.08, '', 1200, null, false, 3356, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('698', 143, 1, 115, 119.17, 14.9, 14.94, 15.72, 5, 'EM_USO', 1, 4, null, 15.24, '', 1200, null, false, 3357, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('699', 143, 1, 115, 113.89, 10.9, 9.74, 12.95, 5, 'EM_USO', 1, 4, null, 11.11, '', 1200, null, false, 3358, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('677', 143, 1, 115, 4.34, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3359, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('687', 143, 1, 115, 4.69, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 3360, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('688', 143, 1, 115, 113.19, 6.83, 4.58, 8.44, 5, 'ESTOQUE', 1, 4, null, 6.56, '', 1200, null, false, 3361, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('658', 137, 2, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 3363, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('578', 133, 4, 105, 106.5, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 3364, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('testrr', 135, 3, 150, 0, 10, 10, 10, 5, 'EM_USO', 1, 4, null, 10, '', 0, null, false, 3374, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('tre', 135, 1, 150, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, '', 0, null, false, 3384, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('zartteste', 165, 1, 123, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, '1234', 1.23,
        '2018-04-04 14:41:21.258202', false, 4106, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('547', 156, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5078, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('548', 161, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5079, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('549', 156, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5080, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('550', 156, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5081, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('551', 150, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5082, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('552', 133, 4, 105, 100.5, 10.5, 10.5, 10.5, 5, 'EM_USO', 1, 4, null, 10.5, '', 1200, null, false, 5083, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('151', 158, 3, 115, 118.19, 8.24, 6.19, 5.64, 5, 'DESCARTE', 1, 4, null, 6.19, '', 1200, null, false, 5412, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('141', 137, 2, 105, 112.77, 15, 15, 15, 103, 'ESTOQUE', 6, 6, 11, 15, '', 1200, null, false, 5460, 3, 103,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('534', 150, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6114, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('535', 159, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6115, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('536', 143, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6116, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('537', 159, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6117, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('538', 150, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6118, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('539', 143, 4, 105, -1, -1, -1, -1, 5, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6119, 3, 5, false, null,
        null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('577', 133, 4, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 6120, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('14', 137, 2, 105, 112.59, 15, 15, 15, 5, 'DESCARTE', 3, 4, 11, 15, '', 1200, null, false, 6121, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('125', 143, 1, 115, 106.27, 16, 16, 16, 5, 'DESCARTE', 1, 4, null, 16, '', 1200, null, false, 6246, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('123teste', 136, 4, 150, 0, 15, 15, 15, 5, 'EM_USO', 2, 4, 11, 15, '7867', 15, '2018-04-17 00:09:18.220999',
        false, 6317, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('432', 145, 2, 105, 98.5, 7.14, 7.05, 7.31, 5, 'EM_USO', 1, 4, null, 7.08, '', 1200, null, false, 6525, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('433', 145, 2, 105, 26.37, 7.4, 6.91, 7.17, 5, 'EM_USO', 1, 4, null, 6.65, '', 1200, null, false, 6526, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('434', 145, 2, 105, 26.37, 5.79, 6.02, 7.17, 5, 'EM_USO', 1, 4, null, 6.71, '', 1200, null, false, 6527, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('435', 145, 2, 105, 26.17, 7.17, 7.54, 7.14, 5, 'EM_USO', 1, 4, null, 7.14, '', 1200, null, false, 6528, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('436', 145, 2, 105, 25.98, 7.34, 5.88, 6.77, 5, 'EM_USO', 1, 4, null, 6.77, '', 1200, null, false, 6529, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('437', 145, 2, 105, 26.17, 7.26, 5.51, 6.08, 5, 'EM_USO', 1, 4, null, 6.68, '', 1200, null, false, 6530, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('438', 145, 2, 105, 25.98, 5.77, 5.62, 7.74, 5, 'EM_USO', 1, 4, null, 6.65, '', 1200, null, false, 6531, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('439', 145, 2, 105, 26.17, 6.8, 7.14, 5.65, 5, 'EM_USO', 1, 4, null, 5.74, '', 1200, null, false, 6532, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('440', 145, 2, 105, 25.79, 5.71, 6.17, 7.34, 5, 'EM_USO', 1, 4, null, 6.54, '', 1200, null, false, 6533, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('441', 145, 2, 105, 26.17, 7.05, 7.03, 6.42, 5, 'EM_USO', 1, 4, null, 7.17, '', 1200, null, false, 6534, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('478', 156, 4, 105, 0, 12.57, 15.23, 15.31, 5, 'EM_USO', 1, 4, null, 15.25, '', 1200, null, false, 6571, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('479', 156, 4, 105, 0, 15.31, 15.2, 15.31, 5, 'EM_USO', 1, 4, null, 15.28, '', 1200, null, false, 6572, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('480', 159, 4, 105, 0, 15.25, 15.23, 15.34, 5, 'EM_USO', 1, 4, null, 15.2, '', 1200, null, false, 6603, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('481', 152, 4, 105, 0, 15.31, 15.31, 13.01, 5, 'EM_USO', 1, 4, null, 15.34, '', 1200, null, false, 6629, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('482', 156, 4, 105, 0, 15.31, 15.28, 12.46, 5, 'EM_USO', 1, 4, null, 15.28, '', 1200, null, false, 6630, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('tfoto', 291, 4, 6, 0, 25, 6, 6, 5, 'ESTOQUE', 1, 4, null, 6, '2020', 500, '2018-05-17 16:10:05.956200', true,
        8512, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('tfoto2', 136, 6, 150, 0, 5, 5, 5, 5, 'ESTOQUE', 1, 4, null, 5, '2520', 500, '2018-05-17 17:08:09.062857', true,
        8554, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('teste', 136, 6, 115, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '2808', 50, '2018-05-18 00:23:28.396011',
        true, 8674, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('foto3', 135, 1, 150, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '2020', 1500, '2018-05-21 21:12:46.981447',
        true, 9120, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ps3', 135, 6, 4, 0, 9, 5, 8, 5, 'ESTOQUE', 4, 4, 91, 8, '2568', 0.08, '2018-05-23 17:54:30.217968', false,
        9296, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('OH0189', 142, 15, 150, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, '2020', 150, '2018-06-03 20:39:08.398997',
        true, 10298, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('pb167', 136, 4, 100, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, '2828', 2500, '2018-06-07 21:28:18.923718',
        true, 10631, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('teste01', 291, 3, 120, 0, 1, 1, 1, 5, 'ESTOQUE', 2, 4, 31, 1, '1223', 200, '2018-06-12 11:43:19.185902', false,
        10883, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('278909', 135, 1, 150, 0, 15, 15, 15, 5, 'DESCARTE', 1, 4, null, 15, '2020', 12300,
        '2018-06-13 23:56:53.813902', true, 11056, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28917', 135, 3, 150, 0, 10.72, 11.26, 17.8, 5, 'ANALISE', 1, 4, null, 13.26, '2020', 1220,
        '2018-06-14 00:14:07.571114', true, 11057, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('teste12', 136, 1, 150, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '2020', 32323.23,
        '2018-06-18 20:26:44.660155', false, 11269, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('12354', 135, 3, 120, 0, 15, 15, 15, 5, 'DESCARTE', 1, 4, null, 15, '2020', 155, '2018-06-27 17:10:12.099069',
        true, 11728, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5657', 135, 2, 150, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '1718', 280, '2018-07-15 14:31:34.793166',
        true, 12440, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('7272alugag', 137, 13, 120, 0, 40, 40, 40, 5, 'ESTOQUE', 1, 4, null, 40, '0617', 0.01,
        '2018-07-23 17:28:01.655737', false, 12779, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('hrdh', 165, 3, 150, 0, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '1717', 15.58, '2018-08-03 20:00:13.095550',
        true, 13428, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('4ddd', 132, 4, 150, 0, 15.1, 15, 15, 5, 'ESTOQUE', 1, 4, null, 18, '1717', 5.55, '2018-08-03 20:03:07.486111',
        true, 13429, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ggff', 157, 5, 120, 0, 8, 8, 8, 5, 'ESTOQUE', 1, 4, null, 8, '1515', 0.25, '2018-08-08 11:41:33.623831', false,
        13657, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('1234567', 158, 5, 120, 0, 12, 12, 12, 103, 'ESTOQUE', 1, 4, null, 12, '1418', 1500,
        '2018-12-05 17:19:49.306747', true, 65311, 3, 103, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('HORIZONTE12345', 158, 1, 120, 0, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '1111', 1500,
        '2019-01-03 17:42:50.475065', false, 67909, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('sulc1', 165, 1, 120, 0, null, null, null, 5, 'ESTOQUE', 2, 4, 9, null, '2020', 1200,
        '2019-01-12 01:56:52.554456', false, 68091, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('hgf', 135, 23, 150, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '1514', 1200,
        '2019-03-15 11:37:04.959843', true, 81371, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('rodalog', 147, 1, 110, 0, 18, 18, 18, 5, 'ESTOQUE', 2, 4, 461, 18, '1218', 1400, '2019-03-28 13:52:31.157319',
        false, 83888, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('2323', 138, 3, 110, 100.5, 15.5, 15.5, 15.5, 5, 'ANALISE', 1, 4, null, 15.5, '0614', 1530,
        '2019-04-07 19:25:54.960820', false, 85732, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('2324', 159, 3, 110, 0, null, null, null, 5, 'DESCARTE', 1, 4, null, null, '0919', 153,
        '2019-04-07 19:27:48.822102', false, 85733, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('bomlog1', 1051, 1, 120, 0, 18, 18, 18, 5, 'ESTOQUE', 3, 4, 507, 18, '1218', 1450, '2019-04-11 16:30:12.030957',
        false, 87849, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('bomlog2', 1052, 1, 120, 113.2, 13.96, 14.3, 14.53, 5, 'ESTOQUE', 3, 4, 11, 14.3, '1318', 1450,
        '2019-04-11 16:33:10.937807', false, 87850, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('COD01', 135, 5, 120, 0, null, null, null, 5, 'ANALISE', 3, 4, 11, null, '1214', 1500,
        '2019-04-15 17:33:32.410432', false, 88217, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('cod02', 165, 23, 120, 0, null, null, null, 5, 'ESTOQUE', 2, 4, 9, null, '1214', 1000,
        '2019-04-15 17:38:25.253566', false, 88218, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('P0001', 165, 19, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '1212', 150,
        '2019-04-18 12:11:58.216435', true, 89474, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('37611', 136, 1, 110, 0, null, null, null, 215, 'EM_USO', 1, 4, null, null, '0619', 1000,
        '2019-04-22 19:23:21.097827', false, 90117, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63438', 137, 1, 115, 0, 16.33, 16.14, 16.25, 179, 'DESCARTE', 1, 2, null, 16.02, '3256', 1200,
        '2019-06-05 11:51:54.271383', true, 100875, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28654', 135, 1, 115, 111.36, 2.85, 2.6, 4.46, 179, 'EM_USO', 3, 4, 9, 2.6, '3277', 1500,
        '2019-06-05 11:51:54.271383', false, 100876, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63439', 165, 1, 115, 0, 16.28, 16.11, 16.33, 179, 'ESTOQUE', 1, 2, null, 16.39, '3298', 1000,
        '2019-06-05 11:51:54.271383', true, 100877, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60452', 1051, 1, 115, 112.01, 7.99, 7.94, 7.66, 179, 'EM_USO', 1, 5, null, 7.66, '3319', 1450,
        '2019-06-05 11:51:54.271383', true, 100878, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56596', 136, 1, 115, 0, 22.1, 22.26, 21.64, 179, 'EM_USO', 1, 4, null, 21.2, '3340', 1000,
        '2019-06-05 11:51:54.271383', false, 100879, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63498', 137, 1, 115, 0, 15.97, 15.97, 16.28, 179, 'EM_USO', 1, 4, null, 16.25, '3361', 1200,
        '2019-06-05 11:51:54.271383', true, 100880, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28632', 135, 1, 115, 115.14, 12.29, 11.84, 12.21, 179, 'EM_USO', 3, 4, 11, 11.84, '3382', 280,
        '2019-06-05 11:51:54.271383', false, 100881, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28675', 143, 1, 115, 113.52, 16, 16, 16, 179, 'EM_USO', 4, 4, 395, 16, '3403', 1200,
        '2019-06-05 11:51:54.271383', false, 100882, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('528591', 165, 1, 115, 115.47, 5.22, 3.44, 3.42, 179, 'ESTOQUE', 3, 4, 13, 3.44, '3424', 150,
        '2019-06-05 11:51:54.271383', false, 100883, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60207', 137, 1, 115, 112.01, 11.33, 12.66, 11.95, 179, 'ESTOQUE', 1, 4, null, 12.66, '3445', 1200,
        '2019-06-05 11:51:54.271383', false, 100884, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63495', 137, 29, 1, 0, null, null, null, 179, 'EM_USO', 1, 2, null, null, '3466', 1200,
        '2019-06-05 11:51:54.271383', true, 100885, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63494', 137, 29, 1, 0, null, null, null, 179, 'EM_USO', 1, 2, null, null, '3487', 1200,
        '2019-06-05 11:51:54.271383', true, 100886, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59503', 137, 9, 0, 0, 18, 18, 18, 179, 'EM_USO', 1, 2, null, 18, '3508', 1200, '2019-06-05 11:51:54.271383',
        false, 100887, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63496', 158, 29, 1, 0, null, null, null, 179, 'EM_USO', 1, 2, null, null, '3529', 1200,
        '2019-06-05 11:51:54.271383', true, 100888, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63493', 143, 29, 1, 0, null, null, null, 179, 'EM_USO', 1, 2, null, null, '3550', 1200,
        '2019-06-05 11:51:54.271383', true, 100889, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('135561', 137, 1, 115, 112.51, 15, 15, 15, 179, 'ANALISE', 3, 4, 11, 15, '3571', 1500,
        '2019-06-05 11:51:54.271383', false, 100890, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28639', 158, 1, 115, 108.24, 6.5, 5.84, 8.85, 179, 'ESTOQUE', 2, 4, 13, 5.84, '3592', 1200,
        '2019-06-05 11:51:54.271383', false, 100891, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('33852', 163, 1, 115, 108.24, 2.82, 3.02, 6.95, 179, 'DESCARTE', 2, 4, 13, 2.82, '3613', 1200,
        '2019-06-05 11:51:54.271383', false, 100892, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28627', 143, 1, 115, 112.01, 11.98, 9.04, 10.99, 179, 'DESCARTE', 4, 4, 11, 9.04, '3634', 1200,
        '2019-06-05 11:51:54.271383', false, 100893, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('52858', 132, 1, 115, 105.28, 2.17, 2.51, 3.19, 179, 'ESTOQUE', 2, 4, 56, 2.51, '3655', 1200,
        '2019-06-05 11:51:54.271383', false, 100894, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('51782', 158, 3, 115, 0, 11, 11, 11, 179, 'ESTOQUE', 2, 4, 13, 11, '3676', 1200, '2019-06-05 11:51:54.271383',
        false, 100895, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13072', 143, 1, 115, 113.49, 15, 15, 15, 179, 'DESCARTE', 5, 5, 11, 15, '3697', 1220,
        '2019-06-05 11:51:54.271383', false, 100896, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28695', 135, 1, 115, 111.03, 3.1, 2.88, 4.63, 179, 'ANALISE', 3, 4, 9, 2.88, '3718', 500,
        '2019-06-05 11:51:54.271383', false, 100897, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28683', 291, 1, 115, 119.24, 5.31, 3.27, 5.11, 179, 'DESCARTE', 2, 4, 104, 3.27, '3739', 500,
        '2019-06-05 11:51:54.271383', false, 100898, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53961', 136, 1, 115, 108.4, 4.83, 2.96, 5.59, 179, 'ESTOQUE', 1, 4, null, 2.96, '3760', 50,
        '2019-06-05 11:51:54.271383', false, 100899, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28697', 136, 1, 115, 119.57, 9.89, 9.38, 9.92, 179, 'DESCARTE', 3, 4, 11, 9.38, '3781', 1200,
        '2019-06-05 11:51:54.271383', false, 100900, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60446', 156, 1, 115, 113.33, 9.07, 8.56, 9.95, 179, 'EM_USO', 1, 5, null, 8.76, '3802', 1200,
        '2019-06-05 11:51:54.271383', true, 100901, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28689', 150, 1, 115, 110.37, 8.73, 8.28, 9.24, 179, 'EM_USO', 3, 4, 11, 8.28, '3823', 1200,
        '2019-06-05 11:51:54.271383', false, 100902, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59505', 135, 11, 0, 0, 20, 20, 20, 179, 'ESTOQUE', 1, 2, null, 20, '3844', 1200, '2019-06-05 11:51:54.271383',
        false, 100903, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59500', 150, 11, 0, 0, 16, 16, 16, 179, 'ESTOQUE', 1, 2, null, 16, '3865', 1200, '2019-06-05 11:51:54.271383',
        false, 100904, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63440', 133, 1, 115, 0, 4.16, 4.53, 3.14, 179, 'EM_USO', 1, 2, null, 4.1, '3886', 1500,
        '2019-06-05 11:51:54.271383', true, 100905, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28665', 135, 1, 115, 110.86, 9.41, 9.49, 9.07, 179, 'EM_USO', 3, 4, 31, 9.49, '3907', 1500,
        '2019-06-05 11:51:54.271383', false, 100906, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59501', 132, 11, 0, 0, 12, 12, 12, 179, 'ESTOQUE', 1, 2, null, 12, '3928', 1200, '2019-06-05 11:51:54.271383',
        false, 100907, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54815', 165, 1, 115, 114.64, 4.97, 5.48, 5.51, 179, 'EM_USO', 2, 4, 11, 5.48, '3949', 1200,
        '2019-06-05 11:51:54.271383', false, 100908, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35073', 160, 1, 115, 115.3, 12.72, 12.29, 12.6, 179, 'ESTOQUE', 4, 4, 461, 12.29, '3970', 1200,
        '2019-06-05 11:51:54.271383', false, 100909, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60447', 163, 1, 115, 115.79, 11.16, 11.7, 10.74, 179, 'EM_USO', 1, 5, null, 11.33, '3991', 1200,
        '2019-06-05 11:51:54.271383', true, 100910, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28670', 160, 1, 115, 112.51, 11.59, 11.64, 11.16, 179, 'EM_USO', 4, 4, 91, 11.64, '4012', 1200,
        '2019-06-05 11:51:54.271383', false, 100911, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56603', 159, 1, 115, 0, 13.88, 16.5, 16.39, 179, 'EM_USO', 1, 4, null, 18.9, '4033', 1200,
        '2019-06-05 11:51:54.271383', false, 100912, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35061', 143, 1, 115, 117.77, 1.52, 1.01, 3.7, 179, 'DESCARTE', 2, 4, 56, 0.96, '4054', 1200,
        '2019-06-05 11:51:54.271383', false, 100913, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63437', 143, 1, 115, 0, 19.2, 22.32, 18.7, 179, 'EM_USO', 1, 2, null, 16.29, '4075', 1200,
        '2019-06-05 11:51:54.271383', true, 100914, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60831', 165, 9, 0, 0, 40, 40, 40, 179, 'ESTOQUE', 1, 2, null, 40, '4096', 1200, '2019-06-05 11:51:54.271383',
        false, 100915, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35104', 137, 1, 115, 0, 3.56, 2.47, 2.69, 179, 'EM_USO', 2, 4, 11, 2.47, '4117', 1200,
        '2019-06-05 11:51:54.271383', false, 100916, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28686', 158, 1, 115, 0, 18.6, 17.17, 17.33, 179, 'EM_USO', 3, 4, 11, 17.17, '4138', 1200,
        '2019-06-05 11:51:54.271383', false, 100917, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54814', 139, 1, 115, 0, 3.6, 3.36, 1.85, 179, 'EM_USO', 3, 4, 13, 3.36, '4159', 1200,
        '2019-06-05 11:51:54.271383', false, 100918, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35058', 158, 1, 115, 0, 16.8, 18.26, 17.98, 179, 'EM_USO', 3, 4, 13, 18.26, '4180', 1200,
        '2019-06-05 11:51:54.271383', false, 100919, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59502', 158, 9, 0, 0, 22, 22, 22, 179, 'ESTOQUE', 1, 2, null, 22, '4201', 1200, '2019-06-05 11:51:54.271383',
        false, 100920, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59504', 158, 11, 0, 0, 20, 20, 20, 179, 'ESTOQUE', 1, 2, null, 20, '4222', 1200, '2019-06-05 11:51:54.271383',
        false, 100921, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28640', 137, 1, 115, 0, 23.26, 23.8, 23.83, 179, 'EM_USO', 4, 4, 11, 23.8, '4243', 1200,
        '2019-06-05 11:51:54.271383', false, 100922, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('58383', 158, 1, 115, 111.36, 8.02, 5.96, 6.33, 179, 'EM_USO', 2, 4, 395, 5.96, '4264', 150,
        '2019-06-05 11:51:54.271383', false, 100923, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28648', 137, 1, 115, 115.79, 3.56, 2.43, 5.39, 179, 'DESCARTE', 4, 4, 13, 2.68, '4285', 1200,
        '2019-06-05 11:51:54.271383', false, 100924, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54368', 137, 1, 115, 115.79, 11.67, 10.77, 12.21, 179, 'EM_USO', 2, 4, 11, 10.77, '4306', 1200,
        '2019-06-05 11:51:54.271383', false, 100925, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35067', 143, 1, 115, 114.97, 4.74, 4.35, 6.72, 179, 'ESTOQUE', 3, 4, 104, 4.35, '4327', 1200,
        '2019-06-05 11:51:54.271383', false, 100926, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60451', 142, 1, 115, 112.34, 8.02, 7.68, 8.22, 179, 'EM_USO', 1, 5, null, 7.51, '4348', 1200,
        '2019-06-05 11:51:54.271383', true, 100927, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('50030', 137, 1, 115, 0, 23.36, 23.33, 23.44, 179, 'EM_USO', 2, 4, 13, 23.33, '4369', 1200,
        '2019-06-05 11:51:54.271383', false, 100928, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56599', 137, 1, 115, 0, 22.27, 22.35, 20.99, 179, 'EM_USO', 1, 4, null, 22.14, '4390', 2500,
        '2019-06-05 11:51:54.271383', false, 100929, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28641', 137, 1, 115, 110.86, 13.25, 13.68, 13.33, 179, 'EM_USO', 4, 4, 13, 13.68, '4411', 1200,
        '2019-06-05 11:51:54.271383', false, 100930, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35077', 137, 1, 115, 0, 3.57, 2.47, 1.3, 179, 'EM_USO', 2, 4, 13, 2.47, '4432', 1200,
        '2019-06-05 11:51:54.271383', false, 100931, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('50031', 137, 1, 115, 0, 21.68, 21.22, 22.25, 179, 'EM_USO', 2, 4, 11, 21.22, '4453', 155,
        '2019-06-05 11:51:54.271383', false, 100932, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60595', 136, 11, 0, 0, 46, 46, 46, 179, 'EM_USO', 1, 2, null, 46, '4474', 1200, '2019-06-05 11:51:54.271383',
        false, 100933, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28625', 159, 1, 115, 115.14, 7.54, 5.51, 7.15, 179, 'EM_USO', 4, 4, 31, 5.51, '4495', 1200,
        '2019-06-05 11:51:54.271383', false, 100934, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55199', 165, 1, 115, 113.82, 12.12, 11.08, 12.15, 179, 'EM_USO', 2, 4, 11, 11.08, '4516', 1200,
        '2019-06-05 11:51:54.271383', false, 100935, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28687', 135, 1, 115, 114.81, 4.97, 4.23, 6.19, 179, 'EM_USO', 3, 4, 11, 4.23, '4537', 1200,
        '2019-06-05 11:51:54.271383', false, 100936, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28700', 158, 1, 115, 115.3, 11.05, 10.37, 10.96, 179, 'EM_USO', 3, 4, 13, 10.37, '4558', 1200,
        '2019-06-05 11:51:54.271383', false, 100937, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28631', 163, 1, 115, 113.33, 12.24, 10.03, 11.05, 179, 'EM_USO', 4, 4, 56, 10.03, '4579', 1200,
        '2019-06-05 11:51:54.271383', false, 100938, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56593', 291, 1, 115, 116.29, 9.75, 8.25, 10.23, 179, 'EM_USO', 1, 4, null, 8.25, '4600', 1200,
        '2019-06-05 11:51:54.271383', false, 100939, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56406', 143, 1, 115, 0, 0.11, 0.84, 3.68, 179, 'DESCARTE', 1, 4, null, 2.21, '4621', 1200,
        '2019-06-05 11:51:54.271383', false, 100940, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28653', 137, 1, 115, 112.34, 11.64, 11.84, 11.92, 179, 'EM_USO', 5, 5, 11, 11.84, '4642', 1200,
        '2019-06-05 11:51:54.271383', false, 100941, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53960', 137, 1, 115, 111.19, 7.85, 6.33, 8.22, 179, 'EM_USO', 2, 4, 11, 6.33, '4663', 1200,
        '2019-06-05 11:51:54.271383', false, 100942, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28688', 158, 1, 115, 114.64, 7.06, 4.86, 5.39, 179, 'EM_USO', 2, 4, 13, 4.86, '4684', 1200,
        '2019-06-05 11:51:54.271383', false, 100943, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60929', 163, 1, 115, 110.37, 13.96, 14.27, 14.64, 179, 'EM_USO', 1, 6, null, 14.27, '4705', 1200,
        '2019-06-05 11:51:54.271383', true, 100944, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60830', 163, 9, 0, 0, 40, 40, 40, 179, 'ESTOQUE', 1, 6, null, 40, '4726', 1200, '2019-06-05 11:51:54.271383',
        false, 100945, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56405', 137, 1, 115, 113.82, 8.36, 6.89, 6.64, 179, 'EM_USO', 1, 4, null, 6.89, '4747', 1200,
        '2019-06-05 11:51:54.271383', false, 100946, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56597', 137, 1, 115, 0, 21.81, 21.85, 21.41, 179, 'EM_USO', 1, 4, null, 22.3, '4768', 1200,
        '2019-06-05 11:51:54.271383', false, 100947, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28679', 133, 1, 115, 80, 16, 16, 16, 179, 'EM_USO', 2, 4, 56, 16, '4789', 1200, '2019-06-05 11:51:54.271383',
        false, 100948, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28652', 137, 1, 115, 112.18, 11.53, 11.02, 10.31, 179, 'EM_USO', 4, 4, 9, 11.02, '4810', 1200,
        '2019-06-05 11:51:54.271383', false, 100949, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60450', 137, 1, 115, 116.45, 8.81, 8.65, 9.8, 179, 'EM_USO', 1, 5, null, 9.49, '4831', 1200,
        '2019-06-05 11:51:54.271383', true, 100950, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28622', 137, 1, 115, 0, 19.78, 17.29, 18.51, 179, 'EM_USO', 4, 4, 9, 17.29, '4852', 1200,
        '2019-06-05 11:51:54.271383', false, 100951, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60449', 137, 1, 115, 112.51, 10.31, 9.63, 9.66, 179, 'EM_USO', 1, 5, null, 9.97, '4873', 1200,
        '2019-06-05 11:51:54.271383', true, 100952, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54370', 137, 1, 115, 0, 18.39, 18.78, 18.36, 179, 'EM_USO', 2, 4, 13, 18.78, '4894', 1200,
        '2019-06-05 11:51:54.271383', false, 100953, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60930', 137, 1, 115, 112.34, 13.82, 12.4, 13.99, 179, 'EM_USO', 1, 6, null, 12.4, '4915', 1200,
        '2019-06-05 11:51:54.271383', true, 100954, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35098', 145, 1, 115, 0, 22.38, 21.57, 22.55, 179, 'EM_USO', 2, 4, 11, 21.57, '4936', 1200,
        '2019-06-05 11:51:54.271383', false, 100955, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35068', 145, 1, 115, 115.14, 3.05, 3.02, 5.14, 179, 'EM_USO', 2, 4, 11, 3.02, '4957', 1200,
        '2019-06-05 11:51:54.271383', false, 100956, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60931', 145, 1, 115, 0, 20.74, 20.58, 19.97, 179, 'EM_USO', 1, 6, null, 20.8, '4978', 1200,
        '2019-06-05 11:51:54.271383', true, 100957, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35069', 137, 1, 115, 115.96, 12.72, 12.57, 13.25, 179, 'EM_USO', 4, 4, 11, 12.57, '4999', 1200,
        '2019-06-05 11:51:54.271383', false, 100958, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35051', 137, 1, 115, 110.54, 10.68, 9.8, 10.28, 179, 'EM_USO', 3, 4, 11, 9.8, '5020', 1200,
        '2019-06-05 11:51:54.271383', false, 100959, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60448', 137, 1, 115, 113.16, 7.17, 8.79, 9.92, 179, 'EM_USO', 1, 5, null, 8.59, '5041', 1200,
        '2019-06-05 11:51:54.271383', true, 100960, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59497', 137, 11, 0, 0, 40, 40, 40, 179, 'EM_USO', 1, 2, null, 40, '5062', 1200, '2019-06-05 11:51:54.271383',
        false, 100961, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60370', 137, 9, 0, 0, 42, 42, 42, 179, 'EM_USO', 1, 2, null, 42, '5083', 1200, '2019-06-05 11:51:54.271383',
        false, 100962, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35101', 137, 1, 115, 0, 2.7, 2.3, 3.31, 179, 'EM_USO', 2, 4, 11, 2.3, '5104', 1200,
        '2019-06-05 11:51:54.271383', false, 100963, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56601', 137, 1, 115, 0, 16.49, 17.3, 17.59, 179, 'EM_USO', 1, 4, null, 23.27, '5096', 1200,
        '2019-06-05 11:51:54.271383', false, 100964, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('22345', 137, 1, 115, 114.81, 7.85, 6.21, 7.17, 179, 'EM_USO', 3, 4, 31, 6.21, '5088', 1200,
        '2019-06-05 11:51:54.271383', false, 100965, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60932', 137, 1, 115, 0, 22.16, 20.9, 19.77, 179, 'EM_USO', 1, 6, null, 19.24, '5080', 1200,
        '2019-06-05 11:51:54.271383', true, 100966, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56412', 137, 1, 115, 116.78, 5.28, 4.86, 5.59, 179, 'EM_USO', 1, 4, null, 4.86, '5072', 1200,
        '2019-06-05 11:51:54.271383', false, 100967, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('52861', 137, 1, 115, 112.01, 8.05, 6.81, 7.23, 179, 'EM_USO', 2, 4, 11, 6.81, '5064', 1200,
        '2019-06-05 11:51:54.271383', false, 100968, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28650', 137, 1, 115, 116.62, 3.13, 3.27, 4.91, 179, 'EM_USO', 4, 4, 507, 3.27, '5056', 1200,
        '2019-06-05 11:51:54.271383', false, 100969, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53959', 137, 1, 115, 0, 22.68, 21.61, 22.5, 179, 'DESCARTE', 2, 4, 11, 21.61, '5048', 1200,
        '2019-06-05 11:51:54.271383', false, 100970, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54367', 137, 1, 115, 116.78, 7.09, 4.57, 6.04, 179, 'EM_USO', 1, 4, null, 4.57, '5040', 1200,
        '2019-06-05 11:51:54.271383', false, 100971, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35103', 133, 1, 115, 115.3, 4.04, 2.99, 3.53, 179, 'ESTOQUE', 2, 4, 13, 2.99, '5032', 1200,
        '2019-06-05 11:51:54.271383', false, 100972, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35057', 143, 1, 115, 114.81, 5.51, 3.75, 2.99, 179, 'ESTOQUE', 2, 4, 13, 2.99, '5024', 1200,
        '2019-06-05 11:51:54.271383', false, 100973, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35072', 145, 1, 115, 116.94, 10.77, 8.9, 10.34, 179, 'EM_USO', 3, 4, 13, 8.9, '5016', 1200,
        '2019-06-05 11:51:54.271383', false, 100974, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28690', 143, 1, 115, 116.94, 9.83, 10, 11.16, 179, 'EM_USO', 5, 5, 13, 10, '5008', 1200,
        '2019-06-05 11:51:54.271383', false, 100975, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62500', 143, 1, 115, 115.96, 16.84, 16.84, 16.87, 179, 'EM_USO', 1, 5, null, 16.84, '5000', 1200,
        '2019-06-05 11:51:54.271383', true, 100976, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35059', 161, 1, 115, 113.33, 12.15, 12.18, 12.38, 179, 'EM_USO', 3, 4, 13, 12.18, '4992', 1200,
        '2019-06-05 11:51:54.271383', false, 100977, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62498', 143, 1, 115, 113.66, 16.76, 17.49, 17.44, 179, 'EM_USO', 1, 5, null, 17.49, '4984', 1200,
        '2019-06-05 11:51:54.271383', true, 100978, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55201', 143, 1, 115, 0, 3.69, 4.5, 4.65, 179, 'ESTOQUE', 1, 4, null, 4.41, '4976', 1200,
        '2019-06-05 11:51:54.271383', false, 100979, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56598', 158, 1, 115, 0, 22.14, 22.93, 22.34, 179, 'EM_USO', 1, 4, null, 22.17, '4968', 1200,
        '2019-06-05 11:51:54.271383', false, 100980, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56600', 137, 1, 115, 0, 15.83, 15.47, 15.47, 179, 'EM_USO', 1, 4, null, 18.14, '4960', 1200,
        '2019-06-05 11:51:54.271383', false, 100981, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56595', 137, 1, 115, 114.32, 8.93, 8.19, 10.85, 179, 'EM_USO', 1, 4, null, 8.19, '4952', 1200,
        '2019-06-05 11:51:54.271383', false, 100982, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56602', 158, 1, 115, 113.82, 7.63, 6.07, 8.14, 179, 'EM_USO', 1, 4, null, 6.07, '4944', 1200,
        '2019-06-05 11:51:54.271383', false, 100983, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59508', 137, 11, 0, 0, 36, 36, 36, 179, 'EM_USO', 1, 2, null, 36, '4936', 1200, '2019-06-05 11:51:54.271383',
        false, 100984, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59511', 158, 9, 0, 0, 18, 18, 18, 179, 'EM_USO', 1, 2, null, 18, '4928', 1200, '2019-06-05 11:51:54.271383',
        false, 100985, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59510', 143, 9, 0, 0, 22, 22, 22, 179, 'EM_USO', 1, 2, null, 22, '4920', 1200, '2019-06-05 11:51:54.271383',
        false, 100986, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56408', 137, 1, 115, 0, 18.19, 16.68, 17.4, 179, 'EM_USO', 1, 4, null, 17.35, '4912', 1200,
        '2019-06-05 11:51:54.271383', false, 100987, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56414', 137, 1, 115, 113.82, 8.02, 6.04, 9.49, 179, 'EM_USO', 1, 4, null, 6.04, '4904', 1200,
        '2019-06-05 11:51:54.271383', false, 100988, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35078', 137, 1, 115, 115.14, 10.31, 9.89, 11.44, 179, 'EM_USO', 3, 4, 11, 9.89, '4896', 1200,
        '2019-06-05 11:51:54.271383', false, 100989, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60206', 137, 1, 115, 117.11, 7.99, 7.77, 8.67, 179, 'ESTOQUE', 1, 4, null, 7.77, '4888', 1200,
        '2019-06-05 11:51:54.271383', false, 100990, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59509', 158, 11, 0, 0, 32, 32, 32, 179, 'EM_USO', 1, 2, null, 32, '4880', 1200, '2019-06-05 11:51:54.271383',
        false, 100991, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60205', 163, 1, 115, 116.45, 7.46, 8.79, 10.17, 179, 'ESTOQUE', 1, 4, null, 8.79, '4872', 1200,
        '2019-06-05 11:51:54.271383', false, 100992, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28645', 137, 1, 115, 112.01, 6.47, 6.3, 7.6, 179, 'EM_USO', 3, 4, 13, 6.3, '4864', 1200,
        '2019-06-05 11:51:54.271383', false, 100993, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('61953', 143, 1, 115, 0, 0.2, 0.21, 0.42, 179, 'EM_USO', 1, 6, null, 0.2, '4856', 1200,
        '2019-06-05 11:51:54.271383', true, 100994, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35066', 137, 1, 115, 113.66, 11.36, 11.84, 11.13, 179, 'EM_USO', 4, 4, 13, 11.84, '4848', 1200,
        '2019-06-05 11:51:54.271383', false, 100995, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60445', 145, 1, 115, 115.47, 10.62, 11.56, 11.02, 179, 'EM_USO', 1, 5, null, 11.13, '4840', 1200,
        '2019-06-05 11:51:54.271383', false, 100996, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35076', 133, 1, 115, 111.19, 10.48, 8.98, 10.14, 179, 'EM_USO', 3, 4, 56, 8.98, '4832', 1200,
        '2019-06-05 11:51:54.271383', false, 100997, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60594', 143, 11, 0, 0, 46, 46, 46, 179, 'EM_USO', 1, 2, null, 46, '4824', 1200, '2019-06-05 11:51:54.271383',
        false, 100998, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28633', 143, 1, 115, 116.45, 3.78, 2.99, 5.56, 179, 'DESCARTE', 2, 4, 56, 2.99, '4816', 1200,
        '2019-06-05 11:51:54.271383', false, 100999, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55200', 137, 1, 115, 116.29, 3.7, 3.36, 5.17, 179, 'ESTOQUE', 1, 4, null, 3.36, '4808', 1200,
        '2019-06-05 11:51:54.271383', false, 101000, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35052', 133, 1, 115, 0, 20.55, 19.79, 19.57, 179, 'EM_USO', 4, 4, 104, 19.72, '4800', 1200,
        '2019-06-05 11:51:54.271383', false, 101001, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28658', 158, 1, 115, 112.51, 5.11, 3.25, 4.97, 179, 'EM_USO', 2, 4, 13, 3.25, '4792', 1200,
        '2019-06-05 11:51:54.271383', false, 101002, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28668', 158, 1, 115, 111.85, 6.95, 5.99, 6.36, 179, 'EM_USO', 4, 4, 11, 5.99, '4784', 1200,
        '2019-06-05 11:51:54.271383', false, 101003, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60572', 137, 11, 0, 0, 44, 44, 44, 179, 'EM_USO', 1, 2, null, 44, '4776', 1200, '2019-06-05 11:51:54.271383',
        false, 101004, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53958', 158, 1, 115, 113.33, 11.53, 9.24, 8.87, 179, 'EM_USO', 2, 4, 11, 9.32, '4768', 1200,
        '2019-06-05 11:51:54.271383', false, 101005, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28694', 143, 1, 115, 0, 1.34, 0.63, 2.49, 179, 'EM_USO', 3, 4, 13, 0.63, '4760', 1200,
        '2019-06-05 11:51:54.271383', false, 101006, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56407', 158, 1, 115, 114.64, 11.56, 10.28, 12.04, 179, 'EM_USO', 2, 4, 12, 10.28, '4752', 1200,
        '2019-06-05 11:51:54.271383', false, 101007, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35097', 137, 1, 115, 0, 18.67, 19.27, 19.33, 179, 'EM_USO', 3, 4, 10, 18.86, '4744', 1200,
        '2019-06-05 11:51:54.271383', false, 101008, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35070', 158, 1, 115, 112.84, 6.5, 5.7, 7.51, 179, 'EM_USO', 2, 4, 31, 5.7, '4736', 1200,
        '2019-06-05 11:51:54.271383', false, 101009, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35079', 143, 1, 115, 112.01, 7.2, 5.39, 5.31, 179, 'EM_USO', 2, 4, 11, 5.39, '4728', 1200,
        '2019-06-05 11:51:54.271383', false, 101010, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28669', 143, 1, 115, 112.01, 7.43, 5.17, 6.13, 179, 'EM_USO', 4, 4, 11, 5.17, '4720', 1200,
        '2019-06-05 11:51:54.271383', false, 101011, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28635', 143, 1, 115, 113, 6.07, 3.27, 7.43, 179, 'EM_USO', 2, 4, 461, 3.81, '4712', 1200,
        '2019-06-05 11:51:54.271383', false, 101012, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('59496', 143, 11, 0, 0, 40, 40, 40, 179, 'EM_USO', 1, 2, null, 40, '4704', 1200, '2019-06-05 11:51:54.271383',
        false, 101013, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60369', 163, 9, 0, 0, 42, 42, 42, 179, 'EM_USO', 1, 2, null, 42, '4696', 1200, '2019-06-05 11:51:54.271383',
        false, 101014, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28647', 133, 1, 115, 113, 10.88, 10.65, 10.65, 179, 'EM_USO', 3, 4, 11, 10.26, '4688', 1200,
        '2019-06-05 11:51:54.271383', false, 101015, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35060', 137, 1, 115, 115.14, 5.48, 5.25, 7.01, 179, 'EM_USO', 2, 4, 56, 4.74, '4680', 1200,
        '2019-06-05 11:51:54.271383', false, 101016, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('61954', 137, 1, 115, 0, 14.3, 14.62, 16.74, 179, 'ESTOQUE', 1, 6, null, 11.19, '4672', 1200,
        '2019-06-05 11:51:54.271383', true, 101017, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60208', 137, 1, 115, 0, 3.45, 3.23, 3.31, 179, 'EM_USO', 1, 4, null, 3.2, '4664', 1200,
        '2019-06-05 11:51:54.271383', false, 101018, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28674', 143, 1, 115, 115.79, 11.78, 10.14, 11.73, 179, 'EM_USO', 3, 4, 11, 10.14, '4656', 1200,
        '2019-06-05 11:51:54.271383', false, 101019, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56413', 143, 1, 115, 0, 0.9, 0.45, 0.25, 179, 'EM_USO', 1, 4, null, 6.21, '4648', 1200,
        '2019-06-05 11:51:54.271383', false, 101020, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28678', 158, 1, 115, 114.97, 11.78, 10.4, 11.87, 179, 'EM_USO', 3, 4, 13, 10.4, '4640', 1200,
        '2019-06-05 11:51:54.271383', false, 101021, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35102', 143, 1, 115, 112.84, 7.68, 4.43, 3.25, 179, 'EM_USO', 2, 4, 13, 3.08, '4632', 1200,
        '2019-06-05 11:51:54.271383', false, 101022, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62501', 133, 1, 115, 114.48, 16.73, 16.5, 16.96, 179, 'EM_USO', 1, 5, null, 16.5, '4624', 1200,
        '2019-06-05 11:51:54.271383', true, 101023, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60573', 137, 11, 0, 0, 44, 44, 44, 179, 'EM_USO', 1, 2, null, 44, '4616', 1200, '2019-06-05 11:51:54.271383',
        false, 101024, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56411', 143, 1, 115, 113.82, 7.74, 6.27, 8.02, 179, 'EM_USO', 1, 4, null, 6.27, '4608', 1200,
        '2019-06-05 11:51:54.271383', false, 101025, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35081', 133, 1, 115, 111.36, 4.63, 3.16, 6.64, 179, 'EM_USO', 2, 4, 395, 4.07, '4600', 1200,
        '2019-06-05 11:51:54.271383', false, 101026, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62499', 133, 1, 115, 113.49, 15.97, 16.28, 16.48, 179, 'EM_USO', 1, 5, null, 16.28, '4592', 1200,
        '2019-06-05 11:51:54.271383', true, 101027, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35100', 133, 1, 115, 116.62, 10.14, 9.07, 9.8, 179, 'EM_USO', 3, 4, 11, 9.07, '4584', 1200,
        '2019-06-05 11:51:54.271383', false, 101028, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28623', 137, 1, 115, 0, 2.81, 5.39, 12.22, 179, 'ANALISE', 4, 4, 104, 14.37, '4576', 1200,
        '2019-06-05 11:51:54.271383', false, 101029, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60368', 137, 9, 0, 0, 40, 40, 40, 179, 'ESTOQUE', 1, 2, null, 40, '4568', 1200, '2019-06-05 11:51:54.271383',
        false, 101030, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('60367', 137, 9, 0, 0, 40, 40, 40, 179, 'ESTOQUE', 1, 2, null, 40, '4560', 1200, '2019-06-05 11:51:54.271383',
        false, 101031, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35080', 137, 1, 115, 116.29, 5.31, 4.46, 5.39, 179, 'ESTOQUE', 3, 4, 11, 4.46, '4552', 1200,
        '2019-06-05 11:51:54.271383', false, 101032, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35049', 137, 1, 115, 116.62, 9.78, 9.49, 10.48, 179, 'EM_USO', 3, 4, 13, 9.49, '4544', 1200,
        '2019-06-05 11:51:54.271383', false, 101033, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56594', 137, 1, 115, 0, 21.88, 21.84, 22.52, 179, 'DESCARTE', 1, 4, null, 22.27, '4536', 1200,
        '2019-06-05 11:51:54.271383', false, 101034, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28649', 137, 1, 115, 111.85, 10.85, 10.45, 11.7, 179, 'EM_USO', 5, 5, 11, 10.45, '4528', 1200,
        '2019-06-05 11:51:54.271383', false, 101035, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('52860', 143, 1, 115, 110.54, 5.96, 3.92, 4.52, 179, 'EM_USO', 2, 4, 11, 3.92, '4520', 1200,
        '2019-06-05 11:51:54.271383', false, 101036, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28642', 143, 1, 115, 112.51, 9.75, 9.46, 9.63, 179, 'EM_USO', 3, 4, 31, 9.83, '4512', 1200,
        '2019-06-05 11:51:54.271383', false, 101037, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35071', 143, 1, 115, 0, 22.93, 23.32, 22.66, 179, 'EM_USO', 2, 4, 11, 23.32, '4504', 1200,
        '2019-06-05 11:51:54.271383', false, 101038, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63497', 137, 1, 115, 0, 16, 16.02, 16, 179, 'EM_USO', 1, 4, null, 16.11, '4496', 1200,
        '2019-06-05 11:51:54.271383', true, 101039, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13088', 143, 1, 115, 116.62, 3.84, 3.67, 5.96, 179, 'EM_USO', 4, 4, 13, 3.67, '4488', 1200,
        '2019-06-05 11:51:54.271383', false, 101040, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55202', 143, 1, 115, 117.11, 11.13, 9.83, 10.51, 179, 'EM_USO', 1, 4, null, 9.83, '4480', 1200,
        '2019-06-05 11:51:54.271383', false, 101041, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('1246666', 1273, 1, 100, 0, null, null, null, 5, 'EM_USO', 2, 3, 11, null, '1018', 1450,
        '2019-06-05 12:54:26.959228', false, 101054, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('PB2300', 135, 21, 120, 0, null, null, null, 5, 'ESTOQUE', 2, 5, 11, null, '1212', 1500,
        '2019-07-09 13:31:03.883676', false, 105240, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('pb220', 135, 23, 120, 0, null, null, null, 5, 'EM_USO', 2, 4, 9, null, '1212', 1500,
        '2019-07-09 13:32:43.848868', false, 105241, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('6969', 165, 20, 115, 0, null, null, null, 5, 'EM_USO', 1, 4, null, null, '2225', 2000,
        '2019-07-09 17:01:43.673417', true, 105248, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('111000', 1331, 19, 110, 0, null, null, null, 5, 'EM_USO', 1, 4, null, null, '3419', 2000,
        '2019-07-09 17:31:48.280670', true, 105251, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('A1', 135, 5, 120, -1, 16, 16, 16, 5, 'EM_USO', 2, 4, 57, 16, '2320', 120, '2019-08-05 17:29:23.451956', false,
        111827, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB123', 137, 1, 100, 0, null, null, null, 5, 'EM_USO', 1, 4, null, null, '1218', 1250,
        '2019-08-18 14:46:27.187231', false, 118083, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB124', 157, 1, 100, 0, null, null, null, 5, 'EM_USO', 2, 4, 11, null, '1118', 1240,
        '2019-08-18 14:48:17.892831', false, 118084, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB125', 1492, 1, 110, 0, 15, 15, 15, 5, 'EM_USO', 2, 4, 11, 15, '1019', 1300, '2019-08-18 14:50:22.739080',
        false, 118085, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB126', 147, 1, 100, 0, null, null, null, 215, 'DESCARTE', 3, 5, 666, null, '1118', 1300,
        '2019-08-18 14:53:37.601644', false, 118086, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB127', 132, 1, 100, 0, 15, 15, 15, 5, 'EM_USO', 2, 4, 11, 15, '3018', 1240, '2019-08-18 15:11:28.447282',
        false, 118087, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('AB128', 147, 1, 100, 0, null, null, null, 215, 'DESCARTE', 3, 4, 667, null, '1816', 1450,
        '2019-08-18 15:13:21.985471', false, 118088, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('T1', 137, 2, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 3, null, null, null, 1450,
        '2019-09-12 14:48:34.725883', false, 127655, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('T2', 137, 2, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 2, null, null, null, 1580,
        '2019-09-12 14:48:34.725883', false, 127656, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('T3', 137, 2, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 2, null, null, null, 1580,
        '2019-09-12 14:48:34.725883', false, 127657, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('T4', 137, 2, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 2, null, null, null, 1580,
        '2019-09-12 14:48:34.725883', false, 127658, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('T5', 137, 2, 120, 0, null, null, null, 5, 'ESTOQUE', 1, 2, null, null, null, 1580,
        '2019-09-12 14:48:34.725883', false, 127659, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ab123', 152, 6, 100, 0, 14.9, 14.86, 15.7, 5, 'EM_USO', 1, 4, null, 14.85, '1118', 1000,
        '2019-09-16 18:37:13.650602', true, 128411, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ct123', 137, 50, 100, 0, 14.77, 15.69, 14.3, 5, 'EM_USO', 1, 4, null, 13.37, '1118', 1200,
        '2019-09-16 18:51:50.047919', false, 128412, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('crt145', 150, 12, 100, 0, 16, 16, 16, 215, 'DESCARTE', 3, 3, 57, 16, '1118', 1005,
        '2019-09-16 19:09:50.314907', false, 128413, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ab12332', 1152, 18, 100, 0, null, null, null, 215, 'DESCARTE', 1, 4, null, null, '1118', 1000,
        '2019-09-24 18:45:36.882036', false, 138194, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('32141', 1331, 11, 100, 0, null, null, null, 215, 'DESCARTE', 2, 2, 780, null, '1111', 1000,
        '2019-09-27 12:20:34.434107', false, 139502, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('213123', 137, 1, 100, 0, 16, 16, 16, 5, 'EM_USO', 2, 4, 395, 16, '1118', 1000, '2019-10-16 12:26:08.672692',
        false, 157703, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('12345383', 132, 1, 100, 0, 13.46, 15.8, 13.42, 215, 'EM_USO', 4, 4, 1330, 15.11, '1119', 1000,
        '2019-10-16 12:29:28.991417', false, 157705, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('124562623', 2378, 6, 100, 0, 18, 18, 18, 215, 'EM_USO', 2, 4, 1333, 18, '1118', 1000,
        '2019-10-16 13:20:54.201472', false, 157710, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('434355', 135, 1, 100, 0, null, null, null, 5, 'EM_USO', 2, 4, 395, null, '1117', 1000,
        '2019-10-16 13:23:54.922637', false, 157711, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('23131', 135, 21, 110, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '0219', 1,
        '2019-11-04 18:20:23.754548', false, 224042, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('23124151', 1052, 10, 100, 0, null, null, null, 215, 'EM_USO', 1, 4, null, null, '0000', 0.01,
        '2019-11-04 18:23:31.726606', true, 224441, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('93y36298h', 1152, 1, 120, 0, null, null, null, 179, 'ESTOQUE', 1, 4, null, null, '2020', 399,
        '2019-11-06 02:40:24.095199', true, 236001, 3, 179, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('151894', 2378, 6, 100, 0, 16, 16, 16, 215, 'ANALISE', 3, 4, 58, 16, '1118', 1000, '2019-11-11 19:34:01.409699',
        false, 303707, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ab12315165', 1152, 6, 100, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '3123', 111.11,
        '2019-11-12 20:23:33.659469', false, 314107, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5689', 163, 1, 115, 0, null, null, null, 5, 'EM_USO', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:46.795683', false, 316753, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5690', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:46.862022', false, 316754, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5691', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:46.928764', false, 316755, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5692', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:46.993025', false, 316756, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5693', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:47.051624', false, 316757, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5694', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:47.121213', false, 316758, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5695', 163, 1, 115, 0, null, null, null, 215, 'EM_USO', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:47.184847', false, 316759, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('5696', 163, 1, 115, 0, null, null, null, 215, 'EM_USO', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:47.243946', false, 316760, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('21415415', 152, 1, 100, 0, 13.6, 13.45, 12.79, 215, 'EM_USO', 1, 2, null, 13.72, '1118', 1400,
        '2019-11-21 12:06:04.938969', false, 323588, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('569 6', 163, 1, 115, 0, null, null, null, 5, 'ESTOQUE', 1, 4, null, null, '3717', 1200,
        '2019-11-18 17:15:47.243946', false, 338732, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('423526', 137, 1, 100, -1, -1, -1, -1, 215, 'EM_USO', 2, 2, 461, -1, '0719', 1000, '2019-11-27 20:01:13.274905',
        false, 341754, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('ab12314', 2912, 10, 100, 98.5, 15.5, 14.5, 12.5, 215, 'ESTOQUE', 1, 2, null, 13.5, '1118', 1111,
        '2020-01-03 14:53:17.961338', true, 373770, 3, 215, false, null, null);
SELECT setval('pneu_script_codigo_seq', 373770, true);

drop trigger TG_PNEU_SCRIPT_INSERT ON pneu_script;
drop function TG_FUNC_PNEU_SCRIPT_INSERT();
drop table pneu_script;


INSERT INTO public.pneu_restricao_unidade (cod_empresa, cod_unidade, tolerancia_calibragem, sulco_minimo_recapagem,
                                           sulco_minimo_descarte, tolerancia_inspecao, periodo_afericao_pressao,
                                           periodo_afericao_sulco, codigo, cod_colaborador_ultima_atualizacao,
                                           data_hora_ultima_atualizacao)
VALUES (3, 103, 0.1, 3, 1.6, 0.25, 7, 30, 220, 2272, '2019-12-09 19:51:11.923000');
INSERT INTO public.pneu_restricao_unidade (cod_empresa, cod_unidade, tolerancia_calibragem, sulco_minimo_recapagem,
                                           sulco_minimo_descarte, tolerancia_inspecao, periodo_afericao_pressao,
                                           periodo_afericao_sulco, codigo, cod_colaborador_ultima_atualizacao,
                                           data_hora_ultima_atualizacao)
VALUES (3, 179, 0.1, 3, 1.6, 0.25, 7, 20, 71, 2272, '2019-12-09 19:51:11.923000');
INSERT INTO public.pneu_restricao_unidade (cod_empresa, cod_unidade, tolerancia_calibragem, sulco_minimo_recapagem,
                                           sulco_minimo_descarte, tolerancia_inspecao, periodo_afericao_pressao,
                                           periodo_afericao_sulco, codigo, cod_colaborador_ultima_atualizacao,
                                           data_hora_ultima_atualizacao)
VALUES (3, 5, 0.1, 3, 1.6, 0.25, 7, 30, 12, null, '2019-01-01 10:00:00.000000');
INSERT INTO public.pneu_restricao_unidade (cod_empresa, cod_unidade, tolerancia_calibragem, sulco_minimo_recapagem,
                                           sulco_minimo_descarte, tolerancia_inspecao, periodo_afericao_pressao,
                                           periodo_afericao_sulco, codigo, cod_colaborador_ultima_atualizacao,
                                           data_hora_ultima_atualizacao)
VALUES (3, 215, 0.05, 3, 1.6, 0.25, 7, 30, 114, 2272, '2020-02-11 18:07:16.799000');


INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0038', 2127, 5, 121, '2019-07-09 14:46:17.166982');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1777, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 2109, 5, 111, '2019-07-09 16:52:55.599554');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 1873, 5, 421, '2019-07-09 17:02:23.827702');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 2450, 5, 211, '2019-07-09 17:02:23.827702');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 2568, 5, 322, '2019-07-09 17:02:23.827702');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 478, 5, 121, '2019-07-09 17:09:10.387325');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 2158, 5, 111, '2019-07-09 17:09:10.387325');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 2220, 5, 411, '2019-07-09 17:37:50.665947');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 3341, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2487, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2509, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2508, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2488, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2505, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2507, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2506, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0035', 3313, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0035', 3251, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0035', 3252, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0035', 3253, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0035', 3254, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 3213, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 981, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 978, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1051, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1048, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 979, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1041, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1045, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1044, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 219, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 3195, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 187, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 3060, 103, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3079, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3328, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 189, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 218, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 217, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 190, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 191, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 3363, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0044', 192, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3279, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3218, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3271, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3272, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3278, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3277, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3273, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3093, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3078, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3321, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3104, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3282, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3017, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 2427, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3345, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3121, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3086, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3274, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3276, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0042', 3275, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 2511, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3211, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3217, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3219, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 2510, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 3098, 103, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3224, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3220, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3221, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3223, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0040', 3222, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3201, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3161, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3083, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3016, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3056, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3074, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3103, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3299, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3358, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3100, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3175, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3329, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3139, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3123, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0005', 3182, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3165, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3196, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 1833, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3198, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3200, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3199, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3240, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3287, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3242, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0033', 3241, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2580, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3042, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3096, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3322, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3298, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3137, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3018, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 863, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3113, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3176, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3168, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3140, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3046, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0005', 3183, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3166, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 3268, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 3267, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2573, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2579, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2578, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2574, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2575, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2577, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0043', 2576, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3281, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3080, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3323, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 2437, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3138, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3019, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3152, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3178, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 1846, 103, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3120, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3171, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3141, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3164, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0005', 3151, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3167, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3330, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3038, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3256, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3257, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3258, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3264, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3263, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3285, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3082, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3131, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3135, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3343, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3326, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3095, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3087, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3177, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3169, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3259, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3327, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3283, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0005', 3058, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0027', 3333, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 2389, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3260, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3262, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0041', 3261, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0019', 3239, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0019', 3245, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0019', 3249, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0019', 3250, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3160, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3081, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 1761, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3162, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3142, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3325, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3153, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3340, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3336, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3334, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3335, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3094, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3003, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3020, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3338, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3215, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3212, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3210, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3214, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3136, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0004', 3039, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3021, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3159, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3324, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0026', 3158, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0016', 3022, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 984, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 3040, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 3091, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 3057, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 3061, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0028', 3085, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0038', 3243, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0038', 3244, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0038', 3307, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0022', 3337, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3044, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0023', 3133, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3122, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 3110, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3090, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 3084, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 3148, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3128, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0021', 3099, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 3097, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3037, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3089, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3114, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3115, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3045, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3154, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3284, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0032', 3155, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3303, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0020', 3304, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 1270, 103, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0008', 3072, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3157, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3156, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 3048, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0013', 2153, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3073, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0014', 3071, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3348, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3349, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3143, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0017', 3350, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 1312, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3193, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3144, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3227, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3288, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 2160, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3077, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3075, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 3266, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3147, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3351, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3352, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 2462, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 2463, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3353, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3354, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0037', 3355, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3300, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3111, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3149, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3150, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 862, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 1134, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3301, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 1894, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3059, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0031', 3302, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3049, 5, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3092, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3356, 5, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3357, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3116, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3036, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3051, 5, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0034', 3286, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3047, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0012', 3043, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3346, 5, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3347, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3344, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0018', 3076, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3225, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0015', 3194, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3145, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0025', 3192, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 1049, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 3359, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 3360, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3112, 103, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3124, 103, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3125, 103, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3226, 103, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3332, 103, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 3041, 103, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 1733, 5, 901, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 3216, 5, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0030', 1763, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1762, 5, 900, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 980, 5, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1768, 5, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1769, 5, 421, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1800, 5, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1801, 5, 422, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1802, 5, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1878, 5, 411, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0029', 2539, 5, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 129, 103, 112, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 921, 103, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 1784, 103, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 1785, 103, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 2769, 103, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0045', 2817, 103, 122, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 915, 5, 211, '2018-06-29 20:15:27.555242');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 1807, 5, 111, '2018-11-26 11:28:39.703256');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 87, 5, 903, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 6, 5, 904, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 1874, 5, 121, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 2393, 5, 221, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 2394, 5, 222, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 2120, 5, 212, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 1939, 5, 111, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0050', 2421, 5, 211, '2019-07-10 18:22:03.415625');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 1824, 5, 902, '2019-05-03 18:29:12.601418');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0001', 1988, 5, 212, '2019-08-05 17:32:14.931239');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0001', 987, 5, 211, '2019-08-05 17:32:14.931239');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0006', 1837, 5, 111, '2019-01-03 17:58:13.755950');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 1849, 5, 412, '2019-02-07 22:01:22.113023');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 2566, 5, 221, '2019-08-29 18:35:36.122766');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 3106, 5, 222, '2019-08-29 18:35:36.122766');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 967, 5, 311, '2019-05-08 19:51:35.808799');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 1864, 5, 212, '2019-05-08 19:51:35.808799');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 1851, 5, 312, '2019-05-08 19:51:35.808799');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0003', 1806, 5, 222, '2019-05-08 19:51:35.808799');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 969, 5, 212, '2019-06-05 13:10:55.327584');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 1892, 5, 312, '2019-06-05 13:10:55.327584');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 1885, 5, 211, '2019-06-05 13:10:55.327584');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 2451, 5, 311, '2019-06-05 13:10:55.327584');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('MMM0001', 2551, 5, 311, '2019-03-13 20:27:47.542964');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 1828, 5, 221, '2019-07-01 17:25:05.216847');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 850, 5, 212, '2019-07-09 13:35:40.744487');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 1876, 5, 312, '2019-07-09 13:35:40.744487');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('GGG2222', 1884, 5, 412, '2019-07-09 13:36:39.599540');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 3132, 103, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 3179, 103, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 3180, 103, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0009', 3181, 103, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3102, 103, 121, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3290, 103, 111, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3291, 103, 211, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3292, 103, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3320, 103, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3319, 103, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3293, 103, 311, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3294, 103, 312, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3318, 103, 322, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0039', 3297, 103, 321, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 1850, 103, 121, '2019-07-09 13:14:44.759386');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 1869, 103, 901, '2019-07-09 13:14:44.759386');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0010', 3228, 103, 902, '2019-07-09 13:14:44.759386');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 3172, 5, 221, '2019-06-18 11:16:54.932727');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 1827, 5, 211, '2019-06-18 11:16:54.932727');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 1775, 5, 212, '2019-06-18 11:16:54.932727');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0007', 1521, 5, 222, '2019-06-18 11:16:54.932727');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 918, 103, 221, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 2396, 103, 901, '2019-07-10 18:26:39.684578');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 2410, 103, 909, '2019-07-10 18:26:39.684578');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 3163, 103, 222, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0011', 3331, 103, 212, null);
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('PRO0001', 1961, 5, 121, '2019-10-01 13:10:14.502196');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 1063, 5, 421, '2019-11-26 18:11:51.869329');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 3374, 5, 322, '2019-11-26 18:11:51.869329');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 973, 5, 422, '2019-11-26 18:11:51.869329');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 2122, 5, 412, '2020-01-21 11:57:23.035634');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('99EMP55', 1826, 5, 411, '2020-01-21 11:57:23.035634');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2411, 5, 212, '2020-01-21 12:22:58.539228');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2423, 5, 900, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 3088, 5, 901, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2417, 5, 221, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2416, 5, 122, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2419, 5, 222, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 988, 5, 312, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 989, 5, 512, '2020-01-21 12:23:48.320823');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 852, 5, 121, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2529, 5, 321, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2532, 5, 421, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2533, 5, 521, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1056, 5, 621, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2530, 5, 322, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2531, 5, 422, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2534, 5, 522, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1121, 5, 622, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2590, 5, 112, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 60, 5, 412, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1125, 5, 111, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1126, 5, 211, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2588, 5, 311, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 2591, 5, 411, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1128, 5, 511, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('JHT1000', 1127, 5, 611, '2020-01-21 16:42:42.771088');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2412, 5, 212, '2020-01-21 17:38:02.478413');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 990, 5, 211, '2020-01-21 17:38:02.478413');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 3108, 5, 900, '2020-01-21 17:38:17.334471');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2397, 5, 901, '2020-01-21 17:38:17.334471');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 3173, 5, 121, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 3170, 5, 221, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 549, 5, 321, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 1102, 5, 122, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 1103, 5, 222, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 1124, 5, 322, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2583, 5, 112, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2581, 5, 111, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2584, 5, 311, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0021', 2587, 5, 411, '2020-01-21 17:45:33.456303');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 899, 215, 121, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 1096, 215, 321, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2326, 215, 421, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 1087, 215, 122, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2461, 215, 222, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2469, 215, 322, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2468, 215, 123, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2518, 215, 223, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 3184, 215, 323, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 3364, 215, 423, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2485, 215, 124, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 1095, 215, 224, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2384, 215, 424, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2431, 215, 214, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2432, 215, 314, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2466, 215, 414, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2459, 215, 113, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2669, 215, 213, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2671, 215, 313, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2710, 215, 413, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2661, 215, 112, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2684, 215, 212, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2683, 215, 312, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 1248, 215, 412, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2670, 215, 111, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2709, 215, 211, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2682, 215, 311, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 3035, 215, 411, '2020-01-21 19:20:14.591550');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2564, 215, 900, '2020-01-21 19:21:05.580445');
INSERT INTO public.veiculo_pneu (placa, cod_pneu, cod_unidade, posicao, data_hora_cadastro)
VALUES ('DIA0020', 2582, 215, 901, '2020-01-21 19:21:05.580445');


INSERT INTO public.socorro_rota_opcao_problema (cod_empresa, descricao, obriga_descricao,
                                                cod_colaborador_ultima_atualizacao, data_hora_ultima_atualizacao,
                                                status_ativo)
VALUES (3, 'Pneu furado', false, 2272, '2020-03-18 18:25:25.431000', true);

INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Multiplos picotamentos na banda de rodagem', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Quebra do talão devido a utilização / montagem', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Trinca uniforme pontual/circunferencial próx. linha de centragem', true, '2018-02-08 21:25:25.612143',
        null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Desgaste localizado', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Contaminação (Químico / Derivado de  Petróleo)', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Dano próximo ao talão', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Rodagem vazio ou com baixa pressão', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Corte e/ou perfuração com tamanho acima do limite p/ conserto', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Cortes e/ou perfurações com distância inferior a 45º', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Quebra por impacto', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Cortes e perfurações em quantidades acima do limite p/ reparação', true, '2018-02-08 21:25:25.612143',
        null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Deterioração por contato com peças fixas do veículo', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Corte lateral', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Corte lateral', true, '2019-09-16 18:52:58.806000', '2019-09-16 18:52:58.806000');
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Rasgo lateral', true, '2019-10-16 13:35:21.578000', '2019-10-16 13:35:21.578000');
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Arrancamento do ombro (Manobra)', true, '2018-02-08 21:25:25.612143', '2020-01-14 21:02:59.385000');

INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Itaruban', 3, true, '2018-07-26 18:13:32.140000');
INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Borracharia 01', 3, true, '2019-04-16 18:17:29.331000');
INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Zalf Recap', 3, true, '2018-07-15 01:09:52.286000');
INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Recapadora Teste', 3, true, '2020-03-09 18:22:51.979000');

-- Seta tokens fixos para o checklist offline:
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_5'
WHERE cod_unidade = 5;
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_103'
WHERE cod_unidade = 103;
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_179'
WHERE cod_unidade = 179;
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_215'
WHERE cod_unidade = 215;

-- Seta um token default para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'token_teste', now(), 2272);

-- Seta o token default que usado no Postman para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'PROLOG_DEV_TEAM', now(), 2272);

SET CONSTRAINTS ALL DEFERRED;
INSERT INTO public.checklist_modelo_versao (cod_versao_checklist_modelo, cod_versao_user_friendly, cod_checklist_modelo,
                                            data_hora_criacao_versao, cod_colaborador_criacao_versao)
VALUES (1, 1, 1, '2020-06-09 18:33:04.425000', 2272);
SELECT setval('checklist_modelo_versao_cod_versao_checklist_modelo_seq', 1, true);

INSERT INTO public.checklist_modelo_data (cod_unidade, codigo, nome, status_ativo, deletado, data_hora_deletado,
                                          pg_username_delecao, cod_versao_atual)
VALUES (215, 1, 'Modelo Teste 1', true, false, null, null, 1);
SELECT setval('checklist_modelo_data_codigo_seq', 1, true);

INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 45);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 158);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 976);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 942);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 951);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 1030);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 159);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 160);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 104);

INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 13);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 65);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 635);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 642);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 579);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 610);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 633);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 634);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 702);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 774);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 975);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 973);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 974);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 411);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 63);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 64);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 643);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 641);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 617);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 775);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 776);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 665);

INSERT INTO public.checklist_perguntas_data (cod_checklist_modelo, cod_unidade, ordem, pergunta, single_choice,
                                             cod_imagem, codigo, deletado, data_hora_deletado, pg_username_delecao,
                                             cod_versao_checklist_modelo, codigo_contexto)
VALUES (1, 215, 1, 'Triângulo de sinalização', false, 15, 1, false, null, null, 1, 1);
INSERT INTO public.checklist_perguntas_data (cod_checklist_modelo, cod_unidade, ordem, pergunta, single_choice,
                                             cod_imagem, codigo, deletado, data_hora_deletado, pg_username_delecao,
                                             cod_versao_checklist_modelo, codigo_contexto)
VALUES (1, 215, 2, 'Cones', true, 21, 2, false, null, null, 1, 2);
INSERT INTO public.checklist_perguntas_data (cod_checklist_modelo, cod_unidade, ordem, pergunta, single_choice,
                                             cod_imagem, codigo, deletado, data_hora_deletado, pg_username_delecao,
                                             cod_versao_checklist_modelo, codigo_contexto)
VALUES (1, 215, 3, 'Estofamento', false, 2, 3, false, null, null, 1, 3);
SELECT setval('checklist_perguntas_data_codigo_contexto_seq', 3, true);
SELECT setval('checklist_perguntas_data_codigo_seq', 3, true);

INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Quebrado', 1, 1, 1, false, 'BAIXA', false, false, null, null, 1, 1);
INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Faltando', 2, 1, 2, false, 'CRITICA', true, false, null, null, 1, 2);
INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Outros', 3, 1, 3, true, 'ALTA', true, false, null, null, 1, 3);

INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Faltando', 1, 2, 4, false, 'CRITICA', false, false, null, null, 1, 4);
INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Outros', 2, 2, 5, true, 'BAIXA', false, false, null, null, 1, 5);

INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Rasgado', 1, 3, 6, false, 'BAIXA', true, false, null, null, 1, 6);
INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Sujo', 2, 3, 7, false, 'BAIXA', true, false, null, null, 1, 7);
INSERT INTO public.checklist_alternativa_pergunta_data (cod_checklist_modelo, cod_unidade, alternativa, ordem,
                                                        cod_pergunta, codigo, alternativa_tipo_outros, prioridade,
                                                        deve_abrir_ordem_servico, deletado, data_hora_deletado,
                                                        pg_username_delecao, cod_versao_checklist_modelo,
                                                        codigo_contexto)
VALUES (1, 215, 'Outros', 3, 3, 8, true, 'BAIXA', true, false, null, null, 1, 8);
SELECT setval('checklist_alternativa_pergunta_data_codigo_contexto_seq', 8, true);
SELECT setval('checklist_alternativa_pergunta_data_codigo_seq', 8, true);

