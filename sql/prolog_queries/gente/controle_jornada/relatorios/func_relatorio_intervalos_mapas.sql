-- Sobre:
--
-- Esta function lista os mapas e os intervalos realizados pelos seus colaboradores.
--
-- Histórico:
-- 2019-08-06 -> Criado tracking da function (wvinim - PL-2152).
-- 2020-04-28 -> Altera busca para usar nova function base e remove where não mais necessário. (luiz_fp - PL-2720).
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALOS_MAPAS(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "DATA"                                 VARCHAR,
    "MAPA"                                 INT,
    "MOTORISTA"                            VARCHAR,
    "INICIO INTERVALO MOTORISTA"           VARCHAR,
    "FIM INTERVALO MOTORISTA"              VARCHAR,
    "MARCAÇÕES RECONHECIDAS MOT"           VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS MOTORISTA" VARCHAR,
    "MOTORISTA CUMPRIU TEMPO MÍNIMO"       VARCHAR,
    "AJUDANTE 1"                           VARCHAR,
    "INICIO INTERVALO AJ 1"                VARCHAR,
    "FIM INTERVALO AJ 1"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 1"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 1"      VARCHAR,
    "AJ 1 CUMPRIU TEMPO MÍNIMO"            VARCHAR,
    "AJ 2"                                 VARCHAR,
    "INICIO INTERVALO AJ 2"                VARCHAR,
    "FIM INTERVALO AJ 2"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 2"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 2"      VARCHAR,
    "AJ 2 CUMPRIU TEMPO MÍNIMO"            VARCHAR
  ) AS
$func$
SELECT to_char(dados.data, 'DD/MM/YYYY'),
       dados.mapa,
       dados.NOME_MOTORISTA,
       dados.INICIO_INTERVALO_MOT,
       dados.FIM_INTERVALO_MOT,
       F_IF(dados.MARCACOES_RECONHECIDAS_MOT, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_MOT,
       dados.MOT_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj1,
       dados.INICIO_INTERVALO_aj1,
       dados.FIM_INTERVALO_aj1,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ1, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj1,
       dados.aj1_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj2,
       dados.INICIO_INTERVALO_aj2,
       dados.FIM_INTERVALO_aj2,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ2, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj2,
       dados.aj2_CUMPRIU_TEMPO_MINIMO
FROM func_marcacao_intervalos_versus_mapas(f_cod_unidade, f_data_inicial, f_data_final) dados
ORDER BY dados.MAPA desc
$func$
LANGUAGE SQL;