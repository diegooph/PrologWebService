-- Alteramos a constraint para facilitar alguns suportes de mudança de CPF, como a PS-1234.
alter table solicitacao_folga
    drop constraint fk_solicitacao_folga_colaborador,
    add constraint fk_solicitacao_folga_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table solicitacao_folga
    drop constraint fk_solicitacao_folga_colaborador_feedback,
    add constraint fk_solicitacao_folga_colaborador_feedback
        foreign key (cpf_feedback) references colaborador_data (cpf) on update cascade;