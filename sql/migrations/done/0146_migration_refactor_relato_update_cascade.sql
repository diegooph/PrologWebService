-- Alteramos a constraint para facilitar alguns suportes de mudança de CPF, como a PS-1234.
alter table relato
    drop constraint fk_relato_colaborador,
    add constraint fk_relato_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table relato
    drop constraint fk_relato_colaborador_classificacao,
    add constraint fk_relato_colaborador_classificacao
        foreign key (cpf_classificacao) references colaborador_data (cpf) on update cascade;
alter table relato
    drop constraint fk_relato_colaborador_fechamento,
    add constraint fk_relato_colaborador_fechamento
        foreign key (cpf_fechamento) references colaborador_data (cpf) on update cascade;