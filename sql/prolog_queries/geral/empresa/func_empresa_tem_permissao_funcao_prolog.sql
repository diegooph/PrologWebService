create or replace function func_empresa_tem_permissao_funcao_prolog(f_cod_unidade bigint,
                                                                    f_cod_funcao_colaborador bigint,
                                                                    f_cod_funcao_prolog bigint)
    returns boolean
    language sql
as
$$
select exists(select *
              from cargo_funcao_prolog_v11 cfp
              where cfp.cod_unidade = f_cod_unidade
                and cfp.cod_funcao_colaborador = f_cod_funcao_colaborador
                and cfp.cod_funcao_prolog = f_cod_funcao_prolog) as tem_permissao;
$$;