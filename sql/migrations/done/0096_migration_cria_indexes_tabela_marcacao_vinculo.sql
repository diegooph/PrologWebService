create index if not exists idx_marcacao_vinculo_inicio_fim_cod_marcacao_inicio
    on marcacao_vinculo_inicio_fim (cod_marcacao_inicio);
create index if not exists idx_marcacao_vinculo_inicio_fim_cod_marcacao_fim
    on marcacao_vinculo_inicio_fim (cod_marcacao_fim);