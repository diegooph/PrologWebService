CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(F_COD_EMPRESA BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_SERVICO_INCREMENTA_VIDA BIGINT := (SELECT PTS.CODIGO
                                         FROM PUBLIC.PNEU_TIPO_SERVICO PTS
                                         WHERE PTS.COD_EMPRESA = F_COD_EMPRESA
                                               AND PTS.INCREMENTA_VIDA IS TRUE
                                               AND PTS.UTILIZADO_CADASTRO_PNEU IS FALSE
                                               AND PTS.STATUS_ATIVO IS TRUE
                                         ORDER BY PTS.CODIGO
                                         LIMIT 1);
  TIPO_SERVICO_RECAPAGEM TEXT := 'RECAPAGEM';
BEGIN
  IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
  THEN
    INSERT INTO PUBLIC.PNEU_TIPO_SERVICO(
      COD_EMPRESA,
      NOME,
      INCREMENTA_VIDA,
      DATA_HORA_CRIACAO)
    VALUES (
      F_COD_EMPRESA,
      TIPO_SERVICO_RECAPAGEM,
      TRUE,
      NOW()) RETURNING CODIGO INTO COD_SERVICO_INCREMENTA_VIDA;
  END IF;
  RETURN COD_SERVICO_INCREMENTA_VIDA;
END;
$$;