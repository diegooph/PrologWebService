BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--################################# ATUALIZA O KM DO VEÍCULO NA FUNCTION DE ABERTURA ###################################
--######################################################################################################################
--######################################################################################################################
-- PL-2528
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA(F_COD_UNIDADE BIGINT,
                                                      F_COD_COLABORADOR_ABERTURA BIGINT,
                                                      F_COD_VEICULO_PROBLEMA BIGINT,
                                                      F_KM_VEICULO_ABERTURA BIGINT,
                                                      F_COD_PROBLEMA_SOCORRO_ROTA BIGINT,
                                                      F_DESCRICAO_PROBLEMA TEXT,
                                                      F_DATA_HORA_ABERTURA TIMESTAMP WITH TIME ZONE,
                                                      F_URL_FOTO_1_ABERTURA TEXT,
                                                      F_URL_FOTO_2_ABERTURA TEXT,
                                                      F_URL_FOTO_3_ABERTURA TEXT,
                                                      F_LATITUDE_ABERTURA TEXT,
                                                      F_LONGITUDE_ABERTURA TEXT,
                                                      F_PRECISAO_LOCALIZACAO_ABERTURA_METROS NUMERIC,
                                                      F_ENDERECO_AUTOMATICO TEXT,
                                                      F_PONTO_REFERENCIA TEXT,
                                                      F_VERSAO_APP_MOMENTO_ABERTURA BIGINT,
                                                      F_DEVICE_ID_ABERTURA TEXT,
                                                      F_DEVICE_IMEI_ABERTURA TEXT,
                                                      F_DEVICE_UPTIME_MILLIS_ABERTURA BIGINT,
                                                      F_ANDROID_API_VERSION_ABERTURA INTEGER,
                                                      F_MARCA_DEVICE_ABERTURA TEXT,
                                                      F_MODELO_DEVICE_ABERTURA TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_SOCORRO_INSERIDO          BIGINT;
    F_COD_SOCORRO_ABERTURA_INSERIDO BIGINT;
    F_COD_EMPRESA                   BIGINT := (SELECT COD_EMPRESA
                                               FROM UNIDADE
                                               WHERE CODIGO = F_COD_UNIDADE);
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    -- Insere na tabela pai
    INSERT INTO SOCORRO_ROTA (COD_UNIDADE, STATUS_ATUAL)
    VALUES (F_COD_UNIDADE, 'ABERTO') RETURNING CODIGO INTO F_COD_SOCORRO_INSERIDO;

    -- Exibe erro se não puder inserir
    IF F_COD_SOCORRO_INSERIDO IS NULL OR F_COD_SOCORRO_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    -- Insere na tabela de abertura
    INSERT INTO SOCORRO_ROTA_ABERTURA (COD_SOCORRO_ROTA,
                                       COD_COLABORADOR_ABERTURA,
                                       COD_VEICULO_PROBLEMA,
                                       KM_VEICULO_ABERTURA,
                                       COD_PROBLEMA_SOCORRO_ROTA,
                                       DESCRICAO_PROBLEMA,
                                       DATA_HORA_ABERTURA,
                                       URL_FOTO_1_ABERTURA,
                                       URL_FOTO_2_ABERTURA,
                                       URL_FOTO_3_ABERTURA,
                                       LATITUDE_ABERTURA,
                                       LONGITUDE_ABERTURA,
                                       PRECISAO_LOCALIZACAO_ABERTURA_METROS,
                                       ENDERECO_AUTOMATICO,
                                       PONTO_REFERENCIA,
                                       VERSAO_APP_MOMENTO_ABERTURA,
                                       DEVICE_ID_ABERTURA,
                                       DEVICE_IMEI_ABERTURA,
                                       DEVICE_UPTIME_MILLIS_ABERTURA,
                                       ANDROID_API_VERSION_ABERTURA,
                                       MARCA_DEVICE_ABERTURA,
                                       MODELO_DEVICE_ABERTURA)
    VALUES (F_COD_SOCORRO_INSERIDO,
            F_COD_COLABORADOR_ABERTURA,
            F_COD_VEICULO_PROBLEMA,
            F_KM_VEICULO_ABERTURA,
            F_COD_PROBLEMA_SOCORRO_ROTA,
            F_DESCRICAO_PROBLEMA,
            F_DATA_HORA_ABERTURA,
            F_URL_FOTO_1_ABERTURA,
            F_URL_FOTO_2_ABERTURA,
            F_URL_FOTO_3_ABERTURA,
            F_LATITUDE_ABERTURA,
            F_LONGITUDE_ABERTURA,
            F_PRECISAO_LOCALIZACAO_ABERTURA_METROS,
            F_ENDERECO_AUTOMATICO,
            F_PONTO_REFERENCIA,
            F_VERSAO_APP_MOMENTO_ABERTURA,
            F_DEVICE_ID_ABERTURA,
            F_DEVICE_IMEI_ABERTURA,
            F_DEVICE_UPTIME_MILLIS_ABERTURA,
            F_ANDROID_API_VERSION_ABERTURA,
            F_MARCA_DEVICE_ABERTURA,
            F_MODELO_DEVICE_ABERTURA) RETURNING CODIGO INTO F_COD_SOCORRO_ABERTURA_INSERIDO;

    -- Exibe erro se não puder inserir
    IF F_COD_SOCORRO_ABERTURA_INSERIDO IS NULL OR F_COD_SOCORRO_ABERTURA_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    -- Atualiza o KM no veículo, caso:
    -- Não esteja deletado
    -- O KM coletado na abertura seja maior que o atual do veículo
    UPDATE VEICULO SET KM = F_KM_VEICULO_ABERTURA WHERE CODIGO = F_COD_VEICULO_PROBLEMA AND KM < F_KM_VEICULO_ABERTURA;

    -- Retorna o código do socorro
    RETURN F_COD_SOCORRO_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################### CRIA O DIAGRAMA DE REBOQUE 4 EIXOS #########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2506
-- Já rodado em prod, aqui apenas para histórico.

-- Cria o diagrama
-- INSERT INTO VEICULO_DIAGRAMA (CODIGO, NOME, URL_IMAGEM)
-- VALUES ('23', 'REBOQUE 4 EIXOS', 'WWW.GOOGLE.COM/REBOQUE-4-EIXOS');

-- Cria os eixos
-- INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
-- VALUES (23, 'D', 1, 4, TRUE),
--        (23, 'D', 2, 4, TRUE),
--        (23, 'T', 3, 4, FALSE),
--        (23, 'T', 4, 4, FALSE);

-- Cria a posição Prolog
-- INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
-- VALUES (23, 111),
--        (23, 112),
--        (23, 121),
--        (23, 122),
--        (23, 211),
--        (23, 212),
--        (23, 221),
--        (23, 222),
--        (23, 311),
--        (23, 312),
--        (23, 321),
--        (23, 322),
--        (23, 411),
--        (23, 412),
--        (23, 421),
--        (23, 422),
--        (23, 900),
--        (23, 901),
--        (23, 902),
--        (23, 903),
--        (23, 904),
--        (23, 905),
--        (23, 906),
--        (23, 907),
--        (23, 908);
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- 2517 - Adicionar fotos na listagem
-- Dropa a function de listagem
DROP FUNCTION FUNC_SOCORRO_ROTA_LISTAGEM(BIGINT[],DATE,DATE,TEXT);

-- Recria a function de listagem com as fotos
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_LISTAGEM(F_COD_UNIDADES BIGINT[],
                                                      F_DATA_INICIAL DATE,
                                                      F_DATA_FINAL DATE,
                                                      F_TOKEN TEXT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                          BIGINT,
                UNIDADE                                   TEXT,
                PLACA_VEICULO                             TEXT,
                VEICULO_DELETADO                          BOOLEAN,
                NOME_RESPONSAVEL_ABERTURA_SOCORRO         TEXT,
                COLABORADOR_DELETADO                      BOOLEAN,
                DESCRICAO_FORNECIDA_ABERTURA_SOCORRO      TEXT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO TEXT,
                DATA_HORA_ABERTURA_SOCORRO                TIMESTAMP WITHOUT TIME ZONE,
                ENDERECO_AUTOMATICO_ABERTURA_SOCORRO      TEXT,
                URL_FOTO_1_ABERTURA                       TEXT,
                URL_FOTO_2_ABERTURA                       TEXT,
                URL_FOTO_3_ABERTURA                       TEXT,
                STATUS_ATUAL_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Permissões para ver todos os socorros em rota
    -- 146 - TRATAR_SOCORRO
    -- 147 - VISUALIZAR_SOCORROS_E_RELATORIOS
    F_PERMISSOES_VISUALIZAR_TODOS INTEGER[] := ARRAY [146,147];
    F_VER_TODOS                   BOOLEAN   := (SELECT POSSUI_PERMISSSAO
                                                FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN,
                                                                                                F_PERMISSOES_VISUALIZAR_TODOS,
                                                                                                FALSE,
                                                                                                TRUE));
    F_COD_COLABORADOR             BIGINT    := (SELECT COD_COLABORADOR
                                                FROM TOKEN_AUTENTICACAO
                                                WHERE TOKEN = F_TOKEN);
    -- Busca o código de empresa com base na primeira unidade do array recebido
    F_COD_EMPRESA                 BIGINT    := (SELECT COD_EMPRESA
                                                FROM UNIDADE
                                                WHERE CODIGO = (SELECT (F_COD_UNIDADES)[1]));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    RETURN QUERY
        SELECT SR.CODIGO                                                      AS COD_SOCORRO_ROTA,
               U.NOME :: TEXT                                                 AS UNIDADE,
               VD.PLACA :: TEXT                                               AS PLACA_VEICULO,
               VD.DELETADO                                                    AS VEICULO_DELETADO,
               CD.NOME :: TEXT                                                AS NOME_RESPONSAVEL,
               CD.DELETADO                                                    AS COLABORADOR_DELETADO,
               SRA.DESCRICAO_PROBLEMA                                         AS DESCRICAO_FORNECIDA,
               SROP.DESCRICAO :: TEXT                                         AS DESCRICAO_OPCAO_PROBLEMA,
               SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_ABERTURA,
               SRA.ENDERECO_AUTOMATICO                                        AS ENDERECO_AUTOMATICO_ABERTURA,
               SRA.URL_FOTO_1_ABERTURA :: TEXT                                AS URL_FOTO_1_ABERTURA,
               SRA.URL_FOTO_2_ABERTURA :: TEXT                                AS URL_FOTO_2_ABERTURA,
               SRA.URL_FOTO_3_ABERTURA :: TEXT                                AS URL_FOTO_3_ABERTURA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                    AS STATUS_ATUAL_SOCORRO
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON U.CODIGO = SR.COD_UNIDADE
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
                 JOIN VEICULO_DATA VD ON SRA.COD_VEICULO_PROBLEMA = VD.CODIGO
                 JOIN COLABORADOR_DATA CD ON SRA.COD_COLABORADOR_ABERTURA = CD.CODIGO
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRA.COD_PROBLEMA_SOCORRO_ROTA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          -- Aplica o filtro por colaborador apenas se não tiver permissão para ver todos
          AND F_IF(F_VER_TODOS, TRUE, SRA.COD_COLABORADOR_ABERTURA = F_COD_COLABORADOR)
          AND (SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) :: DATE
            BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY SRA.DATA_HORA_ABERTURA DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### CRIA FUNCTION DE RELATÓRIO DE DADOS GERAIS PARA SOCORRO EM ROTA ##########################
--######################################################################################################################
--######################################################################################################################
-- PL-2523

-- Cria function para o cálculo de duração em HH:MM:SS
CREATE OR REPLACE FUNCTION FUNC_CONVERTE_INTERVAL_HHMMSS(F_INTERVAL INTERVAL)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
	DATA_FORMATADA text;
BEGIN
    SELECT CONCAT(
                   TRUNC(EXTRACT(EPOCH FROM (F_INTERVAL)) / 3600),
                   ':',
                   TO_CHAR(TO_TIMESTAMP(
                                   EXTRACT(EPOCH FROM (F_INTERVAL))
                               ), 'MI:SS')
               ) INTO DATA_FORMATADA;
    RETURN  DATA_FORMATADA;
END;
$$;

-- Cria o relatório de dados gerais de socorro em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                    F_DATA_INICIAL DATE,
                                                                    F_DATA_FINAL DATE,
                                                                    F_STATUS_SOCORRO_ROTA VARCHAR[])
    RETURNS TABLE
            (
                "CÓDIGO SOCORRO ROTA"                          TEXT,
                "STATUS SOCORRO ROTA"                          TEXT,
                "PLACA VEÍCULO ABERTURA"                       TEXT,
                "CÓDIGO COLABORADOR ABERTURA"                  TEXT,
                "NOME RESPONSÁVEL ABERTURA"                    TEXT,
                "KM VEÍCULO COLETADO ABERTURA"                 TEXT,
                "DESCRIÇÃO OPÇÃO PROBLEMA ABERTURA"            TEXT,
                "DESCRIÇÃO FORNECIDA ABERTURA"                 TEXT,
                "PONTO REFERÊNCIA FORNECIDO ABERTURA"          TEXT,
                "DATA/HORA ABERTURA"                           TEXT,
                "LATITUDE ABERTURA"                            TEXT,
                "LONGITUDE ABERTURA"                           TEXT,
                "ENDEREÇO AUTOMÁTICO ABERTURA"                 TEXT,
                "MARCA APARELHO ABERTURA"                      TEXT,
                "MODELO APARELHO ABERTURA"                     TEXT,
                "IMEI APARELHO ABERTURA"                       TEXT,
                "URL FOTO 1 ABERTURA"                          TEXT,
                "URL FOTO 2 ABERTURA"                          TEXT,
                "URL FOTO 3 ABERTURA"                          TEXT,
                "CÓDIGO COLABORADOR ATENDIMENTO"               TEXT,
                "NOME RESPONSÁVEL ATENDIMENTO"                 TEXT,
                "OBSERVAÇÃO ATENDIMENTO"                       TEXT,
                "TEMPO ENTRE ABERTURA/ATENDIMENTO HH:MM:SS"    TEXT,
                "DATA/HORA ATENDIMENTO"                        TEXT,
                "LATITUDE ATENDIMENTO"                         TEXT,
                "LONGITUDE ATENDIMENTO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO ATENDIMENTO"              TEXT,
                "MARCA APARELHO ATENDIMENTO"                   TEXT,
                "MODELO APARELHO ATENDIMENTO"                  TEXT,
                "IMEI APARELHO ATENDIMENTO"                    TEXT,
                "CÓDIGO COLABORADOR INVALIDAÇÃO"               TEXT,
                "NOME RESPONSÁVEL INVALIDAÇÃO"                 TEXT,
                "MOTIVO INVALIDAÇÃO"                           TEXT,
                "TEMPO ENTRE ABERTURA/INVALIDAÇÃO HH:MM:SS"    TEXT,
                "TEMPO ENTRE ATENDIMENTO/INVALIDAÇÃO HH:MM:SS" TEXT,
                "DATA/HORA INVALIDAÇÃO"                        TEXT,
                "LATITUDE INVALIDAÇÃO"                         TEXT,
                "LONGITUDE INVALIDAÇÃO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO INVALIDAÇÃO"              TEXT,
                "MARCA APARELHO INVALIDAÇÃO"                   TEXT,
                "MODELO APARELHO INVALIDAÇÃO"                  TEXT,
                "IMEI APARELHO INVALIDAÇÃO"                    TEXT,
                "URL FOTO 1 INVALIDAÇÃO"                       TEXT,
                "URL FOTO 2 INVALIDAÇÃO"                       TEXT,
                "URL FOTO 3 INVALIDAÇÃO"                       TEXT,
                "CÓDIGO COLABORADOR FINALIZAÇÃO"               TEXT,
                "NOME RESPONSÁVEL FINALIZAÇÃO"                 TEXT,
                "OBSERVAÇÃO FINALIZAÇÃO"                       TEXT,
                "TEMPO ENTRE ATENDIMENTO/FINALIZAÇÃO HH:MM:SS" TEXT,
                "DATA/HORA FINALIZAÇÃO"                        TEXT,
                "LATITUDE FINALIZAÇÃO"                         TEXT,
                "LONGITUDE FINALIZAÇÃO"                        TEXT,
                "ENDEREÇO AUTOMÁTICO FINALIZAÇÃO"              TEXT,
                "MARCA APARELHO FINALIZAÇÃO"                   TEXT,
                "MODELO APARELHO FINALIZAÇÃO"                  TEXT,
                "IMEI APARELHO FINALIZAÇÃO"                    TEXT,
                "URL FOTO 1 FINALIZAÇÃO"                       TEXT,
                "URL FOTO 2 FINALIZAÇÃO"                       TEXT,
                "URL FOTO 3 FINALIZAÇÃO"                       TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_ARRAY_CONTEM_STATUS_ABERTO         BOOLEAN := CASE
                                                        WHEN ('ABERTO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO BOOLEAN := CASE
                                                        WHEN ('EM_ATENDIMENTO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_FINALIZADO     BOOLEAN := CASE
                                                        WHEN ('FINALIZADO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
    F_ARRAY_CONTEM_STATUS_INVALIDO       BOOLEAN := CASE
                                                        WHEN ('INVALIDO' = ANY (F_STATUS_SOCORRO_ROTA))
                                                            THEN TRUE
                                                        ELSE FALSE END;
BEGIN
    RETURN QUERY
        SELECT COALESCE(SR.CODIGO ::TEXT, '-')                                                AS COD_SOCORRO_ROTA,
               COALESCE(SR.STATUS_ATUAL :: TEXT, '-')                                         AS STATUS_SOCORRO_ROTA,
               COALESCE(V.PLACA :: TEXT, '-')                                                 AS PLACA_VEICULO_ABERTURA,
               COALESCE(SRAB.COD_COLABORADOR_ABERTURA :: TEXT, '-')                           AS COD_COLABORADOR_ABERTURA,
               COALESCE(CDAB.NOME :: TEXT, '-')                                               AS NOME_RESPONSAVEL_ABERTURA,
               COALESCE(SRAB.KM_VEICULO_ABERTURA ::TEXT, '-')                                 AS KM_VEICULO_COLETADO_ABERTURA,
               COALESCE(SROP.DESCRICAO :: TEXT, '-')                                          AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               COALESCE(SRAB.DESCRICAO_PROBLEMA :: TEXT, '-')                                 AS DESCRICAO_FORNECIDA_ABERTURA,
               COALESCE(SRAB.PONTO_REFERENCIA :: TEXT, '-')                                   AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               COALESCE((SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                                                  AS DATA_HORA_ABERTURA,
               COALESCE(SRAB.LATITUDE_ABERTURA :: TEXT, '-')                                  AS LATITUDE_ABERTURA,
               COALESCE(SRAB.LONGITUDE_ABERTURA :: TEXT, '-')                                 AS LONGITUDE_ABERTURA,
               COALESCE(SRAB.ENDERECO_AUTOMATICO :: TEXT, '-')                                AS ENDERECO_AUTOMATICO_ABERTURA,
               COALESCE(SRAB.MARCA_DEVICE_ABERTURA :: TEXT, '-')                              AS MARCA_APARELHO_ABERTURA,
               COALESCE(SRAB.MODELO_DEVICE_ABERTURA :: TEXT, '-')                             AS MODELO_APARELHO_ABERTURA,
               COALESCE(SRAB.DEVICE_IMEI_ABERTURA :: TEXT, '-')                               AS IMEI_APARELHO_ABERTURA,
               COALESCE(SRAB.URL_FOTO_1_ABERTURA :: TEXT, '-')                                AS URL_FOTO_1_ABERTURA,
               COALESCE(SRAB.URL_FOTO_2_ABERTURA :: TEXT, '-')                                AS URL_FOTO_2_ABERTURA,
               COALESCE(SRAB.URL_FOTO_3_ABERTURA :: TEXT, '-')                                AS URL_FOTO_3_ABERTURA,
               COALESCE(SRAT.COD_COLABORADOR_ATENDIMENTO :: TEXT, '-')                        AS COD_COLABORADOR_ATENDIMENTO,
               COALESCE(CDAT.NOME :: TEXT, '-')                                               AS NOME_RESPONSAVEL_ATENDIMENTO,
               COALESCE(SRAT.OBSERVACAO_ATENDIMENTO :: TEXT, '-')                             AS OBSERVACAO_ATENDIMENTO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRAT.DATA_HORA_ATENDIMENTO - SRAB.DATA_HORA_ABERTURA),
                        '-')                                                                  AS TEMPO_ABERTURA_ATENDIMENTO,
               COALESCE((SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                                                  AS DATA_HORA_ATENDIMENTO,
               COALESCE(SRAT.LATITUDE_ATENDIMENTO :: TEXT, '-')                               AS LATITUDE_ATENDIMENTO,
               COALESCE(SRAT.LONGITUDE_ATENDIMENTO :: TEXT, '-')                              AS LONGITUDE_ATENDIMENTO,
               COALESCE(SRAT.ENDERECO_AUTOMATICO :: TEXT, '-')                                AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               COALESCE(SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT, '-')                           AS MARCA_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT, '-')                          AS MODELO_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT, '-')                            AS IMEI_APARELHO_ATENDIMENTO,
               COALESCE(SRI.COD_COLABORADOR_INVALIDACAO :: TEXT, '-')                         AS COD_COLABORADOR_INVALIDACAO,
               COALESCE(CDI.NOME :: TEXT, '-')                                                AS NOME_RESPONSAVEL_INVALIDACAO,
               COALESCE(SRI.MOTIVO_INVALIDACAO :: TEXT, '-')                                  AS MOTIVO_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAB.DATA_HORA_ABERTURA)
                            ELSE '-'
                            END,
                        '-')                                                                  AS TEMPO_ABERTURA_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NOT NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAT.DATA_HORA_ATENDIMENTO)
                            ELSE '-'
                            END,
                        '-')                                                                  AS TEMPO_ATENDIMENTO_INVALIDACAO,
               COALESCE((SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                                                  AS DATA_HORA_INVALIDACAO,
               COALESCE(SRI.LATITUDE_INVALIDACAO :: TEXT, '-')                                AS LATITUDE_INVALIDACAO,
               COALESCE(SRI.LONGITUDE_INVALIDACAO :: TEXT, '-')                               AS LONGITUDE_INVALIDACAO,
               COALESCE(SRI.ENDERECO_AUTOMATICO :: TEXT, '-')                                 AS ENDERECO_AUTOMATICO_INVALIDACAO,
               COALESCE(SRI.MARCA_DEVICE_INVALIDACAO :: TEXT, '-')                            AS MARCA_APARELHO_INVALIDACAO,
               COALESCE(SRI.MODELO_DEVICE_INVALIDACAO :: TEXT, '-')                           AS MODELO_APARELHO_INVALIDACAO,
               COALESCE(SRI.DEVICE_IMEI_INVALIDACAO :: TEXT, '-')                             AS IMEI_APARELHO_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_1_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_1_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_2_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_2_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_3_INVALIDACAO :: TEXT, '-')                              AS URL_FOTO_3_INVALIDACAO,
               COALESCE(SRF.COD_COLABORADOR_FINALIZACAO :: TEXT, '-')                         AS COD_COLABORADOR_FINALIZACAO,
               COALESCE(CDF.NOME :: TEXT, '-')                                                AS NOME_RESPONSAVEL_FINALIZACAO,
               COALESCE(SRF.OBSERVACAO_FINALIZACAO :: TEXT, '-')                              AS OBSERVACAO_FINALIZACAO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRF.DATA_HORA_FINALIZACAO - SRAT.DATA_HORA_ATENDIMENTO),
                        '-')                                                                  AS TEMPO_ATENDIMENTO_FINALIZACAO,
               COALESCE((SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                                                  AS DATA_HORA_FINALIZACAO,
               COALESCE(SRF.LATITUDE_FINALIZACAO :: TEXT, '-')                                AS LATITUDE_FINALIZACAO,
               COALESCE(SRF.LONGITUDE_FINALIZACAO :: TEXT, '-')                               AS LONGITUDE_FINALIZACAO,
               COALESCE(SRF.ENDERECO_AUTOMATICO :: TEXT, '-')                                 AS ENDERECO_AUTOMATICO_FINALIZACAO,
               COALESCE(SRF.MARCA_DEVICE_FINALIZACAO :: TEXT, '-')                            AS MARCA_APARELHO_FINALIZACAO,
               COALESCE(SRF.MODELO_DEVICE_FINALIZACAO :: TEXT, '-')                           AS MODELO_APARELHO_FINALIZACAO,
               COALESCE(SRF.DEVICE_IMEI_FINALIZACAO :: TEXT, '-')                             AS IMEI_APARELHO_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_1_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_1_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_2_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_2_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_3_FINALIZACAO :: TEXT, '-')                              AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.
                                                        CODIGO = SRAB.COD_SOCORRO_ROTA
                 JOIN VEICULO_DATA V ON V.
                                            CODIGO = SRAB.COD_VEICULO_PROBLEMA
                 JOIN COLABORADOR_DATA CDAB ON CDAB.
                                                   CODIGO = SRAB.COD_COLABORADOR_ABERTURA
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.
                                                              CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::
                                  TEXT = ANY
                              (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO'])
                               AND
                              SR.
                                  CODIGO = SRAT.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDAT
                           ON SR.STATUS_ATUAL::
                                  TEXT = ANY
                              (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO'])
                               AND
                              CDAT.
                                  CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
                 LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
                           ON SR.
                                  STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDI
                           ON SR.
                                  STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
                 LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
                           ON SR.
                                  STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDF
                           ON SR.
                                  STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND F_IF(F_ARRAY_CONTEM_STATUS_ABERTO,
                     (SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                     FALSE)
           OR F_IF(F_ARRAY_CONTEM_STATUS_EM_ATENDIMENTO,
                   (SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                   FALSE)
           OR F_IF(F_ARRAY_CONTEM_STATUS_FINALIZADO,
                   (SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                   FALSE)
           OR F_IF(F_ARRAY_CONTEM_STATUS_INVALIDO,
                   (SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)):: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL,
                   FALSE);
END ;
$$;
--######################################################################################################################
--######################################################################################################################
-- Altera as coluna de precisão para remover a delimitação decimal
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ALTER COLUMN PRECISAO_LOCALIZACAO_ABERTURA_METROS TYPE NUMERIC USING PRECISAO_LOCALIZACAO_ABERTURA_METROS::NUMERIC;

ALTER TABLE SOCORRO_ROTA_ATENDIMENTO
    ALTER COLUMN PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS TYPE NUMERIC USING PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS::NUMERIC;

ALTER TABLE SOCORRO_ROTA_INVALIDACAO
    ALTER COLUMN PRECISAO_LOCALIZACAO_INVALIDACAO_METROS TYPE NUMERIC USING PRECISAO_LOCALIZACAO_INVALIDACAO_METROS::NUMERIC;

ALTER TABLE SOCORRO_ROTA_FINALIZACAO
    ALTER COLUMN PRECISAO_LOCALIZACAO_FINALIZACAO_METROS TYPE NUMERIC USING PRECISAO_LOCALIZACAO_FINALIZACAO_METROS::NUMERIC;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2541
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ATENDIMENTO_GET_COLABORADORES_NOTIFICACAO(F_COD_SOCORRO_ROTA BIGINT)
    RETURNS TABLE
            (
                COD_COLABORADOR     BIGINT,
                TOKEN_PUSH_FIREBASE TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Só iremos notificar se o colaborador tiver alguma permissão que permita à ele visualizar o socorro através da
    -- notificação. As permissões do array são: solicitar, tratar e visualizar socorros.
    PERMISSAO_PERMISSOES_NECESSARIAS CONSTANT BIGINT[] := ARRAY[145, 146, 147];
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                AS COD_COLABORADOR,
               PCT.TOKEN_PUSH_FIREBASE AS TOKEN_PUSH_FIREBASE
        FROM COLABORADOR C
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON C.CODIGO = SRA.COD_COLABORADOR_ABERTURA
                 JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE
            AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                 JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND
                                  F.CODIGO = CFP.COD_FUNCAO_COLABORADOR AND C.COD_EMPRESA = F.COD_EMPRESA
                 JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT ON C.CODIGO = PCT.COD_COLABORADOR
        WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA
          AND CFP.COD_FUNCAO_PROLOG = ANY (PERMISSAO_PERMISSOES_NECESSARIAS)
          -- Filtra apenas por aplicativos do Prolog.
          AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD')
          AND C.STATUS_ATIVO = TRUE;
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;