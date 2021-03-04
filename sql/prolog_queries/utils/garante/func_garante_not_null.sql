-- Sobre:
-- Verifica se o parâmetro informado é dirente de NULL.
-- É solicitado o nome do atributo sendo verificado (F_FIELD_NAME) para em caso de erro usarmos na mensagem.
--
-- Histórico:
-- 2019-08-15 -> Function criada (luizfp - PL-2200).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_NOT_NULL(F_VALUE ANYELEMENT, F_FIELD_NAME TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF (F_VALUE IS NULL)
    THEN
        RAISE EXCEPTION '% não pode ser nulo', F_FIELD_NAME;
    END IF;
END;
$$;