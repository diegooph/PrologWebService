-- PL-3277.
create or replace function func_afericao_relatorio_exportacao_protheus(f_cod_unidades bigint[],
                                                                       f_cod_veiculos bigint[],
                                                                       f_data_inicial date,
                                                                       f_data_final date)
    returns table
            (
                codigo_afericao      bigint,
                cabecalho_placa      text,
                placa                varchar(7),
                data                 text,
                hora                 text,
                cabecalho_pneu       text,
                codigo_cliente_pneu  text,
                nomenclatura_posicao text,
                calibragem_aferida   real,
                calibragem_realizada real,
                sulco_interno        real,
                sulco_central        real,
                sulco_externo        real
            )
    language plpgsql
as
$$
begin
    return query
        select a.codigo                                                                        as codigo_afericao,
               'TTO'                                                                           as cabecalho_placa,
               a.placa_veiculo                                                                 as placa,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'DD/MM/YYYY')::text as data,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'HH24:MI')::text    as hora,
               'TTP'                                                                           as cabecalho_pneu,
               lpad(p.codigo_cliente::text, 7, '0')                                            as codigo_fogo_pneu,
               trim(coalesce(ppne.nomenclatura::text, ''))                                     as nomenclatura_posicao,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_aferida,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_realizada,
               coalesce(round(cast(av.altura_sulco_interno as numeric), 2), -1)::real          as altura_sulco_interno,
               coalesce(round(cast(av.altura_sulco_central_interno as numeric), 2), -1)::real  as sulco_central,
               coalesce(round(cast(av.altura_sulco_externo as numeric), 2), -1)::real          as altura_sulco_externo
        from afericao a
                 inner join afericao_valores av on av.cod_afericao = a.codigo
                 inner join pneu p on p.codigo = av.cod_pneu
                 inner join veiculo v on v.codigo = a.cod_veiculo
                 left join pneu_posicao_nomenclatura_empresa ppne
                           on ppne.posicao_prolog = av.posicao and ppne.cod_diagrama = a.cod_diagrama and
                              ppne.cod_empresa = v.cod_empresa
        where a.tipo_processo_coleta = 'PLACA'
          and a.cod_veiculo = any (f_cod_veiculos)
          and a.cod_unidade = any (f_cod_unidades)
          and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date
            between f_data_inicial and f_data_final
        order by a.codigo;
end
$$;