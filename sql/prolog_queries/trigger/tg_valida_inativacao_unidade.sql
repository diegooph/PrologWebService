create
    or replace function tg_valida_inativacao_unidade()
    returns trigger as
$validacao_inativacao_unidade$
begin
    if
        (old.status_ativo)
    then
        if ((select count(*) from colaborador_data where cod_unidade = new.codigo) > 0)
        then
            perform throw_client_side_error(
                    'Não é possível inativar uma unidade que contenha colaboradores vinculados.');
        elseif
            ((select count(*) from veiculo_data where cod_unidade = old.codigo) > 0)
        then
            perform throw_client_side_error(
                    'Não é possível inativar uma unidade que contenha veículos vinculados.');
        elseif
            ((select count(*) from pneu_data where cod_unidade = old.codigo) > 0)
        then
            perform throw_client_side_error(
                    'Não é possível inativar uma unidade que contenha pneus vinculados.');
        end if;
    end if;
    return new;
end;
$validacao_inativacao_unidade$
    language plpgsql;

create
    constraint trigger validacao_inativacao_unidade
    after
        update
    on unidade
    for each row
execute procedure tg_valida_inativacao_unidade();