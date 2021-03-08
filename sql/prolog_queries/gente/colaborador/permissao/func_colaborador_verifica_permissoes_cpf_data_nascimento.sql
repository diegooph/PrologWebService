create or replace function
    func_colaborador_verifica_permissoes_cpf_data_nascimento(f_cpf bigint,
                                                             f_data_nascimento date,
                                                             f_permisssoes_necessarias integer[],
                                                             f_precisa_ter_todas_as_permissoes boolean,
                                                             f_apenas_usuarios_ativos boolean)
    returns table
            (
                token_valido      boolean,
                possui_permisssao boolean,
                cpf_colaborador   bigint,
                cod_colaborador   bigint
            )
    language plpgsql
as
$$
declare
    v_permissoes_colaborador integer[];
    v_cpf_colaborador        bigint;
    v_cod_colaborador        bigint;
begin
    select array_agg(cfp.cod_funcao_prolog),
           c.cpf,
           c.codigo
    from colaborador c
             -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
             -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
             -- contendo null.
             left join cargo_funcao_prolog_v11 cfp
                       on cfp.cod_unidade = c.cod_unidade
                           and cfp.cod_funcao_colaborador = c.cod_funcao
    where c.cpf = f_cpf
      and c.data_nascimento = f_data_nascimento
      and f_if(f_apenas_usuarios_ativos, c.status_ativo = true, true)
    group by c.cpf, c.codigo
    into v_permissoes_colaborador, v_cpf_colaborador, v_cod_colaborador;

    return query
        select f.token_valido      as token_valido,
               f.possui_permisssao as possui_permissao,
               v_cpf_colaborador   as cpf_colaborador,
               v_cod_colaborador   as cod_colaborador
        from func_colaborador_verifica_permissoes(
                     v_permissoes_colaborador,
                     f_permisssoes_necessarias,
                     f_precisa_ter_todas_as_permissoes) f;
end;
$$;