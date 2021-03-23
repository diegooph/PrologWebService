create or replace view afericao as
select ad.codigo,
       ad.data_hora,
       ad.placa_veiculo,
       ad.cod_veiculo,
       ad.cpf_aferidor,
       ad.km_veiculo,
       ad.tempo_realizacao,
       ad.tipo_medicao_coletada,
       ad.cod_unidade,
       ad.tipo_processo_coleta,
       ad.forma_coleta_dados,
       ad.cod_diagrama
from afericao_data ad
where ad.deletado = false;