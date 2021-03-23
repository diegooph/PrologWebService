BEGIN TRANSACTION;
-- PLI-41
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
                                                                                  F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE COD_PNEU IN (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                       FROM INTEGRACAO.PNEU_CADASTRADO PC
                       WHERE PC.COD_EMPRESA_CADASTRO = (SELECT TI.COD_EMPRESA
                                                        FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                        WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
                         AND PC.COD_PNEU_SISTEMA_INTEGRADO = ANY (F_COD_SISTEMA_INTEGRADO_PNEUS));
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria colunas para salvar informações para autenticação dos métodos
ALTER TABLE INTEGRACAO.EMPRESA_INTEGRACAO_METODOS
    ADD COLUMN API_TOKEN_CLIENT TEXT;
ALTER TABLE INTEGRACAO.EMPRESA_INTEGRACAO_METODOS
    ADD COLUMN API_SHORT_CODE BIGINT;

-- Cria function para buscar informações de autenticação dos métodos
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
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;