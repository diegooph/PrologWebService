-- Sobre:
-- Function para inserir um veículo integrado no ProLog, salvando tanto na tabela VEICULO_DATA quanto na
-- INTEGRACAO.VEICULO_CADASTRADO.
--
-- Histórico:
-- 2019-10-07 -> Adiciona cod_empresa ao insert de veículo (luizfp - PL-2276).
-- 2020-01-22 -> Permite sobrescrever dados dos veículos (diogenesvanzella - PLI-64).
-- 2020-02-26 -> Adiciona a manipulação do código de diagrama (wvinim - PL-1965).
-- 2020-08-05 -> Adapta function para token duplicado (diogenesvanzella - PLI-175).
-- 2020-09-11 -> Alterado para não utilizar o código do diagrama no update. (luiz_fp - PL-3097).
-- 2020-11-10 -> Adiciona parâmetro possui_hubodometro com default (steinert999 - PL-3223).
-- 2020-11-10 -> Realiza modificação na parte de inserção para inclusãod do campo motorizado (steinert999 - PL-3223).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                  F_PLACA_VEICULO_CADASTRADO TEXT,
                                                  F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                  F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                  F_TOKEN_INTEGRACAO TEXT,
                                                  F_POSSUI_HUBODOMETRO BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                                   FROM PUBLIC.UNIDADE U
                                                   WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
    V_INSERT_COD_DIAGRAMA              BIGINT;
    V_INSERT_MOTORIZADO                BOOLEAN;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            COD_EMPRESA_VEICULO,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                     confira se está usando o token correto', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_VEICULO_ALOCADO));

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
        SELECT VT.COD_DIAGRAMA, VD.MOTORIZADO
                 FROM PUBLIC.VEICULO_TIPO VT
                 JOIN VEICULO_DIAGRAMA VD
                 ON VD.CODIGO = VT.COD_DIAGRAMA
                 WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                 AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO V_INSERT_COD_DIAGRAMA, V_INSERT_MOTORIZADO;
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   MOTORIZADO,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO,
                                   POSSUI_HUBODOMETRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                V_INSERT_COD_DIAGRAMA,
                V_INSERT_MOTORIZADO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_POSSUI_HUBODOMETRO)
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