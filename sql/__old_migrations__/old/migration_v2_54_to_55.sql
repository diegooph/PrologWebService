-- Essa migração deve ser executada quando o WS versão 55 for publicado.
BEGIN TRANSACTION;
-- ########################################################################################################
-- ########################################################################################################
-- ########################       CRIAÇÃO DA TABELA ESCALA_DIARIA       ###################################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS ESCALA_DIARIA(
  CODIGO BIGSERIAL NOT NULL,
  COD_UNIDADE BIGINT NOT NULL,
  DATA DATE NOT NULL,
  PLACA VARCHAR(7) NOT NULL,
  MAPA BIGINT NOT NULL,
  CPF_MOTORISTA BIGINT NOT NULL,
  CPF_AJUDANTE_1 BIGINT,
  CPF_AJUDANTE_2 BIGINT,
  DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE,
  DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE,
  CPF_CADASTRO BIGINT,
  CPF_ULTIMA_ALTERACAO BIGINT,
  CONSTRAINT PK_ESCALA_DIARIA PRIMARY KEY (CODIGO)
);
COMMENT ON TABLE ESCALA_DIARIA
IS 'Tabela de veículos e colaboradores que saíram para rota em suas devidas datas';

-- Adição das novas permissões para a função de escala diária
INSERT INTO funcao_prolog_v11 VALUES (410, 'Deletar itens da escala diária', 4);
INSERT INTO funcao_prolog_v11 VALUES (411, 'Upload do arquivo de escala diária', 4);
INSERT INTO funcao_prolog_v11 VALUES (412, 'Visualização da escala diária', 4);
INSERT INTO funcao_prolog_v11 VALUES (413, 'Edição de itens da escala diária', 4);
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ####################### CRIAÇÃO DA TABELA PARA FUNÇÃO DE RECAPADORAS ###################################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS RECAPADORA(
  CODIGO BIGSERIAL NOT NULL,
  NOME TEXT NOT NULL,
  COD_EMPRESA BIGINT NOT NULL,
  ATIVA BOOLEAN NOT NULL DEFAULT TRUE,
  DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE,
  CPF_CADASTRO BIGINT,
  CPF_ALTERACAO_STATUS BIGINT DEFAULT NULL ,
  CONSTRAINT PK_RECAPADORA PRIMARY KEY (CODIGO),
  CONSTRAINT FK_RECAPADORA_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO)
);
COMMENT ON TABLE RECAPADORA
IS 'Tabela de recapadoras associadas a cada empresa';
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########## CRIA VIEW PARA PASSAR A MOSTRAR OS CÓDIGOS DOS TIPOS DE INTERVALO POR UNIDADE ###############
-- ########################################################################################################
-- ########################################################################################################
CREATE VIEW VIEW_INTERVALO_TIPO AS
  SELECT ROW_NUMBER() OVER (PARTITION BY IT.COD_UNIDADE ORDER BY IT.CODIGO) AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
    IT.CODIGO,
    IT.COD_UNIDADE,
    IT.NOME,
    IT.ICONE,
    IT.TEMPO_RECOMENDADO_MINUTOS,
    IT.TEMPO_ESTOURO_MINUTOS,
    IT.HORARIO_SUGERIDO,
    IT.ATIVO
   FROM INTERVALO_TIPO IT;

DROP FUNCTION func_intervalos_agrupados( BIGINT, BIGINT, BIGINT ) CASCADE;

CREATE OR REPLACE FUNCTION func_intervalos_agrupados(f_cod_unidade        BIGINT, f_cpf_colaborador BIGINT,
                                                     f_cod_tipo_intervalo BIGINT)
  RETURNS TABLE(
    fonte_data_hora_fim TEXT,
    fonte_data_hora_inicio TEXT,
    justificativa_estouro TEXT,
    justificativa_tempo_recomendado TEXT,
    latitude_marcacao_inicio TEXT,
    longitude_marcacao_inicio TEXT,
    latitude_marcacao_fim TEXT,
    longitude_marcacao_fim TEXT,
    cod_unidade BIGINT,
    cpf_colaborador BIGINT,
    cod_tipo_intervalo BIGINT,
    cod_tipo_intervalo_por_unidade BIGINT,
    data_hora_inicio TIMESTAMP WITH TIME ZONE,
    data_hora_fim TIMESTAMP WITH TIME ZONE,
    cod_marcacao_inicio BIGINT,
    cod_marcacao_fim BIGINT)
LANGUAGE SQL
AS $$
WITH ORDERED_TABLE AS (
    SELECT
      ROW_NUMBER()
      OVER (
        ORDER BY CPF_COLABORADOR, COD_TIPO_INTERVALO, DATA_HORA ASC ) ROW_NUM,
      *
    FROM INTERVALO
    ORDER BY ROW_NUM
),

    _INITS AS (
      SELECT
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.FONTE_DATA_HORA
        END                   AS FONTE_DATA_HORA_FIM,
        T1.FONTE_DATA_HORA    AS FONTE_DATA_HORA_INICIO,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.JUSTIFICATIVA_ESTOURO
        END                   AS JUSTIFICATIVA_ESTOURO,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.JUSTIFICATIVA_TEMPO_RECOMENDADO
        END                   AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
        T1.LATITUDE_MARCACAO  AS LATITUDE_MARCACAO_INICIO,
        T1.LONGITUDE_MARCACAO AS LONGITUDE_MARCACAO_INICIO,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.LATITUDE_MARCACAO
        END                   AS LATITUDE_MARCACAO_FIM,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.LONGITUDE_MARCACAO
        END                   AS LONGITUDE_MARCACAO_FIM,
        T1.COD_UNIDADE,
        T1.CPF_COLABORADOR,
        T1.COD_TIPO_INTERVALO,
        T1.DATA_HORA          AS DATA_HORA_INICIO,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.DATA_HORA
        END                   AS DATA_HORA_FIM,
        T1.CODIGO             AS CODIGO_INICIO,
        CASE
        WHEN T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
             AND T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             AND T1.CPF_COLABORADOR = T2.CPF_COLABORADOR
             AND T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          THEN T2.CODIGO
        END                   AS CODIGO_FIM
      FROM ORDERED_TABLE AS T1
        LEFT JOIN ORDERED_TABLE AS T2 ON (
          T1.ROW_NUM = T2.ROW_NUM - 1 AND
          T1.CPF_COLABORADOR = T2.CPF_COLABORADOR AND
          T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          )
      WHERE T1.TIPO_MARCACAO = 'MARCACAO_INICIO'
  ),

    _ENDS AS (
      SELECT
        T2.FONTE_DATA_HORA    AS FONTE_DATA_HORA_FIM,
        NULL :: TEXT          AS FONTE_DATA_HORA_INICIO,
        T2.JUSTIFICATIVA_ESTOURO,
        T2.JUSTIFICATIVA_TEMPO_RECOMENDADO,
        NULL :: TEXT          AS LATITUDE_MARCACAO_INICIO,
        NULL :: TEXT          AS LONGITUDE_MARCACAO_INICIO,
        T2.LATITUDE_MARCACAO  AS LATITUDE_MARCACAO_FIM,
        T2.LONGITUDE_MARCACAO AS LONGITUDE_MARCACAO_FIM,
        T2.COD_UNIDADE,
        T2.CPF_COLABORADOR,
        T2.COD_TIPO_INTERVALO,
        NULL :: TIMESTAMP     AS DATA_HORA_INICIO,
        CASE
        WHEN (
               T1.TIPO_MARCACAO = 'MARCACAO_FIM' AND
               T2.TIPO_MARCACAO = 'MARCACAO_FIM'
             )
             OR
             (T1.TIPO_MARCACAO IS NULL) -- CASE WHEN FIRST RECORD FOR CPF_COLABORADOR AND COD_TIPO_INTERVALO IS AN END
          THEN T2.DATA_HORA
        END                   AS DATA_HORA_FIM,
        NULL :: INT           AS CODIGO_INICIO,
        T2.CODIGO             AS CODIGO_FIM
      FROM ORDERED_TABLE AS T1
        RIGHT JOIN ORDERED_TABLE AS T2 ON (
          T1.ROW_NUM = T2.ROW_NUM - 1 AND
          T1.CPF_COLABORADOR = T2.CPF_COLABORADOR AND
          T1.COD_TIPO_INTERVALO = T2.COD_TIPO_INTERVALO
          )
      WHERE T2.TIPO_MARCACAO = 'MARCACAO_FIM'
  )

SELECT
  FONTE_DATA_HORA_INICIO,
  FONTE_DATA_HORA_FIM,
  JUSTIFICATIVA_ESTOURO,
  JUSTIFICATIVA_TEMPO_RECOMENDADO,
  LATITUDE_MARCACAO_INICIO,
  LONGITUDE_MARCACAO_INICIO,
  LATITUDE_MARCACAO_FIM,
  LONGITUDE_MARCACAO_FIM,
  I.COD_UNIDADE,
  CPF_COLABORADOR,
  COD_TIPO_INTERVALO,
  VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  DATA_HORA_INICIO,
  DATA_HORA_FIM,
  CODIGO_INICIO,
  CODIGO_FIM
FROM (
       SELECT *
       FROM _INITS
       UNION ALL
       SELECT *
       FROM _ENDS
     ) I
  JOIN VIEW_INTERVALO_TIPO VIT ON I.COD_TIPO_INTERVALO = VIT.CODIGO
WHERE
  COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM) IS NOT NULL
  AND CASE WHEN f_cod_unidade IS NULL
    THEN TRUE
      ELSE I.COD_UNIDADE = f_cod_unidade END
  AND CASE WHEN f_cpf_colaborador IS NULL
    THEN TRUE
      ELSE I.CPF_COLABORADOR = f_cpf_colaborador END
  AND CASE WHEN f_cod_tipo_intervalo IS NULL
    THEN TRUE
      ELSE I.COD_TIPO_INTERVALO = f_cod_tipo_intervalo END
ORDER BY
  CPF_COLABORADOR,
  COD_TIPO_INTERVALO,
  COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM);
$$;

CREATE VIEW view_extrato_mapas_versus_intervalos AS
  SELECT m.data,
    m.mapa,
    m.cod_unidade,
    (m.fator + (1)::double precision) AS intervalos_previstos,
    ((
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) AS intervalos_realizados,
    mot.cpf AS cpf_motorista,
    mot.nome AS nome_motorista,
    COALESCE(to_char(((int_mot.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_mot,
    COALESCE(to_char(((int_mot.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_mot,
    COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_mot,
        CASE
            WHEN (int_mot.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS mot_cumpriu_tempo_minimo,
    aj1.cpf AS cpf_aj1,
    COALESCE(aj1.nome, '-'::character varying) AS nome_aj1,
    COALESCE(to_char(((int_aj1.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj1,
    COALESCE(to_char(((int_aj1.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj1,
    COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj1,
        CASE
            WHEN (int_aj1.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj1_cumpriu_tempo_minimo,
    aj2.cpf AS cpf_aj2,
    COALESCE(aj2.nome, '-'::character varying) AS nome_aj2,
    COALESCE(to_char(((int_aj2.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj2,
    COALESCE(to_char(((int_aj2.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj2,
    COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj2,
        CASE
            WHEN (int_aj2.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj2_cumpriu_tempo_minimo
   FROM (((((((mapa m
     JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
     JOIN colaborador mot ON ((((mot.cod_unidade = m.cod_unidade) AND (mot.cod_funcao = ufp.cod_funcao_motorista)) AND (mot.matricula_ambev = m.matricmotorista))))
     LEFT JOIN colaborador aj1 ON ((((aj1.cod_unidade = m.cod_unidade) AND (aj1.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj1.matricula_ambev = m.matricajud1))))
     LEFT JOIN colaborador aj2 ON ((((aj2.cod_unidade = m.cod_unidade) AND (aj2.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj2.matricula_ambev = m.matricajud2))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_mot ON (((int_mot.cpf_colaborador = mot.cpf) AND ((int_mot.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj1 ON (((int_aj1.cpf_colaborador = aj1.cpf) AND ((int_aj1.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj2 ON (((int_aj2.cpf_colaborador = aj2.cpf) AND ((int_aj2.data_hora_inicio)::date = m.data))))
  ORDER BY m.mapa DESC;


CREATE VIEW view_intervalo_mapa_colaborador AS
  SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_motorista AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_mot AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_mot AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_mot AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.mot_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos
UNION
 SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_aj1 AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_aj1 AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_aj1 AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj1 AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.aj1_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos
UNION
 SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_aj2 AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_aj2 AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_aj2 AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj2 AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.aj2_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ####################### INCLUI FILTRO DE TIPO DE INTERVALO NO RELATÓRIO DE FOLHA DE PONTO ##############
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION func_relatorio_intervalo_folha_de_ponto( bigint, bigint, date, date, text);

CREATE FUNCTION func_relatorio_intervalo_folha_de_ponto(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text)
  RETURNS TABLE(cpf_colaborador bigint, nome_colaborador text, cod_tipo_intervalo bigint, COD_TIPO_INTERVALO_POR_UNIDADE bigint,  data_hora_inicio timestamp without time zone, data_hora_fim timestamp without time zone)
LANGUAGE SQL
AS $$
SELECT
  CPF_COLABORADOR,
  C.NOME AS NOME_COLABORADOR,
  COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO,
  COD_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
  DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE,
  DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  (F.data_hora_inicio AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
  AND
  (F.data_hora_inicio AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL
ORDER BY F.CPF_COLABORADOR, F.DATA_HORA_INICIO ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ####################### CRIA RELATÓRIO DE INTERVALOS COMPARANDO COM FOLHA DE PONTO #####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_relatorio_intervalo_escala_diaria(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text)
  RETURNS TABLE(
    "UNIDADE" TEXT,
    "PLACA VEÍCULO" TEXT,
    "CÓDIGO ROTA (MAPA)" BIGINT,
    "DATA" TEXT,
    "TIPO DE INTERVALO" TEXT,
    "MOTORISTA" TEXT,
    "INÍCIO INTERVALO MOTORISTA" TEXT,
    "FIM INTERVALO MOTORISTA" TEXT,
    "AJUDANTE 1" TEXT,
    "INÍCIO INTERVALO AJUDANTE 1" TEXT,
    "FIM INTERVALO AJUDANTE 1" TEXT,
    "AJUDANTE 2" TEXT,
    "INÍCIO INTERVALO AJUDANTE 2" TEXT,
    "FIM INTERVALO AJUDANTE 2" TEXT)
LANGUAGE SQL
AS $$

WITH TABLE_INTERVALOS AS (
    SELECT * FROM FUNC_INTERVALOS_AGRUPADOS(f_cod_unidade, NULL, f_cod_tipo_intervalo) F
    WHERE (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE >= f_data_inicial
      AND (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE <= f_data_final
)

SELECT        (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = f_cod_unidade),
              ED.PLACA,
              ED.MAPA,
              TO_CHAR(ED.DATA, 'DD/MM/YYYY'),
              (SELECT IT.NOME FROM INTERVALO_TIPO IT WHERE IT.CODIGO = f_cod_tipo_intervalo),

              -- MOTORISTA
              (CASE WHEN CM.CPF IS NULL THEN 'MOTORISTA NÃO CADASTRADO' ELSE CM.NOME END) AS NOME_MOTORISTA,
              CASE
                WHEN INT_MOT.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_MOT.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_MOT.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_MOT.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,

              -- AJUDANTE 1
              (CASE WHEN CA1.CPF IS NULL THEN 'AJUDANTE 1 NÃO CADASTRADO' ELSE CA1.NOME END) AS NOME_AJUDANTE_1,
              CASE
                WHEN INT_AJ1.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_AJ1.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_AJ1.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_AJ1.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,

              -- AJUDANTE 2
              (CASE WHEN CA2.CPF IS NULL THEN 'AJUDANTE 2 NÃO CADASTRADO' ELSE CA2.NOME END) AS NOME_AJUDANTE_2,
              CASE
                WHEN INT_AJ2.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_AJ2.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_AJ2.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_AJ2.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END
            FROM ESCALA_DIARIA AS ED
              LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA
              LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1
              LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2
              LEFT JOIN TABLE_INTERVALOS INT_MOT
                ON (COALESCE(INT_MOT.data_hora_inicio, INT_MOT.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_MOT.cpf_colaborador = ED.cpf_motorista

              LEFT JOIN TABLE_INTERVALOS INT_AJ1
                ON (COALESCE(INT_AJ1.data_hora_inicio, INT_AJ1.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_AJ1.cpf_colaborador = ED.cpf_ajudante_1

              LEFT JOIN TABLE_INTERVALOS INT_AJ2
                ON (COALESCE(INT_AJ2.data_hora_inicio, INT_AJ2.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_AJ2.cpf_colaborador = ED.cpf_ajudante_2

            WHERE (ED.DATA >= f_data_inicial AND ED.DATA <= f_data_final);
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########## ATUALIZA O NOME DE TODAS AS PERMISSÕES PARA CONTER NOME DA FUNÇÃO ANTES #####################
-- ########################################################################################################
-- ########################################################################################################
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Visualizar farol dos veículos' WHERE CODIGO = 10;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Realizar ' WHERE CODIGO = 11;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Visualizar modelo' WHERE CODIGO = 112;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Cadastrar modelo' WHERE CODIGO = 113;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Alterar modelo' WHERE CODIGO = 114;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Veículo - Cadastrar' WHERE CODIGO = 14;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Veículo - Alterar' WHERE CODIGO = 16;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Veículos - Visualizar' WHERE CODIGO = 115;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneu - Cadastrar' WHERE CODIGO = 15;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneu - Alterar' WHERE CODIGO = 17;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneu - Vincular ao veículo' WHERE CODIGO = 111;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Aferição - Realizar' WHERE CODIGO = 18;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Visualizar Ordem de Serviço (O.S.)' WHERE CODIGO = 12;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Consertar item em Ordem de Serviço (O.S.)' WHERE CODIGO = 13;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneus - Consertar item em Ordem de Serviço (O.S.)' WHERE CODIGO = 19;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneus - Visualizar relatórios' WHERE CODIGO = 110;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'GSD - Realizar' WHERE CODIGO = 20;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Relatos - Realizar' WHERE CODIGO = 21;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Relatos - Visualizar' WHERE CODIGO = 25;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Visualização do ranking' WHERE CODIGO = 33;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Treinamento - Visualizar' WHERE CODIGO = 30;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Treinamento - Criar' WHERE CODIGO = 323;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Treinamento - Alterar' WHERE CODIGO = 318;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Calendário - Visualizar eventos' WHERE CODIGO = 32;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Calendário - Criar evento' WHERE CODIGO = 324;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Calendário - Alterar evento' WHERE CODIGO = 319;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pré-Contracheque - Upload' WHERE CODIGO = 34;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pré-Contracheque - Visualizar' WHERE CODIGO = 35;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Quiz - Realizar' WHERE CODIGO = 36;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Quiz - Visualizar modelo de ' WHERE CODIGO = 320;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Quiz - Criar modelo' WHERE CODIGO = 37;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Quiz - Alterar modelo' WHERE CODIGO = 321;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Solicitação de Folga - Realizar solicitação' WHERE CODIGO = 38;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Solicitação de Folga - Responder a uma solicitação' WHERE CODIGO = 39;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Colaboradores - Cadastrar' WHERE CODIGO = 310;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Colaboradores - Alterar' WHERE CODIGO = 325;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Colaboradores - Visualizar' WHERE CODIGO = 316;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Equipe - Cadastrar' WHERE CODIGO = 311;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Equipe - Visualizar' WHERE CODIGO = 317;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Fale Conosco - Realizar' WHERE CODIGO = 314;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Fale Conosco - Responder' WHERE CODIGO = 315;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Indicadores - Visualizar indicadores individuais' WHERE CODIGO = 40;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Indicadores - Visualizar relatórios de indicadores' WHERE CODIGO = 41;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Mapa (2art) e Tracking - Upload dos arquivos "2art" e "Tracking"' WHERE CODIGO = 42;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Mapa (2art) e Tracking - Verificar dados importados' WHERE CODIGO = 43;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Metas - Editar' WHERE CODIGO = 44;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Metas - Visualizar' WHERE CODIGO = 47;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Produtividade - Visualizar' WHERE CODIGO = 45;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Produtividade - Visualizar consolidade' WHERE CODIGO = 46;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Aferição - Visualziar realizadas' WHERE CODIGO = 117;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Quiz - Visualizar realizados' WHERE CODIGO = 326;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Solicitação de Folga - Visualizar ' WHERE CODIGO = 327;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneus - Visualizar Ordens de Serviços' WHERE CODIGO = 119;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Cargo - Visualizar permissões' WHERE CODIGO = 328;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Cargo - Vincular permissão' WHERE CODIGO = 329;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Relato - Fechar' WHERE CODIGO = 24;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Relato - Classificar' WHERE CODIGO = 23;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Gente - Visualizar relatórios' WHERE CODIGO = 330;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Equipe - Alterar' WHERE CODIGO = 313;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Visualizar relatórios' WHERE CODIGO = 121;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Movimentação - Movimentação de Pneus' WHERE CODIGO = 120;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Fale Conosco - Visualizar relatórios' WHERE CODIGO = 331;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Pneus - Visualizar' WHERE CODIGO = 116;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Solicitação de Folga - Visualizar relatórios' WHERE CODIGO = 332;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Fale Conosco - Visualizar todos' WHERE CODIGO = 322;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Produtividade - Visualizar relatórios' WHERE CODIGO = 48;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Checklist - Visualizar todos os realizados' WHERE CODIGO = 118;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Prontuário do Condutor - Upload' WHERE CODIGO = 333;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Prontuário do Condutor - Visualizar' WHERE CODIGO = 334;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Prontuário do Condutor - Visualizar todos' WHERE CODIGO = 335;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Inativar tipo de intervalo' WHERE CODIGO = 341;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Criar tipo de intervalo' WHERE CODIGO = 340;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Invalidar marcações' WHERE CODIGO = 339;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Editar Marcações' WHERE CODIGO = 338;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Visualizar todas as marcações' WHERE CODIGO = 337;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Marcar intervalos' WHERE CODIGO = 336;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Intervalos - Visualizar relatórios' WHERE CODIGO = 342;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Relatos - Visualizar relatórios' WHERE CODIGO = 26;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Movimentação - Movimentar pneus da análise para o descarte' WHERE CODIGO = 125;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Movimentação - Edição dos motivos de descarte de pneus' WHERE CODIGO = 124;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Movimentação - Cadastrar novos motivos para o descarte de pneus' WHERE CODIGO = 123;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Veículos - Visualizar relatórios' WHERE CODIGO = 122;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Treinamentos - Visualizar relatórios' WHERE CODIGO = 343;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Escala Diária - Deletar itens' WHERE CODIGO = 410;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Escala Diária - Upload do arquivo' WHERE CODIGO = 411;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Escala Diária - Visualização' WHERE CODIGO = 412;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Escala Diária - Edição de itens.' WHERE CODIGO = 413;
--  ########################################################################################################
-- ########################################################################################################
END TRANSACTION;