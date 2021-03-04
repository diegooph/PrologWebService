create trigger tg_func_audit_piccolotur_checklist_pendente_para_sincronizar
    after delete
    on piccolotur.checklist_pendente_para_sincronizar
    for each row
execute procedure audit_integracao.func_audit_integracao();