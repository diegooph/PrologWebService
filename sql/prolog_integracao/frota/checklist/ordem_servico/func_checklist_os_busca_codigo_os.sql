-- Sobre:
--
-- Essa function busca um código interno de O.S (código prolog) dado os códigos de itens de O.S. Por se tratar de
-- integrações, a function apenas retorna os códigos das Ordens de Serviços que estão na tabela de pentendes, ou seja,
-- OSs que são de integração.
--
-- Histórico:
-- 2020-08-28 -> Function criada (gustavocnp95 - PL-2903).
-- 2020-08-31 -> Retorna apenas OSs que devem ser integradas (diogenesvanzella - PL-3114).
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
create or replace function integracao.func_checklist_os_busca_codigo_os(f_cod_itens_os bigint[])
    returns table
            (
                cod_interno_os_prolog bigint
            )
    language plpgsql
as
$$
begin
    return query
        select distinct cos.codigo_prolog
        from checklist_ordem_servico_itens cosi
                 join checklist_ordem_servico cos
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join integracao.checklist_ordem_servico_sincronizacao coss
                      on cos.codigo_prolog = coss.codigo_os_prolog
        where cosi.codigo = any (f_cod_itens_os);
end;
$$;