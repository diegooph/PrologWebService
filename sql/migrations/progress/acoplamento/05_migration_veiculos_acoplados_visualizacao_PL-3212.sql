-- PL-3212.

-- Altera ordem da constraint de (cod_posicao, cod_processo) para (cod_processo, cod_posicao) para que em um filtro
-- por cod_processo na tabela seja mais performático e provável ser utilizado o index de unique.
alter table veiculo_acoplamento_atual
    drop constraint unica_posicao_processo,
    add constraint unica_posicao_processo unique (cod_processo, cod_posicao);

-- A query performou melhor utilizando plpgsql ao invés de sql.
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