-- Sobre:
--
-- Esta function foi criada para a integração de aferições com a nepomuceno.
--
-- A lógica aplicada nessa function consiste em listar os dados das últimas aferições com base numa lista de pneus
-- recebida e o código da empresa, que é buscado através do código da unidade recebido.
--
-- Histórico:
-- 2020-03-12 -> Function criada (wvinim - PL-2563).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_AFERICOES_INTEGRADA(F_COD_UNIDADE BIGINT,
                                                                                       F_COD_PNEUS_CLIENTE TEXT[])
    RETURNS TABLE
            (
                CODIGO_ULTIMA_AFERICAO        BIGINT,
                COD_PNEU                      TEXT,
                COD_PNEU_CLIENTE              TEXT,
                DATA_HORA_ULTIMA_AFERICAO     TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_AFERICAO     TEXT,
                TIPO_MEDICAO_COLETADA         TEXT,
                TIPO_PROCESSO_COLETA          TEXT,
                PLACA_APLICADO_QUANDO_AFERIDO TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA BIGINT := (SELECT COD_EMPRESA
                             FROM PUBLIC.UNIDADE U
                             WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    RETURN QUERY
        WITH ULTIMAS_AFERICOES_PNEU AS (
            SELECT DISTINCT MAX(AVI.CODIGO) OVER (PARTITION BY AVI.COD_PNEU_CLIENTE) AS CODIGO_ULTIMA_AFERICAO,
                            AVI.COD_PNEU                                             AS COD_PNEU,
                            AVI.COD_PNEU_CLIENTE                                     AS COD_PNEU_CLIENTE,
                            MAX(AVI.COD_AFERICAO_INTEGRADA)
                            OVER (PARTITION BY AVI.COD_PNEU_CLIENTE)                 AS COD_AFERICAO_INTEGRADA
            FROM INTEGRACAO.AFERICAO_VALORES_INTEGRADA AVI
            WHERE AVI.COD_PNEU_CLIENTE = ANY (F_COD_PNEUS_CLIENTE)
        )
        SELECT AI.CODIGO                                           AS CODIGO_ULTIMA_AFERICAO,
               UAP.COD_PNEU::TEXT                                  AS COD_PNEU,
               UAP.COD_PNEU_CLIENTE::TEXT                          AS COD_PNEU_CLIENTE,
               AI.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE) AS DATA_HORA_ULTIMA_AFERICAO,
               COALESCE(CD.NOME, 'Nome Indisponível')::TEXT        AS NOME_COLABORADOR_AFERICAO,
               AI.TIPO_MEDICAO_COLETADA                            AS TIPO_MEDICAO_COLETADA,
               AI.TIPO_PROCESSO_COLETA                             AS TIPO_PROCESSO_COLETA,
               AI.PLACA_VEICULO                                    AS PLACA_APLICADO_QUANDO_AFERIDO
        FROM ULTIMAS_AFERICOES_PNEU UAP
                 JOIN INTEGRACAO.AFERICAO_INTEGRADA AI ON UAP.COD_AFERICAO_INTEGRADA = AI.CODIGO
                 LEFT JOIN COLABORADOR_DATA CD ON CD.CPF = AI.CPF_AFERIDOR::BIGINT
        WHERE AI.COD_EMPRESA_PROLOG = V_COD_EMPRESA;
END;
$$;