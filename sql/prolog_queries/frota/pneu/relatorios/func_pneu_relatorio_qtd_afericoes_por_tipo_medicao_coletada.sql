CREATE FUNCTION FUNC_PNEU_RELATORIO_QTD_AFERICOES_POR_TIPO_MEDICAO_COLETADA(F_COD_UNIDADES BIGINT [],
                                                                            F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(DATA_REFERENCIA DATE, DATA_REFERENCIA_FORMATADA TEXT, QTD_AFERICAO_PRESSAO NUMERIC, QTD_AFERICAO_SULCO NUMERIC, QTD_AFERICAO_SULCO_PRESSAO NUMERIC)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT                    TEXT := 'DD/MM';
  MEDICAO_COLETADA_PRESSAO       TEXT := 'PRESSAO';
  MEDICAO_COLETADA_SULCO         TEXT := 'SULCO';
  MEDICAO_COLETADA_SULCO_PRESSAO TEXT := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY
  SELECT
    DADOS.DATA_REFERENCIA                AS DATA_REFERENCIA,
    DADOS.DATA_REFERENCIA_FORMATADA      AS DATA_REFERENCIA_FORMATADA,
    SUM(DADOS.QT_AFERICAO_PRESSAO)       AS QTD_AFERICAO_PRESSAO,
    SUM(DADOS.QT_AFERICAO_SULCO)         AS QTD_AFERICAO_SULCO,
    SUM(DADOS.QT_AFERICAO_SULCO_PRESSAO) AS QTD_AFERICAO_SULCO_PRESSAO
  FROM (SELECT
          (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE               AS DATA_REFERENCIA,
          TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)), DATE_FORMAT) AS DATA_REFERENCIA_FORMATADA,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_PRESSAO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_PRESSAO,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_SULCO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_SULCO,
          SUM(CASE
              WHEN A.TIPO_MEDICAO_COLETADA = MEDICAO_COLETADA_SULCO_PRESSAO
                THEN 1
              ELSE 0 END)                                                            AS QT_AFERICAO_SULCO_PRESSAO
        FROM AFERICAO A
        WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
              AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
        GROUP BY A.DATA_HORA, DATA_REFERENCIA_FORMATADA, A.COD_UNIDADE
        ORDER BY A.DATA_HORA :: DATE ASC) AS DADOS
  GROUP BY DADOS.DATA_REFERENCIA, DADOS.DATA_REFERENCIA_FORMATADA
  ORDER BY DADOS.DATA_REFERENCIA ASC;
END;
$$;