-- Sobre:
--
-- Essa function insere as mídias das alternativas NOK na realização de um checklist.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_MIDIA_ALTERNATIVA(F_UUID_MIDIA UUID,
                                                                   F_COD_CHECKLIST BIGINT,
                                                                   F_COD_ALTERNATIVA BIGINT,
                                                                   F_URL_MIDIA TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_ITEM_OS BIGINT;
    V_COD_MIDIA   BIGINT;
BEGIN
    INSERT INTO CHECKLIST_RESPOSTAS_MIDIAS_ALTERNATIVAS_NOK(UUID,
                                                            COD_CHECKLIST,
                                                            COD_ALTERNATIVA,
                                                            URL_MIDIA,
                                                            TIPO_MIDIA)
    VALUES (F_UUID_MIDIA,
            F_COD_CHECKLIST,
            F_COD_ALTERNATIVA,
            TRIM(F_URL_MIDIA),
            'IMAGEM')
    ON CONFLICT ON CONSTRAINT UNIQUE_UUID_CHECKLIST_RESPOSTAS_MIDIAS_ALTERNATIVA_NOK DO NOTHING
    RETURNING CODIGO INTO V_COD_MIDIA;

    SELECT COD_ITEM_ORDEM_SERVICO
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS
    WHERE COD_CHECKLIST_REALIZADO = F_COD_CHECKLIST
      AND COD_ALTERNATIVA = F_COD_ALTERNATIVA
    INTO V_COD_ITEM_OS;


    IF V_COD_ITEM_OS IS NOT NULL
        AND V_COD_MIDIA IS NOT NULL THEN
        INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS_MIDIA (COD_ITEM_OS,
                                                         COD_MIDIA_NOK)
        VALUES (V_COD_ITEM_OS,
                V_COD_MIDIA)
        ON CONFLICT ON CONSTRAINT UNIQUE_COD_MIDIA_NOK_CHECKLIST_ORDEM_SERVICO_ITENS_MIDIA DO NOTHING;
    END IF;
END;
$$;