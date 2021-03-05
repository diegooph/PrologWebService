create or replace function cs.func_nps_insere_nova_pesquisa(f_titulo_pesquisa text,
                                                            f_breve_descricao_pesquisa text,
                                                            f_titulo_pergunta_escala text,
                                                            f_legenda_escala_baixa text,
                                                            f_legenda_escala_alta text,
                                                            f_titulo_pergunta_descritiva text,
                                                            f_data_inicio_veiculacao_inclusivo date,
                                                            f_data_fim_veiculacao_exclusivo date,
                                                            out aviso_pesquisa_inserida text)
    returns text
    language plpgsql
    -- Para o time de CS poder usar.
    security definer
as
$$
begin
    -- Antes de inserir uma nova pesquisa, inativa todas as anteriores.
    update cs.nps_pesquisa set status_ativo = false;

    insert into cs.nps_pesquisa (titulo_pesquisa,
                                 breve_descricao_pesquisa,
                                 periodo_veiculacao_pesquisa,
                                 titulo_pergunta_escala,
                                 legenda_escala_baixa,
                                 legenda_escala_alta,
                                 titulo_pergunta_descritiva)
    values (f_titulo_pesquisa,
            f_breve_descricao_pesquisa,
            daterange(f_data_inicio_veiculacao_inclusivo, f_data_fim_veiculacao_exclusivo),
            f_titulo_pergunta_escala,
            f_legenda_escala_baixa,
            f_legenda_escala_alta,
            f_titulo_pergunta_descritiva);

    if not found
    then
        raise exception 'Erro ao inserir nova pesquisa de NPS';
    end if;

    select 'Pesquisa de NPS inserida com sucesso!'
    into aviso_pesquisa_inserida;
end;
$$;