-- Sobre:
--
-- Function utilizada para buscar a chave do sistema através do token do usuário. O token do usuário é utilizado para
-- buscar o código da empresa e com esse código conseguimos descobrir qual o sistema que a empresa utiliza e, assim,
-- retornar a chave correta.
--
-- Histórico:
-- 2019-10-29 -> Function criada (diogenesvanzella - PLI-41).
-- 2020-04-23 -> Adiciona mais duas formas de retorno que controlam se a integração está ativa (natanrotta - PL-72).
create or replace function integracao.func_geral_busca_sistema_key(f_user_token text, f_recurso_integrado text)
    returns table
            (
                chave_sistema           text,
                existe_token            boolean,
                token_ativo             boolean,
                recurso_integrado_ativo boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa bigint := (select c.cod_empresa
                             from token_autenticacao ta
                                      left join colaborador c on c.codigo = ta.cod_colaborador
                             where ta.token = f_user_token);
begin
    return query
        select (select eis.chave_sistema
                from integracao.empresa_integracao_sistema eis
                where eis.cod_empresa = v_cod_empresa
                  and eis.recurso_integrado = f_recurso_integrado)                 as chave_sistema,
               (select exists(select ta.token
                              from token_autenticacao ta
                              where ta.token = f_user_token))                      as token_existe,
               coalesce((select ti.ativo
                         from integracao.token_integracao ti
                         where ti.cod_empresa = v_cod_empresa), true)              as token_ativo,
               coalesce((select eis.ativo
                         from integracao.empresa_integracao_sistema eis
                         where eis.cod_empresa = v_cod_empresa
                           and eis.recurso_integrado = f_recurso_integrado), true) as recurso_integrado_ativo;
end;
$$;