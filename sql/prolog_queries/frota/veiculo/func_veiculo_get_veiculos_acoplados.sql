-- Sobre:
--
-- Esta function retorna os veículos que compôem o acoplamento no qual o veículo de código f_cod_veiculo faz parte.
-- Se o veículo informado não fizer parte de nenhum acoplamento, nada é retornado.
--
-- O veículo informado pode fazer parte da composição tanto como trator ou como reboque, isso é irrelevante para a
-- busca em si.
--
-- Importante ressaltar que o próprio veículo informado como parâmetro retorna da busca, não apenas os veículos que
-- estão no acoplamento com ele.
--
-- Histórico:
-- 2020-11-10 -> Function criada (luizfp - PL-3212).
create or replace function func_veiculo_get_veiculos_acoplados(f_cod_veiculo bigint)
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
        where vaa.cod_processo = (select q.cod_processo
                                  from veiculo_acoplamento_atual q
                                  where q.cod_veiculo = f_cod_veiculo)
        order by vaa.cod_posicao;
end;
$$;