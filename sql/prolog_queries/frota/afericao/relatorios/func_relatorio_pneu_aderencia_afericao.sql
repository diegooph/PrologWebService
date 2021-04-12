CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_ADERENCIA_AFERICAO(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE ALOCADA"                          TEXT,
                "PLACA"                                    CHARACTER VARYING,
                "IDENTIFICADOR FROTA"                      TEXT,
                "QT AFERIÇÕES DE PRESSÃO"                  BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
                "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META"  BIGINT,
                "ADERÊNCIA AFERIÇÕES DE PRESSÃO"           TEXT,
                "QT AFERIÇÕES DE SULCO"                    BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO"   TEXT,
                "QTD AFERIÇÕES DE SULCO DENTRO DA META"    BIGINT,
                "ADERÊNCIA AFERIÇÕES DE SULCO"             TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                               AS "UNIDADE ALOCADA",
       V.PLACA                              AS PLACA,
       COALESCE(V.IDENTIFICADOR_FROTA, '-') AS IDENTIFICADOR_FROTA,
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_PRESSAO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_PRESSAO.ADERENCIA, '0%'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_SULCO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_SULCO.ADERENCIA, '0%')
FROM VEICULO V
         JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
         LEFT JOIN (SELECT CALCULO_AFERICAO_PRESSAO.PLACA,
                           COUNT(CALCULO_AFERICAO_PRESSAO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END)::TEXT
                               ELSE '-' END                      AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                   AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)::NUMERIC * 100) ||
                           '%'                                   AS ADERENCIA
                    FROM (SELECT V.PLACA                AS PLACA,
                                 A.DATA_HORA            AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     END                    AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_PRESSAO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE >= F_DATA_INICIAL::DATE
                      AND (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO
                   ON CALCULO_PRESSAO.PLACA = V.PLACA
         LEFT JOIN (SELECT CALCULO_AFERICAO_SULCO.PLACA,
                           COUNT(CALCULO_AFERICAO_SULCO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END) :: TEXT
                               ELSE '-' END                    AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                 AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_SULCO.PLACA)::NUMERIC * 100) ||
                           '%'                                 AS ADERENCIA
                    FROM (SELECT V.PLACA          AS PLACA,
                                 A.DATA_HORA      AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_SULCO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     ELSE 0
                                     END                  AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_SULCO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) >= F_DATA_INICIAL::DATE
                      AND CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
                   ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
  AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;