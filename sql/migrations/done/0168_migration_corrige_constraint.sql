alter table veiculo_pneu
    drop constraint fk_veiculo_pneu_cod_veiculo_placa;

alter table veiculo_pneu
    add constraint fk_veiculo_pneu_cod_veiculo_placa
        foreign key (cod_veiculo, placa)
            references veiculo_data (codigo, placa) deferrable;


