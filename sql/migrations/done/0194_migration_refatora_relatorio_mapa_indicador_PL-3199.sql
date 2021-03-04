-- 2020-10-18 -> Refatora filtragens e altera o nome (luiz_fp - PL-3199).
drop function func_relatorio_consolidado_mapas_indicadores(f_data_inicial date,
    f_data_final date,
    f_cpf text,
    f_cod_unidade text,
    f_cod_equipe text,
    f_cod_empresa bigint,
    f_cod_regional text);

create or replace function func_indicador_relatorio_consolidado_mapas_indicadores(f_cod_empresa bigint,
                                                                                  f_cod_regional bigint,
                                                                                  f_cod_unidade bigint,
                                                                                  f_cod_equipe bigint,
                                                                                  f_cpf bigint,
                                                                                  f_data_inicial date,
                                                                                  f_data_final date)
    returns table
            (
                "NOME"                          text,
                "EQUIPE"                        text,
                "FUNÇÃO"                        text,
                "TOTAL MAPAS REALIZADOS"        bigint,
                "HL CARREGADOS"                 numeric,
                "HL ENTREGUES"                  numeric,
                "HL DEVOLVIDOS"                 numeric,
                "RESULTADO DEV HL"              text,
                "META DEV HL"                   text,
                "BATEU DEV HL"                  text,
                "GOLS DEV HL"                   bigint,
                "NF CARREGADAS"                 bigint,
                "NF ENTREGUES"                  bigint,
                "NF DEVOLVIDAS"                 bigint,
                "RESULTADO DEV NF"              text,
                "META DEV NF"                   text,
                "BATEU DEV NF"                  text,
                "GOLS DEV NF"                   bigint,
                "PDVS CARREGADOS"               bigint,
                "PDVS ENTREGUES"                bigint,
                "RESULTADO DEV PDV"             text,
                "META DEV PDV"                  text,
                "BATEU DEV PDV"                 text,
                "GOLS DEV PDV"                  bigint,
                "KM PLANEJADO"                  numeric,
                "KM PERCORRIDO"                 bigint,
                "KM DISPERSO"                   numeric,
                "RESULTADO DISPERSAO KM"        text,
                "META DISPERSAO KM"             text,
                "BATEU DISPERSAO KM"            text,
                "GOLS DISPERSAO KM"             bigint,
                "MEDIA TEMPO LARGADA"           text,
                "MAPAS VÁLIDOS TEMPO LARGADA"   bigint,
                "MAPAS BATERAM TEMPO LARGADA"   bigint,
                "RESULTADO TEMPO LARGADA"       text,
                "META TEMPO LARGADA"            text,
                "BATEU TEMPO LARGADA"           text,
                "GOLS TEMPO LARGADA"            bigint,
                "MEDIA TEMPO EM ROTA"           text,
                "MAPAS VÁLIDOS TEMPO EM ROTA"   bigint,
                "MAPAS BATERAM TEMPO EM ROTA"   bigint,
                "RESULTADO TEMPO EM ROTA"       text,
                "META TEMPO EM ROTA"            text,
                "BATEU TEMPO EM ROTA"           text,
                "GOLS TEMPO EM ROTA"            bigint,
                "MEDIA TEMPO INTERNO"           text,
                "MAPAS VÁLIDOS TEMPO INTERNO"   bigint,
                "MAPAS BATERAM TEMPO INTERNO"   bigint,
                "RESULTADO TEMPO INTERNO"       text,
                "META TEMPO INTERNO"            text,
                "BATEU TEMPO INTERNO"           text,
                "GOLS TEMPO INTERNO"            bigint,
                "MEDIA JORNADA"                 text,
                "MAPAS VÁLIDOS JORNADA"         bigint,
                "MAPAS BATERAM JORNADA"         bigint,
                "RESULTADO JORNADA"             text,
                "META JORNADA"                  text,
                "BATEU JORNADA"                 text,
                "GOLS JORNADA"                  bigint,
                "MEDIA TEMPO PLANEJADO"         text,
                "MAPAS VÁLIDOS DISPERSAO TEMPO" bigint,
                "MAPAS BATERAM DISP TEMPO"      bigint,
                "RESULTADO DISP TEMPO"          text,
                "META DISP TEMPO"               text,
                "BATEU DISP TEMPO"              text,
                "GOLS DISP TEMPO"               bigint,
                "TOTAL TRACKING"                numeric,
                "TOTAL OK"                      numeric,
                "TOTAL NOK"                     numeric,
                "RESULTADO TRACKING"            text,
                "META TRACKING"                 text,
                "BATEU TRACKING"                text,
                "GOLS TRACKING"                 bigint,
                "TOTAL DE GOLS GERAL"           bigint
            )
    language sql
as
$$
select dados.nome                                                                 as nome,
       dados.equipe                                                               as equipe,
       dados.funcao                                                               as funcao,
       dados.total_mapas                                                          as total_mapas,
       trunc(dados.hl_carregados_total::numeric, 2)                               as hl_carregados_total,
       trunc((dados.hl_carregados_total - dados.hl_devolvidos_total)::numeric, 2) as hl_entregues_total,
       trunc(dados.hl_devolvidos_total::numeric, 2)                               as hl_devolvidos_total,
       case
           when dados.hl_carregados_total > 0 then
               trunc(((dados.hl_devolvidos_total / dados.hl_carregados_total) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_dev_hl,
       trunc((dados.meta_dev_hl * 100)::numeric, 2) || '%'                        as meta_dev_hl,
       case
           when dados.hl_carregados_total > 0 and
                ((dados.hl_devolvidos_total / dados.hl_carregados_total) <= dados.meta_dev_hl)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_dev_hl,
       dados.gols_dev_hl                                                          as gols_dev_hl,
       dados.nf_carregadas_total                                                  as nf_carregadas_total,
       dados.nf_carregadas_total - dados.nf_devolvidas_total                      as nf_entregues_total,
       dados.nf_devolvidas_total                                                  as nf_devolvidas_total,
       case
           when dados.nf_carregadas_total > 0 then
               trunc(((dados.nf_devolvidas_total::float / dados.nf_carregadas_total) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_dev_nf,
       trunc((dados.meta_dev_nf * 100)::numeric, 2) || '%'                        as meta_dev_nf,
       case
           when dados.nf_carregadas_total > 0 and
                ((dados.nf_devolvidas_total / dados.nf_carregadas_total) <= dados.meta_dev_nf)
               then 'SIM'
           else 'NÃO' end,
       dados.gols_dev_nf                                                          as gols_dev_nf,
       dados.pdv_carregados_total                                                 as pdv_carregados_total,
       dados.pdv_carregados_total - dados.pdv_devolvidos_total                    as pdv_entregues,
       case
           when dados.pdv_carregados_total > 0 then
               trunc(((dados.pdv_devolvidos_total / dados.pdv_carregados_total::float) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_dev_pdv,
       trunc((dados.meta_dev_pdv * 100)::numeric, 2) || '%'                       as meta_dev_pdv,
       case
           when dados.pdv_carregados_total > 0 and
                ((dados.pdv_devolvidos_total / dados.pdv_carregados_total) <= dados.meta_dev_pdv)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_dev_pdv,
       dados.gols_dev_pdv                                                         as gols_dev_pdv,
       trunc(dados.km_planejado_total::numeric, 2)                                as km_planejado_total,
       dados.km_percorrido_total                                                  as km_percorrido_total,
       trunc((dados.km_percorrido_total - dados.km_planejado_total)::numeric, 2)  as km_disperso,
       trunc(((case
                   when dados.km_planejado_total > 0
                       then (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
                   else 0 end) * 100)::numeric, 2) || '% '                        as resultado_disp_km,
       trunc((dados.meta_dispersao_km * 100)::numeric, 2) || '%'                  as meta_disp_km,
       case
           when (case
                     when dados.km_planejado_total > 0
                         then (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
                     else 0 end) <= dados.meta_dev_pdv
               then 'SIM'
           else 'NÃO' end                                                         as bateu_disp_km,
       dados.gols_dispersao_km                                                    as gols_dispersao_km,
       dados.media_tempo_largada                                                  as media_tempo_largada,
       dados.total_mapas_validos_tempo_largada                                    as total_mapas_validos_tempo_largada,
       dados.total_mapas_bateu_tempo_largada                                      as total_mapas_bateu_tempo_largada,
       case
           when dados.total_mapas_validos_tempo_largada > 0 then
               trunc(((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) *
                      100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_tempo_largada,
       trunc((dados.meta_tempo_largada_mapas * 100)::numeric, 2) || '%'           as meta_tempo_largada,
       case
           when dados.total_mapas_validos_tempo_largada > 0 and
                ((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) >=
                 dados.meta_tempo_largada_mapas)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_tempo_largada,
       dados.gols_tempo_largada                                                   as gols_tempo_largada,
       dados.media_tempo_rota                                                     as media_tempo_rota,
       dados.total_mapas                                                          as total_mapas,
       dados.total_mapas_bateu_tempo_rota                                         as total_mapas_bateu_tempo_rota,
       case
           when dados.total_mapas > 0 then
               trunc(((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_tempo_rota,
       trunc((dados.meta_tempo_rota_mapas * 100)::numeric, 2) || '%'              as meta_tempo_rota,
       case
           when dados.total_mapas > 0 and
                ((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float) >= dados.meta_tempo_rota_mapas)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_tempo_rota,
       dados.gols_tempo_rota                                                      as gols_tempo_rota,
       dados.media_tempo_interno                                                  as media_tempo_interno,
       dados.total_mapas_validos_tempo_interno                                    as total_mapas_validos_tempo_interno,
       dados.total_mapas_bateu_tempo_interno                                      as total_mapas_bateu_tempo_interno,
       case
           when dados.total_mapas_validos_tempo_interno > 0 then
               trunc(((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float) *
                      100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_tempo_interno,
       trunc((dados.meta_tempo_interno_mapas * 100)::numeric, 2) || '%'           as meta_tempo_interno_mapas,
       case
           when dados.total_mapas_validos_tempo_interno > 0 and
                ((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float) >=
                 dados.meta_tempo_interno_mapas) then 'SIM'
           else 'NÃO' end                                                         as bateu_tempo_interno,
       dados.gols_tempo_interno                                                   as gols_tempo_interno,
       dados.media_jornada                                                        as media_jornada,
       dados.total_mapas                                                          as total_mapas,
       dados.total_mapas_bateu_jornada                                            as total_mapas_bateu_jornada,
       case
           when dados.total_mapas > 0 then
               trunc(((dados.total_mapas_bateu_jornada / dados.total_mapas::float) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_jornada,
       trunc((dados.meta_jornada_liquida_mapas * 100)::numeric, 2) || '%'         as meta_jornada_liquida_mapas,
       case
           when dados.total_mapas > 0 and
                ((dados.total_mapas_bateu_jornada / dados.total_mapas::float) >= dados.meta_jornada_liquida_mapas)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_jornada,
       dados.gols_jornada                                                         as gols_jornada,
       dados.media_tempo_planejado                                                as media_tempo_planejado,
       dados.total_mapas                                                          as total_mapas,
       dados.total_mapas_bateram_dispersao_tempo                                  as total_mapas_bateram_dispersao_tempo,
       case
           when dados.total_mapas > 0 then
               trunc(((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_dispersao_tempo,
       trunc((dados.meta_dispersao_tempo * 100)::numeric, 2) || '%'               as meta_dispersao_tempo,
       case
           when dados.total_mapas > 0 and
                ((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float) >= dados.meta_dispersao_tempo)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_dispersao_tempo,
       dados.gols_dispersao_tempo                                                 as gols_dispersao_tempo,
       dados.total_apontamentos                                                   as total_apontamentos,
       dados.total_apontamentos_ok                                                as total_apontamentos_ok,
       dados.total_apontamentos - dados.total_apontamentos_ok                     as total_apontamentos_nok,
       case
           when dados.total_apontamentos > 0 then
               trunc(((dados.total_apontamentos_ok / dados.total_apontamentos::float) * 100)::numeric, 2)
           else 0 end || '%'                                                      as resultado_tracking,
       trunc(trunc((dados.meta_tracking)::numeric, 3) * 100, 2) || '%'            as meta_tracking,
       case
           when dados.total_apontamentos > 0 and
                ((dados.total_apontamentos_ok / dados.total_apontamentos::float) >= dados.meta_tracking)
               then 'SIM'
           else 'NÃO' end                                                         as bateu_tracking,
       dados.gols_tracking                                                        as gols_tracking,
       (dados.gols_dev_nf +
        dados.gols_dev_pdv +
        dados.gols_dev_hl +
        dados.gols_tracking +
        dados.gols_dispersao_tempo +
        dados.gols_jornada +
        dados.gols_tempo_interno +
        dados.gols_tempo_largada +
        dados.gols_tempo_rota +
        dados.gols_dispersao_km)                                                  as total_gols
from (select m.nome                                                                                  as nome,
             m.equipe                                                                                as equipe,
             m.funcao                                                                                as funcao,
             count(m.mapa)                                                                           as total_mapas,
             -- Dev Hl.
             sum(m.qthlcarregados)                                                                   as hl_carregados_total,
             sum(qthlcarregados - qthlentregues)                                                     as hl_devolvidos_total,
             -- Dev Nf.
             sum(m.qtnfcarregadas)                                                                   as nf_carregadas_total,
             sum(qtnfcarregadas - qtnfentregues)                                                     as nf_devolvidas_total,
             -- Dev Pdv.
             sum(
                     m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais)             as pdv_carregados_total,
             sum(m.entregasnaorealizadas + m.entregasparciais)                                       as pdv_devolvidos_total,
             -- Dispersão Km.
             sum(case
                     when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad
                     else 0 end)                                                                     as km_planejado_total,
             sum(case
                     when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)
                     else 0 end)                                                                     as km_percorrido_total,
             -- Dispersão de tempo.
             sum(case
                     when m.bateu_dispersao_tempo = 'SIM' then 1
                     else 0 end)                                                                     as total_mapas_bateram_dispersao_tempo,
             to_char((avg(m.tempoprevistoroad) || ' second')::interval,
                     'HH24:MI:SS')                                                                   as media_tempo_planejado,
             -- Jornada.
             to_char((avg(m.RESULTADO_tempo_largada_SEGUNDOS + m.RESULTADO_TEMPO_ROTA_SEGUNDOS +
                          m.RESULTADO_TEMPO_INTERNO_SEGUNDOS) || ' second')::interval, 'HH24:MI:SS') as media_jornada,
             sum(case when m.bateu_jornada = 'SIM' then 1 else 0 end)                                as total_mapas_bateu_jornada,
             -- Tempo Interno.
             sum(case when m.bateu_tempo_interno = 'SIM' then 1 else 0 end)                          as total_mapas_bateu_tempo_interno,
             sum(case
                     when to_char((m.tempo_interno || ' second')::interval, 'HH24:MI:SS')::TIME <= '05:00' and
                          m.resultado_tempo_interno_segundos > 0 then 1
                     else 0
                 end)                                                                                as total_mapas_validos_tempo_interno,
             to_char((avg(m.resultado_tempo_interno_segundos) || ' second')::interval,
                     'HH24:MI:SS')                                                                   as media_tempo_interno,
             -- Tempo largada.
             sum(case when m.bateu_tempo_largada = 'SIM' then 1 else 0 end)                          as total_mapas_bateu_tempo_largada,
             sum(case
                     when
                             (case
                                  when m.hr_sai::time < m.hrmatinal then to_char(
                                          (M.meta_tempo_largada_horas || ' second')::interval, 'HH24:MI:SS')::time
                                  else (m.hr_sai - m.hrmatinal)::time
                                 end) <= '05:00' then 1
                     else 0 end)                                                                     as total_mapas_validos_tempo_largada,
             to_char((AVG(resultado_tempo_largada_segundos) || ' second')::interval,
                     'HH24:MI:SS')                                                                   as media_tempo_largada,
             -- Tempo Rota.
             sum(case when m.bateu_tempo_rota = 'SIM' then 1 else 0 end)                             as total_mapas_bateu_tempo_rota,
             to_char((avg(m.resultado_tempo_rota_segundos) || ' second')::interval,
                     'HH24:MI:SS')                                                                   as media_tempo_rota,
             -- Tracking.
             sum(M.apontamentos_ok)                                                                  as total_apontamentos_ok,
             sum(M.total_tracking)                                                                   as total_apontamentos,
             m.meta_tracking                                                                         as meta_tracking,
             m.meta_tempo_rota_mapas                                                                 as meta_tempo_rota_mapas,
             m.meta_dev_hl                                                                           as meta_dev_hl,
             m.meta_dev_pdv                                                                          as meta_dev_pdv,
             m.meta_dispersao_km                                                                     as meta_dispersao_km,
             m.meta_dispersao_tempo                                                                  as meta_dispersao_tempo,
             m.meta_jornada_liquida_mapas                                                            as meta_jornada_liquida_mapas,
             m.meta_tempo_interno_mapas                                                              as meta_tempo_interno_mapas,
             m.meta_tempo_largada_mapas                                                              as meta_tempo_largada_mapas,
             m.meta_dev_nf                                                                           as meta_dev_nf,
             sum(m.gol_dev_pdv)                                                                      as gols_dev_pdv,
             sum(m.gol_dev_nf)                                                                       as gols_dev_nf,
             sum(m.gol_dev_hl)                                                                       as gols_dev_hl,
             sum(m.gol_jornada)                                                                      as gols_jornada,
             sum(m.gol_tempo_interno)                                                                as gols_tempo_interno,
             sum(m.gol_tempo_rota)                                                                   as gols_tempo_rota,
             sum(m.gol_dispersao_tempo)                                                              as gols_dispersao_tempo,
             sum(m.gol_dispersao_km)                                                                 as gols_dispersao_km,
             sum(m.gol_tracking)                                                                     as gols_tracking,
             sum(m.gol_tempo_largada)                                                                as gols_tempo_largada
      from view_extrato_indicadores m
      where m.cod_empresa = f_cod_empresa
        and case when f_cod_regional is null then true else m.cod_regional = f_cod_regional end
        and case when f_cod_unidade is null then true else m.cod_unidade = f_cod_unidade end
        and case when f_cod_equipe is null then true else m.cod_equipe = f_cod_equipe end
        and case when f_cpf is null then true else m.cpf = f_cpf end
        and m.data between f_data_inicial and f_data_final
      group by m.equipe, m.cpf, m.nome, m.cod_unidade, m.meta_tracking,
               m.meta_tempo_rota_horas, m.meta_tempo_rota_mapas, m.meta_caixa_viagem, m.meta_dev_hl, m.meta_dev_pdv,
               m.meta_dispersao_km, m.meta_dispersao_tempo, m.meta_jornada_liquida_horas, m.meta_jornada_liquida_mapas,
               m.meta_raio_tracking, m.meta_tempo_interno_horas, m.meta_tempo_interno_mapas, m.meta_tempo_largada_horas,
               m.meta_tempo_largada_mapas, m.meta_dev_nf, m.funcao) as dados
order by total_gols desc
$$;