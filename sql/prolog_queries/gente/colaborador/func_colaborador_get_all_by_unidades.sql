-- Sobre:
--
-- Esta function retorna uma lista dos colaboradores de acordo com o código de uma ou mais unidades e apenas
-- status ativos.
--
-- Histórico:
-- 2020-06-17 -> Function criada (wvinim - PL-2695).
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADES(F_COD_UNIDADES BIGINT[], F_APENAS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO           BIGINT,
                NOME_COLABORADOR TEXT,
                CPF              BIGINT,
                COD_REGIONAL     BIGINT,
                NOME_REGIONAL    TEXT,
                COD_UNIDADE      BIGINT,
                NOME_UNIDADE     TEXT,
                COD_FUNCAO       BIGINT,
                NOME_FUNCAO      TEXT,
                COD_EQUIPE       BIGINT,
                NOME_EQUIPE      TEXT,
                COD_SETOR        BIGINT,
                NOME_SETOR       TEXT,
                MATRICULA_AMBEV  INTEGER,
                MATRICULA_TRANS  INTEGER,
                DATA_NASCIMENTO  DATE,
                STATUS_ATIVO     BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       C.CPF,
       U.CODIGO        AS COD_UNIDADE,
       U.NOME          AS NOME_UNIDADE,
       R.CODIGO        AS COD_REGIONAL,
       R.REGIAO        AS NOME_REGIONAL,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       EQ.CODIGO       AS COD_EQUIPE,
       EQ.NOME         AS NOME_EQUIPE,
       S.CODIGO        AS COD_SETOR,
       S.NOME          AS NOME_SETOR,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.STATUS_ATIVO
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_APENAS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_APENAS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;