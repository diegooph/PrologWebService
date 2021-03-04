create or replace function migration_checklist.func_migration_12_move_tabela_respostas_antiga()
    returns void
    language plpgsql
as
$func$
begin
    ALTER TABLE checklist_respostas
        SET SCHEMA migration_checklist;
    -- CRIA TRIGGER PARA IMPEDIR A INSERÇÃO/ATUALIZAÇÃO DE REGISTROS NA TABELA CHECKLIST_RESPOSTAS ANTIGA.
    CREATE TRIGGER TG_BLOQUEIO_INSERT_UPDATE_DELETE_CHECKLIST_RESPOSTAS_ANTIGA
        BEFORE INSERT OR UPDATE OR DELETE
        ON MIGRATION_CHECKLIST.CHECKLIST_RESPOSTAS
        FOR EACH ROW
    EXECUTE PROCEDURE TG_FUNC_BLOQUEIO();
end;
$func$;