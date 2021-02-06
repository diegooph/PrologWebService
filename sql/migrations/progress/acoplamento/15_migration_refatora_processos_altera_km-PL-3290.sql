---------------- CHECKLIST
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
    v_cod_novo_checklist        bigint;
    v_cod_checklist_inserido    bigint;
    v_qtd_linhas_atualizadas    bigint;
    v_checklist_ja_existia      boolean := false;
    v_km_final                  bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));

    if v_deve_atualizar_km_veiculo
    then
        v_km_final :=
                (select *
                 from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                                   f_cod_veiculo,
                                                   f_km_coletado,
                                                   v_cod_novo_checklist,
                                                   'CHECKLIST',
                                                   true,
                                                   f_data_hora_realizacao));
    end if;

    insert into checklist_data(codigo,
                               cod_unidade,
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
                               total_midias_alternativas_nok,
                               cod_veiculo)
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
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
            nullif(f_total_midias_alternativas_nok, 0),
            f_cod_veiculo)
    on conflict on constraint unique_checklist
        do update set data_hora_sincronizacao = f_data_hora_sincronizacao
                      -- https://stackoverflow.com/a/40880200/4744158
    returning codigo, not (checklist_data.xmax = 0) into v_cod_checklist_inserido, v_checklist_ja_existia;

    -- Verificamos se o insert funcionou.
    if v_cod_checklist_inserido <= 0
    then
        raise exception 'Não foi possível inserir o checklist.';
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


------------------ SOCORRO EM ROTA
create or replace function func_socorro_rota_abertura(f_cod_unidade bigint,
                                                      f_cod_colaborador_abertura bigint,
                                                      f_cod_veiculo_problema bigint,
                                                      f_km_veiculo_abertura bigint,
                                                      f_cod_problema_socorro_rota bigint,
                                                      f_descricao_problema text,
                                                      f_data_hora_abertura timestamp with time zone,
                                                      f_url_foto_1_abertura text,
                                                      f_url_foto_2_abertura text,
                                                      f_url_foto_3_abertura text,
                                                      f_latitude_abertura text,
                                                      f_longitude_abertura text,
                                                      f_precisao_localizacao_abertura_metros numeric,
                                                      f_endereco_automatico text,
                                                      f_ponto_referencia text,
                                                      f_device_id_abertura text,
                                                      f_device_imei_abertura text,
                                                      f_device_uptime_millis_abertura bigint,
                                                      f_android_api_version_abertura integer,
                                                      f_marca_device_abertura text,
                                                      f_modelo_device_abertura text,
                                                      f_plataforma_origem prolog_plataforma_socorro_rota_type,
                                                      f_versao_plataforma_origem text) returns bigint
    language plpgsql
as
$$
declare
    f_cod_socorro_inserido bigint;
    f_cod_empresa          bigint := (select cod_empresa
                                      from unidade
                                      where codigo = f_cod_unidade);
    f_cod_abertura         bigint;
    v_km_final             bigint;
begin
    -- Verifica se a funcionalidade está liberada para a empresa.
    perform func_socorro_rota_empresa_liberada(f_cod_empresa);

    -- Assim conseguimos inserir mantendo a referência circular entre a pai e as filhas.
    set constraints all deferred;

    -- Pega o código de abertura da sequence para poder atualizar a tabela pai.
    f_cod_abertura := (select nextval(pg_get_serial_sequence('socorro_rota_abertura', 'codigo')));


    v_km_final := (select *
                   from func_veiculo_update_km_atual(f_cod_unidade,
                                                     f_cod_veiculo_problema,
                                                     f_km_veiculo_abertura,
                                                     f_cod_abertura,
                                                     'SOCORRO_EM_ROTA',
                                                     true,
                                                     f_data_hora_abertura));

    -- Insere na tabela pai.
    insert into socorro_rota (cod_unidade, status_atual, cod_abertura)
    values (f_cod_unidade, 'ABERTO', f_cod_abertura)
    returning codigo into f_cod_socorro_inserido;

    -- Exibe erro se não puder inserir.
    if f_cod_socorro_inserido is null or f_cod_socorro_inserido <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Insere na tabela de abertura
    insert into socorro_rota_abertura (codigo,
                                       cod_socorro_rota,
                                       cod_colaborador_abertura,
                                       cod_veiculo_problema,
                                       km_veiculo_abertura,
                                       cod_problema_socorro_rota,
                                       descricao_problema,
                                       data_hora_abertura,
                                       url_foto_1_abertura,
                                       url_foto_2_abertura,
                                       url_foto_3_abertura,
                                       latitude_abertura,
                                       longitude_abertura,
                                       precisao_localizacao_abertura_metros,
                                       endereco_automatico,
                                       ponto_referencia,
                                       device_id_abertura,
                                       device_imei_abertura,
                                       device_uptime_millis_abertura,
                                       android_api_version_abertura,
                                       marca_device_abertura,
                                       modelo_device_abertura,
                                       cod_empresa,
                                       plataforma_origem,
                                       versao_plataforma_origem)
    values (f_cod_abertura,
            f_cod_socorro_inserido,
            f_cod_colaborador_abertura,
            f_cod_veiculo_problema,
            v_km_final,
            f_cod_problema_socorro_rota,
            f_descricao_problema,
            f_data_hora_abertura,
            f_url_foto_1_abertura,
            f_url_foto_2_abertura,
            f_url_foto_3_abertura,
            f_latitude_abertura,
            f_longitude_abertura,
            f_precisao_localizacao_abertura_metros,
            f_endereco_automatico,
            f_ponto_referencia,
            f_device_id_abertura,
            f_device_imei_abertura,
            f_device_uptime_millis_abertura,
            f_android_api_version_abertura,
            f_marca_device_abertura,
            f_modelo_device_abertura,
            f_cod_empresa,
            f_plataforma_origem,
            f_versao_plataforma_origem);

    -- Exibe erro se não puder inserir.
    if f_cod_abertura is null or f_cod_abertura <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Retorna o código do socorro.
    return f_cod_socorro_inserido;
end;
$$;


---------------- AFERICAO
create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_placa_veiculo varchar(255),
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.placa = f_placa_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
                              placa_veiculo,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (v_cod_afericao,
            f_data_hora,
            f_placa_veiculo,
            f_cpf_aferidor,
            v_km_final,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
$$;


-------------- Movimentacao
drop function if exists func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
    f_cod_unidade bigint,
    f_tipo_origem varchar(255),
    f_cod_movimentacao bigint,
    f_placa_veiculo varchar(7),
    f_km_atual bigint,
    f_posicao_prolog integer);
create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem varchar(255),
                                                                                f_cod_movimentacao bigint,
                                                                                f_placa_veiculo varchar(7),
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_veiculo                bigint;
    v_cod_diagrama_veiculo       bigint;
    v_km_atual                   bigint;
    v_tipo_origem_atual          varchar(255) := (select p.status
                                                  from pneu p
                                                  where p.codigo = f_cod_pneu
                                                    and p.cod_unidade = f_cod_unidade
                                                    and f_tipo_origem in (select p.status
                                                                          from pneu p
                                                                          where p.codigo = f_cod_pneu
                                                                            and p.cod_unidade = f_cod_unidade));
    f_cod_movimentacao_realizada bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict
        v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO ORIGEM
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    placa,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            f_placa_veiculo,
            v_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into f_cod_movimentacao_realizada;

    if (f_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;

drop function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
    f_tipo_destino varchar,
    f_placa_veiculo varchar,
    f_km_atual bigint,
    f_posicao_prolog bigint);
-- 2020-12-15 -> Adiciona lógica para propagação de km (thaisksf - PL-3124).
create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino varchar(255),
                                                                                 f_placa_veiculo varchar(255),
                                                                                 f_posicao_prolog bigint)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
    v_cod_veiculo                bigint;
    v_km_atual                   bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO DESTINO.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
                                     placa,
                                     km_veiculo,
                                     posicao_pneu_destino,
                                     cod_motivo_descarte,
                                     url_imagem_descarte_1,
                                     url_imagem_descarte_2,
                                     url_imagem_descarte_3,
                                     cod_recapadora_destino,
                                     cod_coleta,
                                     cod_diagrama,
                                     cod_veiculo)
    values (f_cod_movimentacao,
            f_tipo_destino,
            f_placa_veiculo,
            v_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;