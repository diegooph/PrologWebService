-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para realizar a inserção de Itens de Ordem de Serviço no ProLog.
--
-- Essa function possui duas particularidades que divergem do processo padrão (processo do ProLog) para abertura de
-- Ordem de Serviço, são elas:
-- 1) Diferente do padrão no restante das functions, essa insere também o código que a O.S terá, esse código é o
-- mesmo que a O.S tem no sistema integrado (Globus).
-- 2) Essa function permite adicionar itens a uma O.S já aberta, desde de que todos os vínculos sejam cumpridos.
-- 3) Caso o item já exista na OS, não incrementamos a quantidade de apontamentos, pois, esse incremento é feito via
-- JAVA no momentos que os itens NOK são enviados para o Globus.
--
-- Histórico:
-- 2019-08-07 -> Function criada (diogenesvanzella - PL-2019).
-- 2019-12-10 -> Altera function para utilizar a nova estrutura de checklist (diogenesvanzella - PL-2416).
-- 2020-01-14 -> Restrutura function para abrir OS com base no código de contexto (diogenesvanzella - PL-2416).
-- 2020-01-21 -> Reabre OS para inserir um novo item, caso ela já esteja fechada (diogenesvanzella - PLI-66).
-- 2020-03-03 -> Remove verificação de código de Serviço Globus (diogenesvanzella - PLI-92).
-- 2020-04-08 -> Permite a validação com tokens duplicados (diogenesvanzella - PLI-117).
-- 2020-06-12 -> Usa o cod_checklist para ver se existe uma OS aberta (diogenesvanzella - PLI-140).
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