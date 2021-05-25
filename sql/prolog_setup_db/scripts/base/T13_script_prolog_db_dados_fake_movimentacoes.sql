--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias de movimentações e insere movimentações.
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

--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (1, 215, '2019-12-16 18:17:29.331000', 3383283194, 'teste movimentação');

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (1, 1, 215, 1270, 15, 15, 15, 1, 'teste mov 1', 15);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (1, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (1, 'ANALISE', null, null, null, null, null, null, null, 1, '12345', null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (2, 1, 215, 1273, 11.81, 12.2, 12.13, 1, 'teste mov 2', 12.43);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (2, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (2, 'ANALISE', null, null, null, null, null, null, null, 1, '12345', null);

--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (2, 215, '2019-12-16 18:17:29.331000', 3383283194, null);

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (3, 2, 215, 1274, 8.92, 8.14, 9.28, 1, null, 8.17);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (3, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (3, 'ANALISE', null, null, null, null, null, null, null, 1, '12345', null);


--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (3, 215, '2019-12-17 15:17:29.331000', 3383283194, null);

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (4, 3, 215, 1294, 12.3, 13.53, 13.33, 1, null, 13.43);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (4, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (4, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);

--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (4, 215, '2019-12-17 15:17:29.331000', 3383283194, null);

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (5, 4, 215, 1295, 12.26, 11.91, 13.66, 1, null, 12.55);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (5, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (5, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (6, 4, 215, 1296, 106.99, 11.81, 11.81, 1, null, 12.3);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (6, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (6, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);

--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (5, 5, '2019-12-17 15:17:29.331000', 3383283194, null);

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (7, 5, 5, 938, 7.52, 4.71, 6.49, 1, null, 3.35);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (7, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (7, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (8, 5, 5, 951, 17.5, 17.5, 17.5, 1, null, 17.5);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (8, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (8, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (9, 5, 5, 952, 15, 15, 15, 1, null, 15);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (9, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (9, 'DESCARTE', null, null, null, 1, 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg', null, null,
        null, null, null);

--######################################################################################################################
--######################################################################################################################
insert into movimentacao_processo (codigo, cod_unidade, data_hora, cpf_responsavel, observacao)
values (6, 5, '2019-12-17 15:17:29.331000', 3383283194, null);

insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (10, 6, 5, 967, 6.57, 9.6, 9.02, 1, null, 9.02);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (10, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (10, 'ANALISE', null, null, null, null, null, null, null, 1, '123', null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (11, 6, 5, 969, 5.12, 4.02, 5.64, 1, null, 4.02);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (11, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (11, 'ANALISE', null, null, null, null, null, null, null, 1, null, null);


insert into movimentacao (codigo, cod_movimentacao_processo, cod_unidade, cod_pneu, sulco_interno,
                          sulco_central_interno, sulco_externo, vida, observacao, sulco_central_externo)
values (12, 6, 5, 972, 6.29, 7.62, 10.15, 1, null, 7.62);

insert into movimentacao_origem(cod_movimentacao, tipo_origem, placa, km_veiculo, posicao_pneu_origem, cod_diagrama)
values (12, 'ESTOQUE', null, null, null, null);

insert into movimentacao_destino (cod_movimentacao, tipo_destino, placa, km_veiculo, posicao_pneu_destino,
                                  cod_motivo_descarte, url_imagem_descarte_1, url_imagem_descarte_2,
                                  url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, cod_diagrama)
values (12, 'ANALISE', null, null, null, null, null, null, null, 1, 'teste', null);
SELECT setval('movimentacao_processo_codigo_seq', 6, true);
SELECT setval('movimentacao_codigo_seq', 12, true);