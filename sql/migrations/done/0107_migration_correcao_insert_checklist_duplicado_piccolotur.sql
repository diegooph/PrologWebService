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