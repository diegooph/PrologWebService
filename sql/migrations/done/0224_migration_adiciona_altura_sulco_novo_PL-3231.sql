DROP FUNCTION IF EXISTS  FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(F_COD_UNIDADE TEXT[], F_STATUS_PNEU TEXT);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(F_COD_UNIDADE TEXT[],
                                                                  F_STATUS_PNEU TEXT)
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
                "IDENTIFICADOR FROTA"   TEXT,
                "TIPO"                  TEXT,
                "POSIÇÃO"               TEXT,
                "QUANTIDADE DE SULCOS"  TEXT,
                "ALTURA DO SULCO NOVO"  TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO ATUAL (PSI)"   TEXT,
                "PRESSÃO IDEAL (PSI)"   TEXT,
                "VIDA ATUAL"            TEXT,
                "DOT"                   TEXT,
                "ÚLTIMA AFERIÇÃO"       TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                   AS UNIDADE_ALOCADO,
       P.CODIGO_CLIENTE                                                         AS COD_PNEU,
       P.STATUS                                                                 AS STATUS,
       COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                      AS VALOR_AQUISICAO,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                            AS DATA_HORA_CADASTRO,
       MAP.NOME                                                                 AS NOME_MARCA_PNEU,
       MP.NOME                                                                  AS NOME_MODELO_PNEU,
       CASE
           WHEN MARB.CODIGO IS NULL
               THEN 'NUNCA RECAPADO'
           ELSE MARB.NOME || ' - ' || MODB.NOME
           END                                                                  AS BANDA_APLICADA,
       COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                    AS VALOR_BANDA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO) AS MEDIDAS,
       COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                   AS PLACA,
       COALESCE(POSICAO_PNEU_VEICULO.IDENTIFICADOR_FROTA, '-')                  AS IDENTIFICADOR_FROTA,
       COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                         AS TIPO_VEICULO,
       COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-') :: TEXT                 AS POSICAO_PNEU,
       COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                           AS QTD_SULCOS,
       CASE P.VIDA_ATUAL
       WHEN 1 THEN
           FUNC_PNEU_FORMAT_SULCO(MP.ALTURA_SULCOS)
       ELSE
           FUNC_PNEU_FORMAT_SULCO(MODB.ALTURA_SULCOS) END                       AS ALTURA_SULCO_NOVO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                           AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                   AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                   AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                           AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                    AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                            AS PRESSAO_ATUAL,
       P.PRESSAO_RECOMENDADA :: TEXT                                            AS PRESSAO_RECOMENDADA,
       PVN.NOME :: TEXT                                                         AS VIDA_ATUAL,
       COALESCE(P.DOT, '-')                                                     AS DOT,
       COALESCE(
               TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_ULTIMA_AFERICAO),
                       'DD/MM/YYYY HH24:MI'), 'NUNCA AFERIDO')                  AS ULTIMA_AFERICAO
FROM PNEU P
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN (SELECT PPNE.NOMENCLATURA     AS POSICAO_PNEU,
                           VP.COD_PNEU           AS CODIGO_PNEU,
                           VP.PLACA              AS PLACA_VEICULO_PNEU,
                           VP.COD_UNIDADE        AS COD_UNIDADE_PNEU,
                           VT.NOME               AS VEICULO_TIPO,
                           V.IDENTIFICADOR_FROTA AS IDENTIFICADOR_FROTA
                    FROM VEICULO V
                             JOIN VEICULO_PNEU VP
                                  ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
                             JOIN VEICULO_TIPO VT
                                  ON V.COD_TIPO = VT.CODIGO
                             JOIN EMPRESA E ON VT.COD_EMPRESA = E.CODIGO
                        -- LEFT JOIN PORQUE UNIDADE PODE NÃO TER NOMENCLATURA.
                             LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                             LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                                       ON PPNE.COD_EMPRESA = E.CODIGO
                                           AND PPNE.COD_DIAGRAMA = VD.CODIGO
                                           AND VP.POSICAO = PPNE.POSICAO_PROLOG
                    WHERE V.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
                    ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
                   ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
         LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F
                   ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND CASE
          WHEN F_STATUS_PNEU IS NULL
              THEN TRUE
          ELSE P.STATUS = F_STATUS_PNEU
    END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;