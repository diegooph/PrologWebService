-- 2020-09-08 -> Corrige cálculo de tempo de rota (thaisksf - PL-3131).
DROP VIEW IF EXISTS VIEW_PRODUTIVIDADE_EXTRATO;
create or replace view view_produtividade_extrato
            (cod_unidade, matricula_ambev, data, cpf, nome_colaborador, data_nascimento,
             funcao, cod_funcao, nome_equipe, fator, cargaatual, entrega, mapa, placa,
             cxcarreg, cxentreg, qthlcarregados, qthlentregues, qtnfcarregadas, qtnfentregues,
             entregascompletas, entregasnaorealizadas, entregasparciais, kmprevistoroad,
             kmsai, kmentr, tempoprevistoroad, hrsai, hrentr, tempo_rota, tempointerno,
             hrmatinal, apontamentos_ok, total_tracking, tempo_largada, meta_tracking,
             meta_tempo_rota_mapas, meta_caixa_viagem, meta_dev_hl, meta_dev_nf, meta_dev_pdv,
             meta_dispersao_km, meta_dispersao_tempo, meta_jornada_liquida_mapas,
             meta_raio_tracking, meta_tempo_interno_mapas, meta_tempo_largada_mapas,
             meta_tempo_rota_horas, meta_tempo_interno_horas, meta_tempo_largada_horas,
             meta_jornada_liquida_horas, valor_rota, valor_recarga, valor_diferenca_eld,
             valor_as, valor)
as
SELECT vmc.cod_unidade,
       c.matricula_ambev,
       m.data,
       vmc.cpf,
       c.nome                                                             AS nome_colaborador,
       c.data_nascimento,
       f.nome                                                             AS funcao,
       f.codigo                                                           AS cod_funcao,
       e.nome                                                             AS nome_equipe,
       m.fator,
       m.cargaatual,
       m.entrega,
       m.mapa,
       m.placa,
       m.cxcarreg,
       m.cxentreg,
       m.qthlcarregados,
       m.qthlentregues,
       m.qtnfcarregadas,
       m.qtnfentregues,
       m.entregascompletas,
       m.entregasnaorealizadas,
       m.entregasparciais,
       m.kmprevistoroad,
       m.kmsai,
       m.kmentr,
       to_seconds((m.tempoprevistoroad)::text)                            AS tempoprevistoroad,
       m.hrsai,
       m.hrentr,
       to_seconds(m.hrentr - m.hrsai)                                     AS tempo_rota,
       to_seconds((m.tempointerno)::text)                                 AS tempointerno,
       m.hrmatinal,
       tracking.apontamentos_ok,
       tracking.total_apontamentos                                        AS total_tracking,
       to_seconds((
           CASE
               WHEN (((m.hrsai)::time without time zone < m.hrmatinal) OR
                     (m.hrmatinal = '00:00:00'::time without time zone)) THEN um.meta_tempo_largada_horas
               ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
               END)::text)                                                AS tempo_largada,
       um.meta_tracking,
       um.meta_tempo_rota_mapas,
       um.meta_caixa_viagem,
       um.meta_dev_hl,
       um.meta_dev_nf,
       um.meta_dev_pdv,
       um.meta_dispersao_km,
       um.meta_dispersao_tempo,
       um.meta_jornada_liquida_mapas,
       um.meta_raio_tracking,
       um.meta_tempo_interno_mapas,
       um.meta_tempo_largada_mapas,
       to_seconds(((m.hrmetajornada - '01:00:00'::interval))::text)       AS meta_tempo_rota_horas,
       to_seconds((um.meta_tempo_interno_horas)::text)                    AS meta_tempo_interno_horas,
       to_seconds((um.meta_tempo_largada_horas)::text)                    AS meta_tempo_largada_horas,
       to_seconds((m.hrmetajornada)::text)                                AS meta_jornada_liquida_horas,

---------------------------------------------------------------------------------------------------------

       -- verifica se o mapa é de DISTRIBUIÇÃO e NÃO é RECARGA
       round(CASE
                 WHEN m.entrega <> 'AS' and m.cargaatual <> 'Recarga' THEN
                     -- verifica o cargo
                     CASE
                         -- motorista
                         WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista THEN
                             m.vlbateujornmot + m.vlnaobateujornmot
                         -- ajudante
                         WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                             (m.vlbateujornaju + m.vlnaobateujornaju) / fator
                         ELSE 0
                         END
                 ELSE 0
                 END::numeric, 2)::real                                   AS valor_rota,

       -- calcula mapas de recarga (somando tanto DISTRIBUIÇÃO quanto AS)
       -- primeiro calculo: mapas de DISTRIBUIÇÃO
       round((CASE
                  WHEN m.entrega <> 'AS' and m.cargaatual = 'Recarga' and m.fator <> 0 THEN
                      -- verifica o cargo
                      CASE
                          -- motorista
                          WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista THEN
                              m.vlrecargamot
                          -- ajudante
                          WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                              m.vlrecargaaju / m.fator
                          ELSE 0
                          END
                  ELSE 0
                  END +
           -- segundo calculo: mapas de AS
              CASE
                  WHEN m.entrega = 'AS' and m.cargaatual = 'Recarga' and fator <> 0 THEN
                      -- verifica se unidade paga por caixa ou por entrega
                      CASE
                          -- unidade paga os mapas de AS por caixa entregue e não por número de entregas
                          WHEN uv.as_paga_por_cx is true THEN
                              --verifica o fator do mapa
                              CASE
                                  WHEN m.fator = 1 THEN
                                      -- verifica o cargo
                                      CASE
                                          -- motorista
                                          WHEN c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista THEN
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator1
                                          -- ajudante
                                          WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator1
                                          ELSE 0
                                          END
                                  WHEN m.fator = 2 THEN
                                      -- verifica o cargo
                                      CASE
                                          -- motorista
                                          WHEN c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista THEN
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator2
                                          -- ajudante
                                          WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator2
                                          ELSE 0
                                          END
                                  ELSE 0
                                  END
                          -- unidade não paga por caixa e sim com base no número de entregas
                          WHEN uv.as_paga_por_cx IS NOT TRUE THEN
                              CASE
                                  -- verifica o cargo
                                  -- motorista
                                  WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      THEN
                                      uv.rm_motorista_valor_as_recarga
                                  -- ajudante
                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                      uv.rm_ajudante_valor_as_recarga
                                  ELSE 0
                                  END
                          ELSE 0
                          END
                  ELSE 0
                  END)::numeric, 2)::real                                 as valor_recarga,

--------------------
-- calcula da diferença a ser paga em mapas com tempo previsto superior a meta
       --verifica se é mapa de DISTRIBUIÇÃO
       round(CASE
                 WHEN m.entrega <> 'AS' and cargaatual <> 'Recarga' and m.classificacao_roadshow <> 'Longa Distância'
                     and (m.tempoprevistoroad > (m.hrmetajornada - '01:00:00'::interval)) and
                      m.hrjornadaliq > m.hrmetajornada THEN
                     CASE
                         -- motorista
                         WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista THEN
                             (m.cxentreg * uv.rm_motorista_rota_jornada) - m.vlnaobateujornmot
                         WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante THEN
                                 (m.cxentreg * uv.rm_ajudante_rota_jornada) - (m.vlnaobateujornaju / m.fator)
                         ELSE 0
                         END
                 ELSE 0
                 END::numeric, 2)::double precision                       as diferenca_eld,
----------------------
       -- calculo dos mapas de AS NÃO RECARGA
       round(CASE
                 WHEN m.entrega = 'AS' and m.cargaatual <> 'Recarga' and fator <> 0 THEN
                     -- verifica se unidade paga por caixa ou por entrega
                     CASE
                         -- unidade paga os mapas de AS por caixa entregue e nõa por número de entregas
                         WHEN uv.as_paga_por_cx is true THEN
                             --verifica o fator do mapa
                             CASE
                                 WHEN m.fator = 1 THEN
                                     -- verifica se bateu jornada
                                     CASE
                                         WHEN m.hrjornadaliq <= m.hrmetajornada THEN
                                             -- verifica o cargo
                                             CASE
                                                 -- motorista
                                                 WHEN c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator1
                                                 -- ajudante
                                                 WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator1
                                                 ELSE 0
                                                 END
                                         WHEN m.hrjornadaliq > m.hrmetajornada THEN
                                             -- verifica o cargo
                                             CASE
                                                 -- motorista
                                                 WHEN c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator1
                                                 -- ajudante
                                                 WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator1
                                                 ELSE 0
                                                 END
                                         ELSE 0
                                         END
                                 WHEN m.fator = 2 THEN
                                     -- verifica se bateu jornada
                                     CASE
                                         WHEN m.hrjornadaliq <= m.hrmetajornada THEN
                                             -- verifica o cargo
                                             CASE
                                                 -- motorista
                                                 WHEN c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator2
                                                 -- ajudante
                                                 WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator2
                                                 ELSE 0
                                                 END
                                         WHEN m.hrjornadaliq > m.hrmetajornada THEN
                                             -- verifica o cargo
                                             CASE
                                                 -- motorista
                                                 WHEN c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator2
                                                 -- ajudante
                                                 WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator2
                                                 ELSE 0
                                                 END
                                         ELSE 0
                                         END
                                 ELSE 0
                                 END
                         -- unidade não paga por caixa e sim com base no número de entregas
                         WHEN uv.as_paga_por_cx is not true THEN
                             CASE
                                 -- verifica o cargo
                                 -- motorista com ajudante
                                 WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 > 0 or m.matricajud2 > 0) THEN
                                     -- verifica o número de entregas
                                     CASE
                                         WHEN (m.entregas = 1) THEN uv.rm_motorista_valor_as_1_entrega
                                         WHEN (m.entregas = 2) THEN uv.rm_motorista_valor_as_2_entregas
                                         WHEN (m.entregas = 3) THEN uv.rm_motorista_valor_as_3_entregas
                                         WHEN (m.entregas > 3) THEN uv.rm_motorista_valor_as_maior_3_entregas
                                         ELSE (0)::real
                                         END
                                 -- motorista sem ajudante
                                 WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 is null or m.matricajud1 <= 0) and
                                      (m.matricajud2 is null or m.matricajud2 <= 0) THEN
                                     -- verifica o número de entregas
                                     uv.rm_motorista_valor_as_sem_ajudante
                                 -- ajudante
                                 WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                     CASE
                                         WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                                         WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                                         WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                                         WHEN (m.entregas > 3) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                         ELSE (0)::real
                                         END
                                 ELSE 0
                                 END
                         ELSE 0
                         END
                 ELSE 0
                 END::numeric, 2)::real                                   as valor_as,
------------------
-- soma de todas as colunas acima (rota + as + recarga + dif. eld)
       -- verifica se o mapa é de DISTRIBUIÇÃO e NÃO é RECARGA
       (round(CASE
                  WHEN m.entrega <> 'AS' and m.cargaatual <> 'Recarga' THEN
                      -- verifica o cargo
                      CASE
                          -- motorista
                          WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista THEN
                              m.vlbateujornmot + m.vlnaobateujornmot
                          -- ajudante
                          WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                              (m.vlbateujornaju + m.vlnaobateujornaju) / fator
                          ELSE 0
                          END
                  ELSE 0
                  END::numeric, 2) +

           -- calcula mapas de recarga (somando tanto DISTRIBUIÇÃO quanto AS)
           -- primeiro calculo: mapas de DISTRIBUIÇÃO
        round((CASE
                   WHEN m.entrega <> 'AS' and m.cargaatual = 'Recarga' and m.fator <> 0 THEN
                       -- verifica o cargo
                       CASE
                           -- motorista
                           WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista THEN
                               m.vlrecargamot
                           -- ajudante
                           WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                               m.vlrecargaaju / m.fator
                           ELSE 0
                           END
                   ELSE 0
                   END +
            -- segundo calculo: mapas de AS
               CASE
                   WHEN m.entrega = 'AS' and m.cargaatual = 'Recarga' and fator <> 0 THEN
                       -- verifica se unidade paga por caixa ou por entrega
                       CASE
                           -- unidade paga os mapas de AS por caixa entregue e não por número de entregas
                           WHEN uv.as_paga_por_cx is true THEN
                               --verifica o fator do mapa
                               CASE
                                   WHEN m.fator = 1 THEN
                                       -- verifica o cargo
                                       CASE
                                           -- motorista
                                           WHEN c.matricula_ambev = m.matricmotorista and
                                                c.cod_funcao = ufp.cod_funcao_motorista THEN
                                               m.cxentreg * uv.rm_motorista_as_recarga_cx_fator1
                                           -- ajudante
                                           WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                               m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator1
                                           ELSE 0
                                           END
                                   WHEN m.fator = 2 THEN
                                       -- verifica o cargo
                                       CASE
                                           -- motorista
                                           WHEN c.matricula_ambev = m.matricmotorista and
                                                c.cod_funcao = ufp.cod_funcao_motorista THEN
                                               m.cxentreg * uv.rm_motorista_as_recarga_cx_fator2
                                           -- ajudante
                                           WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                               m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator2
                                           ELSE 0
                                           END
                                   ELSE 0
                                   END
                           -- unidade não paga por caixa e sim com base no número de entregas
                           WHEN uv.as_paga_por_cx IS NOT TRUE THEN
                               CASE
                                   -- verifica o cargo
                                   -- motorista
                                   WHEN c.matricula_ambev = m.matricmotorista and
                                        c.cod_funcao = ufp.cod_funcao_motorista THEN
                                       uv.rm_motorista_valor_as_recarga
                                   -- ajudante
                                   WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                        c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                       uv.rm_ajudante_valor_as_recarga
                                   ELSE 0
                                   END
                           ELSE 0
                           END
                   ELSE 0
                   END)::numeric, 2) +

           --------------------
-- calcula da diferença a ser paga em mapas com tempo previsto superior a meta (ELD)
           --verifica se é mapa de DISTRIBUIÇÃO
        CASE
            WHEN uv.diferenca_eld_soma_total is false then 0
            else
                round(CASE
                          WHEN m.entrega <> 'AS' and cargaatual <> 'Recarga' and
                               m.classificacao_roadshow <> 'Longa Distância'
                              and (m.tempoprevistoroad > (m.hrmetajornada - '01:00:00'::interval)) and
                               m.hrjornadaliq > m.hrmetajornada THEN
                              CASE
                                  -- motorista
                                  WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      THEN
                                      (m.cxentreg * uv.rm_motorista_rota_jornada) - m.vlnaobateujornmot
                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante THEN
                                          (m.cxentreg * uv.rm_ajudante_rota_jornada) - (m.vlnaobateujornaju / m.fator)
                                  ELSE 0
                                  END
                          ELSE 0
                          END::numeric, 2)
            end +
           ----------------------
           -- calculo dos mapas de AS NÃO RECARGA
        round(CASE
                  WHEN m.entrega = 'AS' and m.cargaatual <> 'Recarga' and fator <> 0 THEN
                      -- verifica se unidade paga por caixa ou por entrega
                      CASE
                          -- unidade paga os mapas de AS por caixa entregue e nõa por número de entregas
                          WHEN uv.as_paga_por_cx is true THEN
                              --verifica o fator do mapa
                              CASE
                                  WHEN m.fator = 1 THEN
                                      -- verifica se bateu jornada
                                      CASE
                                          WHEN m.hrjornadaliq <= m.hrmetajornada THEN
                                              -- verifica o cargo
                                              CASE
                                                  -- motorista
                                                  WHEN c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                      m.cxentreg * uv.rm_motorista_as_cx_jornada_fator1
                                                  -- ajudante
                                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                      m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator1
                                                  ELSE 0
                                                  END
                                          WHEN m.hrjornadaliq > m.hrmetajornada THEN
                                              -- verifica o cargo
                                              CASE
                                                  -- motorista
                                                  WHEN c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                      m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator1
                                                  -- ajudante
                                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                      m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator1
                                                  ELSE 0
                                                  END
                                          ELSE 0
                                          END
                                  WHEN m.fator = 2 THEN
                                      -- verifica se bateu jornada
                                      CASE
                                          WHEN m.hrjornadaliq <= m.hrmetajornada THEN
                                              -- verifica o cargo
                                              CASE
                                                  -- motorista
                                                  WHEN c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                      m.cxentreg * uv.rm_motorista_as_cx_jornada_fator2
                                                  -- ajudante
                                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                      m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator2
                                                  ELSE 0
                                                  END
                                          WHEN m.hrjornadaliq > m.hrmetajornada THEN
                                              -- verifica o cargo
                                              CASE
                                                  -- motorista
                                                  WHEN c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista THEN
                                                      m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator2
                                                  -- ajudante
                                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                                      m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator2
                                                  ELSE 0
                                                  END
                                          ELSE 0
                                          END
                                  ELSE 0
                                  END
                          -- unidade não paga por caixa e sim com base no número de entregas
                          WHEN uv.as_paga_por_cx is not true THEN
                              CASE
                                  -- verifica o cargo
                                  -- motorista com ajudante
                                  WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      and (m.matricajud1 > 0 or m.matricajud2 > 0) THEN
                                      -- verifica o número de entregas
                                      CASE
                                          WHEN (m.entregas = 1) THEN uv.rm_motorista_valor_as_1_entrega
                                          WHEN (m.entregas = 2) THEN uv.rm_motorista_valor_as_2_entregas
                                          WHEN (m.entregas = 3) THEN uv.rm_motorista_valor_as_3_entregas
                                          WHEN (m.entregas > 3) THEN uv.rm_motorista_valor_as_maior_3_entregas
                                          ELSE (0)::real
                                          END
                                  -- motorista sem ajudante
                                  WHEN c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      and (m.matricajud1 is null or m.matricajud1 <= 0) and
                                       (m.matricajud2 is null or m.matricajud2 <= 0) THEN
                                      -- verifica o número de entregas
                                      uv.rm_motorista_valor_as_sem_ajudante
                                  -- ajudante
                                  WHEN (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 THEN
                                      CASE
                                          WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                                          WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                                          WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                                          WHEN (m.entregas > 3) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                          ELSE (0)::real
                                          END
                                  ELSE 0
                                  END
                          ELSE 0
                          END
                  ELSE 0
                  END::numeric, 2))::double precision                     as valor
FROM (((((((((view_mapa_colaborador vmc
    JOIN colaborador_data c ON ((vmc.cpf = c.cpf)))
    JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = c.cod_empresa))))
    JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
    JOIN unidade_metas um ON ((um.cod_unidade = m.cod_unidade)))
    JOIN view_valor_cx_unidade ON ((view_valor_cx_unidade.cod_unidade = m.cod_unidade)))
    JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
    JOIN unidade_funcao_produtividade ufp ON (((ufp.cod_unidade = c.cod_unidade) AND (ufp.cod_unidade = m.cod_unidade))))
    LEFT JOIN unidade_valores_rm uv ON ((uv.cod_unidade = m.cod_unidade)))
         LEFT JOIN (SELECT t.mapa                         AS tracking_mapa,
                           t."código_transportadora"      AS cod_transportadora,
                           sum(
                                   CASE
                                       WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                                       ELSE 0
                                       END)               AS apontamentos_ok,
                           count(t.disp_apont_cadastrado) AS total_apontamentos
                    FROM (tracking t
                             JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
                    GROUP BY t.mapa, t."código_transportadora") tracking
                   ON (((tracking.tracking_mapa = m.mapa) AND (tracking.cod_transportadora = m.cod_unidade))));

-- 2020-09-09 -> Corrige tipagem (thaisksf - PL-3131).
DROP FUNCTION IF EXISTS FUNC_GET_PRODUTIVIDADE_COLABORADOR(F_MES INTEGER, F_ANO INTEGER, F_CPF BIGINT);
-- SELECT * from FUNC_GET_PRODUTIVIDADE_COLABORADOR(8, 2020, 5971089609) where MAPA = 178722;
CREATE OR REPLACE FUNCTION FUNC_GET_PRODUTIVIDADE_COLABORADOR(F_MES INTEGER, F_ANO INTEGER, F_CPF BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE                INTEGER,
                MATRICULA_AMBEV            INTEGER,
                DATA                       DATE,
                CPF                        BIGINT,
                NOME_COLABORADOR           CHARACTER VARYING,
                DATA_NASCIMENTO            DATE,
                FUNCAO                     CHARACTER VARYING,
                COD_FUNCAO                 BIGINT,
                NOME_EQUIPE                CHARACTER VARYING,
                FATOR                      REAL,
                CARGAATUAL                 CHARACTER VARYING,
                ENTREGA                    CHARACTER VARYING,
                MAPA                       INTEGER,
                PLACA                      CHARACTER VARYING,
                CXCARREG                   REAL,
                CXENTREG                   REAL,
                QTHLCARREGADOS             REAL,
                QTHLENTREGUES              REAL,
                QTNFCARREGADAS             INTEGER,
                QTNFENTREGUES              INTEGER,
                ENTREGASCOMPLETAS          INTEGER,
                ENTREGASNAOREALIZADAS      INTEGER,
                ENTREGASPARCIAIS           INTEGER,
                KMPREVISTOROAD             REAL,
                KMSAI                      INTEGER,
                KMENTR                     INTEGER,
                TEMPOPREVISTOROAD          INTEGER,
                HRSAI                      TIMESTAMP WITHOUT TIME ZONE,
                HRENTR                     TIMESTAMP WITHOUT TIME ZONE,
                TEMPO_ROTA                 BIGINT,
                TEMPOINTERNO               INTEGER,
                HRMATINAL                  TIME WITHOUT TIME ZONE,
                APONTAMENTOS_OK            BIGINT,
                TOTAL_TRACKING             BIGINT,
                TEMPO_LARGADA              INTEGER,
                META_TRACKING              REAL,
                META_TEMPO_ROTA_MAPAS      REAL,
                META_CAIXA_VIAGEM          REAL,
                META_DEV_HL                REAL,
                META_DEV_NF                REAL,
                META_DEV_PDV               REAL,
                META_DISPERSAO_KM          REAL,
                META_DISPERSAO_TEMPO       REAL,
                META_JORNADA_LIQUIDA_MAPAS REAL,
                META_RAIO_TRACKING         REAL,
                META_TEMPO_INTERNO_MAPAS   REAL,
                META_TEMPO_LARGADA_MAPAS   REAL,
                META_TEMPO_ROTA_HORAS      INTEGER,
                META_TEMPO_INTERNO_HORAS   INTEGER,
                META_TEMPO_LARGADA_HORAS   INTEGER,
                META_JORNADA_LIQUIDA_HORAS INTEGER,
                VALOR_ROTA                 REAL,
                VALOR_RECARGA              REAL,
                VALOR_DIFERENCA_ELD        DOUBLE PRECISION,
                VALOR_AS                   REAL,
                VALOR                      DOUBLE PRECISION
            )
    LANGUAGE SQL
AS
$$
SELECT *
FROM VIEW_PRODUTIVIDADE_EXTRATO
WHERE DATA BETWEEN FUNC_GET_DATA_INICIO_PRODUTIVIDADE(F_ANO, F_MES, F_CPF, NULL) AND
    FUNC_GET_DATA_FIM_PRODUTIVIDADE(F_ANO, F_MES, F_CPF, NULL)
  AND CPF = F_CPF
ORDER BY DATA ASC
$$;