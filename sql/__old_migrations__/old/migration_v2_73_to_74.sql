BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ##########  CRIA PERMISSÃO PARA PERMITIR ALTERAR TIPOS DE INTERVALO  ###################################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO FUNCAO_PROLOG_V11
(CODIGO, COD_PILAR, FUNCAO)
VALUES (344, 3, 'Controle de Jornada - Alterar tipo de marcação');

-- LIBERA ESSA PERMISSÃO PARA QUEM JÁ TEM PERMISSÃO DE ADICIONAR TIPOS DE INTERVALO.
INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
  SELECT
    C.COD_UNIDADE,
    C.COD_FUNCAO_COLABORADOR,
    344,
    3
  FROM CARGO_FUNCAO_PROLOG_V11 C
  WHERE C.COD_FUNCAO_PROLOG = 340;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##########  ADICIONA POSIÇÕES PARA UM QUINTO EIXO NO PROLOG  ###########################################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (511, null);
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (512, null);
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (521, null);
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (522, null);

INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (511, 9);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (512, 10);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (521, 11);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (522, 12);

UPDATE public.pneu_ordem SET ordem_exibicao = 1 WHERE posicao_prolog = 111;
UPDATE public.pneu_ordem SET ordem_exibicao = 2 WHERE posicao_prolog = 112;
UPDATE public.pneu_ordem SET ordem_exibicao = 19 WHERE posicao_prolog = 121;
UPDATE public.pneu_ordem SET ordem_exibicao = 20 WHERE posicao_prolog = 122;
UPDATE public.pneu_ordem SET ordem_exibicao = 3 WHERE posicao_prolog = 211;
UPDATE public.pneu_ordem SET ordem_exibicao = 4 WHERE posicao_prolog = 212;
UPDATE public.pneu_ordem SET ordem_exibicao = 17 WHERE posicao_prolog = 221;
UPDATE public.pneu_ordem SET ordem_exibicao = 18 WHERE posicao_prolog = 222;
UPDATE public.pneu_ordem SET ordem_exibicao = 5 WHERE posicao_prolog = 311;
UPDATE public.pneu_ordem SET ordem_exibicao = 6 WHERE posicao_prolog = 312;
UPDATE public.pneu_ordem SET ordem_exibicao = 15 WHERE posicao_prolog = 321;
UPDATE public.pneu_ordem SET ordem_exibicao = 16 WHERE posicao_prolog = 322;
UPDATE public.pneu_ordem SET ordem_exibicao = 7 WHERE posicao_prolog = 411;
UPDATE public.pneu_ordem SET ordem_exibicao = 8 WHERE posicao_prolog = 412;
UPDATE public.pneu_ordem SET ordem_exibicao = 13 WHERE posicao_prolog = 421;
UPDATE public.pneu_ordem SET ordem_exibicao = 14 WHERE posicao_prolog = 422;
UPDATE public.pneu_ordem SET ordem_exibicao = 90 WHERE posicao_prolog = 900;
UPDATE public.pneu_ordem SET ordem_exibicao = 91 WHERE posicao_prolog = 901;
UPDATE public.pneu_ordem SET ordem_exibicao = 92 WHERE posicao_prolog = 902;
UPDATE public.pneu_ordem SET ordem_exibicao = 93 WHERE posicao_prolog = 903;
UPDATE public.pneu_ordem SET ordem_exibicao = 94 WHERE posicao_prolog = 904;
UPDATE public.pneu_ordem SET ordem_exibicao = 95 WHERE posicao_prolog = 905;
UPDATE public.pneu_ordem SET ordem_exibicao = 96 WHERE posicao_prolog = 906;
UPDATE public.pneu_ordem SET ordem_exibicao = 97 WHERE posicao_prolog = 907;
UPDATE public.pneu_ordem SET ordem_exibicao = 98 WHERE posicao_prolog = 908;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##########  ADICIONA INFORMAÇÕES DE EIXOS PARA UMA CARRETA DE 4 EIXOS ##################################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO public.veiculo_diagrama_eixos (cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional) VALUES (9, 'T', 1, 4, false);
INSERT INTO public.veiculo_diagrama_eixos (cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional) VALUES (9, 'T', 2, 4, false);
INSERT INTO public.veiculo_diagrama_eixos (cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional) VALUES (9, 'T', 3, 4, false);
INSERT INTO public.veiculo_diagrama_eixos (cod_diagrama, tipo_eixo, posicao, qt_pneus, eixo_direcional) VALUES (9, 'T', 4, 4, false);
-- ########################################################################################################
-- ########################################################################################################

DROP FUNCTION FUNC_CHECKLIST_GET_FAROL_CHECKLIST(
  F_COD_UNIDADE BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE,
  F_ITENS_CRITICOS_RETROATIVOS BOOLEAN,
  F_TZ_UNIDADE TEXT);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_FAROL_CHECKLIST(
  F_COD_UNIDADE BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE,
  F_ITENS_CRITICOS_RETROATIVOS BOOLEAN,
  F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    DATA                               DATE,
    PLACA                              VARCHAR(7),
    COD_CHECKLIST_SAIDA                BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_SAIDA   TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_SAIDA         BIGINT,
    NOME_COLABORADOR_CHECKLIST_SAIDA   VARCHAR(255),
    COD_CHECKLIST_RETORNO              BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_RETORNO TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_RETORNO       BIGINT,
    NOME_COLABORADOR_CHECKLIST_RETORNO VARCHAR(255),
    CODIGO_PERGUNTA                    BIGINT,
    DESCRICAO_PERGUNTA                 TEXT,
    DESCRICAO_ALTERNATIVA              TEXT,
    ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
    DESCRICAO_ALTERNATIVA_TIPO_OUTROS  TEXT,
    CODIGO_ITEM_CRITICO                BIGINT,
    DATA_HORA_APONTAMENTO_ITEM_CRITICO TIMESTAMP WITHOUT TIME ZONE)
LANGUAGE PLPGSQL
AS
$$
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
           G.DAY::DATE                                       AS DATA,
           V.PLACA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.codigo END)                              AS COD_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.codigo END)                              AS COD_CHECKLIST_RETORNO,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_RETORNO
         FROM VEICULO V
           CROSS JOIN GENERATE_SERIES(F_DATA_INICIAL, F_DATA_FINAL, '1 DAY') G(DAY)
           LEFT JOIN CHECKLIST C
             ON C.PLACA_VEICULO = V.PLACA AND G.DAY::DATE = (C.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE
         WHERE V.COD_UNIDADE = F_COD_UNIDADE AND V.STATUS_ATIVO = TRUE
         GROUP BY 1, 2
         ORDER BY 1, 2) AS INNERTABLE
        LEFT JOIN CHECKLIST CS ON CS.CODIGO = INNERTABLE.COD_CHECKLIST_SAIDA
        LEFT JOIN CHECKLIST CR ON CR.CODIGO = INNERTABLE.COD_CHECKLIST_RETORNO
        LEFT JOIN COLABORADOR COS ON COS.CPF = CS.CPF_COLABORADOR
        LEFT JOIN COLABORADOR COR ON COR.CPF = CR.CPF_COLABORADOR
      ORDER BY INNERTABLE.DATA, INNERTABLE.PLACA
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
                  AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS
                  AND COSI.COD_ALTERNATIVA = CAP.CODIGO)
         THEN TRUE
       ELSE FALSE
       END             AS ITEM_CRITICO_DE_SAIDA_TIPO_OUTROS,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                  AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
                  AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO,
       CASE WHEN CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN COSI.CODIGO
       ELSE NULL
       END             AS CODIGO_ITEM_CRITICO,
       CASE
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO
       ELSE NULL
       END             AS DATA_HORA_APONTAMENTO_ITEM_CRITICO
     FROM ULTIMOS_CHECKLISTS_VEICULOS UCV
       LEFT JOIN CHECKLIST_ORDEM_SERVICO COS
         ON COS.COD_CHECKLIST IN (UCV.COD_CHECKLIST_SAIDA, UCV.COD_CHECKLIST_RETORNO)
            AND COS.STATUS = ORDEM_SERVICO_ABERTA
            AND COS.CODIGO IS NOT NULL
       LEFT JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
         ON COS.CODIGO = COSI.COD_OS
            AND COS.COD_UNIDADE = COSI.COD_UNIDADE
            AND COSI.STATUS_RESOLUCAO = ORDEM_SERVICO_ITEM_PENDENDTE
       LEFT JOIN CHECKLIST_PERGUNTAS CP
         ON CP.CODIGO = COSI.COD_PERGUNTA
            AND CP.PRIORIDADE IS NOT NULL
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
       LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
         ON CAP.COD_PERGUNTA = CP.CODIGO
            AND CAP.CODIGO = COSI.COD_ALTERNATIVA) AS Q
  ORDER BY Q.DATA, Q.PLACA, Q.CODIGO_PERGUNTA;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########### CRIA FUNCTION PARA BUSCAR QTD DE ITENS DE OS ABERTOS POR PLACA #############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_QTD_ITENS_OS(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_ITENS_OS_ABERTOS BOOLEAN,
  F_LIMIT            INTEGER,
  F_OFFSET           INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                    TEXT,
    KM_ATUAL_VEICULO                 BIGINT,
    TOTAL_ITENS_ABERTOS              BIGINT,
    ITENS_PRIORIDADE_CRITICA_ABERTOS BIGINT,
    ITENS_PRIORIDADE_ALTA_ABERTOS    BIGINT,
    ITENS_PRIORIDADE_BAIXA_ABERTOS   BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_ITEM_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  TIPO_ITEM_PRIORIDADE_ALTA    TEXT := 'ALTA';
  TIPO_ITEM_PRIORIDADE_BAIXA   TEXT := 'BAIXA';
  STATUS_ITEM_OS               CHAR := CASE WHEN F_ITENS_OS_ABERTOS THEN 'P' ELSE 'R' END;
  STATUS_OS                    CHAR := CASE WHEN F_ITENS_OS_ABERTOS THEN 'A' ELSE 'F' END;
BEGIN
  RETURN QUERY
  SELECT
    V.PLACA::TEXT        AS PLACA_VEICULO,
    V.KM                 AS KM_ATUAL_VEICULO,
    COUNT(CP.PRIORIDADE) AS TOTAL_ITENS_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_CRITICA
      THEN 1 END)        AS ITENS_PRIORIDADE_CRITICA_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_ALTA
      THEN 1 END)        AS ITENS_PRIORIDADE_ALTA_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_BAIXA
      THEN 1 END)        AS ITENS_PRIORIDADE_BAIXA_ABERTOS
  FROM VEICULO V
    JOIN CHECKLIST C
      ON V.PLACA = C.PLACA_VEICULO
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS
         AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON COSI.COD_PERGUNTA = CP.CODIGO
  WHERE V.COD_UNIDADE = F_COD_UNIDADE
        AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_OS
        AND COS.STATUS = STATUS_OS
        AND CASE WHEN F_COD_TIPO_VEICULO IS NOT NULL THEN V.COD_TIPO = F_COD_TIPO_VEICULO ELSE TRUE END
        AND CASE WHEN F_PLACA_VEICULO IS NOT NULL THEN V.PLACA = F_PLACA_VEICULO ELSE TRUE END
  GROUP BY V.PLACA
  ORDER BY
    ITENS_PRIORIDADE_CRITICA_ABERTOS DESC,
    ITENS_PRIORIDADE_ALTA_ABERTOS DESC,
    ITENS_PRIORIDADE_BAIXA_ABERTOS DESC,
    PLACA_VEICULO ASC
  LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########### CRIA FUNCTION PARA BUSCAR OS TIPOS DE INTERVALO DE UMA UNIDADE #############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CONTROLE_JORNADA_GET_TIPOS_INTERVALOS_UNIDADE(
  F_COD_UNIDADE   BIGINT,
  F_APENAS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO_TIPO_INTERVALO             BIGINT,
    CODIGO_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    NOME_TIPO_INTERVALO               VARCHAR(255),
    COD_UNIDADE                       BIGINT,
    ATIVO                             BOOLEAN,
    HORARIO_SUGERIDO                  TIME,
    ICONE                             VARCHAR(255),
    TEMPO_ESTOURO_MINUTOS             BIGINT,
    TEMPO_RECOMENDADO_MINUTOS         BIGINT)
LANGUAGE SQL
AS
$$
SELECT DISTINCT
  IT.CODIGO                            AS CODIGO_TIPO_INTERVALO,
  IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  IT.NOME                              AS NOME_TIPO_INTERVALO,
  IT.COD_UNIDADE,
  IT.ATIVO,
  IT.HORARIO_SUGERIDO,
  IT.ICONE,
  IT.TEMPO_ESTOURO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS
FROM INTERVALO_TIPO_CARGO ITC
  JOIN VIEW_INTERVALO_TIPO IT
    ON ITC.COD_UNIDADE = IT.COD_UNIDADE
       AND ITC.COD_TIPO_INTERVALO = IT.CODIGO
WHERE IT.COD_UNIDADE = F_COD_UNIDADE
      AND CASE WHEN F_APENAS_ATIVOS IS TRUE THEN IT.ATIVO = TRUE ELSE TRUE END
ORDER BY IT.ATIVO DESC, IT.NOME ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########### CRIA FUNCTION PARA COPIAR UM MODELO DE CHECKLIST DE UMA UNIDADE PARA OUTRA #################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(
  IN  F_COD_MODELO_CHECKLIST_COPIADO         BIGINT,
  IN  F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
  IN  F_COPIAR_CARGOS_LIBERADOS              BOOLEAN,
  OUT COD_MODELO_CHECKLIST_INSERIDO          BIGINT,
  OUT AVISO_MODELO_COPIADO                   TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_UNIDADE_MODELO_CHECKLIST_COPIADO BIGINT;
  COD_PERGUNTA_CRIADO                  BIGINT;
  PERGUNTA_MODELO_CHECKLIST_COPIADO    CHECKLIST_PERGUNTAS%ROWTYPE;
BEGIN
  SELECT 'ATENÇÃO! AINDA É PRECISO VINCULAR OS TIPOS DE VEÍCULO QUE PODEM REALIZAR ESTE MODELO DE CHECKLIST'
  INTO AVISO_MODELO_COPIADO;

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

  -- OS TIPOS DE VEÍCULO NÃO SÃO COPIADOS POIS ELES NÃO SÃO POR EMPRESA E SIM POR UNIDADE.

  -- INSERE AS PERGUNTAS E ALTERNATIVAS.
  FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
  SELECT
    CP.COD_CHECKLIST_MODELO,
    CP.COD_UNIDADE,
    CP.ORDEM,
    CP.PERGUNTA,
    CP.STATUS_ATIVO,
    CP.PRIORIDADE,
    CP.SINGLE_CHOICE,
    CP.COD_IMAGEM,
    CP.CODIGO
  FROM CHECKLIST_PERGUNTAS CP
  WHERE CP.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
  LOOP
    -- PERGUNTA.
    INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, STATUS_ATIVO, PRIORIDADE, SINGLE_CHOICE, COD_IMAGEM)
    VALUES (COD_MODELO_CHECKLIST_INSERIDO,
            F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.STATUS_ATIVO,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.PRIORIDADE,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
            PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM)
    RETURNING CODIGO
      INTO COD_PERGUNTA_CRIADO;
    -- ALTERNATIVA.
    INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO, COD_UNIDADE, ALTERNATIVA, ORDEM, STATUS_ATIVO, COD_PERGUNTA, ALTERNATIVA_TIPO_OUTROS)
      (SELECT
         COD_MODELO_CHECKLIST_INSERIDO,
         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
         CAP.ALTERNATIVA,
         CAP.ORDEM,
         CAP.STATUS_ATIVO,
         COD_PERGUNTA_CRIADO,
         CAP.ALTERNATIVA_TIPO_OUTROS
       FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
       WHERE CAP.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
             AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
  END LOOP;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################



-- ########################################################################################################
-- ########################################################################################################
-- ########### REMOVE CÓDIGO DA UNIDADE DA TABELA DE PNEU_FOTO_CADASTRO                   #################
-- ########################################################################################################
-- ########################################################################################################
-- Essa informação não é mais necessária de se existir na tabela visto que agora temos na tabela pneu a
-- informação de em qual unidade ele foi cadastrado.
ALTER TABLE PNEU_FOTO_CADASTRO DROP COLUMN COD_UNIDADE_PNEU;

-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########### REMOVE FUNCTION QUE NÃO É MAIS UTILIZADA ###################################################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION DEPRECATED_FUNC_RELATORIO_PNEU_PROBLEMAS_PLACAS_EXTRATO(F_PLACA_VEICULO TEXT);
-- ########################################################################################################
-- ########################################################################################################















-- ########################################################################################################
-- ########################################################################################################
-- ##########  ALTERA TABELA DE AFERIÇÃO PARA PERMITIR O CONCEITO DE AFERIÇÕES AVULSAS  ###################
-- ########################################################################################################
-- ########################################################################################################

-- ADICIONA COLUNA PARA DIFERENCIARMOS ENTRE UM PROCESSO DE AFERIÇÃO DE UMA PLACA E DE UM PNEU AVULSO.
ALTER TABLE AFERICAO ADD COLUMN TIPO_PROCESSO_COLETA VARCHAR(11);
UPDATE AFERICAO SET TIPO_PROCESSO_COLETA = 'PLACA';
ALTER TABLE AFERICAO ALTER COLUMN TIPO_PROCESSO_COLETA SET NOT NULL;
ALTER TABLE AFERICAO
  ADD CONSTRAINT CHECK_TIPO_PROCESSO_COLETA
CHECK (TIPO_PROCESSO_COLETA IN ('PLACA', 'PNEU_AVULSO'));

-- RENOMEIA COLUNA DA AFERIÇÃO QUE SALVA SE FOI UMA AFERIÇÃO DE SULCO, PRESSAO OU SULCO_PRESSAO.
ALTER TABLE AFERICAO RENAME TIPO_AFERICAO TO TIPO_MEDICAO_COLETADA;

-- ADICIONA CONSTRAINT PARA PERMITIR APENAS TIPO DE MEDIÇÕES DE COLETA VÁLIDAS.
ALTER TABLE AFERICAO
  ADD CONSTRAINT CHECK_TIPO_MEDICAO_COLETADA
CHECK (TIPO_MEDICAO_COLETADA IN ('SULCO',
                                 'PRESSAO',
                                 'SULCO_PRESSAO'));

-- REMOVE NOT NULL DAS COLUNAS PLACA_VEICULO E KM_VEICULO.
ALTER TABLE AFERICAO ALTER COLUMN PLACA_VEICULO DROP NOT NULL;
ALTER TABLE AFERICAO ALTER COLUMN KM_VEICULO DROP NOT NULL;

-- ADICIONA VERIFICAÇÕES ESPECÍFICAS CASO SEJA AFERIÇÃO DE UMA PLACA.
ALTER TABLE AFERICAO
  ADD CONSTRAINT CHECK_ESTADO_PLACA_KM
CHECK (
  CASE WHEN TIPO_PROCESSO_COLETA = 'PLACA'
    THEN PLACA_VEICULO IS NOT NULL AND KM_VEICULO IS NOT NULL
  WHEN TIPO_PROCESSO_COLETA = 'PNEU_AVULSO'
    THEN PLACA_VEICULO IS NULL AND KM_VEICULO IS NULL
  END);

-- INSERE NOVA PERMISSÃO PARA A REALIZAÇÃO DE AFERIÇÕES AVULSAS.
INSERT INTO FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR) VALUES (140, 'Aferição - Realizar Aferição de Pneus (avulsa)', 1);
-- ATUALIZA O NOME DA PERMISSÃO UTILIZADA PARA AFERIÇÕES DE PLACAS.
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Aferição - Realizar Aferição de Placas (cronograma)' WHERE CODIGO = 18 AND COD_PILAR = 1;

-- CRIA FUNCTION PARA BUSCAR OS PNEUS DISPONÍVEIS PARA AFERIÇÃO AVULSA NO SISTEMA
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    CODIGO BIGINT,
    CODIGO_CLIENTE TEXT,
    POSICAO_PNEU INTEGER,
    DOT TEXT,
    VALOR REAL,
    COD_UNIDADE_ALOCADO BIGINT,
    COD_REGIONAL_ALOCADO BIGINT,
    PNEU_NOVO_NUNCA_RODADO BOOLEAN,
    COD_MARCA_PNEU BIGINT,
    NOME_MARCA_PNEU TEXT,
    COD_MODELO_PNEU BIGINT,
    NOME_MODELO_PNEU TEXT,
    QT_SULCOS_MODELO_PNEU SMALLINT,
    COD_MARCA_BANDA BIGINT,
    NOME_MARCA_BANDA TEXT,
    ALTURA_SULCOS_MODELO_PNEU REAL,
    COD_MODELO_BANDA BIGINT,
    NOME_MODELO_BANDA TEXT,
    QT_SULCOS_MODELO_BANDA SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA REAL,
    VALOR_BANDA REAL,
    ALTURA INTEGER,
    LARGURA INTEGER,
    ARO REAL,
    COD_DIMENSAO BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO REAL,
    ALTURA_SULCO_EXTERNO REAL,
    PRESSAO_RECOMENDADA REAL,
    PRESSAO_ATUAL REAL,
    STATUS TEXT,
    VIDA_ATUAL INTEGER,
    VIDA_TOTAL INTEGER,
    JA_FOI_AFERIDO BOOLEAN,
    COD_ULTIMA_AFERICAO BIGINT,
    DATA_HORA_ULTIMA_AFERICAO TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO_ULTIMA_AFERICAO TEXT,
    TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO TEXT,
    TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO TEXT,
    NOME_COLABORADOR_ULTIMA_AFERICAO TEXT)
LANGUAGE SQL
AS $$
WITH AFERICOES AS (
    SELECT
      INNER_TABLE.CODIGO           AS COD_AFERICAO,
      INNER_TABLE.COD_PNEU         AS COD_PNEU,
      INNER_TABLE.DATA_HORA,
      INNER_TABLE.PLACA_VEICULO,
      INNER_TABLE.TIPO_MEDICAO_COLETADA,
      INNER_TABLE.TIPO_PROCESSO_COLETA,
      INNER_TABLE.NOME_COLABORADOR AS NOME_COLABORADOR,
      CASE WHEN INNER_TABLE.NOME_COLABORADOR IS NOT NULL
        THEN TRUE
      ELSE FALSE END               AS JA_FOI_AFERIDO
    FROM (SELECT
            A.CODIGO,
            AV.COD_PNEU,
            A.DATA_HORA,
            A.PLACA_VEICULO,
            A.TIPO_MEDICAO_COLETADA,
            A.TIPO_PROCESSO_COLETA,
            C.NOME                    AS NOME_COLABORADOR,
            MAX(A.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
            LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
            LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
            LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.COD_UNIDADE = f_cod_unidade AND P.STATUS = 'ESTOQUE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT
  F.*,
  A.JA_FOI_AFERIDO AS JA_FOI_AFERIDO,
  A.COD_AFERICAO AS COD_ULTIMA_AFERICAO,
  A.DATA_HORA AT TIME ZONE TZ_UNIDADE(f_cod_unidade) AS DATA_HORA_ULTIMA_AFERICAO,
  A.PLACA_VEICULO::TEXT AS PLACA_VEICULO_ULTIMA_AFERICAO,
  A.TIPO_MEDICAO_COLETADA::TEXT AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
  A.TIPO_PROCESSO_COLETA::TEXT AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
  A.NOME_COLABORADOR::TEXT AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEUS_GET_LISTAGEM_PNEUS_BY_STATUS(f_cod_unidade, 'ESTOQUE') AS F
  LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########## ALTERAÇÃO DE VIEWS E FUNCTIONS PARA LIDAREM COM AS ALTERAÇÕES FEITAS NA TABELA DE AFERIÇÃO ##
-- ########################################################################################################
-- ########################################################################################################
-- Primeiro remove as views.
DROP VIEW VIEW_PNEU_ANALISE_VIDA_ATUAL;
DROP VIEW VIEW_PNEU_ANALISE_VIDAS;
DROP VIEW VIEW_PNEU_KM_PERCORRIDO;

-- OK!
-- Nova view conta KM apenas das aferições que foram de placa, pois é onde acontece a coleta
-- de quilometragem.
CREATE VIEW VIEW_PNEU_KM_RODADO_VIDA AS
  SELECT
    Q.COD_PNEU       AS COD_PNEU,
    Q.VIDA_PNEU      AS VIDA_PNEU,
    -- Agora sim realizamos a soma do KM em diferentes placas na vida atual.
    SUM(Q.KM_RODADO) AS KM_RODADO_VIDA
  FROM
    (SELECT
       AV.COD_PNEU                           AS COD_PNEU,
       AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU,
       MAX(A.KM_VEICULO) - MIN(A.KM_VEICULO) AS KM_RODADO
     FROM AFERICAO_VALORES AV
       JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
     WHERE A.TIPO_PROCESSO_COLETA = 'PLACA'
     -- Precisamos que o cálculo de KM do pneu leve em conta a placa, para pegar o menor e maior KM que aquela placa teve
     -- enquanto aquele pneu esteve aplicado nela. Se não agrupassemos por placa, seria pego o menor KM de qualquer placa
     -- comparando com o maior KM de qualquer placa também.
     GROUP BY AV.COD_PNEU, A.PLACA_VEICULO, AV.VIDA_MOMENTO_AFERICAO
     ORDER BY COD_PNEU) AS Q
  GROUP BY Q.COD_PNEU, Q.VIDA_PNEU
  ORDER BY Q.COD_PNEU, Q.VIDA_PNEU;

CREATE VIEW VIEW_PNEU_KM_RODADO_TOTAL AS
  WITH KM_RODADO_TOTAL AS (
      SELECT
        COD_PNEU            AS COD_PNEU,
        SUM(KM_RODADO_VIDA) AS TOTAL_KM_RODADO_TODAS_VIDAS
      FROM VIEW_PNEU_KM_RODADO_VIDA
      GROUP BY COD_PNEU
      ORDER BY COD_PNEU)

  SELECT
    KM_VIDA.COD_PNEU                     AS COD_PNEU,
    KM_VIDA.VIDA_PNEU                    AS VIDA_PNEU,
    KM_VIDA.KM_RODADO_VIDA               AS KM_RODADO_VIDA,
    KM_TOTAL.TOTAL_KM_RODADO_TODAS_VIDAS AS TOTAL_KM_RODADO_TODAS_VIDAS
  FROM VIEW_PNEU_KM_RODADO_VIDA KM_VIDA
    JOIN KM_RODADO_TOTAL KM_TOTAL ON KM_VIDA.COD_PNEU = KM_TOTAL.COD_PNEU
  ORDER BY KM_VIDA.COD_PNEU, KM_VIDA.VIDA_PNEU;
-- ########################################################################################################
-- ########################################################################################################

-- Function utilitária
CREATE OR REPLACE FUNCTION FUNC_PNEU_CALCULA_SULCO_RESTANTE(
  F_VIDA_ATUAL_PNEU        INTEGER,
  F_VIDAS_TOTAL_PNEU       INTEGER,
  F_SULCO_1                REAL,
  F_SULCO_2                REAL,
  F_SULCO_3                REAL,
  F_SULCO_4                REAL,
  F_SULCO_MINIMO_RECAPAGEM REAL,
  F_SULCO_MINIMO_DESCARTE  REAL)
  RETURNS REAL
LANGUAGE PLPGSQL
AS $$
DECLARE
  SULCO_MININO REAL;
BEGIN
  IF F_VIDA_ATUAL_PNEU > F_VIDAS_TOTAL_PNEU
  THEN RAISE EXCEPTION 'A vida atual do pneu não pode ser maior do que o total de vidas';
  END IF;

  SULCO_MININO := CASE
                  WHEN F_VIDA_ATUAL_PNEU < F_VIDAS_TOTAL_PNEU
                    THEN F_SULCO_MINIMO_RECAPAGEM
                  WHEN F_VIDA_ATUAL_PNEU = F_VIDAS_TOTAL_PNEU
                    THEN F_SULCO_MINIMO_DESCARTE
                  END;
  RETURN LEAST(F_SULCO_1, F_SULCO_2, F_SULCO_3, F_SULCO_4) - SULCO_MININO;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- Function para trazer a primeira e última aferições de um pneu
CREATE FUNCTION FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(F_COD_PNEU BIGINT)
  RETURNS TABLE(
    COD_PNEU                               BIGINT,
    DATA_HORA_PRIMEIRA_AFERICAO            TIMESTAMP WITH TIME ZONE,
    COD_PRIMEIRA_AFERICAO                  BIGINT,
    COD_UNIDADE_PRIMEIRA_AFERICAO          BIGINT,
    TIPO_PROCESSO_COLETA_PRIMEIRA_AFERICAO TEXT,
    DATA_HORA_ULTIMA_AFERICAO              TIMESTAMP WITH TIME ZONE,
    COD_ULTIMA_AFERICAO                    BIGINT,
    COD_UNIDADE_ULTIMA_AFERICAO            BIGINT,
    TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO   TEXT,
    QUANTIDADE_AFERICOES_PNEU              INTEGER)
LANGUAGE SQL
AS $$
WITH DADOS_AFERICAO AS (
    SELECT
      A.CODIGO                      AS COD_AFERICAO,
      A.COD_UNIDADE                 AS COD_UNIDADE_AFERICAO,
      A.DATA_HORA                   AS DATA_HORA_AFERICAO,
      A.TIPO_PROCESSO_COLETA        AS TIPO_PROCESSO_COLETA_AFERICAO,
      AV.COD_PNEU                   AS COD_PNEU,
      AV.ALTURA_SULCO_CENTRAL_INTERNO,
      AV.ALTURA_SULCO_CENTRAL_EXTERNO,
      AV.ALTURA_SULCO_EXTERNO,
      AV.ALTURA_SULCO_INTERNO,
      ROW_NUMBER()
      OVER (
        PARTITION BY AV.COD_PNEU
        ORDER BY A.DATA_HORA ASC )  AS ROW_NUMBER_ASC,
      ROW_NUMBER()
      OVER (
        PARTITION BY AV.COD_PNEU
        ORDER BY A.DATA_HORA DESC ) AS ROW_NUMBER_DESC
    FROM AFERICAO A
      INNER JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
    WHERE AV.COD_PNEU = F_COD_PNEU),


    PRIMEIRA_AFERICAO AS (
      SELECT
        DA.COD_PNEU,
        DA.COD_AFERICAO,
        DA.COD_UNIDADE_AFERICAO,
        DA.TIPO_PROCESSO_COLETA_AFERICAO,
        DA.DATA_HORA_AFERICAO
      FROM DADOS_AFERICAO DA
      WHERE DA.ROW_NUMBER_ASC = 1
            AND DA.COD_PNEU = F_COD_PNEU),


    ULTIMA_AFERICAO AS (
      SELECT
        DA.COD_PNEU,
        DA.COD_AFERICAO,
        DA.COD_UNIDADE_AFERICAO,
        DA.TIPO_PROCESSO_COLETA_AFERICAO,
        DA.DATA_HORA_AFERICAO
      FROM DADOS_AFERICAO DA
      WHERE DA.ROW_NUMBER_DESC = 1
            AND DA.COD_PNEU = F_COD_PNEU),


    ANALISES_AFERICOES AS (
      SELECT
        DA.COD_PNEU,
        COUNT(COD_PNEU) :: INTEGER AS QUANTIDADE_AFERICOES_PNEU
      FROM DADOS_AFERICAO DA
      WHERE DA.COD_PNEU = F_COD_PNEU
      GROUP BY DA.COD_PNEU
  )
SELECT
  AA.COD_PNEU                      AS COD_PNEU,
  PA.DATA_HORA_AFERICAO            AS DATA_HORA_PRIMEIRA_AFERICAO,
  PA.COD_AFERICAO                  AS COD_PRIMEIRA_AFERICAO,
  PA.COD_UNIDADE_AFERICAO          AS COD_UNIDADE_PRIMEIRA_AFERICAO,
  PA.TIPO_PROCESSO_COLETA_AFERICAO AS TIPO_PROCESSO_COLETA_PRIMEIRA_AFERICAO,
  UA.DATA_HORA_AFERICAO            AS DATA_HORA_ULTIMA_AFERICAO,
  UA.COD_AFERICAO                  AS COD_ULTIMA_AFERICAO,
  UA.COD_UNIDADE_AFERICAO          AS COD_UNIDADE_ULTIMA_AFERICAO,
  UA.TIPO_PROCESSO_COLETA_AFERICAO AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
  AA.QUANTIDADE_AFERICOES_PNEU     AS QUANTIDADE_AFERICOES_PNEU
FROM
  ANALISES_AFERICOES AA
  INNER JOIN PRIMEIRA_AFERICAO PA
    ON PA.COD_PNEU = AA.COD_PNEU
  INNER JOIN ULTIMA_AFERICAO UA
    ON UA.COD_PNEU = AA.COD_PNEU
WHERE AA.COD_PNEU = F_COD_PNEU
ORDER BY AA.COD_PNEU;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- Function para trazer a primeira e última aferições de um pneu por vida
CREATE FUNCTION FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO_POR_VIDA(F_COD_PNEU BIGINT)
  RETURNS TABLE(
    COD_PNEU                       BIGINT,
    DATA_HORA_PRIMEIRA_AFERICAO    TIMESTAMP WITH TIME ZONE,
    COD_PRIMEIRA_AFERICAO          BIGINT,
    COD_UNIDADE_PRIMEIRA_AFERICAO  BIGINT,
    DATA_HORA_ULTIMA_AFERICAO      TIMESTAMP WITH TIME ZONE,
    COD_ULTIMA_AFERICAO            BIGINT,
    COD_UNIDADE_ULTIMA_AFERICAO    BIGINT,
    VIDA_ANALISADA_PNEU            INTEGER,
    QUANTIDADE_AFERICOES_PNEU_VIDA INTEGER,
    MAIOR_SULCO_AFERIDO_VIDA       REAL,
    MENOR_SULCO_AFERIDO_VIDA       REAL)
LANGUAGE SQL
AS $$
WITH DADOS_AFERICAO AS (
    SELECT
      A.CODIGO                      AS COD_AFERICAO,
      A.COD_UNIDADE                 AS COD_UNIDADE_AFERICAO,
      A.DATA_HORA                   AS DATA_HORA_AFERICAO,
      A.TIPO_PROCESSO_COLETA        AS TIPO_PROCESSO_COLETA_AFERICAO,
      AV.COD_PNEU                   AS COD_PNEU,
      AV.VIDA_MOMENTO_AFERICAO      AS VIDA_MOMENTO_AFERICAO,
      AV.ALTURA_SULCO_CENTRAL_INTERNO,
      AV.ALTURA_SULCO_CENTRAL_EXTERNO,
      AV.ALTURA_SULCO_EXTERNO,
      AV.ALTURA_SULCO_INTERNO,
      ROW_NUMBER()
      OVER (
        PARTITION BY AV.COD_PNEU, AV.VIDA_MOMENTO_AFERICAO
        ORDER BY A.DATA_HORA ASC )  AS ROW_NUMBER_ASC,
      ROW_NUMBER()
      OVER (
        PARTITION BY AV.COD_PNEU, AV.VIDA_MOMENTO_AFERICAO
        ORDER BY A.DATA_HORA DESC ) AS ROW_NUMBER_DESC
    FROM AFERICAO A
      INNER JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
    WHERE AV.COD_PNEU = F_COD_PNEU),


    PRIMEIRA_AFERICAO AS (
      SELECT
        DA.COD_PNEU,
        DA.VIDA_MOMENTO_AFERICAO,
        DA.COD_AFERICAO,
        DA.COD_UNIDADE_AFERICAO,
        DA.DATA_HORA_AFERICAO
      FROM DADOS_AFERICAO DA
      WHERE DA.ROW_NUMBER_ASC = 1
            AND DA.COD_PNEU = F_COD_PNEU),


    ULTIMA_AFERICAO AS (
      SELECT
        DA.COD_PNEU,
        DA.VIDA_MOMENTO_AFERICAO,
        DA.COD_AFERICAO,
        DA.COD_UNIDADE_AFERICAO,
        DA.DATA_HORA_AFERICAO
      FROM DADOS_AFERICAO DA
      WHERE DA.ROW_NUMBER_DESC = 1
            AND DA.COD_PNEU = F_COD_PNEU),


    ANALISES_AFERICOES AS (
      SELECT
        DA.COD_PNEU,
        DA.VIDA_MOMENTO_AFERICAO            AS VIDA_ANALISADA_PNEU,
        COUNT(COD_PNEU) :: INTEGER          AS QUANTIDADE_AFERICOES_PNEU_VIDA,
        MAX(GREATEST(ALTURA_SULCO_EXTERNO, ALTURA_SULCO_CENTRAL_EXTERNO, ALTURA_SULCO_CENTRAL_INTERNO,
                     ALTURA_SULCO_INTERNO)) AS MAIOR_SULCO_AFERIDO_VIDA,
        MIN(LEAST(ALTURA_SULCO_EXTERNO, ALTURA_SULCO_CENTRAL_EXTERNO, ALTURA_SULCO_CENTRAL_INTERNO,
                  ALTURA_SULCO_INTERNO))    AS MENOR_SULCO_AFERIDO_VIDA
      FROM DADOS_AFERICAO DA
      WHERE DA.COD_PNEU = F_COD_PNEU
      GROUP BY DA.COD_PNEU, DA.VIDA_MOMENTO_AFERICAO
  )
SELECT
  AA.COD_PNEU                       AS COD_PNEU,
  PA.DATA_HORA_AFERICAO             AS DATA_HORA_PRIMEIRA_AFERICAO,
  PA.COD_AFERICAO                   AS COD_PRIMEIRA_AFERICAO,
  PA.COD_UNIDADE_AFERICAO           AS COD_UNIDADE_PRIMEIRA_AFERICAO,
  UA.DATA_HORA_AFERICAO             AS DATA_HORA_ULTIMA_AFERICAO,
  UA.COD_AFERICAO                   AS COD_ULTIMA_AFERICAO,
  UA.COD_UNIDADE_AFERICAO           AS COD_UNIDADE_ULTIMA_AFERICAO,
  AA.VIDA_ANALISADA_PNEU            AS VIDA_ANALISADA_PNEU,
  AA.QUANTIDADE_AFERICOES_PNEU_VIDA AS QUANTIDADE_AFERICOES_PNEU_VIDA,
  AA.MAIOR_SULCO_AFERIDO_VIDA       AS MAIOR_SULCO_AFERIDO_VIDA,
  AA.MENOR_SULCO_AFERIDO_VIDA       AS MENOR_SULCO_AFERIDO_VIDA
FROM
  ANALISES_AFERICOES AA
  INNER JOIN PRIMEIRA_AFERICAO PA
    ON PA.COD_PNEU = AA.COD_PNEU
       AND PA.VIDA_MOMENTO_AFERICAO = AA.VIDA_ANALISADA_PNEU
  INNER JOIN ULTIMA_AFERICAO UA
    ON UA.COD_PNEU = AA.COD_PNEU
       AND UA.VIDA_MOMENTO_AFERICAO = AA.VIDA_ANALISADA_PNEU
WHERE AA.COD_PNEU = F_COD_PNEU
ORDER BY AA.COD_PNEU, AA.VIDA_ANALISADA_PNEU;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- OK! ESSA VIEW FOI TOTALMENTE REFEITA.
CREATE VIEW VIEW_PNEU_ANALISE_VIDAS AS
  WITH DADOS_AFERICAO AS (
      SELECT
        A.CODIGO                      AS COD_AFERICAO,
        A.COD_UNIDADE                 AS COD_UNIDADE_AFERICAO,
        A.DATA_HORA                   AS DATA_HORA_AFERICAO,
        A.TIPO_PROCESSO_COLETA        AS TIPO_PROCESSO_COLETA_AFERICAO,
        AV.COD_PNEU                   AS COD_PNEU,
        AV.VIDA_MOMENTO_AFERICAO      AS VIDA_MOMENTO_AFERICAO,
        AV.ALTURA_SULCO_CENTRAL_INTERNO,
        AV.ALTURA_SULCO_CENTRAL_EXTERNO,
        AV.ALTURA_SULCO_EXTERNO,
        AV.ALTURA_SULCO_INTERNO,
        ROW_NUMBER()
        OVER (
          PARTITION BY AV.COD_PNEU, AV.VIDA_MOMENTO_AFERICAO
          ORDER BY A.DATA_HORA ASC )  AS ROW_NUMBER_ASC,
        ROW_NUMBER()
        OVER (
          PARTITION BY AV.COD_PNEU, AV.VIDA_MOMENTO_AFERICAO
          ORDER BY A.DATA_HORA DESC ) AS ROW_NUMBER_DESC
      FROM AFERICAO A
        INNER JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO),


      PRIMEIRA_AFERICAO AS (
        SELECT
          DA.COD_PNEU,
          DA.VIDA_MOMENTO_AFERICAO,
          DA.COD_AFERICAO,
          DA.COD_UNIDADE_AFERICAO,
          DA.DATA_HORA_AFERICAO
        FROM DADOS_AFERICAO DA
        WHERE DA.ROW_NUMBER_ASC = 1),


      ULTIMA_AFERICAO AS (
        SELECT
          DA.COD_PNEU,
          DA.VIDA_MOMENTO_AFERICAO,
          DA.COD_AFERICAO,
          DA.COD_UNIDADE_AFERICAO,
          DA.DATA_HORA_AFERICAO
        FROM DADOS_AFERICAO DA
        WHERE DA.ROW_NUMBER_DESC = 1),


      ANALISES_AFERICOES AS (
        SELECT
          DA.COD_PNEU,
          DA.VIDA_MOMENTO_AFERICAO            AS VIDA_ANALISADA_PNEU,
          COUNT(COD_PNEU)                     AS QUANTIDADE_AFERICOES_PNEU_VIDA,
          MAX(GREATEST(ALTURA_SULCO_EXTERNO, ALTURA_SULCO_CENTRAL_EXTERNO, ALTURA_SULCO_CENTRAL_INTERNO,
                       ALTURA_SULCO_INTERNO)) AS MAIOR_SULCO_AFERIDO_VIDA,
          MIN(LEAST(ALTURA_SULCO_EXTERNO, ALTURA_SULCO_CENTRAL_EXTERNO, ALTURA_SULCO_CENTRAL_INTERNO,
                    ALTURA_SULCO_INTERNO))    AS MENOR_SULCO_AFERIDO_VIDA
        FROM DADOS_AFERICAO DA
        GROUP BY DA.COD_PNEU, DA.VIDA_MOMENTO_AFERICAO
    )
  SELECT
    P.CODIGO                                                                    AS COD_PNEU,
    P.STATUS                                                                    AS STATUS,
    P.VALOR                                                                     AS VALOR_PNEU,
    COALESCE(PVV.VALOR, 0)                                                      AS VALOR_BANDA,
    PA.DATA_HORA_AFERICAO                                                       AS DATA_HORA_PRIMEIRA_AFERICAO,
    PA.COD_AFERICAO                                                             AS COD_PRIMEIRA_AFERICAO,
    PA.COD_UNIDADE_AFERICAO                                                     AS COD_UNIDADE_PRIMEIRA_AFERICAO,
    UA.DATA_HORA_AFERICAO                                                       AS DATA_HORA_ULTIMA_AFERICAO,
    UA.COD_AFERICAO                                                             AS COD_ULTIMA_AFERICAO,
    UA.COD_UNIDADE_AFERICAO                                                     AS COD_UNIDADE_ULTIMA_AFERICAO,
    AA.VIDA_ANALISADA_PNEU                                                      AS VIDA_ANALISADA_PNEU,
    AA.QUANTIDADE_AFERICOES_PNEU_VIDA                                           AS QUANTIDADE_AFERICOES_PNEU_VIDA,
    AA.MAIOR_SULCO_AFERIDO_VIDA                                                 AS MAIOR_SULCO_AFERIDO_VIDA,
    AA.MENOR_SULCO_AFERIDO_VIDA                                                 AS MENOR_SULCO_AFERIDO_VIDA,
    AA.MAIOR_SULCO_AFERIDO_VIDA - AA.MENOR_SULCO_AFERIDO_VIDA                   AS SULCO_GASTO,
    EXTRACT(DAYS FROM UA.DATA_HORA_AFERICAO - PA.DATA_HORA_AFERICAO) :: INTEGER AS TOTAL_DIAS_ATIVO,
    KM_RODADO_PNEU.KM_RODADO_VIDA                                               AS TOTAL_KM_RODADO_VIDA,
    FUNC_PNEU_CALCULA_SULCO_RESTANTE(P.VIDA_ATUAL,
                                     P.VIDA_TOTAL,
                                     P.ALTURA_SULCO_EXTERNO,
                                     P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                     P.ALTURA_SULCO_CENTRAL_INTERNO,
                                     P.ALTURA_SULCO_INTERNO,
                                     PRU.SULCO_MINIMO_RECAPAGEM,
                                     PRU.SULCO_MINIMO_DESCARTE)                 AS SULCO_RESTANTE,
    CASE
    WHEN EXTRACT(DAYS FROM UA.DATA_HORA_AFERICAO - PA.DATA_HORA_AFERICAO) > 0
         AND (AA.MAIOR_SULCO_AFERIDO_VIDA - AA.MENOR_SULCO_AFERIDO_VIDA) > 0
      THEN KM_RODADO_PNEU.KM_RODADO_VIDA / (AA.MAIOR_SULCO_AFERIDO_VIDA - AA.MENOR_SULCO_AFERIDO_VIDA)
    ELSE 0
    END                                                                         AS KM_POR_MM_VIDA,
    CASE
    WHEN KM_RODADO_PNEU.KM_RODADO_VIDA = 0
      THEN 0
    ELSE
      CASE
      WHEN KM_RODADO_PNEU.VIDA_PNEU = 1
        THEN (P.VALOR / KM_RODADO_PNEU.KM_RODADO_VIDA)
      ELSE COALESCE(PVV.VALOR, 0) / KM_RODADO_PNEU.KM_RODADO_VIDA
      END
    END                                                                         AS VALOR_POR_KM_VIDA
  FROM
    ANALISES_AFERICOES AA
    INNER JOIN PRIMEIRA_AFERICAO PA
      ON PA.COD_PNEU = AA.COD_PNEU
         AND PA.VIDA_MOMENTO_AFERICAO = AA.VIDA_ANALISADA_PNEU
    INNER JOIN ULTIMA_AFERICAO UA
      ON UA.COD_PNEU = AA.COD_PNEU
         AND UA.VIDA_MOMENTO_AFERICAO = AA.VIDA_ANALISADA_PNEU
    JOIN PNEU P ON AA.COD_PNEU = P.CODIGO
    JOIN PNEU_RESTRICAO_UNIDADE PRU ON P.COD_UNIDADE = PRU.COD_UNIDADE
    LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU
    JOIN VIEW_PNEU_KM_RODADO_VIDA KM_RODADO_PNEU
      ON KM_RODADO_PNEU.COD_PNEU = AA.COD_PNEU
         AND KM_RODADO_PNEU.VIDA_PNEU = AA.VIDA_ANALISADA_PNEU
  ORDER BY AA.COD_PNEU, AA.VIDA_ANALISADA_PNEU;
-- ########################################################################################################
-- ########################################################################################################

-- OK! VIEW CORRIGIDA.
create or replace view view_pneu_analise_vida_atual as
  SELECT
    u.nome                                                                                         AS "UNIDADE ALOCADO",
    p.codigo                                                                                       AS "COD PNEU",
    p.codigo_cliente                                                                               AS "COD PNEU CLIENTE",
    (p.valor + sum(PVV.valor))                                                                     AS valor_acumulado,
    SUM(V.TOTAL_KM_RODADO_TODAS_VIDAS)                                                                  AS km_acumulado,
    p.vida_atual                                                                                   AS "VIDA ATUAL",
    p.status                                                                                       AS "STATUS PNEU",
    p.cod_unidade,
    p.valor                                                                                        AS valor_pneu,
    CASE
    WHEN (dados.VIDA_ANALISADA_PNEU = 1)
      THEN dados.valor_pneu
    ELSE dados.valor_banda
    END                                                                                            AS valor_vida_atual,
    map.nome                                                                                       AS "MARCA",
    mp.nome                                                                                        AS "MODELO",
    ((((dp.largura || '/' :: text) || dp.altura) || ' R' :: text) || dp.aro)                       AS "MEDIDAS",
    dados.QUANTIDADE_AFERICOES_PNEU_VIDA                                                           AS "QTD DE AFERIÇÕES",
    to_char((dados.DATA_HORA_PRIMEIRA_AFERICAO) :: timestamp with time zone, 'DD/MM/YYYY' :: text) AS "DTA 1a AFERIÇÃO",
    to_char((dados.DATA_HORA_ULTIMA_AFERICAO) :: timestamp with time zone,
            'DD/MM/YYYY' :: text)                                                                  AS "DTA ÚLTIMA AFERIÇÃO",
    dados.TOTAL_DIAS_ATIVO                                                                         AS "DIAS ATIVO",
    round(
        CASE
        WHEN (dados.TOTAL_DIAS_ATIVO > 0)
          THEN (dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric)
        ELSE NULL :: numeric
        END)                                                                                       AS "MÉDIA KM POR DIA",
    round((dados.MAIOR_SULCO_AFERIDO_VIDA) :: numeric,
          2)                                                                                       AS "MAIOR MEDIÇÃO VIDA",
    round((dados.MENOR_SULCO_AFERIDO_VIDA) :: numeric,
          2)                                                                                       AS "MENOR SULCO ATUAL",
    round((dados.sulco_gasto) :: numeric,
          2)                                                                                       AS "MILIMETROS GASTOS",
    round((dados.KM_POR_MM_VIDA) :: numeric,
          2)                                                                                       AS "KMS POR MILIMETRO",
    round((dados.VALOR_POR_KM_VIDA) :: numeric, 2)                                                 AS "VALOR POR KM",
    round((
            CASE
            WHEN (sum(v.TOTAL_KM_RODADO_TODAS_VIDAS) > (0) :: numeric)
              THEN ((p.valor + sum(pvv.valor)) /
                    (sum(v.TOTAL_KM_RODADO_TODAS_VIDAS)) :: double precision)
            ELSE (0) :: double precision
            END) :: numeric,
          2)                                                                                       AS "VALOR POR KM ACUMULADO",
    round(((dados.KM_POR_MM_VIDA * dados.sulco_restante)) :: numeric)                              AS "KMS A PERCORRER",
    trunc(
        CASE
        WHEN (((dados.TOTAL_KM_RODADO_VIDA > (0) :: numeric) AND (dados.TOTAL_DIAS_ATIVO > 0)) AND
              ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric) > (0) :: numeric))
          THEN ((dados.KM_POR_MM_VIDA * dados.sulco_restante) /
                ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric)) :: double precision)
        ELSE (0) :: double precision
        END)                                                                                       AS "DIAS RESTANTES",
    CASE
    WHEN (((dados.TOTAL_KM_RODADO_VIDA > (0) :: numeric) AND (dados.TOTAL_DIAS_ATIVO > 0)) AND
          ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric) > (0) :: numeric))
      THEN ((((dados.KM_POR_MM_VIDA * dados.sulco_restante) /
              ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric)) :: double precision)) :: integer +
            ('NOW' :: text) :: date)
    ELSE NULL :: date
    END                                                                                            AS "PREVISÃO DE TROCA",
    CASE
    WHEN (p.vida_atual = p.vida_total)
      THEN 'DESCARTE' :: text
    ELSE 'ANÁLISE' :: text
    END                                                                                            AS "DESTINO"
  FROM ((((((pneu p
    JOIN (SELECT
            view_pneu_analise_vidas.cod_pneu,
            view_pneu_analise_vidas.VIDA_ANALISADA_PNEU,
            view_pneu_analise_vidas.status,
            view_pneu_analise_vidas.valor_pneu,
            view_pneu_analise_vidas.valor_banda,
            view_pneu_analise_vidas.QUANTIDADE_AFERICOES_PNEU_VIDA,
            view_pneu_analise_vidas.DATA_HORA_PRIMEIRA_AFERICAO,
            view_pneu_analise_vidas.DATA_HORA_ULTIMA_AFERICAO,
            view_pneu_analise_vidas.TOTAL_DIAS_ATIVO,
            view_pneu_analise_vidas.TOTAL_KM_RODADO_VIDA,
            view_pneu_analise_vidas.MAIOR_SULCO_AFERIDO_VIDA,
            view_pneu_analise_vidas.MENOR_SULCO_AFERIDO_VIDA,
            view_pneu_analise_vidas.sulco_gasto,
            view_pneu_analise_vidas.sulco_restante,
            view_pneu_analise_vidas.KM_POR_MM_VIDA,
            view_pneu_analise_vidas.VALOR_POR_KM_VIDA
          FROM view_pneu_analise_vidas) dados
      ON (((dados.cod_pneu = p.codigo) AND (dados.VIDA_ANALISADA_PNEU = p.vida_atual))))
    JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
    JOIN unidade u ON ((u.codigo = p.cod_unidade)))
    JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
    JOIN marca_pneu map ON ((map.codigo = mp.cod_marca))))
    JOIN VIEW_PNEU_KM_RODADO_TOTAL V ON P.codigo = V.COD_PNEU AND P.vida_atual = V.VIDA_PNEU
    LEFT JOIN pneu_valor_vida PVV ON PVV.cod_pneu = P.codigo
  GROUP BY u.nome, p.codigo, p.cod_unidade, dados.valor_banda, dados.valor_pneu, map.nome, mp.nome, dp.largura,
    dp.altura, dp.aro, dados.QUANTIDADE_AFERICOES_PNEU_VIDA, dados.DATA_HORA_PRIMEIRA_AFERICAO,
    dados.DATA_HORA_ULTIMA_AFERICAO, dados.TOTAL_DIAS_ATIVO, dados.TOTAL_KM_RODADO_VIDA, dados.MAIOR_SULCO_AFERIDO_VIDA,
    dados.MENOR_SULCO_AFERIDO_VIDA, dados.sulco_gasto, dados.KM_POR_MM_VIDA, dados.VALOR_POR_KM_VIDA,
    dados.sulco_restante, dados.VIDA_ANALISADA_PNEU
  ORDER BY
    CASE
    WHEN (((dados.TOTAL_KM_RODADO_VIDA > (0) :: numeric) AND (dados.TOTAL_DIAS_ATIVO > 0)) AND
          ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric) > (0) :: numeric))
      THEN ((((dados.KM_POR_MM_VIDA * dados.sulco_restante) /
              ((dados.TOTAL_KM_RODADO_VIDA / (dados.TOTAL_DIAS_ATIVO) :: numeric)) :: double precision)) :: integer +
            ('NOW' :: text) :: date)
    ELSE NULL :: date
    END;
-- ########################################################################################################
-- ########################################################################################################

-- OK!
create or replace function func_relatorio_previsao_troca(f_data_inicial date, f_data_final date, f_cod_unidade text[], f_status_pneu character varying)
  returns TABLE(
    "UNIDADE ALOCADO" text,
    "COD PNEU" text,
    "STATUS" text,
    "VIDA ATUAL" integer,
    "MARCA" text,
    "MODELO" text,
    "MEDIDAS" text,
    "QTD DE AFERIÇÕES" bigint,
    "DATA 1ª AFERIÇÃO" text,
    "DATA ÚLTIMA AFERIÇÃO" text,
    "DIAS ATIVO" integer,
    "MÉDIA KM POR DIA" numeric,
    "MAIOR MEDIÇÃO VIDA" numeric,
    "MENOR SULCO ATUAL" numeric,
    "MILÍMETROS GASTOS" numeric,
    "KMS POR MILÍMETRO" numeric,
    "VALOR VIDA" real,
    "VALOR ACUMULADO" real,
    "VALOR POR KM VIDA ATUAL" numeric,
    "VALOR POR KM ACUMULADO" numeric,
    "KMS A PERCORRER" numeric,
    "DIAS RESTANTES" double precision,
    "PREVISÃO DE TROCA" text,
    "DESTINO" text)
language sql
as $$
SELECT
  VAP."UNIDADE ALOCADO",
  VAP."COD PNEU CLIENTE",
  VAP."STATUS PNEU",
  VAP."VIDA ATUAL",
  VAP."MARCA",
  VAP."MODELO",
  VAP."MEDIDAS",
  VAP."QTD DE AFERIÇÕES",
  VAP."DTA 1a AFERIÇÃO",
  VAP."DTA ÚLTIMA AFERIÇÃO",
  VAP."DIAS ATIVO",
  VAP."MÉDIA KM POR DIA",
  VAP."MAIOR MEDIÇÃO VIDA",
  VAP."MENOR SULCO ATUAL",
  VAP."MILIMETROS GASTOS",
  VAP."KMS POR MILIMETRO",
  VAP.VALOR_VIDA_ATUAL,
  VAP.VALOR_ACUMULADO,
  VAP."VALOR POR KM",
  VAP."VALOR POR KM ACUMULADO" ,
  VAP."KMS A PERCORRER",
  VAP."DIAS RESTANTES",
  TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'),
  VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL AS VAP
WHERE VAP.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND VAP."PREVISÃO DE TROCA" BETWEEN f_data_inicial AND f_data_final
      AND VAP."STATUS PNEU" LIKE f_status_pneu
ORDER BY VAP."UNIDADE ALOCADO";
$$;
-- ########################################################################################################
-- ########################################################################################################

-- OK!
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEUS_DESCARTADOS(F_COD_UNIDADE TEXT [], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "UNIDADE DO DESCARTE"          TEXT,
    "RESPONSÁVEL PELO DESCARTE"    TEXT,
    "DATA/HORA DO DESCARTE"        TEXT,
    "CÓDIGO DO PNEU"               TEXT,
    "MARCA DO PNEU"                TEXT,
    "MODELO DO PNEU"               TEXT,
    "MARCA DA BANDA"               TEXT,
    "MODELO DA BANDA"              TEXT,
    "DIMENSÃO DO PNEU"             TEXT,
    "ÚLTIMA PRESSÃO"               NUMERIC,
    "TOTAL DE VIDAS"               INTEGER,
    "ALTURA SULCO INTERNO"         NUMERIC,
    "ALTURA SULCO CENTRAL INTERNO" NUMERIC,
    "ALTURA SULCO CENTRAL EXTERNO" NUMERIC,
    "ALTURA SULCO EXTERNO"         NUMERIC,
    "DOT"                          TEXT,
    "MOTIVO DO DESCARTE"           TEXT,
    "FOTO 1"                       TEXT,
    "FOTO 2"                       TEXT,
    "FOTO 3"                       TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                          AS UNIDADE_DO_DESCARTE,
  C.NOME                                                                          AS RESPONSAVEL_PELO_DESCARTE,
  TO_CHAR(MP.DATA_HORA AT TIME ZONE tz_unidade(P.COD_UNIDADE),
          'DD/MM/YYYY HH24:MI')                                                   AS DATA_HORA_DESCARTE,
  P.CODIGO_CLIENTE                                                                AS CODIGO_PNEU,
  MAP.NOME                                                                        AS MARCA_PNEU,
  MOP.NOME                                                                        AS MODELO_PNEU,
  MAB.NOME                                                                        AS MARCA_BANDA,
  MOB.NOME                                                                        AS MODELO_BANDA,
  'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' || DP.ARO AS DIMENSAO_PNEU,
  ROUND(P.PRESSAO_ATUAL :: NUMERIC, 2)                                            AS ULTIMA_PRESSAO,
  P.VIDA_ATUAL                                                                    AS TOTAL_VIDAS,
  ROUND(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2)                                     AS ALTURA_SULCO_INTERNO,
  ROUND(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2)                             AS ALTURA_SULCO_CENTRAL_INTERNO,
  ROUND(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2)                             AS ALTURA_SULCO_CENTRAL_EXTERNO,
  ROUND(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2)                                     AS ALTURA_SULCO_EXTERNO,
  P.DOT                                                                           AS DOT,
  MMDE.MOTIVO                                                                     AS MOTIVO_DESCARTE,
  MD.URL_IMAGEM_DESCARTE_1                                                        AS FOTO_1,
  MD.URL_IMAGEM_DESCARTE_2                                                        AS FOTO_2,
  MD.URL_IMAGEM_DESCARTE_3                                                        AS FOTO_3
FROM PNEU P
  JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
  JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
  LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
  LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
  LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
  LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
  LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
  LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
  LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
    ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
      AND P.STATUS = 'DESCARTE'
      AND M.COD_PNEU = P.CODIGO
      AND MD.TIPO_DESTINO = 'DESCARTE'
      AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE >= f_data_inicial
      AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE <= f_data_final
ORDER BY U.NOME;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- OK!
-- Já colocado na pasta functions
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_ADERENCIA_AFERICAO(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "UNIDADE ALOCADA" TEXT,
    "PLACA" CHARACTER VARYING,
    "QT AFERIÇÕES DE PRESSÃO" BIGINT,
    "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
    "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META" BIGINT,
    "ADERÊNCIA AFERIÇÕES DE PRESSÃO" TEXT,
    "QT AFERIÇÕES DE SULCO" BIGINT,
    "MAX DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "MIN DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO" TEXT,
    "QTD AFERIÇÕES DE SULCO DENTRO DA META" BIGINT,
    "ADERÊNCIA AFERIÇÕES DE SULCO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME  AS "UNIDADE ALOCADA",
  V.PLACA AS PLACA,
  COALESCE(CALCULO_PRESSAO.QTD_AFERICOES, 0),
  COALESCE(CALCULO_PRESSAO.MAX_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.MIN_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.MD_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_PRESSAO.QTD_AFERICOES_DENTRO_META, 0),
  COALESCE(CALCULO_PRESSAO.ADERENCIA, '0'),
  COALESCE(CALCULO_SULCO.QTD_AFERICOES, 0),
  COALESCE(CALCULO_SULCO.MAX_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.MIN_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.MD_DIAS_ENTRE_AFERICOES, '0'),
  COALESCE(CALCULO_SULCO.QTD_AFERICOES_DENTRO_META, 0),
  COALESCE(CALCULO_SULCO.ADERENCIA, '0')
FROM VEICULO V
  JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
  LEFT JOIN (SELECT
               CALCULO_AFERICAO_PRESSAO.PLACA,
               COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)                              AS QTD_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                       AS MAX_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                       AS MIN_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN TRUNC(
                     CASE WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                       THEN
                         SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
                         SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
                           THEN 1 ELSE 0 END)
                     END)::TEXT
               ELSE '-' END                                                       AS MD_DIAS_ENTRE_AFERICOES,
               SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END)                                               AS QTD_AFERICOES_DENTRO_META,
               TRUNC(SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END) / COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)::NUMERIC * 100) || '%' AS ADERENCIA
             FROM
               (SELECT
                  A.PLACA_VEICULO            AS PLACA,
                  A.DATA_HORA,
                  A.TIPO_MEDICAO_COLETADA,
                  R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                  CASE WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                    THEN EXTRACT(DAYS FROM A.DATA_HORA - LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                  END                        AS DIAS_ENTRE_AFERICOES
                FROM AFERICAO A
                  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
                  JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >= (f_data_inicial)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (f_data_final)
                      AND (A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                      AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
             GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO
    ON CALCULO_PRESSAO.PLACA = V.PLACA
  LEFT JOIN (SELECT
               CALCULO_AFERICAO_SULCO.PLACA,
               COUNT(CALCULO_AFERICAO_SULCO.PLACA)                              AS QTD_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                     AS MAX_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
               ELSE '-' END                                                     AS MIN_DIAS_ENTRE_AFERICOES,
               CASE WHEN
                 MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                 THEN TRUNC(
                     CASE WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                       THEN
                         SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
                         SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
                           THEN 1 ELSE 0 END)
                     END) :: TEXT
               ELSE '-' END                                                     AS MD_DIAS_ENTRE_AFERICOES,
               SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END)                                                  AS QTD_AFERICOES_DENTRO_META,
               TRUNC(SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                 THEN 1 ELSE 0 END) / COUNT(CALCULO_AFERICAO_SULCO.PLACA)::NUMERIC * 100) || '%' AS ADERENCIA
             FROM
               (SELECT
                  A.PLACA_VEICULO            AS PLACA,
                  A.DATA_HORA,
                  A.TIPO_MEDICAO_COLETADA,
                  R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                  CASE WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                    THEN EXTRACT(DAYS FROM A.DATA_HORA - LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                  ELSE 0
                  END                        AS DIAS_ENTRE_AFERICOES
                FROM AFERICAO A
                  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
                  JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                WHERE V.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >= (f_data_inicial)
                      AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (f_data_final)
                      AND (A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                      AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
             GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
    ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
      AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- OK!
-- Já colocado na pasta functions.
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE TEXT[]);
CREATE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE TEXT[])
  RETURNS TABLE(
    "UNIDADE ALOCADO" TEXT,
    "PNEU" TEXT,
    "MARCA PNEU" TEXT,
    "MODELO PNEU" TEXT,
    "MEDIDAS" TEXT,
    "PLACA APLICADO" TEXT,
    "MARCA VEÍCULO" TEXT,
    "MODELO VEÍCULO" TEXT,
    "TIPO VEÍCULO" TEXT,
    "POSIÇÃO APLICADO" TEXT,
    "SULCO INTERNO" TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO" TEXT,
    "PRESSÃO (PSI)" TEXT,
    "VIDA ATUAL" TEXT,
    "DOT" TEXT,
    "ÚLTIMA AFERIÇÃO" TEXT,
    "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                AS UNIDADE_ALOCADO,
  P.CODIGO_CLIENTE                                                                      AS COD_PNEU,
  MAP.NOME                                                                              AS NOME_MARCA,
  MP.NOME                                                                               AS NOME_MODELO,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
   DP.ARO)                                                                              AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU,
           '-')                                                                         AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_MARCA, '-')                                     AS MARCA_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_MODELO, '-')                                    AS MODELO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                      AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                      AS POSICAO_PNEU,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                      AS PRESSAO_ATUAL,
  P.VIDA_ATUAL :: TEXT                                                                  AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                                                                  AS DOT,
  COALESCE(TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE tz_unidade(F.COD_UNIDADE_ULTIMA_AFERICAO),
                   'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                              AS ULTIMA_AFERICAO,
  CASE
  WHEN F.TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO IS NULL
    THEN 'Nunca Aferido'
  WHEN F.TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO = 'PLACA'
    THEN 'Aferido em uma placa'
  ELSE 'Aferido Avulso (em estoque)' END                                                AS TIPO_PROCESSO_ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO,
               MOV.NOME         AS VEICULO_MODELO,
               MAV.NOME         AS VEICULO_MARCA
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_UNIDADE = VT.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO
               JOIN MODELO_VEICULO MOV
                 ON V.COD_MODELO = MOV.CODIGO
               JOIN MARCA_VEICULO MAV
                 ON MOV.COD_MARCA = MAV.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- OK!
-- Já colocado na pasta functions.
DROP FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(F_COD_UNIDADE TEXT [], F_STATUS_PNEU TEXT);
CREATE FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(F_COD_UNIDADE TEXT [], F_STATUS_PNEU TEXT)
  RETURNS TABLE(
    "UNIDADE ALOCADO"       TEXT,
    "PNEU"                  TEXT,
    "STATUS"                TEXT,
    "VALOR DE AQUISIÇÃO"    TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
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
    "ÚLTIMA AFERIÇÃO"       TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                AS UNIDADE_ALOCADO,
  P.CODIGO_CLIENTE                                                                      AS COD_PNEU,
  P.STATUS                                                                              AS STATUS,
  CASE WHEN P.VALOR IS NULL
    THEN '-'
  ELSE P.VALOR :: TEXT END                                                              AS VALOR,
  MAP.NOME                                                                              AS NOME_MARCA_PNEU,
  MP.NOME                                                                               AS NOME_MODELO_PNEU,
  CASE WHEN MARB.CODIGO IS NULL
    THEN 'Nunca Recapado'
  ELSE MARB.NOME || ' - ' || MODB.NOME
  END                                                                                   AS BANDA_APLICADA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)              AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                      AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                      AS POSICAO_PNEU,
  COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                                        AS QTD_SULCOS,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                          AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',') AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                         AS PRESSAO_ATUAL,
  P.PRESSAO_RECOMENDADA :: TEXT                                                         AS PRESSAO_RECOMENDADA,
  PVN.NOME :: TEXT                                                                      AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                                                                  AS DOT,
  COALESCE(
      TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_ULTIMA_AFERICAO),
              'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                                   AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_UNIDADE = VT.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F
    ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
      AND CASE
          WHEN f_status_pneu IS NULL
            THEN TRUE
          ELSE P.STATUS = f_status_pneu
          END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##################### CRIA NOVAS FUNCTIONS PARA AS BUSCAS DE AFERIÇÕES E TUDO RELACIONADO ##############
-- ########################################################################################################
-- ########################################################################################################
-- CRIA FUNCTION PARA BUSCAR AS AFERIÇÕES DE PLACAS NO SISTEMA DE FORMA PAGINADA.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_LIMIT            BIGINT,
  F_OFFSET           BIGINT,
  F_TZ_UNIDADE       TEXT)
  RETURNS TABLE(
    KM_VEICULO            BIGINT,
    COD_AFERICAO          BIGINT,
    COD_UNIDADE           BIGINT,
    DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO         TEXT,
    TIPO_MEDICAO_COLETADA TEXT,
    TIPO_PROCESSO_COLETA  TEXT,
    CPF                   TEXT,
    NOME                  TEXT,
    TEMPO_REALIZACAO      BIGINT)
LANGUAGE SQL
AS $$
SELECT
  A.KM_VEICULO,
  A.CODIGO                              AS COD_AFERICAO,
  A.COD_UNIDADE                         AS COD_UNIDADE,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
  A.PLACA_VEICULO :: TEXT,
  A.TIPO_MEDICAO_COLETADA :: TEXT,
  A.TIPO_PROCESSO_COLETA :: TEXT,
  C.CPF :: TEXT,
  C.NOME :: TEXT,
  A.TEMPO_REALIZACAO
FROM AFERICAO A
  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
      AND CASE WHEN F_COD_TIPO_VEICULO IS NOT NULL
  THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
      AND CASE WHEN F_PLACA_VEICULO IS NOT NULL
  THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
      AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT
OFFSET F_OFFSET;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR AS AFERIÇÕES AVULSAS NO SISTEMA DE FORMA PAGINADA.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(
  F_COD_UNIDADE  BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE,
  F_LIMIT        BIGINT,
  F_OFFSET       BIGINT,
  F_TZ_UNIDADE   TEXT)
  RETURNS TABLE(
    KM_VEICULO            BIGINT,
    COD_AFERICAO          BIGINT,
    COD_UNIDADE           BIGINT,
    DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO         TEXT,
    TIPO_MEDICAO_COLETADA TEXT,
    TIPO_PROCESSO_COLETA  TEXT,
    CPF                   TEXT,
    NOME                  TEXT,
    TEMPO_REALIZACAO      BIGINT)
LANGUAGE SQL
AS $$
SELECT
  A.KM_VEICULO,
  A.CODIGO                              AS COD_AFERICAO,
  A.COD_UNIDADE                         AS COD_UNIDADE,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
  A.PLACA_VEICULO :: TEXT,
  A.TIPO_MEDICAO_COLETADA :: TEXT,
  A.TIPO_PROCESSO_COLETA :: TEXT,
  C.CPF :: TEXT,
  C.NOME :: TEXT,
  A.TEMPO_REALIZACAO
FROM AFERICAO A
  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
      AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT
OFFSET F_OFFSET;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR O CRONOGRAMA DAS AFERIÇÕES DE PLACAS.
CREATE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(
  F_COD_UNIDADE                BIGINT,
  F_DATA_HORA_ATUAL_TZ_CLIENTE TIMESTAMP WITHOUT TIME ZONE,
  F_TZ_UNIDADE                 TEXT)
  RETURNS TABLE(
    PLACA                     TEXT,
    NOME                      TEXT,
    INTERVALO_PRESSAO         INTEGER,
    INTERVALO_SULCO           INTEGER,
    PNEUS_APLICADOS           INTEGER,
    STATUS_ATIVO              BOOLEAN,
    PODE_AFERIR_SULCO         BOOLEAN,
    PODE_AFERIR_PRESSAO       BOOLEAN,
    PODE_AFERIR_SULCO_PRESSAO BOOLEAN,
    PODE_AFERIR_ESTEPE        BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  V.PLACA :: TEXT,
  M.NOME :: TEXT,
  COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER AS INTERVALO_PRESSAO,
  COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER   AS INTERVALO_SULCO,
  COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER           AS PNEUS_APLICADOS,
  VCTA.STATUS_ATIVO,
  VCTA.PODE_AFERIR_SULCO,
  VCTA.PODE_AFERIR_PRESSAO,
  VCTA.PODE_AFERIR_SULCO_PRESSAO,
  VCTA.PODE_AFERIR_ESTEPE
FROM VEICULO V
  JOIN MODELO_VEICULO M ON M.CODIGO = V.COD_MODELO
  JOIN VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO VCTA ON VCTA.COD_TIPO_VEICULO = V.COD_TIPO
  LEFT JOIN
  (SELECT
     PLACA_VEICULO                                                                                AS PLACA_INTERVALO,
     EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_TZ_CLIENTE) - MAX(DATA_HORA AT TIME ZONE F_TZ_UNIDADE)) AS INTERVALO
   FROM AFERICAO
   WHERE TIPO_MEDICAO_COLETADA = 'PRESSAO' OR TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
   GROUP BY PLACA_VEICULO) AS INTERVALO_PRESSAO ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA
  LEFT JOIN
  (SELECT
     PLACA_VEICULO                                                                                AS PLACA_INTERVALO,
     EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_TZ_CLIENTE) - MAX(DATA_HORA AT TIME ZONE F_TZ_UNIDADE)) AS INTERVALO
   FROM AFERICAO
   WHERE TIPO_MEDICAO_COLETADA = 'SULCO' OR TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
   GROUP BY PLACA_VEICULO) AS INTERVALO_SULCO ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA
  LEFT JOIN
  (SELECT
     VP.PLACA           AS PLACA_PNEUS,
     COUNT(VP.COD_PNEU) AS TOTAL
   FROM VEICULO_PNEU VP
   WHERE COD_UNIDADE = F_COD_UNIDADE
   GROUP BY 1) AS NUMERO_PNEUS ON PLACA_PNEUS = V.PLACA
WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = F_COD_UNIDADE
ORDER BY M.NOME ASC, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR UMA AFERIÇÃO ESPECÍFICA PELO SEU CÓDIGO.
CREATE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(
  F_COD_UNIDADE  BIGINT,
  F_COD_AFERICAO BIGINT,
  F_TZ_UNIDADE   TEXT)
  RETURNS TABLE(
    COD_AFERICAO                 BIGINT,
    COD_UNIDADE                  BIGINT,
    DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO                TEXT,
    KM_VEICULO                   BIGINT,
    TEMPO_REALIZACAO             BIGINT,
    TIPO_PROCESSO_COLETA         TEXT,
    TIPO_MEDICAO_COLETADA        TEXT,
    CPF                          TEXT,
    NOME                         TEXT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    ALTURA_SULCO_INTERNO         REAL,
    PRESSAO_PNEU                 INTEGER,
    POSICAO_PNEU                 INTEGER,
    CODIGO_PNEU                  BIGINT,
    CODIGO_PNEU_CLIENTE          TEXT,
    PRESSAO_RECOMENDADA          REAL)
LANGUAGE SQL
AS $$
SELECT
  A.CODIGO                              AS COD_AFERICAO,
  A.COD_UNIDADE                         AS COD_UNIDADE,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
  A.PLACA_VEICULO :: TEXT,
  A.KM_VEICULO,
  A.TEMPO_REALIZACAO,
  A.TIPO_PROCESSO_COLETA :: TEXT,
  A.TIPO_MEDICAO_COLETADA :: TEXT,
  C.CPF :: TEXT,
  C.NOME :: TEXT,
  AV.ALTURA_SULCO_CENTRAL_INTERNO,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO,
  AV.ALTURA_SULCO_EXTERNO,
  AV.ALTURA_SULCO_INTERNO,
  AV.PSI :: INT                         AS PRESSAO_PNEU,
  AV.POSICAO                            AS POSICAO_PNEU,
  P.CODIGO                              AS CODIGO_PNEU,
  P.CODIGO_CLIENTE :: TEXT              AS CODIGO_PNEU_CLIENTE,
  P.PRESSAO_RECOMENDADA
FROM AFERICAO A
  JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
  JOIN PNEU_ORDEM PO ON AV.POSICAO = PO.POSICAO_PROLOG
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
  JOIN MODELO_PNEU MO ON MO.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MO.COD_MARCA
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR UM PNEU PELO SEU CÓDIGO.
CREATE FUNCTION FUNC_PNEUS_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT)
  RETURNS TABLE(
    CODIGO                       BIGINT,
    CODIGO_CLIENTE               TEXT,
    POSICAO_PNEU                 INTEGER,
    DOT                          TEXT,
    VALOR                        REAL,
    COD_UNIDADE_ALOCADO          BIGINT,
    COD_REGIONAL_ALOCADO         BIGINT,
    PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
    COD_MARCA_PNEU               BIGINT,
    NOME_MARCA_PNEU              TEXT,
    COD_MODELO_PNEU              BIGINT,
    NOME_MODELO_PNEU             TEXT,
    QT_SULCOS_MODELO_PNEU        SMALLINT,
    COD_MARCA_BANDA              BIGINT,
    NOME_MARCA_BANDA             TEXT,
    ALTURA_SULCOS_MODELO_PNEU    REAL,
    COD_MODELO_BANDA             BIGINT,
    NOME_MODELO_BANDA            TEXT,
    QT_SULCOS_MODELO_BANDA       SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA   REAL,
    VALOR_BANDA                  REAL,
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    PRESSAO_RECOMENDADA          REAL,
    PRESSAO_ATUAL                REAL,
    STATUS                       TEXT,
    VIDA_ATUAL                   INTEGER,
    VIDA_TOTAL                   INTEGER)
LANGUAGE SQL
AS $$
SELECT
  P.CODIGO,
  P.CODIGO_CLIENTE,
  VP.POSICAO        AS POSICAO_PNEU,
  P.DOT,
  P.VALOR,
  U.CODIGO          AS COD_UNIDADE_ALOCADO,
  R.CODIGO          AS COD_REGIONAL_ALOCADO,
  P.PNEU_NOVO_NUNCA_RODADO,
  MP.CODIGO         AS COD_MARCA_PNEU,
  MP.NOME           AS NOME_MARCA_PNEU,
  MOP.CODIGO        AS COD_MODELO_PNEU,
  MOP.NOME          AS NOME_MODELO_PNEU,
  MOP.QT_SULCOS     AS QT_SULCOS_MODELO_PNEU,
  MAB.CODIGO        AS COD_MARCA_BANDA,
  MAB.NOME          AS NOME_MARCA_BANDA,
  MOP.ALTURA_SULCOS AS ALTURA_SULCOS_MODELO_PNEU,
  MOB.CODIGO        AS COD_MODELO_BANDA,
  MOB.NOME          AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS     AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS AS ALTURA_SULCOS_MODELO_BANDA,
  PVV.VALOR         AS VALOR_BANDA,
  PD.ALTURA,
  PD.LARGURA,
  PD.ARO,
  PD.CODIGO         AS COD_DIMENSAO,
  P.ALTURA_SULCO_CENTRAL_INTERNO,
  P.ALTURA_SULCO_CENTRAL_EXTERNO,
  P.ALTURA_SULCO_INTERNO,
  P.ALTURA_SULCO_EXTERNO,
  P.PRESSAO_RECOMENDADA,
  P.PRESSAO_ATUAL,
  P.STATUS,
  P.VIDA_ATUAL,
  P.VIDA_TOTAL
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
  LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
WHERE P.CODIGO = F_COD_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR UM PNEU PARA SER AFERIDO AVULSAMENTE.
CREATE FUNCTION FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(F_COD_PNEU BIGINT, F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    CODIGO                                BIGINT,
    CODIGO_CLIENTE                        TEXT,
    POSICAO_PNEU                          INTEGER,
    DOT                                   TEXT,
    VALOR                                 REAL,
    COD_UNIDADE_ALOCADO                   BIGINT,
    COD_REGIONAL_ALOCADO                  BIGINT,
    PNEU_NOVO_NUNCA_RODADO                BOOLEAN,
    COD_MARCA_PNEU                        BIGINT,
    NOME_MARCA_PNEU                       TEXT,
    COD_MODELO_PNEU                       BIGINT,
    NOME_MODELO_PNEU                      TEXT,
    QT_SULCOS_MODELO_PNEU                 SMALLINT,
    COD_MARCA_BANDA                       BIGINT,
    NOME_MARCA_BANDA                      TEXT,
    ALTURA_SULCOS_MODELO_PNEU             REAL,
    COD_MODELO_BANDA                      BIGINT,
    NOME_MODELO_BANDA                     TEXT,
    QT_SULCOS_MODELO_BANDA                SMALLINT,
    ALTURA_SULCOS_MODELO_BANDA            REAL,
    VALOR_BANDA                           REAL,
    ALTURA                                INTEGER,
    LARGURA                               INTEGER,
    ARO                                   REAL,
    COD_DIMENSAO                          BIGINT,
    ALTURA_SULCO_CENTRAL_INTERNO          REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO          REAL,
    ALTURA_SULCO_INTERNO                  REAL,
    ALTURA_SULCO_EXTERNO                  REAL,
    PRESSAO_RECOMENDADA                   REAL,
    PRESSAO_ATUAL                         REAL,
    STATUS                                TEXT,
    VIDA_ATUAL                            INTEGER,
    VIDA_TOTAL                            INTEGER,
    JA_FOI_AFERIDO                        BOOLEAN,
    COD_ULTIMA_AFERICAO                   BIGINT,
    DATA_HORA_ULTIMA_AFERICAO             TIMESTAMP WITHOUT TIME ZONE,
    PLACA_VEICULO_ULTIMA_AFERICAO         TEXT,
    TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO TEXT,
    TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO  TEXT,
    NOME_COLABORADOR_ULTIMA_AFERICAO      TEXT)
LANGUAGE SQL
AS $$
WITH AFERICOES AS (
    SELECT
      INNER_TABLE.CODIGO           AS COD_AFERICAO,
      INNER_TABLE.COD_PNEU         AS COD_PNEU,
      INNER_TABLE.DATA_HORA,
      INNER_TABLE.PLACA_VEICULO,
      INNER_TABLE.TIPO_MEDICAO_COLETADA,
      INNER_TABLE.TIPO_PROCESSO_COLETA,
      INNER_TABLE.NOME_COLABORADOR AS NOME_COLABORADOR,
      CASE WHEN INNER_TABLE.NOME_COLABORADOR IS NOT NULL
        THEN TRUE
      ELSE FALSE END               AS JA_FOI_AFERIDO
    FROM (SELECT
            A.CODIGO,
            AV.COD_PNEU,
            A.DATA_HORA,
            A.PLACA_VEICULO,
            A.TIPO_MEDICAO_COLETADA,
            A.TIPO_PROCESSO_COLETA,
            C.NOME                    AS NOME_COLABORADOR,
            MAX(A.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
            LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
            LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
            LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.STATUS = 'ESTOQUE' AND P.CODIGO = F_COD_PNEU) AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT
  F.*,
  A.JA_FOI_AFERIDO                      AS JA_FOI_AFERIDO,
  A.COD_AFERICAO                        AS COD_ULTIMA_AFERICAO,
  A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMA_AFERICAO,
  A.PLACA_VEICULO :: TEXT               AS PLACA_VEICULO_ULTIMA_AFERICAO,
  A.TIPO_MEDICAO_COLETADA :: TEXT       AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
  A.TIPO_PROCESSO_COLETA :: TEXT        AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
  A.NOME_COLABORADOR :: TEXT            AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEUS_GET_PNEU_BY_CODIGO(F_COD_PNEU) AS F
  LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU
WHERE F.CODIGO = F_COD_PNEU;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR A RESTRIÇÃO COM BASE EM UMA UNIDADE.
CREATE FUNCTION FUNC_AFERICAO_GET_RESTRICAO_BY_UNIDADE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    SULCO_MINIMO_DESCARTE    REAL,
    SULCO_MINIMO_RECAPAGEM   REAL,
    TOLERANCIA_CALIBRAGEM    REAL,
    TOLERANCIA_INSPECAO      REAL,
    PERIODO_AFERICAO_SULCO   INTEGER,
    PERIODO_AFERICAO_PRESSAO INTEGER)
LANGUAGE SQL
AS $$
SELECT
  ER.SULCO_MINIMO_DESCARTE,
  ER.SULCO_MINIMO_RECAPAGEM,
  ER.TOLERANCIA_CALIBRAGEM,
  ER.TOLERANCIA_INSPECAO,
  ER.PERIODO_AFERICAO_SULCO,
  ER.PERIODO_AFERICAO_PRESSAO
FROM UNIDADE U
  JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA
  JOIN PNEU_RESTRICAO_UNIDADE ER ON ER.COD_EMPRESA = E.CODIGO AND U.CODIGO = ER.COD_UNIDADE
WHERE U.CODIGO = F_COD_UNIDADE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR A RESTRIÇÃO COM BASE EM UMA PLACA.
CREATE FUNCTION FUNC_AFERICAO_GET_RESTRICAO_BY_PLACA(F_PLACA_VEICULO TEXT)
  RETURNS TABLE(
    SULCO_MINIMO_DESCARTE    REAL,
    SULCO_MINIMO_RECAPAGEM   REAL,
    TOLERANCIA_CALIBRAGEM    REAL,
    TOLERANCIA_INSPECAO      REAL,
    PERIODO_AFERICAO_SULCO   INTEGER,
    PERIODO_AFERICAO_PRESSAO INTEGER)
LANGUAGE SQL
AS $$
SELECT
  ER.SULCO_MINIMO_DESCARTE,
  ER.SULCO_MINIMO_RECAPAGEM,
  ER.TOLERANCIA_INSPECAO,
  ER.TOLERANCIA_CALIBRAGEM,
  ER.PERIODO_AFERICAO_SULCO,
  ER.PERIODO_AFERICAO_PRESSAO
FROM VEICULO V
  JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
  JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA
  JOIN PNEU_RESTRICAO_UNIDADE ER ON ER.COD_EMPRESA = E.CODIGO AND ER.COD_UNIDADE = U.CODIGO
WHERE V.PLACA = F_PLACA_VEICULO;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA FUNCTION PARA BUSCAR AS CONFIGURAÇÕES DE AFERIÇÃO COM BASE EM UMA PLACA.
CREATE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_AFERICAO_BY_PLACA(F_PLACA_VEICULO TEXT)
  RETURNS TABLE(
    STATUS_ATIVO              BOOLEAN,
    PODE_AFERIR_SULCO         BOOLEAN,
    PODE_AFERIR_PRESSAO       BOOLEAN,
    PODE_AFERIR_SULCO_PRESSAO BOOLEAN,
    PODE_AFERIR_ESTEPE        BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  VACTA.STATUS_ATIVO,
  VACTA.PODE_AFERIR_SULCO,
  VACTA.PODE_AFERIR_PRESSAO,
  VACTA.PODE_AFERIR_SULCO_PRESSAO,
  VACTA.PODE_AFERIR_ESTEPE
FROM VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO AS VACTA
WHERE COD_UNIDADE = (SELECT COD_UNIDADE
                     FROM VEICULO
                     WHERE PLACA = F_PLACA_VEICULO)
      AND COD_TIPO_VEICULO = (SELECT COD_TIPO
                              FROM VEICULO
                              WHERE PLACA = F_PLACA_VEICULO);
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA RELATÓRIO PARA LISTAR AS AFERIÇÕES AVULSAS REALIZADAS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "DATA/HORA AFERIÇÃO"    TEXT,
    "QUEM AFERIU?"          VARCHAR(255),
    "UNIDADE ALOCADO"       VARCHAR(40),
    "PNEU"                  VARCHAR(255),
    "MARCA"                 VARCHAR(255),
    "MODELO"                VARCHAR(255),
    "MEDIDAS"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "VIDA"                  TEXT,
    "DOT"                   CHARACTER VARYING)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
  PNEU_NUNCA_AFERIDO            TEXT := 'Nunca Aferido';
  PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
  RETURN QUERY
  SELECT
    COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
             PNEU_NUNCA_AFERIDO) AS ULTIMA_AFERICAO,
    C.NOME,
    U.NOME                    AS UNIDADE_ALOCADO,
    P.CODIGO_CLIENTE          AS COD_PNEU,
    MAP.NOME                  AS NOME_MARCA,
    MP.NOME                   AS NOME_MODELO,
    ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)                       AS MEDIDAS,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')         AS SULCO_INTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',') AS SULCO_CENTRAL_INTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',') AS SULCO_CENTRAL_EXTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')         AS SULCO_EXTERNO,
    P.VIDA_ATUAL::TEXT      AS VIDA_ATUAL,
    COALESCE(P.DOT, '-')      AS DOT
  FROM PNEU P
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
    JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
    JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
  WHERE
    P.COD_UNIDADE = ANY (F_COD_UNIDADES)
    AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
    AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN f_data_inicial AND f_data_final
  ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- CRIA RELATÓRIO PARA LISTAR AS AFERIÇÕES AVULSAS REALIZADAS POR UM COLABORADOR
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS_BY_COLABORADOR(
  F_COD_UNIDADE BIGINT, F_COD_COLABORADOR BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "DATA/HORA AFERIÇÃO"    TEXT,
    "QUEM AFERIU?"          VARCHAR(255),
    "UNIDADE ALOCADO"       VARCHAR(40),
    "PNEU"                  VARCHAR(255),
    "MARCA"                 VARCHAR(255),
    "MODELO"                VARCHAR(255),
    "MEDIDAS"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "VIDA"                  TEXT,
    "DOT"                   CHARACTER VARYING)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
  PNEU_NUNCA_AFERIDO            TEXT := 'Nunca Aferido';
  PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
  RETURN QUERY
  SELECT
    COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
             PNEU_NUNCA_AFERIDO) AS ULTIMA_AFERICAO,
    C.NOME,
    U.NOME                    AS UNIDADE_ALOCADO,
    P.CODIGO_CLIENTE          AS COD_PNEU,
    MAP.NOME                  AS NOME_MARCA,
    MP.NOME                   AS NOME_MODELO,
    ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)                       AS MEDIDAS,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')         AS SULCO_INTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',') AS SULCO_CENTRAL_INTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',') AS SULCO_CENTRAL_EXTERNO,
    REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.', ',')         AS SULCO_EXTERNO,
    P.VIDA_ATUAL::TEXT      AS VIDA_ATUAL,
    COALESCE(P.DOT, '-')      AS DOT
  FROM PNEU P
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
    JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
    JOIN COLABORADOR C ON A.CPF_AFERIDOR = (SELECT CO.CPF FROM COLABORADOR CO WHERE CODIGO = F_COD_COLABORADOR)
  WHERE
    C.CODIGO = F_COD_COLABORADOR
    AND P.COD_UNIDADE = F_COD_UNIDADE
    AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
    AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN f_data_inicial AND f_data_final
  ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- Cria function de relatório que já existia no servidor
CREATE FUNCTION FUNC_PNEU_RELATORIO_QUANTIDADE_AFERICOES_POR_TIPO_MEDICAO_COLETADA(
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
          (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)) :: DATE               AS DATA_REFERENCIA,
          TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)), DATE_FORMAT) AS DATA_REFERENCIA_FORMATADA,
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
              AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
              AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
        GROUP BY A.DATA_HORA, DATA_REFERENCIA_FORMATADA, A.COD_UNIDADE
        ORDER BY A.DATA_HORA :: DATE ASC) AS DADOS
  GROUP BY DADOS.DATA_REFERENCIA, DADOS.DATA_REFERENCIA_FORMATADA
  ORDER BY DADOS.DATA_REFERENCIA ASC;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- Cria function de relatório que já existia no servidor
CREATE FUNCTION FUNC_PNEU_RELATORIO_STATUS_PLACAS_AFERICAO(
  F_COD_UNIDADES        BIGINT [],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITHOUT TIME ZONE)
  RETURNS TABLE(
    TOTAL_VENCIDAS BIGINT,
    TOTAL_PLACAS   BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_PROCESSO_PLACA            TEXT := 'PLACA';
  MEDICAO_COLETADA_PRESSAO       TEXT := 'PRESSAO';
  MEDICAO_COLETADA_SULCO         TEXT := 'SULCO';
  MEDICAO_COLETADA_SULCO_PRESSAO TEXT := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY
  SELECT
    SUM(CASE
        WHEN (DADOS.INTERVALO_PRESSAO > DADOS.PERIODO_AFERICAO_PRESSAO OR DADOS.INTERVALO_PRESSAO < 0) AND
             (DADOS.INTERVALO_SULCO > DADOS.PERIODO_AFERICAO_SULCO OR DADOS.INTERVALO_SULCO < 0)
          THEN 1
        ELSE 0 END)    AS TOTAL_VENCIDAS,
    COUNT(DADOS.PLACA) AS TOTAL_PLACAS
  FROM (SELECT
          V.PLACA,
          COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER AS INTERVALO_PRESSAO,
          COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER   AS INTERVALO_SULCO,
          ERP.PERIODO_AFERICAO_PRESSAO,
          ERP.PERIODO_AFERICAO_SULCO
        FROM VEICULO V
          JOIN PNEU_RESTRICAO_UNIDADE ERP ON ERP.COD_UNIDADE = V.COD_UNIDADE
          LEFT JOIN (SELECT
                       PLACA_VEICULO                                                 AS PLACA_INTERVALO,
                       EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC - MAX((DATA_HORA)))) AS INTERVALO
                     FROM AFERICAO AF
                     WHERE TIPO_MEDICAO_COLETADA IN (MEDICAO_COLETADA_PRESSAO, MEDICAO_COLETADA_SULCO_PRESSAO)
                           AND TIPO_PROCESSO_COLETA = TIPO_PROCESSO_PLACA
                     GROUP BY PLACA_VEICULO, AF.COD_UNIDADE) AS INTERVALO_PRESSAO
            ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA
          LEFT JOIN (SELECT
                       PLACA_VEICULO                                                 AS PLACA_INTERVALO,
                       EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC - MAX((DATA_HORA)))) AS INTERVALO
                     FROM AFERICAO AF
                     WHERE TIPO_MEDICAO_COLETADA IN (MEDICAO_COLETADA_SULCO, MEDICAO_COLETADA_SULCO_PRESSAO)
                           AND TIPO_PROCESSO_COLETA = TIPO_PROCESSO_PLACA
                     GROUP BY PLACA_VEICULO, AF.COD_UNIDADE) AS INTERVALO_SULCO
            ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA
        WHERE V.STATUS_ATIVO = TRUE
              AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)) AS DADOS;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- Cria function de relatório que já existia no servidor
CREATE FUNCTION FUNC_PNEU_RELATORIO_QUANTIDADE_KMS_RODADOS_COM_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    PLACA_VEICULO TEXT,
    TOTAL_KM      NUMERIC)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_SERVICO_CALIBRAGEM TEXT := 'calibragem';
  TIPO_SERVICO_INSPECAO   TEXT := 'inspecao';
BEGIN
  RETURN QUERY
  SELECT
    DADOS.PLACA_VEICULO :: TEXT AS PLACA_VEICULO,
    DADOS.TOTAL_KM              AS TOTAL_KM
  FROM (SELECT
          A.PLACA_VEICULO,
          SUM(AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO) AS TOTAL_KM
        FROM AFERICAO_MANUTENCAO AM
          JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
          JOIN VEICULO_PNEU VP ON VP.PLACA = A.PLACA_VEICULO
                                  AND AM.COD_PNEU = VP.COD_PNEU
                                  AND AM.COD_UNIDADE = VP.COD_UNIDADE
        WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
              AND (AM.TIPO_SERVICO IN (TIPO_SERVICO_CALIBRAGEM, TIPO_SERVICO_INSPECAO))
        GROUP BY A.PLACA_VEICULO
        ORDER BY 2 DESC) AS DADOS
  WHERE DADOS.TOTAL_KM > 0;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION ;