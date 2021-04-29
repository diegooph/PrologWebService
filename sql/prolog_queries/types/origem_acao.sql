-- Sobre:
-- Esta view busca os tipos ativos do tipo de origem ação, usado nos logs. Ela já considera a config LC_MESSAGES
-- do postgres para retornar o traduzido (ou não) no origem_acao_legivel.
--
-- Histórico:
-- 2020-09-15 -> View criada (gustavocnp95 - PL-3098).
create view types.origem_acao
    (origem_acao, origem_acao_legivel) as
select types.origem_acao_type.origem_acao,
       f_if((select current_setting('lc_messages'::text) = 'es_es.UTF-8'::text),
            types.origem_acao_type.origem_acao_legivel_es,
            types.origem_acao_type.origem_acao_legivel_pt_br) as origem_acao_legivel
from types.origem_acao_type
where types.origem_acao_type.ativo = true;