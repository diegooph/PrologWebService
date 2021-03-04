-- Sobre:
--
-- Essa function busca todas as O.S's a integrar que estejam pendentes e não estejam bloqueadas.
--
-- Histórico:
-- 2020-08-25 -> Function criada (gustavocnp95 - PL-2903).
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
create or replace function integracao.func_checklist_os_busca_os_sincronizar()
    returns table
            (
                codigo_interno_os_prolog bigint
            )
    language PLPGSQL
as
$$
begin
    return query
        select coss.codigo_os_prolog as codigo_os_prolog
        from integracao.checklist_ordem_servico_sincronizacao coss
        where coss.pendente_sincronia = true
          and coss.bloquear_sicronia = false;
end;
$$;