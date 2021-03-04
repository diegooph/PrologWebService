CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(
  F_COD_UNIDADE                 BIGINT,
  F_COD_TIPO_INTERVALO          BIGINT,
  F_CPF_COLABORADOR             BIGINT,
  F_DATA_INICIAL                DATE,
  F_DATA_FINAL                  DATE,
  F_APENAS_COLABORADORES_ATIVOS BOOLEAN,
  F_TIME_ZONE_UNIDADE           TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                BIGINT,
    NOME_COLABORADOR               TEXT,
    COD_TIPO_INTERVALO             BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_INICIO_UTC           TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_UTC              TIMESTAMP WITHOUT TIME ZONE,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN,
    DIFERENCA_MARCACOES_SEGUNDOS   DOUBLE PRECISION,
    TROCOU_DIA                     BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  F.CPF_COLABORADOR                                                                      AS CPF_COLABORADOR,
  C.NOME                                                                                 AS NOME_COLABORADOR,
  F.COD_TIPO_INTERVALO                                                                   AS COD_TIPO_INTERVALO,
  F.COD_TIPO_INTERVALO_POR_UNIDADE                                                       AS COD_TIPO_INTERVALO_POR_UNIDADE,
  F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL) AT TIME ZONE F_TIME_ZONE_UNIDADE AS DATA_HORA_INICIO,
  F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL) AT TIME ZONE F_TIME_ZONE_UNIDADE       AS DATA_HORA_FIM,
  F_IF(F.STATUS_ATIVO_INICIO, F.DATA_HORA_INICIO, NULL) AT TIME ZONE 'UTC'               AS DATA_HORA_INICIO_UTC,
  F_IF(F.STATUS_ATIVO_FIM, F.DATA_HORA_FIM, NULL) AT TIME ZONE 'UTC'                     AS DATA_HORA_FIM_UTC,
  F_IF(F.STATUS_ATIVO_INICIO, F.FOI_AJUSTADO_INICIO, NULL)                               AS FOI_AJUSTADO_INICIO,
  F_IF(F.STATUS_ATIVO_FIM, F.FOI_AJUSTADO_FIM, NULL)                                     AS FOI_AJUSTADO_FIM,
  F_IF(F.STATUS_ATIVO_INICIO AND F.STATUS_ATIVO_FIM,
       EXTRACT(EPOCH FROM (F.DATA_HORA_FIM - F.DATA_HORA_INICIO)),
       NULL)                                                                             AS DIFERENCA_MARCACOES_SEGUNDOS,
  -- Só importa essa verificação se início e fim estiverem ativos.
  F.STATUS_ATIVO_INICIO AND F.STATUS_ATIVO_FIM AND
  (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE !=
  (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE                             AS TROCOU_DIA
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  F_IF(F_APENAS_COLABORADORES_ATIVOS IS NULL, TRUE, C.STATUS_ATIVO)
  AND
  (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
  AND
  -- FILTRA POR MARCAÇÕES QUE TENHAM SEU INÍCIO DENTRO DO PERÍODO FILTRADO, NÃO IMPORTANDO SE TENHAM FIM.
  (((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
    AND (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
   OR
   -- FILTRA POR MARCAÇÕES QUE TENHAM SEU FIM DENTRO DO PERÍODO FILTRADO, NÃO IMPORTANDO SE TENHAM INÍCIO.
   ((F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
    AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
   OR
   -- FILTRA POR MARCAÇÕES QUE TIVERAM SEU INÍCIO ANTES DO FILTRO E FIM APÓS O FILTRO.
   ((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE < F_DATA_INICIAL
    AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE > F_DATA_FINAL))
ORDER BY F.CPF_COLABORADOR, COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) ASC;
$$;