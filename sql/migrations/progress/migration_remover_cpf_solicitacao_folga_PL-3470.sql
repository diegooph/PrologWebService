begin transaction;
alter table solicitacao_folga
    add column if not exists cod_colaborador          bigint,
    add constraint fk_solicitacao_folga_cod_colaborador foreign key (cod_colaborador)
        references colaborador_data (codigo),

    add column if not exists cod_colaborador_feedback bigint,
    add constraint fk_solicitacao_folga_cod_colaborador_feedback foreign key (cod_colaborador_feedback)
        references colaborador_data (codigo);

end transaction;