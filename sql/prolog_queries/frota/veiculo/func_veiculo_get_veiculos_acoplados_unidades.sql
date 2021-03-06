-- Sobre:
--
-- Esta function retorna todos os acoplamentos existentes nas unidades informadas em f_cod_unidades.
--
-- Também existem dois parâmetros de filtro: f_apenas_veiculos_ativos e f_cod_tipo_veiculo. Caso não seja necessário
-- filtrar por eles, null pode ser enviado.
--
-- Histórico:
-- 2020-11-11 -> Function criada (luizfp - PL-3211).
create or replace function func_veiculo_get_veiculos_acoplados_unidades(f_cod_unidades bigint[],
                                                                        f_apenas_veiculos_ativos boolean,
                                                                        f_cod_tipo_veiculo bigint)
    returns table
            (
                cod_processo_acoplamento bigint,
                cod_veiculo              bigint,
                placa                    text,
                identificador_frota      text,
                motorizado               boolean,
                posicao_acoplado         smallint
            )
    language plpgsql
as
$$
begin
    return query
        select vaa.cod_processo              as cod_processo_acoplamento,
               v.codigo                      as cod_veiculo,
               v.placa :: text               as placa,
               v.identificador_frota :: text as identificador_frota,
               v.motorizado                  as motorizado,
               vaa.cod_posicao               as posicao_acoplado
        from veiculo_acoplamento_atual vaa
                 join veiculo v on vaa.cod_veiculo = v.codigo
        where vaa.cod_unidade = any (f_cod_unidades)
          and case
                  when f_apenas_veiculos_ativos is null or f_apenas_veiculos_ativos = false
                      then true
                  else v.status_ativo = true
            end
          and case
                  when f_cod_tipo_veiculo is null
                      then true
                  else v.cod_tipo = f_cod_tipo_veiculo
            end
        order by vaa.cod_processo, vaa.cod_posicao;
end;
$$;