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