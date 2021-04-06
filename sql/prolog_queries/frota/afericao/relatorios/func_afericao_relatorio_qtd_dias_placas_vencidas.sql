CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
                                                                            F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                           TEXT,
                PLACA                             TEXT,
                IDENTIFICADOR_FROTA               TEXT,
                PODE_AFERIR_SULCO                 BOOLEAN,
                PODE_AFERIR_PRESSAO               BOOLEAN,
                QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
                QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    AFERICAO_SULCO         VARCHAR := 'SULCO';
    AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
    AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
    RETURN QUERY
        WITH VEICULOS_ATIVOS_UNIDADES AS (
            SELECT V.CODIGO
            FROM VEICULO V
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND V.STATUS_ATIVO
        ),
             -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam o codigo de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
             -- que a aferição vai vencer em 20 dias.
             ULTIMA_AFERICAO_SULCO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                      A.COD_VEICULO              AS COD_VEICULO,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
                   -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_SULCO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),
             ULTIMA_AFERICAO_PRESSAO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                      A.COD_VEICULO                AS COD_VEICULO,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_PRESSAO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),

             PRE_SELECT AS (
                 SELECT U.NOME                                            AS NOME_UNIDADE,
                        V.PLACA                                           AS PLACA_VEICULO,
                        COALESCE(V.IDENTIFICADOR_FROTA, '-')              AS IDENTIFICADOR_FROTA,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_SULCO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_PRESSAO,
                        -- Por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
                        UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
                 FROM UNIDADE U
                          JOIN VEICULO V
                               ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
                                    ON UAS.COD_VEICULO = V.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
                                    ON UAP.COD_VEICULO = V.CODIGO
                 WHERE
                     -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
                     (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR
                      UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
                 GROUP BY U.NOME,
                          V.PLACA,
                          V.IDENTIFICADOR_FROTA,
                          V.COD_TIPO,
                          V.COD_UNIDADE,
                          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
                          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
             )
        SELECT PS.NOME_UNIDADE::TEXT                         AS NOME_UNIDADE,
               PS.PLACA_VEICULO::TEXT                        AS PLACA_VEICULO,
               PS.IDENTIFICADOR_FROTA::TEXT                  AS IDENTIFICADOR_FROTA,
               PS.PODE_AFERIR_SULCO                          AS PODE_AFERIR_SULCO,
               PS.PODE_AFERIR_PRESSAO                        AS PODE_AFERIR_PRESSAO,
               PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA::INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
               PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA::INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM PRE_SELECT PS
             -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        WHERE PS.PODE_AFERIR_SULCO <> FALSE
           OR PS.PODE_AFERIR_PRESSAO <> FALSE
        ORDER BY PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
                 PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;