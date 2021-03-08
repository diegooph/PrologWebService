create or replace function func_colaborador_verifica_possui_funcao_prolog(f_cod_colaborador bigint,
                                                                          f_cod_funcao_prolog integer)
    returns boolean
    language sql
as
$$
select exists(
               select c.codigo
               from colaborador c
                        join cargo_funcao_prolog_v11 cargo
                             on c.cod_unidade = cargo.cod_unidade and c.cod_funcao = cargo.cod_funcao_colaborador
               where c.codigo = f_cod_colaborador
                 and cargo.cod_funcao_prolog = f_cod_funcao_prolog) as tem_permissao
$$;