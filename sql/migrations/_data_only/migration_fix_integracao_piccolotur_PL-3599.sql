-- #####################################################################################################################
-- Volta as functions para o padrão antigo.
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

-- #####################################################################################################################
create or replace function piccolotur.func_check_os_insere_checklist_pendente_sincronia(f_cod_checklist bigint)
    returns void
    language plpgsql
as
$$
declare
    v_data_hora_realizado constant timestamp with time zone := (select data_hora
                                                                from checklist
                                                                where codigo = f_cod_checklist);
begin
    insert into piccolotur.checklist_pendente_para_sincronizar (cod_checklist_para_sincronizar, data_hora_realizado)
    values (f_cod_checklist, v_data_hora_realizado)
    on conflict on constraint unique_cod_checklist_para_sincronizar
        do update set data_hora_realizado = v_data_hora_realizado;

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível inserir o checklist (%s) na tabela de pendentes para envio',
                                     f_cod_checklist));
    end if;
end;
$$;

-- #####################################################################################################################
-- Marca os checklists para sincronizar novamente.
with dados_checklists_pendentes as (
    select c.data_hora_realizacao_tz_aplicado as data_hora_realizado,
           cpps.*
    from piccolotur.checklist_pendente_para_sincronizar cpps
             join checklist c on cpps.cod_checklist_para_sincronizar = c.codigo
    where c.data_hora_realizacao_tz_aplicado > '2021-03-30 21:00:00'
      and cpps.precisa_ser_sincronizado is true
      and cpps.bloqueado_sincronia is true
      and cpps.sincronizado is false
    order by data_hora_realizacao_tz_aplicado desc
)

update piccolotur.checklist_pendente_para_sincronizar
set bloqueado_sincronia = false
where cod_checklist_para_sincronizar in (select dcp.cod_checklist_para_sincronizar from dados_checklists_pendentes dcp);

update piccolotur.checklist_pendente_para_sincronizar
set next_to_sync = false;

update piccolotur.checklist_pendente_para_sincronizar
set next_to_sync = true
where cod_checklist_para_sincronizar =
      (select cod_checklist_para_sincronizar
       from piccolotur.checklist_pendente_para_sincronizar
       where precisa_ser_sincronizado is true
         and bloqueado_sincronia is true
         and sincronizado is false
       order by cod_checklist_para_sincronizar
       limit 1);