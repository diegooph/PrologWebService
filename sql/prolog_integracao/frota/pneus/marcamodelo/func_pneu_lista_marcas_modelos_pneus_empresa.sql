-- Sobre:
--
-- Function disponível na API do ProLog para listar as marcas e modelos de pneus disponíveis no ProLog.
--
-- Essa function lista todas as informações das marcas e modelos de pneus disponíveis. Utilizamos um parâmetro booleano
-- que indica se serão retornados apenas as marcas ativas ou também as desativadas. O parâmetro é específico para a marca,
-- ele não será aplicado aos modelos associados aquela marca.
--
-- Para filtrar apenas modelos ativos de uma marca, ver: 'func_pneu_lista_modelos_pneus_empresa'.
--
-- Fizemos essa abordagem pensando num futuro onde o usuário poderá desativar e ativar marcas de pneus, sendo que agora
-- esse parâmetro não é utilizado pois o ProLog não possui o conceito de marcar ativada/desativada.
--
-- Histórico:
-- 2019-08-19 -> Function criada (diogenesvanzella - PL-2225).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_PNEUS_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                                                   APENAS_MARCAS_PNEU_ATIVAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_PNEU            BIGINT,
                NOME_MARCA_PNEU           TEXT,
                STATUS_ATIVO_MARCA_PNEU   BOOLEAN,
                COD_MODELO_PNEU           BIGINT,
                NOME_MODELO_PNEU          TEXT,
                COD_EMPRESA_MARCA_MODELO  BIGINT,
                QTD_SULCOS_MODELO_PNEU    INTEGER,
                ALTURA_SULCOS_NODELO_PNEU REAL,
                STATUS_ATIVO_MODELO_PNEU  BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT MP.CODIGO                     AS COD_MARCA_PNEU,
               MP.NOME::TEXT                 AS NOME_MARCA_PNEU,
               TRUE                          AS STATUS_ATIVO_MARCA_PNEU,
               MPE.COD_MODELO_PNEU           AS COD_MODELO_PNEU,
               MPE.NOME_MODELO_PNEU          AS NOME_MODELO_PNEU,
               MPE.COD_EMPRESA_MODELO_PNEU   AS COD_EMPRESA_MARCA_MODELO,
               MPE.QTD_SULCOS_MODELO_PNEU    AS QTD_SULCOS_MODELO_PNEU,
               MPE.ALTURA_SULCOS_NODELO_PNEU AS ALTURA_SULCOS_NODELO_PNEU,
               MPE.STATUS_ATIVO_MODELO_PNEU  AS STATUS_ATIVO_MODELO_PNEU
        FROM PUBLIC.MARCA_PNEU MP
                 LEFT JOIN (SELECT * FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_PNEUS_EMPRESA(F_TOKEN_INTEGRACAO)) MPE
                           ON MPE.COD_MARCA_PNEU = MP.CODIGO
        ORDER BY MP.CODIGO, MPE.COD_MODELO_PNEU;
END;
$$;