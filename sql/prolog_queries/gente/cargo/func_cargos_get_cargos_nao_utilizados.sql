-- Sobre:
--
-- Function utilizada para listar os cargos não utilizados na unidade. Um cargo não utilizado é um cargo que não está
-- associado a nenhum colaborador, não tendo relação com a quantidade de permissões liberadas que possui.
--
-- Histórico:
-- 2020-06-25 -> Removemos as funções bloqueadas da contagem de permissões (diogenesvanzella - PL-2671).
-- 2020-07-08 -> Remove verificação das funções bloqueadas (diogenesvanzella - PL-2671).
create or replace function func_cargos_get_cargos_nao_utilizados(f_cod_unidade bigint)
    returns table
            (
                cod_cargo                 bigint,
                nome_cargo                text,
                qtd_permissoes_vinculadas bigint
            )
    language plpgsql
as
$$
declare
    pilares_liberados_unidade bigint[] := (select array_agg(upp.cod_pilar)
                                           from unidade_pilar_prolog upp
                                           where upp.cod_unidade = f_cod_unidade);
begin
    return query
        with cargos_em_uso as (
            select distinct cod_funcao
            from colaborador c
            where c.cod_unidade = f_cod_unidade
        )

        select f.codigo                                                        as cod_cargo,
               f.nome :: text                                                  as nome_cargo,
               count(*)
               filter (where cfp.cod_unidade is not null
                   -- Consideramos apenas as permissões de pilares liberados para a unidade.
                   and cfp.cod_pilar_prolog = any (pilares_liberados_unidade)) as qtd_permissoes_vinculadas
        from funcao f
                 left join cargo_funcao_prolog_v11 cfp
                           on f.codigo = cfp.cod_funcao_colaborador
                               and cfp.cod_unidade = f_cod_unidade
             -- Para buscar os cargos não utilizados, adotamos a lógica de buscar todos os da empresa e depois
             -- remover os que tem colaboradores vinculados, isso é feito nas duas condições abaixo do WHERE.
        where f.cod_empresa = (select u.cod_empresa
                               from unidade u
                               where u.codigo = f_cod_unidade)
          and f.codigo not in (select *
                               from cargos_em_uso)
        group by f.codigo, f.nome
        order by f.nome;
end;
$$;