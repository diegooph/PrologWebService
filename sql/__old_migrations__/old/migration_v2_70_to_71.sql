BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ############### FUNCTION PARA BUSCAR OS COLABORADORES DE UMA UNIDADE ###################################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION FUNC_COLABORADORES_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_STATUS_ATIVOS BOOLEAN);
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_STATUS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO             BIGINT,
    CPF                BIGINT,
    PIS                VARCHAR(11),
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
    PERMISSAO          BIGINT)
LANGUAGE SQL
AS $$

SELECT
  C.CODIGO,
  C.CPF,
  C.PIS,
  C.MATRICULA_AMBEV,
  C.MATRICULA_TRANS,
  C.DATA_NASCIMENTO,
  C.DATA_ADMISSAO,
  C.DATA_DEMISSAO,
  C.STATUS_ATIVO,
  initcap(C.NOME) AS NOME_COLABORADOR,
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
  C.COD_PERMISSAO AS PERMISSAO
FROM COLABORADOR C
  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE
  C.COD_UNIDADE = F_COD_UNIDADE
  AND
  CASE
  WHEN F_STATUS_ATIVOS IS NULL
    THEN 1 = 1
  ELSE C.status_ativo = F_STATUS_ATIVOS
  END
ORDER BY C.NOME ASC
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ############### FUNCTION PARA BUSCAR OS COLABORADORES DE UMA EMPRESA ###################################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION FUNC_COLABORADORES_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN);
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO             BIGINT,
    CPF                BIGINT,
    PIS                VARCHAR(11),
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
    PERMISSAO          BIGINT)
LANGUAGE SQL
AS $$

SELECT
  C.CODIGO,
  C.CPF,
  C.PIS,
  C.MATRICULA_AMBEV,
  C.MATRICULA_TRANS,
  C.DATA_NASCIMENTO,
  C.DATA_ADMISSAO,
  C.DATA_DEMISSAO,
  C.STATUS_ATIVO,
  initcap(C.NOME) AS NOME_COLABORADOR,
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
  C.COD_PERMISSAO AS PERMISSAO
FROM COLABORADOR C
  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE
  C.COD_EMPRESA = F_COD_EMPRESA
  AND
  CASE
  WHEN F_STATUS_ATIVOS IS NULL
    THEN 1 = 1
  ELSE C.status_ativo = F_STATUS_ATIVOS
  END
ORDER BY C.NOME ASC
$$;
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;