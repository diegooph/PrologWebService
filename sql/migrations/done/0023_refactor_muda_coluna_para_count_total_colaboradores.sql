drop function if exists func_unidade_listagem(f_cod_empresa bigint, f_cod_regionais bigint[]);

drop function if exists func_unidade_visualizacao(f_cod_empresa bigint);

CREATE OR REPLACE FUNCTION FUNC_UNIDADE_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                 F_COD_REGIONAIS BIGINT[])
    RETURNS TABLE
            (
                CODIGO_UNIDADE               BIGINT,
                NOME_UNIDADE                 TEXT,
                TOTAL_COLABORADORES_UNIDADE  BIGINT,
                TIMEZONE_UNIDADE             TEXT,
                DATA_HORA_CADASTRO_UNIDADE   TIMESTAMP WITHOUT TIME ZONE,
                STATUS_ATIVO_UNIDADE         BOOLEAN,
                CODIGO_AUXILIAR_UNIDADE      TEXT,
                LATITUDE_UNIDADE             TEXT,
                LONGITUDE_UNIDADE            TEXT,
                CODIGO_REGIONAL_UNIDADE      BIGINT,
                NOME_REGIAO_REGIONAL_UNIDADE TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT UN.CODIGO                                                            AS CODIGO_UNIDADE,
       UN.NOME                                                              AS NOME_UNIDADE,
       (SELECT COUNT(*) FROM COLABORADOR C WHERE C.cod_unidade = UN.CODIGO) AS TOTAL_COLABORADORES_UNIDADE,
       UN.TIMEZONE                                                          AS TIMEZONE_UNIDADE,
       UN.DATA_HORA_CADASTRO AT TIME ZONE UN.TIMEZONE                       AS DATA_HORA_CADASTRO_UNIDADE,
       UN.STATUS_ATIVO                                                      AS STATUS_ATIVO_UNIDADE,
       UN.COD_AUXILIAR                                                      AS CODIGO_AUXILIAR_UNIDADE,
       UN.LATITUDE_UNIDADE                                                  AS LATITUDE_UNIDADE,
       UN.LONGITUDE_UNIDADE                                                 AS LONGITUDE_UNIDADE,
       R.CODIGO                                                             AS CODIGO_REGIONAL_UNIDADE,
       R.REGIAO                                                             AS NOME_REGIAO_REGIONAL_UNIDADE
FROM UNIDADE UN
         INNER JOIN REGIONAL R ON UN.COD_REGIONAL = R.CODIGO
WHERE UN.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_COD_REGIONAIS IS NULL, TRUE, R.CODIGO = ANY (F_COD_REGIONAIS));
$$;

CREATE OR REPLACE FUNCTION FUNC_UNIDADE_VISUALIZACAO(
    F_COD_UNIDADE BIGINT
)
    RETURNS TABLE
            (
                CODIGO_UNIDADE               BIGINT,
                NOME_UNIDADE                 TEXT,
                TOTAL_COLABORADORES_UNIDADE  BIGINT,
                TIMEZONE_UNIDADE             TEXT,
                DATA_HORA_CADASTRO_UNIDADE   TIMESTAMP WITHOUT TIME ZONE,
                STATUS_ATIVO_UNIDADE         BOOLEAN,
                CODIGO_AUXILIAR_UNIDADE      TEXT,
                LATITUDE_UNIDADE             TEXT,
                LONGITUDE_UNIDADE            TEXT,
                CODIGO_REGIONAL_UNIDADE      BIGINT,
                NOME_REGIAO_REGIONAL_UNIDADE TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT UN.CODIGO                                                            AS CODIGO_UNIDADE,
       UN.NOME                                                              AS NOME_UNIDADE,
       (SELECT COUNT(*) FROM COLABORADOR C WHERE C.cod_unidade = UN.CODIGO) AS TOTAL_COLABORADORES_UNIDADE,
       UN.TIMEZONE                                                          AS TIMEZONE_UNIDADE,
       UN.DATA_HORA_CADASTRO AT TIME ZONE UN.TIMEZONE                       AS DATA_HORA_CADASTRO_UNIDADE,
       UN.STATUS_ATIVO                                                      AS STATUS_ATIVO_UNIDADE,
       UN.COD_AUXILIAR                                                      AS CODIGO_AUXILIAR_UNIDADE,
       UN.LATITUDE_UNIDADE                                                  AS LATITUDE_UNIDADE,
       UN.LONGITUDE_UNIDADE                                                 AS LONGITUDE_UNIDADE,
       R.CODIGO                                                             AS CODIGO_REGIONAL_UNIDADE,
       R.REGIAO                                                             AS NOME_REGIAO_REGIONAL_UNIDADE
FROM UNIDADE UN
         INNER JOIN REGIONAL R ON UN.COD_REGIONAL = R.CODIGO
WHERE UN.CODIGO = F_COD_UNIDADE;
$$;