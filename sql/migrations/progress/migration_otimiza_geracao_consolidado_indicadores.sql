drop view view_extrato_indicadores;
create or replace view view_extrato_indicadores as
with internal_tracking as (
    select t.mapa                                                                 as tracking_mapa,
           t.cod_unidade                                                          as cod_unidade,
           sum(1) filter (where t.disp_apont_cadastrado <= um.meta_raio_tracking) as apontamentos_ok,
           count(t.disp_apont_cadastrado)                                         as total_apontamentos
    from tracking t
             join unidade_metas um on um.cod_unidade = t.cod_unidade
    group by t.mapa, t.cod_unidade
)
select dados.cod_unidade,
       dados.cod_equipe,
       dados.cpf,
       dados.nome,
       dados.equipe,
       dados.funcao,
       dados.data,
       dados.mapa,
       dados.placa,
       dados.cxcarreg,
       dados.qthlcarregados,
       dados.qthlentregues,
       dados.qthldevolvidos,
       dados.resultado_devolucao_hectolitro,
       dados.qtnfcarregadas,
       dados.qtnfentregues,
       dados.qtnfdevolvidas,
       dados.resultado_devolucao_nf,
       dados.entregascompletas,
       dados.entregasnaorealizadas,
       dados.entregasparciais,
       dados.entregas_carregadas,
       dados.resultado_devolucao_pdv,
       dados.kmprevistoroad,
       dados.kmsai,
       dados.kmentr,
       dados.km_percorrido,
       dados.resultado_dispersao_km,
       dados.hrsai,
       dados.hr_sai,
       dados.hrentr,
       dados.hr_entr,
       dados.tempo_rota,
       dados.tempoprevistoroad,
       dados.resultado_tempo_rota_segundos,
       dados.resultado_dispersao_tempo,
       dados.resultado_tempo_interno_segundos,
       dados.tempo_interno,
       dados.hrmatinal,
       dados.resultado_tempo_largada_segundos,
       dados.tempo_largada,
       dados.total_tracking,
       dados.apontamentos_ok,
       dados.apontamentos_nok,
       dados.resultado_tracking,
       dados.meta_tracking,
       dados.meta_tempo_rota_mapas,
       dados.meta_caixa_viagem,
       dados.meta_dev_hl,
       dados.meta_dev_pdv,
       dados.meta_dev_nf,
       dados.meta_dispersao_km,
       dados.meta_dispersao_tempo,
       dados.meta_jornada_liquida_mapas,
       dados.meta_raio_tracking,
       dados.meta_tempo_interno_mapas,
       dados.meta_tempo_largada_mapas,
       dados.meta_tempo_rota_horas,
       dados.meta_tempo_interno_horas,
       dados.meta_tempo_largada_horas,
       dados.meta_jornada_liquida_horas,
       case
           when ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_dev_pdv,
       case
           when ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_dev_hl,
       case
           when ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_dev_nf,
       case
           when (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_dispersao_tempo,
       case
           when ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_dispersao_km,
       case
           when (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision)
               then 'SIM'::text
           else 'NÃO'::text
           end as bateu_tempo_interno,
       case
           when (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_tempo_rota,
       case
           when (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision)
               then 'SIM'::text
           else 'NÃO'::text
           end as bateu_tempo_largada,
       case
           when ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) or
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas)::double precision)) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_jornada,
       case
           when ((dados.resultado_tracking)::double precision >= dados.meta_tracking) then 'SIM'::text
           else 'NÃO'::text
           end as bateu_tracking,
       case
           when ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) then 1
           else 0
           end as gol_dev_pdv,
       case
           when ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) then 1
           else 0
           end as gol_dev_hl,
       case
           when ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) then 1
           else 0
           end as gol_dev_nf,
       case
           when (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) then 1
           else 0
           end as gol_dispersao_tempo,
       case
           when ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) then 1
           else 0
           end as gol_dispersao_km,
       case
           when (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision) then 1
           else 0
           end as gol_tempo_interno,
       case
           when (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) then 1
           else 0
           end as gol_tempo_rota,
       case
           when (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision) then 1
           else 0
           end as gol_tempo_largada,
       case
           when ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) or
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas)::double precision)) then 1
           else 0
           end as gol_jornada,
       case
           when ((dados.resultado_tracking)::double precision >= dados.meta_tracking) then 1
           else 0
           end as gol_tracking
from (select m.cod_unidade                                                       as cod_unidade,
             e.codigo                                                            as cod_equipe,
             c.cpf                                                               as cpf,
             c.nome                                                              as nome,
             e.nome                                                              as equipe,
             f.nome                                                              as funcao,
             m.data                                                              as data,
             m.mapa                                                              as mapa,
             m.placa                                                             as placa,
             m.cxcarreg                                                          as cxcarreg,
             m.qthlcarregados                                                    as qthlcarregados,
             m.qthlentregues                                                     as qthlentregues,
             trunc(((m.qthlcarregados - m.qthlentregues))::numeric, 2)           as qthldevolvidos,
             trunc((
                       case
                           when (m.qthlcarregados > (0)::double precision)
                               then ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                           else (0)::real
                           end)::numeric,
                   4)                                                            as resultado_devolucao_hectolitro,
             m.qtnfcarregadas,
             m.qtnfentregues,
             (m.qtnfcarregadas - m.qtnfentregues)                                as qtnfdevolvidas,
             trunc((
                       case
                           when (m.qtnfcarregadas > 0) then (((m.qtnfcarregadas - m.qtnfentregues))::double precision /
                                                             (m.qtnfcarregadas)::real)
                           else (0)::double precision
                           end)::numeric, 4)                                     as resultado_devolucao_nf,
             m.entregascompletas,
             m.entregasnaorealizadas,
             m.entregasparciais,
             (m.entregascompletas + m.entregasnaorealizadas)                     as entregas_carregadas,
             trunc((
                       case
                           when (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais) > 0) then (
                                   ((m.entregasnaorealizadas)::real + (m.entregasparciais)::double precision) /
                                   (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais))::double precision)
                           else (0)::double precision
                           end)::numeric, 4)                                     as resultado_devolucao_pdv,
             m.kmprevistoroad,
             m.kmsai,
             m.kmentr,
             (m.kmentr - m.kmsai)                                                as km_percorrido,
             case
                 when (m.kmprevistoroad > (0)::double precision) then trunc(
                         (((((m.kmentr - m.kmsai))::double precision - m.kmprevistoroad) / m.kmprevistoroad))::numeric,
                         4)
                 else null::numeric
                 end                                                             as resultado_dispersao_km,
             to_char(m.hrsai, 'DD/MM/YYYY HH24:MI:SS'::text)                     as hrsai,
             m.hrsai                                                             as hr_sai,
             to_char(m.hrentr, 'DD/MM/YYYY HH24:MI:SS'::text)                    as hrentr,
             m.hrentr                                                            as hr_entr,
             to_char((m.hrentr - m.hrsai), 'HH24:MI:SS'::text)                   as tempo_rota,
             date_part('epoch'::text, m.tempoprevistoroad)                       as tempoprevistoroad,
             date_part('epoch'::text, (m.hrentr - m.hrsai))                      as resultado_tempo_rota_segundos,
             case
                 when (date_part('epoch'::text, m.tempoprevistoroad) > (0)::double precision) then (
                         (date_part('epoch'::text, (m.hrentr - m.hrsai)) -
                          date_part('epoch'::text, m.tempoprevistoroad)) /
                         date_part('epoch'::text, m.tempoprevistoroad))
                 else (0)::double precision
                 end                                                             as resultado_dispersao_tempo,
             date_part('epoch'::text, m.tempointerno)                            as resultado_tempo_interno_segundos,
             m.tempointerno                                                      as tempo_interno,
             m.hrmatinal,
             date_part('epoch'::text,
                       case
                           when ((m.hrsai)::time without time zone < m.hrmatinal) then um.meta_tempo_largada_horas
                           else ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                           end)                                                  as resultado_tempo_largada_segundos,
             case
                 when ((m.hrsai)::time without time zone < m.hrmatinal) then um.meta_tempo_largada_horas
                 else ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                 end                                                             as tempo_largada,
             coalesce(it.total_apontamentos, (0)::bigint)                        as total_tracking,
             coalesce(it.apontamentos_ok, (0)::bigint)                           as apontamentos_ok,
             coalesce((it.total_apontamentos - it.apontamentos_ok), (0)::bigint) as apontamentos_nok,
             case
                 when (it.total_apontamentos > 0) then (it.apontamentos_ok / it.total_apontamentos)
                 else (0)::bigint
                 end                                                             as resultado_tracking,
             um.meta_tracking                                                    as meta_tracking,
             um.meta_tempo_rota_mapas                                            as meta_tempo_rota_mapas,
             um.meta_caixa_viagem                                                as meta_caixa_viagem,
             um.meta_dev_hl                                                      as meta_dev_hl,
             um.meta_dev_pdv                                                     as meta_dev_pdv,
             um.meta_dev_nf                                                      as meta_dev_nf,
             um.meta_dispersao_km                                                as meta_dispersao_km,
             um.meta_dispersao_tempo                                             as meta_dispersao_tempo,
             um.meta_jornada_liquida_mapas                                       as meta_jornada_liquida_mapas,
             um.meta_raio_tracking                                               as meta_raio_tracking,
             um.meta_tempo_interno_mapas                                         as meta_tempo_interno_mapas,
             um.meta_tempo_largada_mapas                                         as meta_tempo_largada_mapas,
             to_seconds(m.hrmetajornada - interval '1 hour')                     as meta_tempo_rota_horas,
             to_seconds(um.meta_tempo_interno_horas)                             as meta_tempo_interno_horas,
             to_seconds(um.meta_tempo_largada_horas)                             as meta_tempo_largada_horas,
             to_seconds(um.meta_jornada_liquida_horas)                           as meta_jornada_liquida_horas
      from view_mapa_colaborador vmc
               join colaborador c on c.cpf = vmc.cpf and c.cod_unidade = vmc.cod_unidade
               join mapa m on m.cod_unidade = vmc.cod_unidade and m.mapa = vmc.mapa
               join unidade_metas um on um.cod_unidade = m.cod_unidade
               join equipe e on c.cod_equipe = e.codigo
               join funcao f on f.codigo = c.cod_funcao
               left join internal_tracking it
                         on it.cod_unidade = m.cod_unidade
                             and it.tracking_mapa = m.mapa) dados;

drop function func_relatorio_extrato_mapas_indicadores(f_data_inicial date, f_data_final date, f_cpf text,
    f_cod_unidade text, f_cod_equipe text, f_cod_empresa bigint,
    f_cod_regional text);
create function func_indicador_relatorio_extrato_mapas_indicadores(f_cod_empresa bigint,
                                                                   f_cod_unidade bigint,
                                                                   f_cod_equipe bigint,
                                                                   f_cpf bigint,
                                                                   f_data_inicial date,
                                                                   f_data_final date)
    returns table
            (
                "DATA"                     text,
                "EQUIPE"                   text,
                "NOME"                     text,
                "FUNÇÃO"                   text,
                "MAPA"                     integer,
                "PLACA"                    text,
                "CAIXAS CARREGADAS"        real,
                "HL CARREGADOS"            real,
                "HL ENTREGUES"             real,
                "HL DEVOLVIDOS"            numeric,
                "RESULTADO DEV HL"         text,
                "META DEV HL"              text,
                "BATEU DEV HL"             text,
                "NF CARREGADAS"            integer,
                "NF ENTREGUES"             integer,
                "NF DEVOLVIDAS"            integer,
                "RESULTADO DEV NF"         text,
                "META DEV NF"              text,
                "BATEU DEV NF"             text,
                "ENTREGAS CARREGADAS"      integer,
                "ENTREGAS REALIZADAS"      integer,
                "ENTREGAS DEVOLVIDAS"      integer,
                "RESULTADO DEV PDV"        text,
                "META DEV PDV"             text,
                "BATEU DEV PDV"            text,
                "KM PREVISTO"              real,
                "KM PERCORRIDO"            integer,
                "KM DISPERSO"              numeric,
                "RESULTADO DISP KM"        text,
                "META DISP KM"             text,
                "BATEU DISP KM"            text,
                "HORARIO MATINAL"          time without time zone,
                "HORARIO SAÍDA"            text,
                "TEMPO DE LARGADA"         time without time zone,
                "META TEMPO DE LARGADA"    text,
                "BATEU TML"                text,
                "HR SAÍDA"                 text,
                "HR ENTR"                  text,
                "TEMPO EM ROTA"            text,
                "META TEMPO EM ROTA"       text,
                "BATEU TEMPO EM ROTA"      text,
                "TEMPO INTERNO"            text,
                "META TEMPO INTERNO"       text,
                "BATEU TEMPO INTERNO"      text,
                "JORNADA"                  text,
                "META JORNADA"             text,
                "BATEU JORNADA"            text,
                "TEMPO PREVISTO"           text,
                "TEMPO REALIZADO"          text,
                "DISPERSÃO"                text,
                "META DISPERSÃO DE TEMPO"  text,
                "BATEU DISPERSÃO DE TEMPO" text,
                "TOTAL ENTREGAS"           bigint,
                "APONTAMENTOS OK"          bigint,
                "APONTAMENTOS NOK"         bigint,
                "ADERENCIA TRACKING"       text,
                "META TRACKING"            text,
                "BATEU TRACKING"           text,
                "TOTAL DE GOLS"            integer
            )
    language sql
as
$$
select to_char(v.data, 'DD/MM/YYYY'),
       v.equipe,
       v.nome,
       v.funcao,
       v.mapa,
       v.placa,
       v.cxcarreg,
       v.qthlcarregados,
       v.qthlentregues,
       v.qthldevolvidos,
       trunc((v.resultado_devolucao_hectolitro * 100)::numeric, 2) || '%',
       trunc((v.meta_dev_hl * 100)::numeric, 2) || '%',
       v.bateu_dev_hl,
       v.qtnfcarregadas,
       v.qtnfentregues,
       v.qtnfdevolvidas,
       trunc((v.resultado_devolucao_nf * 100)::numeric, 2) || '%',
       trunc((v.meta_dev_nf * 100)::numeric, 2) || '%',
       v.bateu_dev_nf,
       v.entregas_carregadas,
       v.entregascompletas,
       v.entregasparciais + v.entregasnaorealizadas,
       trunc((v.resultado_devolucao_pdv * 100)::numeric, 2) || '%',
       trunc((v.meta_dev_pdv * 100)::numeric, 2) || '%',
       v.bateu_dev_pdv,
       v.kmprevistoroad,
       v.km_percorrido,
       trunc((v.km_percorrido - v.kmprevistoroad)::numeric, 2),
       trunc((v.resultado_dispersao_km * 100)::numeric, 2) || '%',
       trunc((v.meta_dispersao_km * 100)::numeric, 2) || '%',
       v.bateu_dispersao_km,
       v.hrmatinal,
       v.hrsai,
       v.tempo_largada,
       to_char((v.meta_tempo_largada_horas || ' second')::interval, 'HH24:MI:SS'),
       v.bateu_tempo_largada,
       v.hrsai,
       v.hrentr,
       v.tempo_rota,
       to_char((v.meta_tempo_rota_horas || ' second')::interval, 'HH24:MI:SS'),
       v.bateu_tempo_rota,
       case
           when v.resultado_tempo_interno_segundos > 0 then
               to_char((v.resultado_tempo_interno_segundos || ' second')::interval, 'HH24:MI:SS')
           else 0::text end,
       case
           when v.meta_tempo_interno_horas > 0 then
               to_char((v.meta_tempo_interno_horas || ' second')::interval, 'HH24:MI:SS')
           else 0::text end,
       v.bateu_tempo_interno,
       case
           when (v.resultado_tempo_interno_segundos + v.resultado_tempo_largada_segundos +
                 v.resultado_tempo_rota_segundos) > 0 then
               to_char(((v.resultado_tempo_interno_segundos + v.resultado_tempo_largada_segundos +
                         v.resultado_tempo_rota_segundos) || ' second')::interval, 'HH24:MI:SS')
           else 0::text end,
       case
           when v.meta_jornada_liquida_horas > 0 then
               to_char((v.meta_jornada_liquida_horas || ' second')::interval, 'HH24:MI:SS')
           else 0::text end,
       v.bateu_jornada,
       case
           when v.tempoprevistoroad > 0 then
               to_char((v.tempoprevistoroad || ' second')::interval, 'HH24:MI:SS')
           else 0::text end,
       v.tempo_rota,
       trunc((v.resultado_dispersao_tempo * 100)::numeric, 2) || '%',
       trunc(trunc((v.meta_dispersao_tempo)::numeric, 3) * 100, 2) || '%',
       v.bateu_dispersao_tempo,
       v.total_tracking,
       v.apontamentos_ok,
       v.apontamentos_nok,
       trunc((v.resultado_tracking * 100)::numeric, 2) || '%',
       trunc(trunc((v.meta_tracking)::numeric, 3) * 100, 2) || '%',
       v.bateu_tracking,
       (v.gol_dev_nf +
        v.gol_dev_pdv +
        v.gol_dev_hl +
        v.gol_tracking +
        v.gol_dispersao_tempo +
        v.gol_jornada +
        v.gol_tempo_interno +
        v.gol_tempo_largada +
        v.gol_tempo_rota +
        v.gol_dispersao_km) as total_gols
from view_extrato_indicadores v
where case
          when f_cod_unidade is null
              then v.cod_unidade in (select u.codigo from unidade u where u.cod_empresa = f_cod_empresa)
          else v.cod_unidade = f_cod_unidade
    end
  and v.data between f_data_inicial and f_data_final
  and case when f_cod_equipe is null then true else v.cod_equipe = f_cod_equipe end
  and case when f_cpf is null then true else v.cpf = f_cpf end
$$;

drop function func_indicador_relatorio_consolidado_mapas_indicadores(f_cod_empresa bigint,
    f_cod_regional bigint,
    f_cod_unidade bigint,
    f_cod_equipe bigint,
    f_cpf bigint,
    f_data_inicial date,
    f_data_final date);
create or replace function func_indicador_relatorio_consolidado_mapas_indicadores(f_cod_empresa bigint,
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
with dados as (
    select m.nome                                                                                  as nome,
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
           to_char((avg(m.resultado_tempo_largada_segundos + m.resultado_tempo_rota_segundos +
                        m.resultado_tempo_interno_segundos) || ' second')::interval, 'HH24:MI:SS') as media_jornada,
           sum(case when m.bateu_jornada = 'SIM' then 1 else 0 end)                                as total_mapas_bateu_jornada,
           -- Tempo Interno.
           sum(case when m.bateu_tempo_interno = 'SIM' then 1 else 0 end)                          as total_mapas_bateu_tempo_interno,
           sum(case
                   when to_char((m.tempo_interno || ' second')::interval, 'HH24:MI:SS')::time <= '05:00' and
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
                                        (m.meta_tempo_largada_horas || ' second')::interval, 'HH24:MI:SS')::time
                                else (m.hr_sai - m.hrmatinal)::time
                               end) <= '05:00' then 1
                   else 0 end)                                                                     as total_mapas_validos_tempo_largada,
           to_char((avg(resultado_tempo_largada_segundos) || ' second')::interval,
                   'HH24:MI:SS')                                                                   as media_tempo_largada,
           -- Tempo Rota.
           sum(case when m.bateu_tempo_rota = 'SIM' then 1 else 0 end)                             as total_mapas_bateu_tempo_rota,
           to_char((avg(m.resultado_tempo_rota_segundos) || ' second')::interval,
                   'HH24:MI:SS')                                                                   as media_tempo_rota,
           -- Tracking.
           sum(m.apontamentos_ok)                                                                  as total_apontamentos_ok,
           sum(m.total_tracking)                                                                   as total_apontamentos,
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
    where case
              when f_cod_unidade is null
                  then m.cod_unidade in (select u.codigo
                                         from unidade u
                                         where u.cod_empresa = f_cod_empresa
                                           and status_ativo is true)
              else m.cod_unidade = f_cod_unidade
        end
      and m.data between f_data_inicial and f_data_final
      and case when f_cod_equipe is null then true else m.cod_equipe = f_cod_equipe end
      and case when f_cpf is null then true else m.cpf = f_cpf end
    group by m.equipe, m.cpf, m.nome, m.cod_unidade, m.meta_tracking,
             m.meta_tempo_rota_horas, m.meta_tempo_rota_mapas, m.meta_caixa_viagem, m.meta_dev_hl, m.meta_dev_pdv,
             m.meta_dispersao_km, m.meta_dispersao_tempo, m.meta_jornada_liquida_horas, m.meta_jornada_liquida_mapas,
             m.meta_raio_tracking, m.meta_tempo_interno_horas, m.meta_tempo_interno_mapas, m.meta_tempo_largada_horas,
             m.meta_tempo_largada_mapas, m.meta_dev_nf, m.funcao
)
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
from dados
order by total_gols desc
$$;