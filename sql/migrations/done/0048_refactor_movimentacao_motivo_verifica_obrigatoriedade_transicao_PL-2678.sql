CREATE EXTENSION btree_gist;

ALTER TABLE movimentacao_motivo_movimento_transicao
    ADD CONSTRAINT exclude_movimentacao_motivo_unidade_obrigatoriedade EXCLUDE USING gist(cod_unidade with =, origem with =, destino with =, cast(obrigatorio as INT) with <>);

comment on constraint exclude_movimentacao_motivo_unidade_obrigatoriedade on movimentacao_motivo_movimento_transicao
is 'Essa constraint impede um motivo para uma mesma unidade, origem e destino com a obrigatoriedade diferente doss que já existem.'