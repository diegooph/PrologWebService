alter table colaborador_data
    add column cod_colaborador_cadastro bigint;

alter table colaborador_data
    add column cod_colaborador_ultima_atualizacao bigint;

create or replace view colaborador as
select
    c.cpf,
    c.matricula_ambev,
    c.matricula_trans,
    c.data_nascimento,
    c.data_admissao,
    c.data_demissao,
    c.status_ativo,
    c.nome,
    c.cod_equipe,
    c.cod_funcao,
    c.cod_unidade,
    c.cod_permissao,
    c.cod_empresa,
    c.cod_setor,
    c.pis,
    c.data_hora_cadastro,
    c.codigo,
    c.cod_unidade_cadastro,
    c.cod_colaborador_cadastro,
    c.cod_colaborador_ultima_atualizacao
from colaborador_data c
where c.deletado = false;

drop function func_colaborador_update_colaborador(f_cod_colaborador bigint,
    f_cpf bigint,
    f_matricula_ambev integer,
    f_matricula_trans integer,
    f_data_nascimento date,
    f_data_admissao date,
    f_nome varchar,
    f_cod_setor bigint,
    f_cod_funcao integer,
    f_cod_unidade integer,
    f_cod_permissao bigint,
    f_cod_empresa bigint,
    f_cod_equipe bigint,
    f_pis varchar,
    f_sigla_iso2 character varying,
    f_prefixo_pais integer,
    f_telefone text,
    f_email email,
    f_token text);

create or replace function func_colaborador_update_colaborador(f_cod_colaborador bigint,
                                                               f_cpf bigint,
                                                               f_matricula_ambev integer,
                                                               f_matricula_trans integer,
                                                               f_data_nascimento date,
                                                               f_data_admissao date,
                                                               f_nome varchar,
                                                               f_cod_setor bigint,
                                                               f_cod_funcao integer,
                                                               f_cod_unidade integer,
                                                               f_cod_permissao bigint,
                                                               f_cod_empresa bigint,
                                                               f_cod_equipe bigint,
                                                               f_pis varchar,
                                                               f_sigla_iso2 character varying,
                                                               f_prefixo_pais integer,
                                                               f_telefone text,
                                                               f_email email,
                                                               f_token text)
    returns bigint
    language plpgsql
as
$$
declare
f_cod_colaborador_update constant bigint := (select cod_colaborador
                                                 from token_autenticacao
                                                 where token = f_token);
begin
update colaborador
set cpf                                = f_cpf,
    matricula_ambev                    = f_matricula_ambev,
    matricula_trans                    = f_matricula_trans,
    data_nascimento                    = f_data_nascimento,
    data_admissao                      = f_data_admissao,
    nome                               = f_nome,
    cod_setor                          = f_cod_setor,
    cod_funcao                         = f_cod_funcao,
    cod_unidade                        = f_cod_unidade,
    cod_permissao                      = f_cod_permissao,
    cod_empresa                        = f_cod_empresa,
    cod_equipe                         = f_cod_equipe,
    pis                                = f_pis,
    cod_colaborador_ultima_atualizacao = f_cod_colaborador_update
where codigo = f_cod_colaborador;

-- validamos se houve alguma atualização dos valores.
if not found
    then
        perform throw_generic_error('erro ao atualizar os dados do colaborador, tente novamente');
end if;

    -- será permitido somente 1 email e telefone por colaborador no lançamento inicial.
    -- deletamos email e telefone vinculados ao colaborador
delete from colaborador_email where cod_colaborador = f_cod_colaborador;
delete from colaborador_telefone where cod_colaborador = f_cod_colaborador;

if f_prefixo_pais is not null and f_telefone is not null
    then
        insert into colaborador_telefone (sigla_iso2,
                                          prefixo_pais,
                                          cod_colaborador,
                                          numero_telefone,
                                          cod_colaborador_ultima_atualizacao)
        values (f_sigla_iso2,
                f_prefixo_pais,
                f_cod_colaborador,
                f_telefone,
                f_cod_colaborador_update);

        -- verificamos se o insert do telefone do colaborador funcionou.
        if not found
        then
            perform throw_generic_error(
                            'não foi possível atualizar o colaborador devido a problemas no telefone, tente novamente');
end if;
end if;

    if f_email is not null
    then
        insert into colaborador_email (cod_colaborador,
                                       email,
                                       cod_colaborador_ultima_atualizacao)
                                       values (f_cod_colaborador,
                                               f_email,
                                               f_cod_colaborador_update);

         -- verificamos se o insert do email funcionou.
        if not found
        then
            perform throw_generic_error(
                            'não foi possível atualizar o colaborador devido a problemas no e-mail, tente novamente');
end if;
end if;

return f_cod_colaborador;
end;
$$;

create or replace function suporte.func_colaborador_busca_historico_edicoes(f_cod_colaborador bigint)
    returns table
            (
                cpf                                bigint,
                matricula_ambev                    integer,
                matricula_trans                    integer,
                data_nascimento                    date,
                data_admissao                      date,
                data_demissao                      date,
                status_ativo                       boolean,
                nome                               varchar(255),
                cod_equipe                         bigint,
                cod_funcao                         integer,
                cod_unidade                        integer,
                cod_permissao                      bigint,
                cod_empresa                        bigint,
                cod_setor                          bigint,
                pis                                varchar(11),
                data_hora_cadastro                 timestamp with time zone,
                codigo                             bigint,
                cod_unidade_cadastro               integer,
                deletado                           boolean,
                data_hora_deletado                 timestamp with time zone,
                pg_username_delecao                text,
                motivo_delecao                     text,
                cod_colaborador_cadastro           bigint,
                cod_colaborador_ultima_atualizacao bigint
            )
    language plpgsql
as
$$
begin
    return query
    select (jsonb_populate_record(NULL::colaborador_data, cd.row_log)).*
    from audit.colaborador_data_audit cd
    where cd.row_log ->> 'codigo' = f_cod_colaborador::text
    order by cd.row_log -> 'codigo' desc, cd.data_hora_utc desc;
end
$$;

drop function suporte.func_colaborador_transfere_entre_empresas(f_cod_unidade_origem bigint,
    f_cpf_colaborador bigint,
    f_cod_unidade_destino integer,
    f_cod_empresa_destino bigint,
    f_cod_setor_destino bigint,
    f_cod_equipe_destino bigint,
    f_cod_funcao_destino integer,
    f_matricula_trans integer default null,
    f_matricula_ambev integer default null,
    f_nivel_permissao integer default 0,
    out aviso_colaborador_transferido text);

create or replace function suporte.func_colaborador_transfere_entre_empresas(f_cod_unidade_origem bigint,
                                                                             f_cpf_colaborador bigint,
                                                                             f_cod_unidade_destino integer,
                                                                             f_cod_empresa_destino bigint,
                                                                             f_cod_setor_destino bigint,
                                                                             f_cod_equipe_destino bigint,
                                                                             f_cod_funcao_destino integer,
                                                                             f_matricula_trans integer default null,
                                                                             f_matricula_ambev integer default null,
                                                                             f_nivel_permissao integer default 0,
                                                                             out aviso_colaborador_transferido text)
    returns text
    language plpgsql
    security definer
as
$$
declare
empresa_origem bigint := (select u.cod_empresa as empresa_origem
                              from unidade u
                              where u.codigo = f_cod_unidade_origem);
begin
    perform suporte.func_historico_salva_execucao();
    -- verifica se empresa origem/destino são distintas
    perform func_garante_empresas_distintas(empresa_origem, f_cod_empresa_destino);

    -- verifica se unidade destino existe e se pertence a empresa.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_destino, f_cod_unidade_destino);

    --verifica se o colaborador está cadastrado e se pertence a unidade origem.
    perform func_garante_integridade_unidade_colaborador(f_cod_unidade_origem, f_cpf_colaborador);

    -- verifica se o setor existe na unidade destino.
    perform func_garante_setor_existe(f_cod_unidade_destino, f_cod_setor_destino);

    -- verifica se a equipe existe na unidade destino.
    perform func_garante_equipe_existe(f_cod_unidade_destino, f_cod_equipe_destino);

    -- verifica se a função existe na empresa destino.
    perform func_garante_cargo_existe(f_cod_empresa_destino, f_cod_funcao_destino);

    -- verifica se permissão existe
    if not exists(select p.codigo from permissao p where p.codigo = f_nivel_permissao)
    then
        raise exception 'não existe permissão com o código: %', f_nivel_permissao;
end if;

    -- transfere colaborador
update colaborador
set cod_unidade                        = f_cod_unidade_destino,
    cod_empresa                        = f_cod_empresa_destino,
    cod_setor                          = f_cod_setor_destino,
    cod_equipe                         = f_cod_equipe_destino,
    cod_funcao                         = f_cod_funcao_destino,
    matricula_trans                    = f_matricula_trans,
    matricula_ambev                    = f_matricula_ambev,
    cod_permissao                      = f_nivel_permissao,
    cod_colaborador_ultima_atualizacao = null
    where cpf = f_cpf_colaborador
        and cod_unidade = f_cod_unidade_origem;

select ('COLABORADOR: '
    || (select c.nome from colaborador c where c.cpf = f_cpf_colaborador)
    || ' , TRANSFERIDO PARA A UNIDADE: '
    || (select u.nome from unidade u where u.codigo = f_cod_unidade_destino))
into aviso_colaborador_transferido;
end;
$$;