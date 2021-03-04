-- Sobre:
--
-- Esta function foi criada para a integração de aferições. Foi desenhada para ser genérica e funcionar com qualquer
-- empresa que queira utilizar a integração de aferição de pneus do Prolog.
--
-- Utilizamos a function para retornar, para cada placa informada por parâmetro, quantos dias passaram desde a última
-- vez que foi aferida. Caso a placa nunca tenha sido aferida, retornamos o valor -1.
--
-- Histórico:
-- 2020-03-24 -> Function criada (diogenesvanzella - PL-2563).
create or replace function
    integracao.func_pneu_afericao_get_infos_placas_aferidas(f_cod_empresa bigint,
                                                            f_placas_afericao text[],
                                                            f_data_hora_atual timestamp with time zone)
    returns table
            (
                placa_afericao    text,
                intervalo_pressao integer,
                intervalo_sulco   integer
            )
    language sql
as
$$
with placas as (select unnest(f_placas_afericao) as placa)

select p.placa                                            as placa_afericao,
       coalesce(intervalo_pressao.intervalo, -1)::integer as intervalo_pressao,
       coalesce(intervalo_sulco.intervalo, -1)::integer   as intervalo_sulco
from placas p
         left join integracao.afericao_integrada ai
                   on p.placa = ai.placa_veiculo
                       and ai.cod_empresa_prolog = f_cod_empresa
         left join (select ai.placa_veiculo                                          as placa_intervalo,
                           extract(days from f_data_hora_atual -
                                             max(ai.data_hora at time zone
                                                 tz_unidade(ai.cod_unidade_prolog))) as intervalo
                    from integracao.afericao_integrada ai
                    where ai.tipo_medicao_coletada = 'PRESSAO'
                       or ai.tipo_medicao_coletada = 'SULCO_PRESSAO'
                    group by ai.placa_veiculo) as intervalo_pressao
                   on intervalo_pressao.placa_intervalo = p.placa
         left join (select ai.placa_veiculo                                          as placa_intervalo,
                           extract(days from f_data_hora_atual -
                                             max(ai.data_hora at time zone
                                                 tz_unidade(ai.cod_unidade_prolog))) as intervalo
                    from integracao.afericao_integrada ai
                    where ai.tipo_medicao_coletada = 'SULCO'
                       or ai.tipo_medicao_coletada = 'SULCO_PRESSAO'
                    group by ai.placa_veiculo) as intervalo_sulco
                   on intervalo_sulco.placa_intervalo = p.placa;
$$;