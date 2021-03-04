-- 2020-06-12 -> Adiciona identificador de frota (thaisksf - PL-2761).
DROP FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE);

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"               TEXT,
                "DATA AFERIÇÃO"                    TEXT,
                "DATA RESOLUÇÃO"                   TEXT,
                "HORAS PARA RESOLVER"              DOUBLE PRECISION,
                "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
                "PLACA"                            TEXT,
                "IDENTIFICADOR FROTA"              TEXT,
                "KM AFERIÇÃO"                      BIGINT,
                "KM CONSERTO"                      BIGINT,
                "KM PERCORRIDO"                    BIGINT,
                "COD PNEU"                         CHARACTER VARYING,
                "PRESSÃO RECOMENDADA"              REAL,
                "PRESSÃO AFERIÇÃO"                 TEXT,
                "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
                "PRESSÃO INSERIDA"                 TEXT,
                "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
                "SERVIÇO"                          TEXT,
                "MECÂNICO"                         TEXT,
                "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
                "FECHADO AUTOMATICAMENTE"          TEXT,
                "FORMA DE COLETA DOS DADOS"        TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                       AS UNIDADE_SERVICO,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_AFERICAO,
       TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             3600)                                                                  AS HORAS_RESOLUCAO,
       TRUNC(
               EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 60) AS MINUTOS_RESOLUCAO,
       A.PLACA_VEICULO                                                              AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                         AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                                                                 AS KM_AFERICAO,
       AM.KM_MOMENTO_CONSERTO                                                       AS KM_MOMENTO_CONSERTO,
       AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                        AS KM_PERCORRIDO,
       P.CODIGO_CLIENTE                                                             AS CODIGO_CLIENTE_PNEU,
       P.PRESSAO_RECOMENDADA                                                        AS PRESSAO_RECOMENDADA_PNEU,
       COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_AFERICAO,
       COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','),
                '-')                                                                AS DISPERSAO_PRESSAO_ANTES,
       COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_POS_CONSERTO,
       COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
                        ','),
                '-')                                                                AS DISPERSAO_PRESSAO_DEPOIS,
       COALESCE(PPNE.NOMENCLATURA, '-')                                             AS POSICAO,
       AM.TIPO_SERVICO                                                              AS TIPO_SERVICO,
       COALESCE(INITCAP(C.NOME), '-')                                               AS NOME_MECANICO,
       COALESCE(AA.ALTERNATIVA, '-')                                                AS PROBLEMA_APONTADO,
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, 'Sim' :: TEXT,
            'Não')                                                                  AS TIPO_FECHAMENTO,
       COALESCE(AFCD.STATUS_LEGIVEL, '-')                                           AS FORMA_COLETA_DADOS_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
         JOIN UNIDADE U
              ON AM.COD_UNIDADE = U.CODIGO
         JOIN AFERICAO_VALORES AV
              ON AM.COD_UNIDADE = AV.COD_UNIDADE
                  AND AM.COD_AFERICAO = AV.COD_AFERICAO
                  AND AM.COD_PNEU = AV.COD_PNEU
         JOIN AFERICAO A
              ON A.CODIGO = AV.COD_AFERICAO
         LEFT JOIN COLABORADOR C
                   ON AM.CPF_MECANICO = C.CPF
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         LEFT JOIN VEICULO_PNEU VP
                   ON VP.COD_PNEU = P.CODIGO
                       AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
                   ON AA.CODIGO = AM.COD_ALTERNATIVA
         LEFT JOIN VEICULO V
                   ON V.PLACA = VP.PLACA
         LEFT JOIN EMPRESA E
                   ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT
                   ON E.CODIGO = VT.COD_EMPRESA
                       AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS AFCD
                   ON AFCD.FORMA_COLETA_DADOS = AM.FORMA_COLETA_DADOS_FECHAMENTO
WHERE AV.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;

-- 2020-06-12 -> Adiciona identificador de frota (thaisksf - PL-2761).
DROP FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_DATA_ATUAL DATE);

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"            TEXT,
                "CÓDIGO DO SERVIÇO"             TEXT,
                "TIPO DO SERVIÇO"               TEXT,
                "QTD APONTAMENTOS"              TEXT,
                "DATA HORA ABERTURA"            TEXT,
                "QTD DIAS EM ABERTO"            TEXT,
                "NOME DO COLABORADOR"           TEXT,
                "PLACA"                         TEXT,
                "IDENTIFICADOR FROTA"           TEXT,
                "PNEU"                          TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
                "MEDIDAS"                       TEXT,
                "COD AFERIÇÃO"                  TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "PRESSÃO RECOMENDADA (PSI)"     TEXT,
                "ESTADO ATUAL"                  TEXT,
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                        AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                          AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                               AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                    AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT AS DIAS_EM_ABERTO,
       C.NOME                                                                                   AS NOME_COLABORADOR,
       A.PLACA_VEICULO                                                                          AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                                     AS IDENTIFICADOR_FROTA,
       P.CODIGO_CLIENTE                                                                         AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                 AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                         AS MEDIDAS,
       A.CODIGO :: TEXT                                                                         AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                          AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                  AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                  AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                          AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                   AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                  AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                             AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                 AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                 AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
         JOIN PNEU P
              ON AM.COD_PNEU = P.CODIGO
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN AFERICAO A
              ON A.CODIGO = AM.COD_AFERICAO
         JOIN COLABORADOR C
              ON A.CPF_AFERIDOR = C.CPF
         JOIN AFERICAO_VALORES AV
              ON AV.COD_AFERICAO = AM.COD_AFERICAO
                  AND AV.COD_PNEU = AM.COD_PNEU
         JOIN UNIDADE U
              ON U.CODIGO = AM.COD_UNIDADE
         JOIN EMPRESA E
              ON U.COD_EMPRESA = E.CODIGO
         JOIN PNEU_VIDA_NOMENCLATURA PVN
              ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
         JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
              ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
         JOIN VEICULO V
              ON A.PLACA_VEICULO = V.PLACA
                  AND V.COD_UNIDADE = A.COD_UNIDADE
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND AV.POSICAO = PPNE.POSICAO_PROLOG
WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
  AND (AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;
