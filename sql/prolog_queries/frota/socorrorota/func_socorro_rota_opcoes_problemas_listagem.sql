-- Sobre:
-- Esta function retorna uma lista das opções de problemas com base no código de empresa
--
-- Histórico:
-- 2020-01-14 -> Function criada (wvinim - PL-2465).
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_OPCOES_PROBLEMAS_LISTAGEM(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                COD_OPCAO_PROBLEMA BIGINT,
                DESCRICAO          CITEXT,
                OBRIGA_DESCRICAO   BOOLEAN,
                STATUS_ATIVO       BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT SROP.CODIGO,
               SROP.DESCRICAO,
               SROP.OBRIGA_DESCRICAO,
               SROP.STATUS_ATIVO
        FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
        WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
        ORDER BY SROP.STATUS_ATIVO DESC, SROP.DESCRICAO ASC ;
END;
$$;