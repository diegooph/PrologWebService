-- Sobre:
--
-- Essa function atualiza uma O.S que sofreu erro ao tentativa de sincronização com a mensagem de erro em questão,
-- além de aumentar a quantidade de tentativas de sincronização e registrar a hora da tentativa.
--
-- Histórico:
-- 2020-08-28 -> Function criada (gustavocnp95 - PL-2903).
-- 2020-08-31 -> Adiciona a exception que gerou o erro (diogenesvanzella - PL-3114).
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
create or replace function integracao.func_checklist_os_atualiza_erro_os(f_cod_interno_os_prolog bigint,
                                                                         f_error_message text,
                                                                         f_exception_logada text)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set quantidade_tentativas     = quantidade_tentativas + 1,
        data_ultima_tentativa     = now(),
        mensagem_ultima_tentativa = f_if(f_error_message is not null,
                                         f_error_message,
                                         'Nenhuma resposta do servidor integrado.'),
        exception_logada          = f_if(f_exception_logada is not null,
                                         f_exception_logada,
                                         null)
    where codigo_os_prolog = f_cod_interno_os_prolog;
end;
$$;