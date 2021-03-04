-- Sobre:
--
-- Busca os intervalos (início e fim) de um colaborador específico.
--
-- Os parâmetros 'f_cod_unidade' e 'f_cpf_colaborador' são obrigatórios e para esta function espera-se que nunca sejam
-- nulos.
-- O parâmetro 'f_cod_tipo_intervalo' é opcional. Caso seja nulo, será buscado intervalos de todos os tipos.
--
-- Histórico:
-- 2020-10-14 -> Arquivo específico criado (luizfp).
-- 2020-10-14 -> Query alterada para otimização (luizfp).
create or replace function func_intervalos_get_marcacoes_colaborador(f_cod_unidade bigint,
                                                                     f_cpf_colaborador bigint,
                                                                     f_cod_tipo_intervalo bigint,
                                                                     f_limit bigint,
                                                                     f_offset bigint)
    returns table
            (
                cod_unidade                     bigint,
                cod_tipo_intervalo              bigint,
                nome_tipo_intervalo             text,
                cpf_colaborador                 bigint,
                data_hora_inicio                timestamp without time zone,
                data_hora_fim                   timestamp without time zone,
                fonte_data_hora_inicio          text,
                fonte_data_hora_fim             text,
                justificativa_tempo_recomendado text,
                justificativa_estouro           text,
                latitude_marcacao_inicio        text,
                longitude_marcacao_inicio       text,
                latitude_marcacao_fim           text,
                longitude_marcacao_fim          text
            )
    language sql
as
$$
select f_cod_unidade                                                   as cod_unidade,
       coalesce(i.cod_tipo_intervalo_inicio, f.cod_tipo_intervalo_fim) as cod_tipo_intervalo,
       coalesce(iti.nome, itf.nome)                                    as nome_tipo_intervalo,
       coalesce(i.cpf_colaborador_inicio, f.cpf_colaborador_fim)       as cpf_colaborador,
       i.data_hora_inicio at time zone tz_unidade(f_cod_unidade)       as data_hora_inicio,
       f.data_hora_fim at time zone tz_unidade(f_cod_unidade)          as data_hora_fim,
       i.fonte_data_hora_inicio                                        as fonte_data_hora_inicio,
       f.fonte_data_hora_fim                                           as fonte_data_hora_fim,
       f.justificativa_tempo_recomendado                               as justificativa_tempo_recomendado,
       f.justificativa_estouro                                         as justificativa_estouro,
       i.latitude_marcacao_inicio                                      as latitude_marcacao_inicio,
       i.longitude_marcacao_inicio                                     as longitude_marcacao_inicio,
       f.latitude_marcacao_fim                                         as latitude_marcacao_fim,
       f.longitude_marcacao_fim                                        as longitude_marcacao_fim
from (select i.codigo             as cod_marcacao_inicio,
             mi.cod_marcacao_fim  as cod_marcacao_fim_vinculada,
             i.cod_tipo_intervalo as cod_tipo_intervalo_inicio,
             i.cpf_colaborador    as cpf_colaborador_inicio,
             i.data_hora          as data_hora_inicio,
             i.fonte_data_hora    as fonte_data_hora_inicio,
             i.latitude_marcacao  as latitude_marcacao_inicio,
             i.longitude_marcacao as longitude_marcacao_inicio
      from marcacao_vinculo_inicio_fim mi
               right join intervalo i on mi.cod_marcacao_inicio = i.codigo
      where i.cod_unidade = f_cod_unidade
        and i.cpf_colaborador = f_cpf_colaborador
        -- Usamos case e não f_if pois o case melhora o query plan e temos uma execução mais rápida.
        and case when f_cod_tipo_intervalo is null then true else i.cod_tipo_intervalo = f_cod_tipo_intervalo end
        and i.tipo_marcacao = 'MARCACAO_INICIO') i
         full outer join
     (select i.codigo                          as cod_marcacao_fim,
             i.cod_tipo_intervalo              as cod_tipo_intervalo_fim,
             i.cpf_colaborador                 as cpf_colaborador_fim,
             i.data_hora                       as data_hora_fim,
             i.fonte_data_hora                 as fonte_data_hora_fim,
             i.justificativa_tempo_recomendado as justificativa_tempo_recomendado,
             i.justificativa_estouro           as justificativa_estouro,
             i.latitude_marcacao               as latitude_marcacao_fim,
             i.longitude_marcacao              as longitude_marcacao_fim
      from marcacao_vinculo_inicio_fim mf
               right join intervalo i on mf.cod_marcacao_fim = i.codigo
      where i.cod_unidade = f_cod_unidade
        and i.cpf_colaborador = f_cpf_colaborador
        -- Usamos case e não f_if pois o case melhora o query plan e temos uma execução mais rápida.
        and case when f_cod_tipo_intervalo is null then true else i.cod_tipo_intervalo = f_cod_tipo_intervalo end
        and i.tipo_marcacao = 'MARCACAO_FIM') f
     on i.cod_marcacao_fim_vinculada = f.cod_marcacao_fim
         left join intervalo_tipo iti on i.cod_tipo_intervalo_inicio = iti.codigo
         left join intervalo_tipo itf on f.cod_tipo_intervalo_fim = itf.codigo
order by coalesce(data_hora_inicio, data_hora_fim) desc
limit f_limit offset f_offset;
$$;