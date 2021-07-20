-- Foi adicionado o 'security definer'.
create or replace function tg_func_checklist_update_versao_dados_offline_empresa_liberada()
    returns trigger as
$TG_FUNC_UPDATE_VERSAO_DADOS_CHECKLIST_OFFLINE_EMPRESA_LIBERADA$
begin
    update checklist_offline_dados_unidade
    set versao_dados = versao_dados + 1
    where cod_unidade in (select cod_unidade from unidade where cod_empresa = old.cod_empresa);
    return new;
end;
$TG_FUNC_UPDATE_VERSAO_DADOS_CHECKLIST_OFFLINE_EMPRESA_LIBERADA$
    security definer
    language plpgsql;