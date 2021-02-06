create table veiculo_processo_km_historico
(
    codigo                             bigserial                not null
        constraint pk_veiculo_km_propagacao
            primary key,
    cod_unidade                        bigint                   not null
        constraint fk_unidade
            references unidade (codigo),
    cod_historico_processo_acoplamento bigint                   null
        constraint fk_veiculo_acoplamento_historico
            references veiculo_acoplamento_historico (codigo),
    cod_processo_acoplamento           bigint                   null,
    cod_processo_veiculo               bigint                   not null,
    tipo_processo_veiculo              text                     not null,
    cod_veiculo                        bigint                   not null
        constraint fk_veiculo
            references veiculo_data (codigo),
    veiculo_fonte_processo             boolean                  not null,
    motorizado                         boolean                  not null,
    km_antigo                          bigint                   not null,
    km_final                           bigint                   not null,
    km_coletado_processo               bigint                   not null,
    data_hora_processo                 timestamp with time zone not null,
    constraint unique_cod_processo_tipo_processo_cod_veiculo
        unique (cod_processo_veiculo, tipo_processo_veiculo, cod_veiculo),
    constraint fk_historico_veiculo_acoplamento_historico_veiculo
        foreign key (cod_historico_processo_acoplamento, cod_processo_acoplamento, cod_veiculo)
            references veiculo_acoplamento_historico (codigo, cod_processo, cod_veiculo),
    constraint check_motorizado_cod_acoplamento check (
            (motorizado = true and cod_processo_acoplamento is not null)
            or (motorizado = false))
);

create or replace view veiculo_km_propagacao as
select codigo,
       cod_veiculo,
       cod_processo_acoplamento,
       cod_historico_processo_acoplamento,
       cod_processo_veiculo,
       tipo_processo_veiculo,
       cod_unidade,
       veiculo_fonte_processo,
       motorizado,
       km_antigo,
       km_final,
       km_coletado_processo,
       data_hora_processo
from veiculo_processo_km_historico
where km_antigo <> km_final
  and veiculo_fonte_processo = false;