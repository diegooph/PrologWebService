-- Sobre:
--
-- Essa function busca os cargos de uma unidade e a quantidade de permissões vinculadas à ele.
--
-- Histórico:
-- 2020-02-20 -> Adiciona qtd de permissões vinculadas ao cargo (thaisksf - PL-2532).
-- 2020-06-25 -> Removemos as funções bloqueadas da contagem de permissões (diogenesvanzella - PL-2671).
-- 2020-07-08 -> Removemos verificação das funções bloqueadas (diogenesvanzella - PL-2671).
create or replace function func_cargos_get_todos_cargos_unidade(f_cod_unidade bigint)
    returns table
            (
                cod_cargo      bigint,
                nome_cargo     text,
                qtd_permissoes bigint
            )
    language plpgsql
as
$$
begin
    return query
        with qtd_permissoes as (
            select distinct count(cfpv11.cod_funcao_colaborador) as qtd_permissoes_cargo,
                            cfpv11.cod_funcao_colaborador        as cod_funcao_colaborador,
                            cfpv11.cod_unidade                   as cod_unidade
            from cargo_funcao_prolog_v11 cfpv11
            where cfpv11.cod_unidade = f_cod_unidade
            group by cfpv11.cod_funcao_colaborador, cfpv11.cod_unidade
        )
        select f.codigo                             as cod_cargo,
               f.nome :: text                       as nome_cargo,
               coalesce(qp.qtd_permissoes_cargo, 0) as qtd_permissoes
        from funcao f
                 join unidade u on u.cod_empresa = f.cod_empresa
                 left join qtd_permissoes qp on qp.cod_unidade = u.codigo and qp.cod_funcao_colaborador = f.codigo
        where u.codigo = f_cod_unidade
        group by f.codigo, f.nome, qp.qtd_permissoes_cargo
        order by f.nome, f.codigo, qp.qtd_permissoes_cargo;
end;
$$;