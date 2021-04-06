create or replace function suporte.func_historico_salva_execucao(f_informacoes_extras text default null)
    returns void
    security definer
    language sql
as
$$
insert into suporte.historico_uso_function (function_query,
                                            data_hora_execucao,
                                            pg_username_execucao,
                                            informacoes_extras)
values (current_query(),
        now(),
        session_user,
        f_informacoes_extras)
$$;