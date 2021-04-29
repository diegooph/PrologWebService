-- 2020-10-23 -> Corrige comparação com token (cast para uuid) (luizfp).
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
                                 where ups.token = f_token::uuid);
begin
    return query
    select f.codigo             as codigo,
           f.username           as username,
           f.encrypted_password as encrypted_password,
           f.database_username  as database_username
    from interno.func_usuario_busca_dados_by_username(v_username) f;
end;
$$;