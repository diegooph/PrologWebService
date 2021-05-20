create or replace view view_extrato_indicadores as
with internal_tracking as (
    select t.mapa                                                                 as tracking_mapa,
           t."código_transportadora"                                              as cod_unidade,
           sum(1) filter (where t.disp_apont_cadastrado <= um.meta_raio_tracking) as apontamentos_ok,
           count(t.disp_apont_cadastrado)                                         as total_apontamentos
    from tracking t
             join unidade_metas um on um.cod_unidade = t."código_transportadora"
    group by t.mapa, t."código_transportadora"
)
SELECT dados.cod_empresa,
       dados.cod_regional,
       dados.cod_unidade,
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
           END AS bateu_dev_nf,
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
from (select u.cod_empresa                                             as cod_empresa,
             u.cod_regional                                            as cod_regional,
             u.codigo                                                  as cod_unidade,
             e.codigo                                                  as cod_equipe,
             c.cpf                                                     as cpf,
             c.nome                                                    as nome,
             e.nome                                                    as equipe,
             f.nome                                                    as funcao,
             m.data                                                    as data,
             m.mapa                                                    as mapa,
             m.placa                                                   as placa,
             m.cxcarreg                                                as cxcarreg,
             m.qthlcarregados                                          as qthlcarregados,
             m.qthlentregues                                           as qthlentregues,
             trunc(((m.qthlcarregados - m.qthlentregues))::numeric, 2) as qthldevolvidos,
             trunc((
                       case
                           when (m.qthlcarregados > (0)::double precision)
                               then ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                           else (0)::real
                           end)::numeric,
                   4)                                                  as resultado_devolucao_hectolitro,
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
               join unidade u on u.codigo = m.cod_unidade
               join regional r on r.codigo = u.cod_regional
               join unidade_metas um on um.cod_unidade = u.codigo
               join equipe e on c.cod_equipe = e.codigo
               join funcao f on f.codigo = c.cod_funcao
               left join internal_tracking it
                         on it.tracking_mapa = m.mapa
                             and it.cod_unidade = m.cod_unidade
      order by m.data) dados;