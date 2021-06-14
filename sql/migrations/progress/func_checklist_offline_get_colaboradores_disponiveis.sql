create or replace function func_checklist_offline_get_colaboradores_disponiveis(f_cod_unidade bigint)
    returns table
            (
                cod_unidade_colaborador   bigint,
                cod_colaborador           bigint,
                nome_colaborador          text,
                cpf_colaborador           text,
                data_nascimento           date,
                cod_cargo_colaborador     integer,
                cod_permissao_colaborador integer
            )
    language plpgsql
as
$$
begin
    return query
        with funcoes_modelos as (
            select cmf.cod_funcao
            from checklist_modelo cm
                     join checklist_modelo_funcao cmf
                          on cm.codigo = cmf.cod_checklist_modelo
            where cm.cod_unidade = f_cod_unidade
              and cm.status_ativo = true
        )

        select c.cod_unidade :: bigint      as cod_unidade_colaborador,
               c.codigo                     as cod_colaborador,
               c.nome :: text               as nome_colaborador,
               lpad(c.cpf :: text, 11, '0') as cpf_colaborador,
               c.data_nascimento :: date    as data_nascimento,
               c.cod_funcao                 as cod_cargo_colaborador,
               c.cod_permissao :: integer   as cod_permissao_colaborador
        from colaborador c
                 inner join unidade unidade_filtro on unidade_filtro.codigo = f_cod_unidade
        where (c.cod_unidade = unidade_filtro.codigo
            and c.status_ativo
            and c.cod_funcao in (select cod_funcao from funcoes_modelos))
           or (c.cod_empresa not in (select cod_empresa from checklist_diferentes_unidades_empresa_bloqueada)
            and c.cod_empresa = unidade_filtro.cod_empresa
            and c.cod_funcao in (select cod_funcao from funcoes_modelos));
end;
$$;