CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_EXPORTACAO_GENERICA(F_COD_UNIDADE             BIGINT,
                                                                       F_COD_TIPO_INTERVALO      BIGINT,
                                                                       F_COD_COLABORADOR         BIGINT,
                                                                       F_APENAS_MARCACOES_ATIVAS BOOLEAN,
                                                                       F_DATA_INICIAL            DATE,
                                                                       F_DATA_FINAL              DATE)
    RETURNS TABLE
            (
                PIS           TEXT,
                EVENTO        TEXT,
                DATA          TEXT,
                HORA          TEXT,
                NUMERORELOGIO TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
    RETURN QUERY
        SELECT LPAD(C.PIS :: TEXT, 11, '0')                             AS PIS,
               COALESCE(IT.COD_AUXILIAR, '00')                          AS EVENTO,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'DDMMYYYY') AS DATA,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'HH24mi')   AS HORA,
               COALESCE(U.COD_AUXILIAR, '00')                           AS NUMERORELOGIO
        FROM INTERVALO I
                 JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF
                 JOIN INTERVALO_TIPO IT ON I.COD_UNIDADE = IT.COD_UNIDADE AND I.COD_TIPO_INTERVALO = IT.CODIGO
                 JOIN UNIDADE U ON I.COD_UNIDADE = U.CODIGO
        WHERE I.COD_UNIDADE = F_COD_UNIDADE
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE >= F_DATA_INICIAL
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE <= F_DATA_FINAL
          AND C.PIS IS NOT NULL
          AND C.PIS <> ''
          AND F_IF(F_COD_COLABORADOR IS NULL, TRUE, C.CODIGO = F_COD_COLABORADOR)
          AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.CODIGO = F_COD_TIPO_INTERVALO)
          AND F_IF(F_APENAS_MARCACOES_ATIVAS IS NULL, TRUE, I.STATUS_ATIVO)
          AND I.STATUS_ATIVO;
END;
$$;