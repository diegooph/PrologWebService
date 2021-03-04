-- Sobre:
--
-- Lógica de retrocesso de vida:
--
-- Para a regressão o problema foi entendido como duas partes separadas: regressões de vida até a primeira vida e
-- regressões até uma vida diferente da primeira.
--
-- Para regressões até a primeira vida: todos os serviços das tabelas 'pneu_servico_realizado_data'
-- e 'pneu_servico_realizado_incrementa_vida_data' devem ser deletados (bem como tabelas relacionadas -
-- mesmo que não diretamente). Além disso, é preciso setar para null o código do modelo de banda na tabela 'pneu'
-- (além de regredir a vida).
--
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
--
-- Para regressões até uma vida diferente da primeira: este cenário se divide em dois casos diferentes.
-- Primeiro -> precisamos regredir a vida até uma vida POSTERIOR à vida de cadastro do pneu.
--             Alcançamos isso deletando os serviços até chegarmos na vida desejada, deixando presente o serviço de
--             incremento desta vida e qualquer outro serviço realizado que não seja de incremento de vida.
-- Exemplo:
--
-- Vida atual: 7
-- Nova vida: 4
-- Vida cadastro: 1
--
-- servico_realizado: 				  1 2 3 *4* ~5 6~
-- servico_realizado_incrementa_vida: 2 3 4 ~5 6 7~
-- Vidas entre os tis (~) devem ser deletadas.
-- Vida entre o asterisco (*) deve ter apenas o serviço que incrementa vida deletado.
--
-- Segundo -> precisamos regredir a vida até uma vida ANTERIOR à vida de cadastro do pneu.
--            Alcançamos isso deletando todos os serviços e cadastrando um novo (como FONTE_CADASTRO) para a vida da
--            qual se deseja retroceder.
-- Exemplo:
--
-- Vida atual: 7
-- Nova vida: 2
-- Vida cadastro: 4
--
-- servico_realizado: 				  ~3 4 5 6~
-- servico_realizado_incrementa_vida: ~4 5 6 7~
-- Vidas entre os tis (~) devem ser deletadas.
--
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
--
-- Importante salientear que todos os serviços da vida são deletados, não apenas os de incremento de vida, com exceção
-- dos servições que não incrementam vida da nova vida do pneu (F_VIDA_NOVA_PNEU). Esses serão mantidos, caso existam.
--
-- Os sulcos do pneu não foram tratados, isso foi proposital. Temos ciência que tal escolha pode influenciar com alertas
-- na tela de aferição, mas optamos por deixar assim ao invés de aumentar a complexidade da function.
--
-- Histórico:
-- 2020-07-15 -> Function criada (luiz_fp - PL-2205).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_RETROCEDER_VIDA_PNEU(F_COD_UNIDADE_PNEU BIGINT,
                                                                  F_COD_CLIENTE_PNEU TEXT,
                                                                  F_VIDA_NOVA_PNEU INTEGER,
                                                                  F_MOTIVO_RETROCESSO TEXT,
                                                                  OUT AVISO_PNEU_VIDA_RETROCEDIDA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_PNEU      CONSTANT      BIGINT := (SELECT P.CODIGO
                                             FROM PNEU P
                                             WHERE P.COD_UNIDADE = F_COD_UNIDADE_PNEU
                                               AND P.CODIGO_CLIENTE = F_COD_CLIENTE_PNEU);
    V_VIDA_CADASTRO CONSTANT      BIGINT := COALESCE((SELECT PSR.VIDA
                                                      FROM PUBLIC.PNEU_SERVICO_REALIZADO PSR
                                                      WHERE PSR.COD_PNEU = V_COD_PNEU
                                                        AND PSR.FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO') + 1,
                                                     1);
    V_VIDA_ATUAL_PNEU             INTEGER;
    V_COD_MODELO_BANDA_ATUAL_PNEU BIGINT;
    V_VALOR_BANDA_ATUAL_PNEU      REAL;
    V_COD_SERVICOS_DELETADOS      BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Validações.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU);

    IF (SELECT NOT EXISTS(SELECT P.CODIGO
                          FROM PNEU P
                          WHERE P.COD_UNIDADE = F_COD_UNIDADE_PNEU
                            AND P.CODIGO_CLIENTE = F_COD_CLIENTE_PNEU))
    THEN
        RAISE EXCEPTION 'Pneu da unidade % e código % não encontrado!', F_COD_UNIDADE_PNEU, F_COD_CLIENTE_PNEU;
    END IF;

    -- Busca valores.
    -- Feito após a primeira validação pois caso o pneu com os parâmetros informados não exista, queremos
    -- retornar um erro formatado e não que o STRICT estoure.
    SELECT P.VIDA_ATUAL, PVV.COD_MODELO_BANDA, PVV.VALOR
    INTO STRICT V_VIDA_ATUAL_PNEU, V_COD_MODELO_BANDA_ATUAL_PNEU, V_VALOR_BANDA_ATUAL_PNEU
    FROM PNEU P
             LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA
    WHERE P.CODIGO = V_COD_PNEU;

    IF (SELECT P.STATUS
        FROM PNEU P
        WHERE P.CODIGO = V_COD_PNEU) = 'DESCARTE'
    THEN
        RAISE EXCEPTION 'O pneu % está descartado e não pode ter sua vida retrocedida!', F_COD_CLIENTE_PNEU;
    END IF;

    -- Esse cenário já é coberto pelo conjunto das duas próximas validações, mas mantemos ele para retornar uma mensagem
    -- de erro melhor.
    IF V_VIDA_ATUAL_PNEU = 1
    THEN
        RAISE EXCEPTION 'Só é possível retroceder a vida de pneus acima da vida 1! Vida atual: %', V_VIDA_ATUAL_PNEU;
    END IF;

    IF F_VIDA_NOVA_PNEU <= 0
    THEN
        RAISE EXCEPTION 'A nova vida do pneu precisa ser um valor maior do que 0! Nova: %', F_VIDA_NOVA_PNEU;
    END IF;

    IF F_VIDA_NOVA_PNEU >= V_VIDA_ATUAL_PNEU
    THEN
        RAISE EXCEPTION 'A nova vida do pneu deve ser menor do que a vida atual! Nova: % | Atual: %',
            F_VIDA_NOVA_PNEU,
            V_VIDA_ATUAL_PNEU;
    END IF;

    -- Lógica de retrocesso.
    IF (SELECT EXISTS(SELECT PVV.COD_PNEU
                      FROM PNEU_VALOR_VIDA PVV
                      WHERE PVV.COD_PNEU = V_COD_PNEU))
    THEN
        WITH SERVICOS_DELETADOS AS (
            UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_DATA
                SET DELETADO = TRUE,
                    DATA_HORA_DELETADO = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO = F_MOTIVO_RETROCESSO
                WHERE DELETADO = FALSE
                    AND COD_PNEU = V_COD_PNEU
                    AND (VIDA > F_VIDA_NOVA_PNEU
                        OR VIDA = F_VIDA_NOVA_PNEU AND EXISTS(SELECT PSRIVD.COD_SERVICO_REALIZADO
                                                              FROM PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA PSRIVD
                                                              WHERE PSRIVD.COD_SERVICO_REALIZADO = CODIGO))
                RETURNING CODIGO
        )
        SELECT ARRAY_AGG(SD.CODIGO)
        INTO STRICT V_COD_SERVICOS_DELETADOS
        FROM SERVICOS_DELETADOS SD;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND, FORMAT('Erro ao deletar serviços do pneu %s!', F_COD_CLIENTE_PNEU));

        UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS)
          AND VIDA_NOVA_PNEU > F_VIDA_NOVA_PNEU;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND,
                                       FORMAT('Erro ao deletar serviços de incremento de vida do pneu %s!',
                                              F_COD_CLIENTE_PNEU));

        -- Estes três updates nas tabelas de serviço de cadastro/movimentação propositalmente não possuem validação de
        -- sucesso pois o pneu pode não ter entradas nestas tabelas.
        UPDATE PUBLIC.PNEU_SERVICO_CADASTRO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS);

        UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS);

        UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO_MOVIMENTACAO = ANY (V_COD_SERVICOS_DELETADOS);

        -- Se estamos regredindo a vida para uma anterior à vida de cadastro e diferente da primeira vida, precisamos
        -- cadastrar um novo serviço de FONTE_CADASTRO de incremento de vida.
        IF F_VIDA_NOVA_PNEU < V_VIDA_CADASTRO AND F_VIDA_NOVA_PNEU <> 1
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               V_COD_PNEU,
                                                               V_COD_MODELO_BANDA_ATUAL_PNEU,
                                                               V_VALOR_BANDA_ATUAL_PNEU,
                                                               F_VIDA_NOVA_PNEU);
        END IF;

        -- Por fim, atualizamos o pneu.
        UPDATE PNEU
        SET VIDA_ATUAL       = F_VIDA_NOVA_PNEU,
            COD_MODELO_BANDA = F_IF(F_VIDA_NOVA_PNEU = 1, NULL, (SELECT PVV.COD_MODELO_BANDA
                                                                 FROM PNEU_VALOR_VIDA PVV
                                                                 WHERE PVV.COD_PNEU = V_COD_PNEU
                                                                   AND PVV.VIDA = F_VIDA_NOVA_PNEU))
        WHERE CODIGO = V_COD_PNEU;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND, FORMAT('Erro ao atualizar a vida do pneu %s!', F_COD_CLIENTE_PNEU));

        SELECT FORMAT('Pneu %s teve sua vida regredida de %s para %s com sucesso!',
                      F_COD_CLIENTE_PNEU,
                      V_VIDA_ATUAL_PNEU,
                      F_VIDA_NOVA_PNEU)
        INTO AVISO_PNEU_VIDA_RETROCEDIDA;
        RETURN;
    END IF;

    RAISE EXCEPTION 'Não é possível regredir a vida do pneu %. Ele encontra-se em um estado não mapeado!',
        F_COD_CLIENTE_PNEU;
END ;
$$;