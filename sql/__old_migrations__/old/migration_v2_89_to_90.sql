BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Correção de componentes da dashboard de checklist (PL-1959, PL-1960)
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE(
  F_COD_UNIDADES BIGINT [],
  F_STATUS_ITENS TEXT)
  RETURNS TABLE(
    PRIORIDADE TEXT,
    QUANTIDADE BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH PRIORIDADES AS (
      SELECT CAP.PRIORIDADE :: TEXT AS PRIORIDADE
      FROM CHECKLIST_ALTERNATIVA_PRIORIDADE CAP
      ORDER BY CAP.PRAZO ASC
  )

  SELECT
    P.PRIORIDADE       AS PRIORIDADE,
    COUNT(COSI.CODIGO) AS QUANTIDADE
  FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON CAP.CODIGO = COSI.COD_ALTERNATIVA
         AND COSI.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND COSI.STATUS_RESOLUCAO = F_STATUS_ITENS
    RIGHT JOIN PRIORIDADES P
      ON CAP.PRIORIDADE = P.PRIORIDADE
  GROUP BY P.PRIORIDADE
  ORDER BY CASE P.PRIORIDADE
           WHEN 'CRITICA'
             THEN 1
           WHEN 'ALTA'
             THEN 2
           WHEN 'BAIXA'
             THEN 3
           END;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PLACAS_MAIOR_QTD_ITENS_ABERTOS(
  F_COD_UNIDADES             BIGINT [],
  F_TOTAL_PLACAS_PARA_BUSCAR INTEGER)
  RETURNS TABLE(
    NOME_UNIDADE                      CHARACTER VARYING,
    PLACA                             CHARACTER VARYING,
    QUANTIDADE_ITENS_ABERTOS          BIGINT,
    QUANTIDADE_ITENS_CRITICOS_ABERTOS BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITENS_ABERTOS CHAR := 'P';
  PRIORIDADE_CRITICA   VARCHAR := 'CRITICA';
BEGIN
  RETURN QUERY
  WITH PLACAS AS (
      SELECT
        C.PLACA_VEICULO    AS PLACA_VEICULO,
        COUNT(COSI.CODIGO) AS QUANTIDADE_ITENS_ABERTOS,
        COUNT(CASE WHEN CAP.PRIORIDADE = PRIORIDADE_CRITICA
          THEN 1 END)      AS QUANTIDADE_ITENS_CRITICOS_ABERTOS
      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON COSI.COD_OS = COS.CODIGO
             AND COSI.COD_UNIDADE = COS.COD_UNIDADE
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON COSI.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON C.CODIGO = COS.COD_CHECKLIST
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND COSI.STATUS_RESOLUCAO = STATUS_ITENS_ABERTOS
      GROUP BY C.PLACA_VEICULO
      LIMIT F_TOTAL_PLACAS_PARA_BUSCAR
  )

  SELECT
    U.NOME AS NOME_UNIDADE,
    P.PLACA_VEICULO,
    P.QUANTIDADE_ITENS_ABERTOS,
    P.QUANTIDADE_ITENS_CRITICOS_ABERTOS
  FROM PLACAS P
    JOIN VEICULO V ON V.PLACA = P.PLACA_VEICULO
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
  ORDER BY
    P.QUANTIDADE_ITENS_ABERTOS DESC,
    P.PLACA_VEICULO ASC
  LIMIT F_TOTAL_PLACAS_PARA_BUSCAR;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Corrige function de busca das jornadas em andamento para tela de viagens (PL-1906)
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
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Integração com transport.
-- FUNCTION PARA FORMATAR CORRETAMENTE O CPF.
CREATE OR REPLACE FUNCTION FORMAT_CPF(CPF BIGINT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
DECLARE
  _CPF TEXT;
BEGIN
  IF LENGTH(CPF::TEXT) > 11
  THEN RAISE EXCEPTION 'CPF % fora do padrão', CPF;
  END IF;

  SELECT REPLACE(TO_CHAR(CPF, '000:000:000-00'), ':', '.') INTO _CPF;
  RETURN _CPF;
END;
$$;

-- CRIA CHAVE DO SISTEMA PARA INTEGRAÇÃO DO TRANSPORT - TRANSLECCHI
INSERT INTO INTEGRACAO(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO) VALUES (4, 'TRANSPORT_TRANSLECCHI', 'CHECKLIST');
INSERT INTO INTEGRACAO(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO) VALUES (4, 'TRANSPORT_TRANSLECCHI', 'CHECKLIST_MODELO');
INSERT INTO INTEGRACAO(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO) VALUES (4, 'TRANSPORT_TRANSLECCHI', 'CHECKLIST_ORDEM_SERVICO');

-- CRIA TABELA PARA SALVAR ITENS QUE FORAM RESOLVIDOS PELA INTEGRAÇÃO
CREATE TABLE IF NOT EXISTS INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO (
  COD_EMPRESA                   BIGINT NOT NULL,
  COD_UNIDADE                   BIGINT NOT NULL,
  COD_OS                        BIGINT NOT NULL,
  COD_ITEM_OS                   BIGINT NOT NULL,
  DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT UNIQUE_ITEM_ORDEM_SERVICO_RESOLVIDO UNIQUE (COD_EMPRESA, COD_UNIDADE, COD_OS, COD_ITEM_OS),
  CONSTRAINT FK_INTEGRACAO_ITEM_RESOLVIDO_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT FK_INTEGRACAO_ITEM_RESOLVIDO_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO)
);

-- CRIA FUNCTION PARA REALIZAR A BUSCA DOS ITENS PENDENTES DE ORDENS DE SERVIÇOS A PARTIR DO TOKEN DA EMPRESA
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(
  F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO BIGINT,
  F_TOKEN_INTEGRACAO                      TEXT)
  RETURNS TABLE(
    PLACA_VEICULO                      TEXT,
    KM_ABERTURA_SERVICO                BIGINT,
    COD_ORDEM_SERVICO                  BIGINT,
    COD_UNIDADE_ORDEM_SERVICO          BIGINT,
    STATUS_ORDEM_SERVICO               TEXT,
    DATA_HORA_ABERTURA_SERVICO         TIMESTAMP WITHOUT TIME ZONE,
    COD_ITEM_ORDEM_SERVICO             BIGINT,
    COD_UNIDADE_ITEM_ORDEM_SERVICO     BIGINT,
    DATA_HORA_PRIMEIRO_APONTAMENTO     TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ITEM_ORDEM_SERVICO          TEXT,
    PRAZO_RESOLUCAO_ITEM_HORAS         INTEGER,
    QTD_APONTAMENTOS                   INTEGER,
    COD_CHECKLIST_PRIMEIRO_APONTAMENTO BIGINT,
    COD_PERGUNTA                       BIGINT,
    DESCRICAO_PERGUNTA                 TEXT,
    COD_ALTERNATIVA_PERGUNTA           BIGINT,
    DESCRICAO_ALTERNATIVA              TEXT,
    IS_TIPO_OUTROS                     BOOLEAN,
    DESCRICAO_TIPO_OUTROS              TEXT,
    PRIORIDADE_ALTERNATIVA             TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE TEXT := 'P';
BEGIN
  RETURN QUERY
  WITH ITENS_OS AS (
      SELECT
        C.PLACA_VEICULO                                    AS PLACA_VEICULO,
        C.KM_VEICULO                                       AS KM_ABERTURA_SERVICO,
        COS.CODIGO                                         AS COD_ORDEM_SERVICO,
        COS.COD_UNIDADE                                    AS COD_UNIDADE_SERVICO,
        COS.STATUS                                         AS STATUS_ORDEM_SERVICO,
        C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_ABERTURA_SERVICO,
        COSI.CODIGO                                        AS COD_ITEM_ORDEM_SERVICO,
        COSI.COD_UNIDADE                                   AS COD_UNIDADE_ITEM_ORDEM_SERVICO,
        C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_PRIMEIRO_APONTAMENTO,
        COSI.STATUS_RESOLUCAO                              AS STATUS_ITEM_ORDEM_SERVICO,
        PRIORIDADE.PRAZO                                   AS PRAZO_RESOLUCAO_ITEM_HORAS,
        COSI.QT_APONTAMENTOS                               AS QTD_APONTAMENTOS,
        C.CODIGO                                           AS COD_CHECKLIST_PRIMEIRO_APONTAMENTO,
        COSI.COD_PERGUNTA                                  AS COD_PERGUNTA,
        CP.PERGUNTA                                        AS DESCRICAO_PERGUNTA,
        COSI.COD_ALTERNATIVA                               AS COD_ALTERNATIVA_PERGUNTA,
        CAP.ALTERNATIVA                                    AS DESCRICAO_ALTERNATIVA,
        CAP.ALTERNATIVA_TIPO_OUTROS                        AS IS_TIPO_OUTROS,
        CAP.PRIORIDADE                                     AS PRIORIDADE_ALTERNATIVA
      FROM CHECKLIST_ORDEM_SERVICO COS
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
             AND COS.COD_UNIDADE = COSI.COD_UNIDADE
        JOIN CHECKLIST C
          ON COS.COD_CHECKLIST = C.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON COSI.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIORIDADE
          ON CAP.PRIORIDADE = PRIORIDADE.PRIORIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA = CP.CODIGO
      WHERE COSI.COD_UNIDADE IN (SELECT CODIGO
                                 FROM UNIDADE
                                 WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                      FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                      WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
            AND COSI.STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE
            AND COSI.CODIGO > F_COD_ULTIMO_ITEM_PENDENTE_SINCRONIZADO
      ORDER BY COSI.CODIGO ASC
  )

  SELECT
    IOS.PLACA_VEICULO :: TEXT              AS PLACA_VEICULO,
    IOS.KM_ABERTURA_SERVICO                AS KM_ABERTURA_SERVICO,
    IOS.COD_ORDEM_SERVICO                  AS COD_ORDEM_SERVICO,
    IOS.COD_UNIDADE_SERVICO                AS COD_UNIDADE_SERVICO,
    IOS.STATUS_ORDEM_SERVICO :: TEXT       AS STATUS_ORDEM_SERVICO,
    IOS.DATA_HORA_ABERTURA_SERVICO         AS DATA_HORA_ABERTURA_SERVICO,
    IOS.COD_ITEM_ORDEM_SERVICO             AS COD_ITEM_ORDEM_SERVICO,
    IOS.COD_UNIDADE_ITEM_ORDEM_SERVICO     AS COD_UNIDADE_ITEM_ORDEM_SERVICO,
    IOS.DATA_HORA_PRIMEIRO_APONTAMENTO     AS DATA_HORA_PRIMEIRO_APONTAMENTO,
    IOS.STATUS_ITEM_ORDEM_SERVICO :: TEXT  AS STATUS_ITEM_ORDEM_SERVICO,
    IOS.PRAZO_RESOLUCAO_ITEM_HORAS         AS PRAZO_RESOLUCAO_ITEM_HORAS,
    IOS.QTD_APONTAMENTOS                   AS QTD_APONTAMENTOS,
    IOS.COD_CHECKLIST_PRIMEIRO_APONTAMENTO AS COD_CHECKLIST_PRIMEIRO_APONTAMENTO,
    IOS.COD_PERGUNTA                       AS COD_PERGUNTA,
    IOS.DESCRICAO_PERGUNTA                 AS DESCRICAO_PERGUNTA,
    IOS.COD_ALTERNATIVA_PERGUNTA           AS COD_ALTERNATIVA_PERGUNTA,
    IOS.DESCRICAO_ALTERNATIVA              AS DESCRICAO_ALTERNATIVA,
    IOS.IS_TIPO_OUTROS                     AS IS_TIPO_OUTROS,
    CASE WHEN IOS.IS_TIPO_OUTROS
      THEN CR.RESPOSTA
    ELSE NULL END                          AS DESCRICAO_TIPO_OUTROS,
    IOS.PRIORIDADE_ALTERNATIVA :: TEXT     AS PRIORIDADE_ALTERNATIVA
  FROM ITENS_OS IOS
    JOIN CHECKLIST_RESPOSTAS CR
      ON IOS.COD_CHECKLIST_PRIMEIRO_APONTAMENTO = CR.COD_CHECKLIST
         AND IOS.COD_ALTERNATIVA_PERGUNTA = CR.COD_ALTERNATIVA
  ORDER BY IOS.COD_ITEM_ORDEM_SERVICO ASC;
END;
$$;

-- CRIA FUNCTION PARA RESOLVER OS ITENS DE ORDENS DE SERVIÇOS. CASO A ORDEM DE SERVIÇO NÃO FIQUE COM NENHUM ITEM
-- PENDENT,E ELA SERÁ FECHADA TAMBÉM.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_INTEGRACAO_RESOLVE_ITENS_PENDENTES_EMPRESA(
  F_COD_UNIDADE_ORDEM_SERVICO     BIGINT,
  F_COD_ORDEM_SERVICO             BIGINT,
  F_COD_ITEM_RESOLVIDO            BIGINT,
  F_CPF_COLABORADOR_RESOLUCAO     BIGINT,
  F_KM_MOMENTO_RESOLUCAO          BIGINT,
  F_DURACAO_RESOLUCAO_MS          BIGINT,
  F_FEEDBACK_RESOLUCAO            TEXT,
  F_DATA_HORA_RESOLVIDO_PROLOG    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_INICIO_RESOLUCAO    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_FIM_RESOLUCAO       TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO              TEXT,
  F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE  TEXT := 'P';
  F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO TEXT := 'R';
  F_STATUS_ORDEM_SERVICO_ABERTA         TEXT := 'A';
  F_STATUS_ORDEM_SERVICO_FECHADA        TEXT := 'F';
  F_QTD_ROWS_ITENS_OS                   BIGINT;
  F_QTD_ROWS_ITENS_RESOLVIDOS_OS        BIGINT;
BEGIN

  -- VERIFICAMOS SE O CPF ESTÁ CADASTRADO PARA A EMPRESA EM QUESTÃO
  IF (SELECT NOT EXISTS(SELECT C.CODIGO FROM COLABORADOR C
  WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
        AND C.COD_EMPRESA = (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORDEM_SERVICO)))
  THEN RAISE EXCEPTION 'O CPF % não encontra-se cadastrado no ProLog', FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  -- POR SEGURANÇA, VERIFICAMOS SE A INTEGRAÇÃO ESTÁ FECHANDO OS ITENS DE O.S. QUE PERTENCEM A EMPRESA CORRETA.
  IF (F_COD_UNIDADE_ORDEM_SERVICO NOT IN (SELECT CODIGO
                                          FROM UNIDADE
                                          WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
  THEN RAISE EXCEPTION 'Você está tentando fechar um item de uma OS que não pertence à sua empresa.';
  END IF;

  -- VERIFICAMOS SE O ITEM JÁ ESTÁ RESOLVIDO. OPTAMOS POR LANÇAR UMA EXCEÇÃO NESSE CASO POIS TRATA-SE DE
  -- UMA INCONSISTÊNCIA EXISTENTE NA INTEGRAÇÃO, DEVENDO SER ANALISADO O MOTIVO DELA EXISTIR.
  IF ((SELECT COSI.STATUS_RESOLUCAO FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
  WHERE COSI.CODIGO = F_COD_ITEM_RESOLVIDO) = F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO)
  THEN RAISE EXCEPTION 'O item % do ProLog já encontra-se resolvido', F_COD_ITEM_RESOLVIDO
  USING ERRCODE = (SELECT SQL_ERROR_CODE FROM PROLOG_SQL_ERROR_CODE WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
  END IF;

  UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET
    CPF_MECANICO               = F_CPF_COLABORADOR_RESOLUCAO,
    TEMPO_REALIZACAO           = F_DURACAO_RESOLUCAO_MS,
    KM                         = F_KM_MOMENTO_RESOLUCAO,
    STATUS_RESOLUCAO           = F_STATUS_ITEM_ORDEM_SERVICO_RESOLVIDO,
    DATA_HORA_CONSERTO         = F_DATA_HORA_RESOLVIDO_PROLOG,
    DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
    DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO,
    FEEDBACK_CONSERTO          = F_FEEDBACK_RESOLUCAO
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND CODIGO  = F_COD_ITEM_RESOLVIDO
        AND DATA_HORA_CONSERTO IS NULL;

  -- O ATRIBUTO 'ROW_COUNT' CONTERÁ A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
  -- IRÁ RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
  GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

  -- O PRIMEIRO IF VERIFICA SE O ITEM ESTÁ PENDENTE, SE APÓS O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA
  -- QUE O UPDATE NÃO EXECUTOU CORRETAMENTE. LANÇAMOS AQUI UMA EXCEÇÃO PARA RASTREAR ESSE ERRO
  IF F_QTD_ROWS_ITENS_OS <= 0
  THEN RAISE EXCEPTION 'Não foi possível resolver o item %', F_COD_ITEM_RESOLVIDO;
  END IF;

  -- AO RESOLVER UM ITEM DE ORDEM DE SERVIÇO É NECESSÁRIO VERIFICAR SE A ORDEM DE SERVIÇO FOI FINALIZADA.
  -- UMA 'O.S.' FECHADA CONSISTE EM UMA 'O.S.' QUE POSSUI TODOS OS SEUS ITENS RESOLVIDOS.
  UPDATE CHECKLIST_ORDEM_SERVICO SET
    STATUS = F_STATUS_ORDEM_SERVICO_FECHADA,
    DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND CODIGO = F_COD_ORDEM_SERVICO
        -- UTILIZAMOS ESSA VERIFICAÇÃO PARA FORÇAR QUE O UPDATE ACONTEÇA APENAS SE A 'O.S.' TIVER
        -- TODOS SEUS ITENS RESOLVIDOS.
        AND (SELECT COUNT(*) FROM CHECKLIST_ORDEM_SERVICO_ITENS
  WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
        AND COD_OS = F_COD_ORDEM_SERVICO
        AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0;

  -- PARA GARANTIR A CONSISTÊNCIA DO PROCESSO DE RESOLUÇÃO DE ITENS E FECHAMENTO DE ORDENS DE SERVIÇO
  -- VERIFICAMOS SE A 'O.S.' QUE POSSUIU SEU ITEM FECHADO ESTÁ COM O 'STATUS' CORRETO.
  IF (((SELECT COUNT(*)
        FROM CHECKLIST_ORDEM_SERVICO_ITENS
        WHERE COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO
              AND COD_OS = F_COD_ORDEM_SERVICO
              AND STATUS_RESOLUCAO = F_STATUS_ITEM_ORDEM_SERVICO_PENDENTE) = 0)
      AND (SELECT STATUS
           FROM CHECKLIST_ORDEM_SERVICO
           WHERE CODIGO = F_COD_ORDEM_SERVICO
                 AND COD_UNIDADE = F_COD_UNIDADE_ORDEM_SERVICO) = F_STATUS_ORDEM_SERVICO_ABERTA)
  -- CASO A 'O.S.' NÃO TEM NENHUM ITEM PENDENTE MAS O SEU STATUS É 'ABERTA', ENTÃO TEMOS DADOS INCONSISTENTES.
  THEN RAISE EXCEPTION 'Não foi possível fechar a Ordem de Serviço %', F_COD_ORDEM_SERVICO;
  END IF;

  -- APÓS REALIZAR O PROCESSO DE FECHAMENTO, INSERIMOS O ITEM RESOLVIDO NA TABELA DE MAPEAMENTO DE INTES RESOLVIDOS
  -- ATRAVÉS DA INTEGRAÇÃO
  INSERT INTO INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
  VALUES ((SELECT TI.COD_EMPRESA
           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO),
          F_COD_UNIDADE_ORDEM_SERVICO,
          F_COD_ORDEM_SERVICO,
          F_COD_ITEM_RESOLVIDO,
          F_DATA_HORA_SINCRONIA_RESOLUCAO);

  GET DIAGNOSTICS F_QTD_ROWS_ITENS_RESOLVIDOS_OS = ROW_COUNT;

  -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTE DE ITENS RESOLVIDOS NA INTEGRAÇÃO OCORREU COM ÊXITO
  IF F_QTD_ROWS_ITENS_RESOLVIDOS_OS <= 0
  THEN
    RAISE EXCEPTION 'Não foi possível inserir o item resolvido na tabela de mapeamento, item %', F_COD_ITEM_RESOLVIDO;
  END IF;

  RETURN F_QTD_ROWS_ITENS_OS;
END;
$$;
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;