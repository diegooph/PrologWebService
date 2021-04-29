create or replace function func_empresa_get_funcoes_pilares_by_unidade(f_cod_unidade bigint)
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
from pilar_prolog pp
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo
         join unidade_pilar_prolog upp on upp.cod_pilar = pp.codigo
where upp.cod_unidade = f_cod_unidade
  and fpv.codigo not in (select fpb.cod_funcao_prolog
                         from funcao_prolog_bloqueada fpb
                         where fpb.cod_unidade = f_cod_unidade)
order by pilar, funcao;
$$;