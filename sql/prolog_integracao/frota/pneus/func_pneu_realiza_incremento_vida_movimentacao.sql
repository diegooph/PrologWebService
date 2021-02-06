-- Sobre:
--
-- Function disponível na API do ProLog para incrementar a vida de um pneu utilizando um serviço de incremento de
-- vida da empresa específica.
--
-- Esta function é utilizada para "recapar um pneu". Ela incrementa a vida do pneu associando a ele um novo código de
-- modelo de banda. Apenas um serviço que incrementa a vida do pneu pode ser utilizado como vínculo para esta function.
--
-- Essa function deve ser utilizada especificamente em cenários onde há o incremento de vida do pneu, pois internamente
-- ela utiliza a FONTE_MOVIMENTACAO como fonte da geração do serviço de troca de banda, sinalizando que o serviço foi
-- realizado decorrente de uma movimentação.
--
-- Histórico:
-- 2019-08-23 -> Function criada (diogenesvanzella - PL-2237).
-- 2019-11-28 -> Altera nome de colunas de algumas tabelas (luizfp - PL-2295).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(
  F_COD_UNIDADE_PNEU                 BIGINT,
  F_COD_PNEU_PROLOG                  BIGINT,
  F_COD_MODELO_BANDA_PNEU            BIGINT,
  F_VALOR_BANDA_PNEU                 REAL,
  F_VIDA_NOVA_PNEU                   INTEGER,
  F_COD_TIPO_SERVICO_INCREMENTA_VIDA BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  FONTE_SERVICO_REALIZADO_MOVIMENTACAO TEXT := 'FONTE_MOVIMENTACAO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  F_COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    F_COD_TIPO_SERVICO_INCREMENTA_VIDA,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO) RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    F_COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;