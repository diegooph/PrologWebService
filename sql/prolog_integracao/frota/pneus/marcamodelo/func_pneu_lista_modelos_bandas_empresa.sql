-- Sobre:
--
-- Function disponível na API do ProLog para listar os modelos de bandas disponíveis no ProLog.
--
-- Essa function lista todas as informações dos modelos de bandas disponíveis. Utilizamos um parâmetro de filtragem para
-- definir se o retorno será os modelos de bandas de uma marca específica ou todos os modelos disponíveis.
-- A function também recebe um parâmetro booleano que indica se serão retornados apenas modelos ativos ou também modelos
-- desativados.
-- Fizemos essa abordagem pensando num futuro onde o usuário poderá desativar os modelos de bandas, sendo que agora
-- esse parâmetro não é utilizado pois o ProLog não possui o conceito de modelo ativado/desativado.
--
-- Histórico:
-- 2019-08-19 -> Function criada (diogenesvanzella - PL-2225).
-- 2019-10-23 -> Altera nome 'QTS_SULCOS_MODELO_BANDA' para 'QTD_SULCOS_MODELO_BANDA'  (diogenesvanzella - PLI-30).
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_BANDAS_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                      F_COD_MARCA_BANDA BIGINT DEFAULT NULL,
                                                      F_APENAS_MODELOS_BANDA_ATIVOS BOOLEAN DEFAULT TRUE)
    RETURNS TABLE
            (
                COD_EMPRESA_MODELO_BANDA   BIGINT,
                COD_MARCA_BANDA            BIGINT,
                COD_MODELO_BANDA           BIGINT,
                NOME_MODELO_BANDA          TEXT,
                QTD_SULCOS_MODELO_BANDA    INTEGER,
                ALTURA_SULCOS_MODELO_BANDA REAL,
                STATUS_ATIVO_MODELO_BANDA  BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT MB.COD_EMPRESA        AS COD_EMPRESA_MODELO_BANDA,
               MB.COD_MARCA          AS COD_MARCA_BANDA,
               MB.CODIGO             AS COD_MODELO_BANDA,
               MB.NOME::TEXT         AS NOME_MODELO_BANDA,
               MB.QT_SULCOS::INTEGER AS QTD_SULCOS_MODELO_BANDA,
               MB.ALTURA_SULCOS      AS ALTURA_SULCOS_MODELO_BANDA,
               TRUE                  AS STATUS_ATIVO_MODELO_BANDA
        FROM PUBLIC.MODELO_BANDA MB
        WHERE MB.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
          AND F_IF(F_COD_MARCA_BANDA IS NULL, TRUE, MB.COD_MARCA = F_COD_MARCA_BANDA)
        ORDER BY MB.CODIGO;
END;
$$;