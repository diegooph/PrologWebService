-- View criada com base nos filtros necessários para usar as informações de mudança de km do engate e desengate
-- na evolução de km.
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