-- Criamos uma tabela para salvar os motivos padrões pelos quais as permissões podem estar bloqueadas.
create table if not exists funcao_prolog_motivo_bloqueio
(
    codigo    smallserial not null,
    motivo    citext      not null,
    descricao text        not null,
    constraint pk_motivo_bloqueio primary key (codigo),
    constraint unique_motivo_bloqueio_funcao_prolog unique (motivo)
);
insert into funcao_prolog_motivo_bloqueio (motivo, descricao)
values ('Não contratado', 'Esta permissão não pode ser associada pois a funcionalidade não foi contratada');

insert into funcao_prolog_motivo_bloqueio (motivo, descricao)
values ('Integrado', 'Esta permissão não pode ser associada pois a funcionalidade está integrada');

insert into funcao_prolog_motivo_bloqueio (motivo, descricao)
values ('Outros', 'Permissão indisponível');

-- Criamos uma tabela para armazenar as permissões bloqueadas de cada unidade
create table if not exists funcao_prolog_bloqueada
(
    cod_unidade         bigint not null,
    cod_pilar_funcao    bigint not null,
    cod_funcao_prolog   bigint not null,
    cod_motivo_bloqueio bigint not null,
    observacao_bloqueio text,
    constraint pk_funcao_prolog_bloqueada primary key (cod_unidade, cod_funcao_prolog),
    constraint fk_motivo_bloqueio_funcao
        foreign key (cod_motivo_bloqueio) references funcao_prolog_motivo_bloqueio (codigo),
    constraint fk_cod_unidade_funcao_bloqueada
        foreign key (cod_unidade) references unidade (codigo),
    constraint fk_cod_funcao_bloqueada
        foreign key (cod_funcao_prolog, cod_pilar_funcao) references funcao_prolog_v11 (codigo, cod_pilar)
);
comment on column funcao_prolog_bloqueada.observacao_bloqueio
    is 'Esta coluna contém um texto opcional. Se for preenchida, ela irá sobrescrever o valor padrão da descrição
    do motivo de bloqueio.';

create index idx_funcao_prolog_bloqueada_cod_unidade on funcao_prolog_bloqueada (cod_unidade);
create index idx_funcao_prolog_bloqueada_cod_motivo_bloqueio on funcao_prolog_bloqueada (cod_motivo_bloqueio);

-- Cria audit_table para salvar as deleções das permissões bloqueadas.
create trigger tg_func_audit_funcao_prolog_bloqueada
    after insert or delete
    on funcao_prolog_bloqueada
    for each row
execute procedure audit.func_audit();

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################

-- Alteramos function para verificar permissões bloqueadas
create or replace function
    func_colaborador_verifica_permissoes_cpf_data_nascimento(f_cpf bigint,
                                                             f_data_nascimento date,
                                                             f_permisssoes_necessarias integer[],
                                                             f_precisa_ter_todas_as_permissoes boolean,
                                                             f_apenas_usuarios_ativos boolean)
    returns table
            (
                token_valido      boolean,
                possui_permisssao boolean,
                cpf_colaborador   bigint,
                cod_colaborador   bigint
            )
    language plpgsql
as
$$
declare
    v_permissoes_colaborador integer[];
    v_cpf_colaborador        bigint;
    v_cod_colaborador        bigint;
begin
    select array_agg(cfp.cod_funcao_prolog),
           c.cpf,
           c.codigo
    from colaborador c
             -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
             -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
             -- contendo null.
             left join cargo_funcao_prolog_v11 cfp
                       on cfp.cod_unidade = c.cod_unidade
                           and cfp.cod_funcao_colaborador = c.cod_funcao
    where c.cpf = f_cpf
      and c.data_nascimento = f_data_nascimento
      and f_if(f_apenas_usuarios_ativos, c.status_ativo = true, true)
    group by c.cpf, c.codigo
    into v_permissoes_colaborador, v_cpf_colaborador, v_cod_colaborador;

    return query
        select f.token_valido      as token_valido,
               f.possui_permisssao as possui_permissao,
               v_cpf_colaborador   as cpf_colaborador,
               v_cod_colaborador   as cod_colaborador
        from func_colaborador_verifica_permissoes(
                     v_permissoes_colaborador,
                     f_permisssoes_necessarias,
                     f_precisa_ter_todas_as_permissoes) f;
end;
$$;

create or replace function func_colaborador_verifica_permissoes_token(f_token text,
                                                                      f_permisssoes_necessarias integer[],
                                                                      f_precisa_ter_todas_as_permissoes boolean,
                                                                      f_apenas_usuarios_ativos boolean)
    returns table
            (
                token_valido      boolean,
                possui_permisssao boolean,
                cpf_colaborador   bigint,
                cod_colaborador   bigint
            )
    language plpgsql
as
$$
declare
    v_permissoes_colaborador integer[];
    v_cpf_colaborador        bigint;
    v_cod_colaborador        bigint;
begin
    select array_agg(cfp.cod_funcao_prolog),
           c.cpf,
           c.codigo
    from token_autenticacao ta
             join colaborador c on c.cpf = ta.cpf_colaborador
        -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
        -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
        -- contendo null.
             left join cargo_funcao_prolog_v11 cfp
                       on cfp.cod_unidade = c.cod_unidade
                           and cfp.cod_funcao_colaborador = c.cod_funcao
    where ta.token = f_token
      and f_if(f_apenas_usuarios_ativos, c.status_ativo = true, true)
    group by c.cpf, c.codigo
    into v_permissoes_colaborador, v_cpf_colaborador, v_cod_colaborador;

    return query
        select f.token_valido      as token_valido,
               f.possui_permisssao as possui_permissao,
               v_cpf_colaborador   as cpf_colaborador,
               v_cod_colaborador   as cod_colaborador
        from func_colaborador_verifica_permissoes(
                     v_permissoes_colaborador,
                     f_permisssoes_necessarias,
                     f_precisa_ter_todas_as_permissoes) f;
end;
$$;

-- Alteramos exibição das permissões para tratar as bloqueadas
create or replace function func_cargos_get_todos_cargos_unidade(f_cod_unidade bigint)
    returns table
            (
                cod_cargo      bigint,
                nome_cargo     text,
                qtd_permissoes bigint
            )
    language plpgsql
as
$$
begin
    return query
        with qtd_permissoes as (
            select distinct count(cfpv11.cod_funcao_colaborador) as qtd_permissoes_cargo,
                            cfpv11.cod_funcao_colaborador        as cod_funcao_colaborador,
                            cfpv11.cod_unidade                   as cod_unidade
            from cargo_funcao_prolog_v11 cfpv11
            where cfpv11.cod_unidade = f_cod_unidade
            group by cfpv11.cod_funcao_colaborador, cfpv11.cod_unidade
        )
        select f.codigo                             as cod_cargo,
               f.nome :: text                       as nome_cargo,
               coalesce(qp.qtd_permissoes_cargo, 0) as qtd_permissoes
        from funcao f
                 join unidade u on u.cod_empresa = f.cod_empresa
                 left join qtd_permissoes qp on qp.cod_unidade = u.codigo and qp.cod_funcao_colaborador = f.codigo
        where u.codigo = f_cod_unidade
        group by f.codigo, f.nome, qp.qtd_permissoes_cargo
        order by f.nome, f.codigo, qp.qtd_permissoes_cargo;
end;
$$;

drop function func_cargos_get_permissoes_detalhadas(bigint, bigint);
create or replace function func_cargos_get_permissoes_detalhadas(f_cod_unidade bigint,
                                                                 f_cod_cargo bigint)
    returns table
            (
                cod_cargo                       bigint,
                cod_unidade_cargo               bigint,
                nome_cargo                      text,
                cod_pilar                       bigint,
                nome_pilar                      varchar(255),
                cod_funcionalidade              smallint,
                nome_funcionalidade             varchar(255),
                cod_permissao                   bigint,
                nome_permissao                  varchar(255),
                impacto_permissao               prolog_impacto_permissao_type,
                descricao_permissao             text,
                permissao_associada             boolean,
                permissao_bloqueada             boolean,
                cod_motivo_permissao_bloqueada  bigint,
                nome_motivo_permissao_bloqueada citext,
                observacao_permissao_bloqueada  text
            )
    language plpgsql
as
$$
declare
    pilares_liberados_unidade bigint[] := (select array_agg(upp.cod_pilar)
                                           from unidade_pilar_prolog upp
                                           where upp.cod_unidade = f_cod_unidade);
begin
    return query
        with permissoes_cargo_unidade as (
            select cfp.cod_funcao_colaborador as cod_cargo,
                   cfp.cod_unidade            as cod_unidade_cargo,
                   cfp.cod_funcao_prolog      as cod_funcao_prolog,
                   cfp.cod_pilar_prolog       as cod_pilar_prolog
            from cargo_funcao_prolog_v11 cfp
            where cfp.cod_unidade = f_cod_unidade
              and cfp.cod_funcao_colaborador = f_cod_cargo
        )

        select f_cod_cargo                       as cod_cargo,
               f_cod_unidade                     as cod_unidade_cargo,
               f.nome::text                      as nome_cargo,
               fp.cod_pilar                      as cod_pilar,
               pp.pilar                          as nome_pilar,
               fp.cod_agrupamento                as cod_funcionalidade,
               fpa.nome                          as nome_funcionalidade,
               fp.codigo                         as cod_permissao,
               fp.funcao                         as nome_permissao,
               fp.impacto                        as impacto_permissao,
               fp.descricao                      as descricao_permissao,
               pcu.cod_unidade_cargo is not null as permissao_associada,
               fpb.cod_funcao_prolog is not null as permissao_bloqueada,
               fpb.cod_motivo_bloqueio           as cod_motivo_permissao_bloqueada,
               fpmb.motivo                       as nome_motivo_permissao_bloqueada,
               f_if(fpb.observacao_bloqueio is null,
                    fpmb.descricao,
                    fpb.observacao_bloqueio)     as observacao_permissao_bloqueada
        from pilar_prolog pp
                 join funcao_prolog_v11 fp on fp.cod_pilar = pp.codigo
                 join unidade_pilar_prolog upp on upp.cod_pilar = pp.codigo
                 join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
                 join funcao f on f.codigo = f_cod_cargo
                 left join permissoes_cargo_unidade pcu on pcu.cod_funcao_prolog = fp.codigo
                 left join funcao_prolog_bloqueada fpb
                           on fp.codigo = fpb.cod_funcao_prolog
                               and fp.cod_pilar = fpb.cod_pilar_funcao
                               and fpb.cod_unidade = f_cod_unidade
                 left join funcao_prolog_motivo_bloqueio fpmb on fpb.cod_motivo_bloqueio = fpmb.codigo
        where upp.cod_unidade = f_cod_unidade
          and fp.cod_pilar = any (pilares_liberados_unidade)
        order by pp.pilar, fp.cod_agrupamento, fp.impacto desc;
end;
$$;

create or replace function func_cargos_get_cargos_em_uso(f_cod_unidade bigint)
    returns table
            (
                cod_cargo                    bigint,
                nome_cargo                   text,
                qtd_colaboradores_vinculados bigint,
                qtd_permissoes_vinculadas    bigint
            )
    language plpgsql
as
$$
declare
    pilares_liberados_unidade bigint[] := (select array_agg(upp.cod_pilar)
                                           from unidade_pilar_prolog upp
                                           where upp.cod_unidade = f_cod_unidade);
begin
    return query
        with cargos_em_uso as (
            select distinct cod_funcao
            from colaborador c
            where c.cod_unidade = f_cod_unidade
        )

        select f.codigo                                                        as cod_cargo,
               f.nome::text                                                    as nome_cargo,
               (select count(*)
                from colaborador c
                where c.cod_funcao = f.codigo
                  and c.cod_unidade = f_cod_unidade)                           as qtd_colaboradores_vinculados,
               -- Se não tivesse esse FILTER, cargos que não possuem nenhuma permissão vinculada retornariam 1.
               count(*)
               filter (where cfp.cod_unidade is not null
                   -- Consideramos apenas as permissões de pilares liberados para a unidade.
                   and cfp.cod_pilar_prolog = any (pilares_liberados_unidade)) as qtd_permissoes_vinculadas
        from funcao f
                 left join cargo_funcao_prolog_v11 cfp
                           on f.codigo = cfp.cod_funcao_colaborador
                               and cfp.cod_unidade = f_cod_unidade
             -- Não podemos simplesmente filtrar pelo código da unidade presente na tabela CARGO_FUNCAO_PROLOG_V11,
             -- pois desse modo iríamos remover do retorno cargos usados mas sem permissões vinculadas. Por isso
             -- utilizamos esse modo de filtragem com a CTE criada acima.
        where f.codigo in (select *
                           from cargos_em_uso)
        group by f.codigo, f.nome
        order by f.nome;
end ;
$$;


-- Alteramos as listagens para não contar permissões bloqueadas
create or replace function func_cargos_get_cargos_nao_utilizados(f_cod_unidade bigint)
    returns table
            (
                cod_cargo                 bigint,
                nome_cargo                text,
                qtd_permissoes_vinculadas bigint
            )
    language plpgsql
as
$$
declare
    pilares_liberados_unidade bigint[] := (select array_agg(upp.cod_pilar)
                                           from unidade_pilar_prolog upp
                                           where upp.cod_unidade = f_cod_unidade);
begin
    return query
        with cargos_em_uso as (
            select distinct cod_funcao
            from colaborador c
            where c.cod_unidade = f_cod_unidade
        )

        select f.codigo                                                        as cod_cargo,
               f.nome :: text                                                  as nome_cargo,
               count(*)
               filter (where cfp.cod_unidade is not null
                   -- Consideramos apenas as permissões de pilares liberados para a unidade.
                   and cfp.cod_pilar_prolog = any (pilares_liberados_unidade)) as qtd_permissoes_vinculadas
        from funcao f
                 left join cargo_funcao_prolog_v11 cfp
                           on f.codigo = cfp.cod_funcao_colaborador
                               and cfp.cod_unidade = f_cod_unidade
             -- Para buscar os cargos não utilizados, adotamos a lógica de buscar todos os da empresa e depois
             -- remover os que tem colaboradores vinculados, isso é feito nas duas condições abaixo do WHERE.
        where f.cod_empresa = (select u.cod_empresa
                               from unidade u
                               where u.codigo = f_cod_unidade)
          and f.codigo not in (select *
                               from cargos_em_uso)
        group by f.codigo, f.nome
        order by f.nome;
end;
$$;

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################

-- Criamos functions para ser utilizadas pelo suporte, auxiliando o bloqueio em massa.
-- Cenários que devemos cobrir:
-- · Bloqueio/liberação de uma lista avulsa de permissões - Todas unidades da empresa, Unidades específicas;
--   Obs: As functions abaixo são uma variação desta, que é a function principal.
-- · Bloqueio/liberação de um pilar para - Todas unidades da empresa, Unidades específicas;
-- · Bloqueio/liberação de um agrupamento para - Todas unidades da empresa, Unidades específicas;

-- Function para fazer a liberação ou bloqueio com base em códigos de permissões específicos.
create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear text,
                                                             f_cod_permissoes bigint[],
                                                             f_cod_empresas bigint[],
                                                             f_cod_motivo_bloqueio bigint,
                                                             f_observacao_bloqueio text default null,
                                                             f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes_nao_mapeados bigint[] := (select array_agg(permissoes.cod_permissao)
                                               from (select unnest(f_cod_permissoes) as cod_permissao) permissoes
                                               where permissoes.cod_permissao not in
                                                     (select fp.codigo from funcao_prolog_v11 fp));
    v_cod_empresas_nao_mapeadas   bigint[] := (select array_agg(empresas.cod_empresa)
                                               from (select unnest(f_cod_empresas) as cod_empresa) empresas
                                               where empresas.cod_empresa not in (select e.codigo from empresa e));
    v_cod_unidades_nao_mapeadas   bigint[] := (select array_agg(unidades.cod_unidade)
                                               from (select unnest(f_cod_unidades) as cod_unidade) unidades
                                               where unidades.cod_unidade not in
                                                     (select u.codigo
                                                      from unidade u
                                                      where u.cod_empresa = any (f_cod_empresas)));
    -- Caso o usuário informar as unidades, utilizaremos elas para liberar ou bloquear as permissões. Caso o usuário
    -- forneça somente a empresa, utilizamos todas as unidades de empresa para liberar ou bloquear as permissões.
    v_cod_unidades_mapeadas       bigint[] := (f_if(f_cod_unidades is not null,
                                                    f_cod_unidades,
                                                    (select array_agg(u.codigo)
                                                     from unidade u
                                                     where u.cod_empresa = any (f_cod_empresas))));
begin
    if (upper(f_liberar_bloquear) != 'LIBERAR' and upper(f_liberar_bloquear) != 'BLOQUEAR')
    then
        perform throw_generic_error('Deve-se informar o tipo correto da operação: LIBERAR ou BLOQUEAR.');
    end if;

    if (f_size_array(v_cod_permissoes_nao_mapeados) > 0)
    then
        perform throw_generic_error(
                format('Códigos de permissões inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_v11.',
                       v_cod_permissoes_nao_mapeados));
    end if;

    if (f_size_array(v_cod_empresas_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('Nenhuma empresa encontrada para os códigos (%s)', v_cod_empresas_nao_mapeadas));
    end if;

    if (f_size_array(v_cod_unidades_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('As unidades (%s) não pertencem as empresas (%s)',
                       v_cod_unidades_nao_mapeadas,
                       f_cod_empresas));
    end if;

    -- Depois de validar os atributos necessários, fazemos o bloqueio ou liberação.
    if (upper(f_liberar_bloquear) = 'LIBERAR')
    then
        -- Devemos deletar da tabela de bloqueio as permissões com códigos e unidades mapeadas.
        delete
        from funcao_prolog_bloqueada
        where cod_funcao_prolog = any (f_cod_permissoes)
          and cod_unidade = any (v_cod_unidades_mapeadas);
    else
        if (select not exists(select codigo from funcao_prolog_motivo_bloqueio where codigo = f_cod_motivo_bloqueio))
        then
            perform throw_generic_error(
                    format('O motivo do bloqueio informado (%s) não é válido', f_cod_motivo_bloqueio));
        end if;

        -- Devemos inserir na tabela de bloqueio as permissões com códigos e unidades mapeadas.
        insert into funcao_prolog_bloqueada (cod_unidade,
                                             cod_pilar_funcao,
                                             cod_funcao_prolog,
                                             cod_motivo_bloqueio,
                                             observacao_bloqueio)
        select unnest(v_cod_unidades_mapeadas) as cod_unidade,
               fp.cod_pilar,
               fp.codigo,
               f_cod_motivo_bloqueio,
               f_observacao_bloqueio
        from funcao_prolog_v11 as fp
        where fp.codigo = any (f_cod_permissoes)
        on conflict
            on constraint pk_funcao_prolog_bloqueada
            do update
            set cod_motivo_bloqueio = f_cod_motivo_bloqueio,
                observacao_bloqueio = f_observacao_bloqueio;

        -- Devemos também remover essas permissões dos cargos associados nas unidades.
        delete
        from cargo_funcao_prolog_v11
        where cod_unidade = any (v_cod_unidades_mapeadas)
          and cod_funcao_prolog = any (f_cod_permissoes);
    end if;

    return (select format('A operação de %s foi realizada com sucesso para as permissões (%s)',
                          f_liberar_bloquear, f_cod_permissoes));
end;
$$;

-- Function para fazer a liberação ou bloqueio com base em pilares.
create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_pilar(f_liberar_bloquear text,
                                                            f_cod_pilares bigint[],
                                                            f_cod_empresas bigint[],
                                                            f_cod_motivo_bloqueio bigint,
                                                            f_observacao_bloqueio text default null,
                                                            f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes bigint[] := (select array_agg(fp.codigo)
                                  from funcao_prolog_v11 fp
                                  where fp.cod_pilar = any (f_cod_pilares));
begin
    -- Validamos apenas os pilares não mapeados, demais validações são feitas pela function interna.
    perform func_garante_pilares_validos(f_cod_pilares::integer[]);

    perform suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear,
                                                                     v_cod_permissoes,
                                                                     f_cod_empresas,
                                                                     f_cod_motivo_bloqueio,
                                                                     f_observacao_bloqueio,
                                                                     f_cod_unidades);

    return (select format('A operação de %s foi realizada com sucesso para as permissões dos pilares (%s)',
                          f_liberar_bloquear, f_cod_pilares));
end;
$$;

-- Function para fazer a liberação ou bloqueio com base em agrupamentos de permissões.
create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_agrupamento(f_liberar_bloquear text,
                                                                  f_cod_agrupamentos bigint[],
                                                                  f_cod_empresas bigint[],
                                                                  f_cod_motivo_bloqueio bigint,
                                                                  f_observacao_bloqueio text default null,
                                                                  f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_agrupamentos_nao_mapeados bigint[] := (select array_agg(agrupamento.cod_agrupamento)
                                                 from (select unnest(f_cod_agrupamentos) as cod_agrupamento) agrupamento
                                                 where agrupamento.cod_agrupamento not in
                                                       (select fpa.codigo from funcao_prolog_agrupamento fpa));
    v_cod_permissoes                bigint[] := (select array_agg(fp.codigo)
                                                 from funcao_prolog_v11 fp
                                                 where fp.cod_agrupamento = any (f_cod_agrupamentos));
begin
    -- Validamos apenas os agrupamentos não mapeados, demais validações são feitas pela function interna.
    if (f_size_array(v_cod_agrupamentos_nao_mapeados) > 0)
    then
        perform throw_generic_error(
                format('Códigos de agrupamentos inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_agrupamento.',
                       v_cod_agrupamentos_nao_mapeados));
    end if;

    perform suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear,
                                                                     v_cod_permissoes,
                                                                     f_cod_empresas,
                                                                     f_cod_motivo_bloqueio,
                                                                     f_observacao_bloqueio,
                                                                     f_cod_unidades);

    return (select format('A operação de %s foi realizada com sucesso para as permissões dos agrupamentos (%s)',
                          f_liberar_bloquear, f_cod_agrupamentos));
end;
$$;

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################

-- Altera relatório para não trazer permissões bloqueadas (luiz_fp - PL-2671)
create or replace function func_cargos_relatorio_permissoes_detalhadas(f_cod_unidades bigint[])
    returns table
            (
                UNIDADE                 text,
                CARGO                   text,
                PILAR                   text,
                "FUNCIONALIDADE PROLOG" text,
                "PERMISSÃO PROLOG"      text,
                "IMPACTO PERMISSÃO"     text,
                "DESCRIÇÃO PERMISSÃO"   text
            )
    language plpgsql
as
$$
begin
    return query
        select u.nome::text       as nome_unidade,
               f.nome::text       as nome_cargo,
               pp.pilar::text     as nome_pilar,
               fpa.nome::text     as nome_funcionalidade,
               fp.funcao::text    as nome_permissao,
               fp.impacto::text   as impacto_permissao,
               fp.descricao::text as descricao_permissao
        from unidade u
                 join cargo_funcao_prolog_v11 cfp on u.codigo = cfp.cod_unidade
                 join funcao_prolog_v11 fp on fp.codigo = cfp.cod_funcao_prolog
                 join pilar_prolog pp on cfp.cod_pilar_prolog = pp.codigo
                 join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
                 join funcao f on f.codigo = cfp.cod_funcao_colaborador
        where u.codigo = any (f_cod_unidades)
        order by nome_unidade, nome_cargo, fp.impacto desc;
end;
$$;


-- Altera function para não considerar permissões bloqueadas (luiz_fp - PL-2671)
create or replace function suporte.func_colaborador_busca_por_permissao_empresa(f_cod_empresa bigint,
                                                                                f_cod_permissao bigint)
    returns table
            (
                funcionalidade  text,
                permissao       text,
                cod_empresa     bigint,
                empresa         text,
                cod_unidade     bigint,
                unidade         text,
                cod_colaborador bigint,
                colaborador     text,
                cpf             bigint,
                data_nascimento date,
                cargo           text
            )
    language plpgsql
as
$$
begin
    return query
        select fpa.nome::text    as funcionalidade,
               fp.funcao::text   as permissao,
               e.codigo          as cod_empresa,
               e.nome::text      as empresa,
               u.codigo          as cod_unidade,
               u.nome::text      as unidade,
               c.codigo          as cod_colaborador,
               c.nome::text      as colaborador,
               c.cpf             as cpf,
               c.data_nascimento as data_nascimento,
               f.nome::text      as cargo
        from colaborador c
                 left join cargo_funcao_prolog_v11 cfp
                           on cfp.cod_funcao_colaborador = c.cod_funcao and cfp.cod_unidade = c.cod_unidade
                 left join unidade u on u.codigo = c.cod_unidade
                 left join empresa e on e.codigo = c.cod_empresa
                 left join funcao f on f.codigo = c.cod_funcao
                 left join funcao_prolog_v11 fp on fp.codigo = cfp.cod_funcao_prolog
                 left join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
        where c.cod_empresa = f_cod_empresa
          and c.status_ativo = true
          and cfp.cod_funcao_prolog = f_cod_permissao
        order by unidade, colaborador;
end;
$$;

-- novas functions para usar no WS
create or replace function func_dashboard_get_componentes_colaborador(f_user_token text)
    returns table
            (
                codigo_componente           smallint,
                identificador_tipo          text,
                cod_pilar_prolog_componente smallint,
                titulo_componente           text,
                subtitulo_componente        text,
                descricao_componente        text,
                qtd_blocos_horizontais      smallint,
                qtd_blocos_verticais        smallint,
                url_endpoint_dados          text
            )
    language sql
as
$$
select dc.codigo                      as codigo_componente,
       dct.identificador_tipo::text   as identificador_tipo,
       dc.cod_pilar_prolog_componente as cod_pilar_prolog_componente,
       dc.titulo::text                as titulo_componente,
       dc.subtitulo::text             as subtitulo_componente,
       dc.descricao                   as descricao_componente,
       dc.qtd_blocos_horizontais      as qtd_blocos_horizontais,
       dc.qtd_blocos_verticais        as qtd_blocos_verticais,
       dc.url_endpoint_dados          as url_endpoint_dados
from dashboard_componente dc
         join token_autenticacao ta on ta.token = f_user_token
         join colaborador c on ta.cpf_colaborador = c.cpf
         join cargo_funcao_prolog_v11 cfp
              on c.cod_funcao = cfp.cod_funcao_colaborador and c.cod_unidade = cfp.cod_unidade
         join dashboard_componente_funcao_prolog dcfp
              on cfp.cod_funcao_prolog = dcfp.cod_funcao_prolog and dc.codigo = dcfp.cod_componente
         join dashboard_componente_tipo dct on dc.cod_tipo_componente = dct.codigo
where dc.ativo = true
order by cod_pilar_prolog_componente;
$$;

-- Única function que necessita da tratativa da tabela de bloqueio de fechamento.
create or replace function func_empresa_get_funcoes_pilares_by_unidade(f_cod_unidade bigint)
    returns table
            (
                cod_pilar  bigint,
                pilar      text,
                cod_funcao bigint,
                funcao     text
            )
    language sql
as
$$
select distinct pp.codigo        as cod_pilar,
                pp.pilar::text   as pilar,
                fpv.codigo       as cod_funcao,
                fpv.funcao::text as funcao
from pilar_prolog pp
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo
         join unidade_pilar_prolog upp on upp.cod_pilar = pp.codigo
where upp.cod_unidade = f_cod_unidade
  and fpv.codigo not in (select fpb.cod_funcao_prolog
                         from funcao_prolog_bloqueada fpb
                         where fpb.cod_unidade = f_cod_unidade)
order by pilar, funcao;
$$;


create or replace function func_empresa_get_funcoes_pilares_by_cargo(f_cod_unidade bigint,
                                                                     f_cod_cargo_colaborador bigint)
    returns table
            (
                cod_pilar  bigint,
                pilar      text,
                cod_funcao bigint,
                funcao     text
            )
    language sql
as
$$
select distinct pp.codigo        as cod_pilar,
                pp.pilar::text   as pilar,
                fpv.codigo       as cod_funcao,
                fpv.funcao::text as funcao
from cargo_funcao_prolog_v11 cfp
         join pilar_prolog pp on pp.codigo = cfp.cod_pilar_prolog
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo and fpv.codigo = cfp.cod_funcao_prolog
where cfp.cod_unidade = f_cod_unidade
  and cfp.cod_funcao_colaborador = f_cod_cargo_colaborador
order by pilar, funcao;
$$;


create or replace function func_empresa_tem_permissao_funcao_prolog(f_cod_unidade bigint,
                                                                    f_cod_funcao_colaborador bigint,
                                                                    f_cod_funcao_prolog bigint)
    returns boolean
    language sql
as
$$
select exists(select *
              from cargo_funcao_prolog_v11 cfp
              where cfp.cod_unidade = f_cod_unidade
                and cfp.cod_funcao_colaborador = f_cod_funcao_colaborador
                and cfp.cod_funcao_prolog = f_cod_funcao_prolog) as tem_permissao;
$$;


create or replace function func_colaborador_tem_permissao_funcao_prolog(f_cpf_colaborador bigint,
                                                                        f_cod_pilar_prolog bigint,
                                                                        f_cod_funcao_prolog bigint)
    returns boolean
    language sql
as
$$
select exists(select c.cpf
              from colaborador c
                       join cargo_funcao_prolog_v11 cfp
                            on c.cod_funcao = cfp.cod_funcao_colaborador and c.cod_unidade = cfp.cod_unidade
              where c.cpf = f_cpf_colaborador
                and cfp.cod_pilar_prolog = f_cod_pilar_prolog
                and cfp.cod_funcao_prolog = f_cod_funcao_prolog);
$$;


create or replace function func_colaborador_get_colaboradores_acesso_funcao_prolog(f_cod_unidade bigint,
                                                                                   f_cod_funcao_prolog bigint)
    returns table
            (
                cpf              bigint,
                nome_colaborador text,
                data_nascimento  date,
                nome_cargo       text,
                codigo_cargo     bigint
            )
    language sql
as
$$
select c.cpf             as cpf,
       c.nome::text      as nome_colaborador,
       c.data_nascimento as data_nascimento,
       f.nome            as nome_cargo,
       f.codigo          as codigo_cargo
from colaborador c
         join cargo_funcao_prolog_v11 cfp
              on c.cod_unidade = cfp.cod_unidade and c.cod_funcao = cfp.cod_funcao_colaborador
         join funcao f
              on f.codigo = c.cod_funcao and f.codigo = cfp.cod_funcao_colaborador and c.cod_empresa = f.cod_empresa
where c.cod_unidade = f_cod_unidade
  and cfp.cod_funcao_prolog = f_cod_funcao_prolog
  and c.status_ativo = true
order by codigo_cargo;
$$;


create or replace function func_colaborador_get_funcoes_pilares_by_cpf(f_cpf_colaborador bigint)
    returns table
            (
                cod_pilar  bigint,
                pilar      text,
                cod_funcao bigint,
                funcao     text
            )
    language sql
as
$$
select distinct pp.codigo        as cod_pilar,
                pp.pilar::text   as pilar,
                fpv.codigo       as cod_funcao,
                fpv.funcao::text as funcao
from cargo_funcao_prolog_v11 cfv
         join pilar_prolog pp on pp.codigo = cfv.cod_pilar_prolog
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo and fpv.codigo = cfv.cod_funcao_prolog
         join colaborador c on c.cod_unidade = cfv.cod_unidade and cfv.cod_funcao_colaborador = c.cod_funcao
         join unidade_pilar_prolog upp on upp.cod_unidade = c.cod_unidade and upp.cod_pilar = cfv.cod_pilar_prolog
where c.cpf = f_cpf_colaborador
order by pilar, funcao;
$$;


create or replace function func_colaborador_verifica_possui_funcao_prolog(f_cod_colaborador bigint,
                                                                          f_cod_funcao_prolog integer)
    returns boolean
    language sql
as
$$
select exists(
               select c.codigo
               from colaborador c
                        join cargo_funcao_prolog_v11 cargo
                             on c.cod_unidade = cargo.cod_unidade and c.cod_funcao = cargo.cod_funcao_colaborador
               where c.codigo = f_cod_colaborador
                 and cargo.cod_funcao_prolog = f_cod_funcao_prolog) as tem_permissao
$$;