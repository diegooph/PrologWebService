-- Alteramos a constraint para facilitar alguns suportes de mudança de CPF, como a PS-1234.
alter table treinamento_colaborador
    drop constraint fk_treinamento_colaborador_colaborador,
    add constraint fk_treinamento_colaborador_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;