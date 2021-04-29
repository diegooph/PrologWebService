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
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
    END IF;

    IF (PNEU_POSSUI_BANDA
        AND NOT (SELECT *
                 FROM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(COD_PNEU_PROLOG,
                                                          F_NOVO_COD_MODELO_BANDA_PNEU,
                                                          F_NOVO_VALOR_BANDA_PNEU)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('Não foi possível atualizar a banda do pneu');
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
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