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

create or replace function suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo text,
                                                                    f_cod_veiculo_tipo_novo bigint,
                                                                    f_cod_unidade bigint,
                                                                    f_informacoes_extras_suporte text,
                                                                    out aviso_tipo_veiculo_alterado text)
    returns text
    security definer
    language plpgsql
as
$$
declare
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    v_cod_diagrama_novo constant bigint := (select vt.cod_diagrama
                                            from veiculo_tipo vt
                                            where vt.codigo = f_cod_veiculo_tipo_novo);
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    V_cod_empresa       constant bigint := (select u.cod_empresa
                                            from unidade u
                                            where u.codigo = f_cod_unidade);
    v_cod_veiculo                bigint;
    v_identificador_frota_antigo text;
    v_km_antigo                  bigint;
    v_cod_diagrama_antigo        bigint;
    v_cod_tipo_antigo            bigint;
    v_cod_modelo_antigo          bigint;
    v_possui_hubodometro_antigo  boolean;
    v_status_antigo              boolean;
begin
    perform suporte.func_historico_salva_execucao();

    -- Garante que unidade/empresa existem.
    perform func_garante_unidade_existe(f_cod_unidade);

    -- Garante que veiculo existe e pertence a unidade sem considerar os deletados.
    perform func_garante_veiculo_existe(f_cod_unidade, f_placa_veiculo, false);

    -- Garante que tipo_veiculo_novo pertence a empresa.
    if not exists(select vt.codigo
                  from veiculo_tipo vt
                  where vt.codigo = f_cod_veiculo_tipo_novo
                    and vt.cod_empresa = V_cod_empresa)
    then
        raise exception
            'O tipo de veículo de código: % não pertence à empresa: %',
            f_cod_veiculo_tipo_novo,
            V_cod_empresa;
    end if;

    -- Busca os dados necessários para mandarmos para a function de update.
    select v.codigo,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.possui_hubodometro,
           v.status_ativo
    into strict
        v_cod_veiculo,
        v_identificador_frota_antigo,
        v_km_antigo,
        v_cod_diagrama_antigo,
        v_cod_tipo_antigo,
        v_cod_modelo_antigo,
        v_possui_hubodometro_antigo,
        v_status_antigo
    from veiculo v
    where v.placa = f_placa_veiculo
      and v.cod_unidade = f_cod_unidade;

    -- Verifica se placa tem pneus aplicados.
    if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_veiculo = v_cod_veiculo)
    then
        -- Se existirem pneus, verifica se os pneus que aplicados possuem as mesmas posições do novo tipo.
        if ((select array_agg(vp.posicao)
             from veiculo_pneu vp
             where vp.cod_veiculo = v_cod_veiculo) <@
            (select array_agg(vdpp.posicao_prolog :: integer)
             from veiculo_diagrama_posicao_prolog vdpp
             where cod_diagrama = v_cod_diagrama_novo) = false)
        then
            raise exception
                'Existem pneus aplicados em posições que não fazem parte do tipo de veículo de código: %',
                f_cod_veiculo_tipo_novo;
        end if;
    end if;

    -- Verifica se o tipo_veiculo_novo é o atual.
    if v_cod_tipo_antigo = f_cod_veiculo_tipo_novo
    then
        raise exception
            'O tipo de veículo atual da placa % é igual ao informado. Código tipo de veículo: %',
            f_placa_veiculo,
            f_cod_veiculo_tipo_novo;
    end if;

    if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_veiculo = v_cod_veiculo)
        and v_cod_diagrama_antigo <> v_cod_diagrama_novo
    then
        -- Assim conseguimos alterar o cod_diagrama na VEICULO_PNEU sem ele ainda estar alterado na tabela VEICULO_DATA.
        set constraints all deferred;

        update veiculo_pneu
        set cod_diagrama = v_cod_diagrama_novo
        where cod_veiculo = v_cod_veiculo
          and cod_unidade = f_cod_unidade
          and cod_diagrama = v_cod_diagrama_antigo;

        if (not found)
        then
            raise exception
                'Não foi possível modificar o cod_diagrama para a placa % no vínculo de veículo pneu.', f_placa_veiculo;
        end if;
    end if;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    v_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_veiculo,
                                                    v_identificador_frota_antigo,
                                                    v_km_antigo,
                                                    v_cod_diagrama_novo,
                                                    f_cod_veiculo_tipo_novo,
                                                    v_cod_modelo_antigo,
                                                    v_status_antigo,
                                                    v_possui_hubodometro_antigo,
                                                    (f_if(v_cod_tipo_antigo <> f_cod_veiculo_tipo_novo, 1, 0)
                                                        +
                                                     f_if(v_cod_diagrama_antigo <> v_cod_diagrama_novo, 1, 0))::smallint);

    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set cod_tipo     = f_cod_veiculo_tipo_novo,
        cod_diagrama = v_cod_diagrama_novo,
        foi_editado  = true
    where codigo = v_cod_veiculo
      and placa = f_placa_veiculo
      and cod_unidade = f_cod_unidade;

    -- Mensagem de sucesso.
    select 'Tipo do veículo alterado! ' ||
           'Placa: ' || f_placa_veiculo ||
           ', Código da unidade: ' || f_cod_unidade ||
           ', Tipo: ' || (select vt.nome from veiculo_tipo vt where vt.codigo = f_cod_veiculo_tipo_novo) ||
           ', Código do tipo: ' || f_cod_veiculo_tipo_novo || '.'
    into aviso_tipo_veiculo_alterado;
end;
$$;

create or replace function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
                                                               f_placa varchar(255),
                                                               f_motivo_delecao text,
                                                               out dependencias_deletadas text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_codigo_loop                   bigint;
    v_lista_cod_afericao_placa      bigint[];
    v_lista_cod_check_placa         bigint[];
    v_lista_cod_prolog_deletado_cos bigint[];
    v_nome_empresa                  varchar(255) := (select e.nome
                                                     from empresa e
                                                     where e.codigo =
                                                           (select u.cod_empresa
                                                            from unidade u
                                                            where u.codigo = f_cod_unidade));
    v_nome_unidade                  varchar(255) := (select u.nome
                                                     from unidade u
                                                     where u.codigo = f_cod_unidade);
    v_cod_veiculo                   bigint       := (select codigo
                                                     from veiculo v
                                                     where v.placa = f_placa
                                                       and v.cod_unidade = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veiculo possui pneus aplicados.
    if exists(select vp.cod_pneu
              from veiculo_pneu vp
              where cod_veiculo = v_cod_veiculo and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se possui acoplamento.
    if EXISTS(select vd.codigo
              from veiculo_data vd
              where vd.placa = f_placa
                and vd.cod_unidade = f_cod_unidade
                and vd.acoplado is true)
    then
        raise exception 'Erro! A Placa: % possui acoplamentos. Favor removê-los', f_placa;
    end if;

    -- Verifica se placa possui aferição. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select a.codigo from afericao_data a where a.cod_veiculo = v_cod_veiculo)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Deletamos aferição em afericao_manutencao_data, caso não esteja deletada.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição em afericao_valores_data, caso não esteja deletada.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição, caso não esteja deletada.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- Verifica se placa possui checklist. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select c.placa_veiculo from checklist_data c where c.deletado = false and c.placa_veiculo = f_placa)
    then
        -- Busca todos os códigos de checklists da placa.
        select array_agg(c.codigo)
        from checklist_data c
        where c.deletado = false
          and c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- Deleta todos os checklists da placa. Usamos deleção lógica em conjunto com uma tabela de deleção específica.
        insert into checklist_delecao (cod_checklist,
                                       cod_colaborador,
                                       data_hora,
                                       acao_executada,
                                       origem_delecao,
                                       observacao,
                                       pg_username_delecao)
        select unnest(v_lista_cod_check_placa),
               null,
               now(),
               'DELETADO',
               'SUPORTE',
               f_motivo_delecao,
               session_user;

        update checklist_data set deletado = true where codigo = any (v_lista_cod_check_placa);

        -- Usamos, obrigatoriamente, a view checklist_ordem_servico para
        -- evitar de tentar deletar OSs que estão deletadas.
        if exists(select cos.codigo
                  from checklist_ordem_servico cos
                  where cos.cod_checklist = any (v_lista_cod_check_placa))
        then
            -- Deleta ordens de serviços dos checklists.
            update checklist_ordem_servico_data
            set deletado            = true,
                data_hora_deletado  = now(),
                pg_username_delecao = session_user,
                motivo_delecao      = f_motivo_delecao
            where deletado = false
              and cod_checklist = any (v_lista_cod_check_placa);

            -- Busca os codigo Prolog deletados nas Ordens de Serviços.
            select array_agg(codigo_prolog)
            from checklist_ordem_servico_data
            where cod_checklist = any (v_lista_cod_check_placa)
              and deletado is true
            into v_lista_cod_prolog_deletado_cos;

            -- Para cada código prolog deletado em cos, deletamos o referente na cosi.
            foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
                loop
                    -- Deleta em cosi aqueles que foram deletados na cos.
                    update checklist_ordem_servico_itens_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user,
                        motivo_delecao      = f_motivo_delecao
                    where deletado = false
                      and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                                   from checklist_ordem_servico_data cos
                                                   where cos.codigo_prolog = v_codigo_loop);
                end loop;
        end if;
    end if;

    -- Verifica se a placa é integrada.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa (não possuímos deleção lógica).
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- Realiza deleção da placa.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = now(),
        pg_username_delecao = session_user,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- Mensagem de sucesso.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;

create or replace function suporte.func_pneu_remove_vinculo_pneu(f_cpf_solicitante bigint,
                                                                 f_cod_unidade bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_lista_cod_pneus bigint[],
                                                                 out aviso_pneus_desvinculados text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    status_pneu_estoque              text                     := 'ESTOQUE';
    status_pneu_em_uso               text                     := 'EM_USO';
    data_hora_atual                  timestamp with time zone := now();
    cod_pneu_da_vez                  bigint;
    cod_movimentacao_criada          bigint;
    cod_processo_movimentacao_criado bigint;
    vida_atual_pneu                  bigint;
    posicao_pneu                     integer;
    km_atual_veiculo                 bigint                   := (select v.km
                                                                  from veiculo v
                                                                  where v.cod_unidade = f_cod_unidade
                                                                    and v.codigo = f_cod_veiculo);
    nome_colaborador                 text                     := (select c.nome
                                                                  from colaborador c
                                                                  where c.cpf = f_cpf_solicitante);
begin
    perform suporte.func_historico_salva_execucao();
    -- verifica se colaborador possui integridade com unidade;
    perform func_garante_integridade_unidade_colaborador(f_cod_unidade, f_cpf_solicitante);

    -- verifica se unidade existe;
    perform func_garante_unidade_existe(f_cod_unidade);

    -- verifica se veículo existe;
    perform func_garante_veiculo_existe_by_codigo(f_cod_unidade, f_cod_veiculo);

    -- verifica quantiade de pneus recebida;
    if (array_length(f_lista_cod_pneus, 1) > 0)
    then
        -- cria processo para movimentação
        insert into movimentacao_processo(cod_unidade, data_hora, cpf_responsavel, observacao)
        values (f_cod_unidade,
                data_hora_atual,
                f_cpf_solicitante,
                'Processo para desvincular o pneu de uma placa')
        returning codigo into cod_processo_movimentacao_criado;

        foreach cod_pneu_da_vez in array f_lista_cod_pneus
            loop
                -- verifica se pneu não está vinculado a placa informada;
                if not exists(select vp.cod_veiculo
                              from veiculo_pneu vp
                              where vp.cod_veiculo = f_cod_veiculo
                                and vp.cod_pneu = cod_pneu_da_vez)
                then
                    raise exception 'Erro! O pneu com código: % não está vinculado ao veículo de código %',
                        cod_pneu_da_vez, f_cod_veiculo;
                end if;

                -- busca vida atual e posicao do pneu;
                select p.vida_atual, vp.posicao
                from pneu p
                         join veiculo_pneu vp on p.codigo = vp.cod_pneu
                where p.codigo = cod_pneu_da_vez
                into vida_atual_pneu, posicao_pneu;

                if (cod_processo_movimentacao_criado > 0)
                then
                    -- insere movimentação retornando o código da mesma;
                    insert into movimentacao(cod_movimentacao_processo,
                                             cod_unidade,
                                             cod_pneu,
                                             sulco_interno,
                                             sulco_central_interno,
                                             sulco_externo,
                                             vida,
                                             observacao,
                                             sulco_central_externo)
                    select cod_processo_movimentacao_criado,
                           f_cod_unidade,
                           cod_pneu_da_vez,
                           p.altura_sulco_interno,
                           p.altura_sulco_central_interno,
                           p.altura_sulco_externo,
                           vida_atual_pneu,
                           null,
                           p.altura_sulco_central_externo
                    from pneu p
                    where p.codigo = cod_pneu_da_vez
                    returning codigo into cod_movimentacao_criada;

                    -- insere destino da movimentação;
                    insert into movimentacao_destino(cod_movimentacao, tipo_destino)
                    values (cod_movimentacao_criada, status_pneu_estoque);

                    -- insere origem da movimentação;
                    perform func_movimentacao_insert_movimentacao_veiculo_origem(cod_pneu_da_vez,
                                                                                 f_cod_unidade,
                                                                                 status_pneu_em_uso,
                                                                                 cod_movimentacao_criada,
                                                                                 f_cod_veiculo,
                                                                                 km_atual_veiculo,
                                                                                 posicao_pneu);

                    -- remove pneu do vinculo;
                    delete from veiculo_pneu where cod_pneu = cod_pneu_da_vez and cod_veiculo = f_cod_veiculo;

                    -- atualiza status do pneu
                    update pneu
                    set status = status_pneu_estoque
                    where codigo = cod_pneu_da_vez
                      and cod_unidade = f_cod_unidade;

                    -- verifica se o pneu possui serviços em aberto;
                    if exists(select am.cod_pneu
                              from afericao_manutencao am
                              where am.cod_unidade = f_cod_unidade
                                and am.cod_pneu = cod_pneu_da_vez
                                and am.data_hora_resolucao is null
                                and am.cpf_mecanico is null
                                and am.fechado_automaticamente_movimentacao is false
                                and am.fechado_automaticamente_integracao is false)
                    then
                        -- remove serviços em aberto;
                        update afericao_manutencao
                        set fechado_automaticamente_movimentacao = true,
                            cod_processo_movimentacao            = cod_processo_movimentacao_criado,
                            data_hora_resolucao                  = data_hora_atual
                        where cod_unidade = f_cod_unidade
                          and cod_pneu = cod_pneu_da_vez
                          and data_hora_resolucao is null
                          and cpf_mecanico is null
                          and fechado_automaticamente_movimentacao is false
                          and fechado_automaticamente_integracao is false;
                    end if;
                else
                    raise exception 'Erro! Não foi possível realizar o processo de movimentação para o pneu código: %',
                        cod_pneu_da_vez;
                end if;
            end loop;
    else
        raise exception 'Erro! Precisa-se de pelo menos um (1) pneu para realizar a operação!';
    end if;

    -- mensagem de sucesso;
    select 'Movimentação realizada com sucesso!! Autorizada por ' || nome_colaborador ||
           ' com CPF: ' || f_cpf_solicitante || '. Os pneus que estavam na placa de código ' || f_cod_veiculo ||
           ' foram movidos para estoque.'
    into aviso_pneus_desvinculados;
end
$$;

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
begin
    perform suporte.func_historico_salva_execucao();
    raise exception 'O veiculo pode ser cadastrado com a mesma placa na nova empresa. Não é necessário utilizar esse procedimento.';
end
$$;