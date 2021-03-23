BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
--##################################              TABELAS NECESSÁRIAS            #######################################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE IF NOT EXISTS INTEGRACAO.PNEU_CADASTRADO(
  COD_PNEU_CADASTRO_PROLOG    BIGINT NOT NULL,
  COD_PNEU_SISTEMA_INTEGRADO  BIGINT NOT NULL,
  COD_EMPRESA_CADASTRO        BIGINT NOT NULL,
  COD_UNIDADE_CADASTRO        BIGINT NOT NULL,
  COD_CLIENTE_PNEU_CADASTRO   TEXT NOT NULL,
  TOKEN_AUTENTICACAO_CADASTRO TEXT NOT NULL,
  DATA_HORA_CADASTRO_PROLOG   TIMESTAMP WITH TIME ZONE NOT NULL,
  DATA_HORA_ULTIMA_EDICAO     TIMESTAMP WITH TIME ZONE,
  CONSTRAINT UNIQUE_PNEU_CADASTRADO_EMPRESA_INTEGRACAO UNIQUE (COD_EMPRESA_CADASTRO, COD_CLIENTE_PNEU_CADASTRO),
  CONSTRAINT FK_PNEU_CADASTRO_EMPRESA FOREIGN KEY (COD_EMPRESA_CADASTRO) REFERENCES PUBLIC.EMPRESA(CODIGO),
  CONSTRAINT FK_PNEU_CADASTRO_UNIDADE FOREIGN KEY (COD_UNIDADE_CADASTRO) REFERENCES PUBLIC.UNIDADE(CODIGO),
  CONSTRAINT FK_PNEU_CADASTRO_PNEU FOREIGN KEY (COD_PNEU_CADASTRO_PROLOG) REFERENCES PUBLIC.PNEU_DATA(CODIGO)
);
COMMENT ON TABLE INTEGRACAO.PNEU_CADASTRADO
IS 'Tabela utilizada para salvar os pneus que foram cadastrados no ProLog a partir de integrações';

--######################################################################################################################
--######################################################################################################################
--##################################             FUNCTIONS RECRIADAS             #######################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_GARANTE_EMPRESA_EXISTE(BIGINT);
DROP FUNCTION FUNC_GARANTE_UNIDADE_EXISTE(BIGINT);
DROP FUNCTION FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(BIGINT, BIGINT);

CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA BIGINT, F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL, FORMAT('Empresa de código %s não existe!', F_COD_EMPRESA), F_ERROR_MESSAGE);
BEGIN
  IF NOT EXISTS(SELECT E.CODIGO
                FROM EMPRESA E
                WHERE E.CODIGO = F_COD_EMPRESA)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE BIGINT, F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL, FORMAT('Unidade de código %s não existe!', F_COD_UNIDADE), F_ERROR_MESSAGE);
BEGIN
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  ERROR_MESSAGE TEXT :=
  F_IF(F_ERROR_MESSAGE IS NULL,
       FORMAT('A unidade %s não pertence a empresa %s!', F_COD_UNIDADE, F_COD_EMPRESA),
       F_ERROR_MESSAGE);
BEGIN
  -- Verifica se a unidade é da empresa informada.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE AND U.COD_EMPRESA = F_COD_EMPRESA)
  THEN PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
  END IF;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_ERROR_MESSAGE TEXT DEFAULT NULL)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA, F_ERROR_MESSAGE);
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE, F_ERROR_MESSAGE);
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE, F_ERROR_MESSAGE);
END;
$$;

--######################################################################################################################
--######################################################################################################################
--##################################            FUNCTIONS NECESSÁRIAS            #######################################
--######################################################################################################################
--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(
  F_COD_UNIDADE_PNEU      BIGINT,
  F_COD_PNEU_PROLOG       BIGINT,
  F_COD_MODELO_BANDA_PNEU BIGINT,
  F_VALOR_BANDA_PNEU      REAL,
  F_VIDA_NOVA_PNEU        INTEGER)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO BIGINT := (SELECT PTS.CODIGO
                                                       FROM PNEU_TIPO_SERVICO AS PTS
                                                       WHERE PTS.COD_EMPRESA IS NULL
                                                             AND PTS.STATUS_ATIVO = TRUE
                                                             AND PTS.INCREMENTA_VIDA = TRUE
                                                             AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE);
  FONTE_SERVICO_REALIZADO_CADASTRO TEXT := 'FONTE_CADASTRO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  IS_PRIMEIRA_VIDA BOOLEAN := F_VIDA_NOVA_PNEU < 2;
  COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos que o pneu pode receber o serviço de incremento de vida.
  IF (IS_PRIMEIRA_VIDA)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        'Não é possível aplicar um serviço de troca de banda em um pneu na primeira vida');
  END IF;

  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_PNEU_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_CADASTRO) RETURNING CODIGO INTO COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_PNEU_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_CADASTRO);

  INSERT INTO PUBLIC.PNEU_SERVICO_CADASTRO(
    COD_PNEU,
    COD_PNEU_SERVICO_REALIZADO)
  VALUES (F_COD_PNEU_PROLOG,
          COD_SERVICO_REALIZADO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;

--######################################################################################################################
-- PL-2222
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(
  F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
  F_CODIGO_PNEU_CLIENTE        CHARACTER VARYING,
  F_COD_UNIDADE_PNEU           BIGINT,
  F_COD_MODELO_PNEU            BIGINT,
  F_COD_DIMENSAO_PNEU          BIGINT,
  F_PRESSAO_CORRETA_PNEU       DOUBLE PRECISION,
  F_VIDA_ATUAL_PNEU            INTEGER,
  F_VIDA_TOTAL_PNEU            INTEGER,
  F_DOT_PNEU                   CHARACTER VARYING,
  F_VALOR_PNEU                 NUMERIC,
  F_PNEU_NOVO_NUNCA_RODADO     BOOLEAN,
  F_COD_MODELO_BANDA_PNEU      BIGINT,
  F_VALOR_BANDA_PNEU           NUMERIC,
  F_DATA_HORA_PNEU_CADASTRO    TIMESTAMP WITHOUT TIME ZONE,
  F_TOKEN_INTEGRACAO           CHARACTER VARYING)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  PNEU_PRIMEIRA_VIDA   BIGINT  := 1;
  PNEU_STATUS_ESTOQUE  TEXT    := 'ESTOQUE';
  PNEU_POSSUI_BANDA    BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
  COD_EMPRESA_PNEU     BIGINT  := (SELECT TI.COD_EMPRESA
                                   FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                   WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_PNEU_PROLOG      BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  -- Validamos se a Unidade repassada existe.
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                      FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                             F_COD_UNIDADE_PNEU));

  -- Validamos se a Unidade pertence a Empresa do token repassado.
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                              F_COD_UNIDADE_PNEU,
                                              FORMAT('A Unidade %s não está configurada para esta empresa',
                                                     F_COD_UNIDADE_PNEU));

  -- Validamos se o pneu já existe no ProLog.
  IF (SELECT EXISTS(SELECT P.CODIGO FROM PUBLIC.PNEU P WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
                                                             AND P.COD_EMPRESA = COD_EMPRESA_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu %s já está cadastrada no Sistema ProLog', F_CODIGO_PNEU_CLIENTE));
  END IF;

  -- Validamos se o código do sistema integrado já está mapeado na tabela.
  IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                    FROM INTEGRACAO.PNEU_CADASTRADO PC
                    WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                          AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s já está cadastrada no Sistema ProLog',
                                              F_COD_PNEU_SISTEMA_INTEGRADO));
  END IF;

  -- Validamos se o modelo do pneu está mapeado.
  IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                        FROM PUBLIC.MODELO_PNEU MP
                        WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                              AND MP.CODIGO = F_COD_MODELO_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado no Sistema ProLog',
                                              F_COD_MODELO_PNEU));
  END IF;

  -- Validamos se a dimensão do pneu está mapeada.
  IF (SELECT NOT EXISTS(SELECT DP.CODIGO
                        FROM PUBLIC.DIMENSAO_PNEU DP
                        WHERE DP.CODIGO = F_COD_DIMENSAO_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada no Sistema ProLog',
                                              F_COD_DIMENSAO_PNEU));
  END IF;

  -- Validamos se a pressão recomendada é válida.
  IF (F_PRESSAO_CORRETA_PNEU < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('A pressão recomendada para o pneu não pode ser um número negativo');
  END IF;

  -- Validamos se a vida atual é correta.
  IF (F_VIDA_ATUAL_PNEU < PNEU_PRIMEIRA_VIDA)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida atual do pneu deve ser no mínimo 1 (caso novo)');
  END IF;

  -- Validamos se a vida total é válida.
  IF (F_VIDA_TOTAL_PNEU < F_VIDA_ATUAL_PNEU)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida total do pneu não pode ser menor que a vida atual');
  END IF;

  -- Validamos se o valor do pneu é um valor válido.
  IF (F_VALOR_PNEU < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
  END IF;

  -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND F_COD_MODELO_BANDA_PNEU IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O pneu não está na primeira vida, deve ser informado um modelo de banda');
  END IF;

  -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                               FROM PUBLIC.MODELO_BANDA MB
                                               WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                     AND MB.CODIGO = F_COD_MODELO_BANDA_PNEU)))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado no Sistema ProLog',
                                              F_COD_MODELO_BANDA_PNEU));
  END IF;

  -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        'O pneu não está na primeira vida, deve ser informado o valor da banda aplicada');
  END IF;

  -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
  END IF;

  INSERT INTO PUBLIC.PNEU(
    COD_EMPRESA,
    COD_UNIDADE_CADASTRO,
    COD_UNIDADE,
    CODIGO_CLIENTE,
    COD_MODELO,
    COD_DIMENSAO,
    PRESSAO_RECOMENDADA,
    PRESSAO_ATUAL,
    ALTURA_SULCO_INTERNO,
    ALTURA_SULCO_CENTRAL_INTERNO,
    ALTURA_SULCO_CENTRAL_EXTERNO,
    ALTURA_SULCO_EXTERNO,
    STATUS,
    VIDA_ATUAL,
    VIDA_TOTAL,
    DOT,
    VALOR,
    COD_MODELO_BANDA,
    PNEU_NOVO_NUNCA_RODADO,
    DATA_HORA_CADASTRO)
  VALUES (
    COD_EMPRESA_PNEU,
    F_COD_UNIDADE_PNEU,
    F_COD_UNIDADE_PNEU,
    F_CODIGO_PNEU_CLIENTE,
    F_COD_MODELO_PNEU,
    F_COD_DIMENSAO_PNEU,
    F_PRESSAO_CORRETA_PNEU,
    0,    -- PRESSAO_ATUAL
    NULL, -- ALTURA_SULCO_INTERNO
    NULL, -- ALTURA_SULCO_CENTRAL_INTERNO
    NULL, -- ALTURA_SULCO_CENTRAL_EXTERNO
    NULL, -- ALTURA_SULCO_EXTERNO
    PNEU_STATUS_ESTOQUE,
    F_VIDA_ATUAL_PNEU,
    F_VIDA_TOTAL_PNEU,
    F_DOT_PNEU,
    F_VALOR_PNEU,
    F_IF(PNEU_POSSUI_BANDA, F_COD_MODELO_BANDA_PNEU, NULL),
    F_IF(PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO), -- Forçamos FALSE caso o pneu já possua uma banda aplicada.
    F_DATA_HORA_PNEU_CADASTRO) RETURNING CODIGO INTO COD_PNEU_PROLOG;

  INSERT INTO INTEGRACAO.PNEU_CADASTRADO(
    COD_PNEU_CADASTRO_PROLOG,
    COD_PNEU_SISTEMA_INTEGRADO,
    COD_EMPRESA_CADASTRO,
    COD_UNIDADE_CADASTRO,
    COD_CLIENTE_PNEU_CADASTRO,
    TOKEN_AUTENTICACAO_CADASTRO,
    DATA_HORA_CADASTRO_PROLOG)
  VALUES (
    COD_PNEU_PROLOG,
    F_COD_PNEU_SISTEMA_INTEGRADO,
    COD_EMPRESA_PNEU,
    F_COD_UNIDADE_PNEU,
    F_CODIGO_PNEU_CLIENTE,
    F_TOKEN_INTEGRACAO,
    F_DATA_HORA_PNEU_CADASTRO);

  -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
  IF (PNEU_POSSUI_BANDA)
  THEN
    PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                                  COD_PNEU_PROLOG,
                                                                  F_COD_MODELO_BANDA_PNEU,
                                                                  F_VALOR_BANDA_PNEU,
                                                                  F_VIDA_ATUAL_PNEU);
  END IF;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a inserção na tabela de mapeamento ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    RAISE EXCEPTION
    'Não foi possível inserir o pneu "%" na tabela de mapeamento', F_CODIGO_PNEU_CLIENTE;
  END IF;

  RETURN COD_PNEU_PROLOG;
END;
$$;

--######################################################################################################################

-- PL-2222
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_PNEU_PROLOG(
  F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
  F_NOVO_CODIGO_PNEU_CLIENTE   TEXT,
  F_NOVO_COD_MODELO_PNEU       BIGINT,
  F_NOVO_COD_DIMENSAO_PNEU     BIGINT,
  F_NOVO_DOT_PNEU              TEXT,
  F_NOVO_VALOR_PNEU            REAL,
  F_NOVO_COD_MODELO_BANDA_PNEU BIGINT,
  F_NOVO_VALOR_BANDA_PNEU      REAL,
  F_DATA_HORA_EDICAO_PNEU      TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO           TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_PNEU BIGINT := (SELECT TI.COD_EMPRESA
                              FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                              WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_PNEU_PROLOG BIGINT := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                             FROM INTEGRACAO.PNEU_CADASTRADO PC
                             WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                   AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
  PNEU_POSSUI_BANDA BOOLEAN := F_IF(((SELECT P.COD_MODELO_BANDA
                                      FROM PUBLIC.PNEU P
                                      WHERE P.CODIGO = COD_PNEU_PROLOG) IS NULL), FALSE, TRUE);
  TROCOU_BANDA_PNEU BOOLEAN := F_IF(F_NOVO_COD_MODELO_BANDA_PNEU IS NULL, FALSE, TRUE);
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN

  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
  IF (COD_PNEU_PROLOG IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                              F_COD_PNEU_SISTEMA_INTEGRADO));
  END IF;

  -- Validamos se o novo_codigo_cliente é um código válido ou já possui um igual na base dados.
  IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                    FROM INTEGRACAO.PNEU_CADASTRADO PC
                    WHERE PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
                          AND PC.COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE
                          AND PC.COD_PNEU_SISTEMA_INTEGRADO != F_COD_PNEU_SISTEMA_INTEGRADO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu com o código %s cadastrado no Sistema ProLog',
                                              F_NOVO_CODIGO_PNEU_CLIENTE));
  END IF;

  -- Validamos se o modelo do pneu está mapeado.
  IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                        FROM PUBLIC.MODELO_PNEU MP
                        WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                              AND MP.CODIGO = F_NOVO_COD_MODELO_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado', F_NOVO_COD_MODELO_PNEU));
  END IF;

  -- Validamos se a dimensão do pneu está mapeada.
  IF (SELECT NOT EXISTS(SELECT DP.CODIGO
                        FROM PUBLIC.DIMENSAO_PNEU DP
                        WHERE DP.CODIGO = F_NOVO_COD_DIMENSAO_PNEU))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada',
                                              F_NOVO_COD_DIMENSAO_PNEU));
  END IF;

  -- Validamos se o valor do pneu é um valor válido.
  IF (F_NOVO_VALOR_PNEU < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
  END IF;

  -- Validamos se o pneu possui banda e se ela não foi removida na atualização.
  IF (PNEU_POSSUI_BANDA AND F_NOVO_COD_MODELO_BANDA_PNEU IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O modelo da banda do pneu deve ser informado');
  END IF;

  -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                               FROM PUBLIC.MODELO_BANDA MB
                                               WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                     AND MB.CODIGO = F_NOVO_COD_MODELO_BANDA_PNEU)))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado',
                                              F_NOVO_COD_MODELO_BANDA_PNEU));
  END IF;

  -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda e a mesma tiver sido
  -- atualizada.
  IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        'Você está trocando a banda, deve ser informado o valor da nova banda aplicada');
  END IF;

  -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
  IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL AND F_NOVO_VALOR_BANDA_PNEU < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da nova banda do pneu não pode ser um número negativo');
  END IF;

  UPDATE PUBLIC.PNEU
  SET
    CODIGO_CLIENTE = F_NOVO_CODIGO_PNEU_CLIENTE,
    COD_MODELO = F_NOVO_COD_MODELO_PNEU,
    COD_DIMENSAO = F_NOVO_COD_DIMENSAO_PNEU,
    DOT = F_NOVO_DOT_PNEU,
    VALOR = F_NOVO_VALOR_PNEU,
    COD_MODELO_BANDA = F_IF(PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU, F_NOVO_COD_MODELO_BANDA_PNEU, NULL)
  WHERE CODIGO = COD_PNEU_PROLOG;

  UPDATE INTEGRACAO.PNEU_CADASTRADO
  SET
    DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_PNEU
  WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
        AND COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a atualização na tabela de mapeamento ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    RAISE EXCEPTION
    'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
  END IF;

  IF (PNEU_POSSUI_BANDA
      AND NOT (SELECT * FROM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(COD_PNEU_PROLOG,
                                                                 F_NOVO_COD_MODELO_BANDA_PNEU,
                                                                 F_NOVO_VALOR_BANDA_PNEU)))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('Não foi possível atualizar a banda do pneu');
  END IF;

  RETURN COD_PNEU_PROLOG;
END;
$$;

--######################################################################################################################

-- CREATE OR REPLACE FUNCTION IS_PLACA_POSICAO_PNEU_VALIDA(
--   F_COD_VEICULO    BIGINT,
--   F_POSICAO_PNEU   INTEGER,
--   F_IS_PNEU_ESTEPE BOOLEAN)
--   RETURNS BOOLEAN
-- LANGUAGE PLPGSQL
-- AS $$
-- DECLARE
--   IS_POSICAO_VALIDA BOOLEAN;
-- BEGIN
--   IF (F_IS_PNEU_ESTEPE)
--   THEN
--     SELECT (F_POSICAO_PNEU >= 900 AND F_POSICAO_PNEU <= 908) INTO IS_POSICAO_VALIDA;
--   ELSE
--     SELECT EXISTS(SELECT
--                     PONU.POSICAO_PROLOG
--                   FROM VEICULO V
--                     JOIN VEICULO_TIPO VT
--                       ON V.COD_TIPO = VT.CODIGO
--                     JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
--                       ON VT.CODIGO = PONU.COD_TIPO_VEICULO
--                          AND V.COD_UNIDADE = PONU.COD_UNIDADE
--                   WHERE V.CODIGO = F_COD_VEICULO
--                         AND PONU.POSICAO_PROLOG = F_POSICAO_PNEU) INTO IS_POSICAO_VALIDA;
--   END IF;
--   RETURN IS_POSICAO_VALIDA;
-- END;
-- $$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(
  F_COD_VEICULO_PROLOG            BIGINT,
  F_PLACA_VEICULO_PNEU_APLICADO   TEXT,
  F_COD_PNEU_PROLOG               BIGINT,
  F_CODIGO_PNEU_CLIENTE           TEXT,
  F_COD_UNIDADE_PNEU              BIGINT,
  F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
  F_IS_POSICAO_ESTEPE             BOOLEAN)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se a placa existe no ProLog.
  IF (F_COD_VEICULO_PROLOG IS NULL OR F_COD_VEICULO_PROLOG <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A placa informada %s não está presente no Sistema ProLog',
                                              F_PLACA_VEICULO_PNEU_APLICADO));
  END IF;

  -- Validamos se o placa e o pneu pertencem a mesma unidade.
  IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG) <> F_COD_UNIDADE_PNEU)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('A placa informada %s  está em uma Unidade diferente do pneu informado %s',
               F_PLACA_VEICULO_PNEU_APLICADO,
               F_CODIGO_PNEU_CLIENTE));
  END IF;

  -- Validamos se a posição repassada é uma posição válida no ProLog.
  IF (NOT IS_PLACA_POSICAO_PNEU_VALIDA(F_COD_VEICULO_PROLOG, F_POSICAO_VEICULO_PNEU_APLICADO, F_IS_POSICAO_ESTEPE))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
               F_POSICAO_VEICULO_PNEU_APLICADO,
               F_PLACA_VEICULO_PNEU_APLICADO));
  END IF;

  -- Validamos se a placa possui algum outro pneu aplicado na posição.
  IF (SELECT EXISTS(SELECT *
                    FROM PUBLIC.VEICULO_PNEU VP
                    WHERE VP.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                          AND VP.COD_UNIDADE = F_COD_UNIDADE_PNEU
                          AND VP.POSICAO = F_POSICAO_VEICULO_PNEU_APLICADO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu na placa %s, posição %s',
                                              F_PLACA_VEICULO_PNEU_APLICADO,
                                              F_POSICAO_VEICULO_PNEU_APLICADO));
  END IF;

  -- Vincula pneu a placa.
  INSERT INTO PUBLIC.VEICULO_PNEU(
    PLACA,
    COD_PNEU,
    COD_UNIDADE,
    POSICAO)
  VALUES (
    F_PLACA_VEICULO_PNEU_APLICADO,
    F_COD_PNEU_PROLOG,
    F_COD_UNIDADE_PNEU,
    F_POSICAO_VEICULO_PNEU_APLICADO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se o update ocorreu como deveria
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível aplicar o pneu %s na placa %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_PLACA_VEICULO_PNEU_APLICADO));
  END IF;

  -- Retornamos sucesso se o pneu estiver aplicado na placa e posição que deveria estar.
  IF (SELECT EXISTS(SELECT VP.POSICAO
                    FROM PUBLIC.VEICULO_PNEU VP
                    WHERE VP.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                          AND VP.COD_PNEU = F_COD_PNEU_PROLOG
                          AND VP.POSICAO = F_POSICAO_VEICULO_PNEU_APLICADO
                          AND VP.COD_UNIDADE = F_COD_UNIDADE_PNEU))
  THEN
    RETURN TRUE;
  ELSE
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível aplicar o pneu %s na placa %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_PLACA_VEICULO_PNEU_APLICADO));
  END IF;
END;
$$;

--######################################################################################################################

-- PL-2222
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(
  F_COD_PNEU_SISTEMA_INTEGRADO    BIGINT,
  F_CODIGO_PNEU_CLIENTE           CHARACTER VARYING,
  F_COD_UNIDADE_PNEU              BIGINT,
  F_COD_MODELO_PNEU               BIGINT,
  F_COD_DIMENSAO_PNEU             BIGINT,
  F_PRESSAO_CORRETA_PNEU          DOUBLE PRECISION,
  F_VIDA_ATUAL_PNEU               INTEGER,
  F_VIDA_TOTAL_PNEU               INTEGER,
  F_DOT_PNEU                      CHARACTER VARYING,
  F_VALOR_PNEU                    NUMERIC,
  F_PNEU_NOVO_NUNCA_RODADO        BOOLEAN,
  F_COD_MODELO_BANDA_PNEU         BIGINT,
  F_VALOR_BANDA_PNEU              NUMERIC,
  F_STATUS_PNEU                   CHARACTER VARYING,
  F_PLACA_VEICULO_PNEU_APLICADO   CHARACTER VARYING,
  F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
  F_DATA_HORA_PNEU_CADASTRO       TIMESTAMP WITHOUT TIME ZONE,
  F_TOKEN_INTEGRACAO              CHARACTER VARYING)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_PNEU BIGINT := (SELECT TI.COD_EMPRESA
                              FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                              WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_VEICULO_PROLOG BIGINT := (SELECT V.CODIGO
                                FROM PUBLIC.VEICULO V
                                WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                      AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                            FROM PUBLIC.UNIDADE U
                                                            WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
  IS_POSICAO_ESTEPE BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                    AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
  STATUS_APLICADO_VEICULO CONSTANT TEXT := 'EM_USO';
  COD_PNEU_PROLOG  BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Inserimos o pneu utilizando a function de inserção padrão.
  SELECT * FROM INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(
      F_COD_PNEU_SISTEMA_INTEGRADO,
      F_CODIGO_PNEU_CLIENTE,
      F_COD_UNIDADE_PNEU,
      F_COD_MODELO_PNEU,
      F_COD_DIMENSAO_PNEU,
      F_PRESSAO_CORRETA_PNEU,
      F_VIDA_ATUAL_PNEU,
      F_VIDA_TOTAL_PNEU,
      F_DOT_PNEU,
      F_VALOR_PNEU,
      F_PNEU_NOVO_NUNCA_RODADO,
      F_COD_MODELO_BANDA_PNEU,
      F_VALOR_BANDA_PNEU,
      F_DATA_HORA_PNEU_CADASTRO,
      F_TOKEN_INTEGRACAO) INTO COD_PNEU_PROLOG;

  -- Validamos se a inserção do pneu aconteceu com sucesso.
  IF (COD_PNEU_PROLOG IS NULL OR COD_PNEU_PROLOG <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível cadastrar o pneu %s no Sistema ProLog',
                                              F_CODIGO_PNEU_CLIENTE));
  END IF;

  -- Atualiza o pneu para o status em que ele deve estar.
  UPDATE PUBLIC.PNEU
  SET
    STATUS = F_STATUS_PNEU
  WHERE CODIGO = COD_PNEU_PROLOG;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Validamos se o status do pneu foi atualizado com sucesso
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível inserir o pneu %s com status %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_STATUS_PNEU));
  END IF;

  -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
  IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
  THEN
    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
    THEN
      UPDATE PUBLIC.PNEU
      SET
        COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
      WHERE CODIGO = COD_PNEU_PROLOG;

      SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
    END IF;

    PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
                                                            F_PLACA_VEICULO_PNEU_APLICADO,
                                                            COD_PNEU_PROLOG,
                                                            F_CODIGO_PNEU_CLIENTE,
                                                            F_COD_UNIDADE_PNEU,
                                                            F_POSICAO_VEICULO_PNEU_APLICADO,
                                                            IS_POSICAO_ESTEPE);
  END IF;
  RETURN COD_PNEU_PROLOG;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--###############################    LISTAGEM DE MARCAS E MODELOS DE PNEUS E BANDA    ##################################
--######################################################################################################################
--######################################################################################################################
-- PL-2225
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_PNEUS_EMPRESA(
  F_TOKEN_INTEGRACAO           TEXT,
  F_COD_MARCA_PNEU             BIGINT DEFAULT NULL,
  F_APENAS_MODELOS_PNEU_ATIVOS BOOLEAN DEFAULT TRUE)
  RETURNS TABLE(
    COD_EMPRESA_MODELO_PNEU   BIGINT,
    COD_MARCA_PNEU            BIGINT,
    COD_MODELO_PNEU           BIGINT,
    NOME_MODELO_PNEU          TEXT,
    QTD_SULCOS_MODELO_PNEU    INTEGER,
    ALTURA_SULCOS_NODELO_PNEU REAL,
    STATUS_ATIVO_MODELO_PNEU  BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    MP.COD_EMPRESA        AS COD_EMPRESA_MODELO_PNEU,
    MP.COD_MARCA          AS COD_MARCA_PNEU,
    MP.CODIGO             AS COD_MODELO_PNEU,
    MP.NOME::TEXT         AS NOME_MODELO_PNEU,
    MP.QT_SULCOS::INTEGER AS QTD_SULCOS_MODELO_PNEU,
    MP.ALTURA_SULCOS      AS ALTURA_SULCOS_NODELO_PNEU,
    TRUE                  AS STATUS_ATIVO_MODELO_PNEU
  FROM PUBLIC.MODELO_PNEU MP
  WHERE MP.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
        AND F_IF(F_COD_MARCA_PNEU IS NULL, TRUE, MP.COD_MARCA = F_COD_MARCA_PNEU)
  ORDER BY MP.COD_MARCA, MP.CODIGO;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_PNEUS_EMPRESA(
  F_TOKEN_INTEGRACAO        TEXT,
  APENAS_MARCAS_PNEU_ATIVAS BOOLEAN)
  RETURNS TABLE(
    COD_MARCA_PNEU            BIGINT,
    NOME_MARCA_PNEU           TEXT,
    STATUS_ATIVO_MARCA_PNEU   BOOLEAN,
    COD_MODELO_PNEU           BIGINT,
    NOME_MODELO_PNEU          TEXT,
    COD_EMPRESA_MARCA_MODELO  BIGINT,
    QTD_SULCOS_MODELO_PNEU    INTEGER,
    ALTURA_SULCOS_NODELO_PNEU REAL,
    STATUS_ATIVO_MODELO_PNEU  BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    MP.CODIGO                     AS COD_MARCA_PNEU,
    MP.NOME::TEXT                 AS NOME_MARCA_PNEU,
    TRUE                          AS STATUS_ATIVO_MARCA_PNEU,
    MPE.COD_MODELO_PNEU           AS COD_MODELO_PNEU,
    MPE.NOME_MODELO_PNEU          AS NOME_MODELO_PNEU,
    MPE.COD_EMPRESA_MODELO_PNEU   AS COD_EMPRESA_MARCA_MODELO,
    MPE.QTD_SULCOS_MODELO_PNEU    AS QTD_SULCOS_MODELO_PNEU,
    MPE.ALTURA_SULCOS_NODELO_PNEU AS ALTURA_SULCOS_NODELO_PNEU,
    MPE.STATUS_ATIVO_MODELO_PNEU  AS STATUS_ATIVO_MODELO_PNEU
  FROM PUBLIC.MARCA_PNEU MP
    LEFT JOIN (SELECT * FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_PNEUS_EMPRESA(F_TOKEN_INTEGRACAO)) MPE
      ON MPE.COD_MARCA_PNEU = MP.CODIGO
  ORDER BY MP.CODIGO, MPE.COD_MODELO_PNEU;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_BANDAS_EMPRESA(
  F_TOKEN_INTEGRACAO            TEXT,
  F_COD_MARCA_BANDA             BIGINT DEFAULT NULL,
  F_APENAS_MODELOS_BANDA_ATIVOS BOOLEAN DEFAULT TRUE)
  RETURNS TABLE(
    COD_EMPRESA_MODELO_BANDA   BIGINT,
    COD_MARCA_BANDA            BIGINT,
    COD_MODELO_BANDA           BIGINT,
    NOME_MODELO_BANDA          TEXT,
    QTS_SULCOS_MODELO_BANDA    INTEGER,
    ALTURA_SULCOS_MODELO_BANDA REAL,
    STATUS_ATIVO_MODELO_BANDA  BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    MB.COD_EMPRESA        AS COD_EMPRESA_MODELO_BANDA,
    MB.COD_MARCA          AS COD_MARCA_BANDA,
    MB.CODIGO             AS COD_MODELO_BANDA,
    MB.NOME::TEXT         AS NOME_MODELO_BANDA,
    MB.QT_SULCOS::INTEGER AS QTS_SULCOS_MODELO_BANDA,
    MB.ALTURA_SULCOS      AS ALTURA_SULCOS_MODELO_BANDA,
    TRUE                  AS STATUS_ATIVO_MODELO_BANDA
  FROM PUBLIC.MODELO_BANDA MB
  WHERE MB.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                           FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                           WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
        AND F_IF(F_COD_MARCA_BANDA IS NULL, TRUE, MB.COD_MARCA = F_COD_MARCA_BANDA)
  ORDER BY MB.CODIGO;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_BANDA_EMPRESA(
  F_TOKEN_INTEGRACAO        TEXT,
  APENAS_MARCAS_PNEU_ATIVAS BOOLEAN)
  RETURNS TABLE(
    COD_MARCA_BANDA                BIGINT,
    NOME_MARCA_BANDA               TEXT,
    STATUS_ATIVO_MARCA_BADA        BOOLEAN,
    COD_MODELO_BANDA               BIGINT,
    NOME_MODELO_BANDA              TEXT,
    COD_EMPRESA_MARCA_MODELO_BANDA BIGINT,
    QTS_SULCOS_MODELO_BANDA        INTEGER,
    ALTURA_SULCOS_MODELO_BANDA     REAL,
    STATUS_ATIVO_MODELO_BANDA      BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    MB.CODIGO                      AS COD_MARCA_BANDA,
    MB.NOME::TEXT                  AS NOME_MARCA_BANDA,
    TRUE                           AS STATUS_ATIVO_MARCA_BADA,
    MBE.COD_MODELO_BANDA           AS COD_MODELO_BANDA,
    MBE.NOME_MODELO_BANDA          AS NOME_MODELO_BANDA,
    MBE.COD_EMPRESA_MODELO_BANDA   AS COD_EMPRESA_MARCA_MODELO_BANDA,
    MBE.QTS_SULCOS_MODELO_BANDA    AS QTS_SULCOS_MODELO_BANDA,
    MBE.ALTURA_SULCOS_MODELO_BANDA AS ALTURA_SULCOS_MODELO_BANDA,
    MBE.STATUS_ATIVO_MODELO_BANDA  AS STATUS_ATIVO_MODELO_BANDA
  FROM PUBLIC.MARCA_BANDA MB
    LEFT JOIN (SELECT * FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_BANDAS_EMPRESA(F_TOKEN_INTEGRACAO)) MBE
      ON MBE.COD_MARCA_BANDA = MB.CODIGO
  ORDER BY MB.CODIGO, MBE.COD_MODELO_BANDA;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--###############################   LISTAGEM DE UNIDADES PARA DE-PARA EM INTEGRAÇÕES  ##################################
--######################################################################################################################
--######################################################################################################################
-- PL-2226
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_UNIDADE_LISTA_UNIDADES_EMPRESA(
  F_TOKEN_INTEGRACAO       TEXT,
  F_APENAS_UNIDADES_ATIVAS BOOLEAN DEFAULT FALSE)
  RETURNS TABLE(
    COD_EMPRESA  BIGINT,
    CODIGO       BIGINT,
    NOME         TEXT,
    STATUS_ATIVO BOOLEAN)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    U.COD_EMPRESA,
    U.CODIGO,
    U.NOME::TEXT,
    U.STATUS_ATIVO
  FROM PUBLIC.UNIDADE U
  WHERE U.COD_EMPRESA = (SELECT TI.COD_EMPRESA
                         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
        AND F_IF(F_APENAS_UNIDADES_ATIVAS, U.STATUS_ATIVO = TRUE, TRUE)
  ORDER BY U.CODIGO;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--###############################     ALTERAÇÃO DE STATUS DE PNEUS (MOVIMENTAÇÃO)     ##################################
--######################################################################################################################
--######################################################################################################################

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(
  F_COD_UNIDADE_PNEU                 BIGINT,
  F_COD_PNEU_PROLOG                  BIGINT,
  F_COD_MODELO_BANDA_PNEU            BIGINT,
  F_VALOR_BANDA_PNEU                 REAL,
  F_VIDA_NOVA_PNEU                   INTEGER,
  F_COD_TIPO_SERVICO_INCREMENTA_VIDA BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  FONTE_SERVICO_REALIZADO_MOVIMENTACAO TEXT := 'FONTE_MOVIMENTACAO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_PNEU_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    F_COD_TIPO_SERVICO_INCREMENTA_VIDA,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO) RETURNING CODIGO INTO COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_PNEU_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(F_COD_EMPRESA BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_SERVICO_INCREMENTA_VIDA BIGINT := (SELECT PTS.CODIGO
                                         FROM PUBLIC.PNEU_TIPO_SERVICO PTS
                                         WHERE PTS.COD_EMPRESA = F_COD_EMPRESA
                                               AND PTS.INCREMENTA_VIDA IS TRUE
                                               AND PTS.UTILIZADO_CADASTRO_PNEU IS FALSE
                                               AND PTS.STATUS_ATIVO IS TRUE
                                         ORDER BY PTS.CODIGO
                                         LIMIT 1);
  TIPO_SERVICO_RECAPAGEM TEXT := 'RECAPAGEM';
BEGIN
  IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
  THEN
    INSERT INTO PUBLIC.PNEU_TIPO_SERVICO(
      COD_EMPRESA,
      NOME,
      INCREMENTA_VIDA,
      DATA_HORA_CRIACAO)
    VALUES (
      F_COD_EMPRESA,
      TIPO_SERVICO_RECAPAGEM,
      TRUE,
      NOW()) RETURNING CODIGO INTO COD_SERVICO_INCREMENTA_VIDA;
  END IF;
  RETURN COD_SERVICO_INCREMENTA_VIDA;
END;
$$;

--######################################################################################################################
-- PL-2237
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(
  F_COD_PNEU_SISTEMA_INTEGRADO       BIGINT,
  F_CODIGO_PNEU_CLIENTE              CHARACTER VARYING,
  F_COD_UNIDADE_PNEU                 BIGINT,
  F_CPF_COLABORADOR_ALTERACAO_STATUS CHARACTER VARYING,
  F_DATA_HORA_ALTERACAO_STATUS       TIMESTAMP WITHOUT TIME ZONE,
  F_STATUS_PNEU                      CHARACTER VARYING,
  F_TROCOU_DE_BANDA                  BOOLEAN,
  F_COD_NOVO_MODELO_BANDA_PNEU       BIGINT,
  F_VALOR_NOVA_BANDA_PNEU            NUMERIC,
  F_PLACA_VEICULO_PNEU_APLICADO      CHARACTER VARYING,
  F_POSICAO_VEICULO_PNEU_APLICADO    INTEGER,
  F_TOKEN_INTEGRACAO                 CHARACTER VARYING)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_PNEU BIGINT := (SELECT TI.COD_EMPRESA
                              FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                              WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_VEICULO_PROLOG BIGINT := (SELECT V.CODIGO
                                FROM PUBLIC.VEICULO V
                                WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                      AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                            FROM PUBLIC.UNIDADE U
                                                            WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
  IS_POSICAO_ESTEPE BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                    AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
  COD_PNEU_PROLOG BIGINT := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                             FROM INTEGRACAO.PNEU_CADASTRADO PC
                             WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                   AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
  VIDA_ATUAL_PNEU INTEGER := (SELECT P.VIDA_ATUAL FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG);
  PROXIMA_VIDA_PNEU INTEGER := VIDA_ATUAL_PNEU + 1;
  STATUS_APLICADO_VEICULO TEXT := 'EM_USO';
  COD_SERVICO_INCREMENTA_VIDA BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  -- Validamos se a Unidade repassada existe.
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                      FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                             F_COD_UNIDADE_PNEU));

  -- Validamos se a Unidade pertence a Empresa do token repassado.
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                              F_COD_UNIDADE_PNEU,
                                              FORMAT('A Unidade %s não está configurada para esta empresa',
                                                     F_COD_UNIDADE_PNEU));

  -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
  IF (COD_PNEU_PROLOG IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                              F_COD_PNEU_SISTEMA_INTEGRADO));
  END IF;

  -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
  DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = COD_PNEU_PROLOG;

  -- Atualiza o pneu para o status em que ele deve estar.
  UPDATE PUBLIC.PNEU
  SET
    STATUS = F_STATUS_PNEU,
    COD_UNIDADE = F_COD_UNIDADE_PNEU
  WHERE CODIGO = COD_PNEU_PROLOG;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Validamos se o status do pneu foi atualizado com sucesso
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_STATUS_PNEU));
  END IF;

  -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
  IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
  THEN
    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
    THEN
      UPDATE PUBLIC.PNEU
      SET
        COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
      WHERE CODIGO = COD_PNEU_PROLOG;

      SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
    END IF;

    PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
                                                            F_PLACA_VEICULO_PNEU_APLICADO,
                                                            COD_PNEU_PROLOG,
                                                            F_CODIGO_PNEU_CLIENTE,
                                                            F_COD_UNIDADE_PNEU,
                                                            F_POSICAO_VEICULO_PNEU_APLICADO,
                                                            IS_POSICAO_ESTEPE);
  END IF;

  IF (F_TROCOU_DE_BANDA)
  THEN
    -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
    IF (F_COD_NOVO_MODELO_BANDA_PNEU IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O código do modelo da banda deve ser informado');
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF ((SELECT NOT EXISTS(SELECT MB.CODIGO
                           FROM PUBLIC.MODELO_BANDA MB
                           WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                 AND MB.CODIGO = F_COD_NOVO_MODELO_BANDA_PNEU)))
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado no Sistema ProLog',
                                                F_COD_NOVO_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (F_VALOR_NOVA_BANDA_PNEU IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu deve ser informado');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (F_VALOR_NOVA_BANDA_PNEU < 0)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
    END IF;

    -- Busca serviço que incrementa a vida do pneu dentro da empresa em questão.
    SELECT * FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(COD_EMPRESA_PNEU)
    INTO COD_SERVICO_INCREMENTA_VIDA;

    IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('Erro ao vincular banda ao pneu');
    END IF;

    -- Incrementa a vida do pneu simulando um processo de movimentação.
    PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(F_COD_UNIDADE_PNEU,
                                                                      COD_PNEU_PROLOG,
                                                                      F_COD_NOVO_MODELO_BANDA_PNEU,
                                                                      F_VALOR_NOVA_BANDA_PNEU,
                                                                      PROXIMA_VIDA_PNEU,
                                                                      COD_SERVICO_INCREMENTA_VIDA);

    -- Após incrementar a vida e criar o serviço, atualizamos o pneu para ficar com a banda e a vida correta.
    PERFORM PUBLIC.FUNC_PNEUS_INCREMENTA_VIDA_PNEU(COD_PNEU_PROLOG, F_COD_NOVO_MODELO_BANDA_PNEU);
  END IF;

  RETURN COD_PNEU_PROLOG;
END;
$$;
--######################################################################################################################

--######################################################################################################################
-- PL-2296
CREATE OR REPLACE FUNCTION FUNC_PNEU_AFERICAO_SERVICO_TIPO_SERVICO(F_COD_SERVICO BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_SERVICO_MOVIMENTACAO TEXT := 'movimentacao';
BEGIN
  RETURN
  (SELECT AM.TIPO_SERVICO
   FROM AFERICAO_MANUTENCAO AM
   WHERE AM.CODIGO = F_COD_SERVICO) = TIPO_SERVICO_MOVIMENTACAO;
END;
$$;

END TRANSACTION;