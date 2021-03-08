create or replace function cs.func_nps_insere_respostas_pesquisa(f_cod_pesquisa_nps bigint,
                                                                 f_cod_colaborador_realizacao bigint,
                                                                 f_data_hora_realizacao_pesquisa timestamp with time zone,
                                                                 f_resposta_pergunta_escala smallint,
                                                                 f_resposta_pergunta_descritiva text,
                                                                 f_origem_resposta text)
    returns bigint
    language plpgsql
as
$$
declare
    cod_respostas_pesquisa_nps bigint;
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_respostas (cod_nps_pesquisa,
                                  cod_colaborador_respostas,
                                  data_hora_realizacao_pesquisa,
                                  resposta_pergunta_escala,
                                  resposta_pergunta_descritiva,
                                  origem_resposta)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao,
            f_data_hora_realizacao_pesquisa,
            f_resposta_pergunta_escala,
            f_resposta_pergunta_descritiva,
            f_origem_resposta) returning codigo into cod_respostas_pesquisa_nps;

    if not FOUND
    then
        raise exception 'Erro ao inserir respostas da pesquisa de NPS % para colaborador %.',
            f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao;
    end if;

    return cod_respostas_pesquisa_nps;
end;
$$;
