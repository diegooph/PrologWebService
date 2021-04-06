create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.codigo = f_cod_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (v_cod_afericao,
            f_data_hora,
            f_cpf_aferidor,
            v_km_final,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
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
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
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