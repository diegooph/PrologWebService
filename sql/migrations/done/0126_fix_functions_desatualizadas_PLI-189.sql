-- Arquivo alterado na PLI-102 pelo Natan. Ele alterou o arquivo errado. Devo alterar o arquivo base.
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
    COD_EMPRESA_PNEU     BIGINT  := (SELECT TI.COD_EMPRESA
                                     FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                     WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    COD_PNEU_PROLOG      BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                     FROM INTEGRACAO.PNEU_CADASTRADO PC
                                     WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                       AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    PNEU_POSSUI_BANDA    BOOLEAN := F_IF(((SELECT P.COD_MODELO_BANDA
                                           FROM PUBLIC.PNEU P
                                           WHERE P.CODIGO = COD_PNEU_PROLOG) IS NULL), FALSE, TRUE);
    TROCOU_BANDA_PNEU    BOOLEAN := F_IF(F_NOVO_COD_MODELO_BANDA_PNEU IS NULL, FALSE, TRUE);
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN

    -- Validamos se a Empresa é válida.
    PERFORM
        FUNC_GARANTE_EMPRESA_EXISTE
            (COD_EMPRESA_PNEU,
             FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

-- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF
        (COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                              F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

-- Validamos se o novo_codigo_cliente é um código válido ou já possui um igual na base dados.
    IF
        (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
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
    IF
        (SELECT NOT EXISTS(SELECT MP.CODIGO
                           FROM PUBLIC.MODELO_PNEU MP
                           WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                             AND MP.CODIGO = F_NOVO_COD_MODELO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado', F_NOVO_COD_MODELO_PNEU));
    END IF;

-- Validamos se a dimensão do pneu está mapeada.
    IF
        (SELECT NOT EXISTS(SELECT DP.CODIGO
                           FROM PUBLIC.DIMENSAO_PNEU DP
                           WHERE DP.CODIGO = F_NOVO_COD_DIMENSAO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada',
                                              F_NOVO_COD_DIMENSAO_PNEU));
    END IF;

-- Validamos se o valor do pneu é um valor válido.
    IF
        (F_NOVO_VALOR_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

-- Validamos se o pneu possui banda e se ela não foi removida na atualização.
    IF
        (PNEU_POSSUI_BANDA AND F_NOVO_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O modelo da banda do pneu deve ser informado');
    END IF;

-- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF
        (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
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
    IF
        (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(
                    'Você está trocando a banda, deve ser informado o valor da nova banda aplicada');
    END IF;

-- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF
        (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL AND F_NOVO_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor da nova banda do pneu não pode ser um número negativo');
    END IF;

    UPDATE PUBLIC.PNEU
    SET CODIGO_CLIENTE   = F_NOVO_CODIGO_PNEU_CLIENTE,
        COD_MODELO       = F_NOVO_COD_MODELO_PNEU,
        COD_DIMENSAO     = F_NOVO_COD_DIMENSAO_PNEU,
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
    IF
        (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
    END IF;

    IF
        (PNEU_POSSUI_BANDA
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


-- Function não sendo utilizada.
-- Function atualizada pelo Natan na migration PLI-102.
drop function func_pneu_atualiza_status_pneu_prolog(bigint, varchar, bigint, varchar, timestamp, varchar, boolean, bigint, numeric, varchar, integer, varchar);
-- Backup da function deletada:
-- create function func_pneu_atualiza_status_pneu_prolog(f_cod_pneu_sistema_integrado bigint, f_codigo_pneu_cliente character varying, f_cod_unidade_pneu bigint, f_cpf_colaborador_alteracao_status character varying, f_data_hora_alteracao_status timestamp without time zone, f_status_pneu character varying, f_trocou_de_banda boolean, f_cod_novo_modelo_banda_pneu bigint, f_valor_nova_banda_pneu numeric, f_placa_veiculo_pneu_aplicado character varying, f_posicao_veiculo_pneu_aplicado integer, f_token_integracao character varying) returns bigint
--     language plpgsql
-- as
-- $$
-- DECLARE
--   COD_EMPRESA_PNEU BIGINT := (SELECT TI.COD_EMPRESA
--                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
--                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
--   COD_VEICULO_PROLOG BIGINT := (SELECT V.CODIGO
--                                 FROM PUBLIC.VEICULO V
--                                 WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
--                                       AND V.COD_UNIDADE IN (SELECT U.CODIGO
--                                                             FROM PUBLIC.UNIDADE U
--                                                             WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
--   IS_POSICAO_ESTEPE BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
--                                     AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
--   COD_PNEU_PROLOG BIGINT := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
--                              FROM INTEGRACAO.PNEU_CADASTRADO PC
--                              WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
--                                    AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
--   VIDA_ATUAL_PNEU INTEGER := (SELECT P.VIDA_ATUAL FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG);
--   PROXIMA_VIDA_PNEU INTEGER := VIDA_ATUAL_PNEU + 1;
--   STATUS_APLICADO_VEICULO TEXT := 'EM_USO';
--   COD_SERVICO_INCREMENTA_VIDA BIGINT;
--   F_QTD_ROWS_ALTERADAS BIGINT;
-- BEGIN
--   -- Validamos se a Empresa é válida.
--   PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
--                                       FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));
--
--   -- Validamos se a Unidade repassada existe.
--   PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
--                                       FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
--                                              F_COD_UNIDADE_PNEU));
--
--   -- Validamos se a Unidade pertence a Empresa do token repassado.
--   PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
--                                               F_COD_UNIDADE_PNEU,
--                                               FORMAT('A Unidade %s não está configurada para esta empresa',
--                                                      F_COD_UNIDADE_PNEU));
--
--   -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
--   IF (COD_PNEU_PROLOG IS NULL)
--   THEN
--     PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
--                                               F_COD_PNEU_SISTEMA_INTEGRADO));
--   END IF;
--
--   -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
--   DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = COD_PNEU_PROLOG;
--
--   -- Atualiza o pneu para o status em que ele deve estar.
--   UPDATE PUBLIC.PNEU
--   SET
--     STATUS = F_STATUS_PNEU,
--     COD_UNIDADE = F_COD_UNIDADE_PNEU
--   WHERE CODIGO = COD_PNEU_PROLOG;
--
--   GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;
--
--   -- Validamos se o status do pneu foi atualizado com sucesso
--   IF (F_QTD_ROWS_ALTERADAS <= 0)
--   THEN
--     PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
--                                               F_CODIGO_PNEU_CLIENTE,
--                                               F_STATUS_PNEU));
--   END IF;
--
--   -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
--   IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
--   THEN
--     -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
--     IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
--     THEN
--       UPDATE PUBLIC.PNEU
--       SET
--         COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
--       WHERE CODIGO = COD_PNEU_PROLOG;
--
--       SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
--     END IF;
--
--     PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
--                                                             F_PLACA_VEICULO_PNEU_APLICADO,
--                                                             COD_PNEU_PROLOG,
--                                                             F_CODIGO_PNEU_CLIENTE,
--                                                             F_COD_UNIDADE_PNEU,
--                                                             F_POSICAO_VEICULO_PNEU_APLICADO,
--                                                             IS_POSICAO_ESTEPE);
--   END IF;
--
--   IF (F_TROCOU_DE_BANDA)
--   THEN
--     -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
--     IF (F_COD_NOVO_MODELO_BANDA_PNEU IS NULL)
--     THEN
--       PERFORM PUBLIC.THROW_GENERIC_ERROR('O código do modelo da banda deve ser informado');
--     END IF;
--
--     -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
--     IF ((SELECT NOT EXISTS(SELECT MB.CODIGO
--                            FROM PUBLIC.MODELO_BANDA MB
--                            WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
--                                  AND MB.CODIGO = F_COD_NOVO_MODELO_BANDA_PNEU)))
--     THEN
--       PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado no Sistema ProLog',
--                                                 F_COD_NOVO_MODELO_BANDA_PNEU));
--     END IF;
--
--     -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
--     IF (F_VALOR_NOVA_BANDA_PNEU IS NULL)
--     THEN
--       PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu deve ser informado');
--     END IF;
--
--     -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
--     IF (F_VALOR_NOVA_BANDA_PNEU < 0)
--     THEN
--       PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
--     END IF;
--
--     -- Busca serviço que incrementa a vida do pneu dentro da empresa em questão.
--     SELECT * FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(COD_EMPRESA_PNEU)
--     INTO COD_SERVICO_INCREMENTA_VIDA;
--
--     IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
--     THEN
--       PERFORM PUBLIC.THROW_GENERIC_ERROR('Erro ao vincular banda ao pneu');
--     END IF;
--
--     -- Incrementa a vida do pneu simulando um processo de movimentação.
--     PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(F_COD_UNIDADE_PNEU,
--                                                                       COD_PNEU_PROLOG,
--                                                                       F_COD_NOVO_MODELO_BANDA_PNEU,
--                                                                       F_VALOR_NOVA_BANDA_PNEU,
--                                                                       PROXIMA_VIDA_PNEU,
--                                                                       COD_SERVICO_INCREMENTA_VIDA);
--
--     -- Após incrementar a vida e criar o serviço, atualizamos o pneu para ficar com a banda e a vida correta.
--     PERFORM PUBLIC.FUNC_PNEUS_INCREMENTA_VIDA_PNEU(COD_PNEU_PROLOG, F_COD_NOVO_MODELO_BANDA_PNEU);
--   END IF;
--
--   -- Qualquer alteração de status do pneu deve verificar se o pneu tem serviços aberto e fechá-los.
--   PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(COD_PNEU_PROLOG,
--                                                                   F_DATA_HORA_ALTERACAO_STATUS);
--
--   RETURN COD_PNEU_PROLOG;
-- END;
-- $$;

-- Function atualizada pelo Natan na migration PLI-102.
-- Voltamos para a versão de Prod.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
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
    IF
        (COD_PNEU_PROLOG IS NULL OR COD_PNEU_PROLOG <= 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível cadastrar o pneu %s no Sistema ProLog',
                                              F_CODIGO_PNEU_CLIENTE));
    END IF;

-- Atualiza o pneu para o status em que ele deve estar.
    UPDATE PUBLIC.PNEU
    SET STATUS = F_STATUS_PNEU
    WHERE CODIGO = COD_PNEU_PROLOG;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Validamos se o status do pneu foi atualizado com sucesso
    IF
        (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível inserir o pneu %s com status %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_STATUS_PNEU));
    END IF;

-- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
    IF
        (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
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


-- Function desatualizada, não utilizada.
drop function integracao.func_pneu_remove_vinculo_pneu_placa_posicao(bigint[]);
-- Backup da function deletada:
-- create function func_pneu_remove_vinculo_pneu_placa_posicao(f_cod_sistema_integrado_pneus bigint[]) returns void
--     language plpgsql
-- as
-- $$
-- BEGIN
--   DELETE FROM PUBLIC.VEICULO_PNEU
--   WHERE COD_PNEU IN (SELECT PC.COD_PNEU_CADASTRO_PROLOG
--                      FROM INTEGRACAO.PNEU_CADASTRADO PC
--                      WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = ANY(F_COD_SISTEMA_INTEGRADO_PNEUS));
-- END;
-- $$;
--
-- alter function func_pneu_remove_vinculo_pneu_placa_posicao(bigint[]) owner to prolog_user;


-- Function atualizado pelo natan na PLI-102.
-- Voltamos para a versão de Prod.
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


-- Function atualizada pelo natan. Eu não sei o que aconteceu.
-- Voltamos para a versão de Prod.
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

-- Tem diferença também.
-- Atualizamos a func de prod com a versão base.
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM BIGINT,
                                                       F_COD_UNIDADE_DESTINO BIGINT,
                                                       F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                       F_LISTA_PNEUS TEXT[],
                                                       F_OBSERVACAO TEXT,
                                                       F_TOKEN_INTEGRACAO TEXT,
                                                       F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                       F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                BIGINT := (SELECT TI.COD_EMPRESA
                                            FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                            WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    V_COD_COLABORADOR            BIGINT := (SELECT C.CODIGO
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_COD_UNIDADE_COLABORADOR    BIGINT := (SELECT C.COD_UNIDADE
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_TIPO_TRANSFERENCIA         TIPO_PROCESSO_TRANSFERENCIA_PNEU
                                        := 'TRANSFERENCIA_APENAS_PNEUS';
    V_PNEUS_NAO_MAPEADOS         TEXT[] := (SELECT ARRAY_AGG(CCNE.COD_CLIENTE)
                                            FROM (SELECT UNNEST(F_LISTA_PNEUS) AS COD_CLIENTE) AS CCNE
                                            WHERE CCNE.COD_CLIENTE
                                                      NOT IN (SELECT PC.COD_CLIENTE_PNEU_CADASTRO
                                                              FROM INTEGRACAO.PNEU_CADASTRADO PC
                                                              WHERE PC.COD_EMPRESA_CADASTRO = V_COD_EMPRESA));
    V_COD_PROCESSO_TRANSFERENCIA BIGINT;
    V_COD_PNEUS_TRANSFERIR       BIGINT[];
    V_QTD_ROWS                   BIGINT;
BEGIN
    -- Verificamos se a empresa existe
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado não está autorizado: %s', F_TOKEN_INTEGRACAO));

    -- Verifica se unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do pneu não está mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verifica se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do pneu não está mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_ORIGEM,
            FORMAT('Unidade (%s) não autorizada para o token: %s', F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_DESTINO,
            FORMAT('Unidade (%s) não autorizada para o token: %s', F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF NOT EXISTS(SELECT C.CODIGO FROM COLABORADOR_DATA C WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s não está cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Verificamos se temos algum pneu não mapeado.
    IF (F_SIZE_ARRAY(V_PNEUS_NAO_MAPEADOS) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('Os pneus (%s) não estão cadastrados no Sistema Prolog',
                                           ARRAY_TO_STRING(V_PNEUS_NAO_MAPEADOS, ', ')));
    END IF;

    -- Após validar todos os cenários, pegamos os códigos do pneus que iremos transferir. Transferimos apenas os pneus
    -- que não estão na unidade de destino, ou seja, transferimos somente os que precisam.
    SELECT ARRAY_AGG(P.CODIGO)
    FROM PNEU P
    WHERE P.COD_EMPRESA = V_COD_EMPRESA
      AND P.CODIGO_CLIENTE = ANY (F_LISTA_PNEUS)
      AND P.COD_UNIDADE != F_COD_UNIDADE_DESTINO
    INTO V_COD_PNEUS_TRANSFERIR;

    IF (F_SIZE_ARRAY(V_COD_PNEUS_TRANSFERIR) <= 0)
    THEN
        -- Retornamos 1, simulando um processo de transferência que foi sucesso, porém nada foi transferido.
        RETURN 1;
    END IF;

    -- Verifica se os pneus estão aplicados a uma placa na transferência de veículo.
    IF (F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA IS FALSE)
    THEN
        -- Devemos alterar o status de todos os pneus, colocá-los em estoque caso ainda não estejam.
        DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = ANY (V_COD_PNEUS_TRANSFERIR);

        UPDATE PUBLIC.PNEU
        SET STATUS = 'ESTOQUE'
        WHERE CODIGO = ANY (V_COD_PNEUS_TRANSFERIR);
    ELSE
        V_TIPO_TRANSFERENCIA = 'TRANSFERENCIA_JUNTO_A_VEICULO';
    END IF;

    -- Cria processo de transferência.
    INSERT INTO PNEU_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                            COD_UNIDADE_DESTINO,
                                            COD_UNIDADE_COLABORADOR,
                                            COD_COLABORADOR,
                                            DATA_HORA_TRANSFERENCIA_PROCESSO,
                                            OBSERVACAO,
                                            TIPO_PROCESSO_TRANSFERENCIA)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO,
            V_TIPO_TRANSFERENCIA)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA IS NULL OR V_COD_PROCESSO_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transferência';
    END IF;

    -- Insere valores da transferência
    V_QTD_ROWS = (FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(V_COD_PROCESSO_TRANSFERENCIA,
                                                             V_COD_PNEUS_TRANSFERIR));
    -- Verifica se atualizou.
    IF (V_QTD_ROWS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao realizar a transferência de informações';
    END IF;

    -- Atualiza unidade alocada.
    PERFORM INTEGRACAO.FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(F_COD_UNIDADE_DESTINO,
                                                                       V_COD_PNEUS_TRANSFERIR);

    RETURN V_COD_PROCESSO_TRANSFERENCIA;
END;
$$;


-- Tem diferença também. Natan alterou na PLI-102.
-- Voltamos para a versão de Prod.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(F_COD_VEICULO_PROLOG BIGINT,
                                                                           F_PLACA_VEICULO_PNEU_APLICADO TEXT,
                                                                           F_COD_PNEU_PROLOG BIGINT,
                                                                           F_CODIGO_PNEU_CLIENTE TEXT,
                                                                           F_COD_UNIDADE_PNEU BIGINT,
                                                                           F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                                           F_IS_POSICAO_ESTEPE BOOLEAN)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
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
                FORMAT('A placa informada %s está em uma Unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                       F_PLACA_VEICULO_PNEU_APLICADO,
                       F_CODIGO_PNEU_CLIENTE,
                       (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG),
                       F_COD_UNIDADE_PNEU));
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
    INSERT INTO PUBLIC.VEICULO_PNEU(PLACA,
                                    COD_PNEU,
                                    COD_UNIDADE,
                                    POSICAO,
                                    COD_DIAGRAMA)
    VALUES (F_PLACA_VEICULO_PNEU_APLICADO,
            F_COD_PNEU_PROLOG,
            F_COD_UNIDADE_PNEU,
            F_POSICAO_VEICULO_PNEU_APLICADO,
            (SELECT VT.COD_DIAGRAMA
             FROM VEICULO_TIPO VT
             WHERE VT.CODIGO = (SELECT V.COD_TIPO FROM VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG)));

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
END ;
$$;


-- Function duplicada
drop function integracao.func_veiculo_deleta_servicos_abertos_placa(bigint, bigint);
-- Backup da function deletada:
-- create function func_veiculo_deleta_servicos_abertos_placa(f_placa_veiculo bigint, f_cod_unidade bigint) returns void
--     language plpgsql
-- as
-- $$
-- DECLARE
--     COD_SERVICOS_PARA_DELETAR   CONSTANT BIGINT[] := (SELECT ARRAY_AGG(COSI.CODIGO)
--                                                       FROM CHECKLIST_ORDEM_SERVICO COS
--                                                                JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
--                                                                JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
--                                                                     ON COS.CODIGO = COSI.COD_OS
--                                                                         AND COS.COD_UNIDADE = COSI.COD_UNIDADE
--                                                       WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
--                                                         AND COSI.STATUS_RESOLUCAO = 'P'
--                                                         AND COSI.COD_UNIDADE = F_COD_UNIDADE);
--     -- Usamos o 'DISTINCT' para não repetir o 'cod_os' no array gerado.
--     COD_ORDENS_SERVICO_ANALISAR CONSTANT BIGINT[] := (SELECT ARRAY_AGG(DISTINCT COD_OS)
--                                                       FROM CHECKLIST_ORDEM_SERVICO_ITENS
--                                                       WHERE CODIGO IN (COD_SERVICOS_PARA_DELETAR));
-- BEGIN
--     -- Aqui deletamos os ITENS que estão pendentes de resolução na placa informada.
--     UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS_DATA
--     SET DELETADO            = TRUE,
--         DATA_HORA_DELETADO  = NOW(),
--         PG_USERNAME_DELECAO = SESSION_USER
--     WHERE CODIGO IN (COD_SERVICOS_PARA_DELETAR);
--
--     -- Após deletarmos os itens, varremos as OSs para saber se alguma das OSs que tiveram seus itens deletados estão
--     -- vazias, se estiverem vazias (count() = 0) então deletamos também.
--     -- The secret key:
--     -- O segredo para esse update funcionar está em utilizar a view 'checklist_ordem_servico_itens' e não filtrar
--     -- por status dos itens, pois assim saberemos se após deletar lógicamente os itens na query anterior, a OS se
--     -- mantem com algum item dentro dela, seja pendente ou resolvido. Caso tiver, não devemos deletar.
--     UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO_DATA COSD
--     SET DELETADO            = TRUE,
--         DATA_HORA_DELETADO  = NOW(),
--         PG_USERNAME_DELECAO = SESSION_USER
--     WHERE COSD.COD_UNIDADE = F_COD_UNIDADE
--       AND NOT DELETADO -- Se já está deletada, não nos interessa.
--       AND COSD.CODIGO IN (COD_ORDENS_SERVICO_ANALISAR)
--       AND ((SELECT COUNT(COSI.CODIGO)
--             FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
--             WHERE COSI.COD_OS = COSD.CODIGO
--               AND COSI.COD_UNIDADE = F_COD_UNIDADE) = 0);
--
--     -- Pode acontecer um cenário onde a OS tenha 2 itens, um resolvido e um pendente. Neste cenário a OS está aberta,
--     -- ao deletar o item pendente, devemos fechar a OS e inserir a data de fechamento como a data do último item
--     -- resolvido.
--     -- The secret key:
--     -- O segredo aqui esta em usar a view 'checklist_ordem_servico' para realizar o update, pois ela já não trará as
--     -- OSs que foram deletados na query acima. Bastando verificar se a OS não tem nenhum item pendente, para esses
--     -- casos buscamos a maior 'data_hora_conserto' e usamos ela para fechar a OS.
--     UPDATE PUBLIC.CHECKLIST_ORDEM_SERVICO AS COS
--     SET STATUS               = 'F',
--         DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
--                                 FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
--                                 WHERE COSI.COD_OS = COS.CODIGO
--                                   AND COSI.COD_UNIDADE = COS.COD_UNIDADE)
--     WHERE COS.COD_UNIDADE = F_COD_UNIDADE
--       AND COS.CODIGO IN (COD_ORDENS_SERVICO_ANALISAR)
--       AND COS.STATUS = 'A'
--       AND ((SELECT COUNT(COSI.CODIGO)
--             FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
--             WHERE COSI.COD_OS = COS.CODIGO
--               AND COSI.COD_UNIDADE = F_COD_UNIDADE
--               AND COSI.STATUS_RESOLUCAO = 'P') = 0);
-- END;
-- $$;
--
-- alter function func_veiculo_deleta_servicos_abertos_placa(bigint, bigint) owner to prolog_user;


-- Natan alterou arquivo errado.
-- Voltamos para a versão de Prod.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_TRANSFERE_VEICULO(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                     F_COD_UNIDADE_DESTINO BIGINT,
                                                                     F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                                     F_PLACA TEXT,
                                                                     F_OBSERVACAO TEXT,
                                                                     F_TOKEN_INTEGRACAO TEXT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                        BIGINT := (SELECT ITI.COD_EMPRESA
                                                    FROM INTEGRACAO.TOKEN_INTEGRACAO ITI
                                                    WHERE ITI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    V_OBSERVACAO_TRANSFERENCIA_PNEU      TEXT   := 'Transferência de pneus aplicados';
    V_COD_VEICULO                        BIGINT;
    V_COD_DIAGRAMA_VEICULO               BIGINT;
    V_COD_TIPO_VEICULO                   BIGINT;
    V_KM_VEICULO                         BIGINT;
    V_COD_COLABORADOR                    BIGINT;
    V_COD_UNIDADE_COLABORADOR            BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT;
    V_COD_INFORMACOES_TRANSFERENCIA      BIGINT;
    V_COD_PNEUS_TRANSFERIR               TEXT[];
BEGIN
    -- Verificamos se a empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado não está autorizado: %s', F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verificamos se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_ORIGEM,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_DESTINO,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT V.CODIGO,
           VT.COD_DIAGRAMA,
           VT.CODIGO,
           V.KM
    FROM VEICULO_DATA V
             JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
        -- Fazemos esse Join para remover a placa do retorno caso ela não estiver mapeada na tabela de integração
             JOIN INTEGRACAO.VEICULO_CADASTRADO VC ON V.CODIGO = VC.COD_VEICULO_CADASTRO_PROLOG
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_DIAGRAMA_VEICULO, V_COD_TIPO_VEICULO, V_KM_VEICULO;

    -- Verificamos se o veículo pertence a unidade origem.
    IF (V_COD_VEICULO IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('A placa (%s) não está cadastrada no Sistema Prolog', F_PLACA));
    END IF;

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT C.CODIGO,
           C.COD_UNIDADE
    FROM COLABORADOR_DATA C
    WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA
    INTO V_COD_COLABORADOR, V_COD_UNIDADE_COLABORADOR;

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF (V_COD_COLABORADOR IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s não está cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Precisamos desativar as constraints, para verificar apenas no commit da function, assim poderemos transferir o
    -- veículo e seus pneus com segurança.
    SET CONSTRAINTS ALL DEFERRED;

    -- Cria processo de transferência para veículo.
    INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                               COD_UNIDADE_DESTINO,
                                               COD_UNIDADE_COLABORADOR,
                                               COD_COLABORADOR_REALIZACAO,
                                               DATA_HORA_TRANSFERENCIA_PROCESSO,
                                               OBSERVACAO)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA_VEICULO;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA_VEICULO IS NULL OR V_COD_PROCESSO_TRANSFERENCIA_VEICULO <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transferência';
    END IF;

    -- Insere valores da transferência do veículo
    INSERT INTO VEICULO_TRANSFERENCIA_INFORMACOES(COD_PROCESSO_TRANSFERENCIA,
                                                  COD_VEICULO,
                                                  COD_DIAGRAMA_VEICULO,
                                                  COD_TIPO_VEICULO,
                                                  KM_VEICULO_MOMENTO_TRANSFERENCIA)
    VALUES (V_COD_PROCESSO_TRANSFERENCIA_VEICULO,
            V_COD_VEICULO,
            V_COD_DIAGRAMA_VEICULO,
            V_COD_TIPO_VEICULO,
            V_KM_VEICULO)
    RETURNING CODIGO INTO V_COD_INFORMACOES_TRANSFERENCIA;

    -- Verifica se os valores da transferência foram adicionados com sucesso.
    IF (V_COD_INFORMACOES_TRANSFERENCIA IS NULL OR V_COD_INFORMACOES_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao adicionar valores da transferência';
    END IF;

    -- TODO - verificar através da func, se devemos fechar ou não Itens de O.S (FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO)
    -- Deleta O.S. do veículo transferido.
    PERFORM FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(V_COD_VEICULO,
                                                               V_COD_INFORMACOES_TRANSFERENCIA,
                                                               F_DATA_HORA);

    -- Transfere veículo.
    UPDATE VEICULO
    SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
    WHERE CODIGO = V_COD_VEICULO;

    -- Transfere veículo na integração.
    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO
    WHERE COD_VEICULO_CADASTRO_PROLOG = V_COD_VEICULO;

    -- Verifica se placa possui pneus aplicados, caso tenha, transferimos esses pneus para a unidade origem.
    IF (EXISTS(SELECT COD_PNEU FROM VEICULO_PNEU WHERE PLACA = F_PLACA))
    THEN
        -- Criamos array com os cod_pneu.
        SELECT ARRAY_AGG(P.CODIGO_CLIENTE)
        FROM PNEU_DATA P
        WHERE P.CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA)
        INTO V_COD_PNEUS_TRANSFERIR;

        V_COD_PROCESSO_TRANSFERENCIA_PNEU =
                (INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM,
                                                                    F_COD_UNIDADE_DESTINO,
                                                                    F_CPF_COLABORADOR_TRANSFERENCIA,
                                                                    V_COD_PNEUS_TRANSFERIR,
                                                                    V_OBSERVACAO_TRANSFERENCIA_PNEU,
                                                                    F_TOKEN_INTEGRACAO,
                                                                    F_DATA_HORA,
                                                                    TRUE));
        -- Verifica se a transferência deu certo.
        IF (V_COD_PROCESSO_TRANSFERENCIA_PNEU <= 0)
        THEN
            RAISE EXCEPTION
                'Erro ao transferir os pneus (%s) aplicados ao veículo', ARRAY_TO_STRING(V_COD_PNEUS_TRANSFERIR, ', ');
        END IF;

        --  Modifica unidade dos vínculos.
        UPDATE VEICULO_PNEU
        SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
        WHERE PLACA = F_PLACA;

        -- Adiciona VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU.
        INSERT INTO VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(COD_VEICULO_TRANSFERENCIA_INFORMACOES,
                                                                COD_PROCESSO_TRANSFERENCIA_PNEU)
        VALUES (V_COD_INFORMACOES_TRANSFERENCIA,
                V_COD_PROCESSO_TRANSFERENCIA_PNEU);
    END IF;
END;
$$;

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
create or replace function integracao.func_fix_servicos_cadastro_pneus(cod_pneus bigint[],
                                                                       out execution_message text)
    language plpgsql
as
$$
declare
    cod_pneu_prolog       bigint;
    cod_unidade_pneu      bigint;
    cod_modelo_banda_pneu bigint;
    vida_atual_pneu       integer;
    valor_banda           real;
begin
    foreach cod_pneu_prolog in array cod_pneus
        loop
            select p.cod_unidade,
                   p.cod_modelo_banda,
                   p.vida_atual,
                   psrd.custo
            from pneu p
                     join pneu_servico_realizado_data psrd
                          on p.codigo = psrd.cod_pneu
                     join pneu_servico_realizado_incrementa_vida_data psrivd
                          on psrd.codigo = psrivd.cod_servico_realizado and
                             psrd.fonte_servico_realizado = psrivd.fonte_servico_realizado
            where p.codigo = cod_pneu_prolog
            order by psrd.codigo desc
            limit 1
            into cod_unidade_pneu, cod_modelo_banda_pneu, vida_atual_pneu, valor_banda;

            perform func_pneu_realiza_incremento_vida_cadastro(cod_unidade_pneu,
                                                               cod_pneu_prolog,
                                                               cod_modelo_banda_pneu,
                                                               valor_banda,
                                                               vida_atual_pneu);
        end loop;
    select 'Pneus corrigidos, testar as listagens, aferições e movimentações para validar' into execution_message;
end;
$$;


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
                   -- Forçamos FALSE caso o pneu já possua uma banda aplicada.
                F_IF(PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO),
                F_DATA_HORA_PNEU_CADASTRO)
        RETURNING CODIGO INTO COD_PNEU_PROLOG;

        -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
        IF (PNEU_POSSUI_BANDA)
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
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
                          COD_CLIENTE_PNEU_CADASTRO   = F_CODIGO_PNEU_CLIENTE,
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
    PNEU_PRIMEIRA_VIDA CONSTANT BIGINT := 1;
    COD_UNIDADE_ATUAL_PNEU      BIGINT;
    VIDA_ATUAL_PNEU             BIGINT;
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

        -- Apenas cadastramos um serviço de nova vida caso o pneu não esteja na primeira vida.
        IF (F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA)
        THEN
            -- Como deletamos todos os serviços e o pneu não está na primeira vida, cadastramos o serviço referente a
            -- vida atual do pneu.
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               F_COD_PNEU_PROLOG,
                                                               F_COD_MODELO_BANDA_PNEU,
                                                               F_VALOR_BANDA_PNEU,
                                                               F_VIDA_ATUAL_PNEU);
        END IF;
    ELSE
        -- Se as vidas são iguais, apenas iremos atualizar as informações da banda e custo no serviço já realizado.
        PERFORM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(F_COD_PNEU_PROLOG,
                                                    F_COD_MODELO_BANDA_PNEU,
                                                    F_VALOR_BANDA_PNEU);
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