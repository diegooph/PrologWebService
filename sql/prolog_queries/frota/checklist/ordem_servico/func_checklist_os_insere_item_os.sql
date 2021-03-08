CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_INSERE_ITEM_OS(F_COD_UNIDADE BIGINT,
                                                            F_COD_OS BIGINT,
                                                            F_COD_PERGUNTA_PRIMEIRO_APONTAMENTO BIGINT,
                                                            F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO BIGINT,
                                                            F_STATUS_RESOLUCAO TEXT,
                                                            F_COD_CONTEXTO_PERGUNTA BIGINT,
                                                            F_COD_CONTEXTO_ALTERNATIVA BIGINT,
                                                            F_COD_CHECKLIST_REALIZADO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    NOVA_QTD_APONTAMENTOS_ITEM INTEGER := 1;
    CODIGO_ITEM_OS             BIGINT;
BEGIN
    -- Insere o item da O.S
    INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS(COD_UNIDADE,
                                              COD_OS,
                                              COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                                              COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                                              STATUS_RESOLUCAO,
                                              COD_CONTEXTO_PERGUNTA,
                                              COD_CONTEXTO_ALTERNATIVA)
    VALUES (F_COD_UNIDADE,
            F_COD_OS,
            F_COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
            F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
            F_STATUS_RESOLUCAO,
            F_COD_CONTEXTO_PERGUNTA,
            F_COD_CONTEXTO_ALTERNATIVA)
    RETURNING CODIGO INTO CODIGO_ITEM_OS;

    -- Insere a alternativa que incrementou a quantidade de apontamentos na tabela.
    INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS (COD_ITEM_ORDEM_SERVICO,
                                                            COD_CHECKLIST_REALIZADO,
                                                            COD_ALTERNATIVA,
                                                            NOVA_QTD_APONTAMENTOS)
    VALUES (CODIGO_ITEM_OS,
            F_COD_CHECKLIST_REALIZADO,
            F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
            NOVA_QTD_APONTAMENTOS_ITEM);

    RETURN CODIGO_ITEM_OS;
END;
$$;