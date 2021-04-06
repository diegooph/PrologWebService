-- remove placa da tabela movimentacao_origem
alter table movimentacao_origem
    drop column placa;
alter table movimentacao_origem
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data (codigo);

-- remove placa da tabela movimentacao_destino
alter table movimentacao_destino
    drop column placa;
alter table movimentacao_destino
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data (codigo);