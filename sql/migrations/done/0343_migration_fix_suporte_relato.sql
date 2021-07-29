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

    if (select not exists(select *
                          from setor
                          where cod_unidade = f_cod_unidade
                            and f_if(f_cod_setor is null, true, codigo = f_cod_setor)))
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
      and f_if(f_cod_setor is null, true, cod_setor = f_cod_setor)
      and codigo = any (f_cod_alternativas);

    select 'Alternativas de relato foram ativadas ou inativadas com sucesso!'
    into f_aviso_alternativa_relato;
end
$$;