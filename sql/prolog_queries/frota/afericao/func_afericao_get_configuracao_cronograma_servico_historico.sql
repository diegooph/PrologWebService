CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_HISTORICO(F_COD_RESTRICAO_UNIDADE_PNEU BIGINT)
    RETURNS TABLE
            (
                NOME_UNIDADE             TEXT,
                NOME_COLABORADOR         TEXT,
                DATA_HORA_ALTERACAO      TIMESTAMP WITHOUT TIME ZONE,
                TOLERANCIA_CALIBRAGEM    REAL,
                TOLERANCIA_INSPECAO      REAL,
                SULCO_MINIMO_RECAPAGEM   REAL,
                SULCO_MINIMO_DESCARTE    REAL,
                PERIODO_AFERICAO_PRESSAO INTEGER,
                PERIODO_AFERICAO_SULCO   INTEGER,
                ATUAL                    BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                                                            AS NOME_UNIDADE,
               F_IF(CD.NOME IS NULL, 'Cadastrado pelo Prolog', CD.NOME) :: TEXT          AS NOME_COLABORADOR,
               PRU.DATA_HORA_ULTIMA_ATUALIZACAO AT TIME ZONE TZ_UNIDADE(PRU.COD_UNIDADE) AS DATA_HORA_ALTERACAO,
               PRU.TOLERANCIA_CALIBRAGEM                                                 AS TOLERANCIA_CALIBRAGEM,
               PRU.TOLERANCIA_INSPECAO                                                   AS TOLERANCIA_INSPECAO,
               PRU.SULCO_MINIMO_RECAPAGEM                                                AS SULCO_MINIMO_RECAPAGEM,
               PRU.SULCO_MINIMO_DESCARTE                                                 AS SULCO_MINIMO_DESCARTE,
               PRU.PERIODO_AFERICAO_PRESSAO                                              AS PERIODO_AFERICAO_PRESSAO,
               PRU.PERIODO_AFERICAO_SULCO                                                AS PERIODO_AFERICAO_SULCO,
               TRUE                                                                      AS ATUAL
        FROM PNEU_RESTRICAO_UNIDADE PRU
                 JOIN UNIDADE U ON PRU.COD_UNIDADE = U.CODIGO
                 LEFT JOIN COLABORADOR CD ON PRU.COD_COLABORADOR_ULTIMA_ATUALIZACAO = CD.CODIGO
        WHERE PRU.CODIGO = F_COD_RESTRICAO_UNIDADE_PNEU
        UNION ALL
        SELECT U.NOME :: TEXT                                                     AS NOME_UNIDADE,
               F_IF(CD.NOME IS NULL, 'Cadastrado pelo Prolog', CD.NOME) :: TEXT   AS NOME_COLABORADOR,
               PRUH.DATA_HORA_ALTERACAO AT TIME ZONE TZ_UNIDADE(PRUH.COD_UNIDADE) AS DATA_HORA_ALTERACAO,
               PRUH.TOLERANCIA_CALIBRAGEM                                         AS TOLERANCIA_CALIBRAGEM,
               PRUH.TOLERANCIA_INSPECAO                                           AS TOLERANCIA_INSPECAO,
               PRUH.SULCO_MINIMO_RECAPAGEM                                        AS SULCO_MINIMO_RECAPAGEM,
               PRUH.SULCO_MINIMO_DESCARTE                                         AS SULCO_MINIMO_DESCARTE,
               PRUH.PERIODO_AFERICAO_PRESSAO                                      AS PERIODO_AFERICAO_PRESSAO,
               PRUH.PERIODO_AFERICAO_SULCO                                        AS PERIODO_AFERICAO_SULCO,
               FALSE                                                              AS ATUAL
        FROM PNEU_RESTRICAO_UNIDADE_HISTORICO PRUH
                 JOIN UNIDADE U ON PRUH.COD_UNIDADE = U.CODIGO
                 LEFT JOIN COLABORADOR CD ON PRUH.COD_COLABORADOR = CD.CODIGO
        WHERE PRUH.COD_RESTRICAO_UNIDADE_PNEU = F_COD_RESTRICAO_UNIDADE_PNEU
        ORDER BY ATUAL DESC, DATA_HORA_ALTERACAO DESC;
END;
$$;