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

