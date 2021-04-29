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