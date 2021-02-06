BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--################################# RELATÓRIO QUE CALCULA A VALIDADE DOS PNEUS - DOTS ##################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(
  F_COD_UNIDADES BIGINT [],
  F_DATA_ATUAL   TIMESTAMP)
  RETURNS TABLE(
    "UNIDADE"         TEXT,
    "COD PNEU"        TEXT,
    "DOT CADASTRADO"  TEXT,
    "DOT FORMATADO"   TEXT,
    "DOT VÁLIDO"      TEXT,
    "TEMPO DE USO"    TEXT,
    "TEMPO RESTANTE"  TEXT,
    "DATA VENCIMENTO" TEXT,
    "VENCIDO"         BOOLEAN,
    "DATA_GERAÇÃO"    TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT        TEXT := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
  DIA_MES_ANO_FORMAT TEXT := 'DD/MM/YYYY';
  DATA_HORA_FORMAT   TEXT := 'DD/MM/YYYY HH24:MI';
  DATE_CONVERTER     TEXT := 'YYYYWW';
  PREFIXO_ANO        TEXT := SUBSTRING(F_DATA_ATUAL::TEXT, 1, 2);
BEGIN
  RETURN QUERY

  WITH INFORMACOES_PNEU AS (
      SELECT
        P.CODIGO_CLIENTE                               AS COD_PNEU,
        P.DOT                                          AS DOT_CADASTRADO,
        -- Remove letras, characteres especiais e espaços do dot.
        -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
        TRIM(REGEXP_REPLACE(P.DOT, '[^0-9]', '', 'g')) AS DOT_LIMPO,
        P.COD_UNIDADE                                  AS COD_UNIDADE,
        U.NOME                                         AS UNIDADE
      FROM PNEU P
        JOIN UNIDADE U ON P.COD_UNIDADE = U.codigo
      WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
  ),

      DATA_DOT AS (
        SELECT
          IP.COD_PNEU,
          -- Transforma o DOT_FORMATADO em data
          CASE WHEN (char_length(IP.DOT_LIMPO) = 4)
            THEN
              to_date(concat(PREFIXO_ANO, (substring(IP.DOT_LIMPO, 3, 4)), (substring(IP.DOT_LIMPO, 1, 2))),
                      DATE_CONVERTER)
          ELSE NULL END AS DOT_EM_DATA
        FROM INFORMACOES_PNEU IP
    ),

      VENCIMENTO_DOT AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
          -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
          -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
          -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
          CASE WHEN DD.DOT_EM_DATA <= (F_DATA_ATUAL::DATE)
            THEN DD.DOT_EM_DATA + INTERVAL '5 DAYS 5 YEARS' ELSE NULL END AS DATA_VENCIMENTO
        FROM DATA_DOT DD
    ),

      CALCULOS AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se o dot é válido
          -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
          -- podemos considerar inválidos os que possuem vencimento = null.
          CASE WHEN VD.DATA_VENCIMENTO IS NULL THEN 'INVÁLIDO' ELSE 'VÁLIDO' END     AS DOT_VALIDO,
          -- Cálculo tempo de uso
          CASE WHEN VD.DATA_VENCIMENTO IS NULL
            THEN NULL
          ELSE
            TO_CHAR(AGE((F_DATA_ATUAL :: DATE), DD.DOT_EM_DATA), DATE_FORMAT) END    AS TEMPO_DE_USO,
          -- Cálculo dias restantes
          TO_CHAR(AGE(VD.DATA_VENCIMENTO, F_DATA_ATUAL), DATE_FORMAT)                AS TEMPO_RESTANTE,
          -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
          F_IF(((VD.DATA_VENCIMENTO::DATE) - (F_DATA_ATUAL::DATE)) < 0, TRUE, FALSE) AS VENCIDO
        FROM DATA_DOT DD
          JOIN VENCIMENTO_DOT VD ON DD.COD_PNEU = VD.COD_PNEU
    )
  SELECT
    IP.UNIDADE::TEXT,
    IP.COD_PNEU::TEXT,
    COALESCE(IP.DOT_CADASTRADO::TEXT, '-'),
    COALESCE(IP.DOT_LIMPO::TEXT, '-'),
    CA.DOT_VALIDO,
    COALESCE(CA.TEMPO_DE_USO, '-'),
    COALESCE(CA.TEMPO_RESTANTE, '-'),
    COALESCE(TO_CHAR(VD.DATA_VENCIMENTO, DIA_MES_ANO_FORMAT)::TEXT, '-'),
    CA.VENCIDO,
    TO_CHAR(F_DATA_ATUAL, DATA_HORA_FORMAT)::TEXT
  FROM
    INFORMACOES_PNEU IP
    JOIN VENCIMENTO_DOT VD ON IP.COD_PNEU = VD.COD_PNEU
    JOIN CALCULOS CA ON CA.COD_PNEU = VD.COD_PNEU AND CA.COD_PNEU = IP.COD_PNEU
  ORDER BY VD.DATA_VENCIMENTO ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- Corrige problema de contagem de marcações dentro de jornada ao considerar que status seria sempre diferente de NULL.
--######################################################################################################################
--######################################################################################################################
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
        C.NOME                                 AS NOME_COLABORADOR,
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
--######################################################################################################################
--######################################################################################################################


-- Cria function da busca dos modelos de quizzes para exibição na listagem do site.
--######################################################################################################################
--######################################################################################################################
-- Aproveitamos e criamos FK na tabela QUIZ_MODELO_FUNCAO que não existia.
ALTER TABLE QUIZ_MODELO_FUNCAO
  ADD CONSTRAINT FK_QUIZ_MODELO_FUNCAO_FUNCAO FOREIGN KEY (COD_FUNCAO_COLABORADOR) REFERENCES FUNCAO (CODIGO);

CREATE OR REPLACE FUNCTION FUNC_QUIZ_GET_LISTAGEM_MODELOS(
  F_COD_UNIDADE         BIGINT,
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    COD_MODELO_QUIZ             BIGINT,
    NOME_MODELO_QUIZ            TEXT,
    COD_UNIDADE_MODELO_QUIZ     BIGINT,
    NOME_CARGO_LIBERADO         TEXT,
    PORCENTAGEM_APROVACAO       REAL,
    QTD_PERGUNTAS               BIGINT,
    TEM_MATERIAL_APOIO          BOOLEAN,
    ESTA_ABERTO_PARA_REALIZACAO BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  QM.CODIGO                                       AS COD_MODELO_QUIZ,
  QM.NOME :: TEXT                                 AS NOME_MODELO_QUIZ,
  QM.COD_UNIDADE                                  AS COD_UNIDADE_MODELO_QUIZ,
  F.NOME :: TEXT                                  AS NOME_CARGO_LIBERADO,
  QM.PORCENTAGEM_APROVACAO                        AS PORCENTAGEM_APROVACAO,
  (SELECT COUNT(*)
   FROM QUIZ_PERGUNTAS QP
   WHERE QP.COD_MODELO = QM.CODIGO)               AS QTD_PERGUNTAS,
  QMT.COD_TREINAMENTO IS NOT NULL                 AS TEM_MATERIAL_APOIO,
  QM.DATA_HORA_FECHAMENTO > F_DATA_HORA_ATUAL_UTC AS ESTA_ABERTO_PARA_REALIZACAO
FROM QUIZ_MODELO QM
  LEFT JOIN QUIZ_MODELO_TREINAMENTO QMT
    ON QM.CODIGO = QMT.COD_MODELO_QUIZ
  LEFT JOIN QUIZ_MODELO_FUNCAO QMF
    ON QM.CODIGO = QMF.COD_MODELO
  LEFT JOIN FUNCAO F
    ON F.CODIGO = QMF.COD_FUNCAO_COLABORADOR
WHERE QM.COD_UNIDADE = F_COD_UNIDADE
ORDER BY COD_MODELO_QUIZ ASC, NOME_CARGO_LIBERADO ASC
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Corrige function de busca do farol (PL-1772)
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_FAROL_CHECKLIST(
  F_COD_UNIDADE                BIGINT,
  F_DATA_INICIAL               DATE,
  F_DATA_FINAL                 DATE,
  F_ITENS_CRITICOS_RETROATIVOS BOOLEAN,
  F_TZ_UNIDADE                 TEXT)
  RETURNS TABLE(
    DATA                               DATE,
    PLACA                              CHARACTER VARYING,
    COD_CHECKLIST_SAIDA                BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_SAIDA   TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_SAIDA         BIGINT,
    NOME_COLABORADOR_CHECKLIST_SAIDA   CHARACTER VARYING,
    COD_CHECKLIST_RETORNO              BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_RETORNO TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_RETORNO       BIGINT,
    NOME_COLABORADOR_CHECKLIST_RETORNO CHARACTER VARYING,
    CODIGO_PERGUNTA                    BIGINT,
    DESCRICAO_PERGUNTA                 TEXT,
    DESCRICAO_ALTERNATIVA              TEXT,
    ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
    DESCRICAO_ALTERNATIVA_TIPO_OUTROS  TEXT,
    CODIGO_ITEM_CRITICO                BIGINT,
    DATA_HORA_APONTAMENTO_ITEM_CRITICO TIMESTAMP WITHOUT TIME ZONE)
LANGUAGE PLPGSQL
AS $$
DECLARE
  CHECKLIST_TIPO_SAIDA         CHAR := 'S';
  CHECKLIST_TIPO_RETORNO       CHAR := 'R';
  CHECKLIST_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  ORDEM_SERVICO_ABERTA         CHAR := 'A';
  ORDEM_SERVICO_ITEM_PENDENDTE CHAR := 'P';
BEGIN
  RETURN QUERY
  WITH ULTIMOS_CHECKLISTS_VEICULOS AS (
      SELECT
        INNERTABLE.DATA,
        INNERTABLE.PLACA,
        INNERTABLE.COD_CHECKLIST_SAIDA,
        INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
        CS.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_SAIDA,
        COS.NOME                AS NOME_COLABORADOR_CHECKLIST_SAIDA,
        INNERTABLE.COD_CHECKLIST_RETORNO,
        INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
        CR.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_RETORNO,
        COR.NOME                AS NOME_COLABORADOR_CHECKLIST_RETORNO
      FROM
        (SELECT
           G.DAY :: DATE                                     AS DATA,
           V.PLACA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.CODIGO END)                              AS COD_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.CODIGO END)                              AS COD_CHECKLIST_RETORNO,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_RETORNO
         FROM VEICULO V
           CROSS JOIN GENERATE_SERIES(F_DATA_INICIAL, F_DATA_FINAL, '1 DAY') G(DAY)
           LEFT JOIN CHECKLIST C
             ON C.PLACA_VEICULO = V.PLACA AND G.DAY :: DATE = (C.DATA_HORA AT TIME ZONE F_TZ_UNIDADE) :: DATE
         WHERE V.COD_UNIDADE = F_COD_UNIDADE AND V.STATUS_ATIVO = TRUE
         GROUP BY 1, 2
         ORDER BY 1, 2) AS INNERTABLE
        LEFT JOIN CHECKLIST CS ON CS.CODIGO = INNERTABLE.COD_CHECKLIST_SAIDA
        LEFT JOIN CHECKLIST CR ON CR.CODIGO = INNERTABLE.COD_CHECKLIST_RETORNO
        LEFT JOIN COLABORADOR COS ON COS.CPF = CS.CPF_COLABORADOR
        LEFT JOIN COLABORADOR COR ON COR.CPF = CR.CPF_COLABORADOR
      ORDER BY INNERTABLE.DATA, INNERTABLE.PLACA
  ),

      ITENS_PRIORIDADE AS (
        SELECT
          COSI.*,
          CAP.PRIORIDADE
        FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
          JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
            ON CAP.CODIGO = COSI.COD_ALTERNATIVA
          JOIN CHECKLIST_PERGUNTAS CP
            ON CP.CODIGO = CAP.COD_PERGUNTA
        WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
              AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
              AND COSI.STATUS_RESOLUCAO = ORDEM_SERVICO_ITEM_PENDENDTE
    )

  SELECT
    Q.DATA,
    Q.PLACA,
    Q.COD_CHECKLIST_SAIDA,
    Q.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
    Q.COD_CHECKLIST_MODELO_SAIDA,
    Q.NOME_COLABORADOR_CHECKLIST_SAIDA,
    Q.COD_CHECKLIST_RETORNO,
    Q.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
    Q.COD_CHECKLIST_MODELO_RETORNO,
    Q.NOME_COLABORADOR_CHECKLIST_RETORNO,
    Q.CODIGO_PERGUNTA,
    Q.DESCRICAO_PERGUNTA,
    Q.DESCRICAO_ALTERNATIVA,
    CASE
    WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA
         OR Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
      THEN TRUE
    ELSE FALSE
    END AS ALTERNATIVA_TIPO_OUTROS,
    CASE
    WHEN Q.ITEM_CRITICO_DE_SAIDA_TIPO_OUTROS
      THEN (SELECT CR.RESPOSTA
            FROM CHECKLIST_RESPOSTAS CR
            WHERE CR.COD_CHECKLIST = Q.COD_CHECKLIST_SAIDA AND CR.COD_ALTERNATIVA = Q.CODIGO_ALTERNATIVA)
    WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
      THEN (SELECT CR.RESPOSTA
            FROM CHECKLIST_RESPOSTAS CR
            WHERE CR.COD_CHECKLIST = Q.COD_CHECKLIST_RETORNO AND CR.COD_ALTERNATIVA = Q.CODIGO_ALTERNATIVA)
    ELSE NULL
    END AS DESCRICAO_ALTERNATIVA_TIPO_OUTROS,
    Q.CODIGO_ITEM_CRITICO,
    Q.DATA_HORA_APONTAMENTO_ITEM_CRITICO
  FROM
    (SELECT
       DISTINCT ON (UCV.PLACA)
       UCV.DATA,
       UCV.PLACA,
       UCV.COD_CHECKLIST_SAIDA,
       UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
       UCV.COD_CHECKLIST_MODELO_SAIDA,
       UCV.NOME_COLABORADOR_CHECKLIST_SAIDA,
       UCV.COD_CHECKLIST_RETORNO,
       UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
       UCV.COD_CHECKLIST_MODELO_RETORNO,
       UCV.NOME_COLABORADOR_CHECKLIST_RETORNO,
       CP.CODIGO       AS CODIGO_PERGUNTA,
       CP.PERGUNTA     AS DESCRICAO_PERGUNTA,
       CAP.ALTERNATIVA AS DESCRICAO_ALTERNATIVA,
       CAP.CODIGO      AS CODIGO_ALTERNATIVA,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                  AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS
                  AND COSI.COD_ALTERNATIVA = CAP.CODIGO)
         THEN TRUE
       ELSE FALSE
       END             AS ITEM_CRITICO_DE_SAIDA_TIPO_OUTROS,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                  AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
                  AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO,
       CASE WHEN CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN COSI.CODIGO
       ELSE NULL
       END             AS CODIGO_ITEM_CRITICO,
       CASE
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
            AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
            AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO
       ELSE NULL
       END             AS DATA_HORA_APONTAMENTO_ITEM_CRITICO
     FROM ULTIMOS_CHECKLISTS_VEICULOS UCV
       LEFT JOIN CHECKLIST_ORDEM_SERVICO COS
         ON COS.COD_CHECKLIST IN (UCV.COD_CHECKLIST_SAIDA, UCV.COD_CHECKLIST_RETORNO)
            AND COS.STATUS = ORDEM_SERVICO_ABERTA
            AND COS.CODIGO IS NOT NULL
       -- UTILIZAMOS O JOIN COM ITENS_PRIORIDADE AO INVÉS DE CHECKLIST_ORDEM_SERVICO_ITENS POIS DESSE MODO CONSIDERAMOS
       -- APENAS OS ITENS ABERTOS CRÍTICOS, QUE É O QUE IMPORTA PARA O FAROL.
       LEFT JOIN ITENS_PRIORIDADE COSI
         ON COS.CODIGO = COSI.COD_OS
            AND COS.COD_UNIDADE = COSI.COD_UNIDADE
            AND COSI.STATUS_RESOLUCAO = ORDEM_SERVICO_ITEM_PENDENDTE
            AND COSI.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
       LEFT JOIN CHECKLIST_PERGUNTAS CP
         ON CP.CODIGO = COSI.COD_PERGUNTA
       LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
         ON CAP.COD_PERGUNTA = CP.CODIGO
            AND CAP.CODIGO = COSI.COD_ALTERNATIVA
            AND CAP.PRIORIDADE IS NOT NULL
            AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA) AS Q
  ORDER BY Q.DATA, Q.PLACA, Q.CODIGO_PERGUNTA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- Adiciona colunas para salvar início e fim da resolução de um item de O.S.
--######################################################################################################################
--######################################################################################################################
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS
  ADD CONSTRAINT CHECK_DATA_HORA_INICIO_RESOLUCAO_NOT_NULL CHECK (
  (DATA_HORA_CONSERTO IS NOT NULL AND DATA_HORA_INICIO_RESOLUCAO IS NOT NULL)
  OR (DATA_HORA_CONSERTO IS NULL AND DATA_HORA_INICIO_RESOLUCAO IS NULL)) NOT VALID;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS
  ADD CONSTRAINT CHECK_DATA_HORA_FIM_RESOLUCAO_NOT_NULL CHECK (
  (DATA_HORA_CONSERTO IS NOT NULL AND DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
  OR (DATA_HORA_CONSERTO IS NULL AND DATA_HORA_FIM_RESOLUCAO IS NULL)) NOT VALID;

COMMENT ON CONSTRAINT CHECK_DATA_HORA_INICIO_RESOLUCAO_NOT_NULL
ON CHECKLIST_ORDEM_SERVICO_ITENS
IS 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_INICIO_RESOLUCAO nula. Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.';

COMMENT ON CONSTRAINT CHECK_DATA_HORA_FIM_RESOLUCAO_NOT_NULL
ON CHECKLIST_ORDEM_SERVICO_ITENS
IS 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_FIM_RESOLUCAO nula. Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.';

-- Refatora para retornar novas informações nas functions de busca de itens de O.S..
DROP FUNCTION FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(F_COD_UNIDADE BIGINT,
F_COD_OS BIGINT,
F_PLACA_VEICULO TEXT,
F_PRIORIDADE_ALTERNATIVA TEXT,
F_STATUS_ITENS TEXT,
F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
F_LIMIT INTEGER,
F_OFFSET INTEGER );

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(
  F_COD_UNIDADE            BIGINT,
  F_COD_OS                 BIGINT,
  F_PLACA_VEICULO          TEXT,
  F_PRIORIDADE_ALTERNATIVA TEXT,
  F_STATUS_ITENS           TEXT,
  F_DATA_HORA_ATUAL_UTC    TIMESTAMP WITH TIME ZONE,
  F_LIMIT                  INTEGER,
  F_OFFSET                 INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                         TEXT,
    KM_ATUAL_VEICULO                      BIGINT,
    COD_OS                                BIGINT,
    COD_UNIDADE_ITEM_OS                   BIGINT,
    COD_ITEM_OS                           BIGINT,
    DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ITEM_OS                        TEXT,
    PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
    PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
    QTD_APONTAMENTOS                      INTEGER,
    COD_COLABORADOR_RESOLUCAO             BIGINT,
    NOME_COLABORADOR_RESOLUCAO            TEXT,
    DATA_HORA_RESOLUCAO                   TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_INICIO_RESOLUCAO            TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_RESOLUCAO               TIMESTAMP WITHOUT TIME ZONE,
    FEEDBACK_RESOLUCAO                    TEXT,
    DURACAO_RESOLUCAO_MINUTOS             BIGINT,
    KM_VEICULO_COLETADO_RESOLUCAO         BIGINT,
    COD_PERGUNTA                          BIGINT,
    DESCRICAO_PERGUNTA                    TEXT,
    COD_ALTERNATIVA                       BIGINT,
    DESCRICAO_ALTERNATIVA                 TEXT,
    ALTERNATIVA_TIPO_OUTROS               BOOLEAN,
    DESCRICAO_TIPO_OUTROS                 TEXT,
    PRIORIDADE_ALTERNATIVA                TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH DADOS AS (
      SELECT
        C.PLACA_VEICULO :: TEXT                                                AS PLACA_VEICULO,
        V.KM                                                                   AS KM_ATUAL_VEICULO,
        COS.CODIGO                                                             AS COD_OS,
        COS.COD_UNIDADE                                                        AS COD_UNIDADE_ITEM_OS,
        COSI.CODIGO                                                            AS COD_ITEM_OS,
        C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
        COSI.STATUS_RESOLUCAO                                                  AS STATUS_ITEM_OS,
        PRIO.PRAZO                                                             AS PRAZO_RESOLUCAO_ITEM_HORAS,
        TO_MINUTES_TRUNC((C.DATA_HORA
                          + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                         - F_DATA_HORA_ATUAL_UTC)                              AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
        COSI.QT_APONTAMENTOS                                                   AS QTD_APONTAMENTOS,
        CO.CODIGO                                                              AS COD_COLABORADOR_RESOLUCAO,
        CO.NOME :: TEXT                                                        AS NOME_COLABORADOR_RESOLUCAO,
        COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)         AS DATA_HORA_RESOLUCAO,
        COSI.DATA_HORA_INICIO_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_INICIO_RESOLUCAO,
        COSI.DATA_HORA_FIM_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)    AS DATA_HORA_FIM_RESOLUCAO,
        COSI.FEEDBACK_CONSERTO                                                 AS FEEDBACK_RESOLUCAO,
        MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                               AS DURACAO_RESOLUCAO_MINUTOS,
        COSI.KM                                                                AS KM_VEICULO_COLETADO_RESOLUCAO,
        CP.CODIGO                                                              AS COD_PERGUNTA,
        CP.PERGUNTA                                                            AS DESCRICAO_PERGUNTA,
        CAP.CODIGO                                                             AS COD_ALTERNATIVA,
        CAP.ALTERNATIVA                                                        AS DESCRICAO_ALTERNATIVA,
        CAP.ALTERNATIVA_TIPO_OUTROS                                            AS ALTERNATIVA_TIPO_OUTROS,
        F_IF(CAP.ALTERNATIVA_TIPO_OUTROS,
             (SELECT CR.RESPOSTA
              FROM CHECKLIST_RESPOSTAS CR
              WHERE CR.COD_CHECKLIST = C.CODIGO
                    AND CR.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT,
             NULL)                                                             AS DESCRICAO_TIPO_OUTROS,
        CAP.PRIORIDADE :: TEXT                                                 AS PRIORIDADE_ALTERNATIVA
      FROM CHECKLIST C
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON C.CODIGO = COS.COD_CHECKLIST
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
             AND COS.COD_UNIDADE = COSI.COD_UNIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA = CP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON COSI.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
          ON CAP.PRIORIDADE = PRIO.PRIORIDADE
        JOIN VEICULO V
          ON C.PLACA_VEICULO = V.PLACA
        LEFT JOIN COLABORADOR CO
          ON CO.CPF = COSI.CPF_MECANICO
      WHERE F_IF(F_COD_UNIDADE IS NULL, TRUE, COS.COD_UNIDADE = F_COD_UNIDADE)
            AND F_IF(F_COD_OS IS NULL, TRUE, COS.CODIGO = F_COD_OS)
            AND F_IF(F_PLACA_VEICULO IS NULL, TRUE, C.PLACA_VEICULO = F_PLACA_VEICULO)
            AND F_IF(F_PRIORIDADE_ALTERNATIVA IS NULL, TRUE, CAP.PRIORIDADE = F_PRIORIDADE_ALTERNATIVA)
            AND F_IF(F_STATUS_ITENS IS NULL, TRUE, COSI.STATUS_RESOLUCAO = F_STATUS_ITENS)
      LIMIT F_LIMIT
      OFFSET F_OFFSET
  ),
      DADOS_VEICULO AS (
        SELECT
          V.PLACA :: TEXT AS PLACA_VEICULO,
          V.KM            AS KM_ATUAL_VEICULO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO
    )

  -- NÓS USAMOS ESSE DADOS_VEICULO COM F_IF POIS PODE ACONTECER DE NÃO EXISTIR DADOS PARA OS FILTROS APLICADOS E
  -- DESSE MODO ACABARÍAMOS NÃO RETORNANDO PLACA E KM TAMBÉM, MAS ESSAS SÃO INFORMAÇÕES NECESSÁRIAS POIS O OBJETO
  -- CONSTRUÍDO A PARTIR DESSA FUNCTION USA ELAS.
  SELECT
    F_IF(D.PLACA_VEICULO IS NULL, DV.PLACA_VEICULO, D.PLACA_VEICULO)          AS PLACA_VEICULO,
    F_IF(D.KM_ATUAL_VEICULO IS NULL, DV.KM_ATUAL_VEICULO, D.KM_ATUAL_VEICULO) AS KM_ATUAL_VEICULO,
    D.COD_OS                                                                  AS COD_OS,
    D.COD_UNIDADE_ITEM_OS                                                     AS COD_UNIDADE_ITEM_OS,
    D.COD_ITEM_OS                                                             AS COD_ITEM_OS,
    D.DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM                                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
    D.STATUS_ITEM_OS                                                          AS STATUS_ITEM_OS,
    D.PRAZO_RESOLUCAO_ITEM_HORAS                                              AS PRAZO_RESOLUCAO_ITEM_HORAS,
    D.PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS                                   AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
    D.QTD_APONTAMENTOS                                                        AS QTD_APONTAMENTOS,
    D.COD_COLABORADOR_RESOLUCAO                                               AS COD_COLABORADOR_RESOLUCAO,
    D.NOME_COLABORADOR_RESOLUCAO                                              AS NOME_COLABORADOR_RESOLUCAO,
    D.DATA_HORA_RESOLUCAO                                                     AS DATA_HORA_RESOLUCAO,
    D.DATA_HORA_INICIO_RESOLUCAO                                              AS DATA_HORA_INICIO_RESOLUCAO,
    D.DATA_HORA_FIM_RESOLUCAO                                                 AS DATA_HORA_FIM_RESOLUCAO,
    D.FEEDBACK_RESOLUCAO                                                      AS FEEDBACK_RESOLUCAO,
    D.DURACAO_RESOLUCAO_MINUTOS                                               AS DURACAO_RESOLUCAO_MINUTOS,
    D.KM_VEICULO_COLETADO_RESOLUCAO                                           AS KM_VEICULO_COLETADO_RESOLUCAO,
    D.COD_PERGUNTA                                                            AS COD_PERGUNTA,
    D.DESCRICAO_PERGUNTA                                                      AS DESCRICAO_PERGUNTA,
    D.COD_ALTERNATIVA                                                         AS COD_ALTERNATIVA,
    D.DESCRICAO_ALTERNATIVA                                                   AS DESCRICAO_ALTERNATIVA,
    D.ALTERNATIVA_TIPO_OUTROS                                                 AS ALTERNATIVA_TIPO_OUTROS,
    D.DESCRICAO_TIPO_OUTROS                                                   AS DESCRICAO_TIPO_OUTROS,
    D.PRIORIDADE_ALTERNATIVA                                                  AS PRIORIDADE_ALTERNATIVA
  FROM DADOS D
    RIGHT JOIN DADOS_VEICULO DV
      ON D.PLACA_VEICULO = DV.PLACA_VEICULO;
END;
$$;


DROP FUNCTION FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(
F_COD_UNIDADE BIGINT,
F_COD_OS BIGINT,
F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE );


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(
  F_COD_UNIDADE         BIGINT,
  F_COD_OS              BIGINT,
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    PLACA_VEICULO                         TEXT,
    KM_ATUAL_VEICULO                      BIGINT,
    COD_OS                                BIGINT,
    COD_UNIDADE_OS                        BIGINT,
    STATUS_OS                             TEXT,
    DATA_HORA_ABERTURA_OS                 TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FECHAMENTO_OS               TIMESTAMP WITHOUT TIME ZONE,
    COD_ITEM_OS                           BIGINT,
    COD_UNIDADE_ITEM_OS                   BIGINT,
    DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ITEM_OS                        TEXT,
    PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
    PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
    QTD_APONTAMENTOS                      INTEGER,
    COD_COLABORADOR_RESOLUCAO             BIGINT,
    NOME_COLABORADOR_RESOLUCAO            TEXT,
    DATA_HORA_RESOLUCAO                   TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_INICIO_RESOLUCAO            TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM_RESOLUCAO               TIMESTAMP WITHOUT TIME ZONE,
    FEEDBACK_RESOLUCAO                    TEXT,
    DURACAO_RESOLUCAO_MINUTOS             BIGINT,
    KM_VEICULO_COLETADO_RESOLUCAO         BIGINT,
    COD_PERGUNTA                          BIGINT,
    DESCRICAO_PERGUNTA                    TEXT,
    COD_ALTERNATIVA                       BIGINT,
    DESCRICAO_ALTERNATIVA                 TEXT,
    ALTERNATIVA_TIPO_OUTROS               BOOLEAN,
    DESCRICAO_TIPO_OUTROS                 TEXT,
    PRIORIDADE_ALTERNATIVA                TEXT)
LANGUAGE PLPGSQL
as $$
BEGIN
  RETURN QUERY
  SELECT
    C.PLACA_VEICULO :: TEXT                                                AS PLACA_VEICULO,
    V.KM                                                                   AS KM_ATUAL_VEICULO,
    COS.CODIGO                                                             AS COD_OS,
    COS.COD_UNIDADE                                                        AS COD_UNIDADE_OS,
    COS.STATUS :: TEXT                                                     AS STATUS_OS,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)                     AS DATA_HORA_ABERTURA_OS,
    COS.DATA_HORA_FECHAMENTO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)        AS DATA_HORA_FECHAMENTO_OS,
    COSI.CODIGO                                                            AS COD_ITEM_OS,
    COS.COD_UNIDADE                                                        AS COD_UNIDADE_ITEM_OS,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
    COSI.STATUS_RESOLUCAO                                                  AS STATUS_ITEM_OS,
    PRIO.PRAZO                                                             AS PRAZO_RESOLUCAO_ITEM_HORAS,
    TO_MINUTES_TRUNC((C.DATA_HORA
                      + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                     - F_DATA_HORA_ATUAL_UTC)                              AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
    COSI.QT_APONTAMENTOS                                                   AS QTD_APONTAMENTOS,
    CO.CODIGO                                                              AS COD_COLABORADOR_RESOLUCAO,
    CO.NOME :: TEXT                                                        AS NOME_COLABORADOR_RESOLUCAO,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)         AS DATA_HORA_RESOLUCAO,
    COSI.DATA_HORA_INICIO_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_INICIO_RESOLUCAO,
    COSI.DATA_HORA_FIM_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)    AS DATA_HORA_FIM_RESOLUCAO,
    COSI.FEEDBACK_CONSERTO                                                 AS FEEDBACK_RESOLUCAO,
    MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                               AS DURACAO_RESOLUCAO_MINUTOS,
    COSI.KM                                                                AS KM_VEICULO_COLETADO_RESOLUCAO,
    CP.CODIGO                                                              AS COD_PERGUNTA,
    CP.PERGUNTA                                                            AS DESCRICAO_PERGUNTA,
    CAP.CODIGO                                                             AS COD_ALTERNATIVA,
    CAP.ALTERNATIVA                                                        AS DESCRICAO_ALTERNATIVA,
    CAP.ALTERNATIVA_TIPO_OUTROS                                            AS ALTERNATIVA_TIPO_OUTROS,
    F_IF(CAP.ALTERNATIVA_TIPO_OUTROS,
         (SELECT CR.RESPOSTA
          FROM CHECKLIST_RESPOSTAS CR
          WHERE CR.COD_CHECKLIST = C.CODIGO
                AND CR.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT,
         NULL)                                                             AS DESCRICAO_TIPO_OUTROS,
    CAP.PRIORIDADE :: TEXT                                                 AS PRIORIDADE_ALTERNATIVA
  FROM CHECKLIST C
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON COSI.COD_PERGUNTA = CP.CODIGO
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON COSI.COD_ALTERNATIVA = CAP.CODIGO
    JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
      ON CAP.PRIORIDADE = PRIO.PRIORIDADE
    JOIN VEICULO V
      ON C.PLACA_VEICULO = V.PLACA
    LEFT JOIN COLABORADOR CO
      ON CO.CPF = COSI.CPF_MECANICO
  WHERE COS.CODIGO = F_COD_OS
        AND COS.COD_UNIDADE = F_COD_UNIDADE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- Cria e atribui novas permissões da parte de movimentação. Remove também as permissões antigas.
--######################################################################################################################
--######################################################################################################################
-- Quem tiver essa permissão:
-- MOVIMENTAR_GERAL = 120;
--
-- Deve receber essas:
--‘Movimentação - Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)’
-- MOVIMENTAR_VEICULO_ESTOQUE = 142;
--‘Movimentação - Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)’
-- MOVIMENTAR_ANALISE = 143;
--‘Movimentação - Descarte (Estoque ou Veículo ou Análise -> Descarte)’
-- MOVIMENTAR_DESCARTE = 144;

INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR)
VALUES (142, 'Movimentação - 1) Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)', 1);
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR)
VALUES (143, 'Movimentação - 2) Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)', 1);
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR)
VALUES (144, 'Movimentação - 3) Descarte (Estoque ou Veículo ou Análise -> Descarte)', 1);

INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
  SELECT
    CFP.COD_UNIDADE,
    CFP.COD_FUNCAO_COLABORADOR,
    -- MOVIMENTAR_VEICULO_ESTOQUE
    142,
    CFP.COD_PILAR_PROLOG
  FROM CARGO_FUNCAO_PROLOG_V11 CFP
WHERE CFP.COD_FUNCAO_PROLOG = 120;

INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
  SELECT
    CFP.COD_UNIDADE,
    CFP.COD_FUNCAO_COLABORADOR,
    -- MOVIMENTAR_ANALISE
    143,
    CFP.COD_PILAR_PROLOG
  FROM CARGO_FUNCAO_PROLOG_V11 CFP
WHERE CFP.COD_FUNCAO_PROLOG = 120;

INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
  SELECT
    CFP.COD_UNIDADE,
    CFP.COD_FUNCAO_COLABORADOR,
    -- MOVIMENTAR_DESCARTE
    144,
    CFP.COD_PILAR_PROLOG
  FROM CARGO_FUNCAO_PROLOG_V11 CFP
WHERE CFP.COD_FUNCAO_PROLOG = 120;


-- Remove permissões antigas que não serão mais utilizadas:
-- MOVIMENTAR_GERAL = 120;
-- MOVIMENTAR_ANALISE_TO_DESCARTE = 125;

DELETE FROM CARGO_FUNCAO_PROLOG_V11 CFP
WHERE CFP.COD_FUNCAO_PROLOG IN (120, 125);

DELETE FROM FUNCAO_PROLOG_V11 FP
WHERE FP.CODIGO IN (120, 125);
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;