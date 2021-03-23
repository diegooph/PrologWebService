-- Vamos parar de usar soundex para usar o algoritmo de comparação Jaro.
-- https://rosettacode.org/wiki/Jaro_distance
-- Esse é o mesmo algortimo usado para detectar mudanças nas respostas de alternativas tipo_outros no java, na classe
-- TipoOutrosSimilarityFinder.
CREATE EXTENSION PG_SIMILARITY;

CREATE FUNCTION FUNC_JARO(TEXT, TEXT)
    RETURNS DOUBLE PRECISION
    IMMUTABLE
    STRICT
    LANGUAGE SQL
AS
$$
SELECT JARO(UNACCENT(LOWER(TRIM($1))), UNACCENT(LOWER(TRIM($2))));
$$;

--REALIZA TODA A LOGICA DE CRIAÇÃO DOS TYPES
CREATE SCHEMA IF NOT EXISTS TYPES;

CREATE TABLE TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
(
    TIPO_PARAMETRIZACAO_MIDIA               TEXT    NOT NULL
        CONSTRAINT PK_CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
            PRIMARY KEY,
    TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_PT_BR TEXT    NOT NULL,
    TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_ES    TEXT    NOT NULL,
    ATIVO                                   BOOLEAN NOT NULL
);

CREATE OR REPLACE VIEW TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA AS
SELECT TIPO_PARAMETRIZACAO_MIDIA                     AS TIPO_PARAMETRIZACAO_MIDIA,
       F_IF((SELECT CURRENT_SETTING('lc_messages') = 'es_es.UTF-8'),
            TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_ES,
            TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL_PT_BR) AS TIPO_PARAMETRIZACAO_MIDIA_LEGIVEL
FROM TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
WHERE ATIVO = TRUE;

INSERT INTO TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
VALUES ('BLOQUEADO', 'Bloqueado', 'Bloqueado', TRUE);
INSERT INTO TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
VALUES ('OBRIGATORIO', 'Obrigatório', 'Obrigatorio', TRUE);
INSERT INTO TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE
VALUES ('OPCIONAL', 'Opcional', 'Opcional', TRUE);

-- PODE PARAMETRIZAR A NECESSIDADE DE ANEXAR MÍDIA AO RESPONDER OK (PERGUNTA) OU NOK (ALTERNATIVA).
ALTER TABLE CHECKLIST_PERGUNTAS_DATA
    ADD COLUMN ANEXO_MIDIA_RESPOSTA_OK TEXT NOT NULL DEFAULT 'BLOQUEADO'
        CONSTRAINT FK_ANEXO_MIDIA
            REFERENCES TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE;
ALTER TABLE CHECKLIST_PERGUNTAS_DATA
    ALTER COLUMN ANEXO_MIDIA_RESPOSTA_OK DROP DEFAULT;

ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    ADD COLUMN ANEXO_MIDIA TEXT NOT NULL DEFAULT 'BLOQUEADO'
        CONSTRAINT FK_ANEXO_MIDIA
            REFERENCES TYPES.CHECKLIST_PARAMETRIZACAO_MIDIA_TYPE;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    ALTER COLUMN ANEXO_MIDIA DROP DEFAULT;

-- ATUALIZA VIEWS.
CREATE OR REPLACE VIEW CHECKLIST_PERGUNTAS AS
SELECT CP.COD_CHECKLIST_MODELO,
       CP.COD_VERSAO_CHECKLIST_MODELO,
       CP.COD_UNIDADE,
       CP.ORDEM,
       CP.PERGUNTA,
       CP.SINGLE_CHOICE,
       CP.COD_IMAGEM,
       CP.CODIGO,
       CP.CODIGO_CONTEXTO,
       CP.ANEXO_MIDIA_RESPOSTA_OK
FROM CHECKLIST_PERGUNTAS_DATA CP
WHERE CP.DELETADO = FALSE;

CREATE OR REPLACE VIEW CHECKLIST_ALTERNATIVA_PERGUNTA AS
SELECT CAP.COD_CHECKLIST_MODELO,
       CAP.COD_VERSAO_CHECKLIST_MODELO,
       CAP.COD_UNIDADE,
       CAP.ALTERNATIVA,
       CAP.ORDEM,
       CAP.COD_PERGUNTA,
       CAP.CODIGO,
       CAP.CODIGO_CONTEXTO,
       CAP.ALTERNATIVA_TIPO_OUTROS,
       CAP.PRIORIDADE,
       CAP.DEVE_ABRIR_ORDEM_SERVICO,
       CAP.ANEXO_MIDIA
FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
WHERE CAP.DELETADO = FALSE;

-- Atualiza functions.
DROP FUNCTION FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT,
    F_COD_MODELO BIGINT,
    F_COD_VERSAO_MODELO BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO BIGINT,
                                                                          F_COD_VERSAO_MODELO BIGINT)
    RETURNS TABLE
            (
                COD_PERGUNTA             BIGINT,
                COD_CONTEXTO_PERGUNTA    BIGINT,
                COD_IMAGEM               BIGINT,
                URL_IMAGEM               TEXT,
                PERGUNTA                 TEXT,
                ORDEM_PERGUNTA           INTEGER,
                SINGLE_CHOICE            BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK  TEXT,
                COD_ALTERNATIVA          BIGINT,
                COD_CONTEXTO_ALTERNATIVA BIGINT,
                ALTERNATIVA              TEXT,
                PRIORIDADE               TEXT,
                ORDEM_ALTERNATIVA        INTEGER,
                DEVE_ABRIR_ORDEM_SERVICO BOOLEAN,
                ALTERNATIVA_TIPO_OUTROS  BOOLEAN,
                ANEXO_MIDIA              TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT CP.CODIGO                    AS COD_PERGUNTA,
       CP.CODIGO_CONTEXTO           AS COD_CONTEXTO_PERGUNTA,
       CGI.COD_IMAGEM               AS COD_IMAGEM,
       CGI.URL_IMAGEM               AS URL_IMAGEM,
       CP.PERGUNTA                  AS PERGUNTA,
       CP.ORDEM                     AS ORDEM_PERGUNTA,
       CP.SINGLE_CHOICE             AS SINGLE_CHOICE,
       CP.ANEXO_MIDIA_RESPOSTA_OK   AS ANEXO_MIDIA_RESPOSTA_OK,
       CAP.CODIGO                   AS COD_ALTERNATIVA,
       CAP.CODIGO_CONTEXTO          AS COD_CONTEXTO_ALTERNATIVA,
       CAP.ALTERNATIVA              AS ALTERNATIVA,
       CAP.PRIORIDADE :: TEXT       AS PRIORIDADE,
       CAP.ORDEM                    AS ORDEM_ALTERNATIVA,
       CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO,
       CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS,
       CAP.ANEXO_MIDIA              AS ANEXO_MIDIA
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.CODIGO = CAP.COD_PERGUNTA
                  AND CAP.COD_UNIDADE = CP.COD_UNIDADE
                  AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
                  AND CAP.COD_VERSAO_CHECKLIST_MODELO = CP.COD_VERSAO_CHECKLIST_MODELO
         LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                   ON CGI.COD_IMAGEM = CP.COD_IMAGEM
WHERE CP.COD_UNIDADE = F_COD_UNIDADE
  AND CP.COD_CHECKLIST_MODELO = F_COD_MODELO
  AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
ORDER BY CP.ORDEM, CP.PERGUNTA, CAP.ORDEM;
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_ANALISA_MUDANCAS_MODELO(F_COD_MODELO BIGINT,
                                                                  F_COD_VERSAO_MODELO BIGINT,
                                                                  F_NOME_MODELO TEXT,
                                                                  F_COD_CARGOS BIGINT[],
                                                                  F_COD_TIPOS_VEICULOS BIGINT[],
                                                                  F_PERGUNTAS_ALTERNATIVAS_JSON JSONB)
    RETURNS TABLE
            (
                CODIGO_ITEM                   BIGINT,
                ITEM_NOVO                     BOOLEAN,
                ITEM_MUDOU_CONTEXTO           BOOLEAN,
                ITEM_TIPO_PERGUNTA            BOOLEAN,
                ALGO_MUDOU_NO_MODELO          BOOLEAN,
                ALGO_MUDOU_NO_CONTEXTO        BOOLEAN,
                DEVE_CRIAR_NOVA_VERSAO_MODELO BOOLEAN
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    V_ALGO_MUDOU_NO_MODELO                  BOOLEAN := FALSE;
    V_ALGO_MUDOU_NO_CONTEXTO                BOOLEAN := FALSE;
    V_DEVE_CRIAR_NOVA_VERSAO_MODELO         BOOLEAN := FALSE;
    -- Essa é a mesma porcentagem utilizada para comparar respostas a alternativas tipo_outros na classe do java:
    -- TipoOutrosSimilarityFinder.
    -- 1.0 muito similar ou idêntico.
    -- 0.0 muito dissimilar ou totalmente diferente.
    ACCEPTED_SIMILARITY_PERCENTAGE CONSTANT FLOAT8  := 0.82;
BEGIN
    -- Verifica se o nome do modelo sofreu alteração. Nesse caso criamos nova versão sem alterar contexto.
    IF (SELECT FUNC_JARO(F_NOME_MODELO, (SELECT CM.NOME
                                         FROM CHECKLIST_MODELO CM
                                         WHERE COD_VERSAO_ATUAL = F_COD_VERSAO_MODELO)) <
               ACCEPTED_SIMILARITY_PERCENTAGE)
    THEN
        V_ALGO_MUDOU_NO_MODELO := TRUE;
        V_DEVE_CRIAR_NOVA_VERSAO_MODELO := TRUE;
    END IF;

    -- 1 -> Cria tabelas temporárias para nos ajudarem a trabalhar com os dados.
    CREATE TEMP TABLE IF NOT EXISTS PERGUNTAS
    (
        _ID                           BIGSERIAL NOT NULL,
        CODIGO                        BIGINT,
        COD_IMAGEM                    BIGINT,
        DESCRICAO                     TEXT      NOT NULL,
        SINGLE_CHOICE                 BOOLEAN   NOT NULL,
        ORDEM_EXIBICAO                INTEGER   NOT NULL,
        ANEXO_MIDIA_RESPOSTA_OK       TEXT      NOT NULL,
        PERGUNTA_NOVA                 BOOLEAN   NOT NULL,
        PERGUNTA_MUDOU                BOOLEAN,
        PERGUNTA_MUDOU_CONTEXTO       BOOLEAN,
        DEVE_CRIAR_NOVA_VERSAO_MODELO BOOLEAN,
        -- Útil para debugar o que ocasionou a mudança.
        MOTIVOS_MUDANCA               TEXT[]
    ) ON COMMIT DELETE ROWS;

    CREATE TEMP TABLE IF NOT EXISTS ALTERNATIVAS
    (
        _ID                           BIGSERIAL NOT NULL,
        CODIGO                        BIGINT,
        DESCRICAO                     TEXT      NOT NULL,
        PRIORIDADE                    TEXT      NOT NULL,
        TIPO_OUTROS                   BOOLEAN   NOT NULL,
        ORDEM_EXIBICAO                INTEGER   NOT NULL,
        DEVE_ABRIR_ORDEM_SERVICO      BOOLEAN   NOT NULL,
        ANEXO_MIDIA                   TEXT      NOT NULL,
        ALTERNATIVA_NOVA              BOOLEAN   NOT NULL,
        ALTERNATIVA_MUDOU             BOOLEAN,
        ALTERNATIVA_MUDOU_CONTEXTO    BOOLEAN,
        DEVE_CRIAR_NOVA_VERSAO_MODELO BOOLEAN,
        -- Útil para debugar o que ocasionou a mudança.
        MOTIVOS_MUDANCA               TEXT[]
    ) ON COMMIT DELETE ROWS;
    --

    -- 2 -> Insere as perguntas.
    WITH CTE AS (
        SELECT jsonb_array_elements(F_PERGUNTAS_ALTERNATIVAS_JSON) SRC
    )
    INSERT
    INTO PERGUNTAS (CODIGO,
                    COD_IMAGEM,
                    DESCRICAO,
                    SINGLE_CHOICE,
                    ORDEM_EXIBICAO,
                    ANEXO_MIDIA_RESPOSTA_OK,
                    PERGUNTA_NOVA)
    SELECT (SRC ->> 'codigo') :: BIGINT,
           (SRC ->> 'codImagem') :: BIGINT,
           (SRC ->> 'descricao'),
           (SRC ->> 'singleChoice') :: BOOLEAN,
           (SRC ->> 'ordemExibicao') :: INTEGER,
           (SRC ->> 'anexoMidiaRespostaOk'),
           -- Se for uma pergunta sendo cadastrada, então ainda não tem código.
           (SRC ->> 'codigo') IS NULL
    FROM CTE;
    --

    -- 3 -> Insere as alternativas.
    WITH CTE AS (
        SELECT jsonb_array_elements(jsonb_array_elements(F_PERGUNTAS_ALTERNATIVAS_JSON) -> 'alternativas') SRC
    )
    INSERT
    INTO ALTERNATIVAS (CODIGO,
                       DESCRICAO,
                       PRIORIDADE,
                       TIPO_OUTROS,
                       ORDEM_EXIBICAO,
                       DEVE_ABRIR_ORDEM_SERVICO,
                       ANEXO_MIDIA,
                       ALTERNATIVA_NOVA)
    SELECT (SRC ->> 'codigo') :: BIGINT,
           (SRC ->> 'descricao'),
           (SRC ->> 'prioridade'),
           (SRC ->> 'tipoOutros') :: BOOLEAN,
           (SRC ->> 'ordemExibicao') :: INTEGER,
           (SRC ->> 'deveAbrirOrdemServico') :: BOOLEAN,
           (SRC ->> 'anexoMidia'),
           -- Se for uma alternativa sendo cadastrada, então ainda não tem código.
           (SRC ->> 'codigo') IS NULL
    FROM CTE;
    --

    --
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    -- 4 - INÍCIO das verificações das perguntas.
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    --
    -- 4.1 -> Verifica se alguma pergunta foi deletada.
    IF (SELECT exists(SELECT CP.CODIGO
                      FROM CHECKLIST_PERGUNTAS CP
                      WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
                        AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
                          EXCEPT
                      SELECT CODIGO
                      FROM PERGUNTAS
                      WHERE CODIGO IS NOT NULL
                   ))
    THEN
        V_ALGO_MUDOU_NO_MODELO := TRUE;
        V_DEVE_CRIAR_NOVA_VERSAO_MODELO := TRUE;
    END IF;

    -- 4.2 -> Verifica se a descrição da pergunta mudou, alterando contexto.
    WITH PERGUNTAS_DESCRICAO_ALTERADA AS (
        SELECT P.CODIGO
        FROM PERGUNTAS P
        WHERE FUNC_JARO(P.DESCRICAO,
                        (SELECT CP.PERGUNTA
                         FROM CHECKLIST_PERGUNTAS CP
                         WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
                           AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
                           AND CP.CODIGO = P.CODIGO)) < ACCEPTED_SIMILARITY_PERCENTAGE
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU_CONTEXTO       = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA, 'Descrição (com jaro) mudou - muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM PERGUNTAS_DESCRICAO_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;
    --

    -- 4.3 -> Verifica se as imagens das perguntas mudaram, alterando o contexto.
    WITH PERGUNTAS_IMAGEM_ALTERADA AS (
        SELECT CODIGO,
               COD_IMAGEM
        FROM PERGUNTAS
            EXCEPT
        SELECT CP.CODIGO,
               CP.COD_IMAGEM
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU_CONTEXTO       = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA, 'Código da imagem mudou - muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM PERGUNTAS_IMAGEM_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;
    --

    -- 4.4 -> Verifica se o tipo de escolha das perguntas mudou sem alteração de contexto.
    WITH PERGUNTAS_TIPO_SELECAO_ALTERADA AS (
        SELECT CODIGO,
               SINGLE_CHOICE
        FROM PERGUNTAS
            EXCEPT
        SELECT CP.CODIGO,
               CP.SINGLE_CHOICE
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU                = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA,
                                                     'Tipo de escolha da pergunta mudou - não muda contexto')
        -- CTE irá conter apenas as perguntas que mudaram o tipo de seleção.
    FROM PERGUNTAS_TIPO_SELECAO_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;
    --

    -- 4.5 -> Verifica se a ordem das perguntas mudou sem alteração de contexto.
    WITH PERGUNTAS_ORDEM_ALTERADA AS (
        SELECT CODIGO,
               ORDEM_EXIBICAO
        FROM PERGUNTAS
            EXCEPT
        SELECT CP.CODIGO,
               CP.ORDEM
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU                = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA,
                                                     'Ordem de exibição da pergunta mudou - não muda contexto')
        -- CTE irá conter apenas as perguntas que mudaram a ordem de exibição.
    FROM PERGUNTAS_ORDEM_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;
    --

    -- 4.6 -> Verifica se a descrição das perguntas mudaram, sem alteração de contexto.
    WITH PERGUNTAS_DESCRICAO_ALTERADA AS (
        SELECT CODIGO,
               DESCRICAO
        FROM PERGUNTAS
            EXCEPT
        SELECT CP.CODIGO,
               CP.PERGUNTA
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU  = TRUE,
        MOTIVOS_MUDANCA = array_append(MOTIVOS_MUDANCA, 'Descrição (sem jaro) da pergunta mudou - não muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM PERGUNTAS_DESCRICAO_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;

    -- 4.7 -> Verifica se a parametrização mídia das perguntas mudou sem alteração de contexto.
    WITH PERGUNTAS_ANEXO_MIDIA_ALTERADA AS (
        SELECT CODIGO,
               ANEXO_MIDIA_RESPOSTA_OK
        FROM PERGUNTAS
            EXCEPT
        SELECT CP.CODIGO,
               CP.ANEXO_MIDIA_RESPOSTA_OK
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE PERGUNTAS P
    SET PERGUNTA_MUDOU                = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA,
                                                     'Anexo de mídias da pergunta mudou - não muda contexto')
        -- CTE irá conter apenas as perguntas que mudaram o anexo de mídia.
    FROM PERGUNTAS_ANEXO_MIDIA_ALTERADA PNE
         -- Só considera como alterada as perguntas que já existiam.
    WHERE PNE.CODIGO IS NOT NULL
      AND PNE.CODIGO = P.CODIGO;
    --
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    -- 4 - FIM das verificações das perguntas.
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    --

    --
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    -- 5 - INÍCIO das verificações das alternativas.
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    --
    -- 5.1 -> Verifica se alguma alternativa foi deletada.
    IF (SELECT exists(SELECT CAP.CODIGO
                      FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
                        AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
                          EXCEPT
                      SELECT CODIGO
                      FROM ALTERNATIVAS
                      WHERE CODIGO IS NOT NULL
                   ))
    THEN
        V_ALGO_MUDOU_NO_MODELO := TRUE;
        V_DEVE_CRIAR_NOVA_VERSAO_MODELO := TRUE;
    END IF;

    -- 5.2 -> Verifica se descrição das alternativas mudaram, alterando o contexto.
    WITH ALTERNATIVAS_DESCRICAO_ALTERADA AS (
        SELECT A.CODIGO
        FROM ALTERNATIVAS A
        WHERE FUNC_JARO(A.DESCRICAO, (SELECT CAP.ALTERNATIVA
                                      FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                                      WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
                                        AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
                                        AND CAP.CODIGO = A.CODIGO)) < ACCEPTED_SIMILARITY_PERCENTAGE
    )

    UPDATE ALTERNATIVAS A
    SET ALTERNATIVA_MUDOU_CONTEXTO    = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA, 'Descrição (com jaro) mudou - muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM ALTERNATIVAS_DESCRICAO_ALTERADA ANE
         -- Só considera como alterada as alternativas que já existiam.
    WHERE ANE.CODIGO IS NOT NULL
      AND A.CODIGO = ANE.CODIGO;

    -- 5.3 -> Verifica se prioridade, alternativa_tipo_outros e/ou deve_abrir_ordem_servico mudaram, alterando o contexto.
    WITH ALTERNATIVAS_ALTERADAS AS (
        SELECT CODIGO,
               PRIORIDADE,
               TIPO_OUTROS,
               DEVE_ABRIR_ORDEM_SERVICO
        FROM ALTERNATIVAS
            EXCEPT
        SELECT CAP.CODIGO,
               CAP.PRIORIDADE,
               CAP.ALTERNATIVA_TIPO_OUTROS,
               CAP.DEVE_ABRIR_ORDEM_SERVICO
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
        WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE ALTERNATIVAS A
    SET ALTERNATIVA_MUDOU_CONTEXTO    = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA,
                                                     'Prioridade, tipo_outros ou/e deve abrir OS mudaram - muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM ALTERNATIVAS_ALTERADAS ANE
         -- Só considera como alterada as alternativas que já existiam.
    WHERE ANE.CODIGO IS NOT NULL
      AND A.CODIGO = ANE.CODIGO;
    --

    -- 5.4 -> Verifica se houve mudanças, porém, que NÃO JUSTIFICAM a criação de uma nova versão do modelo ou a mudança
    --        de contexto.
    --        Dois atributos são considerados:
    --        * DESCRICAO
    --        * ORDEM_EXIBICAO
    WITH ALTERNATIVAS_DESCRICAO_ORDEM_EXIBICAO_ALTERADA AS (
        SELECT CODIGO,
               DESCRICAO,
               ORDEM_EXIBICAO
        FROM ALTERNATIVAS
            EXCEPT
        SELECT CAP.CODIGO,
               CAP.ALTERNATIVA,
               CAP.ORDEM
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
        WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE ALTERNATIVAS A
    SET ALTERNATIVA_MUDOU = TRUE,
        MOTIVOS_MUDANCA   = array_append(MOTIVOS_MUDANCA,
                                         'Descrição (sem jaro) ou/e ordem de exibição mudaram - não muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM ALTERNATIVAS_DESCRICAO_ORDEM_EXIBICAO_ALTERADA ANE
         -- Só considera como alterada as alternativas que já existiam.
    WHERE ANE.CODIGO IS NOT NULL
      AND A.CODIGO = ANE.CODIGO;

    -- 5.5 -> Verifica se houve mudanças que JUSTIFICAM a criação de uma nova versão do modelo mas não a mudança de
    --        contexto.
    --        Um atributo é considerado:
    --        * ANEXO_MIDIA
    WITH ALTERNATIVAS_ANEXO_MIDIA_ALTERADA AS (
        SELECT CODIGO,
               ANEXO_MIDIA
        FROM ALTERNATIVAS
            EXCEPT
        SELECT CAP.CODIGO,
               CAP.ANEXO_MIDIA
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
        WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE ALTERNATIVAS A
    SET ALTERNATIVA_MUDOU             = TRUE,
        DEVE_CRIAR_NOVA_VERSAO_MODELO = TRUE,
        MOTIVOS_MUDANCA               = array_append(MOTIVOS_MUDANCA, 'Anexo de mídia mudou, sem alterar contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM ALTERNATIVAS_ANEXO_MIDIA_ALTERADA ANE
         -- Só considera como alterada as alternativas que já existiam.
    WHERE ANE.CODIGO IS NOT NULL
      AND A.CODIGO = ANE.CODIGO;
    --
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    -- 5 - FIM das verificações das alternativas.
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    --

    -- 6 -> Verifica as mudanças que ocorreram em perguntas e alternativa e seta as variáveis corretamente.
    IF V_ALGO_MUDOU_NO_MODELO IS NULL OR V_ALGO_MUDOU_NO_MODELO IS FALSE
    THEN
        -- Nessas counts usamos * pois mais de uma coluna contribui para uma contagem válida e as condições de WHERE
        -- já cuidam disso.
        V_ALGO_MUDOU_NO_MODELO := COALESCE(
                F_IF((SELECT COUNT(*)
                      FROM PERGUNTAS P
                      WHERE P.PERGUNTA_MUDOU IS NOT NULL
                         OR P.PERGUNTA_NOVA IS TRUE) > 0, TRUE, NULL),
                F_IF((SELECT COUNT(*)
                      FROM ALTERNATIVAS A
                      WHERE A.ALTERNATIVA_MUDOU IS NOT NULL
                         OR A.ALTERNATIVA_NOVA IS TRUE) > 0, TRUE, NULL),
                FALSE);
    END IF;
    IF V_ALGO_MUDOU_NO_CONTEXTO IS NULL OR V_ALGO_MUDOU_NO_CONTEXTO IS FALSE
    THEN
        V_ALGO_MUDOU_NO_CONTEXTO := COALESCE(
                F_IF((SELECT COUNT(P.PERGUNTA_MUDOU_CONTEXTO)
                      FROM PERGUNTAS P
                      WHERE P.PERGUNTA_MUDOU_CONTEXTO IS NOT NULL) > 0, TRUE, NULL),
                F_IF((SELECT COUNT(A.ALTERNATIVA_MUDOU_CONTEXTO)
                      FROM ALTERNATIVAS A
                      WHERE A.ALTERNATIVA_MUDOU_CONTEXTO IS NOT NULL) > 0, TRUE, NULL),
                FALSE);
    END IF;
    IF V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS NULL OR V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS FALSE
    THEN
        -- Nessas counts usamos * pois mais de uma coluna contribui para uma contagem válida e as condições de WHERE
        -- já cuidam disso.
        V_DEVE_CRIAR_NOVA_VERSAO_MODELO := COALESCE(
                F_IF((SELECT COUNT(*)
                      FROM PERGUNTAS P
                      WHERE P.DEVE_CRIAR_NOVA_VERSAO_MODELO IS NOT NULL
                         OR P.PERGUNTA_NOVA IS TRUE) > 0, TRUE, NULL),
                F_IF((SELECT COUNT(*)
                      FROM ALTERNATIVAS A
                      WHERE A.DEVE_CRIAR_NOVA_VERSAO_MODELO IS NOT NULL
                         OR A.ALTERNATIVA_NOVA IS TRUE) > 0, TRUE, NULL),
                FALSE);
    END IF;

    -- 7 -> Verifica se os cargos mudaram.
    IF (SELECT count(*)
        FROM (SELECT CODIGO
              FROM unnest(F_COD_CARGOS) CODIGO
                  EXCEPT
              SELECT CMF.COD_FUNCAO
              FROM CHECKLIST_MODELO_FUNCAO CMF
              WHERE CMF.COD_CHECKLIST_MODELO = F_COD_MODELO) T) > 0
    THEN
        V_ALGO_MUDOU_NO_MODELO := TRUE;
    END IF;
    --

    -- 8 -> Verifica se os tipos de veículos mudaram.
    IF (SELECT count(*)
        FROM (SELECT CODIGO
              FROM unnest(F_COD_TIPOS_VEICULOS) CODIGO
                  EXCEPT
              SELECT CMVT.COD_TIPO_VEICULO
              FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
              WHERE CMVT.COD_MODELO = F_COD_MODELO) T) > 0
    THEN
        V_ALGO_MUDOU_NO_MODELO := TRUE;
    END IF;
    --

    CASE
        -- A) Caso mais simples: nada mudou, não precisamos fazer nada.
        WHEN V_ALGO_MUDOU_NO_CONTEXTO IS FALSE AND V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS FALSE AND
             V_ALGO_MUDOU_NO_MODELO IS FALSE
            THEN RETURN QUERY
                SELECT NULL :: BIGINT  AS CODIGO_ITEM,
                       NULL :: BOOLEAN AS ITEM_NOVO,
                       NULL :: BOOLEAN AS ITEM_MUDOU_CONTEXTO,
                       NULL :: BOOLEAN AS ITEM_TIPO_PERGUNTA,
                       FALSE           AS ALGO_MUDOU_NO_MODELO,
                       FALSE           AS ALGO_MUDOU_NO_CONTEXTO,
                       FALSE           AS DEVE_CRIAR_NOVA_VERSAO_MODELO;

        -- B) Caso intermediário: algo mudou no modelo porém nada que justifique a criação de uma nova versão.
        WHEN V_ALGO_MUDOU_NO_MODELO IS TRUE AND V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS FALSE
            THEN RETURN QUERY
                SELECT NULL :: BIGINT  AS CODIGO_ITEM,
                       NULL :: BOOLEAN AS ITEM_NOVO,
                       NULL :: BOOLEAN AS ITEM_MUDOU_CONTEXTO,
                       NULL :: BOOLEAN AS ITEM_TIPO_PERGUNTA,
                       TRUE            AS ALGO_MUDOU_NO_MODELO,
                       FALSE           AS ALGO_MUDOU_NO_CONTEXTO,
                       FALSE           AS DEVE_CRIAR_NOVA_VERSAO_MODELO;

        -- C) Caso intermediário: algo mudou no modelo com a criação de uma nova versão e mantendo o código de contexto.
        WHEN V_ALGO_MUDOU_NO_MODELO IS TRUE AND V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS TRUE AND
             V_ALGO_MUDOU_NO_CONTEXTO IS FALSE
            THEN RETURN QUERY
                SELECT P.CODIGO                        AS CODIGO_ITEM,
                       P.PERGUNTA_NOVA                 AS ITEM_NOVO,
                       P.PERGUNTA_MUDOU_CONTEXTO       AS ITEM_MUDOU_CONTEXTO,
                       TRUE                            AS ITEM_TIPO_PERGUNTA,
                       TRUE                            AS ALGO_MUDOU_NO_MODELO,
                       FALSE                           AS ALGO_MUDOU_NO_CONTEXTO,
                       V_DEVE_CRIAR_NOVA_VERSAO_MODELO AS DEVE_CRIAR_NOVA_VERSAO_MODELO
                FROM PERGUNTAS P
                UNION ALL
                SELECT A.CODIGO                        AS CODIGO_ITEM,
                       A.ALTERNATIVA_NOVA              AS ITEM_NOVO,
                       A.ALTERNATIVA_MUDOU_CONTEXTO    AS ITEM_MUDOU_CONTEXTO,
                       FALSE                           AS ITEM_TIPO_PERGUNTA,
                       TRUE                            AS ALGO_MUDOU_NO_MODELO,
                       FALSE                           AS ALGO_MUDOU_NO_CONTEXTO,
                       V_DEVE_CRIAR_NOVA_VERSAO_MODELO AS DEVE_CRIAR_NOVA_VERSAO_MODELO
                FROM ALTERNATIVAS A;

        -- D) Caso mais complexo: algo mudou e iremos precisar criar nova versão. Nesse cenário temos que retornar
        -- todas as informações.
        WHEN V_ALGO_MUDOU_NO_CONTEXTO IS TRUE AND V_DEVE_CRIAR_NOVA_VERSAO_MODELO IS TRUE
            THEN RETURN QUERY
                SELECT P.CODIGO                        AS CODIGO_ITEM,
                       P.PERGUNTA_NOVA                 AS ITEM_NOVO,
                       P.PERGUNTA_MUDOU_CONTEXTO       AS ITEM_MUDOU_CONTEXTO,
                       TRUE                            AS ITEM_TIPO_PERGUNTA,
                       TRUE                            AS ALGO_MUDOU_NO_MODELO,
                       TRUE                            AS ALGO_MUDOU_NO_CONTEXTO,
                       V_DEVE_CRIAR_NOVA_VERSAO_MODELO AS DEVE_CRIAR_NOVA_VERSAO_MODELO
                FROM PERGUNTAS P
                UNION ALL
                SELECT A.CODIGO                        AS CODIGO_ITEM,
                       A.ALTERNATIVA_NOVA              AS ITEM_NOVO,
                       A.ALTERNATIVA_MUDOU_CONTEXTO    AS ITEM_MUDOU_CONTEXTO,
                       FALSE                           AS ITEM_TIPO_PERGUNTA,
                       TRUE                            AS ALGO_MUDOU_NO_MODELO,
                       TRUE                            AS ALGO_MUDOU_NO_CONTEXTO,
                       V_DEVE_CRIAR_NOVA_VERSAO_MODELO AS DEVE_CRIAR_NOVA_VERSAO_MODELO
                FROM ALTERNATIVAS A;
        ELSE RAISE EXCEPTION
            'Erro! Estado ilegal dos dados. algo_mudou_no_modelo = false AND deve_criar_nova_versao_modelo = true';
        END CASE;

    DROP TABLE ALTERNATIVAS;
    DROP TABLE PERGUNTAS;
END;
$$;


------- 2.

create table types.checklist_tipo_midia_type
(
    tipo_midia               text    not null
        constraint pk_checklist_tipo_midia_type
            primary key,
    tipo_midia_legivel_pt_br text    not null,
    tipo_midia_legivel_es    text    not null,
    ativo                    boolean not null
);

create or replace view types.checklist_tipo_midia as
select tipo_midia                     as tipo_midia,
       f_if((select current_setting('lc_messages') = 'es_es.UTF-8'),
            tipo_midia_legivel_es,
            tipo_midia_legivel_pt_br) as tipo_parametrizacao_midia_legivel
from types.checklist_tipo_midia_type
where ativo = true;

insert into types.checklist_tipo_midia_type
values ('IMAGEM', 'Imagem', 'Imagen', true);
insert into types.checklist_tipo_midia_type
values ('VIDEO', 'Vídeo', 'Vídeo', true);
insert into types.checklist_tipo_midia_type
values ('AUDIO', 'Áudio', 'Audio', true);

-- Cria as tabelas que irão salvar as URLs das mídias anexadas durante a realização do checklist.
create table checklist_respostas_midias_perguntas_ok
(
    uuid          uuid   not null,
    cod_checklist bigint not null,
    cod_pergunta  bigint not null,
    url_midia     text   not null,
    tipo_midia    text   not null,
    constraint pk_checklist_respostas_midias_perguntas_ok
        primary key (uuid),
    constraint fk_checklist foreign key (cod_checklist)
        references checklist_data (codigo),
    constraint fk_checklist_pergunta foreign key (cod_pergunta)
        references checklist_perguntas_data (codigo),
    constraint fk_tipo_midia foreign key (tipo_midia)
        references types.checklist_tipo_midia_type (tipo_midia)
);

comment on table checklist_respostas_midias_perguntas_ok is 'Salva as mídias anexadas à perguntas do checklist
durante o processo de realização. O UUID é gerado diretamente na aplicação da ponta (app). Ignoramos a unicidade das
URLs para acelerar os processos de insert.';

create table checklist_respostas_midias_alternativas_nok
(
    uuid            uuid   not null,
    cod_checklist   bigint not null,
    cod_alternativa bigint not null,
    url_midia       text   not null,
    tipo_midia      text   not null,
    constraint pk_checklist_respostas_midias_alternativas_nok
        primary key (uuid),
    constraint fk_checklist foreign key (cod_checklist)
        references checklist_data (codigo),
    constraint fk_checklist_alternativa foreign key (cod_alternativa)
        references checklist_alternativa_pergunta_data (codigo),
    constraint fk_tipo_midia foreign key (tipo_midia)
        references types.checklist_tipo_midia_type (tipo_midia)
);

comment on table checklist_respostas_midias_alternativas_nok is 'Salva as mídias anexadas à alternativas do checklist
durante o processo de realização. O UUID é gerado diretamente na aplicação da ponta (app). Ignoramos a unicidade das
URLs para acelerar os processos de insert.';

-- Adiciona colunas de totais e recria view de checklist.
alter table checklist_data
    add column total_midias_perguntas_ok smallint;
alter table checklist_data
    add column total_midias_alternativas_nok smallint;
DROP VIEW CHECKLIST;
CREATE VIEW CHECKLIST AS
SELECT C.COD_UNIDADE                        AS COD_UNIDADE,
       C.COD_CHECKLIST_MODELO               AS COD_CHECKLIST_MODELO,
       C.COD_VERSAO_CHECKLIST_MODELO        AS COD_VERSAO_CHECKLIST_MODELO,
       C.CODIGO                             AS CODIGO,
       C.DATA_HORA                          AS DATA_HORA,
       C.DATA_HORA_REALIZACAO_TZ_APLICADO   AS DATA_HORA_REALIZACAO_TZ_APLICADO,
       C.DATA_HORA_IMPORTADO_PROLOG         AS DATA_HORA_IMPORTADO_PROLOG,
       C.CPF_COLABORADOR                    AS CPF_COLABORADOR,
       C.PLACA_VEICULO                      AS PLACA_VEICULO,
       C.TIPO                               AS TIPO,
       C.TEMPO_REALIZACAO                   AS TEMPO_REALIZACAO,
       C.KM_VEICULO                         AS KM_VEICULO,
       C.DATA_HORA_SINCRONIZACAO            AS DATA_HORA_SINCRONIZACAO,
       C.FONTE_DATA_HORA_REALIZACAO         AS FONTE_DATA_HORA_REALIZACAO,
       C.VERSAO_APP_MOMENTO_REALIZACAO      AS VERSAO_APP_MOMENTO_REALIZACAO,
       C.VERSAO_APP_MOMENTO_SINCRONIZACAO   AS VERSAO_APP_MOMENTO_SINCRONIZACAO,
       C.DEVICE_ID                          AS DEVICE_ID,
       C.DEVICE_IMEI                        AS DEVICE_IMEI,
       C.DEVICE_UPTIME_REALIZACAO_MILLIS    AS DEVICE_UPTIME_REALIZACAO_MILLIS,
       C.DEVICE_UPTIME_SINCRONIZACAO_MILLIS AS DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
       C.FOI_OFFLINE                        AS FOI_OFFLINE,
       C.TOTAL_PERGUNTAS_OK                 AS TOTAL_PERGUNTAS_OK,
       C.TOTAL_PERGUNTAS_NOK                AS TOTAL_PERGUNTAS_NOK,
       C.TOTAL_ALTERNATIVAS_OK              AS TOTAL_ALTERNATIVAS_OK,
       C.TOTAL_ALTERNATIVAS_NOK             AS TOTAL_ALTERNATIVAS_NOK,
       C.TOTAL_MIDIAS_PERGUNTAS_OK          AS TOTAL_MIDIAS_PERGUNTAS_OK,
       C.TOTAL_MIDIAS_ALTERNATIVAS_NOK      AS TOTAL_MIDIAS_ALTERNATIVAS_NOK
FROM CHECKLIST_DATA C
WHERE (C.DELETADO = FALSE);

DROP FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(BIGINT, BIGINT, BIGINT, TIMESTAMP WITH TIME ZONE, BIGINT, BIGINT,
    CHAR, BIGINT, BIGINT, TIMESTAMP WITH TIME ZONE, TEXT, INTEGER, INTEGER, TEXT, TEXT, BIGINT, BIGINT, BOOLEAN, INTEGER,
    INTEGER, INTEGER, INTEGER);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(F_COD_UNIDADE_CHECKLIST BIGINT,
                                                                 F_COD_MODELO_CHECKLIST BIGINT,
                                                                 F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                 F_DATA_HORA_REALIZACAO TIMESTAMP WITH TIME ZONE,
                                                                 F_COD_COLABORADOR BIGINT,
                                                                 F_COD_VEICULO BIGINT,
                                                                 F_TIPO_CHECKLIST CHAR,
                                                                 F_KM_COLETADO BIGINT,
                                                                 F_TEMPO_REALIZACAO BIGINT,
                                                                 F_DATA_HORA_SINCRONIZACAO TIMESTAMP WITH TIME ZONE,
                                                                 F_FONTE_DATA_HORA_REALIZACAO TEXT,
                                                                 F_VERSAO_APP_MOMENTO_REALIZACAO INTEGER,
                                                                 F_VERSAO_APP_MOMENTO_SINCRONIZACAO INTEGER,
                                                                 F_DEVICE_ID TEXT,
                                                                 F_DEVICE_IMEI TEXT,
                                                                 F_DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT,
                                                                 F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                                                                 F_FOI_OFFLINE BOOLEAN,
                                                                 F_TOTAL_PERGUNTAS_OK INTEGER,
                                                                 F_TOTAL_PERGUNTAS_NOK INTEGER,
                                                                 F_TOTAL_ALTERNATIVAS_OK INTEGER,
                                                                 F_TOTAL_ALTERNATIVAS_NOK INTEGER,
                                                                 F_TOTAL_MIDIAS_PERGUNTAS_OK INTEGER,
                                                                 F_TOTAL_MIDIAS_ALTERNATIVAS_NOK INTEGER)
    RETURNS TABLE
            (
                COD_CHECKLIST_INSERIDO BIGINT,
                CHECKLIST_JA_EXISTIA   BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
    V_DEVE_ATUALIZAR_KM_VEICULO BOOLEAN := (CASE
                                                WHEN (F_KM_COLETADO > (SELECT V.KM
                                                                       FROM VEICULO V
                                                                       WHERE V.CODIGO = F_COD_VEICULO))
                                                    THEN
                                                    TRUE
                                                ELSE FALSE END);
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    V_PLACA_ATUAL_DO_VEICULO    TEXT    := (SELECT VD.PLACA
                                            FROM VEICULO_DATA VD
                                            WHERE VD.CODIGO = F_COD_VEICULO);
    V_COD_CHECKLIST_INSERIDO    BIGINT;
    V_QTD_LINHAS_ATUALIZADAS    BIGINT;
    V_CHECKLIST_JA_EXISTIA      BOOLEAN := FALSE;
BEGIN

    INSERT INTO CHECKLIST_DATA(COD_UNIDADE,
                               COD_CHECKLIST_MODELO,
                               COD_VERSAO_CHECKLIST_MODELO,
                               DATA_HORA,
                               DATA_HORA_REALIZACAO_TZ_APLICADO,
                               CPF_COLABORADOR,
                               PLACA_VEICULO,
                               TIPO,
                               TEMPO_REALIZACAO,
                               KM_VEICULO,
                               DATA_HORA_SINCRONIZACAO,
                               FONTE_DATA_HORA_REALIZACAO,
                               VERSAO_APP_MOMENTO_REALIZACAO,
                               VERSAO_APP_MOMENTO_SINCRONIZACAO,
                               DEVICE_ID,
                               DEVICE_IMEI,
                               DEVICE_UPTIME_REALIZACAO_MILLIS,
                               DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                               FOI_OFFLINE,
                               TOTAL_PERGUNTAS_OK,
                               TOTAL_PERGUNTAS_NOK,
                               TOTAL_ALTERNATIVAS_OK,
                               TOTAL_ALTERNATIVAS_NOK,
                               TOTAL_MIDIAS_PERGUNTAS_OK,
                               TOTAL_MIDIAS_ALTERNATIVAS_NOK)
    VALUES (F_COD_UNIDADE_CHECKLIST,
            F_COD_MODELO_CHECKLIST,
            F_COD_VERSAO_MODELO_CHECKLIST,
            F_DATA_HORA_REALIZACAO,
            (F_DATA_HORA_REALIZACAO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE_CHECKLIST)),
            (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
            V_PLACA_ATUAL_DO_VEICULO,
            F_TIPO_CHECKLIST,
            F_TEMPO_REALIZACAO,
            F_KM_COLETADO,
            F_DATA_HORA_SINCRONIZACAO,
            F_FONTE_DATA_HORA_REALIZACAO,
            F_VERSAO_APP_MOMENTO_REALIZACAO,
            F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
            F_DEVICE_ID,
            F_DEVICE_IMEI,
            F_DEVICE_UPTIME_REALIZACAO_MILLIS,
            F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
            F_FOI_OFFLINE,
            F_TOTAL_PERGUNTAS_OK,
            F_TOTAL_PERGUNTAS_NOK,
            F_TOTAL_ALTERNATIVAS_OK,
            F_TOTAL_ALTERNATIVAS_NOK,
            NULLIF(F_TOTAL_MIDIAS_PERGUNTAS_OK, 0),
            NULLIF(F_TOTAL_MIDIAS_ALTERNATIVAS_NOK, 0))
    ON CONFLICT ON CONSTRAINT UNIQUE_CHECKLIST
        DO UPDATE SET DATA_HORA_SINCRONIZACAO = F_DATA_HORA_SINCRONIZACAO
        -- https://stackoverflow.com/a/40880200/4744158
    RETURNING CODIGO, NOT (CHECKLIST_DATA.XMAX = 0) INTO V_COD_CHECKLIST_INSERIDO, V_CHECKLIST_JA_EXISTIA;

    -- Verificamos se o insert funcionou.
    IF V_COD_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível inserir o checklist.';
    END IF;

    IF V_DEVE_ATUALIZAR_KM_VEICULO
    THEN
        UPDATE VEICULO SET KM = F_KM_COLETADO WHERE CODIGO = F_COD_VEICULO;
    END IF;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
    IF (V_DEVE_ATUALIZAR_KM_VEICULO AND (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0))
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o km do veículo.';
    END IF;

    RETURN QUERY SELECT V_COD_CHECKLIST_INSERIDO, V_CHECKLIST_JA_EXISTIA;
END;
$$;


----- 3.

create or replace function func_checklist_insert_midia_pergunta(f_uuid_midia uuid,
                                                                f_cod_checklist bigint,
                                                                f_cod_pergunta bigint,
                                                                f_url_midia text)
    returns void
    language sql
as
$$
insert into checklist_respostas_midias_perguntas_ok(uuid,
                                                    cod_checklist,
                                                    cod_pergunta,
                                                    url_midia,
                                                    tipo_midia)
values (f_uuid_midia,
        f_cod_checklist,
        f_cod_pergunta,
        trim(f_url_midia),
        'IMAGEM')
on conflict on constraint pk_checklist_respostas_midias_perguntas_ok do nothing;
$$;

create or replace function func_checklist_insert_midia_alternativa(f_uuid_midia uuid,
                                                                   f_cod_checklist bigint,
                                                                   f_cod_alternativa bigint,
                                                                   f_url_midia text)
    returns void
    language sql
as
$$
insert into checklist_respostas_midias_alternativas_nok(uuid,
                                                        cod_checklist,
                                                        cod_alternativa,
                                                        url_midia,
                                                        tipo_midia)
values (f_uuid_midia,
        f_cod_checklist,
        f_cod_alternativa,
        trim(f_url_midia),
        'IMAGEM')
on conflict on constraint pk_checklist_respostas_midias_alternativas_nok do nothing;
$$;



----- 4.

-- Implementa a busca dos modelos de checklist com as informações de parametrização de fotos.

-- Atualiza function que busca o modelo de checklist para realização (online).
DROP FUNCTION FUNC_CHECKLIST_GET_MODELO_REALIZACAO(F_COD_MODELO_CHECKLIST BIGINT, F_COD_VEICULO_REALIZACAO BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_MODELO_REALIZACAO(F_COD_MODELO_CHECKLIST BIGINT,
                                                                F_COD_VEICULO_REALIZACAO BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_MODELO_CHECKLIST BIGINT,
                COD_MODELO_CHECKLIST         BIGINT,
                COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                NOME_MODELO_CHECKLIST        TEXT,
                COD_PERGUNTA                 BIGINT,
                DESCRICAO_PERGUNTA           TEXT,
                COD_IMAGEM                   BIGINT,
                URL_IMAGEM                   TEXT,
                PERGUNTA_ORDEM_EXIBICAO      INTEGER,
                SINGLE_CHOICE                BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK      TEXT,
                COD_ALTERNATIVA              BIGINT,
                DESCRICAO_ALTERNATIVA        TEXT,
                TIPO_OUTROS                  BOOLEAN,
                ALTERNATIVA_ORDEM_EXIBICAO   INTEGER,
                PRIORIDADE_ALTERNATIVA       TEXT,
                KM_ATUAL_VEICULO_REALIZACAO  BIGINT,
                ANEXO_MIDIA                  TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    KM_ATUAL_VEICULO CONSTANT BIGINT := (SELECT V.KM
                                         FROM VEICULO V
                                         WHERE V.CODIGO = F_COD_VEICULO_REALIZACAO);
BEGIN
    IF KM_ATUAL_VEICULO IS NULL
    THEN
        RAISE EXCEPTION 'Erro ao buscar KM atual do veículo para realização do checklist!';
    END IF;

    RETURN QUERY
        SELECT CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
               CM.CODIGO                   AS COD_MODELO_CHECKLIST,
               CM.COD_VERSAO_ATUAL         AS COD_VERSAO_MODELO_CHECKLIST,
               CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
               CP.CODIGO                   AS COD_PERGUNTA,
               CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
               CP.COD_IMAGEM               AS COD_IMAGEM,
               CGI.URL_IMAGEM              AS URL_IMAGEM,
               CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
               CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
               CP.ANEXO_MIDIA_RESPOSTA_OK  AS ANEXO_MIDIA_RESPOSTA_OK,
               CAP.CODIGO                  AS COD_ALTERNATIVA,
               CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
               CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
               CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
               CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
               KM_ATUAL_VEICULO            AS KM_ATUAL_VEICULO_REALIZACAO,
               CAP.ANEXO_MIDIA             AS ANEXO_MIDIA
        FROM CHECKLIST_MODELO CM
                 JOIN CHECKLIST_PERGUNTAS CP
                      ON CM.CODIGO = CP.COD_CHECKLIST_MODELO AND
                         CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CP.CODIGO = CAP.COD_PERGUNTA AND CM.COD_VERSAO_ATUAL = CAP.COD_VERSAO_CHECKLIST_MODELO
            -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
                 LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                           ON CP.COD_IMAGEM = CGI.COD_IMAGEM
        WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST
          AND CM.STATUS_ATIVO
        ORDER BY COD_MODELO_CHECKLIST,
                 PERGUNTA_ORDEM_EXIBICAO,
                 COD_PERGUNTA,
                 ALTERNATIVA_ORDEM_EXIBICAO,
                 COD_ALTERNATIVA;
END ;
$$;

-- Atualiza function que busca o modelo de checklist para realização (offline).
DROP FUNCTION FUNC_CHECKLIST_OFFLINE_GET_MODELOS_DISPONIVEIS(F_COD_UNIDADE BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_MODELOS_DISPONIVEIS(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_MODELO_CHECKLIST      BIGINT,
                COD_MODELO_CHECKLIST              BIGINT,
                COD_VERSAO_ATUAL_MODELO_CHECKLIST BIGINT,
                NOME_MODELO_CHECKLIST             TEXT,
                COD_PERGUNTA                      BIGINT,
                COD_CONTEXTO_PERGUNTA             BIGINT,
                DESCRICAO_PERGUNTA                TEXT,
                COD_IMAGEM                        BIGINT,
                URL_IMAGEM                        TEXT,
                PERGUNTA_ORDEM_EXIBICAO           INTEGER,
                SINGLE_CHOICE                     BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK           TEXT,
                COD_ALTERNATIVA                   BIGINT,
                COD_CONTEXTO_ALTERNATIVA          BIGINT,
                DESCRICAO_ALTERNATIVA             TEXT,
                TIPO_OUTROS                       BOOLEAN,
                ALTERNATIVA_ORDEM_EXIBICAO        INTEGER,
                PRIORIDADE_ALTERNATIVA            TEXT,
                ANEXO_MIDIA                       TEXT,
                COD_CARGO                         BIGINT,
                COD_TIPO_VEICULO                  BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH CHECKLIST_MODELO_ATIVO AS (
            SELECT CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
                   CM.CODIGO                   AS COD_MODELO_CHECKLIST,
                   CM.COD_VERSAO_ATUAL         AS COD_VERSAO_MODELO_CHECKLIST,
                   CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
                   CP.CODIGO                   AS COD_PERGUNTA,
                   CP.CODIGO_CONTEXTO          AS COD_CONTEXTO_PERGUNTA,
                   CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
                   CP.COD_IMAGEM               AS COD_IMAGEM,
                   CGI.URL_IMAGEM              AS URL_IMAGEM,
                   CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
                   CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
                   CP.ANEXO_MIDIA_RESPOSTA_OK  AS ANEXO_MIDIA_RESPOSTA_OK,
                   CAP.CODIGO                  AS COD_ALTERNATIVA,
                   CAP.CODIGO_CONTEXTO         AS COD_CONTEXTO_ALTERNATIVA,
                   CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
                   CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
                   CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
                   CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
                   CAP.ANEXO_MIDIA             AS ANEXO_MIDIA,
                   NULL :: BIGINT              AS COD_CARGO,
                   NULL :: BIGINT              AS COD_TIPO_VEICULO
            FROM CHECKLIST_MODELO CM
                     JOIN CHECKLIST_PERGUNTAS CP
                          ON CM.CODIGO = CP.COD_CHECKLIST_MODELO
                              AND CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CP.CODIGO = CAP.COD_PERGUNTA
                -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
                     LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                               ON CP.COD_IMAGEM = CGI.COD_IMAGEM
            WHERE CM.COD_UNIDADE = F_COD_UNIDADE
              AND CM.STATUS_ATIVO
        ),

             CHECKLIST_MODELO_CARGO AS (
                 SELECT NULL :: BIGINT           AS COD_UNIDADE_MODELO_CHECKLIST,
                        CMF.COD_CHECKLIST_MODELO AS COD_MODELO_CHECKLIST,
                        NULL :: BIGINT           AS COD_VERSAO_MODELO_CHECKLIST,
                        NULL :: TEXT             AS NOME_MODELO_CHECKLIST,
                        NULL :: BIGINT           AS COD_PERGUNTA,
                        NULL :: BIGINT           AS COD_CONTEXTO_PERGUNTA,
                        NULL :: TEXT             AS DESCRICAO_PERGUNTA,
                        NULL :: BIGINT           AS COD_IMAGEM,
                        NULL :: TEXT             AS URL_IMAGEM,
                        NULL :: INTEGER          AS PERGUNTA_ORDEM_EXIBICAO,
                        NULL :: BOOLEAN          AS SINGLE_CHOICE,
                        NULL :: TEXT             AS ANEXO_MIDIA_RESPOSTA_OK,
                        NULL :: BIGINT           AS COD_ALTERNATIVA,
                        NULL :: BIGINT           AS COD_CONTEXTO_ALTERNATIVA,
                        NULL :: TEXT             AS DESCRICAO_ALTERNATIVA,
                        NULL :: BOOLEAN          AS TIPO_OUTROS,
                        NULL :: INTEGER          AS ALTERNATIVA_ORDEM_EXIBICAO,
                        NULL :: TEXT             AS PRIORIDADE_ALTERNATIVA,
                        NULL :: TEXT             AS ANEXO_MIDIA,
                        CMF.COD_FUNCAO           AS COD_CARGO,
                        NULL :: BIGINT           AS COD_TIPO_VEICULO
                 FROM CHECKLIST_MODELO_FUNCAO CMF
                 WHERE CMF.COD_CHECKLIST_MODELO IN (SELECT CODIGO
                                                    FROM CHECKLIST_MODELO CM
                                                    WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                                      AND CM.STATUS_ATIVO = TRUE)
             ),

             CHECKLIST_MODELO_TIPO_VEICULO AS (
                 SELECT NULL :: BIGINT        AS COD_UNIDADE_MODELO_CHECKLIST,
                        CMVT.COD_MODELO       AS COD_MODELO_CHECKLIST,
                        NULL :: BIGINT        AS COD_VERSAO_MODELO_CHECKLIST,
                        NULL :: TEXT          AS NOME_MODELO_CHECKLIST,
                        NULL :: BIGINT        AS COD_PERGUNTA,
                        NULL :: BIGINT        AS COD_CONTEXTO_PERGUNTA,
                        NULL :: TEXT          AS DESCRICAO_PERGUNTA,
                        NULL :: BIGINT        AS COD_IMAGEM,
                        NULL :: TEXT          AS URL_IMAGEM,
                        NULL :: INTEGER       AS PERGUNTA_ORDEM_EXIBICAO,
                        NULL :: BOOLEAN       AS SINGLE_CHOICE,
                        NULL :: TEXT          AS ANEXO_MIDIA_RESPOSTA_OK,
                        NULL :: BIGINT        AS COD_ALTERNATIVA,
                        NULL :: BIGINT        AS COD_CONTEXTO_ALTERNATIVA,
                        NULL :: TEXT          AS DESCRICAO_ALTERNATIVA,
                        NULL :: BOOLEAN       AS TIPO_OUTROS,
                        NULL :: INTEGER       AS ALTERNATIVA_ORDEM_EXIBICAO,
                        NULL :: TEXT          AS PRIORIDADE_ALTERNATIVA,
                        NULL :: TEXT          AS ANEXO_MIDIA,
                        NULL :: BIGINT        AS COD_CARGO,
                        CMVT.COD_TIPO_VEICULO AS COD_TIPO_VEICULO
                 FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
                 WHERE CMVT.COD_MODELO IN (SELECT CODIGO
                                           FROM CHECKLIST_MODELO CM
                                           WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                             AND CM.STATUS_ATIVO = TRUE)
             ),

             CHECKLISTS_FILTRADOS AS (
                 SELECT *
                 FROM CHECKLIST_MODELO_ATIVO
                 UNION ALL
                 SELECT *
                 FROM CHECKLIST_MODELO_CARGO
                 UNION ALL
                 SELECT *
                 FROM CHECKLIST_MODELO_TIPO_VEICULO
             )

        SELECT CF.COD_UNIDADE_MODELO_CHECKLIST AS COD_UNIDADE_MODELO_CHECKLIST,
               CF.COD_MODELO_CHECKLIST         AS COD_MODELO_CHECKLIST,
               CF.COD_VERSAO_MODELO_CHECKLIST  AS COD_VERSAO_MODELO_CHECKLIST,
               CF.NOME_MODELO_CHECKLIST        AS NOME_MODELO_CHECKLIST,
               CF.COD_PERGUNTA                 AS COD_PERGUNTA,
               CF.COD_CONTEXTO_PERGUNTA        AS COD_CONTEXTO_PERGUNTA,
               CF.DESCRICAO_PERGUNTA           AS DESCRICAO_PERGUNTA,
               CF.COD_IMAGEM                   AS COD_IMAGEM,
               CF.URL_IMAGEM                   AS URL_IMAGEM,
               CF.PERGUNTA_ORDEM_EXIBICAO      AS PERGUNTA_ORDEM_EXIBICAO,
               CF.SINGLE_CHOICE                AS SINGLE_CHOICE,
               CF.ANEXO_MIDIA_RESPOSTA_OK      AS ANEXO_MIDIA_RESPOSTA_OK,
               CF.COD_ALTERNATIVA              AS COD_ALTERNATIVA,
               CF.COD_CONTEXTO_ALTERNATIVA     AS COD_CONTEXTO_ALTERNATIVA,
               CF.DESCRICAO_ALTERNATIVA        AS DESCRICAO_ALTERNATIVA,
               CF.TIPO_OUTROS                  AS TIPO_OUTROS,
               CF.ALTERNATIVA_ORDEM_EXIBICAO   AS ALTERNATIVA_ORDEM_EXIBICAO,
               CF.PRIORIDADE_ALTERNATIVA       AS PRIORIDADE_ALTERNATIVA,
               CF.ANEXO_MIDIA                  AS ANEXO_MIDIA,
               CF.COD_CARGO                    AS COD_CARGO,
               CF.COD_TIPO_VEICULO             AS COD_TIPO_VEICULO
        FROM CHECKLISTS_FILTRADOS CF
        ORDER BY CF.COD_MODELO_CHECKLIST,
                 CF.PERGUNTA_ORDEM_EXIBICAO,
                 CF.COD_PERGUNTA,
                 CF.ALTERNATIVA_ORDEM_EXIBICAO,
                 CF.COD_ALTERNATIVA,
                 CF.COD_CARGO,
                 CF.COD_TIPO_VEICULO;
END;
$$;


---- 5.

-- Para manter a estrutura de integração e apps antigos funcionando, foi necessário alterar o nome das functions
-- antigas.
-- Cria com outro nome as functions antigas.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR_DEPRECATED(F_CPF_COLABORADOR BIGINT,
                                                                                   F_DATA_INICIAL DATE,
                                                                                   F_DATA_FINAL DATE,
                                                                                   F_TIMEZONE TEXT,
                                                                                   F_LIMIT INTEGER,
                                                                                   F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                CPF_COLABORADOR               BIGINT,
                PLACA_VEICULO                 TEXT,
                TIPO_CHECKLIST                CHARACTER,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
    F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA AT TIME ZONE F_TIMEZONE                  AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               C.PLACA_VEICULO :: TEXT                              AS PLACA_VEICULO,
               C.TIPO                                               AS TIPO_CHECKLIST,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_ITENS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
        WHERE C.CPF_COLABORADOR = F_CPF_COLABORADOR
          AND (F_HAS_DATA_INICIAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL)
          AND (F_HAS_DATA_FINAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL)
        ORDER BY C.DATA_HORA DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS_DEPRECATED(F_COD_UNIDADE BIGINT,
                                                                                   F_COD_EQUIPE BIGINT,
                                                                                   F_COD_TIPO_VEICULO BIGINT,
                                                                                   F_PLACA_VEICULO CHARACTER VARYING,
                                                                                   F_DATA_INICIAL DATE,
                                                                                   F_DATA_FINAL DATE,
                                                                                   F_TIMEZONE TEXT,
                                                                                   F_LIMIT INTEGER,
                                                                                   F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                CPF_COLABORADOR               BIGINT,
                PLACA_VEICULO                 TEXT,
                TIPO_CHECKLIST                CHAR,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_EQUIPE           INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 0 ELSE 1 END;
    F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 0 ELSE 1 END;
    F_HAS_PLACA_VEICULO    INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 0 ELSE 1 END;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA AT TIME ZONE F_TIMEZONE                  AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               C.PLACA_VEICULO :: TEXT                              AS PLACA_VEICULO,
               C.TIPO                                               AS TIPO_CHECKLIST,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_ITENS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN EQUIPE E
                      ON E.CODIGO = CO.COD_EQUIPE
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
          AND C.COD_UNIDADE = F_COD_UNIDADE
          AND (F_HAS_EQUIPE = 0 OR E.CODIGO = F_COD_EQUIPE)
          AND (F_HAS_COD_TIPO_VEICULO = 0 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
          AND (F_HAS_PLACA_VEICULO = 0 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
        ORDER BY DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;

-- Dropa as functionas antigas e cria as functions novas.
-- Adiciona os totais de imagens coletadas nas functions utilizadas para listar os checklists realizados.
DROP FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(F_CPF_COLABORADOR BIGINT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE,
    F_TIMEZONE TEXT,
    F_LIMIT INTEGER,
    F_OFFSET BIGINT);
CREATE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(F_COD_COLABORADOR BIGINT,
                                                             F_DATA_INICIAL DATE,
                                                             F_DATA_FINAL DATE,
                                                             F_TIMEZONE TEXT,
                                                             F_LIMIT INTEGER,
                                                             F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                COD_COLABORADOR               BIGINT,
                CPF_COLABORADOR               BIGINT,
                NOME_COLABORADOR              TEXT,
                COD_VEICULO                   BIGINT,
                PLACA_VEICULO                 TEXT,
                IDENTIFICADOR_FROTA           TEXT,
                TIPO_CHECKLIST                CHARACTER,
                TOTAL_PERGUNTAS_OK            SMALLINT,
                TOTAL_PERGUNTAS_NOK           SMALLINT,
                TOTAL_ALTERNATIVAS_OK         SMALLINT,
                TOTAL_ALTERNATIVAS_NOK        SMALLINT,
                TOTAL_MIDIAS_PERGUNTAS_OK     SMALLINT,
                TOTAL_MIDIAS_ALTERNATIVAS_NOK SMALLINT,
                TOTAL_NOK_BAIXA               SMALLINT,
                TOTAL_NOK_ALTA                SMALLINT,
                TOTAL_NOK_CRITICA             SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                   AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               CO.CODIGO                                            AS COD_COLABORADOR,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               V.CODIGO                                             AS COD_VEICULO,
               C.PLACA_VEICULO :: TEXT                              AS PLACA_VEICULO,
               V.IDENTIFICADOR_FROTA :: TEXT                        AS IDENTIFICADOR_FROTA,
               C.TIPO                                               AS TIPO_CHECKLIST,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_PERGUNTAS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_PERGUNTAS_NOK,
               C.TOTAL_ALTERNATIVAS_OK                              AS TOTAL_ALTERNATIVAS_OK,
               C.TOTAL_ALTERNATIVAS_NOK                             AS TOTAL_ALTERNATIVAS_NOK,
               C.TOTAL_MIDIAS_PERGUNTAS_OK                          AS TOTAL_MIDIAS_PERGUNTAS_OK,
               C.TOTAL_MIDIAS_ALTERNATIVAS_NOK                      AS TOTAL_MIDIAS_ALTERNATIVAS_NOK,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'BAIXA') :: SMALLINT         AS TOTAL_BAIXA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'ALTA') :: SMALLINT          AS TOTAL_ALTA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'CRITICA') :: SMALLINT       AS TOTAL_CRITICA
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE CO.CODIGO = F_COD_COLABORADOR
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
        ORDER BY C.DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;

DROP FUNCTION IF EXISTS FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(F_COD_UNIDADE BIGINT,
    F_COD_EQUIPE BIGINT,
    F_COD_TIPO_VEICULO BIGINT,
    F_PLACA_VEICULO CHARACTER VARYING,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE,
    F_TIMEZONE TEXT,
    F_LIMIT INTEGER,
    F_OFFSET BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_EQUIPE BIGINT,
                                                                        F_COD_TIPO_VEICULO BIGINT,
                                                                        F_COD_VEICULO BIGINT,
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_TIMEZONE TEXT,
                                                                        F_LIMIT INTEGER,
                                                                        F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                COD_COLABORADOR               BIGINT,
                CPF_COLABORADOR               BIGINT,
                NOME_COLABORADOR              TEXT,
                COD_VEICULO                   BIGINT,
                PLACA_VEICULO                 TEXT,
                IDENTIFICADOR_FROTA           TEXT,
                TIPO_CHECKLIST                CHARACTER,
                TOTAL_PERGUNTAS_OK            SMALLINT,
                TOTAL_PERGUNTAS_NOK           SMALLINT,
                TOTAL_ALTERNATIVAS_OK         SMALLINT,
                TOTAL_ALTERNATIVAS_NOK        SMALLINT,
                TOTAL_MIDIAS_PERGUNTAS_OK     SMALLINT,
                TOTAL_MIDIAS_ALTERNATIVAS_NOK SMALLINT,
                TOTAL_NOK_BAIXA               SMALLINT,
                TOTAL_NOK_ALTA                SMALLINT,
                TOTAL_NOK_CRITICA             SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_EQUIPE           INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 0 ELSE 1 END;
    F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 0 ELSE 1 END;
    F_HAS_COD_VEICULO      INTEGER := CASE WHEN F_COD_VEICULO IS NULL THEN 0 ELSE 1 END;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                   AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               CO.CODIGO                                            AS COD_COLABORADOR,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               V.CODIGO                                             AS COD_VEICULO,
               V.PLACA :: TEXT                                      AS PLACA_VEICULO,
               V.IDENTIFICADOR_FROTA :: TEXT                        AS IDENTIFICADOR_FROTA,
               C.TIPO                                               AS TIPO_CHECKLIST,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_PERGUNTAS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_PERGUNTAS_NOK,
               C.TOTAL_ALTERNATIVAS_OK                              AS TOTAL_ALTERNATIVAS_OK,
               C.TOTAL_ALTERNATIVAS_NOK                             AS TOTAL_ALTERNATIVAS_NOK,
               C.TOTAL_MIDIAS_PERGUNTAS_OK                          AS TOTAL_MIDIAS_PERGUNTAS_OK,
               C.TOTAL_MIDIAS_ALTERNATIVAS_NOK                      AS TOTAL_MIDIAS_ALTERNATIVAS_NOK,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'BAIXA') :: SMALLINT         AS TOTAL_BAIXA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'ALTA') :: SMALLINT          AS TOTAL_ALTA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'CRITICA') :: SMALLINT       AS TOTAL_CRITICA
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN EQUIPE E
                      ON E.CODIGO = CO.COD_EQUIPE
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
          AND C.COD_UNIDADE = F_COD_UNIDADE
          AND (F_HAS_EQUIPE = 0 OR E.CODIGO = F_COD_EQUIPE)
          AND (F_HAS_COD_TIPO_VEICULO = 0 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
          AND (F_HAS_COD_VEICULO = 0 OR V.CODIGO = F_COD_VEICULO)
        ORDER BY DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;



----- 6.

-- Dropa e recria a function utilizada na busca de um checklist realizado por código para adicionar as urls das mídias
-- capturadas.
DROP FUNCTION FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                CPF_COLABORADOR               BIGINT,
                PLACA_VEICULO                 TEXT,
                TIPO_CHECKLIST                CHAR,
                NOME_COLABORADOR              TEXT,
                COD_PERGUNTA                  BIGINT,
                ORDEM_PERGUNTA                INTEGER,
                DESCRICAO_PERGUNTA            TEXT,
                PERGUNTA_SINGLE_CHOICE        BOOLEAN,
                COD_ALTERNATIVA               BIGINT,
                PRIORIDADE_ALTERNATIVA        TEXT,
                ORDEM_ALTERNATIVA             INTEGER,
                DESCRICAO_ALTERNATIVA         TEXT,
                ALTERNATIVA_TIPO_OUTROS       BOOLEAN,
                COD_IMAGEM                    BIGINT,
                URL_IMAGEM                    TEXT,
                ALTERNATIVA_SELECIONADA       BOOLEAN,
                RESPOSTA_OUTROS               TEXT,
                TEM_MIDIA_PERGUNTA_OK         BOOLEAN,
                UUID_MIDIA_PERGUNTA_OK        UUID,
                URL_MIDIA_PERGUNTA_OK         TEXT,
                TIPO_MIDIA_PERGUNTA_OK        TEXT,
                TEM_MIDIA_ALTERNATIVA         BOOLEAN,
                UUID_MIDIA_ALTERNATIVA        UUID,
                URL_MIDIA_ALTERNATIVA         TEXT,
                TIPO_MIDIA_ALTERNATIVA        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                                            AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                                              AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                                       AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                                  AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                                        AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                                  AS DURACAO_REALIZACAO_MILLIS,
               C.CPF_COLABORADOR                                                   AS CPF_COLABORADOR,
               C.PLACA_VEICULO :: TEXT                                             AS PLACA_VEICULO,
               C.TIPO                                                              AS TIPO_CHECKLIST,
               CO.NOME :: TEXT                                                     AS NOME_COLABORADOR,
               CP.CODIGO                                                           AS COD_PERGUNTA,
               CP.ORDEM                                                            AS ORDEM_PERGUNTA,
               CP.PERGUNTA                                                         AS DESCRICAO_PERGUNTA,
               CP.SINGLE_CHOICE                                                    AS PERGUNTA_SINGLE_CHOICE,
               CAP.CODIGO                                                          AS COD_ALTERNATIVA,
               CAP.PRIORIDADE :: TEXT                                              AS PRIORIDADE_ALTERNATIVA,
               CAP.ORDEM                                                           AS ORDEM_ALTERNATIVA,
               CAP.ALTERNATIVA                                                     AS DESCRICAO_ALTERNATIVA,
               CAP.ALTERNATIVA_TIPO_OUTROS                                         AS ALTERNATIVA_TIPO_OUTROS,
               CGI.COD_IMAGEM                                                      AS COD_IMAGEM,
               CGI.URL_IMAGEM                                                      AS URL_IMAGEM,
               CRN.CODIGO IS NOT NULL                                              AS ALTERNATIVA_SELECIONADA,
               CRN.RESPOSTA_OUTROS                                                 AS RESPOSTA_OUTROS,
               CRMPO.UUID IS NOT NULL                                              AS TEM_MIDIA_PERGUNTA_OK,
               CRMPO.UUID                                                          AS UUID_MIDIA_PERGUNTA_OK,
               CRMPO.URL_MIDIA                                                     AS URL_MIDIA_PERGUNTA_OK,
               CRMPO.TIPO_MIDIA                                                    AS TIPO_MIDIA_PERGUNTA_OK,
               CRMAN.UUID IS NOT NULL                                              AS TEM_MIDIA_ALTERNATIVA,
               CRMAN.UUID                                                          AS UUID_MIDIA_ALTERNATIVA,
               CRMAN.URL_MIDIA                                                     AS URL_MIDIA_ALTERNATIVA,
               CRMAN.TIPO_MIDIA                                                    AS TIPO_MIDIA_ALTERNATIVA
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN CHECKLIST_PERGUNTAS CP
                      ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CAP.COD_PERGUNTA = CP.CODIGO
                 LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                           ON C.CODIGO = CRN.COD_CHECKLIST
                               AND CAP.CODIGO = CRN.COD_ALTERNATIVA
                 LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                           ON CP.COD_IMAGEM = CGI.COD_IMAGEM
                 LEFT JOIN CHECKLIST_RESPOSTAS_MIDIAS_PERGUNTAS_OK CRMPO
                           ON CRMPO.COD_CHECKLIST = C.CODIGO AND CRMPO.COD_PERGUNTA = CP.CODIGO AND CRN.CODIGO IS NULL
                 LEFT JOIN CHECKLIST_RESPOSTAS_MIDIAS_ALTERNATIVAS_NOK CRMAN
                           ON CRMAN.COD_CHECKLIST = C.CODIGO AND CRMAN.COD_ALTERNATIVA = CAP.CODIGO AND
                              CRN.CODIGO IS NOT NULL
        WHERE C.CODIGO = F_COD_CHECKLIST
        ORDER BY CP.CODIGO, CAP.CODIGO;
END ;
$$;



---- 7.

-- Dropa e recria o relatório de resumo de checklists realizados para incluir os totais de midias adicionadas.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
    F_PLACA_VEICULO TEXT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
                                                                      F_PLACA_VEICULO TEXT,
                                                                      F_DATA_INICIAL DATE,
                                                                      F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                     TEXT,
                "MODELO CHECKLIST"            TEXT,
                "CÓDIGO CHECKLIST"            BIGINT,
                "DATA REALIZAÇÃO"             TEXT,
                "DATA IMPORTADO"              TEXT,
                "COLABORADOR"                 TEXT,
                "CPF"                         TEXT,
                "PLACA"                       TEXT,
                "TIPO DE VEÍCULO"             TEXT,
                "KM"                          BIGINT,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
                "TIPO"                        TEXT,
                "TOTAL DE PERGUNTAS"          SMALLINT,
                "TOTAL NOK"                   BIGINT,
                "TOTAL IMAGENS PERGUNTAS"     SMALLINT,
                "TOTAL IMAGENS ALTERNATIVAS"  SMALLINT,
                "PRIORIDADE BAIXA"            BIGINT,
                "PRIORIDADE ALTA"             BIGINT,
                "PRIORIDADE CRÍTICA"          BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                 AS NOME_UNIDADE,
       CM.NOME                                                AS NOME_MODELO,
       C.CODIGO                                               AS COD_CHECKLIST,
       FORMAT_TIMESTAMP(
               C.DATA_HORA_REALIZACAO_TZ_APLICADO,
               'DD/MM/YYYY HH24:MI')                          AS DATA_HORA_REALIZACAO,
       FORMAT_WITH_TZ(
               C.DATA_HORA_IMPORTADO_PROLOG,
               TZ_UNIDADE(C.COD_UNIDADE),
               'DD/MM/YYYY HH24:MI',
               '-')                                           AS DATA_HORA_IMPORTADO,
       CO.NOME                                                AS NOME_COLABORADOR,
       LPAD(CO.CPF :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
       C.PLACA_VEICULO                                        AS PLACA_VEICULO,
       VT.NOME                                                AS TIPO_VEICULO,
       C.KM_VEICULO                                           AS KM_VEICULO,
       C.TEMPO_REALIZACAO / 1000                              AS TEMPO_REALIZACAO_SEGUNDOS,
       F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT) AS TIPO_CHECKLIST,
       C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK           AS TOTAL_PERGUNTAS,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
        WHERE CRN.COD_CHECKLIST = C.CODIGO)                   AS TOTAL_NOK,
       COALESCE(C.TOTAL_MIDIAS_PERGUNTAS_OK, 0)::SMALLINT     AS TOTAL_MIDIAS_PERGUNTAS,
       COALESCE(C.TOTAL_MIDIAS_ALTERNATIVAS_NOK, 0)::SMALLINT AS TOTAL_MIDIAS_ALTERNATIVAS,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'BAIXA')                       AS TOTAL_BAIXA,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'ALTA')                        AS TOTAL_ALTA,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CRN.COD_ALTERNATIVA = CAP.CODIGO
        WHERE CRN.COD_CHECKLIST = C.CODIGO
          AND CAP.PRIORIDADE = 'CRITICA')                     AS TOTAL_CRITICA
FROM CHECKLIST C
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
         JOIN COLABORADOR CO
              ON C.CPF_COLABORADOR = CO.CPF
         JOIN UNIDADE U
              ON C.COD_UNIDADE = U.CODIGO
         JOIN CHECKLIST_MODELO CM ON CM.CODIGO = C.COD_CHECKLIST_MODELO
         JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
  AND (F_PLACA_VEICULO = '%' OR C.PLACA_VEICULO LIKE F_PLACA_VEICULO)
GROUP BY C.CODIGO,
         CM.NOME,
         C.TOTAL_PERGUNTAS_OK,
         C.TOTAL_MIDIAS_PERGUNTAS_OK,
         C.TOTAL_MIDIAS_ALTERNATIVAS_NOK,
         C.TOTAL_PERGUNTAS_NOK,
         U.CODIGO,
         U.NOME,
         CO.CPF,
         CO.NOME,
         CO.CPF,
         C.DATA_HORA,
         C.DATA_HORA_REALIZACAO_TZ_APLICADO,
         C.DATA_HORA_IMPORTADO_PROLOG,
         C.DATA_HORA_SINCRONIZACAO,
         C.COD_UNIDADE,
         C.PLACA_VEICULO,
         VT.NOME,
         C.KM_VEICULO,
         C.TEMPO_REALIZACAO,
         C.TIPO
ORDER BY U.NOME,
         C.DATA_HORA_SINCRONIZACAO DESC;
$$;

-- Dropa e recria o relatório de estratificação de respostas NOK para aficionar o total de midias por alternativa.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(F_COD_UNIDADES BIGINT[],
    F_PLACA_VEICULO CHARACTER VARYING,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(F_COD_UNIDADES BIGINT[],
                                                                                 F_PLACA_VEICULO CHARACTER VARYING,
                                                                                 F_DATA_INICIAL DATE,
                                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                         TEXT,
                "CODIGO CHECKLIST"                BIGINT,
                "DATA"                            CHARACTER VARYING,
                "PLACA"                           CHARACTER VARYING,
                "TIPO DE VEÍCULO"                 TEXT,
                "TIPO"                            TEXT,
                "KM"                              BIGINT,
                "NOME"                            CHARACTER VARYING,
                "PERGUNTA"                        CHARACTER VARYING,
                "ALTERNATIVA"                     CHARACTER VARYING,
                "RESPOSTA"                        CHARACTER VARYING,
                "IMAGENS ADICIONADAS ALTERNATIVA" BIGINT,
                "PRIORIDADE"                      CHARACTER VARYING,
                "PRAZO EM HORAS"                  INTEGER
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                     AS NOME_UNIDADE,
       C.CODIGO                                                                   AS COD_CHECKLIST,
       FORMAT_TIMESTAMP(C.DATA_HORA_REALIZACAO_TZ_APLICADO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_CHECK,
       C.PLACA_VEICULO                                                            AS PLACA_VEICULO,
       VT.NOME                                                                    AS TIPO_VEICULO,
       CASE
           WHEN C.TIPO = 'S'
               THEN 'Saída'
           ELSE 'Retorno' END                                                     AS TIPO_CHECKLIST,
       C.KM_VEICULO                                                               AS KM_VEICULO,
       CO.NOME                                                                    AS NOME_REALIZADOR_CHECK,
       CP.PERGUNTA                                                                AS DESCRICAO_PERGUNTA,
       CAP.ALTERNATIVA                                                            AS DESCRICAO_ALTERNATIVA,
       CRN.RESPOSTA_OUTROS                                                        AS RESPOSTA,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_MIDIAS_ALTERNATIVAS_NOK CRMAN
        WHERE C.CODIGO = CRMAN.COD_CHECKLIST
          AND CRN.COD_ALTERNATIVA = CRMAN.COD_ALTERNATIVA)                        AS TOTAL_MIDIAS_ALTERNATIVA,
       CAP.PRIORIDADE                                                             AS PRIORIDADE,
       PRIO.PRAZO                                                                 AS PRAZO
FROM CHECKLIST C
         JOIN VEICULO V
              ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CAP.COD_PERGUNTA = CP.CODIGO
         JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
              ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
         JOIN CHECKLIST_RESPOSTAS_NOK CRN
              ON C.CODIGO = CRN.COD_CHECKLIST
                  AND CRN.COD_ALTERNATIVA = CAP.CODIGO
         JOIN COLABORADOR CO
              ON CO.CPF = C.CPF_COLABORADOR
         JOIN UNIDADE U
              ON C.COD_UNIDADE = U.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA_SINCRONIZACAO DESC, C.CODIGO ASC
$$;


----- 8.


-- Dropa e recria as functions de suporte que copiam modelos de checklist entre unidades e empresas para adicionar
-- as parametrizações de mídias.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                         F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                         F_COPIAR_CARGOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         F_COPIAR_TIPOS_VEICULOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                         OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO  BIGINT;
    COD_PERGUNTA_CRIADO                   BIGINT;
    F_COD_EMPRESA                         BIGINT := (SELECT COD_EMPRESA
                                                     FROM UNIDADE
                                                     WHERE CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    PERGUNTA_MODELO_CHECKLIST_COPIADO     CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
    MODELO_VEICULO_TIPO_CHECKLIST_COPIADO CHECKLIST_MODELO_VEICULO_TIPO%ROWTYPE;
    NOME_MODELO_CHECKLIST_COPIADO         TEXT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO   BIGINT := (SELECT COD_VERSAO_ATUAL
                                                     FROM CHECKLIST_MODELO
                                                     WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    STATUS_MODELO_CHECKLIST_COPIADO       BOOLEAN;
    NOVO_COD_VERSAO_MODELO                BIGINT;
BEGIN
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA, F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CODIGO
                  FROM CHECKLIST_MODELO
                  WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DA MESMA EMPRESA.
    SELECT COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA !=
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Só é possível copiar modelos de checklists entre unidades da mesma empresa para garantirmos
            o vínculo correto de imagens da galeria.';
    END IF;

    -- Busca o nome e status do modelo copiado.
    SELECT CONCAT(CC.NOME, ' (cópia)'), CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO NOME_MODELO_CHECKLIST_COPIADO, STATUS_MODELO_CHECKLIST_COPIADO;

    -- Busca o novo código de versão do modelo de checklist
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
            NOVO_COD_VERSAO_MODELO,
            NOME_MODELO_CHECKLIST_COPIADO,
            STATUS_MODELO_CHECKLIST_COPIADO)
    RETURNING CODIGO INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            1,
            COD_MODELO_CHECKLIST_INSERIDO,
            NOW(),
            F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COPIAR_CARGOS_LIBERADOS
    THEN
        -- INSERE OS CARGOS LIBERADOS.
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
            (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    CMF.COD_FUNCAO
             FROM CHECKLIST_MODELO_FUNCAO CMF
             WHERE CMF.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO);
    END IF;

    IF F_COPIAR_TIPOS_VEICULOS_LIBERADOS
    THEN
        -- COPIA OS TIPOS DE VEÍCULO VINCULADOS.
        FOR MODELO_VEICULO_TIPO_CHECKLIST_COPIADO IN
            SELECT CMVT.COD_UNIDADE,
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
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               CP.ANEXO_MIDIA_RESPOSTA_OK
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             ANEXO_MIDIA_RESPOSTA_OK,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ANEXO_MIDIA_RESPOSTA_OK,
                    NOVO_COD_VERSAO_MODELO)
            RETURNING CODIGO INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        DEVE_ABRIR_ORDEM_SERVICO,
                                                        ANEXO_MIDIA,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        CAP.DEVE_ABRIR_ORDEM_SERVICO,
                        CAP.ANEXO_MIDIA,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END;
$$;

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST_ENTRE_EMPRESAS(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                                        F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                                        F_COD_CARGOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        F_COD_TIPOS_VEICULOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                                        OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST BIGINT := (SELECT U.COD_EMPRESA
                                                      FROM UNIDADE U
                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO   BIGINT;
    COD_PERGUNTA_CRIADO                    BIGINT;
    NOVO_COD_VERSAO_MODELO                 BIGINT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO    BIGINT := (SELECT COD_VERSAO_ATUAL
                                                      FROM CHECKLIST_MODELO
                                                      WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    PERGUNTA_MODELO_CHECKLIST_COPIADO      CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
BEGIN
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST,
                                                             F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CM.CODIGO
                  FROM CHECKLIST_MODELO CM
                  WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DE EMPRESAS DIFERENTES.
    SELECT CM.COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST =
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Essa function deve ser utilizada para copiar modelos de checklists entre empresas diferentes.
                        Utilize a function: FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST, para copiar checklists entre unidades
             da mesma empresa.';
    END IF;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS CARGOS EXISTEM.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_CARGO)
                                                                        FROM UNNEST(F_COD_CARGOS_CHECKLIST)
                                                                                 AS COD_CARGO
                                                                                 LEFT JOIN FUNCAO F
                                                                                           ON F.CODIGO = COD_CARGO
                                                                        WHERE F.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS CARGOS PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não pertence(m) a empresa para a qual você está tentando copiar o
                modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_CARGO)
                 FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                          LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                 WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO EXISTEM.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                                                                                  FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST)
                                                                                           AS COD_TIPO_VEICULO
                                                                                           LEFT JOIN VEICULO_TIPO VT
                                                                                                     ON VT.CODIGO = COD_TIPO_VEICULO
                                                                                  WHERE VT.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não pertence(m) a empresa para a qual você está tentando
                copiar o modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                 FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                          LEFT JOIN VEICULO_TIPO VT
                                    ON VT.CODIGO = COD_TIPO_VEICULO
                 WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    -- Busca o novo código de versão do modelo de checklist.
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    SELECT F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
           NOVO_COD_VERSAO_MODELO,
           CONCAT(CC.NOME, ' (cópia)'),
           CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    RETURNING CODIGO INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO.
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            1,
            COD_MODELO_CHECKLIST_INSERIDO,
            NOW(),
            F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE CARGOS QUE PODEM REALIZAR O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_FUNCAO
        FROM UNNEST(F_COD_CARGOS_CHECKLIST) CODIGO_FUNCAO;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE TIPOS DE VEÍCULOS LIBERADOS PARA O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (COD_MODELO, COD_UNIDADE, COD_TIPO_VEICULO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_TIPO_VEICULO
        FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) CODIGO_TIPO_VEICULO;
    END IF;

    -- INSERE AS PERGUNTAS E ALTERNATIVAS.
    FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               CP.ANEXO_MIDIA_RESPOSTA_OK
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             ANEXO_MIDIA_RESPOSTA_OK,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                       -- Só copiamos o código da imagem se a imagem vinculada for da galeria pública do Prolog.
                    F_IF((SELECT EXISTS(SELECT CGI.COD_IMAGEM
                                        FROM CHECKLIST_GALERIA_IMAGENS CGI
                                        WHERE CGI.COD_IMAGEM = PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM
                                          AND CGI.COD_EMPRESA IS NULL)),
                         PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                         NULL),
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ANEXO_MIDIA_RESPOSTA_OK,
                    NOVO_COD_VERSAO_MODELO)
            RETURNING CODIGO INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        DEVE_ABRIR_ORDEM_SERVICO,
                                                        ANEXO_MIDIA,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        CAP.DEVE_ABRIR_ORDEM_SERVICO,
                        CAP.ANEXO_MIDIA,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END
$$;