create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem text,
                                                                                f_cod_movimentacao bigint,
                                                                                f_cod_veiculo bigint,
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_km_atual                   bigint;
    v_cod_movimentacao_realizada bigint;
    v_tipo_origem_atual constant text := (select p.status
                                          from pneu p
                                          where p.codigo = f_cod_pneu
                                            and p.cod_unidade = f_cod_unidade
                                            and f_tipo_origem in (select p.status
                                                                  from pneu p
                                                                  where p.codigo = f_cod_pneu
                                                                    and p.cod_unidade = f_cod_unidade));
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

    -- Realiza inserção da movimentação origem.
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            v_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;