create or replace view estratificacao_os as
select cos.codigo                                                       as cod_os,
       realizador.nome                                                  as nome_realizador_checklist,
       v.placa                                                          as placa_veiculo,
       c.km_veiculo                                                     as km,
       c.data_hora_realizacao_tz_aplicado                               as data_hora,
       c.tipo                                                           as tipo_checklist,
       cp.codigo                                                        as cod_pergunta,
       cp.codigo_contexto                                               as cod_contexto_pergunta,
       cp.ordem                                                         as ordem_pergunta,
       cp.pergunta,
       cp.single_choice,
       null :: unknown                                                  as url_imagem,
       cap.prioridade,
       case cap.prioridade
           when 'CRITICA' :: text
               then 1
           when 'ALTA' :: text
               then 2
           when 'BAIXA' :: text
               then 3
           else null :: integer
           end                                                          as prioridade_ordem,
       cap.codigo                                                       as cod_alternativa,
       cap.codigo_contexto                                              as cod_contexto_alternativa,
       cap.alternativa,
       prio.prazo,
       crn.resposta_outros,
       v.cod_tipo,
       cos.cod_unidade,
       cos.status                                                       as status_os,
       cos.cod_checklist,
       tz_unidade(cos.cod_unidade)                                      as time_zone_unidade,
       cosi.status_resolucao                                            as status_item,
       mecanico.nome                                                    as nome_mecanico,
       cosi.cpf_mecanico,
       cosi.tempo_realizacao,
       cosi.data_hora_conserto at time zone tz_unidade(cos.cod_unidade) as data_hora_conserto,
       cosi.data_hora_inicio_resolucao                                  as data_hora_inicio_resolucao_utc,
       cosi.data_hora_fim_resolucao                                     as data_hora_fim_resolucao_utc,
       cosi.km                                                          as km_fechamento,
       cosi.qt_apontamentos,
       cosi.feedback_conserto,
       cosi.codigo
from checklist_data c
         join colaborador realizador
              on realizador.cpf = c.cpf_colaborador
         join veiculo v
              on v.codigo = c.cod_veiculo
         join checklist_ordem_servico cos
              on c.codigo = cos.cod_checklist
         join checklist_ordem_servico_itens cosi
              on cos.codigo = cosi.cod_os
                  and cos.cod_unidade = cosi.cod_unidade
         join checklist_perguntas cp
              on cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                  and cosi.cod_contexto_pergunta = cp.codigo_contexto
         join checklist_alternativa_pergunta cap
              on cap.cod_pergunta = cp.codigo
                  and cosi.cod_contexto_alternativa = cap.codigo_contexto
         join checklist_alternativa_prioridade prio
              on prio.prioridade :: text = cap.prioridade :: text
         join checklist_respostas_nok crn
              on crn.cod_checklist = c.codigo
                  and crn.cod_alternativa = cap.codigo
         left join colaborador mecanico on mecanico.cpf = cosi.cpf_mecanico;

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
from checklist_data c
where c.deletado = false;

alter table checklist_data
    drop column placa_veiculo;

alter table checklist_data
    add constraint fk_checklist_cod_veiculo
        foreign key (cod_veiculo) references veiculo_data (codigo);

alter table checklist_data
    add constraint unique_checklist unique
        (cod_unidade, cod_checklist_modelo, data_hora, cpf_colaborador, cod_veiculo, tipo,
         tempo_realizacao, km_veiculo, fonte_data_hora_realizacao, versao_app_momento_realizacao,
         device_id, device_imei, device_uptime_realizacao_millis);
