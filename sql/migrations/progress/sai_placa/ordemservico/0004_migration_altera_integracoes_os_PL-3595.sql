create or replace function integracao.func_checklist_os_busca_informacoes_os(f_cod_interno_os_prolog bigint[],
                                                                             f_status_os text default null)
    returns table
            (
                cod_unidade                  bigint,
                cod_auxiliar_unidade         text,
                cod_interno_os_prolog        bigint,
                cod_os_prolog                bigint,
                data_hora_abertura_os        timestamp without time zone,
                placa_veiculo                text,
                km_veiculo_na_abertura       bigint,
                cpf_colaborador_checklist    text,
                status_os                    text,
                data_hora_fechamento_os      timestamp without time zone,
                cod_item_os                  bigint,
                cod_alternativa              bigint,
                cod_auxiliar_alternativa     text,
                descricao_alternativa        text,
                alternativa_tipo_outros      boolean,
                descricao_tipo_outros        text,
                prioridade_alternativa       text,
                status_item_os               text,
                km_veiculo_fechamento_item   bigint,
                data_hora_fechamento_item_os timestamp without time zone,
                data_hora_inicio_resolucao   timestamp without time zone,
                data_hora_fim_resolucao      timestamp without time zone,
                descricao_fechamento_item_os text
            )
    language plpgsql
as
$$
begin
    return query
        select cos.cod_unidade                                                   as cod_unidade,
               u.cod_auxiliar                                                    as cod_auxiliar_unidade,
               cos.codigo_prolog                                                 as cod_interno_os_prolog,
               cos.codigo                                                        as cod_os_prolog,
               c.data_hora_realizacao_tz_aplicado                                as data_hora_abertura_os,
               v.placa::text                                                     as placa_veiculo,
               c.km_veiculo                                                      as km_veiculo_na_abertura,
               lpad(c.cpf_colaborador::text, 11, '0')                            as cpf_colaborador_checklist,
               cos.status::text                                                  as status_os,
               cos.data_hora_fechamento at time zone tz_unidade(u.codigo)        as data_hora_fechamento_os,
               cosi.codigo                                                       as cod_item_os,
               cosi.cod_alternativa_primeiro_apontamento                         as cod_alternativa,
               cap.cod_auxiliar                                                  as cod_auxiliar_alternativa,
               cap.alternativa                                                   as descricao_alternativa,
               cap.alternativa_tipo_outros                                       as alternativa_tipo_outros,
               case
                   when cap.alternativa_tipo_outros
                       then
                       (select crn.resposta_outros
                        from checklist_respostas_nok crn
                        where crn.cod_checklist = c.codigo
                          and crn.cod_alternativa = cap.codigo)::text
                   end                                                           as descricao_tipo_outros,
               cap.prioridade::text                                              as prioridade_alternativa,
               cosi.status_resolucao                                             as status_item_os,
               cosi.km                                                           as km_veiculo_fechamento_item,
               cosi.data_hora_conserto at time zone tz_unidade(u.codigo)         as data_hora_fechamento_item_os,
               cosi.data_hora_inicio_resolucao at time zone tz_unidade(u.codigo) as data_hora_inicio_resolucao,
               cosi.data_hora_fim_resolucao at time zone tz_unidade(u.codigo)    as data_hora_fim_resolucao,
               cosi.feedback_conserto                                            as descricao_fechamento_item_os
        from checklist_ordem_servico cos
                 join checklist c on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join unidade u on u.codigo = cos.cod_unidade
                 join veiculo v on v.codigo = c.cod_veiculo
        where cos.codigo_prolog = any (f_cod_interno_os_prolog)
          and f_if(f_status_os is null, true, cos.status = f_status_os)
        order by cos.codigo_prolog, cosi.codigo;
end;
$$;

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
            (select v.placa
             from public.checklist c
                      join veiculo v on c.cod_veiculo = v.codigo
             where c.codigo = f_cod_checklist),
            f_cod_checklist,
            f_cod_contexto_pergunta_checklist,
            f_cod_contexto_alternativa_checklist,
            f_data_hora_sincronizacao_pendencia);

    return v_cod_item_os_prolog;
end;
$$;


drop function piccolotur.func_check_os_busca_checklist_itens_nok(f_cod_checklist_prolog bigint);
create or replace function piccolotur.func_check_os_busca_checklist_itens_nok(f_cod_checklist_prolog bigint)
    returns table
            (
                cod_unidade_checklist        bigint,
                cod_modelo_checklist         bigint,
                cod_versao_modelo_checklist  bigint,
                cpf_colaborador_realizacao   text,
                placa_veiculo_checklist      text,
                cod_veiculo_checklist        bigint,
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
       v.placa::text                                            as placa_veiculo_checklist,
       v.codigo                                                 as cod_veiculo_checklist,
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
         join veiculo v on v.codigo = c.cod_veiculo
    -- Usamos LEFT JOIN para os cenários onde o check não possuir nenhum item NOK, mesmo para esses cenários
    -- devemos retornar as infos do checklist mesmo assim.
         left join alternativas a on a.cod_checklist = c.codigo
where c.codigo = f_cod_checklist_prolog
order by a.cod_alternativa;
$$;


CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO BIGINT,
                                                      F_TOKEN_INTEGRACAO TEXT)
    RETURNS TABLE
            (
                PLACA_VEICULO                      TEXT,
                KM_ABERTURA_SERVICO                BIGINT,
                COD_ORDEM_SERVICO                  BIGINT,
                COD_UNIDADE_ORDEM_SERVICO          BIGINT,
                STATUS_ORDEM_SERVICO               TEXT,
                DATA_HORA_ABERTURA_SERVICO         TIMESTAMP WITHOUT TIME ZONE,
                COD_ITEM_ORDEM_SERVICO             BIGINT,
                COD_UNIDADE_ITEM_ORDEM_SERVICO     BIGINT,
                DATA_HORA_PRIMEIRO_APONTAMENTO     TIMESTAMP WITHOUT TIME ZONE,
                STATUS_ITEM_ORDEM_SERVICO          TEXT,
                PRAZO_RESOLUCAO_ITEM_HORAS         INTEGER,
                QTD_APONTAMENTOS                   INTEGER,
                COD_CHECKLIST_PRIMEIRO_APONTAMENTO BIGINT,
                COD_CONTEXTO_PERGUNTA              BIGINT,
                DESCRICAO_PERGUNTA                 TEXT,
                COD_CONTEXTO_ALTERNATIVA           BIGINT,
                DESCRICAO_ALTERNATIVA              TEXT,
                IS_TIPO_OUTROS                     BOOLEAN,
                DESCRICAO_TIPO_OUTROS              TEXT,
                PRIORIDADE_ALTERNATIVA             TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE TEXT := 'P';
BEGIN
    RETURN QUERY
        SELECT VD.PLACA::TEXT                                       AS PLACA_VEICULO,
               CD.KM_VEICULO                                        AS KM_ABERTURA_SERVICO,
               COSD.CODIGO                                          AS COD_ORDEM_SERVICO,
               COSD.COD_UNIDADE                                     AS COD_UNIDADE_ORDEM_SERVICO,
               COSD.STATUS::TEXT                                    AS STATUS_ORDEM_SERVICO,
               CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_ABERTURA_SERVICO,
               COSID.CODIGO                                         AS COD_ITEM_ORDEM_SERVICO,
               COSID.COD_UNIDADE                                    AS COD_UNIDADE_ITEM_ORDEM_SERVICO,
               CD.DATA_HORA AT TIME ZONE TZ_UNIDADE(CD.COD_UNIDADE) AS DATA_HORA_PRIMEIRO_APONTAMENTO,
               COSID.STATUS_RESOLUCAO::TEXT                         AS STATUS_ITEM_ORDEM_SERVICO,
               CAP.PRAZO                                            AS PRAZO_RESOLUCAO_ITEM_HORAS,
               COSID.QT_APONTAMENTOS                                AS QTD_APONTAMENTOS,
               CD.CODIGO                                            AS COD_CHECKLIST_PRIMEIRO_APONTAMENTO,
               COSID.COD_CONTEXTO_PERGUNTA                          AS COD_CONTEXTO_PERGUNTA,
               CPD.PERGUNTA                                         AS DESCRICAO_PERGUNTA,
               COSID.COD_CONTEXTO_ALTERNATIVA                       AS COD_CONTEXTO_ALTERNATIVA,
               CAPD.ALTERNATIVA                                     AS DESCRICAO_ALTERNATIVA,
               CAPD.ALTERNATIVA_TIPO_OUTROS                         AS IS_TIPO_OUTROS,
               CASE
                   WHEN CAPD.ALTERNATIVA_TIPO_OUTROS
                       THEN
                       (SELECT CRN.RESPOSTA_OUTROS
                        FROM CHECKLIST_RESPOSTAS_NOK CRN
                        WHERE CRN.COD_CHECKLIST = CD.CODIGO
                          AND CRN.COD_ALTERNATIVA = CAPD.CODIGO)
                   END                                              AS DESCRICAO_TIPO_OUTROS,
               CAPD.PRIORIDADE::TEXT                                AS PRIORIDADE_ALTERNATIVA
        FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                 JOIN CHECKLIST_ORDEM_SERVICO_DATA COSD
                      ON COSID.COD_OS = COSD.CODIGO AND COSID.COD_UNIDADE = COSD.COD_UNIDADE
                 JOIN CHECKLIST_DATA CD ON COSD.COD_CHECKLIST = CD.CODIGO
                 JOIN VEICULO_DATA VD ON CD.COD_VEICULO = VD.CODIGO
                 JOIN CHECKLIST_PERGUNTAS_DATA CPD ON COSID.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CPD.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
                      ON COSID.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAPD.CODIGO
                 JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE CAP ON CAPD.PRIORIDADE = CAP.PRIORIDADE
        WHERE COSID.COD_UNIDADE IN (SELECT U.CODIGO
                                    FROM UNIDADE U
                                    WHERE U.COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
          AND COSID.STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE
          AND COSID.CODIGO > F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO
        ORDER BY COSID.CODIGO;
END;
$$;