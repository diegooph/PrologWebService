create or replace function integracao.func_garante_token_empresa(f_cod_empresa bigint,
                                                                 f_token_integracao text,
                                                                 f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    error_message text :=
        f_if(f_error_message is null,
             format('Token não autorizado para a empresa %s', f_cod_empresa),
             f_error_message);
begin
    if (f_cod_empresa is null or f_cod_empresa not in (select ti.cod_empresa
                                                       from integracao.token_integracao ti
                                                       where ti.token_integracao = f_token_integracao))
    then
        perform throw_client_side_error(error_message);
    end if;
end;
$$;