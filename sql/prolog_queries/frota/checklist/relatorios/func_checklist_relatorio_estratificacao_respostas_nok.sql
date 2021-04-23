create or replace function func_checklist_relatorio_estratificacao_respostas_nok(f_cod_unidades bigint[],
                                                                                 f_data_inicial date,
                                                                                 f_data_final date)
    returns table
            (
                "UNIDADE"                         text,
                "CODIGO CHECKLIST"                bigint,
                "DATA"                            character varying,
                "PLACA"                           character varying,
                "TIPO DE VEÍCULO"                 text,
                "TIPO"                            text,
                "KM"                              bigint,
                "NOME"                            character varying,
                "PERGUNTA"                        character varying,
                "ALTERNATIVA"                     character varying,
                "RESPOSTA"                        character varying,
                "IMAGENS ADICIONADAS ALTERNATIVA" bigint,
                "PRIORIDADE"                      character varying,
                "PRAZO EM HORAS"                  integer,
                "AÇÃO GERADA"                     text
            )
    language sql
as
$$
select u.nome                                                                     as nome_unidade,
       c.codigo                                                                   as cod_checklist,
       format_timestamp(c.data_hora_realizacao_tz_aplicado, 'DD/MM/YYYY HH24:MI') as data_hora_check,
       v.placa                                                                    as placa_veiculo,
       vt.nome                                                                    as tipo_veiculo,
       case
           when c.tipo = 'S'
               then 'Saída'
           else 'Retorno' end                                                     as tipo_checklist,
       c.km_veiculo                                                               as km_veiculo,
       co.nome                                                                    as nome_realizador_check,
       cp.pergunta                                                                as descricao_pergunta,
       cap.alternativa                                                            as descricao_alternativa,
       crn.resposta_outros                                                        as resposta,
       (select count(*)
        from checklist_respostas_midias_alternativas_nok crman
        where c.codigo = crman.cod_checklist
          and crn.cod_alternativa = crman.cod_alternativa)                        as total_midias_alternativa,
       cap.prioridade                                                             as prioridade,
       prio.prazo                                                                 as prazo,
       case
           when cosia.nova_qtd_apontamentos is null
               then 'Não abriu O.S.'
           when cosia.nova_qtd_apontamentos = 1
               then 'Abriu O.S.'
           when cosia.nova_qtd_apontamentos > 1
               then 'Incrementou Apontamentos'
           end                                                                    as acao_ordem_servico
from checklist c
         join veiculo v
              on v.codigo = c.cod_veiculo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
         join checklist_perguntas cp
              on cp.cod_checklist_modelo = c.cod_checklist_modelo
         join checklist_alternativa_pergunta cap
              on cap.cod_pergunta = cp.codigo
         join checklist_alternativa_prioridade prio
              on prio.prioridade::text = cap.prioridade::text
         join checklist_respostas_nok crn
              on c.codigo = crn.cod_checklist
                  and crn.cod_alternativa = cap.codigo
         join colaborador co
              on co.cpf = c.cpf_colaborador
         join unidade u
              on c.cod_unidade = u.codigo
         left join checklist_ordem_servico_itens_apontamentos cosia
                   on cap.deve_abrir_ordem_servico is true
                       and cosia.cod_checklist_realizado = c.codigo
                       and cosia.cod_alternativa = crn.cod_alternativa
where c.cod_unidade = any (f_cod_unidades)
  and c.data_hora_realizacao_tz_aplicado::date >= f_data_inicial
  and c.data_hora_realizacao_tz_aplicado::date <= f_data_final
order by u.nome, c.data_hora_sincronizacao desc, c.codigo
$$;