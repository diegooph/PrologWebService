create or replace function func_checklist_get_listagem(f_cod_unidades bigint[],
                                                       f_data_inicial date,
                                                       f_data_final date,
                                                       f_cod_colaborador bigint,
                                                       f_cod_veiculo bigint,
                                                       f_cod_tipo_veiculo bigint,
                                                       f_incluir_respostas boolean,
                                                       f_limit integer,
                                                       f_offset integer)
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
                resposta_outros text
            )
    language plpgsql
as
$$
begin
    return query
        with checklists_filtrados as (
            select c.*
            from checklist c
            where c.cod_unidade = any (f_cod_unidades)
              and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
              and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
            order by c.data_hora_sincronizacao desc
            limit f_limit offset f_offset
        ),
             dados_checklists as
                 (select cf.*,
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
                         crn.resposta_outros           as resposta_outros
                  from checklists_filtrados cf
                           left join checklist_perguntas cp
                                     on f_incluir_respostas
                                         and cp.cod_versao_checklist_modelo = cf.cod_versao_checklist_modelo
                                         and cp.cod_unidade = any (f_cod_unidades)
                           left join checklist_alternativa_pergunta cap
                                     on f_incluir_respostas and cap.cod_pergunta = cp.codigo
                           left join checklist_respostas_nok crn
                                     on f_incluir_respostas and cf.codigo = crn.cod_checklist
                                         and crn.cod_alternativa = cap.codigo
                           join colaborador co
                                on co.cpf = cf.cpf_colaborador
                           join veiculo v
                                on v.codigo = cf.cod_veiculo
                  where case when f_cod_colaborador is null then true else co.codigo = f_cod_colaborador end
                    and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
                    and case when f_cod_veiculo is null then true else cf.cod_veiculo = f_cod_veiculo end)
        select dc.cod_unidade                                            as cod_unidade,
               dc.codigo                                                 as cod_checklist,
               dc.cod_checklist_modelo                                   as cod_checklist_modelo,
               dc.cod_versao_checklist_modelo                            as cod_versao_checklist_modelo,
               dc.cod_colaborador                                        as cod_colaborador,
               dc.cpf_colaborador                                        as cpf_colaborador,
               dc.nome_colaborador                                       as nome_colaborador,
               dc.cod_veiculo                                            as cod_veiculo,
               dc.placa ::text                                           as placa_veiculo,
               dc.identificador_frota                                    as identificador_frota,
               dc.km_veiculo                                             as km_veiculo_momento_realizacao,
               f_if(dc.tipo = 'R', 'RETORNO'::text, 'SAIDA'::text)::text as tipo_checklist,
               dc.data_hora                                              as data_hora_realizacao_utc,
               dc.data_hora_realizacao_tz_aplicado                       as data_hora_realizacao_tz_aplicado,
               dc.data_hora_importado_prolog                             as data_hora_importado_prolog_utc,
               dc.data_hora_importado_prolog at time zone tz_unidade(dc.cod_unidade)
                                                                         as data_hora_importado_prolog_tz_aplicado,
               dc.tempo_realizacao                                       as duracao_realizacao_millis,
               dc.observacao                                             as observacao_checklist,
               dc.total_perguntas_ok                                     as total_perguntas_ok,
               dc.total_perguntas_nok                                    as total_perguntas_nok,
               dc.total_alternativas_ok                                  as total_alternativas_ok,
               dc.total_alternativas_nok                                 as total_alternativas_nok,
               f_if(dc.total_midias_perguntas_ok is null, 0,
                    dc.total_midias_perguntas_ok)                        as total_midias_perguntas_ok,
               f_if(dc.total_midias_alternativas_nok is null, 0,
                    dc.total_midias_alternativas_nok)                    as total_midias_alternativas_nok,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = dc.codigo
                  and cap.prioridade = 'BAIXA') :: smallint              as total_alternativas_nok_prioridade_baixa,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = dc.codigo
                  and cap.prioridade = 'ALTA') :: smallint               as total_alternativas_nok_prioridade_alta,
               (select count(*)
                from checklist_respostas_nok crn
                         join checklist_alternativa_pergunta cap
                              on crn.cod_alternativa = cap.codigo
                where crn.cod_checklist = dc.codigo
                  and cap.prioridade = 'CRITICA') :: smallint            as total_alternativas_nok_prioridade_critica,
               dc.foi_offline                                            as foi_offline,
               dc.data_hora_sincronizacao                                as data_hora_sincronizacao_utc,
               dc.data_hora_sincronizacao at time zone tz_unidade(dc.cod_unidade)
                                                                         as data_hora_sincronizacao_tz_aplicado,
               dc.fonte_data_hora_realizacao                             as fonte_data_hora,
               dc.versao_app_momento_realizacao                          as versao_app_momento_realizacao,
               dc.versao_app_momento_sincronizacao                       as versao_app_momento_sincronizacao,
               dc.device_id                                              as device_id,
               dc.device_imei                                            as device_imei,
               dc.device_uptime_realizacao_millis                        as device_uptime_realizacao_millis,
               dc.device_uptime_sincronizacao_millis                     as device_uptime_sincronizacao_millis,
               dc.cod_pergunta                                           as cod_pergunta,
               dc.cod_contexto_pergunta                                  as cod_contexto_pergunta,
               dc.descricao_pergunta                                     as descricao_pergunta,
               dc.ordem_pergunta                                         as ordem_pergunta,
               dc.pergunta_single_choice                                 as pergunta_single_choice,
               dc.anexo_midia_pergunta_ok                                as anexo_midia_pergunta_ok,
               dc.cod_alternativa                                        as cod_alternativa,
               dc.cod_contexto_alternativa                               as cod_contexto_alternativa,
               dc.descricao_alternativa                                  as descricao_alternativa,
               dc.ordem_alternativa                                      as ordem_alternativa,
               dc.prioridade_alternativa                                 as prioridade_alternativa,
               dc.alternativa_tipo_outros                                as alternativa_tipo_outros,
               dc.deve_abrir_ordem_servico                               as deve_abrir_ordem_servico,
               dc.anexo_midia_alternativa_nok                            as anexo_midia_alternativa_nok,
               dc.cod_auxiliar_alternativa                               as cod_auxiliar_alternativa,
               dc.alternativa_selecionada                                as alternativa_selecionada,
               dc.resposta_outros                                        as resposta_outros
        from dados_checklists dc
                 join equipe e
                      on e.codigo = dc.cod_equipe_colaborador
        order by dc.data_hora_sincronizacao desc;
end;
$$;