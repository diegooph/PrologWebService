drop function if exists func_afericao_relatorio_exportacao_protheus(f_cod_unidades bigint[],
    f_cod_veiculos bigint[],
    f_data_inicial date,
    f_data_final date);
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
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'MM/DD/YYYY')::text as data,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'HH24:MI')::text    as hora,
               'TTP'                                                                           as cabecalho_pneu,
               p.codigo_cliente::text                                                          as codigo_fogo_pneu,
               coalesce(ppne.nomenclatura::text, '')                                           as nomenclatura_posicao,
               coalesce(av.psi, -1)                                                            as calibragem_aferida,
               coalesce(av.psi, -1)                                                            as calibragem_realizada,
               coalesce(av.altura_sulco_interno, -1)                                           as altura_sulco_interno,
               coalesce(av.altura_sulco_central_interno, -1)                                   as sulco_central,
               coalesce(av.altura_sulco_externo, -1)                                           as altura_sulco_externo
        from afericao a
                 inner join afericao_valores av on av.cod_afericao = a.codigo
                 inner join pneu p on p.codigo = av.cod_pneu
                 inner join veiculo_pneu vp on vp.cod_pneu = p.codigo
                 left join pneu_posicao_nomenclatura_empresa ppne on ppne.posicao_prolog = vp.posicao
            and vp.placa = a.placa_veiculo
        where a.tipo_processo_coleta = 'PLACA'
          and a.cod_veiculo = any (f_cod_veiculos)
          and a.cod_unidade = any (f_cod_unidades)
          and a.data_hora at time zone tz_unidade(a.cod_unidade)
            between f_data_inicial and f_data_final
        order by a.codigo;
end
$$;