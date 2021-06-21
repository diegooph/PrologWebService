drop index if exists unique_pneu_empresa;
create unique index unique_pneu_empresa
    on pneu_data (codigo_cliente, cod_empresa)
    where (deletado = false);

alter table veiculo_data
    drop constraint if exists unique_cod_empresa_placa;
create unique index unique_cod_empresa_placa
    on veiculo_data (placa, cod_empresa)
    where (deletado = false);