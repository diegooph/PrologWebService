-- Sobre:
-- Function genérica para logar as alterações realizadas em tabelas de integração.
-- Para mais informações sobre o funcionamento e configuração do audit, veja 'func_audit.sql';
--
-- Histórico:
-- 2020-06-08 -> Function criada (diogenesvanzella - PLI-116).
create or replace function audit_integracao.func_audit_integracao()
    returns trigger
    language plpgsql
    security definer
as
$$
declare
    f_table_name_audit text    := tg_relname || '_audit';
    f_tg_op            text    := substring(tg_op, 1, 1);
    f_json             text    := case
                                      when f_tg_op = 'D'
                                          then row_to_json(old)
                                      else row_to_json(new)
        end;
    is_new_row         boolean := case when f_tg_op = 'D' then false else true end;
begin
    execute format(
            'create table if not exists audit_integracao.%I (
              codigo                  bigserial primary key,
              data_hora_utc           timestamp with time zone default now(),
              operacao                varchar(1),
              query                   text,
              pg_username             text,
              pg_application_name     text,
              row_log                 jsonb,
              is_new_row              boolean
            );', f_table_name_audit);

    execute format(
            'insert into audit_integracao.%I (operacao, query, row_log, is_new_row, pg_username, pg_application_name)
             values (%L, %L, %L, %L, %L, %L);',
            f_table_name_audit,
            f_tg_op,
            current_query(),
            f_json,
            is_new_row,
            session_user,
            (select current_setting('application_name')));
    return null;
end;
$$;