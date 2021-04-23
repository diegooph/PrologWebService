create or replace function func_afericao_relatorio_dados_base_validacao_vencimento(f_cod_unidades bigint[],
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
               sd.dias_ultima_afericao - pru.periodo_afericao_sulco       as dias_vencimento_sulco,
               pd.dias_ultima_afericao - pru.periodo_afericao_pressao     as dias_vencimento_pressao,
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
$$;

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
begin
    return query
        with calculo_vencimento_afericoes as (
            select base.dias_vencimento_pressao  as qtd_dias_afericao_pressao_vencida,
                   base.pode_aferir_pressao      as pode_aferir_pressao,
                   base.dias_vencimento_sulco    as qtd_dias_afericao_sulco_vencida,
                   base.pode_aferir_sulco        as pode_aferir_sulco,
                   base.cod_veiculo              as cod_veiculo,
                   base.afericao_sulco_vencida   as afericao_sulco_vencida,
                   base.afericao_pressao_vencida as afericao_pressao_vencida,
                   base.sulco_nunca_aferido      as sulco_nunca_aferido,
                   base.pressao_nunca_aferico    as pressao_nunca_aferico
            from func_afericao_relatorio_dados_base_validacao_vencimento(f_cod_unidades,
                                                                         f_data_hoje_utc) as base
        ),
             pre_select as (
                 select u.nome                                as nome_unidade,
                        v.placa                               as placa_veiculo,
                        coalesce(v.identificador_frota, '-')  as identificador_frota,
                        cva.pode_aferir_sulco                 as pode_aferir_sulco,
                        cva.pode_aferir_pressao               as pode_aferir_pressao,
                        cva.qtd_dias_afericao_sulco_vencida   as qtd_dias_afericao_sulco_vencida,
                        cva.qtd_dias_afericao_pressao_vencida as qtd_dias_afericao_pressao_vencida
                 from unidade u
                          join veiculo v
                               on v.cod_unidade = u.codigo
                          join calculo_vencimento_afericoes cva
                               on cva.cod_veiculo = v.codigo
                 where cva.afericao_sulco_vencida
                    or cva.afericao_pressao_vencida
                    or cva.sulco_nunca_aferido
                    or cva.pressao_nunca_aferico
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

create or replace function
    func_afericao_relatorio_cronograma_afericoes_placas(f_cod_unidades bigint[],
                                                        f_data_hora_atual_utc timestamp with time zone,
                                                        f_data_hora_geracao_relatorio timestamp with time zone)
    returns table
            (
                UNIDADE                              text,
                PLACA                                text,
                "IDENTIFICADOR FROTA"                text,
                "QTD PNEUS APLICADOS"                text,
                "MODELO VEÍCULO"                     text,
                "TIPO VEÍCULO"                       text,
                "STATUS SULCO"                       text,
                "STATUS PRESSÃO"                     text,
                "DATA VENCIMENTO SULCO"              text,
                "DATA VENCIMENTO PRESSÃO"            text,
                "DIAS VENCIMENTO SULCO"              text,
                "DIAS VENCIMENTO PRESSÃO"            text,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   text,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    text,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" text,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  text,
                "DATA/HORA GERAÇÃO RELATÓRIO"        text
            )
    language plpgsql
as
$$
begin
    return query
        with dados as (
            select u.nome::text                                                          as nome_unidade,
                   v.placa::text                                                         as placa_veiculo,
                   coalesce(v.identificador_frota::text, '-')                            as identificador_frota,
                   (select count(vp.cod_pneu)
                    from veiculo_pneu vp
                    where vp.cod_veiculo = v.codigo
                    group by vp.cod_veiculo)::text                                       as qtd_pneus_aplicados,
                   mv.nome::text                                                         as nome_modelo_veiculo,
                   vt.nome::text                                                         as nome_tipo_veiculo,
                   to_char(base.data_hora_ultima_afericao_sulco, 'dd/mm/yyyy hh24:mi')   as data_hora_ultima_afericao_sulco,
                   to_char(base.data_hora_ultima_afericao_pressao, 'dd/mm/yyyy hh24:mi') as data_hora_ultima_afericao_pressao,
                   to_char(base.data_vencimento_sulco, 'dd/mm/yyyy')                     as data_vencimento_sulco,
                   to_char(base.data_vencimento_pressao, 'dd/mm/yyyy')                   as data_vencimento_pressao,
                   base.dias_vencimento_sulco::text                                      as dias_vencimento_sulco,
                   base.dias_vencimento_pressao::text                                    as dias_vencimento_pressao,
                   base.dias_desde_ultima_afericao_sulco::text                           as dias_desde_ultima_afericao_sulco,
                   base.dias_desde_ultima_afericao_pressao::text                         as dias_desde_ultima_afericao_pressao,
                   base.pode_aferir_sulco                                                as pode_aferir_sulco,
                   base.pode_aferir_pressao                                              as pode_aferir_pressao,
                   base.sulco_nunca_aferido                                              as sulco_nunca_aferido,
                   base.pressao_nunca_aferico                                            as pressao_nunca_aferida,
                   base.afericao_sulco_vencida                                           as afericao_sulco_vencida,
                   base.afericao_pressao_vencida as afericao_pressao_vencida
            from veiculo v
                     join modelo_veiculo mv
                          on mv.codigo = v.cod_modelo
                     join veiculo_tipo vt
                          on vt.codigo = v.cod_tipo
                     join unidade u
                          on u.codigo = v.cod_unidade
                     join func_afericao_relatorio_dados_base_validacao_vencimento(f_cod_unidades,
                                                                                  f_data_hora_atual_utc) as base
                          on base.cod_veiculo = v.codigo
            where v.status_ativo = true
              and v.cod_unidade = any (f_cod_unidades)
            order by u.codigo, v.placa
        )
             -- todos os coalesce ficam aqui.
        select d.nome_unidade                                               as nome_unidade,
               d.placa_veiculo                                              as placa_veiculo,
               d.identificador_frota                                        as identificador_frota,
               coalesce(d.qtd_pneus_aplicados, '-')                         as qtd_pneus_aplicados,
               d.nome_modelo_veiculo                                        as nome_modelo_veiculo,
               d.nome_tipo_veiculo                                          as nome_tipo_veiculo,
               case
                   when not d.pode_aferir_sulco
                       then 'BLOQUEADO AFERIÇÃO'
                   when d.sulco_nunca_aferido
                       then 'VENCIDO (NUNCA AFERIDO)'
                   when d.afericao_sulco_vencida
                       then 'VENCIDO (FORA DO PRAZO)'
                   else 'NO PRAZO'
                   end                                                      as status_sulco,
               case
                   when not d.pode_aferir_pressao
                       then 'BLOQUEADO AFERIÇÃO'
                   when d.pressao_nunca_aferida
                       then 'VENCIDO (NUNCA AFERIDA)'
                   when d.afericao_pressao_vencida
                       then 'VENCIDO (FORA DO PRAZO)'
                   else 'NO PRAZO'
                   end                                                      as status_pressao,
               f_if(d.sulco_nunca_aferido, '-',
                    d.data_vencimento_sulco)                                as data_vencimento_sulco,
               f_if(d.pressao_nunca_aferida, '-',
                    d.data_vencimento_pressao)                              as data_vencimento_pressao,
               f_if(d.sulco_nunca_aferido, '-',
                    d.dias_vencimento_sulco)                                as dias_vencimento_sulco,
               f_if(d.pressao_nunca_aferida, '-',
                    d.dias_vencimento_pressao)                              as dias_vencimento_pressao,
               f_if(d.sulco_nunca_aferido, '-',
                    d.dias_desde_ultima_afericao_sulco)                     as dias_desde_ultima_afericao_sulco,
               coalesce(d.data_hora_ultima_afericao_sulco, '-')             as data_hora_ultima_afericao_sulco,
               f_if(d.pressao_nunca_aferida, '-',
                    d.dias_desde_ultima_afericao_pressao)                   as dias_desde_ultima_afericao_pressao,
               coalesce(d.data_hora_ultima_afericao_pressao, '-')           as data_hora_ultima_afericao_pressao,
               to_char(f_data_hora_geracao_relatorio, 'dd/mm/yyyy hh24:mi') as data_hora_geracao_relatorio
        from dados d;
end;
$$;

CReate or replace function func_pneu_relatorio_status_placas_afericao(f_cod_unidades bigint[],
                                                                      f_data_hora_atual_utc timestamp with time zone)
    returns table
            (
                total_vencidas bigint,
                total_no_prazo bigint
            )
    language plpgsql
as
$$
declare
    qtd_placas_ativas bigint := (select count(v.placa)
                                 from veiculo v
                                 where v.cod_unidade = any (f_cod_unidades)
                                   and v.status_ativo = true);
begin
    return query
        with qtd_placas_vencidas as (
            select (select count(placa)
                    from func_afericao_relatorio_qtd_dias_placas_vencidas(f_cod_unidades,
                                                                          f_data_hora_atual_utc)) as qtd_vencidas
        )

        select qpv.qtd_vencidas                     as qtd_vencidas,
               qtd_placas_ativas - qpv.qtd_vencidas as qtd_prazo
        from qtd_placas_vencidas qpv;
end;
$$;