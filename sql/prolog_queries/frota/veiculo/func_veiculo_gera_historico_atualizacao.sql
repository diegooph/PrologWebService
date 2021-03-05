create or replace function func_veiculo_gera_historico_atualizacao(f_cod_empresa bigint,
                                                                   f_cod_veiculo bigint,
                                                                   f_cod_colaborador_edicao bigint,
                                                                   f_origem_edicao text,
                                                                   f_data_hora_edicao_tz_aplicado timestamp
                                                                       with time zone,
                                                                   f_informacoes_extras_edicao text,
                                                                   f_nova_placa text,
                                                                   f_novo_identificador_frota text,
                                                                   f_novo_km bigint,
                                                                   f_novo_cod_diagrama bigint,
                                                                   f_novo_cod_tipo bigint,
                                                                   f_novo_cod_modelo bigint,
                                                                   f_novo_status boolean,
                                                                   f_novo_possui_hubodometro boolean,
                                                                   f_total_edicoes smallint)
    returns table
            (
                codigo_historico_estado_antigo bigint,
                codigo_historico_estado_novo   bigint
            )
    language plpgsql
as
$$
declare
    v_cod_edicao_historico_estado_antigo bigint;
    v_cod_edicao_historico_estado_novo   bigint;
    v_cod_unidade                        bigint;
    v_antiga_placa                       text;
    v_antigo_identificador_frota         text;
    v_antigo_km                          bigint;
    v_antigo_cod_diagrama                bigint;
    v_antigo_cod_tipo                    bigint;
    v_antigo_cod_marca                   bigint;
    v_antigo_cod_modelo                  bigint;
    v_antigo_status                      boolean;
    v_antigo_possui_hubodometro          boolean;
    v_data_hora_edicao_tz_unidade        timestamp with time zone;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    v_data_hora_edicao_tz_unidade := f_data_hora_edicao_tz_aplicado at time zone
                                     tz_unidade(v_cod_unidade);

    set constraints all deferred;
    v_cod_edicao_historico_estado_antigo
        := (select nextval(pg_get_serial_sequence('veiculo_edicao_historico', 'codigo')));
    v_cod_edicao_historico_estado_novo
        := (select nextval(pg_get_serial_sequence('veiculo_edicao_historico', 'codigo')));

    insert into veiculo_edicao_historico (codigo,
                                          cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          data_hora_utc,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo,
                                          codigo_edicao_vinculada,
                                          estado_antigo,
                                          possui_hubodometro)
    values (v_cod_edicao_historico_estado_antigo,
            f_cod_empresa,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            v_data_hora_edicao_tz_unidade,
            now(),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            v_antiga_placa,
            v_antigo_identificador_frota,
            v_antigo_km,
            v_antigo_status,
            v_antigo_cod_diagrama,
            v_antigo_cod_tipo,
            v_antigo_cod_modelo,
            v_cod_edicao_historico_estado_novo,
            true,
            v_antigo_possui_hubodometro);

    insert into veiculo_edicao_historico (codigo,
                                          cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          data_hora_utc,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo,
                                          codigo_edicao_vinculada,
                                          estado_antigo,
                                          possui_hubodometro)
    values (v_cod_edicao_historico_estado_novo,
            f_cod_empresa,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            v_data_hora_edicao_tz_unidade,
            now(),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            f_nova_placa,
            f_novo_identificador_frota,
            f_novo_km,
            f_novo_status,
            f_novo_cod_diagrama,
            f_novo_cod_tipo,
            f_novo_cod_modelo,
            v_cod_edicao_historico_estado_antigo,
            false,
            f_novo_possui_hubodometro);

    return query
        select v_cod_edicao_historico_estado_antigo, v_cod_edicao_historico_estado_novo;
end;
$$;