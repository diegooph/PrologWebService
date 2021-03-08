create or replace function integracao.func_checklist_modelo_get_modelos_bloqueados(f_cod_unidade bigint)
    returns table
            (
                cod_unidade          bigint,
                cod_modelo_checklist bigint
            )
    language plpgsql
as
$$
begin
    return query
        select a.cod_unidade,
               a.cod_modelo_checklist
        from integracao.checklist_modelo_bloqueado a
        where a.cod_unidade = f_cod_unidade;
end;
$$;