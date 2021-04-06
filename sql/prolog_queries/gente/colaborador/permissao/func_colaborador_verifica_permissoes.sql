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