-- Sobre:
--
-- Essa function busca todas as informações pertinentes a uma O.S, de forma genérica, para que seja realizada uma
-- integração.
--
-- Histórico:
-- 2020-08-25 -> Function criada (gustavocnp95 - PL-2903).
-- 2020-08-27 -> Adiciona código interno da os (diogenesvanzella - PL-2903).
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
-- 2020-11-07 -> Adiciona filtro de status da OS (diogenesvanzella - PL-3283).
-- 2020-11-07 -> Adiciona mais informações no retorno da OS (diogenesvanzella - PL-3283).
create or replace function integracao.func_checklist_os_busca_informacoes_os(f_cod_interno_os_prolog bigint[],
                                                                             f_status_os text default null)
    returns table
            (
                cod_unidade                  bigint,
                cod_auxiliar_unidade         text,
                cod_interno_os_prolog        bigint,
                cod_os_prolog                bigint,
                data_hora_abertura_os        timestamp without time zone,
                placa_veiculo                text,
                km_veiculo_na_abertura       bigint,
                cpf_colaborador_checklist    text,
                status_os                    text,
                data_hora_fechamento_os      timestamp without time zone,
                cod_item_os                  bigint,
                cod_alternativa              bigint,
                cod_auxiliar_alternativa     text,
                descricao_alternativa        text,
                alternativa_tipo_outros      boolean,
                descricao_tipo_outros        text,
                prioridade_alternativa       text,
                status_item_os               text,
                km_veiculo_fechamento_item   bigint,
                data_hora_fechamento_item_os timestamp without time zone,
                data_hora_inicio_resolucao   timestamp without time zone,
                data_hora_fim_resolucao      timestamp without time zone,
                descricao_fechamento_item_os text
            )
    language plpgsql
as
$$
begin
    return query
        select cos.cod_unidade                                                   as cod_unidade,
               u.cod_auxiliar                                                    as cod_auxiliar_unidade,
               cos.codigo_prolog                                                 as cod_interno_os_prolog,
               cos.codigo                                                        as cod_os_prolog,
               c.data_hora_realizacao_tz_aplicado                                as data_hora_abertura_os,
               c.placa_veiculo::text                                             as placa_veiculo,
               c.km_veiculo                                                      as km_veiculo_na_abertura,
               lpad(c.cpf_colaborador::text, 11, '0')                            as cpf_colaborador_checklist,
               cos.status::text                                                  as status_os,
               cos.data_hora_fechamento at time zone tz_unidade(u.codigo)        as data_hora_fechamento_os,
               cosi.codigo                                                       as cod_item_os,
               cosi.cod_alternativa_primeiro_apontamento                         as cod_alternativa,
               cap.cod_auxiliar                                                  as cod_auxiliar_alternativa,
               cap.alternativa                                                   as descricao_alternativa,
               cap.alternativa_tipo_outros                                       as alternativa_tipo_outros,
               case
                   when cap.alternativa_tipo_outros
                       then
                       (select crn.resposta_outros
                        from checklist_respostas_nok crn
                        where crn.cod_checklist = c.codigo
                          and crn.cod_alternativa = cap.codigo)::text
                   end                                                           as descricao_tipo_outros,
               cap.prioridade::text                                              as prioridade_alternativa,
               cosi.status_resolucao                                             as status_item_os,
               cosi.km                                                           as km_veiculo_fechamento_item,
               cosi.data_hora_conserto at time zone tz_unidade(u.codigo)         as data_hora_fechamento_item_os,
               cosi.data_hora_inicio_resolucao at time zone tz_unidade(u.codigo) as data_hora_inicio_resolucao,
               cosi.data_hora_fim_resolucao at time zone tz_unidade(u.codigo)    as data_hora_fim_resolucao,
               cosi.feedback_conserto                                            as descricao_fechamento_item_os
        from checklist_ordem_servico cos
                 join checklist c on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join unidade u on u.codigo = cos.cod_unidade
        where cos.codigo_prolog = any (f_cod_interno_os_prolog)
          and f_if(f_status_os is null, true, cos.status = f_status_os)
        order by cos.codigo_prolog, cosi.codigo;
end;
$$;