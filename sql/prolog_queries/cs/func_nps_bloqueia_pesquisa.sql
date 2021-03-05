create or replace function cs.func_nps_bloqueia_pesquisa(f_cod_pesquisa_nps bigint,
                                                         f_cod_colaborador_bloqueio bigint,
                                                         f_data_hora_bloqueio_pesquisa timestamp with time zone,
                                                         f_origem_bloqueio_pesquisa text)
    returns void
    language plpgsql
as
$$
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_bloqueio_pesquisa_colaborador (cod_nps_pesquisa,
                                                      cod_colaborador_bloqueio,
                                                      data_hora_bloqueio_pesquisa,
                                                      origem_bloqueio)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio,
            f_data_hora_bloqueio_pesquisa,
            f_origem_bloqueio_pesquisa);

    if not found
    then
        raise exception 'Erro ao bloquear pesquisa de NPS % para colaborador %.',
            f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio;
    end if;
end;
$$;