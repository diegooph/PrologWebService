-- Sobre:
-- Esta view busca os processos de evolução de km ativos usado no relatório. Ela já considera a config LC_MESSAGES do
-- postgres para retornar o traduzido (ou não) no processo_legivel.
--
-- Histórico:
-- 2020-10-07 -> View criada (thaisksf - PL-3172).
create view types.processo_evolucao_km
    (processo, processo_legivel) as
select types.processo_evolucao_km_type.processo,
       f_if((select current_setting('lc_messages'::text) = 'es_es.UTF-8'::text),
            types.processo_evolucao_km_type.processo_legivel_es,
            types.processo_evolucao_km_type.processo_legivel_pt_br) as processo_legivel
from types.processo_evolucao_km_type
where types.processo_evolucao_km_type.ativo = true;