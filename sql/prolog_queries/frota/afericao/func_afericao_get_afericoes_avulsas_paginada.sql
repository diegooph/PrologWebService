create or replace function func_afericao_get_afericoes_avulsas_paginada(f_cod_unidades bigint[],
                                                                        f_data_inicial date,
                                                                        f_data_final date,
                                                                        f_limit bigint,
                                                                        f_offset bigint,
                                                                        f_incluir_medidas boolean default false)
    returns table
            (
                cod_afericao          bigint,
                cod_unidade           bigint,
                data_hora             timestamp without time zone,
                tipo_medicao_coletada text,
                tipo_processo_coleta  text,
                forma_coleta_dados    text,
                cpf                   text,
                nome                  text,
                tempo_realizacao      bigint
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta,
       a.forma_coleta_dados::text                         as forma_coleta_dados,
       c.cpf::text                                        as cpf,
       c.nome::text                                       as nome,
       a.tempo_realizacao                                 as tempo_realizacao
from afericao a
         join colaborador c on c.cpf = a.cpf_aferidor
where a.cod_unidade = any (f_cod_unidades)
  and a.tipo_processo_coleta = 'PNEU_AVULSO'
  and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date between f_data_inicial and f_data_final
order by a.data_hora desc
limit f_limit offset f_offset;
$$;