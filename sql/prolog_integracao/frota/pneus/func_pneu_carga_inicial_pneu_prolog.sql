-- Sobre:
--
-- Function disponível na API do ProLog para inserir um pneu que pode estar aplicado, no estoque ou em análise.
--
-- Essa function utiliza como base a function 'integracao.func_pneu_insere_pneu_prolog' para realizar a inserção do
-- pneu. Após a inserção ela altera o status do pneu para o status desejado.
-- Caso o status seja 'EM_USO' então a function aplica o pneu na placa e posição desejada.
--
-- Precondições:
-- A function irá aplicar o pneu apenas se na placa e posição não houver nenhum outro.
--
-- Histórico:
-- 2019-08-15 -> Function criada (diogenesvanzella - PL-2222).
-- 2019-09-13 -> Transfere pneu para a unidade do veículo caso não estejam na mesma (diogenesvanzella - PL-2222).
-- 2019-10-23 -> Altera tipo da 'F_DATA_HORA_PNEU_CADASTRO' recebida
--               para 'TIMESTAMP WITH TIME ZONE' (diogenesvanzella - PLI-30).
-- 2020-01-03 -> Corrige comparação da unidade da placa com a unidade do pneu (diogenesvanzella - PLI-30).
-- 2020-01-22 -> Permite sobrescrever dados dos pneus (diogenesvanzella - PLI-43).
-- 2020-03-26 -> Atualiza variáveis e utiliza function para atualizar status (diogenesvanzella - PLI-79).
-- 2020-07-27 -> Volta arquivo base para versão de Prod (diogenesvanzella - PLI-189).
-- 2020-08-06 -> Adapta function para lidar com tokens repetidos (diogenesvanzella - PLI-175).
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
                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_PNEU                 BIGINT  := (SELECT U.COD_EMPRESA
                                                 FROM PUBLIC.UNIDADE U
                                                 WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
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
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

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

            SELECT V.COD_UNIDADE
            FROM PUBLIC.VEICULO V
            WHERE V.CODIGO = COD_VEICULO_PROLOG
            INTO F_COD_UNIDADE_PNEU;
        END IF;

        PERFORM
            INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
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