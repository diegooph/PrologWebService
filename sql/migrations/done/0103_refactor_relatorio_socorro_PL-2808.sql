-- Esta migration dropa a function antiga de relatório de dados gerais de socorro em rota e recria com mais uma coluna
-- referente ao tempo máximo de atendimento a um socorro, de acordo com o DPO, utilizando faixas fixas.
DROP FUNCTION FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(BIGINT[], DATE, DATE, CHARACTER VARYING[]);

CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                           F_DATA_INICIAL DATE,
                                                                           F_DATA_FINAL DATE,
                                                                           F_STATUS_SOCORRO_ROTA VARCHAR[])
    RETURNS TABLE
            (
                "UNIDADE"                                                     TEXT,
                "DISTÂNCIA ENTRE UNIDADE E ABERTURA"                          TEXT,
                "TEMPO MÁXIMO DE SOCORRO (DPO)"                               TEXT,
                "CÓDIGO SOCORRO ROTA"                                         TEXT,
                "STATUS SOCORRO ROTA"                                         TEXT,
                "PLACA VEÍCULO ABERTURA"                                      TEXT,
                "CÓDIGO COLABORADOR ABERTURA"                                 TEXT,
                "NOME RESPONSÁVEL ABERTURA"                                   TEXT,
                "KM VEÍCULO COLETADO ABERTURA"                                TEXT,
                "DESCRIÇÃO OPÇÃO PROBLEMA ABERTURA"                           TEXT,
                "DESCRIÇÃO FORNECIDA ABERTURA"                                TEXT,
                "PONTO REFERÊNCIA FORNECIDO ABERTURA"                         TEXT,
                "DATA/HORA ABERTURA"                                          TEXT,
                "LATITUDE ABERTURA"                                           TEXT,
                "LONGITUDE ABERTURA"                                          TEXT,
                "ENDEREÇO AUTOMÁTICO ABERTURA"                                TEXT,
                "MARCA APARELHO ABERTURA"                                     TEXT,
                "MODELO APARELHO ABERTURA"                                    TEXT,
                "IMEI APARELHO ABERTURA"                                      TEXT,
                "URL FOTO 1 ABERTURA"                                         TEXT,
                "URL FOTO 2 ABERTURA"                                         TEXT,
                "URL FOTO 3 ABERTURA"                                         TEXT,
                "CÓDIGO COLABORADOR ATENDIMENTO"                              TEXT,
                "NOME RESPONSÁVEL ATENDIMENTO"                                TEXT,
                "OBSERVAÇÃO ATENDIMENTO"                                      TEXT,
                "TEMPO ENTRE ABERTURA/ATENDIMENTO HH:MM:SS"                   TEXT,
                "DATA/HORA ATENDIMENTO"                                       TEXT,
                "LATITUDE ATENDIMENTO"                                        TEXT,
                "LONGITUDE ATENDIMENTO"                                       TEXT,
                "ENDEREÇO AUTOMÁTICO ATENDIMENTO"                             TEXT,
                "MARCA APARELHO ATENDIMENTO"                                  TEXT,
                "MODELO APARELHO ATENDIMENTO"                                 TEXT,
                "IMEI APARELHO ATENDIMENTO"                                   TEXT,
                "LATITUDE INICIAL DE DESLOCAMENTO"                            TEXT,
                "LONGITUDE INICIAL DE DESLOCAMENTO"                           TEXT,
                "DATA/HORA INICIAL DE DESLOCAMENTO"                           TEXT,
                "LATITUDE FINAL DE DESLOCAMENTO"                              TEXT,
                "LONGITUDE FINAL DE DESLOCAMENTO"                             TEXT,
                "DATA/HORA FINAL DE DESLOCAMENTO"                             TEXT,
                "TEMPO ENTRE INICIO DESLOCAMENTO/FINAL DESLOCAMENTO HH:MM:SS" TEXT,
                "TEMPO ENTRE FINAL DESLOCAMENTO/FINALIZAÇÃO SOCORRO HH:MM:SS" TEXT,
                "CÓDIGO COLABORADOR INVALIDAÇÃO"                              TEXT,
                "NOME RESPONSÁVEL INVALIDAÇÃO"                                TEXT,
                "MOTIVO INVALIDAÇÃO"                                          TEXT,
                "TEMPO ENTRE ABERTURA/INVALIDAÇÃO HH:MM:SS"                   TEXT,
                "TEMPO ENTRE ATENDIMENTO/INVALIDAÇÃO HH:MM:SS"                TEXT,
                "DATA/HORA INVALIDAÇÃO"                                       TEXT,
                "LATITUDE INVALIDAÇÃO"                                        TEXT,
                "LONGITUDE INVALIDAÇÃO"                                       TEXT,
                "ENDEREÇO AUTOMÁTICO INVALIDAÇÃO"                             TEXT,
                "MARCA APARELHO INVALIDAÇÃO"                                  TEXT,
                "MODELO APARELHO INVALIDAÇÃO"                                 TEXT,
                "IMEI APARELHO INVALIDAÇÃO"                                   TEXT,
                "URL FOTO 1 INVALIDAÇÃO"                                      TEXT,
                "URL FOTO 2 INVALIDAÇÃO"                                      TEXT,
                "URL FOTO 3 INVALIDAÇÃO"                                      TEXT,
                "CÓDIGO COLABORADOR FINALIZAÇÃO"                              TEXT,
                "NOME RESPONSÁVEL FINALIZAÇÃO"                                TEXT,
                "OBSERVAÇÃO FINALIZAÇÃO"                                      TEXT,
                "TEMPO ENTRE ATENDIMENTO/FINALIZAÇÃO HH:MM:SS"                TEXT,
                "DATA/HORA FINALIZAÇÃO"                                       TEXT,
                "LATITUDE FINALIZAÇÃO"                                        TEXT,
                "LONGITUDE FINALIZAÇÃO"                                       TEXT,
                "ENDEREÇO AUTOMÁTICO FINALIZAÇÃO"                             TEXT,
                "MARCA APARELHO FINALIZAÇÃO"                                  TEXT,
                "MODELO APARELHO FINALIZAÇÃO"                                 TEXT,
                "IMEI APARELHO FINALIZAÇÃO"                                   TEXT,
                "URL FOTO 1 FINALIZAÇÃO"                                      TEXT,
                "URL FOTO 2 FINALIZAÇÃO"                                      TEXT,
                "URL FOTO 3 FINALIZAÇÃO"                                      TEXT
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
        SELECT U.NOME :: TEXT                                          AS NOME_UNIDADE,
               COALESCE(
                       DISTANCIA.DISTANCIA_UNIDADE_ABERTURA::TEXT || ' KM',
                       'Unidade sem localização definida no Prolog')   AS DISTANCIA_ENTRE_UNIDADE_ABERTURA,
               CASE
                   WHEN DISTANCIA.DISTANCIA_UNIDADE_ABERTURA::NUMERIC BETWEEN 0.01 AND 51 THEN '2 Horas'
                   WHEN (DISTANCIA.DISTANCIA_UNIDADE_ABERTURA::NUMERIC > 50
                       AND DISTANCIA.DISTANCIA_UNIDADE_ABERTURA::NUMERIC <= 100) THEN '3 Horas'
                   WHEN DISTANCIA.DISTANCIA_UNIDADE_ABERTURA::NUMERIC > 100 THEN '4 Horas'
                   ELSE '-'
                   END                                                 AS DPO,
               COALESCE(SR.CODIGO ::TEXT, '-')                         AS COD_SOCORRO_ROTA,
               COALESCE(SR.STATUS_ATUAL :: TEXT, '-')                  AS STATUS_SOCORRO_ROTA,
               COALESCE(V.PLACA :: TEXT, '-')                          AS PLACA_VEICULO_ABERTURA,
               COALESCE(SRAB.COD_COLABORADOR_ABERTURA :: TEXT, '-')    AS COD_COLABORADOR_ABERTURA,
               COALESCE(CDAB.NOME :: TEXT, '-')                        AS NOME_RESPONSAVEL_ABERTURA,
               COALESCE(SRAB.KM_VEICULO_ABERTURA ::TEXT, '-')          AS KM_VEICULO_COLETADO_ABERTURA,
               COALESCE(SROP.DESCRICAO :: TEXT, '-')                   AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               COALESCE(SRAB.DESCRICAO_PROBLEMA :: TEXT, '-')          AS DESCRICAO_FORNECIDA_ABERTURA,
               COALESCE(SRAB.PONTO_REFERENCIA :: TEXT, '-')            AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               COALESCE((SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                           AS DATA_HORA_ABERTURA,
               COALESCE(SRAB.LATITUDE_ABERTURA :: TEXT, '-')           AS LATITUDE_ABERTURA,
               COALESCE(SRAB.LONGITUDE_ABERTURA :: TEXT, '-')          AS LONGITUDE_ABERTURA,
               COALESCE(SRAB.ENDERECO_AUTOMATICO :: TEXT, '-')         AS ENDERECO_AUTOMATICO_ABERTURA,
               COALESCE(SRAB.MARCA_DEVICE_ABERTURA :: TEXT, '-')       AS MARCA_APARELHO_ABERTURA,
               COALESCE(SRAB.MODELO_DEVICE_ABERTURA :: TEXT, '-')      AS MODELO_APARELHO_ABERTURA,
               COALESCE(SRAB.DEVICE_IMEI_ABERTURA :: TEXT, '-')        AS IMEI_APARELHO_ABERTURA,
               COALESCE(SRAB.URL_FOTO_1_ABERTURA :: TEXT, '-')         AS URL_FOTO_1_ABERTURA,
               COALESCE(SRAB.URL_FOTO_2_ABERTURA :: TEXT, '-')         AS URL_FOTO_2_ABERTURA,
               COALESCE(SRAB.URL_FOTO_3_ABERTURA :: TEXT, '-')         AS URL_FOTO_3_ABERTURA,
               COALESCE(SRAT.COD_COLABORADOR_ATENDIMENTO :: TEXT, '-') AS COD_COLABORADOR_ATENDIMENTO,
               COALESCE(CDAT.NOME :: TEXT, '-')                        AS NOME_RESPONSAVEL_ATENDIMENTO,
               COALESCE(SRAT.OBSERVACAO_ATENDIMENTO :: TEXT, '-')      AS OBSERVACAO_ATENDIMENTO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRAT.DATA_HORA_ATENDIMENTO - SRAB.DATA_HORA_ABERTURA),
                        '-')                                           AS TEMPO_ABERTURA_ATENDIMENTO,
               COALESCE((SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                           AS DATA_HORA_ATENDIMENTO,
               COALESCE(SRAT.LATITUDE_ATENDIMENTO :: TEXT, '-')        AS LATITUDE_ATENDIMENTO,
               COALESCE(SRAT.LONGITUDE_ATENDIMENTO :: TEXT, '-')       AS LONGITUDE_ATENDIMENTO,
               COALESCE(SRAT.ENDERECO_AUTOMATICO :: TEXT, '-')         AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               COALESCE(SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT, '-')    AS MARCA_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT, '-')   AS MODELO_APARELHO_ATENDIMENTO,
               COALESCE(SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT, '-')     AS IMEI_APARELHO_ATENDIMENTO,
               COALESCE(SRAD.LATITUDE_INICIO :: TEXT, '-')             AS LATITUDE_INICIO,
               COALESCE(SRAD.LONGITUDE_INICIO :: TEXT, '-')            AS LONGITUDE_INICIO,
               COALESCE((SRAD.DATA_HORA_DESLOCAMENTO_INICIO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                           AS DATA_HORA_DESLOCAMENTO_INICIO,
               COALESCE(SRAD.LATITUDE_FIM :: TEXT, '-')                AS LATITUDE_FIM,
               COALESCE(SRAD.LONGITUDE_FIM :: TEXT, '-')               AS LONGITUDE_FIM,
               COALESCE((SRAD.DATA_HORA_DESLOCAMENTO_FIM AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT, '-')
                                                                       AS DATA_HORA_DESLOCAMENTO_FIM,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRAD.DATA_HORA_DESLOCAMENTO_FIM - SRAD.DATA_HORA_DESLOCAMENTO_INICIO),
                        '-')                                           AS TEMPO_INICIO_FIM_DESLOCAMENTO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRF.DATA_HORA_FINALIZACAO - SRAD.DATA_HORA_DESLOCAMENTO_FIM),
                        '-')                                           AS TEMPO_FIM_DESLOCAMENTO_FINALIZACAO_SOCORRO,
               COALESCE(SRI.COD_COLABORADOR_INVALIDACAO :: TEXT, '-')  AS COD_COLABORADOR_INVALIDACAO,
               COALESCE(CDI.NOME :: TEXT, '-')                         AS NOME_RESPONSAVEL_INVALIDACAO,
               COALESCE(SRI.MOTIVO_INVALIDACAO :: TEXT, '-')           AS MOTIVO_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAB.DATA_HORA_ABERTURA)
                            ELSE '-'
                            END,
                        '-')                                           AS TEMPO_ABERTURA_INVALIDACAO,
               COALESCE(CASE
                            WHEN (SRAT.DATA_HORA_ATENDIMENTO IS NOT NULL)
                                THEN FUNC_CONVERTE_INTERVAL_HHMMSS(
                                    SRI.DATA_HORA_INVALIDACAO - SRAT.DATA_HORA_ATENDIMENTO)
                            ELSE '-'
                            END,
                        '-')                                           AS TEMPO_ATENDIMENTO_INVALIDACAO,
               COALESCE((SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                           AS DATA_HORA_INVALIDACAO,
               COALESCE(SRI.LATITUDE_INVALIDACAO :: TEXT, '-')         AS LATITUDE_INVALIDACAO,
               COALESCE(SRI.LONGITUDE_INVALIDACAO :: TEXT, '-')        AS LONGITUDE_INVALIDACAO,
               COALESCE(SRI.ENDERECO_AUTOMATICO :: TEXT, '-')          AS ENDERECO_AUTOMATICO_INVALIDACAO,
               COALESCE(SRI.MARCA_DEVICE_INVALIDACAO :: TEXT, '-')     AS MARCA_APARELHO_INVALIDACAO,
               COALESCE(SRI.MODELO_DEVICE_INVALIDACAO :: TEXT, '-')    AS MODELO_APARELHO_INVALIDACAO,
               COALESCE(SRI.DEVICE_IMEI_INVALIDACAO :: TEXT, '-')      AS IMEI_APARELHO_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_1_INVALIDACAO :: TEXT, '-')       AS URL_FOTO_1_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_2_INVALIDACAO :: TEXT, '-')       AS URL_FOTO_2_INVALIDACAO,
               COALESCE(SRI.URL_FOTO_3_INVALIDACAO :: TEXT, '-')       AS URL_FOTO_3_INVALIDACAO,
               COALESCE(SRF.COD_COLABORADOR_FINALIZACAO :: TEXT, '-')  AS COD_COLABORADOR_FINALIZACAO,
               COALESCE(CDF.NOME :: TEXT, '-')                         AS NOME_RESPONSAVEL_FINALIZACAO,
               COALESCE(SRF.OBSERVACAO_FINALIZACAO :: TEXT, '-')       AS OBSERVACAO_FINALIZACAO,
               COALESCE(FUNC_CONVERTE_INTERVAL_HHMMSS(
                                SRF.DATA_HORA_FINALIZACAO - SRAT.DATA_HORA_ATENDIMENTO),
                        '-')                                           AS TEMPO_ATENDIMENTO_FINALIZACAO,
               COALESCE((SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) ::TEXT,
                        '-')                                           AS DATA_HORA_FINALIZACAO,
               COALESCE(SRF.LATITUDE_FINALIZACAO :: TEXT, '-')         AS LATITUDE_FINALIZACAO,
               COALESCE(SRF.LONGITUDE_FINALIZACAO :: TEXT, '-')        AS LONGITUDE_FINALIZACAO,
               COALESCE(SRF.ENDERECO_AUTOMATICO :: TEXT, '-')          AS ENDERECO_AUTOMATICO_FINALIZACAO,
               COALESCE(SRF.MARCA_DEVICE_FINALIZACAO :: TEXT, '-')     AS MARCA_APARELHO_FINALIZACAO,
               COALESCE(SRF.MODELO_DEVICE_FINALIZACAO :: TEXT, '-')    AS MODELO_APARELHO_FINALIZACAO,
               COALESCE(SRF.DEVICE_IMEI_FINALIZACAO :: TEXT, '-')      AS IMEI_APARELHO_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_1_FINALIZACAO :: TEXT, '-')       AS URL_FOTO_1_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_2_FINALIZACAO :: TEXT, '-')       AS URL_FOTO_2_FINALIZACAO,
               COALESCE(SRF.URL_FOTO_3_FINALIZACAO :: TEXT, '-')       AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON SR.COD_UNIDADE = U.CODIGO
                 JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.CODIGO = SRAB.COD_SOCORRO_ROTA
                 JOIN VEICULO_DATA V ON V.CODIGO = SRAB.COD_VEICULO_PROBLEMA
                 JOIN COLABORADOR_DATA CDAB ON CDAB.CODIGO = SRAB.COD_COLABORADOR_ABERTURA
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SR.CODIGO = SRAT.COD_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO SRAD
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAD.COD_SOCORRO_ROTA_ATENDIMENTO = SRAT.CODIGO
                 LEFT JOIN COLABORADOR_DATA CDAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              CDAT.CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
                 LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
                 LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
           , LATERAL ( SELECT FUNC_CALCULA_DISTANCIA_UNIDADE_ABERTURA(
                                      LONGITUDE_UNIDADE::REAL,
                                      LATITUDE_UNIDADE::REAL,
                                      LONGITUDE_ABERTURA::REAL,
                                      LATITUDE_ABERTURA::REAL) AS DISTANCIA_UNIDADE_ABERTURA) DISTANCIA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND (F_IF(F_ARRAY_CONTEM_STATUS_ABERTO,
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
                    FALSE))
        ORDER BY U.NOME,
                 -- Perceba que pelo STATUS_ATUAL ser um enum, ele vai ser ordenado pela ordem em que os status foram
                 -- declarados na criação do enum. E essa ordem é exatamente a que queremos para esse relatório:
                 -- 'ABERTO', 'EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO'
                 SR.STATUS_ATUAL,
                 V.PLACA;
END ;
$$;


CREATE OR REPLACE FUNCTION FUNC_CALCULA_DISTANCIA_UNIDADE_ABERTURA(F_LONGITUDE_UNIDADE REAL,
                                                                   F_LATITUDE_UNIDADE REAL,
                                                                   F_LONGITUDE_ABERTURA REAL,
                                                                   F_LATITUDE_ABERTURA REAL)
    RETURNS NUMERIC
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    RETURN TRUNC(
                   (ST_DISTANCE_SPHERE(
                            ST_POINT(
                                    F_LONGITUDE_UNIDADE::REAL,
                                    F_LATITUDE_UNIDADE::REAL),
                            ST_POINT(
                                    F_LONGITUDE_ABERTURA::REAL,
                                    --Neste ponto é realizada a divisão por 1000 pois o retorno do ST_DISTANCE_SPHERE
                                    --é em metros. Dessa forma, dividindo por 1000, passamos para KM.
                                    F_LATITUDE_ABERTURA::REAL)) / 1000)::NUMERIC,
                    -- Este parametro, fazendo parte da function TRUNC, limita o retorno em 2 caracteres apos a virgula.
                   2);
END;
$$;