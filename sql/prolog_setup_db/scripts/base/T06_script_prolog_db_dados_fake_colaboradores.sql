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
VALUES (215, 159, 100, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 140, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 117, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 18, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 114, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 118, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 13, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 113, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 10, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 121, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 12, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 112, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 11, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 141, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 144, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 143, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 124, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 123, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 142, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 17, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 135, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 133, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 15, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 110, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 134, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 116, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 19, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 111, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 119, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 131, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 132, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 130, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 14, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 16, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 115, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 122, 1);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 329, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 328, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 310, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 325, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 316, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 313, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 311, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 317, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 159, 501, 5);

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
VALUES (215, 951, 313, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 311, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 317, 3);
INSERT INTO public.cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (215, 951, 501, 5);

INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (1, 5, 'Administrativo');
INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (2, 5, 'Frota');
INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (13, 215, 'Administrativo');
INSERT INTO public.setor (codigo, cod_unidade, nome)
VALUES (356, 215, 'Frota');
SELECT setval('setor_codigo_seq', 356, true);

INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (1, 'Sala 1', 5);
INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (2, 'Sala 2', 5);
INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (7, 'Sala 1', 215);
INSERT INTO public.equipe (codigo, nome, cod_unidade)
VALUES (38, 'Sala 2', 215);
SELECT setval('equipe_codigo_seq', 38, true);


INSERT INTO public.colaborador_data (cpf, matricula_ambev, matricula_trans, data_nascimento, data_admissao,
                                     data_demissao, status_ativo, nome, cod_equipe, cod_funcao, cod_unidade,
                                     cod_permissao, cod_empresa, cod_setor, pis, data_hora_cadastro, codigo,
                                     cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES (3383283194, 4, null, '2018-02-26', '2018-02-20', null, true, 'John Doe', 7, 159, 215, 3, 3, 356, '', null,
        2272, 215, false, null, null);
INSERT INTO public.colaborador_data (cpf, matricula_ambev, matricula_trans, data_nascimento, data_admissao,
                                     data_demissao, status_ativo, nome, cod_equipe, cod_funcao, cod_unidade,
                                     cod_permissao, cod_empresa, cod_setor, pis, data_hora_cadastro, codigo,
                                     cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES (53439478084, null, 500, '1991-10-26', '2019-08-19', '2019-10-30', true, 'Maria Silva', 38, 951, 215, 1, 3, 13,
        '88558403410', '2019-08-19 17:04:46.675294', 30745, 215, false, null, null);
SELECT setval('colaborador_data_codigo_seq', 30745, true);


-- Seta um token default para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'token_teste', now(), 2272);

-- Seta o token default que usado no Postman para o usuário John Doe:
INSERT INTO public.token_autenticacao (cpf_colaborador, token, data_hora, cod_colaborador)
VALUES (3383283194, 'PROLOG_DEV_TEAM', now(), 2272);