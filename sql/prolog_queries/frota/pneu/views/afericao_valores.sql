CREATE OR REPLACE VIEW AFERICAO_VALORES AS
  SELECT
    AV.COD_AFERICAO,
    AV.COD_PNEU,
    AV.COD_UNIDADE,
    AV.ALTURA_SULCO_CENTRAL_INTERNO,
    AV.ALTURA_SULCO_EXTERNO,
    AV.ALTURA_SULCO_INTERNO,
    AV.PSI,
    AV.POSICAO,
    AV.VIDA_MOMENTO_AFERICAO,
    AV.ALTURA_SULCO_CENTRAL_EXTERNO
  FROM AFERICAO_VALORES_DATA AV
  WHERE AV.DELETADO = FALSE;