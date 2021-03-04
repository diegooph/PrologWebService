-- Sobre:
--
-- Esta function retorna os dados necessários para a construção do relatório de farol de aferições.
-- Este relatório exibe as quantidades e percentuais de pneus que estão aplicados (atualmente) e
-- foram aferidos em um range de data.
--
-- Histórico:
-- 2019-11-22 -> Function criada (wvinim - PL-2268).
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_FAROL_AFERICAO(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                             TEXT,
                "QTD DE FROTAS"                       TEXT,
                "QTD DE PNEUS"                        TEXT,
                "QTD DE PNEUS AFERIDOS - PRESSÃO"     TEXT,
                "PERCENTUAL PNEUS AFERIDOS - PRESSÃO" TEXT,
                "QTD DE PNEUS AFERIDOS - SULCO"       TEXT,
                "PERCENTUAL PNEUS AFERIDOS - SULCO"   TEXT
            )
    LANGUAGE SQL
AS
$$
WITH FAROL_AFERICAO AS (
    SELECT U.NOME                                                AS NOME_UNIDADE,
           COUNT(DISTINCT V.PLACA)                               AS QTD_VEICULOS,
           COUNT(DISTINCT VP.*)                                  AS QTD_PNEUS,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_PRESSAO,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_SULCO
    FROM VEICULO_DATA V
             JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
             JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
             LEFT JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = VP.COD_PNEU
             LEFT JOIN AFERICAO A ON V.PLACA = A.PLACA_VEICULO AND A.CODIGO = AV.COD_AFERICAO
    WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE >= (F_DATA_INICIAL)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
    GROUP BY V.COD_UNIDADE, U.NOME
)
SELECT NOME_UNIDADE :: TEXT,
       QTD_VEICULOS :: TEXT,
       QTD_PNEUS :: TEXT,
       TOTAL_PRESSAO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_PRESSAO, QTD_PNEUS) :: TEXT AS PERCENTUAL_PRESSAO,
       TOTAL_SULCO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_SULCO, QTD_PNEUS) :: TEXT   AS PERCENTUAL_SULCO
FROM FAROL_AFERICAO
ORDER BY (TOTAL_PRESSAO :: REAL / NULLIF(QTD_PNEUS, 0) :: REAL) ASC NULLS LAST;
$$;