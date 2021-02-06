alter table if exists audit.colaborador_data_audit add column query text;
alter table if exists audit.afericao_configuracao_tipo_afericao_veiculo_audit add column query text;
alter table if exists audit.afericao_data_audit add column query text;
alter table if exists audit.colaborador_email_audit add column query text;
alter table if exists audit.colaborador_telefone_audit add column query text;
alter table if exists audit.pneu_restricao_unidade_audit add column query text;
alter table if exists audit.movimentacao_motivo_descarte_empresa_audit add column query text;
alter table if exists audit.fechamento_os_audit add column query text;
alter table if exists audit.relato_alternativa_audit add column query text;
alter table if exists audit.push_colaborador_token_audit add column query text;
alter table if exists audit.socorro_rota_empresa_liberada_audit add column query text;
alter table if exists audit.token_autenticacao_audit add column query text;
alter table if exists audit.empresa_audit add column query text;
alter table if exists audit.funcao_data_audit add column query text;
alter table if exists audit.veiculo_data_audit add column query text;
alter table if exists audit.unidade_audit add column query text;
alter table if exists audit.pneu_data_audit add column query text;
alter table if exists audit.veiculo_pneu_audit add column query text;

CREATE OR REPLACE FUNCTION AUDIT.FUNC_AUDIT()
  RETURNS TRIGGER
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
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