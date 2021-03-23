-- Sobre:
--
-- Function disponível na API do ProLog para atualizar o status de um pneu. Essa function simula movimentações nos
-- pneus.
--
-- Esta function recebe as informações necessárias para atualizar o status do pneu para qualquer um dos possíveis:
--   • EM_USO.
--   • ESTOQUE.
--   • ANALISE.
--   • DESCARTE.
-- Caso o status do pneu for EM_USO, a function irá aplicar o pneu na placa e posição repassadas para ela. Para os
-- demais status é apenas atualizado as informações do pneu.
--
-- A function recebe uma flag indicando se houve uma troca de banda no pneu, se houve, então a function irá realizar
-- o incremento da vida e a troca da banda do pneu.
--
-- Para todos os pneus que sofrerem atualização de Status, iremos fechar todos os seus serviços pendentes, de forma
-- automática, sentando uma flag onde indicará que aquele serviço foi resolvido de forma automática pela integração.
--
-- Precondições:
-- 1) Se status = EM_USO, é obrigatório a existência de uma placa e posição onde o pneu será aplicado.
-- 2) Se F_TROCOU_DE_BANDA = TRUE, então o código da banda e custo são obrigatórios para o incremento de vida do pneu.
--
-- Histórico:
-- 2019-08-23 -> Function criada (diogenesvanzella - PL-2237).
-- 2019-09-13 -> Transfere pneu para a unidade do veículo caso não estejam na mesma (diogenesvanzella - PL-2237).
-- 2019-09-18 -> Fecha automaticamente serviços de pneus para atualizações de status (diogenesvanzella - PL-2302).
-- 2020-01-03 -> Altera tipagem de recebimento da data_hora (diogenesvanzella - PLI-30).
-- 2020-08-06 -> Volta arquivo base para versão de Prod (diogenesvanzella - PLI-175).
-- 2020-08-06 -> Adapta function para lidar com tokens repetidos (diogenesvanzella - PLI-175).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
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
                                                     F_TOKEN_INTEGRACAO CHARACTER VARYING) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_PNEU            BIGINT  := (SELECT U.COD_EMPRESA
                                            FROM PUBLIC.UNIDADE U
                                            WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    COD_VEICULO_PROLOG          BIGINT  := (SELECT V.CODIGO
                                            FROM PUBLIC.VEICULO V
                                            WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                              AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                    FROM PUBLIC.UNIDADE U
                                                                    WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
    IS_POSICAO_ESTEPE           BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                    AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    COD_PNEU_PROLOG             BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                            FROM INTEGRACAO.PNEU_CADASTRADO PC
                                            WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                              AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    VIDA_ATUAL_PNEU             INTEGER := (SELECT P.VIDA_ATUAL
                                            FROM PUBLIC.PNEU P
                                            WHERE P.CODIGO = COD_PNEU_PROLOG);
    PROXIMA_VIDA_PNEU           INTEGER := VIDA_ATUAL_PNEU + 1;
    STATUS_APLICADO_VEICULO     TEXT    := 'EM_USO';
    COD_SERVICO_INCREMENTA_VIDA BIGINT;
    F_QTD_ROWS_ALTERADAS        BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

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
    SET STATUS      = F_STATUS_PNEU,
        COD_UNIDADE = F_COD_UNIDADE_PNEU
    WHERE CODIGO = COD_PNEU_PROLOG;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Validamos se o status do pneu foi atualizado com sucesso
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                       F_CODIGO_PNEU_CLIENTE,
                       F_STATUS_PNEU));
    END IF;

    -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
    IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
    THEN
        -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
        IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
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
        SELECT *
        FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(COD_EMPRESA_PNEU)
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

    -- Qualquer alteração de status do pneu deve verificar se o pneu tem serviços aberto e fechá-los.
    PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(COD_PNEU_PROLOG,
                                                                    F_DATA_HORA_ALTERACAO_STATUS);

    RETURN COD_PNEU_PROLOG;
END;
$$;