drop function if exists func_pneu_retorna_pneu_descarte(f_cod_empresa bigint,
    f_cod_unidade bigint,
    f_cod_pneu bigint,
    f_cod_colaborador_retorno_descarte bigint,
    f_motivo_retorno_descarte text);
create or replace function func_pneu_retorna_pneu_descarte(f_cod_empresa bigint,
                                                           f_cod_unidade bigint,
                                                           f_cod_pneu bigint,
                                                           f_cod_colaborador_retorno_descarte bigint,
                                                           f_motivo_retorno_descarte text)
    returns table
            (
                cod_pneu_retornado      bigint,
                cod_movimentacao_gerada bigint
            )
    language plpgsql
    security definer
as
$$
declare
    v_status_pneu_estoque  constant      varchar := 'ESTOQUE';
    v_status_pneu_descarte constant      varchar := 'DESCARTE';
    v_status_pneu                        varchar;
    v_cod_movimentacao_processo_inserida bigint;
    v_cod_movimentacao_inserida          bigint;
begin
    if (select not exists(
            select p.codigo
            from pneu p
                     inner join unidade u on u.codigo = f_cod_unidade
            where p.codigo = f_cod_pneu
              and p.cod_unidade = f_cod_unidade
              and u.cod_empresa = f_cod_empresa))
    then
        perform throw_client_side_error(format(
                'O pneu de código %s da unidade %s não foi encontrado e empresa %s,',
                f_cod_pneu,
                f_cod_unidade,
                f_cod_empresa));
    end if;

    select status
    into v_status_pneu
    from pneu
    where codigo = f_cod_pneu
      and cod_unidade = f_cod_unidade;
    if v_status_pneu != v_status_pneu_descarte
    then
        perform throw_client_side_error(format(
                '[INCONSISTÊNCIA] A ultima movimentação do pneu código %s da unidade %s, não foi para %s!',
                f_cod_pneu,
                f_cod_unidade,
                v_status_pneu_descarte));
    end if;

    insert into movimentacao_processo (cod_unidade,
                                       data_hora,
                                       cpf_responsavel,
                                       observacao)
    values (f_cod_unidade,
            now(),
            (select cpf from colaborador where codigo = f_cod_colaborador_retorno_descarte),
            f_motivo_retorno_descarte)
    returning codigo into v_cod_movimentacao_processo_inserida;

    v_cod_movimentacao_inserida := (
        select *
        from func_movimentacao_insere_movimentacao(f_cod_unidade,
                                                   v_cod_movimentacao_processo_inserida,
                                                   f_cod_pneu,
                                                   f_motivo_retorno_descarte));

    insert into movimentacao_origem (cod_movimentacao,
                                     tipo_origem,
                                     km_veiculo,
                                     posicao_pneu_origem,
                                     cod_diagrama,
                                     cod_veiculo)
    values (v_cod_movimentacao_inserida,
            v_status_pneu_descarte,
            null,
            null,
            null,
            null);

    insert into movimentacao_destino (cod_movimentacao,
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
    values (v_cod_movimentacao_inserida,
            v_status_pneu_estoque,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    update pneu
    set status = v_status_pneu_estoque
    where codigo = f_cod_pneu;

    insert into pneu_retorno_descarte (cod_pneu,
                                       cod_colaborador_realizacao,
                                       data_hora_realizacao,
                                       motivo_retorno_descarte)
    values (f_cod_pneu,
            f_cod_colaborador_retorno_descarte,
            now(),
            f_motivo_retorno_descarte);

    return query
        select f_cod_pneu, v_cod_movimentacao_inserida;
end;
$$;