begin transaction;
alter table veiculo_data
    add constraint cod_veiculo_unidade_unique unique (codigo, cod_unidade);

alter table veiculo_pneu
    drop constraint if exists veiculo_pneu_placa_posicao_key;

alter table veiculo_pneu
    add constraint veiculo_pneu_cod_veiculo_posicao_key unique (cod_veiculo, posicao);

alter table veiculo_pneu
    drop constraint if exists fk_veiculo_pneu_cod_veiculo_placa;

alter table veiculo_pneu
    add constraint fk_veiculo_pneu_cod_veiculo
        foreign key (cod_veiculo, cod_unidade) references veiculo_data (codigo, cod_unidade) deferrable;

alter table veiculo_pneu
    drop constraint if exists pk_veiculo_pneu;

alter table veiculo_pneu
    add constraint pk_veiculo_pneu primary key (cod_veiculo, cod_pneu);

alter table veiculo_pneu
    drop column if exists placa;
end transaction;