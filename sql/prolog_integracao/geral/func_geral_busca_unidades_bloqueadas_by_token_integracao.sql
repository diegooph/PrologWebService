-- Sobre:
--
-- Function utilizada para buscar as unidades que estão bloqueadas para a empresa através do token de integração. Para
-- uma unidade ser considerada bloqueada, ela deve estar associada com a chave do sistema e o recurso integrado
-- recebido por parâmetro.
--
-- Histórico:
-- 2020-07-22 -> Function criada (diogenesvanzella - PLI-174).
-- 2020-08-05 -> Adapta function para token duplicado (diogenesvanzella - PLI-175).
create or replace function integracao.func_geral_busca_unidades_bloqueadas_by_token_integracao(f_token_integracao text,
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
where euib.cod_empresa in (select cod_empresa
                           from integracao.token_integracao ti
                           where ti.token_integracao = f_token_integracao)
  and euib.chave_sistema = f_sistema_key
  and euib.recuro_integrado = f_recurso_integrado
$$;