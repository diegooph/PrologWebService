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