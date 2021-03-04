-- sobre:
--
-- function utilizada para buscar o cronograma de aferição de uma empresa. a function recebe um array de unidades,
-- retornando o cronograma das unidades requisitadas.
--
-- histórico:
-- 2019-11-21 -> Function atualizada para filtrar por várias unidades (diogenesvanzella - pli-51).
-- 2020-04-07 -> Adiciona código da unidade da placa (diogenesvanzella - pli-119).
-- 2020-05-08 -> Modifica atributos pode aferir de boolean para text, conforme novo formato da tabela (gustavocnp95 - PL-2689)
-- 2020-06-17 -> Adiciona identificador de frota ao cronograma de aferições. (thaisksf - PL-2760)
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