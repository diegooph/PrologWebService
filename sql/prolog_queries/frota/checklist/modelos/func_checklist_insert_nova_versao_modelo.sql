CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_NOVA_VERSAO_MODELO(F_COD_UNIDADE_MODELO BIGINT,
                                                                    F_COD_MODELO BIGINT,
                                                                    F_NOME_MODELO TEXT,
                                                                    F_STATUS_ATIVO BOOLEAN,
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
    ERROR_MESSAGE          TEXT   := 'Erro ao atualizar modelo de checklist, tente novamente';
    QTD_LINHAS_ATUALIZADAS BIGINT;
    NOVO_COD_VERSAO_MODELO BIGINT := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));
BEGIN
    -- 1 -> Primeiro criamos uma nova versão.
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            (SELECT MAX(COD_VERSAO_USER_FRIENDLY) + 1
             FROM CHECKLIST_MODELO_VERSAO CMV
             WHERE CMV.COD_CHECKLIST_MODELO = F_COD_MODELO),
            F_COD_MODELO,
            F_DATA_HORA_ATUAL,
            (SELECT TA.COD_COLABORADOR
             FROM TOKEN_AUTENTICACAO TA
             WHERE TA.TOKEN = F_TOKEN_COLABORADOR));

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF QTD_LINHAS_ATUALIZADAS IS NULL
        OR QTD_LINHAS_ATUALIZADAS <> 1
        OR NOVO_COD_VERSAO_MODELO IS NULL
        OR NOVO_COD_VERSAO_MODELO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;

    -- 2 -> Agora atualizamos o modelo de checklist.
    UPDATE CHECKLIST_MODELO
    SET NOME             = F_NOME_MODELO,
        STATUS_ATIVO     = F_STATUS_ATIVO,
        COD_VERSAO_ATUAL = NOVO_COD_VERSAO_MODELO
    WHERE CODIGO = F_COD_MODELO
      AND COD_UNIDADE = F_COD_UNIDADE_MODELO;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <> 1
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;

    RETURN QUERY
        SELECT F_COD_MODELO           AS COD_MODELO_CHECKLIST,
               NOVO_COD_VERSAO_MODELO AS COD_VERSAO_MODELO_CHECKLIST;
END;
$$;