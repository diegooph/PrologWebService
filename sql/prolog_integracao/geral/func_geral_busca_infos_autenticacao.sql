-- Sobre:
--
-- Function utilizada para buscar as informações utilizadas para autenticar métodos na integração.
--
-- Histórico:
-- 2019-11-27 -> Function criada (diogenesvanzella - PLI-41).
-- 2020-11-08 -> Adiciona token Prolog no retorno (didivz - PL-3251).
create or replace function integracao.func_geral_busca_infos_autenticacao(f_cod_empresa bigint,
                                                                          f_sistema_key text,
                                                                          f_metodo_integrado text)
    returns table
            (
                prolog_token_integracao text,
                url_completa            text,
                api_token_client        text,
                api_short_code          bigint
            )
    language sql
as
$$
select ti.token_integracao  as prolog_token_integracao,
       eim.url_completa     as url_completa,
       eim.api_token_client as api_token_client,
       eim.api_short_code   as api_short_code
from integracao.empresa_integracao_metodos eim
         join integracao.empresa_integracao_sistema eis on eim.cod_integracao_sistema = eis.codigo
         join integracao.token_integracao ti on ti.cod_empresa = eis.cod_empresa
where eis.cod_empresa = f_cod_empresa
  and eis.chave_sistema = f_sistema_key
  and eim.metodo_integrado = f_metodo_integrado;
$$;