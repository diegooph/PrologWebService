-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Insere os dados na tabela dados_autor_import, após isso chama a function correspondente ao tipo de import, e
-- retorna o código que foi inserido na tabela de dados_autor_import e o nome da tabela de pré-import criada.
--
-- Pré-requisitos:
-- function func_veiculo_import_cria_tabela_import criada.
-- function func_pneu_import_cria_tabela_import criada.
-- function func_colaborador_import_cria_tabela_import criada.
-- function func_vinculo_veiculo_pneu_cria_tabela_vinculo criada.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
-- 2019-12-13 -> Mescla para ser utilizada em qualquer tipo de import (thaisksf - PL-2320).
-- 2020-08-31 -> Adiciona tipo 'vinculo' para criação de tabela (thaisksf - PL-2771).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_IMPORT_INSERE_DADOS_AUTOR(F_COD_EMPRESA BIGINT,
                                                                      F_COD_UNIDADE BIGINT,
                                                                      F_USUARIO TEXT,
                                                                      F_TIPO_IMPORT TEXT)
    RETURNS TABLE
            (
                COD_DADOS_AUTOR_IMPORT BIGINT,
                NOME_TABELA_IMPORT     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATA_HORA_IMPORT       TIMESTAMP WITH TIME ZONE := NOW();
    DATA_IMPORT            DATE                     := CURRENT_DATE;
    COD_DADOS_AUTOR_IMPORT BIGINT;
    NOME_TABELA_CRIADA     TEXT;
BEGIN
    -- VERIFICA SE UNIDADE DESTINO EXISTE E SE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    INSERT INTO IMPLANTACAO.DADOS_AUTOR_IMPORT (COD_EMPRESA, COD_UNIDADE, TIPO_IMPORT, USUARIO, DATA_HORA)
    VALUES (F_COD_EMPRESA, F_COD_UNIDADE, F_TIPO_IMPORT, F_USUARIO, DATA_HORA_IMPORT) RETURNING CODIGO
        INTO COD_DADOS_AUTOR_IMPORT;

    -- Verificamos se o insert funcionou.
    IF COD_DADOS_AUTOR_IMPORT > 0
    THEN
        CASE
            WHEN F_TIPO_IMPORT = 'PNEU'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_PNEU_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'VEICULO'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'COLABORADOR'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_COLABORADOR_IMPORT_CRIA_TABELA_IMPORT(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            WHEN F_TIPO_IMPORT = 'VINCULO'
                THEN
                    SELECT *
                    FROM IMPLANTACAO.FUNC_VINCULO_VEICULO_PNEU_CRIA_TABELA_VINCULO(
                                 F_COD_EMPRESA,
                                 F_COD_UNIDADE,
                                 F_USUARIO,
                                 DATA_IMPORT)
                    INTO NOME_TABELA_CRIADA;
            ELSE
                PERFORM THROW_GENERIC_ERROR(
                                'Não foi possível identificar o tipo de import, verifique para tentar novamente.');
            END CASE;
    ELSE
        PERFORM THROW_GENERIC_ERROR('Não foi possível inserir os dados do autor de import, tente novamente');
    END IF;

    RETURN QUERY SELECT COD_DADOS_AUTOR_IMPORT, NOME_TABELA_CRIADA;
END ;
$$;