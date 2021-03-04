CREATE OR REPLACE FUNCTION FUNC_UNIDADE_ATUALIZA(F_COD_UNIDADE BIGINT,
                                                 F_NOME_UNIDADE VARCHAR(40),
                                                 F_COD_AUXILIAR_UNIDADE TEXT,
                                                 F_LATITUDE_UNIDADE TEXT,
                                                 F_LONGITUDE_UNIDADE TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    UPDATE UNIDADE
    SET NOME              = F_NOME_UNIDADE,
        COD_AUXILIAR      = F_COD_AUXILIAR_UNIDADE,
        LATITUDE_UNIDADE  = F_LATITUDE_UNIDADE,
        LONGITUDE_UNIDADE = F_LONGITUDE_UNIDADE
    WHERE CODIGO = F_COD_UNIDADE;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados da unidade, tente novamente.');
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_UNIDADE_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                 F_COD_REGIONAIS BIGINT[])
    RETURNS TABLE
            (
                CODIGO_UNIDADE               BIGINT,
                NOME_UNIDADE                 TEXT,
                TOTAL_COLABORADORES_UNIDADE  INTEGER,
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
SELECT UN.CODIGO                                      AS CODIGO_UNIDADE,
       UN.NOME                                        AS NOME_UNIDADE,
       UN.TOTAL_COLABORADORES                         AS TOTAL_COLABORADORES_UNIDADE,
       UN.TIMEZONE                                    AS TIMEZONE_UNIDADE,
       UN.DATA_HORA_CADASTRO AT TIME ZONE UN.TIMEZONE AS DATA_HORA_CADASTRO_UNIDADE,
       UN.STATUS_ATIVO                                AS STATUS_ATIVO_UNIDADE,
       UN.COD_AUXILIAR                                AS CODIGO_AUXILIAR_UNIDADE,
       UN.LATITUDE_UNIDADE                            AS LATITUDE_UNIDADE,
       UN.LONGITUDE_UNIDADE                           AS LONGITUDE_UNIDADE,
       R.CODIGO                                       AS CODIGO_REGIONAL_UNIDADE,
       R.REGIAO                                       AS NOME_REGIAO_REGIONAL_UNIDADE
FROM UNIDADE UN
         INNER JOIN REGIONAL R ON UN.COD_REGIONAL = R.CODIGO
WHERE UN.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_COD_REGIONAIS IS NULL, TRUE, R.CODIGO = ANY(F_COD_REGIONAIS));
$$;

CREATE OR REPLACE FUNCTION FUNC_UNIDADE_VISUALIZACAO(
    F_COD_UNIDADE BIGINT
)
    RETURNS TABLE
            (
                CODIGO_UNIDADE               BIGINT,
                NOME_UNIDADE                 TEXT,
                TOTAL_COLABORADORES_UNIDADE  INTEGER,
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
SELECT UN.CODIGO                                      AS CODIGO_UNIDADE,
       UN.NOME                                        AS NOME_UNIDADE,
       UN.TOTAL_COLABORADORES                         AS TOTAL_COLABORADORES_UNIDADE,
       UN.TIMEZONE                                    AS TIMEZONE_UNIDADE,
       UN.DATA_HORA_CADASTRO AT TIME ZONE UN.TIMEZONE AS DATA_HORA_CADASTRO_UNIDADE,
       UN.STATUS_ATIVO                                AS STATUS_ATIVO_UNIDADE,
       UN.COD_AUXILIAR                                AS CODIGO_AUXILIAR_UNIDADE,
       UN.LATITUDE_UNIDADE                            AS LATITUDE_UNIDADE,
       UN.LONGITUDE_UNIDADE                           AS LONGITUDE_UNIDADE,
       R.CODIGO                                       AS CODIGO_REGIONAL_UNIDADE,
       R.REGIAO                                       AS NOME_REGIAO_REGIONAL_UNIDADE
FROM UNIDADE UN
         INNER JOIN REGIONAL R ON UN.COD_REGIONAL = R.CODIGO
WHERE UN.CODIGO = F_COD_UNIDADE;
$$;

INSERT INTO public.funcao_prolog_agrupamento (codigo, nome, cod_pilar)
VALUES (29, 'Empresa', 5);

INSERT INTO public.funcao_prolog_v11 (codigo, cod_pilar, impacto, cod_agrupamento, descricao, funcao)
VALUES (503, 5, 'MEDIO', 29, 'Permite ao usuário acessar a listagem de regionais e unidades.', 'Visualizar estrutura'),
       (502, 5, 'CRITICO', 29, 'Permite ao usuário alterar o nome, as regionais e unidades da empresa.',
        'Alterar estrutura');