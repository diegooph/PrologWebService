create or replace function func_checklist_get_listagem(f_cod_unidades bigint[],
                                                       f_data_inicial date,
                                                       f_data_final date,
                                                       f_cod_colaborador bigint,
                                                       f_cod_tipo_veiculo bigint,
                                                       f_cod_veiculo bigint,
                                                       f_incluir_respostas boolean,
                                                       f_limit integer,
                                                       f_offset bigint)
    returns table
            (
                cod_unidade                               bigint,
                cod_checklist                             bigint,
                cod_checklist_modelo                      bigint,
                cod_versao_checklist_modelo               bigint,
                cod_colaborador                           bigint,
                cpf_colaborador                           bigint,
                nome_colaborador                          text,
                cod_veiculo                               bigint,
                placa_veiculo                             text,
                identificador_frota                       text,
                km_veiculo_momento_realizacao             bigint,
                tipo_checklist                            text,
                data_hora_realizacao_utc                  timestamp with time zone,
                data_hora_realizacao_tz_aplicado          timestamp without time zone,
                data_hora_importado_prolog_utc            timestamp with time zone,
                data_hora_importado_prolog_tz_aplicado    timestamp without time zone,
                duracao_realizacao_millis                 bigint,
                observacao_checklist                      text,
                total_perguntas_ok                        smallint,
                total_perguntas_nok                       smallint,
                total_alternativas_ok                     smallint,
                total_alternativas_nok                    smallint,
                total_midias_perguntas_ok                 smallint,
                total_midias_alternativas_nok             smallint,
                total_alternativas_nok_prioridade_baixa   smallint,
                total_alternativas_nok_prioridade_alta    smallint,
                total_alternativas_nok_prioridade_critica smallint,
                foi_offline                               boolean,
                data_hora_sincronizacao_utc               timestamp with time zone,
                data_hora_sincronizacao_tz_aplicado       timestamp without time zone,
                fonte_data_hora                           text,
                versao_app_momento_realizacao             bigint,
                versao_app_momento_sincronizacao          bigint,
                device_id                                 text,
                device_imei                               text,
                device_uptime_realizacao_millis           bigint,
                device_uptime_sincronizacao_millis        bigint,
                cod_pergunta                              bigint,
                cod_contexto_pergunta                     bigint,
                descricao_pergunta                        text,
                ordem_pergunta                            integer,
                pergunta_single_choice                    boolean,
                anexo_midia_pergunta_ok                   text,
                cod_alternativa                           bigint,
                cod_contexto_alternativa                  bigint,
                descricao_alternativa                     text,
                ordem_alternativa                         integer,
                prioridade_alternativa                    text,
                alternativa_tipo_outros                   boolean,
                deve_abrir_ordem_servico                  boolean,
                anexo_midia_alternativa_nok               text,
                cod_auxiliar_alternativa                  text,
                alternativa_selecionada                   boolean,
                resposta_outros                           text,
                tem_midia_pergunta_ok                     boolean,
                tem_midia_alternativa_nok                 boolean
            )
    language plpgsql
as
$$
begin
    return query
        with checks as (
            select c.*
            from checklist c
            where c.cod_unidade = any (f_cod_unidades)
              and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
              and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
            order by c.data_hora_sincronizacao desc
            limit f_limit offset f_offset
        ),
             checks_resto as --!ver outro nome
                 (select c.*,
                         co.codigo                     as cod_colaborador,
                         co.cod_equipe                 as cod_equipe_colaborador,
                         co.nome :: text               as nome_colaborador,
                         v.placa :: text               as placa,
                         v.identificador_frota :: text as identificador_frota,
                         cp.codigo                     as cod_pergunta,
                         cp.codigo_contexto            as cod_contexto_pergunta,
                         cp.pergunta                   as descricao_pergunta,
                         cp.ordem                      as ordem_pergunta,
                         cp.single_choice              as pergunta_single_choice,
                         cp.anexo_midia_resposta_ok    as anexo_midia_pergunta_ok,
                         cap.codigo                    as cod_alternativa,
                         cap.codigo_contexto           as cod_contexto_alternativa,
                         cap.alternativa               as descricao_alternativa,
                         cap.ordem                     as ordem_alternativa,
                         cap.prioridade ::text         as prioridade_alternativa,
                         cap.alternativa_tipo_outros   as alternativa_tipo_outros,
                         cap.deve_abrir_ordem_servico  as deve_abrir_ordem_servico,
                         cap.anexo_midia               as anexo_midia_alternativa_nok,
                         cap.cod_auxiliar              as cod_auxiliar_alternativa,
                         crn.codigo is not null        as alternativa_selecionada,
                         crn.resposta_outros           as resposta_outros,
                         crmpo.uuid is not null        as tem_midia_pergunta_ok,
                         crman.uuid is not null        as tem_midia_alternativa_nok
                  from checks c
                           left join checklist_perguntas cp on f_incluir_respostas
                      and cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                      and cp.cod_unidade = any (f_cod_unidades)
                           left join checklist_alternativa_pergunta cap on f_incluir_respostas
                      and cap.cod_pergunta = cp.codigo
                           left join checklist_respostas_nok crn on f_incluir_respostas
                      and c.codigo = crn.cod_checklist
                      and crn.cod_alternativa = cap.codigo
                           left join checklist_respostas_midias_perguntas_ok crmpo on f_incluir_respostas
                      and crmpo.cod_checklist = c.codigo
                      and crmpo.cod_pergunta = cp.codigo
                      and crn.codigo is null
                           left join checklist_respostas_midias_alternativas_nok crman on f_incluir_respostas
                      and crman.cod_checklist = c.codigo and crman.cod_alternativa = cap.codigo
                      and crn.codigo is not null
                           join colaborador co
                                on co.cpf = c.cpf_colaborador
                           join veiculo v
                                on v.codigo = c.cod_veiculo
                  where case when f_cod_colaborador is null then true else co.codigo = f_cod_colaborador end
                    and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
                    and case when f_cod_veiculo is null then true else c.cod_veiculo = f_cod_veiculo end)
        select cr.cod_unidade                                            as cod_unidade,
               cr.codigo                                                 as cod_checklist,
               cr.cod_checklist_modelo                                   as cod_checklist_modelo,
               cr.cod_versao_checklist_modelo                            as cod_versao_checklist_modelo,
               cr.cod_colaborador                                        as cod_colaborador,
               cr.cpf_colaborador                                        as cpf_colaborador,
               cr.nome_colaborador                                       as nome_colaborador,
               cr.cod_veiculo                                            as cod_veiculo,
               cr.placa ::text                                           as placa_veiculo,
               cr.identificador_frota                                    as identificador_frota,
               cr.km_veiculo                                             as km_veiculo_momento_realizacao,
               f_if(cr.tipo = 'R', 'RETORNO'::text, 'SAIDA'::text)::text as tipo_checklist,
               cr.data_hora                                              as data_hora_realizacao_utc,
               cr.data_hora_realizacao_tz_aplicado                       as data_hora_realizacao_tz_aplicado,
               cr.data_hora_importado_prolog                             as data_hora_importado_prolog_utc,
               cr.data_hora_importado_prolog at time zone tz_unidade(cr.cod_unidade)
                                                                         as data_hora_importado_prolog_tz_aplicado,
               cr.tempo_realizacao                                       as duracao_realizacao_millis,
               cr.observacao                                             as observacao_checklist,
               cr.total_perguntas_ok                                     as total_perguntas_ok,
               cr.total_perguntas_nok                                    as total_perguntas_nok,
               cr.total_alternativas_ok                                  as total_alternativas_ok,
               cr.total_alternativas_nok                                 as total_alternativas_nok,
               cr.total_midias_perguntas_ok                              as total_midias_perguntas_ok,
               cr.total_midias_alternativas_nok                          as total_midias_alternativas_nok,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = cr.codigo
                  and cap.prioridade = 'BAIXA') :: smallint              as total_alternativas_nok_prioridade_baixa,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = cr.codigo
                  and cap.prioridade = 'ALTA') :: smallint               as total_alternativas_nok_prioridade_alta,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = cr.codigo
                  and cap.prioridade = 'CRITICA') :: smallint            as total_alternativas_nok_prioridade_critica,
               cr.foi_offline                                            as foi_offline,
               cr.data_hora_sincronizacao                                as data_hora_sincronizacao_utc,
               cr.data_hora_sincronizacao at time zone tz_unidade(cr.cod_unidade)
                                                                         as data_hora_sincronizacao_tz_aplicado,
               cr.fonte_data_hora_realizacao                             as fonte_data_hora,
               cr.versao_app_momento_realizacao                          as versao_app_momento_realizacao,
               cr.versao_app_momento_sincronizacao                       as versao_app_momento_sincronizacao,
               cr.device_id                                              as device_id,
               cr.device_imei                                            as device_imei,
               cr.device_uptime_realizacao_millis                        as device_uptime_realizacao_millis,
               cr.device_uptime_sincronizacao_millis                     as device_uptime_sincronizacao_millis,
               cr.cod_pergunta                                           as cod_pergunta,
               cr.cod_contexto_pergunta                                  as cod_contexto_pergunta,
               cr.descricao_pergunta                                     as descricao_pergunta,
               cr.ordem_pergunta                                         as ordem_pergunta,
               cr.pergunta_single_choice                                 as pergunta_single_choice,
               cr.anexo_midia_pergunta_ok                                as anexo_midia_pergunta_ok,
               cr.cod_alternativa                                        as cod_alternativa,
               cr.cod_contexto_alternativa                               as cod_contexto_alternativa,
               cr.descricao_alternativa                                  as descricao_alternativa,
               cr.ordem_alternativa                                      as ordem_alternativa,
               cr.prioridade_alternativa                                 as prioridade_alternativa,
               cr.alternativa_tipo_outros                                as alternativa_tipo_outros,
               cr.deve_abrir_ordem_servico                               as deve_abrir_ordem_servico,
               cr.anexo_midia_alternativa_nok                            as anexo_midia_alternativa_nok,
               cr.cod_auxiliar_alternativa                               as cod_auxiliar_alternativa,
               cr.alternativa_selecionada                                as alternativa_selecionada,
               cr.resposta_outros                                        as resposta_outros,
               cr.tem_midia_pergunta_ok                                  as tem_midia_pergunta_ok,
               cr.tem_midia_alternativa_nok                              as tem_midia_alternativa_nok
        from checks_resto cr
                 join equipe e
                      on e.codigo = cr.cod_equipe_colaborador
        order by cr.data_hora_sincronizacao desc;
end;
$$;