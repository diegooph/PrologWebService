CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_COLABORADORES_JORNADA_EM_ANDAMENTO(
  F_COD_UNIDADE         BIGINT,
  F_COD_CARGOS          BIGINT [],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    NOME_COLABORADOR                           TEXT,
    CPF_COLABORADOR                            BIGINT,
    COD_INICIO_JORNADA                         BIGINT,
    DATA_HORA_INICIO_JORNADA                   TIMESTAMP WITHOUT TIME ZONE,
    FOI_AJUSTADO_INICIO_JORNADA                BOOLEAN,
    DURACAO_JORNADA_SEM_DESCONTOS_SEGUNDOS     BIGINT,
    DESCONTOS_JORNADA_BRUTA_SEGUNDOS           BIGINT,
    DURACAO_JORNADA_BRUTA_SEGUNDOS             BIGINT,
    DESCONTOS_JORNADA_LIQUIDA_SEGUNDOS         BIGINT,
    DURACAO_JORNADA_LIQUIDA_SEGUNDOS           BIGINT,
    COD_MARCACAO_INICIO                        BIGINT,
    COD_MARCACAO_FIM                           BIGINT,
    DATA_HORA_INICIO                           TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                              TIMESTAMP WITHOUT TIME ZONE,
    MARCACAO_EM_ANDAMENTO                      BOOLEAN,
    DURACAO_MARCACAO_SEGUNDOS                  BIGINT,
    FOI_AJUSTADO_INICIO                        BOOLEAN,
    FOI_AJUSTADO_FIM                           BOOLEAN,
    COD_TIPO_MARCACAO                          BIGINT,
    NOME_TIPO_MARCACAO                         TEXT,
    TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_JORNADA              BIGINT := (SELECT VIT.CODIGO
                                           FROM VIEW_INTERVALO_TIPO VIT
                                           WHERE VIT.COD_UNIDADE = F_COD_UNIDADE AND VIT.TIPO_JORNADA);
  TZ_UNIDADE                    TEXT := (SELECT U.TIMEZONE
                                         FROM UNIDADE U
                                         WHERE U.CODIGO = F_COD_UNIDADE);
  COD_TIPOS_DESCONTADOS_BRUTA   BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_BRUTA);
  COD_TIPOS_DESCONTADOS_LIQUIDA BIGINT [] := (SELECT ARRAY_AGG(COD_TIPO_DESCONTADO)
                                              FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
                                              WHERE M.COD_UNIDADE = F_COD_UNIDADE AND M.DESCONTA_JORNADA_LIQUIDA);
BEGIN
  IF COD_TIPO_JORNADA IS NULL OR COD_TIPO_JORNADA <= 0
  THEN RAISE EXCEPTION 'Unidade não possui nenhum tipo de marcação definido como jornada';
  END IF;

  RETURN QUERY
  WITH MARCACOES AS (
      SELECT
        INITCAP(C.NOME)                        AS NOME_COLABORADOR,
        C.CPF                                  AS CPF_COLABORADOR,
        F.COD_MARCACAO_INICIO                  AS COD_MARCACAO_INICIO,
        F.COD_MARCACAO_FIM                     AS COD_MARCACAO_FIM,
        F.DATA_HORA_INICIO                     AS DATA_HORA_INICIO,
        F.DATA_HORA_FIM                        AS DATA_HORA_FIM,
        -- Algumas comparações abaixo assumem que o status será sempre diferente de NULL.
        COALESCE(F.FOI_AJUSTADO_INICIO, FALSE) AS FOI_AJUSTADO_INICIO,
        COALESCE(F.FOI_AJUSTADO_FIM, FALSE)    AS FOI_AJUSTADO_FIM,
        COALESCE(F.STATUS_ATIVO_INICIO, FALSE) AS STATUS_ATIVO_INICIO,
        COALESCE(F.STATUS_ATIVO_FIM, FALSE)    AS STATUS_ATIVO_FIM,
        F.COD_TIPO_INTERVALO                   AS COD_TIPO_MARCACAO
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, NULL) F
        JOIN COLABORADOR C
          ON C.CPF = F.CPF_COLABORADOR
      WHERE
        C.COD_FUNCAO = ANY (F_COD_CARGOS)
        AND C.STATUS_ATIVO
  ),

      ULTIMA_MARCACAO_JORNADA_COLABORADORES AS (
        SELECT
          DISTINCT ON (M.CPF_COLABORADOR)
          M.CPF_COLABORADOR     AS CPF_COLABORADOR,
          M.NOME_COLABORADOR    AS NOME_COLABORADOR,
          M.COD_MARCACAO_INICIO AS COD_INICIO_JORNADA,
          M.COD_MARCACAO_FIM    AS COD_FIM_JORNADA,
          M.DATA_HORA_INICIO    AS DATA_HORA_INICIO_JORNADA,
          M.FOI_AJUSTADO_INICIO AS FOI_AJUSTADO_INICIO_JORNADA,
          M.STATUS_ATIVO_INICIO AS STATUS_ATIVO_INICIO_JORNADA
        FROM MARCACOES M
        WHERE M.COD_TIPO_MARCACAO = COD_TIPO_JORNADA
        ORDER BY
          M.CPF_COLABORADOR,
          COALESCE(M.COD_MARCACAO_INICIO, M.COD_MARCACAO_FIM) DESC
    ),

      JORNADAS_EM_ABERTO AS (
        SELECT
          UMJC.CPF_COLABORADOR             AS CPF_COLABORADOR,
          UMJC.NOME_COLABORADOR            AS NOME_COLABORADOR,
          UMJC.COD_INICIO_JORNADA          AS COD_INICIO_JORNADA,
          UMJC.DATA_HORA_INICIO_JORNADA    AS DATA_HORA_INICIO_JORNADA,
          UMJC.FOI_AJUSTADO_INICIO_JORNADA AS FOI_AJUSTADO_INICIO_JORNADA
        FROM ULTIMA_MARCACAO_JORNADA_COLABORADORES UMJC
        WHERE
          -- Precisamos filtrar isso apenas depois de pegarmos a última marcação de jornada de um colaborador,
          -- pois nesse caso pegaremos até mesmo marcações inativas e em andamento. Se fôssemos filtrar na CTE anterior,
          -- poderíamos acabar removendo antes as em andamento e inativas e trazer como última jornada do colaborador uma que
          -- pode ter sido finalizada a até um mês atrás. Ou mesmo uma que está inativa.
          UMJC.COD_INICIO_JORNADA IS NOT NULL
          AND UMJC.COD_FIM_JORNADA IS NULL
          AND UMJC.STATUS_ATIVO_INICIO_JORNADA
    ),

      JORNADAS_COM_MARCACOES AS (
        SELECT
          -- Pegamos CPF e nome da jornada pois o colaborador pode não ter outras marcações.
          JA.CPF_COLABORADOR                                      AS CPF_COLABORADOR,
          JA.NOME_COLABORADOR                                     AS NOME_COLABORADOR,
          M.COD_MARCACAO_INICIO                                   AS COD_MARCACAO_INICIO,
          M.COD_MARCACAO_FIM                                      AS COD_MARCACAO_FIM,
          M.DATA_HORA_INICIO                                      AS DATA_HORA_INICIO,
          M.DATA_HORA_FIM                                         AS DATA_HORA_FIM,
          M.FOI_AJUSTADO_INICIO                                   AS FOI_AJUSTADO_INICIO,
          M.FOI_AJUSTADO_FIM                                      AS FOI_AJUSTADO_FIM,
          M.STATUS_ATIVO_INICIO                                   AS STATUS_ATIVO_INICIO,
          M.STATUS_ATIVO_FIM                                      AS STATUS_ATIVO_FIM,
          M.COD_TIPO_MARCACAO                                     AS COD_TIPO_MARCACAO,
          JA.COD_INICIO_JORNADA                                   AS COD_INICIO_JORNADA,
          JA.DATA_HORA_INICIO_JORNADA                             AS DATA_HORA_INICIO_JORNADA,
          JA.FOI_AJUSTADO_INICIO_JORNADA                          AS FOI_AJUSTADO_INICIO_JORNADA,
          F_DATA_HORA_ATUAL_UTC - JA.DATA_HORA_INICIO_JORNADA     AS DURACAO_JORNADA,
          -- Precisamos garantir que só usamos na conta marcações de início e fim ativas. Se uma delas for inativa
          -- o resultado será nulo. Isso não tem problema, pois na CTE seguinte, o SUM lida com o null usando um
          -- COALESCE.
          COALESCE(F_IF(M.STATUS_ATIVO_FIM, M.DATA_HORA_FIM, NULL),
                   F_DATA_HORA_ATUAL_UTC)
          - F_IF(M.STATUS_ATIVO_INICIO, M.DATA_HORA_INICIO, NULL) AS DURACAO_MARCACAO
        FROM JORNADAS_EM_ABERTO JA
          LEFT JOIN MARCACOES M
            ON JA.CPF_COLABORADOR = M.CPF_COLABORADOR
               AND M.COD_TIPO_MARCACAO <> COD_TIPO_JORNADA
               -- Verifica se início e fim são após o início da jornada ou se apenas o início é após
               -- desde que não tenha fim, ou se é um fim avulso realizado no período da jornada.
               AND
               ((M.DATA_HORA_INICIO >= JA.DATA_HORA_INICIO_JORNADA
                 AND M.DATA_HORA_FIM >= JA.DATA_HORA_INICIO_JORNADA
                 AND M.DATA_HORA_FIM <= F_DATA_HORA_ATUAL_UTC)
                OR
                (M.DATA_HORA_INICIO >= JA.DATA_HORA_INICIO_JORNADA
                 AND M.DATA_HORA_INICIO <= F_DATA_HORA_ATUAL_UTC
                 AND M.DATA_HORA_FIM IS NULL)
                OR
                (M.DATA_HORA_INICIO IS NULL
                 AND M.DATA_HORA_FIM >= JA.DATA_HORA_INICIO_JORNADA
                 AND M.DATA_HORA_FIM <= F_DATA_HORA_ATUAL_UTC))
        WHERE
          -- Inserimos dentro da jornada apenas as marções que possuirem Início ou Fim ativos.
          -- Não podemos fazer essa verificação na cte MARCACOES para não retirar as marcações de jornadas inativas
          -- causando erro na busca. Importante lembrar que o colaborador pode ter uma jornada sem marcações dentro,
          -- por isso o F_IF é necessário.
          F_IF(M.COD_TIPO_MARCACAO IS NULL, TRUE, M.STATUS_ATIVO_INICIO) OR
          F_IF(M.COD_TIPO_MARCACAO IS NULL, TRUE, M.STATUS_ATIVO_FIM)
        ORDER BY JA.CPF_COLABORADOR, JA.DATA_HORA_INICIO_JORNADA, M.DATA_HORA_INICIO
    ),

      MARCACOES_E_DESCONTOS_JORNADA AS (
        SELECT
          JCM.*,
          -- Precisamos do coalesce pois esses dois SUMs podem retornar null, se retornarem null, quando formos
          -- descontar esses valores no SELECT abaixo do tempo total da jornada, iremos tornar o tempo total da jornada
          -- nulo também, o que está incorreto.
          COALESCE(SUM(JCM.DURACAO_MARCACAO)
                     FILTER (WHERE JCM.COD_TIPO_MARCACAO = ANY (COD_TIPOS_DESCONTADOS_BRUTA))
                   OVER CPFS, INTERVAL '0') AS DESCONTOS_JORNADA_BRUTA,
          COALESCE(SUM(JCM.DURACAO_MARCACAO)
                     FILTER (WHERE JCM.COD_TIPO_MARCACAO = ANY (COD_TIPOS_DESCONTADOS_LIQUIDA))
                   OVER CPFS, INTERVAL '0') AS DESCONTOS_JORNADA_LIQUIDA,
          -- Marcações com início e fim contam como 2, avulsas 1, sem nada 0.
          SUM(CASE
              WHEN JCM.STATUS_ATIVO_INICIO AND JCM.STATUS_ATIVO_FIM
                THEN 2
              WHEN ((JCM.STATUS_ATIVO_INICIO AND NOT JCM.STATUS_ATIVO_FIM) OR
                    (NOT JCM.STATUS_ATIVO_INICIO AND JCM.STATUS_ATIVO_FIM))
                THEN 1
              ELSE 0
              END)
          OVER CPFS                         AS TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR
        FROM JORNADAS_COM_MARCACOES JCM
        WINDOW CPFS AS (
          PARTITION BY JCM.CPF_COLABORADOR )
    )

  SELECT
    M.NOME_COLABORADOR :: TEXT                                                    AS NOME_COLABORADOR,
    M.CPF_COLABORADOR                                                             AS CPF_COLABORADOR,
    M.COD_INICIO_JORNADA                                                          AS COD_INICIO_JORNADA,
    M.DATA_HORA_INICIO_JORNADA AT TIME ZONE TZ_UNIDADE                            AS DATA_HORA_INICIO_JORNADA,
    M.FOI_AJUSTADO_INICIO_JORNADA                                                 AS FOI_AJUSTADO_INICIO_JORNADA,
    TO_SECONDS(
        M.DURACAO_JORNADA)                                                        AS DURACAO_JORNADA_SEM_DESCONTOS_SEGUNDOS,
    TO_SECONDS(M.DESCONTOS_JORNADA_BRUTA)                                         AS DESCONTOS_JORNADA_BRUTA_SEGUNDOS,
    TO_SECONDS(M.DURACAO_JORNADA
               - M.DESCONTOS_JORNADA_BRUTA)                                       AS DURACAO_JORNADA_BRUTA_SEGUNDOS,
    TO_SECONDS(M.DESCONTOS_JORNADA_LIQUIDA)                                       AS DESCONTOS_JORNADA_LIQUIDA_SEGUNDOS,
    -- A Jornada Líquida deve descontar tudo que foi descontado da bruta
    -- mais os descontos específicos da líquida.
    TO_SECONDS(M.DURACAO_JORNADA
               - M.DESCONTOS_JORNADA_BRUTA
               - M.DESCONTOS_JORNADA_LIQUIDA)                                     AS DURACAO_JORNADA_LIQUIDA_SEGUNDOS,
    F_IF(M.STATUS_ATIVO_INICIO, M.COD_MARCACAO_INICIO, NULL)                      AS COD_MARCACAO_INICIO,
    F_IF(M.STATUS_ATIVO_FIM, M.COD_MARCACAO_FIM, NULL)                            AS COD_MARCACAO_FIM,
    F_IF(M.STATUS_ATIVO_INICIO, M.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE, NULL) AS DATA_HORA_INICIO,
    F_IF(M.STATUS_ATIVO_FIM, M.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE, NULL)       AS DATA_HORA_FIM,
    M.DATA_HORA_FIM IS NULL                                                       AS MARCACAO_EM_ANDAMENTO,
    TO_SECONDS(M.DURACAO_MARCACAO)                                                AS DURACAO_MARCACAO_SEGUNDOS,
    F_IF(M.STATUS_ATIVO_INICIO, M.FOI_AJUSTADO_INICIO, FALSE)                     AS FOI_AJUSTADO_INICIO,
    F_IF(M.STATUS_ATIVO_FIM, M.FOI_AJUSTADO_FIM, FALSE)                           AS FOI_AJUSTADO_FIM,
    M.COD_TIPO_MARCACAO                                                           AS COD_TIPO_MARCACAO,
    VIT.NOME :: TEXT                                                              AS NOME_TIPO_MARCACAO,
    M.TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR                                  AS TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR
  FROM MARCACOES_E_DESCONTOS_JORNADA M
    -- LEFT JOIN pois jornada pode não ter marcações.
    LEFT JOIN VIEW_INTERVALO_TIPO VIT
      ON VIT.CODIGO = M.COD_TIPO_MARCACAO
  ORDER BY M.CPF_COLABORADOR, M.DATA_HORA_INICIO_JORNADA, COALESCE(M.DATA_HORA_INICIO, M.DATA_HORA_FIM);
END;
$$;