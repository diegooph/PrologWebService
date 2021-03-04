-- Cria function para a listagem de colaboradores por múltiplas unidades
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADES(F_COD_UNIDADES BIGINT[], F_APENAS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO           BIGINT,
                NOME_COLABORADOR TEXT,
                CPF              BIGINT,
                COD_REGIONAL     BIGINT,
                NOME_REGIONAL    TEXT,
                COD_UNIDADE      BIGINT,
                NOME_UNIDADE     TEXT,
                COD_FUNCAO       BIGINT,
                NOME_FUNCAO      TEXT,
                COD_EQUIPE       BIGINT,
                NOME_EQUIPE      TEXT,
                COD_SETOR        BIGINT,
                NOME_SETOR       TEXT,
                MATRICULA_AMBEV  INTEGER,
                MATRICULA_TRANS  INTEGER,
                DATA_NASCIMENTO  DATE,
                STATUS_ATIVO     BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       C.CPF,
       R.CODIGO        AS COD_REGIONAL,
       R.REGIAO        AS NOME_REGIONAL,
       U.CODIGO        AS COD_UNIDADE,
       U.NOME          AS NOME_UNIDADE,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       EQ.CODIGO       AS COD_EQUIPE,
       EQ.NOME         AS NOME_EQUIPE,
       S.CODIGO        AS COD_SETOR,
       S.NOME          AS NOME_SETOR,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.STATUS_ATIVO
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_APENAS_ATIVOS IS NULL OR F_APENAS_ATIVOS IS FALSE
              THEN TRUE
          ELSE C.STATUS_ATIVO = F_APENAS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;

-- Dropa a function de listagem de veículos apenas por uma unidade
DROP FUNCTION FUNC_VEICULO_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_SOMENTE_ATIVOS BOOLEAN);
-- Cria function de listagem de veículos para múltiplas unidades.
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_ALL_BY_UNIDADES(F_COD_UNIDADES BIGINT[],
                                                            F_APENAS_ATIVOS BOOLEAN,
                                                            F_COD_TIPO_VEICULO BIGINT)
    RETURNS TABLE
            (
                CODIGO              BIGINT,
                PLACA               TEXT,
                COD_REGIONAL        BIGINT,
                NOME_REGIONAL       TEXT,
                COD_UNIDADE         BIGINT,
                NOME_UNIDADE        TEXT,
                KM                  BIGINT,
                STATUS_ATIVO        BOOLEAN,
                COD_TIPO            BIGINT,
                COD_MODELO          BIGINT,
                COD_DIAGRAMA        BIGINT,
                IDENTIFICADOR_FROTA TEXT,
                MODELO              TEXT,
                NOME_DIAGRAMA       TEXT,
                DIANTEIRO           BIGINT,
                TRASEIRO            BIGINT,
                TIPO                TEXT,
                MARCA               TEXT,
                COD_MARCA           BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT V.CODIGO                                                AS CODIGO,
       V.PLACA                                                 AS PLACA,
       R.CODIGO                                                AS COD_REGIONAL,
       R.REGIAO                                                AS NOME_REGIONAL,
       U.CODIGO                                                AS COD_UNIDADE,
       U.NOME                                                  AS NOME_UNIDADE,
       V.KM                                                    AS KM,
       V.STATUS_ATIVO                                          AS STATUS_ATIVO,
       V.COD_TIPO                                              AS COD_TIPO,
       V.COD_MODELO                                            AS COD_MODELO,
       V.COD_DIAGRAMA                                          AS COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA                                   AS IDENTIFICADOR_FROTA,
       MV.NOME                                                 AS MODELO,
       VD.NOME                                                 AS NOME_DIAGRAMA,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'D') AS DIANTEIRO,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'T') AS TRASEIRO,
       VT.NOME                                                 AS TIPO,
       MAV.NOME                                                AS MARCA,
       MAV.CODIGO                                              AS COD_MARCA
FROM VEICULO V
         JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = V.COD_DIAGRAMA
         JOIN VEICULO_DIAGRAMA_EIXOS VDE ON VDE.COD_DIAGRAMA = VD.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA
         JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_APENAS_ATIVOS IS NULL OR F_APENAS_ATIVOS = FALSE
              THEN TRUE
          ELSE V.STATUS_ATIVO = TRUE
    END
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NULL
              THEN TRUE
          ELSE V.COD_TIPO = F_COD_TIPO_VEICULO
    END
GROUP BY V.PLACA, V.CODIGO, V.CODIGO, V.PLACA, U.CODIGO, V.KM, V.STATUS_ATIVO, V.COD_TIPO, V.COD_MODELO,
         V.COD_DIAGRAMA, V.IDENTIFICADOR_FROTA, R.CODIGO, MV.NOME, VD.NOME, VT.NOME, MAV.NOME, MAV.CODIGO
ORDER BY V.PLACA;
$$;

-- Dropa function antiga de listagem de pneus.
DROP FUNCTION FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(F_COD_UNIDADE BIGINT, F_STATUS_PNEU TEXT);
-- Cria function que lista os pneus por status e códigos de unidades.
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
         LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
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

-- Atualiza dependências da FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS em outras do banco.
-- Dropa e recria a function FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE.
DROP FUNCTION FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE(F_COD_UNIDADE BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEUS_GET_LISTAGEM_PNEUS_MOVIMENTACOES_ANALISE(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
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
    IDENTIFICADOR_FROTA          TEXT,
    COD_MOVIMENTACAO             BIGINT,
    COD_RECAPADORA               BIGINT,
    NOME_RECAPADORA              TEXT,
    COD_EMPRESA_RECAPADORA       BIGINT,
    RECAPADORA_ATIVA             BOOLEAN,
    COD_COLETA                   TEXT)
LANGUAGE SQL
AS $$
WITH MOVIMENTACOES_ANALISE AS (
    SELECT
      INNER_TABLE.CODIGO                 AS COD_MOVIMENTACAO,
      INNER_TABLE.COD_PNEU               AS COD_PNEU,
      INNER_TABLE.COD_RECAPADORA_DESTINO AS COD_RECAPADORA,
      INNER_TABLE.NOME                   AS NOME_RECAPADORA,
      INNER_TABLE.COD_EMPRESA            AS COD_EMPRESA_RECAPADORA,
      INNER_TABLE.ATIVA                  AS RECAPADORA_ATIVA,
      INNER_TABLE.COD_COLETA             AS COD_COLETA
    FROM (SELECT
            MOV.CODIGO,
            MOV.COD_PNEU,
            MAX(MOV.CODIGO)
            OVER (
              PARTITION BY COD_PNEU ) AS MAX_COD_MOVIMENTACAO,
            MD.COD_RECAPADORA_DESTINO,
            REC.NOME,
            REC.COD_EMPRESA,
            REC.ATIVA,
            MD.COD_COLETA
          FROM MOVIMENTACAO AS MOV
            JOIN MOVIMENTACAO_DESTINO AS MD ON MOV.CODIGO = MD.COD_MOVIMENTACAO
            LEFT JOIN RECAPADORA AS REC ON MD.COD_RECAPADORA_DESTINO = REC.CODIGO
          WHERE COD_UNIDADE = F_COD_UNIDADE AND MD.TIPO_DESTINO = 'ANALISE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_MOVIMENTACAO
)

SELECT
  FUNC.*,
  MA.COD_MOVIMENTACAO,
  MA.COD_RECAPADORA,
  MA.NOME_RECAPADORA,
  MA.COD_EMPRESA_RECAPADORA,
  MA.RECAPADORA_ATIVA,
  MA.COD_COLETA
FROM FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(array[F_COD_UNIDADE], 'ANALISE') AS FUNC
  JOIN MOVIMENTACOES_ANALISE MA ON MA.COD_PNEU = FUNC.CODIGO;
$$;

-- Dropa e recria a FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA.
DROP FUNCTION FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(F_COD_UNIDADE BIGINT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_PNEUS_DISPONIVEIS_AFERICAO_AVULSA(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                CODIGO                                BIGINT,
                CODIGO_CLIENTE                        TEXT,
                DOT                                   TEXT,
                VALOR                                 REAL,
                COD_UNIDADE_ALOCADO                   BIGINT,
                NOME_UNIDADE_ALOCADO                  TEXT,
                COD_REGIONAL_ALOCADO                  BIGINT,
                NOME_REGIONAL_ALOCADO                 TEXT,
                PNEU_NOVO_NUNCA_RODADO                BOOLEAN,
                COD_MARCA_PNEU                        BIGINT,
                NOME_MARCA_PNEU                       TEXT,
                COD_MODELO_PNEU                       BIGINT,
                NOME_MODELO_PNEU                      TEXT,
                QT_SULCOS_MODELO_PNEU                 SMALLINT,
                COD_MARCA_BANDA                       BIGINT,
                NOME_MARCA_BANDA                      TEXT,
                ALTURA_SULCOS_MODELO_PNEU             REAL,
                COD_MODELO_BANDA                      BIGINT,
                NOME_MODELO_BANDA                     TEXT,
                QT_SULCOS_MODELO_BANDA                SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA            REAL,
                VALOR_BANDA                           REAL,
                ALTURA                                INTEGER,
                LARGURA                               INTEGER,
                ARO                                   REAL,
                COD_DIMENSAO                          BIGINT,
                ALTURA_SULCO_CENTRAL_INTERNO          REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO          REAL,
                ALTURA_SULCO_INTERNO                  REAL,
                ALTURA_SULCO_EXTERNO                  REAL,
                PRESSAO_RECOMENDADA                   REAL,
                PRESSAO_ATUAL                         REAL,
                STATUS                                TEXT,
                VIDA_ATUAL                            INTEGER,
                VIDA_TOTAL                            INTEGER,
                POSICAO_PNEU                          INTEGER,
                POSICAO_APLICADO_CLIENTE              TEXT,
                COD_VEICULO_APLICADO                  BIGINT,
                PLACA_APLICADO                        TEXT,
                IDENTIFICADOR_FROTA                   TEXT,
                JA_FOI_AFERIDO                        BOOLEAN,
                COD_ULTIMA_AFERICAO                   BIGINT,
                DATA_HORA_ULTIMA_AFERICAO             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO_ULTIMA_AFERICAO         TEXT,
                IDENTIFICADOR_FROTA_ULTIMA_AFERICAO   TEXT,
                TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO TEXT,
                TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO  TEXT,
                NOME_COLABORADOR_ULTIMA_AFERICAO      TEXT
            )
    LANGUAGE SQL
AS
$$
WITH AFERICOES AS (
    SELECT INNER_TABLE.CODIGO           AS COD_AFERICAO,
           INNER_TABLE.COD_PNEU         AS COD_PNEU,
           INNER_TABLE.DATA_HORA,
           INNER_TABLE.PLACA_VEICULO,
           INNER_TABLE.TIPO_MEDICAO_COLETADA,
           INNER_TABLE.TIPO_PROCESSO_COLETA,
           INNER_TABLE.NOME_COLABORADOR AS NOME_COLABORADOR,
           CASE
               WHEN INNER_TABLE.NOME_COLABORADOR IS NOT NULL
                   THEN TRUE
               ELSE FALSE END           AS JA_FOI_AFERIDO
    FROM (SELECT A.CODIGO,
                 AV.COD_PNEU,
                 A.DATA_HORA,
                 A.PLACA_VEICULO,
                 A.TIPO_MEDICAO_COLETADA,
                 A.TIPO_PROCESSO_COLETA,
                 C.NOME                      AS NOME_COLABORADOR,
                 MAX(A.CODIGO)
                 OVER (
                     PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
                   LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
                   LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
                   LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.COD_UNIDADE = F_COD_UNIDADE
            AND P.STATUS = 'ESTOQUE') AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT F.*,
       A.JA_FOI_AFERIDO                                   AS JA_FOI_AFERIDO,
       A.COD_AFERICAO                                     AS COD_ULTIMA_AFERICAO,
       A.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE) AS DATA_HORA_ULTIMA_AFERICAO,
       A.PLACA_VEICULO :: TEXT                            AS PLACA_VEICULO_ULTIMA_AFERICAO,
       V.IDENTIFICADOR_FROTA :: TEXT                      AS IDENTIFICADOR_FROTA_ULTIMA_AFERICAO,
       A.TIPO_MEDICAO_COLETADA :: TEXT                    AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
       A.TIPO_PROCESSO_COLETA :: TEXT                     AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
       A.NOME_COLABORADOR :: TEXT                         AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(array [F_COD_UNIDADE], 'ESTOQUE') AS F
         LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU
         LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO;
$$;