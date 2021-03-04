-- Sobre:
--
-- Relatório que cálculo a aderência do colaborador nas marcações de intervalo dentro do período filtrado. É considerado
-- para o cálculo da aderência a tabela MAPA. Os colaboradores que tiverem mapa em um dia, é contabilizado como tendo a
-- necessidade de realizar as marcações que seu cargo pede.
--
-- Histórico:
-- 2020-04-24 -> Function documentada e adicionada ao GitHub (luiz_fp - PL-2720).
-- 2020-04-27 -> Nome da function alterado de 'func_relatorio_aderencia_intervalo_colaborador' para
--               'func_marcacao_relatorio_aderencia_marcacoes_colaboradores_mapa'.
--               CPF tmbém foi alterado para ser BIGINT ao invés de TEXT. (luiz_fp - PL-2720).
-- 2020-04-28 -> O relatório foi extensivamente alterado visando otimização, dentre as alterações:
--               1. Paramos de utilizar a 'view_extrato_mapas_versus_intervalos' que era muito lenta. Agora é
--                  utilizado a nova function: func_marcacao_intervalos_versus_mapas.
--               2. As junções dos dados de motoristas e ajudantes no mesmo CPF é feita diretamente nesse relatório
--                  e através de UNION ALL, não mais UNION, como era feito na antiga 'view_intervalo_mapa_colaborador'.
--               3. Valores de filtragens são aplicados o quanto antes no processo de busca dos dados, por isso mesmo
--                  repassamos os parâmetros de unidade e data inicial/final para a
--                  'func_marcacao_intervalos_versus_mapas'.
--               4. O uso de CTEs nos permite buscar informações de dados já filtrados (vide item 3). Isso pode ser
--                  visto na CTE 'mapas_intervalos_por_funcao', que realiza três consultas na CTE
--                  'mapas_intervalos_todos'.
--               5. As condições de filtragem (os WHEREs) foram alterados de modo que contemplem a aplicação de um
--                  index e evitem um scan sequencial.
--               6. Alguns counts e sums foram feitos em CTEs inicias para poderem ser reaproveitados mais adiante no
--                  fluxo, evitando a necessidade de derivá-los novamente
--                  (luiz_fp - PL-2720).
create or replace function func_marcacao_relatorio_aderencia_marcacoes_colaboradores_mapa(f_cod_unidade bigint,
                                                                                          f_cpf bigint,
                                                                                          f_data_inicial date,
                                                                                          f_data_final date)
    returns TABLE
            (
                "NOME"                  text,
                "FUNÇÃO"                text,
                "EQUIPE"                text,
                "INTERVALOS PREVISTOS"  bigint,
                "INTERVALOS REALIZADOS" bigint,
                "ADERÊNCIA"             text
            )
    language sql
as
$$
with mapas_intervalos_todos as (
    select v.mapa,
           v.cpf_motorista,
           v.tempo_decorrido_minutos_mot,
           v.cpf_aj1,
           v.tempo_decorrido_minutos_aj1,
           v.cpf_aj2,
           v.tempo_decorrido_minutos_aj2
    from func_marcacao_intervalos_versus_mapas(f_cod_unidade, f_data_inicial, f_data_final) v
    where case when f_cpf is null then true else f_cpf in (v.cpf_motorista, v.cpf_aj1, v.cpf_aj2) end
),
     -- Aqui usamos UNION ALL para evitar o processamento de remover linhas duplicadas. Garantimos que elas
     -- (as linhas duplicadas) não irão existir com as condições de WHERE.
     mapas_intervalos_por_funcao as (
         select mit.cpf_motorista                                                       as cpf,
                count(mit.mapa)                                                         as intervalos_previstos,
                sum(case when mit.tempo_decorrido_minutos_mot <> '-' then 1 else 0 end) as intevalos_realizados,
                case
                    when count(mit.mapa) > 0 then
                        trunc((sum(case when mit.tempo_decorrido_minutos_mot <> '-' then 1 else 0 end)::float /
                               count(mit.mapa)) * 100)
                    else 0 end                                                          as aderencia_intervalo
         from mapas_intervalos_todos mit
         where case when f_cpf is null then true else f_cpf = mit.cpf_motorista end
         group by cpf
         union all
         select mit.cpf_aj1                                                             as cpf,
                count(mit.mapa)                                                         as intervalos_previstos,
                sum(case when mit.tempo_decorrido_minutos_aj1 <> '-' then 1 else 0 end) as intevalos_realizados,
                case
                    when count(mit.mapa) > 0 then
                        trunc((sum(case when mit.tempo_decorrido_minutos_aj1 <> '-' then 1 else 0 end)::float /
                               count(mit.mapa)) * 100)
                    else 0 end                                                          as aderencia_intervalo
         from mapas_intervalos_todos mit
         where case when f_cpf is null then true else f_cpf = mit.cpf_aj1 end
         group by cpf
         union all
         select mit.cpf_aj2                                                             as cpf,
                count(mit.mapa)                                                         as intervalos_previstos,
                sum(case when mit.tempo_decorrido_minutos_aj2 <> '-' then 1 else 0 end) as intevalos_realizados,
                case
                    when count(mit.mapa) > 0 then
                        trunc((sum(case when mit.tempo_decorrido_minutos_aj2 <> '-' then 1 else 0 end)::float /
                               count(mit.mapa)) * 100)
                    else 0 end                                                          as aderencia_intervalo
         from mapas_intervalos_todos mit
         where case when f_cpf is null then true else f_cpf = mit.cpf_aj2 end
         group by cpf
     ),

     -- Precisamos reagrupar aqui para contar no mesmo CPF colaboradores que saíram uma vez como motorista,
     -- outra como ajudante 1 e também como ajudante 2.
     mapas_intervalos_por_colaborador as (
         select mipf.cpf                       as cpf,
                sum(mipf.intervalos_previstos) as intervalos_previstos,
                sum(mipf.intevalos_realizados) as intevalos_realizados,
                case
                    when sum(mipf.intervalos_previstos) > 0 then
                        trunc((sum(intevalos_realizados)::float /
                               sum(mipf.intervalos_previstos)) * 100)
                    else 0 end                 as aderencia_intervalo
         from mapas_intervalos_por_funcao mipf
         group by cpf
     )

select c.nome :: text                                          as nome_colaborador,
       f.nome :: text                                          as nome_cargo,
       e.nome :: text                                          as nome_equipe,
       coalesce(dados.intervalos_previstos, 0) :: bigint       as intervalos_previstos,
       coalesce(dados.intevalos_realizados, 0) :: bigint       as intevalos_realizados,
       (coalesce(dados.aderencia_intervalo, 0) || '%') :: text as aderencia_intervalo
from colaborador c
         join unidade u on u.codigo = c.cod_unidade
         join funcao f on f.codigo = c.cod_funcao
         join equipe e on e.codigo = c.cod_equipe
         left join mapas_intervalos_por_colaborador as dados on dados.cpf = c.cpf
where
  -- Necessário pois queremos apenas colaboradores da unidade filtrada.
    c.cod_unidade = f_cod_unidade
  -- Assim trazemos apenas cargos que tenham intervalos parametrizados.
  and c.cod_funcao in (select cod_cargo from intervalo_tipo_cargo where cod_unidade = f_cod_unidade)
order by dados.aderencia_intervalo desc nulls last, c.nome;
$$;