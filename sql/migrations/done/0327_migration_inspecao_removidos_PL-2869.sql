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


alter table integracao.afericao_integrada
    add column respostas_campos_personalizados jsonb;

--------------------------------function-----------------------------------------------------------------------
drop function if exists integracao.func_pneu_afericao_insert_afericao_integrada(f_cod_unidade_prolog bigint,
    f_cod_auxiliar_unidade text,
    f_cpf_aferidor text,
    f_cod_veiculo bigint,
    f_placa_veiculo text,
    f_cod_auxiliar_tipo_veiculo_prolog text,
    f_km_veiculo text,
    f_tempo_realizacao bigint,
    f_data_hora timestamp with time zone,
    f_tipo_medicao_coletada text,
    f_tipo_processo_coleta text);
create or replace function
    integracao.func_pneu_afericao_insert_afericao_integrada(f_cod_unidade_prolog bigint,
                                                            f_cod_auxiliar_unidade text,
                                                            f_cpf_aferidor text,
                                                            f_cod_veiculo bigint,
                                                            f_placa_veiculo text,
                                                            f_cod_auxiliar_tipo_veiculo_prolog text,
                                                            f_km_veiculo text,
                                                            f_tempo_realizacao bigint,
                                                            f_data_hora timestamp with time zone,
                                                            f_tipo_medicao_coletada text,
                                                            f_tipo_processo_coleta text,
                                                            f_respostas_campos_personalizados jsonb)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_prolog              bigint;
    v_cod_empresa_cliente             text;
    v_cod_tipo_veiculo_prolog         bigint;
    v_cod_diagrama_veiculo_prolog     bigint;
    v_cod_afericao_integrada_inserida bigint;
begin
    -- Busca os dados de empresa e unidade para integração
    select e.codigo,
           e.cod_auxiliar
    from unidade u
             join empresa e on u.cod_empresa = e.codigo
    where u.codigo = f_cod_unidade_prolog
    into v_cod_empresa_prolog, v_cod_empresa_cliente;

    -- Busca os dados de tipo de veículo e diagrama para enriquecer o registro de aferição integrada.
    select vt.cod_tipo_veiculo,
           vt.cod_diagrama
    from (select vt.codigo                                   as cod_tipo_veiculo,
                 vt.cod_diagrama                             as cod_diagrama,
                 regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
          from veiculo_tipo vt
          where vt.cod_empresa = v_cod_empresa_prolog) as vt
    where vt.cod_auxiliar = f_cod_auxiliar_tipo_veiculo_prolog
    into v_cod_tipo_veiculo_prolog, v_cod_diagrama_veiculo_prolog;

    -- Realiza a inserção do registro de aferição integrada.
    insert into integracao.afericao_integrada(cod_empresa_prolog,
                                              cod_empresa_cliente,
                                              cod_unidade_prolog,
                                              cod_unidade_cliente,
                                              cpf_aferidor,
                                              cod_veiculo,
                                              placa_veiculo,
                                              cod_tipo_veiculo_prolog,
                                              cod_tipo_veiculo_cliente,
                                              cod_diagrama_prolog,
                                              km_veiculo,
                                              tempo_realizacao,
                                              data_hora,
                                              tipo_medicao_coletada,
                                              tipo_processo_coleta,
                                              respostas_campos_personalizados)
    values (v_cod_empresa_prolog,
            v_cod_empresa_cliente,
            f_cod_unidade_prolog,
            f_cod_auxiliar_unidade,
            f_cpf_aferidor,
            f_cod_veiculo,
            f_placa_veiculo,
            v_cod_tipo_veiculo_prolog,
            f_cod_auxiliar_tipo_veiculo_prolog,
            v_cod_diagrama_veiculo_prolog,
            f_km_veiculo,
            f_tempo_realizacao,
            f_data_hora,
            f_tipo_medicao_coletada,
            f_tipo_processo_coleta,
            f_respostas_campos_personalizados)
    returning codigo into v_cod_afericao_integrada_inserida;

    if (v_cod_afericao_integrada_inserida is null or v_cod_afericao_integrada_inserida <= 0)
    then
        raise exception 'Não foi possível inserir a aferição nas tabelas de integração, tente novamente';
    end if;

    return v_cod_afericao_integrada_inserida;
end
$$;


alter table afericao_campo_personalizado_unidade
    add column tipo_processo_coleta_afericao text;

update afericao_campo_personalizado_unidade
set tipo_processo_coleta_afericao = 'PNEU_AVULSO'
where true;

alter table afericao_campo_personalizado_unidade
    alter column tipo_processo_coleta_afericao set not null;

-- Optamos por não ter um tipo AMBOS para podermos controlar melhor as constraints de PK e UNIQUE.
alter table afericao_campo_personalizado_unidade
    add constraint check_tipo_processo_afericao
        check ((tipo_processo_coleta_afericao = 'PLACA') or (tipo_processo_coleta_afericao = 'PNEU_AVULSO'));

-- Um mesmo campo pode estar associado à PLACA ou PNEU_AVULSO
alter table afericao_campo_personalizado_unidade
    drop constraint pk_afericao_campo_personalizado_unidade;
alter table afericao_campo_personalizado_unidade
    add constraint pk_afericao_campo_personalizado_unidade
        primary key (cod_campo, cod_unidade, tipo_processo_coleta_afericao);

alter table afericao_campo_personalizado_unidade
    drop constraint unique_afericao_ordem_exibicao_por_unidade;
alter table afericao_campo_personalizado_unidade
    add constraint unique_afericao_ordem_exibicao_por_unidade
        unique (cod_unidade, ordem_exibicao, tipo_processo_coleta_afericao);

drop function func_campo_get_disponiveis_afericao(f_cod_unidade bigint);
create or replace function func_campo_get_disponiveis_afericao(f_cod_unidade bigint,
                                                               f_tipo_processo_coleta_afericao text)
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
  and acpu.tipo_processo_coleta_afericao = f_tipo_processo_coleta_afericao
order by acpu.ordem_exibicao;
$$;