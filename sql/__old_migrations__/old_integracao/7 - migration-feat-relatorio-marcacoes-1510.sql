BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_RELATORIO_INTERVALO_PORTARIA_1510_TIPO_3(F_TOKEN_INTEGRACAO TEXT,
                                                                                    F_COD_ULTIMA_MARCACAO_SINCRONIZADA BIGINT,
                                                                                    F_DATA_INICIAL DATE,
                                                                                    F_DATA_FINAL DATE,
                                                                                    F_COD_UNIDADE BIGINT,
                                                                                    F_COD_TIPO_INTERVALO BIGINT,
                                                                                    F_CPF_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                COD_MARCACAO     BIGINT,
                NSR              TEXT,
                TIPO_REGISTRO    TEXT,
                DATA_MARCACAO    TEXT,
                HORARIO_MARCACAO TEXT,
                PIS_COLABORADOR  TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    SELECT VI.CODIGO                                                                 AS COD_MARCACAO,
           LPAD(VI.CODIGO_MARCACAO_POR_UNIDADE::TEXT, 9, '0')                        AS NSR,
           '3'::TEXT                                                                 AS TIPO_REGISTRO,
           TO_CHAR(VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE), 'DDMMYYYY') AS DATA_MARCACAO,
           TO_CHAR(VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE), 'HH24MI')   AS HORARIO_MARCACAO,
           LPAD(C.PIS::TEXT, 12, '0')                                                AS PIS_COLABORADOR
    FROM VIEW_INTERVALO VI
             JOIN COLABORADOR C ON VI.CPF_COLABORADOR = C.CPF AND C.PIS IS NOT NULL
             JOIN UNIDADE U ON U.CODIGO = VI.COD_UNIDADE
             JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
    WHERE (VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE))::DATE >= F_DATA_INICIAL
      AND (VI.DATA_HORA AT TIME ZONE TZ_UNIDADE(VI.COD_UNIDADE))::DATE <= F_DATA_FINAL
      AND VI.CODIGO > F_COD_ULTIMA_MARCACAO_SINCRONIZADA
      AND E.CODIGO =
          (SELECT TI.COD_EMPRESA FROM INTEGRACAO.TOKEN_INTEGRACAO TI WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
      AND F_IF(F_COD_UNIDADE IS NULL, TRUE, VI.COD_UNIDADE = F_COD_UNIDADE)
      AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, VI.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO)
      AND F_IF(F_CPF_COLABORADOR IS NULL, TRUE, VI.CPF_COLABORADOR = F_CPF_COLABORADOR);
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;