create function func_veiculo_get_cod_pneus_aplicados(f_cod_veiculo bigint)
    returns table
            (
                cod_pneu bigint
            )
    language plpgsql
as
$$
begin
    return query
        select vp.cod_pneu as cod_pneu
        from veiculo_pneu vp
        where vp.cod_veiculo = f_cod_veiculo;
end;
$$;