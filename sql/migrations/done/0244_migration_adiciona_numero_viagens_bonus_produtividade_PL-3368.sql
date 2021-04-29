alter table unidade_valores_rm
    add column rm_numero_viagens smallint default 15;

--? Adiciona informação de RM na view
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
select vmc.cod_unidade,
       c.matricula_ambev,
       m.data,
       vmc.cpf,
       c.nome                                                       as nome_colaborador,
       c.data_nascimento,
       f.nome                                                       as funcao,
       f.codigo                                                     as cod_funcao,
       e.nome                                                       as nome_equipe,
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
       to_seconds((m.tempoprevistoroad)::text)                      as tempoprevistoroad,
       m.hrsai,
       m.hrentr,
       to_seconds(m.hrentr - m.hrsai)                               as tempo_rota,
       to_seconds((m.tempointerno)::text)                           as tempointerno,
       m.hrmatinal,
       tracking.apontamentos_ok,
       tracking.total_apontamentos                                  as total_tracking,
       to_seconds((
           case
               when (((m.hrsai)::time without time zone < m.hrmatinal) or
                     (m.hrmatinal = '00:00:00'::time without time zone)) then um.meta_tempo_largada_horas
               else ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
               end)::text)                                          as tempo_largada,
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
       to_seconds(((m.hrmetajornada - '01:00:00'::interval))::text) as meta_tempo_rota_horas,
       to_seconds((um.meta_tempo_interno_horas)::text)              as meta_tempo_interno_horas,
       to_seconds((um.meta_tempo_largada_horas)::text)              as meta_tempo_largada_horas,
       to_seconds((m.hrmetajornada)::text)                          as meta_jornada_liquida_horas,

---------------------------------------------------------------------------------------------------------

       -- verifica se o mapa é de DISTRIBUIÇÃO e NÃO é RECARGA
       round(case
                 when m.entrega <> 'AS' and m.cargaatual <> 'Recarga' then
                     -- verifica o cargo
                     case
                         -- motorista
                         when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista then
                             m.vlbateujornmot + m.vlnaobateujornmot
                         -- ajudante
                         when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                             (m.vlbateujornaju + m.vlnaobateujornaju) / fator
                         else 0
                         end
                 else 0
                 end::numeric, 2)::real                             as valor_rota,

       -- calcula mapas de recarga (somando tanto DISTRIBUIÇÃO quanto AS)
       -- primeiro calculo: mapas de DISTRIBUIÇÃO
       round((case
                  when m.entrega <> 'AS' and m.cargaatual = 'Recarga' and m.fator <> 0 then
                      -- verifica o cargo
                      case
                          -- motorista
                          when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista then
                              m.vlrecargamot
                          -- ajudante
                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                              m.vlrecargaaju / m.fator
                          else 0
                          end
                  else 0
                  end +
           -- segundo calculo: mapas de AS
              case
                  when m.entrega = 'AS' and m.cargaatual = 'Recarga' and fator <> 0 then
                      -- verifica se unidade paga por caixa ou por entrega
                      case
                          -- unidade paga os mapas de AS por caixa entregue e não por número de entregas
                          when uv.as_paga_por_cx is true then
                              --verifica o fator do mapa
                              case
                                  when m.fator = 1 then
                                      -- verifica o cargo
                                      case
                                          -- motorista
                                          when c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista then
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator1
                                          -- ajudante
                                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator1
                                          else 0
                                          end
                                  when m.fator = 2 then
                                      -- verifica o cargo
                                      case
                                          -- motorista
                                          when c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista then
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator2
                                          -- ajudante
                                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator2
                                          else 0
                                          end
                                  else 0
                                  end
                          -- unidade não paga por caixa e sim com base no número de entregas
                          when uv.as_paga_por_cx is not true then
                              case
                                  -- verifica o cargo
                                  -- motorista
                                  when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      then
                                      uv.rm_motorista_valor_as_recarga
                                  -- ajudante
                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                      uv.rm_ajudante_valor_as_recarga
                                  else 0
                                  end
                          else 0
                          end
                  else 0
                  end)::numeric, 2)::real                           as valor_recarga,

--------------------
-- calcula da diferença a ser paga em mapas com tempo previsto superior a meta
       --verifica se é mapa de DISTRIBUIÇÃO
       round(case
                 when m.entrega <> 'AS' and cargaatual <> 'Recarga' and m.classificacao_roadshow <> 'Longa Distância'
                     and (m.tempoprevistoroad > (m.hrmetajornada - '01:00:00'::interval)) and
                      m.hrjornadaliq > m.hrmetajornada then
                     case
                         -- motorista
                         when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista then
                             (m.cxentreg * uv.rm_motorista_rota_jornada) - m.vlnaobateujornmot
                         when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante then
                                 (m.cxentreg * uv.rm_ajudante_rota_jornada) - (m.vlnaobateujornaju / m.fator)
                         else 0
                         end
                 else 0
                 end::numeric, 2)::double precision                 as diferenca_eld,
----------------------
       -- calculo dos mapas de AS NÃO RECARGA
       round(case
                 when m.entrega = 'AS' and m.cargaatual <> 'Recarga' and fator <> 0 then
                     -- verifica se unidade paga por caixa ou por entrega
                     case
                         -- unidade paga os mapas de AS por caixa entregue e nõa por número de entregas
                         when uv.as_paga_por_cx is true then
                             --verifica o fator do mapa
                             case
                                 when m.fator = 1 then
                                     -- verifica se bateu jornada
                                     case
                                         when m.hrjornadaliq <= m.hrmetajornada then
                                             -- verifica o cargo
                                             case
                                                 -- motorista
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator1
                                                 -- ajudante
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator1
                                                 else 0
                                                 end
                                         when m.hrjornadaliq > m.hrmetajornada then
                                             -- verifica o cargo
                                             case
                                                 -- motorista
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator1
                                                 -- ajudante
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator1
                                                 else 0
                                                 end
                                         else 0
                                         end
                                 when m.fator = 2 then
                                     -- verifica se bateu jornada
                                     case
                                         when m.hrjornadaliq <= m.hrmetajornada then
                                             -- verifica o cargo
                                             case
                                                 -- motorista
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator2
                                                 -- ajudante
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator2
                                                 else 0
                                                 end
                                         when m.hrjornadaliq > m.hrmetajornada then
                                             -- verifica o cargo
                                             case
                                                 -- motorista
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator2
                                                 -- ajudante
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator2
                                                 else 0
                                                 end
                                         else 0
                                         end
                                 else 0
                                 end
                         -- unidade não paga por caixa e sim com base no número de entregas
                         when uv.as_paga_por_cx is not true then
                             case
                                 -- verifica o cargo
                                 -- motorista com ajudante
                                 when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 > 0 or m.matricajud2 > 0) then
                                     -- verifica o número de entregas
                                     case
                                         when (m.entregas = 1) then uv.rm_motorista_valor_as_1_entrega
                                         when (m.entregas = 2) then uv.rm_motorista_valor_as_2_entregas
                                         when (m.entregas = 3) then uv.rm_motorista_valor_as_3_entregas
                                         when (m.entregas > 3) then uv.rm_motorista_valor_as_maior_3_entregas
                                         else (0)::real
                                         end
                                 -- motorista sem ajudante
                                 when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 is null or m.matricajud1 <= 0) and
                                      (m.matricajud2 is null or m.matricajud2 <= 0) then
                                     -- verifica o número de entregas
                                     uv.rm_motorista_valor_as_sem_ajudante
                                 -- ajudante
                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                     case
                                         when (m.entregas = 1) then uv.rm_ajudante_valor_as_1_entrega
                                         when (m.entregas = 2) then uv.rm_ajudante_valor_as_2_entregas
                                         when (m.entregas = 3) then uv.rm_ajudante_valor_as_3_entregas
                                         when (m.entregas > 3) then uv.rm_ajudante_valor_as_maior_3_entregas
                                         else (0)::real
                                         end
                                 else 0
                                 end
                         else 0
                         end
                 else 0
                 end::numeric, 2)::real                             as valor_as,
------------------
-- soma de todas as colunas acima (rota + as + recarga + dif. eld)
       -- verifica se o mapa é de DISTRIBUIÇÃO e NÃO é RECARGA
       (round(case
                  when m.entrega <> 'AS' and m.cargaatual <> 'Recarga' then
                      -- verifica o cargo
                      case
                          -- motorista
                          when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista then
                              m.vlbateujornmot + m.vlnaobateujornmot
                          -- ajudante
                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                              (m.vlbateujornaju + m.vlnaobateujornaju) / fator
                          else 0
                          end
                  else 0
                  end::numeric, 2) +

           -- calcula mapas de recarga (somando tanto DISTRIBUIÇÃO quanto AS)
           -- primeiro calculo: mapas de DISTRIBUIÇÃO
        round((case
                   when m.entrega <> 'AS' and m.cargaatual = 'Recarga' and m.fator <> 0 then
                       -- verifica o cargo
                       case
                           -- motorista
                           when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista then
                               m.vlrecargamot
                           -- ajudante
                           when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                               m.vlrecargaaju / m.fator
                           else 0
                           end
                   else 0
                   end +
            -- segundo calculo: mapas de AS
               case
                   when m.entrega = 'AS' and m.cargaatual = 'Recarga' and fator <> 0 then
                       -- verifica se unidade paga por caixa ou por entrega
                       case
                           -- unidade paga os mapas de AS por caixa entregue e não por número de entregas
                           when uv.as_paga_por_cx is true then
                               --verifica o fator do mapa
                               case
                                   when m.fator = 1 then
                                       -- verifica o cargo
                                       case
                                           -- motorista
                                           when c.matricula_ambev = m.matricmotorista and
                                                c.cod_funcao = ufp.cod_funcao_motorista then
                                               m.cxentreg * uv.rm_motorista_as_recarga_cx_fator1
                                           -- ajudante
                                           when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                               m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator1
                                           else 0
                                           end
                                   when m.fator = 2 then
                                       -- verifica o cargo
                                       case
                                           -- motorista
                                           when c.matricula_ambev = m.matricmotorista and
                                                c.cod_funcao = ufp.cod_funcao_motorista then
                                               m.cxentreg * uv.rm_motorista_as_recarga_cx_fator2
                                           -- ajudante
                                           when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                               m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator2
                                           else 0
                                           end
                                   else 0
                                   end
                           -- unidade não paga por caixa e sim com base no número de entregas
                           when uv.as_paga_por_cx is not true then
                               case
                                   -- verifica o cargo
                                   -- motorista
                                   when c.matricula_ambev = m.matricmotorista and
                                        c.cod_funcao = ufp.cod_funcao_motorista then
                                       uv.rm_motorista_valor_as_recarga
                                   -- ajudante
                                   when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                        c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                       uv.rm_ajudante_valor_as_recarga
                                   else 0
                                   end
                           else 0
                           end
                   else 0
                   end)::numeric, 2) +

           --------------------
-- calcula da diferença a ser paga em mapas com tempo previsto superior a meta (ELD)
           --verifica se é mapa de DISTRIBUIÇÃO
        case
            when uv.diferenca_eld_soma_total is false then 0
            else
                round(case
                          when m.entrega <> 'AS' and cargaatual <> 'Recarga' and
                               m.classificacao_roadshow <> 'Longa Distância'
                              and (m.tempoprevistoroad > (m.hrmetajornada - '01:00:00'::interval)) and
                               m.hrjornadaliq > m.hrmetajornada then
                              case
                                  -- motorista
                                  when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      then
                                      (m.cxentreg * uv.rm_motorista_rota_jornada) - m.vlnaobateujornmot
                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante then
                                          (m.cxentreg * uv.rm_ajudante_rota_jornada) - (m.vlnaobateujornaju / m.fator)
                                  else 0
                                  end
                          else 0
                          end::numeric, 2)
            end +
           ----------------------
           -- calculo dos mapas de AS NÃO RECARGA
        round(case
                  when m.entrega = 'AS' and m.cargaatual <> 'Recarga' and fator <> 0 then
                      -- verifica se unidade paga por caixa ou por entrega
                      case
                          -- unidade paga os mapas de AS por caixa entregue e nõa por número de entregas
                          when uv.as_paga_por_cx is true then
                              --verifica o fator do mapa
                              case
                                  when m.fator = 1 then
                                      -- verifica se bateu jornada
                                      case
                                          when m.hrjornadaliq <= m.hrmetajornada then
                                              -- verifica o cargo
                                              case
                                                  -- motorista
                                                  when c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista then
                                                      m.cxentreg * uv.rm_motorista_as_cx_jornada_fator1
                                                  -- ajudante
                                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                      m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator1
                                                  else 0
                                                  end
                                          when m.hrjornadaliq > m.hrmetajornada then
                                              -- verifica o cargo
                                              case
                                                  -- motorista
                                                  when c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista then
                                                      m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator1
                                                  -- ajudante
                                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                      m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator1
                                                  else 0
                                                  end
                                          else 0
                                          end
                                  when m.fator = 2 then
                                      -- verifica se bateu jornada
                                      case
                                          when m.hrjornadaliq <= m.hrmetajornada then
                                              -- verifica o cargo
                                              case
                                                  -- motorista
                                                  when c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista then
                                                      m.cxentreg * uv.rm_motorista_as_cx_jornada_fator2
                                                  -- ajudante
                                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                      m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator2
                                                  else 0
                                                  end
                                          when m.hrjornadaliq > m.hrmetajornada then
                                              -- verifica o cargo
                                              case
                                                  -- motorista
                                                  when c.matricula_ambev = m.matricmotorista and
                                                       c.cod_funcao = ufp.cod_funcao_motorista then
                                                      m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator2
                                                  -- ajudante
                                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                                      m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator2
                                                  else 0
                                                  end
                                          else 0
                                          end
                                  else 0
                                  end
                          -- unidade não paga por caixa e sim com base no número de entregas
                          when uv.as_paga_por_cx is not true then
                              case
                                  -- verifica o cargo
                                  -- motorista com ajudante
                                  when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      and (m.matricajud1 > 0 or m.matricajud2 > 0) then
                                      -- verifica o número de entregas
                                      case
                                          when (m.entregas = 1) then uv.rm_motorista_valor_as_1_entrega
                                          when (m.entregas = 2) then uv.rm_motorista_valor_as_2_entregas
                                          when (m.entregas = 3) then uv.rm_motorista_valor_as_3_entregas
                                          when (m.entregas > 3) then uv.rm_motorista_valor_as_maior_3_entregas
                                          else (0)::real
                                          end
                                  -- motorista sem ajudante
                                  when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                                      and (m.matricajud1 is null or m.matricajud1 <= 0) and
                                       (m.matricajud2 is null or m.matricajud2 <= 0) then
                                      -- verifica o número de entregas
                                      uv.rm_motorista_valor_as_sem_ajudante
                                  -- ajudante
                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                      case
                                          when (m.entregas = 1) then uv.rm_ajudante_valor_as_1_entrega
                                          when (m.entregas = 2) then uv.rm_ajudante_valor_as_2_entregas
                                          when (m.entregas = 3) then uv.rm_ajudante_valor_as_3_entregas
                                          when (m.entregas > 3) then uv.rm_ajudante_valor_as_maior_3_entregas
                                          else (0)::real
                                          end
                                  else 0
                                  end
                          else 0
                          end
                  else 0
                  end::numeric, 2))::double precision               as valor,
       uv.rm_numero_viagens                                         as rm_numero_viagens
from (((((((((view_mapa_colaborador vmc
    join colaborador_data c on ((vmc.cpf = c.cpf)))
    join funcao f on (((f.codigo = c.cod_funcao) and (f.cod_empresa = c.cod_empresa))))
    join mapa m on (((m.mapa = vmc.mapa) and (m.cod_unidade = vmc.cod_unidade))))
    join unidade_metas um on ((um.cod_unidade = m.cod_unidade)))
    join view_valor_cx_unidade on ((view_valor_cx_unidade.cod_unidade = m.cod_unidade)))
    join equipe e on (((e.cod_unidade = c.cod_unidade) and (c.cod_equipe = e.codigo))))
    join unidade_funcao_produtividade ufp on (((ufp.cod_unidade = c.cod_unidade) and (ufp.cod_unidade = m.cod_unidade))))
    left join unidade_valores_rm uv on ((uv.cod_unidade = m.cod_unidade)))
         left join (select t.mapa                         as tracking_mapa,
                           t."código_transportadora"      as cod_transportadora,
                           sum(
                                   case
                                       when (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) then 1
                                       else 0
                                       end)               as apontamentos_ok,
                           count(t.disp_apont_cadastrado) as total_apontamentos
                    from (tracking t
                             join unidade_metas um_1 on ((um_1.cod_unidade = t."código_transportadora")))
                    group by t.mapa, t."código_transportadora") tracking
                   on (((tracking.tracking_mapa = m.mapa) and (tracking.cod_transportadora = m.cod_unidade))));

--? Considera número de viagens para bonificação
drop function func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint);
create or replace function func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint)
    returns table
            (
                "MATRICULA AMBEV"   integer,
                "COLABORADOR"       text,
                "FUNÇÃO"            text,
                "CXS ENTREGUES"     integer,
                "JORNADAS BATIDAS"  bigint,
                "RESULTADO JORNADA" text,
                "DEV PDV"           text,
                "META DEV PDV"      text,
                "RECEBE BÔNUS"      text,
                "VALOR BÔNUS"       text,
                "Nº FATOR 1"        bigint,
                "Nº FATOR 2"        bigint,
                "Nº ROTAS"          bigint,
                "VALOR ROTA"        text,
                "Nº RECARGAS"       bigint,
                "VALOR RECARGA"     text,
                "Nº ELD"            bigint,
                "DIFERENÇA ELD"     text,
                "Nº AS"             bigint,
                "VALOR AS"          text,
                "Nº MAPAS TOTAL"    bigint,
                "VALOR TOTAL"       text
            )
    language sql
as
$$
select matricula_ambev,
       initcap(nome_colaborador)                                                            as "COLABORADOR",
       funcao                                                                               as "FUNÇÃO",
       trunc(sum(cxentreg))::int                                                            as "CXS ENTREGUES",
       sum(case
               when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
                   then 1
               else 0 end)                                                                  as qtde_jornada_batida,
       trunc((sum(case
                      when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
                          then 1
                      else 0 end)::float / count(meta_jornada_liquida_horas)) * 100) || '%' as porcentagem_jornada,
       REPLACE(round(((sum(entregasnaorealizadas + entregasparciais))::numeric /
                      nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas), 0)::numeric) * 100,
                     2)::text, '.', ',') || '%'                                             as "DEV PDV",
       REPLACE(round((meta_dev_pdv * 100)::numeric, 2)::text, '.', ',') || '%'              as "META DEV PDV",
       case
           when round(1 - sum(entregascompletas) /
                          nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas), 0)::numeric, 4) <=
                meta_dev_pdv and sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
               then
               'SIM'
           else 'NÃO' end                                                                   as "RECEBE BÔNUS",
       REPLACE((case
                    when round(1 - sum(entregascompletas) /
                                   nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                          0)::numeric, 4) <= meta_dev_pdv and vpe.cod_funcao = ufp.cod_funcao_motorista
                        and sum(case when valor > 0 then 1 else 0 end) >= vpe.rm_numero_viagens
                        then
                        pci.bonus_motorista
                    when round(1 - sum(entregascompletas) /
                                   nullif(sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                          0)::numeric, 4) <= meta_dev_pdv and vpe.cod_funcao = ufp.cod_funcao_ajudante
                        and sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
                        then
                        pci.bonus_ajudante
                    else 0 end)::text, '.', ',')                                            as "VALOR BÔNUS",
       sum(case when fator = 1 then 1 else 0 end)                                           as "Nº FATOR 1",
       sum(case when fator = 2 then 1 else 0 end)                                           as "Nº FATOR 2",
       sum(case when valor_rota > 0 then 1 else 0 end)                                      as "Nº ROTAS",
       REPLACE('R$ ' || trunc(sum(valor_rota)::numeric, 2), '.', ',')                       as "VALOR ROTA",
       sum(case when valor_recarga > 0 then 1 else 0 end)                                   as "Nº RECARGAS",
       REPLACE('R$ ' || trunc(sum(valor_recarga) :: numeric, 2), '.', ',')                  as "VALOR RECARGA",
       sum(case when valor_diferenca_eld > 0 then 1 else 0 end)                             as "Nº ELD",
       REPLACE('R$ ' || trunc(sum(valor_diferenca_eld) :: numeric, 2), '.', ',')            as "DIFERENÇA ELD",
       sum(case when valor_as > 0 then 1 else 0 end)                                        as "Nº AS",
       REPLACE('R$ ' || trunc(sum(valor_as) :: numeric, 2), '.', ',')                       as "VALOR AS",
       sum(case when valor > 0 then 1 else 0 end)                                           as "Nº MAPAS TOTAL",
       REPLACE('R$ ' || trunc(((case
                                    when sum(case when valor > 0 then 1 else 0 end)::smallint >= vpe.rm_numero_viagens
                                        then (case
                                                  when round(1 - sum(entregascompletas) / nullif(
                                                          sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                                          0)::numeric, 4) <= meta_dev_pdv and
                                                       vpe.cod_funcao = ufp.cod_funcao_motorista then
                                                      pci.bonus_motorista
                                                  when round(1 - sum(entregascompletas) / nullif(
                                                          sum(entregascompletas + entregasparciais + entregasnaorealizadas),
                                                          0)::numeric, 4) <= meta_dev_pdv and
                                                       vpe.cod_funcao = ufp.cod_funcao_ajudante
                                                      then
                                                      pci.bonus_ajudante end)
                                    else 0 end) +
                               sum(valor)) :: numeric, 2), '.', ',')                        as "VALOR TOTAL"
from view_produtividade_extrato vpe
         left join pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
         left join unidade_funcao_produtividade ufp on ufp.cod_unidade = vpe.cod_unidade
where vpe.cod_unidade = f_cod_unidade
  and vpe.data between f_dt_inicial and f_dt_final
group by matricula_ambev, nome_colaborador, vpe.cod_funcao, funcao, meta_dev_pdv, ufp.cod_funcao_ajudante,
         ufp.cod_funcao_motorista, pci.bonus_ajudante, pci.bonus_motorista, vpe.rm_numero_viagens
order by nome_colaborador;
$$;

--? Adiciona o número de viagens
drop function func_get_produtividade_colaborador(integer, integer, bigint);
create or replace function func_get_produtividade_colaborador(f_mes integer, f_ano integer, f_cpf bigint)
    returns table
            (
                cod_unidade                integer,
                matricula_ambev            integer,
                data                       date,
                cpf                        bigint,
                nome_colaborador           character varying,
                data_nascimento            date,
                funcao                     character varying,
                cod_funcao                 bigint,
                nome_equipe                character varying,
                fator                      real,
                cargaatual                 character varying,
                entrega                    character varying,
                mapa                       integer,
                placa                      character varying,
                cxcarreg                   real,
                cxentreg                   real,
                qthlcarregados             real,
                qthlentregues              real,
                qtnfcarregadas             integer,
                qtnfentregues              integer,
                entregascompletas          integer,
                entregasnaorealizadas      integer,
                entregasparciais           integer,
                kmprevistoroad             real,
                kmsai                      integer,
                kmentr                     integer,
                tempoprevistoroad          integer,
                hrsai                      timestamp without time zone,
                hrentr                     timestamp without time zone,
                tempo_rota                 bigint,
                tempointerno               integer,
                hrmatinal                  time without time zone,
                apontamentos_ok            bigint,
                total_tracking             bigint,
                tempo_largada              integer,
                meta_tracking              real,
                meta_tempo_rota_mapas      real,
                meta_caixa_viagem          real,
                meta_dev_hl                real,
                meta_dev_nf                real,
                meta_dev_pdv               real,
                meta_dispersao_km          real,
                meta_dispersao_tempo       real,
                meta_jornada_liquida_mapas real,
                meta_raio_tracking         real,
                meta_tempo_interno_mapas   real,
                meta_tempo_largada_mapas   real,
                meta_tempo_rota_horas      integer,
                meta_tempo_interno_horas   integer,
                meta_tempo_largada_horas   integer,
                meta_jornada_liquida_horas integer,
                valor_rota                 real,
                valor_recarga              real,
                valor_diferenca_eld        double precision,
                valor_as                   real,
                valor                      double precision,
                rm_numero_viagens          smallint
            )
    language sql
as
$$
select *
from view_produtividade_extrato
where data between func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, null) and
    func_get_data_fim_produtividade(f_ano, f_mes, f_cpf, null)
  and cpf = f_cpf
order by data asc
$$;