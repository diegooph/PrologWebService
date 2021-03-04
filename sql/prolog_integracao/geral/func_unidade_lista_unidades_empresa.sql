-- Sobre:
--
-- Function disponível na API do ProLog para listar as unidades disponíveis no ProLog.
--
-- Essa function lista todas as informações das unidades disponíveis de uma empresa específica. Utilizamos um parâmetro
-- de filtragem para definir se o retorno será apenas as unidades ativas ou também as unidades desativadas.
--
-- Histórico:
-- 2019-08-19 -> Function criada (diogenesvanzella - PL-2226).
-- 2020-08-05 -> Adapta function para token duplicado (diogenesvanzella - PLI-175).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_UNIDADE_LISTA_UNIDADES_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                   F_APENAS_UNIDADES_ATIVAS BOOLEAN DEFAULT FALSE)
    RETURNS TABLE
            (
                COD_EMPRESA  BIGINT,
                CODIGO       BIGINT,
                NOME         TEXT,
                STATUS_ATIVO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT U.COD_EMPRESA,
               U.CODIGO,
               U.NOME::TEXT,
               U.STATUS_ATIVO
        FROM PUBLIC.UNIDADE U
        WHERE U.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
          AND F_IF(F_APENAS_UNIDADES_ATIVAS, U.STATUS_ATIVO = TRUE, TRUE)
        ORDER BY U.CODIGO;
END;
$$;