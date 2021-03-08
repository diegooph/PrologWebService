-- migramos vamos adicionar cod_empresa e token onde ainda não tem. Casos de logs que temos:
-- 1. Sem token / Sem cod_empresa. (93225)
-- 2. Com token / Sem cod_empresa - Token existe. (1146) 2020-01-31 13:08:26 | 2020-03-05 19:24:16
-- 3. Com token / Sem cod_empresa - Token mapeado errado. (16717) (15494) 2020-11-04 14:27:44 | 2020-11-29 06:00:01
-- 4. Com token / Sem cod_empresa - Token que não existe. (16717) (77)

-- Primeiro, retirar os [] dos tokens
update log.log_request_response lrr
set token_integracao = replace(replace(lrr.token_integracao, '[', ''), ']', '')
where lrr.token_integracao ilike '%[%'
   or lrr.token_integracao ilike '%]%';

-- Depois vamos inserir o cod_empresa nos casos que foram corrigidos. isso vai resolver os cenários 2 e 3.
update log.log_request_response lrr
set cod_empresa = (select ti.cod_empresa
                   from integracao.token_integracao ti
                   where ti.token_integracao = lrr.token_integracao
                     -- Piccolotur e Andrade usam o mesmo token, removemos a Andrade para não quebrar.
                     and ti.cod_empresa != 28)
where cod_empresa is null
  and lrr.token_integracao in (select ti.token_integracao
                               from integracao.token_integracao ti
                               where ti.token_integracao = lrr.token_integracao);

-- Corrigimos os cenários FROM_PROLOG da Avilan e Nepomuceno. Isso deve resolver o cenário 1.
update log.log_request_response
set cod_empresa      = 2,
    token_integracao = '4dh4didip8qdfkcso0n0dkev2kb07f5abasc67uuk1pi2eqn380'
where cod_empresa is null
  and token_integracao is null
  and log_type = 'FROM_PROLOG'
  and request_json ->> 'url' ilike '%avaconcloud%';

update log.log_request_response
set cod_empresa      = 15,
    token_integracao = 'e7dpv6ma0kb47le9k7p2jvoi2m8n0svbrtp9ud5hq76didi21ur'
where cod_empresa is null
  and token_integracao is null
  and log_type = 'FROM_PROLOG'
  and request_json ->> 'url' ilike '%expressonepomuceno%';
-- Fim da correção dos dados

drop function integracao.func_geral_busca_infos_autenticacao(f_cod_empresa bigint,
    f_sistema_key text,
    f_metodo_integrado text);
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