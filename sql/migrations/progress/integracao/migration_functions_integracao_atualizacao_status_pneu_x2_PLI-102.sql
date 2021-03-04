DROP FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(F_COD_PNEU BIGINT,
    F_STATUS_PNEU CHARACTER VARYING,
    F_DEVE_FECHAR_SERVICOS BOOLEAN,
    F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE);

-- Cria nova function;
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(F_COD_PNEU BIGINT,
                                                       F_STATUS_PNEU CHARACTER VARYING,
                                                       F_DEVE_FECHAR_SERVICOS BOOLEAN,
                                                       F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_STATUS_ATUAL_PNEU   TEXT    := (SELECT STATUS
                                      FROM PNEU
                                      WHERE CODIGO = F_COD_PNEU);
    V_TEM_SERVICO_ABERTO  BOOLEAN := (SELECT EXISTS(SELECT COD_PNEU
                                                    FROM AFERICAO_MANUTENCAO_DATA
                                                    WHERE COD_PNEU = F_COD_PNEU
                                                      AND DELETADO IS FALSE
                                                      AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                                      AND FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE));
    V_RETIRADO_DO_VEICULO BOOLEAN := (SELECT (V_STATUS_ATUAL_PNEU = 'EM_USO' AND F_STATUS_PNEU <> 'EM_USO'));
BEGIN
    SET CONSTRAINTS ALL DEFERRED;
    --VERIFICA SE PNEU POSSUI SERVIÇOS EM ABERTO
    IF (V_TEM_SERVICO_ABERTO AND V_RETIRADO_DO_VEICULO AND F_DEVE_FECHAR_SERVICOS)
    THEN
        PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(F_COD_PNEU, F_DATA_HORA_ALTERACAO_STATUS);
    END IF;

    IF (V_RETIRADO_DO_VEICULO)
    THEN
        -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
        DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = F_COD_PNEU;
    END IF;

    --ATUALIZA STATUS
    UPDATE PUBLIC.PNEU
    SET STATUS = F_STATUS_PNEU
    WHERE CODIGO = F_COD_PNEU;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                       F_COD_PNEU,
                       F_STATUS_PNEU));
    END IF;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

DROP FUNCTION
    INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
    F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[]);

CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
                                                           F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_PNEUS_PROLOG CONSTANT BIGINT[]     := (SELECT ARRAY_AGG(PC.COD_PNEU_CADASTRO_PROLOG)
                                                 FROM INTEGRACAO.PNEU_CADASTRADO PC
                                                 WHERE PC.COD_EMPRESA_CADASTRO = (SELECT TI.COD_EMPRESA
                                                                                  FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                                  WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
                                                   AND PC.COD_PNEU_SISTEMA_INTEGRADO = ANY (F_COD_SISTEMA_INTEGRADO_PNEUS));
    V_STATUS_PNEU               VARCHAR(255) := 'ESTOQUE';
    V_VALOR_PNEU_LOOP           BIGINT;
    V_DATA_HORA                 TIMESTAMP    := NOW();
BEGIN
    SET CONSTRAINTS ALL DEFERRED;
    /*
        Mando todos para estoque sem remover os serviços do pneu.
        No momento da atualização, eu tenho que identificar se os pneus vão ir para o mesmo veículo.
        Se eles forem para o mesmo veículo, os serviços permanecem, caso não, são fechados.
    */
    -- Remove vinculos dos pneus.
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE COD_PNEU = ANY (V_COD_PNEUS_PROLOG);

    -- Muda o status dos pneus.
    FOREACH V_VALOR_PNEU_LOOP IN ARRAY V_COD_PNEUS_PROLOG
        LOOP
            PERFORM INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(V_VALOR_PNEU_LOOP,
                                                                       V_STATUS_PNEU,
                                                                       FALSE,
                                                                       V_DATA_HORA);
        END LOOP;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
    F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
    F_COD_UNIDADE_PNEU BIGINT,
    F_CPF_COLABORADOR_ALTERACAO_STATUS CHARACTER VARYING,
    F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE,
    F_STATUS_PNEU CHARACTER VARYING,
    F_TROCOU_DE_BANDA BOOLEAN,
    F_COD_NOVO_MODELO_BANDA_PNEU BIGINT,
    F_VALOR_NOVA_BANDA_PNEU NUMERIC,
    F_PLACA_VEICULO_PNEU_APLICADO CHARACTER VARYING,
    F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
    F_TOKEN_INTEGRACAO CHARACTER VARYING);

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                            F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                                            F_COD_UNIDADE_PNEU BIGINT,
                                                                            F_CPF_COLABORADOR_ALTERACAO_STATUS CHARACTER VARYING,
                                                                            F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE,
                                                                            F_STATUS_PNEU CHARACTER VARYING,
                                                                            F_TROCOU_DE_BANDA BOOLEAN,
                                                                            F_COD_NOVO_MODELO_BANDA_PNEU BIGINT,
                                                                            F_VALOR_NOVA_BANDA_PNEU NUMERIC,
                                                                            F_PLACA_VEICULO_PNEU_APLICADO CHARACTER VARYING,
                                                                            F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                                            F_TOKEN_INTEGRACAO CHARACTER VARYING)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA_PNEU            BIGINT  := (SELECT TI.COD_EMPRESA
                                              FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                              WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    V_COD_VEICULO_PROLOG          BIGINT  := (SELECT V.CODIGO
                                              FROM PUBLIC.VEICULO V
                                              WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                                AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                      FROM PUBLIC.UNIDADE U
                                                                      WHERE U.COD_EMPRESA = V_COD_EMPRESA_PNEU));
    V_IS_POSICAO_ESTEPE           BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                      AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    V_COD_PNEU_PROLOG             BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                              FROM INTEGRACAO.PNEU_CADASTRADO PC
                                              WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                                AND PC.COD_EMPRESA_CADASTRO = V_COD_EMPRESA_PNEU);
    V_VIDA_ATUAL_PNEU             INTEGER := (SELECT P.VIDA_ATUAL
                                              FROM PUBLIC.PNEU P
                                              WHERE P.CODIGO = V_COD_PNEU_PROLOG);
    V_PROXIMA_VIDA_PNEU           INTEGER := V_VIDA_ATUAL_PNEU + 1;
    V_STATUS_APLICADO_VEICULO     TEXT    := 'EM_USO';
    V_COD_SERVICO_INCREMENTA_VIDA BIGINT;
BEGIN
    SET CONSTRAINTS ALL DEFERRED;
    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA_PNEU,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se a Unidade repassada existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                        FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                               F_COD_UNIDADE_PNEU));

    -- Validamos se a Unidade pertence a Empresa do token repassado.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA_PNEU,
                                                F_COD_UNIDADE_PNEU,
                                                FORMAT('A Unidade %s não está configurada para esta empresa',
                                                       F_COD_UNIDADE_PNEU));

    -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF (V_COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;


    /*
        Com o removePneusAplicados fazendo com que os pneus permanecem em ESTOQUE com os serviçois em aberto,
        eu faço a validação de onde esses pneus vão estar novamente. Caso o destino do pneu seja o proprio veículo
        que ele estava, não vamos fechar os serviços, caso contrario os serviços serão fechados.
    */

    -- Se o pneu continuar com o status EM_USO devemos validar se ele vai permanecer no mesmo veículo, caso ele
    -- permaneça, não devemos fechar os serviços do mesmo.
    IF (F_STATUS_PNEU = V_STATUS_APLICADO_VEICULO)
    THEN

        -- Atualiza status pneu sem fechar os serviços em aberto.
        PERFORM INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(V_COD_PNEU_PROLOG,
                                                                   F_STATUS_PNEU,
                                                                   FALSE,
                                                                   F_DATA_HORA_ALTERACAO_STATUS);
    ELSE
        -- Atualiza status pneu fechando os serviços em aberto.
        PERFORM INTEGRACAO.FUNC_PNEU_INTERNAL_ATUALIZA_STATUS_PNEU(V_COD_PNEU_PROLOG,
                                                                   F_STATUS_PNEU,
                                                                   TRUE,
                                                                   F_DATA_HORA_ALTERACAO_STATUS);
    END IF;

    -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
    IF (F_STATUS_PNEU = V_STATUS_APLICADO_VEICULO)
    THEN
        -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
        IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = V_COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
        THEN
            UPDATE PUBLIC.PNEU
            SET COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = V_COD_VEICULO_PROLOG)
            WHERE CODIGO = V_COD_PNEU_PROLOG;

            SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = V_COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
        END IF;

        PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(V_COD_VEICULO_PROLOG,
                                                                F_PLACA_VEICULO_PNEU_APLICADO,
                                                                V_COD_PNEU_PROLOG,
                                                                F_CODIGO_PNEU_CLIENTE,
                                                                F_COD_UNIDADE_PNEU,
                                                                F_POSICAO_VEICULO_PNEU_APLICADO,
                                                                V_IS_POSICAO_ESTEPE);
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
                               WHERE MB.COD_EMPRESA = V_COD_EMPRESA_PNEU
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
        SELECT *
        FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(V_COD_EMPRESA_PNEU)
        INTO V_COD_SERVICO_INCREMENTA_VIDA;

        IF (V_COD_SERVICO_INCREMENTA_VIDA IS NULL)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR('Erro ao vincular banda ao pneu');
        END IF;

        -- Incrementa a vida do pneu simulando um processo de movimentação.
        PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(F_COD_UNIDADE_PNEU,
                                                                          V_COD_PNEU_PROLOG,
                                                                          F_COD_NOVO_MODELO_BANDA_PNEU,
                                                                          F_VALOR_NOVA_BANDA_PNEU,
                                                                          V_PROXIMA_VIDA_PNEU,
                                                                          V_COD_SERVICO_INCREMENTA_VIDA);

        -- Após incrementar a vida e criar o serviço, atualizamos o pneu para ficar com a banda e a vida correta.
        PERFORM PUBLIC.FUNC_PNEUS_INCREMENTA_VIDA_PNEU(V_COD_PNEU_PROLOG, F_COD_NOVO_MODELO_BANDA_PNEU);
    END IF;

    RETURN V_COD_PNEU_PROLOG;
END;
$$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(F_COD_VEICULO_PROLOG BIGINT,
    F_PLACA_VEICULO_PNEU_APLICADO TEXT,
    F_COD_PNEU_PROLOG BIGINT,
    F_CODIGO_PNEU_CLIENTE TEXT,
    F_COD_UNIDADE_PNEU BIGINT,
    F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
    F_IS_POSICAO_ESTEPE BOOLEAN);

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
BEGIN
    SET CONSTRAINTS ALL DEFERRED;

    -- VALIDAMOS SE A PLACA EXISTE NO PROLOG.
    IF (F_COD_VEICULO_PROLOG IS NULL OR F_COD_VEICULO_PROLOG <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A Placa %s repassada não existe no Sistema Prolog',
                                                  F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;

    -- VALIDAMOS SE O PLACA E O PNEU PERTENCEM A MESMA UNIDADE.
    IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG) <> F_COD_UNIDADE_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('A placa informada %s está em uma unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                       F_PLACA_VEICULO_PNEU_APLICADO,
                       F_CODIGO_PNEU_CLIENTE,
                       (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG),
                       F_COD_UNIDADE_PNEU));
    END IF;

    -- VALIDAMOS SE A POSIÇÃO REPASSADA É UMA POSIÇÃO VÁLIDA NO PROLOG.
    IF (NOT IS_PLACA_POSICAO_PNEU_VALIDA(F_COD_VEICULO_PROLOG, F_POSICAO_VEICULO_PNEU_APLICADO, F_IS_POSICAO_ESTEPE))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
                       F_POSICAO_VEICULO_PNEU_APLICADO,
                       F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;

    -- Realiza os vínculos.
    PERFORM INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA_VEICULO_PNEU_APLICADO,
                                                                     F_COD_PNEU_PROLOG,
                                                                     F_POSICAO_VEICULO_PNEU_APLICADO);

    -- Valida se o pneu está na posição correta.
    IF ((SELECT VP.POSICAO
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_COD_PNEU_PROLOG
           AND VP.PLACA = F_PLACA_VEICULO_PNEU_APLICADO) =
        F_POSICAO_VEICULO_PNEU_APLICADO)
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
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
    F_COD_PNEU BIGINT,
    F_POSICAO INTEGER);

-- Criar func que apenas muda de posição o pneu
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
                                                                                    F_COD_PNEU BIGINT,
                                                                                    F_POSICAO INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_UNIDADE  BIGINT := (SELECT COD_UNIDADE
                              FROM VEICULO_DATA
                              WHERE PLACA = F_PLACA);
    V_COD_DIAGRAMA BIGINT := (SELECT COD_DIAGRAMA
                              FROM VEICULO_TIPO
                              WHERE CODIGO = (SELECT COD_TIPO FROM VEICULO_DATA WHERE PLACA = F_PLACA));
BEGIN
    -- Valida se posição existe no diagrama.
    IF NOT EXISTS(SELECT VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.COD_DIAGRAMA = (SELECT V.COD_DIAGRAMA FROM VEICULO_DATA V WHERE V.PLACA = F_PLACA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A posição %s não existe no diagrama do veículo de placa %s',
                                                  F_POSICAO,
                                                  F_PLACA));
    END IF;

    -- Verifica se tem pneu aplicado nessa posição, caso tenha é prq não passou pelo método
    -- do Java de removePneusAplicados;
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.POSICAO = F_POSICAO AND VP.PLACA = F_PLACA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Erro! O veículo %s já possui pneu aplicado na posição %s',
                                                  F_PLACA,
                                                  F_POSICAO));
    END IF;

    SET CONSTRAINTS ALL DEFERRED;
    -- Não tem pneu aplicado a posição, então eu adiciono.
    INSERT INTO VEICULO_PNEU(PLACA, COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA)
    VALUES (F_PLACA, F_COD_PNEU, V_COD_UNIDADE, F_POSICAO, V_COD_DIAGRAMA);
END;
$$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

DROP FUNCTION INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
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
    F_TOKEN_INTEGRACAO CHARACTER VARYING);

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
    SET CONSTRAINTS ALL DEFERRED;
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