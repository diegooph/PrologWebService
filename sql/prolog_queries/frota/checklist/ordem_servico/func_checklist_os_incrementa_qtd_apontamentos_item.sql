-- Sobre:
--
-- Function utilizada para incrementar a quantidade de apontamentos de um item específico de Ordem de Serviço. A
-- function, além de incrementar a quantidade de apontamento, também salva qual foi a alteranativa/checklist que gerou
-- a nova contagem de apontamentos.
--
-- Histórico:
-- 2019-12-11 -> Function criada (diogenesvanzella - PL-2416).
-- 2020-07-14 -> Adiciona verificação para evitar reprocessamento de item já existente (luiz_fp).
create or replace function func_checklist_os_incrementa_qtd_apontamentos_item(f_cod_item_ordem_servico bigint,
                                                                              f_cod_checklist_realizado bigint,
                                                                              f_cod_alternativa bigint,
                                                                              f_status_resolucao text)
    returns void
    language plpgsql
as
$$
declare
    v_nova_qtd_apontamentos_item integer;
begin
    if (select not exists(select codigo
                          from checklist_ordem_servico_itens_apontamentos
                          where cod_checklist_realizado = f_cod_checklist_realizado
                            and cod_alternativa = f_cod_alternativa))
    then
        -- Atualiza quantidade de apontamentos do item.
        update checklist_ordem_servico_itens
        set qt_apontamentos = qt_apontamentos + 1
        where codigo = f_cod_item_ordem_servico
          and status_resolucao = f_status_resolucao
        returning qt_apontamentos into v_nova_qtd_apontamentos_item;

        -- Insere a alternativa que incrementou a quantidade de apontamentos na tabela.
        insert into checklist_ordem_servico_itens_apontamentos (cod_item_ordem_servico,
                                                                cod_checklist_realizado,
                                                                cod_alternativa,
                                                                nova_qtd_apontamentos)
        values (f_cod_item_ordem_servico, f_cod_checklist_realizado, f_cod_alternativa, v_nova_qtd_apontamentos_item);
    end if;
end;
$$;