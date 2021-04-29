-- Sobre:
-- Verifica compara código da empresa com o nome fornecidos.
--
-- Histórico:
-- 2020-06-24 -> Function criada (thaisksf - PL-2744)
CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA(F_COD_EMPRESA BIGINT,
                                                            F_NOME_EMPRESA TEXT,
                                                            F_ERROR_MESSAGE TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_ERROR_MESSAGE TEXT :=
        F_IF(F_ERROR_MESSAGE IS NULL,
             FORMAT('Empresa de código %s com nome %s não existe!', F_COD_EMPRESA, F_NOME_EMPRESA), F_ERROR_MESSAGE);
BEGIN
    IF NOT EXISTS(SELECT E.CODIGO
                  FROM EMPRESA E
                  WHERE E.CODIGO = F_COD_EMPRESA
                    AND E.NOME = F_NOME_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(V_ERROR_MESSAGE);
    END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Libera a função de checklist offline para uma determinada empresa.
--
-- Histórico:
-- 2020-06-24 - Function criada. (thaisksf - PL-2744)
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