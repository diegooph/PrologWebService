-- Separamos em duas migrations por conta de problemas com triggers.
-- Cria constraint.
alter table pneu_data
    add constraint check_em_uso_nao_deletado
        check (status <> 'EM_USO' or
               (status = 'EM_USO' and deletado is false));