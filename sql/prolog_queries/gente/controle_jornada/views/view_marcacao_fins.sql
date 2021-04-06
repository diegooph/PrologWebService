create or replace view view_marcacao_fins as
select mf.cod_marcacao_fim,
       mv.cod_marcacao_inicio            as cod_marcacao_vinculo,
       f.fonte_data_hora                 as fonte_data_hora_fim,
       f.justificativa_estouro           as justificativa_estouro,
       f.justificativa_tempo_recomendado as justificativa_tempo_recomendado,
       f.latitude_marcacao               as latitude_marcacao_fim,
       f.longitude_marcacao              as longitude_marcacao_fim,
       f.cod_unidade                     as cod_unidade,
       f.cpf_colaborador                 as cpf_colaborador,
       f.cod_tipo_intervalo              as cod_tipo_intervalo,
       f.data_hora                       as data_hora_fim,
       f.codigo                          as codigo_fim,
       f.status_ativo                    as status_ativo_fim,
       f.foi_ajustado                    as foi_ajustado_fim,
       f.data_hora_sincronizacao         as data_hora_sincronizacao_fim,
       f.device_imei                     as device_imei,
       dmi.imei is not null              as device_reconhecido,
       f.marca_device                    as device_marca,
       f.modelo_device                   as device_modelo
from marcacao_fim mf
         left join marcacao_vinculo_inicio_fim mv
                   on mf.cod_marcacao_fim = mv.cod_marcacao_fim
         join intervalo f
              on mf.cod_marcacao_fim = f.codigo
         join unidade uni
              on uni.codigo = f.cod_unidade
         left join dispositivo_movel_imei dmi
                   on dmi.cod_empresa = uni.cod_empresa and dmi.imei = f.device_imei
where f.valido = true;