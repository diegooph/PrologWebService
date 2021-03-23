-- Cria novas views para produzir os intervalos agrupados.
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

-- Criação de indexes para algumas colunas.
-- Unidade.
create index idx_unidade_cod_empresa on unidade (cod_empresa);

-- Colaborador.
create index idx_colaborador_cod_equipe on colaborador_data (cod_equipe);
create index idx_colaborador_cod_funcao on colaborador_data (cod_funcao);
create index idx_colaborador_cod_empresa on colaborador_data (cod_empresa);
create index idx_colaborador_cod_setor on colaborador_data (cod_setor);
create index idx_colaborador_matricula_ambev on colaborador_data (matricula_ambev);

-- Intervalo.
create index idx_intervalo_cod_unidade on intervalo (cod_unidade);
create index idx_intervalo_cod_tipo_intervalo on intervalo (cod_tipo_intervalo);

-- Intervalo Tipo.
create index idx_intervalo_tipo_cargo_cod_unidade on intervalo_tipo_cargo (cod_unidade);
create index idx_intervalo_tipo_cargo_cod_tipo_intervalo on intervalo_tipo_cargo (cod_tipo_intervalo);
create index idx_intervalo_tipo_cargo_cod_cargo on intervalo_tipo_cargo (cod_cargo);

-- Mapa.
create index idx_mapa_data on mapa (data);

-- Equipe.
-- Renomeia a PK da tabela. Estava nomeado como FK. ¯\_(ツ)_/¯
alter table equipe rename constraint fk_equipe to pk_equipe;

-- Dropa view que não será mais utilizada.
drop view view_intervalo_mapa_colaborador;

-- Dropa view que não será mais utilizada, sendo trocada pela function abaixo.
drop view view_extrato_mapas_versus_intervalos;

create or replace function func_marcacao_intervalos_versus_mapas(f_cod_unidade bigint,
                                                                 f_data_inicial date,
                                                                 f_data_final date)
    returns table
            (
                data                        date,
                mapa                        integer,
                cod_unidade                 bigint,
                intervalos_previstos        integer,
                intervalos_realizados       integer,
                cpf_motorista               bigint,
                nome_motorista              text,
                inicio_intervalo_mot        text,
                fim_intervalo_mot           text,
                marcacoes_reconhecidas_mot  boolean,
                tempo_decorrido_minutos_mot text,
                mot_cumpriu_tempo_minimo    text,
                cpf_aj1                     bigint,
                nome_aj1                    text,
                inicio_intervalo_aj1        text,
                fim_intervalo_aj1           text,
                marcacoes_reconhecidas_aj1  boolean,
                tempo_decorrido_minutos_aj1 text,
                aj1_cumpriu_tempo_minimo    text,
                cpf_aj2                     bigint,
                nome_aj2                    text,
                inicio_intervalo_aj2        text,
                fim_intervalo_aj2           text,
                marcacoes_reconhecidas_aj2  boolean,
                tempo_decorrido_minutos_aj2 text,
                aj2_cumpriu_tempo_minimo    text
            )
    language sql
as
$$
with intervalos_agrupados as (
    select coalesce(i.cpf_colaborador, f.cpf_colaborador)                      as cpf_colaborador,
           coalesce(i.cod_tipo_intervalo, f.cod_tipo_intervalo)                as cod_tipo_intervalo,
           i.data_hora_inicio                                                  as data_hora_inicio,
           (i.data_hora_inicio at time zone tz_unidade(i.cod_unidade)) :: date as data_inicio_tz,
           f.data_hora_fim                                                     as data_hora_fim,
           (f.data_hora_fim at time zone tz_unidade(f.cod_unidade)) :: date    as data_fim_tz,
           i.device_reconhecido :: boolean                                     as device_imei_inicio_reconhecido,
           f.device_reconhecido :: boolean                                     as device_imei_fim_reconhecido,
           tz_unidade(coalesce(i.cod_unidade, f.cod_unidade))                  as tz_unidade
    from view_marcacao_inicios i
             full outer join view_marcacao_fins f
                             on i.cod_marcacao_vinculo = f.cod_marcacao_fim
         -- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
         -- diretamente para as inner views e elas são executadas já com os filtros aplicados.
         -- Inícios.
    where i.cod_unidade = f_cod_unidade
      -- Fins.
      and f.cod_unidade = f_cod_unidade
      and (((i.data_hora_inicio at time zone tz_unidade(i.cod_unidade)) :: date between f_data_inicial and f_data_final)
        or
           ((f.data_hora_fim at time zone tz_unidade(f.cod_unidade)) :: date between f_data_inicial and f_data_final))
)

select m.data                        as data,
       m.mapa                        as mapa,
       m.cod_unidade                 as cod_unidade,
       -- (PL-2220) Antes era utilizado "m.fator + 1", mas isso não funciona porque acaba considerando colaboradores
       -- que não estão cadastrados no Prolog e possuem apenas os mapas importados. Lembre-se que fator é quantidade de
       -- ajudantes que sairam junto do motorista. Podendo ser, atualmente, 1 ou 2.
       (F_IF(mot.cpf is null, 0, 1) + F_IF(aj1.cpf is null, 0, 1) +
        F_IF(aj2.cpf is null, 0, 1)) AS intervalos_previstos,
       (F_IF(intervalo_mot.data_hora_fim is null or intervalo_mot.data_hora_inicio is null, 0, 1) +
        F_IF(intervalo_aj1.data_hora_fim is null or intervalo_aj1.data_hora_inicio is null, 0, 1) +
        F_IF(intervalo_aj2.data_hora_fim is null or intervalo_aj2.data_hora_inicio is null, 0,
             1))                     AS intervalos_realizados,
       mot.cpf                       AS cpf_motorista,
       mot.nome                      AS nome_motorista,
       COALESCE(to_char(intervalo_mot.data_hora_inicio at time zone intervalo_mot.tz_unidade, 'HH24:MI'),
                '-')                 AS inicio_intervalo_mot,
       COALESCE(to_char(intervalo_mot.data_hora_fim at time zone intervalo_mot.tz_unidade, 'HH24:MI'),
                '-')                 AS fim_intervalo_mot,
       F_IF(intervalo_mot.device_imei_inicio_reconhecido AND intervalo_mot.device_imei_fim_reconhecido, TRUE,
            FALSE)                   AS marcacoes_reconhecidas_mot,
       coalesce(to_minutes_trunc(intervalo_mot.data_hora_fim - intervalo_mot.data_hora_inicio) :: text,
                '-')                 AS tempo_decorrido_minutos_mot,
       CASE
           WHEN (intervalo_mot.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_mot.tempo_recomendado_minutos >
                 to_minutes_trunc(intervalo_mot.data_hora_fim - intervalo_mot.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS mot_cumpriu_tempo_minimo,
       aj1.cpf                       AS cpf_aj1,
       COALESCE(aj1.nome, '-')       AS nome_aj1,
       COALESCE(to_char(intervalo_aj1.data_hora_inicio at time zone intervalo_aj1.tz_unidade, 'HH24:MI'),
                '-')                 AS inicio_intervalo_aj1,
       COALESCE(to_char(intervalo_aj1.data_hora_fim at time zone intervalo_aj1.tz_unidade, 'HH24:MI'),
                '-')                 AS fim_intervalo_aj1,
       F_IF(intervalo_aj1.device_imei_inicio_reconhecido AND intervalo_aj1.device_imei_fim_reconhecido, TRUE,
            FALSE)                   AS marcacoes_reconhecidas_aj1,
       coalesce(to_minutes_trunc(intervalo_aj1.data_hora_fim - intervalo_aj1.data_hora_inicio) :: text,
                '-')                 AS tempo_decorrido_minutos_aj1,
       CASE
           WHEN (intervalo_aj1.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_aj1.tempo_recomendado_minutos >
                 to_minutes_trunc(intervalo_aj1.data_hora_fim - intervalo_aj1.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS aj1_cumpriu_tempo_minimo,
       aj2.cpf                       AS cpf_aj2,
       COALESCE(aj2.nome, '-')       AS nome_aj2,
       COALESCE(to_char(intervalo_aj2.data_hora_inicio at time zone intervalo_aj2.tz_unidade, 'HH24:MI'),
                '-')                 AS inicio_intervalo_aj2,
       COALESCE(to_char(intervalo_aj2.data_hora_fim at time zone intervalo_aj2.tz_unidade, 'HH24:MI'),
                '-')                 AS fim_intervalo_aj2,
       F_IF(intervalo_aj2.device_imei_inicio_reconhecido AND intervalo_aj2.device_imei_fim_reconhecido, TRUE,
            FALSE)                   AS marcacoes_reconhecidas_aj2,
       coalesce(to_minutes_trunc(intervalo_aj2.data_hora_fim - intervalo_aj2.data_hora_inicio) :: text,
                '-')                 AS tempo_decorrido_minutos_aj2,
       CASE
           WHEN (intervalo_aj2.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_aj2.tempo_recomendado_minutos >
                 to_minutes_trunc(intervalo_aj2.data_hora_fim - intervalo_aj2.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS aj2_cumpriu_tempo_minimo
from mapa m
         join unidade_funcao_produtividade ufp
              on ufp.cod_unidade = m.cod_unidade
         join colaborador mot
              on mot.cod_unidade = m.cod_unidade
                  and mot.cod_funcao = ufp.cod_funcao_motorista
                  and mot.matricula_ambev = m.matricmotorista
         left join colaborador aj1
                   on aj1.cod_unidade = m.cod_unidade
                       and aj1.cod_funcao = ufp.cod_funcao_ajudante
                       and aj1.matricula_ambev = m.matricajud1
         left join colaborador aj2
                   on aj2.cod_unidade = m.cod_unidade
                       and aj2.cod_funcao = ufp.cod_funcao_ajudante
                       and aj2.matricula_ambev = m.matricajud2
         left join intervalos_agrupados intervalo_mot
                   on intervalo_mot.cpf_colaborador = mot.cpf
                       and intervalo_mot.data_inicio_tz = m.data
                       and intervalo_mot.data_fim_tz = m.data
         left join intervalo_tipo tipo_mot
                   on tipo_mot.codigo = intervalo_mot.cod_tipo_intervalo
         left join intervalos_agrupados intervalo_aj1
                   on intervalo_aj1.cpf_colaborador = aj1.cpf
                       and intervalo_aj1.data_inicio_tz = m.data
                       and intervalo_aj1.data_fim_tz = m.data
         left join intervalo_tipo tipo_aj1
                   on tipo_aj1.codigo = intervalo_aj1.cod_tipo_intervalo
         left join intervalos_agrupados intervalo_aj2
                   on intervalo_aj2.cpf_colaborador = aj2.cpf
                       and intervalo_aj2.data_inicio_tz = m.data
                       and intervalo_aj2.data_fim_tz = m.data
         left join intervalo_tipo tipo_aj2
                   on tipo_aj2.codigo = intervalo_aj2.cod_tipo_intervalo
where m.cod_unidade = f_cod_unidade
  and m.data between f_data_inicial and f_data_final;
$$;


-- Realiza diversas melhorias no relatório.
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

-- Remove "order by" dessa function base para evitar processamento desnecessário.
-- Altera para usar VIEWs.
CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE BIGINT,
                                                     F_CPF_COLABORADOR BIGINT,
                                                     F_COD_TIPO_INTERVALO BIGINT)
    RETURNS TABLE
            (
                FONTE_DATA_HORA_FIM             TEXT,
                FONTE_DATA_HORA_INICIO          TEXT,
                JUSTIFICATIVA_ESTOURO           TEXT,
                JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
                LATITUDE_MARCACAO_INICIO        TEXT,
                LONGITUDE_MARCACAO_INICIO       TEXT,
                LATITUDE_MARCACAO_FIM           TEXT,
                LONGITUDE_MARCACAO_FIM          TEXT,
                COD_UNIDADE                     BIGINT,
                CPF_COLABORADOR                 BIGINT,
                COD_TIPO_INTERVALO              BIGINT,
                COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
                DATA_HORA_INICIO                TIMESTAMP WITH TIME ZONE,
                DATA_HORA_FIM                   TIMESTAMP WITH TIME ZONE,
                COD_MARCACAO_INICIO             BIGINT,
                COD_MARCACAO_FIM                BIGINT,
                STATUS_ATIVO_INICIO             BOOLEAN,
                STATUS_ATIVO_FIM                BOOLEAN,
                FOI_AJUSTADO_INICIO             BOOLEAN,
                FOI_AJUSTADO_FIM                BOOLEAN,
                DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITH TIME ZONE,
                DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITH TIME ZONE,
                TIPO_JORNADA                    BOOLEAN,
                DEVICE_IMEI_INICIO              TEXT,
                DEVICE_IMEI_INICIO_RECONHECIDO  BOOLEAN,
                DEVICE_MARCA_INICIO             TEXT,
                DEVICE_MODELO_INICIO            TEXT,
                DEVICE_IMEI_FIM                 TEXT,
                DEVICE_IMEI_FIM_RECONHECIDO     BOOLEAN,
                DEVICE_MARCA_FIM                TEXT,
                DEVICE_MODELO_FIM               TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT I.FONTE_DATA_HORA_INICIO                               AS FONTE_DATA_HORA_INICIO,
       F.FONTE_DATA_HORA_FIM                                  AS FONTE_DATA_HORA_FIM,
       F.JUSTIFICATIVA_ESTOURO                                AS JUSTIFICATIVA_ESTOURO,
       F.JUSTIFICATIVA_TEMPO_RECOMENDADO                      AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
       I.LATITUDE_MARCACAO_INICIO                             AS LATITUDE_MARCACAO_INICIO,
       I.LONGITUDE_MARCACAO_INICIO                            AS LONGITUDE_MARCACAO_INICIO,
       F.LATITUDE_MARCACAO_FIM                                AS LATITUDE_MARCACAO_FIM,
       F.LONGITUDE_MARCACAO_FIM                               AS LONGITUDE_MARCACAO_FIM,
       COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                 AS COD_UNIDADE,
       COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR)         AS CPF_COLABORADOR,
       COALESCE(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)   AS COD_TIPO_INTERVALO,
       COALESCE(
               VITI.CODIGO_TIPO_INTERVALO_POR_UNIDADE,
               VITF.CODIGO_TIPO_INTERVALO_POR_UNIDADE)        AS COD_TIPO_INTERVALO_POR_UNIDADE,
       I.DATA_HORA_INICIO                                     AS DATA_HORA_INICIO,
       F.DATA_HORA_FIM                                        AS DATA_HORA_FIM,
       I.CODIGO_INICIO                                        AS CODIGO_INICIO,
       F.CODIGO_FIM                                           AS CODIGO_FIM,
       I.STATUS_ATIVO_INICIO                                  AS STATUS_ATIVO_INICIO,
       F.STATUS_ATIVO_FIM                                     AS STATUS_ATIVO_FIM,
       I.FOI_AJUSTADO_INICIO                                  AS FOI_AJUSTADO_INICIO,
       F.FOI_AJUSTADO_FIM                                     AS FOI_AJUSTADO_FIM,
       I.DATA_HORA_SINCRONIZACAO_INICIO                       AS DATA_HORA_SINCRONIZACAO_INICIO,
       F.DATA_HORA_SINCRONIZACAO_FIM                          AS DATA_HORA_SINCRONIZACAO_FIM,
       (VITI.TIPO_JORNADA = TRUE OR VITF.TIPO_JORNADA = TRUE) AS TIPO_JORNADA,
       I.DEVICE_IMEI :: TEXT                                  AS DEVICE_IMEI_INICIO,
       I.DEVICE_RECONHECIDO :: BOOLEAN                        AS DEVICE_IMEI_INICIO_RECONHECIDO,
       I.DEVICE_MARCA :: TEXT                                 AS DEVICE_MARCA_INICIO,
       I.DEVICE_MODELO :: TEXT                                AS DEVICE_MODELO_INICIO,
       F.DEVICE_IMEI :: TEXT                                  AS DEVICE_IMEI_FIM,
       F.DEVICE_RECONHECIDO :: BOOLEAN                        AS DEVICE_IMEI_FIM_RECONHECIDO,
       F.DEVICE_MARCA :: TEXT                                 AS DEVICE_MARCA_FIM,
       F.DEVICE_MODELO :: TEXT                                AS DEVICE_MODELO_FIM
FROM VIEW_MARCACAO_INICIOS I
         FULL OUTER JOIN VIEW_MARCACAO_FINS F
                         ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
         LEFT JOIN VIEW_INTERVALO_TIPO VITI
                   ON I.COD_TIPO_INTERVALO = VITI.CODIGO
         LEFT JOIN VIEW_INTERVALO_TIPO VITF
                   ON F.COD_TIPO_INTERVALO = VITF.CODIGO
-- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
-- diretamente para as inner views e elas são executadas já com os filtros aplicados.
-- Inícios.
WHERE CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE I.COD_UNIDADE = F_COD_UNIDADE END
  AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE I.CPF_COLABORADOR = F_CPF_COLABORADOR END
  AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
-- Fins.
  AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE F.COD_UNIDADE = F_COD_UNIDADE END
  AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE F.CPF_COLABORADOR = F_CPF_COLABORADOR END
  AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE F.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
$$;


-- Como o "order by" da FUNC_INTERVALOS_AGRUPADOS foi removido, colocamos um nesse relatório.
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_ESCALA_DIARIA(f_cod_unidade bigint,
                                                                  f_cod_tipo_intervalo bigint,
                                                                  f_data_inicial date,
                                                                  f_data_final date,
                                                                  f_time_zone_unidade text)
    returns TABLE
            (
                "UNIDADE"                     text,
                "PLACA VEÍCULO"               text,
                "CÓDIGO ROTA (MAPA)"          bigint,
                "DATA"                        text,
                "TIPO DE INTERVALO"           text,
                "MOTORISTA"                   text,
                "INÍCIO INTERVALO MOTORISTA"  text,
                "FIM INTERVALO MOTORISTA"     text,
                "MARCAÇÕES RECONHECIDAS MOT"  text,
                "AJUDANTE 1"                  text,
                "INÍCIO INTERVALO AJUDANTE 1" text,
                "FIM INTERVALO AJUDANTE 1"    text,
                "MARCAÇÕES RECONHECIDAS AJ 1" text,
                "AJUDANTE 2"                  text,
                "INÍCIO INTERVALO AJUDANTE 2" text,
                "FIM INTERVALO AJUDANTE 2"    text,
                "MARCAÇÕES RECONHECIDAS AJ 2" text
            )
    language sql
as
$$
WITH TABLE_INTERVALOS AS (SELECT *
                          FROM FUNC_INTERVALOS_AGRUPADOS(f_cod_unidade, NULL, f_cod_tipo_intervalo) F
                          WHERE (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE
                                 f_time_zone_unidade) :: DATE >= f_data_inicial
                            AND (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE
                                 f_time_zone_unidade) :: DATE <= f_data_final)

SELECT (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = f_cod_unidade),
       ED.PLACA,
       ED.MAPA,
       TO_CHAR(ED.DATA, 'DD/MM/YYYY'),
       (SELECT IT.NOME FROM INTERVALO_TIPO IT WHERE IT.CODIGO = f_cod_tipo_intervalo),
       -- MOTORISTA
       F_IF(CM.CPF IS NULL, 'MOTORISTA NÃO CADASTRADO', CM.NOME)    AS NOME_MOTORISTA,
       F_IF(INT_MOT.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_MOT.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_MOT.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_MOT.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_MOT.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_MOT.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_MOT,
       -- AJUDANTE 1
       F_IF(CA1.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA1.NOME) AS NOME_AJUDANTE_1,
       F_IF(INT_AJ1.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_AJ1.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ1.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_AJ1.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ1.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_AJ1.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ1,
       -- AJUDANTE 2
       F_IF(CA2.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA2.NOME) AS NOME_AJUDANTE_2,
       F_IF(INT_AJ2.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_AJ2.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ2.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_AJ2.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ2.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_AJ2.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ2
FROM ESCALA_DIARIA AS ED
         LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA
         LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1
         LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2
         LEFT JOIN TABLE_INTERVALOS INT_MOT
                   ON (COALESCE(INT_MOT.data_hora_inicio, INT_MOT.data_hora_fim) AT TIME ZONE
                       f_time_zone_unidade) :: DATE =
                      ED.data
                       AND INT_MOT.cpf_colaborador = ED.cpf_motorista
         LEFT JOIN TABLE_INTERVALOS INT_AJ1
                   ON (COALESCE(INT_AJ1.data_hora_inicio, INT_AJ1.data_hora_fim) AT TIME ZONE
                       f_time_zone_unidade) :: DATE =
                      ED.data
                       AND INT_AJ1.cpf_colaborador = ED.cpf_ajudante_1
         LEFT JOIN TABLE_INTERVALOS INT_AJ2
                   ON (COALESCE(INT_AJ2.data_hora_inicio, INT_AJ2.data_hora_fim) AT TIME ZONE
                       f_time_zone_unidade) :: DATE =
                      ED.data
                       AND INT_AJ2.cpf_colaborador = ED.cpf_ajudante_2

WHERE (ED.DATA >= f_data_inicial AND ED.DATA <= f_data_final)
  AND ED.COD_UNIDADE = F_COD_UNIDADE
ORDER BY ED.COD_UNIDADE, ED.DATA DESC;
$$;

-- Altera busca para usar nova function base e remove where não mais necessário.
create or replace function func_relatorio_aderencia_intervalo_dias(f_cod_unidade bigint,
                                                                   f_data_inicial date,
                                                                   f_data_final date)
    returns TABLE
            (
                "DATA"                     CHARACTER VARYING,
                "QT MAPAS"                 BIGINT,
                "QT MOTORISTAS"            BIGINT,
                "QT INTERVALOS MOTORISTAS" BIGINT,
                "ADERÊNCIA MOTORISTAS"     TEXT,
                "QT AJUDANTES"             BIGINT,
                "QT INTERVALOS AJUDANTES"  BIGINT,
                "ADERÊNCIA AJUDANTES"      TEXT,
                "QT INTERVALOS PREVISTOS"  BIGINT,
                "QT INTERVALOS REALIZADOS" BIGINT,
                "ADERÊNCIA DIA"            TEXT
            )
    language sql
as
$$
SELECT to_char(V.DATA, 'DD/MM/YYYY'),
       COUNT(V.MAPA)                                                              as mapas,
       SUM(f_if(v.cpf_motorista is not null, 1, 0))                               as qt_motoristas,
       SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0))                      as qt_intervalos_mot,
       COALESCE_PERCENTAGE(SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0)) :: FLOAT,
                           SUM(f_if(v.cpf_motorista is not null, 1, 0)) :: FLOAT) as aderencia_motoristas,
       SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
       SUM(f_if(v.cpf_aj2 is not null, 1, 0))                                     as numero_ajudantes,
       SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
       SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0))                      as qt_intervalos_aj,
       COALESCE_PERCENTAGE(
                   SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
                   SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0)) :: FLOAT,
                   SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
                   SUM(f_if(v.cpf_aj2 is not null, 1, 0)) :: FLOAT)               as aderencia_ajudantes,
       SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
       SUM(V.intervalos_previstos)                                                as qt_intervalos_previstos,
       SUM(V.INTERVALOS_realizados)                                               as qt_intervalos_realizados,
       COALESCE_PERCENTAGE(SUM(V.intervalos_realizados) :: FLOAT,
                           SUM(V.intervalos_previstos) :: FLOAT)                  as aderencia_dia
FROM func_marcacao_intervalos_versus_mapas(f_cod_unidade, f_data_inicial, f_data_final) v
         JOIN unidade u on u.codigo = v.cod_unidade
         JOIN empresa e on e.codigo = u.cod_empresa
GROUP BY V.DATA
ORDER BY V.DATA
$$;


-- Altera busca para usar nova function base e remove where não mais necessário.
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALOS_MAPAS(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "DATA"                                 VARCHAR,
    "MAPA"                                 INT,
    "MOTORISTA"                            VARCHAR,
    "INICIO INTERVALO MOTORISTA"           VARCHAR,
    "FIM INTERVALO MOTORISTA"              VARCHAR,
    "MARCAÇÕES RECONHECIDAS MOT"           VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS MOTORISTA" VARCHAR,
    "MOTORISTA CUMPRIU TEMPO MÍNIMO"       VARCHAR,
    "AJUDANTE 1"                           VARCHAR,
    "INICIO INTERVALO AJ 1"                VARCHAR,
    "FIM INTERVALO AJ 1"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 1"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 1"      VARCHAR,
    "AJ 1 CUMPRIU TEMPO MÍNIMO"            VARCHAR,
    "AJ 2"                                 VARCHAR,
    "INICIO INTERVALO AJ 2"                VARCHAR,
    "FIM INTERVALO AJ 2"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 2"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 2"      VARCHAR,
    "AJ 2 CUMPRIU TEMPO MÍNIMO"            VARCHAR
  ) AS
$func$
SELECT to_char(dados.data, 'DD/MM/YYYY'),
       dados.mapa,
       dados.NOME_MOTORISTA,
       dados.INICIO_INTERVALO_MOT,
       dados.FIM_INTERVALO_MOT,
       F_IF(dados.MARCACOES_RECONHECIDAS_MOT, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_MOT,
       dados.MOT_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj1,
       dados.INICIO_INTERVALO_aj1,
       dados.FIM_INTERVALO_aj1,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ1, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj1,
       dados.aj1_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj2,
       dados.INICIO_INTERVALO_aj2,
       dados.FIM_INTERVALO_aj2,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ2, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj2,
       dados.aj2_CUMPRIU_TEMPO_MINIMO
FROM func_marcacao_intervalos_versus_mapas(f_cod_unidade, f_data_inicial, f_data_final) dados
ORDER BY dados.MAPA desc
$func$
LANGUAGE SQL;