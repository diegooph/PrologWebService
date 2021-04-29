-- PL-2843.
DROP FUNCTION FUNC_RELATORIO_PNEUS_DESCARTADOS(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE);

-- 2020-08-31 -> Adicionada informações da origem do descarte (origem, placa e posição) (luiz_fp - PL-2843).
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEUS_DESCARTADOS(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO DESCARTE"               TEXT,
                "RESPONSÁVEL PELO DESCARTE"         TEXT,
                "DATA/HORA DO DESCARTE"             TEXT,
                "CÓDIGO DO PNEU"                    TEXT,
                "MARCA DO PNEU"                     TEXT,
                "MODELO DO PNEU"                    TEXT,
                "MARCA DA BANDA"                    TEXT,
                "MODELO DA BANDA"                   TEXT,
                "DIMENSÃO DO PNEU"                  TEXT,
                "ÚLTIMA PRESSÃO"                    TEXT,
                "ORIGEM DESCARTE"                   TEXT,
                "PLACA APLICADO MOMENTO DESCARTE"   TEXT,
                "POSIÇÃO APLICADO MOMENTO DESCARTE" TEXT,
                "TOTAL DE VIDAS"                    TEXT,
                "ALTURA SULCO INTERNO"              TEXT,
                "ALTURA SULCO CENTRAL INTERNO"      TEXT,
                "ALTURA SULCO CENTRAL EXTERNO"      TEXT,
                "ALTURA SULCO EXTERNO"              TEXT,
                "MENOR SULCO"                       TEXT,
                "DOT"                               TEXT,
                "MOTIVO DO DESCARTE"                TEXT,
                "FOTO 1"                            TEXT,
                "FOTO 2"                            TEXT,
                "FOTO 3"                            TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                          AS UNIDADE_DO_DESCARTE,
       C.NOME                                                                          AS RESPONSAVEL_PELO_DESCARTE,
       TO_CHAR(MP.DATA_HORA AT TIME ZONE tz_unidade(P.COD_UNIDADE),
               'DD/MM/YYYY HH24:MI')                                                   AS DATA_HORA_DESCARTE,
       P.CODIGO_CLIENTE                                                                AS CODIGO_PNEU,
       MAP.NOME                                                                        AS MARCA_PNEU,
       MOP.NOME                                                                        AS MODELO_PNEU,
       MAB.NOME                                                                        AS MARCA_BANDA,
       MOB.NOME                                                                        AS MODELO_BANDA,
       'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' || DP.ARO AS DIMENSAO_PNEU,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                AS ULTIMA_PRESSAO,
       MO.TIPO_ORIGEM                                                                  AS ORIGEM_DESCARTE,
       COALESCE(MO.PLACA :: TEXT, '-')                                                 AS PLACA_APLICADO_MOMENTO_DESCARTE,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                        AS POSICAO_APLICADO_MOMENTO_DESCARTE,
       P.VIDA_ATUAL :: TEXT                                                            AS TOTAL_VIDAS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                  AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                          AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                          AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                  AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                           AS MENOR_SULCO,
       P.DOT                                                                           AS DOT,
       MMDE.MOTIVO                                                                     AS MOTIVO_DESCARTE,
       MD.URL_IMAGEM_DESCARTE_1                                                        AS FOTO_1,
       MD.URL_IMAGEM_DESCARTE_2                                                        AS FOTO_2,
       MD.URL_IMAGEM_DESCARTE_3                                                        AS FOTO_3
FROM PNEU P
         JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
         JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
         LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
         LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
         LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
         LEFT JOIN MOVIMENTACAO_ORIGEM MO ON M.CODIGO = MO.COD_MOVIMENTACAO
         LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
         LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
         LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
                   ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_DIAGRAMA = MO.COD_DIAGRAMA
                          AND PPNE.POSICAO_PROLOG = MO.POSICAO_PNEU_ORIGEM
                          AND PPNE.COD_EMPRESA = U.COD_EMPRESA
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND P.STATUS = 'DESCARTE'
  AND M.COD_PNEU = P.CODIGO
  AND MD.TIPO_DESTINO = 'DESCARTE'
  AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME;
$$;