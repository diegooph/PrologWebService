-- Criamos o diagrama
insert into veiculo_diagrama (codigo, nome, url_imagem)
values (24, 'TRUCK EIXO SINGLE', 'WWW.GOOGLE.COM/TRUCK-EIXO-SINGLE');

-- Criamos os eixos
insert into veiculo_diagrama_eixos(cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional)
values (24, 'D', 1, 2, true),
       (24, 'T', 2, 2, false),
       (24, 'T', 3, 2, false);

-- Criamos as posições
insert into veiculo_diagrama_posicao_prolog(cod_diagrama, posicao_prolog)
values (24, 111),
       (24, 121),
       (24, 211),
       (24, 221),
       (24, 311),
       (24, 321),
       (24, 900),
       (24, 901),
       (24, 902),
       (24, 903),
       (24, 904),
       (24, 905),
       (24, 906),
       (24, 907),
       (24, 908);