create or replace function suporte.func_afericao_altera_km_coletado_afericao(f_placa text,
                                                                             f_cod_afericao bigint,
                                                                             f_novo_km bigint,
                                                                             out aviso_km_afericao_alterado text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_qtd_linhas_atualizadas bigint;
    -- Não usa NOT NULL para não quebrar aqui com um erro não significativo para quem usar a function.
    v_cod_veiculo            bigint;
    v_km_atual               bigint;
begin
    select a.cod_veiculo, vd.km
    into v_cod_veiculo, v_km_atual
    from afericao a
             join veiculo_data vd
                  on vd.placa = f_placa and a.cod_veiculo = vd.codigo
    where a.codigo = f_cod_afericao;

    perform suporte.func_historico_salva_execucao();

    if (v_cod_veiculo is null)
    then
        raise exception 'Não foi possível encontrar a aferição realizada com estes parâmetros: Placa %,
                     Código da aferição %', f_placa, f_cod_afericao;
    end if;

    perform func_garante_novo_km_menor_que_atual_veiculo(v_cod_veiculo, f_novo_km);

    update afericao
    set km_veiculo = f_novo_km
    where codigo = f_cod_afericao
      and cod_veiculo = v_cod_veiculo;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    if (v_qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao atualizar o km da aferição com estes parâemtros: Placa %, Código
            da aferição %', f_placa, f_cod_afericao;
    end if;

    select 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO '
               || ', PLACA: '
               || f_placa
               || ', CÓDIGO DA AFERIÇÃO: '
               || f_cod_afericao
    into aviso_km_afericao_alterado;
end;
$$;