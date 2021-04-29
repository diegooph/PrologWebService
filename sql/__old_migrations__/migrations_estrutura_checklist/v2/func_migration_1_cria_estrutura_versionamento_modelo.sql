create or replace function func_migration_1_cria_estrutura_versionamento_modelo()
    returns void
    language plpgsql
as
$$
begin
    --######################################################################################################################
    --######################################################################################################################
    --######################################### Migra estrutura da tabela de respostas #####################################
    --######################################################################################################################
    --######################################################################################################################
    -- PL-2184
    -- Dropa constraints e views necessárias.
    drop view estratificacao_os;
    alter table checklist_modelo_data
        drop constraint unico_modelo_por_unidade;
    alter table checklist_alternativa_pergunta_data
        drop constraint fk_checklist_alternativa_pergunta_pergunta;
    alter table checklist_perguntas_data
        drop constraint unica_pergunta_por_modelo;
    alter table checklist_alternativa_pergunta_data
        drop constraint unica_alternativa_por_pergunta;

    -- Recria constraint.
    alter table checklist_alternativa_pergunta_data
        add constraint fk_checklist_alternativa_pergunta_pergunta
            foreign key (cod_pergunta) references checklist_perguntas_data (codigo);

    -- Cria tabela para salvar as versões dos modelos de checklist.
    create table if not exists checklist_modelo_versao
    (
        cod_versao_checklist_modelo    bigserial not null,
        cod_versao_user_friendly       bigint    not null,
        cod_checklist_modelo           bigint    not null,
        data_hora_criacao_versao       timestamp with time zone not null,
        cod_colaborador_criacao_versao bigint,
        constraint pk_checklist_modelo_versao
            primary key (cod_versao_checklist_modelo),
        constraint fk_checklist_modelo_versao_checklist_modelo
            foreign key (cod_checklist_modelo) references checklist_modelo_data (codigo) DEFERRABLE INITIALLY IMMEDIATE,
        constraint fk_checklist_modelo_versao_colaborador
            foreign key (cod_colaborador_criacao_versao) references colaborador_data (codigo),
        constraint unique_versao_user_friendly_modelo_checklist unique (cod_checklist_modelo, cod_versao_user_friendly),
        constraint unique_versao_modelo_checklist unique (cod_checklist_modelo, cod_versao_checklist_modelo)
    );

    comment on table checklist_modelo_versao is 'Salva as versões de um modelo de checklist. data_hora_criacao_versao e cod_colaborador_criacao_versao
    podem ser nulos pois na primeira versão não tínhamos quando foi criada e nem quem criou. Porém, existem checks que impedem que essas colunas
    sejam nulas em novas versões.';

    --######################################################################################################################
    -- CHECKLIST_MODELO_DATA
    -- Cria coluna de versão atual na tabela de modelo.
    alter table checklist_modelo_data
        add column cod_versao_atual bigint;
    alter table checklist_modelo_data
        add constraint fk_checklist_modelo_checklist_modelo_versao
            foreign key (cod_versao_atual)
                references checklist_modelo_versao (cod_versao_checklist_modelo) DEFERRABLE INITIALLY IMMEDIATE;

    drop view checklist_modelo;
    create or replace view checklist_modelo as
    select cm.cod_unidade,
           cm.codigo,
           cm.cod_versao_atual,
           cm.nome,
           cm.status_ativo
    from checklist_modelo_data cm
    where cm.deletado = false;
    --######################################################################################################################

    --######################################################################################################################
    -- CHECKLIST_DATA
    -- Cria versão na tabela CHECKLIST_DATA.
    alter table checklist_data
        add column cod_versao_checklist_modelo bigint;

    -- Dropa a view checklist
    drop view checklist;

    -- Recria a view checklist com a coluna cod_versao_checklist_modelo
    create view checklist(cod_unidade, cod_checklist_modelo, codigo, data_hora, data_hora_importado_prolog,
                          cpf_colaborador,
                          placa_veiculo, tipo, tempo_realizacao, km_veiculo, data_hora_sincronizacao,
                          fonte_data_hora_realizacao, versao_app_momento_realizacao, versao_app_momento_sincronizacao,
                          device_id, device_imei, device_uptime_realizacao_millis, device_uptime_sincronizacao_millis,
                          foi_offline, total_perguntas_ok, total_perguntas_nok, total_alternativas_ok,
                          total_alternativas_nok, cod_versao_checklist_modelo) as
    select c.cod_unidade,
           c.cod_checklist_modelo,
           c.codigo,
           c.data_hora,
           c.data_hora_importado_prolog,
           c.cpf_colaborador,
           c.placa_veiculo,
           c.tipo,
           c.tempo_realizacao,
           c.km_veiculo,
           c.data_hora_sincronizacao,
           c.fonte_data_hora_realizacao,
           c.versao_app_momento_realizacao,
           c.versao_app_momento_sincronizacao,
           c.device_id,
           c.device_imei,
           c.device_uptime_realizacao_millis,
           c.device_uptime_sincronizacao_millis,
           c.foi_offline,
           c.total_perguntas_ok,
           c.total_perguntas_nok,
           c.total_alternativas_ok,
           c.total_alternativas_nok,
           c.cod_versao_checklist_modelo
    from checklist_data c
    where (c.deletado = false);

    -- Essa constraint não precisa mais pois a fk com a versão já garante a existência do modelo.
    alter table checklist_data
        drop constraint fk_checklist_checklist_modelo;
    alter table checklist_data
        add constraint fk_checklist_data_checklist_modelo_versao
            foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);
    --######################################################################################################################

    --######################################################################################################################
    -- CHECKLIST_PERGUNTAS_DATA
    -- Cria versão na tabela CHECKLIST_PERGUNTAS_DATA.
    alter table checklist_perguntas_data
        add column cod_versao_checklist_modelo bigint;
    -- Para perguntas antigas (inativas) o código gerado será diferente da pergunta ativa atual. Iremos ignorar esses casos.
    alter table checklist_perguntas_data
        add column codigo_fixo_pergunta bigserial not null;

    -- Constraint útil para servir como FK na COSI.
    alter table checklist_perguntas_data
        add constraint unica_pergunta_versao unique (codigo_fixo_pergunta, codigo);

    drop view checklist_perguntas;
    create or replace view checklist_perguntas as
    select cp.cod_checklist_modelo,
           cp.cod_versao_checklist_modelo,
           cp.cod_unidade,
           cp.ordem,
           cp.pergunta,
           cp.status_ativo,
           cp.single_choice,
           cp.cod_imagem,
           cp.codigo,
           cp.codigo_fixo_pergunta
    from checklist_perguntas_data cp
    where cp.deletado = false;
    --######################################################################################################################

    --######################################################################################################################
    -- CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    -- Cria versão na tabela CHECKLIST_ALTERNATIVA_PERGUNTA_DATA.
    alter table checklist_alternativa_pergunta_data
        add column cod_versao_checklist_modelo bigint;
    -- Para alternativas antigas (inativas) o código gerado será diferente da alternativas ativa atual.
    -- Iremos ignorar esses casos.
    alter table checklist_alternativa_pergunta_data
        add column codigo_fixo_alternativa bigserial not null;

    -- Constraint útil para servir como FK na COSI.
    alter table checklist_alternativa_pergunta_data
        add constraint unica_alternativa_versao unique (codigo_fixo_alternativa, codigo);

    drop view checklist_alternativa_pergunta;
    create or replace view checklist_alternativa_pergunta as
    select cap.cod_checklist_modelo,
           cap.cod_versao_checklist_modelo,
           cap.cod_unidade,
           cap.alternativa,
           cap.ordem,
           cap.status_ativo,
           cap.cod_pergunta,
           cap.codigo,
           cap.codigo_fixo_alternativa,
           cap.alternativa_tipo_outros,
           cap.prioridade,
           cap.deve_abrir_ordem_servico
    from checklist_alternativa_pergunta_data cap
    where cap.deletado = false;
    --######################################################################################################################

    -- Cria as tabelas e functions que o JAVA irá utilizar.
    create table check_perguntas_aux (
        cod_modelo bigint not null references checklist_modelo_data(codigo),
        cod_modelo_versao bigint not null references checklist_modelo_versao(cod_versao_checklist_modelo),
        cod_pergunta_antigo bigint not null references checklist_perguntas_data (codigo),
        cod_pergunta_novo bigint not null references checklist_perguntas_data (codigo)
    );

    create table check_alternativas_aux (
        cod_modelo bigint not null references checklist_modelo_data(codigo),
        cod_modelo_versao bigint not null references checklist_modelo_versao(cod_versao_checklist_modelo),
        cod_alternativa_antigo bigint not null references checklist_alternativa_pergunta_data (codigo),
        cod_alternativa_novo bigint not null references checklist_alternativa_pergunta_data (codigo)
    );

    create table check_vida_modelo_aux (
        cod_modelo bigint not null,
        cod_modelo_versao bigint not null,
        data_inicial date not null,
        data_final date not null,
        constraint fK_versao_modelo foreign key (cod_modelo, cod_modelo_versao)
            references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo)
    );

    alter table checklist_respostas add column cod_alternativa_novo bigint
        references checklist_alternativa_pergunta_data (codigo);
    alter table checklist_respostas add column cod_pergunta_novo bigint
        references checklist_perguntas_data (codigo);
    alter table checklist_respostas add column cod_versao_modelo bigint
        references checklist_modelo_versao (cod_versao_checklist_modelo);

    create or replace function func_checklist_cria_versao_modelo(f_cod_modelo bigint,
                                                                 f_cod_primeiro_check_versao_modelo bigint,
                                                                 f_data_hora_atual timestamp with time zone)
        returns bigint
    as
    $func$
    declare
        cod_pergunta_criado          bigint;
        cod_alternativa_criado       bigint;
        pergunta_modelo_checklist    checklist_perguntas_data%rowtype;
        alternativa_modelo_checklist checklist_alternativa_pergunta_data%rowtype;
        qtd_linhas_inseridas         bigint;
        qtd_linhas_atualizadas       bigint;
        novo_cod_versao_modelo       bigint := nextval(
                pg_get_serial_sequence('checklist_modelo_versao', 'cod_versao_checklist_modelo'));
    begin
        -- 1 -> Insere a versão.
        insert into checklist_modelo_versao(cod_versao_checklist_modelo,
                                            cod_versao_user_friendly,
                                            cod_checklist_modelo,
                                            data_hora_criacao_versao,
                                            cod_colaborador_criacao_versao)
        values (novo_cod_versao_modelo,
                (select coalesce(max(cmv.cod_versao_user_friendly) + 1, 1)
                 from checklist_modelo_versao cmv
                 where cmv.cod_checklist_modelo = f_cod_modelo),
                f_cod_modelo,
                f_data_hora_atual,
                null);

        get diagnostics qtd_linhas_inseridas = row_count;

        if qtd_linhas_inseridas is null or qtd_linhas_inseridas <> 1
        then
            raise exception 'Erro ao inserir versão do modelo';
        end if;
        --

        -- 2 -> Agora atualizamos o modelo de checklist.
        update checklist_modelo
        set cod_versao_atual = novo_cod_versao_modelo
        where codigo = f_cod_modelo;

        get diagnostics qtd_linhas_atualizadas = row_count;

        if qtd_linhas_atualizadas is null or qtd_linhas_atualizadas <> 1
        then
            raise exception 'Erro ao atualizar versão do modelo';
        end if;
        --

        -- 3 -> Criamos as perguntas e alternativas, mesmo que seja a versão 1.
        -- insere as perguntas e alternativas.
        for pergunta_modelo_checklist in
            select cp.*
            from checklist_respostas cr
                     join checklist_perguntas_data cp on cp.codigo = cr.cod_pergunta
            where cr.cod_checklist = f_cod_primeiro_check_versao_modelo
            group by cr.cod_pergunta,
                     cp.cod_checklist_modelo,
                     cp.cod_unidade,
                     cp.ordem,
                     cp.pergunta,
                     cp.status_ativo,
                     cp.single_choice,
                     cp.cod_imagem,
                     cp.codigo,
                     cp.deletado,
                     cp.data_hora_deletado,
                     cp.codigo_fixo_pergunta
            loop
                -- Pergunta.
                insert into checklist_perguntas_data (cod_checklist_modelo,
                                                      cod_unidade,
                                                      ordem,
                                                      pergunta,
                                                      status_ativo,
                                                      single_choice,
                                                      cod_imagem,
                                                      codigo_fixo_pergunta)
                values (pergunta_modelo_checklist.cod_checklist_modelo,
                        pergunta_modelo_checklist.cod_unidade,
                        pergunta_modelo_checklist.ordem,
                        pergunta_modelo_checklist.pergunta,
                        pergunta_modelo_checklist.status_ativo,
                        pergunta_modelo_checklist.single_choice,
                        pergunta_modelo_checklist.cod_imagem,
                        pergunta_modelo_checklist.codigo_fixo_pergunta) returning codigo
                           into cod_pergunta_criado;

--                 select array_append(perguntas, concat(pergunta_modelo_checklist.codigo, '_', cod_pergunta_criado));
                insert into check_perguntas_aux (cod_modelo, cod_modelo_versao, cod_pergunta_antigo, cod_pergunta_novo)
                values (f_cod_modelo, novo_cod_versao_modelo, pergunta_modelo_checklist.codigo, cod_pergunta_criado);

                get diagnostics qtd_linhas_inseridas = row_count;

                if qtd_linhas_inseridas is null or qtd_linhas_inseridas <> 1
                then
                    raise exception 'Erro ao inserir pergunta aux';
                end if;

                -- Alternativa.
                for alternativa_modelo_checklist in
                    select cap.*
                    from checklist_respostas cr
                             join checklist_alternativa_pergunta_data cap on cap.codigo = cr.cod_alternativa
                    where cr.cod_checklist = f_cod_primeiro_check_versao_modelo
                      and cr.cod_pergunta = cod_pergunta_criado
                    loop
                        insert into checklist_alternativa_pergunta_data (cod_checklist_modelo,
                                                                         cod_unidade,
                                                                         alternativa,
                                                                         ordem,
                                                                         status_ativo,
                                                                         cod_pergunta,
                                                                         alternativa_tipo_outros,
                                                                         prioridade,
                                                                         deve_abrir_ordem_servico,
                                                                         codigo_fixo_alternativa)
                        values (alternativa_modelo_checklist.cod_checklist_modelo,
                                alternativa_modelo_checklist.cod_unidade,
                                alternativa_modelo_checklist.alternativa,
                                alternativa_modelo_checklist.ordem,
                                alternativa_modelo_checklist.status_ativo,
                                alternativa_modelo_checklist.cod_pergunta,
                                alternativa_modelo_checklist.alternativa_tipo_outros,
                                alternativa_modelo_checklist.prioridade,
                                alternativa_modelo_checklist.deve_abrir_ordem_servico,
                                alternativa_modelo_checklist.codigo_fixo_alternativa) returning codigo
                                   into cod_alternativa_criado;

                        --                         select array_append(alternativas,
--                                             concat(alternativa_modelo_checklist.codigo, '_', cod_alternativa_criado));
                        insert into check_alternativas_aux (cod_modelo,
                                                            cod_modelo_versao,
                                                            cod_alternativa_antigo,
                                                            cod_alternativa_novo)
                        values (f_cod_modelo,
                                novo_cod_versao_modelo,
                                alternativa_modelo_checklist.codigo,
                                cod_alternativa_criado);

                        get diagnostics qtd_linhas_inseridas = row_count;

                        if qtd_linhas_inseridas is null or qtd_linhas_inseridas <> 1
                        then
                            raise exception 'Erro ao inserir alternativa aux';
                        end if;

                    end loop;
            end loop;
        --

        return novo_cod_versao_modelo;
    end
    $func$ language plpgsql;


    create or replace function func_checklist_ativar_constraints_versao()
        returns void
        language plpgsql
    as
    $func$
    begin
        -- Impede novas versão com cod_colaborador_criacao_versao nulo.
        alter table checklist_modelo_versao
            add constraint check_colaborador_not_null_acima_versao_1 check (cod_colaborador_criacao_versao is not null) not valid;

        -- Agora pode ser NOT NULL.
        alter table checklist_modelo_data
            alter column cod_versao_atual set not null;
        alter table checklist_data
            alter column cod_versao_checklist_modelo set not null;

        -- Remove FK única com cod_modelo e usa uma dupla compondo com versão do modelo.
        alter table checklist_perguntas_data
            drop constraint fk_checklist_perguntas_checklist_modelo;
        alter table checklist_perguntas_data
            add constraint fk_checklist_perguntas_checklist_modelo_versao
                foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                    references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);

        -- Remove FK única com cod_modelo e usa uma dupla compondo com versão do modelo.
        alter table checklist_alternativa_pergunta_data
            drop constraint fk_checklist_alternativa_pergunta_checklist_modelo;
        alter table checklist_alternativa_pergunta_data
            add constraint fk_checklist_alternativa_pergunta_checklist_modelo_versao
                foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                    references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);
    end
    $func$;
end
$$;