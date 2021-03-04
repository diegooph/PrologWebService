-- Sobre:
--
-- Function disponível na API do ProLog para listar os ajustes de marcações realizados pelos colaboradores de uma
-- empresa.
--
-- A function lista todos os ajustes de marcações realizados por todos os colaboradores da empresa. Utiliza um
-- parâmetro para saber a partir de qual índice (código) que serão retornados os ajustes.
-- Além das informações do ajuste, a function também retorna o estado atual da marcação, já com os ajustes aplicados.
--
-- Precondições:
-- Para listar os ajustes realizados é necessário que o token repassado para function exista.
--
-- Histórico:
-- 2019-09-02 -> Function criada (diogenesvanzella - PL-2274).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_LISTA_AJUSTES_MARCACOES_REALIZADOS(
  F_TOKEN_INTEGRACAO TEXT,
  F_COD_ULTIMO_AJUSTE_MARCACAO_SINCRONIZADO BIGINT)
  RETURNS TABLE (
    CODIGO_EDICAO                        BIGINT,
    COD_JUSTIFICATIVA_SELECIONADA_AJUSTE BIGINT,
    JUSTIFICATIVA_SELECIONADA_AJUSTE     TEXT,
    OBSERVACAO_AJUSTE_MARCACAO           TEXT,
    ACAO_AJUSTE_MARCACAO                 TEXT,
    CPF_COLABORADOR_AJUSTE               TEXT,
    DATA_HORA_AJUSTE_UTC                 TIMESTAMP WITHOUT TIME ZONE,
    COD_UNIDADE                          BIGINT,
    CODIGO                               BIGINT,
    COD_MARCACAO_VINCULO                 BIGINT,
    COD_TIPO_MARCACAO                    BIGINT,
    CPF_COLABORADOR                      TEXT,
    TIPO_MARCACAO                        TEXT,
    DATA_HORA_MARCACAO_UTC               TIMESTAMP WITHOUT TIME ZONE,
    FONTE_DATA_HORA                      TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO      TEXT,
    JUSTIFICATIVA_ESTOURO                TEXT,
    LATITUDE_MARCACAO                    TEXT,
    LONGITUDE_MARCACAO                   TEXT,
    DATA_HORA_SINCRONIZACAO_UTC          TIMESTAMP WITHOUT TIME ZONE,
    DEVICE_IMEI                          TEXT,
    DEVICE_ID                            TEXT,
    MARCA_DEVICE                         TEXT,
    MODELO_DEVICE                        TEXT,
    VERSAO_APP_MOMENTO_MARCACAO          INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO     INTEGER,
    DEVICE_UPTIME_REALIZACAO_MILLIS      BIGINT,
    DEVICE_UPTIME_SINCRONIZACAO_MILLIS   BIGINT,
    ANDROID_API_VERSION                  INTEGER,
    STATUS_ATIVO                         BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_TOKEN BIGINT := (SELECT TI.COD_EMPRESA
                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
BEGIN
  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_TOKEN,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  RETURN QUERY
  SELECT
    MH.CODIGO                                    AS CODIGO_EDICAO,
    MA.COD_JUSTIFICATIVA_AJUSTE                  AS COD_JUSTIFICATIVA_SELECIONADA_AJUSTE,
    MJA.NOME                                     AS JUSTIFICATIVA_SELECIONADA_AJUSTE,
    MA.OBSERVACAO_AJUSTE                         AS OBSERVACAO_AJUSTE_MARCACAO,
    MA.ACAO_AJUSTE::TEXT                         AS ACAO_AJUSTE_MARCACAO,
    LPAD(C.CPF::TEXT, 11, '0')::TEXT             AS CPF_COLABORADOR_AJUSTE,
    MA.DATA_HORA_AJUSTE AT TIME ZONE 'UTC'       AS DATA_HORA_AJUSTE_UTC,
    I.COD_UNIDADE                                AS COD_UNIDADE,
    I.CODIGO                                     AS CODIGO,
    CASE
    WHEN I.TIPO_MARCACAO = 'MARCACAO_INICIO'
        THEN
        (SELECT MVIF.COD_MARCACAO_FIM
         FROM MARCACAO_VINCULO_INICIO_FIM MVIF
         WHERE MVIF.COD_MARCACAO_INICIO = I.CODIGO)
    ELSE
        (SELECT MVIF.COD_MARCACAO_INICIO
         FROM MARCACAO_VINCULO_INICIO_FIM MVIF
         WHERE MVIF.COD_MARCACAO_FIM = I.CODIGO)
    END                                          AS COD_MARCACAO_VINCULO,
    I.COD_TIPO_INTERVALO                         AS COD_TIPO_MARCACAO,
    LPAD(I.CPF_COLABORADOR::TEXT, 11, '0')::TEXT AS CPF_COLABORADOR,
    I.TIPO_MARCACAO::TEXT                        AS TIPO_MARCACAO,
    I.DATA_HORA AT TIME ZONE 'UTC'               AS DATA_HORA_MARCACAO_UTC,
    I.FONTE_DATA_HORA::TEXT                      AS FONTE_DATA_HORA,
    I.JUSTIFICATIVA_TEMPO_RECOMENDADO            AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
    I.JUSTIFICATIVA_ESTOURO                      AS JUSTIFICATIVA_ESTOURO,
    I.LATITUDE_MARCACAO                          AS LATITUDE_MARCACAO,
    I.LONGITUDE_MARCACAO                         AS LONGITUDE_MARCACAO,
    I.DATA_HORA_SINCRONIZACAO AT TIME ZONE 'UTC' AS DATA_HORA_SINCRONIZACAO_UTC,
    I.DEVICE_IMEI                                AS DEVICE_IMEI,
    I.DEVICE_ID                                  AS DEVICE_ID,
    I.MARCA_DEVICE                               AS MARCA_DEVICE,
    I.MODELO_DEVICE                              AS MODELO_DEVICE,
    I.VERSAO_APP_MOMENTO_MARCACAO                AS VERSAO_APP_MOMENTO_MARCACAO,
    I.VERSAO_APP_MOMENTO_SINCRONIZACAO           AS VERSAO_APP_MOMENTO_SINCRONIZACAO,
    I.DEVICE_UPTIME_REALIZACAO_MILLIS            AS DEVICE_UPTIME_REALIZACAO_MILLIS,
    I.DEVICE_UPTIME_SINCRONIZACAO_MILLIS         AS DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
    I.ANDROID_API_VERSION                        AS ANDROID_API_VERSION,
    I.STATUS_ATIVO                               AS STATUS_ATIVO
  FROM MARCACAO_HISTORICO MH
    JOIN MARCACAO_AJUSTE MA ON MH.COD_AJUSTE = MA.CODIGO
    JOIN MARCACAO_JUSTIFICATIVA_AJUSTE MJA ON MA.COD_JUSTIFICATIVA_AJUSTE = MJA.CODIGO
    JOIN INTERVALO I ON MH.COD_MARCACAO = I.CODIGO
    JOIN COLABORADOR C ON MA.COD_COLABORADOR_AJUSTE = C.CODIGO
  WHERE MA.COD_UNIDADE_AJUSTE IN (SELECT U.CODIGO FROM UNIDADE U WHERE U.COD_EMPRESA = COD_EMPRESA_TOKEN)
        AND MH.CODIGO > F_COD_ULTIMO_AJUSTE_MARCACAO_SINCRONIZADO
  ORDER BY MH.CODIGO;
END;
$$;