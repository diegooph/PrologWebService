-- Sobre:
--
-- Essa function traz a quantidade de intervalos que existiram no período filtrado, uma linha por dia.
-- Para cada dia, é informado a aderência de intervalos dos motoristas e ajudantes (1 e 2) da unidade,
-- bem como a aderência total do dia.
--
-- Functions de dependência:
--  • f_if
--  • coalesce_percentage
--
-- Histórico:
-- 2019-08-12 -> Function melhorada, documentada e adicionada ao GitHub (luizfp - PL-2220).
-- 2019-08-15 -> Function melhorada, corrigido bug de divisão por 0. (wvinim - PL-2220).
-- 2020-04-28 -> Altera busca para usar nova function base e remove where não mais necessário. (luiz_fp - PL-2720).
create or replace function func_relatorio_aderencia_intervalo_dias(f_cod_unidade bigint,
                                                                   f_data_inicial date,
                                                                   f_data_final date)
    returns TABLE
            (
                "DATA"                     CHARACTER VARYING,
                "QT MAPAS"                 BIGINT,
                "QT MOTORISTAS"            BIGINT,
                "QT INTERVALOS MOTORISTAS" BIGINT,
                "ADERÊNCIA MOTORISTAS"     TEXT,
                "QT AJUDANTES"             BIGINT,
                "QT INTERVALOS AJUDANTES"  BIGINT,
                "ADERÊNCIA AJUDANTES"      TEXT,
                "QT INTERVALOS PREVISTOS"  BIGINT,
                "QT INTERVALOS REALIZADOS" BIGINT,
                "ADERÊNCIA DIA"            TEXT
            )
    language sql
as
$$
SELECT to_char(V.DATA, 'DD/MM/YYYY'),
       COUNT(V.MAPA)                                                              as mapas,
       SUM(f_if(v.cpf_motorista is not null, 1, 0))                               as qt_motoristas,
       SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0))                      as qt_intervalos_mot,
       COALESCE_PERCENTAGE(SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0)) :: FLOAT,
                           SUM(f_if(v.cpf_motorista is not null, 1, 0)) :: FLOAT) as aderencia_motoristas,
       SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
       SUM(f_if(v.cpf_aj2 is not null, 1, 0))                                     as numero_ajudantes,
       SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
       SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0))                      as qt_intervalos_aj,
       COALESCE_PERCENTAGE(
                   SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
                   SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0)) :: FLOAT,
                   SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
                   SUM(f_if(v.cpf_aj2 is not null, 1, 0)) :: FLOAT)               as aderencia_ajudantes,
       SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
       SUM(V.intervalos_previstos)                                                as qt_intervalos_previstos,
       SUM(V.INTERVALOS_realizados)                                               as qt_intervalos_realizados,
       COALESCE_PERCENTAGE(SUM(V.intervalos_realizados) :: FLOAT,
                           SUM(V.intervalos_previstos) :: FLOAT)                  as aderencia_dia
FROM func_marcacao_intervalos_versus_mapas(f_cod_unidade, f_data_inicial, f_data_final) v
         JOIN unidade u on u.codigo = v.cod_unidade
         JOIN empresa e on e.codigo = u.cod_empresa
GROUP BY V.DATA
ORDER BY V.DATA
$$;