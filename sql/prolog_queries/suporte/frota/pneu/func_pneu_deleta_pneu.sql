create or replace function suporte.func_pneu_deleta_pneu(f_cod_unidade bigint,
                                                         f_codigo_pneu bigint,
                                                         f_codigo_cliente text,
                                                         f_motivo_delecao text,
                                                         out aviso_pneu_deletado text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_status_pneu_analise constant   text := 'ANALISE';
    v_qtd_linhas_atualizadas         bigint;
    v_cod_afericao                   bigint[];
    v_cod_afericao_foreach           bigint;
    v_qtd_afericao_valores           bigint;
    v_qtd_afericao_valores_deletados bigint;
begin
    perform suporte.func_historico_salva_execucao();
    -- verifica se o pneu existe.
    if ((select count(p.codigo)
         from pneu_data p
         where p.codigo = f_codigo_pneu
           and p.cod_unidade = f_cod_unidade
           and p.codigo_cliente = f_codigo_cliente) <= 0)
    then
        raise exception 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
    end if;

    -- verifica se o pneu está aplicado.
    if ((select count(vp.cod_veiculo)
         from veiculo_pneu vp
         where vp.cod_pneu = f_codigo_pneu
           and vp.cod_unidade = f_cod_unidade) > 0)
    then
        raise exception 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
    end if;

    -- verifica se o pneu está em análise.
    if ((select count(p.codigo)
         from pneu_data p
         where p.codigo = f_codigo_pneu
           and p.cod_unidade = f_cod_unidade
           and p.codigo_cliente = f_codigo_cliente
           and p.status = v_status_pneu_analise) > 0)
    then
        raise exception 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
    end if;

    -- verifica se pneu é integrado
    if exists(select ipc.cod_pneu_cadastro_prolog
              from integracao.pneu_cadastrado ipc
              where ipc.cod_pneu_cadastro_prolog = f_codigo_pneu
                and ipc.cod_unidade_cadastro = f_cod_unidade)
    then
        -- deleta pneu (não temos deleção lógica)
        delete
        from integracao.pneu_cadastrado
        where cod_pneu_cadastro_prolog = f_codigo_pneu
          and cod_unidade_cadastro = f_cod_unidade;
    end if;

    -- deleta pneu prolog.
    update pneu_data
    set deletado            = true,
        data_hora_deletado  = now(),
        pg_username_delecao = session_user,
        motivo_delecao      = f_motivo_delecao
    where codigo = f_codigo_pneu
      and cod_unidade = f_cod_unidade
      and codigo_cliente = f_codigo_cliente;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    if (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
    end if;

    -- verifica se o pneu está em afericao_manutencao_data.
    if (select exists(select am.cod_afericao
                      from afericao_manutencao_data am
                      where am.cod_pneu = f_codigo_pneu
                        and am.cod_unidade = f_cod_unidade
                        and am.deletado = false))
    then
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where cod_pneu = f_codigo_pneu
          and cod_unidade = f_cod_unidade
          and deletado = false;

        get diagnostics v_qtd_linhas_atualizadas = row_count;

        -- garante que a deleção foi realizada.
        if (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0)
        then
            raise exception 'Erro ao deletar o pneu de Código %, Código Cliente % e Unidade % '
                'em afericao_manutencao_data', f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
        end if;
    end if;

    -- verifica se o pneu está em afericao_valores_data.
    if (select exists(select av.cod_afericao
                      from afericao_valores_data av
                      where av.cod_pneu = f_codigo_pneu
                        and av.cod_unidade = f_cod_unidade
                        and av.deletado = false))
    then
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where cod_pneu = f_codigo_pneu
          and cod_unidade = f_cod_unidade
          and deletado = false;

        get diagnostics v_qtd_linhas_atualizadas = row_count;

        -- garante que a deleção foi realizada.
        if (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0)
        then
            raise exception 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade % em afericao_valores_data',
                f_codigo_pneu, f_codigo_cliente, f_cod_unidade;
        end if;
    end if;

    --busca todos os cod_afericao deletados a partir do pneu.
    select array_agg(av.cod_afericao)
    from afericao_valores_data av
    where av.cod_pneu = f_codigo_pneu
      and av.cod_unidade = f_cod_unidade
      and av.deletado is true
    into v_cod_afericao;

    -- verifica se algum valor foi deletado em afericao_valores_data.
    if (v_cod_afericao is not null and array_length(v_cod_afericao, 1) > 0)
    then
        -- iteração com cada cod_afericao deletado em afericao_valores_data.
        foreach v_cod_afericao_foreach in array v_cod_afericao
            loop
                -- coleta a quantidade de aferições em afericao_valores_data.
                v_qtd_afericao_valores = (select count(avd.cod_afericao)
                                          from afericao_valores_data avd
                                          where avd.cod_afericao = v_cod_afericao_foreach);

                -- coleta a quantidade de aferições deletadas em afericao_valores_data.
                v_qtd_afericao_valores_deletados = (select count(avd.cod_afericao)
                                                    from afericao_valores_data avd
                                                    where avd.cod_afericao = v_cod_afericao_foreach
                                                      and avd.deletado is true);

                -- verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                if (v_qtd_afericao_valores = v_qtd_afericao_valores_deletados)
                then
                    update afericao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user,
                        motivo_delecao      = f_motivo_delecao
                    where codigo = v_cod_afericao_foreach;

                    get diagnostics v_qtd_linhas_atualizadas = row_count;

                    -- garante que a deleção foi realizada.
                    if (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0)
                    then
                        raise exception 'Erro ao deletar aferição com Código: %, Unidade: %',
                            v_cod_afericao_foreach, f_cod_unidade;
                    end if;
                end if;
            end loop;
    end if;

    select 'PNEU DELETADO: '
               || f_codigo_pneu
               || ', CÓDIGO DO CLIENTE: '
               || f_codigo_cliente
               || ', CÓDIGO DA UNIDADE: '
               || f_cod_unidade
    into aviso_pneu_deletado;
end
$$;