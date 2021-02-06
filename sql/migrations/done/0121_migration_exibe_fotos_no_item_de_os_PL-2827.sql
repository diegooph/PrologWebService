alter table checklist_respostas_midias_alternativas_nok
    add column codigo bigserial;

alter table checklist_respostas_midias_alternativas_nok
    drop constraint pk_checklist_respostas_midias_alternativas_nok;

alter table checklist_respostas_midias_alternativas_nok
    add constraint pk_checklist_respostas_midias_alternativas_nok primary key (codigo);

alter table checklist_respostas_midias_alternativas_nok
    add constraint unique_uuid_checklist_respostas_midias_alternativa_nok unique (uuid);

create table if not exists checklist_ordem_servico_itens_midia
(
    codigo        bigserial not null
        constraint pk_checklist_ordem_servico_itens_midia
            primary key,
    cod_item_os   bigint    not null
        constraint fk_cod_item_os_checklist_ordem_servico_itens_midia
            references checklist_ordem_servico_itens_data,
    cod_midia_nok bigint    not null
        constraint unique_cod_midia_nok_checklist_ordem_servico_itens_midia
            unique
        constraint fk_cod_midia_checklist_ordem_servico_itens_midia
            references checklist_respostas_midias_alternativas_nok
);

comment on table checklist_ordem_servico_itens_midia
    is 'Salva o código das midias já relacionando com o código do item da os,
    pra performar melhor na busca de imagens capturadas no checklist para serem exibidas junto com os itens de o.s';

--INSERE AS MIDIAS JA EXISTENTES
with codigo_item_midia as (
    select cosia.cod_item_ordem_servico as codigo_item,
           crma.codigo                  as codigo_midia
    from checklist_ordem_servico_itens_apontamentos cosia
             inner join checklist_respostas_midias_alternativas_nok crma
                        on crma.cod_alternativa = cosia.cod_alternativa
                            and crma.cod_checklist = cosia.cod_checklist_realizado
)

insert
into checklist_ordem_servico_itens_midia (cod_item_os, cod_midia_nok)
        (select codigo_item, codigo_midia from codigo_item_midia)
on conflict on constraint unique_cod_midia_nok_checklist_ordem_servico_itens_midia do nothing;
------------------------------------------------------------------------------


create or replace function func_checklist_insert_midia_alternativa(f_uuid_midia uuid,
                                                                   f_cod_checklist bigint,
                                                                   f_cod_alternativa bigint,
                                                                   f_url_midia text)
    returns void
    language plpgsql
as
$$
declare
    v_cod_item_os bigint;
    v_cod_midia   bigint;
begin
    insert into checklist_respostas_midias_alternativas_nok(uuid,
                                                            cod_checklist,
                                                            cod_alternativa,
                                                            url_midia,
                                                            tipo_midia)
    values (f_uuid_midia,
            f_cod_checklist,
            f_cod_alternativa,
            trim(f_url_midia),
            'IMAGEM')
    on conflict on constraint unique_uuid_checklist_respostas_midias_alternativa_nok do nothing
    returning codigo into v_cod_midia;

    select cod_item_ordem_servico
    from checklist_ordem_servico_itens_apontamentos
    where cod_checklist_realizado = f_cod_checklist
      and cod_alternativa = f_cod_alternativa
    into v_cod_item_os;


    if v_cod_item_os is not null then
        insert into checklist_ordem_servico_itens_midia (cod_item_os,
                                                         cod_midia_nok)
        values (v_cod_item_os,
                v_cod_midia)
        on conflict on constraint unique_cod_midia_nok_checklist_ordem_servico_itens_midia do nothing;
    end if;
end;
$$;


drop function if exists func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_data_hora_atual_utc timestamp with time zone);
create or replace function func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
                                                                         f_cod_os bigint,
                                                                         f_data_hora_atual_utc timestamp with time zone)
    returns table
            (
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_os                        bigint,
                status_os                             text,
                data_hora_abertura_os                 timestamp without time zone,
                data_hora_fechamento_os               timestamp without time zone,
                cod_item_os                           bigint,
                cod_unidade_item_os                   bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        select c.placa_veiculo::text                                                  as placa_veiculo,
               v.km                                                                   as km_atual_veiculo,
               cos.codigo                                                             as cod_os,
               cos.cod_unidade                                                        as cod_unidade_os,
               cos.status::text                                                       as status_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_abertura_os,
               cos.data_hora_fechamento at time zone tz_unidade(f_cod_unidade)        as data_hora_fechamento_os,
               cosi.codigo                                                            as cod_item_os,
               cos.cod_unidade                                                        as cod_unidade_item_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_primeiro_apontamento_item,
               cosi.status_resolucao                                                  as status_item_os,
               prio.prazo                                                             as prazo_resolucao_item_horas,
               to_minutes_trunc(
                           (c.data_hora + (prio.prazo || ' HOURS')::interval)
                           -
                           f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
               cosi.qt_apontamentos                                                   as qtd_apontamentos,
               co.codigo                                                              as cod_colaborador_resolucao,
               co.nome::text                                                          as nome_colaborador_resolucao,
               cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
               cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
               cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
               cosi.feedback_conserto                                                 as feedback_resolucao,
               millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
               cosi.km                                                                as km_veiculo_coletado_resolucao,
               cp.codigo                                                              as cod_pergunta,
               cp.pergunta                                                            as descricao_pergunta,
               cap.codigo                                                             as cod_alternativa,
               cap.alternativa                                                        as descricao_alternativa,
               cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
               case
                   when cap.alternativa_tipo_outros
                       then
                       (select crn.resposta_outros
                        from checklist_respostas_nok crn
                        where crn.cod_checklist = c.codigo
                          and crn.cod_alternativa = cap.codigo)::text
                   end                                                                as descricao_tipo_outros,
               cap.prioridade::text                                                   as prioridade_alternativa,
               an.url_midia::text                                                     as url_midia,
               an.cod_checklist::bigint                                               as cod_checklist
        from checklist c
                 join checklist_ordem_servico cos
                      on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os
                          and cos.cod_unidade = cosi.cod_unidade
            -- O join com perguntas e alternativas é feito com a tabela _DATA pois OSs de perguntas e alternativas
            -- deletadas ainda devem ser exibidas.
                 join checklist_perguntas_data cp
                      on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                 join checklist_alternativa_pergunta_data cap
                      on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                 join checklist_alternativa_prioridade prio
                      on cap.prioridade = prio.prioridade
                 join veiculo v
                      on c.placa_veiculo = v.placa
                 left join colaborador co
                           on co.cpf = cosi.cpf_mecanico
                 left join checklist_ordem_servico_itens_midia im
                           on im.cod_item_os = cosi.codigo
                 left join checklist_respostas_midias_alternativas_nok an
                           on im.cod_midia_nok = an.codigo
        where cos.codigo = f_cod_os
          and cos.cod_unidade = f_cod_unidade
        order by cosi.codigo;
end;
$$;


DROP FUNCTION IF EXISTS FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(F_COD_UNIDADE BIGINT,
    F_COD_OS BIGINT,
    F_PLACA_VEICULO TEXT,
    F_PRIORIDADE_ALTERNATIVA TEXT,
    F_STATUS_ITENS TEXT,
    F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
    F_LIMIT INTEGER,
    F_OFFSET INTEGER);
create or replace function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
                                                                 f_cod_os bigint,
                                                                 f_placa_veiculo text,
                                                                 f_prioridade_alternativa text,
                                                                 f_status_itens text,
                                                                 f_data_hora_atual_utc timestamp with time zone,
                                                                 f_limit integer,
                                                                 f_offset integer)
    returns table
            (
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_item_os                   bigint,
                cod_item_os                           bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        with dados as (
            select c.placa_veiculo::text                                                  as placa_veiculo,
                   v.km                                                                   as km_atual_veiculo,
                   cos.codigo                                                             as cod_os,
                   cos.cod_unidade                                                        as cod_unidade_item_os,
                   cosi.codigo                                                            as cod_item_os,
                   c.data_hora at time zone tz_unidade(c.cod_unidade)                     as data_hora_primeiro_apontamento_item,
                   cosi.status_resolucao                                                  as status_item_os,
                   prio.prazo                                                             as prazo_resolucao_item_horas,
                   to_minutes_trunc(
                               (c.data_hora + (prio.prazo || ' HOURS')::interval)
                               -
                               f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
                   cosi.qt_apontamentos                                                   as qtd_apontamentos,
                   co.codigo                                                              as cod_colaborador_resolucao,
                   co.nome::text                                                          as nome_colaborador_resolucao,
                   cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
                   cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
                   cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
                   cosi.feedback_conserto                                                 as feedback_resolucao,
                   millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
                   cosi.km                                                                as km_veiculo_coletado_resolucao,
                   cp.codigo                                                              as cod_pergunta,
                   cp.pergunta                                                            as descricao_pergunta,
                   cap.codigo                                                             as cod_alternativa,
                   cap.alternativa                                                        as descricao_alternativa,
                   cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
                   case
                       when cap.alternativa_tipo_outros
                           then
                           (select crn.resposta_outros
                            from checklist_respostas_nok crn
                            where crn.cod_checklist = c.codigo
                              and crn.cod_alternativa = cap.codigo)::text
                       end                                                                as descricao_tipo_outros,
                   cap.prioridade::text                                                   as prioridade_alternativa,
                   an.url_midia::text                                                     as url_midia,
                   an.cod_checklist::bigint                                               as cod_checklist
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
                     join checklist_perguntas cp
                          on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist_alternativa_prioridade prio
                          on cap.prioridade = prio.prioridade
                     join veiculo v
                          on c.placa_veiculo = v.placa
                     left join colaborador co
                               on co.cpf = cosi.cpf_mecanico
                     left join checklist_ordem_servico_itens_midia im
                               on im.cod_item_os = cosi.codigo
                     left join checklist_respostas_midias_alternativas_nok an
                               on im.cod_midia_nok = an.codigo
            where f_if(f_cod_unidade is null, true, cos.cod_unidade = f_cod_unidade)
              and f_if(f_cod_os is null, true, cos.codigo = f_cod_os)
              and f_if(f_placa_veiculo is null, true, c.placa_veiculo = f_placa_veiculo)
              and f_if(f_prioridade_alternativa is null, true, cap.prioridade = f_prioridade_alternativa)
              and f_if(f_status_itens is null, true, cosi.status_resolucao = f_status_itens)
            limit f_limit
            offset
            f_offset
        ),
             dados_veiculo as (
                 select v.placa::text as placa_veiculo,
                        v.km          as km_atual_veiculo
                 from veiculo v
                 where v.placa = f_placa_veiculo
             )

             -- Nós usamos esse dados_veiculo com f_if pois pode acontecer de não existir dados para os filtros aplicados e
             -- desse modo acabaríamos não retornando placa e km também, mas essas são informações necessárias pois o objeto
             -- construído a partir dessa function usa elas.
        select f_if(d.placa_veiculo is null, dv.placa_veiculo, d.placa_veiculo)          as placa_veiculo,
               f_if(d.km_atual_veiculo is null, dv.km_atual_veiculo, d.km_atual_veiculo) as km_atual_veiculo,
               d.cod_os                                                                  as cod_os,
               d.cod_unidade_item_os                                                     as cod_unidade_item_os,
               d.cod_item_os                                                             as cod_item_os,
               d.data_hora_primeiro_apontamento_item                                     as data_hora_primeiro_apontamento_item,
               d.status_item_os                                                          as status_item_os,
               d.prazo_resolucao_item_horas                                              as prazo_resolucao_item_horas,
               d.prazo_restante_resolucao_item_minutos                                   as prazo_restante_resolucao_item_minutos,
               d.qtd_apontamentos                                                        as qtd_apontamentos,
               d.cod_colaborador_resolucao                                               as cod_colaborador_resolucao,
               d.nome_colaborador_resolucao                                              as nome_colaborador_resolucao,
               d.data_hora_resolucao                                                     as data_hora_resolucao,
               d.data_hora_inicio_resolucao                                              as data_hora_inicio_resolucao,
               d.data_hora_fim_resolucao                                                 as data_hora_fim_resolucao,
               d.feedback_resolucao                                                      as feedback_resolucao,
               d.duracao_resolucao_minutos                                               as duracao_resolucao_minutos,
               d.km_veiculo_coletado_resolucao                                           as km_veiculo_coletado_resolucao,
               d.cod_pergunta                                                            as cod_pergunta,
               d.descricao_pergunta                                                      as descricao_pergunta,
               d.cod_alternativa                                                         as cod_alternativa,
               d.descricao_alternativa                                                   as descricao_alternativa,
               d.alternativa_tipo_outros                                                 as alternativa_tipo_outros,
               d.descricao_tipo_outros                                                   as descricao_tipo_outros,
               d.prioridade_alternativa                                                  as prioridade_alternativa,
               d.url_midia                                                               as url_midia,
               d.cod_checklist                                                           as cod_checklist
        from dados d
                 right join dados_veiculo dv
                            on d.placa_veiculo = dv.placa_veiculo
        order by cod_os, cod_item_os, cod_checklist;
end;
$$;