BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
--########################### Cria estrutura para deleção lógica de serviços de movimentação ###########################
--######################################################################################################################
--######################################################################################################################

-- Altera nomes das tabelas para conter '_data' no final delas.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    RENAME TO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
    RENAME TO MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO
    RENAME TO MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA;

ALTER TABLE PNEU_SERVICO_REALIZADO
    RENAME TO PNEU_SERVICO_REALIZADO_DATA;

ALTER TABLE PNEU_SERVICO_CADASTRO
    RENAME TO PNEU_SERVICO_CADASTRO_DATA;

-- Adicionamos coluna para saber se o serviço está deletado.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE PNEU_SERVICO_CADASTRO_DATA
    ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- Adicionamos coluna para saber a data_hora que ocorreu a deleção.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    ADD COLUMN DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    ADD COLUMN DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE;

ALTER TABLE PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE;

ALTER TABLE PNEU_SERVICO_CADASTRO_DATA
    ADD COLUMN DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE;

-- Adiciona coluna para identificar o usuário que realizou a deleção.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE PNEU_SERVICO_CADASTRO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

-- Adiciona constraint para garantir a integridade da deleção
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    ADD CONSTRAINT DELETADO_CHECK CHECK (
            (NOT DELETADO)
            OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL));

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    ADD CONSTRAINT DELETADO_CHECK CHECK (
            (NOT DELETADO)
            OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL));

ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    ADD CONSTRAINT DELETADO_CHECK CHECK (
            (NOT DELETADO)
            OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL));

ALTER TABLE PNEU_SERVICO_REALIZADO_DATA
    ADD CONSTRAINT DELETADO_CHECK CHECK (
            (NOT DELETADO)
            OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL));

ALTER TABLE PNEU_SERVICO_CADASTRO_DATA
    ADD CONSTRAINT DELETADO_CHECK CHECK (
            (NOT DELETADO)
            OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL));

-- Criamos as views com os nomes das tabelas antigas, filtrando por NOT DELETADO.
CREATE OR REPLACE VIEW PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA AS
SELECT PSRIVD.COD_SERVICO_REALIZADO,
       PSRIVD.COD_MODELO_BANDA,
       PSRIVD.VIDA_NOVA_PNEU,
       PSRIVD.FONTE_SERVICO_REALIZADO
FROM PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA PSRIVD
WHERE PSRIVD.DELETADO = FALSE;

CREATE OR REPLACE VIEW MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA AS
SELECT MPSRRD.COD_MOVIMENTACAO,
       MPSRRD.COD_SERVICO_REALIZADO_MOVIMENTACAO,
       MPSRRD.COD_RECAPADORA
FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA MPSRRD
WHERE MPSRRD.DELETADO = FALSE;

CREATE OR REPLACE VIEW MOVIMENTACAO_PNEU_SERVICO_REALIZADO AS
SELECT MPSRD.COD_MOVIMENTACAO,
       MPSRD.COD_SERVICO_REALIZADO,
       MPSRD.FONTE_SERVICO_REALIZADO
FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA MPSRD
WHERE MPSRD.DELETADO = FALSE;

CREATE OR REPLACE VIEW PNEU_SERVICO_REALIZADO AS
SELECT PSRD.CODIGO,
       PSRD.COD_TIPO_SERVICO,
       PSRD.COD_UNIDADE,
       PSRD.COD_PNEU,
       PSRD.CUSTO,
       PSRD.VIDA,
       PSRD.FONTE_SERVICO_REALIZADO
FROM PNEU_SERVICO_REALIZADO_DATA PSRD
WHERE PSRD.DELETADO = FALSE;

CREATE OR REPLACE VIEW PNEU_SERVICO_CADASTRO AS
SELECT PSCD.COD_PNEU,
       PSCD.COD_SERVICO_REALIZADO,
       PSCD.FONTE_SERVICO_REALIZADO
FROM PNEU_SERVICO_CADASTRO_DATA PSCD
WHERE PSCD.DELETADO = FALSE;

-- Cria function para deletar lógicamente TODOS os serviços
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_DELETA_SERVICOS_MOVIMENTACAO_PNEU(F_COD_PNEU_PROLOG BIGINT,
                                                           F_DATA_HORA_DELECAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL AS
$$
DECLARE
    -- Códigos dos serviços que estão pendentes e deverão ser deletados.
    COD_SERVICOS_PARA_DELETAR CONSTANT BIGINT[] := ARRAY(SELECT PSRD.CODIGO
                                                         FROM PUBLIC.PNEU_SERVICO_REALIZADO_DATA PSRD
                                                         WHERE PSRD.DELETADO = FALSE
                                                           AND PSRD.COD_PNEU = F_COD_PNEU_PROLOG);
BEGIN
    -- Deleta lógicamente incrementos de vida para o pneu.
    UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE DELETADO = FALSE
      AND COD_SERVICO_REALIZADO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente vínculo entre serviço e recapadora que realizou o servico, para o pneu.
    UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_SERVICO_REALIZADO_MOVIMENTACAO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente vinculo entre serviço e movimentação de origem do serviço, para o pneu.
    UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_SERVICO_REALIZADO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente os serviços realizados no pneu.
    UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Deleta lógicamente os serviços de cadastro realizados no pneu.
    UPDATE PUBLIC.PNEU_SERVICO_CADASTRO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = F_DATA_HORA_DELECAO,
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COD_PNEU = F_COD_PNEU_PROLOG;
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Cria constraint para previnir que sejam inseridos códigos iguais nos pneus integrados da mesma empresa.
ALTER TABLE INTEGRACAO.PNEU_CADASTRADO
    ADD CONSTRAINT UNIQUE_COD_PNEU_SISTEMA_INTEGRADO_EMPRESA UNIQUE (COD_EMPRESA_CADASTRO, COD_PNEU_SISTEMA_INTEGRADO);

--######################################################################################################################
--######################################################################################################################
-- Cria tabela para salvar empresas que tem a sobrescrita de dados de pneus ativa.
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO
(
    COD_EMPRESA BIGINT NOT NULL,
    CONSTRAINT UNIQUE_COD_EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO UNIQUE (COD_EMPRESA),
    CONSTRAINT FK_COD_EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO)
);

-- Já inserimos a empresa PICCOLOTUR - 11, pois vai utilizar essa abordagem
INSERT INTO INTEGRACAO.EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO(COD_EMPRESA)
VALUES (11);
-- Já inserimos a empresa TLX - 45, pois vai utilizar essa abordagem
INSERT INTO INTEGRACAO.EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO(COD_EMPRESA)
VALUES (45);

--######################################################################################################################
--######################################################################################################################
DROP FUNCTION INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU_PROLOG BIGINT,
    F_DATA_HORA_RESOLUCAO TIMESTAMP WITHOUT TIME ZONE);

CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU_PROLOG BIGINT,
                                                            F_DATA_HORA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    UPDATE PUBLIC.AFERICAO_MANUTENCAO
    SET KM_MOMENTO_CONSERTO                  = 0, -- Zero pois no fechamento automatico não há o input de KM
        DATA_HORA_RESOLUCAO                  = F_DATA_HORA_RESOLUCAO,
        FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE,
        FECHADO_AUTOMATICAMENTE_INTEGRACAO   = TRUE
    WHERE COD_PNEU = F_COD_PNEU_PROLOG
      AND DATA_HORA_RESOLUCAO IS NULL;
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- PLI-43
-- Cria function para sobrescrever informações do pneu
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_SOBRESCREVE_PNEU_CADASTRADO(F_COD_PNEU_PROLOG BIGINT,
                                                     F_COD_UNIDADE_PNEU BIGINT,
                                                     F_COD_MODELO_PNEU BIGINT,
                                                     F_COD_DIMENSAO_PNEU BIGINT,
                                                     F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                     F_VIDA_ATUAL_PNEU INTEGER,
                                                     F_VIDA_TOTAL_PNEU INTEGER,
                                                     F_DOT_PNEU CHARACTER VARYING,
                                                     F_VALOR_PNEU NUMERIC,
                                                     F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                     F_COD_MODELO_BANDA_PNEU BIGINT,
                                                     F_VALOR_BANDA_PNEU NUMERIC,
                                                     F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_UNIDADE_ATUAL_PNEU BIGINT;
    VIDA_ATUAL_PNEU        BIGINT;
BEGIN
    SELECT P.COD_UNIDADE, P.VIDA_ATUAL
    FROM PNEU P
    WHERE P.CODIGO = F_COD_PNEU_PROLOG
    INTO COD_UNIDADE_ATUAL_PNEU, VIDA_ATUAL_PNEU;

    -- Devemos remover o vínculo do pneu com qualquer placa. Se o pneu não está aplicado, nada vai acontecer.
    DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = F_COD_PNEU_PROLOG;

    -- Devemos tratar os serviços abertos para o pneu (setar fechado_integracao), apenas se a unidade mudar.
    IF (COD_UNIDADE_ATUAL_PNEU <> F_COD_UNIDADE_PNEU)
    THEN
        PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU_PROLOG,
                                                                        F_DATA_HORA_PNEU_CADASTRO);
    END IF;

    -- Devemos deletar os serviços realizados para o pneu (recapagens, consertos). Apenas se não for a mesma vida.
    IF (VIDA_ATUAL_PNEU <> F_VIDA_ATUAL_PNEU)
    THEN
        -- Deleta todos os serviços do pneu.
        PERFORM INTEGRACAO.FUNC_PNEU_DELETA_SERVICOS_MOVIMENTACAO_PNEU(F_COD_PNEU_PROLOG,
                                                                       F_DATA_HORA_PNEU_CADASTRO);
    ELSE
        -- Se as vidas são iguais, apenas iremos atualizar as informações da banda e custo no serviço já realizado.
        PERFORM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(F_COD_PNEU_PROLOG, F_COD_MODELO_BANDA_PNEU, F_VALOR_BANDA_PNEU);
    END IF;

    -- Devemos mudar o status do pneu para ESTOQUE e atualizar as informações do pneu.
    UPDATE PUBLIC.PNEU
    SET STATUS                 = 'ESTOQUE',
        COD_UNIDADE            = F_COD_UNIDADE_PNEU,
        COD_MODELO             = F_COD_MODELO_PNEU,
        COD_DIMENSAO           = F_COD_DIMENSAO_PNEU,
        PRESSAO_RECOMENDADA    = F_PRESSAO_CORRETA_PNEU,
        VIDA_ATUAL             = F_VIDA_ATUAL_PNEU,
        VIDA_TOTAL             = F_VIDA_TOTAL_PNEU,
        DOT                    = F_DOT_PNEU,
        VALOR                  = F_VALOR_PNEU,
        PNEU_NOVO_NUNCA_RODADO = F_PNEU_NOVO_NUNCA_RODADO,
        COD_MODELO_BANDA       = F_IF(F_COD_MODELO_BANDA_PNEU IS NULL, NULL, F_COD_MODELO_BANDA_PNEU)
    WHERE CODIGO = F_COD_PNEU_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                                   F_COD_MODELO_PNEU BIGINT,
                                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                                   F_DOT_PNEU CHARACTER VARYING,
                                                                   F_VALOR_PNEU NUMERIC,
                                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING,
                                                                   F_DEVE_SOBRESCREVER_PNEU BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PNEU_PRIMEIRA_VIDA  CONSTANT BIGINT  := 1;
    PNEU_STATUS_ESTOQUE CONSTANT TEXT    := 'ESTOQUE';
    PNEU_POSSUI_BANDA   CONSTANT BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_EMPRESA_PNEU    CONSTANT BIGINT  := (SELECT TI.COD_EMPRESA
                                             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    PNEU_ESTA_NO_PROLOG CONSTANT BOOLEAN := (SELECT EXISTS(SELECT P.CODIGO
                                                           FROM PUBLIC.PNEU P
                                                           WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
                                                             AND P.COD_EMPRESA = COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG              BIGINT;
    F_QTD_ROWS_AFETADAS          BIGINT;
BEGIN
    -- Validamos se a Empresa existe.
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
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT(
                'O pneu %s não está na primeira vida, deve ser informado um modelo de banda',
                F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda %s do pneu não está mapeado no Sistema ProLog',
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

    -- Validamos se o código do sistema integrado já está mapeado na tabela, apenas se não estiver devemos sobrescrever.
    -- Pode acontecer o caso onde o pneu está na base do ProLog e é rodado a sobrecarga. Neste cenário o pneu deve
    -- apenas ter as informações sobrescritas e a tabela de vínculo atualizada.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                        AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU) AND NOT F_DEVE_SOBRESCREVER_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s já está cadastrado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Já validamos se o pneu existe no ProLog através código do sistema integrado, então sobrescrevemos as
    -- informações dele ou, caso não deva sobrescrever, inserimos no base. Validamos também se o pneu já está na base
    -- do ProLog, caso ele não esteja, deveremos inserir e não sobrescrever.
    IF (PNEU_ESTA_NO_PROLOG AND F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Pegamos o código do pneu que iremos sobrescrever.
        SELECT P.CODIGO
        FROM PUBLIC.PNEU P
        WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
          AND P.COD_EMPRESA = COD_EMPRESA_PNEU
        INTO COD_PNEU_PROLOG;

        -- Sebrescrevemos os dados do pneu.
        PERFORM INTEGRACAO.FUNC_PNEU_SOBRESCREVE_PNEU_CADASTRADO(COD_PNEU_PROLOG,
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
                                                                 F_DATA_HORA_PNEU_CADASTRO);
    ELSEIF (NOT PNEU_ESTA_NO_PROLOG)
    THEN
        -- Deveremos inserir os dados na base.
        INSERT INTO PUBLIC.PNEU(COD_EMPRESA,
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
        VALUES (COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_COD_MODELO_PNEU,
                F_COD_DIMENSAO_PNEU,
                F_PRESSAO_CORRETA_PNEU,
                0, -- PRESSAO_ATUAL
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
                F_DATA_HORA_PNEU_CADASTRO)
        RETURNING CODIGO INTO COD_PNEU_PROLOG;

        -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
        IF (PNEU_POSSUI_BANDA)
        THEN
            PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                                          COD_PNEU_PROLOG,
                                                                          F_COD_MODELO_BANDA_PNEU,
                                                                          F_VALOR_BANDA_PNEU,
                                                                          F_VIDA_ATUAL_PNEU);
        END IF;
    ELSE
        -- Pneu está no ProLog e não deve sobrescrever.
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('O pneu %s já está cadastrado no Sistema ProLog', F_CODIGO_PNEU_CLIENTE));
    END IF;

    IF (F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Se houve uma sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o pneu já encontra-se no ProLog,
        -- não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PNEU_CADASTRADO_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_PNEU_SISTEMA_INTEGRADO  = F_COD_PNEU_SISTEMA_INTEGRADO,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_PNEU,
                          TOKEN_AUTENTICACAO_CADASTRO = F_TOKEN_INTEGRACAO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_PNEU_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento e
        -- deixar um erro estourar caso pneu já exista.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

    -- Verificamos se a inserção na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%" na tabela de mapeamento', F_CODIGO_PNEU_CLIENTE;
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Refatoramos a function de carga inicial para repassar a flag se deve ou não sobrescrever os dados do pneu.
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                   F_COD_MODELO_PNEU BIGINT,
                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                   F_DOT_PNEU CHARACTER VARYING,
                                                   F_VALOR_PNEU NUMERIC,
                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                   F_STATUS_PNEU CHARACTER VARYING,
                                                   F_PLACA_VEICULO_PNEU_APLICADO CHARACTER VARYING,
                                                   F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_PNEU        CONSTANT BIGINT  := (SELECT TI.COD_EMPRESA
                                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    COD_VEICULO_PROLOG      CONSTANT BIGINT  := (SELECT V.CODIGO
                                                 FROM PUBLIC.VEICULO V
                                                 WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                                   AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                         FROM PUBLIC.UNIDADE U
                                                                         WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
    IS_POSICAO_ESTEPE       CONSTANT BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                         AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    STATUS_APLICADO_VEICULO CONSTANT TEXT    := 'EM_USO';
    DEVE_SOBRESCREVER_PNEU  CONSTANT BOOLEAN := (SELECT EXISTS(SELECT COD_EMPRESA
                                                               FROM INTEGRACAO.EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO
                                                               WHERE COD_EMPRESA = COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG                  BIGINT;
    F_QTD_ROWS_ALTERADAS             BIGINT;
BEGIN
    -- Inserimos o pneu utilizando a function de inserção padrão. Essa function pode sobrescrever as informações do
    -- pneu caso for necessário.
    SELECT *
    FROM INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(
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
            F_TOKEN_INTEGRACAO,
            DEVE_SOBRESCREVER_PNEU)
    INTO COD_PNEU_PROLOG;

    -- Validamos se a inserção do pneu aconteceu com sucesso.
    IF (COD_PNEU_PROLOG IS NULL OR COD_PNEU_PROLOG <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível cadastrar o pneu %s no Sistema ProLog',
                                                  F_CODIGO_PNEU_CLIENTE));
    END IF;

    -- Atualiza o pneu para o status em que ele deve estar.
    UPDATE PUBLIC.PNEU
    SET STATUS = F_STATUS_PNEU
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
        -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
        IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG) <> F_COD_UNIDADE_PNEU)
        THEN
            UPDATE PUBLIC.PNEU
            SET COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
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
END TRANSACTION;