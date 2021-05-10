update checklist_ordem_servico_itens_data
set km = vpkh.km_final
from veiculo_processo_km_historico vpkh
where checklist_ordem_servico_itens_data.cod_agrupamento_resolucao_em_lote = vpkh.cod_processo_veiculo
  and vpkh.tipo_processo_veiculo = 'FECHAMENTO_ITEM_CHECKLIST';

update veiculo_processo_km_historico
set veiculo_fonte_processo = (cd.cod_veiculo = veiculo_processo_km_historico.cod_veiculo)
from checklist_ordem_servico_itens_data cosid
         inner join checklist_ordem_servico_data cosd
                    on cosid.cod_os = cosd.codigo and cosid.cod_unidade = cosd.cod_unidade
         inner join checklist_data cd on cosd.cod_checklist = cd.codigo
where cosid.cod_agrupamento_resolucao_em_lote = veiculo_processo_km_historico.cod_processo_veiculo;