--* Sobre:
-- Function genérica para logar as alterações realizadas em qualquer tabela.
--
-- Ativando o log em uma tabela:
-- Para ativar o log de uma tabela, adicione uma trigger apontando para essa function na tabela escolhida.
-- Consulte o arquivo /ProLogDatabase/pilares/audit/audited_tables.sql para verificar como adicionar tal trigger
-- e também para adicionar a trigger criada no arquivo em questão, assim saberemos quais tabelas estão com sistema de
-- log ativo.
--
--* Under the hood:
-- Para cada tabela com sistema de log ativo, sempre que uma alteração for feita (INSERT, UPDATE ou DELETE) essa func
-- irá logar essas alterações. Para cada tabela auditada, uma nova tabela é criada para salvar seus logs. As tabelas
-- auditadas são criadas no schema audit e seguem o seguinte padrão de nomenclatura:
-- Para uma tabela chamada FUNCAO_DATA, a sua tabela de logs será criada no schema audit com o nome FUNCAO_DATA_AUDIT.
-- ATENÇÃO: Você não precisa criar a tabela de audit manualmente, a própria function fará quando tentar salvar o
-- primeiro log e perceber que a tabela ainda não existe.
CREATE OR REPLACE FUNCTION AUDIT.FUNC_AUDIT()
  RETURNS TRIGGER
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  F_TABLE_NAME_AUDIT   TEXT := TG_RELNAME || '_audit';
  --F_TG_OP: I = Insert, U = Update, D = Delete.
  F_TG_OP              TEXT := SUBSTRING(TG_OP, 1, 1);
  F_JSON               TEXT := CASE
                               WHEN F_TG_OP = 'D'
                                 THEN ROW_TO_JSON(OLD)
                               ELSE ROW_TO_JSON(NEW)
                               END;
  IS_NEW_ROW        BOOLEAN := CASE WHEN F_TG_OP = 'D' THEN FALSE ELSE TRUE END;
BEGIN
  EXECUTE FORMAT(
      'CREATE TABLE IF NOT EXISTS audit.%I (
        CODIGO                  SERIAL,
        DATA_HORA_UTC           TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        OPERACAO                VARCHAR(1),
        QUERY                   TEXT,
        PG_USERNAME             TEXT,
        PG_APPLICATION_NAME     TEXT,
        ROW_LOG                 JSONB,
        IS_NEW_ROW              BOOLEAN
      );', F_TABLE_NAME_AUDIT);

  EXECUTE FORMAT(
      'INSERT INTO audit.%I (operacao, query, row_log, is_new_row, pg_username, pg_application_name)
       VALUES (%L, %L, %L, %L, %L, %L);',
      F_TABLE_NAME_AUDIT,
      F_TG_OP,
      CURRENT_QUERY(),
      F_JSON,
      IS_NEW_ROW,
      SESSION_USER,
      (SELECT CURRENT_SETTING('application_name')));
  RETURN NULL;
END;
$$;