-- Sobre:
--
-- Essa function insere uma ordem de serviço dentro da tabela de ordem de serviço pendente de envio.
--
-- Histórico:
-- 2020-08-03 -> Function criada (gustavocnp95 - PLI-180).
-- 2020-08-27 -> Altera para minúsculo (diogenesvanzella - PLI-180).
-- 2020-09-01 -> Altera nome da function (diogenesvanzella - PL-3114).
create or replace function integracao.func_checklist_os_insert_os_pendente(f_cod_unidade bigint,
                                                                           f_cod_os bigint)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_interno_os_prolog bigint;
begin
    select into strict v_cod_interno_os_prolog codigo_prolog
    from checklist_ordem_servico
    where cod_unidade = f_cod_unidade
      and codigo = f_cod_os;

    insert into integracao.checklist_ordem_servico_sincronizacao(codigo_os_prolog)
    values (v_cod_interno_os_prolog);

    return v_cod_interno_os_prolog;
end;
$$;