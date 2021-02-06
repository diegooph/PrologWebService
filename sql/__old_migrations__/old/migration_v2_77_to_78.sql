BEGIN TRANSACTION ;
--######################################################################################################################
--######################################################################################################################
--############################ ADICIONE FONTE DATA/HORA AO RELATÓRIO ###################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_RELATORIO_MARCACAO_PONTO_REALIZADOS(
  F_COD_UNIDADE  BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE,
  F_CPF          TEXT);

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(
  F_COD_UNIDADE  BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE,
  F_CPF          TEXT)
  RETURNS TABLE(
    "NOME"                                         TEXT,
    "MATRÍCULA TRANSPORTADORA"                     TEXT,
    "MATRÍCULA AMBEV"                              TEXT,
    "CARGO"                                        TEXT,
    "SETOR"                                        TEXT,
    "INTERVALO"                                    TEXT,
    "INICIO INTERVALO"                             TEXT,
    "LATITUDE INÍCIO"                              TEXT,
    "LONGITUDE INÍCIO"                             TEXT,
    "FONTE DATA/HORA INÍCIO"                       TEXT,
    "FIM INTERVALO"                                TEXT,
    "LATITUDE FIM"                                 TEXT,
    "LONGITUDE FIM"                                TEXT,
    "FONTE DATA/HORA FIM"                          TEXT,
    "TEMPO DECORRIDO (MINUTOS)"                    TEXT,
    "TEMPO RECOMENDADO (MINUTOS)"                  BIGINT,
    "CUMPRIU TEMPO MÍNIMO"                         TEXT,
    "JUSTIFICATIVA NÃO CUMPRIMENTO TEMPO MÍNIMO"   TEXT,
    "JUSTIFICATIVA ESTOURO TEMPO MÁXIMO PERMITIDO" TEXT,
    "DISTANCIA ENTRE INÍCIO E FIM (METROS)"        TEXT)
LANGUAGE SQL
AS $$
SELECT
  C.NOME                                   AS NOME_COLABORADOR,
  COALESCE(C.MATRICULA_TRANS :: TEXT, '-') AS MATRICULA_TRANS,
  COALESCE(C.MATRICULA_AMBEV :: TEXT, '-') AS MATRICULA_AMBEV,
  F.NOME                                   AS CARGO,
  S.NOME                                   AS SETOR,
  IT.NOME                                  AS INTERVALO,
  COALESCE(TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE
                                                    FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                   'DD/MM/YYYY HH24:MI:SS'),
           '')                             AS DATA_HORA_INICIO,

  I.LATITUDE_MARCACAO_INICIO :: TEXT       AS LATITUDE_INICIO,
  I.LONGITUDE_MARCACAO_INICIO :: TEXT      AS LONGITUDE_INICIO,
  I.FONTE_DATA_HORA_INICIO,

  COALESCE(TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE
                                                 FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                   'DD/MM/YYYY HH24:MI:SS'),
           '')                             AS DATA_HORA_FIM,

  I.LATITUDE_MARCACAO_FIM :: TEXT          AS LATITUDE_FIM,
  I.LONGITUDE_MARCACAO_FIM :: TEXT         AS LONGITUDE_FIM,
  I.FONTE_DATA_HORA_FIM,

  COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60) :: TEXT,
           '')                             AS TEMPO_DECORRIDO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS,
  CASE WHEN I.DATA_HORA_FIM IS NULL OR I.DATA_HORA_INICIO IS NULL
    THEN ''
  WHEN IT.TEMPO_RECOMENDADO_MINUTOS > (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
    THEN
      'NÃO'
  ELSE 'SIM' END                           AS CUMPRIU_TEMPO_MINIMO,
  I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
  I.JUSTIFICATIVA_ESTOURO,

  COALESCE(TRUNC((ST_DISTANCE(
      ST_POINT(I.LONGITUDE_MARCACAO_INICIO :: FLOAT, I.LATITUDE_MARCACAO_INICIO :: FLOAT) :: GEOGRAPHY,
      ST_POINT(I.LONGITUDE_MARCACAO_FIM :: FLOAT, I.LATITUDE_MARCACAO_FIM :: FLOAT) :: GEOGRAPHY))) :: TEXT,
           '-')                            AS DISTANCIA
FROM
      FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, CASE WHEN F_CPF = '%'
        THEN NULL
                                               ELSE F_CPF :: BIGINT END, NULL) I
  JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
  JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE AND C.COD_EMPRESA = U.COD_EMPRESA
  JOIN FUNCAO F ON F.COD_EMPRESA = U.COD_EMPRESA AND F.CODIGO = C.COD_FUNCAO
  JOIN SETOR S ON S.COD_UNIDADE = C.COD_UNIDADE AND S.CODIGO = C.COD_SETOR
WHERE ((I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE
                                         FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
       BETWEEN (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE
                                             FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
       AND (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE
                                       FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
       OR (I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE
                                         FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
       BETWEEN (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE
                                             FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
       AND (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE
                                       FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))))
ORDER BY I.DATA_HORA_INICIO, C.NOME
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### CRIA FUNCTION PARA EXIBIR DADOS GERAIS DE CHECKLIST REALIZADOS #############################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES     BIGINT[],
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_PLACA            TEXT,
  F_NOME_COLABORADOR TEXT)
  RETURNS TABLE(
    UNIDADE                        TEXT,
    "DATA E HORA DE REALIZAÇÃO"    TEXT,
    "CPF DO COLABORADOR"           TEXT,
    NOME                           TEXT,
    "TIPO DE VEÍCULO"              TEXT,
    PLACA                          TEXT,
    "TIPO DE CHECKLIST"            TEXT,
    "NOME"                         TEXT,
    PERGUNTA                       TEXT,
    ALTERNATIVA                    TEXT,
    RESPOSTA                       TEXT,
    PRIORIDADE                     TEXT,
    "TIPO DE RESPOSTA"             TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                             AS NOME_UNIDADE,
  TO_CHAR(CL.DATA_HORA, 'DD/MM/YYYY HH24:MI:SS')                     AS DATA_HORA_REALIZACAO,
  CL.CPF_COLABORADOR :: TEXT,
  CO.NOME                                                            AS NOME_COLABORADOR,
  VT.NOME                                                            AS NOME_TIPO_VEICULO,
  CL.PLACA_VEICULO,
  CASE WHEN CL.TIPO = 'S' THEN 'SAÍDA' ELSE 'RETORNO' END            AS TIPO_CHECKLIST,
  CM.NOME                                                            AS NOME_MODELO_CHECKLIST,
  CP.PERGUNTA,
  CAP.ALTERNATIVA,
  CR.RESPOSTA,
  CP.PRIORIDADE,
  CASE WHEN CP.SINGLE_CHOICE = TRUE THEN 'ÚNICA' ELSE 'MÚLTIPLA' END AS TIPO_RESPOSTA
FROM CHECKLIST_PERGUNTAS CP
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.COD_UNIDADE = CAP.COD_UNIDADE
    AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
    AND CP.CODIGO = CAP.COD_PERGUNTA
  INNER JOIN CHECKLIST_RESPOSTAS CR ON CAP.CODIGO = CR.COD_ALTERNATIVA
    AND CR.COD_PERGUNTA = CP.CODIGO
  INNER JOIN CHECKLIST_MODELO CM ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
  INNER JOIN UNIDADE U ON CAP.COD_UNIDADE = U.CODIGO AND CP.COD_UNIDADE = U.CODIGO AND CR.COD_UNIDADE = U.CODIGO
  INNER JOIN CHECKLIST CL ON CR.COD_CHECKLIST = CL.CODIGO AND CL.COD_UNIDADE = U.CODIGO
  INNER JOIN COLABORADOR CO ON U.CODIGO = CO.COD_UNIDADE AND CO.CPF = CL.CPF_COLABORADOR
  INNER JOIN VEICULO_TIPO VT ON U.CODIGO = VT.COD_UNIDADE AND VT.CODIGO =
    (SELECT V.COD_TIPO FROM VEICULO V WHERE V.PLACA = CL.PLACA_VEICULO)
WHERE U.CODIGO = ANY  (F_COD_UNIDADES)
  AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE))::DATE >= F_DATA_INICIAL
  AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE))::DATE <= F_DATA_FINAL
  AND (CASE WHEN F_PLACA IS NOT NULL THEN CL.PLACA_VEICULO = F_PLACA ELSE CL.PLACA_VEICULO IS NOT NULL END)
  AND (CASE WHEN F_NOME_COLABORADOR IS NOT NULL THEN CO.NOME = F_NOME_COLABORADOR ELSE CO.NOME IS NOT NULL END)
ORDER BY U.NOME, CL.DATA_HORA, CL.PLACA_VEICULO, CM.NOME,CP.PERGUNTA,CAP.ALTERNATIVA;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;