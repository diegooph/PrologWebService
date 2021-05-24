with dados_functions as (
    select huf.data_hora_execucao,
           lower(trim(substring(huf.function_query,
                                strpos(huf.function_query::citext, 'suporte.'::citext),
                                strpos(huf.function_query, '(') -
                                strpos(huf.function_query::citext, 'suporte.'::citext)))) as function_name
    from suporte.historico_uso_function huf
)

select count(df.function_name) as total_usos,
       df.function_name        as function_name
from dados_functions df
where df.data_hora_execucao::date >= '2021-04-15'
group by function_name
order by total_usos desc;