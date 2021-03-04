-- Sobre:
-- Esta function busca os colaboradores que devem ser notificados quando um socorro em rota for aberto.
--
-- Ela recebe o código da unidade onde o socorro foi solicitado pois apenas colaboradores dessa unidade serão notificados.
-- Além de ser da unidade do socorro, o colaborador precisa ter a permissão de tratar socorro para receber a
-- notificação de abertura (146).
--
-- Como as notificações enviadas são via e-mail ou push, são retornadados os colaboradores que cumpram os critérios
-- acima e que tenham um e-mail OU token do FCM cadastrado.
--
-- Histórico:
-- 2020-01-31 -> Function criada (luizfp - PL-2496).
-- 2020-02-28 -> Altera function para buscar e-mails e tokens do FCM para notificação (luizfp - PL-2522).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA_GET_COLABORADORES_NOTIFICACAO(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_COLABORADOR      BIGINT,
                EMAIL_COLABORADOR    TEXT,
                TOKENS_PUSH_FIREBASE TEXT[]
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PERMISSAO_TRATAR_SOCORRO CONSTANT BIGINT := 146;
    EMPTY_ARRAY              CONSTANT TEXT[] := '{}';
BEGIN
    RETURN QUERY
        WITH DADOS AS (
            SELECT C.CODIGO                                           AS COD_COLABORADOR,
                   CE.EMAIL::TEXT                                     AS EMAIL_COLABORADOR,
                   ARRAY_AGG(PCT.TOKEN_PUSH_FIREBASE)
                   FILTER (WHERE PCT.TOKEN_PUSH_FIREBASE IS NOT NULL) AS TOKENS_PUSH_FIREBASE
            FROM COLABORADOR C
                     JOIN CARGO_FUNCAO_PROLOG_V11 CFP
                          ON C.COD_UNIDADE = CFP.COD_UNIDADE
                              AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                     JOIN FUNCAO F
                          ON F.CODIGO = C.COD_FUNCAO
                              AND F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
                              AND C.COD_EMPRESA = F.COD_EMPRESA
                     LEFT JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT
                               ON C.CODIGO = PCT.COD_COLABORADOR
                                   -- Filtra apenas por aplicativos do Prolog.
                                   AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD')
                     LEFT JOIN COLABORADOR_EMAIL CE
                               ON C.CODIGO = CE.COD_COLABORADOR
            WHERE C.COD_UNIDADE = F_COD_UNIDADE
              AND C.STATUS_ATIVO = TRUE
              AND CFP.COD_FUNCAO_PROLOG = PERMISSAO_TRATAR_SOCORRO
            GROUP BY C.CODIGO, CE.EMAIL
        )

        SELECT D.COD_COLABORADOR                             AS COD_COLABORADOR,
               D.EMAIL_COLABORADOR                           AS EMAIL_COLABORADOR,
               COALESCE(D.TOKENS_PUSH_FIREBASE, EMPTY_ARRAY) AS TOKENS_PUSH_FIREBASE
        FROM DADOS D
        WHERE (D.TOKENS_PUSH_FIREBASE IS NOT NULL OR D.EMAIL_COLABORADOR IS NOT NULL);
END;
$$;