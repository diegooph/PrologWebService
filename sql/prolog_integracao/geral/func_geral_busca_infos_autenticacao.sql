-- Sobre:
--
-- Function utilizada para buscar as informações utilizadas para autenticar métodos na integração.
--
-- Histórico:
-- 2019-11-27 -> Function criada (diogenesvanzella - PLI-41).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_BUSCA_INFOS_AUTENTICACAO(F_COD_EMPRESA BIGINT,
                                                                          F_SISTEMA_KEY TEXT,
                                                                          F_METODO_INTEGRADO TEXT)
    RETURNS TABLE
            (
                URL_COMPLETA     TEXT,
                API_TOKEN_CLIENT TEXT,
                API_SHORT_CODE   BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT EIM.URL_COMPLETA     AS URL_COMPLETA,
       EIM.API_TOKEN_CLIENT AS API_TOKEN_CLIENT,
       EIM.API_SHORT_CODE   AS API_SHORT_CODE
FROM INTEGRACAO.EMPRESA_INTEGRACAO_METODOS EIM
         JOIN INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA EIS ON EIM.COD_INTEGRACAO_SISTEMA = EIS.CODIGO
WHERE EIS.COD_EMPRESA = F_COD_EMPRESA
  AND EIS.CHAVE_SISTEMA = F_SISTEMA_KEY
  AND EIM.METODO_INTEGRADO = F_METODO_INTEGRADO;
$$;