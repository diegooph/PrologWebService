-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Formata a nomenclatura recebida para que não haja espaços excedentes ou characteres que simulam espaço.
--
-- Precondições:
-- 1) Func remove_extra_spaces criada.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION TG_FUNC_PNEU_NOMENCLATURA_FORMATA_NOMENCLATURA()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    NOVA_NOMENCLATURA TEXT := REMOVE_EXTRA_SPACES(NEW.NOMENCLATURA, TRUE);
BEGIN
    IF NOVA_NOMENCLATURA IS NULL OR NOVA_NOMENCLATURA = ''
    THEN
        PERFORM THROW_GENERIC_ERROR('A nomenclatura não pode estar vazia!');
    END IF;
    RETURN NEW;
END
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Trigger acionada a cada inserção/update.
--
-- Precondições:
-- 1) Tabela PNEU_POSICAO_NOMENCLATURA_EMPRESA criada
-- 2) Func tg_func_nomenclatura_formata_nomenclatura criada.
--
-- Histórico:
-- 2019-09-03 -> Trigger criada (thaisksf PL-2259).
CREATE TRIGGER TG_FORMATA_NOMENCLATURA
    BEFORE INSERT OR UPDATE
    ON PNEU_POSICAO_NOMENCLATURA_EMPRESA
    FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_PNEU_NOMENCLATURA_FORMATA_NOMENCLATURA();