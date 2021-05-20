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

-- #####################################################################################################################
-- checklist_data com tudo OK
insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 1, '2020-10-05 09:35:56.514000', 3383283194, 'PRO0180', 'S', 360000, 214700,
        '2020-10-05 09:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-05 06:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 2, '2020-10-05 18:35:56.514000', 3383283194, 'PRO0180', 'R', 306000, 214750,
        '2020-10-05 18:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-05 15:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 3, '2020-10-10 12:35:56.514000', 3383283194, 'PRO0179', 'S', 360000, 214750,
        '2020-10-10 12:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-10 09:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 4, '2020-10-10 18:35:56.514000', 3383283194, 'PRO0179', 'R', 360000, 214750,
        '2020-10-10 18:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-10 15:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 5, '2020-10-10 19:35:56.514000', 3383283194, 'PRO0179', 'R', 360000, 214750,
        '2020-10-10 19:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-10 16:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 6, '2020-10-10 12:35:56.514000', 3383283194, 'PRO0181', 'S', 360000, 214750,
        '2020-10-10 12:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-10 09:35:56.514000');

insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 7, '2020-10-10 18:35:56.514000', 3383283194, 'PRO0181', 'R', 360000, 214750,
        '2020-10-10 18:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 3, 0, 8,
        0, null, 1, '2020-10-10 15:35:56.514000');

-- #####################################################################################################################
-- #####################################################################################################################
-- checklist_data com respostas NOK e Ordens de Serviços
insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 8, '2020-10-15 09:35:56.514000', 3383283194, 'PRO0180', 'S', 360000, 214700,
        '2020-10-15 09:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 1, 2, 5,
        3, null, 1, '2020-10-15 06:35:56.514000');

insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (1, 215, 1, 1, 8, 2, 4, null);
insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (2, 215, 1, 1, 8, 3, 6, null);
insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (3, 215, 1, 1, 8, 3, 7, null);

insert into checklist_ordem_servico_data(codigo, cod_unidade, cod_checklist, status, data_hora_fechamento, deletado,
                                         data_hora_deletado, codigo_prolog, pg_username_delecao)
values (1, 215, 8, 'A', null, false, null, 1, null);

insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 1, null, null, 1, 'P', null, null, null, 1, 2, 4, null, null, false, null, null, 2, 4);
insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 1, null, null, 1, 'P', null, null, null, 2, 3, 6, null, null, false, null, null, 3, 6);
insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 1, null, null, 1, 'P', null, null, null, 3, 3, 7, null, null, false, null, null, 3, 7);

insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (1, 1, 8, 4, 1);
insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (2, 2, 8, 6, 1);
insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (3, 3, 8, 7, 1);

-- #####################################################################################################################
insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 9, '2020-10-15 19:35:56.514000', 3383283194, 'PRO0180', 'R', 360000, 214700,
        '2020-10-15 19:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 1, 2, 5,
        3, null, 1, '2020-10-15 16:35:56.514000');

insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (4, 215, 1, 1, 9, 1, 1, null);
insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (5, 215, 1, 1, 9, 1, 2, null);
insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (6, 215, 1, 1, 9, 1, 3, 'Teste resposta outros');

insert into checklist_ordem_servico_data(codigo, cod_unidade, cod_checklist, status, data_hora_fechamento, deletado,
                                         data_hora_deletado, codigo_prolog, pg_username_delecao)
values (2, 215, 9, 'A', null, false, null, 2, null);

insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 2, null, null, 1, 'P', null, null, null, 4, 1, 1, null, null, false, null, null, 1, 1);
insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 2, null, null, 1, 'P', null, null, null, 5, 1, 2, null, null, false, null, null, 1, 2);
insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 2, 3383283194, 214561, 1, 'R', 360000, '2020-10-16 16:35:56.514000', 'Item de teste resolvido', 6, 1, 3,
        '2020-10-16 16:35:56.514000', '2020-10-16 17:35:56.514000', false, null, null, 1, 3);

insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (4, 4, 9, 1, 1);
insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (5, 5, 9, 2, 1);
insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (6, 6, 9, 3, 1);

-- #####################################################################################################################
insert into checklist_data (cod_unidade, cod_checklist_modelo, codigo, data_hora, cpf_colaborador, placa_veiculo, tipo,
                            tempo_realizacao, km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao,
                            versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, device_imei,
                            device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, foi_offline, deletado,
                            data_hora_deletado, data_hora_importado_prolog, total_perguntas_ok, total_perguntas_nok,
                            total_alternativas_ok, total_alternativas_nok, pg_username_delecao,
                            cod_versao_checklist_modelo, data_hora_realizacao_tz_aplicado)
values (215, 1, 10, '2020-10-15 19:35:56.514000', 3383283194, 'PRO0181', 'R', 360000, 214700,
        '2020-10-15 19:35:56.514000', 'SERVIDOR', 100, 100, null, null, null, null, false, false, null, null, 2, 1, 6,
        2, null, 1, '2020-10-15 16:35:56.514000');

insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (7, 215, 1, 1, 10, 1, 1, null);
insert into checklist_respostas_nok (codigo, cod_unidade, cod_checklist_modelo, cod_versao_checklist_modelo,
                                     cod_checklist, cod_pergunta, cod_alternativa, resposta_outros)
values (8, 215, 1, 1, 10, 1, 2, null);

insert into checklist_ordem_servico_data(codigo, cod_unidade, cod_checklist, status, data_hora_fechamento, deletado,
                                         data_hora_deletado, codigo_prolog, pg_username_delecao)
values (3, 215, 10, 'F', '2020-10-16 20:35:56.514000', false, null, 3, null);

insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 3, 3383283194, 214561, 1, 'R', 360000, '2020-10-16 20:35:56.514000', 'resolvido', 7, 1, 1,
        '2020-10-16 20:05:56.514000', '2020-10-16 20:35:56.514000', false, null, null, 1, 1);
insert into checklist_ordem_servico_itens_data(cod_unidade, cod_os, cpf_mecanico, km, qt_apontamentos, status_resolucao,
                                               tempo_realizacao, data_hora_conserto, feedback_conserto, codigo,
                                               cod_pergunta_primeiro_apontamento, cod_alternativa_primeiro_apontamento,
                                               data_hora_inicio_resolucao, data_hora_fim_resolucao, deletado,
                                               data_hora_deletado, pg_username_delecao, cod_contexto_pergunta,
                                               cod_contexto_alternativa)
values (215, 3, 3383283194, 214561, 1, 'R', 360000, '2020-10-16 20:35:56.514000', 'item fechado', 8, 1, 2,
        '2020-10-16 20:15:56.514000', '2020-10-16 20:35:56.514000', false, null, null, 1, 2);

insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (7, 7, 10, 1, 1);
insert into checklist_ordem_servico_itens_apontamentos (codigo, cod_item_ordem_servico, cod_checklist_realizado,
                                                        cod_alternativa, nova_qtd_apontamentos)
values (8, 8, 10, 2, 1);

SELECT setval('checklist_codigo_seq', 10, true);
SELECT setval('checklist_respostas_nok_codigo_seq', 8, true);
SELECT setval('checklist_ordem_servico_data_codigo_prolog_seq', 3, true);
SELECT setval('checklist_ordem_servico_itens_codigo_seq', 8, true);
SELECT setval('checklist_ordem_servico_itens_apontamentos_codigo_seq', 8, true);