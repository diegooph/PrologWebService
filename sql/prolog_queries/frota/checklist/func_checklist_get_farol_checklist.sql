-- Sobre:
--
-- Function que gera o farol de checklists. A function está preparada para lidar com itens criticos retroativos, ou
-- seja, itens que estão pendentes mas que não foram apontados no checklist do dia.
-- Para não prejudicar o desempenho da function, limitamos em cinco o número de itens críticos pendentes retornados pela
-- function. Isso não impacta negativamente o farol, pois a placa é mostrada como bloqueada, independente da quantidade
-- de itens mostrados.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Corrige funcionamento da query (diogenesvanzella - PL-2816).
-- 2020-10-13 -> Melhora performance da query (luizfp - PL-3199).
-- 2020-10-13 -> Aumenta memória disponível para uso para 8MB (luizfp - PL-3199).
create or replace function func_checklist_get_farol_checklist(f_cod_unidade bigint,
                                                              f_data_inicial date,
                                                              f_data_final date,
                                                              f_itens_criticos_retroativos boolean)
    returns table
            (
                data                               date,
                placa                              text,
                cod_checklist_saida                bigint,
                data_hora_ultimo_checklist_saida   timestamp without time zone,
                cod_checklist_modelo_saida         bigint,
                nome_colaborador_checklist_saida   text,
                cod_checklist_retorno              bigint,
                data_hora_ultimo_checklist_retorno timestamp without time zone,
                cod_checklist_modelo_retorno       bigint,
                nome_colaborador_checklist_retorno text,
                codigo_pergunta                    bigint,
                descricao_pergunta                 text,
                descricao_alternativa              text,
                alternativa_tipo_outros            boolean,
                descricao_alternativa_tipo_outros  text,
                codigo_item_critico                bigint,
                data_hora_apontamento_item_critico timestamp without time zone
            )
    language plpgsql
as
$$
declare
    -- Algumas empresas não fecham OS, o que acarreta em um grande número de itens críticos para cada placa. A função
    -- do Farol é alertar sobre a existência de pendências, não listar um a um, assim limitamos a busca a apenas cinco
    -- itens críticos por placa e garantimos o desempenho da query.
    v_qtd_maxima_itens_criticos constant bigint not null := 5;
begin
    -- Aumenta memória disponível da function para evitar trabalho em disco.
    set local work_mem = '8MB';

    return query
        -- A utilização de uma CTE para filtrar os checklists parece ser desnecessária, porém mostrou uma melhora
        -- significativa no desempenho da query no geral.
        with checks_filtrados as (
            select c.codigo                           as cod_checklist,
                   c.cod_checklist_modelo             as cod_checklist_modelo,
                   c.placa_veiculo                    as placa_veiculo,
                   c.cpf_colaborador                  as cpf_colaborador,
                   c.data_hora_realizacao_tz_aplicado as data_hora_realizacao_tz_aplicado,
                   c.tipo                             as tipo_checklist,
                   co.nome                            as nome_colaborador
            from checklist c
                     join colaborador co on co.cpf = c.cpf_colaborador
            where c.cod_unidade = f_cod_unidade
              and c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final
        ),
             ultimos_checklists_veiculos as (
                 select checks_placas_dias.data                  as data,
                        checks_placas_dias.placa                 as placa,
                        checks_placas_dias.cod_checklist_saida   as cod_checklist_saida,
                        cfs.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_saida,
                        cfs.cod_checklist_modelo                 as cod_checklist_modelo_saida,
                        cfs.nome_colaborador                     as nome_colaborador_checklist_saida,
                        checks_placas_dias.cod_checklist_retorno as cod_checklist_retorno,
                        cfr.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_retorno,
                        cfr.cod_checklist_modelo                 as cod_checklist_modelo_retorno,
                        cfr.nome_colaborador                     as nome_colaborador_checklist_retorno
                 from (select g.day::date                                                      as data,
                              v.placa                                                          as placa,
                              max(case when cf.tipo_checklist = 'S' then cf.cod_checklist end) as cod_checklist_saida,
                              max(case when cf.tipo_checklist = 'R' then cf.cod_checklist end) as cod_checklist_retorno
                       from veiculo v
                                cross join generate_series(f_data_inicial, f_data_final, '1 DAY') g(day)
                                left join checks_filtrados cf
                                          on cf.placa_veiculo = v.placa and
                                             g.day::date = (cf.data_hora_realizacao_tz_aplicado)::date
                       where v.cod_unidade = f_cod_unidade
                         and v.status_ativo = true
                       group by data, v.placa) as checks_placas_dias
                          left join checks_filtrados cfs on cfs.cod_checklist = checks_placas_dias.cod_checklist_saida
                          left join checks_filtrados cfr on cfr.cod_checklist = checks_placas_dias.cod_checklist_retorno
             ),
             itens_criticos_filtrados as (
                 select row_number() over (partition by c.placa_veiculo) as row_id,
                        c.codigo                                         as cod_checklist_item,
                        c.placa_veiculo                                  as placa_veiculo_checklist_item,
                        cap.cod_pergunta                                 as cod_pergunta,
                        cap.codigo                                       as cod_alternativa,
                        cap.alternativa                                  as descricao_alternativa,
                        cap.alternativa_tipo_outros                      as alternativa_tipo_outros,
                        cosi.codigo                                      as cod_item_os,
                        c.data_hora_realizacao_tz_aplicado               as data_hora_abertura_os
                 from checklist_ordem_servico_itens cosi
                          join checklist_ordem_servico cos
                               on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                          join checklist c on cos.cod_checklist = c.codigo
                          join checklist_alternativa_pergunta cap
                               on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 where cosi.cod_unidade = f_cod_unidade
                   and cosi.status_resolucao = 'P'
                   and cap.prioridade = 'CRITICA'
                   -- Buscar itens retroativos é uma decisão de, respeitar ou não a data de apontamento do item.
                   -- Fazemos isso utilizando a data de realização do checklist.
                   and case
                           when f_itens_criticos_retroativos then true
                           else (c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final) end
                   -- Ordenamento para garantirmos a exibição dos itens mais recentes.
                 order by cosi.codigo desc
             ),
             itens_criticos_placa as (
                 select icf.cod_checklist_item           as cod_checklist_item,
                        icf.placa_veiculo_checklist_item as placa_veiculo_checklist_item,
                        cp.codigo                        as cod_pergunta,
                        cp.pergunta                      as descricao_pergunta,
                        icf.cod_alternativa              as cod_alternativa,
                        icf.descricao_alternativa        as descricao_alternativa,
                        icf.alternativa_tipo_outros      as alternativa_tipo_outros,
                        crn.resposta_outros              as descricao_alternativa_tipo_outros,
                        icf.cod_item_os                  as cod_item_os,
                        icf.data_hora_abertura_os        as data_hora_abertura_os
                 from itens_criticos_filtrados icf
                          join checklist_perguntas cp
                               on cp.codigo = icf.cod_pergunta
                          join checklist_respostas_nok crn
                               on crn.cod_checklist = icf.cod_checklist_item
                                   and crn.cod_alternativa = icf.cod_alternativa
                 where icf.row_id <= v_qtd_maxima_itens_criticos
             )

        select ucv.data,
               ucv.placa::text,
               ucv.cod_checklist_saida,
               ucv.data_hora_ultimo_checklist_saida,
               ucv.cod_checklist_modelo_saida,
               ucv.nome_colaborador_checklist_saida::text,
               ucv.cod_checklist_retorno,
               ucv.data_hora_ultimo_checklist_retorno,
               ucv.cod_checklist_modelo_retorno,
               ucv.nome_colaborador_checklist_retorno::text,
               -- informações dos itens críticos, não necessáriamente relacionados ao checklist
               icp.cod_pergunta,
               icp.descricao_pergunta::text,
               icp.descricao_alternativa::text,
               icp.alternativa_tipo_outros,
               icp.descricao_alternativa_tipo_outros::text,
               icp.cod_item_os,
               icp.data_hora_abertura_os
        from ultimos_checklists_veiculos ucv
                 -- O join deve ocorrer pelo código do checklist ou pela placa, mas sem duplicar.
                 left join itens_criticos_placa icp
                           on case
                                  when icp.cod_checklist_item in (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                      then icp.cod_checklist_item in
                                           (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                  else icp.placa_veiculo_checklist_item = ucv.placa end
        order by ucv.data, ucv.placa, icp.cod_item_os;
end;
$$;