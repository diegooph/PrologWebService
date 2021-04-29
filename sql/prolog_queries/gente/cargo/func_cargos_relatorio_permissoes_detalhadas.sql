create or replace function func_cargos_relatorio_permissoes_detalhadas(f_cod_unidades bigint[])
    returns table
            (
                UNIDADE                 text,
                CARGO                   text,
                PILAR                   text,
                "FUNCIONALIDADE PROLOG" text,
                "PERMISSÃO PROLOG"      text,
                "IMPACTO PERMISSÃO"     text,
                "DESCRIÇÃO PERMISSÃO"   text
            )
    language plpgsql
as
$$
begin
    return query
        select u.nome::text       as nome_unidade,
               f.nome::text       as nome_cargo,
               pp.pilar::text     as nome_pilar,
               fpa.nome::text     as nome_funcionalidade,
               fp.funcao::text    as nome_permissao,
               fp.impacto::text   as impacto_permissao,
               fp.descricao::text as descricao_permissao
        from unidade u
                 join cargo_funcao_prolog_v11 cfp on u.codigo = cfp.cod_unidade
                 join funcao_prolog_v11 fp on fp.codigo = cfp.cod_funcao_prolog
                 join pilar_prolog pp on cfp.cod_pilar_prolog = pp.codigo
                 join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
                 join funcao f on f.codigo = cfp.cod_funcao_colaborador
        where u.codigo = any (f_cod_unidades)
        order by nome_unidade, nome_cargo, fp.impacto desc;
end;
$$;