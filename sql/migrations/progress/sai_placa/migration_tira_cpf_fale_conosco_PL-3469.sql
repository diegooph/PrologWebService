alter table fale_conosco
    add column cod_colaborador bigint null;
alter table fale_conosco
    add column cod_colaborador_feedback bigint null;

alter table fale_conosco
    add constraint fk_fale_conosco_cod_colaborador foreign key (cod_colaborador)
        references colaborador_data (codigo);
alter table fale_conosco
    add constraint fk_fale_conosco_cod_colaborador_feedback foreign key (cod_colaborador_feedback)
        references colaborador_data (codigo);

update fale_conosco fc
set cod_colaborador          = (select codigo from colaborador_data where cpf = fc.cpf_colaborador),
    cod_colaborador_feedback = (select codigo from colaborador_data where cpf = fc.cpf_feedback);

alter table fale_conosco
    alter column cod_colaborador set not null;