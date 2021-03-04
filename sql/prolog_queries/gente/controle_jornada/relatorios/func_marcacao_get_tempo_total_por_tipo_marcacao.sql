-- Sobre:
-- Esta função retorna o tempo de cada marcação que o colaborador realizou, uma flag indica se essa marcação
-- está dentro de uma jornada, também identifica dentre todas as marcações, quais são do tipo jornada.
-- O cálculo dos totais de marcações são realizados em JAVA utilizando os resultados dessa function.
--
-- Histórico:
-- 2020-08-13 -> Function retirado filtro por cod intervalo para retornar sempre todas marcações (gustavocnp95 - PL-2850).
create or replace function func_marcacao_get_tempo_total_por_tipo_marcacao(f_cod_unidade bigint,
                                                                           f_data_inicial date,
                                                                           f_data_final date)
    returns table
            (
                cpf_colaborador                      text,
                nome                                 text,
                cargo                                text,
                cod_tipo_intervalo                   text,
                nome_tipo_intervalo                  text,
                tipo_jornada                         boolean,
                tempo_marcacao_millis                text,
                tempo_marcacao_horas_noturnas_millis text,
                marcacao_dentro_jornada              boolean
            )
    language plpgsql
as
$$
declare
    tz_unidade text := tz_unidade(f_cod_unidade);
begin
    return query
        with tipos_unidade as (
            select c.cpf                              as cpf_colaborador,
                   c.nome                             as nome_colaborador,
                   f.nome                             as nome_cargo,
                   it.codigo                          as cod_tipo_marcacao,
                   it.nome                            as nome_tipo_marcacao,
                   (mtj.cod_tipo_jornada is not null) as tipo_jornada
            from colaborador c
                     join funcao f
                          on c.cod_funcao = f.codigo
                     join intervalo_tipo it
                          on c.cod_unidade = it.cod_unidade
                     left join marcacao_tipo_jornada mtj
                               on it.cod_unidade = mtj.cod_unidade and it.codigo = mtj.cod_tipo_jornada
            where it.cod_unidade = f_cod_unidade
            order by c.cpf, it.codigo
        ),
             total_tempo_marcacao as (
                 select c.cpf                                                 as cpf_colaborador,
                        it.codigo                                             as cod_tipo_intervalo,
                        i.data_hora_inicio                                    as data_hora_inicio,
                        i.data_hora_fim                                       as data_hora_fim,
                        sum(to_seconds(i.data_hora_fim - i.data_hora_inicio)) as tempo_marcacao_segundos,
                        sum(func_marcacao_calcula_total_segundos_em_horas_noturnas(i.data_hora_inicio,
                                                                                   i.data_hora_fim,
                                                                                   tz_unidade))
                                                                              as tempo_marcacao_horas_noturnas_segundos
                 from func_intervalos_agrupados(f_cod_unidade, null, null) as i
                          join colaborador as c
                               on i.cpf_colaborador = c.cpf
                          left join intervalo_tipo as it
                                    on i.cod_tipo_intervalo = it.codigo
                 where ((((i.data_hora_inicio at time zone tz_unidade)::date >= f_data_inicial)
                     and (((i.data_hora_inicio at time zone tz_unidade)::date <= f_data_final)))
                     or
                        (((i.data_hora_fim at time zone tz_unidade)::date >= f_data_inicial)
                            and ((i.data_hora_fim at time zone tz_unidade)::date <= f_data_final)))
                   -- Expurga marcações que não tem início ou fim.
                   and i.data_hora_inicio is not null
                   and i.data_hora_fim is not null
                   -- Retiramos do cálculo as marcações que foram desativadas.
                   and i.status_ativo_inicio = true
                   and i.status_ativo_fim = true
                 group by c.cpf, it.codigo, i.data_hora_inicio, i.data_hora_fim
             ),

             todas_marcacoes as (
                 select lpad(tu.cpf_colaborador::text, 11, '0')                   as cpf_colaborador,
                        tu.nome_colaborador::text                                 as nome_colaborador,
                        tu.nome_cargo::text                                       as nome_cargo,
                        tu.cod_tipo_marcacao::text                                as cod_tipo_marcacao,
                        tu.nome_tipo_marcacao::text                               as nome_tipo_marcacao,
                        tu.tipo_jornada                                           as tipo_jornada,
                        ttm.data_hora_inicio                                      as data_hora_inicio,
                        ttm.data_hora_fim                                         as data_hora_fim,
                        (ttm.tempo_marcacao_segundos * 1000)::text                as tempo_marcacao_millis,
                        (ttm.tempo_marcacao_horas_noturnas_segundos * 1000)::text as tempo_marcacao_horas_noturnas_millis
                 from tipos_unidade tu
                          left join total_tempo_marcacao ttm
                                    on tu.cpf_colaborador = ttm.cpf_colaborador and
                                       tu.cod_tipo_marcacao = ttm.cod_tipo_intervalo
                 order by tu.cpf_colaborador, tu.cod_tipo_marcacao desc
             ),

             marcacoes_jornada as (
                 select tm.cpf_colaborador                      as cpf_colaborador,
                        tm.nome_colaborador                     as nome_colaborador,
                        tm.nome_cargo                           as nome_cargo,
                        tm.cod_tipo_marcacao                    as cod_tipo_marcacao,
                        tm.nome_tipo_marcacao                   as nome_tipo_marcacao,
                        tm.tipo_jornada                         as tipo_jornada,
                        tm.data_hora_inicio                     as data_hora_inicio,
                        tm.data_hora_fim                        as data_hora_fim,
                        tm.tempo_marcacao_millis                as tempo_marcacao_millis,
                        tm.tempo_marcacao_horas_noturnas_millis as tempo_marcacao_horas_noturnas_millis
                 from todas_marcacoes tm
                 where tm.tipo_jornada = true
             )

        select tm.cpf_colaborador                      as cpf_colaborador,
               tm.nome_colaborador                     as nome,
               tm.nome_cargo                           as cargo,
               tm.cod_tipo_marcacao                    as cod_tipo_intervalo,
               tm.nome_tipo_marcacao                   as nome_tipo_intervalo,
               tm.tipo_jornada                         as tipo_jornada,
               tm.tempo_marcacao_millis                as tempo_marcacao_millis,
               tm.tempo_marcacao_horas_noturnas_millis as tempo_marcacao_horas_noturnas_millis,
               (mj.tipo_jornada is not null)           as marcacao_dentro_jornada
        from todas_marcacoes tm
                 -- Usamos um left join para descobrir as marcações que estão dentro da faixa de uma marcação de
                 -- jornada.
                 left join marcacoes_jornada mj
                           on tm.tipo_jornada = false
                               and tm.cpf_colaborador = mj.cpf_colaborador
                               and (tm.data_hora_inicio between mj.data_hora_inicio and mj.data_hora_fim)
                               and (tm.data_hora_fim between mj.data_hora_inicio and mj.data_hora_fim)
        order by cpf_colaborador, cod_tipo_intervalo;
end;
$$;