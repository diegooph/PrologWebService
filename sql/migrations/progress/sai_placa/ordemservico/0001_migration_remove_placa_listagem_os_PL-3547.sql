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

        select c.placa_veiculo :: text                                         as placa_veiculo,
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
                      on v.placa = c.placa_veiculo
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
                      on v.placa = c.placa_veiculo and v.cod_unidade = c.cod_unidade
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