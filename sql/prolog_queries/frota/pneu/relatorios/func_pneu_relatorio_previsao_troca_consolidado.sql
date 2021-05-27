-- Sobre:
--
-- A function traz a previsão de quantos pneus você deve comprar e em qual data, agrupados por marca, modelo e medida.
create function func_pneu_relatorio_previsao_troca_consolidado(f_cod_unidades bigint[],
                                                               f_status_pneu text,
                                                               f_data_inicial date,
                                                               f_data_final date)
    returns table
            (
                unidade    text,
                data       text,
                marca      text,
                modelo     text,
                medidas    text,
                quantidade bigint
            )
    language sql
as
$$
select vap.nome_unidade_alocado                  as nome_unidade_alocado,
       to_char(vap.previsao_troca, 'DD/MM/YYYY') as data,
       vap.nome_marca                            as nome_marca,
       vap.nome_modelo                           as nome_modelo,
       vap.medidas                               as medidas,
       count(vap.nome_modelo)                    as quantidade
from view_analise_pneus vap
where vap.cod_unidade_alocado = any (f_cod_unidades)
  and vap.previsao_troca between f_data_inicial and f_data_final
  and vap.status_pneu = f_status_pneu
group by vap.nome_unidade_alocado, vap.previsao_troca, vap.nome_marca, vap.nome_modelo, vap.medidas
order by vap.nome_unidade_alocado, vap.previsao_troca, quantidade desc;
$$;