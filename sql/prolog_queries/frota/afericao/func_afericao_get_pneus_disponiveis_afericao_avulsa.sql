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