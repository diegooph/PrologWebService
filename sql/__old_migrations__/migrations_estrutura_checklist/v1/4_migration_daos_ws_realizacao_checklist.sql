begin transaction ;
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
create or replace function func_checklist_encontra_versao_modelo(f_cod_modelo_checklist bigint,
                                                                 f_perguntas_alternativas jsonb)
    returns bigint
    language plpgsql
as
$$
declare
    modelo_checklist_versao               checklist_modelo_versao%rowtype;
    total_linhas_apos_comparacao          bigint;
    cod_versao_modelo_checklist_procurado bigint;
begin

    create temp table if not exists table_modelo_ws
    (
        cod_checklist_modelo bigint not null,
        cod_pergunta         bigint not null,
        cod_alternativa      bigint not null
    );

    with cte as (
        select jsonb_array_elements(f_perguntas_alternativas)::jsonb -> 'codPergunta'          as cod_pergunta,
               jsonb_array_elements_text(f_perguntas_alternativas)::jsonb -> 'codAlternativas' as cod_alternativas
    ),

         dados_modelo_ws as (
             select f_cod_modelo_checklist                          as cod_modelo_checklist,
                    cte.cod_pergunta                                as cod_pergunta,
                    jsonb_array_elements_text(cte.cod_alternativas) as cod_alternativa
             from cte cte
             order by 2, 3
         )

        insert into
         table_modelo_ws select
         ws.cod_modelo_checklist :: bigint,
         ws.cod_pergunta :: bigint,
         ws.cod_alternativa :: bigint
        from
         dados_modelo_ws ws;

    for modelo_checklist_versao in
        select cmv.cod_checklist_modelo,
               cmv.cod_versao_checklist_modelo
        from checklist_modelo_versao cmv
        where cmv.cod_checklist_modelo = f_cod_modelo_checklist
        loop
            with dados_modelo_versao_bd as (
                select modelo_checklist_versao.cod_checklist_modelo as cod_checklist_modelo,
                       cp.codigo                                    as cod_pergunta,
                       cap.codigo                                   as cod_alternativa
                from checklist_perguntas_data cp
                         join checklist_alternativa_pergunta_data cap on cp.codigo = cap.cod_pergunta
                where cp.cod_checklist_modelo = modelo_checklist_versao.cod_checklist_modelo
                  and cp.cod_versao_checklist_modelo = modelo_checklist_versao.cod_versao_checklist_modelo
            ),
                 comparacao_ws_bd as (
                     (select ws.cod_checklist_modelo,
                             ws.cod_pergunta,
                             ws.cod_alternativa
                      from table_modelo_ws ws
                      order by 2, 3)
                     except
                     (select bd.cod_checklist_modelo,
                             bd.cod_pergunta,
                             bd.cod_alternativa
                      from dados_modelo_versao_bd bd
                      order by 2, 3)
                 )
                select
                 count(*) from
                 comparacao_ws_bd into
                 total_linhas_apos_comparacao;

            if total_linhas_apos_comparacao is not null and total_linhas_apos_comparacao = 0
                -- Encontramos a versão do modelo.
            then
                select modelo_checklist_versao.cod_versao_checklist_modelo into cod_versao_modelo_checklist_procurado;
                -- Saímos do loop.
                exit;
            end if;
        end loop;

    if cod_versao_modelo_checklist_procurado is not null and cod_versao_modelo_checklist_procurado > 0
        -- Encontramos a versão do modelo na busca.
    then
        return cod_versao_modelo_checklist_procurado;
    else
        return 1;
    end if;
end;
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
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(
  F_COD_UNIDADE_CHECKLIST              BIGINT,
  F_COD_MODELO_CHECKLIST               BIGINT,
  F_COD_VERSAO_MODELO_CHECKLIST        BIGINT,
  F_DATA_HORA_REALIZACAO               TIMESTAMP WITH TIME ZONE,
  F_COD_COLABORADOR                    BIGINT,
  F_COD_VEICULO                        BIGINT,
  F_PLACA_VEICULO                      TEXT,
  F_TIPO_CHECKLIST                     CHAR,
  F_KM_COLETADO                        BIGINT,
  F_TEMPO_REALIZACAO                   BIGINT,
  F_DATA_HORA_SINCRONIZACAO            TIMESTAMP WITH TIME ZONE,
  F_FONTE_DATA_HORA_REALIZACAO         TEXT,
  F_VERSAO_APP_MOMENTO_REALIZACAO      INTEGER,
  F_VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
  F_DEVICE_ID                          TEXT,
  F_DEVICE_IMEI                        TEXT,
  F_DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
  F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
  F_TOTAL_PERGUNTAS_OK                 INTEGER,
  F_TOTAL_PERGUNTAS_NOK                INTEGER,
  F_TOTAL_ALTERNATIVAS_OK              INTEGER,
  F_TOTAL_ALTERNATIVAS_NOK             INTEGER)
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
            TRUE,
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

-- Como a busca dos modelos mudou de DAO, a Avilan precisa ter o recurso de modelos integrado agora.
INSERT INTO PUBLIC.INTEGRACAO (COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO)
VALUES (2, 'AVACORP_AVILAN', 'CHECKLIST_MODELO');
--######################################################################################################################
--######################################################################################################################

end transaction ;