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