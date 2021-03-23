BEGIN TRANSACTION;
-- PLI-64 - Executa sobrecarga de veículos
--######################################################################################################################
--######################################################################################################################
-- Dropamos para criar uma tabela mais robusta e completa.
DROP TABLE INTEGRACAO.EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO;

-- Cria tabela para salvar configurações de carga inicial para as empresas.
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL
(
    COD_EMPRESA          BIGINT  NOT NULL,
    SOBRESCREVE_PNEUS    BOOLEAN NOT NULL,
    SOBRESCREVE_VEICULOS BOOLEAN NOT NULL,
    CONSTRAINT PK_EMPRESA_CONFIG_CARGA_INICIAL PRIMARY KEY (COD_EMPRESA),
    CONSTRAINT FK_COD_EMPRESA_SOBRESCREVE_PNEUS_INTEGRACAO FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO)
);

-- Já inserimos a empresa PICCOLOTUR - 11, pois vai utilizar essa abordagem
INSERT INTO INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL(COD_EMPRESA, SOBRESCREVE_PNEUS, SOBRESCREVE_VEICULOS)
VALUES (11, TRUE, TRUE);
-- Já inserimos a empresa TLX - 45, pois vai utilizar essa abordagem
INSERT INTO INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL(COD_EMPRESA, SOBRESCREVE_PNEUS, SOBRESCREVE_VEICULOS)
VALUES (45, TRUE, TRUE);
--######################################################################################################################
--######################################################################################################################

-- Cria functions para buscar informação se a empresa sobrescreve pneu ou veículo.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_PNEUS(F_COD_EMPRESA BIGINT)
    RETURNS BOOLEAN
    LANGUAGE SQL
AS
$$
SELECT F_IF(SOBRESCREVE_PNEUS IS NULL, FALSE, SOBRESCREVE_PNEUS) AS SOBRESCREVE_PNEUS
FROM INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL
WHERE COD_EMPRESA = F_COD_EMPRESA;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(F_COD_EMPRESA BIGINT)
    RETURNS BOOLEAN
    LANGUAGE SQL
AS
$$
SELECT F_IF(SOBRESCREVE_VEICULOS IS NULL, FALSE, SOBRESCREVE_VEICULOS) AS SOBRESCREVE_VEICULOS
FROM INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL
WHERE COD_EMPRESA = F_COD_EMPRESA;
$$;

--######################################################################################################################
--######################################################################################################################
-- Move pneu para o estoque quando remover do veículo
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
                                                           F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PNEUS_PROLOG CONSTANT BIGINT[] := (SELECT ARRAY_AGG(PC.COD_PNEU_CADASTRO_PROLOG)
                                           FROM INTEGRACAO.PNEU_CADASTRADO PC
                                           WHERE PC.COD_EMPRESA_CADASTRO = (SELECT TI.COD_EMPRESA
                                                                            FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                            WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
                                             AND PC.COD_PNEU_SISTEMA_INTEGRADO = ANY (F_COD_SISTEMA_INTEGRADO_PNEUS));
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE COD_PNEU = ANY (COD_PNEUS_PROLOG);

    UPDATE PNEU
    SET STATUS = 'ESTOQUE'
    WHERE CODIGO = ANY (COD_PNEUS_PROLOG);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria function para revemover pneus aplicados com base em uma placa.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PNEUS_PARA_ATUALIZAR BIGINT[] := (SELECT ARRAY_AGG(COD_PNEU)
                                          FROM PUBLIC.VEICULO_PNEU
                                          WHERE PLACA = F_PLACA_VEICULO);
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE PLACA = F_PLACA_VEICULO;

    UPDATE PUBLIC.PNEU
    SET STATUS = 'ESTOQUE'
    WHERE CODIGO = ANY (COD_PNEUS_PARA_ATUALIZAR);
END;
$$;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
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
    COD_EMPRESA_PNEU                 BIGINT  := (SELECT TI.COD_EMPRESA
                                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    COD_VEICULO_PROLOG               BIGINT  := (SELECT V.CODIGO
                                                 FROM PUBLIC.VEICULO V
                                                 WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                                   AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                         FROM PUBLIC.UNIDADE U
                                                                         WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
    IS_POSICAO_ESTEPE                BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                         AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    STATUS_APLICADO_VEICULO CONSTANT TEXT    := 'EM_USO';
    DEVE_SOBRESCREVER_PNEU           BOOLEAN := (SELECT *
                                                 FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_PNEUS(
                                                         COD_EMPRESA_PNEU));
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
-- Fechamos automaticamente os serviços do veículo
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_DELETA_SERVICOS_ABERTOS_PLACA(F_PLACA_VEICULO TEXT,
                                                          F_COD_UNIDADE BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_SERVICOS_PARA_DELETAR   CONSTANT BIGINT[] := (SELECT ARRAY_AGG(COSI.CODIGO)
                                                      FROM CHECKLIST_ORDEM_SERVICO COS
                                                               JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
                                                               JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                                                    ON COS.CODIGO = COSI.COD_OS
                                                                        AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                                                      WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
                                                        AND COSI.STATUS_RESOLUCAO = 'P'
                                                        AND COSI.COD_UNIDADE = F_COD_UNIDADE);
    -- Usamos o 'DISTINCT' para não repetir o 'cod_os' no array gerado.
    COD_ORDENS_SERVICO_ANALISAR CONSTANT BIGINT[] := (SELECT ARRAY_AGG(DISTINCT COD_OS)
                                                      FROM CHECKLIST_ORDEM_SERVICO_ITENS
                                                      WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR));
BEGIN
    -- Aqui deletamos os ITENS que estão pendentes de resolução na placa informada.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = ANY (COD_SERVICOS_PARA_DELETAR);

    -- Após deletarmos os itens, varremos as OSs para saber se alguma das OSs que tiveram seus itens deletados estão
    -- vazias, se estiverem vazias (count() = 0) então deletamos também.
    -- The secret key:
    -- O segredo para esse update funcionar está em utilizar a view 'checklist_ordem_servico_itens' e não filtrar
    -- por status dos itens, pois assim saberemos se após deletar lógicamente os itens na query anterior, a OS se
    -- mantem com algum item dentro dela, seja pendente ou resolvido. Caso tiver, não devemos deletar.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_DATA COSD
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE COSD.COD_UNIDADE = F_COD_UNIDADE
      AND NOT DELETADO -- Se já está deletada, não nos interessa.
      AND COSD.CODIGO = ANY (COD_ORDENS_SERVICO_ANALISAR)
      AND ((SELECT COUNT(COSI.CODIGO)
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = COSD.CODIGO
              AND COSI.COD_UNIDADE = F_COD_UNIDADE) = 0);

    -- Pode acontecer um cenário onde a OS tenha 2 itens, um resolvido e um pendente. Neste cenário a OS está aberta,
    -- ao deletar o item pendente, devemos fechar a OS e inserir a data de fechamento como a data do último item
    -- resolvido.
    -- The secret key:
    -- O segredo aqui esta em usar a view 'checklist_ordem_servico' para realizar o update, pois ela já não trará as
    -- OSs que foram deletados na query acima. Bastando verificar se a OS não tem nenhum item pendente, para esses
    -- casos buscamos a maior 'data_hora_conserto' e usamos ela para fechar a OS.
    UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO AS COS
    SET STATUS               = 'F',
        DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
                                FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                WHERE COSI.COD_OS = COS.CODIGO
                                  AND COSI.COD_UNIDADE = COS.COD_UNIDADE)
    WHERE COS.COD_UNIDADE = F_COD_UNIDADE
      AND COS.CODIGO = ANY (COD_ORDENS_SERVICO_ANALISAR)
      AND COS.STATUS = 'A'
      AND ((SELECT COUNT(COSI.CODIGO)
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = COS.CODIGO
              AND COSI.COD_UNIDADE = F_COD_UNIDADE
              AND COSI.STATUS_RESOLUCAO = 'P') = 0);
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Cria function de sobrecarga de veículo
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(F_PLACA_VEICULO TEXT,
                                                           F_COD_UNIDADE_VEICULO BIGINT,
                                                           F_KM_ATUAL_VEICULO BIGINT,
                                                           F_COD_TIPO_VEICULO BIGINT,
                                                           F_COD_MODELO_VEICULO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT := (SELECT COD_EMPRESA
                                                  FROM UNIDADE
                                                  WHERE CODIGO = F_COD_UNIDADE_VEICULO);
    COD_UNIDADE_ATUAL_VEICULO CONSTANT BIGINT := (SELECT COD_UNIDADE
                                                  FROM PUBLIC.VEICULO
                                                  WHERE PLACA = F_PLACA_VEICULO
                                                    AND COD_EMPRESA = COD_EMPRESA_VEICULO);
BEGIN
    -- Devemos tratar os serviços abertos para o veículo (setar fechado_integracao), apenas se a unidade mudar.
    IF (COD_UNIDADE_ATUAL_VEICULO <> F_COD_UNIDADE_VEICULO)
    THEN
        PERFORM INTEGRACAO.FUNC_VEICULO_DELETA_SERVICOS_ABERTOS_PLACA(F_PLACA_VEICULO,
                                                                      F_COD_UNIDADE_VEICULO);
    END IF;

    UPDATE PUBLIC.VEICULO
    SET COD_UNIDADE = F_COD_UNIDADE_VEICULO,
        KM          = F_KM_ATUAL_VEICULO,
        COD_TIPO    = F_COD_TIPO_VEICULO,
        COD_MODELO  = F_COD_MODELO_VEICULO
    WHERE PLACA = F_PLACA_VEICULO
      AND COD_EMPRESA = COD_EMPRESA_VEICULO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Altera function de carga inicial de veículos para sobrescrever ou não as informações.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                                         F_PLACA_VEICULO_CADASTRADO TEXT,
                                                                         F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                                         F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                         F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT TI.COD_EMPRESA
                                                   FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                   WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO)
        <>
        (SELECT TI.COD_EMPRESA
         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT(
                                '[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                                 confira se está usando o token correto',
                                F_TOKEN_INTEGRACAO,
                                F_COD_UNIDADE_VEICULO_ALOCADO));
    END IF;

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (VEICULO_ESTA_NO_PROLOG AND NOT DEVE_SOBRESCREVER_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                               F_PLACA_VEICULO_CADASTRADO));
    END IF;

    IF (VEICULO_ESTA_NO_PROLOG AND DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Buscamos o código do veículo que será sobrescrito.
        SELECT V.CODIGO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO_CADASTRADO
          AND V.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO COD_VEICULO_PROLOG;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        PERFORM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO_CADASTRADO);

        -- Sebrescrevemos os dados do veículo.
        PERFORM INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(
                        F_PLACA_VEICULO_CADASTRADO,
                        F_COD_UNIDADE_VEICULO_ALOCADO,
                        F_KM_ATUAL_VEICULO_CADASTRADO,
                        F_COD_TIPO_VEICULO_CADASTRADO,
                        F_COD_MODELO_VEICULO_CADASTRADO);

    ELSE
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO)
        RETURNING CODIGO INTO COD_VEICULO_PROLOG;
    END IF;

    IF (DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_VEICULO_CADASTRO_PROLOG = COD_VEICULO_PROLOG,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_VEICULO_ALOCADO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_VEICULO_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;