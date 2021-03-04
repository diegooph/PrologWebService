CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(
  F_COD_UNIDADE                 BIGINT,
  F_COD_TIPO_INTERVALO          BIGINT,
  F_CPF_COLABORADOR             BIGINT,
  F_DATA_INICIAL                DATE,
  F_DATA_FINAL                  DATE,
  F_APENAS_COLABORADORES_ATIVOS BOOLEAN,
  F_TIME_ZONE_UNIDADE           TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                TEXT,
    NOME_COLABORADOR               TEXT,
    COD_MARCACAO_JORNADA           BIGINT,
    DIA_BASE                       DATE,
    COD_TIPO_INTERVALO             BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    DIFERENCA_MARCACOES_SEGUNDOS   BIGINT,
    TEMPO_NOTURNO_EM_SEGUNDOS      BIGINT,
    MARCACAO_INICIO_AJUSTADA       BOOLEAN,
    MARCACAO_FIM_AJUSTADA          BOOLEAN,
    TROCOU_DIA                     BOOLEAN,
    TIPO_JORNADA                   BOOLEAN,
    DESCONTA_JORNADA_BRUTA         BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA       BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPOS_DESCONTADOS_BRUTA   BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_BRUTA);
  COD_TIPOS_DESCONTADOS_LIQUIDA BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_LIQUIDA);
  -- Usamos o 'E' no começo das duas linhas para escapar os \n.
  ERROR_MESSAGE_TSTZRANGE TEXT := E'Erro!\nA marcação do colaborador <b>%s</b> possui fim antes do início, ' ||
                                  E'impossibilitando a geração do relatório.\n<b>Início: %s  </b>\n<b>Fim: %s  </b>\n\n' ||
                                  '<a href="https://prologapp.zendesk.com/hc/pt-br/articles/360002008792-Relat%%C3%%B3rios-Controle-de-Jornada#%%E2%%80%%9Cfolha" ' ||
                                  'target="_blank">Clique aqui para mais informações </ a >';
  ERROR_DATE_FORMAT TEXT := 'DD/MM/YYYY HH24:MI:SS';
BEGIN
  RETURN QUERY
  WITH TODAS_MARCACOES_UNIDADE AS (
      SELECT
        C.NOME                                                   AS NOME_COLABORADOR,
        F.CPF_COLABORADOR                                        AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                                     AS COD_TIPO_INTERVALO,
        F.COD_TIPO_INTERVALO_POR_UNIDADE                         AS COD_TIPO_INTERVALO_POR_UNIDADE,
        F_IF(F.STATUS_ATIVO_INICIO, F.COD_MARCACAO_INICIO, NULL) AS COD_MARCACAO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.COD_MARCACAO_FIM, NULL)       AS COD_MARCACAO_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL)    AS DATA_HORA_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL)          AS DATA_HORA_FIM,
        F_IF(F.STATUS_ATIVO_INICIO, F.FOI_AJUSTADO_INICIO, NULL) AS FOI_AJUSTADO_INICIO,
        F_IF(F.STATUS_ATIVO_FIM, F.FOI_AJUSTADO_FIM, NULL)       AS FOI_AJUSTADO_FIM,
        F.TIPO_JORNADA                                           AS TIPO_JORNADA
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) AS F
        -- O JOIN com colaborador é feito na primeira CTE para além de evitarmos processar colaboradores inativos
        -- (caso esse seja o filtro), também evitarmos de dar um erro de TSRANGE para um colaborador inativo.
        JOIN COLABORADOR C
          ON F.CPF_COLABORADOR = C.CPF
      WHERE
        (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
        AND F_IF(F_APENAS_COLABORADORES_ATIVOS IS NULL, TRUE, C.STATUS_ATIVO)
  ),

    -- MARCAÇÕES DE JORNADA QUE POSSUEM INÍCIO E FIM MARCADOS.
      APENAS_JORNADAS_COMPLETAS AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL)
                   OR
                   (TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) < F_DATA_INICIAL
                    AND TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) > F_DATA_FINAL))
    ),

    -- MARCAÇÕES DE JORNADA QUE NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM.
      APENAS_JORNADAS_INCOMPLETAS AS (
        SELECT
          TDU.NOME_COLABORADOR                                                            AS NOME_COLABORADOR,
          COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)                         AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE TDU.TIPO_JORNADA = TRUE
              AND (TDU.DATA_HORA_INICIO IS NULL
                   OR TDU.DATA_HORA_FIM IS NULL)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO E FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          AJC.COD_MARCACAO_INICIO                            AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> PROLOG_TSTZRANGE(
                   TDU.DATA_HORA_INICIO,
                   TDU.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(TDU.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(TDU.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
               AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
               AND TDU.DATA_HORA_INICIO IS NOT NULL
               AND TDU.DATA_HORA_FIM IS NOT NULL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> TDU.DATA_HORA_INICIO
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM FIM DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
               @> TDU.DATA_HORA_FIM
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
        WHERE TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              -- Se uma marcação está na CTE MARCACOES_COMPLETAS_DENTRO_JORNADA, ela com certeza será trazida nessa query,
              -- mas precisamos garantir que não será retornada nesse caso.
              AND (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                              WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
              -- Se uma marcação teve início dentro de uma jornada e fim dentro de outra, então ela foi adiciona à uma
              -- jornada na CTE MARCACOES_INICIO_DENTRO_JORNADA e agora seria adicionada novamente a outra jornada,
              -- precisamos impedir isso de acontecer.
              AND (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                              FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                              WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM))
    ),

    -- MARCAÇÕES QUE OU NÃO POSSUEM INÍCIO OU NÃO POSSUEM FIM MAS ESTÃO DENTRO DE UMA JORNADA.
      MARCACOES_INCOMPLETAS_DENTRO_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(AJC.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM APENAS_JORNADAS_COMPLETAS AJC
          JOIN TODAS_MARCACOES_UNIDADE TDU
            ON (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
               AND PROLOG_TSTZRANGE(
                   AJC.DATA_HORA_INICIO,
                   AJC.DATA_HORA_FIM,
                   FORMAT(
                       ERROR_MESSAGE_TSTZRANGE,
                       TDU.NOME_COLABORADOR,
                       TO_CHAR(AJC.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT),
                       TO_CHAR(AJC.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE, ERROR_DATE_FORMAT)))
                   @> COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS E NÃO TÊM INÍCIO E NEM FIM DENTRO DE NENHUMA JORNADA.
      MARCACOES_COMPLETAS_FORA_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                               AS NOME_COLABORADOR,
          NULL :: BIGINT                                     AS COD_MARCACAO_JORNADA,
          TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                             AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                 AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                            AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                               AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                               AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                  AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                            AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                               AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                   AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TDU.DATA_HORA_INICIO IS NOT NULL
              AND TDU.DATA_HORA_FIM IS NOT NULL
              AND ((TZ_DATE(TDU.DATA_HORA_INICIO, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
                    OR
                    TZ_DATE(TDU.DATA_HORA_FIM, F_TIME_ZONE_UNIDADE) BETWEEN F_DATA_INICIAL AND F_DATA_FINAL))
              AND ((NOT EXISTS(SELECT MCDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_DENTRO_JORNADA MCDJ
                               WHERE MCDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCIDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA MCIDJ
                               WHERE MCIDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
              AND ((NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_INICIO
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_INICIO = TDU.COD_MARCACAO_INICIO))
                   AND
                   (NOT EXISTS(SELECT MCFDJ.COD_MARCACAO_FIM
                               FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA MCFDJ
                               WHERE MCFDJ.COD_MARCACAO_FIM = TDU.COD_MARCACAO_FIM)))
    ),

    -- MARCAÇÕES QUE POSSUEM APENAS INÍCIO OU FIM, NÃO AMBOS, E ESTÃO FORA DE JORNADA.
      MARCACOES_INCOMPLETAS_FORA_JORNADA AS (
        SELECT
          TDU.NOME_COLABORADOR                                                            AS NOME_COLABORADOR,
          NULL :: BIGINT                                                                  AS COD_MARCACAO_JORNADA,
          TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE) AS DIA_BASE,
          TDU.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
          TDU.COD_TIPO_INTERVALO                                                          AS COD_TIPO_INTERVALO,
          TDU.COD_TIPO_INTERVALO_POR_UNIDADE                                              AS COD_TIPO_INTERVALO_POR_UNIDADE,
          TDU.COD_MARCACAO_INICIO                                                         AS COD_MARCACAO_INICIO,
          TDU.COD_MARCACAO_FIM                                                            AS COD_MARCACAO_FIM,
          TDU.DATA_HORA_INICIO                                                            AS DATA_HORA_INICIO,
          TDU.DATA_HORA_FIM                                                               AS DATA_HORA_FIM,
          TDU.FOI_AJUSTADO_INICIO                                                         AS FOI_AJUSTADO_INICIO,
          TDU.FOI_AJUSTADO_FIM                                                            AS FOI_AJUSTADO_FIM,
          TDU.TIPO_JORNADA                                                                AS TIPO_JORNADA
        FROM TODAS_MARCACOES_UNIDADE TDU
        WHERE (TDU.DATA_HORA_INICIO IS NULL OR TDU.DATA_HORA_FIM IS NULL)
              AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
              AND TZ_DATE(COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM), F_TIME_ZONE_UNIDADE)
              BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
              -- Nossa maneira de garantir que a marcação está fora de jornada, é garantindo que ela não está dentro
              -- através das CTEs criadas anteriormente que contém as marcações incompletas dentro de jornada.
              AND (NOT EXISTS(SELECT COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM)
                              FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA MIDJ
                              -- Se nossas marcações de início e fim tivessem códigos diferentes, isso não funcionaria,
                              -- mas como o código é sequencial, não importa se compararmos um início com um fim ou
                              -- vice versa, porque os códigos não vão bater.
                              WHERE COALESCE(MIDJ.COD_MARCACAO_INICIO, MIDJ.COD_MARCACAO_FIM) =
                                    COALESCE(TDU.COD_MARCACAO_INICIO, TDU.COD_MARCACAO_FIM)))
    ),

      TODAS_MARCACOES AS (
      SELECT *
      FROM APENAS_JORNADAS_COMPLETAS
      UNION ALL
      SELECT *
      FROM APENAS_JORNADAS_INCOMPLETAS
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FIM_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_COMPLETAS_FORA_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_DENTRO_JORNADA
      UNION ALL
      SELECT *
      FROM MARCACOES_INCOMPLETAS_FORA_JORNADA
    )

  SELECT
    LPAD(TM.CPF_COLABORADOR :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
    TM.NOME_COLABORADOR :: TEXT                                        AS NOME_COLABORADOR,
    TM.COD_MARCACAO_JORNADA                                            AS COD_MARCACAO_JORNADA,
    TM.DIA_BASE                                                        AS DIA_BASE,
    TM.COD_TIPO_INTERVALO                                              AS COD_TIPO_INTERVALO,
    TM.COD_TIPO_INTERVALO_POR_UNIDADE                                  AS COD_TIPO_INTERVALO_POR_UNIDADE,
    TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE               AS DATA_HORA_INICIO,
    TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE                  AS DATA_HORA_FIM,
    TO_SECONDS(TM.DATA_HORA_FIM - TM.DATA_HORA_INICIO)                 AS DIFERENCA_MARCACOES_SEGUNDOS,
    COALESCE(
        FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(TM.DATA_HORA_INICIO,
                                                               TM.DATA_HORA_FIM,
                                                               F_TIME_ZONE_UNIDADE),
        0)                                                             AS TEMPO_NOTURNO_EM_SEGUNDOS,
    TM.FOI_AJUSTADO_INICIO                                             AS FOI_AJUSTADO_INICIO,
    TM.FOI_AJUSTADO_FIM                                                AS FOI_AJUSTADO_FIM,
    TM.DATA_HORA_INICIO IS NOT NULL AND TM.DATA_HORA_FIM IS NOT NULL
    AND (TM.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE
        != (TM.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE AS TROCOU_DIA,
    TM.TIPO_JORNADA                                                    AS TIPO_JORNADA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_BRUTA)          AS DESCONTA_JORNADA_BRUTA,
    TM.COD_TIPO_INTERVALO = ANY (COD_TIPOS_DESCONTADOS_LIQUIDA)        AS DESCONTA_JORNADA_LIQUIDA
  FROM TODAS_MARCACOES AS TM
  ORDER BY TM.CPF_COLABORADOR,
    TM.DIA_BASE,
    TM.COD_MARCACAO_JORNADA ASC,
    COALESCE(TM.DATA_HORA_INICIO, TM.DATA_HORA_FIM) ASC;
END;
$$;