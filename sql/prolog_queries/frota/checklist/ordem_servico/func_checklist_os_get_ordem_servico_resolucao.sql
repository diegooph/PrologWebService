-- Sobre:
--
-- Function utilizada para buscar uma Ordem de Serviço para ser fechada.
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