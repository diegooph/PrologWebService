-- Sobre:
-- Essa function, quando perfomada em outra function, loga a execução no mais top level. Ou seja, se alguém executar
-- uma function: select * from function_que_faz_alguma_coisa(); E, no corpo dessa function_que_faz_alguma_coisa(), tiver
-- essa chamada: perform PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
--
-- Histórico:
-- 2020-08-14 -> Cria function (gustavocnp95 - PL-3066).
-- 2020-09-08 -> Adiciona parâmetro opcional para salvar informações extras no histórico (luiz_fp - PL-3134).
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