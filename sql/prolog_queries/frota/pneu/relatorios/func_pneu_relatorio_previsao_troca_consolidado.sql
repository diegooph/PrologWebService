-- Sobre:
--
-- A function traz a previsão de quantos pneus você deve comprar e em qual data, agrupados por marca, modelo e medida.
create function func_pneu_relatorio_previsao_troca_consolidado(f_cod_unidades bigint[],
                                                               f_status_pneu text,
                                                               f_data_inicial date,
                                                               f_data_final date)
    returns table
            (
                "UNIDADE"    text,
                data         text,
                marca        text,
                modelo       text,
                medidas      text,
                "QUANTIDADE" bigint
            )
    language sql
as
$$
select vap."UNIDADE ALOCADO",
       to_char(vap."PREVISÃO DE TROCA", 'DD/MM/YYYY') as data,
       vap."MARCA",
       vap."MODELO",
       vap."MEDIDAS",
       count(vap."MODELO")                            as quantidade
from view_analise_pneus vap
where vap.cod_unidade = any (f_cod_unidades)
  and vap."PREVISÃO DE TROCA" between f_data_inicial and f_data_final
  and vap."STATUS PNEU" = f_status_pneu
group by vap."UNIDADE ALOCADO", vap."PREVISÃO DE TROCA", vap."MARCA", vap."MODELO", vap."MEDIDAS"
order by vap."UNIDADE ALOCADO", vap."PREVISÃO DE TROCA", quantidade desc;
$$;