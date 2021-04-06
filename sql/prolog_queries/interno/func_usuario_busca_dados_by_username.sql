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