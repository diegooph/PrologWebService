-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para buscar as informações do checklist para serem sincronizadas.
--
-- Essa function retorna apenas os itens NOK apontados no checklist, caso nenhum item tenha sido apontado, então nenhuma
-- pergunta e alternativa será retornada, porém, as informações básicas do checklist, serão.
-- Restringimos a function para não retornar alternativas que não abrem O.S, pois não é necessário.
--
-- Histórico:
-- 2020-01-21 -> Function criada (diogenesvanzella - PLI-66).
-- 2020-02-25 -> Alteração do nome da function (diogenesvanzella - PLI-70).
-- 2020-03-09 -> Remove busca de alternativas que não abrem O.S (diogenesvanzella - PLI-98).
-- 2020-04-28 -> Adiciona tipo_outros no retorno da function (diogenesvanzella - PLI-138).
-- 2020-06-12 -> Adiciona cod_pergunta no retorno da function (diogenesvanzella - PLI-137).
create or replace function piccolotur.func_check_os_busca_checklist_itens_nok(f_cod_checklist_prolog bigint)
    returns table
            (
                cod_unidade_checklist        bigint,
                cod_modelo_checklist         bigint,
                cod_versao_modelo_checklist  bigint,
                cpf_colaborador_realizacao   text,
                placa_veiculo_checklist      text,
                km_coletado_checklist        bigint,
                tipo_checklist               text,
                data_hora_realizacao         timestamp without time zone,
                total_alternativas_nok       integer,
                cod_pergunta                 bigint,
                cod_contexto_pergunta_nok    bigint,
                descricao_pergunta_nok       text,
                cod_alternativa_nok          bigint,
                cod_contexto_alternativa_nok bigint,
                descricao_alternativa_nok    text,
                alternativa_tipo_outros      boolean,
                prioridade_alternativa_nok   text
            )
    language sql
as
$$
with alternativas as (
    select crn.cod_checklist                                                       as cod_checklist,
           -- Por conta do filtro no WHERE, apenas buscamos alternativas que devem abrir O.S., assim, teremos sempre
           -- uma única partição.
           count(cap.codigo) over (partition by cap.deve_abrir_ordem_servico)      as qtd_alternativas_nok,
           cp.codigo                                                               as cod_pergunta,
           cp.codigo_contexto                                                      as cod_contexto_pergunta,
           cp.pergunta                                                             as descricao_pergunta,
           cap.codigo                                                              as cod_alternativa,
           cap.codigo_contexto                                                     as cod_contexto_alternativa,
           f_if(cap.alternativa_tipo_outros, crn.resposta_outros, cap.alternativa) as descricao_alternativa,
           cap.alternativa_tipo_outros                                             as alternativa_tipo_outros,
           cap.prioridade                                                          as prioridade_alternativa
    from checklist_respostas_nok crn
             join checklist_alternativa_pergunta cap
        -- Fazemos o JOIN apenas para as alternativas que abrem OS, pois para as demais, não nos interessa nada.
                  on cap.codigo = crn.cod_alternativa and cap.deve_abrir_ordem_servico
             join checklist_perguntas cp on crn.cod_pergunta = cp.codigo
    where crn.cod_checklist = f_cod_checklist_prolog
)

select c.cod_unidade                                            as cod_unidade_checklist,
       c.cod_checklist_modelo                                   as cod_modelo_checklist,
       c.cod_versao_checklist_modelo                            as cod_versao_modelo_checklist,
       lpad(c.cpf_colaborador::text, 11, '0')                   as cpf_colaborador_realizacao,
       c.placa_veiculo::text                                    as placa_veiculo_checklist,
       c.km_veiculo                                             as km_coletado_checklist,
       f_if(c.tipo::text = 'S', 'SAIDA'::text, 'RETORNO'::text) as tipo_checklist,
       c.data_hora at time zone tz_unidade(c.cod_unidade)       as data_hora_realizacao,
       coalesce(a.qtd_alternativas_nok, 0)::integer             as total_alternativas_nok,
       a.cod_pergunta                                           as cod_pergunta,
       a.cod_contexto_pergunta                                  as cod_contexto_pergunta_nok,
       a.descricao_pergunta                                     as descricao_pergunta_nok,
       a.cod_alternativa                                        as cod_alternativa_nok,
       a.cod_contexto_alternativa                               as cod_contexto_alternativa_nok,
       a.descricao_alternativa                                  as descricao_alternativa_nok,
       a.alternativa_tipo_outros                                as alternativa_tipo_outros,
       a.prioridade_alternativa                                 as prioridade_alternativa_nok
from checklist c
         -- Usamos LEFT JOIN para os cenários onde o check não possuir nenhum item NOK, mesmo para esses cenários
         -- devemos retornar as infos do checklist mesmo assim.
         left join alternativas a on a.cod_checklist = c.codigo
where c.codigo = f_cod_checklist_prolog
order by a.cod_alternativa;
$$;