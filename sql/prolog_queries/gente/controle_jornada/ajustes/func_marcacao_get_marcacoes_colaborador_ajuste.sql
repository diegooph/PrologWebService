CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(F_COD_TIPO_MARCACAO BIGINT,
                                                                          F_COD_COLABORADOR   BIGINT,
                                                                          F_DIA DATE)
  RETURNS TABLE(
    COD_MARCACAO_INICIO            BIGINT,
    COD_MARCACAO_FIM               BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ATIVO_INICIO            BOOLEAN,
    STATUS_ATIVO_FIM               BOOLEAN,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN,
    COD_TIPO_MARCACAO              BIGINT,
    NOME_TIPO_MARCACAO             TEXT,
    DEVICE_IMEI_INICIO             TEXT,
    DEVICE_IMEI_INICIO_RECONHECIDO BOOLEAN,
    DEVICE_MARCA_INICIO            TEXT,
    DEVICE_MODELO_INICIO           TEXT,
    DEVICE_IMEI_FIM                TEXT,
    DEVICE_IMEI_FIM_RECONHECIDO    BOOLEAN,
    DEVICE_MARCA_FIM               TEXT,
    DEVICE_MODELO_FIM              TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT F.COD_MARCACAO_INICIO                                       AS COD_MARCACAO_INICIO,
         F.COD_MARCACAO_FIM                                          AS COD_MARCACAO_FIM,
         (F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) AS DATA_HORA_INICIO,
         (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))    AS DATA_HORA_FIM,
         F.STATUS_ATIVO_INICIO                                       AS STATUS_ATIVO_INICIO,
         F.STATUS_ATIVO_FIM                                          AS STATUS_ATIVO_FIM,
         F.FOI_AJUSTADO_INICIO                                       AS FOI_AJUSTADO_INICIO,
         F.FOI_AJUSTADO_FIM                                          AS FOI_AJUSTADO_FIM,
         F.COD_TIPO_INTERVALO                                        AS COD_TIPO_MARCACAO,
         IT.NOME :: TEXT                                             AS NOME_TIPO_MARCACAO,
         F.DEVICE_IMEI_INICIO :: TEXT                                AS DEVICE_IMEI_INICIO,
         F.DEVICE_IMEI_INICIO_RECONHECIDO :: BOOLEAN                 AS DEVICE_IMEI_INICIO_RECONHECIDO,
         F.DEVICE_MARCA_INICIO :: TEXT                               AS DEVICE_MARCA_INICIO,
         F.DEVICE_MODELO_INICIO :: TEXT                              AS DEVICE_MODELO_INICIO,
         F.DEVICE_IMEI_FIM :: TEXT                                   AS DEVICE_IMEI_FIM,
         F.DEVICE_IMEI_FIM_RECONHECIDO :: BOOLEAN                    AS DEVICE_IMEI_FIM_RECONHECIDO,
         F.DEVICE_MARCA_FIM :: TEXT                                  AS DEVICE_MARCA_FIM,
         F.DEVICE_MODELO_FIM :: TEXT                                 AS DEVICE_MODELO_FIM
  FROM FUNC_INTERVALOS_AGRUPADOS(NULL,
                                 (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
                                 F_COD_TIPO_MARCACAO) F
         JOIN INTERVALO_TIPO IT ON F.COD_TIPO_INTERVALO = IT.CODIGO
  WHERE ((F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA
           OR (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA)
  ORDER BY COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM);
END;
$$;