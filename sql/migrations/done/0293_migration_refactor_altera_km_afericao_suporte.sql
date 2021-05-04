drop function suporte.func_afericao_altera_km_coletado_afericao(f_placa text,
    f_cod_afericao bigint,
    f_novo_km bigint);
create or replace function suporte.func_afericao_altera_km_coletado_afericao(f_cod_veiculo bigint,
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
begin
    perform suporte.func_historico_salva_execucao();
    if (select not exists(select codigo from afericao where codigo = f_cod_afericao and cod_veiculo = f_cod_veiculo))
    then
        raise exception 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código Veículo %,
                     Código da aferição %', f_cod_veiculo, f_cod_afericao;
    end if;

    perform func_garante_novo_km_menor_que_atual_veiculo(f_cod_veiculo, f_novo_km);

    update afericao
    set km_veiculo = f_novo_km
    where codigo = f_cod_afericao
      and cod_veiculo = f_cod_veiculo;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    if (v_qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao atualizar o km da aferição com estes parâemtros: Código Veículo %, Código
            da aferição %', f_cod_veiculo, f_cod_afericao;
    end if;

    select 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO '
               || ', CODIGO VEÍCULO: '
               || f_cod_veiculo
               || ', CÓDIGO DA AFERIÇÃO: '
               || f_cod_afericao
    into aviso_km_afericao_alterado;
end;
$$;