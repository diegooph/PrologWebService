CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(F_COD_UNIDADES BIGINT[],
                                                                  F_STATUS_PNEU TEXT)
    RETURNS TABLE
            (
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               TEXT,
                DOT                          TEXT,
                VALOR                        REAL,
                COD_UNIDADE_ALOCADO          BIGINT,
                NOME_UNIDADE_ALOCADO         TEXT,
                COD_REGIONAL_ALOCADO         BIGINT,
                NOME_REGIONAL_ALOCADO        TEXT,
                PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
                COD_MARCA_PNEU               BIGINT,
                NOME_MARCA_PNEU              TEXT,
                COD_MODELO_PNEU              BIGINT,
                NOME_MODELO_PNEU             TEXT,
                QT_SULCOS_MODELO_PNEU        SMALLINT,
                COD_MARCA_BANDA              BIGINT,
                NOME_MARCA_BANDA             TEXT,
                ALTURA_SULCOS_MODELO_PNEU    REAL,
                COD_MODELO_BANDA             BIGINT,
                NOME_MODELO_BANDA            TEXT,
                QT_SULCOS_MODELO_BANDA       SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA   REAL,
                VALOR_BANDA                  REAL,
                ALTURA                       INTEGER,
                LARGURA                      INTEGER,
                ARO                          REAL,
                COD_DIMENSAO                 BIGINT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO         REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                PRESSAO_RECOMENDADA          REAL,
                PRESSAO_ATUAL                REAL,
                STATUS                       TEXT,
                VIDA_ATUAL                   INTEGER,
                VIDA_TOTAL                   INTEGER,
                POSICAO_PNEU                 INTEGER,
                POSICAO_APLICADO_CLIENTE     TEXT,
                COD_VEICULO_APLICADO         BIGINT,
                PLACA_APLICADO               TEXT,
                IDENTIFICADOR_FROTA          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT P.CODIGO,
       P.CODIGO_CLIENTE,
       P.DOT,
       P.VALOR,
       U.CODIGO                         AS COD_UNIDADE_ALOCADO,
       U.NOME                           AS NOME_UNIDADE_ALOCADO,
       R.CODIGO                         AS COD_REGIONAL_ALOCADO,
       R.REGIAO                         AS NOME_REGIONAL_ALOCADO,
       P.PNEU_NOVO_NUNCA_RODADO,
       MP.CODIGO                        AS COD_MARCA_PNEU,
       MP.NOME                          AS NOME_MARCA_PNEU,
       MOP.CODIGO                       AS COD_MODELO_PNEU,
       MOP.NOME                         AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                    AS QT_SULCOS_MODELO_PNEU,
       MAB.CODIGO                       AS COD_MARCA_BANDA,
       MAB.NOME                         AS NOME_MARCA_BANDA,
       MOP.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_PNEU,
       MOB.CODIGO                       AS COD_MODELO_BANDA,
       MOB.NOME                         AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                    AS QT_SULCOS_MODELO_BANDA,
       MOB.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_BANDA,
       PVV.VALOR                        AS VALOR_BANDA,
       PD.ALTURA,
       PD.LARGURA,
       PD.ARO,
       PD.CODIGO                        AS COD_DIMENSAO,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       P.PRESSAO_RECOMENDADA,
       P.PRESSAO_ATUAL,
       P.STATUS,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       VP.POSICAO                       AS POSICAO_PNEU,
       COALESCE(PPNE.NOMENCLATURA, '-') AS POSICAO_APLICADO,
       VEI.CODIGO                       AS COD_VEICULO,
       VEI.PLACA                        AS PLACA_APLICADO,
       VEI.IDENTIFICADOR_FROTA          AS IDENTIFICADOR_FROTA
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN VEICULO VEI ON VEI.CODIGO = VP.COD_VEICULO
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND P.STATUS LIKE F_STATUS_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;