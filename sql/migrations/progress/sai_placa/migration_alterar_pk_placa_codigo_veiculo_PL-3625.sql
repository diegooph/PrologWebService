begin transaction;
alter table socorro_rota_abertura
    drop constraint if exists fk_socorro_rota_abertura_veiculo_codigo;

alter table veiculo_transferencia_informacoes
    drop constraint if exists fk_veiculo_transferencia_informacoes_veiculo;

alter table integracao.veiculo_cadastrado
    drop constraint if exists fk_veiculo_cadastro_veiculo;

alter table veiculo_edicao_historico
    drop constraint if exists fk_veiculo;

alter table veiculo_acoplamento_historico
    drop constraint if exists fk_veiculo;

alter table veiculo_processo_km_historico
    drop constraint if exists fk_veiculo;

alter table movimentacao_origem
    drop constraint if exists fk_movimentacao_origem_veiculo;

alter table movimentacao_destino
    drop constraint if exists fk_movimentacao_origem_veiculo;

alter table afericao_data
    drop constraint if exists fk_afericao_cod_veiculo;

alter table checklist_data
    drop constraint if exists fk_checklist_cod_veiculo;

alter table veiculo_data
    drop constraint if exists veiculo_codigo_key;

alter table veiculo_data
    drop constraint if exists pk_placa;

alter table veiculo_data
    add constraint pk_cod_veiculo primary key (codigo);

alter table veiculo_data
    add constraint unique_cod_empresa_placa unique (cod_empresa, placa);

alter table socorro_rota_abertura
    add constraint fk_socorro_rota_abertura_veiculo_codigo foreign key (cod_veiculo_problema) references veiculo_data;

alter table veiculo_transferencia_informacoes
    add constraint fk_veiculo_transferencia_informacoes_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table integracao.veiculo_cadastrado
    add constraint fk_veiculo_cadastro_veiculo foreign key (cod_veiculo_cadastro_prolog) references veiculo_data;

alter table veiculo_edicao_historico
    add constraint fk_veiculo foreign key (cod_veiculo_edicao) references veiculo_data;

alter table veiculo_acoplamento_historico
    add constraint fk_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table veiculo_processo_km_historico
    add constraint fk_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table movimentacao_origem
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table movimentacao_destino
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table afericao_data
    add constraint fk_afericao_cod_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table checklist_data
    add constraint fk_checklist_cod_veiculo foreign key (cod_veiculo) references veiculo_data;

end transaction;
