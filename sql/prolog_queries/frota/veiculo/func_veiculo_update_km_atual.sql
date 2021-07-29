--? Sobre:
--+ A lógica aplicada nessa function é a seguinte:
--. veículos com hubodômetro (com ou sem acoplamentos):
--      Se o km coletado for menor que o atual, o km não será modificado, caso contrário, esses veículos terão o km
--      modificado de acordo com o km coletado.
--      Em ambos os casos, o km que será inserido no processo é o coletado.
--      Estes não propagam km e não recebem propagação por possuírem a própria coleta.
--. veículos motorizados sem acoplamento:
--      Se o km coletado for menor que o atual, o km não será modificado, caso contrário, esses veículos terão o km
--      modificado de acordo com o km coletado.
--      Em ambos os casos, o km que será inserido no processo é o coletado.
--. veículos motorizados com acoplamento:
--      Se o km coletado for menor que o atual, o km não será modificado e não haverá propagação.
--      Caso contrário, iremos procurar na tabela veiculo_acoplamento_atual os códigos correspondentes aos veículos
--      acoplados que não possuem hubodômetro (estes serão tratados de forma única no sistema e não receberão propagação
--      de km). Será realizado um cálculo para verificar a diferença entre o km coletado e o km atual do veículo
--      motorizado. Essadiferença será propagada para todos os códigos de veículos coletados anteriormente com o
--      seguinte cálculo: km + diferenca_km. O processo irá receber o cálculo de km_atual + diferenca_km.
--      (o mesmo case trata dos veículos não motorizados, por este motivo, não é retornado o km_coletado).
--. veículos não motorizados sem acoplamento:
--      Não será coletado o km por não possuir odômetro e nem hubodômetro. Portanto, o km atual não será modificado e o
--      km inserido no processo será o km atual do veículo.
--. veículos não motorizados com acoplamento:
--      Será necessário consultar o acoplamento para que possamos verificar se algum veículu dele possui motor. Caso
--      exista, a diferença de km é calculada através do veículo motorizado: km_coletado - km. Essa diferença é
--      repassada para todos os veículos do acoplamento - desde que não possuam hubodômetro. Se o acoplamento não
--      possuir um veículo motorizado,  então o km inserido no processo será o atual.
--
--. Obs: veículos que tiveram o km modificado pela propagação terão seu histórico salvo em uma tabela específica.
drop function func_veiculo_update_km_atual(f_cod_unidade bigint, f_cod_veiculo bigint, f_km_coletado bigint,
                                           f_cod_processo bigint, f_tipo_processo text, f_deve_propagar_km boolean,
                                           f_data_hora timestamp with time zone);
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo text,
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
    v_cod_empresa                        bigint;
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo, v.cod_empresa
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento, v_cod_empresa;

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