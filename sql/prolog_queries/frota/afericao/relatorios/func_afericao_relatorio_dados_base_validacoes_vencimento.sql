create or replace function func_afericao_relatorio_dados_base_validacao_vencimento(
    f_cod_unidades bigint[],
    f_data_hora_atual timestamp with time zone)
    returns table
            (
                cod_veiculo                        bigint,
                data_hora_ultima_afericao_sulco    timestamp without time zone,
                data_hora_ultima_afericao_pressao  timestamp without time zone,
                data_vencimento_sulco              date,
                data_vencimento_pressao            date,
                dias_vencimento_sulco              double precision,
                dias_vencimento_pressao            double precision,
                dias_desde_ultima_afericao_sulco   double precision,
                dias_desde_ultima_afericao_pressao double precision,
                pode_aferir_sulco                  boolean,
                pode_aferir_pressao                boolean,
                sulco_nunca_aferido                boolean,
                pressao_nunca_aferico              boolean,
                afericao_sulco_vencida             boolean,
                afericao_pressao_vencida           boolean
            )
    language plpgsql
as
$$
declare
    v_tipo_medicao_pressao          constant text not null := 'PRESSAO';
    v_tipo_medicao_sulco            constant text not null := 'SULCO';
    v_tipo_medicao_sulco_pressao    constant text not null := 'SULCO_PRESSAO';
    v_status_coleta_dados_bloqueado constant text not null := 'BLOQUEADO';
begin
    return query
        with pressao_dados as (
            select a.cod_veiculo                                             as cod_veiculo_intervalo,
                   max(a.data_hora at time zone
                       tz_unidade(a.cod_unidade))::date                      as data_ultima_afericao,
                   max(a.data_hora at time zone
                       tz_unidade(a.cod_unidade))                            as data_hora_ultima_afericao,
                   extract(days from (f_data_hora_atual) - max(a.data_hora)) as dias_ultima_afericao
            from afericao a
            where a.tipo_medicao_coletada = v_tipo_medicao_pressao
               or a.tipo_medicao_coletada = v_tipo_medicao_sulco_pressao
            group by a.cod_veiculo
        ),
             sulco_dados as (
                 select a.cod_veiculo                                             as cod_veiculo_intervalo,
                        max(a.data_hora at time zone
                            tz_unidade(a.cod_unidade))::date                      as data_ultima_afericao,
                        max(a.data_hora at time zone
                            tz_unidade(a.cod_unidade))                            as data_hora_ultima_afericao,
                        extract(days from (f_data_hora_atual) - max(a.data_hora)) as dias_ultima_afericao
                 from afericao a
                 where a.tipo_medicao_coletada = v_tipo_medicao_sulco
                    or a.tipo_medicao_coletada = v_tipo_medicao_sulco_pressao
                 group by a.cod_veiculo
             )
        select v.codigo                                                   as cod_veiculo,
               sd.data_hora_ultima_afericao                               as data_hora_ultima_afericao_sulco,
               pd.data_hora_ultima_afericao                               as data_hora_ultima_afericao_pressao,
               (sd.data_ultima_afericao +
                (pru.periodo_afericao_sulco || 'DAYS')::interval)::date   as data_vencimento_sulco,
               (pd.data_ultima_afericao +
                (pru.periodo_afericao_pressao || 'DAYS')::interval)::date as data_vencimento_pressao,
               pru.periodo_afericao_sulco - sd.dias_ultima_afericao       as dias_vencimento_sulco,
               pru.periodo_afericao_pressao - pd.dias_ultima_afericao     as dias_vencimento_pressao,
               sd.dias_ultima_afericao                                    as dias_desde_ultima_afericao_sulco,
               pd.dias_ultima_afericao                                    as dias_desde_ultima_afericao_pressao,
               f_if(config.forma_coleta_dados_sulco_pressao != v_status_coleta_dados_bloqueado
                        or config.forma_coleta_dados_sulco != v_status_coleta_dados_bloqueado,
                    true,
                    false)                                                as pode_aferir_sulco,
               f_if(config.forma_coleta_dados_pressao != v_status_coleta_dados_bloqueado
                        or config.forma_coleta_dados_sulco_pressao != v_status_coleta_dados_bloqueado,
                    true,
                    false)                                                as pode_aferir_pressao,
               f_if(sd.dias_ultima_afericao is null, true, false)         as sulco_nunca_aferico,
               f_if(pd.dias_ultima_afericao is null, true, false)         as pressao_nunca_aferido,
               f_if(sd.dias_ultima_afericao > pru.periodo_afericao_sulco,
                    true,
                    false)                                                as afericao_sulco_vencida,
               f_if(pd.dias_ultima_afericao > pru.periodo_afericao_pressao,
                    true,
                    false)                                                as afericao_pressao_vencida
        from veiculo v
                 join pneu_restricao_unidade pru
                      on pru.cod_unidade = v.cod_unidade
                 join func_afericao_get_config_tipo_afericao_veiculo(v.cod_unidade) config
                      on config.cod_tipo_veiculo = v.cod_tipo
                 left join pressao_dados pd
                           on pd.cod_veiculo_intervalo = v.codigo
                 left join sulco_dados sd
                           on sd.cod_veiculo_intervalo = v.codigo
        where v.status_ativo = true
          and v.cod_unidade = any (f_cod_unidades)
        order by v.codigo;
end;
$$