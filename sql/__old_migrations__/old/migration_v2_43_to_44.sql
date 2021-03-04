-- Essa migração deve ser executada quando o WS versão 44 for publicado.
BEGIN TRANSACTION;
  -- Normaliza valor de altura dos sulcos novos do pneu. Atributo será removido da tabela PNEU e adicionado
  -- na tabela MODELO_PNEU.
  -- ########################################################################################################
  ALTER TABLE PUBLIC.MODELO_PNEU ADD COLUMN ALTURA_SULCOS REAL;
  UPDATE PUBLIC.MODELO_PNEU SET ALTURA_SULCOS = 15.00;
  ALTER TABLE PUBLIC.MODELO_PNEU ALTER COLUMN ALTURA_SULCOS SET NOT NULL;
  ALTER TABLE PUBLIC.PNEU DROP COLUMN ALTURA_SULCOS_NOVOS;
  -- ########################################################################################################
END TRANSACTION;