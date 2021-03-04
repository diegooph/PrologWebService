-- Sobre:
--
-- Function utilizada para buscar as unidades que estão bloqueadas para a empresa do usuário. Identificamos a empresa
-- do usuário através do token. Para uma unidade ser considerada bloqueada, ela deve estar associada com a chave do
-- sistema e o recurso integrado recebido por parâmetro.
--
-- Histórico:
-- 2020-07-22 -> Function criada (diogenesvanzella - PLI-174).
create or replace function integracao.func_geral_busca_unidades_bloqueadas_integracao(f_user_token text,
                                                                                      f_sistema_key text,
                                                                                      f_recurso_integrado text)
    returns table
            (
                cod_unidade_bloqueada bigint
            )
    language sql
as
$$
select euib.cod_unidade_bloqueada
from integracao.empresa_unidades_integracao_bloqueada euib
where euib.cod_empresa = (select cod_empresa
                          from token_autenticacao ta
                                   join colaborador c on c.codigo = ta.cod_colaborador
                          where ta.token = f_user_token)
  and euib.chave_sistema = f_sistema_key
  and euib.recuro_integrado = f_recurso_integrado
$$;