create temp table if not exists temp_infos_update
as
select distinct on (cosid.codigo) cosid.codigo  as cod_item,
                                  vd.codigo     as cod_veiculo,
                                  cosid.km      as km_errado,
                                  vpkh.km_final as km_correto,
                                  vpkh.codigo   as cod_processo_km_historico
from checklist_ordem_servico_itens_data cosid
         join checklist_ordem_servico_data cosd
              on cosid.cod_unidade = cosd.cod_unidade
                  and cosid.cod_os = cosd.codigo
         join checklist_data cd on cosd.cod_checklist = cd.codigo
         join veiculo_data vd on cd.cod_veiculo = vd.codigo
         join veiculo_processo_km_historico vpkh
              on vd.codigo = vpkh.cod_veiculo
                  and vpkh.cod_processo_veiculo = cosid.cod_agrupamento_resolucao_em_lote
                  and vpkh.tipo_processo_veiculo = 'FECHAMENTO_ITEM_CHECKLIST'
                  and vpkh.motorizado = false
where cosid.cod_agrupamento_resolucao_em_lote is not null
  and vd.motorizado = false
  -- Apenas KMs ainda não editados pelo usuário.
  and cosid.codigo not in
      (select vpak.cod_processo_alterado
       from veiculo_processo_alteracao_km vpak
       where vpak.tipo_processo_alterado = 'FECHAMENTO_ITEM_CHECKLIST')
  -- Apenas KMs que tenham sido fechados com o KM do trator ao invés do KM do reboque.
  and cosid.km = (select vpkh2.km_final
                  from veiculo_processo_km_historico vpkh2
                  where vpkh2.cod_processo_veiculo =
                        cosid.cod_agrupamento_resolucao_em_lote
                    and vpkh2.tipo_processo_veiculo = 'FECHAMENTO_ITEM_CHECKLIST'
                    and vpkh2.motorizado = true);

-- Aqui corrigimos o km no item da O.S.
update checklist_ordem_servico_itens_data
set km = tiu.km_correto
from temp_infos_update tiu
where checklist_ordem_servico_itens_data.codigo = tiu.cod_item;

-- Aqui corrigimos a info de se é ou não fonte do processo, pois como enviava outra placa todos reboques acoplados
-- ficavam como se não fossem fonte do processo.
update veiculo_processo_km_historico
set veiculo_fonte_processo = true
from temp_infos_update tiu
where veiculo_processo_km_historico.codigo = tiu.cod_processo_km_historico;

drop table temp_infos_update;