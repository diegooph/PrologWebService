-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se o pneu existe, se ele não está aplicado ou em análise.
-- Deleta todoas os serviços do pneu em afericao_manutencao_data.
-- Deleta todos os valores do pneu em afericao_valores_data.
-- Caso todos os valores da aferição sejam deletados, deve-se deletar a aferição.
-- Caso o pneu possui integração, é deletado.
--
-- Précondições:
-- Pneu deve existir;
-- Pneu não deve estar aplicado;
-- Pneu não deve estar em análise;
-- Deletar dependências de aferições;
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER (natanrotta - PL-2229).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2019-11-27 -> Deleta os serviços em aberto do pneu e não deletados (luizfp - PL-2270).
-- 2020-01-21 -> Implementa melhoria na lógica de deleção das dependências (natanrotta - PL-2270).
-- 2020-02-24 -> Implementa melhoria na lógica de deleção das dependências (luizfp - PL-2270).
-- 2020-05-22 -> Implementa estrutura para deletar pneu integrado (natanrotta PLI-157).
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
                                                         F_CODIGO_PNEU BIGINT,
                                                         F_CODIGO_CLIENTE TEXT,
                                                         F_MOTIVO_DELECAO TEXT,
                                                         OUT AVISO_PNEU_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_STATUS_PNEU_ANALISE CONSTANT   TEXT := 'ANALISE';
    V_QTD_LINHAS_ATUALIZADAS         BIGINT;
    V_COD_AFERICAO                   BIGINT[];
    V_COD_AFERICAO_FOREACH           BIGINT;
    V_QTD_AFERICAO_VALORES           BIGINT;
    V_QTD_AFERICAO_VALORES_DELETADOS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se o pneu existe.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está aplicado.
    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_CODIGO_PNEU
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em análise.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE
           AND P.STATUS = V_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se pneu é integrado
    IF EXISTS(SELECT IPC.COD_PNEU_CADASTRO_PROLOG
              FROM INTEGRACAO.PNEU_CADASTRADO IPC
              WHERE IPC.COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
                AND IPC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE)
    THEN
        -- Deleta Pneu (Não temos deleção lógica)
        DELETE
        FROM INTEGRACAO.PNEU_CADASTRADO
        WHERE COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE;
    END IF;

    -- Deleta pneu Prolog.
    UPDATE PNEU_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_CODIGO_PNEU
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em afericao_manutencao_data.
    IF (SELECT EXISTS(SELECT AM.COD_AFERICAO
                      FROM AFERICAO_MANUTENCAO_DATA AM
                      WHERE AM.COD_PNEU = F_CODIGO_PNEU
                        AND AM.COD_UNIDADE = F_COD_UNIDADE
                        AND AM.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código Cliente % e Unidade % '
                'em afericao_manutencao_data', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Verifica se o pneu está em afericao_valores_data.
    IF (SELECT EXISTS(SELECT AV.COD_AFERICAO
                      FROM AFERICAO_VALORES_DATA AV
                      WHERE AV.COD_PNEU = F_CODIGO_PNEU
                        AND AV.COD_UNIDADE = F_COD_UNIDADE
                        AND AV.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade % em afericao_valores_data',
                F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    --Busca todos os cod_afericao deletados a partir do pneu.
    SELECT ARRAY_AGG(AV.COD_AFERICAO)
    FROM AFERICAO_VALORES_DATA AV
    WHERE AV.COD_PNEU = F_CODIGO_PNEU
      AND AV.COD_UNIDADE = F_COD_UNIDADE
      AND AV.DELETADO IS TRUE
    INTO V_COD_AFERICAO;

    -- Verifica se algum valor foi deletado em afericao_valores_data.
    IF (V_COD_AFERICAO IS NOT NULL AND ARRAY_LENGTH(V_COD_AFERICAO, 1) > 0)
    THEN
        -- Iteração com cada cod_afericao deletado em afericao_valores_data.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_COD_AFERICAO
            LOOP
                -- Coleta a quantidade de aferições em afericao_valores_data.
                V_QTD_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                          FROM AFERICAO_VALORES_DATA AVD
                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                -- Coleta a quantidade de aferições deletadas em afericao_valores_data.
                V_QTD_AFERICAO_VALORES_DELETADOS = (SELECT COUNT(AVD.COD_AFERICAO)
                                                    FROM AFERICAO_VALORES_DATA AVD
                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                      AND AVD.DELETADO IS TRUE);

                -- Verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                IF (V_QTD_AFERICAO_VALORES = V_QTD_AFERICAO_VALORES_DELETADOS)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER,
                        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
                    WHERE CODIGO = V_COD_AFERICAO_FOREACH;

                    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

                    -- Garante que a deleção foi realizada.
                    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
                    THEN
                        RAISE EXCEPTION 'Erro ao deletar aferição com Código: %, Unidade: %',
                            V_COD_AFERICAO_FOREACH, F_COD_UNIDADE;
                    END IF;
                END IF;
            END LOOP;
    END IF;

    SELECT 'PNEU DELETADO: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DO CLIENTE: '
               || F_CODIGO_CLIENTE
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_PNEU_DELETADO;
END
$$;