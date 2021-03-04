-- Essa migração deve ser executada quando o WS versão 40 for publicado.

BEGIN TRANSACTION;
  -- ########################################################################################################
  -- Alterações para a refatoração da aferição.
  -- Adiciona coluna extra para conter tipo da aferição (PRESSAO-SULCO-SULCO_PRESSAO).
  ALTER TABLE AFERICAO ADD COLUMN TIPO_AFERICAO VARCHAR(13) NULL;
  -- Seta valor inicial como SULCO_PRESSAO  para as aferições já existentes.
  UPDATE AFERICAO SET TIPO_AFERICAO = 'SULCO_PRESSAO' WHERE TIPO_AFERICAO IS NULL;
  -- Altera coluna da tabela para conter apenas a restrição de aferição de pressao.
  ALTER TABLE EMPRESA_RESTRICAO_PNEU RENAME COLUMN PERIODO_AFERICAO TO PERIODO_AFERICAO_PRESSAO;
  -- Cria coluna extra para conter a restrição de aferição de sulco.
  ALTER TABLE EMPRESA_RESTRICAO_PNEU ADD COLUMN PERIODO_AFERICAO_SULCO INT;
  -- Altera valor para a restrição de aferição de sulco.
  UPDATE EMPRESA_RESTRICAO_PNEU SET PERIODO_AFERICAO_SULCO = 30 WHERE PERIODO_AFERICAO_SULCO IS NULL;
  -- Remove restrição NOT NULL da coluna PSI.
  ALTER TABLE AFERICAO_VALORES ALTER COLUMN PSI DROP NOT NULL;
  -- ########################################################################################################
END TRANSACTION;

BEGIN TRANSACTION ;
-- Function que lista os mapas e os intervalos realizados pelos seus colaboradores
CREATE OR REPLACE FUNCTION func_relatorio_intervalos_mapas(f_cod_unidade BIGINT, f_data_inicial DATE, f_data_final  DATE)
  RETURNS TABLE(
    "DATA" VARCHAR,
    "MAPA" INT,
    "MOTORISTA" VARCHAR,
    "INICIO INTERVALO MOTORISTA" VARCHAR,
    "FIM INTERVALO MOTORISTA" VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS MOTORISTA" VARCHAR,
    "MOTORISTA CUMPRIU TEMPO MÍNIMO" VARCHAR,
    "AJUDANTE 1" VARCHAR,
    "INICIO INTERVALO AJ 1" VARCHAR,
    "FIM INTERVALO AJ 1" VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 1" VARCHAR,
    "AJ 1 CUMPRIU TEMPO MÍNIMO" VARCHAR,
    "AJ 2" VARCHAR,
    "INICIO INTERVALO AJ 2" VARCHAR,
    "FIM INTERVALO AJ 2" VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 2" VARCHAR,
    "AJ 2 CUMPRIU TEMPO MÍNIMO" VARCHAR
  ) AS
$func$
SELECT to_char(dados.data, 'DD/MM/YYYY'),
  dados.mapa,
  dados.NOME_MOTORISTA,
  dados.INICIO_INTERVALO_MOT,
  dados.FIM_INTERVALO_MOT,
  dados.TEMPO_DECORRIDO_MINUTOS_MOT,
  dados.MOT_CUMPRIU_TEMPO_MINIMO,
  dados.NOME_aj1,
  dados.INICIO_INTERVALO_aj1,
  dados.FIM_INTERVALO_aj1,
  dados.TEMPO_DECORRIDO_MINUTOS_aj1,
  dados.aj1_CUMPRIU_TEMPO_MINIMO,
  dados.NOME_aj2,
  dados.INICIO_INTERVALO_aj2,
  dados.FIM_INTERVALO_aj2,
  dados.TEMPO_DECORRIDO_MINUTOS_aj2,
  dados.aj2_CUMPRIU_TEMPO_MINIMO
FROM view_extrato_mapas_versus_intervalos dados
WHERE dados.cod_unidade = f_cod_unidade AND dados.data::date between f_data_inicial and f_data_final
ORDER BY dados.MAPA desc
$func$ LANGUAGE SQL;

-- func que calcula a aderência aos intervalos por dia, mostrando o valor total, por motoristas e por ajudantes
CREATE OR REPLACE FUNCTION func_relatorio_aderencia_intervalo_dias(f_cod_unidade BIGINT, f_data_inicial DATE, f_data_final  DATE)
  RETURNS TABLE(
    "DATA" VARCHAR,
    "QT MAPAS" BIGINT,
    "QT MOTORISTAS" BIGINT,
    "QT INTERVALOS MOTORISTAS" BIGINT,
    "ADERÊNCIA MOTORISTAS" TEXT,
    "QT AJUDANTES" BIGINT,
    "QT INTERVALOS AJUDANTES" BIGINT,
    "ADERÊNCIA AJUDANTES" TEXT,
    "QT INTERVALOS PREVISTOS" DOUBLE PRECISION,
    "QT INTERVALOS REALIZADOS" BIGINT,
    "ADERÊNCIA DIA" TEXT
  ) AS
$func$
SELECT
  to_char(V.DATA, 'DD/MM/YYYY'),
  COUNT(V.MAPA) AS mapas,
  SUM( case when v.cpf_motorista is null then 0 else 1 end ) as qt_motoristas,
  SUM( case when v.tempo_decorrido_minutos_mot <> '-' then 1 else 0 end) as qt_intervalos_mot,
  TRUNC((SUM( case when v.tempo_decorrido_minutos_mot <> '-' then 1 else 0 end) /
  SUM( case when v.cpf_motorista is null then 0 else 1 end )::float) * 100) || '%' as aderencia_motoristas,
  SUM( case when v.cpf_aj1 is null then 0 else 1 end ) + SUM( case when v.cpf_aj2 is null then 0 else 1 end ) as numero_ajudantes,
  SUM( case when v.tempo_decorrido_minutos_aj1 <> '-' then 1 else 0 end) + SUM( case when v.tempo_decorrido_minutos_aj2 <> '-' then 1 else 0 end) as qt_intervalos_aj,
  TRUNC((SUM( case when v.tempo_decorrido_minutos_aj1 <> '-' then 1 else 0 end) + SUM( case when v.tempo_decorrido_minutos_aj2 <> '-' then 1 else 0 end)) /
  (SUM( case when v.cpf_aj1 is null then 0 else 1 end ) + SUM( case when v.cpf_aj2 is null then 0 else 1 end )::FLOAT)*100) || '%' AS ADERENCIA_AJUDANTES,
  SUM(V.intervalos_previstos) AS qt_INTERVALOS_PREVISTOS,
  SUM(V.INTERVALOS_realizados) AS qt_INTERVALOS_REALIZADOS,
  TRUNC((SUM(V.intervalos_realizados) / SUM(intervalos_previstos)) * 100) || '%' AS ADERENCIA_DIA
FROM view_extrato_mapas_versus_intervalos V
  JOIN unidade u on u.codigo = v.cod_unidade
  JOIN empresa e on e.codigo = u.cod_empresa
WHERE V.cod_unidade = f_cod_unidade AND V.data BETWEEN f_data_inicial AND f_data_final
GROUP BY V.DATA
ORDER BY V.DATA
$func$ LANGUAGE SQL;

-- Function que gera a aderência aos intervalos de cada colaborador, levando em conta os mapas realizados
CREATE OR REPLACE FUNCTION func_relatorio_aderencia_intervalo_colaborador(f_cod_unidade BIGINT, f_data_inicial DATE,
  f_data_final DATE, f_cpf TEXT)
  RETURNS TABLE(
    "NOME" TEXT,
    "FUNÇÃO" TEXT,
    "EQUIPE" TEXT,
    "INTERVALOS PREVISTOS" BIGINT,
    "INTERVALOS REALIZADOS" BIGINT,
    "ADERÊNCIA" TEXT
  ) AS
$func$
select
  c.nome,
  f.nome,
  e.nome,
  count(dados.mapa) AS intervalos_previstos,
  sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end) as intevalos_realizados,
  case when count(dados.mapa) > 0 then
    trunc((sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end)::float / count(dados.mapa))*100)
    else 0 end || '%'  as aderencia_intervalo
from colaborador c
  join unidade u on u.codigo = c.cod_unidade
  join funcao f on f.codigo = c.cod_funcao and f.cod_empresa = u.cod_empresa
  join equipe e on e.codigo = c.cod_equipe and e.cod_unidade = c.cod_unidade
  left join view_intervalo_mapa_colaborador as dados on dados.cpf = c.cpf
where c.cod_unidade = f_cod_unidade and c.cpf::text like f_cpf and dados.data between f_data_inicial and f_data_final
      and c.cod_funcao in (select COD_CARGO from intervalo_tipo_cargo where COD_UNIDADE = 4)
group by c.cpf, c.nome, e.nome, f.nome
order by case when count(dados.mapa) > 0 then
    trunc((sum(case when dados.tempo_decorrido_minutos <> '-' then 1 else 0 end)::float / count(dados.mapa))*100)
    else 0 end desc
    $func$ LANGUAGE SQL;

END TRANSACTION ;