select cos.codigo                                                              as cod_os,
       cos.cod_unidade                                                         as cod_unidade,
       cos.status                                                              as status_os,
       cos.cod_checklist                                                       as cod_checklist,
       cosi.cod_pergunta_primeiro_apontamento                                  as cod_pergunta,
       cp.pergunta                                                             as pergunta,
       prio.prioridade                                                         as prioridade,
       v.placa                                                                 as placa_veiculo,
       c.km_veiculo                                                            as km_abertura_servico,
       cosi.cod_alternativa_primeiro_apontamento                               as cod_alternativa,
       f_if(cap.alternativa_tipo_outros, crn.resposta_outros, cap.alternativa) as alternativa,
       cosi.status_resolucao                                                   as status_item,
       c.data_hora                                                             as data_hora_abertura,
       cosi.qt_apontamentos                                                    as qt_apontamentosfrom
from checklist_ordem_servico cos
         join checklist_ordem_servico_itens cosi
              on cosi.cod_unidade = cos.cod_unidade and cosi.cod_os = cos.codigo
         join checklist_perguntas_data cp
              on cosi.cod_pergunta_primeiro_apontamento = cp.codigo and cosi.cod_contexto_pergunta = cp.codigo_contexto
         join checklist_alternativa_pergunta_data cap on cosi.cod_alternativa_primeiro_apontamento = cap.codigo and
                                                         cosi.cod_contexto_alternativa = cap.codigo_contexto
         join checklist_alternativa_prioridade prio on cap.prioridade = prio.prioridade
         join checklist c on cos.cod_checklist = c.codigo
         join veiculo v on v.codigo = c.cod_veiculo
         join checklist_respostas_nok crn on crn.cod_checklist_modelo = c.cod_checklist_modelo and
                                             crn.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo and
                                             crn.cod_pergunta = cp.codigo and crn.cod_alternativa = cap.codigo and
                                             crn.cod_checklist = c.codigo
where cos.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 4)
  and cos.status = 'A'
  and cosi.status_resolucao = 'P'
order by cos.cod_unidade;