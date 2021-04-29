-- Sobre:
--
-- Esta function realiza um comparativo dos mapas importados (ambev) no sistema com as marcações dos colaboradores que
-- compõem cada mapa (motorista, ajudante 1 e ajudante 2).
-- As informações são trazidas apenas para os colaboradores que além de terem mapas vinculados, também estão cadastrados
-- no Prolog.

-- Precondições:
--  • Para trazer alguma informação significativa, é necessário que a unidade tenha importado o arquivo de mapa
--    (tabela MAPA).
--  • Também é preciso que a unidade tenha as parametrizações necessárias definidas na tabela
--    UNIDADE_FUNCAO_PRODUTIVIDADE.
--  • Apenas intervalos que tenham início e fim no mesmo dia do mapa são contabilizadas para a aderência.
CREATE OR REPLACE FUNCTION func_marcacao_intervalos_versus_mapas(F_COD_UNIDADE BIGINT,
                                                                 F_DATA_INICIAL DATE,
                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                DATA                        DATE,
                MAPA                        INTEGER,
                COD_UNIDADE                 BIGINT,
                INTERVALOS_PREVISTOS        INTEGER,
                INTERVALOS_REALIZADOS       INTEGER,
                CPF_MOTORISTA               BIGINT,
                NOME_MOTORISTA              TEXT,
                INICIO_INTERVALO_MOT        TEXT,
                FIM_INTERVALO_MOT           TEXT,
                MARCACOES_RECONHECIDAS_MOT  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_MOT TEXT,
                MOT_CUMPRIU_TEMPO_MINIMO    TEXT,
                CPF_AJ1                     BIGINT,
                NOME_AJ1                    TEXT,
                INICIO_INTERVALO_AJ1        TEXT,
                FIM_INTERVALO_AJ1           TEXT,
                MARCACOES_RECONHECIDAS_AJ1  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_AJ1 TEXT,
                AJ1_CUMPRIU_TEMPO_MINIMO    TEXT,
                CPF_AJ2                     BIGINT,
                NOME_AJ2                    TEXT,
                INICIO_INTERVALO_AJ2        TEXT,
                FIM_INTERVALO_AJ2           TEXT,
                MARCACOES_RECONHECIDAS_AJ2  BOOLEAN,
                TEMPO_DECORRIDO_MINUTOS_AJ2 TEXT,
                AJ2_CUMPRIU_TEMPO_MINIMO    TEXT
            )
    LANGUAGE SQL
AS
$$
WITH INTERVALOS_AGRUPADOS AS (
    SELECT coalesce(I.CPF_COLABORADOR, F.CPF_COLABORADOR)                      AS CPF_COLABORADOR,
           coalesce(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                AS COD_TIPO_INTERVALO,
           F_IF(I.STATUS_ATIVO_INICIO, I.DATA_HORA_INICIO, NULL)                 AS DATA_HORA_INICIO,
           F_IF(I.STATUS_ATIVO_INICIO, (I.DATA_HORA_INICIO AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE,
                NULL)                                                            AS DATA_INICIO_TZ,
           F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL)                       AS DATA_HORA_FIM,
           F_IF(F.STATUS_ATIVO_FIM, (F.DATA_HORA_FIM AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE,
                NULL)                                                            AS DATA_FIM_TZ,
           F_IF(I.STATUS_ATIVO_INICIO, I.DEVICE_RECONHECIDO :: BOOLEAN, NULL) AS DEVICE_IMEI_INICIO_RECONHECIDO,
           F_IF(F.STATUS_ATIVO_FIM, F.DEVICE_RECONHECIDO :: BOOLEAN, NULL)    AS DEVICE_IMEI_FIM_RECONHECIDO,
           tz_unidade(coalesce(I.COD_UNIDADE, F.COD_UNIDADE))                  AS TZ_UNIDADE
    FROM VIEW_MARCACAO_INICIOS I
             FULL OUTER JOIN VIEW_MARCACAO_FINS F
                             ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
         -- Aplicamos a mesma filtragem tanto nos inícios quanto nos fins pois o postgres consegue levar essas filtragens
         -- diretamente para as inner views e elas são executadas já com os filtros aplicados.
         -- Inícios.
    WHERE I.COD_UNIDADE = F_COD_UNIDADE
      -- Fins.
      AND F.COD_UNIDADE = F_COD_UNIDADE
      AND (((I.DATA_HORA_INICIO AT TIME ZONE tz_unidade(I.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL)
        OR
           ((F.DATA_HORA_FIM AT TIME ZONE tz_unidade(F.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL))
)

SELECT M.DATA                        AS DATA,
       M.MAPA                        AS MAPA,
       M.COD_UNIDADE                 AS COD_UNIDADE,
       -- (PL-2220) Antes era utilizado "m.fator + 1", mas isso não funciona porque acaba considerando colaboradores
       -- que não estão cadastrados no Prolog e possuem apenas os mapas importados. Lembre-se que fator é quantidade de
       -- ajudantes que sairam junto do motorista. Podendo ser, atualmente, 1 ou 2.
       (F_IF(MOT.CPF IS NULL, 0, 1) + F_IF(AJ1.CPF IS NULL, 0, 1) +
        F_IF(AJ2.CPF IS NULL, 0, 1)) AS INTERVALOS_PREVISTOS,
       (F_IF(INTERVALO_MOT.DATA_HORA_FIM IS NULL OR INTERVALO_MOT.DATA_HORA_INICIO IS NULL, 0, 1) +
        F_IF(INTERVALO_AJ1.DATA_HORA_FIM IS NULL OR INTERVALO_AJ1.DATA_HORA_INICIO IS NULL, 0, 1) +
        F_IF(INTERVALO_AJ2.DATA_HORA_FIM IS NULL OR INTERVALO_AJ2.DATA_HORA_INICIO IS NULL, 0,
             1))                     AS INTERVALOS_REALIZADOS,
       MOT.CPF                       AS CPF_MOTORISTA,
       MOT.NOME                      AS NOME_MOTORISTA,
       COALESCE(to_char(INTERVALO_MOT.DATA_HORA_INICIO AT TIME ZONE INTERVALO_MOT.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_MOT,
       COALESCE(to_char(INTERVALO_MOT.DATA_HORA_FIM AT TIME ZONE INTERVALO_MOT.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_MOT,
       F_IF(INTERVALO_MOT.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_MOT.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_MOT,
       coalesce(to_minutes_trunc(INTERVALO_MOT.DATA_HORA_FIM - INTERVALO_MOT.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_MOT,
       CASE
           WHEN (INTERVALO_MOT.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_MOT.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_MOT.DATA_HORA_FIM - INTERVALO_MOT.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS MOT_CUMPRIU_TEMPO_MINIMO,
       AJ1.CPF                       AS CPF_AJ1,
       COALESCE(AJ1.NOME, '-')       AS NOME_AJ1,
       COALESCE(to_char(INTERVALO_AJ1.DATA_HORA_INICIO AT TIME ZONE INTERVALO_AJ1.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_AJ1,
       COALESCE(to_char(INTERVALO_AJ1.DATA_HORA_FIM AT TIME ZONE INTERVALO_AJ1.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_AJ1,
       F_IF(INTERVALO_AJ1.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_AJ1.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_AJ1,
       coalesce(to_minutes_trunc(INTERVALO_AJ1.DATA_HORA_FIM - INTERVALO_AJ1.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_AJ1,
       CASE
           WHEN (INTERVALO_AJ1.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_AJ1.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_AJ1.DATA_HORA_FIM - INTERVALO_AJ1.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS AJ1_CUMPRIU_TEMPO_MINIMO,
       AJ2.CPF                       AS CPF_AJ2,
       COALESCE(AJ2.NOME, '-')       AS NOME_AJ2,
       COALESCE(to_char(INTERVALO_AJ2.DATA_HORA_INICIO AT TIME ZONE INTERVALO_AJ2.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS INICIO_INTERVALO_AJ2,
       COALESCE(to_char(INTERVALO_AJ2.DATA_HORA_FIM AT TIME ZONE INTERVALO_AJ2.TZ_UNIDADE, 'HH24:MI'),
                '-')                 AS FIM_INTERVALO_AJ2,
       F_IF(INTERVALO_AJ2.DEVICE_IMEI_INICIO_RECONHECIDO AND INTERVALO_AJ2.DEVICE_IMEI_FIM_RECONHECIDO, TRUE,
            FALSE)                   AS MARCACOES_RECONHECIDAS_AJ2,
       coalesce(to_minutes_trunc(INTERVALO_AJ2.DATA_HORA_FIM - INTERVALO_AJ2.DATA_HORA_INICIO) :: TEXT,
                '-')                 AS TEMPO_DECORRIDO_MINUTOS_AJ2,
       CASE
           WHEN (INTERVALO_AJ2.DATA_HORA_FIM IS NULL)
               THEN '-'
           WHEN (TIPO_AJ2.TEMPO_RECOMENDADO_MINUTOS >
                 to_minutes_trunc(INTERVALO_AJ2.DATA_HORA_FIM - INTERVALO_AJ2.DATA_HORA_INICIO))
               THEN 'NÃO'
           ELSE 'SIM'
           END                       AS AJ2_CUMPRIU_TEMPO_MINIMO
FROM MAPA M
         JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP
              ON UFP.COD_UNIDADE = M.COD_UNIDADE
         JOIN COLABORADOR MOT
              ON MOT.COD_UNIDADE = M.COD_UNIDADE
                  AND MOT.COD_FUNCAO = UFP.COD_FUNCAO_MOTORISTA
                  AND MOT.MATRICULA_AMBEV = M.MATRICMOTORISTA
         LEFT JOIN COLABORADOR AJ1
                   ON AJ1.COD_UNIDADE = M.COD_UNIDADE
                       AND AJ1.COD_FUNCAO = UFP.COD_FUNCAO_AJUDANTE
                       AND AJ1.MATRICULA_AMBEV = M.MATRICAJUD1
         LEFT JOIN COLABORADOR AJ2
                   ON AJ2.COD_UNIDADE = M.COD_UNIDADE
                       AND AJ2.COD_FUNCAO = UFP.COD_FUNCAO_AJUDANTE
                       AND AJ2.MATRICULA_AMBEV = M.MATRICAJUD2
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_MOT
                   ON INTERVALO_MOT.CPF_COLABORADOR = MOT.CPF
                       AND INTERVALO_MOT.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_MOT.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_MOT
                   ON TIPO_MOT.CODIGO = INTERVALO_MOT.COD_TIPO_INTERVALO
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_AJ1
                   ON INTERVALO_AJ1.CPF_COLABORADOR = AJ1.CPF
                       AND INTERVALO_AJ1.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_AJ1.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_AJ1
                   ON TIPO_AJ1.CODIGO = INTERVALO_AJ1.COD_TIPO_INTERVALO
         LEFT JOIN INTERVALOS_AGRUPADOS INTERVALO_AJ2
                   ON INTERVALO_AJ2.CPF_COLABORADOR = AJ2.CPF
                       AND INTERVALO_AJ2.DATA_INICIO_TZ = M.DATA
                       AND INTERVALO_AJ2.DATA_FIM_TZ = M.DATA
         LEFT JOIN INTERVALO_TIPO TIPO_AJ2
                   ON TIPO_AJ2.CODIGO = INTERVALO_AJ2.COD_TIPO_INTERVALO
WHERE M.COD_UNIDADE = F_COD_UNIDADE
  AND M.DATA BETWEEN F_DATA_INICIAL AND F_DATA_FINAL;
$$;