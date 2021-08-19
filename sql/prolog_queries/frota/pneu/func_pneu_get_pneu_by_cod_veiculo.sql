CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_COD_VEICULO(F_COD_VEICULO BIGINT)
    RETURNS TABLE
            (
                NOME_MARCA_PNEU              VARCHAR(255),
                COD_MARCA_PNEU               BIGINT,
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               VARCHAR(255),
                COD_UNIDADE_ALOCADO          BIGINT,
                COD_REGIONAL_ALOCADO         BIGINT,
                PRESSAO_ATUAL                REAL,
                VIDA_ATUAL                   INTEGER,
                VIDA_TOTAL                   INTEGER,
                PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
                NOME_MODELO_PNEU             VARCHAR(255),
                COD_MODELO_PNEU              BIGINT,
                QT_SULCOS_MODELO_PNEU        SMALLINT,
                ALTURA_SULCOS_MODELO_PNEU    REAL,
                ALTURA                       NUMERIC,
                LARGURA                      NUMERIC,
                ARO                          NUMERIC,
                COD_DIMENSAO                 BIGINT,
                PRESSAO_RECOMENDADA          REAL,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO         REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                DOT                          VARCHAR(20),
                VALOR                        REAL,
                COD_MODELO_BANDA             BIGINT,
                NOME_MODELO_BANDA            VARCHAR(255),
                QT_SULCOS_MODELO_BANDA       SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA   REAL,
                COD_MARCA_BANDA              BIGINT,
                NOME_MARCA_BANDA             VARCHAR(255),
                VALOR_BANDA                  REAL,
                POSICAO_PNEU                 INTEGER,
                NOMENCLATURA                 VARCHAR(255),
                COD_VEICULO_APLICADO         BIGINT,
                PLACA_APLICADO               VARCHAR(7)
            )
    LANGUAGE SQL
AS
$$
SELECT MP.NOME                                  AS NOME_MARCA_PNEU,
       MP.CODIGO                                AS COD_MARCA_PNEU,
       P.CODIGO,
       P.CODIGO_CLIENTE,
       U.CODIGO                                 AS COD_UNIDADE_ALOCADO,
       R.CODIGO                                 AS COD_REGIONAL_ALOCADO,
       P.PRESSAO_ATUAL,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       P.PNEU_NOVO_NUNCA_RODADO,
       MOP.NOME                                 AS NOME_MODELO_PNEU,
       MOP.CODIGO                               AS COD_MODELO_PNEU,
       MOP.QT_SULCOS                            AS QT_SULCOS_MODELO_PNEU,
       MOP.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_PNEU,
       PD.ALTURA,
       PD.LARGURA,
       PD.ARO,
       PD.CODIGO                                AS COD_DIMENSAO,
       P.PRESSAO_RECOMENDADA,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       COALESCE(P.DOT :: TEXT, '-')             AS DOT,
       P.VALOR,
       MOB.CODIGO                               AS COD_MODELO_BANDA,
       MOB.NOME                                 AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
       MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
       MAB.CODIGO                               AS COD_MARCA_BANDA,
       MAB.NOME                                 AS NOME_MARCA_BANDA,
       PVV.VALOR                                AS VALOR_BANDA,
       PO.POSICAO_PROLOG                        AS POSICAO_PNEU,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS NOMENCLATURA,
       VEI.CODIGO                               AS COD_VEICULO_APLICADO,
       VEI.PLACA                                AS PLACA_APLICADO
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
         LEFT JOIN VEICULO VEI ON VEI.CODIGO = VP.COD_VEICULO
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = P.COD_EMPRESA
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON
        PPNE.COD_EMPRESA = P.COD_EMPRESA AND
        PPNE.COD_DIAGRAMA = VD.CODIGO AND
        PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE VEI.CODIGO = F_COD_VEICULO
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;