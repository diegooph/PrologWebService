-- Sobre:
--
-- Function disponível na API do ProLog para incrementar a vida de um pneu utilizando o serviço padrão do ProLog.
--
-- Utilizamos essa function para pneus que são cadastrados no ProLog e não estão mais na primeira vida, ou seja, eles
-- já possuem uma banda aplicada. A function cria um serviço genérico indicando que o pneu sofreu uma troca de banda e
-- consequentemente um incremento de vida.
--
-- Essa function deve ser utilizada especificamento em cenários de cadastro de pneus, pois internamente ela utiliza a
-- FONTE_CADASTRO como fonte da geração do serviço de troca de banda.
--
-- Precondições:
-- Para essa function executar com sucesso é necessário que o pneu repassado para ela seja um pneu apto a receber o
-- serviço de incremento de vida, por exemplo, um pneu não pode estar cadastrado na PRIMEIRA VIDA e receber um serviço
-- de troca de banda.
--
-- Histórico:
-- 2019-08-15 -> Function criada (diogenesvanzella - PL-2222).
-- 2019-11-27 -> Altera nome de colunas de algumas tabelas (luizfp - PL-2295).
-- 2020-07-20 -> Muda function de schema: integracao -> public (luiz_fp - PL-2205).
CREATE OR REPLACE FUNCTION FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(
  F_COD_UNIDADE_PNEU      BIGINT,
  F_COD_PNEU_PROLOG       BIGINT,
  F_COD_MODELO_BANDA_PNEU BIGINT,
  F_VALOR_BANDA_PNEU      REAL,
  F_VIDA_NOVA_PNEU        INTEGER)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO BIGINT := (SELECT PTS.CODIGO
                                                       FROM PNEU_TIPO_SERVICO AS PTS
                                                       WHERE PTS.COD_EMPRESA IS NULL
                                                             AND PTS.STATUS_ATIVO = TRUE
                                                             AND PTS.INCREMENTA_VIDA = TRUE
                                                             AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE);
  FONTE_SERVICO_REALIZADO_CADASTRO TEXT := 'FONTE_CADASTRO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  IS_PRIMEIRA_VIDA BOOLEAN := F_VIDA_NOVA_PNEU < 2;
  F_COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos que o pneu pode receber o serviço de incremento de vida.
  IF (IS_PRIMEIRA_VIDA)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        'Não é possível aplicar um serviço de troca de banda em um pneu na primeira vida');
  END IF;

  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_CADASTRO) RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    F_COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_CADASTRO);

  INSERT INTO PUBLIC.PNEU_SERVICO_CADASTRO(
    COD_PNEU,
    COD_SERVICO_REALIZADO)
  VALUES (F_COD_PNEU_PROLOG,
          F_COD_SERVICO_REALIZADO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;