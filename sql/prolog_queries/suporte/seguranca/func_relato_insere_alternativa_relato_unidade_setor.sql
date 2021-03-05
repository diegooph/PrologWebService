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