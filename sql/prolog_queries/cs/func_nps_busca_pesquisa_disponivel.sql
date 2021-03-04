-- Sobre:
-- Busca uma pesquisa de NPS que esteja disponível para o colaborador realizar. Uma pesquisa está disponível para
-- realização se passar nas quatro condições seguintes:
-- 1 - Está ativa (status_ativo = true).
-- 2 - O período de veículação da pesquisa deve abranger a data em que a busca é feita.
-- 3 - O colaborador não deve ter bloqueado a pesquisa.
-- 4 - O colaborador não pode já ter respondido a pesquisa.
--
-- Histórico:
-- 2019-10-10 -> Function criada (luizfp - PL-2350).
-- 2019-10-28 -> Adição de colunas para legenda de escala alta/baixa (wvinim - PL-2355).
create or replace function cs.func_nps_busca_pesquisa_disponivel(f_cod_colaborador bigint,
                                                                 f_data_atual date)
    returns table
            (
                COD_PESQUISA_NPS           BIGINT,
                TITULO_PESQUISA            TEXT,
                BREVE_DESCRICAO_PESQUISA   TEXT,
                TITULO_PERGUNTA_ESCALA     TEXT,
                LEGENDA_ESCALA_BAIXA       TEXT,
                LEGENDA_ESCALA_ALTA        TEXT,
                TITULO_PERGUNTA_DESCRITIVA TEXT
            )
    language plpgsql
as
$$
declare
    f_cod_pesquisa_nps           bigint;
    f_titulo_pesquisa            text;
    f_breve_descricao_pesquisa   text;
    f_titulo_pergunta_escala     text;
    f_legenda_escala_baixa       text;
    f_legenda_escala_alta        text;
    f_titulo_pergunta_descritiva text;
begin
    -- Mesmo tendo o index para permitir apenas uma ativa por vez, esse SELECT já garante isso também.
    select np.codigo,
           np.titulo_pesquisa,
           np.breve_descricao_pesquisa,
           np.titulo_pergunta_escala,
           np.legenda_escala_baixa,
           np.legenda_escala_alta,
           np.titulo_pergunta_descritiva
    from cs.nps_pesquisa np
    -- Ativo e ainda em veiculação.
    where np.status_ativo
    and f_data_atual <@ periodo_veiculacao_pesquisa
    into
        f_cod_pesquisa_nps,
        f_titulo_pesquisa,
        f_breve_descricao_pesquisa,
        f_titulo_pergunta_escala,
        f_legenda_escala_baixa,
        f_legenda_escala_alta,
        f_titulo_pergunta_descritiva;

    if f_cod_pesquisa_nps is null
    then
        return query
        select null :: bigint, null :: text, null :: text, null :: text, null :: text, null :: text, null :: text;

        -- Break.
        return;
    end if;

    -- Se o colaborador ainda não respondeu e também não bloqueou a pesquisa, então temos uma disponível.
    if ((select not exists(select nbpc.cod_nps_pesquisa
                          from cs.nps_bloqueio_pesquisa_colaborador nbpc
                          where nbpc.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nbpc.cod_colaborador_bloqueio = f_cod_colaborador))
        and
        (select not exists(select nr.cod_nps_pesquisa
                          from cs.nps_respostas nr
                          where nr.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nr.cod_colaborador_respostas = f_cod_colaborador)))
    then
        return query
            select f_cod_pesquisa_nps,
                   f_titulo_pesquisa,
                   f_breve_descricao_pesquisa,
                   f_titulo_pergunta_escala,
                   f_legenda_escala_baixa,
                   f_legenda_escala_alta,
                   f_titulo_pergunta_descritiva;
    end if;
end;
$$;