create or replace function
    suporte.func_quiz_editar_quiz_modelo(f_cod_unidade bigint,
                                         f_cod_modelo_quiz bigint,
                                         f_novo_nome_quiz text,
                                         f_nova_descricao_quiz text,
                                         f_nova_data_hora_abertura_quiz timestamp with time zone,
                                         f_nova_data_hora_fechamento_quiz timestamp with time zone,
                                         f_informacoes_extras_suporte text,
                                         out f_aviso_associar_treinamento text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);
    perform func_garante_modelo_quiz_existe(f_cod_modelo_quiz);

    -- Verifica se o modelo de quiz é da unidade informada.
    if (select qm.cod_unidade
        from quiz_modelo qm
        where qm.codigo = f_cod_modelo_quiz) <> f_cod_unidade
    then
        raise exception
            'O modelo de quiz de código % não é da unidade %.', f_cod_modelo_quiz, f_cod_unidade;
    end if;

    if (f_nova_data_hora_abertura_quiz > f_nova_data_hora_fechamento_quiz)
    then
        raise exception
            'A data/hora de abertura não pode ser posterior à data/hora fechamento.';
    end if;

    update quiz_modelo
    set nome                 = f_novo_nome_quiz,
        descricao            = f_nova_descricao_quiz,
        data_hora_abertura   = f_nova_data_hora_abertura_quiz,
        data_hora_fechamento = f_nova_data_hora_fechamento_quiz
    where codigo = f_cod_modelo_quiz
      and cod_unidade = f_cod_unidade;

    select 'O modelo de quiz '
               || f_cod_modelo_quiz ||
           ' foi atualizado com sucesso!'
    into f_aviso_associar_treinamento;
end ;
$$;


create or replace function
    suporte.func_quiz_vincula_desvincula_cargo(f_cod_unidade bigint,
                                               f_cod_modelo_quiz bigint,
                                               f_cod_cargo bigint,
                                               f_vincular_cargo boolean,
                                               f_informacoes_extras_suporte text,
                                               out f_aviso_associar_treinamento text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);
    perform func_garante_modelo_quiz_existe(f_cod_modelo_quiz);

    -- Verifica se o modelo de quiz é da unidade informada.
    if (select qm.cod_unidade
        from quiz_modelo qm
        where qm.codigo = f_cod_modelo_quiz) <> f_cod_unidade
    then
        raise exception
            'O modelo de quiz de código % não é da unidade %.', f_cod_modelo_quiz, f_cod_unidade;
    end if;

    if (f_vincular_cargo)
    then
        insert into quiz_modelo_funcao (cod_unidade, cod_modelo, cod_funcao_colaborador)
        values (f_cod_unidade, f_cod_modelo_quiz, f_cod_cargo);
    else
        delete
        from quiz_modelo_funcao
        where cod_unidade = f_cod_unidade
          and cod_modelo = f_cod_modelo_quiz
          and cod_funcao_colaborador = f_cod_cargo;
    end if;

    select 'Os cargos do modelo de quiz '
               || f_cod_modelo_quiz ||
           ' foram atualizado com sucesso!'
    into f_aviso_associar_treinamento;
end ;
$$;