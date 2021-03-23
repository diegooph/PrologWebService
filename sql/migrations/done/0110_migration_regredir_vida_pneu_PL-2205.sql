ALTER TABLE PNEU_SERVICO_CADASTRO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;

-- Busca utilitária.
-- Pneus do caso 1 ou 2.
-- select psr.cod_pneu,
--        count(psr.cod_pneu) + 1 as vida_atual_pneu,
--        psr.cod_unidade
-- from pneu_servico_realizado psr
--          join pneu_servico_realizado_incrementa_vida psriv on psr.codigo = psriv.cod_servico_realizado
--          join pneu p on psr.cod_pneu = p.codigo
-- where p.cod_empresa <> 64 -- Empresa de testes.
--   and p.status <> 'DESCARTE'
-- group by psr.cod_pneu, psr.cod_unidade
-- having count(psr.cod_pneu) > 2;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_UPDATE_OK(F_UPDATE_OK BOOLEAN, F_ERROR_MESSAGE TEXT DEFAULT NULL::TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_ERROR_MESSAGE TEXT := F_IF(F_ERROR_MESSAGE IS NULL, 'Erro ao realizar o update!', F_ERROR_MESSAGE);
BEGIN
    IF NOT F_UPDATE_OK
    THEN
        PERFORM THROW_GENERIC_ERROR(V_ERROR_MESSAGE);
    END IF;
END;
$$;

ALTER FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(BIGINT, BIGINT, BIGINT, REAL, INTEGER)
    SET SCHEMA PUBLIC;

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