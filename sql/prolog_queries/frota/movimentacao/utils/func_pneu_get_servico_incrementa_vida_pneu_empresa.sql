-- Sobre:
--
-- Esta function é utilizada para buscar o código de um tipo de serviço que incrementa a vida do pneu. A empresa pode
-- contér vários serviços que realizam o incremento de vida, neste caso a function retorna o código do primeiro tipo
-- de serviço (realizando um ordenamento ASC pelo código dos serviços, ou seja, o mais antigo cadastrado).
-- Caso a empresa não possua nenhum serviço que incrementa a vida, então a function se encarrega de criar este tipo de
-- serviço onde o nome será genérico "RECAPAGEM" e o código deste tipo de serviço será retornado.
--
-- Histórico:
-- 2019-08-23 -> Function criada (diogenesvanzella - PL-2237).
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