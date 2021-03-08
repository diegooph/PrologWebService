create trigger tg_func_audit_unidade_funcao_produtividade
    after insert or update or delete
    on unidade_funcao_produtividade
    for each row
execute procedure audit.func_audit();