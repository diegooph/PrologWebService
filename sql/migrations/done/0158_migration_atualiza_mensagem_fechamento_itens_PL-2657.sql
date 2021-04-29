DROP FUNCTION FUNC_CHECKLIST_OS_RESOLVER_ITENS(F_COD_UNIDADE BIGINT, F_COD_ITENS BIGINT[], F_CPF BIGINT,
    F_TEMPO_REALIZACAO BIGINT, F_KM BIGINT, F_STATUS_RESOLUCAO TEXT,
    F_DATA_HORA_CONSERTO TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
    F_FEEDBACK_CONSERTO TEXT);

CREATE FUNCTION FUNC_CHECKLIST_OS_RESOLVER_ITENS(F_COD_UNIDADE BIGINT,
                                                 F_COD_ITENS BIGINT[],
                                                 F_CPF BIGINT,
                                                 F_TEMPO_REALIZACAO BIGINT,
                                                 F_KM BIGINT,
                                                 F_STATUS_RESOLUCAO TEXT,
                                                 F_DATA_HORA_CONSERTO TIMESTAMP WITH TIME ZONE,
                                                 F_DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                 F_DATA_HORA_FIM_RESOLUCAO TIMESTAMP WITH TIME ZONE,
                                                 F_FEEDBACK_CONSERTO TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_ITEM                  BIGINT;
    V_DATA_REALIZACAO_CHECKLIST TIMESTAMP WITH TIME ZONE;
    V_ALTERNATIVA_ITEM          BIGINT;
    V_ERROR_MESSAGE             TEXT   := E'Erro! A data de resolução %s não pode ser anterior a data de abertura %s do item "%s".';
    V_QTD_LINHAS_ATUALIZADAS    BIGINT;
    V_TOTAL_LINHAS_ATUALIZADAS  BIGINT := 0;
BEGIN
    FOREACH V_COD_ITEM IN ARRAY F_COD_ITENS
        LOOP
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
            SELECT INTO V_DATA_REALIZACAO_CHECKLIST, V_ALTERNATIVA_ITEM C.DATA_HORA, CAPD.ALTERNATIVA
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                     JOIN CHECKLIST_ORDEM_SERVICO COS
                          ON COSI.COD_OS = COS.CODIGO AND COSI.COD_UNIDADE = COS.COD_UNIDADE
                     JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
                          ON CAPD.CODIGO = COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
            WHERE COSI.CODIGO = V_COD_ITEM;

            -- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
            IF V_DATA_REALIZACAO_CHECKLIST IS NOT NULL AND V_DATA_REALIZACAO_CHECKLIST >= F_DATA_HORA_INICIO_RESOLUCAO
            THEN
                PERFORM THROW_GENERIC_ERROR(
                        FORMAT(V_ERROR_MESSAGE, V_DATA_REALIZACAO_CHECKLIST, F_DATA_HORA_INICIO_RESOLUCAO,
                               V_ALTERNATIVA_ITEM));
            END IF;

            -- Atualiza os itens
            UPDATE CHECKLIST_ORDEM_SERVICO_ITENS
            SET CPF_MECANICO               = F_CPF,
                TEMPO_REALIZACAO           = F_TEMPO_REALIZACAO,
                KM                         = F_KM,
                STATUS_RESOLUCAO           = F_STATUS_RESOLUCAO,
                DATA_HORA_CONSERTO         = F_DATA_HORA_CONSERTO,
                DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
                DATA_HORA_FIM_RESOLUCAO    = F_DATA_HORA_FIM_RESOLUCAO,
                FEEDBACK_CONSERTO          = F_FEEDBACK_CONSERTO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND CODIGO = V_COD_ITEM
              AND DATA_HORA_CONSERTO IS NULL;

            GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            -- Verificamos se o update funcionou.
            IF V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0
            THEN
                PERFORM THROW_GENERIC_ERROR('Erro ao marcar os itens como resolvidos.');
            END IF;
            V_TOTAL_LINHAS_ATUALIZADAS := V_TOTAL_LINHAS_ATUALIZADAS + V_QTD_LINHAS_ATUALIZADAS;
        END LOOP;
    RETURN V_TOTAL_LINHAS_ATUALIZADAS;
END;
$$;