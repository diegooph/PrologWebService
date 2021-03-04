-- Adiciona cod_veiculo.
drop view checklist;
create or replace view checklist as
select c.cod_unidade                        as cod_unidade,
       c.cod_checklist_modelo               as cod_checklist_modelo,
       c.cod_versao_checklist_modelo        as cod_versao_checklist_modelo,
       c.codigo                             as codigo,
       c.data_hora                          as data_hora,
       c.data_hora_realizacao_tz_aplicado   as data_hora_realizacao_tz_aplicado,
       c.data_hora_importado_prolog         as data_hora_importado_prolog,
       c.cpf_colaborador                    as cpf_colaborador,
       c.placa_veiculo                      as placa_veiculo,
       c.cod_veiculo                        as cod_veiculo,
       c.tipo                               as tipo,
       c.tempo_realizacao                   as tempo_realizacao,
       c.km_veiculo                         as km_veiculo,
       c.observacao                         as observacao,
       c.data_hora_sincronizacao            as data_hora_sincronizacao,
       c.fonte_data_hora_realizacao         as fonte_data_hora_realizacao,
       c.versao_app_momento_realizacao      as versao_app_momento_realizacao,
       c.versao_app_momento_sincronizacao   as versao_app_momento_sincronizacao,
       c.device_id                          as device_id,
       c.device_imei                        as device_imei,
       c.device_uptime_realizacao_millis    as device_uptime_realizacao_millis,
       c.device_uptime_sincronizacao_millis as device_uptime_sincronizacao_millis,
       c.foi_offline                        as foi_offline,
       c.total_perguntas_ok                 as total_perguntas_ok,
       c.total_perguntas_nok                as total_perguntas_nok,
       c.total_alternativas_ok              as total_alternativas_ok,
       c.total_alternativas_nok             as total_alternativas_nok,
       c.total_midias_perguntas_ok          as total_midias_perguntas_ok,
       c.total_midias_alternativas_nok      as total_midias_alternativas_nok
FROM checklist_data c
WHERE c.deletado = false;