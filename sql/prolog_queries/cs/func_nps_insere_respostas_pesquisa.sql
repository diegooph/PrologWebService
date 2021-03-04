-- Sobre:
-- Insere as respostas de uma pesquisa de NPS.
--
-- A function não verifica se a resposta já existe, deixando estourar erro de unique.
--
-- Histórico:
-- 2019-10-10 -> Function criada (luizfp - PL-2350).
create or replace function cs.func_nps_insere_respostas_pesquisa(f_cod_pesquisa_nps bigint,
                                                                 f_cod_colaborador_realizacao bigint,
                                                                 f_data_hora_realizacao_pesquisa timestamp with time zone,
                                                                 f_resposta_pergunta_escala smallint,
                                                                 f_resposta_pergunta_descritiva text)
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
                                  resposta_pergunta_descritiva)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao,
            f_data_hora_realizacao_pesquisa,
            f_resposta_pergunta_escala,
            f_resposta_pergunta_descritiva) returning codigo into cod_respostas_pesquisa_nps;

    if not FOUND
    then
        raise exception 'Erro ao inserir respostas da pesquisa de NPS % para colaborador %',
            f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao;
    end if;

    return cod_respostas_pesquisa_nps;
end;
$$;