create or replace function func_afericao_relatorio_cronograma_afericoes_placas(f_cod_unidades bigint[],
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
                   base.afericao_sulco_vencida                                           as afericao_pressao_vencida
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
                       then 'SULCO NUNCA AFERIDO'
                   when d.afericao_sulco_vencida
                       then 'VENCIDO'
                   else 'NO PRAZO'
                   end                                                      as status_sulco,
               case
                   when not d.pode_aferir_pressao
                       then 'BLOQUEADO AFERIÇÃO'
                   when d.pressao_nunca_aferida
                       then 'PRESSÃO NUNCA AFERIDA'
                   when d.afericao_pressao_vencida
                       then 'VENCIDO'
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