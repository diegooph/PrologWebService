CREATE OR REPLACE FUNCTION
    FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                        F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
                                                        F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                              TEXT,
                PLACA                                TEXT,
                "IDENTIFICADOR FROTA"                TEXT,
                "QTD PNEUS APLICADOS"                TEXT,
                "MODELO VEÍCULO"                     TEXT,
                "TIPO VEÍCULO"                       TEXT,
                "STATUS SULCO"                       TEXT,
                "STATUS PRESSÃO"                     TEXT,
                "DATA VENCIMENTO SULCO"              TEXT,
                "DATA VENCIMENTO PRESSÃO"            TEXT,
                "DIAS VENCIMENTO SULCO"              TEXT,
                "DIAS VENCIMENTO PRESSÃO"            TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
                "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH DADOS AS (
            SELECT U.NOME::TEXT                                                         AS NOME_UNIDADE,
                   V.PLACA::TEXT                                                        AS PLACA_VEICULO,
                   COALESCE(V.IDENTIFICADOR_FROTA::TEXT, '-')                           AS IDENTIFICADOR_FROTA,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.COD_VEICULO = V.CODIGO
                    GROUP BY VP.COD_VEICULO)::TEXT                                       AS QTD_PNEUS_APLICADOS,
                   MV.NOME::TEXT                                                         AS NOME_MODELO_VEICULO,
                   VT.NOME::TEXT                                                         AS NOME_TIPO_VEICULO,
                   TO_CHAR(BASE.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI')   AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                   TO_CHAR(BASE.DATA_HORA_ULTIMA_AFERICAO_PRESSAO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                   TO_CHAR(BASE.DATA_VENCIMENTO_SULCO, 'DD/MM/YYYY')                     AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(BASE.DATA_VENCIMENTO_PRESSAO, 'DD/MM/YYYY')                   AS DATA_VENCIMENTO_PRESSAO,
                   BASE.DIAS_VENCIMENTO_SULCO::TEXT                                      AS DIAS_VENCIMENTO_SULCO,
                   BASE.DIAS_VENCIMENTO_PRESSAO::TEXT                                    AS DIAS_VENCIMENTO_PRESSAO,
                   BASE.DIAS_DESDE_ULTIMA_AFERICAO_SULCO::TEXT                           AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   BASE.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO::TEXT                         AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   BASE.PODE_AFERIR_SULCO                                                AS PODE_AFERIR_SULCO,
                   BASE.PODE_AFERIR_PRESSAO                                              AS PODE_AFERIR_PRESSAO,
                   BASE.SULCO_NUNCA_AFERIDO                                              AS SULCO_NUNCA_AFERIDO,
                   BASE.PRESSAO_NUNCA_AFERICO                                            AS PRESSAO_NUNCA_AFERIDA,
                   BASE.AFERICAO_SULCO_VENCIDA                                           AS AFERICAO_SULCO_VENCIDA,
                   BASE.AFERICAO_SULCO_VENCIDA                                           AS AFERICAO_PRESSAO_VENCIDA
            FROM VEICULO V
                     JOIN MODELO_VEICULO MV
                          ON MV.CODIGO = V.COD_MODELO
                     JOIN VEICULO_TIPO VT
                          ON VT.CODIGO = V.COD_TIPO
                     JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                          ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                               AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))::DATE                          AS DATA_ULTIMA_AFERICAO_PRESSAO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                         EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS PRESSAO ON PRESSAO.COD_VEICULO_INTERVALO = V.CODIGO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                             AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                         EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS SULCO ON SULCO.COD_VEICULO_INTERVALO = V.CODIGO
                     JOIN PNEU_RESTRICAO_UNIDADE PRU
                          ON PRU.COD_UNIDADE = V.COD_UNIDADE
                     JOIN UNIDADE U
                          ON U.CODIGO = V.COD_UNIDADE
                     JOIN FUNC_AFERICAO_RELATORIO_DADOS_BASE_VALIDACAO_VENCIMENTO(F_COD_UNIDADES,
                                                                                  F_DATA_HORA_ATUAL_UTC) AS BASE
                          ON BASE.COD_VEICULO = V.CODIGO
            WHERE V.STATUS_ATIVO = TRUE
              AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
            ORDER BY U.CODIGO, V.PLACA
        )
             -- Todos os coalesce ficam aqui.
        SELECT D.NOME_UNIDADE                                               AS NOME_UNIDADE,
               D.PLACA_VEICULO                                              AS PLACA_VEICULO,
               D.IDENTIFICADOR_FROTA                                        AS IDENTIFICADOR_FROTA,
               COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
               D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
               D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
               CASE
                   WHEN NOT D.PODE_AFERIR_SULCO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.SULCO_NUNCA_AFERIDO
                       THEN 'SULCO NUNCA AFERIDO'
                   WHEN D.AFERICAO_SULCO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_SULCO,
               CASE
                   WHEN NOT D.PODE_AFERIR_PRESSAO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.PRESSAO_NUNCA_AFERIDA
                       THEN 'PRESSÃO NUNCA AFERIDA'
                   WHEN D.AFERICAO_PRESSAO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_SULCO, '-')             AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO, '-')           AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
               TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
        FROM DADOS D;
END;
$$;