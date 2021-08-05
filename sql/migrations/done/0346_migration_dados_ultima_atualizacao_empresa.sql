alter table empresa
    add column data_hora_ultima_atualizacao timestamp with time zone,
    add column responsavel_ultima_atualizacao text;