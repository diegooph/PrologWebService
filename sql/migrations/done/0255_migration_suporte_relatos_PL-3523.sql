create or replace function
    suporte.func_relato_insere_alternativa_relato_unidade_setor(f_cod_unidade bigint,
                                                                f_cod_setor bigint,
                                                                f_alternativa_relato text,
                                                                f_informacoes_extras_suporte text,
                                                                out f_aviso_alternativa_relato text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_alternativa_relato_inserido bigint;
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);

    if not exists(select * from setor where cod_unidade = f_cod_unidade and codigo = f_cod_setor)
    then
        raise exception 'O código do setor (%) não pertence à unidade (%)', f_cod_setor, f_cod_unidade;
    end if;

    insert into relato_alternativa (cod_unidade, alternativa, status_ativo, cod_setor)
    values (f_cod_unidade, f_alternativa_relato, true, f_cod_setor)
    returning codigo into v_cod_alternativa_relato_inserido;

    select 'Alternativa de relato inserida para a unidade ' || f_cod_unidade ||
           ' e setor ' || f_cod_setor ||
           ' foi inserida com sucesso. código da Alternativa = ' || v_cod_alternativa_relato_inserido
    into f_aviso_alternativa_relato;
end
$$;

-- #####################################################################################################################
create or replace function
    suporte.func_relato_ativa_inativa_alternativa_relato_unidade_setor(f_cod_unidade bigint,
                                                                       f_cod_setor bigint,
                                                                       f_cod_alternativas bigint[],
                                                                       f_ativar_alternativa boolean,
                                                                       f_informacoes_extras_suporte text,
                                                                       out f_aviso_alternativa_relato text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_alternativas_setor constant bigint[] := (select array_agg(ra.codigo)
                                                   from relato_alternativa ra
                                                   where ra.cod_unidade = f_cod_unidade
                                                     and ra.cod_setor = f_cod_setor);
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);

    if not exists(select * from setor where cod_unidade = f_cod_unidade and codigo = f_cod_setor)
    then
        raise exception 'O código do setor (%) não pertence à unidade (%)', f_cod_setor, f_cod_unidade;
    end if;

    if not (f_cod_alternativas <@ v_cod_alternativas_setor)
    then
        raise exception
            'Alguma das alternativas de relato não pertecem à unidade (%) e setor (%)', f_cod_unidade, f_cod_setor;
    end if;

    update relato_alternativa
    set status_ativo = f_ativar_alternativa
    where cod_unidade = f_cod_unidade
      and cod_setor = f_cod_setor
      and codigo = any (f_cod_alternativas);

    select 'Alternativas de relato foram ativadas ou inativadas com sucesso!'
    into f_aviso_alternativa_relato;
end
$$;

-- #####################################################################################################################
create or replace function
    suporte.func_relato_altera_alternativa_marcada_relato(f_cod_unidade bigint,
                                                          f_cod_setor bigint,
                                                          f_cod_alternativas_antigas bigint[],
                                                          f_novo_cod_alternativa bigint,
                                                          f_informacoes_extras_suporte text,
                                                          out f_aviso_relato_alterado text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_alternativas_setor constant bigint[] := (select array_agg(ra.codigo)
                                                   from relato_alternativa ra
                                                   where ra.cod_unidade = f_cod_unidade
                                                     and ra.cod_setor = f_cod_setor);
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);

    if not exists(select * from setor where cod_unidade = f_cod_unidade and codigo = f_cod_setor)
    then
        raise exception 'O código do setor (%) não pertence à unidade (%)', f_cod_setor, f_cod_unidade;
    end if;

    if not exists(select *
                  from relato_alternativa
                  where codigo = f_novo_cod_alternativa
                    and cod_unidade = f_cod_unidade
                    and cod_setor = f_cod_setor)
    then
        raise exception 'O novo código da alternativa (%) não pertence à unidade (%) e ao setor (%)',
            f_novo_cod_alternativa, f_cod_unidade, f_cod_setor;
    end if;

    if not (f_cod_alternativas_antigas <@ v_cod_alternativas_setor)
    then
        raise exception
            'Alguma das alternativas antigas de relato não pertecem à unidade (%) e setor (%)',
            f_cod_unidade, f_cod_setor;
    end if;

    update relato
    set cod_alternativa = f_novo_cod_alternativa
    where cod_unidade = f_cod_unidade
      and cod_setor = f_cod_setor
      and cod_alternativa = any (f_cod_alternativas_antigas);

    select 'Relatos alterados com sucesso'
    into f_aviso_relato_alterado;
end
$$;