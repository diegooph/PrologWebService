create or replace function func_intervalos_agrupados(f_cod_unidade bigint,
                                                     f_cpf_colaborador bigint,
                                                     f_cod_tipo_intervalo bigint)
    returns table
            (
                fonte_data_hora_inicio          text,
                fonte_data_hora_fim             text,
                justificativa_estouro           text,
                justificativa_tempo_recomendado text,
                latitude_marcacao_inicio        text,
                longitude_marcacao_inicio       text,
                latitude_marcacao_fim           text,
                longitude_marcacao_fim          text,
                cod_unidade                     bigint,
                cpf_colaborador                 bigint,
                cod_tipo_intervalo              bigint,
                cod_tipo_intervalo_por_unidade  bigint,
                data_hora_inicio                timestamp with time zone,
                data_hora_fim                   timestamp with time zone,
                cod_marcacao_inicio             bigint,
                cod_marcacao_fim                bigint,
                status_ativo_inicio             boolean,
                status_ativo_fim                boolean,
                foi_ajustado_inicio             boolean,
                foi_ajustado_fim                boolean,
                data_hora_sincronizacao_inicio  timestamp with time zone,
                data_hora_sincronizacao_fim     timestamp with time zone,
                tipo_jornada                    boolean,
                device_imei_inicio              text,
                device_imei_inicio_reconhecido  boolean,
                device_marca_inicio             text,
                device_modelo_inicio            text,
                device_imei_fim                 text,
                device_imei_fim_reconhecido     boolean,
                device_marca_fim                text,
                device_modelo_fim               text
            )
    language sql
as
$$
select i.fonte_data_hora_inicio                               as fonte_data_hora_inicio,
       f.fonte_data_hora_fim                                  as fonte_data_hora_fim,
       f.justificativa_estouro                                as justificativa_estouro,
       f.justificativa_tempo_recomendado                      as justificativa_tempo_recomendado,
       i.latitude_marcacao_inicio                             as latitude_marcacao_inicio,
       i.longitude_marcacao_inicio                            as longitude_marcacao_inicio,
       f.latitude_marcacao_fim                                as latitude_marcacao_fim,
       f.longitude_marcacao_fim                               as longitude_marcacao_fim,
       coalesce(i.cod_unidade, f.cod_unidade)                 as cod_unidade,
       coalesce(i.cpf_colaborador, f.cpf_colaborador)         as cpf_colaborador,
       coalesce(i.cod_tipo_intervalo, f.cod_tipo_intervalo)   as cod_tipo_intervalo,
       coalesce(
               viti.codigo_tipo_intervalo_por_unidade,
               vitf.codigo_tipo_intervalo_por_unidade)        as cod_tipo_intervalo_por_unidade,
       i.data_hora_inicio                                     as data_hora_inicio,
       f.data_hora_fim                                        as data_hora_fim,
       i.codigo_inicio                                        as codigo_inicio,
       f.codigo_fim                                           as codigo_fim,
       i.status_ativo_inicio                                  as status_ativo_inicio,
       f.status_ativo_fim                                     as status_ativo_fim,
       i.foi_ajustado_inicio                                  as foi_ajustado_inicio,
       f.foi_ajustado_fim                                     as foi_ajustado_fim,
       i.data_hora_sincronizacao_inicio                       as data_hora_sincronizacao_inicio,
       f.data_hora_sincronizacao_fim                          as data_hora_sincronizacao_fim,
       (viti.tipo_jornada = true or vitf.tipo_jornada = true) as tipo_jornada,
       i.device_imei :: text                                  as device_imei_inicio,
       i.device_reconhecido :: boolean                        as device_imei_inicio_reconhecido,
       i.device_marca :: text                                 as device_marca_inicio,
       i.device_modelo :: text                                as device_modelo_inicio,
       f.device_imei :: text                                  as device_imei_fim,
       f.device_reconhecido :: boolean                        as device_imei_fim_reconhecido,
       f.device_marca :: text                                 as device_marca_fim,
       f.device_modelo :: text                                as device_modelo_fim
from view_marcacao_inicios i
         full outer join view_marcacao_fins f
                         on i.cod_marcacao_vinculo = f.cod_marcacao_fim
         left join view_intervalo_tipo viti
                   on i.cod_tipo_intervalo = viti.codigo
         left join view_intervalo_tipo vitf
                   on f.cod_tipo_intervalo = vitf.codigo
-- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
-- diretamente para as inner views e elas são executadas já com os filtros aplicados.
-- Inícios - apenas se houver um início.
where case
          when i.cod_unidade is not null
              then
                  case when f_cod_unidade is null then true else i.cod_unidade = f_cod_unidade end
                  and
                  case when f_cpf_colaborador is null then true else i.cpf_colaborador = f_cpf_colaborador end
                  and
                  case when f_cod_tipo_intervalo is null then true else i.cod_tipo_intervalo = f_cod_tipo_intervalo end
          else true
    end
-- Fins - apenas se houver um fim.
  and case
          when f.cod_unidade is not null
              then
                  case when f_cod_unidade is null then true else f.cod_unidade = f_cod_unidade end
                  and
                  case when f_cpf_colaborador is null then true else f.cpf_colaborador = f_cpf_colaborador end
                  and
                  case when f_cod_tipo_intervalo is null then true else f.cod_tipo_intervalo = f_cod_tipo_intervalo end
          else true
    end
$$;