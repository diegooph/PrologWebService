CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PRODUTIVIDADE_MECANICOS(F_COD_UNIDADES BIGINT[],
                                                                               F_DATA_INICIAL DATE,
                                                                               F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                UNIDADE                TEXT,
                "MECÂNICO"             TEXT,
                CONSERTOS              BIGINT,
                HORAS                  NUMERIC,
                "HORAS POR CONSERTO"   NUMERIC,
                "MINUTOS POR CONSERTO" NUMERIC
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                              AS NOME_UNIDADE,
               C.NOME :: TEXT                              AS NOME_MECANICO,
               COUNT(COSI.CPF_MECANICO)                    AS CONSERTOS,
               SUM(COSI.TEMPO_REALIZACAO / 3600000)        AS HORAS,
               ROUND(AVG(COSI.TEMPO_REALIZACAO / 3600000)) AS HORAS_POR_CONSERTO,
               ROUND(AVG(COSI.TEMPO_REALIZACAO / 60000))   AS MINUTOS_POR_CONSERTO
        FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                 JOIN UNIDADE U ON U.CODIGO = COSI.COD_UNIDADE
                 JOIN COLABORADOR C ON C.CPF = COSI.CPF_MECANICO
        WHERE COSI.TEMPO_REALIZACAO IS NOT NULL
          AND COSI.TEMPO_REALIZACAO > 0
          AND COSI.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND (COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COSI.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
          AND (COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COSI.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
        GROUP BY U.CODIGO, COSI.CPF_MECANICO, C.NOME;
END;
$$;