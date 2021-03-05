drop function if exists suporte.func_checklist_copia_modelo_checklist_entre_empresas(f_cod_modelo_checklist_copiado bigint,
    f_cod_unidade_destino_modelo_checklist bigint,
    f_cod_colaborador_solicitante_copia bigint,
    f_cod_cargos_checklist bigint[],
    f_cod_tipos_veiculos_checklist bigint[]);
create or replace function
    suporte.func_checklist_copia_modelo_checklist_entre_empresas(f_cod_modelo_checklist_copiado bigint,
                                                                 f_cod_unidade_destino_modelo_checklist bigint,
                                                                 f_cod_colaborador_solicitante_copia bigint,
                                                                 f_deve_copiar_cod_auxiliar boolean default false,
                                                                 f_cod_cargos_checklist bigint[] default null,
                                                                 f_cod_tipos_veiculos_checklist bigint[] default null,
                                                                 out cod_modelo_checklist_inserido bigint,
                                                                 out aviso_modelo_inserido text)
    returns record
    language plpgsql
    security definer
as
$$
declare
    f_cod_empresa_destino_modelo_checklist bigint := (select u.cod_empresa
                                                      from unidade u
                                                      where u.codigo = f_cod_unidade_destino_modelo_checklist);
    cod_unidade_modelo_checklist_copiado   bigint;
    cod_pergunta_criado                    bigint;
    novo_cod_versao_modelo                 bigint;
    cod_versao_modelo_checklist_copiado    bigint := (select cod_versao_atual
                                                      from checklist_modelo
                                                      where codigo = f_cod_modelo_checklist_copiado);
    pergunta_modelo_checklist_copiado      checklist_perguntas_data%rowtype;
begin
    perform suporte.func_historico_salva_execucao();
    -- Verifica se colaborador pertence à empresa destino.
    perform func_garante_integridade_empresa_cod_colaborador(f_cod_empresa_destino_modelo_checklist,
                                                             f_cod_colaborador_solicitante_copia);

    -- Verifica se o modelo de checklist existe.
    if not exists(select cm.codigo
                  from checklist_modelo cm
                  where cm.codigo = f_cod_modelo_checklist_copiado)
    then
        raise exception 'Modelo de checklist de código % não existe!', f_cod_modelo_checklist_copiado;
    end if;

    -- Verifica se a unidade de código informado existe.
    perform func_garante_unidade_existe(f_cod_unidade_destino_modelo_checklist);

    -- Verifica se estamos copiando o modelo de checklist entre unidades de empresas diferentes.
    select cm.cod_unidade
    from checklist_modelo cm
    where cm.codigo = f_cod_modelo_checklist_copiado
    into cod_unidade_modelo_checklist_copiado;
    if (f_cod_empresa_destino_modelo_checklist =
        (select u.cod_empresa
         from unidade u
         where u.codigo = cod_unidade_modelo_checklist_copiado))
    then
        raise exception 'Essa function deve ser utilizada para copiar modelos de checklists entre empresas diferentes.
                        Utilize a function: FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST, para copiar checklists entre unidades
             da mesma empresa.';
    end if;

    if f_cod_cargos_checklist is not null
    then
        -- Verifica se todos os cargos existem.
        if (select exists(select cod_cargo
                          from unnest(f_cod_cargos_checklist) as cod_cargo
                                   left join funcao f on f.codigo = cod_cargo
                          where f.codigo is null))
        then
            raise exception 'O(s) cargo(s) % não existe(m) no ProLog', (select array_agg(cod_cargo)
                                                                        from unnest(f_cod_cargos_checklist)
                                                                                 as cod_cargo
                                                                                 left join funcao f
                                                                                           on f.codigo = cod_cargo
                                                                        where f.codigo is null);
        end if;

        -- Verifica se todos os cargos pertencem a empresa de destino.
        if (select exists(select cod_cargo
                          from unnest(f_cod_cargos_checklist) as cod_cargo
                                   left join funcao f on f.codigo = cod_cargo
                          where f.cod_empresa <> f_cod_empresa_destino_modelo_checklist))
        then
            raise exception 'O(s) cargo(s) % não pertence(m) a empresa para a qual você está tentando copiar o
                modelo checklit, empresa: %',
                (select array_agg(cod_cargo)
                 from unnest(f_cod_cargos_checklist) as cod_cargo
                          left join funcao f on f.codigo = cod_cargo
                 where f.cod_empresa <> f_cod_empresa_destino_modelo_checklist),
                (select e.nome
                 from empresa e
                 where e.codigo = f_cod_empresa_destino_modelo_checklist);
        end if;
    end if;

    if f_cod_tipos_veiculos_checklist is not null
    then
        -- Verifica se todos os tipos de veículo existem.
        if (select exists(select cod_tipo_veiculo
                          from unnest(f_cod_tipos_veiculos_checklist) as cod_tipo_veiculo
                                   left join veiculo_tipo vt
                                             on vt.codigo = cod_tipo_veiculo
                          where vt.codigo is null))
        then
            raise exception 'O(s) tipo(s) de veículo % não existe(m) no ProLog', (select array_agg(cod_tipo_veiculo)
                                                                                  from unnest(f_cod_tipos_veiculos_checklist)
                                                                                           as cod_tipo_veiculo
                                                                                           left join veiculo_tipo vt
                                                                                                     on vt.codigo = cod_tipo_veiculo
                                                                                  where vt.codigo is null);
        end if;

        -- Verifica se todos os tipos de veículo pertencem a empresa de destino.
        if (select exists(select cod_tipo_veiculo
                          from unnest(f_cod_tipos_veiculos_checklist) as cod_tipo_veiculo
                                   left join veiculo_tipo vt
                                             on vt.codigo = cod_tipo_veiculo
                          where vt.cod_empresa <> f_cod_empresa_destino_modelo_checklist))
        then
            raise exception 'O(s) tipo(s) de veículo % não pertence(m) a empresa para a qual você está tentando
                copiar o modelo checklit, empresa: %',
                (select array_agg(cod_tipo_veiculo)
                 from unnest(f_cod_tipos_veiculos_checklist) as cod_tipo_veiculo
                          left join veiculo_tipo vt
                                    on vt.codigo = cod_tipo_veiculo
                 where vt.cod_empresa <> f_cod_empresa_destino_modelo_checklist),
                (select e.nome
                 from empresa e
                 where e.codigo = f_cod_empresa_destino_modelo_checklist);
        end if;
    end if;

    -- Busca o novo código de versão do modelo de checklist.
    novo_cod_versao_modelo := nextval(
            pg_get_serial_sequence('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    set constraints all deferred;

    -- Insere o modelo de checklist.
    insert into checklist_modelo (cod_unidade, cod_versao_atual, nome, status_ativo)
    select f_cod_unidade_destino_modelo_checklist,
           novo_cod_versao_modelo,
           concat(cc.nome, ' (cópia)'),
           cc.status_ativo
    from checklist_modelo cc
    where cc.codigo = f_cod_modelo_checklist_copiado
    returning codigo into cod_modelo_checklist_inserido;

    -- Verificamos se o insert funcionou.
    if cod_modelo_checklist_inserido is null or cod_modelo_checklist_inserido <= 0
    then
        raise exception 'Não foi possível copiar o modelo de checklist';
    end if;

    -- Insere a versão.
    insert into checklist_modelo_versao(cod_versao_checklist_modelo,
                                        cod_versao_user_friendly,
                                        cod_checklist_modelo,
                                        data_hora_criacao_versao,
                                        cod_colaborador_criacao_versao)
    values (novo_cod_versao_modelo,
            1,
            cod_modelo_checklist_inserido,
            now(),
            f_cod_colaborador_solicitante_copia);

    select concat('Modelo inserido com sucesso, código: ', cod_modelo_checklist_inserido)
    into aviso_modelo_inserido;

    if f_cod_cargos_checklist is not null
    then
        -- Insere cargos que podem realizar o modelo de checklist
        insert into checklist_modelo_funcao (cod_checklist_modelo, cod_unidade, cod_funcao)
        select cod_modelo_checklist_inserido          cod_checklist_modelo,
               f_cod_unidade_destino_modelo_checklist cod_unidade,
               codigo_funcao
        from unnest(f_cod_cargos_checklist) codigo_funcao;
    end if;

    if f_cod_tipos_veiculos_checklist is not null
    then
        -- Insere tipos de veículos liberados para o modelo de checklist
        insert into checklist_modelo_veiculo_tipo (cod_modelo, cod_unidade, cod_tipo_veiculo)
        select cod_modelo_checklist_inserido          cod_checklist_modelo,
               f_cod_unidade_destino_modelo_checklist cod_unidade,
               codigo_tipo_veiculo
        from unnest(f_cod_tipos_veiculos_checklist) codigo_tipo_veiculo;
    end if;

    -- Insere as perguntas e alternativas.
    for pergunta_modelo_checklist_copiado in
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        select cp.cod_checklist_modelo,
               cp.cod_unidade,
               cp.ordem,
               cp.pergunta,
               cp.single_choice,
               cp.cod_imagem,
               cp.codigo,
               null,
               null,
               null,
               null,
               null,
               cp.anexo_midia_resposta_ok
        from checklist_perguntas cp
        where cp.cod_versao_checklist_modelo = cod_versao_modelo_checklist_copiado
        loop
            -- Pergunta.
            insert into checklist_perguntas (cod_checklist_modelo,
                                             cod_unidade,
                                             ordem,
                                             pergunta,
                                             single_choice,
                                             cod_imagem,
                                             anexo_midia_resposta_ok,
                                             cod_versao_checklist_modelo)
            values (cod_modelo_checklist_inserido,
                    f_cod_unidade_destino_modelo_checklist,
                    pergunta_modelo_checklist_copiado.ordem,
                    pergunta_modelo_checklist_copiado.pergunta,
                    pergunta_modelo_checklist_copiado.single_choice,
                       -- Só copiamos o código da imagem se a imagem vinculada for da galeria pública do Prolog.
                    f_if((select exists(select cgi.cod_imagem
                                        from checklist_galeria_imagens cgi
                                        where cgi.cod_imagem = pergunta_modelo_checklist_copiado.cod_imagem
                                          and cgi.cod_empresa is null)),
                         pergunta_modelo_checklist_copiado.cod_imagem,
                         null),
                    pergunta_modelo_checklist_copiado.anexo_midia_resposta_ok,
                    novo_cod_versao_modelo)
            returning codigo into cod_pergunta_criado;
            -- Alternativa.
            insert into checklist_alternativa_pergunta (cod_checklist_modelo,
                                                        cod_unidade,
                                                        alternativa,
                                                        ordem,
                                                        cod_pergunta,
                                                        alternativa_tipo_outros,
                                                        prioridade,
                                                        deve_abrir_ordem_servico,
                                                        anexo_midia,
                                                        cod_versao_checklist_modelo,
                                                        cod_auxiliar)
                (select cod_modelo_checklist_inserido,
                        f_cod_unidade_destino_modelo_checklist,
                        cap.alternativa,
                        cap.ordem,
                        cod_pergunta_criado,
                        cap.alternativa_tipo_outros,
                        cap.prioridade,
                        cap.deve_abrir_ordem_servico,
                        cap.anexo_midia,
                        novo_cod_versao_modelo,
                        f_if(f_deve_copiar_cod_auxiliar, cap.cod_auxiliar, null)
                 from checklist_alternativa_pergunta cap
                 where cap.cod_versao_checklist_modelo = cod_versao_modelo_checklist_copiado
                   and cap.cod_pergunta = pergunta_modelo_checklist_copiado.codigo);
        end loop;
end
$$;