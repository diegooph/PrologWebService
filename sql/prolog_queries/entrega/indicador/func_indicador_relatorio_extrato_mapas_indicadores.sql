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