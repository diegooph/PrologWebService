create or replace function integracao.func_geral_unidade_get_infos_de_para(f_cod_unidades bigint[])
    returns table
            (
                cod_empresa_prolog   bigint,
                nome_empresa_prolog  text,
                cod_unidade_prolog   bigint,
                nome_unidade_prolog  text,
                cod_regional_prolog  bigint,
                nome_regional_prolog text,
                cod_auxiliar_unidade text
            )
    language plpgsql
as
$$
begin
    return query
        select u.cod_empresa  as cod_empresa_prolog,
               e.nome::text   as nome_empresa_prolog,
               u.codigo       as cod_unidade_prolog,
               u.nome::text   as nome_unidade_prolog,
               r.codigo       as cod_regional_prolog,
               r.regiao::text as nome_regional_prolog,
               u.cod_auxiliar as cod_auxiliar_unidade
        from unidade u
                 join regional r on u.cod_regional = r.codigo
                 join empresa e on u.cod_empresa = e.codigo
        where u.codigo = any (f_cod_unidades);
end
$$;