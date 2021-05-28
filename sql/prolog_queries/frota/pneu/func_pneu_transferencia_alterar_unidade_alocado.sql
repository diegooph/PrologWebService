create function func_pneu_transferencia_alterar_unidade_alocado(f_cod_unidade_origem bigint,
                                                                f_cod_unidade_destino bigint,
                                                                f_cod_pneus bigint[]) returns bigint
    language plpgsql
as
$$
declare
    qtd_rows bigint;
begin
    update pneu
    set cod_unidade = f_cod_unidade_destino
    where cod_unidade = f_cod_unidade_origem
      and codigo = any (f_cod_pneus);
    get diagnostics qtd_rows = row_count;
    return qtd_rows;
end;
$$;