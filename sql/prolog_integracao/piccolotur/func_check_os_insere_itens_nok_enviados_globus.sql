-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para inserir os itens NOK de um checklist que será enviado
-- para o Globus.
--
-- Histórico:
-- 2020-06-12 -> Function criada (diogenesvanzella - PLI-137).
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