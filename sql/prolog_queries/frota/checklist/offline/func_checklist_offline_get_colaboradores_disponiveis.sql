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
declare
    v_cod_empresa_unidade_filtro                   constant bigint  := (select cod_empresa
                                                                        from unidade
                                                                        where codigo = f_cod_unidade);
    v_is_empresa_bloqueada_checklist_entre_unidade constant boolean := (
            v_cod_empresa_unidade_filtro in (select cod_empresa
                                             from checklist_diferentes_unidades_empresa_bloqueada
                                             where cod_empresa = v_cod_empresa_unidade_filtro));
begin
    return query
        with funcoes_modelos as (
            select cmf.cod_funcao
            from checklist_modelo cm
                     join checklist_modelo_funcao cmf
                          on cm.codigo = cmf.cod_checklist_modelo
            where cm.status_ativo = true
              and case
                      when v_is_empresa_bloqueada_checklist_entre_unidade
                          then cm.cod_unidade = f_cod_unidade
                      else cm.cod_unidade in
                           (select codigo from unidade where cod_empresa = v_cod_empresa_unidade_filtro)
                end
        )

        select distinct c.cod_unidade :: bigint      as cod_unidade_colaborador,
                        c.codigo                     as cod_colaborador,
                        c.nome :: text               as nome_colaborador,
                        lpad(c.cpf :: text, 11, '0') as cpf_colaborador,
                        c.data_nascimento :: date    as data_nascimento,
                        c.cod_funcao                 as cod_cargo_colaborador,
                        c.cod_permissao :: integer   as cod_permissao_colaborador
        from colaborador c
        where c.status_ativo
          and c.cod_funcao in (select cod_funcao from funcoes_modelos)
          and case
                  when v_is_empresa_bloqueada_checklist_entre_unidade
                      then c.cod_unidade = f_cod_unidade
                  else c.cod_empresa = v_cod_empresa_unidade_filtro
            end;
end;
$$;