create trigger tg_func_audit_unidade_valores_rm
    after insert or update or delete
    on unidade_valores_rm
    for each row
execute procedure audit.func_audit();