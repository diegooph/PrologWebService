alter table checklist_data
    add column observacao text;


drop function if exists func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
    f_cod_modelo_checklist bigint,
    f_cod_versao_modelo_checklist bigint,
    f_data_hora_realizacao timestamp with time zone,
    f_cod_colaborador bigint,
    f_cod_veiculo bigint,
    f_tipo_checklist char,
    f_km_coletado bigint,
    f_tempo_realizacao bigint,
    f_data_hora_sincronizacao timestamp with time zone,
    f_fonte_data_hora_realizacao text,
    f_versao_app_momento_realizacao integer,
    f_versao_app_momento_sincronizacao integer,
    f_device_id text,
    f_device_imei text,
    f_device_uptime_realizacao_millis bigint,
    f_device_uptime_sincronizacao_millis bigint,
    f_foi_offline boolean,
    f_total_perguntas_ok integer,
    f_total_perguntas_nok integer,
    f_total_alternativas_ok integer,
    f_total_alternativas_nok integer,
    f_total_midias_perguntas_ok integer,
    f_total_midias_alternativas_nok integer);
create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
                                                                 f_tempo_realizacao bigint,
                                                                 f_data_hora_sincronizacao timestamp with time zone,
                                                                 f_fonte_data_hora_realizacao text,
                                                                 f_versao_app_momento_realizacao integer,
                                                                 f_versao_app_momento_sincronizacao integer,
                                                                 f_device_id text,
                                                                 f_device_imei text,
                                                                 f_device_uptime_realizacao_millis bigint,
                                                                 f_device_uptime_sincronizacao_millis bigint,
                                                                 f_foi_offline boolean,
                                                                 f_total_perguntas_ok integer,
                                                                 f_total_perguntas_nok integer,
                                                                 f_total_alternativas_ok integer,
                                                                 f_total_alternativas_nok integer,
                                                                 f_total_midias_perguntas_ok integer,
                                                                 f_total_midias_alternativas_nok integer)
    returns table
            (
                cod_checklist_inserido bigint,
                checklist_ja_existia   boolean
            )
    language plpgsql
as
$$
declare
    -- Iremos atualizar o km do veículo somente para o caso em que o km atual do veículo for menor que o km coletado.
    v_deve_atualizar_km_veiculo boolean := (case
                                                when (f_km_coletado > (select v.km
                                                                       from veiculo v
                                                                       where v.codigo = f_cod_veiculo))
                                                    then
                                                    true
                                                else false end);
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo    text    := (select vd.placa
                                            from veiculo_data vd
                                            where vd.codigo = f_cod_veiculo);
    v_cod_checklist_inserido    bigint;
    v_qtd_linhas_atualizadas    bigint;
    v_checklist_ja_existia      boolean := false;
begin

    insert into checklist_data(cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
                               data_hora_sincronizacao,
                               fonte_data_hora_realizacao,
                               versao_app_momento_realizacao,
                               versao_app_momento_sincronizacao,
                               device_id,
                               device_imei,
                               device_uptime_realizacao_millis,
                               device_uptime_sincronizacao_millis,
                               foi_offline,
                               total_perguntas_ok,
                               total_perguntas_nok,
                               total_alternativas_ok,
                               total_alternativas_nok,
                               total_midias_perguntas_ok,
                               total_midias_alternativas_nok)
    values (f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            f_km_coletado,
            f_observacao,
            f_data_hora_sincronizacao,
            f_fonte_data_hora_realizacao,
            f_versao_app_momento_realizacao,
            f_versao_app_momento_sincronizacao,
            f_device_id,
            f_device_imei,
            f_device_uptime_realizacao_millis,
            f_device_uptime_sincronizacao_millis,
            f_foi_offline,
            f_total_perguntas_ok,
            f_total_perguntas_nok,
            f_total_alternativas_ok,
            f_total_alternativas_nok,
            nullif(f_total_midias_perguntas_ok, 0),
            nullif(f_total_midias_alternativas_nok, 0))
    on conflict on constraint unique_checklist
        do update set data_hora_sincronizacao = f_data_hora_sincronizacao
                      -- https://stackoverflow.com/a/40880200/4744158
    returning codigo, not (checklist_data.xmax = 0) into v_cod_checklist_inserido, v_checklist_ja_existia;

    -- Verificamos se o insert funcionou.
    if v_cod_checklist_inserido <= 0
    then
        raise exception 'Não foi possível inserir o checklist.';
    end if;

    if v_deve_atualizar_km_veiculo
    then
        update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
    end if;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    -- Se devemos atualizar o km mas nenhuma linha foi alterada, então temos um erro.
    if (v_deve_atualizar_km_veiculo and (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0))
    then
        raise exception 'Não foi possível atualizar o km do veículo.';
    end if;

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;