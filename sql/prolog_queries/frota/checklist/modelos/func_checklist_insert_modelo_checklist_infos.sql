-- Sobre:
--
-- Function para criar um modelo de checklist.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_MODELO_CHECKLIST_INFOS(F_COD_UNIDADE_MODELO BIGINT,
                                                                        F_NOME_MODELO TEXT,
                                                                        F_STATUS_ATIVO BOOLEAN,
                                                                        F_COD_CARGOS BIGINT[],
                                                                        F_COD_TIPOS_VEICULOS BIGINT[],
                                                                        F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                                        F_TOKEN_COLABORADOR TEXT)
    RETURNS TABLE
            (
                COD_MODELO_CHECKLIST        BIGINT,
                COD_VERSAO_MODELO_CHECKLIST BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    ERROR_MESSAGE          TEXT   := 'Erro ao salvar modelo de checklist, tente novamente';
    QTD_LINHAS_INSERIDAS   BIGINT;
    COD_MODELO_INSERIDO    BIGINT;
    NOVO_COD_VERSAO_MODELO BIGINT := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));
BEGIN
    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- 1 -> Insere o modelo.
    INSERT INTO CHECKLIST_MODELO(COD_UNIDADE,
                                 COD_VERSAO_ATUAL,
                                 NOME,
                                 STATUS_ATIVO)
    VALUES (F_COD_UNIDADE_MODELO,
            NOVO_COD_VERSAO_MODELO,
            F_NOME_MODELO,
            F_STATUS_ATIVO)
    RETURNING CODIGO INTO COD_MODELO_INSERIDO;


    IF COD_MODELO_INSERIDO IS NULL OR COD_MODELO_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;
    --

    -- 2 -> Insere a versão.
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            1,
            COD_MODELO_INSERIDO,
            F_DATA_HORA_ATUAL,
            (SELECT TA.COD_COLABORADOR FROM TOKEN_AUTENTICACAO TA WHERE TA.TOKEN = F_TOKEN_COLABORADOR));

    GET DIAGNOSTICS QTD_LINHAS_INSERIDAS = ROW_COUNT;

    IF QTD_LINHAS_INSERIDAS IS NULL OR QTD_LINHAS_INSERIDAS <> 1
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;
    --

    -- 3 -> Insere os tipos de veículos.
    INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO(COD_UNIDADE, COD_MODELO, COD_TIPO_VEICULO)
    VALUES (F_COD_UNIDADE_MODELO, COD_MODELO_INSERIDO, UNNEST(F_COD_TIPOS_VEICULOS));
    --

    -- 4 -> Insere os cargos.
    INSERT INTO CHECKLIST_MODELO_FUNCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_FUNCAO)
    VALUES (F_COD_UNIDADE_MODELO, COD_MODELO_INSERIDO, UNNEST(F_COD_CARGOS));
    --

    RETURN QUERY
        SELECT COD_MODELO_INSERIDO              AS COD_MODELO_CHECKLIST,
               NOVO_COD_VERSAO_MODELO :: BIGINT AS COD_VERSAO_MODELO_CHECKLIST;
END;
$$;