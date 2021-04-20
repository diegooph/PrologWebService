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
        raise exception 'A data/hora de abertura não pode ser posterior à data/hora fechamento.';
    end if;

    if (f_novo_nome_quiz is null
        or f_novo_nome_quiz = ''
        or f_nova_descricao_quiz is null
        or f_nova_descricao_quiz = ''
        or f_nova_data_hora_abertura_quiz is null
        or f_nova_data_hora_fechamento_quiz is null)
    then
        raise exception 'Todos os parâmetros do Modelo de Quiz devem ser fornecidos. Não é permitido valores vazios.';
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