-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta unidades de uma empresa.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TOKENS_CHECKLISTS_OFFLINES(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETA TOKENS CHECKLISTS OFFILINES
    DROP TRIGGER TG_BLOQUEIO_DELECAO_TOKEN_CHECKLIST_OFFLINE ON CHECKLIST_OFFLINE_DADOS_UNIDADE;

    DELETE
    FROM CHECKLIST_OFFLINE_DADOS_UNIDADE CODU
    WHERE CODU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    CREATE TRIGGER TG_BLOQUEIO_DELECAO_TOKEN_CHECKLIST_OFFLINE
        BEFORE DELETE
        ON CHECKLIST_OFFLINE_DADOS_UNIDADE
        FOR EACH ROW
    EXECUTE PROCEDURE TG_FUNC_BLOQUEIO();
END;
$$;