create or replace function func_garante_veiculo_existe_by_codigo(f_cod_unidade_veiculo bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_considerar_deletados boolean default true,
                                                                 f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message text :=
        f_if(f_error_message is null,
             format('Não foi possível encontrar o veículo com estes parâmetros: Unidade %s, Código %s',
                    f_cod_unidade_veiculo, f_cod_veiculo),
             f_error_message);
begin
    if not exists(select vd.codigo
                  from veiculo_data vd
                  where vd.codigo = f_cod_veiculo
                    and vd.cod_unidade = f_cod_unidade_veiculo
                    and f_if(f_considerar_deletados, true, vd.deletado = false))
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;