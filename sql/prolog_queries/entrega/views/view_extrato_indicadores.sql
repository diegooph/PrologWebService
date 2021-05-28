-- Sobre:
--
-- A ambev adicionou um novo modelo de jornada, chamado de jornada alternativa.
-- Essa jornada consiste em uma escala diferente, que faz com que o tempo de jornada seja alterado, com isso, a meta
-- também foi ajustada.
-- Antes era utilizada a coluna "meta_tempo_rota_horas" da tabela "unidade_metas", agora utiliza-se a coluna
-- "hrmetajornada" da própria tabela "mapa", com o desconto de 1 hora do valor.
--
-- O desconto de 1 hora serve para termos a meta de tempo em rota, sendo que a coluna tem os dados da meta de
-- jornada líquida, então meta jornada líquida -1h = meta tempo em rota.
create or replace view view_extrato_indicadores as
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
       CASE
           WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_dev_pdv,
       CASE
           WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_dev_hl,
       CASE
           WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_dev_nf,
       CASE
           WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_dispersao_tempo,
       CASE
           WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_dispersao_km,
       CASE
           WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision)
               THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_tempo_interno,
       CASE
           WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_tempo_rota,
       CASE
           WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision)
               THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_tempo_largada,
       CASE
           WHEN ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) OR
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas)::double precision)) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_jornada,
       CASE
           WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 'SIM'::text
           ELSE 'NÃO'::text
           END AS bateu_tracking,
       CASE
           WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 1
           ELSE 0
           END AS gol_dev_pdv,
       CASE
           WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 1
           ELSE 0
           END AS gol_dev_hl,
       CASE
           WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 1
           ELSE 0
           END AS gol_dev_nf,
       CASE
           WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 1
           ELSE 0
           END AS gol_dispersao_tempo,
       CASE
           WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 1
           ELSE 0
           END AS gol_dispersao_km,
       CASE
           WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision) THEN 1
           ELSE 0
           END AS gol_tempo_interno,
       CASE
           WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) THEN 1
           ELSE 0
           END AS gol_tempo_rota,
       CASE
           WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision) THEN 1
           ELSE 0
           END AS gol_tempo_largada,
       CASE
           WHEN ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) OR
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas)::double precision)) THEN 1
           ELSE 0
           END AS gol_jornada,
       CASE
           WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 1
           ELSE 0
           END AS gol_tracking
FROM (SELECT u.cod_empresa,
             u.cod_regional,
             u.codigo                                                                        AS cod_unidade,
             e.codigo                                                                        AS cod_equipe,
             c.cpf,
             c.nome,
             e.nome                                                                          AS equipe,
             f.nome                                                                          AS funcao,
             m.data,
             m.mapa,
             m.placa,
             m.cxcarreg,
             m.qthlcarregados,
             m.qthlentregues,
             trunc(((m.qthlcarregados - m.qthlentregues))::numeric, 2)                       AS qthldevolvidos,
             trunc((
                       CASE
                           WHEN (m.qthlcarregados > (0)::double precision)
                               THEN ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                           ELSE (0)::real
                           END)::numeric,
                   4)                                                                        AS resultado_devolucao_hectolitro,
             m.qtnfcarregadas,
             m.qtnfentregues,
             (m.qtnfcarregadas - m.qtnfentregues)                                            AS qtnfdevolvidas,
             trunc((
                       CASE
                           WHEN (m.qtnfcarregadas > 0) THEN (((m.qtnfcarregadas - m.qtnfentregues))::double precision /
                                                             (m.qtnfcarregadas)::real)
                           ELSE (0)::double precision
                           END)::numeric, 4)                                                 AS resultado_devolucao_nf,
             m.entregascompletas,
             m.entregasnaorealizadas,
             m.entregasparciais,
             (m.entregascompletas + m.entregasnaorealizadas)                                 AS entregas_carregadas,
             trunc((
                       CASE
                           WHEN (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais) > 0) THEN (
                                   ((m.entregasnaorealizadas)::real + (m.entregasparciais)::double precision) /
                                   (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais))::double precision)
                           ELSE (0)::double precision
                           END)::numeric, 4)                                                 AS resultado_devolucao_pdv,
             m.kmprevistoroad,
             m.kmsai,
             m.kmentr,
             (m.kmentr - m.kmsai)                                                            AS km_percorrido,
             CASE
                 WHEN (m.kmprevistoroad > (0)::double precision) THEN trunc(
                         (((((m.kmentr - m.kmsai))::double precision - m.kmprevistoroad) / m.kmprevistoroad))::numeric,
                         4)
                 ELSE NULL::numeric
                 END                                                                         AS resultado_dispersao_km,
             to_char(m.hrsai, 'DD/MM/YYYY HH24:MI:SS'::text)                                 AS hrsai,
             m.hrsai                                                                         AS hr_sai,
             to_char(m.hrentr, 'DD/MM/YYYY HH24:MI:SS'::text)                                AS hrentr,
             m.hrentr                                                                        AS hr_entr,
             to_char((m.hrentr - m.hrsai), 'HH24:MI:SS'::text)                               AS tempo_rota,
             date_part('epoch'::text, m.tempoprevistoroad)                                   AS tempoprevistoroad,
             date_part('epoch'::text, (m.hrentr - m.hrsai))                                  AS resultado_tempo_rota_segundos,
             CASE
                 WHEN (date_part('epoch'::text, m.tempoprevistoroad) > (0)::double precision) THEN (
                         (date_part('epoch'::text, (m.hrentr - m.hrsai)) -
                          date_part('epoch'::text, m.tempoprevistoroad)) /
                         date_part('epoch'::text, m.tempoprevistoroad))
                 ELSE (0)::double precision
                 END                                                                         AS resultado_dispersao_tempo,
             date_part('epoch'::text, m.tempointerno)                                        AS resultado_tempo_interno_segundos,
             m.tempointerno                                                                  AS tempo_interno,
             m.hrmatinal,
             date_part('epoch'::text,
                       CASE
                           WHEN ((m.hrsai)::time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                           ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                           END)                                                              AS resultado_tempo_largada_segundos,
             CASE
                 WHEN ((m.hrsai)::time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                 ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                 END                                                                         AS tempo_largada,
             COALESCE(tracking.total_apontamentos, (0)::bigint)                              AS total_tracking,
             COALESCE(tracking.apontamentos_ok, (0)::bigint)                                 AS apontamentos_ok,
             COALESCE((tracking.total_apontamentos - tracking.apontamentos_ok), (0)::bigint) AS apontamentos_nok,
             CASE
                 WHEN (tracking.total_apontamentos > 0) THEN (tracking.apontamentos_ok / tracking.total_apontamentos)
                 ELSE (0)::bigint
                 END                                                                         AS resultado_tracking,
             um.meta_tracking,
             um.meta_tempo_rota_mapas,
             um.meta_caixa_viagem,
             um.meta_dev_hl,
             um.meta_dev_pdv,
             um.meta_dev_nf,
             um.meta_dispersao_km,
             um.meta_dispersao_tempo,
             um.meta_jornada_liquida_mapas,
             um.meta_raio_tracking,
             um.meta_tempo_interno_mapas,
             um.meta_tempo_largada_mapas,
             to_seconds((m.hrmetajornada - interval '1 hour')::text)                         AS meta_tempo_rota_horas,
             to_seconds((um.meta_tempo_interno_horas)::text)                                 AS meta_tempo_interno_horas,
             to_seconds((um.meta_tempo_largada_horas)::text)                                 AS meta_tempo_largada_horas,
             to_seconds((um.meta_jornada_liquida_horas)::text)                               AS meta_jornada_liquida_horas
      FROM (((((((((view_mapa_colaborador vmc
          JOIN colaborador c ON (((c.cpf = vmc.cpf) AND (c.cod_unidade = vmc.cod_unidade))))
          JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
          JOIN unidade u ON ((u.codigo = m.cod_unidade)))
          JOIN empresa em ON ((em.codigo = u.cod_empresa)))
          JOIN regional r ON ((r.codigo = u.cod_regional)))
          JOIN unidade_metas um ON ((um.cod_unidade = u.codigo)))
          JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
          JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = em.codigo))))
               LEFT JOIN (SELECT t.mapa                         AS tracking_mapa,
                                 t.cod_unidade                  AS tracking_unidade,
                                 count(t.disp_apont_cadastrado) AS total_apontamentos,
                                 sum(
                                         CASE
                                             WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                                             ELSE 0
                                             END)               AS apontamentos_ok
                          FROM (tracking t
                                   JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t.cod_unidade)))
                          GROUP BY t.mapa, t.cod_unidade) tracking
                         ON (((tracking.tracking_mapa = m.mapa) AND (tracking.tracking_unidade = m.cod_unidade))))
      ORDER BY m.data) dados;