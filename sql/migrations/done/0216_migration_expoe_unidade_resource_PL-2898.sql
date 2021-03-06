drop function if exists func_unidade_atualiza(f_cod_unidade bigint,
    f_nome_unidade varchar(40),
    f_cod_auxiliar_unidade text,
    f_latitude_unidade text,
    f_longitude_unidade text);
create or replace function func_unidade_atualiza(f_cod_unidade bigint,
                                                 f_nome_unidade varchar(40),
                                                 f_cod_auxiliar_unidade text,
                                                 f_latitude_unidade text,
                                                 f_longitude_unidade text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_unidade_atualizada bigint;
begin
    update unidade
    set nome              = f_nome_unidade,
        cod_auxiliar      = f_cod_auxiliar_unidade,
        latitude_unidade  = f_latitude_unidade,
        longitude_unidade = f_longitude_unidade
    where codigo = f_cod_unidade
    returning codigo into v_cod_unidade_atualizada;
    return v_cod_unidade_atualizada;
end;
$$;