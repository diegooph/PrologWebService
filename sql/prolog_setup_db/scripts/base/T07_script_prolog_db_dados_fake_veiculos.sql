--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do veículo e insere veículos.
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (63, 'TOCO', true, 1, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (64, 'TRUCK', true, 2, 3);
INSERT INTO public.veiculo_tipo (codigo, nome, status_ativo, cod_diagrama, cod_empresa)
VALUES (65, 'CARRETA', true, 3, 3);
SELECT setval('veiculo_tipo_codigo_seq', 65, true);

INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (119, 'VW 10160', 1, 3);
INSERT INTO public.modelo_veiculo (codigo, nome, cod_marca, cod_empresa)
VALUES (120, 'VW 2220', 1, 3);
SELECT setval('modelo_veiculo_codigo_seq', 120, true);

INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0001', 5, 869900, true, 63, 120, 1, '2019-08-19 17:04:46.675294', 5, 3195, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0003', 5, 4848, true, 64, 119, 2, '2019-08-19 17:04:46.675294', 5, 3121, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0004', 5, 24284, true, 64, 119, 2, '2019-08-19 17:04:46.675294', 5, 3103, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0006', 5, 30984, true, 64, 119, 2, '2019-08-19 17:04:46.675294', 5, 705, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0014', 5, 54822, true, 63, 120, 1, '2019-08-19 17:04:46.675294', 5, 1593, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0015', 5, 28541, true, 63, 120, 1, '2019-08-19 17:04:46.675294', 5, 3513, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0019', 5, 32529, true, 64, 120, 2, null, 5, 3118, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0020', 5, 36370, true, 63, 120, 1, null, 5, 3031, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0021', 5, 25837, true, 65, 120, 1, null, 5, 3032, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0022', 5, 25261, true, 65, 120, 2, null, 5, 3033, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0023', 5, 50746, true, 65, 119, 2, null, 5, 3034, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0024', 5, 200, true, 65, 119, 2, null, 5, 3514, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0041', 5, 26411, true, 65, 119, 2, null, 5, 3516, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0042', 5, 27057, true, 65, 120, 2, null, 5, 3119, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0043', 5, 37856, true, 64, 120, 2, null, 5, 3517, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0044', 5, 27312, true, 64, 120, 2, null, 5, 3518, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0046', 5, 26339, true, 64, 120, 2, null, 5, 3519, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0047', 5, 23401, false, 65, 119, 2, null, 5, 3531, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0048', 5, 35794, false, 65, 119, 2, null, 5, 3532, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0049', 5, 20924, false, 64, 119, 2, null, 5, 3533, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0050', 5, 94511, false, 64, 120, 1, null, 5, 3539, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0051', 5, 342, false, 63, 120, 1, null, 5, 3540, false, null, null, 3);


INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0179', 215, 150498, true, 63, 119, 1, '2019-06-04 16:54:54.671068', 215, 7944, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0180', 215, 214561, false, 63, 119, 1, '2019-06-04 16:54:54.671068', 215, 7945, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0181', 215, 111321, true, 63, 120, 1, '2019-06-04 16:54:54.671068', 215, 7946, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0182', 215, 152292, true, 63, 119, 1, '2019-06-04 16:54:54.671068', 215, 7947, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0183', 215, 11506, true, 63, 120, 1, '2019-06-04 16:54:54.671068', 215, 7948, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0184', 215, 106965, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 215, 7949, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0185', 215, 3194, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 215, 7950, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0186', 215, 164638, true, 64, 120, 2, '2019-06-04 16:54:54.671068', 215, 7951, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0187', 215, 174005, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7952, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0188', 215, 134254, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7953, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0189', 215, 139480, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 215, 7954, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0190', 215, 137997, true, 64, 120, 1, '2019-06-04 16:54:54.671068', 215, 7955, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0191', 215, 170845, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7956, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0192', 215, 130636, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7957, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0193', 215, 147290, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7958, false, null, null, 3);
INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo, cod_eixos,
                                 data_hora_cadastro, cod_unidade_cadastro, codigo, deletado, data_hora_deletado,
                                 pg_username_delecao, cod_empresa)
VALUES ('PRO0194', 215, 12943, true, 65, 120, 1, '2019-06-04 16:54:54.671068', 215, 7959, false, null, null, 3);
SELECT setval('veiculo_data_codigo_seq', 7959, true);