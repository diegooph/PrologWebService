CREATE OR REPLACE FUNCTION FUNC_PNEU_TRANSFERENCIA_VISUALIZACAO(
  F_COD_PROCESSO_TRANSFERENCIA BIGINT)
  RETURNS TABLE(
    COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT,
    REGIONAL_ORIGEM                    TEXT,
    UNIDADE_ORIGEM                     TEXT,
    REGIONAL_DESTINO                   TEXT,
    UNIDADE_DESTINO                    TEXT,
    NOME_COLABORADOR                   TEXT,
    DATA_HORA_TRANSFERENCIA            TIMESTAMP WITHOUT TIME ZONE,
    OBSERVACAO                         TEXT,
    TIPO_PROCESSO_TRANSFERENCIA        TIPO_PROCESSO_TRANSFERENCIA_PNEU,
    COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT,
    PLACA_TRANSFERIDA                  TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY

  WITH TRANSFERENCIA_PROCESSO AS (
      SELECT
        PTP.CODIGO                           AS COD_PROCESSO_TRANSFERENCIA_PNEU,
        PTP.COD_UNIDADE_ORIGEM               AS COD_UNIDADE_ORIGEM,
        UO.NOME                              AS UNIDADE_ORIGEM,
        RO.REGIAO                            AS REGIONAL_ORIGEM,
        PTP.COD_UNIDADE_DESTINO              AS COD_UNIDADE_DESTINO,
        UD.NOME                              AS UNIDADE_DESTINO,
        RD.REGIAO                            AS REGIONAL_DESTINO,
        PTP.COD_UNIDADE_COLABORADOR          AS COD_UNIDADE_COLABORADOR,
        CO.NOME                              AS NOME_COLABORADOR,
        PTP.DATA_HORA_TRANSFERENCIA_PROCESSO AS DATA_HORA_TRANSFERENCIA_PROCESSO,
        PTP.OBSERVACAO                       AS OBSERVACAO,
        PTP.TIPO_PROCESSO_TRANSFERENCIA      AS TIPO_PROCESSO_TRANSFERENCIA,
        VTI.COD_PROCESSO_TRANSFERENCIA       AS COD_PROCESSO_TRANSFERENCIA_VEICULO,
        V.PLACA                              AS PLACA_TRANSFERIDA
      FROM PNEU_TRANSFERENCIA_PROCESSO PTP
        JOIN COLABORADOR CO ON PTP.COD_COLABORADOR = CO.CODIGO
        JOIN UNIDADE UO ON UO.CODIGO = PTP.COD_UNIDADE_ORIGEM
        JOIN REGIONAL RO ON UO.COD_REGIONAL = RO.CODIGO
        JOIN UNIDADE UD ON UD.CODIGO = PTP.COD_UNIDADE_DESTINO
        JOIN REGIONAL RD ON UD.COD_REGIONAL = RD.CODIGO
        LEFT JOIN VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVPP
          ON PTP.CODIGO = VTVPP.COD_PROCESSO_TRANSFERENCIA_PNEU
        LEFT JOIN VEICULO_TRANSFERENCIA_INFORMACOES VTI
          ON VTVPP.COD_VEICULO_TRANSFERENCIA_INFORMACOES = VTI.CODIGO
        LEFT JOIN VEICULO V ON VTI.COD_VEICULO = V.CODIGO
      WHERE PTP.CODIGO = F_COD_PROCESSO_TRANSFERENCIA
  )

  SELECT
    TP.COD_PROCESSO_TRANSFERENCIA_PNEU                  AS COD_PROCESSO_TRANSFERENCIA_PNEU,
    TP.REGIONAL_ORIGEM :: TEXT                          AS REGIONAL_ORIGEM,
    TP.UNIDADE_ORIGEM :: TEXT                           AS UNIDADE_ORIGEM,
    TP.REGIONAL_DESTINO :: TEXT                         AS REGIONAL_DESTINO,
    TP.UNIDADE_DESTINO :: TEXT                          AS UNIDADE_DESTINO,
    TP.NOME_COLABORADOR :: TEXT                         AS NOME_COLABORADOR,
    TP.DATA_HORA_TRANSFERENCIA_PROCESSO
    AT TIME ZONE TZ_UNIDADE(TP.COD_UNIDADE_COLABORADOR) AS DATA_HORA_TRANSFERENCIA_PROCESSO,
    TP.OBSERVACAO :: TEXT                               AS OBSERVACAO,
    TP.TIPO_PROCESSO_TRANSFERENCIA                      AS TIPO_PROCESSO_TRANSFERENCIA,
    TP.COD_PROCESSO_TRANSFERENCIA_VEICULO               AS COD_PROCESSO_TRANSFERENCIA_VEICULO,
    TP.PLACA_TRANSFERIDA :: TEXT                        AS PLACA_TRANSFERIDA
  FROM TRANSFERENCIA_PROCESSO TP
  WHERE TP.COD_PROCESSO_TRANSFERENCIA_PNEU = F_COD_PROCESSO_TRANSFERENCIA;
END;
$$;