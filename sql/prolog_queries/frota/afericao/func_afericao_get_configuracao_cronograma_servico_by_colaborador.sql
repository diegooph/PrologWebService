-- Sobre:
--
-- Esta function retorna uma lista de configurações de abertura de serviço por unidade.
-- Retorna apenas as informações que o colaborador tenha acesso.
--
-- Histórico:
-- 2019-11-25 -> Function criada (wvinim - PL-1989).
-- 2020-02-18 -> Adiciona parâmetro na busca pelas unidades de acesso (wvinim - PL-2547).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_BY_COLABORADOR(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                CODIGO                             BIGINT,
                CODIGO_EMPRESA                     BIGINT,
                CODIGO_REGIONAL                    BIGINT,
                NOME_REGIONAL                      TEXT,
                CODIGO_UNIDADE                     BIGINT,
                NOME_UNIDADE                       TEXT,
                COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT,
                DATA_HORA_ULTIMA_ATUALIZACAO       TIMESTAMP WITHOUT TIME ZONE,
                TOLERANCIA_CALIBRAGEM              REAL,
                TOLERANCIA_INSPECAO                REAL,
                SULCO_MINIMO_RECAPAGEM             REAL,
                SULCO_MINIMO_DESCARTE              REAL,
                PERIODO_AFERICAO_PRESSAO           INTEGER,
                PERIODO_AFERICAO_SULCO             INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        WITH UNIDADES_ACESSO AS (
            SELECT DISTINCT ON (F.CODIGO_UNIDADE) F.CODIGO_UNIDADE,
                                                  F.NOME_UNIDADE,
                                                  F.CODIGO_EMPRESA,
                                                  F.CODIGO_REGIONAL,
                                                  F.NOME_REGIONAL
            FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR, FALSE) F
        )
        SELECT PRU.CODIGO                                                                AS CODIGO,
               UA.CODIGO_EMPRESA                                                         AS CODIGO_EMPRESA,
               UA.CODIGO_REGIONAL                                                        AS CODIGO_REGIONAL,
               UA.NOME_REGIONAL                                                          AS NOME_REGIONAL,
               UA.CODIGO_UNIDADE                                                         AS CODIGO_UNIDADE,
               UA.NOME_UNIDADE                                                           AS NOME_UNIDADE,
               PRU.COD_COLABORADOR_ULTIMA_ATUALIZACAO                                    AS COD_COLABORADOR_ULTIMA_ATUALIZACAO,
               PRU.DATA_HORA_ULTIMA_ATUALIZACAO AT TIME ZONE TZ_UNIDADE(PRU.COD_UNIDADE) AS DATA_HORA_ULTIMA_ATUALIZACAO,
               PRU.TOLERANCIA_CALIBRAGEM                                                 AS TOLERANCIA_CALIBRAGEM,
               PRU.TOLERANCIA_INSPECAO                                                   AS TOLERANCIA_INSPECAO,
               PRU.SULCO_MINIMO_RECAPAGEM                                                AS SULCO_MINIMO_RECAPAGEM,
               PRU.SULCO_MINIMO_DESCARTE                                                 AS SULCO_MINIMO_DESCARTE,
               PRU.PERIODO_AFERICAO_PRESSAO                                              AS PERIODO_AFERICAO_PRESSAO,
               PRU.PERIODO_AFERICAO_SULCO                                                AS PERIODO_AFERICAO_SULCO
        FROM UNIDADES_ACESSO UA
                 LEFT JOIN PNEU_RESTRICAO_UNIDADE PRU ON UA.CODIGO_UNIDADE = PRU.COD_UNIDADE
        ORDER BY UA.NOME_REGIONAL ASC, UA.NOME_UNIDADE ASC;
END;
$$;