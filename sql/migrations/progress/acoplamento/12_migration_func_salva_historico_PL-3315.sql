drop function if exists func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
    f_cod_historico_processo_acoplamento bigint,
    f_cod_processo_acoplamento bigint,
    f_cod_veiculo_propagado bigint,
    f_motorizado boolean,
    f_veiculo_fonte_processo boolean,
    f_km_antigo bigint,
    f_km_final bigint,
    f_km_coletado bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_cod_processo bigint,
    f_data_hora timestamp with time zone);

create or replace function func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
                                                                      f_cod_historico_processo_acoplamento bigint,
                                                                      f_cod_processo_acoplamento bigint,
                                                                      f_cod_veiculo_propagado bigint,
                                                                      f_motorizado boolean,
                                                                      f_veiculo_fonte_processo boolean,
                                                                      f_km_antigo bigint,
                                                                      f_km_final bigint,
                                                                      f_km_coletado bigint,
                                                                      f_tipo_processo types.veiculo_processo_type,
                                                                      f_cod_processo bigint,
                                                                      f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_historico_propagacao bigint;
begin
    insert into veiculo_processo_km_historico (cod_unidade,
                                               cod_historico_processo_acoplamento,
                                               cod_processo_acoplamento,
                                               cod_processo_veiculo,
                                               tipo_processo_veiculo,
                                               cod_veiculo,
                                               motorizado,
                                               veiculo_fonte_processo,
                                               km_antigo,
                                               km_final,
                                               km_coletado_processo,
                                               data_hora_processo)
    values (f_cod_unidade,
            f_cod_historico_processo_acoplamento,
            f_cod_processo_acoplamento,
            f_cod_processo,
            f_tipo_processo,
            f_cod_veiculo_propagado,
            f_motorizado,
            f_veiculo_fonte_processo,
            f_km_antigo,
            f_km_final,
            f_km_coletado,
            f_data_hora)
    returning codigo into v_cod_historico_propagacao;
    return v_cod_historico_propagacao;
end;
$$;


drop function func_veiculo_update_km_atual(f_cod_unidade bigint,
    f_cod_veiculo bigint,
    f_km_coletado bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_deve_propagar_km boolean);

create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo types.veiculo_processo_type,
                                                        f_deve_propagar_km boolean,
                                                        f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                           bigint;
    v_diferenca_km                       bigint;
    v_km_motorizado                      bigint;
    v_possui_hubodometro                 boolean;
    v_motorizado                         boolean;
    v_cod_processo_acoplamento           bigint;
    v_cod_historico_processo_acoplamento bigint[];
    v_cod_veiculos_acoplados             bigint[];
    v_km_veiculos_acoplados              bigint[];
    v_veiculos_motorizados               boolean[];
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.cod_unidade = f_cod_unidade
      and v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento;

    if not f_deve_propagar_km
    then
        if v_km_atual < f_km_coletado
        then
            update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
            return f_km_coletado;
        end if;
    end if;

    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    select array_agg(vaa.cod_veiculo), array_agg(v.motorizado), array_agg(v.km), array_agg(vah.codigo)
                    from veiculo_acoplamento_atual vaa
                             join veiculo v
                                  on vaa.cod_unidade = v.cod_unidade
                                      and vaa.cod_veiculo = v.codigo
                             inner join veiculo_acoplamento_historico vah on vaa.cod_processo = vah.cod_processo
                        and vaa.cod_veiculo = vah.cod_veiculo
                    where vaa.cod_unidade = f_cod_unidade
                      and vaa.cod_processo = v_cod_processo_acoplamento
                      and v.possui_hubodometro is false
                    into v_cod_veiculos_acoplados,
                        v_veiculos_motorizados,
                        v_km_veiculos_acoplados,
                        v_cod_historico_processo_acoplamento;
                end if;
                case when (v_possui_hubodometro is false and v_motorizado is false and
                           v_cod_processo_acoplamento is null)
                    then
                        perform func_veiculo_salva_historico_km_propagacao(
                                f_cod_unidade,
                                null,
                                v_cod_processo_acoplamento,
                                f_cod_veiculo,
                                v_motorizado,
                                true,
                                v_km_atual,
                                v_km_atual,
                                f_km_coletado,
                                f_tipo_processo,
                                f_cod_processo,
                                f_data_hora);
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        perform func_veiculo_salva_historico_km_propagacao(
                                                                f_cod_unidade,
                                                                unnest(v_cod_historico_processo_acoplamento),
                                                                v_cod_processo_acoplamento,
                                                                unnest(v_cod_veiculos_acoplados),
                                                                unnest(v_veiculos_motorizados),
                                                                (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                                unnest(v_km_veiculos_acoplados),
                                                                unnest(v_km_veiculos_acoplados),
                                                                f_km_coletado,
                                                                f_tipo_processo,
                                                                f_cod_processo,
                                                                f_data_hora);
                                                        return v_km_atual;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados) + v_diferenca_km,
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados),
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;


update veiculo v
set km = km + 10
where v.codigo in (24327, 41171, 23410);