alter table checklist_alternativa_pergunta_data
    add column cod_auxiliar text null;

create or replace view checklist_alternativa_pergunta
            (cod_checklist_modelo, cod_versao_checklist_modelo, cod_unidade, alternativa, ordem, cod_pergunta, codigo,
             codigo_contexto, alternativa_tipo_outros, prioridade, deve_abrir_ordem_servico, anexo_midia, cod_auxiliar)
as
SELECT cap.cod_checklist_modelo,
       cap.cod_versao_checklist_modelo,
       cap.cod_unidade,
       cap.alternativa,
       cap.ordem,
       cap.cod_pergunta,
       cap.codigo,
       cap.codigo_contexto,
       cap.alternativa_tipo_outros,
       cap.prioridade,
       cap.deve_abrir_ordem_servico,
       cap.anexo_midia,
       cap.cod_auxiliar
FROM checklist_alternativa_pergunta_data cap
WHERE cap.deletado = false;

DROP FUNCTION IF EXISTS FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT,
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
                ANEXO_MIDIA              TEXT,
                COD_AUXILIAR_ALTERNATIVA TEXT
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
       CAP.ANEXO_MIDIA              AS ANEXO_MIDIA,
       CAP.COD_AUXILIAR :: TEXT     AS COD_AUXILIAR_ALTERNATIVA
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


DROP FUNCTION FUNC_CHECKLIST_ANALISA_MUDANCAS_MODELO(F_COD_MODELO BIGINT,
    F_COD_VERSAO_MODELO BIGINT,
    F_NOME_MODELO TEXT,
    F_COD_CARGOS BIGINT[],
    F_COD_TIPOS_VEICULOS BIGINT[],
    F_PERGUNTAS_ALTERNATIVAS_JSON JSONB);
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
        COD_AUXILIAR                  TEXT,
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
                       COD_AUXILIAR,
                       ALTERNATIVA_NOVA)
    SELECT (SRC ->> 'codigo') :: BIGINT,
           (SRC ->> 'descricao'),
           (SRC ->> 'prioridade'),
           (SRC ->> 'tipoOutros') :: BOOLEAN,
           (SRC ->> 'ordemExibicao') :: INTEGER,
           (SRC ->> 'deveAbrirOrdemServico') :: BOOLEAN,
           (SRC ->> 'anexoMidia'),
           (SRC ->> 'codAuxiliar') :: TEXT,
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
    --        * CODIGO AUXILIAR
    WITH ALTERNATIVAS_DESCRICAO_ORDEM_EXIBICAO_COD_AUXILIAR_ALTERADA AS (
        SELECT CODIGO,
               DESCRICAO,
               ORDEM_EXIBICAO,
               COD_AUXILIAR
        FROM ALTERNATIVAS
            EXCEPT
        SELECT CAP.CODIGO,
               CAP.ALTERNATIVA,
               CAP.ORDEM,
               CAP.COD_AUXILIAR
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
        WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO
          AND CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    )

    UPDATE ALTERNATIVAS A
    SET ALTERNATIVA_MUDOU = TRUE,
        MOTIVOS_MUDANCA   = array_append(MOTIVOS_MUDANCA,
                                         'Descrição (sem jaro) ou/e ordem de exibição mudaram ou/e código auxiliar - não muda contexto')
        -- CTE irá conter apenas as novas e/ou alteradas.
    FROM ALTERNATIVAS_DESCRICAO_ORDEM_EXIBICAO_COD_AUXILIAR_ALTERADA ANE
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

alter table checklist_ordem_servico_data
add constraint unique_checklist_ordem_servico_codigo_prolog unique (codigo_prolog);
create table integracao.checklist_ordem_servico_sincronizacao
(
    codigo                    bigserial not null
        constraint pk_checklist_ordem_servico_sincronizacao
            primary key,
    codigo_os_prolog          bigint    not null
        constraint unique_checklist_ordem_servico_sincronizacao_codigo_os_prolog
            unique
        constraint fk_checklist_ordem_servico_sincronizacao_codigo_os_prolog
            references checklist_ordem_servico_data (codigo_prolog),
    pendente_sincronia        boolean   not null default true,
    bloquear_sicronia         boolean   not null default false,
    quantidade_tentativas     bigint    not null default 0,
    data_ultima_tentativa     timestamp with time zone,
    mensagem_ultima_tentativa text
);



create or replace function integracao.func_checklist_insert_os_pendente(f_cod_unidade bigint,
                                                                        f_cod_os bigint)
    returns bigint
    language PLPGSQL
as
$$
declare
    v_cod_os_prolog bigint;
begin
    select into strict v_cod_os_prolog codigo_prolog
    from checklist_ordem_servico_data
    where cod_unidade = f_cod_unidade
      and codigo = f_cod_os;

    insert into integracao.checklist_ordem_servico_sincronizacao(codigo_os_prolog)
    values (v_cod_os_prolog)
    returning codigo;
end;
$$;


alter table if exists piccolotur.modelo_checklist_integrado
    set schema integracao;


alter table if exists integracao.modelo_checklist_integrado
    rename to checklist_modelo_bloqueado;


----------------FUNCTIONS-----------------------------

create or replace function integracao.func_checklist_modelo_get_modelos_bloqueados(f_cod_unidade bigint)
    returns table
            (
                cod_unidade          bigint,
                cod_modelo_checklist bigint
            )
    language plpgsql
as
$$
begin
    return query
        select a.cod_unidade,
               a.cod_modelo_checklist
        from integracao.checklist_modelo_bloqueado a
        where a.cod_unidade = f_cod_unidade;
end;
$$;



CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_CHECKLIST_INSERT_OS_PENDENTE(F_COD_UNIDADE BIGINT,
                                                                        F_COD_OS BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_OS_PROLOG BIGINT;
    V_COD_SINCRONIZACAO BIGINT;
BEGIN
    SELECT INTO STRICT V_COD_OS_PROLOG CODIGO_PROLOG
    FROM CHECKLIST_ORDEM_SERVICO_DATA
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_COD_OS;

    INSERT INTO INTEGRACAO.CHECKLIST_ORDEM_SERVICO_SINCRONIZACAO(CODIGO_OS_PROLOG)
    VALUES (V_COD_OS_PROLOG)
    RETURNING CODIGO INTO V_COD_SINCRONIZACAO;

    RETURN V_COD_SINCRONIZACAO;
END;
$$;


drop function if exists integracao.func_busca_informacoes_os(f_cod_os_prolog bigint);
create or replace function integracao.func_busca_informacoes_os(f_cod_interno_os_prolog bigint)
    returns table
            (
                cod_unidade                  bigint,
                cod_auxiliar_unidade         text,
                cod_interno_os_prolog        bigint,
                cod_os_prolog                bigint,
                data_hora_abertura_os        timestamp without time zone,
                placa_veiculo                text,
                km_veiculo_na_abertura       bigint,
                cpf_colaborador_checklist    text,
                cod_item_os                  bigint,
                cod_alternativa              bigint,
                cod_auxiliar_alternativa     text,
                descricao_alternativa        text,
                data_hora_fechamento_item_os timestamp without time zone,
                descricao_fechamento_item_os text
            )
    language plpgsql
as
$$
begin
    return query
        select cos.cod_unidade                                           as cod_unidade,
               u.cod_auxiliar                                            as cod_auxiliar_unidade,
               cos.codigo_prolog                                         as cod_interno_os_prolog,
               cos.codigo                                                as cod_os_prolog,
               c.data_hora_realizacao_tz_aplicado                        as data_hora_abertura_os,
               c.placa_veiculo::text                                     as placa_veiculo,
               c.km_veiculo                                              as km_veiculo_na_abertura,
               lpad(c.cpf_colaborador::text, 11, '0')                    as cpf_colaborador_checklist,
               cosi.codigo                                               as cod_item_os,
               cosi.cod_alternativa_primeiro_apontamento                 as cod_alternativa,
               cap.cod_auxiliar                                          as cod_auxiliar_alternativa,
               cap.alternativa                                           as descricao_alternativa,
               cosi.data_hora_conserto at time zone tz_unidade(u.codigo) as data_hora_fechamento_item_os,
               cosi.feedback_conserto                                    as descricao_fechamento_item_os
        from checklist_ordem_servico cos
                 join checklist c on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join unidade u on u.codigo = cos.cod_unidade
        where cos.codigo_prolog = f_cod_interno_os_prolog
        order by cos.codigo_prolog, cosi.codigo;
end;
$$;



drop function if exists integracao.func_busca_os_a_integrar();
create or replace function integracao.func_busca_os_a_integrar()
    returns table
            (
                codigo_interno_os_prolog bigint
            )
    language PLPGSQL
as
$$
begin
    return query
        select coss.codigo_os_prolog as codigo_os_prolog
        from integracao.checklist_ordem_servico_sincronizacao coss
        where coss.pendente_sincronia = true
          and coss.bloquear_sicronia = false;
end;
$$;



drop function if exists integracao.func_atualiza_status_os_integrada(f_cod_os_prolog bigint,
    f_sucesso boolean,
    f_error_message text);
create or replace function integracao.func_atualiza_status_os_integrada(f_cod_interno_os_prolog bigint,
                                                                        f_sincronizado_sucesso boolean,
                                                                        f_error_message text)
    returns void
    language plpgsql
as
$$
begin
    if f_sincronizado_sucesso then
        update integracao.checklist_ordem_servico_sincronizacao
        set pendente_sincronia        = false,
            quantidade_tentativas     = quantidade_tentativas + 1,
            data_ultima_tentativa     = now(),
            mensagem_ultima_tentativa = null
        where codigo_os_prolog = f_cod_interno_os_prolog;
    else
        update integracao.checklist_ordem_servico_sincronizacao
        set pendente_sincronia        = true,
            quantidade_tentativas     = quantidade_tentativas + 1,
            data_ultima_tentativa     = now(),
            mensagem_ultima_tentativa = f_if(f_error_message is not null,
                                             f_error_message,
                                             'Nenhuma mensagem de retorno do servidor integrado.')
        where codigo_os_prolog = f_cod_interno_os_prolog;
    end if;
end;
$$;



create or replace function integracao.func_checklist_insert_os_pendente(f_cod_unidade bigint,
                                                                        f_cod_os bigint)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_interno_os_prolog bigint;
begin
    select into strict v_cod_interno_os_prolog codigo_prolog
    from checklist_ordem_servico
    where cod_unidade = f_cod_unidade
      and codigo = f_cod_os;

    insert into integracao.checklist_ordem_servico_sincronizacao(codigo_os_prolog)
    values (v_cod_interno_os_prolog);

    return v_cod_interno_os_prolog;
end;
$$;

drop function if exists integracao.func_atualiza_status_os_integrada(f_cod_os_prolog bigint,
    f_sucesso boolean,
    f_error_message text);
create or replace function integracao.func_atualiza_status_os_integrada(f_cods_interno_os_prolog bigint[],
                                                                        f_pendente boolean,
                                                                        f_bloqueada boolean,
                                                                        f_incrementar_tentativas boolean,
                                                                        f_error_message text default null)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set pendente_sincronia        = f_pendente,
        quantidade_tentativas     = f_if(f_incrementar_tentativas,
                                         quantidade_tentativas + 1,
                                         quantidade_tentativas),
        bloquear_sicronia         = f_bloqueada,
        data_ultima_tentativa     = f_if(f_incrementar_tentativas, now(), data_ultima_tentativa),
        mensagem_ultima_tentativa = f_if(f_incrementar_tentativas, f_error_message, mensagem_ultima_tentativa)
    where codigo_os_prolog = any (f_cods_interno_os_prolog);
end;
$$;



drop function if exists integracao.func_atualiza_erro_os(f_cod_interno_os_prolog bigint,
    f_error_message text);
create or replace function integracao.func_atualiza_erro_os(f_cod_interno_os_prolog bigint,
                                                            f_error_message text)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set quantidade_tentativas     = quantidade_tentativas + 1,
        data_ultima_tentativa     = now(),
        mensagem_ultima_tentativa = f_if(f_error_message is not null,
                                         f_error_message,
                                         'Nenhuma resposta do servidor integrado.')
    where codigo_os_prolog = f_cod_interno_os_prolog;
end;
$$;



drop function if exists integracao.func_busca_codigo_os(f_cod_itens_os bigint);
create or replace function integracao.func_busca_codigo_os(f_cod_itens_os bigint[])
    returns table
            (
                cod_interno_os_prolog bigint
            )
    language plpgsql
as
$$
begin
    return query
        select distinct cos.codigo_prolog
        from checklist_ordem_servico_itens cosi
                 join checklist_ordem_servico cos on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
        where cosi.codigo = any (f_cod_itens_os);
end;
$$;


-- Adicionamos um coluna para logar a exception que foi responsável pela mensagem de erro logada.
alter table integracao.checklist_ordem_servico_sincronizacao
    add column exception_logada text;

drop function if exists integracao.func_atualiza_erro_os(f_cod_interno_os_prolog bigint,
    f_error_message text);
create or replace function integracao.func_atualiza_erro_os(f_cod_interno_os_prolog bigint,
                                                            f_error_message text,
                                                            f_exception_logada text)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set quantidade_tentativas     = quantidade_tentativas + 1,
        data_ultima_tentativa     = now(),
        mensagem_ultima_tentativa = f_if(f_error_message is not null,
                                         f_error_message,
                                         'Nenhuma resposta do servidor integrado.'),
        exception_logada          = f_if(f_exception_logada is not null,
                                         f_exception_logada,
                                         null)
    where codigo_os_prolog = f_cod_interno_os_prolog;
end;
$$;


create or replace function integracao.func_busca_codigo_os(f_cod_itens_os bigint[])
    returns table
            (
                cod_interno_os_prolog bigint
            )
    language plpgsql
as
$$
begin
    return query
        select distinct cos.codigo_prolog
        from checklist_ordem_servico_itens cosi
                 join checklist_ordem_servico cos
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join integracao.checklist_ordem_servico_sincronizacao coss
                      on cos.codigo_prolog = coss.codigo_os_prolog
        where cosi.codigo = any (f_cod_itens_os);
end;
$$;

drop function if exists integracao.func_atualiza_erro_os(f_cod_interno_os_prolog bigint,
    f_error_message text,
    f_exception_logada text);
create or replace function integracao.func_checklist_os_atualiza_erro_os(f_cod_interno_os_prolog bigint,
                                                                         f_error_message text,
                                                                         f_exception_logada text)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set quantidade_tentativas     = quantidade_tentativas + 1,
        data_ultima_tentativa     = now(),
        mensagem_ultima_tentativa = f_if(f_error_message is not null,
                                         f_error_message,
                                         'Nenhuma resposta do servidor integrado.'),
        exception_logada          = f_if(f_exception_logada is not null,
                                         f_exception_logada,
                                         null)
    where codigo_os_prolog = f_cod_interno_os_prolog;
end;
$$;

drop function if exists integracao.func_atualiza_status_os_integrada(f_cods_interno_os_prolog bigint[],
    f_pendente boolean,
    f_bloqueada boolean,
    f_incrementar_tentativas boolean,
    f_error_message text);
create or replace function integracao.func_checklist_os_atualiza_status_os(f_cods_interno_os_prolog bigint[],
                                                                           f_pendente boolean,
                                                                           f_bloqueada boolean,
                                                                           f_incrementar_tentativas boolean,
                                                                           f_error_message text default null)
    returns void
    language plpgsql
as
$$
begin
    update integracao.checklist_ordem_servico_sincronizacao
    set pendente_sincronia        = f_pendente,
        quantidade_tentativas     = f_if(f_incrementar_tentativas,
                                         quantidade_tentativas + 1,
                                         quantidade_tentativas),
        bloquear_sicronia         = f_bloqueada,
        data_ultima_tentativa     = f_if(f_incrementar_tentativas, now(), data_ultima_tentativa),
        mensagem_ultima_tentativa = f_if(f_incrementar_tentativas, f_error_message, mensagem_ultima_tentativa)
    where codigo_os_prolog = any (f_cods_interno_os_prolog);
end;
$$;

drop function if exists integracao.func_checklist_insert_os_pendente(f_cod_unidade bigint,
    f_cod_os bigint);
create or replace function integracao.func_checklist_os_insert_os_pendente(f_cod_unidade bigint,
                                                                           f_cod_os bigint)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_interno_os_prolog bigint;
begin
    select into strict v_cod_interno_os_prolog codigo_prolog
    from checklist_ordem_servico
    where cod_unidade = f_cod_unidade
      and codigo = f_cod_os;

    insert into integracao.checklist_ordem_servico_sincronizacao(codigo_os_prolog)
    values (v_cod_interno_os_prolog);

    return v_cod_interno_os_prolog;
end;
$$;

drop function if exists integracao.func_busca_os_a_integrar();
create or replace function integracao.func_checklist_os_busca_os_sincronizar()
    returns table
            (
                codigo_interno_os_prolog bigint
            )
    language PLPGSQL
as
$$
begin
    return query
        select coss.codigo_os_prolog as codigo_os_prolog
        from integracao.checklist_ordem_servico_sincronizacao coss
        where coss.pendente_sincronia = true
          and coss.bloquear_sicronia = false;
end;
$$;

drop function if exists integracao.func_busca_informacoes_os(f_cod_interno_os_prolog bigint);
create or replace function integracao.func_checklist_os_busca_informacoes_os(f_cod_interno_os_prolog bigint)
    returns table
            (
                cod_unidade                  bigint,
                cod_auxiliar_unidade         text,
                cod_interno_os_prolog        bigint,
                cod_os_prolog                bigint,
                data_hora_abertura_os        timestamp without time zone,
                placa_veiculo                text,
                km_veiculo_na_abertura       bigint,
                cpf_colaborador_checklist    text,
                cod_item_os                  bigint,
                cod_alternativa              bigint,
                cod_auxiliar_alternativa     text,
                descricao_alternativa        text,
                data_hora_fechamento_item_os timestamp without time zone,
                descricao_fechamento_item_os text
            )
    language plpgsql
as
$$
begin
    return query
        select cos.cod_unidade                                           as cod_unidade,
               u.cod_auxiliar                                            as cod_auxiliar_unidade,
               cos.codigo_prolog                                         as cod_interno_os_prolog,
               cos.codigo                                                as cod_os_prolog,
               c.data_hora_realizacao_tz_aplicado                        as data_hora_abertura_os,
               c.placa_veiculo::text                                     as placa_veiculo,
               c.km_veiculo                                              as km_veiculo_na_abertura,
               lpad(c.cpf_colaborador::text, 11, '0')                    as cpf_colaborador_checklist,
               cosi.codigo                                               as cod_item_os,
               cosi.cod_alternativa_primeiro_apontamento                 as cod_alternativa,
               cap.cod_auxiliar                                          as cod_auxiliar_alternativa,
               cap.alternativa                                           as descricao_alternativa,
               cosi.data_hora_conserto at time zone tz_unidade(u.codigo) as data_hora_fechamento_item_os,
               cosi.feedback_conserto                                    as descricao_fechamento_item_os
        from checklist_ordem_servico cos
                 join checklist c on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join unidade u on u.codigo = cos.cod_unidade
        where cos.codigo_prolog = f_cod_interno_os_prolog
        order by cos.codigo_prolog, cosi.codigo;
end;
$$;

drop function if exists integracao.func_busca_codigo_os(f_cod_itens_os bigint[]);
create or replace function integracao.func_checklist_os_busca_codigo_os(f_cod_itens_os bigint[])
    returns table
            (
                cod_interno_os_prolog bigint
            )
    language plpgsql
as
$$
begin
    return query
        select distinct cos.codigo_prolog
        from checklist_ordem_servico_itens cosi
                 join checklist_ordem_servico cos
                      on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                 join integracao.checklist_ordem_servico_sincronizacao coss
                      on cos.codigo_prolog = coss.codigo_os_prolog
        where cosi.codigo = any (f_cod_itens_os);
end;
$$;


create or replace function integracao.func_checklist_os_busca_oss_pendentes_sincronia(f_data_inicio date default null,
                                                                                      f_data_fim date default null)
    returns table
            (
                nome_unidade               text,
                cod_unidade                bigint,
                de_para_unidade            text,
                cod_os                     bigint,
                placa_veiculo_os           text,
                status_os                  text,
                data_hora_abertura_os      timestamp without time zone,
                data_hora_fechamento_os    timestamp without time zone,
                cod_checklist_os           bigint,
                cpf_motorista              text,
                nome_motorista             text,
                cod_item_os                bigint,
                de_para_alternativa        text,
                descricao_pergunta         text,
                descricao_alternativa      text,
                status_item_os             text,
                data_hora_resolucao_item   timestamp without time zone,
                qtd_tentativas_sincronia   bigint,
                data_hora_ultima_tentativa timestamp without time zone,
                mensagem_ultima_tentativa  text
            )
    language sql
as
$$
select u.nome::text                                                            as nome_unidade,
       u.codigo                                                                as cod_unidade,
       u.cod_auxiliar::text                                                    as de_para_unidade,
       cos.codigo                                                              as cod_os,
       c.placa_veiculo::text                                                   as placa_veiculo_os,
       f_if(cos.status = 'F', 'fechada'::text, 'aberta'::text)::text           as status_os,
       c.data_hora_realizacao_tz_aplicado                                      as data_hora_abertura_os,
       cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade)       as data_hora_fechamento_os,
       c.codigo                                                                as cod_checklist_os,
       lpad(c.cpf_colaborador::text, 11, '0')::text                            as cpf_motorista,
       co.nome::text                                                           as nome_motorista,
       cosi.codigo                                                             as cod_item_os,
       cap.cod_auxiliar::text                                                  as de_para_alternativa,
       cp.pergunta                                                             as descricao_pergunta,
       f_if(cap.alternativa_tipo_outros, crn.resposta_outros, cap.alternativa) as descricao_alternativa,
       f_if(cosi.status_resolucao = 'R', 'resolvido'::text, 'pendente'::text)  as status_item_os,
       cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade)       as data_hora_resolucao_item,
       coss.quantidade_tentativas                                              as qtd_tentativas_sincronia,
       coss.data_ultima_tentativa at time zone tz_unidade(cos.cod_unidade)     as data_hora_ultima_tentativa,
       coss.mensagem_ultima_tentativa::text                                    as mensagem_ultima_tentativa
from integracao.checklist_ordem_servico_sincronizacao coss
         join checklist_ordem_servico cos on cos.codigo_prolog = coss.codigo_os_prolog
         join checklist_ordem_servico_itens cosi on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
         join checklist_perguntas cp on cp.codigo = cosi.cod_pergunta_primeiro_apontamento
         join checklist_alternativa_pergunta cap on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
         join checklist c on c.codigo = cos.cod_checklist
         join colaborador co on c.cpf_colaborador = co.cpf
         join unidade u on cos.cod_unidade = u.codigo
         left join checklist_respostas_nok crn
                   on crn.cod_checklist = c.codigo
                       and crn.cod_pergunta = cp.codigo
                       and crn.cod_alternativa = cap.codigo
where coss.pendente_sincronia = true
  and coss.bloquear_sicronia = false
  -- Filtramos por OSs que tenham sido abertas ou fechadas nas datas filtradas.
  and ((f_if(f_data_inicio is null, true, c.data_hora_realizacao_tz_aplicado::date >= f_data_inicio)
    and f_if(f_data_fim is null, true, c.data_hora_realizacao_tz_aplicado::date <= f_data_fim))
    or (f_if(f_data_inicio is null, true,
             (cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade))::date >= f_data_inicio)
        and
        f_if(f_data_fim is null, true,
             (cos.data_hora_fechamento at time zone tz_unidade(cos.cod_unidade))::date <= f_data_fim))
    or (f_if(f_data_inicio is null, true,
             (cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade))::date >= f_data_inicio)
        and
        f_if(f_data_fim is null, true,
             (cosi.data_hora_conserto at time zone tz_unidade(cosi.cod_unidade))::date <= f_data_fim)))
order by u.codigo,
         cos.codigo, cosi.codigo;
$$;