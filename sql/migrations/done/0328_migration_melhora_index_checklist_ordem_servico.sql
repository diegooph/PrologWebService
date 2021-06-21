-- O index antigo não incluia o código da ordem de serviço.
drop index idx_checklist_ordem_servico_cod_unidade;
create index concurrently idx_checklist_ordem_servico_cod_unidade
    on checklist_ordem_servico_data (cod_unidade) include (codigo);