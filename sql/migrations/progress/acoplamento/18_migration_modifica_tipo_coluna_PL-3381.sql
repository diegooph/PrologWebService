drop view if exists veiculo_km_propagacao;

alter table if exists veiculo_processo_km_historico
    alter column tipo_processo_veiculo type types.veiculo_processo_type
        using tipo_processo_veiculo::types.veiculo_processo_type;

create or replace view veiculo_km_propagacao as
select codigo,
       cod_veiculo,
       cod_processo_acoplamento,
       cod_historico_processo_acoplamento,
       cod_processo_veiculo,
       tipo_processo_veiculo,
       cod_unidade,
       veiculo_fonte_processo,
       motorizado,
       km_antigo,
       km_final,
       km_coletado_processo,
       data_hora_processo
from veiculo_processo_km_historico
where km_antigo <> km_final
  and veiculo_fonte_processo = false;
