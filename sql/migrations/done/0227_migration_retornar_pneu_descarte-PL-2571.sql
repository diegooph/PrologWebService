create table pneu_retorno_descarte
(
    codigo                             bigserial                not null
        constraint pk_pneu_retorno_descarte
            primary key,
    cod_pneu                           bigint                   not null
        constraint fk_pneu_retorno_descarte
            references pneu_data (codigo),
    cod_processo_movimentacao_descarte bigint                   not null,
    cod_movimentacao_descarte          bigint                   not null,
    cod_colaborador_realizacao         bigint                   not null
        constraint fk_colaborador_retorno_descarte
            references colaborador_data (codigo),
    status_destino_resultado           varchar                  not null,
    motivo_retorno_descarte            text                     not null,
    data_hora_realizacao               timestamp with time zone not null
);


create or replace function func_pneu_retorna_pneu_descarte(f_cod_empresa bigint,
                                                           f_cod_unidade bigint,
                                                           f_cod_pneu bigint,
                                                           f_cod_colaborador_retorno_descarte bigint,
                                                           f_motivo_retorno_descarte text)
    returns table
            (
                cod_pneu_retornado                               bigint,
                destino_final_pneu_retornado                     varchar,
                cod_processo_movimentacao_utilizado_para_retorno bigint
            )
    language plpgsql
    security definer
as
$$
declare
    v_status_pneu_estoque       varchar := 'ESTOQUE';
    v_status_pneu_descarte      varchar := 'DESCARTE';
    v_status_pneu_em_uso        varchar := 'EM_USO';
    v_status_origem_pneu        varchar;
    v_status_destino_pneu       varchar;
    v_cod_movimentacao_processo bigint;
    v_cod_movimentacao          bigint;
    v_status_destino_final      varchar;
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

    if (select p.status
        from pneu p
        where p.codigo = f_cod_pneu
          and p.cod_unidade = f_cod_unidade) != v_status_pneu_descarte
    then
        raise exception 'Pneu de código % da unidade %, não está com status = %!',
            f_cod_pneu, f_cod_unidade, v_status_pneu_descarte;
    end if;

    select m.codigo                    as cod_movimentacao,
           m.cod_movimentacao_processo as cod_movimentacao_processo,
           mo.tipo_origem              as tipo_origem,
           md.tipo_destino             as tipo_destino
    from movimentacao m
             join movimentacao_destino md on m.codigo = md.cod_movimentacao
             join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
             join unidade u on u.codigo = m.cod_unidade
    where m.cod_pneu = f_cod_pneu
      and m.cod_unidade = f_cod_unidade
      and u.cod_empresa = f_cod_empresa
    order by m.codigo desc
    limit 1
    into v_cod_movimentacao, v_cod_movimentacao_processo, v_status_origem_pneu, v_status_destino_pneu;

    if v_status_destino_pneu != v_status_pneu_descarte
    then
        raise exception '[INCONSISTÊNCIA] A ultima movimentação do pneu código % da unidade %,
      não foi para %!', f_cod_pneu, f_cod_unidade, v_status_pneu_descarte;
    end if;

    if v_status_origem_pneu != v_status_pneu_em_uso
    then
        delete
        from movimentacao m
        where m.codigo = v_cod_movimentacao
          and m.cod_movimentacao_processo = v_cod_movimentacao_processo
          and m.cod_pneu = f_cod_pneu
          and m.cod_unidade = f_cod_unidade;

        if not exists(
                select m.codigo from movimentacao m where m.cod_movimentacao_processo = v_cod_movimentacao_processo)
        then
            delete
            from movimentacao_campo_personalizado_resposta
            where cod_processo_movimentacao = v_cod_movimentacao_processo;
            delete from movimentacao_processo where codigo = v_cod_movimentacao_processo;
        end if;
    else
        update movimentacao_destino
        set tipo_destino          = v_status_pneu_estoque,
            cod_motivo_descarte   = null,
            url_imagem_descarte_1 = null,
            url_imagem_descarte_2 = null,
            url_imagem_descarte_3 = null
        where cod_movimentacao = v_cod_movimentacao;
    end if;

    if v_status_origem_pneu != v_status_pneu_em_uso
    then
        update pneu
        set status = v_status_origem_pneu
        where codigo = f_cod_pneu
          and cod_unidade = f_cod_unidade
          and cod_empresa = f_cod_empresa
        returning status into v_status_destino_final;
    else
        update pneu
        set status = v_status_pneu_estoque
        where codigo = f_cod_pneu
          and cod_unidade = f_cod_unidade
          and cod_empresa = f_cod_empresa
        returning status into v_status_destino_final;
    end if;

    insert into pneu_retorno_descarte (cod_pneu,
                                       cod_processo_movimentacao_descarte,
                                       cod_movimentacao_descarte,
                                       cod_colaborador_realizacao,
                                       data_hora_realizacao,
                                       status_destino_resultado,
                                       motivo_retorno_descarte)
    values (f_cod_pneu,
            v_cod_movimentacao_processo,
            v_cod_movimentacao,
            f_cod_colaborador_retorno_descarte,
            now(),
            v_status_destino_final,
            f_motivo_retorno_descarte);

    return query
        select f_cod_pneu, v_status_destino_final, v_cod_movimentacao_processo;
end;
$$;