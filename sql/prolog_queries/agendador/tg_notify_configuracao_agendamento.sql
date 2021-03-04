-- Sobre:
-- Trigger utilizada para notificar um evento de alteração de uma linha da tabela AGENDADOR.CONFIGURACAO_AGENDAMENTO.
-- A notificação lançada é composta do conteúdo da linha alterada, o nome e operação realizada na tabela.
-- Caso a alteração seja uma DELEÇÃO então enviamos o conteúdo da linha antes da deleção.
--
-- Histórico:
-- 2019-09-05 -> Function criada (diogenesvanzella - PL-2282).
CREATE OR REPLACE FUNCTION AGENDADOR.TG_NOTIFY_CONFIGURACAO_AGENDAMENTO()
  RETURNS TRIGGER AS $$
DECLARE
    JSON_ROW JSON := CASE
                         WHEN TG_OP::TEXT = 'DELETE'
                             THEN ROW_TO_JSON(OLD)
                         ELSE ROW_TO_JSON(NEW)
                     END;
BEGIN
    PERFORM PG_NOTIFY('configuracao_agendamento_event', JSON_BUILD_OBJECT('tableName', TG_TABLE_NAME,
                                                                          'operation', TG_OP,
                                                                          'configuracaoAgendamento', JSON_ROW) :: TEXT);

    RETURN NULL;
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER TG_NOTIFY_CONFIGURACAO_AGENDAMENTO
  AFTER INSERT OR UPDATE OR DELETE ON AGENDADOR.CONFIGURACAO_AGENDAMENTO
  FOR EACH ROW EXECUTE PROCEDURE AGENDADOR.TG_NOTIFY_CONFIGURACAO_AGENDAMENTO();