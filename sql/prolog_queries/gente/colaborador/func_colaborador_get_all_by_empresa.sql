-- Sobre:
--
-- Esta function retorna uma lista dos colaboradores de acordo com o código de empresa e filtro de status.
--
-- Histórico:
-- 2020-01-07 -> Function criada (wvinim - PL-2367).
-- 2020-01-29 -> Adiciona telefone e e-mail ao retorno (wvinim - PL-2471).
-- 2020-02-04 -> Adiciona a sigla iso2 (wvinim - PL-2471).
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO             BIGINT,
                CPF                BIGINT,
                PIS                CHARACTER VARYING,
                MATRICULA_AMBEV    INTEGER,
                MATRICULA_TRANS    INTEGER,
                DATA_NASCIMENTO    DATE,
                DATA_ADMISSAO      DATE,
                DATA_DEMISSAO      DATE,
                STATUS_ATIVO       BOOLEAN,
                NOME_COLABORADOR   TEXT,
                NOME_EMPRESA       TEXT,
                COD_EMPRESA        BIGINT,
                LOGO_THUMBNAIL_URL TEXT,
                NOME_REGIONAL      TEXT,
                COD_REGIONAL       BIGINT,
                NOME_UNIDADE       TEXT,
                COD_UNIDADE        BIGINT,
                NOME_EQUIPE        TEXT,
                COD_EQUIPE         BIGINT,
                NOME_SETOR         TEXT,
                COD_SETOR          BIGINT,
                COD_FUNCAO         BIGINT,
                NOME_FUNCAO        TEXT,
                PERMISSAO          BIGINT,
                TZ_UNIDADE         TEXT,
                SIGLA_ISO2         TEXT,
                PREFIXO_PAIS       INTEGER,
                NUMERO_TELEFONE    TEXT,
                EMAIL              TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       C.CPF,
       C.PIS,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       EM.NOME         AS NOME_EMPRESA,
       EM.CODIGO       AS COD_EMPRESA,
       EM.LOGO_THUMBNAIL_URL,
       R.REGIAO        AS NOME_REGIONAL,
       R.CODIGO        AS COD_REGIONAL,
       U.NOME          AS NOME_UNIDADE,
       U.CODIGO        AS COD_UNIDADE,
       EQ.NOME         AS NOME_EQUIPE,
       EQ.CODIGO       AS COD_EQUIPE,
       S.NOME          AS NOME_SETOR,
       S.CODIGO        AS COD_SETOR,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       C.COD_PERMISSAO AS PERMISSAO,
       U.TIMEZONE      AS TZ_UNIDADE,
       CT.SIGLA_ISO2 :: TEXT,
       CT.PREFIXO_PAIS,
       CT.NUMERO_TELEFONE,
       CE.EMAIL
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
         LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR
         LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR
WHERE C.COD_EMPRESA = F_COD_EMPRESA
  AND CASE
          WHEN F_STATUS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_STATUS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;

