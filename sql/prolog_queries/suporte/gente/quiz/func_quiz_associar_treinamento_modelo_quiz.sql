create or replace function suporte.func_quiz_associar_treinamento_modelo_quiz(f_cod_unidade bigint,
                                                                              f_cod_modelo_quiz bigint,
                                                                              f_cod_treinamento bigint,
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
    perform func_garante_treinamento_existe(f_cod_treinamento);

    -- Verifica se o modelo de quiz é da unidade informada.
    if (select qm.cod_unidade
        from quiz_modelo qm
        where qm.codigo = f_cod_modelo_quiz) <> f_cod_unidade
    then
        raise exception
            'O modelo de quiz de código % não é da unidade %.', f_cod_modelo_quiz, f_cod_unidade;
    end if;

    -- Verifica se o treinamento é da unidade informada.
    if (select t.cod_unidade
        from treinamento t
        where t.codigo = f_cod_treinamento) <> f_cod_unidade
    then
        raise exception
            'O treinamento de código % não é da unidade %.', f_cod_treinamento, f_cod_unidade;
    end if;

    insert into quiz_modelo_treinamento (cod_modelo_quiz,
                                         cod_unidade,
                                         cod_treinamento)
    values (f_cod_modelo_quiz, f_cod_unidade, f_cod_treinamento)
    on conflict on constraint unico_treinamento_por_modelo
        do update set cod_treinamento = f_cod_treinamento;

    select 'O treinamento de código '
               || f_cod_treinamento ||
           ' foi vinculado ao modelo de quiz de código '
               || f_cod_modelo_quiz ||
           ' com sucesso!'
    into f_aviso_associar_treinamento;
end ;
$$;