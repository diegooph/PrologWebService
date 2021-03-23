-- #####################################################################################################################
--
-- Campos
--
-- #####################################################################################################################
create type prolog_campo_personalizado_type as enum (
    'LISTA_SELECAO',
    'TEXTO_MULTILINHAS');

create table if not exists campo_personalizado_tipo
(
    codigo                     smallint                        not null,
    tipo                       prolog_campo_personalizado_type not null,
    nome                       text                            not null,
    descricao                  text                            not null,
    data_hora_criacao          timestamp with time zone        not null default now(),
    data_hora_ultima_alteracao timestamp with time zone        not null default now(),
    constraint pk_prolog_campos_personalizados_tipo primary key (codigo)
);

insert into campo_personalizado_tipo (codigo, tipo, nome, descricao)
values (1, 'LISTA_SELECAO', 'Lista de valores para seleção',
        'Lista de valores para única ou múltipla seleção, com opções predefinidas no momento do cadastro.');
insert into campo_personalizado_tipo (codigo, tipo, nome, descricao)
values (2, 'TEXTO_MULTILINHAS', 'Texto com várias linhas', 'Campo de texto com várias linhas, sem limite de tamanho.');

create table if not exists campo_personalizado_empresa
(
    codigo                             bigserial                not null,
    cod_empresa                        bigint                   not null,
    cod_tipo_campo                     smallint                 not null,
    cod_funcao_prolog_agrupamento      smallint                 not null,
    nome                               citext                   not null,
    descricao                          varchar(100)             not null,
    texto_auxilio_preenchimento        varchar(60),
    permite_selecao_multipla           boolean,
    opcoes_selecao                     text[],
    codigo_auxiliar                    text,
    status_ativo                       boolean                  not null default true,
    data_hora_ultima_atualizacao       timestamp with time zone not null,
    cod_colaborador_ultima_atualizacao bigint                   not null,
    constraint pk_campo_personalizado_empresa primary key (codigo),
    constraint fk_empresa foreign key (cod_empresa) references empresa (codigo),
    constraint fk_funcao_prolog_agrupamento foreign key (cod_funcao_prolog_agrupamento)
        references funcao_prolog_agrupamento (codigo),
    constraint fk_campo_personalizado_tipo foreign key (cod_tipo_campo)
        references campo_personalizado_tipo (codigo),
    constraint fk_colaborador_ultima_edicao foreign key (cod_colaborador_ultima_atualizacao)
        references colaborador_data (codigo),
    constraint check_tipo_campo_lista_selecao check (
            (cod_tipo_campo <> 1
                and permite_selecao_multipla is null
                and opcoes_selecao is null)
            or
            (cod_tipo_campo = 1
                and permite_selecao_multipla is not null
                and opcoes_selecao is not null)),
    constraint check_max_length_nome check (char_length(nome) <= 50),
    constraint unique_campo_tipo unique (codigo, cod_tipo_campo)
);

comment on column campo_personalizado_empresa.nome
    is 'Nome do campo. No máximo 50 caracteres.';
comment on column campo_personalizado_empresa.descricao
    is 'Descrição do campo. No máximo 100 caracteres.';
comment on column campo_personalizado_empresa.texto_auxilio_preenchimento
    is 'Texto de auxílio para preenchimento. No máximo 60 caracteres. Deve conter um texto de ajuda para orientar o preenchimento do campo.';
comment on column campo_personalizado_empresa.permite_selecao_multipla
    is 'Útil para os campos que são do tipo lista, indicando se permite a seleção múltipla.';
comment on column campo_personalizado_empresa.opcoes_selecao
    is 'As opções de seleção para os campos que são do tipo lista de seleção.';
comment on column campo_personalizado_empresa.codigo_auxiliar
    is 'Código auxiliar do campo, útil para integrações.';

create unique index unique_nome_campo_agrupamento_empresa
    on campo_personalizado_empresa (cod_empresa, cod_funcao_prolog_agrupamento, nome)
    where (status_ativo is true);

create table if not exists campo_personalizado_empresa_historico
(
    codigo                      bigserial                not null,
    cod_campo                   bigint                   not null,
    cod_colaborador_alteracao   bigint                   not null,
    data_hora_alteracao         timestamp with time zone not null,
    nome                        text                     not null,
    descricao                   text                     not null,
    texto_auxilio_preenchimento text,
    permite_selecao_multipla    boolean,
    opcoes_selecao              text[],
    codigo_auxiliar             text,
    status_ativo                boolean                  not null,
    constraint pk_campo_personalizado_empresa_historico primary key (codigo),
    constraint fk_campo_personalizado_empresa foreign key (cod_campo)
        references campo_personalizado_empresa (codigo),
    constraint fk_colaborador_alteracao foreign key (cod_colaborador_alteracao)
        references colaborador_data (codigo)
);
-- #####################################################################################################################
-- #####################################################################################################################


-- #####################################################################################################################
--
-- Movimentação <-> Campos
--
-- #####################################################################################################################
create table if not exists movimentacao_campo_personalizado_unidade
(
    cod_campo                          bigint   not null,
    cod_unidade                        bigint   not null,
    cod_funcao_prolog_agrupamento      bigint   not null,
    preenchimento_obrigatorio          boolean  not null,
    mensagem_caso_campo_nao_preenchido text,
    ordem_exibicao                     smallint not null,
    habilitado_para_uso                boolean default true,
    constraint pk_movimentacao_campo_personalizado_unidade primary key (cod_campo, cod_unidade),
    constraint fk_campo_personalizado_empresa foreign key (cod_campo) references campo_personalizado_empresa (codigo),
    constraint fk_unidade foreign key (cod_unidade) references unidade (codigo),
    constraint fk_funcao_prolog_agrupamento foreign key (cod_funcao_prolog_agrupamento) references funcao_prolog_agrupamento (codigo),
    constraint unique_ordem_exibicao_por_unidade unique (cod_unidade, ordem_exibicao),
    constraint check_agrupamento_movimentacao check (cod_funcao_prolog_agrupamento = 14),
    constraint check_ordem_exibicao_nao_negativa check (ordem_exibicao >= 0),
    constraint check_existencia_mensagem_campo_nao_preenchido check (
            (preenchimento_obrigatorio = false and mensagem_caso_campo_nao_preenchido is null) or
            (preenchimento_obrigatorio = true and mensagem_caso_campo_nao_preenchido is not null))
);

comment on column movimentacao_campo_personalizado_unidade.preenchimento_obrigatorio
    is 'Indica se o preenchimento do campo é obrigatório.';
comment on column movimentacao_campo_personalizado_unidade.mensagem_caso_campo_nao_preenchido
    is 'Caso o campo seja de preenchimento obrigatório e o usuário tente finalizar sem fornecer uma resposta, essa mensagem deve ser exibida.';
comment on column movimentacao_campo_personalizado_unidade.ordem_exibicao
    is 'A ordem de exibição do campo na tela.';

-- create table if not exists movimentacao_campo_personalizado_unidade_historico
-- (
--     nome_campo                text                     not null,
--     acao_executada            char                     not null check (acao_executada in ('A', 'R', 'H', 'D')),
--     cod_colaborador_alteracao bigint                   not null,
--     data_hora_alteracao       timestamp with time zone not null,
--     constraint fk_colaborador foreign key (cod_colaborador_alteracao) references colaborador_data (codigo)
-- );
-- comment on column movimentacao_campo_personalizado_unidade_historico.acao_executada
--     is 'A ação executada que gerou esse histórico, podendo ser:
--     A -> Adicionada: o campo foi vinculado à unidade pelo colaborador;
--     R -> Removida: o campo foi desvinculado da unidade pelo colaborador;
--     H -> Habilitada: o campo foi habilitado para uso na unidade pelo colaborador;
--     D -> Desabilitada: o campo foi desabilitado para uso na unidade pelo colaborador.';


-- Altera a pk de movimentacao_processo para ser apenas o código e não mais a chave composta de codigo + cod_unidade.
alter table afericao_manutencao_data
    drop constraint fk_afericao_manutencao_movimentacao_processo;
alter table movimentacao
    drop constraint fk_movimentacao_movimentacao_procecsso;

alter table movimentacao_processo
    drop constraint pk_movimentacao_processo;
alter table movimentacao_processo
    add constraint pk_movimentacao_processo primary key (codigo);
alter table movimentacao_processo
    add constraint unique_processo_unidade unique (codigo, cod_unidade);

-- Recria as constraints anteriores usando codigo + cod_unidade, como eram.
alter table afericao_manutencao_data
    add constraint fk_afericao_manutencao_movimentacao_processo
        foreign key (cod_processo_movimentacao, cod_unidade) references movimentacao_processo (codigo, cod_unidade);
alter table movimentacao
    add constraint fk_movimentacao_movimentacao_procecsso
        foreign key (cod_movimentacao_processo, cod_unidade) references movimentacao_processo (codigo, cod_unidade);

create table if not exists movimentacao_campo_personalizado_resposta
(
    cod_tipo_campo            bigint not null,
    cod_campo                 bigint not null,
    cod_processo_movimentacao bigint not null,
    resposta                  text,
    resposta_lista_selecao    text[],
    constraint pk_movimentacao_campo_personalizado_resposta primary key (cod_tipo_campo, cod_campo, cod_processo_movimentacao),
    constraint fk_campo_personalizado_tipo foreign key (cod_tipo_campo)
        references campo_personalizado_tipo (codigo),
    constraint fk_campo_personalizado_empresa foreign key (cod_tipo_campo, cod_campo)
        references campo_personalizado_empresa (cod_tipo_campo, codigo),
    constraint fk_movimentacao_processo foreign key (cod_processo_movimentacao)
        references movimentacao_processo (codigo),
    constraint check_alguma_resposta_fornecida check (resposta is not null or resposta_lista_selecao is not null),
    constraint check_apenas_uma_resposta_fornecida check (resposta is null or resposta_lista_selecao is null),
    constraint check_tipo_campo_lista_selecao check (
            (cod_tipo_campo = 1 and resposta_lista_selecao is not null)
            or (cod_tipo_campo <> 1 and resposta_lista_selecao is null))
);
-- #####################################################################################################################
-- #####################################################################################################################

-- TESTES:
INSERT INTO campo_personalizado_empresa (cod_empresa, cod_tipo_campo, cod_funcao_prolog_agrupamento, nome, descricao,
                                         texto_auxilio_preenchimento, permite_selecao_multipla, opcoes_selecao,
                                         status_ativo, data_hora_ultima_atualizacao, cod_colaborador_ultima_atualizacao)
VALUES (3, 1, 14, 'Unidade de Movimento', 'Campo para selecionar a unidade de movimento', 'Selecione...', false,
        '{Unidade 1,Unidade 2,Unidade 3}', true, '2020-03-19 19:07:40.872829', 2272);
INSERT INTO campo_personalizado_empresa (cod_empresa, cod_tipo_campo, cod_funcao_prolog_agrupamento, nome, descricao,
                                         texto_auxilio_preenchimento, permite_selecao_multipla, opcoes_selecao,
                                         status_ativo, data_hora_ultima_atualizacao, cod_colaborador_ultima_atualizacao)
VALUES (3, 2, 14, 'Motivo de Troca', 'Campo para colocar o motivo de troca', 'Digite o motivo de troca', null, null,
        true, '2020-03-19 19:07:40.872829', 2272);

insert into movimentacao_campo_personalizado_unidade (cod_campo, cod_unidade, preenchimento_obrigatorio,
                                         mensagem_caso_campo_nao_preenchido, cod_funcao_prolog_agrupamento, ordem_exibicao)
values (1, 215, true, 'Forneça o campo', 14, 1);
insert into movimentacao_campo_personalizado_unidade (cod_campo, cod_unidade, preenchimento_obrigatorio,
                                         mensagem_caso_campo_nao_preenchido, cod_funcao_prolog_agrupamento, ordem_exibicao)
values (2, 215, true, 'Forneça o campo', 14, 2);

CREATE OR REPLACE FUNCTION FUNC_CAMPO_GET_DISPONIVEIS_MOVIMENTACAO(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_CAMPO                                BIGINT,
                COD_EMPRESA                              BIGINT,
                COD_FUNCAO_PROLOG_AGRUPAMENTO            SMALLINT,
                COD_TIPO_CAMPO                           SMALLINT,
                NOME_CAMPO                               TEXT,
                DESCRICAO_CAMPO                          TEXT,
                TEXTO_AUXILIO_PREENCHIMENTO_CAMPO        TEXT,
                PREENCHIMENTO_OBRIGATORIO_CAMPO          BOOLEAN,
                MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO_CAMPO TEXT,
                PERMITE_SELECAO_MULTIPLA_CAMPO           BOOLEAN,
                OPCOES_SELECAO_CAMPO                     TEXT[],
                ORDEM_EXIBICAO                           SMALLINT
            )
    LANGUAGE SQL
AS
$$
SELECT CPE.CODIGO                              AS COD_CAMPO,
       CPE.COD_EMPRESA                         AS COD_EMPRESA,
       CPE.COD_FUNCAO_PROLOG_AGRUPAMENTO       AS COD_FUNCAO_PROLOG_AGRUPAMENTO,
       CPE.COD_TIPO_CAMPO                      AS COD_TIPO_CAMPO,
       CPE.NOME::TEXT                          AS NOME_CAMPO,
       CPE.DESCRICAO::TEXT                     AS DESCRICAO_CAMPO,
       CPE.TEXTO_AUXILIO_PREENCHIMENTO::TEXT   AS TEXTO_AUXILIO_PREENCHIMENTO_CAMPO,
       MCPU.PREENCHIMENTO_OBRIGATORIO          AS PREENCHIMENTO_OBRIGATORIO_CAMPO,
       MCPU.MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO AS MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO_CAMPO,
       CPE.PERMITE_SELECAO_MULTIPLA            AS PERMITE_SELECAO_MULTIPLA_CAMPO,
       CPE.OPCOES_SELECAO                      AS OPCOES_SELECAO_CAMPO,
       MCPU.ORDEM_EXIBICAO                     AS ORDEM_EXIBICAO
FROM CAMPO_PERSONALIZADO_EMPRESA CPE
         JOIN MOVIMENTACAO_CAMPO_PERSONALIZADO_UNIDADE MCPU
              ON CPE.CODIGO = MCPU.COD_CAMPO
WHERE CPE.STATUS_ATIVO = TRUE
  AND MCPU.HABILITADO_PARA_USO = TRUE
  AND MCPU.COD_UNIDADE = F_COD_UNIDADE
ORDER BY MCPU.ORDEM_EXIBICAO;
$$;