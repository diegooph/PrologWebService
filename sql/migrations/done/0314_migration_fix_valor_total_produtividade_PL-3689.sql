drop view view_produtividade_extrato_com_total;
drop view view_produtividade_extrato;
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
             meta_jornada_liquida_horas, rm_numero_viagens, diferenca_eld_soma_total, valor_rota, valor_recarga,
             valor_diferenca_eld, valor_as)
as
with internal_tracking as (
    select t.mapa                                                                 as tracking_mapa,
           t.cod_unidade                                                          as cod_unidade,
           sum(1) filter (where t.disp_apont_cadastrado <= um.meta_raio_tracking) as apontamentos_ok,
           count(t.disp_apont_cadastrado)                                         as total_apontamentos
    from tracking t
             join unidade_metas um on um.cod_unidade = t.cod_unidade
    group by t.mapa, t.cod_unidade
)

select vmc.cod_unidade                                    as cod_unidade,
       c.matricula_ambev                                  as matricula_ambev,
       m.data                                             as data,
       vmc.cpf                                            as cpf,
       c.nome                                             as nome_colaborador,
       c.data_nascimento                                  as data_nascimento,
       f.nome                                             as funcao,
       f.codigo                                           as cod_funcao,
       e.nome                                             as nome_equipe,
       m.fator                                            as fator,
       m.cargaatual                                       as cargaatual,
       m.entrega                                          as entrega,
       m.mapa                                             as mapa,
       m.placa                                            as placa,
       m.cxcarreg                                         as cxcarreg,
       m.cxentreg                                         as cxentreg,
       m.qthlcarregados                                   as qthlcarregados,
       m.qthlentregues                                    as qthlentregues,
       m.qtnfcarregadas                                   as qtnfcarregadas,
       m.qtnfentregues                                    as qtnfentregues,
       m.entregascompletas                                as entregascompletas,
       m.entregasnaorealizadas                            as entregasnaorealizadas,
       m.entregasparciais                                 as entregasparciais,
       m.kmprevistoroad                                   as kmprevistoroad,
       m.kmsai                                            as kmsai,
       m.kmentr                                           as kmentr,
       to_seconds(m.tempoprevistoroad)                    as tempoprevistoroad,
       m.hrsai                                            as hrsai,
       m.hrentr                                           as hrentr,
       to_seconds(m.hrentr - m.hrsai)                     as tempo_rota,
       to_seconds(m.tempointerno)                         as tempointerno,
       m.hrmatinal                                        as hrmatinal,
       it.apontamentos_ok                                 as apontamentos_ok,
       it.total_apontamentos                              as total_tracking,
       to_seconds((
           case
               when (((m.hrsai)::time without time zone < m.hrmatinal) or
                     (m.hrmatinal = '00:00:00'::time without time zone)) then um.meta_tempo_largada_horas
               else ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
               end))                                      as tempo_largada,
       um.meta_tracking                                   as meta_tracking,
       um.meta_tempo_rota_mapas                           as meta_tempo_rota_mapas,
       um.meta_caixa_viagem                               as meta_caixa_viagem,
       um.meta_dev_hl                                     as meta_dev_hl,
       um.meta_dev_nf                                     as meta_dev_nf,
       um.meta_dev_pdv                                    as meta_dev_pdv,
       um.meta_dispersao_km                               as meta_dispersao_km,
       um.meta_dispersao_tempo                            as meta_dispersao_tempo,
       um.meta_jornada_liquida_mapas                      as meta_jornada_liquida_mapas,
       um.meta_raio_tracking                              as meta_raio_tracking,
       um.meta_tempo_interno_mapas                        as meta_tempo_interno_mapas,
       um.meta_tempo_largada_mapas                        as meta_tempo_largada_mapas,
       to_seconds(m.hrmetajornada - '01:00:00'::interval) as meta_tempo_rota_horas,
       to_seconds(um.meta_tempo_interno_horas)            as meta_tempo_interno_horas,
       to_seconds(um.meta_tempo_largada_horas)            as meta_tempo_largada_horas,
       to_seconds(m.hrmetajornada)                        as meta_jornada_liquida_horas,
       uv.rm_numero_viagens                               as rm_numero_viagens,
       uv.diferenca_eld_soma_total                        as diferenca_eld_soma_total,
       -- Verifica se o mapa é de DISTRIBUIÇÃO e NÃO é RECARGA.
       round(case
                 when m.entrega <> 'AS' and m.cargaatual <> 'Recarga' then
                     -- Verifica o cargo.
                     case
                         -- Motorista.
                         when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                             then
                             m.vlbateujornmot + m.vlnaobateujornmot
                         -- Ajudante.
                         when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                             (m.vlbateujornaju + m.vlnaobateujornaju) / fator
                         else 0
                         end
                 else 0
                 end::numeric, 2)::real                   as valor_rota,

       -- Calcula mapas de recarga (somando tanto DISTRIBUIÇÃO quanto AS).
       -- Primeiro cálculo: mapas de DISTRIBUIÇÃO.
       round((case
                  when m.entrega <> 'AS' and m.cargaatual = 'Recarga' and m.fator <> 0 then
                      -- Verifica o cargo.
                      case
                          -- Motorista.
                          when c.matricula_ambev = m.matricmotorista and
                               c.cod_funcao = ufp.cod_funcao_motorista
                              then
                              m.vlrecargamot
                          -- Ajudante.
                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                              m.vlrecargaaju / m.fator
                          else 0
                          end
                  else 0
                  end +
           -- Segundo calculo: mapas de AS.
              case
                  when m.entrega = 'AS' and m.cargaatual = 'Recarga' and fator <> 0 then
                      -- Verifica se unidade paga por caixa ou por entrega.
                      case
                          -- Unidade paga os mapas de AS por caixa entregue e não por número de entregas.
                          when uv.as_paga_por_cx is true then
                              -- Verifica o fator do mapa.
                              case
                                  when m.fator = 1 then
                                      -- Verifica o cargo.
                                      case
                                          -- Motorista.
                                          when c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista then
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator1
                                          -- ajudante.
                                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator1
                                          else 0
                                          end
                                  when m.fator = 2 then
                                      -- Verifica o cargo.
                                      case
                                          -- Motorista.
                                          when c.matricula_ambev = m.matricmotorista and
                                               c.cod_funcao = ufp.cod_funcao_motorista then
                                              m.cxentreg * uv.rm_motorista_as_recarga_cx_fator2
                                          -- Ajudante.
                                          when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                               c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                              m.cxentreg * uv.rm_ajudante_as_recarga_cx_fator2
                                          else 0
                                          end
                                  else 0
                                  end
                          -- Unidade não paga por caixa e sim com base no número de entregas.
                          when uv.as_paga_por_cx is not true then
                              case
                                  -- Verifica o cargo.
                                  -- Motorista.
                                  when c.matricula_ambev = m.matricmotorista and
                                       c.cod_funcao = ufp.cod_funcao_motorista
                                      then
                                      uv.rm_motorista_valor_as_recarga
                                  -- Ajudante.
                                  when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                       c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0 then
                                      uv.rm_ajudante_valor_as_recarga
                                  else 0
                                  end
                          else 0
                          end
                  else 0
                  end)::numeric, 2)::real                 as valor_recarga,
       -- Calcula da diferença a ser paga em mapas com tempo previsto superior a meta.
       -- Verifica se é mapa de DISTRIBUIÇÃO.
       round(case
                 when m.entrega <> 'AS' and cargaatual <> 'Recarga' and
                      m.classificacao_roadshow <> 'Longa Distância'
                     and (m.tempoprevistoroad > (m.hrmetajornada - '01:00:00'::interval)) and
                      m.hrjornadaliq > m.hrmetajornada then
                     case
                         -- Motorista
                         when c.matricula_ambev = m.matricmotorista and c.cod_funcao = ufp.cod_funcao_motorista
                             then
                             (m.cxentreg * uv.rm_motorista_rota_jornada) - m.vlnaobateujornmot
                         when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                              c.cod_funcao = ufp.cod_funcao_ajudante then
                                 (m.cxentreg * uv.rm_ajudante_rota_jornada) - (m.vlnaobateujornaju / m.fator)
                         else 0
                         end
                 else 0
                 end::numeric, 2)::double precision       as diferenca_eld,
       --
       -- Cálculo dos mapas de AS NÃO RECARGA.
       --
       round(case
                 when m.entrega = 'AS' and m.cargaatual <> 'Recarga' and fator <> 0 then
                     -- Verifica se unidade paga por caixa ou por entrega.
                     case
                         -- Unidade paga os mapas de AS por caixa entregue e nõa por número de entregas.
                         when uv.as_paga_por_cx is true then
                             -- Verifica o fator do mapa.
                             case
                                 when m.fator = 1 then
                                     -- Verifica se bateu jornada.
                                     case
                                         when m.hrjornadaliq <= m.hrmetajornada then
                                             -- Verifica o cargo.
                                             case
                                                 -- Motorista.
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator1
                                                 -- Ajudante.
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0
                                                     then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator1
                                                 else 0
                                                 end
                                         when m.hrjornadaliq > m.hrmetajornada then
                                             -- Verifica o cargo.
                                             case
                                                 -- Motorista.
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator1
                                                 -- Ajudante.
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0
                                                     then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator1
                                                 else 0
                                                 end
                                         else 0
                                         end
                                 when m.fator = 2 then
                                     -- Verifica se bateu jornada.
                                     case
                                         when m.hrjornadaliq <= m.hrmetajornada then
                                             -- Verifica o cargo.
                                             case
                                                 -- Motorista.
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_jornada_fator2
                                                 -- Ajudante.
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0
                                                     then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_jornada_fator2
                                                 else 0
                                                 end
                                         when m.hrjornadaliq > m.hrmetajornada then
                                             -- Verifica o cargo
                                             case
                                                 -- Motorista.
                                                 when c.matricula_ambev = m.matricmotorista and
                                                      c.cod_funcao = ufp.cod_funcao_motorista then
                                                     m.cxentreg * uv.rm_motorista_as_cx_sem_jornada_fator2
                                                 -- ajudante
                                                 when (c.matricula_ambev = m.matricajud1 or c.matricula_ambev = m.matricajud2) and
                                                      c.cod_funcao = ufp.cod_funcao_ajudante and m.fator > 0
                                                     then
                                                     m.cxentreg * uv.rm_ajudante_as_cx_sem_jornada_fator2
                                                 else 0
                                                 end
                                         else 0
                                         end
                                 else 0
                                 end
                         -- Unidade não paga por caixa e sim com base no número de entregas.
                         when uv.as_paga_por_cx is not true then
                             case
                                 -- Verifica o cargo.
                                 -- Motorista com ajudante.
                                 when c.matricula_ambev = m.matricmotorista and
                                      c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 > 0 or m.matricajud2 > 0) then
                                     -- Verifica o número de entregas.
                                     case
                                         when (m.entregas = 1) then uv.rm_motorista_valor_as_1_entrega
                                         when (m.entregas = 2) then uv.rm_motorista_valor_as_2_entregas
                                         when (m.entregas = 3) then uv.rm_motorista_valor_as_3_entregas
                                         when (m.entregas > 3) then uv.rm_motorista_valor_as_maior_3_entregas
                                         else (0)::real
                                         end
                                 -- Motorista sem ajudante.
                                 when c.matricula_ambev = m.matricmotorista and
                                      c.cod_funcao = ufp.cod_funcao_motorista
                                     and (m.matricajud1 is null or m.matricajud1 <= 0) and
                                      (m.matricajud2 is null or m.matricajud2 <= 0) then
                                     -- Verifica o número de entregas.
                                     uv.rm_motorista_valor_as_sem_ajudante
                                 -- Ajudante.
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
                 end::numeric, 2)::real                   as valor_as
from view_mapa_colaborador vmc
         join colaborador_data c on vmc.cpf = c.cpf
         join funcao f on f.codigo = c.cod_funcao
         join mapa m
              on m.cod_unidade = vmc.cod_unidade
                  and m.mapa = vmc.mapa
         join unidade_metas um on um.cod_unidade = m.cod_unidade
         join equipe e on c.cod_equipe = e.codigo
         join unidade_funcao_produtividade ufp
              on ufp.cod_unidade = c.cod_unidade
                  and ufp.cod_unidade = m.cod_unidade
         left join unidade_valores_rm uv on uv.cod_unidade = m.cod_unidade
         left join internal_tracking it
                   on it.tracking_mapa = m.mapa
                       and it.cod_unidade = m.cod_unidade;



create view view_produtividade_extrato_com_total
as
select vpe.*,
       round((vpe.valor_rota + vpe.valor_as + vpe.valor_recarga
           + f_if(vpe.diferenca_eld_soma_total, vpe.valor_diferenca_eld, 0::double precision))::numeric,
             2)::double precision as valor
from view_produtividade_extrato vpe;


drop function func_get_produtividade_colaborador(f_mes integer, f_ano integer, f_cpf bigint);
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
                tempoprevistoroad          bigint,
                hrsai                      timestamp without time zone,
                hrentr                     timestamp without time zone,
                tempo_rota                 bigint,
                tempointerno               bigint,
                hrmatinal                  time without time zone,
                apontamentos_ok            bigint,
                total_tracking             bigint,
                tempo_largada              bigint,
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
                meta_tempo_rota_horas      bigint,
                meta_tempo_interno_horas   bigint,
                meta_tempo_largada_horas   bigint,
                meta_jornada_liquida_horas bigint,
                rm_numero_viagens          smallint,
                diferenca_eld_soma_total   boolean,
                valor_rota                 real,
                valor_recarga              real,
                valor_diferenca_eld        double precision,
                valor_as                   real,
                valor                      double precision
            )
    language sql
as
$$
select *
from view_produtividade_extrato_com_total vpe
where vpe.data between func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, null) and
    func_get_data_fim_produtividade(f_ano, f_mes, f_cpf, null)
  and vpe.cod_unidade = (select c.cod_unidade from colaborador c where c.cpf = f_cpf)
  and vpe.cpf = f_cpf
order by vpe.data
$$;