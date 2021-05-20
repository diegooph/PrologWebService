--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do pneu e insere pneus.
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

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (1, '2020-10-05 09:30:56.514000', 'PRO0180', 3383283194, 214570, 312788, 'SULCO_PRESSAO', 215, 'PLACA', false,
        null, null, 1);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1000, 215, 12.13, 12.1, 12.23, 112.66, 111, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1001, 215, 12.13, 12.1, 12.23, 112.66, 121, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1002, 215, 12.13, 12.1, 12.23, 112.66, 211, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1003, 215, 12.13, 12.1, 12.23, 112.66, 212, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1004, 215, 12.13, 12.1, 12.23, 112.66, 221, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (1, 1005, 215, 12.13, 12.1, 12.23, 112.66, 222, 1, 12.03);

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (2, '2020-10-10 09:30:56.514000', 'PRO0180', 3383283194, 214600, 312788, 'SULCO', 215, 'PLACA', false,
        null, null, 1);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1000, 215, 12.13, 12.1, 12.23, null, 111, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1001, 215, 12.13, 12.1, 12.23, null, 121, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1002, 215, 12.13, 12.1, 12.23, null, 211, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1003, 215, 12.13, 12.1, 12.23, null, 212, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1004, 215, 12.13, 12.1, 12.23, null, 221, 1, 12.03);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (2, 1005, 215, 12.13, 12.1, 12.23, null, 222, 1, 12.03);

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (3, '2020-10-11 09:30:56.514000', 'PRO0180', 3383283194, 214600, 312788, 'PRESSAO', 215, 'PLACA', false,
        null, null, 1);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1000, 215, null, null, null, 112.66, 111, 1, null);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1001, 215, null, null, null, 112.66, 121, 1, null);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1002, 215, null, null, null, 112.66, 211, 1, null);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1003, 215, null, null, null, 112.66, 212, 1, null);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1004, 215, null, null, null, 112.66, 221, 1, null);
insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (3, 1005, 215, null, null, null, 112.66, 222, 1, null);

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (4, '2020-10-05 13:01:23.410000', null, 3383283194, null, 57869, 'SULCO_PRESSAO', 215, 'PNEU_AVULSO', false,
        null, null, null);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (4, 1127, 215, 12.13, 12.1, 12.23, 112.66, 0, 1, 12.03);

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (5, '2020-10-05 13:01:23.410000', null, 3383283194, null, 57869, 'SULCO', 215, 'PNEU_AVULSO', false,
        null, null, null);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (5, 1128, 215, 12.13, 12.1, 12.23, null, 0, 1, 12.03);

-- #####################################################################################################################
insert into afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao,
                           tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado,
                           pg_username_delecao, cod_diagrama)
values (6, '2020-10-05 13:01:23.410000', null, 3383283194, null, 57869, 'PRESSAO', 215, 'PNEU_AVULSO', false,
        null, null, null);

insert into afericao_valores (cod_afericao, cod_pneu, cod_unidade, altura_sulco_central_interno, altura_sulco_externo,
                              altura_sulco_interno, psi, posicao, vida_momento_afericao, altura_sulco_central_externo)
values (6, 1134, 215, null, null, null, 112.66, 0, 1, null);

SELECT setval('afericao_codigo_seq', 6, true);

-- #####################################################################################################################
insert into afericao_manutencao (cod_afericao, cod_pneu, cod_unidade, tipo_servico, data_hora_resolucao, cpf_mecanico,
                                 psi_apos_conserto, km_momento_conserto, cod_alternativa, cod_pneu_inserido,
                                 cod_processo_movimentacao, tempo_realizacao_millis)
values (1, 1000, 215, 'calibragem', null, null, null, null, null, null, null, null);

insert into afericao_manutencao (cod_afericao, cod_pneu, cod_unidade, tipo_servico, data_hora_resolucao, cpf_mecanico,
                                 psi_apos_conserto, km_momento_conserto, cod_alternativa, cod_pneu_inserido,
                                 cod_processo_movimentacao, tempo_realizacao_millis)
values (1, 1001, 215, 'calibragem', null, null, null, null, null, null, null, null);

insert into afericao_manutencao (cod_afericao, cod_pneu, cod_unidade, tipo_servico, data_hora_resolucao, cpf_mecanico,
                                 psi_apos_conserto, km_momento_conserto, cod_alternativa, cod_pneu_inserido,
                                 cod_processo_movimentacao, tempo_realizacao_millis)
values (1, 1002, 215, 'inspecao', '2020-10-05 09:35:56.514000', 3383283194, 120.0, 214571, 2, null, null, 36000);

insert into afericao_manutencao (cod_afericao, cod_pneu, cod_unidade, tipo_servico, data_hora_resolucao, cpf_mecanico,
                                 psi_apos_conserto, km_momento_conserto, cod_alternativa, cod_pneu_inserido,
                                 cod_processo_movimentacao, tempo_realizacao_millis)
values (1, 1003, 215, 'calibragem', '2020-10-05 09:35:56.514000', 3383283194, 120.0, 214571, null, null, null, 36000);