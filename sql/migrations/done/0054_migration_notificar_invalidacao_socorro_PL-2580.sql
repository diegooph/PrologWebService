-- Sobre:
-- Esta function busca os colaboradores que devem ser notificados quando um socorro em rota for invalidado.
--
-- Por enquanto, ela utiliza o código do socorro em rota e busca apenas os tokens de push do colaborador responsável
-- pela abertura. Se o colaborador que invalidou for o mesmo que abriu o socorro, nenhum token será retornado para
-- notificação.
--
-- Também é verificado se o colaborador ainda tem alguma permissão que conceda acesso a visualização de socorro, só se
-- tiver os tokens retornam.
--
-- Histórico:
-- 2020-04-15 -> Function criada (luizfp - PL-2580).
CREATE OR REPLACE FUNCTION
    FUNC_SOCORRO_ROTA_INVALIDACAO_GET_COLABORADORES_NOTIFICACAO(F_COD_COLABORADOR_INVALIDACAO_SOCORRO BIGINT,
                                                                F_COD_SOCORRO_ROTA BIGINT)
    RETURNS TABLE
            (
                COD_COLABORADOR     BIGINT,
                TOKEN_PUSH_FIREBASE TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Só iremos notificar se o colaborador tiver alguma permissão que permita à ele visualizar o socorro através da
    -- notificação. As permissões do array são: solicitar, tratar e visualizar socorros.
    PERMISSAO_PERMISSOES_NECESSARIAS CONSTANT BIGINT[] := ARRAY [145, 146, 147];
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                AS COD_COLABORADOR,
               PCT.TOKEN_PUSH_FIREBASE AS TOKEN_PUSH_FIREBASE
        FROM COLABORADOR C
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON C.CODIGO = SRA.COD_COLABORADOR_ABERTURA
                 JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE
            AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                 JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND
                                  F.CODIGO = CFP.COD_FUNCAO_COLABORADOR AND C.COD_EMPRESA = F.COD_EMPRESA
                 JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT ON C.CODIGO = PCT.COD_COLABORADOR
        WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA
          AND SRA.COD_COLABORADOR_ABERTURA <> F_COD_COLABORADOR_INVALIDACAO_SOCORRO
          AND CFP.COD_FUNCAO_PROLOG = ANY (PERMISSAO_PERMISSOES_NECESSARIAS)
          -- Filtra apenas por aplicativos do Prolog.
          AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD')
          AND C.STATUS_ATIVO = TRUE;
END;
$$;