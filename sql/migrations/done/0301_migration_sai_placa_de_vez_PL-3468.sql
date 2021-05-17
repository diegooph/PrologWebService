-- PL-3614
alter table veiculo_data
    add constraint unique_cod_veiculo_unidade unique (codigo, cod_unidade);

alter table veiculo_pneu
    drop constraint if exists veiculo_pneu_placa_posicao_key;

alter table veiculo_pneu
    add constraint unique_cod_veiculo_posicao unique (cod_veiculo, posicao);

alter table veiculo_pneu
    drop constraint if exists fk_veiculo_pneu_cod_veiculo_placa;

alter table veiculo_pneu
    add constraint fk_veiculo_pneu_cod_veiculo
        foreign key (cod_veiculo, cod_unidade) references veiculo_data (codigo, cod_unidade) deferrable;

alter table veiculo_pneu
    drop constraint if exists pk_veiculo_pneu;

alter table veiculo_pneu
    add constraint pk_veiculo_pneu primary key (cod_veiculo, cod_pneu);

alter table veiculo_pneu
    drop column if exists placa;


-- PL-3615
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
              where cod_veiculo = v_cod_veiculo
                and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se possui acoplamento.
    if exists(select vd.codigo
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
    if exists(select c.cod_veiculo from checklist_data c where c.deletado = false and c.cod_veiculo = v_cod_veiculo)
    then
        -- Busca todos os códigos de checklists da placa.
        select array_agg(c.codigo)
        from checklist_data c
        where c.deletado = false
          and c.cod_veiculo = v_cod_veiculo
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

create or replace function suporte.func_veiculo_altera_placa(f_cod_unidade_veiculo bigint,
                                                             f_cod_veiculo bigint,
                                                             f_placa_antiga text,
                                                             f_placa_nova text,
                                                             f_informacoes_extras_suporte text,
                                                             f_forcar_atualizacao_placa_integracao boolean default false,
                                                             out f_aviso_placa_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_empresa         bigint;
    v_identificador_frota text;
    v_km                  bigint;
    v_cod_diagrama        bigint;
    v_cod_tipo            bigint;
    v_cod_modelo          bigint;
    v_status              boolean;
    v_possui_hubodometro  boolean;
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade_veiculo);
    perform func_garante_veiculo_existe(f_cod_unidade_veiculo, f_placa_antiga);

    -- Verifica se placa nova está disponível.
    if exists(select vd.placa from veiculo_data vd where vd.placa = f_placa_nova)
    then
        raise exception
            'A placa % já existe no banco.', f_placa_nova;
    end if;

    -- Verifica se a placa é de integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        -- Verifica se deve alterar placa em integração.
        if (f_forcar_atualizacao_placa_integracao is false)
        then
            raise exception
                'A placa % pertence à integração. para atualizar a mesma, deve-se passar true como parâmetro.',
                f_placa_antiga;
        end if;
    end if;

    -- Agora alteramos a placa.
    select v.cod_empresa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_empresa,
        v_identificador_frota,
        v_km,
        v_cod_diagrama,
        v_cod_tipo,
        v_cod_modelo,
        v_status,
        v_possui_hubodometro
    from veiculo v
    where v.codigo = f_cod_veiculo
      and v.cod_unidade = f_cod_unidade_veiculo;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    f_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_nova,
                                                    v_identificador_frota,
                                                    v_km,
                                                    v_cod_diagrama, -- Apenas a placa mudou.
                                                    v_cod_tipo,
                                                    v_cod_modelo,
                                                    v_status,
                                                    v_possui_hubodometro,
                                                    1::smallint);
    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set placa       = f_placa_nova,
        foi_editado = true
    where codigo = f_cod_veiculo
      and placa = f_placa_antiga
      and cod_unidade = f_cod_unidade_veiculo;

    -- Modifica placa na integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        update integracao.veiculo_cadastrado
        set placa_veiculo_cadastro = f_placa_nova
        where placa_veiculo_cadastro = f_placa_antiga;

        if (not found)
        then
            raise exception
                'Não foi possível modificar a placa para % na tabela de integração VEICULO_CADASTRADO.', f_placa_nova;
        end if;
    end if;


    select 'A placa foi alterada de '
               || f_placa_antiga ||
           ' para '
               || f_placa_nova || '.'
    into f_aviso_placa_alterada;
end ;
$$;



-- PL-3616
create or replace function interno.func_reseta_empresa_apresentacao(f_cod_empresa_base bigint,
                                                                    f_cod_empresa_usuario bigint,
                                                                    out mensagem_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    v_cod_unidades_base                     bigint[] := (select array_agg(u.codigo)
                                                         from unidade u
                                                         where u.cod_empresa = f_cod_empresa_base);
    v_cod_unidade_base                      bigint;
    v_cod_unidades_usuario                  bigint[] := (select array_agg(u.codigo)
                                                         from unidade u
                                                         where u.cod_empresa = f_cod_empresa_usuario);
    v_cod_unidade_usuario_nova              bigint;
    v_cod_colaboradores_usuario             bigint[] := (select array_agg(cd.codigo)
                                                         from colaborador_data cd
                                                         where cd.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_afericoes                         bigint[] := (select array_agg(ad.codigo)
                                                         from afericao_data ad
                                                         where ad.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_checklists                        bigint[] := (select array_agg(cd.codigo)
                                                         from checklist_data cd
                                                         where cd.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_checklists_modelo                 bigint[] := (select distinct array_agg(cmd.codigo)
                                                         from checklist_modelo_data cmd
                                                         where cmd.cod_unidade = any (v_cod_unidades_usuario));
    v_tokens_checklists_off                 text     := (select array_agg(codu.token_sincronizacao_checklist)
                                                         from checklist_offline_dados_unidade codu
                                                         where codu.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_movimentacoes                     bigint[] := (select array_agg(mo.codigo)
                                                         from movimentacao mo
                                                         where mo.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_socorros                          bigint[] := (select array_agg(sr.codigo)
                                                         from socorro_rota sr
                                                         where sr.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_veiculos_transferencias_processos bigint[] := (select array_agg(vtp.codigo)
                                                         from veiculo_transferencia_processo vtp
                                                         where (vtp.cod_unidade_destino = any (v_cod_unidades_usuario))
                                                            or (vtp.cod_unidade_origem = any (v_cod_unidades_usuario)));
    v_cod_pneu_transferencias_processos     bigint[] := (select array_agg(ptp.codigo)
                                                         from pneu_transferencia_processo ptp
                                                         where (ptp.cod_unidade_origem = any (v_cod_unidades_usuario))
                                                            or (ptp.cod_unidade_destino = any (v_cod_unidades_usuario)));
    v_cod_colaboradores_nps                 bigint[] := (select array_agg(colaboradores.cod_colaborador_nps)
                                                         from (select nbpc.cod_colaborador_bloqueio as cod_colaborador_nps
                                                               from cs.nps_bloqueio_pesquisa_colaborador nbpc
                                                               where nbpc.cod_colaborador_bloqueio = any (v_cod_colaboradores_usuario)
                                                               union
                                                               select nr.cod_colaborador_respostas as cod_colaborador_nps
                                                               from cs.nps_respostas nr
                                                               where nr.cod_colaborador_respostas = any (v_cod_colaboradores_usuario)) colaboradores);
    v_cod_treinamentos                      bigint[] := (select array_agg(t.codigo)
                                                         from treinamento t
                                                         where t.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_servicos_realizados               bigint[] := (select array_agg(psr.codigo)
                                                         from pneu_servico_realizado_data psr
                                                         where psr.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_intervalo                         bigint[] := (select array_agg(iu.cod_unidade)
                                                         from intervalo_unidade iu
                                                         where iu.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_marcacoes                         bigint[] := (select array_agg(i.codigo)
                                                         from intervalo i
                                                         where i.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_relatos                           bigint[] := (select array_agg(r.codigo)
                                                         from relato r
                                                         where r.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_quiz                              bigint[] := (select array_agg(q.codigo)
                                                         from quiz q
                                                         where q.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_fale_conosco                      bigint[] := (select array_agg(fc.codigo)
                                                         from fale_conosco fc
                                                         where fc.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_testes_aferidor                   bigint[] := (select array_agg(pt.codigo)
                                                         from aferidor.procedimento_teste pt
                                                         where pt.cod_colaborador_execucao = any (v_cod_colaboradores_usuario));
    v_colaboradores_cadastrados             text[] ;
begin
    -- verifica se empresas existem.
    perform func_garante_empresa_existe(f_cod_empresa_base);
    perform func_garante_empresa_existe(f_cod_empresa_usuario);

    -- busca e deleta vínculos que possam existir de colaborador, veículos e pneus.
    --- aferiçao.
    if (v_cod_afericoes is not null)
    then
        perform interno.func_deleta_afericoes_dependencias(v_cod_unidades_usuario, v_cod_afericoes);
    end if;

    --- checklist.
    if ((v_cod_checklists is not null) or (v_cod_checklists_modelo is not null))
    then
        perform interno.func_deleta_checklists_dependencias(v_cod_unidades_usuario,
                                                            v_cod_checklists,
                                                            v_cod_checklists_modelo);
    end if;

    --- deleta token ckecklist offline
    -- (mesmo sem ter checklist - pode haver o token - pois ele é criado assim que uma unidade é cadastrada)
    if (v_tokens_checklists_off is not null)
    then
        perform interno.func_deleta_tokens_checklists_offlines(v_cod_unidades_usuario);
    end if;

    -- movimentação.
    if (v_cod_movimentacoes is not null)
    then
        perform interno.func_deleta_movimentacoes_dependencias(v_cod_unidades_usuario, v_cod_movimentacoes);
    end if;

    --- socorro em rota.
    if (v_cod_socorros is not null)
    then
        perform interno.func_deleta_socorros_dependencias(f_cod_empresa_usuario, v_cod_socorros);
    end if;

    --- transferencia de veículos.
    if (v_cod_veiculos_transferencias_processos is not null)
    then
        perform interno.func_deleta_transferencias_veiculos_dependencias(v_cod_veiculos_transferencias_processos);
    end if;

    -- transferencia de pneu
    if (v_cod_pneu_transferencias_processos is not null)
    then
        perform interno.func_deleta_transferencias_pneus_dependencias(v_cod_pneu_transferencias_processos,
                                                                      v_cod_unidades_usuario);
    end if;

    -- intervalo
    if (v_cod_intervalo is not null)
    then
        perform interno.func_deleta_intervalo_dependencias(v_cod_unidades_usuario, v_cod_marcacoes);
    end if;

    -- nps
    if (v_cod_colaboradores_nps is not null)
    then
        perform interno.func_deleta_nps(v_cod_colaboradores_nps);
    end if;

    -- produtividade
    if exists(select ap.cod_unidade from acessos_produtividade ap where ap.cod_unidade = any (v_cod_unidades_usuario))
    then
        perform interno.func_deleta_produtividades_dependencias(v_cod_unidades_usuario);
    end if;

    -- relato
    if (v_cod_relatos is not null)
    then
        perform interno.func_deleta_relatos_dependencias(v_cod_unidades_usuario);
    end if;

    -- quiz
    if (v_cod_quiz is not null)
    then
        perform interno.func_deleta_quiz_dependencias(v_cod_unidades_usuario, v_cod_quiz);
    end if;

    -- treinamento
    if (v_cod_treinamentos is not null)
    then
        perform interno.func_deleta_treinamentos_dependencias(v_cod_treinamentos);
    end if;

    -- servico pneu
    if (v_cod_servicos_realizados is not null)
    then
        perform interno.func_deleta_servicos_pneu_dependencias(f_cod_empresa_usuario, v_cod_servicos_realizados);
    end if;

    -- fale conosco
    if (v_cod_fale_conosco is not null)
    then
        perform interno.func_deleta_fale_conosco(v_cod_fale_conosco);
    end if;

    -- testes aferidor
    if (v_cod_testes_aferidor is not null)
    then
        perform interno.func_deleta_testes_aferidor(v_cod_testes_aferidor);
    end if;

    -- deleta veículos
    perform interno.func_deleta_veiculos(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta pneus
    perform interno.func_deleta_pneus(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta colaboradores
    perform interno.func_deleta_colaboradores(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta unidades
    perform interno.func_deleta_unidades(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- clonagens
    --- clona unidades
    perform interno.func_clona_unidades(f_cod_empresa_base, f_cod_empresa_usuario);

    --- clona nomenclaturas
    perform interno.func_clona_nomenclaturas(f_cod_empresa_base, f_cod_empresa_usuario);

    foreach v_cod_unidade_base in array v_cod_unidades_base
        loop
            v_cod_unidade_usuario_nova := (select unova.codigo
                                           from unidade ubase
                                                    join unidade unova on ubase.nome = unova.nome
                                           where ubase.codigo = v_cod_unidade_base
                                             and unova.cod_empresa = f_cod_empresa_usuario);

            --- clona veículos
            if exists(select vd.codigo from veiculo_data vd where vd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_veiculos(f_cod_empresa_base, v_cod_unidade_base, f_cod_empresa_usuario,
                                                    v_cod_unidade_usuario_nova);

            end if;

            --- clona pneus
            if exists(select pd.codigo from pneu_data pd where pd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_pneus(f_cod_empresa_base, v_cod_unidade_base, f_cod_empresa_usuario,
                                                 v_cod_unidade_usuario_nova);
            end if;

            --- clona vinculos
            if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_vinculo_veiculos_pneus(v_cod_unidade_base, v_cod_unidade_usuario_nova);
            end if;

            --- clona colaboradores
            if exists(select cd.codigo from colaborador_data cd where cd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_colaboradores(f_cod_empresa_base, v_cod_unidade_base,
                                                         f_cod_empresa_usuario,
                                                         v_cod_unidade_usuario_nova);
            end if;
        end loop;

    v_colaboradores_cadastrados = (select array_agg(concat('CPF: ', c.cpf,
                                                           ' | DATA NASCIMENTO: ', c.data_nascimento,
                                                           ' | NÍVEL DE PERMISSAO: ', c.cod_permissao,
                                                           ' | CARGO: ', f.nome))
                                   from colaborador c
                                            join funcao f on f.cod_empresa = c.cod_empresa and f.codigo = c.cod_funcao
                                   where c.cod_empresa = f_cod_empresa_usuario);

    select 'A EMPRESA FOI RESETADA E OS DADOS FORAM CLONADOS COM SUCESSO. OS COLABORADORES CADASTRADOS SÃO: ' ||
           concat(v_colaboradores_cadastrados)
    into mensagem_sucesso;
end ;
$$;

create or replace function
    interno.func_clona_vinculo_veiculos_pneus(f_cod_unidade_base bigint, f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_veiculos_com_vinculo text := (select array_agg(vp.cod_veiculo)
                                        from veiculo_pneu vp
                                        where vp.cod_unidade = f_cod_unidade_base);
begin
    -- COPIA VÍNCULOS, CASO EXISTAM.
    if (v_cod_veiculos_com_vinculo is not null)
    then
        with veiculos_base as (
            select row_number() over () as codigo_comparacao,
                   v.codigo             as cod_veiculo,
                   v.placa,
                   vdpp.posicao_prolog
            from veiculo_data v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                     join veiculo_diagrama_posicao_prolog vdpp
                          on vt.cod_diagrama = vdpp.cod_diagrama
            where v.cod_unidade = f_cod_unidade_base
        ),
             veiculos_novos as (
                 select row_number() over () as codigo_comparacao,
                        v.placa,
                        v.cod_diagrama,
                        v.codigo,
                        vdpp.posicao_prolog
                 from veiculo_data v
                          join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                          join veiculo_diagrama_posicao_prolog vdpp on vt.cod_diagrama = vdpp.cod_diagrama
                 where v.cod_unidade = f_cod_unidade_usuario),
             dados_de_para as (
                 select vn.codigo         as cod_veiculo_novo,
                        vn.placa          as placa_nova,
                        vn.posicao_prolog as posicao_prolog_novo,
                        vn.cod_diagrama   as cod_diagrama_novo,
                        pdn.codigo        as cod_pneu_novo
                 from veiculos_base vb
                          join veiculos_novos vn
                               on vb.codigo_comparacao = vn.codigo_comparacao and vb.posicao_prolog = vn.posicao_prolog
                          join veiculo_pneu vp on vb.cod_veiculo = vp.cod_veiculo and vb.posicao_prolog = vp.posicao
                          join pneu_data pdb
                               on vp.status_pneu = pdb.status and vp.cod_unidade = pdb.cod_unidade and
                                  vp.cod_pneu = pdb.codigo
                          join pneu_data pdn
                               on pdb.codigo_cliente = pdn.codigo_cliente and
                                  pdn.cod_unidade = f_cod_unidade_usuario and
                                  pdn.status = 'EM_USO')
        insert
        into veiculo_pneu (cod_pneu, cod_unidade, posicao, cod_diagrama, cod_veiculo)
        select ddp.cod_pneu_novo,
               f_cod_unidade_usuario,
               ddp.posicao_prolog_novo,
               ddp.cod_diagrama_novo,
               ddp.cod_veiculo_novo
        from dados_de_para ddp;
    end if;
end;
$$;

create or replace function implantacao.tg_func_vinculo_veiculo_pneu_confere_planilha_vinculo()
    returns trigger
    language plpgsql
    security definer
as
$$
declare
    v_qtd_erros            smallint := 0;
    v_msgs_erros           text;
    v_quebra_linha         text     := chr(10);
    v_cod_pneu             bigint;
    v_status_pneu          varchar(255);
    v_cod_unidade_pneu     bigint;
    v_cod_veiculo          bigint;
    v_placa                varchar(7);
    v_cod_tipo_veiculo     bigint;
    v_cod_diagrama_veiculo bigint;
    v_cod_unidade_placa    bigint;
    v_cod_empresa_placa    bigint;
    v_posicao_prolog       integer;
begin
    if (tg_op = 'UPDATE' and old.status_vinculo_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.usuario_update := session_user;
        new.placa_formatada_vinculo := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.numero_fogo_pneu_formatado_vinculo := remove_all_spaces(new.numero_fogo_pneu_editavel);
        new.nomenclatura_posicao_formatada_vinculo := remove_all_spaces(new.nomenclatura_posicao_editavel);

        -- Verifica se empresa existe.
        if not exists(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade existe.
        if not exists(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade pertence a empresa.
        if not exists(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- Verificações placas.
        -- Placa nula: Erro.
        -- Placa cadastrada em outra empresa: Erro.
        -- Placa cadastrada em outra unidade da mesma empresa: Erro.
        -- Posicao já ocupada por outro pneu: Erro.
        if ((new.placa_formatada_vinculo is not null) and
            (length(new.placa_formatada_vinculo) <> 0))
        then
            select v.codigo,
                   v.placa,
                   v.cod_tipo,
                   v.cod_diagrama,
                   v.cod_unidade,
                   v.cod_empresa
            into v_cod_veiculo,
                v_placa,
                v_cod_tipo_veiculo,
                v_cod_diagrama_veiculo,
                v_cod_unidade_placa,
                v_cod_empresa_placa
            from veiculo v
            where remove_all_spaces(v.placa) ilike
                  new.placa_formatada_vinculo
              and v.cod_empresa = new.cod_empresa;
            if (v_placa is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- A PLACA NÃO FOI ENCONTRADA',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_empresa_placa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA EMPRESA',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_cod_unidade_placa != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA
                                              UNIDADE',
                                              v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    else
                        -- Verificar se a posição existe nesse veículo e se está disponível.
                        if ((new.nomenclatura_posicao_formatada_vinculo is not null) and
                            (length(new.nomenclatura_posicao_formatada_vinculo) <> 0))
                        then
                            select ppne.posicao_prolog
                            into v_posicao_prolog
                            from pneu_posicao_nomenclatura_empresa ppne
                            where ppne.cod_diagrama = v_cod_diagrama_veiculo
                              and remove_all_spaces(ppne.nomenclatura)
                                ilike new.nomenclatura_posicao_formatada_vinculo
                              and ppne.cod_empresa = new.cod_empresa;
                            if (v_posicao_prolog is not null)
                            then
                                if exists(select vp.cod_veiculo
                                          from veiculo_pneu vp
                                          where vp.cod_veiculo = v_cod_veiculo
                                            and vp.posicao = v_posicao_prolog
                                            and vp.cod_unidade = new.cod_unidade)
                                then
                                    v_qtd_erros = v_qtd_erros + 1;
                                    v_msgs_erros =
                                            concat(v_msgs_erros, v_qtd_erros,
                                                   '- JÁ EXISTE PNEU VINCULADO À POSIÇÃO (NOMENCLATURA) INFORMADA',
                                                   v_quebra_linha);
                                end if;
                            else
                                v_qtd_erros = v_qtd_erros + 1;
                                v_msgs_erros =
                                        concat(v_msgs_erros, v_qtd_erros,
                                               '- NOMENCLATURA NÃO ENCONTRADA',
                                               v_quebra_linha);
                                new.status_vinculo_realizado = false;
                            end if;
                        else
                            v_qtd_erros = v_qtd_erros + 1;
                            v_msgs_erros =
                                    concat(v_msgs_erros, v_qtd_erros,
                                           '- NOMENCLATURA NÃO PODE SER NULA',
                                           v_quebra_linha);
                        end if;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A PLACA DE FOGO NÃO PODE SER NULA',
                           v_quebra_linha);
        end if;


        -- Verificações número de fogo.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Código do pneu não encontrado: Erro.
        -- Status do pneu diferente de 'ESTOQUE': Erro.
        if ((new.numero_fogo_pneu_formatado_vinculo is not null) and
            (length(new.numero_fogo_pneu_formatado_vinculo) <> 0))
        then
            select p.codigo,
                   p.status,
                   p.cod_unidade
            into v_cod_pneu, v_status_pneu, v_cod_unidade_pneu
            from pneu p
            where remove_all_spaces(p.codigo_cliente) ilike
                  new.numero_fogo_pneu_formatado_vinculo
              and p.cod_empresa = new.cod_empresa;
            if (v_cod_pneu is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- O PNEU NÃO FOI ENCONTRADO',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_unidade_pneu != new.cod_unidade)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS O PNEU PERTENCE A OUTRA UNIDADE',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_status_pneu != 'ESTOQUE')
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros =
                                concat(v_msgs_erros, v_qtd_erros,
                                       '- PARA REALIZAR O VÍNCULO O PNEU DEVE ESTAR EM ESTOQUE, O STATUS ATUAL DO PNEU
                                       É: ',
                                       v_status_pneu, v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- O NÚMERO DE FOGO NÃO PODE SER NULO',
                           v_quebra_linha);
        end if;

        if (v_qtd_erros > 0)
        then
            new.erros_encontrados = v_msgs_erros;
        else
            update pneu_data set status = 'EM_USO' where codigo = v_cod_pneu;
            insert into veiculo_pneu (cod_pneu,
                                      cod_unidade,
                                      posicao,
                                      cod_diagrama,
                                      cod_veiculo)
            values (v_cod_pneu,
                    new.cod_unidade,
                    v_posicao_prolog,
                    v_cod_diagrama_veiculo,
                    v_cod_veiculo);

            new.status_vinculo_realizado = true;
            new.erros_encontrados = '-';
        end if;
    end if;
    return new;
end;
$$;



-- PL-3624
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
                                                                                    F_COD_PNEU BIGINT,
                                                                                    F_POSICAO INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_VEICULO  BIGINT;
    V_COD_UNIDADE  BIGINT;
    V_COD_DIAGRAMA BIGINT := (SELECT COD_DIAGRAMA
                              FROM VEICULO_TIPO
                              WHERE CODIGO = (SELECT COD_TIPO FROM VEICULO_DATA WHERE PLACA = F_PLACA));
BEGIN
    SELECT V.CODIGO, V.COD_UNIDADE
    FROM VEICULO_DATA V
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_UNIDADE;

    -- Valida se posição existe no diagrama.
    IF NOT EXISTS(SELECT VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.COD_DIAGRAMA = (SELECT V.COD_DIAGRAMA FROM VEICULO_DATA V WHERE V.PLACA = F_PLACA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A posição %s não existe no diagrama do veículo de placa %s',
                                                  F_POSICAO,
                                                  F_PLACA));
    END IF;

    -- Verifica se tem pneu aplicado nessa posição, caso tenha é prq não passou pelo método
    -- do Java de removePneusAplicados;
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.POSICAO = F_POSICAO AND VP.COD_VEICULO = V_COD_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Erro! O veículo %s já possui pneu aplicado na posição %s',
                                                  F_PLACA,
                                                  F_POSICAO));
    END IF;

    -- Deleta a posição.
    DELETE FROM VEICULO_PNEU WHERE POSICAO = F_POSICAO AND COD_VEICULO = V_COD_VEICULO;

    -- Não tem pneu aplicado a posição, então eu adiciono.
    INSERT INTO VEICULO_PNEU(COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA, COD_VEICULO)
    VALUES (F_COD_PNEU, V_COD_UNIDADE, F_POSICAO, V_COD_DIAGRAMA, V_COD_VEICULO);
END;
$$;

create or replace function integracao.func_pneu_vincula_pneu_posicao_placa(f_cod_veiculo_prolog bigint,
                                                                           f_placa_veiculo_pneu_aplicado text,
                                                                           f_cod_pneu_prolog bigint,
                                                                           f_codigo_pneu_cliente text,
                                                                           f_cod_unidade_pneu bigint,
                                                                           f_posicao_veiculo_pneu_aplicado integer,
                                                                           f_is_posicao_estepe boolean)
    returns boolean
    language plpgsql
as
$$
declare
    f_qtd_rows_alteradas bigint;
begin
    -- Validamos se a placa existe no ProLog.
    if (f_cod_veiculo_prolog is null or f_cod_veiculo_prolog <= 0)
    then
        perform public.throw_generic_error(format('A placa informada %s não está presente no Sistema ProLog',
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    if ((select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog) <> f_cod_unidade_pneu)
    then
        perform public.throw_generic_error(
                format('A placa informada %s está em uma Unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                       f_placa_veiculo_pneu_aplicado,
                       f_codigo_pneu_cliente,
                       (select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog),
                       f_cod_unidade_pneu));
    end if;

    -- Validamos se a posição repassada é uma posição válida no ProLog.
    if (not is_placa_posicao_pneu_valida(f_cod_veiculo_prolog, f_posicao_veiculo_pneu_aplicado, f_is_posicao_estepe))
    then
        perform public.throw_generic_error(
                format('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
                       f_posicao_veiculo_pneu_aplicado,
                       f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se a placa possui algum outro pneu aplicado na posição.
    if (select exists(select *
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_unidade = f_cod_unidade_pneu
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado))
    then
        perform public.throw_generic_error(format('Já existe um pneu na placa %s, posição %s',
                                                  f_placa_veiculo_pneu_aplicado,
                                                  f_posicao_veiculo_pneu_aplicado));
    end if;

    -- Vincula pneu a placa.
    insert into public.veiculo_pneu(cod_pneu,
                                    cod_unidade,
                                    posicao,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_pneu_prolog,
            f_cod_unidade_pneu,
            f_posicao_veiculo_pneu_aplicado,
            (select vt.cod_diagrama
             from veiculo_tipo vt
             where vt.codigo = (select v.cod_tipo from veiculo v where v.codigo = f_cod_veiculo_prolog)),
            f_cod_veiculo_prolog);

    get diagnostics f_qtd_rows_alteradas = row_count;

    -- Verificamos se o update ocorreu como deveria
    if (f_qtd_rows_alteradas <= 0)
    then
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Retornamos sucesso se o pneu estiver aplicado na placa e posição que deveria estar.
    if (select exists(select vp.posicao
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_pneu = f_cod_pneu_prolog
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado
                        and vp.cod_unidade = f_cod_unidade_pneu))
    then
        return true;
    else
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;
end ;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_TRANSFERE_VEICULO(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                     F_COD_UNIDADE_DESTINO BIGINT,
                                                                     F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                                     F_PLACA TEXT,
                                                                     F_OBSERVACAO TEXT,
                                                                     F_TOKEN_INTEGRACAO TEXT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                        BIGINT := (SELECT U.COD_EMPRESA
                                                    FROM PUBLIC.UNIDADE U
                                                    WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
    V_OBSERVACAO_TRANSFERENCIA_PNEU      TEXT   := 'Transferência de pneus aplicados';
    V_COD_VEICULO                        BIGINT;
    V_COD_DIAGRAMA_VEICULO               BIGINT;
    V_COD_TIPO_VEICULO                   BIGINT;
    V_KM_VEICULO                         BIGINT;
    V_COD_COLABORADOR                    BIGINT;
    V_COD_UNIDADE_COLABORADOR            BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT;
    V_COD_INFORMACOES_TRANSFERENCIA      BIGINT;
    V_COD_PNEUS_TRANSFERIR               TEXT[];
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            V_COD_EMPRESA,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                    verificar vínculos', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_ORIGEM));

    -- Verificamos se a empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado não está autorizado: %s',
                                               F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verificamos se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_ORIGEM,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_DESTINO,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT V.CODIGO,
           VT.COD_DIAGRAMA,
           VT.CODIGO,
           V.KM
    FROM VEICULO_DATA V
             JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
        -- Fazemos esse Join para remover a placa do retorno caso ela não estiver mapeada na tabela de integração
             JOIN INTEGRACAO.VEICULO_CADASTRADO VC ON V.CODIGO = VC.COD_VEICULO_CADASTRO_PROLOG
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_DIAGRAMA_VEICULO, V_COD_TIPO_VEICULO, V_KM_VEICULO;

    -- Verificamos se o veículo pertence a unidade origem.
    IF (V_COD_VEICULO IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('A placa (%s) não está cadastrada no Sistema Prolog', F_PLACA));
    END IF;

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT C.CODIGO,
           C.COD_UNIDADE
    FROM COLABORADOR_DATA C
    WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA
    INTO V_COD_COLABORADOR, V_COD_UNIDADE_COLABORADOR;

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF (V_COD_COLABORADOR IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s não está cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Precisamos desativar as constraints, para verificar apenas no commit da function, assim poderemos transferir o
    -- veículo e seus pneus com segurança.
    SET CONSTRAINTS ALL DEFERRED;

    -- Cria processo de transferência para veículo.
    INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                               COD_UNIDADE_DESTINO,
                                               COD_UNIDADE_COLABORADOR,
                                               COD_COLABORADOR_REALIZACAO,
                                               DATA_HORA_TRANSFERENCIA_PROCESSO,
                                               OBSERVACAO)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA_VEICULO;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA_VEICULO IS NULL OR V_COD_PROCESSO_TRANSFERENCIA_VEICULO <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transferência';
    END IF;

    -- Insere valores da transferência do veículo
    INSERT INTO VEICULO_TRANSFERENCIA_INFORMACOES(COD_PROCESSO_TRANSFERENCIA,
                                                  COD_VEICULO,
                                                  COD_DIAGRAMA_VEICULO,
                                                  COD_TIPO_VEICULO,
                                                  KM_VEICULO_MOMENTO_TRANSFERENCIA)
    VALUES (V_COD_PROCESSO_TRANSFERENCIA_VEICULO,
            V_COD_VEICULO,
            V_COD_DIAGRAMA_VEICULO,
            V_COD_TIPO_VEICULO,
            V_KM_VEICULO)
    RETURNING CODIGO INTO V_COD_INFORMACOES_TRANSFERENCIA;

    -- Verifica se os valores da transferência foram adicionados com sucesso.
    IF (V_COD_INFORMACOES_TRANSFERENCIA IS NULL OR V_COD_INFORMACOES_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao adicionar valores da transferência';
    END IF;

    -- TODO - verificar através da func, se devemos fechar ou não Itens de O.S (FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO)
    -- Deleta O.S. do veículo transferido.
    PERFORM FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(V_COD_VEICULO,
                                                               V_COD_INFORMACOES_TRANSFERENCIA,
                                                               F_DATA_HORA);

    -- Transfere veículo.
    UPDATE VEICULO
    SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
    WHERE CODIGO = V_COD_VEICULO;

    -- Transfere veículo na integração.
    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO
    WHERE COD_VEICULO_CADASTRO_PROLOG = V_COD_VEICULO;

    -- Verifica se placa possui pneus aplicados, caso tenha, transferimos esses pneus para a unidade origem.
    IF (EXISTS(SELECT COD_PNEU FROM VEICULO_PNEU WHERE COD_VEICULO = V_COD_VEICULO))
    THEN
        -- Criamos array com os cod_pneu.
        SELECT ARRAY_AGG(P.CODIGO_CLIENTE)
        FROM PNEU_DATA P
        WHERE P.CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.COD_VEICULO = V_COD_VEICULO)
        INTO V_COD_PNEUS_TRANSFERIR;

        V_COD_PROCESSO_TRANSFERENCIA_PNEU =
                (INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM,
                                                                    F_COD_UNIDADE_DESTINO,
                                                                    F_CPF_COLABORADOR_TRANSFERENCIA,
                                                                    V_COD_PNEUS_TRANSFERIR,
                                                                    V_OBSERVACAO_TRANSFERENCIA_PNEU,
                                                                    F_TOKEN_INTEGRACAO,
                                                                    F_DATA_HORA,
                                                                    TRUE));
        -- Verifica se a transferência deu certo.
        IF (V_COD_PROCESSO_TRANSFERENCIA_PNEU <= 0)
        THEN
            RAISE EXCEPTION
                'Erro ao transferir os pneus (%s) aplicados ao veículo', ARRAY_TO_STRING(V_COD_PNEUS_TRANSFERIR, ', ');
        END IF;

        --  Modifica unidade dos vínculos.
        UPDATE VEICULO_PNEU
        SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
        WHERE COD_VEICULO = V_COD_VEICULO;

        -- Adiciona VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU.
        INSERT INTO VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(COD_VEICULO_TRANSFERENCIA_INFORMACOES,
                                                                COD_PROCESSO_TRANSFERENCIA_PNEU)
        VALUES (V_COD_INFORMACOES_TRANSFERENCIA,
                V_COD_PROCESSO_TRANSFERENCIA_PNEU);
    END IF;
END;
$$;


-- PL-3625
alter table socorro_rota_abertura
    drop constraint if exists fk_socorro_rota_abertura_veiculo_codigo;

alter table veiculo_transferencia_informacoes
    drop constraint if exists fk_veiculo_transferencia_informacoes_veiculo;

alter table integracao.veiculo_cadastrado
    drop constraint if exists fk_veiculo_cadastro_veiculo;

alter table veiculo_edicao_historico
    drop constraint if exists fk_veiculo;

alter table veiculo_acoplamento_historico
    drop constraint if exists fk_veiculo;

alter table veiculo_processo_km_historico
    drop constraint if exists fk_veiculo;

alter table movimentacao_origem
    drop constraint if exists fk_movimentacao_origem_veiculo;

alter table movimentacao_destino
    drop constraint if exists fk_movimentacao_origem_veiculo;

alter table afericao_data
    drop constraint if exists fk_afericao_cod_veiculo;

alter table checklist_data
    drop constraint if exists fk_checklist_cod_veiculo;

alter table veiculo_data
    drop constraint if exists veiculo_codigo_key;

alter table veiculo_data
    drop constraint if exists pk_placa;

alter table veiculo_data
    add constraint pk_cod_veiculo primary key (codigo);

alter table veiculo_data
    add constraint unique_cod_empresa_placa unique (cod_empresa, placa);

alter table socorro_rota_abertura
    add constraint fk_socorro_rota_abertura_veiculo_codigo foreign key (cod_veiculo_problema) references veiculo_data;

alter table veiculo_transferencia_informacoes
    add constraint fk_veiculo_transferencia_informacoes_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table integracao.veiculo_cadastrado
    add constraint fk_veiculo_cadastro_veiculo foreign key (cod_veiculo_cadastro_prolog) references veiculo_data;

alter table veiculo_edicao_historico
    add constraint fk_veiculo foreign key (cod_veiculo_edicao) references veiculo_data;

alter table veiculo_acoplamento_historico
    add constraint fk_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table veiculo_processo_km_historico
    add constraint fk_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table movimentacao_origem
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table movimentacao_destino
    add constraint fk_movimentacao_origem_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table afericao_data
    add constraint fk_afericao_cod_veiculo foreign key (cod_veiculo) references veiculo_data;

alter table checklist_data
    add constraint fk_checklist_cod_veiculo foreign key (cod_veiculo) references veiculo_data;