-- Sobre:
-- Essa function deleta todas as permissões que um cargo possui em uma determinada unidade e libera as permissões
-- informadas por parâmetro.
--
-- Pré-condições:
-- FUNC_GARANTE_COLABORADOR_EXISTE criada.
--
-- Histórico:
-- 2020-02-19 -> Function criada (thaisksf - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_LIBERA_APENAS_PERMISSOES_ESPECIFICAS(F_CPF BIGINT, F_COD_FUNCOES_PROLOG BIGINT[],
                                                                     OUT F_AVISO_PERMISSOES_INSERIDAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_UNIDADE       BIGINT;
    F_COD_FUNCAO        BIGINT;
    F_COD_FUNCAO_PROLOG BIGINT;
BEGIN
    -- VERIFICA SE COLABORADOR EXISTE
    PERFORM FUNC_GARANTE_COLABORADOR_EXISTE(F_CPF);

    SELECT CO.COD_UNIDADE, CO.COD_FUNCAO FROM COLABORADOR CO WHERE CO.CPF = F_CPF INTO F_COD_UNIDADE, F_COD_FUNCAO;

    DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE COD_FUNCAO_COLABORADOR = F_COD_FUNCAO AND COD_UNIDADE = F_COD_UNIDADE;

    FOREACH F_COD_FUNCAO_PROLOG IN ARRAY F_COD_FUNCOES_PROLOG
        LOOP
            IF EXISTS(SELECT FPV11.CODIGO FROM FUNCAO_PROLOG_V11 FPV11 WHERE FPV11.CODIGO = F_COD_FUNCAO_PROLOG)
            THEN
                INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG,
                                                     COD_PILAR_PROLOG)
                SELECT F_COD_UNIDADE,
                       F_COD_FUNCAO,
                       F_COD_FUNCAO_PROLOG,
                       (SELECT FPV11.COD_PILAR FROM FUNCAO_PROLOG_V11 FPV11 WHERE FPV11.CODIGO = F_COD_FUNCAO_PROLOG);
            ELSE
                RAISE EXCEPTION 'A permissão de código % não existe', F_COD_FUNCAO_PROLOG;
            END IF;
        END LOOP;

    IF (array_length(F_COD_FUNCOES_PROLOG, 1) > 1)
    THEN
        --MENSAGEM DE SUCESSO.
        SELECT 'As permissões ' || ARRAY_TO_STRING(F_COD_FUNCOES_PROLOG, ', ') ||
               ' foram atribuídas ao cargo de código ' || F_COD_FUNCAO ||
               ', unidade: ' || F_COD_UNIDADE || '.'
        INTO F_AVISO_PERMISSOES_INSERIDAS;
    ELSE
        --MENSAGEM DE SUCESSO.
        SELECT 'A permissão ' || ARRAY_TO_STRING(F_COD_FUNCOES_PROLOG, ', ') ||
               ' foi atribuída ao cargo de código ' || F_COD_FUNCAO ||
               ', unidade: ' || F_COD_UNIDADE || '.'
        INTO F_AVISO_PERMISSOES_INSERIDAS;
    END IF;
END;
$$;