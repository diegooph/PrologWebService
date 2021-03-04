-- Sobre:
--
-- Essa function atualiza o status de uma O.S pendente de integração, com base nos parâmetros passados.
--
-- Histórico:
-- 2020-08-25 -> Function criada (gustavocnp95 - PL-2903).
-- 2020-08-27 -> Remove query de busca de quantidade de tentativas (diogenesvanzella - PL-2903).
-- 2020-08-28 -> Torna function especifica para atualizar os parametros recebidos sem considerar erro (PL-3080)
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
create or replace function integracao.func_checklist_os_atualiza_status_os(f_cods_interno_os_prolog bigint[],
                                                                           f_pendente boolean,
                                                                           f_bloqueada boolean,
                                                                           f_incrementar_tentativas boolean,
                                                                           f_error_message text default null)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set pendente_sincronia        = f_pendente,
        quantidade_tentativas     = f_if(f_incrementar_tentativas,
                                         quantidade_tentativas + 1,
                                         quantidade_tentativas),
        bloquear_sicronia         = f_bloqueada,
        data_ultima_tentativa     = f_if(f_incrementar_tentativas, now(), data_ultima_tentativa),
        mensagem_ultima_tentativa = f_if(f_incrementar_tentativas, f_error_message, mensagem_ultima_tentativa)
    where codigo_os_prolog = any (f_cods_interno_os_prolog);
end;
$$;