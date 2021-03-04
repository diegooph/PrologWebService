-- Sobre:
--
-- Function para verificar se um colaborador representando pelo F_TOKEN que faz uma requisição no WS do ProLog
-- possui as permissões necessárias que o método que ele utiliza está pedindo.
--
-- Também é possível indicar se a verificação deve levar em conta apenas usuários ativos no sistema ou não. Isso é
-- feito através do parâmetro F_APENAS_USUARIOS_ATIVOS.
--
-- Para verifica a lógica das permissões em si, essa function utiliza:
-- -> FUNC_COLABORADOR_VERIFICA_PERMISSOES
--
-- Histórico:
-- 2019-08-29 -> Function criada (luizfp - PL-2267).
-- 2020-03-25 -> Altera function para retornar CPF e código do colaborador (luizfp - PL-2638).
-- 2020-06-25 -> Removemos as funções bloqueadas do colaborador (diogenesvanzella - PL-2671).
-- 2020-07-08 -> Removemos verificação das funções bloqueadas (diogenesvanzella - PL-2671).
create or replace function func_colaborador_verifica_permissoes_token(f_token text,
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
    from token_autenticacao ta
             join colaborador c on c.cpf = ta.cpf_colaborador
        -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
        -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
        -- contendo null.
             left join cargo_funcao_prolog_v11 cfp
                       on cfp.cod_unidade = c.cod_unidade
                           and cfp.cod_funcao_colaborador = c.cod_funcao
    where ta.token = f_token
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