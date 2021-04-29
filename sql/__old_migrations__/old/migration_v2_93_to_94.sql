BEGIN TRANSACTION ;
--######################################################################################################################
--######################################################################################################################
--###################################  ALTERA  FUNC_AFERICAO_RELATORIO_DADOS_GERAIS  ###################################
--################################### PARA REMOVER ESPAÇOS EM BRANCO NA NOMENCLATURA ###################################
--######################################################################################################################
--######################################################################################################################
-- PL-2026
DROP FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(BIGINT[], DATE, DATE);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "CÓDIGO AFERIÇÃO"           BIGINT,
    "UNIDADE"                   TEXT,
    "DATA E HORA"               TEXT,
    "CPF DO RESPONSÁVEL"        TEXT,
    "NOME COLABORADOR"          TEXT,
    "PNEU"                      TEXT,
    "STATUS ATUAL"              TEXT,
    "VALOR COMPRA"              NUMERIC,
    "MARCA DO PNEU"             TEXT,
    "MODELO DO PNEU"            TEXT,
    "QTD SULCOS MODELO"         SMALLINT,
    "VIDA ATUAL"                TEXT,
    "VALOR VIDA ATUAL"          TEXT,
    "BANDA APLICADA"            TEXT,
    "QTD SULCOS BANDA"          TEXT,
    "DIMENSÃO"                  TEXT,
    "DOT"                       TEXT,
    "DATA E HORA CADASTRO"      TEXT,
    "POSIÇÃO PNEU"              TEXT,
    "PLACA"                     TEXT,
    "VIDA MOMENTO AFERIÇÃO"     TEXT,
    "KM NO MOMENTO DA AFERIÇÃO" TEXT,
    "KM ATUAL"                  TEXT,
    "MARCA DO VEÍCULO"          TEXT,
    "MODELO DO VEÍCULO"         TEXT,
    "TIPO DE MEDIÇÃO COLETADA"  TEXT,
    "TIPO DA AFERIÇÃO"          TEXT,
    "TEMPO REALIZAÇÃO (mm:ss)"  TEXT,
    "SULCO INTERNO"             TEXT,
    "SULCO CENTRAL INTERNO"     TEXT,
    "SULCO CENTRAL EXTERNO"     TEXT,
    "SULCO EXTERNO"             TEXT,
    "PRESSÃO"                   TEXT)
LANGUAGE SQL
AS $$
SELECT
  A.CODIGO                                                                            AS COD_AFERICAO,
  U.NOME                                                                              AS UNIDADE,
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') AS DATA_HORA_AFERICAO,
  LPAD(C.CPF :: TEXT, 11, '0')                                                        AS CPF_COLABORADOR,
  C.NOME                                                                              AS NOME_COLABORADOR,
  P.CODIGO_CLIENTE                                                                    AS CODIGO_CLIENTE_PNEU,
  P.STATUS                                                                            AS STATUS_ATUAL_PNEU,
  ROUND(P.VALOR :: NUMERIC, 2)                                                        AS VALOR_COMPRA,
  MAP.NOME                                                                            AS MARCA_PNEU,
  MP.NOME                                                                             AS MODELO_PNEU,
  MP.QT_SULCOS                                                                        AS QTD_SULCOS_MODELO,
  (SELECT PVN.NOME
   FROM PNEU_VIDA_NOMENCLATURA PVN
   WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                                 AS VIDA_ATUAL,
  COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                               AS VALOR_VIDA_ATUAL,
  F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado')    AS BANDA_APLICADA,
  COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                               AS QTD_SULCOS_BANDA,
  DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                     AS DIMENSAO,
  P.DOT                                                                               AS DOT,
  COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                   'DD/MM/YYYY HH24:MI'), '-')                                        AS DATA_HORA_CADASTRO,
  COALESCE(TRIM(REGEXP_REPLACE(NOMENCLATURA, '[\u0080-\u00ff]|(\s+)', ' ', 'g')), '-')AS POSICAO,
  COALESCE(A.PLACA_VEICULO, '-')                                                      AS PLACA,
  (SELECT PVN.NOME
   FROM PNEU_VIDA_NOMENCLATURA PVN
   WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                     AS VIDA_MOMENTO_AFERICAO,
  COALESCE(A.KM_VEICULO :: TEXT, '-')                                                 AS KM_MOMENTO_AFERICAO,
  COALESCE(V.KM :: TEXT, '-')                                                         AS KM_ATUAL,
  COALESCE(M2.NOME, '-')                                                              AS MARCA_VEICULO,
  COALESCE(MV.NOME, '-')                                                              AS MODELO_VEICULO,
  A.TIPO_MEDICAO_COLETADA,
  A.TIPO_PROCESSO_COLETA,
  TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')               AS TEMPO_REALIZACAO_MINUTOS,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT,
                   '-'), '.', ',')                                                    AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT,
                   '-'), '.', ',')                                                    AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT,
                   '-'), '.', ',')                                                    AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT,
                   '-'), '.', ',')                                                    AS SULCO_EXTERNO,
  REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')
FROM AFERICAO A
  JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
  JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
  JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA

  -- Pode não possuir banda.
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

  -- Se foi aferição de pneu avulso, pode não possuir placa.
  LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON U.CODIGO = PONU.COD_UNIDADE AND PONU.COD_TIPO_VEICULO = V.COD_TIPO AND AV.POSICAO = PONU.POSICAO_PROLOG
  LEFT JOIN MODELO_VEICULO MV
    ON MV.CODIGO = V.COD_MODELO
  LEFT JOIN MARCA_VEICULO M2
    ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################ CORRIGE FUNC CHECKS REALIZADOS ABAIXO DO TEMPO DETERMINADO ##############################
--######################################################################################################################
--######################################################################################################################
-- PL-1865
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_CHECKS_REALIZADOS_ABAIXO_TEMPO_DEFINID(BIGINT[], BIGINT, DATE,BIGINT);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_REALIZADOS_ABAIXO_TEMPO_DEFINIDO(
  F_COD_UNIDADES                  BIGINT[],
  F_TEMPO_REALIZACAO_MILLIS       BIGINT,
  F_DATA_HOJE_UTC                 DATE,
  F_DIAS_RETROATIVOS_PARA_BUSCAR  BIGINT)
  RETURNS TABLE(
    UNIDADE                                                             TEXT,
    NOME                                                                TEXT,
    "QUANTIDADE CHECKLISTS REALIZADOS ABAIXO TEMPO ESPECIFICO"          BIGINT,
    "QUANTIDADE CHECKLISTS REALIZADOS"                                  BIGINT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL       DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL         DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
BEGIN
  RETURN QUERY
    WITH PRE_SELECT AS (
        SELECT
          U.NOME :: TEXT                                                AS NOME_UNIDADE,
          CO.NOME :: TEXT                                               AS NOME_COLABORADOR,
          COUNT(CL.CPF_COLABORADOR)
            FILTER (WHERE TEMPO_REALIZACAO < F_TEMPO_REALIZACAO_MILLIS) AS REALIZADOS_ABAIXO_TEMPO_DEFINIDO,
          COUNT(CL.CPF_COLABORADOR)                                     AS REALIZADOS
        FROM CHECKLIST CL
          JOIN UNIDADE U
            ON CL.COD_UNIDADE = U.CODIGO
          JOIN COLABORADOR CO
            ON CO.CPF = CL.CPF_COLABORADOR
        WHERE CL.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
        GROUP BY U.CODIGO, CO.CPF, CO.NOME
        ORDER BY REALIZADOS_ABAIXO_TEMPO_DEFINIDO DESC, CO.NOME ASC
    )
    SELECT
      PS.NOME_UNIDADE,
      PS.NOME_COLABORADOR,
      PS.REALIZADOS_ABAIXO_TEMPO_DEFINIDO,
      PS.REALIZADOS
  FROM PRE_SELECT PS
  WHERE PS.REALIZADOS_ABAIXO_TEMPO_DEFINIDO > 0;
END;
$$;

UPDATE DASHBOARD_COMPONENTE SET DESCRICAO = 'Mostra a quantidade de checklists realizados em menos de 1 minuto e 30 segundos' ||
                                            ' realizados nos últimos 30 dias'
WHERE CODIGO = 17;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- (PL-1734) Func para verificar se unidade tem tipo definido como Jornada.
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_VERIFICA_UNIDADE_TEM_TIPO_JORNADA(
  F_COD_UNIDADE BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN EXISTS(SELECT MTJ.COD_TIPO_JORNADA
                FROM MARCACAO_TIPO_JORNADA MTJ
                WHERE MTJ.COD_UNIDADE = F_COD_UNIDADE);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################# ALTERA AS FUNCTIONS DE ACOMPANHAMENTO DE JORNADA ###################################
--######################################################################################################################
--######################################################################################################################
-- PL-1968


-- REMOVE COLABORADORES INATIVOS E APLICA INITCAP AO NOME DO COLABORADOR NA LISTAGEM DE COLABORADORES EM DESCANSO
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_COLABORADORES_EM_DESCANSO(
  F_COD_UNIDADE         BIGINT,
  F_COD_CARGOS          BIGINT [],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    NOME_COLABORADOR               TEXT,
    DATA_HORA_INICIO_ULTIMA_VIAGEM TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_ULTIMA_VIAGEM    TIMESTAMP WITHOUT TIME ZONE,
    DURACAO_ULTIMA_VIAGEM          BIGINT,
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
        INITCAP(C.NOME)       AS NOME,
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
        AND C.STATUS_ATIVO
      ORDER BY
        F.CPF_COLABORADOR,
        COALESCE(F.COD_MARCACAO_INICIO, F.COD_MARCACAO_FIM) DESC
  )

  SELECT
    UMJC.NOME :: TEXT                                      AS NOME_COLABORADOR,
    UMJC.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE          AS DATA_HORA_INICIO_ULTIMA_VIAGEM,
    UMJC.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE             AS DATA_HORA_FIM_ULTIMA_VIAGEM,
    TO_SECONDS(UMJC.DATA_HORA_FIM - UMJC.DATA_HORA_INICIO) AS DURACAO_ULTIMA_VIAGEM,
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

-- APLICA INITCAP AO NOME DO COLABORADOR NA LISTAGEM DE JORNADAS EM ANDAMENTO
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

-- APLICA INITCAP AO NOME DO COLABORADOR NA LISTAGEM DE ACOMPANHAMENTO DE MARCAÇÕES POR COLABORADOR
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
    INITCAP(C.NOME) :: TEXT                                               AS NOME_COLABORADOR,
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

--######################################################################################################################
--######################################################################################################################
--######################## ALTERA A FUNCTION DE CÓPIA DE MODELOS DE CHECKLIST ENTRE UNIDADES ###########################
--############################## ADICIONA A CÓPIA DOS TIPOS DE VEÍCULOS VINCULADOS #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-1967

DROP FUNCTION FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(BIGINT,BIGINT,BOOLEAN);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(
      F_COD_MODELO_CHECKLIST_COPIADO         BIGINT,
      F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
      F_COPIAR_CARGOS_LIBERADOS              BOOLEAN DEFAULT TRUE,
      F_COPIAR_TIPOS_VEICULOS_LIBERADOS      BOOLEAN DEFAULT TRUE,
  OUT COD_MODELO_CHECKLIST_INSERIDO          BIGINT,
  OUT AVISO_MODELO_INSERIDO                  TEXT)
  RETURNS RECORD
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  COD_UNIDADE_MODELO_CHECKLIST_COPIADO  BIGINT;
  COD_PERGUNTA_CRIADO                   BIGINT;
  PERGUNTA_MODELO_CHECKLIST_COPIADO     CHECKLIST_PERGUNTAS%ROWTYPE;
  MODELO_VEICULO_TIPO_CHECKLIST_COPIADO CHECKLIST_MODELO_VEICULO_TIPO%ROWTYPE;
BEGIN
  -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
  IF NOT EXISTS(SELECT CODIGO
                FROM CHECKLIST_MODELO
                WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
  THEN RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
  END IF;

  -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
  IF NOT EXISTS(SELECT CODIGO
                FROM UNIDADE
                WHERE CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST)
  THEN RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST;
  END IF;

  -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DA MESMA EMPRESA.
  SELECT COD_UNIDADE
  FROM CHECKLIST_MODELO CM
  WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
  INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
  IF ((SELECT U.COD_EMPRESA
       FROM UNIDADE U
       WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST) !=
      (SELECT U.COD_EMPRESA
       FROM UNIDADE U
       WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
  THEN RAISE EXCEPTION 'Só é possível copiar modelos de checklists entre unidades da mesma empresa para garantirmos o vínculo correto de imagens da galeria.';
  END IF;

  -- INSERE O MODELO DE CHECKLIST.
  INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, NOME, STATUS_ATIVO)
  VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
          (SELECT CC.NOME
           FROM CHECKLIST_MODELO CC
           WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO),
          (SELECT CC.STATUS_ATIVO
           FROM CHECKLIST_MODELO CC
           WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO))
  RETURNING CODIGO
    INTO COD_MODELO_CHECKLIST_INSERIDO;

  -- VERIFICAMOS SE O INSERT FUNCIONOU.
  IF COD_MODELO_CHECKLIST_INSERIDO <= 0
  THEN
    RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
  END IF;

  SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
  INTO AVISO_MODELO_INSERIDO;

  IF F_COPIAR_CARGOS_LIBERADOS
  THEN
    -- INSERE OS CARGOS LIBERADOS.
    INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
      (SELECT
         COD_MODELO_CHECKLIST_INSERIDO,
         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
         CMF.COD_FUNCAO
       FROM CHECKLIST_MODELO_FUNCAO CMF
       WHERE CMF.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO);
  END IF;

  IF F_COPIAR_TIPOS_VEICULOS_LIBERADOS
  THEN
    -- COPIA OS TIPOS DE VEÍCULO VINCULADOS.
    FOR MODELO_VEICULO_TIPO_CHECKLIST_COPIADO IN
    SELECT
      CMVT.COD_UNIDADE,
      CMVT.COD_MODELO,
      CMVT.COD_TIPO_VEICULO
    FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
    WHERE CMVT.COD_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
    LOOP
      -- INSERE OS TIPOS DE VEÍCULOS VINCULADOS.
      INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (cod_unidade, cod_modelo, cod_tipo_veiculo)
      VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
              COD_MODELO_CHECKLIST_INSERIDO,
              MODELO_VEICULO_TIPO_CHECKLIST_COPIADO.COD_TIPO_VEICULO);
    END LOOP;
  END IF;

  -- INSERE AS PERGUNTAS E ALTERNATIVAS.
  FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
  SELECT
    CP.COD_CHECKLIST_MODELO,
    CP.COD_UNIDADE,
    CP.ORDEM,
    CP.PERGUNTA,
    CP.STATUS_ATIVO,
    CP.SINGLE_CHOICE,
    CP.COD_IMAGEM,
    CP.CODIGO
  FROM CHECKLIST_PERGUNTAS CP
  WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
  LOOP
    -- PERGUNTA.
    INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                     COD_UNIDADE,
                                     ORDEM,
                                     PERGUNTA,
                                     STATUS_ATIVO,
                                     SINGLE_CHOICE,
                                     COD_IMAGEM)
    VALUES (COD_MODELO_CHECKLIST_INSERIDO,
            F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.STATUS_ATIVO,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM)
    RETURNING CODIGO
      INTO COD_PERGUNTA_CRIADO;
    -- ALTERNATIVA.
    INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                COD_UNIDADE,
                                                ALTERNATIVA,
                                                ORDEM,
                                                STATUS_ATIVO,
                                                COD_PERGUNTA,
                                                ALTERNATIVA_TIPO_OUTROS,
                                                PRIORIDADE)
      (SELECT
         COD_MODELO_CHECKLIST_INSERIDO,
         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
         CAP.ALTERNATIVA,
         CAP.ORDEM,
         CAP.STATUS_ATIVO,
         COD_PERGUNTA_CRIADO,
         CAP.ALTERNATIVA_TIPO_OUTROS,
         CAP.PRIORIDADE
       FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
       WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
             AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
  END LOOP;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################  ALTERA RELATÓRIO DE VEÍCULOS POR UNIDADE  #######################################
--########################## RENOMEIA AS COLUNAS QUE CONTÉM A PALAVRA SLOTS PARA POSIÇÕES ##############################
--######################################################################################################################
--######################################################################################################################
-- PL-1982

DROP FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(BIGINT[]);
CREATE OR REPLACE FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    UNIDADE                   TEXT,
    PLACA                     TEXT,
    MARCA                     TEXT,
    MODELO                    TEXT,
    TIPO                      TEXT,
    "DIAGRAMA VINCULADO?"     TEXT,
    "KM ATUAL"                TEXT,
    STATUS                    TEXT,
    "DATA/HORA CADASTRO"      TEXT,
    "QTD PNEUS VINCULADOS"    TEXT,
    "QTD POSIÇÕES DIAGRAMA"   TEXT,
    "QTD POSIÇÕES SEM PNEUS"  TEXT,
    "QTD ESTEPES"             TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  ESTEPES INTEGER := 900;
BEGIN
  RETURN QUERY
  WITH QTD_PNEUS_VINCULADOS_PLACA AS (
      SELECT
        V.PLACA,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO < ESTEPES)  AS QTD_PNEUS_VINCULADOS,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO >= ESTEPES) AS QTD_ESTEPES_VINCULADOS,
        VT.COD_DIAGRAMA
      FROM VEICULO V
        JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
        LEFT JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
      WHERE V.COD_UNIDADE = ANY(F_COD_UNIDADES)
      GROUP BY
        V.PLACA,
        VT.COD_DIAGRAMA
  ),

      QTD_POSICOES_DIAGRAMA AS (
        SELECT
          VDE.COD_DIAGRAMA,
          SUM(VDE.QT_PNEUS) AS QTD_POSICOES_DIAGRAMA
        FROM VEICULO_DIAGRAMA_EIXOS VDE
        GROUP BY COD_DIAGRAMA
    )

  SELECT
    U.NOME :: TEXT                                                     AS UNIDADE,
    V.PLACA :: TEXT                                                    AS PLACA,
    MA.NOME :: TEXT                                                    AS MARCA,
    MO.NOME :: TEXT                                                    AS MODELO,
    VT.NOME :: TEXT                                                    AS TIPO,
    CASE WHEN QPVP.COD_DIAGRAMA IS NULL THEN 'NÃO' ELSE 'SIM' END      AS POSSUI_DIAGRAMA,
    V.KM :: TEXT                                                       AS KM_ATUAL,
    F_IF(V.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)           AS STATUS,
    COALESCE(TO_CHAR(V.DATA_HORA_CADASTRO, 'DD/MM/YYYY HH24:MI'), '-') AS DATA_HORA_CADASTRO,
    QPVP.QTD_PNEUS_VINCULADOS :: TEXT                                  AS QTD_PNEUS_VINCULADOS,
    QSD.QTD_POSICOES_DIAGRAMA :: TEXT                                     AS QTD_POSICOES_DIAGRAMA,
    (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) :: TEXT       AS QTD_POSICOES_SEM_PNEUS,
    QPVP.QTD_ESTEPES_VINCULADOS :: TEXT                                AS QTD_ESTEPES_VINCULADOS
  FROM VEICULO V
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
    JOIN MODELO_VEICULO MO ON V.COD_MODELO = MO.CODIGO
    JOIN MARCA_VEICULO MA ON MO.COD_MARCA = MA.CODIGO
    JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
    RIGHT JOIN QTD_PNEUS_VINCULADOS_PLACA QPVP ON QPVP.PLACA = V.PLACA
    LEFT JOIN QTD_POSICOES_DIAGRAMA QSD ON QSD.COD_DIAGRAMA = QPVP.COD_DIAGRAMA
  ORDER BY
    U.NOME ASC,
    STATUS ASC,
    V.PLACA ASC,
    MA.NOME ASC,
    MO.NOME ASC,
    VT.NOME ASC,
    QTD_POSICOES_SEM_PNEUS DESC;
END;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################################### ALTERA A TABELA DE ITENS DE PRÉ CONTRACHEQUE ####################################
--##################### CRIA NOVA PK PARA FACILITAR A DELEÇÃO DE MÚLTIPLOS ITENS EM SIMULTÂNEO #########################
--######################################################################################################################
--######################################################################################################################
-- PL-2002

CREATE OR REPLACE FUNCTION PUBLIC.THROW_GENERIC_ERROR(F_MESSAGE TEXT, VARIADIC F_ATTRS ANYARRAY)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  RAISE EXCEPTION '%', FORMAT(F_MESSAGE, VARIADIC F_ATTRS)
  USING ERRCODE = (SELECT SQL_ERROR_CODE
                   FROM PROLOG_SQL_ERROR_CODE
                   WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
END;
$$;

-- REMOVE A PK COMPOSTA
ALTER TABLE PRE_CONTRACHEQUE_ITENS DROP CONSTRAINT PK_PRE_CONTRACHEQUE_ITENS;

-- ADICIONA A NOVA COLUNA QUE SERVIRÁ DE ÍNDICE ÚNICO
ALTER TABLE PRE_CONTRACHEQUE_ITENS ADD CODIGO BIGSERIAL NOT NULL;

-- TRANSFORMA A NOVA COLUNA EM PK
ALTER TABLE PRE_CONTRACHEQUE_ITENS
 ADD CONSTRAINT PK_PRE_CONTRACHEQUE_ITENS PRIMARY KEY (CODIGO);

-- ADICIONA UM ÍNDICE ÚNICO PARA MANTER A INTEGRIDADE DAS IMPORTAÇÕES
ALTER TABLE PRE_CONTRACHEQUE_ITENS
  ADD CONSTRAINT UNIQUE_ITEM_PRE_CONTRACHEQUE UNIQUE (
  COD_UNIDADE,
  CPF_COLABORADOR,
  MES_REFERENCIA,
  ANO_REFERENCIA,
  CODIGO_ITEM);

-- ADICIONA FUNÇÃO DE DELEÇÃO DE MÚLTIPLOS ITENS
CREATE OR REPLACE FUNCTION FUNC_PRE_CONTRACHEQUE_DELETA_ITENS(F_COD_ITENS BIGINT[])
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  IF ((SELECT COUNT(CODIGO) FROM PRE_CONTRACHEQUE_ITENS WHERE CODIGO =  ANY (F_COD_ITENS)) <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR('Os itens selecionados não foram encontrados. Por favor, filtre novamente.');
  END IF;

  -- DELETA ITENS DO PRÉ-CONTRACHEQUE IMPORTADO.
  DELETE FROM PRE_CONTRACHEQUE_ITENS WHERE CODIGO =  ANY (F_COD_ITENS);

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--#################################### Adiciona conStraints em COLABORADOR_DATA ########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2027
-- EQUIPE
ALTER TABLE COLABORADOR_DATA
  DROP CONSTRAINT FK_COLABORADOR_EQUIPE;
ALTER TABLE EQUIPE
  ADD CONSTRAINT UNICA_EQUIPE_UNIDADE UNIQUE (CODIGO, COD_UNIDADE);
ALTER TABLE COLABORADOR_DATA
  ADD CONSTRAINT FK_COLABORADOR_EQUIPE
FOREIGN KEY (COD_UNIDADE, COD_EQUIPE) REFERENCES EQUIPE (COD_UNIDADE, CODIGO);

-- FUNCAO
ALTER TABLE COLABORADOR_DATA
  DROP CONSTRAINT FK_COLABORADOR_FUNCAO;
ALTER TABLE FUNCAO
  ADD CONSTRAINT UNICA_FUNCAO_EMPRESA UNIQUE (CODIGO, COD_EMPRESA);
ALTER TABLE COLABORADOR_DATA
  ADD CONSTRAINT FK_COLABORADOR_FUNCAO
FOREIGN KEY (COD_EMPRESA, COD_FUNCAO) REFERENCES FUNCAO (COD_EMPRESA, CODIGO);

-- UNIDADE.
ALTER TABLE COLABORADOR_DATA
  DROP CONSTRAINT FK_COLABORADOR_UNIDADE;
ALTER TABLE UNIDADE
  ADD CONSTRAINT UNICA_UNIDADE_EMPRESA UNIQUE (CODIGO, COD_EMPRESA);
ALTER TABLE COLABORADOR_DATA
  ADD CONSTRAINT FK_COLABORADOR_UNIDADE
FOREIGN KEY (COD_EMPRESA, COD_UNIDADE) REFERENCES UNIDADE (COD_EMPRESA, CODIGO);

-- UNIDADE CADASTRO.
COMMENT ON COLUMN COLABORADOR_DATA.COD_UNIDADE_CADASTRO IS 'O código da unidade onde o colaborador foi primeiramente cadastrado.
Essa coluna é sempre preenchida no insert com o valor de cod_unidade e ela não utiliza FK composta com unidade usando
cod_empresa pois um colaborador pode trocar de empresa.';

-- Se o código da unidade de cadastrado for null, seta igual ao código da unidade, do contrário, apenas garante
-- que cod_unidade e cod_unidade_cadastro sejam iguais.
CREATE OR REPLACE FUNCTION TG_FUNC_COLABORADOR_SETA_UNIDADE_CADASTRO()
  RETURNS TRIGGER AS $$
BEGIN
  IF NEW.COD_UNIDADE_CADASTRO IS NULL
  THEN
    NEW.COD_UNIDADE_CADASTRO := NEW.COD_UNIDADE;
  ELSEIF NEW.COD_UNIDADE <> NEW.COD_UNIDADE_CADASTRO
    THEN
      RAISE EXCEPTION
      'COD_UNIDADE (%) e COD_UNIDADE_CADASTRO (%) não podem ser diferentes!',
      NEW.COD_UNIDADE,
      NEW.COD_UNIDADE_CADASTRO;
  END IF;
  RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

-- CRIA A TRIGGER.
CREATE TRIGGER TG_SETA_UNIDADE_CADASTRO_COLABORADOR
  BEFORE INSERT
  ON COLABORADOR_DATA
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_COLABORADOR_SETA_UNIDADE_CADASTRO();
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### ADICIONA FUNCTION DO RELATÓRIO DE RESPOSTAS DE QUIZ ######################################
--######################################################################################################################
--######################################################################################################################
-- PL-1998

CREATE OR REPLACE FUNCTION FORMAT_WITH_TZ(
  TS_TZ         TIMESTAMP WITH TIME ZONE,
  TZ_UNIDADE    TEXT,
  TS_FORTMAT    TEXT,
  VALUE_IF_NULL TEXT DEFAULT NULL)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN COALESCE(TO_CHAR(TS_TZ AT TIME ZONE TZ_UNIDADE, TS_FORTMAT), VALUE_IF_NULL);
END;
$$;

-- CRIA A FUNCTION QUE IRÁ GERAR OS DADOS COM AS RESPOSTAS DOS QUIZZES REALIZADOS
CREATE OR REPLACE FUNCTION FUNC_QUIZ_RELATORIO_RESPOSTAS_REALIZADOS(
  F_COD_UNIDADE         BIGINT,
  F_COD_MODELO          BIGINT,
  F_CPF                 BIGINT,
  F_DATA_INICIAL        DATE,
  F_DATA_FINAL          DATE,
  F_APENAS_SELECIONADAS BOOLEAN
)
  RETURNS TABLE(
    "QUIZ"               TEXT,
    "DATA DE REALIZAÇÃO" TEXT,
    "COLABORADOR"        TEXT,
    "CPF"                TEXT,
    "PERGUNTA"           TEXT,
    "ALTERNATIVA"        TEXT,
    "SELECIONADA"        TEXT,
    "CORRETA"            TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    QM.NOME :: TEXT                                                              AS MODELO,
    FORMAT_WITH_TZ(Q.DATA_HORA, TZ_UNIDADE(Q.COD_UNIDADE), 'DD/MM/YYYY HH24:MI') AS DATA_HORA,
    C.NOME :: TEXT                                                               AS COLABORADOR,
    LPAD(C.CPF :: TEXT, 11, '0')                                                 AS CPF_COLABORADOR,
    QP.PERGUNTA :: TEXT                                                          AS PERGUNTA,
    QAP.ALTERNATIVA :: TEXT                                                      AS ALTERNATIVA,
    F_IF(QR.SELECIONADA, 'Sim' :: TEXT, 'Não' :: TEXT)                           AS SELECIONADA,
    F_IF(QAP.CORRETA, 'Sim' :: TEXT, 'Não' :: TEXT)                              AS CORRETA
  FROM QUIZ Q
    LEFT JOIN QUIZ_MODELO QM ON (QM.CODIGO = Q.COD_MODELO)
    LEFT JOIN COLABORADOR C ON (C.CPF = Q.CPF_COLABORADOR)
    LEFT JOIN QUIZ_RESPOSTAS QR ON (Q.CODIGO = QR.COD_QUIZ)
    LEFT JOIN QUIZ_PERGUNTAS QP ON (QP.CODIGO = QR.COD_PERGUNTA)
    LEFT JOIN QUIZ_ALTERNATIVA_PERGUNTA QAP
      ON (QR.COD_PERGUNTA = QAP.COD_PERGUNTA AND QR.COD_ALTERNATIVA = QAP.CODIGO)
  WHERE Q.COD_UNIDADE = F_COD_UNIDADE
        AND F_IF(F_COD_MODELO IS NULL, TRUE, Q.COD_MODELO = F_COD_MODELO)
        AND F_IF(F_CPF IS NULL, TRUE, Q.CPF_COLABORADOR = F_CPF)
        AND
        (Q.DATA_HORA AT TIME ZONE TZ_UNIDADE(Q.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        AND F_IF(F_APENAS_SELECIONADAS IS TRUE, QR.SELECIONADA, TRUE)
  ORDER BY Q.DATA_HORA, QAP.CORRETA DESC, QR.SELECIONADA;
END;
$$;

--######################################################################################################################
--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_QUIZ_RELATORIO_ESTRATIFICACAO_RESPOSTAS(
  F_COD_UNIDADE BIGINT,
  F_COD_MODELO  BIGINT)
  RETURNS TABLE(
    "QUIZ"        TEXT,
    "PERGUNTA"    TEXT,
    "RESPOSTA"    TEXT,
    "TOTAL"       BIGINT,
    "ACERTOS"     BIGINT,
    "PORCENTAGEM" TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    QM.NOME :: TEXT                                         AS QUIZ,
    QP.PERGUNTA                                             AS PERGUNTA_QUIZ,
    QAP.ALTERNATIVA                                         AS RESPOSTA,
    COUNT(QAP.ALTERNATIVA)                                  AS TOTAL,
    SUM(CASE WHEN QAP.CORRETA = TRUE AND QR.SELECIONADA = TRUE
      THEN 1
        ELSE 0 END)                                         AS ACERTOS,
    ROUND((SUM(CASE WHEN QAP.CORRETA = TRUE AND QR.SELECIONADA = TRUE
      THEN 1
               ELSE 0 END) :: FLOAT /
           COUNT(QAP.ALTERNATIVA) * 100) :: NUMERIC) || '%' AS PORCENTAGEM
  FROM QUIZ Q
    JOIN QUIZ_MODELO QM ON QM.CODIGO = Q.COD_MODELO
    JOIN QUIZ_PERGUNTAS QP ON QP.COD_MODELO = Q.COD_MODELO
    JOIN QUIZ_ALTERNATIVA_PERGUNTA QAP ON QAP.COD_PERGUNTA = QP.CODIGO
    JOIN QUIZ_RESPOSTAS QR ON QR.COD_QUIZ = Q.CODIGO
  WHERE QAP.CORRETA = TRUE
        AND Q.COD_UNIDADE = F_COD_UNIDADE
        AND F_IF(F_COD_MODELO IS NULL, TRUE, Q.COD_MODELO = F_COD_MODELO)
  GROUP BY QUIZ, PERGUNTA, ALTERNATIVA
  ORDER BY QUIZ, PORCENTAGEM DESC;
END;
$$;

--######################################################################################################################
--######################################################################################################################

DROP FUNCTION FUNC_RELATORIO_QUIZ_REALIZACAO_CARGO(
F_COD_UNIDADE BIGINT,
F_EQUIPE TEXT );

CREATE FUNCTION FUNC_QUIZ_RELATORIO_REALIZACAO_CARGO(
  F_COD_UNIDADE BIGINT,
  F_COD_MODELO  BIGINT)
  RETURNS TABLE(
    "QUIZ"                         TEXT,
    "CARGO"                        TEXT,
    "COLABORADORES CADASTRADOS"    BIGINT,
    "COLABORADORES QUE REALIZARAM" BIGINT,
    "PROPORÇÃO"                    TEXT)
LANGUAGE SQL
AS $$
SELECT
  QM.NOME,
  F.NOME,
  REALIZAR.TOTAL_DEVERIAM_TER_REALIZADO,
  COALESCE(REALIZADOS.TOTAL_REALIZARAM, 0) AS REALIZARAM,
  TRUNC((COALESCE(REALIZADOS.TOTAL_REALIZARAM, 0) / REALIZAR.TOTAL_DEVERIAM_TER_REALIZADO :: FLOAT) * 100) || '%'
FROM QUIZ_MODELO_FUNCAO QMF
  JOIN QUIZ_MODELO QM ON QM.CODIGO = QMF.COD_MODELO AND QM.COD_UNIDADE = QMF.COD_UNIDADE
  JOIN UNIDADE U ON U.CODIGO = QMF.COD_UNIDADE
  JOIN FUNCAO F ON F.CODIGO = QMF.COD_FUNCAO_COLABORADOR AND U.COD_EMPRESA = F.COD_EMPRESA
  JOIN (SELECT
          QMF.COD_MODELO,
          QMF.COD_FUNCAO_COLABORADOR AS COD_FUNCAO_DEVERIAM,
          COUNT(C.CPF)               AS TOTAL_DEVERIAM_TER_REALIZADO
        FROM QUIZ_MODELO_FUNCAO QMF
          JOIN COLABORADOR C
            ON C.COD_FUNCAO = QMF.COD_FUNCAO_COLABORADOR AND C.COD_UNIDADE = QMF.COD_UNIDADE AND C.STATUS_ATIVO IS TRUE
        WHERE QMF.COD_UNIDADE = F_COD_UNIDADE
              AND F_IF(F_COD_MODELO IS NULL, TRUE, QMF.COD_MODELO = F_COD_MODELO)
        GROUP BY 1, 2) AS REALIZAR
    ON QMF.COD_FUNCAO_COLABORADOR = REALIZAR.COD_FUNCAO_DEVERIAM AND QMF.COD_MODELO = REALIZAR.COD_MODELO
  LEFT JOIN (SELECT
               CALCULO.COD_MODELO,
               CALCULO.COD_FUNCAO AS COD_FUNCAO_REALIZARAM,
               COUNT(CALCULO.CPF) AS TOTAL_REALIZARAM
             FROM
               (SELECT
                  Q.COD_MODELO,
                  C.CPF,
                  C.COD_FUNCAO,
                  COUNT(C.COD_FUNCAO)
                FROM QUIZ Q
                  JOIN COLABORADOR C ON C.CPF = Q.CPF_COLABORADOR AND C.STATUS_ATIVO IS TRUE
                WHERE Q.COD_UNIDADE = F_COD_UNIDADE
                      AND F_IF(F_COD_MODELO IS NULL, TRUE, Q.COD_MODELO = F_COD_MODELO)
                GROUP BY 1, 2, 3) AS CALCULO
             GROUP BY 1, 2) AS REALIZADOS
    ON QMF.COD_FUNCAO_COLABORADOR = REALIZADOS.COD_FUNCAO_REALIZARAM AND REALIZADOS.COD_MODELO = QMF.COD_MODELO
WHERE QMF.COD_UNIDADE = F_COD_UNIDADE
      AND F_IF(F_COD_MODELO IS NULL, TRUE, QMF.COD_MODELO = F_COD_MODELO)
ORDER BY QM.NOME, F.NOME;
$$;

--######################################################################################################################
--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_QUIZ_RELATORIO_ESTRATIFICACAO_REALIZACAO(
  F_COD_UNIDADE  BIGINT,
  F_COD_MODELO   BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "MAT PROMAX"               TEXT,
    "MAT RH"                   TEXT,
    "NOME"                     TEXT,
    "CARGO"                    TEXT,
    "REALIZADOS"               BIGINT,
    "APROVADOS"                BIGINT,
    "PORCENTAGEM DE APROVAÇÃO" TEXT,
    "MÁXIMO DE ACERTOS"        INTEGER,
    "MÍNIMO DE ACERTOS"        INTEGER)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    C.MATRICULA_AMBEV :: TEXT                                     AS MAT_PROMAX,
    C.MATRICULA_TRANS :: TEXT                                     AS MAT_RH,
    INITCAP(C.NOME)                                               AS NOME_COLABORADOR,
    F.NOME :: TEXT                                                AS CARGO,
    COALESCE(REALIZADOS.REALIZADOS, REALIZADOS.REALIZADOS, 0)     AS REALIZADOS,
    COALESCE(REALIZADOS.QT_APROVADOS, REALIZADOS.QT_APROVADOS, 0) AS APROVADOS,
    CASE WHEN COALESCE(REALIZADOS.REALIZADOS, REALIZADOS.REALIZADOS, 0) > 0
      THEN
        ROUND(
            (COALESCE(REALIZADOS.QT_APROVADOS, REALIZADOS.QT_APROVADOS, 0) /
             COALESCE(REALIZADOS.REALIZADOS, REALIZADOS.REALIZADOS, 0) :: NUMERIC) * 100) || '%'
    ELSE 0 || '%' END                                             AS PORCENTAGEM_APROVACAO,
    COALESCE(REALIZADOS.MAX_ACERTOS, REALIZADOS.MAX_ACERTOS, 0)   AS MAXIMO_ACERTOS,
    COALESCE(REALIZADOS.MIN_ACERTOS, REALIZADOS.MIN_ACERTOS, 0)   AS MINIMO_ACERTOS
  FROM COLABORADOR C
    JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND F.COD_EMPRESA = C.COD_EMPRESA
    LEFT JOIN
    (SELECT
       C.CPF              AS CPF_REALIZADOS,
       COUNT(C.CPF)       AS REALIZADOS,
       SUM(
           CASE WHEN (Q.QT_CORRETAS :: REAL / (Q.QT_CORRETAS + Q.QT_ERRADAS) :: REAL) >= QM.PORCENTAGEM_APROVACAO
             THEN 1
           ELSE 0
           END
       )                  AS QT_APROVADOS,
       MAX(Q.QT_CORRETAS) AS MAX_ACERTOS,
       MIN(Q.QT_CORRETAS) AS MIN_ACERTOS
     FROM
       COLABORADOR C LEFT JOIN QUIZ Q ON Q.CPF_COLABORADOR = C.CPF
       JOIN QUIZ_MODELO QM ON QM.CODIGO = Q.COD_MODELO AND QM.COD_UNIDADE = Q.COD_UNIDADE
     WHERE F_IF(F_COD_MODELO IS NULL, TRUE, Q.COD_MODELO = F_COD_MODELO)
           AND (Q.DATA_HORA AT TIME ZONE TZ_UNIDADE(Q.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
     GROUP BY 1) AS REALIZADOS ON CPF_REALIZADOS = C.CPF
  WHERE C.STATUS_ATIVO = TRUE
        AND C.COD_UNIDADE = F_COD_UNIDADE
  ORDER BY REALIZADOS DESC;
END;
$$;

--######################################################################################################################
--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_QUIZ_RELATORIO_EXTRATO_GERAL(
  F_COD_UNIDADE  BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "DATA DE REALIZAÇÃO" TEXT,
    "QUIZ"               TEXT,
    "NOME"               TEXT,
    "CARGO"              TEXT,
    "QTD CORRETAS"       INTEGER,
    "QTD ERRADAS"        INTEGER,
    "TOTAL PERGUNTAS"    INTEGER,
    "NOTA 0 A 10"        NUMERIC,
    "AVALIAÇÃO"          TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    FORMAT_WITH_TZ(Q.DATA_HORA, TZ_UNIDADE(Q.COD_UNIDADE), 'DD/MM/YYYY HH24:MI')          AS DATA_REALIZACAO,
    QM.NOME :: TEXT                                                                       AS QUIZ,
    INITCAP(C.NOME)                                                                       AS NOME,
    F.NOME :: TEXT                                                                        AS CARGO,
    Q.QT_CORRETAS                                                                         AS QTD_CORRETAS,
    Q.QT_ERRADAS                                                                          AS QTD_ERRADAS,
    Q.QT_CORRETAS + Q.QT_ERRADAS                                                          AS TOTAL_PERGUNTAS,
    TRUNC(((Q.QT_CORRETAS / (Q.QT_CORRETAS + Q.QT_ERRADAS) :: FLOAT) * 10) :: NUMERIC, 2) AS NOTA,
    CASE WHEN (Q.QT_CORRETAS / (Q.QT_CORRETAS + Q.QT_ERRADAS) :: FLOAT) > QM.PORCENTAGEM_APROVACAO
      THEN
        'APROVADO'
    ELSE 'REPROVADO' END                                                                  AS AVALIACAO
  FROM QUIZ Q
    JOIN QUIZ_MODELO QM ON Q.COD_MODELO = QM.CODIGO AND Q.COD_UNIDADE = QM.COD_UNIDADE
    JOIN COLABORADOR C ON C.CPF = Q.CPF_COLABORADOR AND C.COD_UNIDADE = Q.COD_UNIDADE
    JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE AND U.CODIGO = Q.COD_UNIDADE
    JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND F.COD_EMPRESA = U.COD_EMPRESA
  WHERE Q.COD_UNIDADE = F_COD_UNIDADE
        AND (Q.DATA_HORA AT TIME ZONE TZ_UNIDADE(Q.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
  ORDER BY Q.DATA_HORA DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ Cria estrutura e relatório de desgaste irregular ####################################
--######################################################################################################################
--######################################################################################################################
-- PL-1996

--######################################################################################################################
--######################################################################################################################
-- Type que contém os tipos de desgastes mapeados atualmente no ProLog.
CREATE TYPE PNEU_DESGASTE_IRREGULAR_TYPE AS ENUM (
  'DESGASTE_DIAGONAL',
  'DESGASTE_ASSIMETRICO',
  'DESGASTE_OMBROS_BANDA_RODAGEM',
  'DESGASTE_CENTRALIZADO',
  'DESGASTE_IRREGULAR_BLOCOS',
  'DESGASTE_ACENTUADO_UM_OMBRO_PNEU',
  'DESGASTE_DEGRAUS');

-- Representa os status disponíveis de pneus.
CREATE TYPE PNEU_STATUS_TYPE AS ENUM (
  'ESTOQUE',
  'EM_USO',
  'ANALISE',
  'DESCARTE');

-- O nível do desgaste é o quanto ele é grave. Desgastes acentuados precisam ser a atenção da operação pois apresentam
-- os maiores problemas.
CREATE TYPE PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE AS ENUM (
  'BAIXO',
  'MODERADO',
  'ACENTUADO');
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
CREATE TABLE IF NOT EXISTS PNEU_TIPO_DESGASTE_IRREGULAR (
  CODIGO                        SMALLINT                     NOT NULL,
  TIPO_DESGASTE_IRREGULAR       PNEU_DESGASTE_IRREGULAR_TYPE NOT NULL,
  DESCRICAO                     TEXT                         NOT NULL,
  APARENCIA_PNEU                TEXT                         NOT NULL,
  CAUSAS_PROVAVEIS              TEXT                         NOT NULL,
  ACAO                          TEXT                         NOT NULL,
  PRECAUCAO                     TEXT                         NOT NULL,
  ANALISADO_RELATORIO_DESGASTES BOOLEAN                      NOT NULL DEFAULT FALSE,
  CONSTRAINT PK_PNEU_TIPO_DESGASTE_IRREGULAR PRIMARY KEY (CODIGO),
  CONSTRAINT UNIQUE_TIPO_DESGASTE_IRREGULAR_PNEU UNIQUE (TIPO_DESGASTE_IRREGULAR)
);
COMMENT ON TABLE PNEU_TIPO_DESGASTE_IRREGULAR IS 'Descreve cada tipo de desgaste irregular que está mapeado no ProLog.';
COMMENT ON COLUMN PNEU_TIPO_DESGASTE_IRREGULAR.ANALISADO_RELATORIO_DESGASTES IS 'Informa se o tipo de desgaste é um
desgaste atualmente analisado e estratificado pelo ProLog no relatório de desgastes irregulares.';

CREATE TABLE IF NOT EXISTS PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG (
  TIPO_DESGASTE_IRREGULAR             PNEU_DESGASTE_IRREGULAR_TYPE NOT NULL,
  FAIXA_DESGASTE_BAIXO_MILIMETROS     NUMRANGE                     NOT NULL,
  FAIXA_DESGASTE_MODERADO_MILIMETROS  NUMRANGE                     NOT NULL,
  FAIXA_DESGASTE_ACENTUADO_MILIMETROS NUMRANGE                     NOT NULL,
  CONSTRAINT PK_PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG PRIMARY KEY (TIPO_DESGASTE_IRREGULAR),
  CONSTRAINT FK_PNEU_NIVEL_DESGASTE_PROLOG_TIPO_DESGASTE
  FOREIGN KEY (TIPO_DESGASTE_IRREGULAR) REFERENCES PNEU_TIPO_DESGASTE_IRREGULAR (TIPO_DESGASTE_IRREGULAR)
);
COMMENT ON TABLE PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG IS 'Essa tabela salva cada tipo de desgaste analisado pelo
ProLog e qual a faixa de sulco, em milímetros, que é analisada para categorizar um desgaste no nível correto, sendo:
BAIXO, MODERADO ou ACENTUADO.';

-- DESGASTE_DIAGONAL
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO)
VALUES (
           1,
           'DESGASTE_DIAGONAL',
           'Desgaste diagonal',
           'Desgastes localizados e acentuados em toda a circunferência.',
           'Problemas mecânicos (desbalanceamento do conjunto, folgas nos rolamentos). Rodados duplos mal geminados e/ou pressões desiguais, desalinhamento do veículo e etc.',
           'Retirar o pneu de serviço e enviar a um reformador. Importante fazer manutenção do veículo.',
           'Realizar periodicamente a manutenção do veículo, sistema de freios, montagem e balanceamento dos pneus.');

-- DESGASTE_ASSIMETRICO
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO)
VALUES (
           2,
           'DESGASTE_ASSIMETRICO',
           'Desgaste assimétrico',
           'Áreas da banda de rodagem mais gastas em pontos alternados.',
           'Problemas mecânicos (falhas na suspensão, desbalanceamento do conjunto, diferença de pressão entre duplos, folgas nos rolamentos e etc) agravados por rodagem sem carga e altas velocidades.',
           'Retirar o pneu de serviço e enviar a um reformador. Importante fazer manutenção do veículo.',
           'Realizar periodicamente a manutenção do veículo, sistema de freios, montagem e balanceamento dos pneus.');

-- DESGASTE_OMBROS_BANDA_RODAGEM
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO,
    ANALISADO_RELATORIO_DESGASTES)
VALUES (
           3,
           'DESGASTE_OMBROS_BANDA_RODAGEM',
           'Desgastes nos ombros da banda de rodagem',
           'Ombros apresentam desgaste mais acentuado que o centro da banda de rodagem.',
           'Rodagem com baixa pressão ou excesso de carga.',
           'Calibrar os pneus com a pressão indicada conforme orientação do fabricante. Quando o desgaste atingir o TWI, em um dos pontos, retire os pneus para reforma ou reposição da borracha no ombro.',
           'Calibrar periodicamente os pneus, sempre frios, e realizar inspeções visuais nos pneus.',
           TRUE);

INSERT INTO PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG (
    TIPO_DESGASTE_IRREGULAR,
    FAIXA_DESGASTE_BAIXO_MILIMETROS,
    FAIXA_DESGASTE_MODERADO_MILIMETROS,
    FAIXA_DESGASTE_ACENTUADO_MILIMETROS)
VALUES (
           'DESGASTE_OMBROS_BANDA_RODAGEM',
           NUMRANGE(0.0, 1.0, '()'),
           NUMRANGE(1.0, 2.0, '[)'),
           NUMRANGE(2.0, NULL, '[)'));

-- DESGASTE_CENTRALIZADO
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO,
    ANALISADO_RELATORIO_DESGASTES)
VALUES (
           4,
           'DESGASTE_CENTRALIZADO',
           'Desgaste centralizado',
           'Desgaste na região central da banda de rodagem.',
           'Pressão excessiva em relação à carga transportada ou equipamento de inflagem desregulado.',
           'Ajustar a pressão com equipamento confiável conforme a carga. Para casos que o desgaste tenha atingido o TWI, retire para reforma.',
           'Calibrar periodicamente os pneus, sempre frios, e de acordo com a orientação do fabricante.',
           TRUE);

INSERT INTO PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG (
    TIPO_DESGASTE_IRREGULAR,
    FAIXA_DESGASTE_BAIXO_MILIMETROS,
    FAIXA_DESGASTE_MODERADO_MILIMETROS,
    FAIXA_DESGASTE_ACENTUADO_MILIMETROS)
VALUES (
           'DESGASTE_CENTRALIZADO',
           NUMRANGE(0.0, 1.0, '()'),
           NUMRANGE(1.0, 2.0, '[)'),
           NUMRANGE(2.0, NULL, '[)'));

-- DESGASTE_IRREGULAR_BLOCOS
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO)
VALUES (
           5,
           'DESGASTE_IRREGULAR_BLOCOS',
           'Desgaste irregular nos blocos',
           'Blocos desgastados irregularmente em toda a circunferência.',
           'Baixa pressão, uso incorreto dos freios e desenhos inadequados.',
           'Fazer rodízio para ajustar a altura dos pneus. Em casos em que o desgaste foi muito irregular, mantenha-os nas posições de uso.',
           'Escolher desenhos adequados ao segmento. Calibrar os pneus conforme orientação do fabricante e sempre frios. Utilizar os freios a motor corretamente.');

-- DESGASTE_ACENTUADO_UM_OMBRO_PNEU
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO,
    ANALISADO_RELATORIO_DESGASTES)
VALUES (
           6,
           'DESGASTE_ACENTUADO_UM_OMBRO_PNEU',
           'Desgaste em um dos ombros do pneu',
           'O pneu apresenta maior desgaste em um dos ombros, porém sem sinal de arraste.',
           'Câmber do veículo incorreto, sobrecarga ou eixo empenado.',
           'Realizar manutenção preventiva na suspensão do veículo.',
           'Manter manutenção preventiva na suspensão do veículo.',
           TRUE);

INSERT INTO PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG (
    TIPO_DESGASTE_IRREGULAR,
    FAIXA_DESGASTE_BAIXO_MILIMETROS,
    FAIXA_DESGASTE_MODERADO_MILIMETROS,
    FAIXA_DESGASTE_ACENTUADO_MILIMETROS)
VALUES (
           'DESGASTE_ACENTUADO_UM_OMBRO_PNEU',
           NUMRANGE(0.0, 1.0, '()'),
           NUMRANGE(1.0, 2.0, '[)'),
           NUMRANGE(2.0, NULL, '[)'));

-- DESGASTE_DEGRAUS
INSERT INTO PNEU_TIPO_DESGASTE_IRREGULAR (
    CODIGO,
    TIPO_DESGASTE_IRREGULAR,
    DESCRICAO,
    APARENCIA_PNEU,
    CAUSAS_PROVAVEIS,
    ACAO,
    PRECAUCAO)
VALUES (
           7,
           'DESGASTE_DEGRAUS',
           'Desgaste em degraus',
           'Desgaste em algumas raias dos desenhos de eixos livres.',
           'Diferenças nas pressões e tamanhos dos pneus, folgas nos rolamentos, rodas inadequadas, mau assentamento dos talões. A alta velocidade sem carga agrava todos os fatores anteriores.',
           'Retirar o pneu de uso e encaminhar a um reformador para reforma ou reparo. Importante realizar manutenção corretiva no veículo.',
           'Realizar manutenção preventiva no veículo e nos pneus, principalmente a calibragem periódica.');

--######################################################################################################################
--######################################################################################################################
-- Cria funcs auxiliares.

-- Dado uma variação de desgaste e um tipo de desgaste, verifica em qual nível se enquadra o desgaste atual.
-- Atualmente essa function compara apenas os padrões do ProLog para o tipo de desgaste informado.
-- Caso a variação seja <= 0, será retornado null.
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(
  F_VARIACAO                REAL,
  F_TIPO_DESGASTE_ANALISADO PNEU_DESGASTE_IRREGULAR_TYPE)
  RETURNS PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF F_VARIACAO <= 0
  THEN
    RETURN NULL :: PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE;
  END IF;

  IF F_VARIACAO :: NUMERIC <@ (SELECT FAIXA_DESGASTE_BAIXO_MILIMETROS
                               FROM PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG
                               WHERE TIPO_DESGASTE_IRREGULAR = F_TIPO_DESGASTE_ANALISADO)
  THEN
    RETURN 'BAIXO' :: PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE;
  ELSEIF F_VARIACAO :: NUMERIC <@ (SELECT FAIXA_DESGASTE_MODERADO_MILIMETROS
                                   FROM PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG
                                   WHERE TIPO_DESGASTE_IRREGULAR = F_TIPO_DESGASTE_ANALISADO)
    THEN
      RETURN 'MODERADO' :: PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE;
  ELSEIF F_VARIACAO :: NUMERIC <@ (SELECT FAIXA_DESGASTE_ACENTUADO_MILIMETROS
                                   FROM PNEU_NIVEL_DESGASTE_IRREGULAR_PADRAO_PROLOG
                                   WHERE TIPO_DESGASTE_IRREGULAR = F_TIPO_DESGASTE_ANALISADO)
    THEN
      RETURN 'ACENTUADO' :: PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE;
  END IF;
END;
$$;

-- Essa function analisa e categoriza os pneus, atualmente, em 3 tipos de desgastes: DESGASTE_OMBROS_BANDA_RODAGEM,
-- DESGASTE_CENTRALIZADO e DESGASTE_ACENTUADO_UM_OMBRO_PNEU. Se o desgaste não se enquadrar em nenhum desses 3, será
-- retornado null para TIPO_DESGASTE_IRREGULAR e NIVEL_DESGASTE_IRREGULAR. O código do pneu sempre retornará o mesmo
-- fornecido.
CREATE OR REPLACE FUNCTION FUNC_PNEU_VERIFICA_DESGASTE_IRREGULAR(
  F_COD_PNEU              BIGINT,
  F_SULCO_EXTERNO         REAL,
  F_SULCO_CENTRAL_EXTERNO REAL,
  F_SULCO_CENTRAL_INTERNO REAL,
  F_SULCO_INTERNO         REAL)
  RETURNS TABLE(
    COD_PNEU                 BIGINT,
    TEM_DESGASTE_IRREGULAR   BOOLEAN,
    TIPO_DESGASTE_IRREGULAR  PNEU_DESGASTE_IRREGULAR_TYPE,
    NIVEL_DESGASTE_IRREGULAR PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE)
LANGUAGE PLPGSQL
AS $$
DECLARE
  -- Esse array irá salvar os níveis de desgaste encontrados em um mesmo pneu.
  -- Ex.: Para um desgaste central, o sulco central em relação ao sulco externo pode ter um desgaste MODERADO. Porém,
  -- em relação ao interno pode ter um desgaste ACENTUADO. Nesse array será salvo os dois níveis.
  TT PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE [];
BEGIN

  -- DESGASTE_OMBROS_BANDA_RODAGEM
  IF (F_SULCO_EXTERNO < F_SULCO_CENTRAL_EXTERNO AND F_SULCO_EXTERNO < F_SULCO_CENTRAL_INTERNO)
     AND (F_SULCO_INTERNO < F_SULCO_CENTRAL_EXTERNO AND F_SULCO_INTERNO < F_SULCO_CENTRAL_INTERNO)
  THEN
    -- Verfica a variação do sulco externo com relação aos centrais.
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_EXTERNO - F_SULCO_EXTERNO, 'DESGASTE_OMBROS_BANDA_RODAGEM')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_INTERNO - F_SULCO_EXTERNO, 'DESGASTE_OMBROS_BANDA_RODAGEM')) INTO TT;

    -- Verfica a variação do sulco interno com relação aos centrais.
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_EXTERNO - F_SULCO_INTERNO, 'DESGASTE_OMBROS_BANDA_RODAGEM')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_INTERNO - F_SULCO_INTERNO, 'DESGASTE_OMBROS_BANDA_RODAGEM')) INTO TT;
    RETURN QUERY
    SELECT
           F_COD_PNEU,
           TRUE,
           'DESGASTE_OMBROS_BANDA_RODAGEM' :: PNEU_DESGASTE_IRREGULAR_TYPE,
        -- Nesse momento, o array irá conter todos os níveis de desgaste encontrados no pneu analisado. Incluindo nulls,
        -- caso a variação seja <= 0. Nós pegamos o nível mais alto de desgaste encontrado e retornamos como o nível
        -- de desgaste do pneu. O que esse SELECT faz é transformar o array em várias linhas usando UNNEST, ordenar pelo
        -- ENUM e pegar apenas o nível mais alto usando LIMIT 1.
        -- Atente-se para algumas coisas importantes:
        -- 1 - ORDER BY NIVEL só funciona pois o PG ordena um enum com base na ordem em que os elementos foram criados
        -- nesse enum. E nós criamos como BAIXO, MODERADO, ACENTUADO. Por isso ORDER BY NIVEL DESC funciona.
        -- 2 - É imprescindível manter o NULLS LAST. Isso porque se o nível de desgaste for null, caso variação <= 0, como
        -- falado acima, o ORDER BY faria esses valores nulls ficarem primeiro e nós acabaríamos retornando eles como o nível
        -- de desgaste do pneu.
           (SELECT UNNEST(TT) AS NIVEL ORDER BY NIVEL DESC NULLS LAST LIMIT 1);
    RETURN;
  END IF;

  -- DESGASTE_CENTRALIZADO
  IF (F_SULCO_CENTRAL_EXTERNO < F_SULCO_EXTERNO AND F_SULCO_CENTRAL_EXTERNO < F_SULCO_INTERNO)
     AND (F_SULCO_CENTRAL_INTERNO < F_SULCO_EXTERNO AND F_SULCO_CENTRAL_INTERNO < F_SULCO_INTERNO)
  THEN
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_EXTERNO - F_SULCO_CENTRAL_EXTERNO, 'DESGASTE_CENTRALIZADO')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_INTERNO - F_SULCO_CENTRAL_EXTERNO, 'DESGASTE_CENTRALIZADO')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_EXTERNO - F_SULCO_CENTRAL_INTERNO, 'DESGASTE_CENTRALIZADO')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_INTERNO - F_SULCO_CENTRAL_INTERNO, 'DESGASTE_CENTRALIZADO')) INTO TT;
    RETURN QUERY
    SELECT
           F_COD_PNEU,
           TRUE,
           'DESGASTE_CENTRALIZADO' :: PNEU_DESGASTE_IRREGULAR_TYPE,
           (SELECT UNNEST(TT) AS NIVEL ORDER BY NIVEL DESC NULLS LAST LIMIT 1);
    RETURN;
  END IF;

  -- DESGASTE_ACENTUADO_UM_OMBRO_PNEU
  IF (F_SULCO_EXTERNO < F_SULCO_CENTRAL_EXTERNO
      AND F_SULCO_EXTERNO < F_SULCO_CENTRAL_INTERNO
      AND F_SULCO_EXTERNO < F_SULCO_INTERNO)
     OR (F_SULCO_INTERNO < F_SULCO_CENTRAL_EXTERNO
         AND F_SULCO_INTERNO < F_SULCO_CENTRAL_INTERNO
         AND F_SULCO_INTERNO < F_SULCO_EXTERNO)
  THEN
    -- Verifica a variação do sulco externo com relação a todos os outros sulcos.
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_EXTERNO - F_SULCO_EXTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_INTERNO - F_SULCO_EXTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_INTERNO - F_SULCO_EXTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;

    -- Verifica a variação do sulco interno com relação a todos os outros sulcos.
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_EXTERNO - F_SULCO_INTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_CENTRAL_INTERNO - F_SULCO_INTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;
    SELECT ARRAY_APPEND(TT, FUNC_PNEU_GET_NIVEL_DESGASTE_IRREGULAR(F_SULCO_EXTERNO - F_SULCO_INTERNO, 'DESGASTE_ACENTUADO_UM_OMBRO_PNEU')) INTO TT;

    RETURN QUERY
    SELECT
           F_COD_PNEU,
           TRUE,
           'DESGASTE_ACENTUADO_UM_OMBRO_PNEU' :: PNEU_DESGASTE_IRREGULAR_TYPE,
           (SELECT UNNEST(TT) AS NIVEL ORDER BY NIVEL DESC NULLS LAST LIMIT 1);
    RETURN;
  END IF;

  RETURN QUERY
  SELECT
         F_COD_PNEU,
         FALSE,
         NULL :: PNEU_DESGASTE_IRREGULAR_TYPE,
         NULL :: PNEU_DESGASTE_IRREGULAR_NIVEL_TYPE;
END;
$$;

-- Function para formatar um timestamp aplicando tz e coalesce.
CREATE OR REPLACE FUNCTION FORMAT_WITH_TZ(
  TS_TZ         TIMESTAMP WITH TIME ZONE,
  TZ_UNIDADE    TEXT,
  TS_FORTMAT    TEXT,
  VALUE_IF_NULL TEXT DEFAULT NULL)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN COALESCE(TO_CHAR(TS_TZ AT TIME ZONE TZ_UNIDADE, TS_FORTMAT), VALUE_IF_NULL);
END;
$$;

-- Formata a dimensão do pneu no padrão atualmente utilizado pelo ProLog.
CREATE OR REPLACE FUNCTION FUNC_PNEU_FORMAT_DIMENSAO(
  F_LARGURA INTEGER,
  F_ALTURA  INTEGER,
  F_ARO     REAL)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN (((F_LARGURA || '/' :: TEXT) || F_ALTURA) || ' R' :: TEXT) || F_ARO;
END;
$$;

-- Formata um sulco do pneu no padrão atualmente utilizado pelo ProLog.
CREATE OR REPLACE FUNCTION FUNC_PNEU_FORMAT_SULCO(
  F_SULCO REAL)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN REPLACE(COALESCE(TRUNC(F_SULCO :: NUMERIC, 2) :: TEXT, '-'), '.', ',');
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Cria func de relatório.

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(
  F_COD_UNIDADES BIGINT [],
  F_STATUS_PNEU  PNEU_STATUS_TYPE DEFAULT NULL)
  RETURNS TABLE(
    "UNIDADE ALOCADO"       TEXT,
    "PNEU"                  TEXT,
    "STATUS"                TEXT,
    "VALOR DE AQUISIÇÃO"    TEXT,
    "DATA/HORA CADASTRO"    TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "VALOR DA BANDA"        TEXT,
    "MEDIDAS"               TEXT,
    "PLACA"                 TEXT,
    "TIPO"                  TEXT,
    "POSIÇÃO"               TEXT,
    "QUANTIDADE DE SULCOS"  TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "PRESSÃO IDEAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "DOT"                   TEXT,
    "ÚLTIMA AFERIÇÃO"       TEXT,
    "DESCRIÇÃO DESGASTE"    TEXT,
    "NÍVEL DE DESGASTE"     TEXT,
    "APARÊNCIA PNEU"        TEXT,
    "CAUSAS PROVÁVEIS"      TEXT,
    "AÇÃO"                  TEXT,
    "PRECAUÇÃO"             TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_TIMESTAMP_FORMAT TEXT := 'DD/MM/YYYY HH24:MI';
BEGIN
  RETURN QUERY
  -- Essa CTE busca o código da última aferição de cada pneu.
  -- Com o código nós conseguimos buscar depois a data/hora da aferição e o código da unidade em que ocorreu,
  -- para aplicar o TZ correto.
  WITH ULTIMAS_AFERICOES AS (
      SELECT
             AV.COD_PNEU   AS COD_PNEU_AFERIDO,
             MAX(A.CODIGO) AS COD_AFERICAO
      FROM AFERICAO A
             JOIN AFERICAO_VALORES AV
               ON AV.COD_AFERICAO = A.CODIGO
             JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
      WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
      GROUP BY AV.COD_PNEU
  )

  SELECT
         U.NOME :: TEXT                                                               AS UNIDADE_ALOCADO,
         P.CODIGO_CLIENTE :: TEXT                                                     AS COD_PNEU,
         P.STATUS :: TEXT                                                             AS STATUS,
         COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                          AS VALOR_AQUISICAO,
         FORMAT_WITH_TZ(P.DATA_HORA_CADASTRO,
                        TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        F_TIMESTAMP_FORMAT,
                        '-')                                                          AS DATA_HORA_CADASTRO,
         MAP.NOME :: TEXT                                                             AS NOME_MARCA_PNEU,
         MP.NOME :: TEXT                                                              AS NOME_MODELO_PNEU,
         F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME) AS BANDA_APLICADA,
         COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                        AS VALOR_BANDA,
         FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO)                     AS MEDIDAS,
         COALESCE(VP.PLACA, '-') :: TEXT                                              AS PLACA,
         COALESCE(VT.NOME, '-') :: TEXT                                               AS TIPO_VEICULO,
         COALESCE(PONU.NOMENCLATURA, '-') :: TEXT                                     AS POSICAO_PNEU,
         COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                               AS QTD_SULCOS,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
         COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                AS PRESSAO_ATUAL,
         P.PRESSAO_RECOMENDADA :: TEXT                                                AS PRESSAO_RECOMENDADA,
         PVN.NOME :: TEXT                                                             AS VIDA_ATUAL,
         COALESCE(P.DOT, '-') :: TEXT                                                 AS DOT,
      -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
      -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
         CASE WHEN UA.COD_AFERICAO IS NULL
                   THEN 'Nunca Aferido'
              ELSE
             FORMAT_WITH_TZ((SELECT A.DATA_HORA
                             FROM AFERICAO A
                             WHERE A.CODIGO = UA.COD_AFERICAO),
                            TZ_UNIDADE((SELECT A.COD_UNIDADE
                                        FROM AFERICAO A
                                        WHERE A.CODIGO = UA.COD_AFERICAO)),
                            F_TIMESTAMP_FORMAT)
             END                                                                          AS ULTIMA_AFERICAO,
         PTDI.DESCRICAO                                                               AS DESCRICAO_DESGASTE,
      -- Por enquanto, deixamos hardcoded os ranges de cada nível de desgaste.
         CASE
           WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'BAIXO'
                   THEN 'BAIXO (0.1 mm até 0.9 mm)'
           WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'MODERADO'
                   THEN 'MODERADO (1.0 mm até 2.0 mm)'
           WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'ACENTUADO'
                   THEN 'ACENTUADO (2.1 mm e acima)'
             END                                                                          AS NIVEL_DESGASTE,
         PTDI.APARENCIA_PNEU                                                          AS APARENCIA_PNEU,
         PTDI.CAUSAS_PROVAVEIS                                                        AS CAUSAS_PROVAVEIS,
         PTDI.ACAO                                                                    AS ACAO,
         PTDI.PRECAUCAO                                                               AS PRECAUCAO
  FROM PNEU P
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
         JOIN FUNC_PNEU_VERIFICA_DESGASTE_IRREGULAR(P.CODIGO,
                                                    P.ALTURA_SULCO_EXTERNO,
                                                    P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                                    P.ALTURA_SULCO_INTERNO) VERIF_DESGASTE
           ON VERIF_DESGASTE.COD_PNEU = P.CODIGO
         LEFT JOIN PNEU_TIPO_DESGASTE_IRREGULAR PTDI
           ON PTDI.TIPO_DESGASTE_IRREGULAR = VERIF_DESGASTE.TIPO_DESGASTE_IRREGULAR
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN VEICULO_PNEU VP
           ON P.CODIGO = VP.COD_PNEU
                AND P.COD_UNIDADE = VP.COD_UNIDADE
         LEFT JOIN VEICULO V
           ON VP.PLACA = V.PLACA
                AND VP.COD_UNIDADE = V.COD_UNIDADE
         LEFT JOIN VEICULO_TIPO VT
           ON V.cod_tipo = VT.codigo
         LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
           ON PONU.COD_UNIDADE = V.COD_UNIDADE
                AND PONU.COD_TIPO_VEICULO = V.COD_TIPO
                AND PONU.POSICAO_PROLOG = VP.POSICAO
         LEFT JOIN ULTIMAS_AFERICOES UA
           ON UA.COD_PNEU_AFERIDO = P.CODIGO
  WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
    AND F_IF(F_STATUS_PNEU IS NULL, TRUE, F_STATUS_PNEU = P.STATUS :: PNEU_STATUS_TYPE)
    AND VERIF_DESGASTE.TEM_DESGASTE_IRREGULAR
  ORDER BY VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR DESC, U.NOME, P.CODIGO_CLIENTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### EXCLUI A FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_VENCIDOS ################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_AFERICAO_VENCIDA(BIGINT [], TIMESTAMPTZ);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### MODIFICA FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_VENCIDOS ################################
--#################################################  PL-1736  ##########################################################
--######################################################################################################################
--######################################################################################################################
-- Essa function possuía um bug: não contabilizava no cálculo a aferição do tipo SULCO_PRESSAO. Além disso o cálculo
-- estava errado, quando existia uma aferição (sulco ou pressão) o período era subtraído de ambos, não apenas da
-- aferição realizada.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(
  F_COD_UNIDADES  BIGINT [],
  F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    UNIDADE                           TEXT,
    PLACA                             TEXT,
    PODE_AFERIR_SULCO                 BOOLEAN,
    PODE_AFERIR_PRESSAO               BOOLEAN,
    QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
    QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  AFERICAO_SULCO         VARCHAR := 'SULCO';
  AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
  AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY

  WITH VEICULOS_ATIVOS_UNIDADES AS (
      SELECT V.PLACA
      FROM VEICULO V
      WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES) AND V.STATUS_ATIVO
  ),
    -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam a placa de cada veículo e a quantidade de dias
    -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
    -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
    -- que a placa vai vencer em 20 dias.
      ULTIMA_AFERICAO_SULCO AS (
        SELECT DISTINCT ON (A.PLACA_VEICULO)
          A.COD_UNIDADE,
          A.PLACA_VEICULO                AS PLACA,
          DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
          - (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
        FROM AFERICAO A
          JOIN PNEU_RESTRICAO_UNIDADE PRU
            ON (SELECT V.COD_UNIDADE
                FROM VEICULO V
                WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
        WHERE
          A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
          -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
          -- de onde foram foram aferidas.
          AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                   FROM VEICULOS_ATIVOS_UNIDADES VAU)
        GROUP BY
          A.DATA_HORA,
          A.COD_UNIDADE,
          A.PLACA_VEICULO,
          PRU.PERIODO_AFERICAO_SULCO
        ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
    ),
      ULTIMA_AFERICAO_PRESSAO AS (
        SELECT DISTINCT ON (A.PLACA_VEICULO)
          A.COD_UNIDADE,
          A.PLACA_VEICULO                  AS PLACA,
          DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
          - (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
        FROM AFERICAO A
          JOIN PNEU_RESTRICAO_UNIDADE PRU
            ON (SELECT V.COD_UNIDADE
                FROM VEICULO V
                WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
        WHERE
          A.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
          AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                   FROM VEICULOS_ATIVOS_UNIDADES VAU)
        GROUP BY
          A.DATA_HORA,
          A.COD_UNIDADE,
          A.PLACA_VEICULO,
          PRU.PERIODO_AFERICAO_PRESSAO
        ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
    ),

      PRE_SELECT AS (
        SELECT
          U.NOME                                            AS NOME_UNIDADE,
          V.PLACA                                           AS PLACA_VEICULO,
          COALESCE((
                     SELECT (FA.PODE_AFERIR_SULCO OR FA.PODE_AFERIR_SULCO_PRESSAO)
                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                            AS PODE_AFERIR_SULCO,
          COALESCE((
                     SELECT (FA.PODE_AFERIR_PRESSAO OR FA.PODE_AFERIR_SULCO_PRESSAO)
                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                            AS PODE_AFERIR_PRESSAO,
          -- Por conta do filtro no WHERE, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
          -- nunca aferidas).
          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM UNIDADE U
          JOIN VEICULO V
            ON V.COD_UNIDADE = U.CODIGO
          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
            ON UAS.PLACA = V.PLACA
          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
            ON UAP.PLACA = V.PLACA
        WHERE
          -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
          (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
        GROUP BY
          U.NOME,
          V.PLACA,
          V.COD_TIPO,
          V.COD_UNIDADE,
          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
    )
  SELECT
    PS.NOME_UNIDADE :: TEXT                         AS NOME_UNIDADE,
    PS.PLACA_VEICULO :: TEXT                        AS PLACA_VEICULO,
    PS.PODE_AFERIR_SULCO                            AS PODE_AFERIR_SULCO,
    PS.PODE_AFERIR_PRESSAO                          AS PODE_AFERIR_PRESSAO,
    PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA :: INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
    PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA :: INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
  FROM PRE_SELECT PS
  -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
  WHERE PS.PODE_AFERIR_SULCO OR PS.PODE_AFERIR_PRESSAO
  ORDER BY
    PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
    PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;

-- ATIVA O COMPONENTE DA DASHBOARD "PLACAS COM AFERIÇÃO VENCIDA"
UPDATE DASHBOARD_COMPONENTE SET ATIVO = TRUE WHERE CODIGO = 18;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-1532

-- CORRIGE CONSTRAINT QUE VERIFICAVA O CÓDIGO ERRADO.
ALTER TABLE PNEU_TRANSFERENCIA_PROCESSO DROP CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_UNIDADE_COLABORADOR;
ALTER TABLE PNEU_TRANSFERENCIA_PROCESSO ADD CONSTRAINT FK_PNEU_TRANSFERENCIA_PROCESSO_UNIDADE_COLABORADOR
FOREIGN KEY (COD_UNIDADE_COLABORADOR) REFERENCES UNIDADE (CODIGO);

-- RENOMEAMOS A COLUNA DA TABELA DE INFORMAÇÕES DOS PNEUS TRANSFERIDOS.
ALTER TABLE PNEU_TRANSFERENCIA_INFORMACOES RENAME COLUMN COD_TRANSFERENCIA TO COD_PROCESSO_TRANSFERENCIA;

-- INSERE COLUNA PARA IDENTIFICAR A POSIÇÃO DO PNEU QUANDO TRANSFERIDO
ALTER TABLE PNEU_TRANSFERENCIA_INFORMACOES ADD COLUMN POSICAO_PNEU_TRANSFERENCIA INTEGER;

-- RECRIA FUNCTIONS QUE UTILIZAVAM A ANTIGA COLUNA.
DROP FUNCTION FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(BIGINT, BIGINT[]);
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(
  F_COD_PROCESSO_TRANSFERENCIA BIGINT,
  F_COD_PNEUS         BIGINT [])
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_ROWS BIGINT;
BEGIN
  WITH INFORMACOES_PNEUS AS (
      SELECT
        P.CODIGO,
        P.ALTURA_SULCO_INTERNO,
        P.ALTURA_SULCO_CENTRAL_INTERNO,
        P.ALTURA_SULCO_CENTRAL_EXTERNO,
        P.ALTURA_SULCO_EXTERNO,
        P.PRESSAO_ATUAL,
        P.VIDA_ATUAL,
        (SELECT VP.POSICAO FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = P.CODIGO) AS POSICAO
      FROM PNEU P
      WHERE P.CODIGO = ANY(F_COD_PNEUS)
  )

  INSERT INTO PNEU_TRANSFERENCIA_INFORMACOES (
    COD_PROCESSO_TRANSFERENCIA,
    COD_PNEU,
    ALTURA_SULCO_INTERNO,
    ALTURA_SULCO_CENTRAL_INTERNO,
    ALTURA_SULCO_CENTRAL_EXTERNO,
    ALTURA_SULCO_EXTERNO,
    PSI,
    VIDA_MOMENTO_TRANSFERENCIA,
    POSICAO_PNEU_TRANSFERENCIA)
    SELECT
      F_COD_PROCESSO_TRANSFERENCIA,
      IP.CODIGO,
      IP.ALTURA_SULCO_INTERNO,
      IP.ALTURA_SULCO_CENTRAL_INTERNO,
      IP.ALTURA_SULCO_CENTRAL_EXTERNO,
      IP.ALTURA_SULCO_EXTERNO,
      IP.PRESSAO_ATUAL,
      IP.VIDA_ATUAL,
      IP.POSICAO
    FROM INFORMACOES_PNEUS IP;
  GET DIAGNOSTICS QTD_ROWS = ROW_COUNT;
  RETURN QTD_ROWS;
END;
$$;

-- PL-2061
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_VEICULOS_SELECAO(F_COD_UNIDADE_ORIGEM BIGINT)
  RETURNS TABLE(
    COD_VEICULO                 BIGINT,
    PLACA_VEICULO               TEXT,
    KM_ATUAL_VEICULO            BIGINT,
    QTD_PNEUS_APLICADOS_VEICULO BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    V.CODIGO                                 AS COD_VEICULO,
    V.PLACA :: TEXT                          AS PLACA_VEICULO,
    V.KM                                     AS KM_ATUAL_VEICULO,
    COUNT(*)
      -- Com esse filter veículos sem pneu retornam 0 na quantidade e não 1.
      FILTER (WHERE VP.COD_PNEU IS NOT NULL) AS QTD_PNEUS_APLICADOS_VEICULO
  FROM VEICULO V
    LEFT JOIN VEICULO_PNEU VP
      ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
  WHERE V.COD_UNIDADE = F_COD_UNIDADE_ORIGEM
  GROUP BY V.CODIGO, V.PLACA, V.KM;
END;
$$;


DROP FUNCTION FUNC_PNEU_TRANSFERENCIA_LISTAGEM(BIGINT[], BIGINT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_LISTAGEM(
  F_COD_UNIDADES_ORIGEM  BIGINT [],
  F_COD_UNIDADES_DESTINO BIGINT [],
  F_DATA_INICIAL         DATE,
  F_DATA_FINAL           DATE)
  RETURNS TABLE(
    COD_PROCESSO_TRANSFERENCIA BIGINT,
    REGIONAL_ORIGEM            TEXT,
    UNIDADE_ORIGEM             TEXT,
    REGIONAL_DESTINO           TEXT,
    UNIDADE_DESTINO            TEXT,
    NOME_COLABORADOR           TEXT,
    DATA_HORA_TRANSFERENCIA    TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO                 TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH REGIONAL_ORIGEM AS (
      SELECT
        R.REGIAO AS REGIAO_ORIGEM,
        U.NOME   AS UNIDADE_ORIGEM,
        U.CODIGO AS COD_UNIDADE_ORIGEM
      FROM REGIONAL R
        JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_ORIGEM)
  ),

      REGIONAL_DESTINO AS (
        SELECT
          R.REGIAO AS REGIAO_DESTINO,
          U.NOME   AS UNIDADE_DESTINO,
          U.CODIGO AS COD_UNIDADE_DESTINO
        FROM REGIONAL R
          JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
        WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO)
    )

  SELECT
    PTP.CODIGO                 AS COD_PROCESSO_TRANSFERENCIA,
    RO.REGIAO_ORIGEM :: TEXT   AS REGIAO_ORIGEM,
    RO.UNIDADE_ORIGEM :: TEXT  AS UNIDADE_ORIGEM,
    RD.REGIAO_DESTINO :: TEXT  AS REGIAO_DESTINO,
    RD.UNIDADE_DESTINO :: TEXT AS UNIDADE_DESTINO,
    CO.NOME :: TEXT            AS NOME_COLABORADOR,
    PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR),
    PTP.OBSERVACAO :: TEXT
  FROM PNEU_TRANSFERENCIA_PROCESSO PTP
    JOIN REGIONAL_ORIGEM RO ON RO.COD_UNIDADE_ORIGEM = PTP.COD_UNIDADE_ORIGEM
    JOIN REGIONAL_DESTINO RD ON RD.COD_UNIDADE_DESTINO = PTP.COD_UNIDADE_DESTINO
    JOIN COLABORADOR CO ON CO.CODIGO = PTP.COD_COLABORADOR
  WHERE
    PTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES_ORIGEM) AND
    PTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES_DESTINO) AND
    (DATA_HORA_TRANSFERENCIA_PROCESSO AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR)) :: DATE
    BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
  ORDER BY DATA_HORA_TRANSFERENCIA_PROCESSO DESC;
END;
$$;

DROP FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(
  F_COD_PROCESSO_TRANSFERENCIA BIGINT)
  RETURNS TABLE(
    COD_PROCESSO_TRANSFERENCIA BIGINT,
    REGIONAL_ORIGEM            TEXT,
    UNIDADE_ORIGEM             TEXT,
    REGIONAL_DESTINO           TEXT,
    UNIDADE_DESTINO            TEXT,
    NOME_COLABORADOR           TEXT,
    DATA_HORA_TRANSFERENCIA    TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO                 TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH TRANSFERENCIA_PROCESSO AS (
      SELECT
        PTP.CODIGO                           AS COD_PROCESSO_TRANSFERENCIA,
        RO.REGIAO                            AS REGIONAL_ORIGEM,
        PTP.COD_UNIDADE_ORIGEM               AS COD_UNIDADE_ORIGEM,
        RD.REGIAO                            AS REGIONAL_DESTINO,
        UD.NOME                              AS UNIDADE_DESTINO,
        UO.NOME                              AS UNIDADE_ORIGEM,
        PTP.COD_UNIDADE_DESTINO              AS COD_UNIDADE_DESTINO,
        PTP.COD_UNIDADE_COLABORADOR          AS COD_UNIDADE_COLABORADOR,
        CO.NOME                              AS NOME_COLABORADOR,
        PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AS DATA_HORA_TRANSFERENCIA_PROCESSO,
        PTP.OBSERVACAO                       AS OBSERVACAO
      FROM PNEU_TRANSFERENCIA_PROCESSO PTP
        JOIN COLABORADOR CO ON PTP.COD_COLABORADOR = CO.CODIGO
        JOIN UNIDADE UO ON UO.CODIGO = PTP.COD_UNIDADE_ORIGEM
        JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
        JOIN UNIDADE UD ON UD.CODIGO = PTP.COD_UNIDADE_DESTINO
        JOIN REGIONAL RD ON UD.COD_REGIONAL = RO.CODIGO
      WHERE PTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
  )

  SELECT
    TP.COD_PROCESSO_TRANSFERENCIA                       AS COD_PROCESSO_TRANSFERENCIA,
    TP.REGIONAL_ORIGEM :: TEXT                          AS REGIAO_ORIGEM,
    TP.UNIDADE_ORIGEM :: TEXT                           AS UNIDADE_ORIGEM,
    TP.REGIONAL_DESTINO :: TEXT                         AS REGIONAL_DESTINO,
    TP.UNIDADE_DESTINO :: TEXT                          AS UNIDADE_DESTINO,
    TP.NOME_COLABORADOR :: TEXT                         AS NOME_COLABORADOR,
    TP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(TP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_TRANSFERENCIA_PROCESSO,
    TP.OBSERVACAO :: TEXT                               AS OBSERVACAO
  FROM TRANSFERENCIA_PROCESSO TP
  WHERE
    TP.COD_PROCESSO_TRANSFERENCIA = F_COD_PROCESSO_TRANSFERENCIA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################### CRIA TABELAS PARA NOVA FUNCIONALIDADE ########################################
--######################################        TRANSFERÊNCIA DE VEÍCULO        ########################################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE VEICULO_TRANSFERENCIA_PROCESSO (
  CODIGO                           BIGSERIAL                NOT NULL,
  COD_UNIDADE_ORIGEM               BIGINT                   NOT NULL,
  COD_UNIDADE_DESTINO              BIGINT                   NOT NULL,
  COD_UNIDADE_COLABORADOR          BIGINT                   NOT NULL,
  COD_COLABORADOR_REALIZACAO       BIGINT                   NOT NULL,
  DATA_HORA_TRANSFERENCIA_PROCESSO TIMESTAMP WITH TIME ZONE NOT NULL,
  OBSERVACAO                       TEXT,
  CONSTRAINT PK_VEICULO_TRANSFERENCIA_PROCESSO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_PROCESSO_UNIDADE_ORIGEM
  FOREIGN KEY (COD_UNIDADE_ORIGEM) REFERENCES UNIDADE (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_PROCESSO_UNIDADE_DESTINO
  FOREIGN KEY (COD_UNIDADE_DESTINO) REFERENCES UNIDADE (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_PROCESSO_COLABORADOR
  FOREIGN KEY (COD_COLABORADOR_REALIZACAO) REFERENCES COLABORADOR_DATA (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_PROCESSO_UNIDADE_COLABORADOR
  FOREIGN KEY (COD_UNIDADE_COLABORADOR) REFERENCES UNIDADE (CODIGO),
  CONSTRAINT UNIDADE_ORIGEM_DESTINO_DIFERENTES CHECK(COD_UNIDADE_ORIGEM != COD_UNIDADE_DESTINO)
);
COMMENT ON TABLE VEICULO_TRANSFERENCIA_PROCESSO
IS 'Tabela utilizada para salvar um processo de transferência de veículos. Um processo pode conter vários veículos.';

CREATE TABLE VEICULO_TRANSFERENCIA_INFORMACOES (
  CODIGO                           BIGSERIAL  NOT NULL,
  COD_PROCESSO_TRANSFERENCIA       BIGINT     NOT NULL,
  COD_VEICULO                      BIGINT     NOT NULL,
  COD_DIAGRAMA_VEICULO             BIGINT     NOT NULL,
  COD_TIPO_VEICULO                 BIGINT     NOT NULL,
  KM_VEICULO_MOMENTO_TRANSFERENCIA BIGINT     NOT NULL,
  CONSTRAINT PK_VEICULO_TRANSFERENCIA_INFORMACOES PRIMARY KEY (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFO_VEICULO_TRANSFERENCIA_PROC
  FOREIGN KEY (COD_PROCESSO_TRANSFERENCIA) REFERENCES VEICULO_TRANSFERENCIA_PROCESSO (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFORMACOES_VEICULO FOREIGN KEY (COD_VEICULO) REFERENCES VEICULO_DATA (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFORMACOES_DIAGRAMA
  FOREIGN KEY (COD_DIAGRAMA_VEICULO) REFERENCES VEICULO_DIAGRAMA (CODIGO),
  CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFORMACOES_TIPO_VEICULO
  FOREIGN KEY (COD_TIPO_VEICULO) REFERENCES VEICULO_TIPO (CODIGO)
);
COMMENT ON TABLE VEICULO_TRANSFERENCIA_INFORMACOES
IS 'Tabela utilizada para salvar as informações de cada veículo transferido. Toda transferência possuí um processo
associado.';

CREATE TABLE VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU (
  COD_VEICULO_TRANSFERENCIA_INFORMACOES BIGINT NOT NULL,
  COD_PROCESSO_TRANSFERENCIA_PNEU       BIGINT NOT NULL,
  CONSTRAINT PK_VINCULO_TRANSFERENCIA_VEICULO_PNEU
  PRIMARY KEY (COD_VEICULO_TRANSFERENCIA_INFORMACOES, COD_PROCESSO_TRANSFERENCIA_PNEU),
  CONSTRAINT FK_VINCULO_VEICULO_TRANSFERENCIA_INFORMACOES FOREIGN KEY (COD_VEICULO_TRANSFERENCIA_INFORMACOES)
  REFERENCES VEICULO_TRANSFERENCIA_INFORMACOES (CODIGO),
  CONSTRAINT FK_VINCULO_PNEU_TRANSFERENCIA_PROCESSO FOREIGN KEY (COD_PROCESSO_TRANSFERENCIA_PNEU)
  REFERENCES PNEU_TRANSFERENCIA_PROCESSO (CODIGO),
  CONSTRAINT UNIQUE_VINCULO_PROCESSO_TRANSFERENCIA_VEICULO UNIQUE (COD_VEICULO_TRANSFERENCIA_INFORMACOES),
  CONSTRAINT UNIQUE_VINCULO_PROCESSO_TRANSFERENCIA_PNEU UNIQUE (COD_PROCESSO_TRANSFERENCIA_PNEU)
);
COMMENT ON TABLE VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU
IS 'Tabela utilizada para salvar os códigos de vínculos entre um processo de transferência de um veículo com o processo
de transferência dos pneus que estavam aplicados nesse veículo.';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################################   INSERE COLUNA PARA IDENTIFICAR QUAL PROCESSO   ###################################
--#################################       DE TRANSFERNCIA TRANSFERIU CADA PNEU       ###################################
--######################################################################################################################
--######################################################################################################################
CREATE TYPE TIPO_PROCESSO_TRANSFERENCIA_PNEU AS ENUM ('TRANSFERENCIA_APENAS_PNEUS', 'TRANSFERENCIA_JUNTO_A_VEICULO');
ALTER TABLE PNEU_TRANSFERENCIA_PROCESSO ADD COLUMN TIPO_PROCESSO_TRANSFERENCIA TIPO_PROCESSO_TRANSFERENCIA_PNEU;
UPDATE PNEU_TRANSFERENCIA_PROCESSO SET TIPO_PROCESSO_TRANSFERENCIA = 'TRANSFERENCIA_APENAS_PNEUS';
ALTER TABLE PNEU_TRANSFERENCIA_PROCESSO ALTER COLUMN TIPO_PROCESSO_TRANSFERENCIA SET NOT NULL;
--######################################################################################################################
--######################################################################################################################

-- Altera constraints para serem 'deferíveis', assim podemos verificar apenas no final da transaction.
-- https://begriffs.com/posts/2017-08-27-deferrable-sql-constraints.html
ALTER TABLE VEICULO_PNEU ALTER CONSTRAINT FK_VEICULO_PNEU_VEICULO DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE VEICULO_PNEU ALTER CONSTRAINT FK_VEICULO_PNEU_PNEU DEFERRABLE INITIALLY IMMEDIATE;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################################   ALTERA LISTAGEM DE TRANSFERENCIAS DE PNEUS     ###################################
--#################################     PARA TER O TIPO DE PROCESSO REALIZADO        ###################################
--################################################      PL-1941     ####################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_PNEU_TRANSFERENCIA_LISTAGEM(BIGINT[], BIGINT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_LISTAGEM(
  F_COD_UNIDADES_ORIGEM  BIGINT [],
  F_COD_UNIDADES_DESTINO BIGINT [],
  F_DATA_INICIAL         DATE,
  F_DATA_FINAL           DATE)
  RETURNS TABLE(
    COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT,
    REGIONAL_ORIGEM                    TEXT,
    UNIDADE_ORIGEM                     TEXT,
    REGIONAL_DESTINO                   TEXT,
    UNIDADE_DESTINO                    TEXT,
    NOME_COLABORADOR                   TEXT,
    DATA_HORA_TRANSFERENCIA            TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO                         TEXT,
    TIPO_PROCESSO_TRANSFERENCIA        TIPO_PROCESSO_TRANSFERENCIA_PNEU,
    COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT,
    PLACA_TRANSFERIDA                  TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH REGIONAL_ORIGEM AS (
      SELECT
        R.REGIAO AS REGIAO_ORIGEM,
        U.NOME   AS UNIDADE_ORIGEM,
        U.CODIGO AS COD_UNIDADE_ORIGEM
      FROM REGIONAL R
        JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_ORIGEM)
  ),
      REGIONAL_DESTINO AS (
        SELECT
          R.REGIAO AS REGIAO_DESTINO,
          U.NOME   AS UNIDADE_DESTINO,
          U.CODIGO AS COD_UNIDADE_DESTINO
        FROM REGIONAL R
          JOIN UNIDADE U ON R.CODIGO = u.COD_REGIONAL
        WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO)
    )

  SELECT
    PTP.CODIGO                                           AS COD_PROCESSO_TRANSFERENCIA_PNEU,
    RO.REGIAO_ORIGEM :: TEXT                             AS REGIAO_ORIGEM,
    RO.UNIDADE_ORIGEM :: TEXT                            AS UNIDADE_ORIGEM,
    RD.REGIAO_DESTINO :: TEXT                            AS REGIAO_DESTINO,
    RD.UNIDADE_DESTINO :: TEXT                           AS UNIDADE_DESTINO,
    CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
    PTP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_TRANSFERENCIA_PROCESSO,
    PTP.OBSERVACAO :: TEXT                               AS OBSERVACAO,
    PTP.TIPO_PROCESSO_TRANSFERENCIA                      AS TIPO_PROCESSO_TRANSFERENCIA,
    VTI.COD_PROCESSO_TRANSFERENCIA                       AS COD_PROCESSO_TRANSFERENCIA_VEICULO,
    V.PLACA :: TEXT                                      AS PLACA_TRANSFERIDA
  FROM PNEU_TRANSFERENCIA_PROCESSO PTP
    JOIN REGIONAL_ORIGEM RO ON RO.COD_UNIDADE_ORIGEM = PTP.COD_UNIDADE_ORIGEM
    JOIN REGIONAL_DESTINO RD ON RD.COD_UNIDADE_DESTINO = PTP.COD_UNIDADE_DESTINO
    JOIN COLABORADOR CO ON CO.CODIGO = PTP.COD_COLABORADOR
    LEFT JOIN VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVPP ON PTP.CODIGO = VTVPP.COD_PROCESSO_TRANSFERENCIA_PNEU
    LEFT JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI ON VTVPP.COD_VEICULO_TRANSFERENCIA_INFORMACOES = VTI.CODIGO
    LEFT JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
  WHERE
    PTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES_ORIGEM) AND
    PTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES_DESTINO) AND
    (DATA_HORA_TRANSFERENCIA_PROCESSO AT TIME ZONE TZ_UNIDADE(PTP.COD_UNIDADE_COLABORADOR)) :: DATE
    BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
  ORDER BY DATA_HORA_TRANSFERENCIA_PROCESSO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(
  F_COD_PROCESSO_TRANSFERENCIA BIGINT)
  RETURNS TABLE(
    COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT,
    REGIONAL_ORIGEM                    TEXT,
    UNIDADE_ORIGEM                     TEXT,
    REGIONAL_DESTINO                   TEXT,
    UNIDADE_DESTINO                    TEXT,
    NOME_COLABORADOR                   TEXT,
    DATA_HORA_TRANSFERENCIA            TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO                         TEXT,
    TIPO_PROCESSO_TRANSFERENCIA        TIPO_PROCESSO_TRANSFERENCIA_PNEU,
    COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT,
    PLACA_TRANSFERIDA                  TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH TRANSFERENCIA_PROCESSO AS (
      SELECT
        PTP.CODIGO                           AS COD_PROCESSO_TRANSFERENCIA_PNEU,
        PTP.COD_UNIDADE_ORIGEM               AS COD_UNIDADE_ORIGEM,
        UO.NOME                              AS UNIDADE_ORIGEM,
        RO.REGIAO                            AS REGIONAL_ORIGEM,
        PTP.COD_UNIDADE_DESTINO              AS COD_UNIDADE_DESTINO,
        UD.NOME                              AS UNIDADE_DESTINO,
        RD.REGIAO                            AS REGIONAL_DESTINO,
        PTP.COD_UNIDADE_COLABORADOR          AS COD_UNIDADE_COLABORADOR,
        CO.NOME                              AS NOME_COLABORADOR,
        PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AS DATA_HORA_TRANSFERENCIA_PROCESSO,
        PTP.OBSERVACAO                       AS OBSERVACAO,
        PTP.TIPO_PROCESSO_TRANSFERENCIA      AS TIPO_PROCESSO_TRANSFERENCIA,
        VTI.COD_PROCESSO_TRANSFERENCIA       AS COD_PROCESSO_TRANSFERENCIA_VEICULO,
        V.PLACA                              AS PLACA_TRANSFERIDA
      FROM PNEU_TRANSFERENCIA_PROCESSO PTP
        JOIN COLABORADOR CO ON PTP.COD_COLABORADOR = CO.CODIGO
        JOIN UNIDADE UO ON UO.CODIGO = PTP.COD_UNIDADE_ORIGEM
        JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
        JOIN UNIDADE UD ON UD.CODIGO = PTP.COD_UNIDADE_DESTINO
        JOIN REGIONAL RD ON UD.COD_REGIONAL = RD.CODIGO
        LEFT JOIN VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVPP
          ON PTP.CODIGO = VTVPP.COD_PROCESSO_TRANSFERENCIA_PNEU
        LEFT JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI
          ON VTVPP.COD_VEICULO_TRANSFERENCIA_INFORMACOES = VTI.CODIGO
        LEFT JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
      WHERE PTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
  )

  SELECT
    TP.COD_PROCESSO_TRANSFERENCIA_PNEU                  AS COD_PROCESSO_TRANSFERENCIA_PNEU,
    TP.REGIONAL_ORIGEM :: TEXT                          AS REGIONAL_ORIGEM,
    TP.UNIDADE_ORIGEM :: TEXT                           AS UNIDADE_ORIGEM,
    TP.REGIONAL_DESTINO :: TEXT                         AS REGIONAL_DESTINO,
    TP.UNIDADE_DESTINO :: TEXT                          AS UNIDADE_DESTINO,
    TP.NOME_COLABORADOR :: TEXT                         AS NOME_COLABORADOR,
    TP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(TP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_TRANSFERENCIA_PROCESSO,
    TP.OBSERVACAO :: TEXT                               AS OBSERVACAO,
    TP.TIPO_PROCESSO_TRANSFERENCIA                      AS TIPO_PROCESSO_TRANSFERENCIA,
    TP.COD_PROCESSO_TRANSFERENCIA_VEICULO               AS COD_PROCESSO_TRANSFERENCIA_VEICULO,
    TP.PLACA_TRANSFERIDA :: TEXT                        AS PLACA_TRANSFERIDA
  FROM TRANSFERENCIA_PROCESSO TP
  WHERE TP.COD_PROCESSO_TRANSFERENCIA_PNEU = F_COD_PROCESSO_TRANSFERENCIA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- FUNTION PARA VERIFICAR SE AS PLACAS POSSUEM DIAGRAMA ASSOCIADO E QUAIS OS TIPOS.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_VEICULOS_DIAGRAMAS(
  F_COD_VEICULOS                 BIGINT [],
  F_FILTRO_VEICULO_POSSUI_DIAGRAMA BOOLEAN DEFAULT NULL)
  RETURNS TABLE(
    COD_VEICULO       BIGINT,
    PLACA_VEICULO     TEXT,
    COD_TIPO_VEICULO  BIGINT,
    NOME_TIPO_VEICULO TEXT,
    POSSUI_DIAGAMA    BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH DADOS_VEICULOS AS (
      SELECT
        V.CODIGO                                   AS COD_VEICULO,
        V.PLACA :: TEXT                            AS PLACA_VEICULO,
        VT.CODIGO                                  AS COD_TIPO_VEICULO,
        VT.NOME :: TEXT                            AS NOME_TIPO_VEICULO,
        F_IF(VT.COD_DIAGRAMA IS NULL, FALSE, TRUE) AS POSSUI_DIAGAMA
      FROM VEICULO V
        JOIN VEICULO_TIPO VT
          ON V.COD_TIPO = VT.CODIGO
      WHERE V.CODIGO = ANY (F_COD_VEICULOS)
  )

  SELECT
    DV.COD_VEICULO       AS COD_VEICULO,
    DV.PLACA_VEICULO     AS PLACA_VEICULO,
    DV.COD_TIPO_VEICULO  AS COD_TIPO_VEICULO,
    DV.NOME_TIPO_VEICULO AS NOME_TIPO_VEICULO,
    DV.POSSUI_DIAGAMA    AS POSSUI_DIAGRAMA
  FROM DADOS_VEICULOS DV
  WHERE F_IF(F_FILTRO_VEICULO_POSSUI_DIAGRAMA IS NULL, TRUE, F_FILTRO_VEICULO_POSSUI_DIAGRAMA = DV.POSSUI_DIAGAMA);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_LISTAGEM_PROCESSOS(
  F_COD_UNIDADES_ORIGEM  BIGINT [],
  F_COD_UNIDADES_DESTINO BIGINT [],
  F_DATA_INICIAL         DATE,
  F_DATA_FINAL           DATE)
  RETURNS TABLE(
    COD_PROCESSO_TRANFERENCIA BIGINT,
    NOME_COLABORADOR          TEXT,
    DATA_HORA_REALIZACAO      TIMESTAMP WITHOUT TIME ZONE,
    NOME_UNIDADE_ORIGEM       TEXT,
    NOME_REGIONAL_ORIGEM      TEXT,
    NOME_UNIDADE_DESTINO      TEXT,
    NOME_REGIONAL_DESTINO     TEXT,
    OBSERVACAO                TEXT,
    PLACA_TRANSFERIDA         TEXT,
    QTD_PLACAS_TRANSFERIDAS   BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    VTP.CODIGO                                           AS COD_PROCESSO_TRANFERENCIA,
    C.NOME :: TEXT                                       AS NOME_COLABORADOR,
    VTP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(VTP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_REALIZACAO,
    UO.NOME :: TEXT                                      AS NOME_UNIDADE_ORIGEM,
    RO.REGIAO :: TEXT                                    AS NOME_REGIONAL_ORIGEM,
    UD.NOME :: TEXT                                      AS NOME_UNIDADE_DESTINO,
    RD.REGIAO :: TEXT                                    AS NOME_REGIONAL_DESTINO,
    VTP.OBSERVACAO                                       AS OBSERVACAO,
    V.PLACA :: TEXT                                      AS PLACA_TRANSFERIDA,
    COUNT(VTI.COD_VEICULO)
    OVER (
      PARTITION BY VTP.CODIGO )                          AS QTD_PLACAS_TRANSFERIDAS
  FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
    JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI ON VTP.CODIGO = VTI.COD_PROCESSO_TRANSFERENCIA
    JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
    JOIN COLABORADOR C ON VTP.COD_COLABORADOR_REALIZACAO = C.CODIGO
    JOIN UNIDADE UO ON VTP.COD_UNIDADE_ORIGEM = UO.CODIGO
    JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
    JOIN UNIDADE UD ON VTP.COD_UNIDADE_DESTINO = UD.CODIGO
    JOIN REGIONAL RD ON UD.COD_REGIONAL = RD.CODIGO
  WHERE VTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES_ORIGEM)
        AND VTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES_DESTINO)
        AND (VTP.DATA_HORA_TRANSFERENCIA_PROCESSO
             AT TIME ZONE TZ_UNIDADE(VTP.COD_UNIDADE_COLABORADOR))::DATE >= F_DATA_INICIAL
        AND (VTP.DATA_HORA_TRANSFERENCIA_PROCESSO
             AT TIME ZONE TZ_UNIDADE(VTP.COD_UNIDADE_COLABORADOR))::DATE <= F_DATA_FINAL
  ORDER BY VTP.DATA_HORA_TRANSFERENCIA_PROCESSO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-1851
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_VISUALIZACAO_PROCESSO(F_COD_PROCESSO_TRANSFERENCIA BIGINT)
  RETURNS TABLE(
    COD_PROCESSO_TRANFERENCIA               BIGINT,
    NOME_COLABORADOR                        TEXT,
    DATA_HORA_REALIZACAO                    TIMESTAMP WITHOUT TIME ZONE,
    NOME_UNIDADE_ORIGEM                     TEXT,
    NOME_REGIONAL_ORIGEM                    TEXT,
    NOME_UNIDADE_DESTINO                    TEXT,
    NOME_REGIONAL_DESTINO                   TEXT,
    OBSERVACAO                              TEXT,
    COD_VEICULO_TRANSFERIDO                 BIGINT,
    PLACA_TRANSFERIDA                       TEXT,
    NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA TEXT,
    KM_VEICULO_MOMENTO_TRANSFERENCIA        BIGINT,
    COD_PNEU_TRANFERIDO                     BIGINT,
    COD_CLIENTE_PNEU_TRANSFERIDO            TEXT,
    QTD_PLACAS_TRANSFERIDAS                 BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH COUNT_PROCESSOS AS (
      SELECT
        VTI.COD_PROCESSO_TRANSFERENCIA AS COD_PROCESSO_TRANSFERENCIA,
        COUNT(VTI.COD_PROCESSO_TRANSFERENCIA) AS QTD_PLACAS_TRANSFERIDAS
      FROM VEICULO_TRANSFERENCIA_INFORMACOES VTI
        JOIN VEICULO_TRANSFERENCIA_PROCESSO VTP ON VTI.COD_PROCESSO_TRANSFERENCIA = VTP.CODIGO
      WHERE VTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
      GROUP BY VTI.COD_PROCESSO_TRANSFERENCIA
  )

  SELECT
    VTP.CODIGO                                           AS COD_PROCESSO_TRANFERENCIA,
    C.NOME :: TEXT                                       AS NOME_COLABORADOR,
    VTP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(VTP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_REALIZACAO,
    UO.NOME :: TEXT                                      AS NOME_UNIDADE_ORIGEM,
    RO.REGIAO :: TEXT                                    AS NOME_REGIONAL_ORIGEM,
    UD.NOME :: TEXT                                      AS NOME_UNIDADE_DESTINO,
    RD.REGIAO :: TEXT                                    AS NOME_REGIONAL_DESTINO,
    VTP.OBSERVACAO                                       AS OBSERVACAO,
    VTI.COD_VEICULO                                      AS COD_VEICULO_TRANSFERIDO,
    V.PLACA :: TEXT                                      AS PLACA_TRANSFERIDA,
    VT.NOME :: TEXT                                      AS NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA,
    VTI.KM_VEICULO_MOMENTO_TRANSFERENCIA                 AS KM_VEICULO_MOMENTO_TRANSFERENCIA,
    P.CODIGO                                             AS COD_PNEU_TRANFERIDO,
    P.CODIGO_CLIENTE :: TEXT                             AS COD_CLIENTE_PNEU_TRANSFERIDO,
    CP.QTD_PLACAS_TRANSFERIDAS                           AS QTD_PLACAS_TRANSFERIDAS
  FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
    JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI ON VTP.CODIGO = VTI.COD_PROCESSO_TRANSFERENCIA
    JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
    JOIN VEICULO_TIPO VT ON VTI.COD_TIPO_VEICULO = VT.CODIGO
    JOIN COLABORADOR C ON VTP.COD_COLABORADOR_REALIZACAO = C.CODIGO
    JOIN UNIDADE UO ON VTP.COD_UNIDADE_ORIGEM = UO.CODIGO
    JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
    JOIN UNIDADE UD ON VTP.COD_UNIDADE_DESTINO = UD.CODIGO
    JOIN REGIONAL RD ON UD.COD_REGIONAL = RD.CODIGO
    JOIN COUNT_PROCESSOS CP ON CP.COD_PROCESSO_TRANSFERENCIA = VTP.CODIGO
    LEFT JOIN VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVPP ON VTI.CODIGO = VTVPP.COD_VEICULO_TRANSFERENCIA_INFORMACOES
    LEFT JOIN PNEU_TRANSFERENCIA_PROCESSO PTP ON VTVPP.COD_PROCESSO_TRANSFERENCIA_PNEU = PTP.CODIGO
    LEFT JOIN PNEU_TRANSFERENCIA_INFORMACOES PTI ON PTP.CODIGO = PTI.COD_PROCESSO_TRANSFERENCIA
    LEFT JOIN PNEU P ON PTI.COD_PNEU = P.CODIGO
  WHERE VTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
  ORDER BY VTI.COD_VEICULO DESC, P.CODIGO ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-1851
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DETALHES_PLACA_TRANSFERIDA(
  F_COD_PROCESSO_TRANSFERENCIA BIGINT,
  F_COD_VEICULO                BIGINT)
  RETURNS TABLE(
    PLACA_VEICULO                           TEXT,
    COD_DIAGRAMA_VEICULO                    BIGINT,
    NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA TEXT,
    COD_PNEU                                BIGINT,
    CODIGO_CLIENTE                          TEXT,
    ALTURA_SULCO_EXTERNO                    REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO            REAL,
    ALTURA_SULCO_CENTRAL_INTERNO            REAL,
    ALTURA_SULCO_INTERNO                    REAL,
    PRESSAO_PNEU                            REAL,
    VIDA_MOMENTO_TRANSFERENCIA              INTEGER,
    POSICAO_PNEU_TRANSFERENCIA              INTEGER)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    V.PLACA::TEXT                    AS PLACA_VEICULO,
    VTI.COD_DIAGRAMA_VEICULO         AS COD_DIAGRAMA_VEICULO,
    VT.NOME::TEXT                    AS NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA,
    PTI.COD_PNEU                     AS COD_PNEU,
    P.CODIGO_CLIENTE::TEXT           AS CODIGO_CLIENTE,
    PTI.ALTURA_SULCO_EXTERNO         AS ALTURA_SULCO_EXTERNO,
    PTI.ALTURA_SULCO_CENTRAL_EXTERNO AS ALTURA_SULCO_CENTRAL_EXTERNO,
    PTI.ALTURA_SULCO_CENTRAL_INTERNO AS ALTURA_SULCO_CENTRAL_INTERNO,
    PTI.ALTURA_SULCO_INTERNO         AS ALTURA_SULCO_INTERNO,
    PTI.PSI                          AS PRESSAO_PNEU,
    PTI.VIDA_MOMENTO_TRANSFERENCIA   AS VIDA_MOMENTO_TRANSFERENCIA,
    PTI.POSICAO_PNEU_TRANSFERENCIA   AS POSICAO_PNEU_TRANSFERENCIA
  FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
    JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI ON VTP.CODIGO = VTI.COD_PROCESSO_TRANSFERENCIA
    JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
    JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
    LEFT JOIN VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVPP
      ON VTI.CODIGO = VTVPP.COD_VEICULO_TRANSFERENCIA_INFORMACOES
    LEFT JOIN PNEU_TRANSFERENCIA_PROCESSO PTP ON VTVPP.COD_PROCESSO_TRANSFERENCIA_PNEU = PTP.CODIGO
    LEFT JOIN PNEU_TRANSFERENCIA_INFORMACOES PTI ON PTP.CODIGO = PTI.COD_PROCESSO_TRANSFERENCIA
    LEFT JOIN PNEU P ON PTI.COD_PNEU = P.CODIGO
  WHERE VTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
        AND VTI.COD_VEICULO = F_COD_VEICULO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-1845
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_COD_PNEUS_APLICADOS(
  F_COD_VEICULO BIGINT)
  RETURNS TABLE(
    COD_PNEU BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT VP.COD_PNEU AS COD_PNEU
  FROM VEICULO_PNEU VP
  WHERE VP.PLACA = (SELECT V.PLACA
                    FROM VEICULO V
                    WHERE V.CODIGO = F_COD_VEICULO);
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2062 -- Tratar serviços em aberto ao transferir veículos e pneus.

ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA ADD COLUMN CODIGO_PROLOG BIGSERIAL NOT NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA ADD CONSTRAINT UNIQUE_CODIGO_DELETADO_OS_CHECK UNIQUE (CODIGO_PROLOG, DELETADO);
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA ADD CONSTRAINT UNIQUE_CODIGO_DELETADO_ITEM_OS_CHECK UNIQUE (CODIGO, DELETADO);

DROP VIEW CHECKLIST_ORDEM_SERVICO;

CREATE OR REPLACE VIEW CHECKLIST_ORDEM_SERVICO AS
  SELECT
    COS.CODIGO_PROLOG,
    COS.CODIGO,
    COS.COD_UNIDADE,
    COS.COD_CHECKLIST,
    COS.STATUS,
    COS.DATA_HORA_FECHAMENTO
  FROM CHECKLIST_ORDEM_SERVICO_DATA COS
  WHERE COS.DELETADO = FALSE;

CREATE TABLE CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA (
  COD_OS_PROLOG                         BIGINT  NOT NULL,
  OS_DELETADA                           BOOLEAN NOT NULL DEFAULT TRUE,
  COD_VEICULO_TRANSFERENCIA_INFORMACOES BIGINT  NOT NULL,
  CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA PRIMARY KEY (COD_OS_PROLOG),
  CONSTRAINT FK_OS_DELETADA_TRANSFERENCIA_CHECKLIST_ORDEM_SERVICO
  FOREIGN KEY (COD_OS_PROLOG, OS_DELETADA) REFERENCES CHECKLIST_ORDEM_SERVICO_DATA (CODIGO_PROLOG, DELETADO) DEFERRABLE INITIALLY IMMEDIATE,
  CONSTRAINT FK_OS_DELETADA_TRANSFERENCIA_INFORMACOES
  FOREIGN KEY (COD_VEICULO_TRANSFERENCIA_INFORMACOES) REFERENCES VEICULO_TRANSFERENCIA_INFORMACOES (CODIGO)
);
COMMENT ON TABLE CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA
IS 'Tabela utilizada para salvar as OSs do checklist que foram deletadas como consequência de uma transferência de veículos.';

CREATE TABLE CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA (
  COD_ITEM_OS_PROLOG BIGINT  NOT NULL,
  ITEM_OS_DELETADO   BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA PRIMARY KEY (COD_ITEM_OS_PROLOG),
  CONSTRAINT FK_ITEM_OS_DELETADO_TRANSFERENCIA_CHECKLIST_ORDEM_SERVICO_ITENS
  FOREIGN KEY (COD_ITEM_OS_PROLOG, ITEM_OS_DELETADO) REFERENCES CHECKLIST_ORDEM_SERVICO_ITENS_DATA (CODIGO, DELETADO) DEFERRABLE INITIALLY IMMEDIATE
);
COMMENT ON TABLE CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA
IS 'Tabela utilizada para salvar os itens de O.S. do checklist que foram deletados como consequência de uma transferência de veículos.';


CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(
  F_COD_VEICULO                           BIGINT,
  F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
  F_DATA_HORA_REALIZACAO_TRANSFERENCIA    TIMESTAMP WITH TIME ZONE)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_INSERTS            BIGINT;
  QTD_UPDATES            BIGINT;
  F_STATUS_OS_ABERTA     TEXT := 'A';
  F_STATUS_OS_FECHADA    TEXT := 'F';
  F_STATUS_ITEM_PENDENTE TEXT := 'P';
  F_PLACA_VEICULO        TEXT := (SELECT V.PLACA
                                  FROM VEICULO V
                                  WHERE V.CODIGO = F_COD_VEICULO);
  F_OS                   CHECKLIST_ORDEM_SERVICO%ROWTYPE;
BEGIN
  FOR F_OS IN
  SELECT
    COS.CODIGO_PROLOG,
    COS.CODIGO,
    COS.COD_UNIDADE,
    COS.COD_CHECKLIST
  -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar OSs deletadas e não resolvidos.
  FROM CHECKLIST_ORDEM_SERVICO COS
    JOIN CHECKLIST C ON C.CODIGO = COS.cod_checklist
  WHERE
    COS.STATUS = F_STATUS_OS_ABERTA
    AND C.PLACA_VEICULO = F_PLACA_VEICULO
  LOOP
    -- Copia os itens da OS.
    INSERT INTO CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA (
      COD_ITEM_OS_PROLOG)
      SELECT
        COSI.CODIGO
      -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar itens já deletados e não resolvidos.
      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
      WHERE
        COSI.COD_OS = F_OS.CODIGO
        AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
        AND COSI.STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE;

    GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

    -- Deleta os itens da OS.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET
      DELETADO           = TRUE,
      DATA_HORA_DELETADO = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
    WHERE
      COD_OS = F_OS.CODIGO
      AND COD_UNIDADE = F_OS.COD_UNIDADE
      AND STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

    IF QTD_INSERTS <> QTD_UPDATES
    THEN
      RAISE EXCEPTION
      'Erro ao deletar os itens de O.S. de checklist na transferência de veículos. Rollback necessário! __INSERTS: % UPDATES: %__',
      QTD_INSERTS,
      QTD_UPDATES;
    END IF;

    IF ((SELECT COUNT(COSI.CODIGO)
         FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
         WHERE COSI.COD_OS = F_OS.CODIGO
               AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
               AND COSI.DELETADO = FALSE) > 0)
    THEN
      -- Se entrou aqui siginifca que a OS não tem mais itens em aberto, ela possuia alguns fechados e outros em aberto
      -- mas nós acabamos de deletar os que estavam em aberto.
      -- Por isso, precisamos fechar essa OS.
      UPDATE
        CHECKLIST_ORDEM_SERVICO_DATA
      SET
        STATUS               = F_STATUS_OS_FECHADA,
        DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
                                FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                                WHERE COSI.COD_OS = F_OS.CODIGO
                                      AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
                                      AND COSI.DELETADO = FALSE)
      WHERE
        CODIGO_PROLOG = F_OS.CODIGO_PROLOG
        AND CODIGO = F_OS.CODIGO
        AND COD_UNIDADE = F_OS.COD_UNIDADE
        AND COD_CHECKLIST = F_OS.COD_CHECKLIST
        AND DELETADO = FALSE;
    ELSE
      -- Se entrou aqui siginifica que nós deletamos todos os itens da OS.
      -- Por isso, precisamos copiar a OS para a tabela de vínculo como deletada por transferência e depois deletá-la.

      -- Copia a OS.
      INSERT INTO CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA (
        COD_OS_PROLOG,
        COD_VEICULO_TRANSFERENCIA_INFORMACOES)
        SELECT
          F_OS.CODIGO_PROLOG,
          F_COD_TRANSFERENCIA_VEICULO_INFORMACOES;

      GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

      -- Deleta a OS copiada.
      UPDATE CHECKLIST_ORDEM_SERVICO_DATA
      SET
        DELETADO           = TRUE,
        DATA_HORA_DELETADO = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
      WHERE
        CODIGO_PROLOG = F_OS.CODIGO_PROLOG
        AND CODIGO = F_OS.CODIGO
        AND COD_UNIDADE = F_OS.COD_UNIDADE
        AND COD_CHECKLIST = F_OS.COD_CHECKLIST
        AND DELETADO = FALSE;

      GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

      IF QTD_INSERTS <> QTD_UPDATES
      THEN
        RAISE EXCEPTION
        'Erro ao deletar as OSs de checklist na transferência de veículos. Rollback necessário! __INSERTS: % UPDATES: %__',
        QTD_INSERTS,
        QTD_UPDATES;
      END IF;

    END IF;
  END LOOP;
END;
$$;


-- Aferição.
ALTER TABLE AFERICAO_MANUTENCAO_DATA ADD CONSTRAINT UNIQUE_CODIGO_DELETADO_AFERICAO_MANUTENCAO UNIQUE (CODIGO, DELETADO);

CREATE TABLE AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA (
  COD_SERVICO                           BIGINT  NOT NULL,
  SERVICO_DELETADO                      BOOLEAN NOT NULL DEFAULT TRUE,
  COD_VEICULO_TRANSFERENCIA_INFORMACOES BIGINT  NOT NULL,
  CONSTRAINT PK_AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA PRIMARY KEY (COD_SERVICO),
  CONSTRAINT FK_SERVICO_DELETADO_TRANSFERENCIA_AFERICAO_MANUTENCAO
  FOREIGN KEY (COD_SERVICO, SERVICO_DELETADO) REFERENCES AFERICAO_MANUTENCAO_DATA (CODIGO, DELETADO) DEFERRABLE INITIALLY IMMEDIATE,
  CONSTRAINT FK_SERVICO_DELETADO_TRANSFERENCIA_INFORMACOES
  FOREIGN KEY (COD_VEICULO_TRANSFERENCIA_INFORMACOES) REFERENCES VEICULO_TRANSFERENCIA_INFORMACOES (CODIGO)
);
COMMENT ON TABLE AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA
IS 'Tabela utilizada para salvar os serviços de pneus que foram deletados como consequência de uma transferência de veículos.';


CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU(
  F_COD_VEICULO                           BIGINT,
  F_COD_PNEU                              BIGINT,
  F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
  F_DATA_HORA_REALIZACAO_TRANSFERENCIA    TIMESTAMP WITH TIME ZONE)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_INSERTS BIGINT;
  QTD_UPDATES BIGINT;
  F_PLACA_VEICULO TEXT := (SELECT V.PLACA FROM VEICULO V WHERE V.CODIGO = F_COD_VEICULO);
BEGIN
  INSERT INTO AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA (
    COD_SERVICO,
    COD_VEICULO_TRANSFERENCIA_INFORMACOES)
    SELECT
      AM.CODIGO,
      F_COD_TRANSFERENCIA_VEICULO_INFORMACOES
    -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    FROM AFERICAO_MANUTENCAO AM
      JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
    WHERE
      A.PLACA_VEICULO = F_PLACA_VEICULO
      AND AM.COD_PNEU = F_COD_PNEU
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

  GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

  UPDATE AFERICAO_MANUTENCAO_DATA
  SET
    DELETADO           = TRUE,
    DATA_HORA_DELETADO = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
  WHERE
    COD_PNEU = F_COD_PNEU
    AND DELETADO = FALSE
    AND DATA_HORA_RESOLUCAO IS NULL
    AND (FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

  GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

  -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
  -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
  -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo A
  -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
  -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
  -- Porém, podemos ter uma inconsistência no BD.
  -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
  -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
  IF QTD_INSERTS <> QTD_UPDATES
  THEN
    RAISE EXCEPTION 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
  END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2067

DROP FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE(F_COD_UNIDADE BIGINT);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO BOOLEAN,
    VERSAO_DADOS_CHECKLIST_UNIDADE BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_EMPRESA BIGINT := (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  VERSAO_DADOS_BD BIGINT;
BEGIN
  IF (SELECT EXISTS(SELECT COEB.COD_EMPRESA
                    FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA COEB
                    WHERE COEB.COD_EMPRESA = F_COD_EMPRESA))
  THEN
    RETURN QUERY
    SELECT
      TRUE           AS EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO,
      NULL :: BIGINT AS VERSAO_DADOS_CHECKLIST_UNIDADE;
  ELSE
    IF (SELECT EXISTS(SELECT COD_UNIDADE FROM CHECKLIST_OFFLINE_DADOS_UNIDADE WHERE COD_UNIDADE = F_COD_UNIDADE))
    THEN
      UPDATE CHECKLIST_OFFLINE_DADOS_UNIDADE SET VERSAO_DADOS = VERSAO_DADOS + 1
      WHERE COD_UNIDADE = F_COD_UNIDADE RETURNING VERSAO_DADOS INTO VERSAO_DADOS_BD;
    ELSE
      INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE(COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
      VALUES (F_COD_UNIDADE, F_RANDOM_STRING(64)) RETURNING VERSAO_DADOS INTO VERSAO_DADOS_BD;
    END IF;

    RETURN QUERY
    SELECT
        FALSE AS EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO,
        VERSAO_DADOS_BD AS VERSAO_DADOS_CHECKLIST_UNIDADE;
  END IF;
END;
$$;


DROP FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_COLABORADOR(F_COD_COLABORADOR BIGINT );
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_COLABORADOR(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO BOOLEAN,
    VERSAO_DADOS_CHECKLIST_UNIDADE      BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT *
  FROM FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT C.COD_UNIDADE
                                                           FROM COLABORADOR C
                                                           WHERE C.CODIGO = F_COD_COLABORADOR));
END;
$$;


DROP FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_MODELO_CHECK(
F_COD_MODELO_CHECKLIST BIGINT );
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_MODELO_CHECK(
  F_COD_MODELO_CHECKLIST BIGINT)
  RETURNS TABLE(
    EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO BOOLEAN,
    VERSAO_DADOS_CHECKLIST_UNIDADE      BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT *
  FROM FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT CM.COD_UNIDADE
                                                           FROM CHECKLIST_MODELO CM
                                                           WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST));
END;
$$;


DROP FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_VEICULO(F_COD_VEICULO BIGINT );
CREATE FUNCTION FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_VEICULO(F_COD_VEICULO BIGINT)
  RETURNS TABLE(
    EMPRESA_CHECKLIST_OFFLINE_BLOQUEADO BOOLEAN,
    VERSAO_DADOS_CHECKLIST_UNIDADE      BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT *
  FROM FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE((SELECT V.COD_UNIDADE
                                                           FROM VEICULO V
                                                           WHERE V.CODIGO = F_COD_VEICULO));
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2068

-- Se o código da OS for null, nós geramos um código baseado no maior código já existente para a unidade + 1.
-- Utilizamos a tabela CHECKLIST_ORDEM_SERVICO_DATA para isso e não a view CHECKLIST_ORDEM_SERVICO para considerar
-- OSs deletadas na busca do maior código.
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_OS_GERA_CODIGO_OS()
  RETURNS TRIGGER AS $$
BEGIN
  IF NEW.CODIGO IS NULL
  THEN
    NEW.CODIGO := (SELECT MAX(COS.CODIGO) + 1
                   FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                   WHERE COS.COD_UNIDADE = NEW.COD_UNIDADE);
  END IF;
  RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

-- CRIA A TRIGGER.
CREATE TRIGGER TG_GERA_CODIGO_OS
  BEFORE INSERT
  ON CHECKLIST_ORDEM_SERVICO_DATA
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_CHECKLIST_OS_GERA_CODIGO_OS();


-- Para garantir que a trigger vá funcionar, precisamos alterar a coluna código de BIGSERIAL para BIGINT. Assim não
-- corremos o risco dela assumir algum valor default se não setarmos no java. Para fazer isso, basta droppar a sequence
-- que gera os códigos automaticamente.
DROP SEQUENCE CHECKLIST_ORDEM_SERVICO_CODIGO_SEQ CASCADE;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;