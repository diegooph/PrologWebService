create or replace function func_veiculo_relatorio_evolucao_km_consolidado(f_cod_empresa bigint,
                                                                          f_cod_veiculo bigint)
    returns table
            (
                "PROCESSO"                               text,
                "CÓDIGO PROCESSO"                        text,
                "DATA/HORA"                              text,
                "PLACA"                                  text,
                "KM COLETADO"                            text,
                "VARIAÇÃO KM ENTRE COLETAS"              text,
                "KM ATUAL"                               text,
                "DIFERENÇA ENTRE KM ATUAL E KM COLETADO" text
            )
    language plpgsql
as
$$
begin
    return query
        select pek.processo_legivel ::text,
               func.cod_processo ::text,
               to_char(func.data_hora, 'DD/MM/YYYY HH24:MI')::text,
               func.placa ::text,
               func.km_coletado ::text,
               func.variacao_km_entre_coletas :: text,
               func.km_atual ::text,
               func.diferenca_km_atual_km_coletado ::text
        from func_veiculo_busca_evolucao_km_consolidado(
                     f_cod_empresa,
                     f_cod_veiculo,
                     null,
                     null) as func
                 join types.processo_evolucao_km pek on pek.processo = func.processo;
end;
$$;