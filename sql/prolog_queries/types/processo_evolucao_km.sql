create view types.processo_evolucao_km
    (processo, processo_legivel) as
select types.processo_evolucao_km_type.processo,
       f_if((select current_setting('lc_messages'::text) = 'es_es.UTF-8'::text),
            types.processo_evolucao_km_type.processo_legivel_es,
            types.processo_evolucao_km_type.processo_legivel_pt_br) as processo_legivel
from types.processo_evolucao_km_type
where types.processo_evolucao_km_type.ativo = true;