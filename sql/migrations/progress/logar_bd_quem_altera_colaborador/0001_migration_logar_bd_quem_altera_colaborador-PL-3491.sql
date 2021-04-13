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