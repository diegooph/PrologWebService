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


    if v_cod_item_os is not null
        and v_cod_midia is not null then
        insert into checklist_ordem_servico_itens_midia (cod_item_os,
                                                         cod_midia_nok)
        values (v_cod_item_os,
                v_cod_midia)
        on conflict on constraint unique_cod_midia_nok_checklist_ordem_servico_itens_midia do nothing;
    end if;
end;
$$;