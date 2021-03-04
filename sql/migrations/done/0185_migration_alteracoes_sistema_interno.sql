alter table interno.prolog_user
    rename to usuario_prolog;

create table if not exists interno.usuario_prolog_sessao
(
    token                uuid                     not null,
    cod_usuario          bigint                   not null,
    data_hora_criacao    timestamp with time zone not null,
    data_hora_ultimo_uso timestamp with time zone not null,
    constraint pk_usuario_prolog_sessao primary key (token),
    constraint fk_usuario foreign key (cod_usuario) references interno.usuario_prolog (codigo)
);

create or replace function interno.func_usuario_iniciar_sessao(f_cod_usuario bigint,
                                                               f_token_usuario text,
                                                               f_data_hora_atual timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    insert into interno.usuario_prolog_sessao (token,
                                               cod_usuario,
                                               data_hora_criacao,
                                               data_hora_ultimo_uso)
    values (f_token_usuario,
            f_cod_usuario,
            f_data_hora_atual,
            f_data_hora_atual);

    if not found
    then
        perform throw_generic_error('Erro ao criar sessão do usuário Prolog, tente novamente.');
    end if;
end
$$;

-- Altera function para retornar mais informações.
drop function interno.func_busca_dados_usuario(f_username text);
create or replace function interno.func_usuario_busca_dados_by_username(f_username text)
    returns table
            (
                codigo             bigint,
                username           text,
                encrypted_password text,
                database_username  text
            )
    language plpgsql
as
$$
begin
    if (select not exists(select up.username
                          from interno.usuario_prolog up
                          where up.username = f_username))
    then
        perform throw_generic_error(format('Usuário "%s" não encontrado!', f_username));
    end if;

    if ((select up.active
         from interno.usuario_prolog up
         where up.username = f_username) = false)
    then
        perform throw_generic_error(format('Usuário "%s" não está ativo!', f_username));
    end if;

    return query
        select up.codigo,
               up.username,
               up.password,
               up.database_username
        from interno.usuario_prolog up
        where up.username = f_username
          and active = true;
end;
$$;

create or replace function interno.func_usuario_busca_dados_by_token(f_token text)
    returns table
            (
                codigo             bigint,
                username           text,
                encrypted_password text,
                database_username  text
            )
    language plpgsql
as
$$
declare
    v_username constant text := (select up.username
                                 from interno.usuario_prolog up
                                          join interno.usuario_prolog_sessao ups on up.codigo = ups.cod_usuario
                                 where ups.token = f_token);
begin
    return query
    select f.codigo             as codigo,
           f.username           as username,
           f.encrypted_password as encrypted_password,
           f.database_username  as database_username
    from interno.func_usuario_busca_dados_by_username(v_username) f;
end;
$$;

create or replace function interno.func_busca_nome_usuario_banco(f_username text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    if (select not exists(select up.username
                          from interno.usuario_prolog up
                          where up.username = f_username))
    then
        perform throw_generic_error(format('Usuário "%s" não encontrado!', f_username));
    end if;

    if ((select up.active
         from interno.usuario_prolog up
         where up.username = f_username) = false)
    then
        perform throw_generic_error(format('Usuário "%s" não está ativo!', f_username));
    end if;

    if (select up.database_username is null
        from interno.usuario_prolog up
        where up.username = f_username)
    then
        perform throw_generic_error(format('Usuário "%s" não tem um usuário de banco de dados vinculado!', f_username));
    end if;

    return
        (select up.database_username
         from interno.usuario_prolog up
         where up.username = f_username
           and active = true);
end;
$$;