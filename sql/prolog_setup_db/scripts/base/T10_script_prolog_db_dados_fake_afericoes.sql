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
SELECT setval('pneu_restricao_unidade_codigo_seq', 114, true);