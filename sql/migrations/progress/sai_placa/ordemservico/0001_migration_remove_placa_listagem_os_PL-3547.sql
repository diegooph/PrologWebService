drop function if exists func_checklist_os_get_os_listagem(f_cod_unidade bigint,
    f_cod_tipo_veiculo bigint,
    f_placa_veiculo text,
    f_status_os text,
    f_limit integer,
    f_offset integer);
create or replace function func_checklist_os_get_os_listagem(f_cod_unidade bigint,
                                                             f_cod_tipo_veiculo bigint,
                                                             f_cod_veiculo bigint,
                                                             f_status_os text,
                                                             f_limit integer,
                                                             f_offset integer)
    returns table
            (
                placa_veiculo        text,
                cod_os               bigint,
                cod_unidade_os       bigint,
                cod_checklist        bigint,
                data_hora_abertura   timestamp without time zone,
                data_hora_fechamento timestamp without time zone,
                status_os            text,
                qtd_itens_pendentes  integer,
                qtd_itens_resolvidos integer
            )
    language plpgsql
as
$$
declare
    v_status_item_pendente  constant text not null = 'P';
    v_status_item_resolvido constant text not null = 'R';
begin
    return query
        with os as (
            select cos.codigo                                                     as cod_os,
                   cos.cod_unidade                                                as cod_unidade_os,
                   count(cos.codigo)
                   filter (where cosi.status_resolucao = v_status_item_pendente)  as qtd_itens_pendentes,
                   count(cos.codigo)
                   filter (where cosi.status_resolucao = v_status_item_resolvido) as qtd_itens_resolvidos
            from checklist_ordem_servico cos
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
            where cos.cod_unidade = f_cod_unidade
            group by cos.cod_unidade, cos.codigo
        )

        select v.placa :: text                                                 as placa_veiculo,
               cos.codigo                                                      as cod_os,
               cos.cod_unidade                                                 as cod_unidade_os,
               cos.cod_checklist                                               as cod_checklist,
               -- A data/hora do check é a abertura da O.S.
               c.data_hora at time zone tz_unidade(c.cod_unidade)              as data_hora_abertura,
               cos.data_hora_fechamento at time zone tz_unidade(c.cod_unidade) as data_hora_fechamento,
               cos.status :: text                                              as status_os,
               os.qtd_itens_pendentes :: integer                               as qtd_itens_pendentes,
               os.qtd_itens_resolvidos :: integer                              as qtd_itens_resolvidos
        from checklist c
                 join checklist_ordem_servico cos
                      on cos.cod_checklist = c.codigo
                 join os
                      on os.cod_os = cos.codigo
                          and os.cod_unidade_os = cos.cod_unidade
                 join veiculo v
                      on v.codigo = c.cod_veiculo
                 join veiculo_tipo vt
                      on v.cod_tipo = vt.codigo
        where c.cod_unidade = f_cod_unidade
          and case when f_cod_tipo_veiculo is null then true else f_cod_tipo_veiculo = vt.codigo end
          and case when f_cod_veiculo is null then true else f_cod_veiculo = c.cod_veiculo end
          and case when f_status_os is null then true else f_status_os = cos.status end
        order by cos.codigo desc
        limit f_limit offset f_offset;
end;
$$;


drop function if exists func_checklist_os_get_qtd_itens_placa_listagem(f_cod_unidade bigint,
    f_cod_tipo_veiculo bigint,
    f_placa_veiculo text,
    f_status_itens_os text,
    f_limit integer,
    f_offset integer);
create or replace function func_checklist_os_get_qtd_itens_placa_listagem(f_cod_unidade bigint,
                                                                          f_cod_tipo_veiculo bigint,
                                                                          f_cod_veiculo bigint,
                                                                          f_status_itens_os text,
                                                                          f_limit integer,
                                                                          f_offset integer)
    returns table
            (
                placa_veiculo                text,
                qtd_itens_prioridade_critica bigint,
                qtd_itens_prioridade_alta    bigint,
                qtd_itens_prioridade_baixa   bigint,
                total_itens                  bigint
            )
    language plpgsql
as
$$
declare
    tipo_item_prioridade_critica text := 'CRITICA';
    tipo_item_prioridade_alta    text := 'ALTA';
    tipo_item_prioridade_baixa   text := 'BAIXA';
begin
    return query
        select v.placa :: text           as placa_veiculo,
               count(case
                         when cap.prioridade = tipo_item_prioridade_critica
                             then 1 end) as qtd_itens_prioridade_critica,
               count(case
                         when cap.prioridade = tipo_item_prioridade_alta
                             then 1 end) as qtd_itens_prioridade_alta,
               count(case
                         when cap.prioridade = tipo_item_prioridade_baixa
                             then 1 end) as qtd_itens_prioridade_baixa,
               count(cap.prioridade)     as total_itens
        from veiculo v
                 join checklist c
            -- Queremos apenas veículos da unidade onde o checklist foi feito.
            -- Isso evita de trazer itens de O.S. de outra empresa em caso de transferência de veículos.
                      on v.codigo = c.cod_veiculo and v.cod_unidade = c.cod_unidade
                 join checklist_ordem_servico cos
                      on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os
                          and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join veiculo_tipo vt
                      on v.cod_tipo = vt.codigo
        where v.cod_unidade = f_cod_unidade
          and case when f_cod_tipo_veiculo is null then true else vt.codigo = f_cod_tipo_veiculo end
          and case when f_cod_veiculo is null then true else v.codigo = f_cod_veiculo end
          and case when f_status_itens_os is null then true else cosi.status_resolucao = f_status_itens_os end
        group by v.placa
        order by qtd_itens_prioridade_critica desc,
                 qtd_itens_prioridade_alta desc,
                 qtd_itens_prioridade_baixa desc,
                 placa_veiculo
        limit f_limit offset f_offset;
end;
$$;


drop function if exists func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_placa_veiculo text,
    f_prioridade_alternativa text,
    f_status_itens text,
    f_data_hora_atual_utc timestamp with time zone,
    f_limit integer,
    f_offset integer);
create or replace function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
                                                                 f_cod_os bigint,
                                                                 f_cod_veiculo bigint,
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
                   v.placa::text                                                          as placa_veiculo,
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
                          on c.cod_veiculo = v.codigo
                     left join colaborador co
                               on co.cpf = cosi.cpf_mecanico
                     left join checklist_ordem_servico_itens_midia im
                               on im.cod_item_os = cosi.codigo
                     left join checklist_respostas_midias_alternativas_nok an
                               on im.cod_midia_nok = an.codigo
            where f_if(f_cod_unidade is null, true, cos.cod_unidade = f_cod_unidade)
              and f_if(f_cod_os is null, true, cos.codigo = f_cod_os)
              and f_if(f_cod_veiculo is null, true, c.cod_veiculo = f_cod_veiculo)
              and f_if(f_prioridade_alternativa is null, true, cap.prioridade = f_prioridade_alternativa)
              and f_if(f_status_itens is null, true, cosi.status_resolucao = f_status_itens)
            limit f_limit offset f_offset
        ),
             dados_veiculo as (
                 select v.placa::text as placa_veiculo,
                        v.km          as km_atual_veiculo
                 from veiculo v
                 where v.codigo = f_cod_veiculo
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
               d.data_hora_fim_resolucao       as data_hora_fim_resolucao,
               d.feedback_resolucao            as feedback_resolucao,
               d.duracao_resolucao_minutos     as duracao_resolucao_minutos,
               d.km_veiculo_coletado_resolucao as km_veiculo_coletado_resolucao,
               d.cod_pergunta                  as cod_pergunta,
               d.descricao_pergunta            as descricao_pergunta,
               d.cod_alternativa               as cod_alternativa,
               d.descricao_alternativa         as descricao_alternativa,
               d.alternativa_tipo_outros       as alternativa_tipo_outros,
               d.descricao_tipo_outros         as descricao_tipo_outros,
               d.prioridade_alternativa        as prioridade_alternativa,
               d.url_midia                     as url_midia,
               d.cod_checklist                 as cod_checklist
        from dados d
                 right join dados_veiculo dv
                            on d.placa_veiculo = dv.placa_veiculo
        order by cod_os, cod_item_os, cod_checklist;
end;
$$;

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
               v.placa::text                                                          as placa_veiculo,
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
                      on c.cod_veiculo = v.codigo
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

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- Ao setupar o BD local e tentar inserir um checklist, esta function estourou...
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

-- Essa function apresentou erro ao inserir um checklist.
drop function func_checklist_os_alternativas_abertura_os(f_cod_modelo_checklist bigint,
    f_cod_versao_modelo_checklist bigint,
    f_placa_veiculo text);
create or replace function func_checklist_os_alternativas_abertura_os(f_cod_modelo_checklist bigint,
                                                                      f_cod_versao_modelo_checklist bigint,
                                                                      f_cod_veiculo bigint)
    returns table
            (
                cod_alternativa                    bigint,
                cod_contexto_pergunta              bigint,
                cod_contexto_alternativa           bigint,
                cod_item_ordem_servico             bigint,
                resposta_tipo_outros_abertura_item text,
                tem_item_os_pendente               boolean,
                deve_abrir_ordem_servico           boolean,
                alternativa_tipo_outros            boolean,
                qtd_apontamentos_item              integer,
                prioridade_alternativa             text
            )
    language plpgsql
as
$$
declare
    status_item_pendente text = 'P';
begin
    return query
        -- Nessa CTE nós não usamos as tabelas com _DATA pois não queremos incrementar quantidade de itens de OS
        -- deletados.
        with itens_pendentes as (
            select cosi.codigo                               as cod_item_ordem_servico,
                   cosi.cod_alternativa_primeiro_apontamento as cod_alternativa_primeiro_apontamento,
                   cosi.cod_contexto_alternativa             as cod_contexto_alternativa,
                   cosi.qt_apontamentos                      as qtd_apontamentos_item,
                   cos.cod_checklist                         as cod_checklist,
                   c.cod_checklist_modelo                    as cod_checklist_modelo
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
            where c.cod_veiculo = f_cod_veiculo
              and c.cod_checklist_modelo = f_cod_modelo_checklist
              and cosi.status_resolucao = status_item_pendente
        )
        select cap.codigo                                          as cod_alternativa,
               cp.codigo_contexto                                  as cod_contexto_pergunta,
               cap.codigo_contexto                                 as cod_contexto_alternativa,
               ip.cod_item_ordem_servico                           as cod_item_ordem_servico,
               crn.resposta_outros                                 as resposta_tipo_outros_abertura_item,
               f_if(ip.cod_item_ordem_servico isnull, false, true) as tem_item_os_pendente,
               cap.deve_abrir_ordem_servico                        as deve_abrir_ordem_servico,
               cap.alternativa_tipo_outros                         as alternativa_tipo_outros,
               ip.qtd_apontamentos_item                            as qtd_apontamentos_item,
               cap.prioridade::text                                as prioridade_alternativa
               -- Nesse SELECT é utilizado a _DATA, pois um item pode estar pendente e sua alternativa deletada, nesse caso
               -- a alternativa ainda deve retornar para não quebrar o fluxo de processamento do checklist realizado.
        from checklist_alternativa_pergunta_data cap
                 join checklist_perguntas_data cp
                      on cap.cod_pergunta = cp.codigo
                 left join itens_pendentes ip
                           on ip.cod_contexto_alternativa = cap.codigo_contexto
                 left join checklist_respostas_nok crn
                           on crn.cod_alternativa = ip.cod_alternativa_primeiro_apontamento
                               and crn.cod_checklist = ip.cod_checklist
        where cap.cod_versao_checklist_modelo = f_cod_versao_modelo_checklist;
end ;
$$;


create or replace function func_checklist_get_farol_checklist(f_cod_unidade bigint,
                                                              f_data_inicial date,
                                                              f_data_final date,
                                                              f_itens_criticos_retroativos boolean)
    returns table
            (
                data                               date,
                placa                              text,
                cod_checklist_saida                bigint,
                data_hora_ultimo_checklist_saida   timestamp without time zone,
                cod_checklist_modelo_saida         bigint,
                nome_colaborador_checklist_saida   text,
                cod_checklist_retorno              bigint,
                data_hora_ultimo_checklist_retorno timestamp without time zone,
                cod_checklist_modelo_retorno       bigint,
                nome_colaborador_checklist_retorno text,
                codigo_pergunta                    bigint,
                descricao_pergunta                 text,
                descricao_alternativa              text,
                alternativa_tipo_outros            boolean,
                descricao_alternativa_tipo_outros  text,
                codigo_item_critico                bigint,
                data_hora_apontamento_item_critico timestamp without time zone
            )
    language plpgsql
as
$$
declare
    -- Algumas empresas não fecham OS, o que acarreta em um grande número de itens críticos para cada placa. A função
    -- do Farol é alertar sobre a existência de pendências, não listar um a um, assim limitamos a busca a apenas cinco
    -- itens críticos por placa e garantimos o desempenho da query.
    v_qtd_maxima_itens_criticos constant bigint not null := 5;
begin
    -- Aumenta memória disponível da function para evitar trabalho em disco.
    set local work_mem = '8MB';

    return query
        -- A utilização de uma CTE para filtrar os checklists parece ser desnecessária, porém mostrou uma melhora
        -- significativa no desempenho da query no geral.
        with checks_filtrados as (
            select c.codigo                           as cod_checklist,
                   c.cod_checklist_modelo             as cod_checklist_modelo,
                   c.cod_veiculo                      as cod_veiculo,
                   c.cpf_colaborador                  as cpf_colaborador,
                   c.data_hora_realizacao_tz_aplicado as data_hora_realizacao_tz_aplicado,
                   c.tipo                             as tipo_checklist,
                   co.nome                            as nome_colaborador
            from checklist c
                     join colaborador co on co.cpf = c.cpf_colaborador
            where c.cod_unidade = f_cod_unidade
              and c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final
        ),
             ultimos_checklists_veiculos as (
                 select checks_placas_dias.data                  as data,
                        checks_placas_dias.cod_veiculo           as cod_veiculo,
                        checks_placas_dias.placa                 as placa,
                        checks_placas_dias.cod_checklist_saida   as cod_checklist_saida,
                        cfs.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_saida,
                        cfs.cod_checklist_modelo                 as cod_checklist_modelo_saida,
                        cfs.nome_colaborador                     as nome_colaborador_checklist_saida,
                        checks_placas_dias.cod_checklist_retorno as cod_checklist_retorno,
                        cfr.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_retorno,
                        cfr.cod_checklist_modelo                 as cod_checklist_modelo_retorno,
                        cfr.nome_colaborador                     as nome_colaborador_checklist_retorno
                 from (select g.day::date                                                      as data,
                              v.codigo                                                         as cod_veiculo,
                              v.placa                                                          as placa,
                              max(case when cf.tipo_checklist = 'S' then cf.cod_checklist end) as cod_checklist_saida,
                              max(case when cf.tipo_checklist = 'R' then cf.cod_checklist end) as cod_checklist_retorno
                       from veiculo v
                                cross join generate_series(f_data_inicial, f_data_final, '1 DAY') g(day)
                                left join checks_filtrados cf
                                          on cf.cod_veiculo = v.codigo and
                                             g.day::date = (cf.data_hora_realizacao_tz_aplicado)::date
                       where v.cod_unidade = f_cod_unidade
                         and v.status_ativo = true
                       group by data, v.codigo, v.placa) as checks_placas_dias
                          left join checks_filtrados cfs on cfs.cod_checklist = checks_placas_dias.cod_checklist_saida
                          left join checks_filtrados cfr on cfr.cod_checklist = checks_placas_dias.cod_checklist_retorno
             ),
             itens_criticos_filtrados as (
                 select row_number() over (partition by c.cod_veiculo) as row_id,
                        c.codigo                                       as cod_checklist_item,
                        c.cod_veiculo                                  as cod_veiculo_checklist_item,
                        cap.cod_pergunta                               as cod_pergunta,
                        cap.codigo                                     as cod_alternativa,
                        cap.alternativa                                as descricao_alternativa,
                        cap.alternativa_tipo_outros                    as alternativa_tipo_outros,
                        cosi.codigo                                    as cod_item_os,
                        c.data_hora_realizacao_tz_aplicado             as data_hora_abertura_os
                 from checklist_ordem_servico_itens cosi
                          join checklist_ordem_servico cos
                               on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                          join checklist c on cos.cod_checklist = c.codigo
                          join checklist_alternativa_pergunta cap
                               on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 where cosi.cod_unidade = f_cod_unidade
                   and cosi.status_resolucao = 'P'
                   and cap.prioridade = 'CRITICA'
                   -- Buscar itens retroativos é uma decisão de, respeitar ou não a data de apontamento do item.
                   -- Fazemos isso utilizando a data de realização do checklist.
                   and case
                           when f_itens_criticos_retroativos then true
                           else (c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final) end
                   -- Ordenamento para garantirmos a exibição dos itens mais recentes.
                 order by cosi.codigo desc
             ),
             itens_criticos_placa as (
                 select icf.cod_checklist_item         as cod_checklist_item,
                        icf.cod_veiculo_checklist_item as cod_veiculo_checklist_item,
                        cp.codigo                      as cod_pergunta,
                        cp.pergunta                    as descricao_pergunta,
                        icf.cod_alternativa            as cod_alternativa,
                        icf.descricao_alternativa      as descricao_alternativa,
                        icf.alternativa_tipo_outros    as alternativa_tipo_outros,
                        crn.resposta_outros            as descricao_alternativa_tipo_outros,
                        icf.cod_item_os                as cod_item_os,
                        icf.data_hora_abertura_os      as data_hora_abertura_os
                 from itens_criticos_filtrados icf
                          join checklist_perguntas cp
                               on cp.codigo = icf.cod_pergunta
                          join checklist_respostas_nok crn
                               on crn.cod_checklist = icf.cod_checklist_item
                                   and crn.cod_alternativa = icf.cod_alternativa
                 where icf.row_id <= v_qtd_maxima_itens_criticos
             )

        select ucv.data,
               ucv.placa::text,
               ucv.cod_checklist_saida,
               ucv.data_hora_ultimo_checklist_saida,
               ucv.cod_checklist_modelo_saida,
               ucv.nome_colaborador_checklist_saida::text,
               ucv.cod_checklist_retorno,
               ucv.data_hora_ultimo_checklist_retorno,
               ucv.cod_checklist_modelo_retorno,
               ucv.nome_colaborador_checklist_retorno::text,
               -- informações dos itens críticos, não necessáriamente relacionados ao checklist
               icp.cod_pergunta,
               icp.descricao_pergunta::text,
               icp.descricao_alternativa::text,
               icp.alternativa_tipo_outros,
               icp.descricao_alternativa_tipo_outros::text,
               icp.cod_item_os,
               icp.data_hora_abertura_os
        from ultimos_checklists_veiculos ucv
                 -- O join deve ocorrer pelo código do checklist ou pelo código do veículo, mas sem duplicar.
                 left join itens_criticos_placa icp
                           on case
                                  when icp.cod_checklist_item in (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                      then icp.cod_checklist_item in
                                           (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                  else icp.cod_veiculo_checklist_item = ucv.cod_veiculo end
        order by ucv.data, ucv.cod_veiculo, icp.cod_item_os;
end;
$$;