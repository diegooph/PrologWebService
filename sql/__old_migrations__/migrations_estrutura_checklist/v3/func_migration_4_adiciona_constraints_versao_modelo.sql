create or replace function migration_checklist.func_migration_4_adiciona_constraints_versao_modelo()
    returns void
    language plpgsql
as
$$
begin
    -- Impede novas versão com cod_colaborador_criacao_versao nulo.
    alter table checklist_modelo_versao
        add constraint check_colaborador_not_null_acima_versao_1 check (cod_colaborador_criacao_versao is not null) not valid;

    -- Agora pode ser NOT NULL.
    alter table checklist_modelo_data
        alter column cod_versao_atual set not null;
    alter table checklist_data
        alter column cod_versao_checklist_modelo set not null;

    -- Remove FK única com cod_modelo e usa uma dupla compondo com versão do modelo.
    alter table checklist_perguntas_data
        drop constraint fk_checklist_perguntas_checklist_modelo;
    alter table checklist_perguntas_data
        add constraint fk_checklist_perguntas_checklist_modelo_versao
            foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);

    -- Remove FK única com cod_modelo e usa uma dupla compondo com versão do modelo.
    alter table checklist_alternativa_pergunta_data
        drop constraint fk_checklist_alternativa_pergunta_checklist_modelo;
    alter table checklist_alternativa_pergunta_data
        add constraint fk_checklist_alternativa_pergunta_checklist_modelo_versao
            foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);
end
$$;