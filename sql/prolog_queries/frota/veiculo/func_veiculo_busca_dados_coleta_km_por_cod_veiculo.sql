-- Sobre:
--
--  function que, dado um código de veículo, retorna dados do estado
--  do acoplamento do veiculo.
--
-- Histórico:
-- 2020-11-10 -> Cria function (steinert999 - PL-3291).
-- 2020-11-12 -> Altera atributo de km para km_atual (steinert999 - PL-3291).
-- 2020-11-12 -> Adiciona campos para veiculo sem hubodometro com trator (steinert999 - PL-3291).
-- 2020-11-13 -> Corrige bugs na function em caso de sem hubodometro e sem trator (steinert999 - PL-3291).
create or replace function func_veiculo_busca_dados_coleta_km_por_cod_veiculo(f_cod_veiculo bigint)
returns table (cod_veiculo                 bigint,
                placa                      text,
                motorizado                 boolean,
                km_atual                   bigint,
                identificador_frota        text,
                possui_hubodometro         boolean,
                acoplado                   boolean,
                deve_coletar_km            boolean,
                cod_veiculo_trator         bigint,
                placa_trator               text,
                km_atual_trator            bigint,
                identificador_frota_trator text)
language plpgsql
as
$$
    declare
        v_posicao_trator constant bigint not null  := 1;

        v_placa                               text;
        v_motorizado                          boolean;
        v_km_atual                            bigint;
        v_identificador_frota                 text;
        v_possui_hubodometro                  boolean;
        v_deve_coletar_km                     boolean;

        v_cod_processo  constant              bigint := (select vaa.cod_processo
                                                              from veiculo_acoplamento_atual vaa
                                                              where vaa.cod_veiculo = f_cod_veiculo);
        v_acoplado      constant              boolean := v_cod_processo is not null;
        v_cod_veiculo_trator_processo         bigint;
        v_placa_veiculo_trator_processo       text;
        v_km_atual_trator_processo            bigint;
        v_identificador_frota_trator_processo text;

    begin
        select v.placa,
               v.motorizado,
               v.km,
               v.identificador_frota,
               v.possui_hubodometro
        from veiculo v
        where v.codigo = f_cod_veiculo
        into
        v_placa,
        v_motorizado,
        v_km_atual,
        v_identificador_frota,
        v_possui_hubodometro;

        if(not v_motorizado and not v_possui_hubodometro)
        then
             select vaa.cod_veiculo
               from veiculo_acoplamento_historico vaa
               where vaa.cod_processo = v_cod_processo
               and vaa.cod_posicao = v_posicao_trator
        into v_cod_veiculo_trator_processo;
              select v.codigo,
                     v.placa,
                     v.km,
                     v.identificador_frota
            from veiculo v
            where v_cod_veiculo_trator_processo is not null
                and v.codigo = v_cod_veiculo_trator_processo
            into v_cod_veiculo_trator_processo,
                v_placa_veiculo_trator_processo,
                 v_km_atual_trator_processo,
                 v_identificador_frota_trator_processo;
        end if;

        v_deve_coletar_km := f_if(not v_motorizado and
                                  not v_possui_hubodometro and
                                  (not v_acoplado or
                                   v_cod_veiculo_trator_processo is null), false, true);
        return query
            select f_cod_veiculo,
                   v_placa,
                   v_motorizado,
                   v_km_atual,
                   v_identificador_frota,
                   v_possui_hubodometro,
                   v_acoplado,
                   v_deve_coletar_km,
                   v_cod_veiculo_trator_processo,
                   v_placa_veiculo_trator_processo,
                   v_km_atual_trator_processo,
                   v_identificador_frota_trator_processo;
    end;
$$