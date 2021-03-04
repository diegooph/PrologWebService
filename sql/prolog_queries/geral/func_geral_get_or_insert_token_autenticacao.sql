-- Sobre:
--
-- Function utilizada para inserir a token de autenticação de um usuário através do seu código. Caso o usuário já
-- possua um token válido, nenhum novo será inserido, iserimos um novo apenas para o caso de o usuário não possuir
-- nenhum.
--
-- Histórico:
-- 2020-04-08 -> Function criada (diogenesvanzella - PLI-118).
create or replace function func_geral_get_or_insert_token_autenticacao(f_cod_colaborador bigint,
                                                                       f_token_autenticacao text)
    returns table
            (
                cpf_colaborador    bigint,
                cod_colaborador    bigint,
                token_autenticacao text
            )
    language plpgsql
as
$$
begin
    if (select not exists(select ta.token from token_autenticacao ta where ta.cod_colaborador = f_cod_colaborador))
    then
        insert into token_autenticacao (cpf_colaborador,
                                        token,
                                        cod_colaborador)
        values ((select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
                f_token_autenticacao,
                f_cod_colaborador);
    end if;

    return query
        select ta.cpf_colaborador as cpf_colaborador,
               ta.cod_colaborador as cod_colaborador,
               ta.token::text     as token_autenticacao
        from token_autenticacao ta
        where ta.cod_colaborador = f_cod_colaborador
        limit 1;
end;
$$;