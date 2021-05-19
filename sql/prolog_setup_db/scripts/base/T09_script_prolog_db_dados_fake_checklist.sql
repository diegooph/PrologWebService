--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias dos checklists e insere alguns.
-- Seta tokens fixos para o checklist offline:
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_5'
WHERE cod_unidade = 5;
UPDATE public.checklist_offline_dados_unidade
SET token_sincronizacao_checklist = 'token_teste_unidade_215'
WHERE cod_unidade = 215;

SET CONSTRAINTS ALL DEFERRED;
INSERT INTO public.checklist_modelo_versao (cod_versao_checklist_modelo, cod_versao_user_friendly, cod_checklist_modelo,
                                            data_hora_criacao_versao, cod_colaborador_criacao_versao)
VALUES (1, 1, 1, '2020-06-09 18:33:04.425000', 2272);
SELECT setval('checklist_modelo_versao_cod_versao_checklist_modelo_seq', 1, true);

INSERT INTO public.checklist_modelo_data (cod_unidade, codigo, nome, status_ativo, deletado, data_hora_deletado,
                                          pg_username_delecao, cod_versao_atual)
VALUES (215, 1, 'Modelo Teste 1', true, false, null, null, 1);
SELECT setval('checklist_modelo_codigo_seq', 1, true);

INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 951);
INSERT INTO public.checklist_modelo_funcao (cod_unidade, cod_checklist_modelo, cod_funcao)
VALUES (215, 1, 159);

INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 63);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 64);
INSERT INTO public.checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
VALUES (215, 1, 65);

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
SELECT setval('checklist_perguntas_codigo_seq1', 3, true);

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
SELECT setval('checklist_alternativa_pergunta_codigo_seq1', 8, true);