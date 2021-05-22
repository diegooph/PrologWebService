create or replace function func_indicador_get_acumulado_individual(f_cpf bigint,
                                                                   f_data_inicial date,
                                                                   f_data_final date)
    returns table
            (
                carregadas_total                    real,
                viagens_total                       bigint,
                hl_carregados_total                 real,
                hl_devolvidos_total                 real,
                nf_carregadas_total                 bigint,
                nf_devolvidas_total                 bigint,
                pdv_carregados_total                bigint,
                pdv_devolvidos_total                bigint,
                km_planejado_total                  real,
                km_percorrido_total                 bigint,
                total_mapas_bateram_dispersao_tempo bigint,
                media_dispersao_tempo_realizado     double precision,
                media_dispersao_tempo_planejado     double precision,
                total_mapas_bateram_jornada         bigint,
                media_jornada                       double precision,
                total_mapas_bateram_tempo_interno   bigint,
                total_mapas_validos_tempo_interno   bigint,
                media_tempo_interno                 double precision,
                total_mapas_bateram_tempo_largada   bigint,
                total_mapas_validos_tempo_largada   bigint,
                media_tempo_largada                 double precision,
                total_mapas_bateram_tempo_rota      bigint,
                media_tempo_rota                    double precision,
                total_apontamentos_ok               numeric,
                total_apontamentos                  numeric,
                meta_tracking                       real,
                meta_tempo_rota_horas               bigint,
                meta_tempo_rota_mapas               real,
                meta_caixa_viagem                   real,
                meta_dev_hl                         real,
                meta_dev_pdv                        real,
                meta_dispersao_km                   real,
                meta_dispersao_tempo                real,
                meta_jornada_liquida_horas          bigint,
                meta_jornada_liquida_mapas          real,
                meta_raio_tracking                  real,
                meta_tempo_interno_horas            bigint,
                meta_tempo_interno_mapas            real,
                meta_tempo_largada_horas            bigint,
                meta_tempo_largada_mapas            real,
                meta_dev_nf                         real
            )
    language sql
as
$$
with internal_tracking as (
    select t.mapa                                                                 as mapa,
           sum(1) filter (where t.disp_apont_cadastrado <= um.meta_raio_tracking) as apontamentos_ok,
           count(t.disp_apont_cadastrado)                                         as total_apontamentos
    from tracking t
             join unidade_metas um on um.cod_unidade = t.cod_unidade
    where t.cod_unidade = (select c.cod_unidade from colaborador c where c.cpf = f_cpf)
    group by t.mapa
)
select sum(m.cxcarreg)                                                         as carregadas_total,
       count(m.mapa)                                                           as viagens_total,
       sum(m.qthlcarregados)                                                   as hl_carregados_total,
       sum(qthlcarregados - qthlentregues)                                     as hl_devolvidos_total,
       sum(m.qtnfcarregadas)                                                   as nf_carregadas_total,
       sum(qtnfcarregadas - qtnfentregues)                                     as nf_devolvidas_total,
       sum(m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais) as pdv_carregados_total,
       sum(m.entregasnaorealizadas + m.entregasparciais)                       as pdv_devolvidos_total,
       sum(case
               when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad
               else 0 end)                                                     as km_planejado_total,
       sum(case
               when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)
               else 0 end)                                                     as km_percorrido_total,
       sum(case
               when (m.hrentr - m.hrsai) <= m.tempoprevistoroad and (m.hrentr - m.hrsai) > '00:00' and
                    m.tempoprevistoroad > '00:00' then 1
               else 0
           end)                                                                as total_mapas_bateram_dispersao_tempo,
       extract(epoch from avg(case
                                  when (m.hrentr - m.hrsai) > '00:00' and m.tempoprevistoroad > '00:00'
                                      then (m.hrentr - m.hrsai)
           end))                                                               as media_dispersao_tempo_realizado,
       extract(epoch from avg(case
                                  when (m.hrentr - m.hrsai) > '00:00' and m.tempoprevistoroad > '00:00'
                                      then m.tempoprevistoroad
           end))                                                               as media_dispersao_tempo_planejado,
       -- Jornada -> primeiro verifica se é >00:00, depois verifica se é menor do que a meta.
       sum(case
               when
                           (case
                                when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz +
                                                                       (m.hrentr - m.hrsai) + m.tempointerno)
                                when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) +
                                                                        (m.hrentr - m.hrsai) +
                                                                        m.tempointerno)
                               end) > '00:00'
                       and
                           (case
                                when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz +
                                                                       (m.hrentr - m.hrsai) + m.tempointerno)
                                when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) +
                                                                        (m.hrentr - m.hrsai) +
                                                                        m.tempointerno)
                               end) <= um.meta_jornada_liquida_horas then 1
               else 0 end)                                                     as total_mapas_bateram_jornada,
       extract(epoch from avg(case
                                  when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::interval +
                                                                         (m.hrentr - m.hrsai) + m.tempointerno)
                                  when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) +
                                                                          (m.hrentr - m.hrsai) + m.tempointerno)
           end))                                                               as media_jornada,
       sum(case
               when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00' then 1
               else 0
           end)                                                                as total_mapas_bateram_tempo_interno,
       sum(case
               when m.tempointerno <= '05:00' and m.tempointerno > '00:00' then 1
               else 0
           end)                                                                as total_mapas_validos_tempo_interno,
       extract(epoch from avg(
               case
                   when m.tempointerno > '00:00' and m.tempointerno <= '05:00'
                       then m.tempointerno
                   end))                                                       as media_tempo_interno,
       sum(case
               when
                       (case
                            when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas
                            else (m.hrsai - m.hrmatinal)::time
                           end) <= um.meta_tempo_largada_horas then 1
               else 0 end)                                                     as total_mapas_bateram_tempo_largada,
       sum(case
               when
                       (case
                            when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas
                            else (m.hrsai - m.hrmatinal)::time
                           end) <= '05:00' then 1
               else 0 end)                                                     as total_mapas_validos_tempo_largada,
       extract(epoch from avg(case
                                  when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas
                                  when (m.hrsai - m.hrmatinal)::time > '05:00' then '00:30'
                                  else (m.hrsai - m.hrmatinal)::time
           end))                                                               as media_tempo_largada,
       sum(case
               when (m.hrentr - m.hrsai) > '00:00' and (m.hrentr - m.hrsai) <= meta_tempo_rota_horas then 1
               else 0 end)                                                     as total_mapas_bateram_tempo_rota,
       extract(epoch from avg(CASE
                                  when (m.hrentr - m.hrsai) > '00:00'
                                      THEN (m.hrentr - m.hrsai)
           END))                                                               as media_tempo_rota,
       sum(it.apontamentos_ok)                                                 as total_apontamentos_ok,
       sum(it.total_apontamentos)                                              as total_apontamentos,
       um.meta_tracking                                                        as meta_tracking,
       to_seconds(um.meta_tempo_rota_horas)                                    as meta_tempo_rota_horas,
       um.meta_tempo_rota_mapas                                                as meta_tempo_rota_mapas,
       um.meta_caixa_viagem                                                    as meta_caixa_viagem,
       um.meta_dev_hl                                                          as meta_dev_hl,
       um.meta_dev_pdv                                                         as meta_dev_pdv,
       um.meta_dispersao_km                                                    as meta_dispersao_km,
       um.meta_dispersao_tempo                                                 as meta_dispersao_tempo,
       to_seconds(um.meta_jornada_liquida_horas)                               as meta_jornada_liquida_horas,
       um.meta_jornada_liquida_mapas                                           as meta_jornada_liquida_mapas,
       um.meta_raio_tracking                                                   as meta_raio_tracking,
       to_seconds(um.meta_tempo_interno_horas)                                 as meta_tempo_interno_horas,
       um.meta_tempo_interno_mapas                                             as meta_tempo_interno_mapas,
       to_seconds(um.meta_tempo_largada_horas)                                 as meta_tempo_largada_horas,
       um.meta_tempo_largada_mapas                                             as meta_tempo_largada_mapas,
       um.meta_dev_nf                                                          as meta_dev_nf
from mapa m
         join unidade_metas um on um.cod_unidade = m.cod_unidade
         join view_mapa_colaborador vmc on vmc.cod_unidade = m.cod_unidade and m.mapa = vmc.mapa
         join colaborador c on c.cod_unidade = vmc.cod_unidade and c.cpf = vmc.cpf
         left join internal_tracking as it on it.mapa = m.mapa
where m.cod_unidade = (select c.cod_unidade from colaborador c where c.cpf = f_cpf)
  and m.data between f_data_inicial and f_data_final
  and c.cpf = f_cpf
group by um.cod_unidade, um.meta_tracking, um.meta_tempo_rota_horas, um.meta_tempo_rota_mapas, um.meta_caixa_viagem,
         um.meta_dev_hl, um.meta_dev_pdv, um.meta_dispersao_km, um.meta_dispersao_tempo, um.meta_jornada_liquida_horas,
         um.meta_jornada_liquida_mapas, um.meta_raio_tracking, um.meta_tempo_interno_horas, um.meta_tempo_interno_mapas,
         um.meta_tempo_largada_horas,
         um.meta_tempo_largada_mapas, um.meta_dev_nf;
$$;