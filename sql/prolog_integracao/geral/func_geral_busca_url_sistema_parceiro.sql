-- Sobre:
--
-- Function utilizada para buscar a URL_COMPLETA para qual a integração deverá se comunicar. A URL deve ser completa,
-- contendo a 'baseUrl' e também o 'path' do endpoint que a integração irá se comunicar.
-- Para identificar a URL correta, utilizamos o 'codEmpresa' e também o 'sistemaKey' (contendo a
-- chave do sistema integrado), e o 'metodoIntegrado' identificando para qual método será utilizada a URL.
--
-- Histórico:
-- 2019-10-30 -> Function criada (diogenesvanzella - PLI-41).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_BUSCA_URL_SISTEMA_PARCEIRO(F_COD_EMPRESA BIGINT,
                                                                            F_SISTEMA_KEY TEXT,
                                                                            F_METODO_INTEGRADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN (SELECT EIM.URL_COMPLETA AS URL_COMPLETA
            FROM INTEGRACAO.EMPRESA_INTEGRACAO_METODOS EIM
                     JOIN INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA EIS ON EIM.COD_INTEGRACAO_SISTEMA = EIS.CODIGO
            WHERE EIS.COD_EMPRESA = F_COD_EMPRESA
              AND EIS.CHAVE_SISTEMA = F_SISTEMA_KEY
              AND EIM.METODO_INTEGRADO = F_METODO_INTEGRADO);
END;
$$;