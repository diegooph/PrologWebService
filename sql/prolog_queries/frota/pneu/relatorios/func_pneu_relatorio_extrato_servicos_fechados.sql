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
       V.PLACA                                                                      AS PLACA_VEICULO,
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
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO OR
            AM.FECHADO_AUTOMATICAMENTE_AFERICAO, 'Sim' :: TEXT, 'Não')              AS TIPO_FECHAMENTO,
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
                   ON V.CODIGO = VP.COD_VEICULO
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