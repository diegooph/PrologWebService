create or replace view view_marcacao_inicios as
select mi.cod_marcacao_inicio,
       mv.cod_marcacao_fim       as cod_marcacao_vinculo,
       i.fonte_data_hora         as fonte_data_hora_inicio,
       i.latitude_marcacao       as latitude_marcacao_inicio,
       i.longitude_marcacao      as longitude_marcacao_inicio,
       i.cod_unidade             as cod_unidade,
       i.cpf_colaborador         as cpf_colaborador,
       i.cod_tipo_intervalo      as cod_tipo_intervalo,
       i.data_hora               as data_hora_inicio,
       i.codigo                  as codigo_inicio,
       i.status_ativo            as status_ativo_inicio,
       i.foi_ajustado            as foi_ajustado_inicio,
       i.data_hora_sincronizacao as data_hora_sincronizacao_inicio,
       i.device_imei             as device_imei,
       dmi.imei is not null      as device_reconhecido,
       i.marca_device            as device_marca,
       i.modelo_device           as device_modelo
from marcacao_inicio mi
         left join marcacao_vinculo_inicio_fim mv
                   on mi.cod_marcacao_inicio = mv.cod_marcacao_inicio
         join intervalo i
              on mi.cod_marcacao_inicio = i.codigo
         join unidade uni
              on uni.codigo = i.cod_unidade
         left join dispositivo_movel_imei dmi
                   on dmi.cod_empresa = uni.cod_empresa and dmi.imei = i.device_imei
where i.valido = true;

