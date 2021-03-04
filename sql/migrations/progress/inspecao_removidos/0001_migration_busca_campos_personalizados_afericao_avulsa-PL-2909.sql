create table if not exists afericao_campo_personalizado_unidade
(
    cod_campo                          bigint   not null
        constraint fk_campo_personalizado_empresa
            references campo_personalizado_empresa,
    cod_unidade                        bigint   not null
        constraint fk_unidade
            references unidade,
    cod_funcao_prolog_agrupamento      bigint   not null
        constraint fk_funcao_prolog_agrupamento
            references funcao_prolog_agrupamento
        constraint check_agrupamento_afericao
            check (cod_funcao_prolog_agrupamento = 1),
    preenchimento_obrigatorio          boolean  not null,
    mensagem_caso_campo_nao_preenchido text,
    ordem_exibicao                     smallint not null
        constraint check_ordem_exibicao_nao_negativa
            check (ordem_exibicao >= 0),
    habilitado_para_uso                boolean default true,
    constraint pk_afericao_campo_personalizado_unidade
        primary key (cod_campo, cod_unidade),
    constraint unique_afericao_ordem_exibicao_por_unidade
        unique (cod_unidade, ordem_exibicao),
    constraint check_existencia_mensagem_campo_nao_preenchido
        check (((preenchimento_obrigatorio = false) and (mensagem_caso_campo_nao_preenchido is null)) or
               ((preenchimento_obrigatorio = true) and (mensagem_caso_campo_nao_preenchido is not null)))
);
comment on column afericao_campo_personalizado_unidade.preenchimento_obrigatorio is 'Indica se o preenchimento do campo é obrigatório.';
comment on column afericao_campo_personalizado_unidade.mensagem_caso_campo_nao_preenchido is 'Caso o campo seja de preenchimento obrigatório e o usuário tente finalizar sem fornecer uma resposta, essa mensagem deve ser exibida.';
comment on column afericao_campo_personalizado_unidade.ordem_exibicao is 'A ordem de exibição do campo na tela.';

create table if not exists afericao_campo_personalizado_resposta
(
    cod_tipo_campo         bigint not null
        constraint fk_campo_personalizado_tipo
            references campo_personalizado_tipo,
    cod_campo              bigint not null,
    cod_processo_afericao  bigint not null
        constraint fk_movimentacao_afericao
            references afericao_data,
    resposta               text,
    resposta_lista_selecao text[],
    constraint pk_afericao_campo_personalizado_resposta
        primary key (cod_tipo_campo, cod_campo, cod_processo_afericao),
    constraint fk_campo_personalizado_empresa
        foreign key (cod_tipo_campo, cod_campo) references campo_personalizado_empresa (cod_tipo_campo, codigo),
    constraint check_alguma_resposta_fornecida
        check ((resposta is not null) or (resposta_lista_selecao is not null)),
    constraint check_apenas_uma_resposta_fornecida
        check ((resposta is null) or (resposta_lista_selecao is null)),
    constraint check_tipo_campo_lista_selecao
        check (((cod_tipo_campo = 1) and (resposta_lista_selecao is not null)) or
               ((cod_tipo_campo <> 1) and (resposta_lista_selecao is null)))
);

-----------------------------------------------------------------------------------------------------------------------

create or replace function func_campo_get_disponiveis_afericao(f_cod_unidade bigint)
    returns table
            (
                cod_campo                                bigint,
                cod_empresa                              bigint,
                cod_funcao_prolog_agrupamento            smallint,
                cod_tipo_campo                           smallint,
                nome_campo                               text,
                descricao_campo                          text,
                texto_auxilio_preenchimento_campo        text,
                preenchimento_obrigatorio_campo          boolean,
                mensagem_caso_campo_nao_preenchido_campo text,
                permite_selecao_multipla_campo           boolean,
                opcoes_selecao_campo                     text[],
                ordem_exibicao                           smallint
            )
    language sql
as
$$
select cpe.codigo                              as cod_campo,
       cpe.cod_empresa                         as cod_empresa,
       cpe.cod_funcao_prolog_agrupamento       as cod_funcao_prolog_agrupamento,
       cpe.cod_tipo_campo                      as cod_tipo_campo,
       cpe.nome::text                          as nome_campo,
       cpe.descricao::text                     as descricao_campo,
       cpe.texto_auxilio_preenchimento::text   as texto_auxilio_preenchimento_campo,
       acpu.preenchimento_obrigatorio          as preenchimento_obrigatorio_campo,
       acpu.mensagem_caso_campo_nao_preenchido as mensagem_caso_campo_nao_preenchido_campo,
       cpe.permite_selecao_multipla            as permite_selecao_multipla_campo,
       cpe.opcoes_selecao                      as opcoes_selecao_campo,
       acpu.ordem_exibicao                     as ordem_exibicao
from campo_personalizado_empresa cpe
         join afericao_campo_personalizado_unidade acpu
              on cpe.codigo = acpu.cod_campo
where cpe.status_ativo = true
  and acpu.habilitado_para_uso = true
  and acpu.cod_unidade = f_cod_unidade
order by acpu.ordem_exibicao;
$$;