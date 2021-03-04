-- 2020-06-17 -> Adiciona identificador de frota ao cronograma de aferições. (thaistks - PL-2760)
DROP FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                            TEXT,
                IDENTIFICADOR_FROTA              TEXT,
                COD_UNIDADE_PLACA                BIGINT,
                NOME_MODELO                      TEXT,
                INTERVALO_PRESSAO                INTEGER,
                INTERVALO_SULCO                  INTEGER,
                PERIODO_AFERICAO_SULCO           INTEGER,
                PERIODO_AFERICAO_PRESSAO         INTEGER,
                PNEUS_APLICADOS                  INTEGER,
                STATUS_ATIVO_TIPO_VEICULO        BOOLEAN,
                FORMA_COLETA_DADOS_SULCO         TEXT,
                FORMA_COLETA_DADOS_PRESSAO       TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                PODE_AFERIR_ESTEPE               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                              AS PLACA,
               V.IDENTIFICADOR_FROTA ::TEXT                                 AS IDENTIFICADOR_FROTA,
               V.COD_UNIDADE :: BIGINT                                      AS COD_UNIDADE_PLACA,
               MV.NOME :: TEXT                                              AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER         AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER           AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                   AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                 AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                   AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE
        FROM VEICULO V
                 JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.
                                                        COD_UNIDADE = V.COD_UNIDADE
                 JOIN VEICULO_TIPO VT ON VT.
                                             CODIGO = V.COD_TIPO
                 JOIN MODELO_VEICULO MV ON MV.
                                               CODIGO = V.COD_MODELO
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.
                                  COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_PRESSAO
                           ON INTERVALO_PRESSAO.
                                  PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_SULCO
                           ON INTERVALO_SULCO.
                                  PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT VP.PLACA           AS PLACA_PNEUS,
                                   COUNT(VP.COD_PNEU) AS TOTAL
                            FROM VEICULO_PNEU VP
                            WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES)
                            GROUP BY VP.PLACA) AS NUMERO_PNEUS ON
            PLACA_PNEUS = V.PLACA
        WHERE V.STATUS_ATIVO = TRUE
          AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY MV.NOME, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC, PNEUS_APLICADOS DESC;
END;
$$;

-- 2020-06-17 -> Adiciona identificador frota ao relatório. (thaisksf PL-2760)
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT[]);
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

-- 2020-06-18 -> Adiciona identificador de frota ao relatório. (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                               F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
                                                                               F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                              TEXT,
                PLACA                                TEXT,
                "IDENTIFICADOR FROTA"                TEXT,
                "QTD PNEUS APLICADOS"                TEXT,
                "MODELO VEÍCULO"                     TEXT,
                "TIPO VEÍCULO"                       TEXT,
                "STATUS SULCO"                       TEXT,
                "STATUS PRESSÃO"                     TEXT,
                "DATA VENCIMENTO SULCO"              TEXT,
                "DATA VENCIMENTO PRESSÃO"            TEXT,
                "DIAS VENCIMENTO SULCO"              TEXT,
                "DIAS VENCIMENTO PRESSÃO"            TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
                "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        WITH DADOS AS (SELECT U.NOME :: TEXT                                                       AS NOME_UNIDADE,
                              V.PLACA :: TEXT                                                      AS PLACA_VEICULO,
                              COALESCE(V.IDENTIFICADOR_FROTA :: TEXT, '-')                         AS IDENTIFICADOR_FROTA,
                              (SELECT COUNT(VP.COD_PNEU)
                               FROM VEICULO_PNEU VP
                               WHERE VP.PLACA = V.PLACA
                               GROUP BY VP.PLACA) :: TEXT                                          AS QTD_PNEUS_APLICADOS,
                              MV.NOME :: TEXT                                                      AS NOME_MODELO_VEICULO,
                              VT.NOME :: TEXT                                                      AS NOME_TIPO_VEICULO,
                              TO_CHAR(SULCO.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                              TO_CHAR(PRESSAO.DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                                      'DD/MM/YYYY HH24:MI')                                        AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                              TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO + (PRU.PERIODO_AFERICAO_SULCO ||
                                                                          ' DAYS') :: INTERVAL,
                                      'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_SULCO,
                              TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO + (PRU.PERIODO_AFERICAO_PRESSAO ||
                                                                              ' DAYS') :: INTERVAL,
                                      'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_PRESSAO,
                              (PRU.PERIODO_AFERICAO_SULCO -
                               SULCO.DIAS) :: TEXT                                                 AS DIAS_VENCIMENTO_SULCO,
                              (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS) :: TEXT
                                                                                                   AS DIAS_VENCIMENTO_PRESSAO,
                              SULCO.DIAS :: TEXT                                                   AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                              PRESSAO.DIAS :: TEXT                                                 AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                              F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL')
                                       OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'), TRUE,
                                   FALSE)                                                          AS PODE_AFERIR_SULCO,
                              F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL')
                                       OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'), TRUE,
                                   FALSE)                                                          AS PODE_AFERIR_PRESSAO,
                              F_IF(SULCO.DIAS IS NULL, TRUE,
                                   FALSE)                                                          AS SULCO_NUNCA_AFERIDO,
                              F_IF(PRESSAO.DIAS IS NULL, TRUE,
                                   FALSE)                                                          AS PRESSAO_NUNCA_AFERIDA,
                              F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                                   FALSE)                                                          AS AFERICAO_SULCO_VENCIDA,
                              F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                                   FALSE)                                                          AS AFERICAO_PRESSAO_VENCIDA
                       FROM VEICULO V
                                JOIN MODELO_VEICULO MV
                                     ON MV.CODIGO = V.COD_MODELO
                                JOIN VEICULO_TIPO VT
                                     ON VT.CODIGO = V.COD_TIPO
                                JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                                     ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                                LEFT JOIN
                            (SELECT A.PLACA_VEICULO                                               AS PLACA_INTERVALO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                        AS DATA_ULTIMA_AFERICAO_PRESSAO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                                    EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                             FROM AFERICAO A
                             WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                                OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                             GROUP BY A.PLACA_VEICULO) AS PRESSAO ON PRESSAO.PLACA_INTERVALO = V.PLACA
                                LEFT JOIN
                            (SELECT A.PLACA_VEICULO                                             AS PLACA_INTERVALO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                                    EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                             FROM AFERICAO A
                             WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                                OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                             GROUP BY A.PLACA_VEICULO) AS SULCO ON SULCO.PLACA_INTERVALO = V.PLACA
                                JOIN PNEU_RESTRICAO_UNIDADE PRU
                                     ON PRU.COD_UNIDADE = V.COD_UNIDADE
                                JOIN UNIDADE U
                                     ON U.CODIGO = V.COD_UNIDADE
                       WHERE V.STATUS_ATIVO = TRUE
                         AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                       ORDER BY U.CODIGO ASC, V.PLACA ASC)

             -- Todos os coalesce ficam aqui.
        SELECT D.NOME_UNIDADE                                               AS NOME_UNIDADE,
               D.PLACA_VEICULO                                              AS PLACA_VEICULO,
               D.IDENTIFICADOR_FROTA                                        AS IDENTIFICADOR_FROTA,
               COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
               D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
               D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
               CASE
                   WHEN D.SULCO_NUNCA_AFERIDO
                       THEN 'SULCO NUNCA AFERIDO'
                   WHEN NOT D.PODE_AFERIR_SULCO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.AFERICAO_SULCO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_SULCO,
               CASE
                   WHEN D.PRESSAO_NUNCA_AFERIDA
                       THEN 'PRESSÃO NUNCA AFERIDA'
                   WHEN NOT D.PODE_AFERIR_PRESSAO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.AFERICAO_PRESSAO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
               D.DATA_HORA_ULTIMA_AFERICAO_SULCO                            AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
               D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO                          AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
               TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
        FROM DADOS D;
END;
$$;

-- 2020-06-18 -> Adiciona identificador de frota ao relatório. (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                F_DATA_INICIAL DATE,
                                                                F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "CÓDIGO AFERIÇÃO"           TEXT,
                "UNIDADE"                   TEXT,
                "DATA E HORA"               TEXT,
                "CPF DO RESPONSÁVEL"        TEXT,
                "NOME COLABORADOR"          TEXT,
                "PNEU"                      TEXT,
                "STATUS ATUAL"              TEXT,
                "VALOR COMPRA"              TEXT,
                "MARCA DO PNEU"             TEXT,
                "MODELO DO PNEU"            TEXT,
                "QTD SULCOS MODELO"         TEXT,
                "VIDA ATUAL"                TEXT,
                "VALOR VIDA ATUAL"          TEXT,
                "BANDA APLICADA"            TEXT,
                "QTD SULCOS BANDA"          TEXT,
                "DIMENSÃO"                  TEXT,
                "DOT"                       TEXT,
                "DATA E HORA CADASTRO"      TEXT,
                "POSIÇÃO PNEU"              TEXT,
                "PLACA"                     TEXT,
                "IDENTIFICADOR FROTA"       TEXT,
                "VIDA MOMENTO AFERIÇÃO"     TEXT,
                "KM NO MOMENTO DA AFERIÇÃO" TEXT,
                "KM ATUAL"                  TEXT,
                "MARCA DO VEÍCULO"          TEXT,
                "MODELO DO VEÍCULO"         TEXT,
                "TIPO DE MEDIÇÃO COLETADA"  TEXT,
                "TIPO DA AFERIÇÃO"          TEXT,
                "TEMPO REALIZAÇÃO (MM:SS)"  TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT,
                "FORMA DE COLETA DOS DADOS" TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO :: TEXT                                                                 AS COD_AFERICAO,
       U.NOME                                                                           AS UNIDADE,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI')                                                    AS DATA_HORA_AFERICAO,
       LPAD(C.CPF :: TEXT, 11, '0')                                                     AS CPF_COLABORADOR,
       C.NOME                                                                           AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                 AS CODIGO_CLIENTE_PNEU,
       P.STATUS                                                                         AS STATUS_ATUAL_PNEU,
       ROUND(P.VALOR :: NUMERIC, 2) :: TEXT                                             AS VALOR_COMPRA,
       MAP.NOME                                                                         AS MARCA_PNEU,
       MP.NOME                                                                          AS MODELO_PNEU,
       MP.QT_SULCOS :: TEXT                                                             AS QTD_SULCOS_MODELO,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                              AS VIDA_ATUAL,
       COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                            AS VALOR_VIDA_ATUAL,
       F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado') AS BANDA_APLICADA,
       COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                            AS QTD_SULCOS_BANDA,
       DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                  AS DIMENSAO,
       P.DOT                                                                            AS DOT,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    AS DATA_HORA_CADASTRO,
       COALESCE(PPNE.NOMENCLATURA, '-')                                                 AS POSICAO,
       COALESCE(A.PLACA_VEICULO :: TEXT, '-')                                                   AS PLACA,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                             AS IDENTIFICADOR_FROTA,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                  AS VIDA_MOMENTO_AFERICAO,
       COALESCE(A.KM_VEICULO :: TEXT, '-')                                              AS KM_MOMENTO_AFERICAO,
       COALESCE(V.KM :: TEXT, '-')                                                      AS KM_ATUAL,
       COALESCE(M2.NOME, '-')                                                           AS MARCA_VEICULO,
       COALESCE(MV.NOME, '-')                                                           AS MODELO_VEICULO,
       A.TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA,
       TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')            AS TEMPO_REALIZACAO_MINUTOS,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                  AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                          AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                          AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                  AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                           AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')            AS PRESSAO,
       COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
         JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
         JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
         JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD ON TAFCD.FORMA_COLETA_DADOS = A.FORMA_COLETA_DADOS::TEXT


    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Se foi aferição de pneu avulso, pode não possuir placa.
         LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO

         LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT ON E.CODIGO = VT.COD_EMPRESA AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN MODELO_VEICULO MV
                   ON MV.CODIGO = V.COD_MODELO
         LEFT JOIN MARCA_VEICULO M2
                   ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;

-- 2020-06-18 -> Adiciona identificador de frota ao relatório. (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[], F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
                                                                            F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                           TEXT,
                PLACA                             TEXT,
                IDENTIFICADOR_FROTA               TEXT,
                PODE_AFERIR_SULCO                 BOOLEAN,
                PODE_AFERIR_PRESSAO               BOOLEAN,
                QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
                QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    AFERICAO_SULCO         VARCHAR := 'SULCO';
    AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
    AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
    RETURN QUERY
        WITH VEICULOS_ATIVOS_UNIDADES AS (
            SELECT V.PLACA
            FROM VEICULO V
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND V.STATUS_ATIVO
        ),
             -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam a placa de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
             -- que a placa vai vencer em 20 dias.
             ULTIMA_AFERICAO_SULCO AS (
                 SELECT DISTINCT ON (A.PLACA_VEICULO) A.COD_UNIDADE,
                                                      A.PLACA_VEICULO              AS PLACA,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
                   -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.PLACA_VEICULO,
                          PRU.PERIODO_AFERICAO_SULCO
                 ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
             ),
             ULTIMA_AFERICAO_PRESSAO AS (
                 SELECT DISTINCT ON (A.PLACA_VEICULO) A.COD_UNIDADE,
                                                      A.PLACA_VEICULO                AS PLACA,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
                   AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.PLACA_VEICULO,
                          PRU.PERIODO_AFERICAO_PRESSAO
                 ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
             ),

             PRE_SELECT AS (
                 SELECT U.NOME                                            AS NOME_UNIDADE,
                        V.PLACA                                           AS PLACA_VEICULO,
                        COALESCE(V.IDENTIFICADOR_FROTA, '-')              AS IDENTIFICADOR_FROTA,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_SULCO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_PRESSAO,
                        -- Por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
                        UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
                 FROM UNIDADE U
                          JOIN VEICULO V
                               ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
                                    ON UAS.PLACA = V.PLACA
                          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
                                    ON UAP.PLACA = V.PLACA
                 WHERE
                     -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
                     (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR
                      UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
                 GROUP BY U.NOME,
                          V.PLACA,
                          V.IDENTIFICADOR_FROTA,
                          V.COD_TIPO,
                          V.COD_UNIDADE,
                          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
                          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
             )
        SELECT PS.NOME_UNIDADE::TEXT                         AS NOME_UNIDADE,
               PS.PLACA_VEICULO::TEXT                        AS PLACA_VEICULO,
               PS.IDENTIFICADOR_FROTA::TEXT                  AS IDENTIFICADOR_FROTA,
               PS.PODE_AFERIR_SULCO                          AS PODE_AFERIR_SULCO,
               PS.PODE_AFERIR_PRESSAO                        AS PODE_AFERIR_PRESSAO,
               PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA::INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
               PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA::INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM PRE_SELECT PS
             -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        WHERE PS.PODE_AFERIR_SULCO <> FALSE
           OR PS.PODE_AFERIR_PRESSAO <> FALSE
        ORDER BY PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
                 PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;

-- 2020-06-18 -> Adiciona identificador de frota ao relatório. (thaisksf - PL-2760).
DROP FUNCTION FUNC_RELATORIO_PNEU_ADERENCIA_AFERICAO(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE);
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
                    FROM (SELECT A.PLACA_VEICULO            AS PLACA,
                                 A.DATA_HORA,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                                     END                    AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
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
                    WHERE (CALCULO_AFERICAO_PRESSAO.DATA_HORA AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE >= F_DATA_INICIAL::DATE
                      AND (CALCULO_AFERICAO_PRESSAO.DATA_HORA AT TIME ZONE
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
                    FROM (SELECT A.PLACA_VEICULO          AS PLACA,
                                 A.DATA_HORA,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_SULCO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN A.PLACA_VEICULO = LAG(A.PLACA_VEICULO) OVER (ORDER BY PLACA_VEICULO, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY PLACA_VEICULO, DATA_HORA))
                                     ELSE 0
                                     END                  AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
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
                    WHERE CAST(CALCULO_AFERICAO_SULCO.DATA_HORA AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) >= F_DATA_INICIAL::DATE
                      AND CAST(CALCULO_AFERICAO_SULCO.DATA_HORA AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
                   ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
  AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;

-- 2020-06-18 -> Adiciona identificador de frota. (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
    F_COD_AFERICAO BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
                                                                F_COD_AFERICAO BIGINT,
                                                                F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                COD_AFERICAO                 BIGINT,
                COD_UNIDADE                  BIGINT,
                DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO                TEXT,
                IDENTIFICADOR_FROTA          TEXT,
                KM_VEICULO                   BIGINT,
                TEMPO_REALIZACAO             BIGINT,
                TIPO_PROCESSO_COLETA         TEXT,
                TIPO_MEDICAO_COLETADA        TEXT,
                FORMA_COLETA_DADOS           TEXT,
                CPF                          TEXT,
                NOME                         TEXT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                ALTURA_SULCO_INTERNO         REAL,
                PRESSAO_PNEU                 INTEGER,
                POSICAO_PNEU                 INTEGER,
                VIDA_PNEU_MOMENTO_AFERICAO   INTEGER,
                VIDAS_TOTAL_PNEU             INTEGER,
                CODIGO_PNEU                  BIGINT,
                CODIGO_PNEU_CLIENTE          TEXT,
                PRESSAO_RECOMENDADA          REAL
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                          AS KM_VEICULO,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       AV.ALTURA_SULCO_CENTRAL_INTERNO       AS ALTURA_SULCO_CENTRAL_INTERNO,
       AV.ALTURA_SULCO_CENTRAL_EXTERNO       AS ALTURA_SULCO_CENTRAL_EXTERNO,
       AV.ALTURA_SULCO_EXTERNO               AS ALTURA_SULCO_EXTERNO,
       AV.ALTURA_SULCO_INTERNO               AS ALTURA_SULCO_INTERNO,
       AV.PSI::INT                           AS PRESSAO_PNEU,
       AV.POSICAO                            AS POSICAO_PNEU,
       AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU_MOMENTO_AFERICAO,
       P.VIDA_TOTAL                          AS VIDAS_TOTAL_PNEU,
       P.CODIGO                              AS CODIGO_PNEU,
       P.CODIGO_CLIENTE::TEXT                AS CODIGO_PNEU_CLIENTE,
       P.PRESSAO_RECOMENDADA                 AS PRESSAO_RECOMENDADA
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV
              ON A.CODIGO = AV.COD_AFERICAO
         JOIN VEICULO V
              ON V.PLACA = A.PLACA_VEICULO
         JOIN PNEU_ORDEM PO
              ON AV.POSICAO = PO.POSICAO_PROLOG
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         JOIN MODELO_PNEU MO
              ON MO.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP
              ON MP.CODIGO = MO.COD_MARCA
         JOIN COLABORADOR C
              ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO
  AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;

-- 2020-06-18 -> Adiciona identificador de frota (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE,
    F_LIMIT BIGINT,
    F_OFFSET BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT,
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_LIMIT BIGINT,
                                                                        F_OFFSET BIGINT,
                                                                        F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                IDENTIFICADOR_FROTA   TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

-- 2020-06-18 -> Adiciona identificador de frota (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT,
    F_COD_TIPO_VEICULO BIGINT,
    F_PLACA_VEICULO TEXT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE,
    F_LIMIT BIGINT,
    F_OFFSET BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT,
                                                                       F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                IDENTIFICADOR_FROTA   TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

-- TODO VERIFICAR A PARTIR DAQUI
-- 2020-06-19 -> Adiciona identificador frota. (thaisksf - PL-2760)
DROP FUNCTION FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT)
    RETURNS TABLE
            (
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               TEXT,
                DOT                          TEXT,
                VALOR                        REAL,
                COD_UNIDADE_ALOCADO          BIGINT,
                COD_REGIONAL_ALOCADO         BIGINT,
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
       R.CODIGO                         AS COD_REGIONAL_ALOCADO,
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
       COALESCE(PPNE.NOMENCLATURA, '-') AS POSICAO_APLICADO_CLIENTE,
       VEI.CODIGO                       AS COD_VEICULO_APLICADO,
       VEI.PLACA                        AS PLACA_APLICADO,
       VEI.IDENTIFICADOR_FROTA          AS IDENTIFICADOR_FROTA
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
         LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE P.CODIGO = F_COD_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;

-- 2020-06-19 -> Adiciona identificador frota (thaisksf - PL-2760).
DROP FUNCTION FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(F_COD_PNEU BIGINT, F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_PNEU_PARA_AFERICAO_AVULSA(F_COD_PNEU BIGINT, F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                CODIGO                                BIGINT,
                CODIGO_CLIENTE                        TEXT,
                DOT                                   TEXT,
                VALOR                                 REAL,
                COD_UNIDADE_ALOCADO                   BIGINT,
                COD_REGIONAL_ALOCADO                  BIGINT,
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
           INNER_TABLE.IDENTIFICADOR_FROTA,
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
                 V.IDENTIFICADOR_FROTA,
                 A.TIPO_MEDICAO_COLETADA,
                 A.TIPO_PROCESSO_COLETA,
                 C.NOME                      AS NOME_COLABORADOR,
                 MAX(A.CODIGO)
                 OVER (
                     PARTITION BY COD_PNEU ) AS MAX_COD_AFERICAO
          FROM PNEU P
                   LEFT JOIN AFERICAO_VALORES AV ON P.CODIGO = AV.COD_PNEU
                   LEFT JOIN AFERICAO A ON AV.COD_AFERICAO = A.CODIGO
                   LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
                   LEFT JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
          WHERE P.STATUS = 'ESTOQUE'
            AND P.CODIGO = F_COD_PNEU) AS INNER_TABLE
    WHERE CODIGO = INNER_TABLE.MAX_COD_AFERICAO
)

SELECT F.*,
       A.JA_FOI_AFERIDO                      AS JA_FOI_AFERIDO,
       A.COD_AFERICAO                        AS COD_ULTIMA_AFERICAO,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMA_AFERICAO,
       A.PLACA_VEICULO :: TEXT               AS PLACA_VEICULO_ULTIMA_AFERICAO,
       A.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA_ULTIMA_AFERICAO,
       A.TIPO_MEDICAO_COLETADA :: TEXT       AS TIPO_MEDICAO_COLETADA_ULTIMA_AFERICAO,
       A.TIPO_PROCESSO_COLETA :: TEXT        AS TIPO_PROCESSO_COLETA_ULTIMA_AFERICAO,
       A.NOME_COLABORADOR :: TEXT            AS NOME_COLABORADOR_ULTIMA_AFERICAO
FROM FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU) AS F
         LEFT JOIN AFERICOES A ON F.CODIGO = A.COD_PNEU
WHERE F.CODIGO = F_COD_PNEU;
$$;


-- 2020-07-01 -> Adiciona identificador frota (luiz_fp - PL-2760).
DROP FUNCTION FUNC_PNEU_GET_PNEU_BY_PLACA(VARCHAR(7));
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_PLACA(F_PLACA VARCHAR(7))
  RETURNS TABLE (
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
    ALTURA                       INTEGER,
    LARGURA                      INTEGER,
    ARO                          REAL,
    COD_DIMENSAO                 BIGINT,
    PRESSAO_RECOMENDADA          REAL,
    ALTURA_SULCO_CENTRAL_INTERNO REAL,
    ALTURA_SULCO_CENTRAL_EXTERNO REAL,
    ALTURA_SULCO_INTERNO         REAL,
    ALTURA_SULCO_EXTERNO         REAL,
    STATUS                       VARCHAR(255),
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
    POSICAO_APLICADO_CLIENTE     VARCHAR(255),
    COD_VEICULO_APLICADO         BIGINT,
    PLACA_APLICADO               VARCHAR(7),
    IDENTIFICADOR_FROTA          TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  MP.NOME                                  AS NOME_MARCA_PNEU,
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
  P.STATUS,
  P.DOT,
  P.VALOR,
  MOB.CODIGO                               AS COD_MODELO_BANDA,
  MOB.NOME                                 AS NOME_MODELO_BANDA,
  MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
  MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
  MAB.CODIGO                               AS COD_MARCA_BANDA,
  MAB.NOME                                 AS NOME_MARCA_BANDA,
  PVV.VALOR                                AS VALOR_BANDA,
  PO.POSICAO_PROLOG                        AS POSICAO_PNEU,
  COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO_CLIENTE,
  VEI.CODIGO                               AS COD_VEICULO_APLICADO,
  VEI.PLACA                                AS PLACA_APLICADO,
  VEI.IDENTIFICADOR_FROTA                  AS IDENTIFICADOR_FROTA
FROM PNEU P
  JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
  JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
  JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
  LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
  LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
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
WHERE VP.PLACA = F_PLACA
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;
