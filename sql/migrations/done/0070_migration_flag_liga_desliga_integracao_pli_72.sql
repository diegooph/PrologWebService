-- Adiciona nova coluna e implementa true para já existentes.
alter table integracao.token_integracao
    add column ativo boolean not null default true;

-- Adiciona nova coluna e implementa true para já existentes.
alter table integracao.empresa_integracao_sistema
    add column ativo boolean not null default true;

-- Altera function para validar se TOKEN está ativo ou não.
drop function integracao.func_geral_busca_sistema_key(f_user_token text, f_recurso_integrado text);
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