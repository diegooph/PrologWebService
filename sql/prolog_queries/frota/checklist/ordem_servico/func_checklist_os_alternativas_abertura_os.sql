-- Sobre:
--
-- Function utilizada para listar as alternativas de um modelo de checklist.
--
-- Essa function utiliza a F_COD_VEICULO para descobrir se alguma alternativa possui item pendente no veículo em
-- questão. Além de lista as alternativas, a function retorna informações úteis à abertura de O.S, como:
-- TEM_ITEM_OS_PENDENTE, DEVE_ABRIR_ORDEM_SERVICO, QTD_APONTAMENTOS_ITEM, PRIORIDADE_ALTERNATIVA...
create or replace function func_checklist_os_alternativas_abertura_os(f_cod_modelo_checklist bigint,
                                                                      f_cod_versao_modelo_checklist bigint,
                                                                      f_cod_veiculo bigint)
    returns table
            (
                cod_alternativa                    bigint,
                cod_contexto_pergunta              bigint,
                cod_contexto_alternativa           bigint,
                cod_item_ordem_servico             bigint,
                resposta_tipo_outros_abertura_item text,
                tem_item_os_pendente               boolean,
                deve_abrir_ordem_servico           boolean,
                alternativa_tipo_outros            boolean,
                qtd_apontamentos_item              integer,
                prioridade_alternativa             text
            )
    language plpgsql
as
$$
declare
    status_item_pendente text = 'P';
begin
    return query
        -- Nessa CTE nós não usamos as tabelas com _DATA pois não queremos incrementar quantidade de itens de OS
        -- deletados.
        with itens_pendentes as (
            select cosi.codigo                               as cod_item_ordem_servico,
                   cosi.cod_alternativa_primeiro_apontamento as cod_alternativa_primeiro_apontamento,
                   cosi.cod_contexto_alternativa             as cod_contexto_alternativa,
                   cosi.qt_apontamentos                      as qtd_apontamentos_item,
                   cos.cod_checklist                         as cod_checklist,
                   c.cod_checklist_modelo                    as cod_checklist_modelo
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
            where c.cod_veiculo = f_cod_veiculo
              and c.cod_checklist_modelo = f_cod_modelo_checklist
              and cosi.status_resolucao = status_item_pendente
        )
        select cap.codigo                                          as cod_alternativa,
               cp.codigo_contexto                                  as cod_contexto_pergunta,
               cap.codigo_contexto                                 as cod_contexto_alternativa,
               ip.cod_item_ordem_servico                           as cod_item_ordem_servico,
               crn.resposta_outros                                 as resposta_tipo_outros_abertura_item,
               f_if(ip.cod_item_ordem_servico isnull, false, true) as tem_item_os_pendente,
               cap.deve_abrir_ordem_servico                        as deve_abrir_ordem_servico,
               cap.alternativa_tipo_outros                         as alternativa_tipo_outros,
               ip.qtd_apontamentos_item                            as qtd_apontamentos_item,
               cap.prioridade::text                                as prioridade_alternativa
               -- Nesse SELECT é utilizado a _DATA, pois um item pode estar pendente e sua alternativa deletada, nesse caso
               -- a alternativa ainda deve retornar para não quebrar o fluxo de processamento do checklist realizado.
        from checklist_alternativa_pergunta_data cap
                 join checklist_perguntas_data cp
                      on cap.cod_pergunta = cp.codigo
                 left join itens_pendentes ip
                           on ip.cod_contexto_alternativa = cap.codigo_contexto
                 left join checklist_respostas_nok crn
                           on crn.cod_alternativa = ip.cod_alternativa_primeiro_apontamento
                               and crn.cod_checklist = ip.cod_checklist
        where cap.cod_versao_checklist_modelo = f_cod_versao_modelo_checklist;
end ;
$$;