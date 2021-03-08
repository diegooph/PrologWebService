-- PL-2173.
drop table public.meta;
drop table public.pdv;
drop table public.pneu_ordem_nomenclatura_antiga;
drop table public.pneu_ordem_nomenclatura_unidade_backup;
drop table public.veiculo_backup;
drop table public.checklist_modelo_veiculo_tipo_backup;
drop table public.afericao_configuracao_tipo_afericao_veiculo_backup;
drop table public.veiculo_tipo_backup;

drop table log.old_log_requisicao;

drop schema avilan cascade;
drop schema migration_checklist cascade;