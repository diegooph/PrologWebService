insert into veiculo_diagrama (codigo, nome, url_imagem, motorizado)
values (27, 'PRANCHA 5 EIXOS - 4 PNEUS POR EIXO', 'WWW.GOOGLE.COM/PRANCHE-5-EIXOS-4-PNEUS', false);

insert into veiculo_diagrama_eixos (cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional)
values (27, 'T', 1, 4, false),
       (27, 'T', 2, 4, false),
       (27, 'T', 3, 4, false),
       (27, 'T', 4, 4, false),
       (27, 'T', 5, 4, false);


insert into veiculo_diagrama_posicao_prolog (cod_diagrama, posicao_prolog)
values (27, 111),
       (27, 112),
       (27, 121),
       (27, 122),
       (27, 211),
       (27, 212),
       (27, 221),
       (27, 222),
       (27, 311),
       (27, 312),
       (27, 321),
       (27, 322),
       (27, 411),
       (27, 412),
       (27, 421),
       (27, 422),
       (27, 511),
       (27, 512),
       (27, 521),
       (27, 522),
       (27, 900),
       (27, 901),
       (27, 902),
       (27, 903),
       (27, 904),
       (27, 905),
       (27, 906),
       (27, 907),
       (27, 908);

insert into implantacao.veiculo_diagrama_usuario_prolog (cod_veiculo_diagrama, nome, qtd_eixos, motorizado)
values (27, 'PRANCHA 5 EIXOS - 4 PNEUS POR EIXO', 5, false);