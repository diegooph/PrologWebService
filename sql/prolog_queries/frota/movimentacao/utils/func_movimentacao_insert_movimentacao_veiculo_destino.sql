create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino text,
                                                                                 f_cod_veiculo bigint,
                                                                                 f_posicao_prolog bigint)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
    v_km_atual                   bigint;
begin
    select v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo v
    where v.codigo = f_cod_veiculo
    into strict
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    -- Realiza inserção da movimentação destino.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
                                     km_veiculo,
                                     posicao_pneu_destino,
                                     cod_motivo_descarte,
                                     url_imagem_descarte_1,
                                     url_imagem_descarte_2,
                                     url_imagem_descarte_3,
                                     cod_recapadora_destino,
                                     cod_coleta,
                                     cod_diagrama,
                                     cod_veiculo)
    values (f_cod_movimentacao,
            f_tipo_destino,
            v_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;