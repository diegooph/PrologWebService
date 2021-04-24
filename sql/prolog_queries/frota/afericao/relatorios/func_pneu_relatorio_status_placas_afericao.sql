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