BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
-- PLI-41
-- Migra tabela que salva os recursos integrados para o schema de integração.
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA
(
    CODIGO            BIGSERIAL NOT NULL,
    COD_EMPRESA       BIGINT    NOT NULL,
    CHAVE_SISTEMA     TEXT      NOT NULL,
    RECURSO_INTEGRADO TEXT      NOT NULL,
    CONSTRAINT PK_EMPRESA_INTEGRACAO PRIMARY KEY (CODIGO),
    CONSTRAINT UNIQUE_EMPRESA_INTEGRACAO UNIQUE (COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO),
    CONSTRAINT FK_EMPRESA_INTEGRACAO_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES PUBLIC.EMPRESA (CODIGO)
);

INSERT INTO INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO)
SELECT I.COD_EMPRESA, I.CHAVE_SISTEMA, I.RECURSO_INTEGRADO
FROM INTEGRACAO I;

DROP TABLE PUBLIC.INTEGRACAO;

-- Deleta tabela que salva DE-PARA de codigo de unidade que não é mais utilizada
DROP TABLE PUBLIC.INTEGRACAO_UNIDADE;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria function para utilizar
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_BUSCA_SISTEMA_KEY(F_USER_TOKEN TEXT,
                                                                   F_RECURSO_INTEGRADO TEXT)
    RETURNS TABLE
            (
                CHAVE_SISTEMA TEXT,
                EXISTE_TOKEN  BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT (SELECT EIS.CHAVE_SISTEMA
                FROM INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA EIS
                         JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = F_USER_TOKEN
                         LEFT JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR
                WHERE C.COD_EMPRESA = EIS.COD_EMPRESA
                  AND EIS.RECURSO_INTEGRADO = F_RECURSO_INTEGRADO)                              AS CHAVE_SISTEMA,
               (SELECT EXISTS(SELECT TOKEN FROM TOKEN_AUTENTICACAO WHERE TOKEN = F_USER_TOKEN)) AS TOKEN_EXISTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria estrutura para salvar URLs de busca/envio de dados
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_INTEGRACAO_METODOS
(
    COD_INTEGRACAO_SISTEMA BIGINT NOT NULL,
    METODO_INTEGRADO               TEXT   NOT NULL,
    URL_COMPLETA                   TEXT   NOT NULL,
    CONSTRAINT FK_EMPRESA_INTEGRACAO_SISTEMA_METODOS FOREIGN KEY (COD_INTEGRACAO_SISTEMA)
        REFERENCES INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA (CODIGO),
    CONSTRAINT UNIQUE_METODO_INTEGRADO UNIQUE (COD_INTEGRACAO_SISTEMA, METODO_INTEGRADO, URL_COMPLETA)
);
--######################################################################################################################
--######################################################################################################################

-- Adiciona método de envio de movimentação para a praxio.
INSERT INTO INTEGRACAO.EMPRESA_INTEGRACAO_METODOS(COD_INTEGRACAO_SISTEMA, METODO_INTEGRADO, URL_COMPLETA)
VALUES (11, 'INSERT_MOVIMENTACAO', 'http://sp.bgmrodotec.com.br:9100/api/api/Troca/Inserir');

--######################################################################################################################
--######################################################################################################################
-- Function para buscar a URL onde a integração irá se comunicar.
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
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;