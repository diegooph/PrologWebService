-- Sobre:
--
-- Altera a data/hora de um processo de movimentação.
--
-- Histórico:
-- 2020-09-30 -> Function criada (luiz_fp - PS-1260).
create or replace function suporte.func_movimentacao_altera_data_hora_movimentacao(f_cod_unidade_movimentacao bigint,
                                                                                   f_cod_movimentacao_processo bigint,
                                                                                   f_nova_data_hora_movimentacao timestamp without time zone,
                                                                                   f_informacoes_extras_suporte text,
                                                                                   out f_aviso_alterar_data text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    -- Não usamos 'not null' porque não queremos quebrar aqui, temos validações com mensagens específicas para isso.
    v_antiga_data_hora      constant timestamp with time zone := (select mp.data_hora
                                                                  from movimentacao_processo mp
                                                                  where mp.codigo = f_cod_movimentacao_processo);
    -- Não usamos 'not null' porque não queremos quebrar aqui, temos validações com mensagens específicas para isso.
    v_nova_data_hora_com_tz constant timestamp with time zone := f_nova_data_hora_movimentacao at time zone
                                                                 tz_unidade(f_cod_unidade_movimentacao);
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade_movimentacao);
    perform func_garante_movimentacao_processo_existe(f_cod_movimentacao_processo);


    -- Verifica se o processo de movimentação é da unidade informada.
    if (select mp.cod_unidade
        from movimentacao_processo mp
        where mp.codigo = f_cod_movimentacao_processo) <> f_cod_unidade_movimentacao
    then
        raise exception
            'O processo de movimentação de código % não é da unidade %.',
            f_cod_movimentacao_processo,
            f_cod_unidade_movimentacao;
    end if;

    if v_antiga_data_hora = v_nova_data_hora_com_tz
    then
        raise exception
            'A data/hora informada (%) já está definida no processo de movimentação.', f_nova_data_hora_movimentacao;
    end if;

    update movimentacao_processo
    set data_hora = v_nova_data_hora_com_tz
    where codigo = f_cod_movimentacao_processo
      and cod_unidade = f_cod_unidade_movimentacao;

    if not found
    then
        raise exception
            'Erro ao atualizar a data/hora do processo de movimentação, tente novamente.';
    end if;

    select 'O processo de movimentação de código '
               || f_cod_movimentacao_processo ||
           ' teve sua data/hora alterada de '
               || v_antiga_data_hora at time zone tz_unidade(f_cod_unidade_movimentacao) ||
           ' para '
               || f_nova_data_hora_movimentacao ||
           ' com sucesso!'
    into f_aviso_alterar_data;
end ;
$$;