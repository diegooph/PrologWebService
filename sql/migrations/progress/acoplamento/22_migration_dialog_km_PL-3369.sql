drop function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_placa_veiculo text,
    f_prioridade_alternativa text,
    f_status_itens text,
    f_data_hora_atual_utc timestamp with time zone,
    f_limit integer,
    f_offset integer);
create or replace function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
                                                                 f_cod_os bigint,
                                                                 f_placa_veiculo text,
                                                                 f_prioridade_alternativa text,
                                                                 f_status_itens text,
                                                                 f_data_hora_atual_utc timestamp with time zone,
                                                                 f_limit integer,
                                                                 f_offset integer)
    returns table
            (
                cod_veiculo                           bigint,
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_item_os                   bigint,
                cod_item_os                           bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        with dados as (
            select c.cod_veiculo                                                          as cod_veiculo,
                   c.placa_veiculo::text                                                  as placa_veiculo,
                   v.km                                                                   as km_atual_veiculo,
                   cos.codigo                                                             as cod_os,
                   cos.cod_unidade                                                        as cod_unidade_item_os,
                   cosi.codigo                                                            as cod_item_os,
                   c.data_hora at time zone tz_unidade(c.cod_unidade)                     as data_hora_primeiro_apontamento_item,
                   cosi.status_resolucao                                                  as status_item_os,
                   prio.prazo                                                             as prazo_resolucao_item_horas,
                   to_minutes_trunc(
                               (c.data_hora + (prio.prazo || ' HOURS')::interval)
                               -
                               f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
                   cosi.qt_apontamentos                                                   as qtd_apontamentos,
                   co.codigo                                                              as cod_colaborador_resolucao,
                   co.nome::text                                                          as nome_colaborador_resolucao,
                   cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
                   cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
                   cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
                   cosi.feedback_conserto                                                 as feedback_resolucao,
                   millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
                   cosi.km                                                                as km_veiculo_coletado_resolucao,
                   cp.codigo                                                              as cod_pergunta,
                   cp.pergunta                                                            as descricao_pergunta,
                   cap.codigo                                                             as cod_alternativa,
                   cap.alternativa                                                        as descricao_alternativa,
                   cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
                   case
                       when cap.alternativa_tipo_outros
                           then
                           (select crn.resposta_outros
                            from checklist_respostas_nok crn
                            where crn.cod_checklist = c.codigo
                              and crn.cod_alternativa = cap.codigo)::text
                       end                                                                as descricao_tipo_outros,
                   cap.prioridade::text                                                   as prioridade_alternativa,
                   an.url_midia::text                                                     as url_midia,
                   an.cod_checklist::bigint                                               as cod_checklist
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
                     join checklist_perguntas cp
                          on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist_alternativa_prioridade prio
                          on cap.prioridade = prio.prioridade
                     join veiculo v
                          on c.placa_veiculo = v.placa
                     left join colaborador co
                               on co.cpf = cosi.cpf_mecanico
                     left join checklist_ordem_servico_itens_midia im
                               on im.cod_item_os = cosi.codigo
                     left join checklist_respostas_midias_alternativas_nok an
                               on im.cod_midia_nok = an.codigo
            where f_if(f_cod_unidade is null, true, cos.cod_unidade = f_cod_unidade)
              and f_if(f_cod_os is null, true, cos.codigo = f_cod_os)
              and f_if(f_placa_veiculo is null, true, c.placa_veiculo = f_placa_veiculo)
              and f_if(f_prioridade_alternativa is null, true, cap.prioridade = f_prioridade_alternativa)
              and f_if(f_status_itens is null, true, cosi.status_resolucao = f_status_itens)
            limit f_limit
            offset
            f_offset
        ),
             dados_veiculo as (
                 select v.placa::text as placa_veiculo,
                        v.km          as km_atual_veiculo
                 from veiculo v
                 where v.placa = f_placa_veiculo
             )

             -- Nós usamos esse dados_veiculo com f_if pois pode acontecer de não existir dados para os filtros aplicados e
             -- desse modo acabaríamos não retornando placa e km também, mas essas são informações necessárias pois o objeto
             -- construído a partir dessa function usa elas.
        select d.cod_veiculo                                                             as cod_veiculo,
               f_if(d.placa_veiculo is null, dv.placa_veiculo, d.placa_veiculo)          as placa_veiculo,
               f_if(d.km_atual_veiculo is null, dv.km_atual_veiculo, d.km_atual_veiculo) as km_atual_veiculo,
               d.cod_os                                                                  as cod_os,
               d.cod_unidade_item_os                                                     as cod_unidade_item_os,
               d.cod_item_os                                                             as cod_item_os,
               d.data_hora_primeiro_apontamento_item                                     as data_hora_primeiro_apontamento_item,
               d.status_item_os                                                          as status_item_os,
               d.prazo_resolucao_item_horas                                              as prazo_resolucao_item_horas,
               d.prazo_restante_resolucao_item_minutos                                   as prazo_restante_resolucao_item_minutos,
               d.qtd_apontamentos                                                        as qtd_apontamentos,
               d.cod_colaborador_resolucao                                               as cod_colaborador_resolucao,
               d.nome_colaborador_resolucao                                              as nome_colaborador_resolucao,
               d.data_hora_resolucao                                                     as data_hora_resolucao,
               d.data_hora_inicio_resolucao                                              as data_hora_inicio_resolucao,
               d.data_hora_fim_resolucao                                                 as data_hora_fim_resolucao,
               d.feedback_resolucao                                                      as feedback_resolucao,
               d.duracao_resolucao_minutos                                               as duracao_resolucao_minutos,
               d.km_veiculo_coletado_resolucao                                           as km_veiculo_coletado_resolucao,
               d.cod_pergunta                                                            as cod_pergunta,
               d.descricao_pergunta                                                      as descricao_pergunta,
               d.cod_alternativa                                                         as cod_alternativa,
               d.descricao_alternativa                                                   as descricao_alternativa,
               d.alternativa_tipo_outros                                                 as alternativa_tipo_outros,
               d.descricao_tipo_outros                                                   as descricao_tipo_outros,
               d.prioridade_alternativa                                                  as prioridade_alternativa,
               d.url_midia                                                               as url_midia,
               d.cod_checklist                                                           as cod_checklist
        from dados d
                 right join dados_veiculo dv
                            on d.placa_veiculo = dv.placa_veiculo
        order by cod_os, cod_item_os, cod_checklist;
end;
$$;

drop function func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_data_hora_atual_utc timestamp with time zone);
create or replace function func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
                                                                         f_cod_os bigint,
                                                                         f_data_hora_atual_utc timestamp with time zone)
    returns table
            (
                cod_veiculo                           bigint,
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_os                        bigint,
                status_os                             text,
                data_hora_abertura_os                 timestamp without time zone,
                data_hora_fechamento_os               timestamp without time zone,
                cod_item_os                           bigint,
                cod_unidade_item_os                   bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        select c.cod_veiculo                                                          as cod_veiculo,
               c.placa_veiculo::text                                                  as placa_veiculo,
               v.km                                                                   as km_atual_veiculo,
               cos.codigo                                                             as cod_os,
               cos.cod_unidade                                                        as cod_unidade_os,
               cos.status::text                                                       as status_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_abertura_os,
               cos.data_hora_fechamento at time zone tz_unidade(f_cod_unidade)        as data_hora_fechamento_os,
               cosi.codigo                                                            as cod_item_os,
               cos.cod_unidade                                                        as cod_unidade_item_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_primeiro_apontamento_item,
               cosi.status_resolucao                                                  as status_item_os,
               prio.prazo                                                             as prazo_resolucao_item_horas,
               to_minutes_trunc(
                           (c.data_hora + (prio.prazo || ' HOURS')::interval)
                           -
                           f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
               cosi.qt_apontamentos                                                   as qtd_apontamentos,
               co.codigo                                                              as cod_colaborador_resolucao,
               co.nome::text                                                          as nome_colaborador_resolucao,
               cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
               cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
               cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
               cosi.feedback_conserto                                                 as feedback_resolucao,
               millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
               cosi.km                                                                as km_veiculo_coletado_resolucao,
               cp.codigo                                                              as cod_pergunta,
               cp.pergunta                                                            as descricao_pergunta,
               cap.codigo                                                             as cod_alternativa,
               cap.alternativa                                                        as descricao_alternativa,
               cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
               case
                   when cap.alternativa_tipo_outros
                       then
                       (select crn.resposta_outros
                        from checklist_respostas_nok crn
                        where crn.cod_checklist = c.codigo
                          and crn.cod_alternativa = cap.codigo)::text
                   end                                                                as descricao_tipo_outros,
               cap.prioridade::text                                                   as prioridade_alternativa,
               an.url_midia::text                                                     as url_midia,
               an.cod_checklist::bigint                                               as cod_checklist
        from checklist c
                 join checklist_ordem_servico cos
                      on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os
                          and cos.cod_unidade = cosi.cod_unidade
            -- O join com perguntas e alternativas é feito com a tabela _DATA pois OSs de perguntas e alternativas
            -- deletadas ainda devem ser exibidas.
                 join checklist_perguntas_data cp
                      on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                 join checklist_alternativa_pergunta_data cap
                      on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                 join checklist_alternativa_prioridade prio
                      on cap.prioridade = prio.prioridade
                 join veiculo v
                      on c.placa_veiculo = v.placa
                 left join colaborador co
                           on co.cpf = cosi.cpf_mecanico
                 left join checklist_ordem_servico_itens_midia im
                           on im.cod_item_os = cosi.codigo
                 left join checklist_respostas_midias_alternativas_nok an
                           on im.cod_midia_nok = an.codigo
        where cos.codigo = f_cod_os
          and cos.cod_unidade = f_cod_unidade
        order by cosi.codigo;
end;
$$;



-- Corrige a atualização de km no checklist. Não deveria ter a verificação de km menor ou maior.
create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
                                                                 f_tempo_realizacao bigint,
                                                                 f_data_hora_sincronizacao timestamp with time zone,
                                                                 f_fonte_data_hora_realizacao text,
                                                                 f_versao_app_momento_realizacao integer,
                                                                 f_versao_app_momento_sincronizacao integer,
                                                                 f_device_id text,
                                                                 f_device_imei text,
                                                                 f_device_uptime_realizacao_millis bigint,
                                                                 f_device_uptime_sincronizacao_millis bigint,
                                                                 f_foi_offline boolean,
                                                                 f_total_perguntas_ok integer,
                                                                 f_total_perguntas_nok integer,
                                                                 f_total_alternativas_ok integer,
                                                                 f_total_alternativas_nok integer,
                                                                 f_total_midias_perguntas_ok integer,
                                                                 f_total_midias_alternativas_nok integer)
    returns table
            (
                cod_checklist_inserido bigint,
                checklist_ja_existia   boolean
            )
    language plpgsql
as
$$
declare
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo text    := (select vd.placa
                                         from veiculo_data vd
                                         where vd.codigo = f_cod_veiculo);
    v_cod_novo_checklist     bigint;
    v_cod_checklist_inserido bigint;
    v_checklist_ja_existia   boolean := false;
    v_km_final               bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));


    v_km_final :=
            (select *
             from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                               f_cod_veiculo,
                                               f_km_coletado,
                                               v_cod_novo_checklist,
                                               'CHECKLIST',
                                               true,
                                               f_data_hora_realizacao));

    insert into checklist_data(codigo,
                               cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
                               data_hora_sincronizacao,
                               fonte_data_hora_realizacao,
                               versao_app_momento_realizacao,
                               versao_app_momento_sincronizacao,
                               device_id,
                               device_imei,
                               device_uptime_realizacao_millis,
                               device_uptime_sincronizacao_millis,
                               foi_offline,
                               total_perguntas_ok,
                               total_perguntas_nok,
                               total_alternativas_ok,
                               total_alternativas_nok,
                               total_midias_perguntas_ok,
                               total_midias_alternativas_nok,
                               cod_veiculo)
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
            f_observacao,
            f_data_hora_sincronizacao,
            f_fonte_data_hora_realizacao,
            f_versao_app_momento_realizacao,
            f_versao_app_momento_sincronizacao,
            f_device_id,
            f_device_imei,
            f_device_uptime_realizacao_millis,
            f_device_uptime_sincronizacao_millis,
            f_foi_offline,
            f_total_perguntas_ok,
            f_total_perguntas_nok,
            f_total_alternativas_ok,
            f_total_alternativas_nok,
            nullif(f_total_midias_perguntas_ok, 0),
            nullif(f_total_midias_alternativas_nok, 0),
            f_cod_veiculo)
    on conflict on constraint unique_checklist
        do update set data_hora_sincronizacao = f_data_hora_sincronizacao
                      -- https://stackoverflow.com/a/40880200/4744158
    returning codigo, not (checklist_data.xmax = 0) into v_cod_checklist_inserido, v_checklist_ja_existia;

    -- Verificamos se o insert funcionou.
    if v_cod_checklist_inserido <= 0
    then
        raise exception 'Não foi possível inserir o checklist.';
    end if;

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;