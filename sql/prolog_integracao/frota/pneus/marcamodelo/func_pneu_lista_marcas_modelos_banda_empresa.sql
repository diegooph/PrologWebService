-- Sobre:
--
-- Function disponível na API do ProLog para listar as marcas e modelos de bandas disponíveis no ProLog.
--
-- Essa function lista todas as informações das marcas e modelos de bandas disponíveis. Utilizamos um parâmetro booleano
-- que indica se serão retornadas apenas as marcas ativas ou também as desativadas. O parâmetro é específico para a
-- marca, ele não será aplicado aos modelos associados aquela marca.
--
-- Para filtrar apenas modelos ativos de uma marca, ver: 'func_pneu_lista_modelos_bandas_empresa'
--
-- Fizemos essa abordagem pensando num futuro onde o usuário poderá desativar e ativar marcas de bandas, sendo que agora
-- esse parâmetro não é utilizado pois o ProLog não possui o conceito de marcar ativada/desativada.
--
-- Histórico:
-- 2019-08-19 -> Function criada (diogenesvanzella - PL-2225).
-- 2019-10-23 -> Adiciona filtro de empresa (diogenesvanzella - PLI-30).
-- 2019-10-23 -> Altera nome 'QTS_SULCOS_MODELO_BANDA' para 'QTD_SULCOS_MODELO_BANDA'  (diogenesvanzella - PLI-30).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_BANDA_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                                                   APENAS_MARCAS_PNEU_ATIVAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_BANDA                BIGINT,
                NOME_MARCA_BANDA               TEXT,
                STATUS_ATIVO_MARCA_BADA        BOOLEAN,
                COD_MODELO_BANDA               BIGINT,
                NOME_MODELO_BANDA              TEXT,
                COD_EMPRESA_MARCA_MODELO_BANDA BIGINT,
                QTD_SULCOS_MODELO_BANDA        INTEGER,
                ALTURA_SULCOS_MODELO_BANDA     REAL,
                STATUS_ATIVO_MODELO_BANDA      BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT MB.CODIGO                      AS COD_MARCA_BANDA,
               MB.NOME::TEXT                  AS NOME_MARCA_BANDA,
               TRUE                           AS STATUS_ATIVO_MARCA_BADA,
               MBE.COD_MODELO_BANDA           AS COD_MODELO_BANDA,
               MBE.NOME_MODELO_BANDA          AS NOME_MODELO_BANDA,
               MBE.COD_EMPRESA_MODELO_BANDA   AS COD_EMPRESA_MARCA_MODELO_BANDA,
               MBE.QTD_SULCOS_MODELO_BANDA    AS QTD_SULCOS_MODELO_BANDA,
               MBE.ALTURA_SULCOS_MODELO_BANDA AS ALTURA_SULCOS_MODELO_BANDA,
               MBE.STATUS_ATIVO_MODELO_BANDA  AS STATUS_ATIVO_MODELO_BANDA
        FROM PUBLIC.MARCA_BANDA MB
                 LEFT JOIN (SELECT * FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_BANDAS_EMPRESA(F_TOKEN_INTEGRACAO)) MBE
                           ON MBE.COD_MARCA_BANDA = MB.CODIGO
        WHERE MB.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
        ORDER BY MB.CODIGO, MBE.COD_MODELO_BANDA;
END;
$$;