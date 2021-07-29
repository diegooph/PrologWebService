create or replace function func_get_checklist_work_order(f_branches_id bigint[],
                                                         f_vehicle_type_id bigint,
                                                         f_vehicle_id bigint,
                                                         f_work_order_status text,
                                                         f_include_work_order_items boolean,
                                                         f_limit integer,
                                                         f_offset integer)
    returns table
            (
                work_order_id_prolog   bigint,
                work_order_id          bigint,
                branch_id              bigint,
                checklist_id           bigint,
                opned_at_utc           timestamp with time zone,
                opned_at_with_tz       timestamp without time zone,
                work_order_status      text,
                closed_at_utc          timestamp with time zone,
                closed_at_with_tz      timestamp without time zone,
                opned_by_user_id       bigint,
                opned_by_user_cpf      text,
                opned_by_user_name     text,
                vehicle_id             bigint,
                vehicle_plate          varchar(7),
                fleet_id               text,
                work_order_item_id     bigint,
                closed_by_user_id      bigint,
                closed_by_user_cpf     bigint,
                closed_by_user_name    text,
                question_id            bigint,
                question_context_id    bigint,
                option_id              bigint,
                option_context_id      bigint,
                work_order_item_status text,
                amount_times_pointed   int,
                vehicle_km             bigint,
                batch_group_id         bigint,
                resolved_at_utc        timestamp with time zone,
                resolved_at_with_tz    timestamp without time zone,
                started_at_utc         timestamp with time zone,
                started_at_with_tz     timestamp without time zone,
                ended_at_utc           timestamp with time zone,
                ended_at_with_tz       timestamp without time zone,
                resolution_time        bigint,
                resolution_notes       text,
                option_additional_id   text
            )
    language sql
as
$$
with filtered_work_orders as (
    select cos.codigo                         as work_order_id,
           cos.cod_unidade                    as branch_id,
           cos.status                         as work_order_status,
           cos.data_hora_fechamento           as closed_at,
           cos.codigo_prolog                  as work_order_id_prolog,
           c.codigo                           as checklist_id,
           c.data_hora                        as checklist_date_time,
           c.data_hora_realizacao_tz_aplicado as checklist_date_time_with_tz,
           c.cpf_colaborador                  as checklist_user_cpf,
           v.codigo                           as vehicle_id,
           v.placa                            as vehicle_plate,
           v.identificador_frota              as fleet_id
    from checklist_ordem_servico cos
             join checklist c on cos.cod_checklist = c.codigo
             join veiculo v on v.codigo = c.cod_veiculo
    where cos.cod_unidade = any (f_branches_id)
      and case when f_work_order_status is null then true else cos.status = f_work_order_status end
      and case when f_vehicle_type_id is null then true else v.cod_tipo = f_vehicle_type_id end
      and case when f_vehicle_id is null then true else c.cod_veiculo = f_vehicle_id end
    order by cos.codigo
    limit f_limit offset f_offset
)
select fwo.work_order_id_prolog                                                as work_order_id_prolog,
       fwo.work_order_id                                                       as work_order_id,
       fwo.branch_id                                                           as branch_id,
       fwo.checklist_id                                                        as checklist_id,
       fwo.checklist_date_time                                                 as opned_at_utc,
       fwo.checklist_date_time_with_tz                                         as opned_at_with_tz,
       fwo.work_order_status                                                   as work_order_status,
       fwo.closed_at                                                           as closed_at_utc,
       fwo.closed_at at time zone tz_unidade(c.cod_unidade)                    as closed_at_with_tz,
       c.codigo                                                                as opned_by_user_id,
       lpad(c.cpf::text, 11, '0')                                              as opned_by_user_cpf,
       c.nome                                                                  as opned_by_user_name,
       fwo.vehicle_id                                                          as vehicle_id,
       fwo.vehicle_plate                                                       as vehicle_plate,
       fwo.fleet_id                                                            as fleet_id,
       cosi.codigo                                                             as work_order_item_id,
       cm.codigo                                                               as closed_by_user_id,
       cosi.cpf_mecanico                                                       as closed_by_user_cpf,
       cm.nome                                                                 as closed_by_user_name,
       cosi.cod_pergunta_primeiro_apontamento                                  as question_id,
       cosi.cod_contexto_pergunta                                              as question_context_id,
       cosi.cod_alternativa_primeiro_apontamento                               as option_id,
       cosi.cod_contexto_alternativa                                           as option_context_id,
       cosi.status_resolucao                                                   as work_order_item_status,
       cosi.qt_apontamentos                                                    as amount_times_pointed,
       cosi.km                                                                 as vehicle_km,
       cosi.cod_agrupamento_resolucao_em_lote                                  as batch_group_id,
       cosi.data_hora_conserto                                                 as resolved_at_utc,
       cosi.data_hora_conserto at time zone tz_unidade(cm.cod_unidade)         as resolved_at_with_tz,
       cosi.data_hora_inicio_resolucao                                         as started_at_utc,
       cosi.data_hora_inicio_resolucao at time zone tz_unidade(cm.cod_unidade) as started_at_with_tz,
       cosi.data_hora_fim_resolucao                                            as ended_at_utc,
       cosi.data_hora_fim_resolucao at time zone tz_unidade(cm.cod_unidade)    as ended_at_with_tz,
       cosi.tempo_realizacao                                                   as resolution_time,
       cosi.feedback_conserto                                                  as resolution_notes,
       cap.cod_auxiliar                                                        as option_additional_id
from filtered_work_orders fwo
         join colaborador c on c.cpf = fwo.checklist_user_cpf
         left join checklist_ordem_servico_itens cosi
                   on fwo.branch_id = cosi.cod_unidade and fwo.work_order_id = cosi.cod_os
                       and f_include_work_order_items
         left join checklist_alternativa_pergunta cap on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
         left join colaborador cm on cm.cpf = cosi.cpf_mecanico
order by fwo.work_order_id, cosi.codigo;
$$;