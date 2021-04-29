BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--###################### ADICIONA KM DE ORIGEM E DESTINO DO VEÍCULO NO MOMENTO DA MOVIMENTAÇÃO #########################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE);

CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"               TEXT,
    "DATA E HORA"           TEXT,
    "CPF DO RESPONSÁVEL"    TEXT,
    "NOME"                  TEXT,
    "PNEU"                  TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "MEDIDAS"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "ORIGEM"                TEXT,
    "PLACA DE ORIGEM"       TEXT,
    "KM COLETADO ORIGEM"    TEXT,
    "POSIÇÃO DE ORIGEM"     TEXT,
    "DESTINO"               TEXT,
    "PLACA DE DESTINO"      TEXT,
    "KM COLETADO DESTINO"   TEXT,
    "POSIÇÃO DE DESTINO"    TEXT,
    "RECAPADORA DESTINO"    TEXT,
    "CÓDIGO COLETA"         TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME,
  TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
  LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
  C.NOME,
  P.CODIGO_CLIENTE                                                                                  AS PNEU,
  MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
  MP.NOME                                                                                           AS NOME_MODELO_PNEU,
  F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME)                      AS BANDA_APLICADA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')             AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                      AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                      AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')             AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
  PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
  O.TIPO_ORIGEM                                                                                     AS ORIGEM,
  COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
  COALESCE(O.KM_VEICULO  :: TEXT, '-')                                                              AS KM_COLETADO_ORIGEM,
  COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
  D.TIPO_DESTINO                                                                                    AS DESTINO,
  COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
  COALESCE(D.KM_VEICULO :: TEXT, '-')                                                               AS KM_COLETADO_DESTINO,
  COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
  COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
  COALESCE(D.COD_COLETA, '-')                                                                       AS COD_COLETA_RECAPADORA
FROM
  MOVIMENTACAO_PROCESSO MOVP
  JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
  JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
  JOIN PNEU P ON P.CODIGO = M.COD_PNEU
  JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
  JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
  JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL

  -- Terá recapadora apenas se foi movido para análise.
  LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

  -- Pode não possuir banda.
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

  -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
  LEFT JOIN VEICULO VORIGEM
    ON O.PLACA = VORIGEM.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_ORIGEM
    ON NOMENCLATURA_ORIGEM.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_ORIGEM.COD_TIPO_VEICULO = VORIGEM.COD_TIPO
       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

  -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
  LEFT JOIN VEICULO VDESTINO
    ON D.PLACA = VDESTINO.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_DESTINO
    ON NOMENCLATURA_DESTINO.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_DESTINO.COD_TIPO_VEICULO = VDESTINO.COD_TIPO
       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--########################### CORREÇÃO DE INCONSISTENCIAS DE SERVIÇOS DE MOVIEMNTAÇÃO ##################################
--######################################################################################################################
--######################################################################################################################
-- CORRIGE INCONSISTENCIAS COM SERVIÇOS DE MOVIMENTAÇÃO NA UNIDADE DE TESTE
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 1763 WHERE CODIGO = 1258 AND COD_UNIDADE = 5;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 1767 WHERE CODIGO = 1261 AND COD_UNIDADE = 5;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 2539 WHERE CODIGO = 1263 AND COD_UNIDADE = 5;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 1772 WHERE CODIGO = 1445 AND COD_UNIDADE = 5;

-- CORRIGE INCONSISTENCIAS COM SERVIÇOS DE MOVIMENTAÇÃO NA UNIDADE DE FLORIANÓPOLIS - CONLOG
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 6939 WHERE CODIGO = 2062 AND COD_UNIDADE = 20;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 6939 WHERE CODIGO = 2066 AND COD_UNIDADE = 20;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 517 WHERE CODIGO = 2069 AND COD_UNIDADE = 20;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 6939 WHERE CODIGO = 2060 AND COD_UNIDADE = 20;
UPDATE AFERICAO_MANUTENCAO SET COD_PNEU_INSERIDO = 517 WHERE CODIGO = 2065 AND COD_UNIDADE = 20;

-- CRIA CONSTRAINT NA TABELA PARA EVITAR QUE ESSA INCONSISTÊNCIA VOLTE A ACONTECER
ALTER TABLE AFERICAO_MANUTENCAO ADD CONSTRAINT CHECK_ESTADOS_SERVICOS
CHECK (CASE
       WHEN TIPO_SERVICO = 'movimentacao'
         THEN (((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 COD_PNEU_INSERIDO,
                 COD_PROCESSO_MOVIMENTACAO,
                 TEMPO_REALIZACAO_MILLIS,
                 FECHADO_AUTOMATICAMENTE_MOVIMENTACAO) IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 COD_PNEU_INSERIDO,
                 COD_PROCESSO_MOVIMENTACAO,
                 TEMPO_REALIZACAO_MILLIS) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 COD_PROCESSO_MOVIMENTACAO) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE))
       WHEN TIPO_SERVICO = 'calibragem'
         THEN (((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 COD_PNEU_INSERIDO,
                 COD_PROCESSO_MOVIMENTACAO,
                 TEMPO_REALIZACAO_MILLIS,
                 FECHADO_AUTOMATICAMENTE_MOVIMENTACAO) IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 TEMPO_REALIZACAO_MILLIS) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 COD_PROCESSO_MOVIMENTACAO) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE))
       WHEN TIPO_SERVICO = 'inspecao'
         THEN (((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 COD_PNEU_INSERIDO,
                 COD_PROCESSO_MOVIMENTACAO,
                 TEMPO_REALIZACAO_MILLIS,
                 FECHADO_AUTOMATICAMENTE_MOVIMENTACAO) IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 CPF_MECANICO,
                 PSI_APOS_CONSERTO,
                 KM_MOMENTO_CONSERTO,
                 COD_ALTERNATIVA,
                 TEMPO_REALIZACAO_MILLIS) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL) OR
               ((DATA_HORA_RESOLUCAO,
                 COD_PROCESSO_MOVIMENTACAO) IS NOT NULL AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE))
       ELSE FALSE END);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### REMOVE A FUNCTION DE RELATÓRIO DE CRONOGRAMA DE AFERICOES ##################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(BIGINT[], TIMESTAMP WITH TIME ZONE);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### CRIA A FUNCTION DE RELATÓRIO DE CRONOGRAMA DE AFERICOES ##################################
--################################## AGORA COM DATA/HORA DE GERAÇÃO DO RELATÓRIO #######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(
  F_COD_UNIDADES                BIGINT [],
  F_DATA_HORA_ATUAL_UTC         TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    UNIDADE                              TEXT,
    PLACA                                TEXT,
    "QTD PNEUS APLICADOS"                TEXT,
    "MODELO VEÍCULO"                     TEXT,
    "TIPO VEÍCULO"                       TEXT,
    "STATUS SULCO"                       TEXT,
    "STATUS PRESSÃO"                     TEXT,
    "DATA VENCIMENTO SULCO"              TEXT,
    "DATA VENCIMENTO PRESSÃO"            TEXT,
    "DIAS VENCIMENTO SULCO"              TEXT,
    "DIAS VENCIMENTO PRESSÃO"            TEXT,
    "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
    "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
    "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
BEGIN
  RETURN QUERY
  WITH DADOS AS (SELECT
                   U.NOME :: TEXT        AS NOME_UNIDADE,
                   V.PLACA :: TEXT       AS PLACA_VEICULO,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.PLACA = V.PLACA
                    GROUP BY
                      VP.PLACA) :: TEXT  AS QTD_PNEUS_APLICADOS,
                   MV.NOME :: TEXT       AS NOME_MODELO_VEICULO,
                   VT.NOME :: TEXT       AS NOME_TIPO_VEICULO,
                   TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO + (PRU.PERIODO_AFERICAO_SULCO ||
                                                               ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO + (PRU.PERIODO_AFERICAO_PRESSAO ||
                                                                   ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_PRESSAO,
                   (PRU.PERIODO_AFERICAO_SULCO -
                    SULCO.DIAS) :: TEXT  AS DIAS_VENCIMENTO_SULCO,
                   (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS) :: TEXT
                                         AS DIAS_VENCIMENTO_PRESSAO,
                   SULCO.DIAS :: TEXT    AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   PRESSAO.DIAS :: TEXT  AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   F_IF(VCTA.PODE_AFERIR_SULCO OR VCTA.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_SULCO,
                   F_IF(VCTA.PODE_AFERIR_PRESSAO OR VCTA.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_PRESSAO,
                   F_IF(SULCO.DIAS IS NULL, TRUE,
                        FALSE)           AS SULCO_NUNCA_AFERIDO,
                   F_IF(PRESSAO.DIAS IS NULL, TRUE,
                        FALSE)           AS PRESSAO_NUNCA_AFERIDA,
                   F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                        FALSE)           AS AFERICAO_SULCO_VENCIDA,
                   F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                        FALSE)           AS AFERICAO_PRESSAO_VENCIDA
                 FROM VEICULO V
                   JOIN MODELO_VEICULO MV
                     ON MV.CODIGO = V.COD_MODELO
                   JOIN VEICULO_TIPO VT
                     ON VT.CODIGO = V.COD_TIPO
                   JOIN VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO VCTA ON VCTA.COD_TIPO_VEICULO = V.COD_TIPO
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                               AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                        AS DATA_ULTIMA_AFERICAO_PRESSAO,
                      -- TODO: TENHO DÚVIDAS SOBRE ESSA SUBTRAÇÃO AQUI :thinking_face:
                      EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS PRESSAO ON PRESSAO.PLACA_INTERVALO = V.PLACA
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                             AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                      -- TODO: TENHO DÚVIDAS SOBRE ESSA SUBTRAÇÃO AQUI :thinking_face:
                      EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS SULCO ON SULCO.PLACA_INTERVALO = V.PLACA
                   JOIN PNEU_RESTRICAO_UNIDADE PRU
                     ON PRU.COD_UNIDADE = V.COD_UNIDADE
                   JOIN UNIDADE U
                     ON U.CODIGO = V.COD_UNIDADE
                 WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                 ORDER BY U.CODIGO ASC, V.PLACA ASC)

  -- TODOS OS COALESCE FICAM AQUI.
  SELECT
    D.NOME_UNIDADE                                               AS NOME_UNIDADE,
    D.PLACA_VEICULO                                              AS PLACA_VEICULO,
    COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
    D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
    D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
    CASE
    WHEN D.SULCO_NUNCA_AFERIDO
      THEN 'SULCO NUNCA AFERIDO'
    WHEN NOT D.PODE_AFERIR_SULCO
      THEN 'BLOQUEADO AFERIÇÃO'
    WHEN D.AFERICAO_SULCO_VENCIDA
      THEN 'VENCIDO'
    ELSE 'NO PRAZO'
    END                                                          AS STATUS_SULCO,
    CASE
    WHEN D.PRESSAO_NUNCA_AFERIDA
      THEN 'PRESSÃO NUNCA AFERIDA'
    WHEN NOT D.PODE_AFERIR_PRESSAO
      THEN 'BLOQUEADO AFERIÇÃO'
    WHEN D.AFERICAO_PRESSAO_VENCIDA
      THEN 'VENCIDO'
    ELSE 'NO PRAZO'
    END                                                          AS STATUS_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
    TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
  FROM DADOS D;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############### Essa function teve que ser lançada em prod alterando os valores retornados pelo ######################
--############### para retornar pressão no lugar do sulco e vice versa, pois o servidor estava lendo ao contrário. #####
--############### Agora ela pode ser corrigida, pois o servidor será corrigido também nesse release. ###################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_AFERICOES_REALIZADAS_POR_DIA(
  F_COD_UNIDADES BIGINT[],
  F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE,
  F_DIAS_RETROATIVOS_PARA_BUSCAR BIGINT)
  RETURNS TABLE(
    DATA DATE,
    DATA_FORMATADA TEXT,
    QTD_AFERICAO_SULCO BIGINT,
    QTD_AFERICAO_PRESSAO BIGINT,
    QTD_AFERICAO_SULCO_PRESSAO BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL            DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY
                                  - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL              DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
  AFERICAO_SULCO          VARCHAR := 'SULCO';
  AFERICAO_PRESSAO        VARCHAR := 'PRESSAO';
  AFERICAO_SULCO_PRESSAO  VARCHAR := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY

  WITH DIAS AS (
      SELECT G.DAY :: DATE AS DATA
      FROM GENERATE_SERIES(DATA_INICIAL, DATA_FINAL, '1 DAY') G(DAY)
      ORDER BY DATA
  ),

  AFERICOES_DIA AS (
    SELECT
      (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE AS DATA,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_SULCO THEN 1 ELSE 0 END)  QTD_AFERICAO_SULCO,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_PRESSAO THEN 1 ELSE 0 END)  QTD_AFERICAO_PRESSAO,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_SULCO_PRESSAO THEN 1 ELSE 0 END)  QTD_AFERICAO_SULCO_PRESSAO
    FROM AFERICAO A
    WHERE
      A.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
    GROUP BY DATA
    ORDER BY DATA
  )

  SELECT
    D.DATA                          AS DATA,
    TO_CHAR(D.DATA, 'DD/MM')        AS DATA_FORMATADA,
    AD.QTD_AFERICAO_SULCO           AS QTD_AFERICAO_SULCO,
    AD.QTD_AFERICAO_PRESSAO         AS QTD_AFERICAO_PRESSAO,
    AD.QTD_AFERICAO_SULCO_PRESSAO   AS QTD_AFERICAO_SULCO_PRESSAO
  FROM DIAS D
    LEFT JOIN AFERICOES_DIA AD ON D.DATA = AD.DATA
  ORDER BY D.DATA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Function para retornar as marcas e modelos de veículos de uma empresa.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_MARCAS_MODELOS_EMPRESA(
  F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    COD_MARCA   BIGINT,
    NOME_MARCA  TEXT,
    COD_MODELO  BIGINT,
    NOME_MODELO TEXT)
LANGUAGE SQL
AS $$
SELECT
  MAV.CODIGO AS COD_MARCA,
  MAV.NOME   AS NOME_MARCA,
  MOV.CODIGO AS COD_MODELO,
  MOV.NOME   AS NOME_MODELO
FROM MARCA_VEICULO MAV
  LEFT JOIN MODELO_VEICULO MOV
    ON MAV.CODIGO = MOV.COD_MARCA AND MOV.COD_EMPRESA = F_COD_EMPRESA
ORDER BY MAV.CODIGO ASC, MOV.CODIGO ASC;
$$;
COMMENT ON FUNCTION FUNC_VEICULO_GET_MARCAS_MODELOS_EMPRESA(BIGINT) IS 'Retorna as marcas e modelos de veículos de uma empresa. Caso a empresa não tenha modelos para uma marca qualquer, essa marca ainda será retornada.';


-- Function para retornar as marcas de veículo existentes no banco.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_MARCAS_NIVEL_PROLOG()
  RETURNS TABLE(
    COD_MARCA   BIGINT,
    NOME_MARCA  TEXT)
LANGUAGE SQL
AS $$
SELECT
  MV.CODIGO AS COD_MARCA,
  MV.NOME   AS NOME_MARCA
FROM MARCA_VEICULO MV
ORDER BY MV.NOME ASC;
$$;
COMMENT ON FUNCTION FUNC_VEICULO_GET_MARCAS_NIVEL_PROLOG() IS 'As marcas de veículo são a nível ProLog. Essa function retorna todas as marcas disponíveis no BD.';


-- Impede que sejam criados modelos de mesmo nome pertencentes a mesma marca e empresa.
--
-- Para fazer a verificação ser case insentive, vamos alterar o tipo da coluna de TEXT para CITEXT (case-insensitive text).
-- Esse tipo também já utiliza por baixo dos panos o lower(text) na hora de comparar os valores dessa coluna.
-- As functions atuais que retornam o nome do tipo, não terão problemas em continuar retornando um TEXT.
CREATE EXTENSION IF NOT EXISTS CITEXT;
ALTER TABLE MODELO_VEICULO ALTER COLUMN NOME TYPE CITEXT;


-- Remove modelo duplicado na mesma marca, tirando primeiro veículos que o utilizam.
UPDATE VEICULO V SET COD_MODELO = 130 WHERE COD_MODELO = 140 AND COD_UNIDADE = 38;
DELETE FROM MODELO_VEICULO WHERE CODIGO = 140 AND COD_EMPRESA = 9 AND COD_MARCA = 8;

ALTER TABLE MODELO_VEICULO ADD CONSTRAINT NOMES_UNICOS_POR_EMPRESA_E_MARCA UNIQUE (NOME, COD_EMPRESA, COD_MARCA);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################### TRUNCA VALORES DE SULCO E PRESSÃO NA FUNCTION DE INTEGRAÇÃO DAS AFERIÇÕES #######################
--######################################################################################################################
--######################################################################################################################
-- POR NECESSIDADE DA EMPRESA PICCOLOTUR, DEVEMOS TRUNCAR O VALOR DAS AFERIÇÕES PARA 1 NÚMERO APÓS A VÍRGULA.
DROP FUNCTION INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_EMPRESA(TEXT, BIGINT);

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_EMPRESA(
  F_TOKEN_INTEGRACAO                 TEXT,
  F_COD_ULTIMA_AFERICAO_SINCRONIZADA BIGINT)
  RETURNS TABLE(
    COD_AFERICAO                       BIGINT,
    COD_UNIDADE_AFERICAO               BIGINT,
    CPF_COLABORADOR                    TEXT,
    PLACA_VEICULO_AFERIDO              VARCHAR(255),
    COD_PNEU_AFERIDO                   BIGINT,
    NUMERO_FOGO                        VARCHAR(255),
    ALTURA_SULCO_INTERNO               NUMERIC,
    ALTURA_SULCO_CENTRAL_INTERNO       NUMERIC,
    ALTURA_SULCO_CENTRAL_EXTERNO       NUMERIC,
    ALTURA_SULCO_EXTERNO               NUMERIC,
    PRESSAO                            NUMERIC,
    KM_VEICULO_MOMENTO_AFERICAO        BIGINT,
    TEMPO_REALIZACAO_AFERICAO_EM_MILIS BIGINT,
    VIDA_MOMENTO_AFERICAO              INTEGER,
    POSICAO_PNEU_MOMENTO_AFERICAO      INTEGER,
    DATA_HORA_AFERICAO                 TIMESTAMP WITHOUT TIME ZONE,
    TIPO_MEDICAO_COLETADA              VARCHAR(13),
    TIPO_PROCESSO_COLETA               VARCHAR(11))
LANGUAGE SQL
AS $$
SELECT
  A.CODIGO                                           AS COD_AFERICAO,
  A.COD_UNIDADE                                      AS COD_UNIDADE_AFERICAO,
  LPAD(A.CPF_AFERIDOR :: TEXT, 11, '0')              AS CPF_COLABORADOR,
  A.PLACA_VEICULO                                    AS PLACA_VEICULO_AFERIDO,
  AV.COD_PNEU                                        AS COD_PNEU_AFERIDO,
  P.CODIGO_CLIENTE                                   AS NUMERO_FOGO,
  TRUNC(AV.ALTURA_SULCO_INTERNO::NUMERIC, 1)         AS ALTURA_SULCO_INTERNO,
  TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO::NUMERIC, 1) AS ALTURA_SULCO_CENTRAL_INTERNO,
  TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO::NUMERIC, 1) AS ALTURA_SULCO_CENTRAL_EXTERNO,
  TRUNC(AV.ALTURA_SULCO_EXTERNO::NUMERIC, 1)         AS ALTURA_SULCO_EXTERNO,
  TRUNC(AV.PSI::NUMERIC, 1)                          AS PRESSAO,
  A.KM_VEICULO                                       AS KM_VEICULO_MOMENTO_AFERICAO,
  A.TEMPO_REALIZACAO                                 AS TEMPO_REALIZACAO_AFERICAO_EM_MILIS,
  AV.VIDA_MOMENTO_AFERICAO                           AS VIDA_MOMENTO_AFERICAO,
  AV.POSICAO                                         AS POSICAO_PNEU_MOMENTO_AFERICAO,
  A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE) AS DATA_HORA_AFERICAO,
  A.TIPO_MEDICAO_COLETADA                            AS TIPO_MEDICAO_COLETADA,
  A.TIPO_PROCESSO_COLETA                             AS TIPO_PROCESSO_COLETA
FROM AFERICAO A
  JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
  JOIN PNEU P ON AV.COD_PNEU = P.CODIGO
WHERE A.COD_UNIDADE IN (SELECT CODIGO
                        FROM UNIDADE
                        WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
      AND A.CODIGO > F_COD_ULTIMA_AFERICAO_SINCRONIZADA
ORDER BY A.CODIGO;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### CORRIGE NOME DA FUNCTION #################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_PNEU_RELATORIO_QUANTIDADE_AFERICOES_POR_TIPO_MEDICAO_COLET(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE );

CREATE FUNCTION FUNC_PNEU_RELATORIO_QTD_AFERICOES_POR_TIPO_MEDICAO_COLETADA(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    DATA_REFERENCIA            DATE,
    DATA_REFERENCIA_FORMATADA  TEXT,
    QTD_AFERICAO_PRESSAO       NUMERIC,
    QTD_AFERICAO_SULCO         NUMERIC,
    QTD_AFERICAO_SULCO_PRESSAO NUMERIC)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT                    TEXT := 'DD/MM';
  MEDICAO_COLETADA_PRESSAO       TEXT := 'PRESSAO';
  MEDICAO_COLETADA_SULCO         TEXT := 'SULCO';
  MEDICAO_COLETADA_SULCO_PRESSAO TEXT := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY
  SELECT
    DADOS.DATA_REFERENCIA                AS DATA_REFERENCIA,
    DADOS.DATA_REFERENCIA_FORMATADA      AS DATA_REFERENCIA_FORMATADA,
    SUM(DADOS.QT_AFERICAO_PRESSAO)       AS QTD_AFERICAO_PRESSAO,
    SUM(DADOS.QT_AFERICAO_SULCO)         AS QTD_AFERICAO_SULCO,
    SUM(DADOS.QT_AFERICAO_SULCO_PRESSAO) AS QTD_AFERICAO_SULCO_PRESSAO
  FROM (SELECT
          (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE               AS DATA_REFERENCIA,
          TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)), DATE_FORMAT) AS DATA_REFERENCIA_FORMATADA,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_PRESSAO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_PRESSAO,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_SULCO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_SULCO,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_SULCO_PRESSAO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_SULCO_PRESSAO
        FROM AFERICAO A
        WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
              AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
        GROUP BY A.DATA_HORA, DATA_REFERENCIA_FORMATADA, A.COD_UNIDADE
        ORDER BY A.DATA_HORA :: DATE ASC) AS DADOS
  GROUP BY DADOS.DATA_REFERENCIA, DADOS.DATA_REFERENCIA_FORMATADA
  ORDER BY DADOS.DATA_REFERENCIA ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################








-- ALTERAÇÕES DO CONTROLE DE JORNADA.









--######################################################################################################################
--######################################################################################################################
--#################### ADICIONA TIPO JORNADA ###########################################################################
--######################################################################################################################
--######################################################################################################################
-- DROPAMOS A TABELA PARA EVITAR TER QUE ALTERAR A FK.
DROP TABLE INTERVALO_TABELA_ANTIGA;

-- DROPA CONSTRAINTS PARA PODER ALTERAR PK DA TABELA INTERVALO_TIPO.
ALTER TABLE INTERVALO_TIPO_CARGO DROP CONSTRAINT FK_INTERVALO_TIPO_CARGO_INTERVALO_TIPO;
ALTER TABLE INTERVALO DROP CONSTRAINT FK_INTERVALO_INTERVALO_TIPO;

-- ALTERA PK DA INTERVALO_TIPO.
ALTER TABLE INTERVALO_TIPO DROP CONSTRAINT PK_INTERVALO_TIPO;
ALTER TABLE INTERVALO_TIPO ADD CONSTRAINT PK_INTERVALO_TIPO PRIMARY KEY (CODIGO);
ALTER TABLE INTERVALO_TIPO ADD CONSTRAINT UNIQUE_TIPO_UNIDADE UNIQUE (COD_UNIDADE, CODIGO);

-- RECRIA CONSTRAINTS REMOVIDAS ACIMA.
ALTER TABLE INTERVALO_TIPO_CARGO ADD CONSTRAINT FK_INTERVALO_TIPO_CARGO_INTERVALO_TIPO
  FOREIGN KEY (COD_UNIDADE, COD_TIPO_INTERVALO) REFERENCES INTERVALO_TIPO (COD_UNIDADE, CODIGO);
ALTER TABLE INTERVALO ADD CONSTRAINT FK_INTERVALO_INTERVALO_TIPO
  FOREIGN KEY (COD_UNIDADE, COD_TIPO_INTERVALO) REFERENCES INTERVALO_TIPO (COD_UNIDADE, CODIGO);

CREATE TABLE IF NOT EXISTS MARCACAO_TIPO_JORNADA (
  COD_UNIDADE      BIGINT NOT NULL,
  COD_TIPO_JORNADA BIGINT NOT NULL,
  CONSTRAINT PK_MARCACAO_TIPO_JORNADA PRIMARY KEY (COD_UNIDADE, COD_TIPO_JORNADA),
  CONSTRAINT FK_TIPO_JORNADA_CALCULO_JORNADA_BRUTA FOREIGN KEY (COD_UNIDADE, COD_TIPO_JORNADA)
  REFERENCES INTERVALO_TIPO (COD_UNIDADE, CODIGO),
  CONSTRAINT UM_TIPO_JORNADA_POR_UNIDADE UNIQUE (COD_UNIDADE),
  CONSTRAINT TIPO_USADO_APENAS_UMA_VEZ UNIQUE (COD_TIPO_JORNADA)
);

CREATE TABLE IF NOT EXISTS MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA (
  COD_UNIDADE              BIGINT  NOT NULL,
  COD_TIPO_JORNADA         BIGINT  NOT NULL,
  COD_TIPO_DESCONTADO      BIGINT  NOT NULL,
  DESCONTA_JORNADA_BRUTA   BOOLEAN NOT NULL,
  DESCONTA_JORNADA_LIQUIDA BOOLEAN NOT NULL,
  CONSTRAINT PK_MARCACAO_TIPOS_CALCULO_JORNADA_BRUTA_LIQUIDA PRIMARY KEY (COD_UNIDADE, COD_TIPO_JORNADA, COD_TIPO_DESCONTADO),
  CONSTRAINT FK_TIPO_JORNADA_CALCULO_JORNADA_BRUTA_LIQUIDA FOREIGN KEY (COD_UNIDADE, COD_TIPO_JORNADA)
  REFERENCES MARCACAO_TIPO_JORNADA (COD_UNIDADE, COD_TIPO_JORNADA),
  CONSTRAINT FK_TIPO_DESCONTADO_JORNADA_CALCULO_JORNADA_BRUTA_LIQUIDA FOREIGN KEY (COD_UNIDADE, COD_TIPO_DESCONTADO)
  REFERENCES INTERVALO_TIPO (COD_UNIDADE, CODIGO),
  CONSTRAINT TIPO_DESCONTADO_JORNADA_BRUTA_APENAS_UMA_VEZ UNIQUE (COD_TIPO_DESCONTADO),
  CONSTRAINT DESCONTA_BRUTA_OU_LIQUIDA_NAO_AMBOS CHECK ((DESCONTA_JORNADA_BRUTA AND NOT DESCONTA_JORNADA_LIQUIDA)
                                                        OR (DESCONTA_JORNADA_LIQUIDA AND NOT DESCONTA_JORNADA_BRUTA))
);

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_REMOVE_INFOS_TIPO_JORNADA_UNIDADE(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_EDITADO BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF (SELECT TIPO_JORNADA
      FROM VIEW_INTERVALO_TIPO VIT
      WHERE VIT.CODIGO = F_COD_TIPO_EDITADO)
  THEN
    DELETE FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA WHERE COD_UNIDADE = F_COD_UNIDADE;
    DELETE FROM MARCACAO_TIPO_JORNADA WHERE COD_UNIDADE = F_COD_UNIDADE;
  END IF;
  RETURN TRUE;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_ATUALIZA_INFOS_TIPO_JORNADA_UNIDADE(
  F_COD_UNIDADE BIGINT,
  F_NOVO_COD_TIPO_JORNADA BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTDROWS BIGINT;
BEGIN
  DELETE FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA WHERE COD_UNIDADE = F_COD_UNIDADE;
  DELETE FROM MARCACAO_TIPO_JORNADA WHERE COD_UNIDADE = F_COD_UNIDADE;

  INSERT INTO MARCACAO_TIPO_JORNADA (COD_UNIDADE, COD_TIPO_JORNADA) VALUES (F_COD_UNIDADE, F_NOVO_COD_TIPO_JORNADA);
  GET DIAGNOSTICS QTDROWS = ROW_COUNT;
  IF QTDROWS = 1
    THEN RETURN TRUE;
  ELSE
    RAISE EXCEPTION 'Erro ao inserir tipo como jornada. Unidade: % e Tipo: %', F_COD_UNIDADE, F_NOVO_COD_TIPO_JORNADA;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPOS_DESCONTADOS_JORNADA_BRUTA_LIQUIDA(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_TIPO_DESCONTADO      BIGINT,
    NOME_TIPO_DESCONTADO     TEXT,
    DESCONTA_JORNADA_BRUTA   BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  M.COD_TIPO_DESCONTADO      AS COD_TIPO_DESCONTADO,
  VIT.NOME                   AS NOME_TIPO_DESCONTADO,
  M.DESCONTA_JORNADA_BRUTA   AS DESCONTA_JORNADA_BRUTA,
  M.DESCONTA_JORNADA_LIQUIDA AS DESCONTA_JORNADA_LIQUIDA
FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA M
  JOIN VIEW_INTERVALO_TIPO VIT
    ON M.COD_TIPO_DESCONTADO = VIT.CODIGO
WHERE M.COD_UNIDADE = F_COD_UNIDADE;
$$;
--######################################################################################################################
--######################################################################################################################

-- ADICIONA TIPO_JORNADA NO RETORNO NA VIEW.
CREATE OR REPLACE VIEW VIEW_INTERVALO_TIPO AS
  SELECT
    ROW_NUMBER()
    OVER (
      PARTITION BY IT.COD_UNIDADE
      ORDER BY IT.CODIGO )           AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
    IT.CODIGO                        AS CODIGO,
    IT.COD_UNIDADE                   AS COD_UNIDADE,
    IT.NOME                          AS NOME,
    IT.ICONE                         AS ICONE,
    IT.TEMPO_RECOMENDADO_MINUTOS     AS TEMPO_RECOMENDADO_MINUTOS,
    IT.TEMPO_ESTOURO_MINUTOS         AS TEMPO_ESTOURO_MINUTOS,
    IT.HORARIO_SUGERIDO              AS HORARIO_SUGERIDO,
    IT.ATIVO                         AS ATIVO,
    MTJ.COD_TIPO_JORNADA IS NOT NULL AS TIPO_JORNADA
  FROM INTERVALO_TIPO IT
    LEFT JOIN MARCACAO_TIPO_JORNADA MTJ
      ON IT.COD_UNIDADE = MTJ.COD_UNIDADE
         AND IT.CODIGO = MTJ.COD_TIPO_JORNADA;

-- RECRIA FUNCTION DE BUSCA DOS TIPOS DE MARCAÇÕES COM NOVO NOME E JÁ RETORNANDO AGORA O ATRIBUTO TIPO JORNADA
DROP FUNCTION FUNC_CONTROLE_JORNADA_GET_TIPOS_INTERVALOS_UNIDADE(F_COD_UNIDADE BIGINT, F_APENAS_ATIVOS BOOLEAN );

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPOS_MARCACOES(F_COD_UNIDADE BIGINT, F_APENAS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO_TIPO_INTERVALO             BIGINT,
    CODIGO_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    NOME_TIPO_INTERVALO               CHARACTER VARYING,
    COD_UNIDADE                       BIGINT,
    ATIVO                             BOOLEAN,
    HORARIO_SUGERIDO                  TIME WITHOUT TIME ZONE,
    ICONE                             CHARACTER VARYING,
    TEMPO_ESTOURO_MINUTOS             BIGINT,
    TEMPO_RECOMENDADO_MINUTOS         BIGINT,
    TIPO_JORNADA                      BOOLEAN)
LANGUAGE SQL
AS $$
SELECT DISTINCT
  IT.CODIGO                            AS CODIGO_TIPO_INTERVALO,
  IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  IT.NOME                              AS NOME_TIPO_INTERVALO,
  IT.COD_UNIDADE,
  IT.ATIVO,
  IT.HORARIO_SUGERIDO,
  IT.ICONE,
  IT.TEMPO_ESTOURO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS,
  IT.TIPO_JORNADA
FROM VIEW_INTERVALO_TIPO IT
WHERE IT.COD_UNIDADE = F_COD_UNIDADE
      AND CASE WHEN F_APENAS_ATIVOS IS TRUE
  THEN IT.ATIVO = TRUE
          ELSE TRUE END
ORDER BY IT.ATIVO DESC, IT.NOME ASC;
$$;


CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPO_MARCACAO(F_COD_TIPO_MARCACAO BIGINT)
  RETURNS TABLE(
    CODIGO_TIPO_INTERVALO             BIGINT,
    CODIGO_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    NOME_TIPO_INTERVALO               CHARACTER VARYING,
    COD_UNIDADE                       BIGINT,
    ATIVO                             BOOLEAN,
    HORARIO_SUGERIDO                  TIME WITHOUT TIME ZONE,
    ICONE                             CHARACTER VARYING,
    TEMPO_ESTOURO_MINUTOS             BIGINT,
    TEMPO_RECOMENDADO_MINUTOS         BIGINT,
    TIPO_JORNADA                      BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  IT.CODIGO                            AS CODIGO_TIPO_INTERVALO,
  IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  IT.NOME                              AS NOME_TIPO_INTERVALO,
  IT.COD_UNIDADE                       AS COD_UNIDADE,
  IT.ATIVO                             AS ATIVO,
  IT.HORARIO_SUGERIDO                  AS HORARIO_SUGERIDO,
  IT.ICONE                             AS ICONE,
  IT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
  IT.TIPO_JORNADA                      AS TIPO_JORNADA
FROM VIEW_INTERVALO_TIPO IT
WHERE IT.CODIGO = F_COD_TIPO_MARCACAO;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################### ALTERA RELATÓRIO DE HORAS TOTAIS POR TIPO DE MARCAÇÃO ###########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(
  F_DATA_HORA_INICIO      TIMESTAMPTZ,
  F_DATA_HORA_FIM         TIMESTAMPTZ,
  F_TIMEZONE_PARA_CALCULO TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  RANGE_INICIO_FIM     TSTZRANGE;
  TOTAL_HORAS_NOTURNAS BIGINT;
  DATA_HORA_INICIO_TZ  TIMESTAMPTZ := F_DATA_HORA_INICIO AT TIME ZONE F_TIMEZONE_PARA_CALCULO;
  DATA_HORA_FIM_TZ     TIMESTAMPTZ := F_DATA_HORA_FIM AT TIME ZONE F_TIMEZONE_PARA_CALCULO;
BEGIN
  -- O range não pode ter o lower bound após o upper bound, por isso essa verificação.
  IF F_DATA_HORA_INICIO < F_DATA_HORA_FIM
  THEN
    RANGE_INICIO_FIM := TSTZRANGE(DATA_HORA_INICIO_TZ, DATA_HORA_FIM_TZ);
  ELSE
    RANGE_INICIO_FIM := TSTZRANGE(DATA_HORA_FIM_TZ, DATA_HORA_INICIO_TZ);
  END IF;

  WITH DIAS AS (
      SELECT GENERATE_SERIES(DATA_HORA_INICIO_TZ :: DATE,
                             DATA_HORA_FIM_TZ :: DATE + '1 DAY' :: INTERVAL,
                             '1 DAY') :: DATE AS DIA
  ), RANGES AS (
      SELECT *
      FROM DIAS
        CROSS JOIN TSTZRANGE(((DIAS.DIA :: DATE || ' 22:00:00') :: TIMESTAMP) AT TIME ZONE
                             F_TIMEZONE_PARA_CALCULO - '1 DAY' :: INTERVAL,
                             ((DIAS.DIA :: DATE || ' 05:00:00') :: TIMESTAMP) AT TIME ZONE
                             F_TIMEZONE_PARA_CALCULO) AS RANGE_VERIFICACAO_DIA
  )

  SELECT SUM(TO_SECONDS(COALESCE(UPPER(R.I) - LOWER(R.I), '0' :: INTERVAL)))
  FROM
    (SELECT
       *,
       RANGE_INICIO_FIM * R.RANGE_VERIFICACAO_DIA AS I
     FROM RANGES R) R
  INTO TOTAL_HORAS_NOTURNAS;

  RETURN TOTAL_HORAS_NOTURNAS;
END;
$$;

DROP FUNCTION FUNC_INTERVALOS_GET_TOTAL_TEMPO_POR_TIPO_INTERVALO(
F_COD_UNIDADE BIGINT,
F_COD_TIPO_INTERVALO BIGINT,
F_DATA_INICIAL TIMESTAMP WITHOUT TIME ZONE,
F_DATA_FINAL TIMESTAMP WITHOUT TIME ZONE,
F_CURRENT_TIMESTAMP_UTC TIMESTAMP WITHOUT TIME ZONE);

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TEMPO_TOTAL_POR_TIPO_MARCACAO(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE)
  RETURNS TABLE(
    CPF_COLABORADOR                   TEXT,
    NOME                              TEXT,
    CARGO                             TEXT,
    COD_TIPO_INTERVALO                TEXT,
    NOME_TIPO_INTERVALO               TEXT,
    TEMPO_TOTAL_MILLIS                TEXT,
    TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH TIPOS_UNIDADE AS (
      SELECT
        C.CPF     AS CPF_COLABORADOR,
        C.NOME    AS NOME_COLABORADOR,
        F.NOME    AS NOME_CARGO,
        IT.CODIGO AS COD_TIPO_MARCACAO,
        IT.NOME   AS NOME_TIPO_MARCACAO
      FROM COLABORADOR C
        JOIN FUNCAO F
          ON C.COD_FUNCAO = F.CODIGO
        JOIN INTERVALO_TIPO IT
          ON C.COD_UNIDADE = IT.COD_UNIDADE
      WHERE IT.COD_UNIDADE = F_COD_UNIDADE
      ORDER BY C.CPF, IT.CODIGO
  ),
      TOTAIS_POR_TIPO AS (
        SELECT *
        FROM
          (SELECT
             C.CPF     AS CPF_COLABORADOR,
             IT.CODIGO AS COD_TIPO_INTERVALO,
             SUM(TO_SECONDS(I.DATA_HORA_FIM - I.DATA_HORA_INICIO))
             OVER W    AS TEMPO_TOTAL_SEGUNDOS,
             SUM(FUNC_MARCACAO_CALCULA_TOTAL_SEGUNDOS_EM_HORAS_NOTURNAS(I.DATA_HORA_INICIO,
                                                                        I.DATA_HORA_FIM,
                                                                        TZ_UNIDADE))
             OVER W    AS TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS
           FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, F_COD_TIPO_INTERVALO) AS I
             JOIN COLABORADOR AS C
               ON I.CPF_COLABORADOR = C.CPF
             LEFT JOIN INTERVALO_TIPO AS IT
               ON I.COD_TIPO_INTERVALO = IT.CODIGO
                  AND IT.ATIVO = TRUE
           WHERE ((((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE)::DATE >= F_DATA_INICIAL)
                   AND (((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE)::DATE <= F_DATA_FINAL)))
                  OR
                  (((I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE)::DATE >= F_DATA_INICIAL)
                   AND ((I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE)::DATE <= F_DATA_FINAL)))
                  -- Expurga marcações que não tem início ou fim.
                  AND I.DATA_HORA_INICIO IS NOT NULL AND I.DATA_HORA_FIM IS NOT NULL
                  -- Retiramos do cálculo as marcações que foram desativadas.
                  AND I.STATUS_ATIVO_INICIO = TRUE
                  AND I.STATUS_ATIVO_FIM = TRUE
           GROUP BY C.CPF, I.COD_TIPO_INTERVALO, I.DATA_HORA_FIM, I.DATA_HORA_INICIO, IT.CODIGO
           WINDOW W AS (
             PARTITION BY C.CPF, IT.CODIGO)) AS T
        GROUP BY
          T.CPF_COLABORADOR,
          T.COD_TIPO_INTERVALO,
          T.TEMPO_TOTAL_SEGUNDOS,
          T.TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS
        ORDER BY T.CPF_COLABORADOR, T.COD_TIPO_INTERVALO)

  SELECT
    LPAD(TU.CPF_COLABORADOR::TEXT, 11, '0')                AS CPF_COLABORADOR,
    TU.NOME_COLABORADOR::TEXT                              AS NOME_COLABORADOR,
    TU.NOME_CARGO::TEXT                                    AS NOME_CARGO,
    TU.COD_TIPO_MARCACAO::TEXT                             AS COD_TIPO_MARCACAO,
    TU.NOME_TIPO_MARCACAO::TEXT                            AS NOME_TIPO_MARCACAO,
    (TPT.TEMPO_TOTAL_SEGUNDOS * 1000)::TEXT                AS TEMPO_TOTAL_MILLIS,
    (TPT.TEMPO_TOTAL_HORAS_NOTURNAS_SEGUNDOS * 1000)::TEXT AS TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS
  FROM TIPOS_UNIDADE TU
    LEFT JOIN TOTAIS_POR_TIPO TPT
      ON TU.CPF_COLABORADOR = TPT.CPF_COLABORADOR AND TU.COD_TIPO_MARCACAO = TPT.COD_TIPO_INTERVALO
  ORDER BY TU.CPF_COLABORADOR, TU.COD_TIPO_MARCACAO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

DROP VIEW VIEW_INTERVALO_MAPA_COLABORADOR;
DROP VIEW VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS;
DROP FUNCTION FUNC_INTERVALOS_AGRUPADOS(BIGINT,BIGINT,BIGINT);
CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_AGRUPADOS(
  F_COD_UNIDADE BIGINT,
  F_CPF_COLABORADOR BIGINT,
  F_COD_TIPO_INTERVALO BIGINT)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO TEXT,
    FONTE_DATA_HORA_FIM TEXT,
    JUSTIFICATIVA_ESTOURO TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    LATITUDE_MARCACAO_INICIO TEXT,
    LONGITUDE_MARCACAO_INICIO TEXT,
    LATITUDE_MARCACAO_FIM TEXT,
    LONGITUDE_MARCACAO_FIM TEXT,
    COD_UNIDADE BIGINT,
    CPF_COLABORADOR BIGINT,
    COD_TIPO_INTERVALO BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    DATA_HORA_INICIO TIMESTAMP WITH TIME ZONE,
    DATA_HORA_FIM TIMESTAMP WITH TIME ZONE,
    COD_MARCACAO_INICIO BIGINT,
    COD_MARCACAO_FIM BIGINT,
    STATUS_ATIVO_INICIO BOOLEAN,
    STATUS_ATIVO_FIM BOOLEAN,
    FOI_AJUSTADO_INICIO BOOLEAN,
    FOI_AJUSTADO_FIM BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO TIMESTAMP WITH TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM TIMESTAMP WITH TIME ZONE,
    TIPO_JORNADA BOOLEAN)
LANGUAGE SQL
AS $$
WITH INICIOS AS (
    SELECT
      MI.COD_MARCACAO_INICIO,
      MV.COD_MARCACAO_FIM                   AS COD_MARCACAO_VINCULO,
      I.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_INICIO,
      I.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_INICIO,
      I.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_INICIO,
      I.COD_UNIDADE                         AS COD_UNIDADE,
      I.CPF_COLABORADOR                     AS CPF_COLABORADOR,
      I.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
      I.DATA_HORA                           AS DATA_HORA_INICIO,
      I.CODIGO                              AS CODIGO_INICIO,
      I.STATUS_ATIVO                        AS STATUS_ATIVO_INICIO,
      I.FOI_AJUSTADO                        AS FOI_AJUSTADO_INICIO,
      I.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_INICIO,
      VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
      VIT.TIPO_JORNADA                      AS TIPO_JORNADA
    FROM MARCACAO_INICIO MI
      LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
        ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
      JOIN INTERVALO I
        ON MI.COD_MARCACAO_INICIO = I.CODIGO
      JOIN VIEW_INTERVALO_TIPO VIT
        ON I.COD_TIPO_INTERVALO = VIT.CODIGO
    WHERE I.VALIDO = TRUE
          AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE I.COD_UNIDADE = F_COD_UNIDADE END
          AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE I.CPF_COLABORADOR = F_CPF_COLABORADOR END
          AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
),

    FINS AS (
      SELECT
        MF.COD_MARCACAO_FIM,
        MV.COD_MARCACAO_INICIO                AS COD_MARCACAO_VINCULO,
        F.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_FIM,
        F.JUSTIFICATIVA_ESTOURO               AS JUSTIFICATIVA_ESTOURO,
        F.JUSTIFICATIVA_TEMPO_RECOMENDADO     AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
        F.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_FIM,
        F.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_FIM,
        F.COD_UNIDADE                         AS COD_UNIDADE,
        F.CPF_COLABORADOR                     AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
        F.DATA_HORA                           AS DATA_HORA_FIM,
        F.CODIGO                              AS CODIGO_FIM,
        F.STATUS_ATIVO                        AS STATUS_ATIVO_FIM,
        F.FOI_AJUSTADO                        AS FOI_AJUSTADO_FIM,
        F.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_FIM,
        VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
        VIT.TIPO_JORNADA                      AS TIPO_JORNADA
      FROM MARCACAO_FIM MF
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
        JOIN INTERVALO F
          ON MF.COD_MARCACAO_FIM = F.CODIGO
        JOIN VIEW_INTERVALO_TIPO VIT
          ON F.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F.VALIDO = TRUE
            AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE F.COD_UNIDADE = F_COD_UNIDADE END
            AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE F.CPF_COLABORADOR = F_CPF_COLABORADOR END
            AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE F.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
  )

SELECT
  I.FONTE_DATA_HORA_INICIO,
  F.FONTE_DATA_HORA_FIM,
  F.JUSTIFICATIVA_ESTOURO,
  F.JUSTIFICATIVA_TEMPO_RECOMENDADO,
  I.LATITUDE_MARCACAO_INICIO,
  I.LONGITUDE_MARCACAO_INICIO,
  F.LATITUDE_MARCACAO_FIM,
  F.LONGITUDE_MARCACAO_FIM,
  COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                                       AS COD_UNIDADE,
  COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR)                               AS CPF_COLABORADOR,
  COALESCE(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                         AS COD_TIPO_INTERVALO,
  COALESCE(I.COD_TIPO_INTERVALO_POR_UNIDADE, F.COD_TIPO_INTERVALO_POR_UNIDADE) AS COD_TIPO_INTERVALO_POR_UNIDADE,
  I.DATA_HORA_INICIO,
  F.DATA_HORA_FIM,
  I.CODIGO_INICIO,
  F.CODIGO_FIM,
  I.STATUS_ATIVO_INICIO,
  F.STATUS_ATIVO_FIM,
  I.FOI_AJUSTADO_INICIO,
  F.FOI_AJUSTADO_FIM,
  I.DATA_HORA_SINCRONIZACAO_INICIO,
  F.DATA_HORA_SINCRONIZACAO_FIM,
  (F.TIPO_JORNADA = TRUE OR I.TIPO_JORNADA = TRUE)                             AS TIPO_JORNADA
FROM INICIOS I
  FULL OUTER JOIN FINS F
    ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
ORDER BY
  CPF_COLABORADOR,
  COD_TIPO_INTERVALO,
  COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM)
$$;

create view view_extrato_mapas_versus_intervalos as
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
     JOIN colaborador mot ON (((mot.cod_unidade = m.cod_unidade) AND (mot.cod_funcao = ufp.cod_funcao_motorista) AND (mot.matricula_ambev = m.matricmotorista))))
     LEFT JOIN colaborador aj1 ON (((aj1.cod_unidade = m.cod_unidade) AND (aj1.cod_funcao = ufp.cod_funcao_ajudante) AND (aj1.matricula_ambev = m.matricajud1))))
     LEFT JOIN colaborador aj2 ON (((aj2.cod_unidade = m.cod_unidade) AND (aj2.cod_funcao = ufp.cod_funcao_ajudante) AND (aj2.matricula_ambev = m.matricajud2))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_mot(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim) ON (((int_mot.cpf_colaborador = mot.cpf) AND ((int_mot.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj1(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim) ON (((int_aj1.cpf_colaborador = aj1.cpf) AND ((int_aj1.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj2(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim) ON (((int_aj2.cpf_colaborador = aj2.cpf) AND ((int_aj2.data_hora_inicio)::date = m.data))))
  ORDER BY m.mapa DESC;

CREATE OR REPLACE VIEW VIEW_INTERVALO_MAPA_COLABORADOR AS
  SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_MOTORISTA AS CPF,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_MOT AS INICIO_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_MOT AS FIM_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_MOT AS TEMPO_DECORRIDO_MINUTOS,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MOT_CUMPRIU_TEMPO_MINIMO AS CUMPRIU_TEMPO_MINIMO
   FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS
UNION
 SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_AJ1 AS CPF,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_AJ1 AS INICIO_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_AJ1 AS FIM_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_AJ1 AS TEMPO_DECORRIDO_MINUTOS,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.AJ1_CUMPRIU_TEMPO_MINIMO AS CUMPRIU_TEMPO_MINIMO
   FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS
UNION
 SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_AJ2 AS CPF,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_AJ2 AS INICIO_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_AJ2 AS FIM_INTERVALO,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_AJ2 AS TEMPO_DECORRIDO_MINUTOS,
    VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.AJ2_CUMPRIU_TEMPO_MINIMO AS CUMPRIU_TEMPO_MINIMO
   FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS;

--######################################################################################################################
--######################################################################################################################
--#################### CRIA FUNCTION PARA GERAR OS DADOS DO RELATÓRIO DE     ###########################################
--####################               FOLHA DE PONTO DE JORNADA               ###########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION TZ_DATE(TIMESTAMPTZ, TEXT)
  RETURNS DATE
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN ($1 AT TIME ZONE $2) :: DATE;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE,
  F_TIME_ZONE_UNIDADE  TEXT)
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
BEGIN
  RETURN QUERY
  WITH TODAS_MARCACOES_UNIDADE AS (
      SELECT
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
      WHERE (F.STATUS_ATIVO_INICIO OR F.STATUS_ATIVO_FIM)
  ),

    -- MARCAÇÕES DE JORNADA QUE POSSUEM INÍCIO E FIM MARCADOS.
      APENAS_JORNADAS_COMPLETAS AS (
        SELECT
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
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM)
               @> TSTZRANGE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
               AND (TDU.TIPO_JORNADA IS NULL OR TDU.TIPO_JORNADA = FALSE)
               AND TDU.DATA_HORA_INICIO IS NOT NULL
               AND TDU.DATA_HORA_FIM IS NOT NULL
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS MAS TÊM INÍCIO DENTRO DE ALGUMA JORNADA.
      MARCACOES_COMPLETAS_INICIO_DENTRO_JORNADA AS (
        SELECT
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
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM) @> TDU.DATA_HORA_INICIO
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
            ON TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM) @> TDU.DATA_HORA_FIM
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
               AND TSTZRANGE(AJC.DATA_HORA_INICIO, AJC.DATA_HORA_FIM)
                   @> COALESCE(TDU.DATA_HORA_INICIO, TDU.DATA_HORA_FIM)
               AND AJC.CPF_COLABORADOR = TDU.CPF_COLABORADOR
    ),

    -- MARCAÇÕES QUE NÃO SÃO JORNADAS E NÃO TÊM INÍCIO E NEM FIM DENTRO DE NENHUMA JORNADA.
      MARCACOES_COMPLETAS_FORA_JORNADA AS (
        SELECT
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
    C.NOME :: TEXT                                                     AS NOME_COLABORADOR,
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
    JOIN COLABORADOR C
      ON TM.CPF_COLABORADOR = C.CPF
  ORDER BY TM.CPF_COLABORADOR,
    TM.DIA_BASE,
    TM.COD_MARCACAO_JORNADA ASC,
    COALESCE(TM.DATA_HORA_INICIO, TM.DATA_HORA_FIM) ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################


-- ADICIONA VERIFICAÇÃO PARA IMPEDIR TIPOS DE MARCAÇÃO DE MESMO NOME ATIVOS POR UNIDADE.
DROP VIEW VIEW_INTERVALO_TIPO;

-- Para fazer a verificação ser case insensitive, vamos alterar o tipo da coluna de TEXT para CITEXT (case-insensitive text).
-- Esse tipo também já utiliza por baixo dos panos o lower(text) na hora de comparar os valores dessa coluna.
-- As functions atuais que retornam o nome do tipo, não terão problemas em continuar retornando um TEXT.
CREATE EXTENSION IF NOT EXISTS CITEXT;

ALTER TABLE INTERVALO_TIPO ALTER COLUMN NOME TYPE CITEXT;

-- Existe apenas essa unidade atualmente com tipos de nome repetido, adicionamos um . no fim para evitar o erro.
UPDATE PUBLIC.INTERVALO_TIPO SET NOME = 'Descanso.' WHERE COD_UNIDADE = 4 AND CODIGO = 53;
UPDATE public.INTERVALO_TIPO SET NOME = 'Refeição 2' WHERE COD_UNIDADE = 19 AND CODIGO = 86;

ALTER TABLE INTERVALO_TIPO ADD CONSTRAINT NOMES_UNICOS_POR_UNIDADE UNIQUE (NOME, COD_UNIDADE, ATIVO);

CREATE OR REPLACE VIEW VIEW_INTERVALO_TIPO AS
  SELECT
    ROW_NUMBER()
    OVER (
      PARTITION BY IT.COD_UNIDADE
      ORDER BY IT.CODIGO )           AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
    IT.CODIGO                        AS CODIGO,
    IT.COD_UNIDADE                   AS COD_UNIDADE,
    IT.NOME                          AS NOME,
    IT.ICONE                         AS ICONE,
    IT.TEMPO_RECOMENDADO_MINUTOS     AS TEMPO_RECOMENDADO_MINUTOS,
    IT.TEMPO_ESTOURO_MINUTOS         AS TEMPO_ESTOURO_MINUTOS,
    IT.HORARIO_SUGERIDO              AS HORARIO_SUGERIDO,
    IT.ATIVO                         AS ATIVO,
    MTJ.COD_TIPO_JORNADA IS NOT NULL AS TIPO_JORNADA
  FROM INTERVALO_TIPO IT
    LEFT JOIN MARCACAO_TIPO_JORNADA MTJ
      ON IT.COD_UNIDADE = MTJ.COD_UNIDADE
         AND IT.CODIGO = MTJ.COD_TIPO_JORNADA;

-- Altera o retorno do NOME_TIPO_MARCACAO de VARCHAR para TEXT para não quebrar.
DROP FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(F_COD_TIPO_MARCACAO BIGINT, F_COD_COLABORADOR BIGINT, F_DIA DATE);
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(F_COD_TIPO_MARCACAO BIGINT, F_COD_COLABORADOR BIGINT, F_DIA DATE)
  RETURNS TABLE(
    COD_MARCACAO_INICIO BIGINT,
    COD_MARCACAO_FIM BIGINT,
    DATA_HORA_INICIO TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ATIVO_INICIO BOOLEAN,
    STATUS_ATIVO_FIM BOOLEAN,
    FOI_AJUSTADO_INICIO BOOLEAN,
    FOI_AJUSTADO_FIM BOOLEAN,
    COD_TIPO_MARCACAO BIGINT,
    NOME_TIPO_MARCACAO TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    F.COD_MARCACAO_INICIO                                       AS COD_MARCACAO_INICIO,
    F.COD_MARCACAO_FIM                                          AS COD_MARCACAO_FIM,
    (F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) AS DATA_HORA_INICIO,
    (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))    AS DATA_HORA_FIM,
    F.STATUS_ATIVO_INICIO                                       AS STATUS_ATIVO_INICIO,
    F.STATUS_ATIVO_FIM                                          AS STATUS_ATIVO_FIM,
    F.FOI_AJUSTADO_INICIO                                       AS FOI_AJUSTADO_INICIO,
    F.FOI_AJUSTADO_FIM                                          AS FOI_AJUSTADO_FIM,
    F.COD_TIPO_INTERVALO                                        AS COD_TIPO_MARCACAO,
    IT.NOME :: TEXT                                             AS NOME_TIPO_MARCACAO
  FROM FUNC_INTERVALOS_AGRUPADOS(NULL,
                                 (SELECT C.CPF
                                  FROM COLABORADOR C
                                  WHERE C.CODIGO = F_COD_COLABORADOR),
                                 F_COD_TIPO_MARCACAO) F
    JOIN INTERVALO_TIPO IT
      ON F.COD_TIPO_INTERVALO = IT.CODIGO
  WHERE ((F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))::DATE = F_DIA
       OR (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))::DATE = F_DIA)
      AND NOT EXISTS(SELECT MI.CODIGO FROM MARCACAO_INCONSISTENCIA MI
                      WHERE MI.COD_MARCACAO_INCONSISTENTE = F.COD_MARCACAO_FIM)
  ORDER BY COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM);
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################








--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- CRIA FUNCTIONS UTILIZADAS NA FUNCIONALIDADE DE ACOMPANHAMENTO DE VIAGENS
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_COLABORADORES_EM_DESCANSO(
  F_COD_UNIDADE         BIGINT,
  F_COD_CARGOS          BIGINT[],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    NOME_COLABORADOR               TEXT,
    DATA_HORA_INICIO_ULTIMA_VIAGEM TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_ULTIMA_VIAGEM    TIMESTAMP WITHOUT TIME ZONE,
    TEMPO_DESCANSO_SEGUNDOS        BIGINT,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_JORNADA BIGINT;
  TZ_UNIDADE       TEXT;
BEGIN
  SELECT U.TIMEZONE
  FROM UNIDADE U
  WHERE U.CODIGO = F_COD_UNIDADE
  INTO TZ_UNIDADE;

  SELECT VIT.CODIGO
  FROM VIEW_INTERVALO_TIPO VIT
  WHERE
    VIT.COD_UNIDADE = F_COD_UNIDADE
    AND VIT.TIPO_JORNADA
  INTO COD_TIPO_JORNADA;

  IF COD_TIPO_JORNADA IS NULL OR COD_TIPO_JORNADA <= 0
  THEN RAISE EXCEPTION 'Unidade não possui nenhum tipo de marcação definido como jornada';
  END IF;

  RETURN QUERY
  -- SELECT DISTINCT ON ( EXPRESSION [, ...] ) KEEPS ONLY THE FIRST ROW OF EACH SET OF ROWS WHERE THE GIVEN EXPRESSIONS EVALUATE TO EQUAL.
  -- DOCS: HTTPS://WWW.POSTGRESQL.ORG/DOCS/9.5/SQL-SELECT.HTML#SQL-DISTINCT
  WITH ULTIMA_MARCACAO_JORNADA_COLABORADORES AS (
      SELECT
        DISTINCT ON (F.CPF_COLABORADOR)
        C.NOME                AS NOME,
        F.DATA_HORA_INICIO    AS DATA_HORA_INICIO,
        F.DATA_HORA_FIM       AS DATA_HORA_FIM,
        F.COD_MARCACAO_INICIO AS COD_MARCACAO_INICIO,
        F.COD_MARCACAO_FIM    AS COD_MARCACAO_FIM,
        F.STATUS_ATIVO_INICIO AS STATUS_ATIVO_INICIO,
        F.STATUS_ATIVO_FIM    AS STATUS_ATIVO_FIM,
        F.FOI_AJUSTADO_INICIO AS FOI_AJUSTADO_INICIO,
        F.FOI_AJUSTADO_FIM    AS FOI_AJUSTADO_FIM
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, COD_TIPO_JORNADA) F
        JOIN COLABORADOR C
          ON C.CPF = F.CPF_COLABORADOR
      WHERE
        C.COD_FUNCAO = ANY (F_COD_CARGOS)
      ORDER BY
        F.CPF_COLABORADOR,
        COALESCE(F.COD_MARCACAO_INICIO, F.COD_MARCACAO_FIM) DESC
  )

  SELECT
    UMJC.NOME::TEXT                                        AS NOME_COLABORADOR,
    UMJC.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE          AS DATA_HORA_INICIO_ULTIMA_VIAGEM,
    UMJC.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE             AS DATA_HORA_FIM_ULTIMA_VIAGEM,
    TO_SECONDS(F_DATA_HORA_ATUAL_UTC - UMJC.DATA_HORA_FIM) AS TEMPO_DESCANSO_SEGUNDOS,
    UMJC.FOI_AJUSTADO_INICIO                               AS FOI_AJUSTADO_INICIO,
    UMJC.FOI_AJUSTADO_FIM                                  AS FOI_AJUSTADO_FIM
  FROM ULTIMA_MARCACAO_JORNADA_COLABORADORES UMJC
  WHERE
    -- Precisamos filtrar isso apenas depois de pegarmos a última marcação de jornada de um colaborador,
    -- pois nesse caso pegaremos até mesmo marcações inativas e em andamento. Se fôssemos filtrar na CTE anterior,
    -- poderíamos acabar removendo antes as em andamento e inativas e trazer como última jornada do colaborador uma que
    -- pode ter sido finalizada a até um mês atrás. Ou mesmo uma que está inativa.

    -- Filtramos apenas por fim not null, pois iremos considerar como em descanso, marcações que tem apenas fim.
    UMJC.COD_MARCACAO_FIM IS NOT NULL
    -- Se apenas início ou fim estiverem ativos, queremos trazer essa linha.
    AND (UMJC.STATUS_ATIVO_INICIO OR UMJC.STATUS_ATIVO_FIM);
END;
$$;



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
        C.NOME                AS NOME_COLABORADOR,
        C.CPF                 AS CPF_COLABORADOR,
        F.COD_MARCACAO_INICIO AS COD_MARCACAO_INICIO,
        F.COD_MARCACAO_FIM    AS COD_MARCACAO_FIM,
        F.DATA_HORA_INICIO    AS DATA_HORA_INICIO,
        F.DATA_HORA_FIM       AS DATA_HORA_FIM,
        F.FOI_AJUSTADO_INICIO AS FOI_AJUSTADO_INICIO,
        F.FOI_AJUSTADO_FIM    AS FOI_AJUSTADO_FIM,
        F.STATUS_ATIVO_INICIO AS STATUS_ATIVO_INICIO,
        F.STATUS_ATIVO_FIM    AS STATUS_ATIVO_FIM,
        F.COD_TIPO_INTERVALO  AS COD_TIPO_MARCACAO
      FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, NULL, NULL) F
        JOIN COLABORADOR C
          ON C.CPF = F.CPF_COLABORADOR
      WHERE
        C.COD_FUNCAO = ANY (F_COD_CARGOS)
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
          M.COD_MARCACAO_INICIO DESC
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
              WHEN (JCM.STATUS_ATIVO_INICIO AND NOT JCM.STATUS_ATIVO_FIM) OR
                   (NOT JCM.STATUS_ATIVO_INICIO AND JCM.STATUS_ATIVO_FIM)
                THEN 1
              ELSE 0
              END)
          OVER CPFS              AS TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR
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



CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_ACOMPANHAMENTO(
  F_COD_INICIO BIGINT,
  F_COD_FIM    BIGINT,
  F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO                    TEXT,
    FONTE_DATA_HORA_FIM                       TEXT,
    JUSTIFICATIVA_ESTOURO                     TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO           TEXT,
    LATITUDE_MARCACAO_INICIO                  TEXT,
    LONGITUDE_MARCACAO_INICIO                 TEXT,
    LATITUDE_MARCACAO_FIM                     TEXT,
    LONGITUDE_MARCACAO_FIM                    TEXT,
    COD_UNIDADE                               BIGINT,
    CPF_COLABORADOR                           TEXT,
    NOME_COLABORADOR                          TEXT,
    NOME_TIPO_MARCACAO                        TEXT,
    DATA_HORA_INICIO                          TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                             TIMESTAMP WITHOUT TIME ZONE,
    TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS BIGINT,
    TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS  BIGINT,
    COD_MARCACAO_INICIO                       BIGINT,
    COD_MARCACAO_FIM                          BIGINT,
    STATUS_ATIVO_INICIO                       BOOLEAN,
    STATUS_ATIVO_FIM                          BOOLEAN,
    FOI_AJUSTADO_INICIO                       BOOLEAN,
    FOI_AJUSTADO_FIM                          BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO            TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM               TIMESTAMP WITHOUT TIME ZONE,
    VERSAO_APP_MOMENTO_MARCACAO_INICIO        INTEGER,
    VERSAO_APP_MOMENTO_MARCACAO_FIM           INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO   INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM      INTEGER,
    TIPO_JORNADA                              BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Se os dois códigos foram fornecidos, garantimos que eles são do mesmo colaborador e também que são marcações
  -- vinculadas.
  IF F_COD_INICIO IS NOT NULL AND F_COD_FIM IS NOT NULL
  THEN
    IF (SELECT I.CPF_COLABORADOR
        FROM INTERVALO I
        WHERE I.CODIGO = F_COD_INICIO) <> (SELECT I.CPF_COLABORADOR
                                           FROM INTERVALO I
                                           WHERE I.CODIGO = F_COD_FIM)
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não pertencem ao mesmo colaborador';
    END IF;
    IF (SELECT NOT EXISTS(SELECT MV.CODIGO
                          FROM MARCACAO_VINCULO_INICIO_FIM MV
                          WHERE MV.COD_MARCACAO_INICIO = F_COD_INICIO AND MV.COD_MARCACAO_FIM = F_COD_FIM))
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não estão vinculadas';
    END IF;
  END IF;

  RETURN QUERY
  WITH INICIOS AS (
      SELECT
        MI.COD_MARCACAO_INICIO             AS COD_MARCACAO_INICIO,
        MV.COD_MARCACAO_FIM                AS COD_MARCACAO_VINCULO,
        I.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_INICIO,
        I.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_INICIO,
        I.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_INICIO,
        I.COD_UNIDADE                      AS COD_UNIDADE,
        I.CPF_COLABORADOR                  AS CPF_COLABORADOR,
        I.DATA_HORA                        AS DATA_HORA_INICIO,
        I.CODIGO                           AS CODIGO_INICIO,
        I.STATUS_ATIVO                     AS STATUS_ATIVO_INICIO,
        I.FOI_AJUSTADO                     AS FOI_AJUSTADO_INICIO,
        I.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_INICIO,
        I.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
        I.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
        VIT.NOME                           AS NOME_TIPO_MARCACAO,
        VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
        VIT.TIPO_JORNADA                   AS TIPO_JORNADA
      FROM MARCACAO_INICIO MI
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
        JOIN INTERVALO I
          ON MI.COD_MARCACAO_INICIO = I.CODIGO
        JOIN VIEW_INTERVALO_TIPO VIT
          ON I.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F_IF(F_COD_INICIO IS NULL, I.CODIGO IS NULL, I.CODIGO = F_COD_INICIO)
  ),

      FINS AS (
        SELECT
          MF.COD_MARCACAO_FIM                AS COD_MARCACAO_FIM,
          MV.COD_MARCACAO_INICIO             AS COD_MARCACAO_VINCULO,
          F.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_FIM,
          F.JUSTIFICATIVA_ESTOURO            AS JUSTIFICATIVA_ESTOURO,
          F.JUSTIFICATIVA_TEMPO_RECOMENDADO  AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
          F.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_FIM,
          F.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_FIM,
          F.COD_UNIDADE                      AS COD_UNIDADE,
          F.CPF_COLABORADOR                  AS CPF_COLABORADOR,
          F.DATA_HORA                        AS DATA_HORA_FIM,
          F.CODIGO                           AS CODIGO_FIM,
          F.STATUS_ATIVO                     AS STATUS_ATIVO_FIM,
          F.FOI_AJUSTADO                     AS FOI_AJUSTADO_FIM,
          F.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_FIM,
          F.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
          F.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
          VIT.NOME                           AS NOME_TIPO_MARCACAO,
          VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TIPO_JORNADA                   AS TIPO_JORNADA
        FROM MARCACAO_FIM MF
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
            ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
          JOIN INTERVALO F
            ON MF.COD_MARCACAO_FIM = F.CODIGO
          JOIN VIEW_INTERVALO_TIPO VIT
            ON F.COD_TIPO_INTERVALO = VIT.CODIGO
        WHERE F_IF(F_COD_FIM IS NULL, F.CODIGO IS NULL, F.CODIGO = F_COD_FIM)
    )

  -- Por algum outro erro que não seja códigos de início e fim de colaboradores diferentes e marcações não vinculadas,
  -- a function poderia também acabar retornando mais de uma linha, preferimos não utilizar limit aqui e deixar esse
  -- erro subir para o servidor tratar.
  SELECT
    I.FONTE_DATA_HORA_INICIO :: TEXT                                      AS FONTE_DATA_HORA_INICIO,
    F.FONTE_DATA_HORA_FIM :: TEXT                                         AS FONTE_DATA_HORA_FIM,
    F.JUSTIFICATIVA_ESTOURO :: TEXT                                       AS JUSTIFICATIVA_ESTOURO,
    F.JUSTIFICATIVA_TEMPO_RECOMENDADO :: TEXT                             AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
    I.LATITUDE_MARCACAO_INICIO :: TEXT                                    AS LATITUDE_MARCACAO_INICIO,
    I.LONGITUDE_MARCACAO_INICIO :: TEXT                                   AS LONGITUDE_MARCACAO_INICIO,
    F.LATITUDE_MARCACAO_FIM :: TEXT                                       AS LATITUDE_MARCACAO_FIM,
    F.LONGITUDE_MARCACAO_FIM :: TEXT                                      AS LONGITUDE_MARCACAO_FIM,
    COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                                AS COD_UNIDADE,
    LPAD(COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR) :: TEXT, 11, '0') AS CPF_COLABORADOR,
    C.NOME :: TEXT                                                        AS NOME_COLABORADOR,
    COALESCE(I.NOME_TIPO_MARCACAO, F.NOME_TIPO_MARCACAO) :: TEXT          AS NOME_TIPO_MARCACAO,
    I.DATA_HORA_INICIO AT TIME ZONE F_TZ_UNIDADE                          AS DATA_HORA_INICIO,
    F.DATA_HORA_FIM AT TIME ZONE F_TZ_UNIDADE                             AS DATA_HORA_FIM,
    TO_SECONDS(F.DATA_HORA_FIM - I.DATA_HORA_INICIO)                      AS TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS,
    COALESCE(I.TEMPO_RECOMENDADO_MINUTOS,
             F.TEMPO_RECOMENDADO_MINUTOS) * 60                            AS TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS,
    I.CODIGO_INICIO                                                       AS CODIGO_INICIO,
    F.CODIGO_FIM                                                          AS CODIGO_FIM,
    I.STATUS_ATIVO_INICIO                                                 AS STATUS_ATIVO_INICIO,
    F.STATUS_ATIVO_FIM                                                    AS STATUS_ATIVO_FIM,
    I.FOI_AJUSTADO_INICIO                                                 AS FOI_AJUSTADO_INICIO,
    F.FOI_AJUSTADO_FIM                                                    AS FOI_AJUSTADO_FIM,
    I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE F_TZ_UNIDADE            AS DATA_HORA_SINCRONIZACAO_INICIO,
    F.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE F_TZ_UNIDADE               AS DATA_HORA_SINCRONIZACAO_FIM,
    I.VERSAO_APP_MOMENTO_MARCACAO_INICIO                                  AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
    F.VERSAO_APP_MOMENTO_MARCACAO_FIM                                     AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
    I.VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO                             AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
    F.VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM                                AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
    (F.TIPO_JORNADA OR I.TIPO_JORNADA)                                    AS TIPO_JORNADA
  FROM INICIOS I
    FULL OUTER JOIN FINS F
      ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
    JOIN COLABORADOR C
      ON C.CPF = COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR);
END;
$$;
--######################################################################################################################
--######################################################################################################################


DROP FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(
F_COD_UNIDADE BIGINT,
F_COD_TIPO_INTERVALO BIGINT,
F_CPF_COLABORADOR BIGINT,
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE,
F_TIME_ZONE_UNIDADE TEXT );

-- Corrige relatório de folha de ponto da jornada. Estava trazendo marcações inativas. Também passa a retornar se
-- marcação foi ajustada.
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(
  F_COD_UNIDADE        BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE,
  F_TIME_ZONE_UNIDADE  TEXT)
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


--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;