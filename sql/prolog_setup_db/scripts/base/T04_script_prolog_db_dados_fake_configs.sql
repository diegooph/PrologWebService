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
SELECT setval('pneu_restricao_unidade_codigo_seq', 114, true);


INSERT INTO public.socorro_rota_opcao_problema (cod_empresa, descricao, obriga_descricao,
                                                cod_colaborador_ultima_atualizacao, data_hora_ultima_atualizacao,
                                                status_ativo)
VALUES (3, 'Pneu furado', false, 2272, '2020-03-18 18:25:25.431000', true);


INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Desgaste localizado', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Dano próximo ao talão', true, '2018-02-08 21:25:25.612143', null);
INSERT INTO public.movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao,
                                                         data_hora_ultima_alteracao)
VALUES (3, 'Rodagem vazio ou com baixa pressão', true, '2018-02-08 21:25:25.612143', null);

INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Itaruban', 3, true, '2018-07-26 18:13:32.140000');
INSERT INTO public.recapadora (nome, cod_empresa, ativa, data_hora_cadastro)
VALUES ('Borracharia 01', 3, true, '2019-04-16 18:17:29.331000');

-- Seta tokens fixos para o checklist offline:
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_5'
WHERE cod_unidade = 5;
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_215'
WHERE cod_unidade = 215;

-- Seta um token default para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'token_teste', now(), 2272);

-- Seta o token default que usado no Postman para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'PROLOG_DEV_TEAM', now(), 2272);

