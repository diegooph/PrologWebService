create or replace function func_empresa_get_funcoes_pilares_by_cargo(f_cod_unidade bigint,
                                                                     f_cod_cargo_colaborador bigint)
    returns table
            (
                cod_pilar  bigint,
                pilar      text,
                cod_funcao bigint,
                funcao     text
            )
    language sql
as
$$
select distinct pp.codigo        as cod_pilar,
                pp.pilar::text   as pilar,
                fpv.codigo       as cod_funcao,
                fpv.funcao::text as funcao
from cargo_funcao_prolog_v11 cfp
         join pilar_prolog pp on pp.codigo = cfp.cod_pilar_prolog
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo and fpv.codigo = cfp.cod_funcao_prolog
where cfp.cod_unidade = f_cod_unidade
  and cfp.cod_funcao_colaborador = f_cod_cargo_colaborador
order by pilar, funcao;
$$;