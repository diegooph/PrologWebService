-- Sobre:
-- Esta function invalida uma solicitação de socorro através dos dados recebidos por parâmetro.
--
-- A invalidação consiste em uma atualização de status, na tabela pai socorro_rota, e uma inserção na tabela
-- socorro_rota_invalidacao.
--
-- Observação:
-- Verificar permissões TRATAR_SOCORRO ou ABERTURA_SOCORRO.
--
-- Quem tem TRATAR_SOCORRO, pode invalidar qualquer socorro que:
-- Não esteja finalizado ou invalidado (verificado e lançado mensagens personalizadas).
--
-- Quem tem ABERTURA_SOCORRO, pode invalidar somente as próprias solicitações em aberto (verificado e lançado
-- mensagens personalizadas).
--
-- Histórico:
-- 2019-12-23 -> Function criada (wvinim - PL-2427).
-- 2020-02-11 -> Atualiza na tabela pai o código de invalidação (wvinim PL-2521).
-- 2020-02-14 -> Insere a plataforma de origem e a versão (wvinim PL-2527).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_INVALIDACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_INVALIDACAO BIGINT,
                                                         F_MOTIVO_INVALIDACAO TEXT,
                                                         F_DATA_HORA_INVALIDACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_INVALIDACAO TEXT,
                                                         F_URL_FOTO_2_INVALIDACAO TEXT,
                                                         F_URL_FOTO_3_INVALIDACAO TEXT,
                                                         F_LATITUDE_INVALIDACAO TEXT,
                                                         F_LONGITUDE_INVALIDACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_DEVICE_ID_INVALIDACAO TEXT,
                                                         F_DEVICE_IMEI_INVALIDACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_INVALIDACAO BIGINT,
                                                         F_ANDROID_API_VERSION_INVALIDACAO INTEGER,
                                                         F_MARCA_DEVICE_INVALIDACAO TEXT,
                                                         F_MODELO_DEVICE_INVALIDACAO TEXT,
                                                         F_PLATAFORMA_ORIGEM PROLOG_PLATAFORMA_SOCORRO_ROTA_TYPE,
                                                         F_VERSAO_PLATAFORMA_ORIGEM TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_PERMISSAO_TRATAR_SOCORRO                BOOLEAN;
    F_PERMISSAO_ABERTURA_SOCORRO              BOOLEAN;
    F_STATUS_SOCORRO                          SOCORRO_ROTA_STATUS_TYPE;
    F_COD_SOCORRO_INVALIDACAO_INSERIDO        BIGINT;
    F_QTD_LINHAS_ATUALIZADAS                  BIGINT;
    F_COD_COLABORADOR_ABERTURA       CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ABERTURA
                                                          FROM SOCORRO_ROTA_ABERTURA
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO    CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                          FROM SOCORRO_ROTA_ATENDIMENTO
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_PERMISSAO_TRATAR_SOCORRO   CONSTANT INTEGER := 146;
    F_COD_PERMISSAO_ABERTURA_SOCORRO CONSTANT INTEGER := 145;
BEGIN
    -- Verifica se o socorro em rota existe.
    IF F_COD_COLABORADOR_ABERTURA IS NULL
        THEN
        PERFORM THROW_GENERIC_ERROR(
                    'Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está invalidando.
    IF F_COD_COLABORADOR_ATENDIMENTO IS NOT NULL AND F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_INVALIDACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível invalidar um socorro que foi atendido por outro colaborador.');
    END IF;

    F_PERMISSAO_TRATAR_SOCORRO := (SELECT *
                                   FROM
                                       FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                               F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_TRATAR_SOCORRO));
    F_PERMISSAO_ABERTURA_SOCORRO := (SELECT *
                                     FROM
                                         FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                                 F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_ABERTURA_SOCORRO));
    F_STATUS_SOCORRO := (SELECT STATUS_ATUAL
                         FROM SOCORRO_ROTA
                         WHERE CODIGO = F_COD_SOCORRO_ROTA);

    -- Caso tenha a permissão de tratar socorros, impede a invalidação caso os status sejam INVALIDO e FINALZADO.
    IF F_PERMISSAO_TRATAR_SOCORRO
    THEN
        IF F_STATUS_SOCORRO = 'INVALIDO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi invalidado.');
        END IF;
        IF F_STATUS_SOCORRO = 'FINALIZADO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi finalizado.');
        END IF;
    -- Caso tenha a permissão de abertura, impede a invalidação de socorros de outros colaboradores e também
    -- de socorros que não estão mais em aberto.
    ELSEIF F_PERMISSAO_ABERTURA_SOCORRO
    THEN
        IF F_COD_COLABORADOR_ABERTURA <> F_COD_COLABORADOR_INVALIDACAO
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Você não tem permissão para invalidar pedidos de socorro de outros colaboradores.');
        END IF;
        IF F_STATUS_SOCORRO <> 'ABERTO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele não está mais em aberto.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                    'Você não ter permissão para invalidar o socorro.');
    END IF;

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    INSERT INTO SOCORRO_ROTA_INVALIDACAO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_INVALIDACAO,
                                          MOTIVO_INVALIDACAO,
                                          DATA_HORA_INVALIDACAO,
                                          URL_FOTO_1_INVALIDACAO,
                                          URL_FOTO_2_INVALIDACAO,
                                          URL_FOTO_3_INVALIDACAO,
                                          LATITUDE_INVALIDACAO,
                                          LONGITUDE_INVALIDACAO,
                                          PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          DEVICE_ID_INVALIDACAO,
                                          DEVICE_IMEI_INVALIDACAO,
                                          DEVICE_UPTIME_MILLIS_INVALIDACAO,
                                          ANDROID_API_VERSION_INVALIDACAO,
                                          MARCA_DEVICE_INVALIDACAO,
                                          MODELO_DEVICE_INVALIDACAO,
                                          PLATAFORMA_ORIGEM,
                                          VERSAO_PLATAFORMA_ORIGEM)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_INVALIDACAO,
            F_MOTIVO_INVALIDACAO,
            F_DATA_HORA_INVALIDACAO,
            F_URL_FOTO_1_INVALIDACAO,
            F_URL_FOTO_2_INVALIDACAO,
            F_URL_FOTO_3_INVALIDACAO,
            F_LATITUDE_INVALIDACAO,
            F_LONGITUDE_INVALIDACAO,
            F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_DEVICE_ID_INVALIDACAO,
            F_DEVICE_IMEI_INVALIDACAO,
            F_DEVICE_UPTIME_MILLIS_INVALIDACAO,
            F_ANDROID_API_VERSION_INVALIDACAO,
            F_MARCA_DEVICE_INVALIDACAO,
            F_MODELO_DEVICE_INVALIDACAO,
            F_PLATAFORMA_ORIGEM,
            F_VERSAO_PLATAFORMA_ORIGEM) RETURNING CODIGO INTO F_COD_SOCORRO_INVALIDACAO_INSERIDO;

    IF F_COD_SOCORRO_INVALIDACAO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_INVALIDACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA
        SET STATUS_ATUAL    = 'INVALIDO',
            COD_INVALIDACAO = F_COD_SOCORRO_INVALIDACAO_INSERIDO
        WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_INVALIDACAO_INSERIDO;
END;
$$;