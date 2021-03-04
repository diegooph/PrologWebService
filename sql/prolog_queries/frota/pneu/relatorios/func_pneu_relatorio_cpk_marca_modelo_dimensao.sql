-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Reúne dados de pneus que serão processados para gerar o relatório de custo por km.
--
-- Atenção: Os registros da tabela PNEU_VALOR_VIDA que estiverem com a vida maior que a atual do pneu
-- serão ignorados do cálculo.
--
-- Histórico:
-- 2020-05-13 -> Function criada (wvinim - PL-2699).
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_CPK_MARCA_MODELO_DIMENSAO(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                "UNIDADES FILTRADAS" TEXT,
                "TIPO"               TEXT,
                "MARCA"              TEXT,
                "MODELO"             TEXT,
                "DIMENSÃO"           TEXT,
                "VALOR TOTAL"        TEXT,
                "KM TOTAL"           TEXT,
                "R$/KM (CPK)"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA    BIGINT := (SELECT U.COD_EMPRESA
                                FROM UNIDADE U
                                WHERE U.CODIGO = ANY (F_COD_UNIDADES)
                                LIMIT 1);
    V_NOMES_UNIDADES TEXT   := (SELECT string_agg(U.NOME, ', ' ORDER BY U.NOME)
                                FROM UNIDADE U
                                WHERE U.CODIGO = ANY (F_COD_UNIDADES));
BEGIN
    RETURN QUERY
        WITH -- Aqui inserimos os dados de CPK para pneus na vida 1, sem recapagem.
             DADOS_PNEUS_NOVOS AS (
                 SELECT 'Pneu'                                                   AS TIPO,
                        MAP.CODIGO                                               AS COD_MARCA,
                        MAP.NOME                                                 AS MARCA,
                        MP.CODIGO                                                AS COD_MODELO,
                        MP.NOME                                                  AS MODELO,
                        FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO) AS DIMENSAO,
                        P.VALOR                                                  AS VALOR,
                        VP.KM_RODADO_VIDA
                 FROM VIEW_PNEU_KM_RODADO_TOTAL VP
                          JOIN PNEU P
                               ON P.CODIGO = VP.COD_PNEU
                          JOIN DIMENSAO_PNEU DP
                               ON DP.CODIGO = P.COD_DIMENSAO
                          JOIN MODELO_PNEU MP
                               ON MP.CODIGO = P.COD_MODELO
                          JOIN MARCA_PNEU MAP
                               ON MAP.CODIGO = MP.COD_MARCA
                 WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   -- Esta cláusula permite reunir apenas os dados de pneus novos que já tiveram sua primeira vida útil
                   -- utilizada, tendo utilizado completamente seu potencial de rodagem em relação so seu custo.
                   AND (VP.VIDA_PNEU = 1 AND P.VIDA_ATUAL > 1)
             ),
             -- Aqui inserimos os dados de modelos de pneus, removendo os já listados acima.
             DADOS_MODELOS_PNEUS AS (
                 SELECT 'Pneu'    AS TIPO,
                        M.CODIGO  AS COD_MARCA,
                        M.NOME    AS MARCA,
                        MP.CODIGO AS COD_MODELO,
                        MP.NOME   AS MODELO,
                        '-'       AS DIMENSAO,
                        0         AS VALOR,
                        0         AS KM_RODADO_VIDA
                 FROM MODELO_PNEU MP
                          JOIN MARCA_PNEU M ON MP.COD_MARCA = M.CODIGO
                 WHERE MP.COD_EMPRESA = V_COD_EMPRESA
                   AND MP.CODIGO NOT IN (SELECT COD_MODELO FROM DADOS_PNEUS_NOVOS)
             ),
             -- Aqui inserimos os dados de CPK para pneus com a vida maior que 1, ou seja, recapados.
             DADOS_PNEUS_RECAPADOS AS (
                 SELECT 'Banda'                                                  AS TIPO,
                        MAB.CODIGO                                               AS COD_MARCA,
                        MAB.NOME                                                 AS MARCA,
                        MB.CODIGO                                                AS COD_MODELO,
                        MB.NOME                                                  AS MODELO,
                        FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO) AS DIMENSAO,
                        PVV.VALOR                                                AS VALOR,
                        VP.KM_RODADO_VIDA
                 FROM VIEW_PNEU_KM_RODADO_TOTAL VP
                          JOIN PNEU P
                               ON P.CODIGO = VP.COD_PNEU
                          JOIN DIMENSAO_PNEU DP
                               ON DP.CODIGO = P.COD_DIMENSAO
                          JOIN PNEU_VALOR_VIDA PVV
                               ON PVV.COD_PNEU = VP.COD_PNEU AND PVV.VIDA = VP.VIDA_PNEU
                          JOIN MODELO_BANDA MB
                               ON MB.CODIGO = PVV.COD_MODELO_BANDA
                          JOIN MARCA_BANDA MAB
                               ON MAB.CODIGO = MB.COD_MARCA
                 WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   -- Esta cláusula permite trazer apenas registros de pneus com bandas.
                   AND VP.VIDA_PNEU > 1
                   -- Estas cláusulas removem do cálculo os pneus que tiveram redução de vida manual abaixo de registros
                   -- de aferição com vida.
                   -- Também garante que caso o pneu esteja na última vida que se tem registro, apenas será considerado
                   -- se tiver sido descartado, para evitar que trocas recentes interfiram no cálculo sem ter utilizado
                   -- o potencial de rodagem.
                   AND ((VP.VIDA_PNEU < P.VIDA_ATUAL) OR (VP.VIDA_PNEU = P.VIDA_ATUAL AND P.STATUS = 'DESCARTE'))
             ),
             DADOS_MODELOS_BANDAS AS (
                 SELECT 'Banda'   AS TIPO,
                        M.CODIGO  AS COD_MARCA,
                        M.NOME    AS MARCA,
                        MB.CODIGO AS COD_MODELO,
                        MB.NOME   AS MODELO,
                        '-'       AS DIMENSAO,
                        0         AS VALOR,
                        0         AS KM_RODADO_VIDA
                 FROM MODELO_BANDA MB
                          JOIN MARCA_BANDA M ON MB.COD_MARCA = M.CODIGO
                 WHERE MB.COD_EMPRESA = V_COD_EMPRESA
                   AND MB.CODIGO NOT IN (SELECT COD_MODELO FROM DADOS_PNEUS_RECAPADOS)
             ),
             DADOS_GERAIS AS (SELECT *
                              FROM DADOS_PNEUS_NOVOS
                              UNION ALL
                              SELECT *
                              FROM DADOS_PNEUS_RECAPADOS),
             DADOS_UNIDOS AS (
                 (SELECT TIPO,
                         MARCA,
                         MODELO,
                         DIMENSAO,
                         ROUND(SUM(VALOR)::NUMERIC, 2)::TEXT                                                   AS TOTAL_VALOR,
                         SUM(KM_RODADO_VIDA)::TEXT                                                             AS TOTAL_KM,
                         COALESCE(ROUND((SUM(VALOR) / nullif(SUM(KM_RODADO_VIDA), 0))::NUMERIC, 3)::TEXT, '-') AS CPK
                  FROM DADOS_GERAIS
                  GROUP BY TIPO, MARCA, MODELO, DIMENSAO
                  ORDER BY TIPO, MARCA, MODELO, DIMENSAO)
                 UNION ALL
                 (SELECT TIPO,
                         MARCA,
                         MODELO,
                         DIMENSAO,
                         '-' AS TOTAL_VALOR,
                         '-' AS TOTAL_KM,
                         '-' AS CPK
                  FROM DADOS_MODELOS_PNEUS
                  ORDER BY TIPO, MARCA, MODELO)
                 UNION ALL
                 (SELECT TIPO,
                         MARCA,
                         MODELO,
                         DIMENSAO,
                         '-' AS TOTAL_VALOR,
                         '-' AS TOTAL_KM,
                         '-' AS CPK
                  FROM DADOS_MODELOS_BANDAS
                  ORDER BY TIPO, MARCA, MODELO))
        SELECT V_NOMES_UNIDADES::TEXT,
               TIPO::TEXT,
               MARCA::TEXT,
               MODELO::TEXT,
               DIMENSAO::TEXT,
               TOTAL_VALOR::TEXT,
               TOTAL_KM::TEXT,
               CPK::TEXT
        FROM DADOS_UNIDOS;
END;
$$;