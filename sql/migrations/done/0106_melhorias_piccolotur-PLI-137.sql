-- ################################## PLI-137 #####################################
-- Dropa constraints erradas
alter table piccolotur.checklist_item_nok_enviado_globus
    drop constraint if exists fk_item_enviado_pergunta_checklist;

alter table piccolotur.checklist_item_nok_enviado_globus
    drop constraint if exists fk_item_enviado_alternativa_checklist;

-- Adiciona colunas para armazenar os códigos das perguntas e alternativas
alter table piccolotur.checklist_item_nok_enviado_globus
    add column cod_pergunta bigint;

alter table piccolotur.checklist_item_nok_enviado_globus
    add column cod_alternativa bigint;

-- Adiciona dados para os itens já sincronizados
-- Usamos as tabelas '_data' pois alguns checklists podem ter sido deletados lógicamente.
with dados as (
    select c.codigo                      as cod_checklist,
           c.cod_checklist_modelo        as cod_modelo_checklist,
           c.cod_versao_checklist_modelo as cod_versao_modelo,
           cp.codigo                     as cod_pergunta,
           cp.codigo_contexto            as cod_contexto_pergunta,
           cap.codigo                    as cod_alternativa,
           cap.codigo_contexto           as cod_contexto_alternativa
    from piccolotur.checklist_item_nok_enviado_globus cineg
             join checklist_data c
                  on c.codigo = cineg.cod_checklist
             join checklist_perguntas_data cp
                  on cp.codigo_contexto = cineg.cod_contexto_pergunta
                      and cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
             join checklist_alternativa_pergunta_data cap
                  on cap.cod_pergunta = cp.codigo
                      and cap.codigo_contexto = cineg.cod_contexto_alternativa
                      and cap.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
)

update piccolotur.checklist_item_nok_enviado_globus cineg
set cod_pergunta    = d.cod_pergunta,
    cod_alternativa = d.cod_alternativa
from dados d
where d.cod_checklist = cineg.cod_checklist
  and d.cod_contexto_pergunta = cineg.cod_contexto_pergunta
  and d.cod_contexto_alternativa = cineg.cod_contexto_alternativa;
-- Setamos as colunas como not null
alter table piccolotur.checklist_item_nok_enviado_globus
    alter column cod_pergunta set not null;

alter table piccolotur.checklist_item_nok_enviado_globus
    alter column cod_alternativa set not null;

-- Adicionamos novamente as constraints nos códigos
-- Referenciamos as tabelas '_data' pois um dado antigo pode estar sendo sincronizado.
alter table piccolotur.checklist_item_nok_enviado_globus
    add constraint fk_item_enviado_pergunta_checklist
        foreign key (cod_pergunta, cod_contexto_pergunta)
            references checklist_perguntas_data (codigo, codigo_contexto);
alter table piccolotur.checklist_item_nok_enviado_globus
    add constraint fk_item_enviado_alternativa_checklist
        foreign key (cod_alternativa, cod_contexto_alternativa)
            references checklist_alternativa_pergunta_data (codigo, codigo_contexto);

-- Criamos functions para inserir itens nok enviados ao globus
create or replace function
    piccolotur.func_check_os_insere_itens_nok_enviados_globus(f_cod_unidade bigint,
                                                              f_placa_veiculo text,
                                                              f_cpf_colaborador bigint,
                                                              f_cod_checklist_realizado bigint,
                                                              f_cod_pergunta bigint,
                                                              f_cod_contexto_pergunta bigint,
                                                              f_cod_alternativa bigint,
                                                              f_cod_contexto_alternativa bigint,
                                                              f_data_hora_envio timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    insert into piccolotur.checklist_item_nok_enviado_globus (cod_unidade,
                                                              placa_veiculo_os,
                                                              cpf_colaborador,
                                                              cod_checklist,
                                                              cod_pergunta,
                                                              cod_contexto_pergunta,
                                                              cod_alternativa,
                                                              cod_contexto_alternativa,
                                                              data_hora_envio)
    values (f_cod_unidade,
            f_placa_veiculo,
            f_cpf_colaborador,
            f_cod_checklist_realizado,
            f_cod_pergunta,
            f_cod_contexto_pergunta,
            f_cod_alternativa,
            f_cod_contexto_alternativa,
            f_data_hora_envio);

    if not found then
        raise exception 'Não foi possível inserir os itens NOK';
    end if;
end;
$$;

-- Atualizamos a function para prover o código da pergunta para ser inserido no banco
drop function piccolotur.func_check_os_busca_checklist_itens_nok(f_cod_checklist_prolog bigint);
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

-- ################################## PLI-140 #####################################
-- Verificamos se o item sendo inserido é de uma OS que já está aberta (validamos usando o codigo do checklist).
create or replace function
    piccolotur.func_check_os_insere_item_os_aberta(f_cod_os_globus bigint,
                                                   f_cod_unidade_os bigint,
                                                   f_cod_checklist bigint,
                                                   f_cod_item_os_globus bigint,
                                                   f_cod_contexto_pergunta_checklist bigint,
                                                   f_cod_contexto_alternativa_checklist bigint,
                                                   f_data_hora_sincronizacao_pendencia timestamp with time zone,
                                                   f_token_integracao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_status_os_aberta        constant text   := 'A';
    v_status_item_os_pendente constant text   := 'P';
    v_codigo_pergunta         constant bigint := (select cp.codigo
                                                  from checklist_perguntas cp
                                                  where cp.codigo_contexto = f_cod_contexto_pergunta_checklist
                                                    and cp.cod_versao_checklist_modelo =
                                                        (select c.cod_versao_checklist_modelo
                                                         from checklist c
                                                         where c.codigo = f_cod_checklist));
    v_codigo_alternativa      constant bigint := (select cap.codigo
                                                  from checklist_alternativa_pergunta cap
                                                  where cap.codigo_contexto = f_cod_contexto_alternativa_checklist
                                                    and cap.cod_pergunta = v_codigo_pergunta
                                                    and cap.cod_versao_checklist_modelo =
                                                        (select c.cod_versao_checklist_modelo
                                                         from checklist c
                                                         where c.codigo = f_cod_checklist));
    v_cod_empresa_os          constant bigint := (select ti.cod_empresa
                                                  from integracao.token_integracao ti
                                                           join unidade u on u.cod_empresa = ti.cod_empresa
                                                  where u.codigo = f_cod_unidade_os);
    v_cod_item_os_prolog               bigint;
begin
    -- Antes de processarmos a abertura da O.S e inserção de Itens, validamos todos os códigos de vínculo.
    -- Validamos se o código da unidade da O.S bate com a empresa do Token
    if (v_cod_empresa_os is null)
    then
        perform public.throw_generic_error(
                format('[ERRO DE VÍNCULO] O token "%s" não está autorizado para a unidade "%s"',
                       f_token_integracao,
                       f_cod_unidade_os));
    end if;

    -- Validamos se o código do checklist existe.
    if (select not exists(
            select c.codigo
            from public.checklist c
            where c.codigo = f_cod_checklist
              and c.cod_unidade = f_cod_unidade_os))
    then
        perform public.throw_generic_error(
                format('[ERRO DE VÍNCULO] O checklist "%s" não encontra-se na base de dados do ProLog',
                       f_cod_checklist));
    end if;

    -- Validamos se a pergunta existe e está mesmo vinculada ao checklist realizado.
    if (select not exists(
            select crn.cod_pergunta
            from public.checklist_respostas_nok crn
            where crn.cod_checklist = f_cod_checklist
              and crn.cod_pergunta = v_codigo_pergunta))
    then
        perform public.throw_generic_error(
                format('[ERRO DE VÍNCULO] A pergunta "%s" não possui vínculo com o checklist "%s"',
                       f_cod_contexto_pergunta_checklist,
                       f_cod_checklist));
    end if;

    -- Validamos se a alternativa existe e pertence a pergunta do checklist realizado.
    if (select not exists(
            select crn.cod_alternativa
            from public.checklist_respostas_nok crn
            where crn.cod_checklist = f_cod_checklist
              and crn.cod_pergunta = v_codigo_pergunta
              and crn.cod_alternativa = v_codigo_alternativa))
    then
        perform public.throw_generic_error(
                format('[ERRO DE VÍNCULO] A alternativa "%s" não possui vínculo com a pergunta "%s"',
                       f_cod_contexto_alternativa_checklist,
                       f_cod_contexto_pergunta_checklist));
    end if;

    -- Validamos se o Item da O.S pertencem a um checklist que de fato foi enviado para o Globus.
    if (not (select exists(
                            select *
                            from piccolotur.checklist_item_nok_enviado_globus cineg
                            where cineg.cod_checklist = f_cod_checklist
                              and cineg.cod_contexto_pergunta = f_cod_contexto_pergunta_checklist
                              and cineg.cod_contexto_alternativa = f_cod_contexto_alternativa_checklist)))
    then
        perform public.throw_generic_error(
                format(
                            '[ERRO DE VÍNCULO] Não existe vínculo entre o cod_checklist "%s",' ||
                            ' cod_pergunta "%s" e cod_alternativa "%s"',
                            f_cod_checklist,
                            f_cod_contexto_pergunta_checklist,
                            f_cod_contexto_alternativa_checklist));
    end if;

    -- Validamos se estamos tentando abrir uma OS que já existe para um checklist diferente.
    if (select exists(select cos.codigo
                      from checklist_ordem_servico cos
                      where cos.codigo = f_cod_os_globus
                        and cos.cod_unidade = f_cod_unidade_os
                        and cos.cod_checklist != f_cod_checklist))
    then
        perform public.throw_generic_error(
                format('[ERRO] A OS %s já está aberta para outro checklist'));
    end if;

    -- Se chegou nesse estágio, já validamos todos os cenários do item, devemos então inserir.
    -- Se a Ordem de Serviço não existe, então criamos ela.
    if (select not exists(
            select cos.codigo
            from public.checklist_ordem_servico cos
            where cos.codigo = f_cod_os_globus
              and cos.cod_unidade = f_cod_unidade_os
              and cos.cod_checklist = f_cod_checklist))
    then
        insert into public.checklist_ordem_servico(codigo,
                                                   cod_unidade,
                                                   cod_checklist,
                                                   status)
        values (f_cod_os_globus, f_cod_unidade_os, f_cod_checklist, v_status_os_aberta);
    else
        -- Caso a OS estiver fechada, iremos reabrir para inserir o novo item.
        -- Se estiver aberta, iremos apenas adicionar o item nela.
        update public.checklist_ordem_servico
        set status               = v_status_os_aberta,
            data_hora_fechamento = null
        where codigo = f_cod_os_globus
          and cod_unidade = f_cod_unidade_os;
    end if;

    -- Não precisamos validar novamente se o item já existe no banco de dados, apenas inserimos.
    insert into public.checklist_ordem_servico_itens(cod_unidade,
                                                     cod_os,
                                                     status_resolucao,
                                                     cod_contexto_pergunta,
                                                     cod_contexto_alternativa,
                                                     cod_pergunta_primeiro_apontamento,
                                                     cod_alternativa_primeiro_apontamento)
    values (f_cod_unidade_os,
            f_cod_os_globus,
            v_status_item_os_pendente,
            f_cod_contexto_pergunta_checklist,
            f_cod_contexto_alternativa_checklist,
            v_codigo_pergunta,
            v_codigo_alternativa)
    returning codigo into v_cod_item_os_prolog;

    -- Não chegará nesse ponto um 'item', 'checklist' ou 'alternativa' que não existam, então podemos inserir os
    -- dados com segurança. Também, não chegará aqui um item que não deveremos inserir ou que devemos aumentar a
    -- quantidade de apontamentos, nesse estágio o item SEMPRE tera 'NOVA_QTD_APONTAMENTOS' = 1 (primeiro apontamento).
    insert into public.checklist_ordem_servico_itens_apontamentos(cod_item_ordem_servico,
                                                                  cod_checklist_realizado,
                                                                  cod_alternativa,
                                                                  nova_qtd_apontamentos)
    values (v_cod_item_os_prolog, f_cod_checklist, v_codigo_alternativa, 1);

    -- Após salvar o item, criamos o vínculo dele na tabela DE-PARA.
    insert into piccolotur.checklist_ordem_servico_item_vinculo(cod_unidade,
                                                                cod_os_globus,
                                                                cod_item_os_globus,
                                                                cod_item_os_prolog,
                                                                placa_veiculo_os,
                                                                cod_checklist_os_prolog,
                                                                cod_contexto_pergunta_os_prolog,
                                                                cod_contexto_alternativa_os_prolog,
                                                                data_hora_sincronia_pendencia)
    values (f_cod_unidade_os,
            f_cod_os_globus,
            f_cod_item_os_globus,
            v_cod_item_os_prolog,
            (select c.placa_veiculo from public.checklist c where c.codigo = f_cod_checklist),
            f_cod_checklist,
            f_cod_contexto_pergunta_checklist,
            f_cod_contexto_alternativa_checklist,
            f_data_hora_sincronizacao_pendencia);

    return v_cod_item_os_prolog;
end;
$$;