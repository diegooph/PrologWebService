-- Cria schema de autit
create schema audit_integracao;

-- Cria function de auditação para integrações
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

-- Cria trigger para auditar a tabela da piccolotur
create trigger tg_func_audit_piccolotur_checklist_pendente_para_sincronizar
    after delete
    on piccolotur.checklist_pendente_para_sincronizar
    for each row
execute procedure audit_integracao.func_audit_integracao();

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_CHECKLIST BIGINT,
                                                                        F_PLACA TEXT,
                                                                        F_CPF_COLABORADOR BIGINT,
                                                                        OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_OS_DELETADA BIGINT;
BEGIN
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE CODIGO = F_COD_CHECKLIST
                            AND COD_UNIDADE = F_COD_UNIDADE
                            AND PLACA_VEICULO = F_PLACA
                            AND CPF_COLABORADOR = F_CPF_COLABORADOR))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique!';
    END IF;

    -- Deleta checklist de forma lógica.
    UPDATE CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CPF_COLABORADOR = F_CPF_COLABORADOR
      AND DELETADO = FALSE;

    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist de código: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_CHECKLIST = F_COD_CHECKLIST
                        AND COD_UNIDADE = F_COD_UNIDADE))
    THEN
        -- Deleta lógicamente a O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE COD_CHECKLIST = F_COD_CHECKLIST
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE
        RETURNING CODIGO INTO V_COD_OS_DELETADA;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. do checklist: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = V_COD_OS_DELETADA
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. do checklist: % da Unidade: %',
                F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST;
    END IF;

    SELECT 'CHECKLIST DELETADO: '
               || F_COD_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;