create or replace function func_checklist_ordem_servico_listagem(f_cod_unidades bigint[],
                                                                 f_cod_tipo_veiculo bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_status_ordem_servico text,
                                                                 f_incluir_itens_ordem_servico boolean,
                                                                 f_limit integer,
                                                                 f_offset integer)
    returns table
            (
                codigo_os_prolog                                 bigint,
                codigo_os                                        bigint,
                codigo_unidade                                   bigint,
                codigo_checklist                                 bigint,
                data_hora_abertura_utc                           timestamp with time zone,
                data_hora_abertura_tz_aplicado                   timestamp without time zone,
                status_os                                        text,
                data_hora_fechamento_utc                         timestamp with time zone,
                data_hora_fechamento_tz_aplicado                 timestamp without time zone,
                codigo_colaborador_abertura                      bigint,
                cpf_colaborador_abertura                         text,
                nome_colaborador_abertura                        text,
                codigo_veiculo                                   bigint,
                placa_veiculo                                    varchar(7),
                identificador_frota                              text,
                codigo_item_os                                   bigint,
                codigo_colaborador_fechamento                    bigint,
                cpf_colaborador_fechamento                       bigint,
                nome_colaborador_fechamento                      text,
                codigo_pergunta_primeiro_apontamento             bigint,
                codigo_contexto_pergunta                         bigint,
                codigo_alternativa_primeiro_apontamento          bigint,
                codigo_contexto_alternativa                      bigint,
                status_resolucao                                 text,
                quantidade_apontamentos                          int,
                km                                               bigint,
                codigo_agrupamento_resolucao_em_lote             bigint,
                data_hora_conserto_utc                           timestamp with time zone,
                data_hora_conserto_tz_aplicado                   timestamp without time zone,
                data_hora_inicio_resolucao_utc                   timestamp with time zone,
                data_hora_inicio_resolucao_tz_aplicado           timestamp without time zone,
                data_hora_fim_resolucao_utc                      timestamp with time zone,
                data_hora_fim_resolucao_tz_aplicado              timestamp without time zone,
                tempo_realizacao                                 bigint,
                feedback_conserto                                text,
                codigo_auxiliar_alternativa_primeiro_apontamento text
            )
    language sql
as
$$
with ordens_servicos_filtradas as (
    select cos.codigo                         as cod_os,
           cos.cod_unidade                    as cod_unidade,
           cos.status                         as status_os,
           cos.data_hora_fechamento           as data_hora_fechamento,
           cos.codigo_prolog                  as codigo_os_prolog,
           c.codigo                           as cod_checklist,
           c.data_hora                        as data_hora_checklist,
           c.data_hora_realizacao_tz_aplicado as data_hora_checklist_tz_aplicado,
           c.cpf_colaborador                  as cpf_colaborador_checklist,
           v.codigo                           as cod_veiculo,
           v.placa                            as placa_veiculo,
           v.identificador_frota              as identificador_frota
    from checklist_ordem_servico cos
             join checklist c on cos.cod_checklist = c.codigo
             join veiculo v on v.codigo = c.cod_veiculo
    where cos.cod_unidade = any (f_cod_unidades)
      and case when f_status_ordem_servico is null then true else cos.status = f_status_ordem_servico end
      and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
      and case when f_cod_veiculo is null then true else c.cod_veiculo = f_cod_veiculo end
    order by cos.codigo
    limit f_limit offset f_offset
)
select osf.codigo_os_prolog                                            as codigo_os_prolog,
       osf.cod_os                                                      as codigo_os,
       osf.cod_unidade                                                 as codigo_unidade,
       osf.cod_checklist                                               as codigo_checklist,
       osf.data_hora_checklist                                         as data_hora_abertura_utc,
       osf.data_hora_checklist_tz_aplicado                             as data_hora_abertura_tz_aplicado,
       osf.status_os                                                   as status_os,
       osf.data_hora_fechamento                                        as data_hora_fechamento_utc,
       osf.data_hora_fechamento at time zone tz_unidade(c.cod_unidade) as data_hora_fechamento_tz_aplicado,
       c.codigo                                                        as codigo_colaborador_abertura,
       lpad(c.cpf::text, 11, '0')                                      as cpf_colaborador_abertura,
       c.nome                                                          as nome_colaborador_abertura,
       osf.cod_veiculo                                                 as codigo_veiculo,
       osf.placa_veiculo                                               as placa_veiculo,
       osf.identificador_frota                                         as identificador_frota,
       cosi.codigo                                                     as codigo_item_os,
       cm.codigo                                                       as codigo_colaborador_fechamento,
       cosi.cpf_mecanico                                               as cpf_colaborador_fechamento,
       cm.nome                                                         as nome_colaborador_fechamento,
       cosi.cod_pergunta_primeiro_apontamento                          as codigo_pergunta_primeiro_apontamento,
       cosi.cod_contexto_pergunta                                      as codigo_contexto_pergunta,
       cosi.cod_alternativa_primeiro_apontamento                       as codigo_alternativa_primeiro_apontamento,
       cosi.cod_contexto_alternativa                                   as codigo_contexto_alternativa,
       cosi.status_resolucao                                           as status_resolucao,
       cosi.qt_apontamentos                                            as quantidade_apontamentos,
       cosi.km                                                         as km,
       cosi.cod_agrupamento_resolucao_em_lote                          as codigo_agrupamento_resolucao_em_lote,
       cosi.data_hora_conserto                                         as data_hora_conserto_utc,
       cosi.data_hora_conserto
           at time zone tz_unidade(cm.cod_unidade)
                                                                       as data_hora_conserto_tz_aplicado,
       cosi.data_hora_inicio_resolucao                                 as data_hora_inicio_resolucao_utc,
       cosi.data_hora_inicio_resolucao
           at time zone tz_unidade(cm.cod_unidade)
                                                                       as data_hora_inicio_resolucao_tz_aplicado,
       cosi.data_hora_fim_resolucao                                    as data_hora_fim_resolucao_utc,
       cosi.data_hora_fim_resolucao
           at time zone tz_unidade(cm.cod_unidade)
                                                                       as data_hora_fim_resolucao_tz_aplicado,
       cosi.tempo_realizacao                                           as tempo_realizacao,
       cosi.feedback_conserto                                          as feedback_conserto,
       cap.cod_auxiliar                                                as codigo_auxiliar_alternativa_primeiro_apontamento
from ordens_servicos_filtradas osf
         join colaborador c on c.cpf = osf.cpf_colaborador_checklist
         left join checklist_ordem_servico_itens cosi
                   on osf.cod_unidade = cosi.cod_unidade and osf.cod_os = cosi.cod_os and f_incluir_itens_ordem_servico
         left join checklist_alternativa_pergunta cap on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
         left join colaborador cm on cm.cpf = cosi.cpf_mecanico
order by osf.cod_os, cosi.codigo;
$$;