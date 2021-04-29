-- Essa migração deve ser executada quando o WS versão 42 for publicado.
BEGIN TRANSACTION;

  -- Adiciona coluna ALTURA_SULCOS na tabela MODELO_BANDA.
  -- ########################################################################################################
  ALTER TABLE PUBLIC.MODELO_BANDA ADD COLUMN ALTURA_SULCOS REAL;
  DELETE FROM PUBLIC.MODELO_BANDA WHERE ALTURA_SULCOS IS NULL;
  ALTER TABLE PUBLIC.MODELO_BANDA ALTER COLUMN ALTURA_SULCOS SET NOT NULL;
  -- ########################################################################################################
END TRANSACTION;