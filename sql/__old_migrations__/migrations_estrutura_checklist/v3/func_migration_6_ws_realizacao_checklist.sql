create or replace function migration_checklist.func_migration_6_ws_realizacao_checklist()
    returns void
    language plpgsql
as
$func$
begin

    --##################################################################################################################
    -- PL-2299
    -- Adiciona nova coluna na tabela de checklists para salvar diretamente a data de realização com o tz da unidade
    -- já aplicado.
    ALTER TABLE CHECKLIST_DATA ADD COLUMN DATA_HORA_REALIZACAO_TZ_APLICADO TIMESTAMP;

    COMMENT ON COLUMN CHECKLIST_DATA.DATA_HORA_REALIZACAO_TZ_APLICADO IS 'A data/hora de realização do checklist já com o TZ da unidade onde foi realizado aplicado.';

    UPDATE checklist_data
    SET DATA_HORA_REALIZACAO_TZ_APLICADO = CHECKLIST_DATA.DATA_HORA AT TIME ZONE TZ_UNIDADE(COD_UNIDADE);

    ALTER TABLE CHECKLIST_DATA ALTER COLUMN DATA_HORA_REALIZACAO_TZ_APLICADO SET NOT NULL;

    CREATE INDEX IDX_CHECKLIST_DATA_HORA_REALIZACAO_TZ ON CHECKLIST_DATA ((DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE));

    DROP VIEW CHECKLIST;

    create view checklist as
    select c.cod_unidade,
           c.cod_checklist_modelo,
           c.cod_versao_checklist_modelo,
           c.codigo,
           c.data_hora,
           c.data_hora_realizacao_tz_aplicado,
           c.data_hora_importado_prolog,
           c.cpf_colaborador,
           c.placa_veiculo,
           c.tipo,
           c.tempo_realizacao,
           c.km_veiculo,
           c.data_hora_sincronizacao,
           c.fonte_data_hora_realizacao,
           c.versao_app_momento_realizacao,
           c.versao_app_momento_sincronizacao,
           c.device_id,
           c.device_imei,
           c.device_uptime_realizacao_millis,
           c.device_uptime_sincronizacao_millis,
           c.foi_offline,
           c.total_perguntas_ok,
           c.total_perguntas_nok,
           c.total_alternativas_ok,
           c.total_alternativas_nok
    from checklist_data c
    where (c.deletado = false);
    --##################################################################################################################


    --######################################################################################################################
    --######################################################################################################################
    --################################ Refatora métodos SQLs usados na ChecklistDao e no Offline ###########################
    --######################################################################################################################
    --######################################################################################################################
    -- PL-2227
    -- '[
    --   {
    --     "codPergunta": 1,
    --     "codAlternativas": [
    --       1,
    --       2,
    --       3,
    --       4
    --     ]
    --   },
    --   {
    --     "codPergunta": 2,
    --     "codAlternativas": [
    --       1,
    --       2,
    --       3,
    --       4
    --     ]
    --   }
    -- ]'
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_ENCONTRA_VERSAO_MODELO(F_COD_MODELO_CHECKLIST BIGINT,
                                                                     F_PERGUNTAS_ALTERNATIVAS JSONB)
        RETURNS BIGINT
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        COD_VERSAO_MODELO_CHECKLIST_PROCURADO BIGINT;
        F_ALTERNATIVA                         BIGINT;
    BEGIN
        -- CRIA UM JSONB DAS ALTERNATIVAS EXTRAÍDAS DO JSON DO MODELO.
        WITH CHECKLIST AS (
            SELECT JSONB_ARRAY_ELEMENTS_TEXT(F_PERGUNTAS_ALTERNATIVAS)::JSONB -> 'codAlternativas' AS COD_ALTERNATIVAS
        ),
             DADOS_MODELO_WS AS (
                 SELECT F_COD_MODELO_CHECKLIST                           AS COD_MODELO_CHECKLIST,
                        JSONB_ARRAY_ELEMENTS(CHECKLIST.COD_ALTERNATIVAS) AS COD_ALTERNATIVA
                 FROM CHECKLIST
                 ORDER BY 2
             )
        SELECT COD_ALTERNATIVA
        FROM DADOS_MODELO_WS WS
        INTO F_ALTERNATIVA;

        -- IDENTIFICA A VERSÃO DO MODELO ATRAVÉS DO CÓDIGO DE ALTERNATIVA E CÓDIGO DO MODELO.
        SELECT COD_VERSAO_CHECKLIST_MODELO
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
        WHERE CODIGO = F_ALTERNATIVA
          AND COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
        INTO COD_VERSAO_MODELO_CHECKLIST_PROCURADO;

        IF COD_VERSAO_MODELO_CHECKLIST_PROCURADO IS NOT NULL AND COD_VERSAO_MODELO_CHECKLIST_PROCURADO > 0
        THEN
            -- ENCONTRAMOS A VERSÃO DO MODELO NA BUSCA.
            RETURN COD_VERSAO_MODELO_CHECKLIST_PROCURADO;
        ELSE
            RAISE EXCEPTION 'Não foi possível encontrar a versão do modelo de checklist';
        END IF;
    END;
    $$;

    -- Para testar a func acima:
    -- with dados_json as (
    --     select '[
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1120,
    --         "codAlternativas": [
    --           319,
    --           321,
    --           320
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1121,
    --         "codAlternativas": [
    --           322,
    --           361,
    --           327,
    --           393
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1122,
    --         "codAlternativas": [
    --           373,
    --           397,
    --           398,
    --           374,
    --           372
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1124,
    --         "codAlternativas": [
    --           350,
    --           70,
    --           349,
    --           348
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1126,
    --         "codAlternativas": [
    --           356,
    --           72,
    --           71,
    --           385,
    --           351
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1128,
    --         "codAlternativas": [
    --           289,
    --           290,
    --           291
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1130,
    --         "codAlternativas": [
    --           295,
    --           294,
    --           293
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1132,
    --         "codAlternativas": [
    --           300,
    --           301,
    --           299
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1135,
    --         "codAlternativas": [
    --           389,
    --           326,
    --           314,
    --           358,
    --           387,
    --           388
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1136,
    --         "codAlternativas": [
    --           325,
    --           305,
    --           362
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1138,
    --         "codAlternativas": [
    --           386,
    --           306,
    --           307,
    --           357
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1140,
    --         "codAlternativas": [
    --           310,
    --           308,
    --           309
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1141,
    --         "codAlternativas": [
    --           311,
    --           313,
    --           312
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1143,
    --         "codAlternativas": [
    --           395,
    --           323,
    --           324,
    --           1
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1144,
    --         "codAlternativas": [
    --           333,
    --           334,
    --           9,
    --           10,
    --           332
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1146,
    --         "codAlternativas": [
    --           377,
    --           375,
    --           376,
    --           378
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1148,
    --         "codAlternativas": [
    --           315,
    --           316,
    --           390,
    --           359
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1150,
    --         "codAlternativas": [
    --           340,
    --           339,
    --           341
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1152,
    --         "codAlternativas": [
    --           15827,
    --           344,
    --           343,
    --           15846,
    --           342,
    --           22091
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1154,
    --         "codAlternativas": [
    --           391,
    --           392,
    --           317,
    --           318
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1156,
    --         "codAlternativas": [
    --           285,
    --           281,
    --           282
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1157,
    --         "codAlternativas": [
    --           286,
    --           383,
    --           382,
    --           353
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1158,
    --         "codAlternativas": [
    --           287,
    --           288,
    --           352
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1159,
    --         "codAlternativas": [
    --           384,
    --           355,
    --           354,
    --           292
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1160,
    --         "codAlternativas": [
    --           345,
    --           364,
    --           365,
    --           366
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1161,
    --         "codAlternativas": [
    --           363,
    --           296,
    --           297,
    --           298,
    --           394
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1162,
    --         "codAlternativas": [
    --           367,
    --           368
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1163,
    --         "codAlternativas": [
    --           379,
    --           380,
    --           381
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1164,
    --         "codAlternativas": [
    --           278,
    --           277,
    --           276,
    --           275,
    --           274
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1165,
    --         "codAlternativas": [
    --           328,
    --           331,
    --           329,
    --           330
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1166,
    --         "codAlternativas": [
    --           13330,
    --           371,
    --           369,
    --           370
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1167,
    --         "codAlternativas": [
    --           399,
    --           400
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1169,
    --         "codAlternativas": [
    --           302,
    --           303,
    --           304
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1178,
    --         "codAlternativas": [
    --           335,
    --           346,
    --           68,
    --           69,
    --           347
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 1863,
    --         "codAlternativas": [
    --           337,
    --           338,
    --           396,
    --           360,
    --           336
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 11068,
    --         "codAlternativas": [
    --           27838,
    --           27839,
    --           27840
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 13232,
    --         "codAlternativas": [
    --           34776,
    --           34775,
    --           34774
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 13233,
    --         "codAlternativas": [
    --           34779,
    --           34778,
    --           34777
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 13234,
    --         "codAlternativas": [
    --           34782,
    --           34781,
    --           34780
    --         ]
    --       },
    --       {
    --         "codModeloChecklist": 1,
    --         "codPergunta": 13235,
    --         "codAlternativas": [
    --           34783
    --         ]
    --       }
    --     ]' :: jsonb as data
    -- )
    --     select * from func_checklist_encontra_versao_modelo(1, (select dados_json.data from dados_json));


    -- Altera function de insert de um checklist offline.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(F_COD_UNIDADE_CHECKLIST BIGINT,
                                                                     F_COD_MODELO_CHECKLIST BIGINT,
                                                                     F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                     F_DATA_HORA_REALIZACAO TIMESTAMP WITH TIME ZONE,
                                                                     F_COD_COLABORADOR BIGINT,
                                                                     F_COD_VEICULO BIGINT,
                                                                     F_PLACA_VEICULO TEXT,
                                                                     F_TIPO_CHECKLIST CHAR,
                                                                     F_KM_COLETADO BIGINT,
                                                                     F_TEMPO_REALIZACAO BIGINT,
                                                                     F_DATA_HORA_SINCRONIZACAO TIMESTAMP WITH TIME ZONE,
                                                                     F_FONTE_DATA_HORA_REALIZACAO TEXT,
                                                                     F_VERSAO_APP_MOMENTO_REALIZACAO INTEGER,
                                                                     F_VERSAO_APP_MOMENTO_SINCRONIZACAO INTEGER,
                                                                     F_DEVICE_ID TEXT,
                                                                     F_DEVICE_IMEI TEXT,
                                                                     F_DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT,
                                                                     F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                                                                     F_FOI_OFFLINE BOOLEAN,
                                                                     F_TOTAL_PERGUNTAS_OK INTEGER,
                                                                     F_TOTAL_PERGUNTAS_NOK INTEGER,
                                                                     F_TOTAL_ALTERNATIVAS_OK INTEGER,
                                                                     F_TOTAL_ALTERNATIVAS_NOK INTEGER)
        RETURNS BIGINT
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
        DEVE_ATUALIZAR_KM_VEICULO BOOLEAN := (CASE
                                                  WHEN (F_KM_COLETADO > (SELECT V.KM
                                                                         FROM VEICULO V
                                                                         WHERE V.CODIGO = F_COD_VEICULO))
                                                      THEN
                                                      TRUE
                                                  ELSE FALSE END);
        COD_CHECKLIST_INSERIDO    BIGINT;
        QTD_LINHAS_ATUALIZADAS    BIGINT;
    BEGIN

        INSERT INTO CHECKLIST(COD_UNIDADE,
                              COD_CHECKLIST_MODELO,
                              COD_VERSAO_CHECKLIST_MODELO,
                              DATA_HORA,
                              DATA_HORA_REALIZACAO_TZ_APLICADO,
                              CPF_COLABORADOR,
                              PLACA_VEICULO,
                              TIPO,
                              TEMPO_REALIZACAO,
                              KM_VEICULO,
                              DATA_HORA_SINCRONIZACAO,
                              FONTE_DATA_HORA_REALIZACAO,
                              VERSAO_APP_MOMENTO_REALIZACAO,
                              VERSAO_APP_MOMENTO_SINCRONIZACAO,
                              DEVICE_ID,
                              DEVICE_IMEI,
                              DEVICE_UPTIME_REALIZACAO_MILLIS,
                              DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                              FOI_OFFLINE,
                              TOTAL_PERGUNTAS_OK,
                              TOTAL_PERGUNTAS_NOK,
                              TOTAL_ALTERNATIVAS_OK,
                              TOTAL_ALTERNATIVAS_NOK)
        VALUES (F_COD_UNIDADE_CHECKLIST,
                F_COD_MODELO_CHECKLIST,
                F_COD_VERSAO_MODELO_CHECKLIST,
                F_DATA_HORA_REALIZACAO,
                (F_DATA_HORA_REALIZACAO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE_CHECKLIST)),
                (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
                F_PLACA_VEICULO,
                F_TIPO_CHECKLIST,
                F_TEMPO_REALIZACAO,
                F_KM_COLETADO,
                F_DATA_HORA_SINCRONIZACAO,
                F_FONTE_DATA_HORA_REALIZACAO,
                F_VERSAO_APP_MOMENTO_REALIZACAO,
                F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
                F_DEVICE_ID,
                F_DEVICE_IMEI,
                F_DEVICE_UPTIME_REALIZACAO_MILLIS,
                F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                F_FOI_OFFLINE,
                F_TOTAL_PERGUNTAS_OK,
                F_TOTAL_PERGUNTAS_NOK,
                F_TOTAL_ALTERNATIVAS_OK,
                F_TOTAL_ALTERNATIVAS_NOK) RETURNING CODIGO INTO COD_CHECKLIST_INSERIDO;

        -- Verificamos se o insert funcionou.
        IF COD_CHECKLIST_INSERIDO <= 0
        THEN
            RAISE EXCEPTION 'Não foi possível inserir o checklist';
        END IF;

        IF DEVE_ATUALIZAR_KM_VEICULO
        THEN
            UPDATE VEICULO SET KM = F_KM_COLETADO WHERE CODIGO = F_COD_VEICULO;
        END IF;

        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
        IF (DEVE_ATUALIZAR_KM_VEICULO AND QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Não foi possível atualizar o km do veículo';
        END IF;

        RETURN COD_CHECKLIST_INSERIDO;
    END;
    $$;


    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_RESPOSTAS_CHECKLIST(F_COD_UNIDADE_CHECKLIST BIGINT,
                                                                         F_COD_MODELO_CHECKLIST BIGINT,
                                                                         F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                         F_COD_CHECKLIST BIGINT,
                                                                         F_COD_PERGUNTA BIGINT,
                                                                         F_COD_ALTERNATIVA BIGINT,
                                                                         F_RESPOSTA_OUTROS TEXT)
        RETURNS BIGINT
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        QTD_LINHAS_INSERIDAS BIGINT;
    BEGIN
        INSERT INTO CHECKLIST_RESPOSTAS_NOK(COD_UNIDADE,
                                            COD_CHECKLIST_MODELO,
                                            COD_VERSAO_CHECKLIST_MODELO,
                                            COD_CHECKLIST,
                                            COD_PERGUNTA,
                                            COD_ALTERNATIVA,
                                            RESPOSTA_OUTROS)
        VALUES (F_COD_UNIDADE_CHECKLIST,
                F_COD_MODELO_CHECKLIST,
                F_COD_VERSAO_MODELO_CHECKLIST,
                F_COD_CHECKLIST,
                F_COD_PERGUNTA,
                F_COD_ALTERNATIVA,
                F_RESPOSTA_OUTROS);

        GET DIAGNOSTICS QTD_LINHAS_INSERIDAS = ROW_COUNT;

        IF QTD_LINHAS_INSERIDAS <> 1
        THEN
            RAISE EXCEPTION 'Não foi possível inserir a(s) resposta(s)';
        END IF;

        RETURN QTD_LINHAS_INSERIDAS;
    END;
    $$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2228
-- Function para buscar os modelos de checklist disponíveis para realização.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_MODELOS_SELECAO_REALIZACAO(F_COD_UNIDADE BIGINT,
                                                                             F_COD_CARGO BIGINT)
        RETURNS TABLE
                (
                    COD_MODELO              BIGINT,
                    COD_VERSAO_ATUAL_MODELO BIGINT,
                    COD_UNIDADE_MODELO      BIGINT,
                    NOME_MODELO             TEXT,
                    COD_VEICULO             BIGINT,
                    PLACA_VEICULO           TEXT,
                    KM_ATUAL_VEICULO        BIGINT
                )
        LANGUAGE SQL
    AS
    $$
    SELECT CM.CODIGO           AS COD_MODELO,
           CM.COD_VERSAO_ATUAL AS COD_VERSAO_ATUAL_MODELO,
           CM.COD_UNIDADE      AS COD_UNIDADE_MODELO,
           CM.NOME :: TEXT     AS NOME_MODELO,
           V.CODIGO            AS COD_VEICULO,
           V.PLACA :: TEXT     AS PLACA_VEICULO,
           V.KM                AS KM_ATUAL_VEICULO
    FROM CHECKLIST_MODELO CM
             JOIN CHECKLIST_MODELO_FUNCAO CMF
                  ON CMF.COD_CHECKLIST_MODELO = CM.CODIGO AND CM.COD_UNIDADE = CMF.COD_UNIDADE
             JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT
                  ON CMVT.COD_MODELO = CM.CODIGO AND CMVT.COD_UNIDADE = CM.COD_UNIDADE
             JOIN VEICULO_TIPO VT
                  ON VT.CODIGO = CMVT.COD_TIPO_VEICULO
             JOIN VEICULO V
                  ON V.COD_TIPO = VT.CODIGO AND V.COD_UNIDADE = CM.COD_UNIDADE
    WHERE CM.COD_UNIDADE = F_COD_UNIDADE
      AND CMF.COD_FUNCAO = F_COD_CARGO
      AND CM.STATUS_ATIVO = TRUE
      AND V.STATUS_ATIVO = TRUE
    ORDER BY CM.CODIGO, V.PLACA
    $$;

    -- PL-2228
-- Function para buscar os modelos de checklist disponíveis para realização.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_MODELO_REALIZACAO(F_COD_MODELO_CHECKLIST BIGINT,
                                                                    F_COD_VEICULO_REALIZACAO BIGINT)
        RETURNS TABLE
                (
                    COD_UNIDADE_MODELO_CHECKLIST BIGINT,
                    COD_MODELO_CHECKLIST         BIGINT,
                    COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                    NOME_MODELO_CHECKLIST        TEXT,
                    COD_PERGUNTA                 BIGINT,
                    DESCRICAO_PERGUNTA           TEXT,
                    COD_IMAGEM                   BIGINT,
                    URL_IMAGEM                   TEXT,
                    PERGUNTA_ORDEM_EXIBICAO      INTEGER,
                    SINGLE_CHOICE                BOOLEAN,
                    COD_ALTERNATIVA              BIGINT,
                    DESCRICAO_ALTERNATIVA        TEXT,
                    TIPO_OUTROS                  BOOLEAN,
                    ALTERNATIVA_ORDEM_EXIBICAO   INTEGER,
                    PRIORIDADE_ALTERNATIVA       TEXT,
                    KM_ATUAL_VEICULO_REALIZACAO  BIGINT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        KM_ATUAL_VEICULO CONSTANT BIGINT := (SELECT V.KM FROM VEICULO V WHERE V.CODIGO = F_COD_VEICULO_REALIZACAO);
    BEGIN
        IF KM_ATUAL_VEICULO IS NULL
        THEN
            RAISE EXCEPTION 'Erro ao buscar KM atual do veículo para realização do checklist!';
        END IF;

        RETURN QUERY
            SELECT CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
                   CM.CODIGO                   AS COD_MODELO_CHECKLIST,
                   CM.COD_VERSAO_ATUAL         AS COD_VERSAO_MODELO_CHECKLIST,
                   CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
                   CP.CODIGO                   AS COD_PERGUNTA,
                   CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
                   CP.COD_IMAGEM               AS COD_IMAGEM,
                   CGI.URL_IMAGEM              AS URL_IMAGEM,
                   CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
                   CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
                   CAP.CODIGO                  AS COD_ALTERNATIVA,
                   CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
                   CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
                   CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
                   CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
                   KM_ATUAL_VEICULO            AS KM_ATUAL_VEICULO_REALIZACAO
            FROM CHECKLIST_MODELO CM
                     JOIN CHECKLIST_PERGUNTAS CP
                          ON CM.CODIGO = CP.COD_CHECKLIST_MODELO AND
                             CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CP.CODIGO = CAP.COD_PERGUNTA AND CM.COD_VERSAO_ATUAL = CAP.COD_VERSAO_CHECKLIST_MODELO
                -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
                     LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                               ON CP.COD_IMAGEM = CGI.COD_IMAGEM
            WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST
              AND CM.STATUS_ATIVO
            ORDER BY COD_MODELO_CHECKLIST,
                     PERGUNTA_ORDEM_EXIBICAO,
                     COD_PERGUNTA,
                     ALTERNATIVA_ORDEM_EXIBICAO,
                     COD_ALTERNATIVA;
    END ;
    $$;

    -- Como a busca dos modelos mudou de DAO, a Avilan precisa ter o recurso de modelos integrado agora.
    INSERT INTO INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA (COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO)
    VALUES (2, 'AVACORP_AVILAN', 'CHECKLIST_MODELO');
    --######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
    -- Function foi refatorada para a nova estrutura. Usando CHECKLIST_RESPOSTAS_NOK. Agora também retorna a versão do
    -- modelo.
    DROP FUNCTION FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST BIGINT);
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST BIGINT)
        RETURNS TABLE
                (
                    COD_CHECKLIST                 BIGINT,
                    COD_CHECKLIST_MODELO          BIGINT,
                    COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                    DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                    DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                    KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                    DURACAO_REALIZACAO_MILLIS     BIGINT,
                    CPF_COLABORADOR               BIGINT,
                    PLACA_VEICULO                 TEXT,
                    TIPO_CHECKLIST                CHAR,
                    NOME_COLABORADOR              TEXT,
                    COD_PERGUNTA                  BIGINT,
                    ORDEM_PERGUNTA                INTEGER,
                    DESCRICAO_PERGUNTA            TEXT,
                    PERGUNTA_SINGLE_CHOICE        BOOLEAN,
                    COD_ALTERNATIVA               BIGINT,
                    PRIORIDADE_ALTERNATIVA        TEXT,
                    ORDEM_ALTERNATIVA             INTEGER,
                    DESCRICAO_ALTERNATIVA         TEXT,
                    ALTERNATIVA_TIPO_OUTROS       BOOLEAN,
                    COD_IMAGEM                    BIGINT,
                    URL_IMAGEM                    TEXT,
                    ALTERNATIVA_SELECIONADA       BOOLEAN,
                    RESPOSTA_OUTROS               TEXT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    BEGIN
        RETURN QUERY
            SELECT C.CODIGO                                                            AS COD_CHECKLIST,
                   C.COD_CHECKLIST_MODELO                                              AS COD_CHECKLIST_MODELO,
                   C.COD_VERSAO_CHECKLIST_MODELO                                       AS COD_CHECKLIST_MODELO,
                   C.DATA_HORA_REALIZACAO_TZ_APLICADO                                  AS DATA_HORA_REALIZACAO,
                   C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_IMPORTADO_PROLOG,
                   C.KM_VEICULO                                                        AS KM_VEICULO_MOMENTO_REALIZACAO,
                   C.TEMPO_REALIZACAO                                                  AS DURACAO_REALIZACAO_MILLIS,
                   C.CPF_COLABORADOR                                                   AS CPF_COLABORADOR,
                   C.PLACA_VEICULO :: TEXT                                             AS PLACA_VEICULO,
                   C.TIPO                                                              AS TIPO_CHECKLIST,
                   CO.NOME :: TEXT                                                     AS NOME_COLABORADOR,
                   CP.CODIGO                                                           AS COD_PERGUNTA,
                   CP.ORDEM                                                            AS ORDEM_PERGUNTA,
                   CP.PERGUNTA                                                         AS DESCRICAO_PERGUNTA,
                   CP.SINGLE_CHOICE                                                    AS PERGUNTA_SINGLE_CHOICE,
                   CAP.CODIGO                                                          AS COD_ALTERNATIVA,
                   CAP.PRIORIDADE :: TEXT                                              AS PRIORIDADE_ALTERNATIVA,
                   CAP.ORDEM                                                           AS ORDEM_ALTERNATIVA,
                   CAP.ALTERNATIVA                                                     AS DESCRICAO_ALTERNATIVA,
                   CAP.ALTERNATIVA_TIPO_OUTROS                                         AS ALTERNATIVA_TIPO_OUTROS,
                   CGI.COD_IMAGEM                                                      AS COD_IMAGEM,
                   CGI.URL_IMAGEM                                                      AS URL_IMAGEM,
                   CRN.CODIGO IS NOT NULL                                              AS ALTERNATIVA_SELECIONADA,
                   CRN.RESPOSTA_OUTROS                                                 AS RESPOSTA_OUTROS
            FROM CHECKLIST C
                     JOIN COLABORADOR CO
                          ON CO.CPF = C.CPF_COLABORADOR
                     JOIN CHECKLIST_PERGUNTAS CP
                          ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CAP.COD_PERGUNTA = CP.CODIGO
                     LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                               ON C.CODIGO = CRN.COD_CHECKLIST
                                   AND CAP.CODIGO = CRN.COD_ALTERNATIVA
                     LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                               ON CP.COD_IMAGEM = CGI.COD_IMAGEM
            WHERE C.CODIGO = F_COD_CHECKLIST
            ORDER BY CP.CODIGO, CAP.CODIGO;
    END ;
    $$;
    --######################################################################################################################
    --######################################################################################################################
    -- PL-2346
    -- Dropa a FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS
    DROP FUNCTION FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(BIGINT, TEXT);

    -- Recria a FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS para considerar o código da versão do modelo de checklist.
    -- Agora ela também retorna se a alternativa é do tipo_outros. E, caso tenha item em aberto, retorna o texto
    -- tipo_outros que o usuário forneceu como resposta.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(F_COD_MODELO_CHECKLIST BIGINT,
                                                                          F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                          F_PLACA_VEICULO TEXT)
        RETURNS TABLE
                (
                    COD_ALTERNATIVA                    BIGINT,
                    COD_CONTEXTO_PERGUNTA              BIGINT,
                    COD_CONTEXTO_ALTERNATIVA           BIGINT,
                    COD_ITEM_ORDEM_SERVICO             BIGINT,
                    RESPOSTA_TIPO_OUTROS_ABERTURA_ITEM TEXT,
                    TEM_ITEM_OS_PENDENTE               BOOLEAN,
                    DEVE_ABRIR_ORDEM_SERVICO           BOOLEAN,
                    ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
                    QTD_APONTAMENTOS_ITEM              INTEGER,
                    PRIORIDADE_ALTERNATIVA             TEXT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        STATUS_ITEM_PENDENTE TEXT = 'P';
    BEGIN
        RETURN QUERY
            WITH ITENS_PENDENTES AS (
                SELECT COSI.CODIGO                               AS COD_ITEM_ORDEM_SERVICO,
                       COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO AS COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                       COSI.COD_CONTEXTO_ALTERNATIVA             AS COD_CONTEXTO_ALTERNATIVA,
                       COSI.QT_APONTAMENTOS                      AS QTD_APONTAMENTOS_ITEM,
                       COS.COD_CHECKLIST                         AS COD_CHECKLIST,
                       C.COD_CHECKLIST_MODELO                    AS COD_CHECKLIST_MODELO
                FROM CHECKLIST_DATA C
                         JOIN CHECKLIST_ORDEM_SERVICO_DATA COS
                              ON C.CODIGO = COS.COD_CHECKLIST
                         JOIN CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                              ON COS.CODIGO = COSI.COD_OS
                                  AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
                  AND C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
                  AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_PENDENTE
            )
            SELECT CAP.CODIGO                                          AS COD_ALTERNATIVA,
                   CP.CODIGO_CONTEXTO                                  AS COD_CONTEXTO_PERGUNTA,
                   CAP.CODIGO_CONTEXTO                                 AS COD_CONTEXTO_ALTERNATIVA,
                   IP.COD_ITEM_ORDEM_SERVICO                           AS COD_ITEM_ORDEM_SERVICO,
                   CRN.RESPOSTA_OUTROS                                 AS RESPOSTA_TIPO_OUTROS_ABERTURA_ITEM,
                   F_IF(IP.COD_ITEM_ORDEM_SERVICO ISNULL, FALSE, TRUE) AS TEM_ITEM_OS_PENDENTE,
                   CAP.DEVE_ABRIR_ORDEM_SERVICO                        AS DEVE_ABRIR_ORDEM_SERVICO,
                   CAP.ALTERNATIVA_TIPO_OUTROS                         AS ALTERNATIVA_TIPO_OUTROS,
                   IP.QTD_APONTAMENTOS_ITEM                            AS QTD_APONTAMENTOS_ITEM,
                   CAP.PRIORIDADE::TEXT                                AS PRIORIDADE_ALTERNATIVA
            FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
                     JOIN CHECKLIST_PERGUNTAS_DATA CP
                          ON CAP.COD_PERGUNTA = CP.CODIGO
                     LEFT JOIN ITENS_PENDENTES IP
                               ON IP.COD_CONTEXTO_ALTERNATIVA = CAP.CODIGO_CONTEXTO
                     LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                               ON CRN.COD_ALTERNATIVA = IP.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
                                   AND CRN.COD_CHECKLIST = IP.COD_CHECKLIST
            WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO_CHECKLIST;
    END ;
    $$;
--######################################################################################################################
--######################################################################################################################
END ;
$func$;