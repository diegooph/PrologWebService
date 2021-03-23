-- Sobre:
--
-- Function disponível na API do ProLog para listar os modelos de pneus disponíveis no ProLog.
--
-- Essa function lista todas as informações dos modelos de pneus disponíveis. Utilizamos um parâmetro de filtragem para
-- definir se o retorno será os modelos de pneus de uma marca específica ou todos os modelos disponíveis.
-- A function também recebe um parâmetro booleano que indica se serão retornados apenas modelos ativos ou também modelos
-- desativados.
-- Fizemos essa abordagem pensando num futuro onde o usuário poderá desativar os modelos, sendo que agora esse
-- parâmetro não é utilizado pois o ProLog não possui o conceito de modelo ativado/desativado.
--
-- Histórico:
-- 2019-08-19 -> Function criada (diogenesvanzella - PL-2225).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_PNEUS_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                     F_COD_MARCA_PNEU BIGINT DEFAULT NULL,
                                                     F_APENAS_MODELOS_PNEU_ATIVOS BOOLEAN DEFAULT TRUE)
    RETURNS TABLE
            (
                COD_EMPRESA_MODELO_PNEU   BIGINT,
                COD_MARCA_PNEU            BIGINT,
                COD_MODELO_PNEU           BIGINT,
                NOME_MODELO_PNEU          TEXT,
                QTD_SULCOS_MODELO_PNEU    INTEGER,
                ALTURA_SULCOS_NODELO_PNEU REAL,
                STATUS_ATIVO_MODELO_PNEU  BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT MP.COD_EMPRESA        AS COD_EMPRESA_MODELO_PNEU,
               MP.COD_MARCA          AS COD_MARCA_PNEU,
               MP.CODIGO             AS COD_MODELO_PNEU,
               MP.NOME::TEXT         AS NOME_MODELO_PNEU,
               MP.QT_SULCOS::INTEGER AS QTD_SULCOS_MODELO_PNEU,
               MP.ALTURA_SULCOS      AS ALTURA_SULCOS_NODELO_PNEU,
               TRUE                  AS STATUS_ATIVO_MODELO_PNEU
        FROM PUBLIC.MODELO_PNEU MP
        WHERE MP.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
          AND F_IF(F_COD_MARCA_PNEU IS NULL, TRUE, MP.COD_MARCA = F_COD_MARCA_PNEU)
        ORDER BY MP.COD_MARCA, MP.CODIGO;
END;
$$;