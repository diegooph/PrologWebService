drop index movimentacao_motivo_motivo_uindex;

create unique index movimentacao_motivo_motivo_uindex
    on movimentacao_motivo_movimento (cod_empresa, motivo)
    where (ativo = true);