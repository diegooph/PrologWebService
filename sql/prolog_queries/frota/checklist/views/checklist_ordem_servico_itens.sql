create or replace view checklist_ordem_servico_itens as
select cosi.cod_unidade,
       cosi.codigo,
       cosi.cod_os,
       cosi.cpf_mecanico,
       cosi.cod_pergunta_primeiro_apontamento,
       cosi.cod_contexto_pergunta,
       cosi.cod_contexto_alternativa,
       cosi.cod_alternativa_primeiro_apontamento,
       cosi.status_resolucao,
       cosi.qt_apontamentos,
       cosi.km,
       cosi.data_hora_conserto,
       cosi.data_hora_inicio_resolucao,
       cosi.data_hora_fim_resolucao,
       cosi.tempo_realizacao,
       cosi.feedback_conserto,
       cosi.cod_agrupamento_resolucao_em_lote
from checklist_ordem_servico_itens_data cosi
where cosi.deletado = false;