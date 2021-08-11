-- Sobre:
--
-- Function disponível na API do ProLog para alterar informações de um pneu já cadastrado no ProLog.
--
-- A function deverá receber todos os atributos do pneu, inclusive aqueles atributos que não sofreram edição. Caso o
-- atributo não tenha sofrido edição deve-se repassar para a function o valor atual.
-- Essa function irá ignorar caso existam informações de banda nas atualizações mas o pneu estiver em PRIMEIRA VIDA.
-- Porém, caso o pneu possua uma banda aplicada, as informações dela deverão ser repassadas para a function, idependente
-- se foram alteradas ou não.
--
-- Precondições:
-- É obrigatório a existência do pneu na base de dados do ProLog para realizar as alterações. Essa function não insere
-- nenhum dado caso o pneu não exista.
--
-- Histórico:
-- 2019-08-15 -> Function criada (diogenesvanzella - PL-2222).
-- 2020-03-30 -> Corrige update da tabela 'pneu_cadastrado' (diogenesvanzella - PLI-111).
-- 2020-07-27 -> Corrige arquivo base (diogenesvanzella - PLI-189).
-- 2020-08-06 -> Adapta function para lidar com tokens repetidos (diogenesvanzella - PLI-175).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                     F_NOVO_CODIGO_PNEU_CLIENTE TEXT,
                                                                     F_NOVO_COD_MODELO_PNEU BIGINT,
                                                                     F_NOVO_COD_DIMENSAO_PNEU BIGINT,
                                                                     F_NOVO_DOT_PNEU TEXT,
                                                                     F_NOVO_VALOR_PNEU REAL,
                                                                     F_NOVO_COD_MODELO_BANDA_PNEU BIGINT,
                                                                     F_NOVO_VALOR_BANDA_PNEU REAL,
                                                                     F_DATA_HORA_EDICAO_PNEU TIMESTAMP WITH TIME ZONE,
                                                                     F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Precisamos utilizar o código do modelo de pneu para chegar até o código da empresa.
    COD_EMPRESA_PNEU     BIGINT  := (SELECT MP.COD_EMPRESA
                                     FROM PUBLIC.MODELO_PNEU MP
                                     WHERE MP.CODIGO = F_NOVO_COD_MODELO_PNEU);
    COD_PNEU_PROLOG      BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                     FROM INTEGRACAO.PNEU_CADASTRADO PC
                                     WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                       AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    PNEU_POSSUI_BANDA    BOOLEAN := F_IF(((SELECT P.COD_MODELO_BANDA
                                           FROM PUBLIC.PNEU P
                                           WHERE P.CODIGO = COD_PNEU_PROLOG) IS NULL), FALSE, TRUE);
    TROCOU_BANDA_PNEU    BOOLEAN := F_IF(F_NOVO_COD_MODELO_BANDA_PNEU IS NULL, FALSE, TRUE);
    F_QTD_ROWS_ALTERADAS BIGINT;
    V_COD_DIMENSAO BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(
            COD_EMPRESA_PNEU,
            FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF (COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                       F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o novo_codigo_cliente é um código válido ou já possui um igual na base dados.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
                        AND PC.COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE
                        AND PC.COD_PNEU_SISTEMA_INTEGRADO != F_COD_PNEU_SISTEMA_INTEGRADO))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu com o código %s cadastrado no Sistema ProLog',
                                              F_NOVO_CODIGO_PNEU_CLIENTE));
    END IF;

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_NOVO_COD_MODELO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado', F_NOVO_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.COD_AUXILIAR
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.COD_AUXILIAR = F_NOVO_COD_DIMENSAO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada',
                                              F_NOVO_COD_DIMENSAO_PNEU));
    ELSE
        SELECT DP.CODIGO
        INTO V_COD_DIMENSAO
        FROM DIMENSAO_PNEU DP
        WHERE DP.COD_AUXILIAR = F_NOVO_COD_DIMENSAO_PNEU
          AND DP.COD_EMPRESA = COD_EMPRESA_PNEU;
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_NOVO_VALOR_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o pneu possui banda e se ela não foi removida na atualização.
    IF (PNEU_POSSUI_BANDA AND F_NOVO_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O modelo da banda do pneu deve ser informado');
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_NOVO_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado',
                                              F_NOVO_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda e a mesma tiver sido
    -- atualizada.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(
                    'Você está trocando a banda, deve ser informado o valor da nova banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL AND F_NOVO_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor da nova banda do pneu não pode ser um número negativo');
    END IF;

    UPDATE PUBLIC.PNEU
    SET CODIGO_CLIENTE   = F_NOVO_CODIGO_PNEU_CLIENTE,
        COD_MODELO       = F_NOVO_COD_MODELO_PNEU,
        COD_DIMENSAO     = V_COD_DIMENSAO,
        DOT              = F_NOVO_DOT_PNEU,
        VALOR            = F_NOVO_VALOR_PNEU,
        COD_MODELO_BANDA = F_IF(PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU, F_NOVO_COD_MODELO_BANDA_PNEU, NULL)
    WHERE CODIGO = COD_PNEU_PROLOG;

    UPDATE INTEGRACAO.PNEU_CADASTRADO
    SET COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE,
        DATA_HORA_ULTIMA_EDICAO   = F_DATA_HORA_EDICAO_PNEU
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
      AND COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Verificamos se a atualização na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
    END IF;

    IF (PNEU_POSSUI_BANDA
        AND NOT (SELECT *
                 FROM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(COD_PNEU_PROLOG,
                                                          F_NOVO_COD_MODELO_BANDA_PNEU,
                                                          F_NOVO_VALOR_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('Não foi possível atualizar a banda do pneu');
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;