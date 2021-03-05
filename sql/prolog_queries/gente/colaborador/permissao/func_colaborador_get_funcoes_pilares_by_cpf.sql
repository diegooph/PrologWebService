create or replace function func_colaborador_get_funcoes_pilares_by_cpf(f_cpf_colaborador bigint)
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
from cargo_funcao_prolog_v11 cfv
         join pilar_prolog pp on pp.codigo = cfv.cod_pilar_prolog
         join funcao_prolog_v11 fpv on fpv.cod_pilar = pp.codigo and fpv.codigo = cfv.cod_funcao_prolog
         join colaborador c on c.cod_unidade = cfv.cod_unidade and cfv.cod_funcao_colaborador = c.cod_funcao
         join unidade_pilar_prolog upp on upp.cod_unidade = c.cod_unidade and upp.cod_pilar = cfv.cod_pilar_prolog
where c.cpf = f_cpf_colaborador
order by pilar, funcao;
$$;