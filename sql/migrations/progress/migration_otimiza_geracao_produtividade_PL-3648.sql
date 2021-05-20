-- Renomeia colunas que possuem acento.
-- Isso é necessário pois precisamos aplicar um index na coluna "código_transportadora" e como ela possui acento a
-- aplicação do index nas queries acaba não ocorrendo.
alter table tracking rename column horário_matinal to horario_matinal;
alter table tracking rename column saída_cdd to saida_cdd;
alter table tracking rename column tempo_almoço to tempo_almoco;
alter table tracking rename column unidade_negócio to unidade_negocio;
alter table tracking rename column distância_prev to distancia_prev;
alter table tracking rename column vel_média_km_h to vel_media_km_h;
alter table tracking rename column distância_perc_apontamento to distancia_perc_apontamento;
alter table tracking rename column aderência_sequencia_entrega to aderencia_sequencia_entrega;
alter table tracking rename column aderência_janela_entrega to aderencia_janela_entrega;
alter table tracking rename column código_transportadora to cod_unidade;

-- Index utilizado pela view_produtividade_extrato.
create index idx_tracking_cod_unidade on tracking (cod_unidade);

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
             meta_jornada_liquida_horas, rm_numero_viagens, valor_rota, valor_recarga, valor_diferenca_eld,
             valor_as)
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
       round((vpe.valor_rota
           + vpe.valor_as
           + vpe.valor_recarga
           + vpe.valor_diferenca_eld)::numeric, 2)::double precision as valor
from view_produtividade_extrato vpe;

-- Remove views que não eram mais utilizadas.
drop view resumo_dados;
-- Essa abaixo possuia um join com view_extrato_produtividade mas seus dados não eram utilizados.
drop view view_valor_cx_unidade;

-- Altera colunas de segundos de integer para bigint.
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
  and vpe.cpf = f_cpf
order by vpe.data
$$;

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
                                                      pci.bonus_ajudante
                                                  else 0 end)
                                    else 0 end) +
                               sum(valor)) :: numeric, 2), '.', ',')                        as "VALOR TOTAL"
from view_produtividade_extrato_com_total vpe
         left join pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
         left join unidade_funcao_produtividade ufp on ufp.cod_unidade = vpe.cod_unidade
where vpe.cod_unidade = f_cod_unidade
  and vpe.data between f_dt_inicial and f_dt_final
group by matricula_ambev, nome_colaborador, vpe.cod_funcao, funcao, meta_dev_pdv, ufp.cod_funcao_ajudante,
         ufp.cod_funcao_motorista, pci.bonus_ajudante, pci.bonus_motorista, vpe.rm_numero_viagens
order by nome_colaborador;
$$;

create or replace function func_relatorio_produtividade_remuneracao_acumulada_colaborador(f_cod_unidade bigint,
                                                                                          f_cpf_colaborador bigint,
                                                                                          f_data_inicial date,
                                                                                          f_data_final date)
    returns table
            (
                "CPF_COLABORADOR"  bigint,
                "NOME_COLABORADOR" text,
                "DATA"             date,
                "CAIXAS_ENTREGUES" numeric,
                "FATOR"            real,
                "VALOR"            double precision
            )
    language sql
as
$$
select vpe.cpf,
       vpe.nome_colaborador,
       vpe.data,
       round(vpe.cxentreg::numeric, 2),
       vpe.fator,
       vpe.valor
from view_produtividade_extrato_com_total as vpe
where vpe.cod_unidade = f_cod_unidade
  and case
          when f_cpf_colaborador is null then true
          else vpe.cpf = f_cpf_colaborador
    end
  and vpe.data between f_data_inicial and f_data_final
order by vpe.cpf, vpe.data;
$$;

comment on function func_relatorio_produtividade_remuneracao_acumulada_colaborador(bigint, bigint, date, date)
    is 'Busca a produtividade do colaborador para um período.';

-- Altera usos do código_transportadora por cod_unidade.
create or replace function func_relatorio_mapa_estratificado(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
    returns table
            (
                data                           character varying,
                placa                          character varying,
                mapa                           integer,
                "MATRIC MOTORISTA"             integer,
                "NOME MOTORISTA"               text,
                "MATRIC AJUDANTE 1"            integer,
                "NOME AJUDANTE 1"              text,
                "MATRIC AJUDANTE 2"            integer,
                "NOME AJUDANTE 2"              text,
                entregas                       integer,
                cxcarreg                       real,
                cxentreg                       real,
                transp                         integer,
                entrega                        character varying,
                cargaatual                     text,
                frota                          text,
                custospot                      real,
                regiao                         integer,
                veiculo                        integer,
                veiculoindisp                  real,
                placaindisp                    real,
                frotaindisp                    real,
                tipoindisp                     integer,
                ocupacao                       real,
                cxrota                         real,
                cxas                           real,
                veicbm                         real,
                rshow                          integer,
                entrvol                        character varying,
                hrsai                          timestamp without time zone,
                hrentr                         timestamp without time zone,
                kmsai                          integer,
                kmentr                         integer,
                custovariavel                  real,
                lucro                          real,
                lucrounit                      real,
                valorfrete                     real,
                tipoimposto                    character varying,
                percimposto                    real,
                valorimposto                   real,
                valorfaturado                  real,
                valorunitcxentregue            real,
                valorpgcxentregsemimp          real,
                valorpgcxentregcomimp          real,
                tempoprevistoroad              time without time zone,
                kmprevistoroad                 real,
                valorunitpontomot              real,
                valorunitpontoajd              real,
                valorequipeentrmot             real,
                valorequipeentrajd             real,
                custovariavelcedbz             real,
                lucrounitcedbz                 real,
                lucrovariavelcxentregueffcedbz real,
                tempointerno                   time without time zone,
                valordropdown                  real,
                veiccaddd                      character varying,
                kmlaco                         real,
                kmdeslocamento                 real,
                tempolaco                      time without time zone,
                tempodeslocamento              time without time zone,
                sitmulticdd                    real,
                unborigem                      integer,
                valorctedifere                 character varying,
                qtnfcarregadas                 integer,
                qtnfentregues                  integer,
                inddevcx                       real,
                inddevnf                       real,
                fator                          real,
                recarga                        character varying,
                hrmatinal                      time without time zone,
                hrjornadaliq                   time without time zone,
                hrmetajornada                  time without time zone,
                vlbateujornmot                 real,
                vlnaobateujornmot              real,
                vlrecargamot                   real,
                vlbateujornaju                 real,
                vlnaobateujornaju              real,
                vlrecargaaju                   real,
                vltotalmapa                    real,
                qthlcarregados                 real,
                qthlentregues                  real,
                indicedevhl                    real,
                regiao2                        character varying,
                qtnfcarreggeral                integer,
                qtnfentreggeral                integer,
                capacidadeveiculokg            real,
                pesocargakg                    real,
                capacveiculocx                 integer,
                entregascompletas              integer,
                entregasparciais               integer,
                entregasnaorealizadas          integer,
                codfilial                      integer,
                nomefilial                     character varying,
                codsupervtrs                   integer,
                nomesupervtrs                  character varying,
                codspot                        integer,
                nomespot                       text,
                equipcarregados                integer,
                equipdevolvidos                integer,
                equiprecolhidos                integer,
                cxentregtracking               real,
                hrcarreg                       timestamp without time zone,
                hrpcfisica                     timestamp without time zone,
                hrpcfinanceira                 timestamp without time zone,
                stmapa                         character varying,
                totalapontamentostracking      bigint,
                apontamentosok                 bigint,
                apontamentosnok                bigint,
                aderencia                      double precision,
                data_hora_import               character varying
            )
    language sql
as
$$
select to_char(m.data, 'DD/MM/YYYY'),
       placa,
       mapa,
       matricmotorista,
       coalesce(motorista.nome, '-')                                                  as nome_motorista,
       matricajud1,
       coalesce(ajudante1.nome, '-')                                                  as nome_ajudante1,
       matricajud2,
       coalesce(ajudante2.nome, '-')                                                  as nome_ajudante2,
       entregas                                                                       as entregas,
       cxcarreg                                                                       as cxcarreg,
       cxentreg                                                                       as cxentreg,
       transp                                                                         as transp,
       entrega                                                                        as entrega,
       cargaatual                                                                     as cargaatual,
       frota                                                                          as frota,
       custospot                                                                      as custospot,
       regiao                                                                         as regiao,
       veiculo                                                                        as veiculo,
       veiculoindisp                                                                  as veiculoindisp,
       placaindisp                                                                    as placaindisp,
       frotaindisp                                                                    as frotaindisp,
       tipoindisp                                                                     as tipoindisp,
       ocupacao                                                                       as ocupacao,
       cxrota                                                                         as cxrota,
       cxas                                                                           as cxas,
       veicbm                                                                         as veicbm,
       rshow                                                                          as rshow,
       entrvol                                                                        as entrvol,
       hrsai                                                                          as hrsai,
       hrentr                                                                         as hrentr,
       kmsai                                                                          as kmsai,
       kmentr                                                                         as kmentr,
       custovariavel                                                                  as custovariavel,
       lucro                                                                          as lucro,
       lucrounit                                                                      as lucrounit,
       valorfrete                                                                     as valorfrete,
       tipoimposto                                                                    as tipoimposto,
       percimposto                                                                    as percimposto,
       valorimposto                                                                   as valorimposto,
       valorfaturado                                                                  as valorfaturado,
       valorunitcxentregue                                                            as valorunitcxentregue,
       valorpgcxentregsemimp                                                          as valorpgcxentregsemimp,
       valorpgcxentregcomimp                                                          as valorpgcxentregcomimp,
       tempoprevistoroad                                                              as tempoprevistoroad,
       kmprevistoroad                                                                 as kmprevistoroad,
       valorunitpontomot                                                              as valorunitpontomot,
       valorunitpontoajd                                                              as valorunitpontoajd,
       valorequipeentrmot                                                             as valorequipeentrmot,
       valorequipeentrajd                                                             as valorequipeentrajd,
       custovariavelcedbz                                                             as custovariavelcedbz,
       lucrounitcedbz                                                                 as lucrounitcedbz,
       lucrovariavelcxentregueffcedbz                                                 as lucrovariavelcxentregueffcedbz,
       tempointerno                                                                   as tempointerno,
       valordropdown                                                                  as valordropdown,
       veiccaddd                                                                      as veiccaddd,
       kmlaco                                                                         as kmlaco,
       kmdeslocamento                                                                 as kmdeslocamento,
       tempolaco                                                                      as tempolaco,
       tempodeslocamento                                                              as tempodeslocamento,
       sitmulticdd                                                                    as sitmulticdd,
       unborigem                                                                      as unborigem,
       valorctedifere                                                                 as valorctedifere,
       qtnfcarregadas                                                                 as qtnfcarregadas,
       qtnfentregues                                                                  as qtnfentregues,
       inddevcx                                                                       as inddevcx,
       inddevnf                                                                       as inddevnf,
       fator                                                                          as fator,
       recarga                                                                        as recarga,
       hrmatinal                                                                      as hrmatinal,
       hrjornadaliq                                                                   as hrjornadaliq,
       hrmetajornada                                                                  as hrmetajornada,
       vlbateujornmot                                                                 as vlbateujornmot,
       vlnaobateujornmot                                                              as vlnaobateujornmot,
       vlrecargamot                                                                   as vlrecargamot,
       vlbateujornaju                                                                 as vlbateujornaju,
       vlnaobateujornaju                                                              as vlnaobateujornaju,
       vlrecargaaju                                                                   as vlrecargaaju,
       vltotalmapa                                                                    as vltotalmapa,
       qthlcarregados                                                                 as qthlcarregados,
       qthlentregues                                                                  as qthlentregues,
       indicedevhl                                                                    as indicedevhl,
       regiao2                                                                        as regiao2,
       qtnfcarreggeral                                                                as qtnfcarreggeral,
       qtnfentreggeral                                                                as qtnfentreggeral,
       capacidadeveiculokg                                                            as capacidadeveiculokg,
       pesocargakg                                                                    as pesocargakg,
       capacveiculocx                                                                 as capacveiculocx,
       entregascompletas                                                              as entregascompletas,
       entregasparciais                                                               as entregasparciais,
       entregasnaorealizadas                                                          as entregasnaorealizadas,
       codfilial                                                                      as codfilial,
       nomefilial                                                                     as nomefilial,
       codsupervtrs                                                                   as codsupervtrs,
       nomesupervtrs                                                                  as nomesupervtrs,
       codspot                                                                        as codspot,
       nomespot                                                                       as nomespot,
       equipcarregados                                                                as equipcarregados,
       equipdevolvidos                                                                as equipdevolvidos,
       equiprecolhidos                                                                as equiprecolhidos,
       cxentregtracking                                                               as cxentregtracking,
       hrcarreg                                                                       as hrcarreg,
       hrpcfisica                                                                     as hrpcfisica,
       hrpcfinanceira                                                                 as hrpcfinanceira,
       stmapa                                                                         as stmapa,
       tracking.total_apontamentos                                                    as total_apontamentos,
       tracking.apontamentos_ok                                                       as apontamentos_ok,
       tracking.total_apontamentos - tracking.apontamentos_ok                         as apontamentos_nok,
       trunc((tracking.apontamentos_ok :: float / tracking.total_apontamentos) * 100) as aderencia,
       to_char(m.data_hora_import, 'DD/MM/YYYY HH24:MI')                              as data_hora_import
from mapa m
         join unidade_funcao_produtividade ufp on ufp.cod_unidade = m.cod_unidade
         left join colaborador motorista
                   on motorista.cod_unidade = m.cod_unidade and motorista.cod_funcao = ufp.cod_funcao_motorista
                       and motorista.matricula_ambev = m.matricmotorista
         left join colaborador ajudante1
                   on ajudante1.cod_unidade = m.cod_unidade and ajudante1.cod_funcao = ufp.cod_funcao_ajudante
                       and ajudante1.matricula_ambev = m.matricajud1
         left join colaborador ajudante2
                   on ajudante2.cod_unidade = m.cod_unidade and ajudante2.cod_funcao = ufp.cod_funcao_ajudante
                       and ajudante2.matricula_ambev = m.matricajud2
         left join (select t.mapa                         as tracking_mapa,
                           t.cod_unidade                     tracking_unidade,
                           count(t.disp_apont_cadastrado) as total_apontamentos,
                           sum(case
                                   when t.disp_apont_cadastrado <= um.meta_raio_tracking
                                       then 1
                                   else 0 end)            as apontamentos_ok
                    from tracking t
                             join unidade_metas um on um.cod_unidade = t.cod_unidade
                    group by 1, 2) as tracking on tracking_mapa = m.mapa and tracking_unidade = m.cod_unidade
where m.cod_unidade = f_cod_unidade
  and m.data between f_data_inicial and f_data_final
order by m.mapa
$$;

-- Altera usos do código_transportadora por cod_unidade.
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