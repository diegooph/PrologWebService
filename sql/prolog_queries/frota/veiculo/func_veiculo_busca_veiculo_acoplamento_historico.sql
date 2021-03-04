-- Sobre:
-- Esta function retorna os processos de acoplamento que foram realizados de acordo com os filtros aplicados.
--
-- Histórico:
-- 2020-11-04 -> Function criada (thaisksf - PL-3209).
create or replace function func_veiculo_busca_veiculo_acoplamento_historico(f_cod_unidades bigint[],
                                                                            f_cod_veiculos bigint[],
                                                                            f_data_inicial date,
                                                                            f_data_final date)
    returns table
            (
                cod_processo        bigint,
                nome_unidade        text,
                nome_colaborador    text,
                placa               text,
                identificador_frota text,
                motorizado          boolean,
                km                  bigint,
                cod_posicao         smallint,
                nome_posicao        text,
                acao                text,
                data_hora           timestamp without time zone,
                observacao          text
            )
    language plpgsql
as
$$
begin
    return query
        select vap.codigo                                             as cod_processo,
               u.nome ::text                                          as unidade,
               c.nome ::text                                          as colaborador,
               v.placa ::text                                         as placa,
               v.identificador_frota ::text                           as identificador_frota,
               v.motorizado                                           as motorizado,
               vah.km_veiculo                                         as km,
               vah.cod_posicao                                        as cod_posicao,
               vapo.posicao_legivel_pt_br ::text                      as posicao_legivel_pt_br,
               vah.acao ::text                                        as acao,
               vap.data_hora at time zone tz_unidade(vap.cod_unidade) as data_hora,
               vap.observacao ::text                                  as observacao
        from veiculo_acoplamento_processo vap
                 join veiculo_acoplamento_historico vah on vap.cod_unidade = any (f_cod_unidades)
            and vap.codigo = vah.cod_processo
                 join types.veiculo_acoplamento_posicao vapo on vapo.codigo = vah.cod_posicao
                 join veiculo v on v.codigo = vah.cod_veiculo
                 join unidade u on u.codigo = vap.cod_unidade
                 join colaborador c on vap.cod_colaborador = c.codigo
        where vap.cod_unidade = any (f_cod_unidades)
          and case
                  when f_cod_veiculos is not null
                      then
                          vah.cod_processo in (select h.cod_processo
                                               from veiculo_acoplamento_historico h
                                               where h.cod_veiculo = any (f_cod_veiculos))
                  else
                      true
            end
          and case
                  when (f_data_inicial is not null and f_data_final is not null)
                      then
                      (vap.data_hora at time zone tz_unidade(vap.cod_unidade)) :: date
                          between f_data_inicial and f_data_final
                  else
                      true
            end
        order by unidade, cod_processo, cod_posicao;
end;
$$;