-- Sobre:
--
-- Function para verificar se um colaborador que faz uma requisição no WS do ProLog possui as permissões necessárias
-- que o método que ele utiliza está pedindo.
--
-- A function pode tanto verificar se o F_PERMISSOES_COLABORADOR possui ALGUMA DAS permissões passadas em
-- F_PERMISSSOES_NECESSARIAS ou se o F_PERMISSOES_COLABORADOR possui TODAS AS permissões de F_PERMISSSOES_NECESSARIAS.
-- Depende do valor de F_PRECISA_TER_TODAS_AS_PERMISSOES.
--
-- Essa function será usada em conjunto das functions:
-- -> FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN
-- -> FUNC_COLABORADOR_VERIFICA_PERMISSOES_CPF_DATA_NASCIMENTO
--
-- Histórico:
-- 2019-08-29 -> Function criada (luizfp - PL-2267).
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_VERIFICA_PERMISSOES(F_PERMISSOES_COLABORADOR INTEGER[],
                                                                F_PERMISSSOES_NECESSARIAS INTEGER[],
                                                                F_PRECISA_TER_TODAS_AS_PERMISSOES BOOLEAN)
    RETURNS TABLE
            (
                TOKEN_VALIDO      BOOLEAN,
                POSSUI_PERMISSSAO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Se permissões colaborador for null, então o token não existe.
    IF F_PERMISSOES_COLABORADOR IS NULL
    THEN
        RETURN QUERY
            SELECT FALSE AS TOKEN_VALIDO,
                   FALSE AS POSSUI_PERMISSAO;
        -- Sem esse RETURN para barrar a execução a query pode acabar retornando duas linhas.
        RETURN;
    END IF;

    -- PERMISSOES_COLABORADOR contains F_PERMISSSOES_NECESSARIAS
    IF (F_PRECISA_TER_TODAS_AS_PERMISSOES AND F_PERMISSOES_COLABORADOR @> F_PERMISSSOES_NECESSARIAS)
        OR
        -- PERMISSOES_COLABORADOR overlap (have elements in common) F_PERMISSSOES_NECESSARIAS
       (NOT F_PRECISA_TER_TODAS_AS_PERMISSOES AND F_PERMISSOES_COLABORADOR && F_PERMISSSOES_NECESSARIAS)
    THEN
        RETURN QUERY
            SELECT TRUE AS TOKEN_VALIDO,
                   TRUE AS POSSUI_PERMISSAO;
    ELSE
        RETURN QUERY
            SELECT TRUE  AS TOKEN_VALIDO,
                   FALSE AS POSSUI_PERMISSAO;
    END IF;
END;
$$;