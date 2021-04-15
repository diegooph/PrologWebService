create or replace function func_afericao_relatorio_qtd_dias_placas_vencidas(f_cod_unidades bigint[],
                                                                            f_data_hoje_utc timestamp with time zone)
    returns table
            (
                unidade                           text,
                placa                             text,
                identificador_frota               text,
                pode_aferir_sulco                 boolean,
                pode_aferir_pressao               boolean,
                qtd_dias_afericao_sulco_vencida   integer,
                qtd_dias_afericao_pressao_vencida integer
            )
    language plpgsql
as
$$
declare
    v_afericao_sulco         varchar := 'sulco';
    v_afericao_pressao       varchar := 'pressao';
    v_afericao_sulco_pressao varchar := 'sulco_pressao';
    v_formas_coleta_dados  text[]  := array ['equipamento', 'manual', 'equipamento_manual'];
begin
    return query
        with calculo_vencimento_afericoes as (
            select base.dias_desde_ultima_afericao_pressao as qtd_dias_afericao_pressao_vencida,
                   base.pode_aferir_pressao                as pode_aferir_pressao,
                   base.dias_desde_ultima_afericao_sulco   as qtd_dias_afericao_sulco_vencida,
                   base.pode_aferir_sulco                  as pode_aferir_sulco,
                   base.cod_veiculo                        as cod_veiculo,
                   base.afericao_sulco_vencida             as afericao_sulco_vencida,
                   base.afericao_pressao_vencida           as afericao_pressao_vencida
            from func_afericao_relatorio_dados_base_validacao_vencimento(f_cod_unidades,
                                                                         f_data_hoje_utc) as base
        ),
             -- as ctes ultima_afericao_sulco e ultima_afericao_pressao retornam o codigo de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. um -20, por exemplo, significa
             -- que a aferição vai vencer em 20 dias.
             ultima_afericao_sulco as (
                 select distinct on (a.cod_veiculo) a.cod_unidade,
                                                      a.cod_veiculo              as cod_veiculo,
                                                      date_part('day', f_data_hoje_utc - max(data_hora))
                                                          -
                                                      (pru.periodo_afericao_sulco) as qtd_dias_entre_ultima_afericao_sulco_e_hoje
                 from afericao a
                          join pneu_restricao_unidade pru
                               on (select v.cod_unidade
                                   from veiculo v
                                   where v.codigo = a.cod_veiculo) = pru.cod_unidade
                 where a.tipo_medicao_coletada in (v_afericao_sulco, v_afericao_sulco_pressao)
                   -- desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   and cod_veiculo = any (select vau.codigo
                                            from veiculos_ativos_unidades vau)
                 group by a.data_hora,
                          a.cod_unidade,
                          a.cod_veiculo,
                          pru.periodo_afericao_sulco
                 order by a.cod_veiculo, a.data_hora desc
             ),
             ultima_afericao_pressao as (
                 select distinct on (a.cod_veiculo) a.cod_unidade,
                                                      a.cod_veiculo                as cod_veiculo,
                                                      date_part('day', f_data_hoje_utc - max(data_hora))
                                                          -
                                                      (pru.periodo_afericao_pressao) as qtd_dias_entre_ultima_afericao_pressao_e_hoje
                 from afericao a
                          join pneu_restricao_unidade pru
                               on (select v.cod_unidade
                                   from veiculo v
                                   where v.codigo = a.cod_veiculo) = pru.cod_unidade
                 where a.cod_unidade = any (f_cod_unidades)
                   and a.tipo_medicao_coletada in (v_afericao_pressao, v_afericao_sulco_pressao)
                   and cod_veiculo = any (select vau.codigo
                                            from veiculos_ativos_unidades vau)
                 group by a.data_hora,
                          a.cod_unidade,
                          a.cod_veiculo,
                          pru.periodo_afericao_pressao
                 order by a.cod_veiculo, a.data_hora desc
             ),

             pre_select as (
                 select u.nome                                as nome_unidade,
                        v.placa                               as placa_veiculo,
                        coalesce(v.identificador_frota, '-')  as identificador_frota,
                        cva.pode_aferir_sulco                 as pode_aferir_sulco,
                        cva.pode_aferir_pressao               as pode_aferir_pressao,
                        -- por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        cva.qtd_dias_afericao_sulco_vencida   as qtd_dias_afericao_sulco_vencida,
                        cva.qtd_dias_afericao_pressao_vencida as qtd_dias_afericao_pressao_vencida
                 from unidade u
                          join veiculo v
                               on v.cod_unidade = u.codigo
                          join calculo_vencimento_afericoes cva
                               on cva.cod_veiculo = v.codigo
                 where cva.afericao_sulco_vencida
                    or cva.afericao_pressao_vencida
                 group by u.nome,
                          v.placa,
                          v.identificador_frota,
                          v.cod_tipo,
                          v.cod_unidade,
                          cva.pode_aferir_sulco,
                          cva.pode_aferir_pressao,
                          cva.qtd_dias_afericao_sulco_vencida,
                          cva.qtd_dias_afericao_pressao_vencida
             )
        select ps.nome_unidade::text                         as nome_unidade,
               ps.placa_veiculo::text                        as placa_veiculo,
               ps.identificador_frota::text                  as identificador_frota,
               ps.pode_aferir_sulco                          as pode_aferir_sulco,
               ps.pode_aferir_pressao                        as pode_aferir_pressao,
               ps.qtd_dias_afericao_sulco_vencida::integer   as qtd_dias_afericao_sulco_vencida,
               ps.qtd_dias_afericao_pressao_vencida::integer as qtd_dias_afericao_pressao_vencida
        from pre_select ps
             -- para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        where ps.pode_aferir_sulco <> false
           or ps.pode_aferir_pressao <> false
        order by ps.qtd_dias_afericao_sulco_vencida desc,
                 ps.qtd_dias_afericao_pressao_vencida desc;
end;
$$;