create or replace function func_garante_modelo_banda_existe(f_cod_modelo_banda bigint,
                                                            f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message constant text not null := f_if(f_error_message is null,
                                                   format('Modelo de banda de código %s não existe!', f_cod_modelo_banda),
                                                   f_error_message);
begin
    if not exists(select mp.codigo from modelo_banda mp where mp.codigo = f_cod_modelo_banda)
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;