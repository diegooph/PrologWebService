create or replace function func_garante_modelo_pneu_existe(f_cod_modelo_pneu bigint,
                                                           f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message constant text not null := f_if(f_error_message is null,
                                                   format('Modelo de pneu de código %s não existe!', f_cod_modelo_pneu),
                                                   f_error_message);
begin
    if not exists(select mp.codigo from modelo_pneu mp where mp.codigo = f_cod_modelo_pneu)
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;