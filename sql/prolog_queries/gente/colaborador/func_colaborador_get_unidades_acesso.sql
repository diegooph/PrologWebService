-- Sobre:
--
-- Esta function retorna uma listagem das unidades em que o colaborador tem acesso.
-- O JOIN com a tabela de equipe pode ser opcional.
--
-- Histórico:
-- 2020-02-18 -> Adiciona parâmetro na busca pelas unidades de acesso (wvinim - PL-2547).
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR BIGINT, F_EQUIPE_OBRIGATORIA BOOLEAN DEFAULT TRUE)
    RETURNS TABLE
            (
                CODIGO_EMPRESA  BIGINT,
                NOME_EMPRESA    TEXT,
                CODIGO_REGIONAL BIGINT,
                NOME_REGIONAL   TEXT,
                CODIGO_UNIDADE  BIGINT,
                NOME_UNIDADE    TEXT,
                CODIGO_EQUIPE   BIGINT,
                NOME_EQUIPE     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_PERMISSAO SMALLINT;
    F_COD_EMPRESA   BIGINT;
    F_COD_REGIONAL  BIGINT;
    F_COD_UNIDADE   BIGINT;
    F_COD_EQUIPE    BIGINT;
BEGIN
    SELECT INTO F_COD_PERMISSAO, F_COD_EMPRESA, F_COD_REGIONAL, F_COD_UNIDADE, F_COD_EQUIPE C.COD_PERMISSAO,
                                                                                            C.COD_EMPRESA,
                                                                                            R.CODIGO,
                                                                                            C.COD_UNIDADE,
                                                                                            C.COD_EQUIPE
    FROM COLABORADOR C
             JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
             JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
    WHERE C.CODIGO = F_COD_COLABORADOR;
    IF F_EQUIPE_OBRIGATORIA
    THEN
        RETURN QUERY
        SELECT EMP.CODIGO       AS COD_EMPRESA,
               EMP.NOME :: TEXT AS NOME_EMPRESA,
               R.CODIGO         AS COD_REGIONAL,
               R.REGIAO :: TEXT AS NOME_REGIONAL,
               U.CODIGO         AS COD_UNIDADE,
               U.NOME :: TEXT   AS NOME_UNIDADE,
               EQ.CODIGO        AS COD_EQUIPE,
               EQ.NOME :: TEXT  AS NOME_EQUIPE
        FROM UNIDADE U
                 JOIN REGIONAL R
                      ON R.CODIGO = U.COD_REGIONAL
                 JOIN EMPRESA EMP
                      ON EMP.CODIGO = U.COD_EMPRESA
                 JOIN EQUIPE EQ
                      ON U.CODIGO = EQ.COD_UNIDADE
        WHERE EMP.CODIGO = F_COD_EMPRESA
          AND F_IF(F_COD_PERMISSAO <= 2, R.CODIGO = F_COD_REGIONAL, TRUE)
          AND F_IF(F_COD_PERMISSAO <= 1, U.CODIGO = F_COD_UNIDADE, TRUE)
          AND F_IF(F_COD_PERMISSAO = 0, EQ.CODIGO = F_COD_EQUIPE, TRUE)
        ORDER BY EMP.CODIGO ASC, R.CODIGO ASC, U.CODIGO ASC, EQ.CODIGO ASC;
    ELSE
        RETURN QUERY
        SELECT EMP.CODIGO       AS COD_EMPRESA,
               EMP.NOME :: TEXT AS NOME_EMPRESA,
               R.CODIGO         AS COD_REGIONAL,
               R.REGIAO :: TEXT AS NOME_REGIONAL,
               U.CODIGO         AS COD_UNIDADE,
               U.NOME :: TEXT   AS NOME_UNIDADE,
               EQ.CODIGO        AS COD_EQUIPE,
               EQ.NOME :: TEXT  AS NOME_EQUIPE
        FROM UNIDADE U
                 JOIN REGIONAL R
                      ON R.CODIGO = U.COD_REGIONAL
                 JOIN EMPRESA EMP
                      ON EMP.CODIGO = U.COD_EMPRESA
                 LEFT JOIN EQUIPE EQ
                           ON U.CODIGO = EQ.COD_UNIDADE
        WHERE EMP.CODIGO = F_COD_EMPRESA
          AND F_IF(F_COD_PERMISSAO <= 2, R.CODIGO = F_COD_REGIONAL, TRUE)
          AND F_IF(F_COD_PERMISSAO <= 1, U.CODIGO = F_COD_UNIDADE, TRUE)
          AND F_IF(F_COD_PERMISSAO = 0, EQ.CODIGO = F_COD_EQUIPE, TRUE)
        ORDER BY EMP.CODIGO ASC, R.CODIGO ASC, U.CODIGO ASC, EQ.CODIGO ASC;
    END IF;
END;
$$;