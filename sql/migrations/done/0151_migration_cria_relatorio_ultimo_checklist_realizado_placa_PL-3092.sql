-- Sobre:
--
-- Function que gera o relatório que mostra o ultimo checklist realizado por placa - múltiplas unidades.
--
-- Histórico:
-- 2020-09-02 -> Cria function (thaisksf - PL-3092).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ULTIMO_CHECKLIST_REALIZADO_PLACA(F_COD_UNIDADES BIGINT[],
                                                                                     F_COD_TIPOS_VEICULOS BIGINT[])
    RETURNS TABLE
            (
                "UNIDADE DA PLACA"           TEXT,
                "PLACA"                      TEXT,
                "TIPO VEÍCULO"               TEXT,
                "KM ATUAL"                   TEXT,
                "KM COLETADO"                TEXT,
                "MODELO ÚLTIMO CHECKLIST"    TEXT,
                "TIPO CHECKLIST"             TEXT,
                "CPF COLABORADOR"            TEXT,
                "COLABORADOR REALIZAÇÃO"     TEXT,
                "DATA/HORA ÚLTIMO CHECKLIST" TEXT,
                "QTD DIAS SEM CHECKLIST"     TEXT,
                "TEMPO REALIZAÇÃO(SEGUNDOS)" TEXT,
                "TOTAL PERGUNTAS"            TEXT,
                "TOTAL NOK"                  TEXT
            )
    LANGUAGE SQL
AS
$$
WITH GERACAO_DADOS AS (SELECT DISTINCT ON (
    C.PLACA_VEICULO) U.NOME                                         AS NOME_UNIDADE,
                     C.PLACA_VEICULO                                AS PLACA,
                     VT.NOME                                        AS NOME_TIPO_VEICULO,
                     V.KM                                           AS KM_ATUAL,
                     C.KM_VEICULO                                   AS KM_COLETADO,
                     CM.NOME                                        AS NOME_MODELO,
                     CASE
                         WHEN (C.TIPO = 'S')
                             THEN 'SAÍDA'
                         ELSE
                             CASE
                                 WHEN (C.TIPO = 'R')
                                     THEN 'RETORNO'
                                 END
                         END                                        AS TIPO_CHECKLIST,
                     LPAD(C.CPF_COLABORADOR::TEXT, 11, '0')         AS CPF_COLABORADOR,
                     CO.NOME                                        AS NOME_COLABORADOR,
                     FORMAT_TIMESTAMP((MAX(C.DATA_HORA)::TIMESTAMP),
                                      'DD/MM/YYYY HH24:MI')         AS DATA_HORA_CHECKLIST,
                     EXTRACT(DAY FROM (now() - C.DATA_HORA))        AS QTD_DIAS_SEM_CHECKLIST,
                     C.TEMPO_REALIZACAO                             AS TEMPO_REALIZACAO,
                     (C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK) AS TOTAL_PERGUNTAS,
                     C.TOTAL_PERGUNTAS_NOK                          AS TOTAL_PERGUNTAS_NOK
                       FROM CHECKLIST C
                                JOIN CHECKLIST_MODELO CM ON C.COD_CHECKLIST_MODELO = CM.CODIGO
                                JOIN COLABORADOR CO ON C.CPF_COLABORADOR = CO.CPF
                                JOIN VEICULO V ON C.PLACA_VEICULO = V.PLACA
                                JOIN VEICULO_TIPO VT ON V.COD_EMPRESA = VT.COD_EMPRESA
                           AND V.COD_TIPO = VT.CODIGO
                                JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                       WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                         AND V.COD_TIPO = ANY (F_COD_TIPOS_VEICULOS)
                       GROUP BY C.DATA_HORA,
                                U.NOME,
                                C.PLACA_VEICULO,
                                VT.NOME,
                                V.KM,
                                C.KM_VEICULO,
                                CM.NOME,
                                C.TIPO,
                                C.CPF_COLABORADOR,
                                CO.NOME,
                                C.TEMPO_REALIZACAO,
                                (C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK),
                                C.TOTAL_PERGUNTAS_NOK
                       ORDER BY C.PLACA_VEICULO, C.DATA_HORA DESC)

SELECT GD.NOME_UNIDADE ::TEXT,
       GD.PLACA ::TEXT,
       GD.NOME_TIPO_VEICULO ::TEXT,
       GD.KM_ATUAL ::TEXT,
       GD.KM_COLETADO ::TEXT,
       GD.NOME_MODELO ::TEXT,
       GD.TIPO_CHECKLIST ::TEXT,
       GD.CPF_COLABORADOR ::TEXT,
       GD.NOME_COLABORADOR ::TEXT,
       GD.DATA_HORA_CHECKLIST ::TEXT,
       GD.QTD_DIAS_SEM_CHECKLIST ::TEXT,
       GD.TEMPO_REALIZACAO ::TEXT,
       GD.TOTAL_PERGUNTAS ::TEXT,
       GD.TOTAL_PERGUNTAS_NOK ::TEXT
FROM GERACAO_DADOS GD
ORDER BY GD.QTD_DIAS_SEM_CHECKLIST DESC, GD.NOME_UNIDADE, GD.PLACA;
$$;