BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
--########################### FIX -  FUNCTION FUNC_PNEU_RELATORIO_STATUS_PLACAS_AFERICAO ###############################
--#################################################  PL-1672  ##########################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_PNEU_RELATORIO_STATUS_PLACAS_AFERICAO(
F_COD_UNIDADES BIGINT [],
F_DATA_HORA_ATUAL_UTC TIMESTAMP WITHOUT TIME ZONE );

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_STATUS_PLACAS_AFERICAO(
  F_COD_UNIDADES        BIGINT [],
  F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
    TOTAL_VENCIDAS BIGINT,
    TOTAL_NO_PRAZO BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_PLACAS_ATIVAS BIGINT := (SELECT COUNT(V.PLACA)
                               FROM VEICULO V
                               WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES) AND V.STATUS_ATIVO = TRUE);
BEGIN
  RETURN QUERY
  WITH QTD_PLACAS_VENCIDAS AS (
      SELECT (SELECT COUNT(PLACA)
              FROM FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES,
                                                                    F_DATA_HORA_ATUAL_UTC)) AS QTD_VENCIDAS
  )

  SELECT
    QPV.QTD_VENCIDAS                     AS QTD_VENCIDAS,
    QTD_PLACAS_ATIVAS - QPV.QTD_VENCIDAS AS QTD_PRAZO
  FROM QTD_PLACAS_VENCIDAS QPV;
END;
$$;

-- ATIVA O COMPONENTE DA DASHBOARD "PLACAS COM AFERIÇÃO VENCIDA"
UPDATE DASHBOARD_COMPONENTE
SET ATIVO = TRUE
WHERE CODIGO = 6;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################## COPIA MODELO DE CHECK ENTRE EMPRESAS ########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2059

-- Adiciona FK de função na tabela checklist_modelo_funcao.
-- Rodado antes em prod, por isso comentado.
-- ALTER TABLE CHECKLIST_MODELO_FUNCAO
--   ADD CONSTRAINT FK_CHECKLIST_MODELO_FUNCAO_FUNCAO FOREIGN KEY (COD_FUNCAO) REFERENCES FUNCAO (CODIGO);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST_ENTRE_EMPRESAS(
      F_COD_MODELO_CHECKLIST_COPIADO         BIGINT,
      F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
      F_COD_CARGOS_CHECKLIST                 BIGINT [] DEFAULT NULL,
      F_COD_TIPOS_VEICULOS_CHECKLIST         BIGINT [] DEFAULT NULL,
  OUT COD_MODELO_CHECKLIST_INSERIDO          BIGINT,
  OUT AVISO_MODELO_INSERIDO                  TEXT)
  RETURNS RECORD
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST BIGINT := (SELECT U.COD_EMPRESA
                                                    FROM UNIDADE U
                                                    WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
  COD_UNIDADE_MODELO_CHECKLIST_COPIADO   BIGINT;
  COD_PERGUNTA_CRIADO                    BIGINT;
  PERGUNTA_MODELO_CHECKLIST_COPIADO      CHECKLIST_PERGUNTAS%ROWTYPE;
BEGIN
  -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
  IF NOT EXISTS(SELECT CM.CODIGO
                FROM CHECKLIST_MODELO CM
                WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
  THEN RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
  END IF;

  -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST)
  THEN RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST;
  END IF;

  -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DE EMPRESAS DIFERENTES.
  SELECT CM.COD_UNIDADE
  FROM CHECKLIST_MODELO CM
  WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
  INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
  IF ((SELECT U.COD_EMPRESA
       FROM UNIDADE U
       WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST) =
      (SELECT U.COD_EMPRESA
       FROM UNIDADE U
       WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
  THEN RAISE EXCEPTION 'Essa function deve ser utilizada para copiar modelos de checklists entre empresas diferentes.
                        Utilize a function: FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST, para copiar checklists entre unidades da mesma empresa.';
  END IF;

  IF F_COD_CARGOS_CHECKLIST IS NOT NULL
  THEN
    -- VERIFICA SE TODOS OS CARGOS EXISTEM.
    IF (SELECT EXISTS(SELECT COD_CARGO
                      FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                      WHERE F.CODIGO IS NULL))
    THEN
      RAISE EXCEPTION 'O(s) cargo(s) % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_CARGO)
                                                                  FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                                                    LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                                                                  WHERE F.CODIGO IS NULL);
    END IF;

    -- VERIFICA SE TODOS OS CARGOS PERTENCEM A EMPRESA DE DESTINO.
    IF (SELECT EXISTS(SELECT COD_CARGO
                      FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                      WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
    THEN
      RAISE EXCEPTION 'O(s) cargo(s) % não pertence(m) a empresa para a qual você está tentando copiar o modelo checklit, empresa: %',
      (SELECT ARRAY_AGG(COD_CARGO)
       FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
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
                      FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO LEFT JOIN VEICULO_TIPO VT
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
                      FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO LEFT JOIN VEICULO_TIPO VT
                          ON VT.CODIGO = COD_TIPO_VEICULO
                      WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
    THEN
      RAISE EXCEPTION 'O(s) tipo(s) de veículo % não pertence(m) a empresa para a qual você está tentando copiar o modelo checklit, empresa: %',
      (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
       FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO LEFT JOIN VEICULO_TIPO VT
           ON VT.CODIGO = COD_TIPO_VEICULO
       WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
      (SELECT E.NOME
       FROM EMPRESA E
       WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
    END IF;
  END IF;

  -- INSERE O MODELO DE CHECKLIST.
  INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, NOME, STATUS_ATIVO)
    SELECT
      F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
      CC.NOME,
      CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
  RETURNING CODIGO
    INTO COD_MODELO_CHECKLIST_INSERIDO;

  -- VERIFICAMOS SE O INSERT FUNCIONOU.
  IF COD_MODELO_CHECKLIST_INSERIDO <= 0
  THEN
    RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
  END IF;

  SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
  INTO AVISO_MODELO_INSERIDO;

  IF F_COD_CARGOS_CHECKLIST IS NOT NULL
  THEN
    -- INSERE CARGOS QUE PODEM REALIZAR O MODELO DE CHECKLIST
    INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
      SELECT
        COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
        CODIGO_FUNCAO
      FROM UNNEST(F_COD_CARGOS_CHECKLIST) CODIGO_FUNCAO;
  END IF;

  IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
  THEN
    -- INSERE TIPOS DE VEÍCULOS LIBERADOS PARA O MODELO DE CHECKLIST
    INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (COD_MODELO, COD_UNIDADE, COD_TIPO_VEICULO)
      SELECT
        COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
        CODIGO_TIPO_VEICULO
      FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) CODIGO_TIPO_VEICULO;
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
            -- só copiamos o código da imagem se a imagem vinculada for da galeria pública do prolog.
            F_IF((SELECT EXISTS(SELECT CGI.COD_IMAGEM
                                FROM CHECKLIST_GALERIA_IMAGENS CGI
                                WHERE CGI.COD_IMAGEM = PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM AND
                                      CGI.COD_EMPRESA IS NULL)),
                 PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                 NULL))
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
--#################################### ALTERA PRESSÃO IDEAL DE ACORDO COM A DIMENSÃO ###################################
--######################################################################################################################
--######################################################################################################################
--PL-2063
CREATE OR REPLACE FUNCTION FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_DIMENSAO(
      F_COD_EMPRESA              BIGINT,
      F_COD_UNIDADE              BIGINT,
      F_COD_DIMENSAO             BIGINT,
      F_NOVA_PRESSAO_RECOMENDADA BIGINT,
      F_QTD_PNEUS_IMPACTADOS     BIGINT,
  OUT AVISO_PRESSAO_ALTERADA     TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  QTD_REAL_PNEUS_IMPACTADOS  BIGINT;
  PRESSAO_MINIMA_RECOMENDADA BIGINT := 25;
  PRESSAO_MAXIMA_RECOMENDADA BIGINT := 150;
BEGIN
  --Verifica se a pressao informada está dentro das recomendadas.
  IF (F_NOVA_PRESSAO_RECOMENDADA NOT BETWEEN PRESSAO_MINIMA_RECOMENDADA AND PRESSAO_MAXIMA_RECOMENDADA)
  THEN RAISE EXCEPTION 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', PRESSAO_MINIMA_RECOMENDADA,
  PRESSAO_MAXIMA_RECOMENDADA;
  END IF;

  -- Verifica se a empresa existe.
  IF NOT EXISTS(SELECT E.CODIGO
                FROM EMPRESA E
                WHERE E.CODIGO = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Empresa de código % não existe!', F_COD_EMPRESA;
  END IF;

  -- Verifica se a unidade existe.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE;
  END IF;

  -- Verifica se existe a dimensão informada.
  IF NOT EXISTS(SELECT DM.CODIGO
                FROM DIMENSAO_PNEU DM
                WHERE DM.CODIGO = F_COD_DIMENSAO)
  THEN RAISE EXCEPTION 'Dimensao de código % não existe!', F_COD_DIMENSAO;
  END IF;

  -- Verifica se a unidade é da empresa informada.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE AND U.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'A unidade % não pertence a empresa %!', F_COD_UNIDADE, F_COD_EMPRESA;
  END IF;

  -- Verifica se algum pneu possui dimensão informada.
  IF NOT EXISTS(SELECT P.COD_DIMENSAO
                FROM PNEU P
                WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
                      AND P.COD_UNIDADE = F_COD_UNIDADE
                      AND P.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Não existem pneus com a dimensão % na unidade %', F_COD_DIMENSAO, F_COD_UNIDADE;
  END IF;

  -- Verifica quantidade de pneus impactados.
  SELECT COUNT(P.CODIGO)
  FROM PNEU P
  WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
        AND P.COD_UNIDADE = F_COD_UNIDADE
        AND P.COD_EMPRESA = F_COD_EMPRESA
  INTO QTD_REAL_PNEUS_IMPACTADOS;
  IF (QTD_REAL_PNEUS_IMPACTADOS <> F_QTD_PNEUS_IMPACTADOS)
  THEN RAISE EXCEPTION 'A quantidade de pneus informados como impactados pela mudança de pressão (%) não condiz com a
                       quantidade real de pneus que serão afetados!', F_QTD_PNEUS_IMPACTADOS;
  END IF;

  UPDATE PNEU
  SET PRESSAO_RECOMENDADA = F_NOVA_PRESSAO_RECOMENDADA
  WHERE COD_DIMENSAO = F_COD_DIMENSAO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND COD_EMPRESA = F_COD_EMPRESA;

  SELECT CONCAT('Pressão recomendada dos pneus com dimensão ',
                F_COD_DIMENSAO,
                ' da unidade ',
                F_COD_UNIDADE,
                ' alterada para ',
                F_NOVA_PRESSAO_RECOMENDADA,
                ' psi')
  INTO AVISO_PRESSAO_ALTERADA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################# FUNC - RETORNA LISTAGEM DE MARCAÕES POR DATA #######################################
--######################################################################################################################
--######################################################################################################################
--PL-2000
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_LISTAGEM_MARCACOES(
  F_COD_UNIDADE        BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_DATA_INICIAL       DATE,
  F_DATA_FINAL         DATE)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO          TEXT,
    FONTE_DATA_HORA_FIM             TEXT,
    JUSTIFICATIVA_ESTOURO           TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    LATITUDE_MARCACAO_INICIO        TEXT,
    LONGITUDE_MARCACAO_INICIO       TEXT,
    LATITUDE_MARCACAO_FIM           TEXT,
    LONGITUDE_MARCACAO_FIM          TEXT,
    COD_UNIDADE                     BIGINT,
    CPF_COLABORADOR                 TEXT,
    NOME_COLABORADOR                TEXT,
    COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
    COD_TIPO_INTERVALO              BIGINT,
    NOME_TIPO_INTERVALO             TEXT,
    ICONE_TIPO_INTERVALO            TEXT,
    TEMPO_RECOMENDADO_MINUTOS       BIGINT,
    TEMPO_ESTOURO_MINUTOS           BIGINT,
    DATA_HORA_INICIO                TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                   TIMESTAMP WITHOUT TIME ZONE,
    DURACAO_EM_SEGUNDOS             BIGINT,
    COD_MARCACAO_VINCULO            BIGINT,
    COD_MARCACAO_INICIO             BIGINT,
    COD_MARCACAO_FIM                BIGINT,
    STATUS_ATIVO_INICIO             BOOLEAN,
    STATUS_ATIVO_FIM                BOOLEAN,
    FOI_AJUSTADO_INICIO             BOOLEAN,
    FOI_AJUSTADO_FIM                BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITHOUT TIME ZONE,
    TIPO_JORNADA                    BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_TIME_ZONE_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH INTERVALOS AS (
      SELECT IT.*
      FROM INTERVALO IT
      WHERE IT.COD_UNIDADE = F_COD_UNIDADE
            AND F_IF(F_CPF_COLABORADOR IS NULL, TRUE, IT.CPF_COLABORADOR = F_CPF_COLABORADOR)
            AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO)
            AND ((IT.DATA_HORA AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
                 AND (IT.DATA_HORA AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)),
      INICIOS AS (
        SELECT
          MI.COD_MARCACAO_INICIO                AS COD_MARCACAO_INICIO,
          MV.COD_MARCACAO_FIM                   AS COD_MARCACAO_VINCULO,
          I.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_INICIO,
          I.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_INICIO,
          I.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_INICIO,
          I.COD_UNIDADE                         AS COD_UNIDADE,
          I.CPF_COLABORADOR                     AS CPF_COLABORADOR,
          CO.NOME                               AS NOME_COLABORADOR,
          I.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
          VIT.NOME                              AS NOME_TIPO_INTERVALO,
          VIT.ICONE                             AS ICONE_TIPO_INTERVALO,
          VIT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
          I.DATA_HORA                           AS DATA_HORA,
          I.CODIGO                              AS CODIGO_INICIO,
          I.STATUS_ATIVO                        AS STATUS_ATIVO_INICIO,
          I.FOI_AJUSTADO                        AS FOI_AJUSTADO_INICIO,
          I.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_INICIO,
          VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
          VIT.TIPO_JORNADA                      AS TIPO_JORNADA
        FROM MARCACAO_INICIO MI
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
          JOIN INTERVALOS I ON MI.COD_MARCACAO_INICIO = I.CODIGO
          JOIN COLABORADOR CO ON CO.CPF = I.CPF_COLABORADOR
          JOIN VIEW_INTERVALO_TIPO VIT ON I.COD_TIPO_INTERVALO = VIT.CODIGO),
      FINS AS (
        SELECT
          MF.COD_MARCACAO_FIM                   AS COD_MARCACAO_FIM,
          MV.COD_MARCACAO_INICIO                AS COD_MARCACAO_VINCULO,
          F.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_FIM,
          F.JUSTIFICATIVA_ESTOURO               AS JUSTIFICATIVA_ESTOURO,
          F.JUSTIFICATIVA_TEMPO_RECOMENDADO     AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
          F.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_FIM,
          F.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_FIM,
          F.COD_UNIDADE                         AS COD_UNIDADE,
          F.CPF_COLABORADOR                     AS CPF_COLABORADOR,
          CO.NOME                               AS NOME_COLABORADOR,
          F.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
          VIT.NOME                              AS NOME_TIPO_INTERVALO,
          VIT.ICONE                             AS ICONE_TIPO_INTERVALO,
          VIT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
          F.DATA_HORA                           AS DATA_HORA,
          F.CODIGO                              AS CODIGO_FIM,
          F.STATUS_ATIVO                        AS STATUS_ATIVO_FIM,
          F.FOI_AJUSTADO                        AS FOI_AJUSTADO_FIM,
          F.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_FIM,
          VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
          VIT.TIPO_JORNADA                      AS TIPO_JORNADA
        FROM MARCACAO_FIM MF
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
          JOIN INTERVALOS F ON MF.COD_MARCACAO_FIM = F.CODIGO
          JOIN COLABORADOR CO ON CO.CPF = F.CPF_COLABORADOR
          JOIN VIEW_INTERVALO_TIPO VIT ON F.COD_TIPO_INTERVALO = VIT.CODIGO)

  SELECT
    COALESCE(IC.FONTE_DATA_HORA_INICIO, IVI.FONTE_DATA_HORA) :: TEXT              AS FONTE_DATA_HORA_INICIO,
    COALESCE(F.FONTE_DATA_HORA_FIM, IVF.FONTE_DATA_HORA) :: TEXT                  AS FONTE_DATA_HORA_FIM,
    F.JUSTIFICATIVA_ESTOURO                                                       AS JUSTIFICATIVA_ESTOURO,
    F.JUSTIFICATIVA_TEMPO_RECOMENDADO                                             AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
    COALESCE(IC.LATITUDE_MARCACAO_INICIO, IVI.LATITUDE_MARCACAO)                  AS LATITUDE_MARCACAO_INICIO,
    COALESCE(IC.LONGITUDE_MARCACAO_INICIO, IVI.LONGITUDE_MARCACAO)                AS LONGITUDE_MARCACAO_INICIO,
    COALESCE(F.LATITUDE_MARCACAO_FIM, IVF.LATITUDE_MARCACAO)                      AS LATITUDE_MARCACAO_FIM,
    COALESCE(F.LONGITUDE_MARCACAO_FIM, IVF.LONGITUDE_MARCACAO)                    AS LONGITUDE_MARCACAO_FIM,
    COALESCE(IC.COD_UNIDADE, F.COD_UNIDADE)                                       AS COD_UNIDADE,
    LPAD(COALESCE(IC.CPF_COLABORADOR, F.CPF_COLABORADOR) :: TEXT, 11, '0')        AS CPF_COLABORADOR,
    COALESCE(IC.NOME_COLABORADOR, F.NOME_COLABORADOR) :: TEXT                     AS NOME_COLABORADOR,
    COALESCE(IC.COD_TIPO_INTERVALO_POR_UNIDADE,
             F.COD_TIPO_INTERVALO_POR_UNIDADE)                                    AS COD_TIPO_INTERVALO_POR_UNIDADE,
    COALESCE(IC.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                         AS COD_TIPO_INTERVALO,
    COALESCE(IC.NOME_TIPO_INTERVALO, F.NOME_TIPO_INTERVALO) :: TEXT               AS NOME_TIPO_INTERVALO,
    COALESCE(IC.ICONE_TIPO_INTERVALO, F.ICONE_TIPO_INTERVALO) :: TEXT             AS ICONE_TIPO_INTERVALO,
    COALESCE(IC.TEMPO_RECOMENDADO_MINUTOS, F.TEMPO_RECOMENDADO_MINUTOS) :: BIGINT AS TEMPO_RECOMENDADO_MINUTOS,
    COALESCE(IC.TEMPO_ESTOURO_MINUTOS, F.TEMPO_ESTOURO_MINUTOS) :: BIGINT         AS TEMPO_ESTOURO_MINUTOS,
    COALESCE(IC.DATA_HORA, IVI.DATA_HORA) AT TIME ZONE F_TIME_ZONE_UNIDADE        AS DATA_HORA_INICIO,
    COALESCE(F.DATA_HORA, IVF.DATA_HORA) AT TIME ZONE F_TIME_ZONE_UNIDADE         AS DATA_HORA_FIM,
    TO_SECONDS(COALESCE(F.DATA_HORA, IVF.DATA_HORA) -
               COALESCE(IC.DATA_HORA, IVI.DATA_HORA))                             AS DURACAO_EM_SEGUNDOS,
    COALESCE(IC.COD_MARCACAO_VINCULO, F.COD_MARCACAO_VINCULO)                     AS COD_MARCACAO_VINCULO,
    COALESCE(IC.CODIGO_INICIO, IVI.CODIGO)                                        AS COD_MARCACAO_INICIO,
    COALESCE(F.CODIGO_FIM, IVF.CODIGO)                                            AS COD_MARCACAO_FIM,
    COALESCE(IC.STATUS_ATIVO_INICIO, IVI.STATUS_ATIVO)                            AS STATUS_ATIVO_INICIO,
    COALESCE(F.STATUS_ATIVO_FIM, IVF.STATUS_ATIVO)                                AS STATUS_ATIVO_FIM,
    COALESCE(IC.FOI_AJUSTADO_INICIO, IVI.FOI_AJUSTADO)                            AS FOI_AJUSTADO_INICIO,
    COALESCE(F.FOI_AJUSTADO_FIM, IVF.FOI_AJUSTADO)                                AS FOI_AJUSTADO_FIM,
    COALESCE(IC.DATA_HORA_SINCRONIZACAO_INICIO,
             IVI.DATA_HORA_SINCRONIZACAO) AT TIME ZONE
    F_TIME_ZONE_UNIDADE                                                           AS DATA_HORA_SINCRONIZACAO_INICIO,
    COALESCE(F.DATA_HORA_SINCRONIZACAO_FIM, IVF.DATA_HORA_SINCRONIZACAO) AT TIME ZONE
    F_TIME_ZONE_UNIDADE                                                           AS DATA_HORA_SINCRONIZACAO_FIM,
    (F.TIPO_JORNADA = TRUE OR IC.TIPO_JORNADA = TRUE)                             AS TIPO_JORNADA
  FROM INICIOS IC
    FULL OUTER JOIN FINS F ON IC.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
    -- Com esses últimos left joins com INTERVALO garantimos que será buscado inícios ou fins vinculados a marcações
    -- que estão fora do filtro de data aplicado na primeira CTE INTERVALOS.
    -- Exemplo: se filtramos de 01/01/19 a 31/01/19 e temos uma marcação iniciada em 31/01/19 e finalizada em 01/02/19,
    -- sem esses left joins, esse fim não seria buscado.
    LEFT JOIN INTERVALO IVI ON IVI.CODIGO = F.COD_MARCACAO_VINCULO
    LEFT JOIN INTERVALO IVF ON IVF.CODIGO = IC.COD_MARCACAO_VINCULO
  ORDER BY
    CPF_COLABORADOR,
    COD_TIPO_INTERVALO,
    COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM);
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################# REMOVE PERMISSÃO E FUNCIONALIDADE DE RANKING  ######################################
--######################################################################################################################
--######################################################################################################################
--PL-2050
DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE COD_FUNCAO_PROLOG = 33;
DELETE FROM FUNCAO_PROLOG_V11 WHERE CODIGO = 33 AND COD_PILAR = 3;
DELETE FROM FUNCAO_PROLOG_AGRUPAMENTO WHERE CODIGO = 21;
--######################################################################################################################
--######################################################################################################################






--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2075
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST BIGINT)
  RETURNS TABLE(
    COD_CHECKLIST                 BIGINT,
    COD_CHECKLIST_MODELO          BIGINT,
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
    COD_IMAGEM                    BIGINT,
    URL_IMAGEM                    TEXT,
    RESPOSTA                      TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO                                                            AS COD_CHECKLIST,
    C.COD_CHECKLIST_MODELO                                              AS COD_CHECKLIST_MODELO,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)                  AS DATA_HORA_REALIZACAO,
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
    CGI.COD_IMAGEM                                                      AS COD_IMAGEM,
    CGI.URL_IMAGEM                                                      AS URL_IMAGEM,
    CR.RESPOSTA                                                         AS RESPOSTA
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
    JOIN CHECKLIST_RESPOSTAS CR
      ON C.CODIGO = CR.COD_CHECKLIST
         AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
         AND C.COD_UNIDADE = CR.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON CP.CODIGO = CR.COD_PERGUNTA
         AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON CAP.CODIGO = CR.COD_ALTERNATIVA
         AND CAP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO
         AND CAP.COD_PERGUNTA = CR.COD_PERGUNTA
    LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
      ON CP.COD_IMAGEM = CGI.COD_IMAGEM
  WHERE C.CODIGO = F_COD_CHECKLIST
  ORDER BY CP.CODIGO, CAP.CODIGO;
END;
$$;

--
-- Refatora essa function para ter mesmo nome nas colunas de retorno.
--
DROP FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(
  F_COD_UNIDADE      BIGINT,
  F_COD_EQUIPE       BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    CHARACTER VARYING,
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_TIMEZONE         TEXT,
  F_LIMIT            INTEGER,
  F_OFFSET           BIGINT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(
  F_COD_UNIDADE      BIGINT,
  F_COD_EQUIPE       BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    CHARACTER VARYING,
  F_DATA_INICIAL     DATE,
  F_DATA_FINAL       DATE,
  F_TIMEZONE         TEXT,
  F_LIMIT            INTEGER,
  F_OFFSET           BIGINT)
  RETURNS TABLE(
    COD_CHECKLIST                 BIGINT,
    COD_CHECKLIST_MODELO          BIGINT,
    DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
    KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
    DURACAO_REALIZACAO_MILLIS     BIGINT,
    CPF_COLABORADOR               BIGINT,
    PLACA_VEICULO                 TEXT,
    TIPO_CHECKLIST                CHAR,
    NOME_COLABORADOR              TEXT,
    TOTAL_ITENS_OK                BIGINT,
    TOTAL_ITENS_NOK               BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  RESPOSTA_OK VARCHAR(2) := 'OK';
  F_HAS_EQUIPE INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 1 ELSE 0 END;
  F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 1 ELSE 0 END;
  F_HAS_PLACA_VEICULO INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO                                              AS COD_CHECKLIST,
    C.COD_CHECKLIST_MODELO                                AS COD_CHECKLIST_MODELO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE                   AS DATA_HORA_REALIZACAO,
    C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE  AS DATA_HORA_IMPORTADO_PROLOG,
    C.KM_VEICULO                                          AS KM_VEICULO_MOMENTO_REALIZACAO,
    C.TEMPO_REALIZACAO                                    AS DURACAO_REALIZACAO_MILLIS,
    C.CPF_COLABORADOR                                     AS CPF_COLABORADOR,
    C.PLACA_VEICULO :: TEXT                               AS PLACA_VEICULO,
    C.TIPO                                                AS TIPO_CHECKLIST,
    CO.NOME :: TEXT                                       AS NOME_COLABORADOR,
    (SELECT COUNT(*) - COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_OK,
    (SELECT COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_NOK
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
    JOIN EQUIPE E
      ON E.CODIGO = CO.COD_EQUIPE
    JOIN VEICULO V
      ON V.PLACA = C.PLACA_VEICULO
  WHERE (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL
        AND C.COD_UNIDADE = F_COD_UNIDADE
        AND (F_HAS_EQUIPE = 1 OR E.CODIGO = F_COD_EQUIPE)
        AND (F_HAS_COD_TIPO_VEICULO = 1 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
        AND (F_HAS_PLACA_VEICULO = 1 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
  ORDER BY DATA_HORA_SINCRONIZACAO DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;

--
-- Refatora essa function para ter mesmo nome nas colunas de retorno.
--
DROP FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(
  F_CPF_COLABORADOR BIGINT,
  F_DATA_INICIAL    DATE,
  F_DATA_FINAL      DATE,
  F_TIMEZONE        TEXT,
  F_LIMIT           INTEGER,
  F_OFFSET          BIGINT);
CREATE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(
  F_CPF_COLABORADOR BIGINT,
  F_DATA_INICIAL    DATE,
  F_DATA_FINAL      DATE,
  F_TIMEZONE        TEXT,
  F_LIMIT           INTEGER,
  F_OFFSET          BIGINT)
  RETURNS TABLE(
    COD_CHECKLIST                 BIGINT,
    COD_CHECKLIST_MODELO          BIGINT,
    DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
    KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
    DURACAO_REALIZACAO_MILLIS     BIGINT,
    CPF_COLABORADOR               BIGINT,
    PLACA_VEICULO                 TEXT,
    TIPO_CHECKLIST                CHAR,
    NOME_COLABORADOR              TEXT,
    TOTAL_ITENS_OK                BIGINT,
    TOTAL_ITENS_NOK               BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  RESPOSTA_OK        VARCHAR(2) := 'OK';
  F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
  F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO                                              AS COD_CHECKLIST,
    C.COD_CHECKLIST_MODELO                                AS COD_CHECKLIST_MODELO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE                   AS DATA_HORA_REALIZACAO,
    C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE  AS DATA_HORA_IMPORTADO_PROLOG,
    C.KM_VEICULO                                          AS KM_VEICULO_MOMENTO_REALIZACAO,
    C.TEMPO_REALIZACAO                                    AS DURACAO_REALIZACAO_MILLIS,
    C.CPF_COLABORADOR                                     AS CPF_COLABORADOR,
    C.PLACA_VEICULO :: TEXT                               AS PLACA_VEICULO,
    C.TIPO                                                AS TIPO_CHECKLIST,
    CO.NOME :: TEXT                                       AS NOME_COLABORADOR,
    (SELECT COUNT(*) - COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_OK,
    (SELECT COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_NOK
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
  WHERE C.CPF_COLABORADOR = F_CPF_COLABORADOR
        AND (F_HAS_DATA_INICIAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL)
        AND (F_HAS_DATA_FINAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL)
  ORDER BY C.DATA_HORA DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
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
-- PL-2076
-- CRIA TABELA PARA ARMAZENAR AS EMPRESAS EM QUE O CHECK DE DIFERENTES UNIDADES ESTÁ BLOQUEADO.
CREATE TABLE IF NOT EXISTS CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA (
  COD_EMPRESA BIGINT NOT NULL,
  CONSTRAINT PK_CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA PRIMARY KEY (COD_EMPRESA),
  CONSTRAINT FK_CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO)
);
COMMENT ON TABLE CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA
IS 'Essa tabela contém as empresas em que a realização de checklist de diferentes unidades está bloqueada para os usuários.
Isso é especialmente útil em caso de integrações onde o sistema integrado não suporta isso.';

-- BLOQUEAMOS A EMPRESA AVILAN POIS A INTEGRAÇÃO LIMITA ESTE FUNCIONAMENTO.
INSERT INTO CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(COD_EMPRESA) VALUES (2);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2077
CREATE FUNCTION FUNC_CHECKLIST_REALIZACAO_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT EXISTS(SELECT CDUEB.COD_EMPRESA
                FROM CHECKLIST_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA CDUEB
                WHERE CDUEB.COD_EMPRESA = F_COD_EMPRESA);
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
-- PL-2085
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REGIONAIS_UNIDADES_SELECAO(F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    CODIGO_REGIONAL                                            BIGINT,
    NOME_REGIONAL                                              TEXT,
    CODIGO_UNIDADE                                             BIGINT,
    NOME_UNIDADE                                               TEXT,
    REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  EMPRESA_BLOQUEADA_DIFERENTES_UNIDADES BOOLEAN := FUNC_CHECKLIST_REALIZACAO_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(
      (SELECT C.COD_EMPRESA
       FROM COLABORADOR C
       WHERE C.CODIGO = F_COD_COLABORADOR));
BEGIN
  RETURN QUERY
  SELECT DISTINCT ON (F.CODIGO_UNIDADE)
    F.CODIGO_REGIONAL                     AS CODIGO_REGIONAL,
    F.NOME_REGIONAL                       AS NOME_REGIONAL,
    F.CODIGO_UNIDADE                      AS CODIGO_UNIDADE,
    F.NOME_UNIDADE                        AS NOME_UNIDADE,
    EMPRESA_BLOQUEADA_DIFERENTES_UNIDADES AS REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA
  FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
  ORDER BY F.CODIGO_UNIDADE ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;