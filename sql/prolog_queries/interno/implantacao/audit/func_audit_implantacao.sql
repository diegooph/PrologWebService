--! ATENÇÃO: Você não precisa criar a tabela de audit manualmente, a própria function fará quando tentar salvar o
--! primeiro log e perceber que a tabela ainda não existe.
CREATE OR REPLACE FUNCTION AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO() RETURNS trigger
    SECURITY DEFINER
    LANGUAGE plpgsql
AS
$$
DECLARE
  F_TABLE_NAME_AUDIT   TEXT := TG_RELNAME || '_audit';
  F_TG_OP              TEXT := SUBSTRING(TG_OP, 1, 1);
  F_JSON               TEXT := CASE
                               WHEN F_TG_OP = 'D'
                                 THEN ROW_TO_JSON(OLD)
                               ELSE ROW_TO_JSON(NEW)
                               END;
  IS_NEW_ROW        BOOLEAN := CASE WHEN F_TG_OP = 'D' THEN FALSE ELSE TRUE END;
BEGIN
  EXECUTE FORMAT(
      'CREATE TABLE IF NOT EXISTS audit_implantacao.%I (
        CODIGO                  SERIAL,
        DATA_HORA_UTC           TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        OPERACAO                VARCHAR(1),
        PG_USERNAME             TEXT,
        PG_APPLICATION_NAME     TEXT,
        ROW_LOG                 JSONB,
        IS_NEW_ROW              BOOLEAN
      );', F_TABLE_NAME_AUDIT);

  EXECUTE FORMAT(
      'INSERT INTO audit_implantacao.%I (operacao, row_log, is_new_row, pg_username, pg_application_name)
       VALUES (%L, %L, %L, %L, %L);', F_TABLE_NAME_AUDIT, F_TG_OP, F_JSON, IS_NEW_ROW, SESSION_USER,
      (SELECT CURRENT_SETTING('application_name')));
  RETURN NULL;
END;
$$;