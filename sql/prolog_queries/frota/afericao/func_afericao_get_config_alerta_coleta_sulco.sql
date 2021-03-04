-- Sobre:
--
-- Esta função retorna a lista de configurações de aferição de acordo com as unidades que o colaborador tem acesso.
-- Retorna valores padrão para as unidades que ainda não tem as configurações setadas.
--
-- Histórico:
-- 2019-12-10 -> Adição de colunas de bloqueio  (wvinim - PL-1934).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIG_ALERTA_COLETA_SULCO(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE                            BIGINT,
                NOME_UNIDADE                           TEXT,
                CODIGO                                 BIGINT,
                VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
                VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
                BLOQUEAR_VALORES_MENORES               BOOLEAN,
                BLOQUEAR_VALORES_MAIORES               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    VARIACAO_MENOR_DEFAULT           DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    VARIACAO_MAIOR_DEFAULT           DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    BLOQUEAR_VALORES_MENORES_DEFAULT BOOLEAN          := (SELECT AP.BLOQUEAR_VALORES_MENORES
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    BLOQUEAR_VALORES_MAIORES_DEFAULT BOOLEAN          := (SELECT AP.BLOQUEAR_VALORES_MAIORES
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
BEGIN
    RETURN QUERY
        WITH UNIDADES_ACESSO AS (
            SELECT DISTINCT ON (F.CODIGO_UNIDADE) F.CODIGO_UNIDADE,
                                                  F.NOME_UNIDADE
            FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
        )

        SELECT
            -- Precisamos utilizar o código de unidade de UA pois a unidade pode não possuir config criada e ainda
            -- assim precisamos retornar o código dela.
            UA.CODIGO_UNIDADE,
            UA.NOME_UNIDADE,
            CONFIG.CODIGO,
            COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS, VARIACAO_MENOR_DEFAULT),
            COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS, VARIACAO_MAIOR_DEFAULT),
            COALESCE(CONFIG.BLOQUEAR_VALORES_MENORES, BLOQUEAR_VALORES_MENORES_DEFAULT),
            COALESCE(CONFIG.BLOQUEAR_VALORES_MAIORES, BLOQUEAR_VALORES_MAIORES_DEFAULT)
        FROM UNIDADES_ACESSO UA
                 LEFT JOIN AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG
                           ON UA.CODIGO_UNIDADE = CONFIG.COD_UNIDADE
        ORDER BY UA.NOME_UNIDADE;
END;
$$;