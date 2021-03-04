-- PL-3298.
create or replace function func_veiculo_get_estado_acoplamento(f_cod_veiculos bigint[])
    returns table
            (
                cod_veiculo                        bigint,
                cod_processo_acoplamento_vinculado bigint,
                posicao_acoplado                   smallint,
                motorizado                         boolean,
                possui_hubodometro                 boolean
            )
    language plpgsql
as
$$
begin
    return query
        select v.codigo             as cod_veiculo,
               vaa.cod_processo     as cod_processo,
               vaa.cod_posicao      as cod_posicao,
               v.motorizado         as motorizado,
               v.possui_hubodometro as possui_hubodometro
        from veiculo v
                 left join veiculo_acoplamento_atual vaa
                           on v.codigo = vaa.cod_veiculo
        where v.codigo = any (f_cod_veiculos);
end;
$$;


-- Corrige throw
create or replace function func_veiculo_remove_acoplamento_atual(f_cod_processo_acoplamento bigint)
    returns void
    language plpgsql
as
$$
begin
    delete
    from veiculo_acoplamento_atual
    where cod_processo = f_cod_processo_acoplamento;

    if not found
    then
        perform throw_server_side_error(format('Erro ao deletar estado atual de acoplamento para o processo: %s.
                                               ', f_cod_processo_acoplamento));
    end if;
end;
$$;