CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DESBLOQUEIA_CHECKLIST_OFFLINE(F_COD_EMPRESA BIGINT,
                                                                                F_NOME_EMPRESA TEXT,
                                                                                OUT AVISO_CHECKLIST_OFFLINE_LIBERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se empresa existe
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

    -- Garante integridade da empresa
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA(F_COD_EMPRESA, F_NOME_EMPRESA);

    -- Verifica se empresa possui bloqueio checklist
    IF NOT EXISTS(SELECT COD_EMPRESA FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA WHERE COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'O checklist offline não está bloqueado para empresa % - %.', F_COD_EMPRESA, F_NOME_EMPRESA;
    END IF;

    -- Desbloqueia checklist.
    DELETE
    FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA
    WHERE COD_EMPRESA = F_COD_EMPRESA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao liberar o checklist offline para a empresa: % - %.',
            F_COD_EMPRESA, F_NOME_EMPRESA;
    END IF;

    SELECT 'CHECKLIST OFFLINE LIBERADO PARA A EMPRESA: '
               || F_NOME_EMPRESA
               || ', CÓDIGO: '
               || F_COD_EMPRESA
               || '.'
    INTO AVISO_CHECKLIST_OFFLINE_LIBERADO;
END
$$;