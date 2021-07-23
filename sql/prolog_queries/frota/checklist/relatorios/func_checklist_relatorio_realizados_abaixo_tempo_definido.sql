create or replace function func_checklist_relatorio_realizados_abaixo_tempo_definido(f_cod_unidades bigint[],
                                                                                     f_tempo_realizacao_millis bigint,
                                                                                     f_data_hoje_utc date,
                                                                                     f_dias_retroativos_para_buscar bigint)
    returns table
            (
                unidade                                                    text,
                nome                                                       text,
                "QUANTIDADE CHECKLISTS REALIZADOS ABAIXO TEMPO ESPECIFICO" bigint,
                "QUANTIDADE CHECKLISTS REALIZADOS"                         bigint
            )
    language plpgsql
as
$$
declare
    data_inicial constant date := f_data_hoje_utc + interval '1' day -
                                  (interval '1' day * f_dias_retroativos_para_buscar);
    data_final   constant date := f_data_hoje_utc + interval '1' day;
begin
    return query
        with pre_select as (
            select cl.cod_unidade                                              as cod_unidade,
                   cl.cpf_colaborador                                          as cpf_colaborador,
                   count(cl.cpf_colaborador)                                   as total_realizados,
                   count(cl.cpf_colaborador)
                   filter (where tempo_realizacao < f_tempo_realizacao_millis) as realizados_abaixo_tempo_definido
            from checklist cl
            where cl.cod_unidade = any (f_cod_unidades)
              and cl.data_hora_realizacao_tz_aplicado between data_inicial and data_final
            group by cl.cod_unidade, cl.cpf_colaborador
        )
        select u.nome::text,
               co.nome::text,
               ps.realizados_abaixo_tempo_definido,
               ps.total_realizados
        from pre_select ps
                 join unidade u
                      on ps.cod_unidade = u.codigo
                 join colaborador co
                      on ps.cpf_colaborador = co.cpf
        where ps.realizados_abaixo_tempo_definido > 0
        order by ps.realizados_abaixo_tempo_definido desc, co.nome;
end;
$$;