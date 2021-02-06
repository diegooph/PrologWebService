-- Sobre:
--
-- Essa function insere uma resposta não ok de um checklist realizado
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_RESPOSTAS_CHECKLIST(F_COD_UNIDADE_CHECKLIST BIGINT,
                                                                     F_COD_MODELO_CHECKLIST BIGINT,
                                                                     F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                     F_COD_CHECKLIST BIGINT,
                                                                     F_COD_PERGUNTA BIGINT,
                                                                     F_COD_ALTERNATIVA BIGINT,
                                                                     F_RESPOSTA_OUTROS TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_LINHAS_INSERIDAS BIGINT;
BEGIN
    INSERT INTO CHECKLIST_RESPOSTAS_NOK(COD_UNIDADE,
                                        COD_CHECKLIST_MODELO,
                                        COD_VERSAO_CHECKLIST_MODELO,
                                        COD_CHECKLIST,
                                        COD_PERGUNTA,
                                        COD_ALTERNATIVA,
                                        RESPOSTA_OUTROS)
    VALUES (F_COD_UNIDADE_CHECKLIST,
            F_COD_MODELO_CHECKLIST,
            F_COD_VERSAO_MODELO_CHECKLIST,
            F_COD_CHECKLIST,
            F_COD_PERGUNTA,
            F_COD_ALTERNATIVA,
            F_RESPOSTA_OUTROS);

    GET DIAGNOSTICS QTD_LINHAS_INSERIDAS = ROW_COUNT;

    IF QTD_LINHAS_INSERIDAS <> 1
    THEN
        RAISE EXCEPTION 'Não foi possível inserir a(s) resposta(s)';
    END IF;

    RETURN QTD_LINHAS_INSERIDAS;
END;
$$;