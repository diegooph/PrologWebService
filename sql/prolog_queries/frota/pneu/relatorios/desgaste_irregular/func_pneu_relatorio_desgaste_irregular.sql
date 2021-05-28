CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(F_COD_UNIDADES BIGINT[],
                                                                  F_STATUS_PNEU PNEU_STATUS_TYPE DEFAULT NULL)
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"       TEXT,
                "PNEU"                  TEXT,
                "STATUS"                TEXT,
                "VALOR DE AQUISIÇÃO"    TEXT,
                "DATA/HORA CADASTRO"    TEXT,
                "MARCA"                 TEXT,
                "MODELO"                TEXT,
                "BANDA APLICADA"        TEXT,
                "VALOR DA BANDA"        TEXT,
                "MEDIDAS"               TEXT,
                "PLACA"                 TEXT,
                "TIPO"                  TEXT,
                "POSIÇÃO"               TEXT,
                "QUANTIDADE DE SULCOS"  TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO ATUAL (PSI)"   TEXT,
                "PRESSÃO IDEAL (PSI)"   TEXT,
                "VIDA ATUAL"            TEXT,
                "DOT"                   TEXT,
                "ÚLTIMA AFERIÇÃO"       TEXT,
                "DESCRIÇÃO DESGASTE"    TEXT,
                "NÍVEL DE DESGASTE"     TEXT,
                "APARÊNCIA PNEU"        TEXT,
                "CAUSAS PROVÁVEIS"      TEXT,
                "AÇÃO"                  TEXT,
                "PRECAUÇÃO"             TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_TIMESTAMP_FORMAT TEXT := 'DD/MM/YYYY HH24:MI';
BEGIN
    RETURN QUERY
        -- Essa CTE busca o código da última aferição de cada pneu.
        -- Com o código nós conseguimos buscar depois a data/hora da aferição e o código da unidade em que ocorreu,
        -- para aplicar o TZ correto.
        WITH ULTIMAS_AFERICOES AS (
            SELECT AV.COD_PNEU   AS COD_PNEU_AFERIDO,
                   MAX(A.CODIGO) AS COD_AFERICAO
            FROM AFERICAO A
                     JOIN AFERICAO_VALORES AV
                          ON AV.COD_AFERICAO = A.CODIGO
                     JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
            WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
            GROUP BY AV.COD_PNEU
        )

        SELECT U.NOME :: TEXT                                                               AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                                                     AS COD_PNEU,
               P.STATUS :: TEXT                                                             AS STATUS,
               COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                          AS VALOR_AQUISICAO,
               FORMAT_WITH_TZ(P.DATA_HORA_CADASTRO,
                              TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                              F_TIMESTAMP_FORMAT,
                              '-')                                                          AS DATA_HORA_CADASTRO,
               MAP.NOME :: TEXT                                                             AS NOME_MARCA_PNEU,
               MP.NOME :: TEXT                                                              AS NOME_MODELO_PNEU,
               F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME) AS BANDA_APLICADA,
               COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                        AS VALOR_BANDA,
               FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO)                     AS MEDIDAS,
               COALESCE(V.PLACA, '-') :: TEXT                                              AS PLACA,
               COALESCE(VT.NOME, '-') :: TEXT                                               AS TIPO_VEICULO,
               COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                     AS POSICAO_PNEU,
               COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                               AS QTD_SULCOS,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                            P.ALTURA_SULCO_INTERNO))                        AS MENOR_SULCO,
               COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                AS PRESSAO_ATUAL,
               P.PRESSAO_RECOMENDADA :: TEXT                                                AS PRESSAO_RECOMENDADA,
               PVN.NOME :: TEXT                                                             AS VIDA_ATUAL,
               COALESCE(P.DOT, '-') :: TEXT                                                 AS DOT,
               -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
               -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
               CASE
                   WHEN UA.COD_AFERICAO IS NULL
                       THEN 'Nunca Aferido'
                   ELSE
                       FORMAT_WITH_TZ((SELECT A.DATA_HORA
                                       FROM AFERICAO A
                                       WHERE A.CODIGO = UA.COD_AFERICAO),
                                      TZ_UNIDADE((SELECT A.COD_UNIDADE
                                                  FROM AFERICAO A
                                                  WHERE A.CODIGO = UA.COD_AFERICAO)),
                                      F_TIMESTAMP_FORMAT)
                   END                                                                      AS ULTIMA_AFERICAO,
               PTDI.DESCRICAO                                                               AS DESCRICAO_DESGASTE,
               -- Por enquanto, deixamos hardcoded os ranges de cada nível de desgaste.
               CASE
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'BAIXO'
                       THEN 'BAIXO (0.1 mm até 0.9 mm)'
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'MODERADO'
                       THEN 'MODERADO (1.0 mm até 2.0 mm)'
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'ACENTUADO'
                       THEN 'ACENTUADO (2.1 mm e acima)'
                   END                                                                      AS NIVEL_DESGASTE,
               PTDI.APARENCIA_PNEU                                                          AS APARENCIA_PNEU,
               PTDI.CAUSAS_PROVAVEIS                                                        AS CAUSAS_PROVAVEIS,
               PTDI.ACAO                                                                    AS ACAO,
               PTDI.PRECAUCAO                                                               AS PRECAUCAO
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
                 JOIN FUNC_PNEU_VERIFICA_DESGASTE_IRREGULAR(P.CODIGO,
                                                            P.ALTURA_SULCO_EXTERNO,
                                                            P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                                            P.ALTURA_SULCO_INTERNO) VERIF_DESGASTE
                      ON VERIF_DESGASTE.COD_PNEU = P.CODIGO
                 LEFT JOIN PNEU_TIPO_DESGASTE_IRREGULAR PTDI
                           ON PTDI.TIPO_DESGASTE_IRREGULAR = VERIF_DESGASTE.TIPO_DESGASTE_IRREGULAR
                 LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
                 LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
                 LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.COD_VEICULO = V.CODIGO
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                    AND PPNE.COD_DIAGRAMA = VD.CODIGO
                    AND PPNE.POSICAO_PROLOG = VP.POSICAO
                 LEFT JOIN ULTIMAS_AFERICOES UA
                           ON UA.COD_PNEU_AFERIDO = P.CODIGO
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND F_IF(F_STATUS_PNEU IS NULL, TRUE, F_STATUS_PNEU = P.STATUS :: PNEU_STATUS_TYPE)
          AND VERIF_DESGASTE.TEM_DESGASTE_IRREGULAR
        ORDER BY VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR DESC, U.NOME, P.CODIGO_CLIENTE;
END;
$$;