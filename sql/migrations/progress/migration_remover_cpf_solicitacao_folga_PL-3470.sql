begin transaction;
alter table solicitacao_folga
    add column if not exists cod_colaborador          bigint,
    add constraint fk_solicitacao_folga_cod_colaborador foreign key (cod_colaborador)
        references colaborador_data (codigo),

    add column if not exists cod_colaborador_feedback bigint,
    add constraint fk_solicitacao_folga_cod_colaborador_feedback foreign key (cod_colaborador_feedback)
        references colaborador_data (codigo);

update solicitacao_folga sf
set cod_colaborador          = (select codigo from colaborador_data cd where cd.cpf = sf.cpf_colaborador),
    cod_colaborador_feedback = (select codigo from colaborador_data cd where cd.cpf = sf.cpf_feedback);

alter table solicitacao_folga
    alter column cod_colaborador set not null;

alter table solicitacao_folga
    drop column if exists cpf_colaborador,
    drop column if exists cpf_feedback;

end transaction;