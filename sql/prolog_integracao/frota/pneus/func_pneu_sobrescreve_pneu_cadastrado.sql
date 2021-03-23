-- Sobre:
--
-- Function disponível na API do ProLog para sobrescrever as informações de um pneu.
--
-- Precondições:
-- 1) Caso houver uma mudança da unidade onde o pneu está alocado, devemos deletar logicamente todos os serviços
-- refererentes a manutenção (calibragens, inspeções e movimentações) do pneu.
-- 2) Se houver uma mudança na vida atual do pneu, deveremos deletar de forma lógica, todos os serviços referentes à
-- recapagens que o pneu possuir. Para esse cenário, devemos também, inserir um novo serviço de cadastro, para que não
-- aconteça de existir bandas sem valor.
-- 3) Devemos mover o pneu para 'ESTOQUE'.
--
-- Histórico:
-- 2020-01-22 -> Function criada (diogenesvanzella - PLI-43).
-- 2020-02-10 -> Corrige problema dos modelos de banda sem valor (diogenesvanzella - PLI-73).
-- 2020-02-27 -> Corrige problema ao sobrescrever pneus que voltam para a primeira vida (diogenesvanzella - PLI-90).
-- 2020-07-27 -> Volta arquivo base para versão de Prod (diogenesvanzella - PLI-189).
-- 2020-07-30 -> Corrige schema da function (diogenesvanzella - PLI-189).
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