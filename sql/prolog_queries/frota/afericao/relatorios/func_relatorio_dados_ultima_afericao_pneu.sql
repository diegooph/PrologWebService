-- Sobre:
--
-- Esta function retorna os dados dos pneus em sua última aferição
--
-- Précondições:
-- 1) Function: FUNC_PNEU_FORMAT_SULCO criada.
-- 2) Function: TZ_UNIDADE criada.
--
-- Histórico:
-- 2019-08-28 -> Adicionada coluna com o menor sulco (wvinim - PL-2169).
-- 2019-09-09 -> Altera vínculo da tabela PNEU_ORDEM_NOMENCLATURA_UNIDADE para PNEU_POSICAO_NOMENCLATURA_EMPRESA.
--               (thaisksf - PL-2258)
-- 2020-06-17 -> Adiciona identificador frota ao relatório. (thaisksf - PL-2760)
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT[])
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"               TEXT,
                "PNEU"                          TEXT,
                "STATUS ATUAL"                  TEXT,
                "MARCA PNEU"                    TEXT,
                "MODELO PNEU"                   TEXT,
                "MEDIDAS"                       TEXT,
                "PLACA APLICADO"                TEXT,
                "IDENTIFICADOR FROTA"           TEXT,
                "MARCA VEÍCULO"                 TEXT,
                "MODELO VEÍCULO"                TEXT,
                "TIPO VEÍCULO"                  TEXT,
                "POSIÇÃO APLICADO"              TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "VIDA ATUAL"                    TEXT,
                "DOT"                           TEXT,
                "ÚLTIMA AFERIÇÃO"               TEXT,
                "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT,
                "FORMA DE COLETA DOS DADOS"     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Essa CTE busca o código da última aferição de cada pneu.
    -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
    RETURN QUERY
        WITH CODS_AFERICOES AS (
            SELECT AV.COD_PNEU   AS COD_PNEU_AFERIDO,
                   MAX(A.CODIGO) AS COD_AFERICAO
            FROM AFERICAO A
                     JOIN AFERICAO_VALORES AV
                          ON AV.COD_AFERICAO = A.CODIGO
                     JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
            WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
            GROUP BY AV.COD_PNEU
        ),

             ULTIMAS_AFERICOES AS (
                 SELECT CA.COD_PNEU_AFERIDO    AS COD_PNEU_AFERIDO,
                        A.DATA_HORA            AS DATA_HORA_AFERICAO,
                        A.COD_UNIDADE          AS COD_UNIDADE_AFERICAO,
                        A.TIPO_PROCESSO_COLETA AS TIPO_PROCESSO_COLETA,
                        A.FORMA_COLETA_DADOS   AS FORMA_COLETA_DADOS
                 FROM CODS_AFERICOES CA
                          JOIN AFERICAO A ON A.CODIGO = CA.COD_AFERICAO)

        SELECT U.NOME :: TEXT                                                   AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                                         AS COD_PNEU,
               P.STATUS :: TEXT                                                 AS STATUS_ATUAL,
               MAP.NOME :: TEXT                                                 AS NOME_MARCA,
               MP.NOME :: TEXT                                                  AS NOME_MODELO,
               ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
                DP.ARO)                                                         AS MEDIDAS,
               COALESCE(VP.PLACA, '-') :: TEXT                                  AS PLACA,
               COALESCE(V.IDENTIFICADOR_FROTA, '-') :: TEXT                     AS IDENTIFICADOR_FROTA,
               COALESCE(MARV.NOME, '-') :: TEXT                                 AS MARCA_VEICULO,
               COALESCE(MODV.NOME, '-') :: TEXT                                 AS MODELO_VEICULO,
               COALESCE(VT.NOME, '-') :: TEXT                                   AS TIPO_VEICULO,
               COALESCE(PPNE.NOMENCLATURA:: TEXT, '-')                          AS POSICAO_PNEU,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                   AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)           AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)           AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                   AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                            P.ALTURA_SULCO_INTERNO))            AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',') AS PRESSAO_ATUAL,
               P.VIDA_ATUAL :: TEXT                                             AS VIDA_ATUAL,
               COALESCE(P.DOT, '-') :: TEXT                                     AS DOT,
               COALESCE(TO_CHAR(UA.DATA_HORA_AFERICAO AT TIME ZONE
                                tz_unidade(UA.COD_UNIDADE_AFERICAO),
                                'DD/MM/YYYY HH24:MI'),
                        'Nunca Aferido')                                        AS ULTIMA_AFERICAO,
               CASE
                   WHEN UA.TIPO_PROCESSO_COLETA IS NULL
                       THEN 'Nunca Aferido'
                   WHEN UA.TIPO_PROCESSO_COLETA = 'PLACA'
                       THEN 'Aferido em uma placa'
                   ELSE 'Aferido Avulso (em estoque)' END                       AS TIPO_PROCESSO_ULTIMA_AFERICAO,
               COALESCE(TAFCD.STATUS_LEGIVEL, '-')::TEXT                        AS FORMA_COLETA_DADOS
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
                 LEFT JOIN MODELO_VEICULO MODV
                           ON MODV.CODIGO = V.COD_MODELO
                 LEFT JOIN MARCA_VEICULO MARV
                           ON MARV.CODIGO = MODV.COD_MARCA
                 LEFT JOIN ULTIMAS_AFERICOES UA
                           ON UA.COD_PNEU_AFERIDO = P.CODIGO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD
                           ON TAFCD.FORMA_COLETA_DADOS::TEXT = UA.FORMA_COLETA_DADOS::TEXT
        WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
        ORDER BY U.NOME, P.CODIGO_CLIENTE;
END;
$$;