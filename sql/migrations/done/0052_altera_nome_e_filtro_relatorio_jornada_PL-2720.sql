-- Remove function antiga para reorganizar atributos e alterar nome.
drop function if exists func_relatorio_aderencia_intervalo_colaborador(f_cod_unidade bigint,
    f_data_inicial date,
    f_data_final date,
    f_cpf text);

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
select c.nome,
       f.nome,
       e.nome,
       count(dados.mapa)                                                     AS intervalos_previstos,
       sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end) as intevalos_realizados,
       case
           when count(dados.mapa) > 0 then
               trunc((sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end)::float /
                      count(dados.mapa)) * 100)
           else 0 end || '%'                                                 as aderencia_intervalo
from colaborador c
         join unidade u on u.codigo = c.cod_unidade
         join funcao f on f.codigo = c.cod_funcao
         join equipe e on e.codigo = c.cod_equipe
         left join view_intervalo_mapa_colaborador as dados on dados.cpf = c.cpf
where c.cod_unidade = f_cod_unidade
  and case when f_cpf is null then true else f_cpf = c.cpf end
  and dados.data between f_data_inicial and f_data_final
  and c.cod_funcao in (select COD_CARGO from intervalo_tipo_cargo where COD_UNIDADE = f_cod_unidade)
group by c.cpf, c.nome, e.nome, f.nome
order by case
             when count(dados.mapa) > 0 then
                 trunc((sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end)::float /
                        count(dados.mapa)) * 100)
             else 0 end desc
$$;