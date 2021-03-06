create or replace function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar(7),
                                                                                 f_cod_empresa_origem bigint,
                                                                                 f_cod_unidade_origem bigint,
                                                                                 f_cod_empresa_destino bigint,
                                                                                 f_cod_unidade_destino bigint,
                                                                                 f_cod_modelo_veiculo_destino bigint,
                                                                                 f_cod_tipo_veiculo_destino bigint,
                                                                                 out veiculo_transferido text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_nome_empresa_destino                           varchar(255) := (select e.nome
                                                                      from empresa e
                                                                      where e.codigo = f_cod_empresa_destino);
    v_nome_unidade_destino                           varchar(255) := (select u.nome
                                                                      from unidade u
                                                                      where u.codigo = f_cod_unidade_destino);
    v_lista_cod_oss_check                            bigint[];
    v_lista_cod_afericao_placa                       bigint[];
    v_cod_afericao_foreach                           bigint;
    v_lista_cod_pneu_em_afericao_manutencao          bigint[];
    v_qtd_cod_afericao_em_afericao_valores           bigint;
    v_qtd_cod_afericao_deletados_em_afericao_valores bigint;
    v_cod_veiculo                                    bigint       := (select v.codigo
                                                                      from veiculo v
                                                                      where v.placa = f_placa_veiculo
                                                                        and v.cod_unidade = f_cod_unidade_origem);
begin
    perform suporte.func_historico_salva_execucao();

    -- Verifica se empresa origem possui unidade origem.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_origem, f_cod_unidade_origem);

    -- Verifica se empresa destino possui unidade destino.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_destino, f_cod_unidade_destino);

    perform func_garante_empresas_distintas(f_cod_empresa_origem, f_cod_empresa_destino);
    perform func_garante_veiculo_existe(f_cod_unidade_origem, f_placa_veiculo);

    -- Verifica se a placa possui pneus.
    if exists(select vp.cod_pneu
              from veiculo_pneu vp
              where vp.cod_veiculo = v_cod_veiculo
                and vp.cod_unidade = f_cod_unidade_origem)
    then
        raise exception 'Erro! A placa: % possui pneus vinculados, favor remover os pneus do mesmo', f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui tipo do ve??culo informado.
    if not exists(
            select vt.codigo
            from veiculo_tipo vt
            where vt.cod_empresa = f_cod_empresa_destino
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception 'Erro! O c??digo tipo: % n??o existe na empresa destino: %', f_cod_tipo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se o tipo de ve??culo informado tem o mesmo diagrama do ve??culo.
    if not exists(
            select v.codigo
            from veiculo v
                     join veiculo_tipo vt on v.cod_diagrama = vt.cod_diagrama
            where v.codigo = v_cod_veiculo
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception
            'Erro! O diagrama do tipo: % ?? diferente do ve??culo: %', f_cod_tipo_veiculo_destino, f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui modelo do ve??culo informado.
    if not exists(select mv.codigo
                  from modelo_veiculo mv
                  where mv.cod_empresa = f_cod_empresa_destino
                    and mv.codigo = f_cod_modelo_veiculo_destino)
    then
        raise exception 'Erro! O c??digo modelo: % n??o existe na empresa destino: %', f_cod_modelo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se placa possui aferi????o.
    if exists(select a.codigo
              from afericao a
              where a.cod_veiculo = v_cod_veiculo)
    then
        -- Ent??o coletamos todos os c??digos das aferi????es que a placa possui e adicionamos no array.
        select distinct array_agg(a.codigo)
        from afericao a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- La??o for para percorrer todos os valores em f_lista_cod_afericao_placa.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                -- Para cada valor em: f_lista_cod_afericao_placa.
                if exists(select am.cod_afericao
                          from afericao_manutencao am
                          where am.cod_afericao = v_cod_afericao_foreach
                            and am.data_hora_resolucao is null
                            and am.fechado_automaticamente_integracao is false
                            and am.fechado_automaticamente_movimentacao is false)
                then
                    -- Coleta o(s) cod_pneu correspondentes ao cod_afericao.
                    select array_agg(am.cod_pneu)
                    from afericao_manutencao am
                    where am.cod_afericao = v_cod_afericao_foreach
                      and am.data_hora_resolucao is null
                      and am.fechado_automaticamente_integracao is false
                      and am.fechado_automaticamente_movimentacao is false
                    into v_lista_cod_pneu_em_afericao_manutencao;

                    -- Deleta aferi????o em afericao_manutencao_data atrav??s do cod_afericao e cod_pneu.
                    update afericao_manutencao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);

                    -- Deleta afericao em afericao_valores_data atrav??s do cod_afericao e cod_pneu.
                    update afericao_valores_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);
                end if;
            end loop;

        -- Se, e somente se, a aferi????o possuir todos os valores exclu??dos, deve-se excluir toda a aferi????o.
        -- Sen??o, a aferi????o continua existindo.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                v_qtd_cod_afericao_em_afericao_valores = (select count(avd.cod_afericao)
                                                          from afericao_valores_data avd
                                                          where avd.cod_afericao = v_cod_afericao_foreach);

                v_qtd_cod_afericao_deletados_em_afericao_valores = (select count(avd.cod_afericao)
                                                                    from afericao_valores_data avd
                                                                    where avd.cod_afericao = v_cod_afericao_foreach
                                                                      and avd.deletado is true);

                -- Se a quantidade de um cod_afericao em afericao_valores_data for igual a quantidade de um cod_afericao
                -- deletado em afericao_valores_data, devemos excluir a aferi????o, pois, todos seus valores foram
                -- deletados.
                if (v_qtd_cod_afericao_em_afericao_valores =
                    v_qtd_cod_afericao_deletados_em_afericao_valores)
                then
                    update afericao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and codigo = v_cod_afericao_foreach;
                end if;
            end loop;
    end if;

    -- Se possuir itens de OS aberto, deletamos esses itens.
    select array_agg(cos.codigo_prolog)
    from checklist c
             join checklist_ordem_servico cos
                  on c.codigo = cos.cod_checklist
    where c.cod_veiculo = v_cod_veiculo
      and cos.status = 'A'
    into v_lista_cod_oss_check;

    if (f_size_array(v_lista_cod_oss_check) > 0)
    then
        -- Deletamos primeiro as OSs.
        update checklist_ordem_servico_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transfer??ncia de ve??culo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
        where codigo_prolog = any (v_lista_cod_oss_check);

        -- Agora deletamos os itens.
        update checklist_ordem_servico_itens_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transfer??ncia de ve??culo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
            -- Precisamos usar a _DATA nesse where pois j?? deletamos as OSs.
        where (cod_os, cod_unidade) in (select cosd.codigo, cosd.cod_unidade
                                        from checklist_ordem_servico_data cosd
                                        where cosd.codigo_prolog = any (v_lista_cod_oss_check));
    end if;

    -- Se o ve??culo for integrado, atualiza os dados de empresa e unidade na tabela de integra????o.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_empresa_cadastro = f_cod_empresa_origem
                and ivc.cod_unidade_cadastro = f_cod_unidade_origem
                and ivc.cod_veiculo_cadastro_prolog = v_cod_veiculo)
    then
        update integracao.veiculo_cadastrado
        set cod_unidade_cadastro = f_cod_unidade_destino,
            cod_empresa_cadastro = f_cod_empresa_destino
        where cod_empresa_cadastro = f_cod_empresa_origem
          and cod_unidade_cadastro = f_cod_unidade_origem
          and cod_veiculo_cadastro_prolog = v_cod_veiculo;
    end if;

    -- Realiza transfer??ncia.
    update veiculo
    set cod_empresa = f_cod_empresa_destino,
        cod_unidade = f_cod_unidade_destino,
        cod_tipo    = f_cod_tipo_veiculo_destino,
        cod_modelo  = f_cod_modelo_veiculo_destino
    where cod_empresa = f_cod_empresa_origem
      and cod_unidade = f_cod_unidade_origem
      and codigo = v_cod_veiculo;

    -- Mensagem de sucesso.
    select 'Ve??culo transferido com sucesso! O ve??culo com placa: ' || f_placa_veiculo ||
           ' foi transferido para a empresa ' || v_nome_empresa_destino || ' junto a unidade ' ||
           v_nome_unidade_destino || '.'
    into veiculo_transferido;
end
$$;