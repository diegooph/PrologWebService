-- Sobre:
--
-- Function utilizada para buscar um relatório das Ordens de Serviços que estão como pendentes de sincronia. Os dados
-- mostram também, quantas tentativas de sincronia foram executadas e qual o último problema.
-- O filtro de data_inicio e data_fim permitem filtrar Ordens de Serviços que foram abertas ou fechadas:
-- 1 - Entre a data de início e fim;
-- 2 - A partir da data de início;
-- 3 - Antes da data de fim;
create or replace function integracao.func_checklist_os_busca_oss_pendentes_sincronia(f_data_inicio date default null,
                                                                                      f_data_fim date default null)
    returns table
            (
                nome_unidade               text,
                cod_unidade                bigint,
                de_para_unidade            text,
                cod_os                     bigint,
                placa_veiculo_os           text,
                status_os                  text,
                data_hora_abertura_os      timestamp without time zone,
                data_hora_fechamento_os    timestamp without time zone,
                cod_checklist_os           bigint,
                cpf_motorista              text,
                nome_motorista             text,
                cod_item_os                bigint,
                de_para_alternativa        text,
                descricao_pergunta         text,
                descricao_alternativa      text,
                status_item_os             text,
                data_hora_resolucao_item   timestamp without time zone,
                qtd_tentativas_sincronia   bigint,
                data_hora_ultima_tentativa timestamp without time zone,
                mensagem_ultima_tentativa  text
            )
    language sql
as
$$
select u.nome::text                                                            as nome_unidade,
       u.codigo                                                                as cod_unidade,
       u.cod_auxiliar::text                                                    as de_para_unidade,
       cos.codigo                                                              as cod_os,
       v.placa::text                                                           as placa_veiculo_os,
       f_if(cos.status = 'F', 'fechada'::text, 'aberta'::text)::text           as status_os,
       c.data_hora_realizacao_tz_aplicado                                      as data_hora_abertura_os,
       cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade)       as data_hora_fechamento_os,
       c.codigo                                                                as cod_checklist_os,
       lpad(c.cpf_colaborador::text, 11, '0')::text                            as cpf_motorista,
       co.nome::text                                                           as nome_motorista,
       cosi.codigo                                                             as cod_item_os,
       cap.cod_auxiliar::text                                                  as de_para_alternativa,
       cp.pergunta                                                             as descricao_pergunta,
       f_if(cap.alternativa_tipo_outros, crn.resposta_outros, cap.alternativa) as descricao_alternativa,
       f_if(cosi.status_resolucao = 'R', 'resolvido'::text, 'pendente'::text)  as status_item_os,
       cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade)       as data_hora_resolucao_item,
       coss.quantidade_tentativas                                              as qtd_tentativas_sincronia,
       coss.data_ultima_tentativa at time zone tz_unidade(cos.cod_unidade)     as data_hora_ultima_tentativa,
       coss.mensagem_ultima_tentativa::text                                    as mensagem_ultima_tentativa
from integracao.checklist_ordem_servico_sincronizacao coss
         join checklist_ordem_servico cos on cos.codigo_prolog = coss.codigo_os_prolog
         join checklist_ordem_servico_itens cosi on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
         join checklist_perguntas cp on cp.codigo = cosi.cod_pergunta_primeiro_apontamento
         join checklist_alternativa_pergunta cap on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
         join checklist c on c.codigo = cos.cod_checklist
         join colaborador co on c.cpf_colaborador = co.cpf
         join unidade u on cos.cod_unidade = u.codigo
         join veiculo v on v.codigo = c.cod_veiculo
         left join checklist_respostas_nok crn
                   on crn.cod_checklist = c.codigo
                       and crn.cod_pergunta = cp.codigo
                       and crn.cod_alternativa = cap.codigo
where coss.pendente_sincronia = true
  and coss.bloquear_sicronia = false
  -- Filtramos por OSs que tenham sido abertas ou fechadas nas datas filtradas.
  and ((f_if(f_data_inicio is null, true, c.data_hora_realizacao_tz_aplicado::date >= f_data_inicio)
    and f_if(f_data_fim is null, true, c.data_hora_realizacao_tz_aplicado::date <= f_data_fim))
    or (f_if(f_data_inicio is null, true,
             (cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade))::date >= f_data_inicio)
        and
        f_if(f_data_fim is null, true,
             (cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade))::date <= f_data_fim))
    or (f_if(f_data_inicio is null, true,
             (cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade))::date >= f_data_inicio)
        and
        f_if(f_data_fim is null, true,
             (cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade))::date <= f_data_fim)))
order by u.codigo,
         cos.codigo, cosi.codigo;
$$;