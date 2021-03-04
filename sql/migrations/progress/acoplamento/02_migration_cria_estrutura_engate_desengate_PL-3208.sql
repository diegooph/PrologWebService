-- Refatora constraints em veiculo_data e veiculo_pneu para utilizar código
alter table veiculo_pneu
    drop constraint fk_veiculo_pneu_veiculo;
alter table veiculo_data
    drop constraint unique_veiculo_unidade_diagrama;

-- Cria constraint unique para fk.
alter table veiculo_data
    add constraint
        unico_veiculo_unidade_diagrama unique (codigo, cod_unidade, cod_diagrama);

alter table veiculo_pneu
    add constraint fk_veiculo_unidade_diagrama
        foreign key (cod_veiculo, cod_unidade, cod_diagrama) references veiculo_data (codigo, cod_unidade, cod_diagrama)
            deferrable;

-- Cria constraint unique para fk.
alter table veiculo_diagrama
    add constraint
        unico_veiculo_diagrama_motorizado unique (codigo, motorizado);

-- Cria type para posicao
create table types.veiculo_acoplamento_posicao
(
    codigo                smallint,
    posicao_legivel_pt_br text                 not null,
    posicao_legivel_es    text                 not null,
    ativo                 boolean default true not null,
    constraint pk_veiculo_acoplamento_posicao primary key (codigo)
);

-- Insere posições.
insert into types.veiculo_acoplamento_posicao (codigo, posicao_legivel_pt_br, posicao_legivel_es, ativo)
values (1, 'Motorizado', 'Motorizado', true),
       (2, 'Engate 1', 'Enganche 1', true),
       (3, 'Engate 2', 'Enganche 2', true),
       (4, 'Engate 3', 'Enganche 3', true),
       (5, 'Engate 4', 'Enganche 4', true),
       (6, 'Engate 5', 'Enganche 5', true);

create type types.veiculo_acoplamento_acao_type as enum ('MUDOU_POSICAO', 'ACOPLADO', 'DESACOPLADO', 'PERMANECEU');

-- Cria type para ação.
create table types.veiculo_acoplamento_acao
(
    acao               types.veiculo_acoplamento_acao_type,
    constraint pk_veiculo_acoplamento_acao primary key (acao),
    acao_legivel_pt_br text    not null,
    acao_legivel_es    text    not null,
    ativo              boolean not null
);

-- Insere ações.
insert into types.veiculo_acoplamento_acao (acao, acao_legivel_pt_br, acao_legivel_es, ativo)
values ('ACOPLADO', 'ACOPLADO', 'ACOPLADO', true),
       ('DESACOPLADO', 'DESACOPLADO', 'DESACOPLADO', true),
       ('PERMANECEU', 'PERMANECEU', 'PERMANECIÓ', true),
       ('MUDOU_POSICAO', 'MUDOU POSIÇÃO', 'POSICIÓN CAMBIADA', true);

-- Cria tabela para processo de acoplamento
create table veiculo_acoplamento_processo
(
    codigo          bigserial,
    cod_unidade     bigint                   not null,
    cod_colaborador bigint                   not null,
    data_hora       timestamp with time zone not null,
    observacao      text,
    constraint pk_veiculo_acoplamento_processo primary key (codigo),
    constraint fk_veiculo_acoplamento_processo_unidade foreign key (cod_unidade)
        references unidade (codigo),
    constraint fk_veiculo_acoplamento_processo_colaborador foreign key (cod_colaborador)
        references colaborador_data (codigo),
    constraint unico_processo_por_unidade unique (codigo, cod_unidade)
);

-- Cria tabela de acoplamento.
create table veiculo_acoplamento_atual
(
    cod_processo bigint   not null,
    cod_unidade  bigint   not null,
    cod_posicao  smallint not null,
    cod_diagrama bigint   not null,
    motorizado   boolean  not null,
    cod_veiculo  bigint   not null,
    constraint fk_processo_unidade foreign key (cod_processo, cod_unidade)
        references veiculo_acoplamento_processo (codigo, cod_unidade),
    constraint fk_veiculo_unidade_diagrama foreign key (cod_veiculo, cod_unidade, cod_diagrama)
        references veiculo_data (codigo, cod_unidade, cod_diagrama),
    constraint fk_posicao foreign key (cod_posicao)
        references types.veiculo_acoplamento_posicao (codigo),
    constraint fk_diagrama_motorizado foreign key (cod_diagrama, motorizado)
        references veiculo_diagrama (codigo, motorizado),
    constraint unica_posicao_processo
        unique (cod_posicao, cod_processo),
    constraint unico_veiculo
        unique (cod_veiculo),
    constraint check_posicao_motorizado
        check ((motorizado is false and cod_posicao <> 1) or
               (motorizado is true and cod_posicao = 1))
);

-- Cria tabela de histórico.
create table veiculo_acoplamento_historico
(
    codigo       bigserial                           not null
        constraint pk_veiculo_acoplamento_historico
            primary key,
    cod_processo bigint                              not null,
    cod_posicao  smallint                            not null,
    cod_diagrama bigint                              not null,
    motorizado   boolean                             not null,
    cod_veiculo  bigint                              not null,
    km_coletado  bigint                              not null,
    acao         types.veiculo_acoplamento_acao_type not null,
    constraint fk_processo_unidade foreign key (cod_processo)
        references veiculo_acoplamento_processo (codigo),
    constraint fk_posicao foreign key (cod_posicao)
        references types.veiculo_acoplamento_posicao (codigo),
    constraint fk_veiculo foreign key (cod_veiculo)
        references veiculo_data (codigo),
    constraint fk_acao foreign key (acao)
        references types.veiculo_acoplamento_acao (acao),
    constraint fk_diagrama_motorizado foreign key (cod_diagrama, motorizado)
        references veiculo_diagrama (codigo, motorizado),
    constraint unico_veiculo_processo_historico
        unique (cod_veiculo, cod_processo),
    constraint check_posicao_motorizado
        check ((motorizado is false and cod_posicao <> 1) or
               (motorizado is true and cod_posicao = 1)),
    constraint unico_processo_posicao_acao
        exclude using gist (cod_processo with =, cod_posicao with =, acao with =),
    constraint unico_codigo_historico_acoplamento_veiculo
        unique (codigo, cod_processo, cod_veiculo)
);
comment on column veiculo_acoplamento_historico.cod_posicao
    is 'Quando a ação for "MUDOU_POSICAO", a posição nesta coluna é a nova posição do veículo no acoplamento.';

-- cria index para garantir que as posições nas ações PERMANECEU e DESACOPLADO sejam únicas.
create unique index unico_processo_posicao_historico_desacoplado on veiculo_acoplamento_historico
    (cod_processo, cod_posicao) where (acao = 'PERMANECEU' or acao = 'DESACOPLADO');
-- cria index para garantir que as posições nas ações PERMANECEU e ACOPLADO sejam únicas.
create unique index unico_processo_posicao_historico_acoplado on veiculo_acoplamento_historico
    (cod_processo, cod_posicao) where (acao = 'PERMANECEU' or acao = 'ACOPLADO');