create
    or replace function func_checklist_ordem_servico_listagem(f_cod_unidades bigint[],
                                                              f_cod_tipo_veiculo bigint,
                                                              f_cod_veiculo bigint,
                                                              f_status_ordem_servico text,
                                                              f_incluir_itens_ordem_servico boolean,
                                                              f_limit integer,
                                                              f_offset integer)
    returns table
            (
                codigo_os_prolog                        bigint,
                codigo_os                               bigint,
                codigo_unidade                          bigint,
                codigo_checklist                        bigint,
                status_os                               text,
                data_hora_fechamento                    timestamp with time zone,
                codigo_item_os                          bigint,
                cpf_mecanico                            bigint,
                codigo_pergunta_primeiro_apontamento    bigint,
                codigo_contexto_pergunta                bigint,
                codigo_alternativa_primeiro_apontamento bigint,
                codigo_contexto_alternativa             bigint,
                status_resolucao                        text,
                quantidade_apontamentos                 int,
                km                                      bigint,
                codigo_agrupamento_resolucao_em_lote    bigint,
                data_hora_conserto                      timestamp with time zone,
                data_hora_inicio_resolucao              timestamp with time zone,
                data_hora_fim_resolucao                 timestamp with time zone,
                tempo_realizacao                        bigint,
                feedback_conserto                       text
            )
    language sql
as
$$
select cos.codigo_prolog                         as codigo_os_prolog,
       cos.codigo                                as codigo_os,
       cos.cod_unidade                           as codigo_unidade,
       cos.cod_checklist                         as codigo_checklist,
       cos.status                                as status_os,
       cos.data_hora_fechamento                  as data_hora_fechamento,
       cosi.codigo                               as codigo_item_os,
       cosi.cpf_mecanico                         as cpf_mecanico,
       cosi.cod_pergunta_primeiro_apontamento    as codigo_pergunta_primeiro_apontamento,
       cosi.cod_contexto_pergunta                as codigo_contexto_pergunta,
       cosi.cod_alternativa_primeiro_apontamento as codigo_alternativa_primeiro_apontamento,
       cosi.cod_contexto_alternativa             as codigo_contexto_alternativa,
       cosi.status_resolucao                     as status_resolucao,
       cosi.qt_apontamentos                      as quantidade_apontamentos,
       cosi.km                                   as km,
       cosi.cod_agrupamento_resolucao_em_lote    as codigo_agrupamento_resolucao_em_lote,
       cosi.data_hora_conserto                   as data_hora_conserto,
       cosi.data_hora_inicio_resolucao           as data_hora_inicio_resolucao,
       cosi.data_hora_fim_resolucao              as data_hora_fim_resolucao,
       cosi.tempo_realizacao                     as tempo_realizacao,
       cosi.feedback_conserto                    as feedback_conserto
from checklist_ordem_servico cos
         left join checklist_ordem_servico_itens cosi
                   on cos.cod_unidade = cosi.cod_unidade
                       and cos.codigo = cosi.cod_os
                       and f_incluir_itens_ordem_servico
         inner join checklist c on c.codigo = cos.cod_checklist
         inner join veiculo v on v.codigo = c.cod_veiculo
where cos.cod_unidade = any (f_cod_unidades)
  and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
  and case when f_cod_veiculo is null then true else v.codigo = f_cod_veiculo end
  and case when f_status_ordem_servico is null then true else cos.status = f_status_ordem_servico end
order by cos.codigo, cosi.codigo
limit f_limit offset f_offset;
$$;


----------------- FUNCTIONS CONVERSORAS PELO NULL --------------------------------------
create
    or replace function to_bigint(value anyelement)
    returns bigint
    language sql
as
$$
select value::text::bigint
$$;

create
    or replace function to_text(value anyelement)
    returns text
    language sql
as
$$
select value::text
$$;