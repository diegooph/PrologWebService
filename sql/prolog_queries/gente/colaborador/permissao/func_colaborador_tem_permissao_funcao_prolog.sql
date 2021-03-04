-- Sobre:
--
-- Function utilizada para validar se um colaborador específico possui acesso à uma função específica do Prolog.
--
-- Histórico:
-- 2020-07-08 -> Function criada (diogenesvanzella - PL-2671).
create or replace function func_colaborador_tem_permissao_funcao_prolog(f_cpf_colaborador bigint,
                                                                        f_cod_pilar_prolog bigint,
                                                                        f_cod_funcao_prolog bigint)
    returns boolean
    language sql
as
$$
select exists(select c.cpf
              from colaborador c
                       join cargo_funcao_prolog_v11 cfp
                            on c.cod_funcao = cfp.cod_funcao_colaborador and c.cod_unidade = cfp.cod_unidade
              where c.cpf = f_cpf_colaborador
                and cfp.cod_pilar_prolog = f_cod_pilar_prolog
                and cfp.cod_funcao_prolog = f_cod_funcao_prolog);
$$;