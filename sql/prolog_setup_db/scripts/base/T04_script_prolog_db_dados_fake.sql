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

