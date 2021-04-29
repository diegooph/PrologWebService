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