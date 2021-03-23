create or replace function func_cargos_get_cargos_em_uso(f_cod_unidade bigint)
    returns table
            (
                cod_cargo                    bigint,
                nome_cargo                   text,
                qtd_colaboradores_vinculados bigint,
                qtd_permissoes_vinculadas    bigint
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
               f.nome::text                                                    as nome_cargo,
               (select count(*)
                from colaborador c
                where c.cod_funcao = f.codigo
                  and c.cod_unidade = f_cod_unidade)                           as qtd_colaboradores_vinculados,
               -- Se não tivesse esse FILTER, cargos que não possuem nenhuma permissão vinculada retornariam 1.
               count(*)
               filter (where cfp.cod_unidade is not null
                   -- Consideramos apenas as permissões de pilares liberados para a unidade.
                   and cfp.cod_pilar_prolog = any (pilares_liberados_unidade)) as qtd_permissoes_vinculadas
        from funcao f
                 left join cargo_funcao_prolog_v11 cfp
                           on f.codigo = cfp.cod_funcao_colaborador
                               and cfp.cod_unidade = f_cod_unidade
             -- Não podemos simplesmente filtrar pelo código da unidade presente na tabela CARGO_FUNCAO_PROLOG_V11,
             -- pois desse modo iríamos remover do retorno cargos usados mas sem permissões vinculadas. Por isso
             -- utilizamos esse modo de filtragem com a CTE criada acima.
        where f.codigo in (select *
                           from cargos_em_uso)
        group by f.codigo, f.nome
        order by f.nome;
end ;
$$;