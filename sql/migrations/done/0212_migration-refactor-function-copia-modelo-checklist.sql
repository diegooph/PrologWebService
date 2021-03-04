create or replace function suporte.func_checklist_copia_modelo_checklist(f_cod_modelo_checklist_copiado bigint,
                                                                         f_cod_unidade_destino_modelo_checklist bigint,
                                                                         f_cod_colaborador_solicitante_copia bigint,
                                                                         f_copiar_cargos_liberados boolean default true,
                                                                         f_copiar_tipos_veiculos_liberados boolean default true,
                                                                         out cod_modelo_checklist_inserido bigint,
                                                                         out aviso_modelo_inserido text)
    returns record
    language plpgsql
    security definer
as
$$
declare
    cod_unidade_modelo_checklist_copiado  bigint;
    cod_pergunta_criado                   bigint;
    f_cod_empresa                         bigint := (select cod_empresa
                                                     from unidade
                                                     where codigo = f_cod_unidade_destino_modelo_checklist);
    pergunta_modelo_checklist_copiado     checklist_perguntas_data%rowtype;
    modelo_veiculo_tipo_checklist_copiado checklist_modelo_veiculo_tipo%rowtype;
    nome_modelo_checklist_copiado         text;
    cod_versao_modelo_checklist_copiado   bigint := (select cod_versao_atual
                                                     from checklist_modelo
                                                     where codigo = f_cod_modelo_checklist_copiado);
    status_modelo_checklist_copiado       boolean;
    novo_cod_versao_modelo                bigint;
begin
    perform suporte.func_historico_salva_execucao();
    -- Verifica se colaborador pertence à empresa.
    perform func_garante_integridade_empresa_cod_colaborador(f_cod_empresa, f_cod_colaborador_solicitante_copia);

    -- Verifica se o modelo de checklist existe.
    if not exists(select codigo
                  from checklist_modelo
                  where codigo = f_cod_modelo_checklist_copiado)
    then
        raise exception 'Modelo de checklist de código % não existe!', f_cod_modelo_checklist_copiado;
    end if;

    -- Verifica se a unidade de código informado existe.
    perform func_garante_unidade_existe(f_cod_unidade_destino_modelo_checklist);

    -- Verifica se estamos copiando o modelo de checklist entre unidades da mesma empresa.
    select cod_unidade
    from checklist_modelo cm
    where cm.codigo = f_cod_modelo_checklist_copiado
    into cod_unidade_modelo_checklist_copiado;
    if (f_cod_empresa !=
        (select u.cod_empresa
         from unidade u
         where u.codigo = cod_unidade_modelo_checklist_copiado))
    then
        raise exception 'Só é possível copiar modelos de checklists entre unidades da mesma empresa para garantirmos
            o vínculo correto de imagens da galeria.';
    end if;

    -- Busca o nome e status do modelo copiado.
    select concat(cc.nome, ' (cópia)'), cc.status_ativo
    from checklist_modelo cc
    where cc.codigo = f_cod_modelo_checklist_copiado
    into nome_modelo_checklist_copiado, status_modelo_checklist_copiado;

    -- Busca o novo código de versão do modelo de checklist.
    novo_cod_versao_modelo := nextval(
            pg_get_serial_sequence('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    set constraints all deferred;

    -- Insere o modelo de checklist.
    insert into checklist_modelo (cod_unidade, cod_versao_atual, nome, status_ativo)
    values (f_cod_unidade_destino_modelo_checklist,
            novo_cod_versao_modelo,
            nome_modelo_checklist_copiado,
            status_modelo_checklist_copiado)
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

    if f_copiar_cargos_liberados
    then
        -- Insere os cargos liberados.
        insert into checklist_modelo_funcao (cod_checklist_modelo, cod_unidade, cod_funcao)
            (select cod_modelo_checklist_inserido,
                    f_cod_unidade_destino_modelo_checklist,
                    cmf.cod_funcao
             from checklist_modelo_funcao cmf
             where cmf.cod_checklist_modelo = f_cod_modelo_checklist_copiado);
    end if;

    if f_copiar_tipos_veiculos_liberados
    then
        -- Copia os tipos de veículo vinculados.
        for modelo_veiculo_tipo_checklist_copiado in
            select cmvt.cod_unidade,
                   cmvt.cod_modelo,
                   cmvt.cod_tipo_veiculo
            from checklist_modelo_veiculo_tipo cmvt
            where cmvt.cod_modelo = f_cod_modelo_checklist_copiado
            loop
                -- Insere os tipos de veículos vinculados.
                insert into checklist_modelo_veiculo_tipo (cod_unidade, cod_modelo, cod_tipo_veiculo)
                values (f_cod_unidade_destino_modelo_checklist,
                        cod_modelo_checklist_inserido,
                        modelo_veiculo_tipo_checklist_copiado.cod_tipo_veiculo);
            end loop;
    end if;

    -- Insere as perguntas e alternativas.
    for pergunta_modelo_checklist_copiado in
        -- Usamos vários nulls pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- e antes da coluna de mídia, existem várias outras.
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
                    pergunta_modelo_checklist_copiado.cod_imagem,
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
                        cap.cod_auxiliar
                 from checklist_alternativa_pergunta cap
                 where cap.cod_versao_checklist_modelo = cod_versao_modelo_checklist_copiado
                   and cap.cod_pergunta = pergunta_modelo_checklist_copiado.codigo);
        end loop;
end;
$$;