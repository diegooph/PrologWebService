-- Esta migração otimiza as seguintes queries:
-- func_intervalos_get_marcacoes_colaborador.
-- func_marcacao_busca_marcacao_em_andamento.
-- integracao.func_marcacao_relatorio_intervalo_portaria_1510_tipo_3.

drop function func_intervalos_agrupados(f_cod_unidade bigint,
    f_cpf_colaborador bigint,
    f_cod_tipo_intervalo bigint);
-- Corrige ordem dos parâmetros: FONTE_DATA_HORA_INICIO e FONTE_DATA_HORA_FIM.
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


-- Otimiza query.
-- Antes:
-- Planning Time: 0.186 ms
-- Execution Time: 5818.349 ms
drop function func_intervalos_get_marcacoes_colaborador(f_cod_unidade bigint,
    f_cpf_colaborador bigint,
    f_cod_tipo_intervalo bigint,
    f_limit bigint,
    f_offset bigint);

-- Depois:
-- Planning Time: 0.709 ms
-- Execution Time: 3.663 ms
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
        and f_if(f_cod_tipo_intervalo is null, true, i.cod_tipo_intervalo = f_cod_tipo_intervalo)
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
        and f_if(f_cod_tipo_intervalo is null, true, i.cod_tipo_intervalo = f_cod_tipo_intervalo)
        and i.tipo_marcacao = 'MARCACAO_FIM') f
     on i.cod_marcacao_fim_vinculada = f.cod_marcacao_fim
         left join intervalo_tipo iti on i.cod_tipo_intervalo_inicio = iti.codigo
         left join intervalo_tipo itf on f.cod_tipo_intervalo_fim = itf.codigo
order by coalesce(data_hora_inicio, data_hora_fim) desc
limit f_limit offset f_offset;
$$;


-- Otimiza query.
-- Antes:
-- Planning Time: 0.565 ms
-- Execution Time: 113.199 ms
drop function func_marcacao_busca_marcacao_em_andamento(f_cod_unidade bigint,
    f_cod_tipo_intervalo bigint,
    f_cpf_colaborador bigint);

-- Depois:
-- Planning Time: 0.400 ms
-- Execution Time: 1.402 ms
create function func_marcacao_busca_marcacao_em_andamento(f_cod_unidade bigint,
                                                          f_cod_tipo_intervalo bigint,
                                                          f_cpf_colaborador bigint)
    returns table
            (
                codigo                          bigint,
                cod_unidade                     bigint,
                cod_tipo_intervalo              bigint,
                cpf_colaborador                 bigint,
                data_nascimento_colaborador     date,
                data_hora                       timestamp without time zone,
                tipo_marcacao                   text,
                fonte_data_hora                 text,
                justificativa_tempo_recomendado text,
                justificativa_estouro           text,
                latitude_marcacao               text,
                longitude_marcacao              text,
                valido                          boolean,
                foi_editado                     boolean,
                cod_colaborador_insercao        bigint,
                status_ativo                    boolean,
                data_hora_sincronizacao         timestamp without time zone
            )
    language sql
as
$$
select i.codigo                                                         as codigo,
       i.cod_unidade                                                    as cod_unidade,
       i.cod_tipo_intervalo                                             as cod_tipo_intervalo,
       i.cpf_colaborador                                                as cpf_colaborador,
       c.data_nascimento                                                as data_nascimento_colaborador,
       i.data_hora at time zone tz_unidade(i.cod_unidade)               as data_hora,
       i.tipo_marcacao                                                  as tipo_marcacao,
       i.fonte_data_hora                                                as fonte_data_hora,
       i.justificativa_tempo_recomendado                                as justificativa_tempo_recomendado,
       i.justificativa_estouro                                          as justificativa_estouro,
       i.latitude_marcacao                                              as latitude_marcacao,
       i.longitude_marcacao                                             as longitude_marcacao,
       i.valido                                                         as valido,
       i.foi_ajustado                                                   as foi_ajustado,
       i.cod_colaborador_insercao                                       as cod_colaborador_insercao,
       i.status_ativo                                                   as status_ativo,
       i.data_hora_sincronizacao at time zone tz_unidade(i.cod_unidade) as data_hora_sincronizacao
from intervalo i
         join colaborador c
              on i.cpf_colaborador = c.cpf
where i.cod_unidade = f_cod_unidade
  and i.cpf_colaborador = f_cpf_colaborador
  and i.cod_tipo_intervalo = f_cod_tipo_intervalo
  and i.tipo_marcacao = 'MARCACAO_INICIO'
  and i.valido = true
  and i.status_ativo = true
  and not exists(select mvif.cod_marcacao_inicio
                 from marcacao_vinculo_inicio_fim mvif
                 where mvif.cod_marcacao_inicio = i.codigo)
order by i.codigo desc
limit 1;
$$;

-- Otimiza query.
-- Antes:
-- Planning Time: 0.030 ms
-- Execution Time: 29227.048 ms

-- Depois:
-- Planning Time: 0.567 ms
-- Execution Time: 825.723 ms
create or replace function integracao.func_marcacao_relatorio_intervalo_portaria_1510_tipo_3(f_token_integracao text,
                                                                                             f_data_inicial date,
                                                                                             f_data_final date,
                                                                                             f_cod_unidade bigint,
                                                                                             f_cod_tipo_intervalo bigint,
                                                                                             f_cpf_colaborador bigint)
    returns table
            (
                nsr              text,
                tipo_registro    text,
                data_marcacao    text,
                horario_marcacao text,
                pis_colaborador  text
            )
    language sql
as
$$
with cte as (
    select lpad(row_number() over (partition by i.cod_unidade order by i.codigo)::text, 9, '0') as nsr,
           i.data_hora                                                                          as data_hora,
           i.cod_unidade                                                                        as cod_unidade,
           i.cod_tipo_intervalo                                                                 as cod_tipo_intervalo,
           i.cpf_colaborador                                                                    as cpf_colaborador,
           i.status_ativo                                                                       as status_ativo
    from intervalo i
    where i.cod_unidade in (select u.codigo
                            from integracao.token_integracao ti
                                     join unidade u on u.cod_empresa = ti.cod_empresa
                            where ti.token_integracao = f_token_integracao)
)
select i.nsr                                                                   as nsr,
       '3'::text                                                               as tipo_registro,
       to_char(i.data_hora at time zone tz_unidade(i.cod_unidade), 'DDMMYYYY') as data_marcacao,
       to_char(i.data_hora at time zone tz_unidade(i.cod_unidade), 'HH24MI')   as horario_marcacao,
       lpad(c.pis::text, 12, '0')                                              as pis_colaborador
from cte i
         join colaborador c on i.cpf_colaborador = c.cpf
     -- Aplicamos um filtro inicial e mais abrangente para que o index de unique da tabela seja utilizado.
     -- Com o '- 1 day' e '+ 1 day' todas as possíveis variações de timezone são abrangidas. É como se fosse um pré-filtro
     -- para depois filtrarmos novamente considerando timezone.
where i.data_hora >= (f_data_inicial::date - interval '1 day')
  and i.data_hora <= (f_data_final::date + interval '1 day')
  and (i.data_hora at time zone tz_unidade(i.cod_unidade))::date >= f_data_inicial
  and (i.data_hora at time zone tz_unidade(i.cod_unidade))::date <= f_data_final
  and c.pis is not null
  and case when f_cod_unidade is null then true else i.cod_unidade = f_cod_unidade end
  and case when f_cod_tipo_intervalo is null then true else i.cod_tipo_intervalo = f_cod_tipo_intervalo end
  and case when f_cpf_colaborador is null then true else i.cpf_colaborador = f_cpf_colaborador end
  and i.status_ativo;
$$;